package com.coffer.businesses.modules.store.v01.dao;

import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 车辆管理DAO接口
 * @author LLF
 * @version 2017-07-30
 */
@MyBatisDao
public interface StoCarInfoDao extends CrudDao<StoCarInfo> {
	
	/**
	 * 根据车牌编号查询车辆信息
	 * 
	 * @author XL
	 * @version 2018-09-06
	 * @param stoCarInfo
	 * @return
	 */
	public StoCarInfo getByCarNo(StoCarInfo stoCarInfo);
	
}