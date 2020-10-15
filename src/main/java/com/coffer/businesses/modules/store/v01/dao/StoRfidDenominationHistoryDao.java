package com.coffer.businesses.modules.store.v01.dao;

import com.coffer.businesses.modules.store.v01.entity.StoRfidDenominationHistory;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * RFID面值绑定信息历史表DAO接口
 * @author yuxixuan
 * @version 2015-09-11
 */
@MyBatisDao
public interface StoRfidDenominationHistoryDao extends CrudDao<StoRfidDenominationHistory> {

	/** 插入新信息 **/
	public int insert(StoRfidDenominationHistory record);
}