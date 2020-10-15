package com.coffer.businesses.modules.doorOrder.v01.dao;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupUserInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清机组人员明细DAO接口
 * 
 * @author ZXK
 * @version 2019-07-25
 */
@MyBatisDao
public interface ClearGroupUserInfoDao extends CrudDao<ClearGroupUserInfo>{

	/**
	 * 根据清机编号删除 清机组人员相关记录
	 * @param clearGroupId
	 * @return
	 */
	public int deleteByClearGroupId(String clearGroupId);
}
