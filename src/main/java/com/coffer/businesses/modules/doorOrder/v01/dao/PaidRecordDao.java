package com.coffer.businesses.modules.doorOrder.v01.dao;

import com.coffer.businesses.modules.doorOrder.v01.entity.PaidRecord;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 汇款记录保存DAO接口
 * @author WQJ
 * @version 2019-08-14
 */
@MyBatisDao
public interface PaidRecordDao extends CrudDao<PaidRecord> {
	
}