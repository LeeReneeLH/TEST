package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoAddCashGroup;
import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 加钞组管理DAO接口
 * 
 * @author wanglu
 * @version 2017-11-10
 */
@MyBatisDao
public interface StoAddCashGroupDao extends CrudDao<StoAddCashGroup> {

	/**
	 * @author wanglu
	 * @date 2017-11-10
	 *
	 *       条件查询加钞组
	 */
	public List<StoAddCashGroup> getStoAddCashGroupList(@Param("stoAddCashGroup") StoAddCashGroup stoAddCashGroup,
			@Param("dbName") String dbName);

	/**
	 * @author wanglu
	 * @date 2017-11-13
	 *
	 *       通过GroupId获取单个加钞组信息
	 */
	public StoAddCashGroup getSingleStoAddCashGroupInfo(@Param("stoAddCashGroup") StoAddCashGroup stoAddCashGroup);

	/**
	 * @author sg
	 * @date 2017-11-17
	 *
	 *       通过GroupId获取单个未删除加钞组信息
	 */
	public StoAddCashGroup getSingleStoAddCashGroupInfoNoDel(@Param("stoAddCashGroup") StoAddCashGroup stoAddCashGroup);

	/**
	 * @author wanglu
	 * @date 2017-11-13
	 *
	 *       获取押运车辆信息，排除已分配押运人的押运车辆
	 */
	public List<StoCarInfo> getUnSetCarInfo(Map<String, Object> parameter);

	/**
	 * @author wanglu
	 * @date 2017-11-13
	 *
	 *       获取未分配车辆的押运员
	 */
	public List<StoEscortInfo> getUnCheeseEscortInfo(Map<String, Object> parameter);
}
