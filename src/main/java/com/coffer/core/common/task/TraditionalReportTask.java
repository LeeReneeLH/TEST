package com.coffer.core.common.task;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorMerchanService;
import com.coffer.businesses.modules.quartz.entity.Quartz;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;


public class TraditionalReportTask implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger("TraditionalReport");

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
		
		if(StringUtils.isNotBlank(quartz.getCenterOfficeId())){
				String[] officeList = quartz.getCenterOfficeId().split(",");
				// 传统日结时间
				Date reportDate = new Date();
				logger.info("{} - 传统日结开始", DateUtils.formatDateTime(reportDate));
				User user = new User();
				user.setName("系统定时传统日结");
				DayReportDoorMerchanService.dayReportCountList = Lists.newArrayList();
				for(String officeid : officeList){
					DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
					dayReportDoorMerchan.setSearchDateEnd(reportDate.toString());
					Office office= new Office();
					office.setId(officeid);
					DoorCommonUtils.traditionalReportTask(office, reportDate, user, dayReportDoorMerchan);
				}
				logger.info("{} - 传统日结结束", DateUtils.formatDateTime(new Date()));
				
				
				for(DayReportDoorMerchan reportOffice : DayReportDoorMerchanService.dayReportCountList){
					List<String> paramList = Lists.newArrayList();
					paramList.add(String.valueOf(reportOffice.getTotalCount()));
					paramList.add(String.valueOf(reportOffice.getUnSettledCount()));
					// 添加日结消息推送
					SysCommonUtils.dayReportMessageQueueAdd(Constant.MessageType.DAY_REPORT_FINISH,
							Constant.MessageType.DAY_REPORT_STATUS, paramList, reportOffice.getOfficeId(), user);
					logger.info("------{}总日结条数:{}------",  reportOffice.getOfficeId(), reportOffice.getTotalCount());
					logger.info("------{}未日结总条数:{}------", reportOffice.getOfficeId(), reportOffice.getUnSettledCount());
				}
		}else{
			logger.info("{} - 中心ID为空，请添加中心ID", DateUtils.formatDateTime(new Date()));
		}
	}

}
