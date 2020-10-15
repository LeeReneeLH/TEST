package com.coffer.businesses.modules.doorOrder.v01.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanUserDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清机任务人员明细DAO接口
 *
 * @author XL
 * @version 2019-08-12
 */
@MyBatisDao
public interface ClearPlanUserDetailDao extends CrudDao<ClearPlanUserDetail> {
	
	/**
	 * 根据清机计划编号删除人员明细
	 *
	 * @author XL 
	 * @version 2019年8月13日 
	 * @return
	 */
	public int deleteByPlanId(@Param("planId") String planId);

}