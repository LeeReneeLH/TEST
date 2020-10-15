package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.ClTaskDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清分管理明细DAO接口
 * @author QPH
 * @version 2017-08-15
 */
@MyBatisDao
public interface ClTaskDetailDao extends CrudDao<ClTaskDetail> {
	/**
	 * 根据任务编号查询任务分配详细
	 * 
	 * @author qipeihong
	 * @version 2017年8月28日
	 * 
	 * 
	 * @param mId
	 *            查询条件 任务编号
	 * @return
	 * 
	 */
	public List<ClTaskDetail> getByMid(String mId);
}