package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupDoorInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清机组门店明细DAO接口
 * 
 * @author ZXK
 * @version 2019-07-25
 */
@MyBatisDao
public interface ClearGroupDoorInfoDao extends CrudDao<ClearGroupDoorInfo>{

	/**
	 * 根据清机编号删除 清机门店明细相关记录
	 * @param ClearGroupId
	 * @return
	 */
	public int deleteByClearGroupId(String clearGroupId);
	
	/**
	 * 查询清机门店明细表中是否有相应门店
	 * @param doorId
	 * @return
	 */
	public List<ClearGroupDoorInfo> checkDoorsList(ClearGroupDoorInfo clearGroupDoorInfo);
}
