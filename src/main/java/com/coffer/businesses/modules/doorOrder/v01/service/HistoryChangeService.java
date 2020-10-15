package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.HistoryChangeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryChange;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 历史更换记录Service
 *
 * @author ZXK
 * @version 2019-10-31
 */
@Service
@Transactional(readOnly = true)
public class HistoryChangeService extends CrudService<HistoryChangeDao, HistoryChange> {

	@Autowired
	private HistoryChangeDao historyChangeDao;

	/**
	 * 获取机具历史更换记录
	 *
	 * @param page
	 * @param historyChange
	 * @return
	 * @author ZXK
	 * @version 2019年10月30日
	 */
	public Page<HistoryChange> findHistoryChangePage(Page<HistoryChange> page, HistoryChange historyChange) {
		// 查询条件： 开始时间
		if (historyChange.getCreateTimeStart() != null) {
			historyChange.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyChange.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (historyChange.getCreateTimeEnd() != null) {
			historyChange.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyChange.getCreateTimeEnd())));
		}

		if (null != UserUtils.getUser()) {
			Office office = UserUtils.getUser().getOffice();
			if (StringUtils.isBlank(office.getType())) {
				throw new BusinessException("message.E5004", "", new String[] {});
			}
			switch (office.getType()) {
			case Constant.OfficeType.CLEAR_CENTER:
				historyChange.setVinOffice(office);
				break;
			case Constant.OfficeType.STORE:
				historyChange.setaOffice(office);
				break;
			default:
				historyChange.getSqlMap().put("dsf",
						"AND (do.parent_ids LIKE '%" + office.getId() + "%' OR do.ID = " + office.getId() + ")");
			}
		}
		historyChange.setPage(page);
		page.setList(historyChangeDao.findEquiHistoryChangeList(historyChange));
		return page;
	}

	/**
	 * 获取机具历史更换记录明细
	 *
	 * @param page
	 * @param historyChange
	 * @return
	 * @author ZXK
	 * @version 2019年10月30日
	 */
	public Page<HistoryChange> gitHistoryChangePageDetail(Page<HistoryChange> page, HistoryChange historyChange) {
		// 查询条件： 开始时间
		if (historyChange.getCreateTimeStart() != null) {
			historyChange.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyChange.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (historyChange.getCreateTimeEnd() != null) {
			historyChange.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyChange.getCreateTimeEnd())));
		}
		historyChange.setPage(page);
		page.setList(historyChangeDao.getHistoryChangePageDetail(historyChange));
		return page;
	}

	/**
	 * 
	 * @author ZXK
	 * @version 所有数据取得（Excel下载用）
	 * 
	 * @param CustWorkDay
	 */
	public List<HistoryChange> findExcelList(HistoryChange historyChange) {

		// 查询条件： 开始时间
		if (historyChange.getCreateTimeStart() != null) {
			historyChange.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyChange.getCreateTimeStart())));
		}

		// 查询条件： 结束时间
		if (historyChange.getCreateTimeEnd() != null) {
			historyChange.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyChange.getCreateTimeEnd())));
		}

		return historyChangeDao.findEquiHistoryChangeList(historyChange);
	}

	public List<HistoryChange> findExcelListDetail(HistoryChange historyChange) {

		// 查询条件： 开始时间
		if (historyChange.getCreateTimeStart() != null) {
			historyChange.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyChange.getCreateTimeStart())));
		}

		// 查询条件： 结束时间
		if (historyChange.getCreateTimeEnd() != null) {
			historyChange.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyChange.getCreateTimeEnd())));
		}

		return historyChangeDao.getHistoryChangePageDetail(historyChange);
	}

}
