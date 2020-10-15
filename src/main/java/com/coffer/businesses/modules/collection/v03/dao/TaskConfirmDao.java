package com.coffer.businesses.modules.collection.v03.dao;

import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 预约明细DAO接口
 * @author wl
 * @date 2017-02-13
 */
@MyBatisDao
public interface TaskConfirmDao extends CrudDao<TaskConfirm> {
	

	/**
	 * 分配状态的更新
	 * @author WL
	 * @date 2016年12月01日
	 * @param 
	 * @return 
	 */
	public int updateAllotStatus(TaskConfirm taskConfirm);

	/**
	 * 状态的更新
	 * @author WL
	 * @date 2016年12月01日
	 * @param 
	 * @return 
	 */
	public int updateStatus(TaskConfirm taskConfirm);
	
	
	
	
}

