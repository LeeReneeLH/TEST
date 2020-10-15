package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.ClTaskDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 清分管理DAO接口
 * @author QPH
 * @version 2017-08-15
 */
@MyBatisDao
public interface ClTaskMainDao extends CrudDao<ClTaskMain> {

	/**
	 * 根据任务编号查询任务分配
	 * 
	 * @author qipeihong
	 * @version 2017年8月28日
	 * 
	 * 
	 * @param taskNo
	 *            查询条件 任务编号
	 * @return
	 * 
	 */
	public ClTaskMain get(String taskNo);

	/**
	 * @author qipeihong
	 * @version 2017年11月20日
	 * 
	 * 
	 * @param cltaskmain
	 * @return
	 */

	public List<ClTaskDetail> getClearGroupByUserId(ClTaskMain clTaskMain);
	
	
}