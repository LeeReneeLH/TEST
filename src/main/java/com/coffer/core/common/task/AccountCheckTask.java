package com.coffer.core.common.task;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

public class AccountCheckTask implements Runnable {

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
		// 需要对账的机构List
		List<Office> officeList = Lists.newArrayList();
		String autoType = Global.getConfig("clear.accounts.accountCheckType");
		String autoTypeWindUp = autoType.replaceAll(";", ",");
		officeList = StoreCommonUtils.getStoCustList(autoTypeWindUp, true);
		if (!Collections3.isEmpty(officeList)) {
			// 对账入账时间
			Date checkDate = new Date();
			logger.info("{} - 对账开始", DateUtils.formatDateTime(checkDate));
			for (Office office : officeList) {
				try {
					DoorCommonUtils.accountChecking(StoreCommonUtils.getOfficeById(office.getId()), checkDate);
				} catch (Exception e) {
					logger.info("officeId：{}对账失败！", office.getId());
				}
			}
			logger.info("{} - 对账结束", DateUtils.formatDateTime(checkDate));
		} else {
			System.out.println("无对账商户！！！");
		}
	}

}
