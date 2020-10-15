package com.coffer.businesses.modules.collection.v03.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.collection.v03.dao.TaskDownDao;
import com.coffer.businesses.modules.collection.v03.entity.TaskDown;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;


/**
 * 门店预约Service
 * @author wl
 * @version 2017-02-13
 */
@Service
@Transactional(readOnly = true)
public class TaskDownService extends CrudService<TaskDownDao, TaskDown> {
	
	
	@Autowired
	private TaskDownDao taskDownDao;
	
	public TaskDown get(String id) {
		TaskDown taskDown  = super.get(id);
		return taskDown;
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 一览页面数据
	 * @param TaskDown
	 * @return
	 */
	public List<TaskDown> findList(TaskDown taskDown) {
		return super.findList(taskDown);
	}
	
	public Page<TaskDown> findPage(Page<TaskDown> page, TaskDown taskDown) {
		// 数据范围过滤
		taskDown.getSqlMap().put("dsf", dataScopeFilter(taskDown.getCurrentUser(), "o", null));
		// 查询条件： 开始时间
		if (taskDown.getCreateTimeStart() != null) {
			taskDown.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(taskDown.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (taskDown.getCreateTimeEnd() != null) {
			taskDown.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(taskDown.getCreateTimeEnd())));
		}
		return super.findPage(page, taskDown);
	}


	/**
	 * 
	 * @author wanglin
	 * @version 2015年10月10日
	 * 任务分配
	 * @param TaskDown 
	 * @return
	 */
	@Transactional(readOnly = false)
	public void taskAllot(TaskDown taskDown) {
		taskDownDao.taskAllot(taskDown);
	}

	
}