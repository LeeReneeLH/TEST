package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;


/**
 * 清机组DAO接口
 * 
 * @author ZXK
 * @version 2019-07-24
 */
@MyBatisDao
public interface ClearGroupMainDao extends CrudDao<ClearGroupMain>{

	/**
	 * 根据清机组名称查询列表
	 * ZXK  
	 * @param clearGroupMain
	 * @return
	 */
	List<ClearGroupMain> findListByName(ClearGroupMain clearGroupMain);

    /**
     * 设备号查找请机组
     * @param eqpId
     * @return
     * @author yinkai
     */
    ClearGroupMain getGroupByEquipment(@Param("eqpId") String eqpId);

	/**
	 * 验证当前清机人员是否有清机组且门店被该清机组维护
	 * @param userId
	 * @param doorId
	 * @return
	 */
	ClearGroupMain getGroupInfoByDoorIdAndUserId(@Param("userId")String userId,@Param("doorId")String doorId);
}
