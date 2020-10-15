package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.doorOrder.v01.dao.ClearEquipmentRecordDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearEquipmentRecord;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;

/**
 * 清机记录Service
 *
 * @author gzd
 * @version 2020-06-03
 */
@Service
@Transactional(readOnly = true)
public class ClearEquipmentRecordService extends CrudService<ClearEquipmentRecordDao, ClearEquipmentRecord> {

	@Autowired
	private ClearEquipmentRecordDao clearEquipmentRecordDao;
	
	/**
	 * 获取清机记录（缴存）
	 *
	 * @param page
	 * @param clearEquipmentRecord
	 * @return
	 * @author gzd
	 * @version 2020年05月27日
	 */
	public Page<ClearEquipmentRecord> getClearEquipmentRecordPage(Page<ClearEquipmentRecord> page, ClearEquipmentRecord clearEquipmentRecord) {
		// 查询条件： 开始时间
		if (clearEquipmentRecord.getCreateTimeStart() != null) {
			clearEquipmentRecord.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clearEquipmentRecord.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clearEquipmentRecord.getCreateTimeEnd() != null) {
			clearEquipmentRecord.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearEquipmentRecord.getCreateTimeEnd())));
		}
		clearEquipmentRecord.setPage(page);
		page.setList(clearEquipmentRecordDao.getClearEquipmentRecordPage(clearEquipmentRecord));
		return page;
	}

	/**
	 * 
	 * @author gzd
	 * @version 所有数据取得（Excel下载用）
	 * 
	 * @param clearEquipmentRecord
	 */
	public List<ClearEquipmentRecord> findExcelList(ClearEquipmentRecord clearEquipmentRecord) {

		// 查询条件： 开始时间
		if (clearEquipmentRecord.getCreateTimeStart() != null) {
			clearEquipmentRecord.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clearEquipmentRecord.getCreateTimeStart())));
		}

		// 查询条件： 结束时间
		if (clearEquipmentRecord.getCreateTimeEnd() != null) {
			clearEquipmentRecord.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearEquipmentRecord.getCreateTimeEnd())));
		}

		return clearEquipmentRecordDao.getClearEquipmentRecordPage(clearEquipmentRecord);
	}

}
