package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearAddMoney;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清机加钞记录DAO接口
 * 
 * @author ZXK
 * @version 2019-07-23
 */
@MyBatisDao
public interface ClearAddMoneyDao extends CrudDao<ClearAddMoney> {

	/**
	 * 根据机具编号查询单条数据
	 *
	 * @author zhr
	 * @param equipmentId
	 * @return
	 */
	public List<ClearAddMoney> getByEquipmentId(@Param("equipmentId") String equipmentId, @Param("type") String type,
			@Param("id") String id);
	
	/**
	 * 根据机具编号查询单条数据
	 *
	 * @author ZXK
	 * @version 2020-6-2
	 * @param clearAddMoney
	 * @return
	 */
	public List<ClearAddMoney> getDepositSerialList(ClearAddMoney clearAddMoney);
}
