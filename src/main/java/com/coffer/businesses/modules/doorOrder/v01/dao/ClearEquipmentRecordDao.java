package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearEquipmentRecord;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清机记录DAO接口
 * 
 * @author gzd
 * @version 2020-06-03
 */
@MyBatisDao
public interface ClearEquipmentRecordDao extends CrudDao<ClearEquipmentRecord> {
		
	/**
	 * 根据机具id清机记录（缴存）列表
	 *
	 * @author gzd
	 * @version 2020-05-27
	 * @param clearEquipmentRecord
	 * @return
	 */
	public List<ClearEquipmentRecord> getClearEquipmentRecordPage(ClearEquipmentRecord clearEquipmentRecord);
	
	/**
	 * 
	 * @author zxk
	 * @date 2019-10-31
	 * 
	 *          Excel下载数据的取得
	 * @param custWorkDay
	 * 
	 * @return
	 */
	public List<ClearEquipmentRecord> findExcelList(ClearEquipmentRecord clearEquipmentRecord);
}
