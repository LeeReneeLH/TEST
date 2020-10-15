package com.coffer.core.common.task;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorAndSevenCodeService;
import com.coffer.businesses.modules.quartz.entity.Quartz;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;

public class DayReportTask implements Runnable {

	private static Logger logger = LoggerFactory.getLogger("AccountChecking");

	private String jobName;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public synchronized void run() {
		Quartz quartz = StoreCommonUtils.selectOfficeId(jobName);
		if (StringUtils.isNotBlank(quartz.getOfficeId())) {
			// 对账入账时间
			Date checkDate = new Date();
			logger.info("{} - 对账开始", DateUtils.formatDateTime(checkDate));
			String[] officeList = quartz.getOfficeId().split(",");
			for (String officeId : officeList) {
				try {
					DoorCommonUtils.accountChecking(StoreCommonUtils.getOfficeById(officeId), checkDate);
				} catch (Exception e) {
					logger.info("officeId：{}对账失败！", officeId);
				}
			}
			logger.info("{} - 对账结束", DateUtils.formatDateTime(checkDate));
			try {
				Thread.sleep(1000);// 休眠一秒钟
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 日结时间
			Date reportDate = new Date();
			// 初始化日结条数统计列表及中心id列表
			DayReportDoorAndSevenCodeService.dayReportCountList = Lists.newArrayList();
			DayReportDoorAndSevenCodeService.centerIdsList = Lists.newArrayList();
			for (String officeId : officeList) {
				DoorCommonUtils.scheduledTask(StoreCommonUtils.getOfficeById(officeId), reportDate);
			}
			// 日结条数
			int totalCount = 0;
			// 未日结条数
			int unSettledCount = 0;
			User user = new User();
			user.setName("系统定时日结");

			// 按中心统计日结相关信息
			for (String centerId : DayReportDoorAndSevenCodeService.centerIdsList) {
				totalCount = 0;
				unSettledCount = 0;
				List<String> paramList = Lists.newArrayList();
				for (DayReportDoorMerchan dayReportCount : DayReportDoorAndSevenCodeService.dayReportCountList) {
					// 判断是否为该中心下的商户
					if (StringUtils.isNotBlank(dayReportCount.getOfficeId())
							&& centerId.equals(dayReportCount.getOfficeId())) {
						totalCount += dayReportCount.getTotalCount();
						unSettledCount += dayReportCount.getUnSettledCount();
					}
				}
				paramList.add(String.valueOf(totalCount));
				paramList.add(String.valueOf(unSettledCount));
				// 添加日结消息推送
				SysCommonUtils.dayReportMessageQueueAdd(Constant.MessageType.DAY_REPORT_FINISH,
						Constant.MessageType.DAY_REPORT_STATUS, paramList, centerId, user);
				logger.info("------{}总日结条数:{}------", centerId, totalCount);
				logger.info("------{}未日结总条数:{}------", centerId, unSettledCount);
			}
		} else {
			System.out.println("商户ID为空，请添加商户！！！");
		}
	}

}
