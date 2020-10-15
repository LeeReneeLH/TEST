package com.coffer.businesses.modules.report.v01.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.report.v01.dao.ManageReportDao;
import com.coffer.businesses.modules.report.v01.entity.ManageReport;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;

/**
 * 管理分析报表Service
 * 
 * @author WQJ
 * @version 2020-1-6
 */
@Service
@Transactional(readOnly = true)
public class ManageReportService extends CrudService<ManageReportDao, ManageReport> {
	@Autowired
	private DoorOrderInfoDao doorOrderInfoDao;
	@Autowired
	private ManageReportDao manageReportDao;

	public Page<ManageReport> findPage(Page<ManageReport> page, ManageReport manageReport) {
		return super.findPage(page, manageReport);
	}

	/**
	 * 上门收钞情况
	 *
	 * @author WQJ
	 * @version 2020年1月6日
	 * @param
	 * @param
	 */
	public Page<ManageReport> collectMoneySituation(Page<ManageReport> page, ManageReport manageReport) {
		// 查询条件： 开始时间
		if (manageReport.getCreateTimeStart() != null) {
			manageReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(manageReport.getCreateTimeStart())));
		}
		// 如果为空，默认当前年
		else {
			manageReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getYearStart(new Date())));
		}
		// 查询条件： 结束时间
		if (manageReport.getCreateTimeEnd() != null) {
			manageReport.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(manageReport.getCreateTimeEnd())));
		}
		// 如果为空，默认当前年
		else {
			manageReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getYearEnd(new Date())));
		}
		manageReport.setPage(page);
		page.setList(manageReportDao.collectSituation(manageReport));
		return page;
	}

	/**
	 * 卡钞情况
	 *
	 * @author WQJ
	 * @version 2020年1月6日
	 * @param
	 * @param
	 */
	public Page<ManageReport> stuckCollectSituation(Page<ManageReport> page, ManageReport manageReport) {
		manageReport.setPage(page);
		page.setList(manageReportDao.stuckCollectSituation(manageReport));
		return page;
	}

	/**
	 * 差错情况
	 *
	 * @author WQJ
	 * @version 2020年1月6日
	 * @param
	 * @param
	 */

	public Page<ManageReport> errorCollectSituation(Page<ManageReport> page, ManageReport manageReport) {
		manageReport.setPage(page);
		page.setList(manageReportDao.errorCollectSituation(manageReport));
		return page;
	}
}
