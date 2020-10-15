package com.coffer.core.common.task;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy(false)
public class TaskManager {

	/*@Scheduled(cron = "0 5 0 * * ? ")
	public synchronized void endDay() {

		// 需要结账的机构List
		List<Office> officeList = Lists.newArrayList();
		// 若结账方式为自动结账
		String autoType = Global.getConfig("clear.accounts.autoWindUpType");
		String autoTypeWindUp = autoType.replaceAll(";", ",");
		officeList = StoreCommonUtils.getStoCustList(autoTypeWindUp, true);
		
		 * if (!Collections3.isEmpty(officeList)) { for (Office office :
		 * officeList) { // 主表状态更新 DayReportMain dayReportMain = new
		 * DayReportMain(); dayReportMain.setReportId(IdGen.uuid());
		 * ClearCommonUtils.dayCenterAccountsReport(ClearConstant.WindupType.
		 * WINDUP_AUTO, dayReportMain, office);
		 * ClearCommonUtils.dayGuestAccountsReport(ClearConstant.WindupType.
		 * WINDUP_AUTO, dayReportMain, office); } }
		 
		if (!Collections3.isEmpty(officeList)) {
			for (Office office : officeList) {
				if (!"10000197".equals(office.getId())) {
					// 主表状态更新
					DoorDayReportMain dayReport = new DoorDayReportMain();
					// 设置日结主键
					dayReport.setReportId(IdGen.uuid());
					// 设置日结时间
					dayReport.setReportDate(new Date());
					// 商户结算
					DoorCommonUtils.dayMerchanAccountsReport(ClearConstant.WindupType.WINDUP_AUTO, dayReport, office);
					// 门店结算
					DoorCommonUtils.dayGuestAccountsReport(ClearConstant.WindupType.WINDUP_AUTO, dayReport, office);
					// 中心账务结算
					DoorCommonUtils.dayCenterAccountsReport(ClearConstant.WindupType.WINDUP_AUTO, dayReport, office);
				}
			}
		}
	}

	@Scheduled(cron = "0 35 15 * * ? ")
	public synchronized void endDay1() {

		// 若结账方式为自动结账
		Office office = new Office("10000197");
		// 主表状态更新
		DoorDayReportMain dayReport = new DoorDayReportMain();
		// 设置日结主键
		dayReport.setReportId(IdGen.uuid());
		// 设置日结时间
		dayReport.setReportDate(new Date());
		// 商户结算
		DoorCommonUtils.dayMerchanAccountsReport(ClearConstant.WindupType.WINDUP_AUTO, dayReport, office);
		// 门店结算
		DoorCommonUtils.dayGuestAccountsReport(ClearConstant.WindupType.WINDUP_AUTO, dayReport, office);
		// 中心账务结算
		DoorCommonUtils.dayCenterAccountsReport(ClearConstant.WindupType.WINDUP_AUTO, dayReport, office);
	}*/
}
