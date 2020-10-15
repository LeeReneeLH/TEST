package com.coffer.businesses.modules.collection.v03.dao;

import com.coffer.businesses.modules.collection.v03.entity.TaskDown;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 预约明细DAO接口
 * @author wanglin
 * @version 2017-02-13
 */
@MyBatisDao
public interface TaskDownDao extends CrudDao<TaskDown> {
	

	/**
	 * 任务分配
	 * @author wanglin
	 * @version 2016年12月01日
	 * @param 
	 * @return 
	 */
	public int taskAllot(TaskDown taskDown);

	
}

