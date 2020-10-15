package com.coffer.businesses.modules.quartz.dao;

import java.util.List;

import com.coffer.businesses.modules.quartz.entity.Quartz;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
/**
 * 任务调度器DAO接口
 * @author wangpengyu
 * @date 2019-12-03
 */
@MyBatisDao
public interface  QuartzDao extends CrudDao<Quartz> {
	/**
	 * 
	 * Title: selectAll
	 * <p>
	 * Description: 
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return List<Quartz> 返回类型
	 */
	List<Quartz> selectAll();
	/**
	 * 
	 * Title: findAllList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return List<Quartz> 返回类型
	 */
	List<Quartz> findAllList();
	/**
	 * 
	 * Title: getJob
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return Quartz 返回类型
	 */
	Quartz getJob(Quartz quartz);
	/**
	 * 
	 * Title: pauseJob
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return 
	 */
	void pauseJob(Quartz quartz);
	/**
	 * 
	 * Title: resumeJob
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return 
	 */
	void resumeJob(Quartz quartz);
	
	/**
	 * 
	 * Title: removeJob
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return 
	 */
	void removeJob(String jobName);
	/**
	 * 
	 * Title: findByTaskName
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return 
	 */
	Quartz findByTaskName(String taskName);
	/**
	 * 
	 * Title: updateByPrimaryKeySelective
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return 
	 */
	void updateByPrimaryKeySelective(Quartz quartz);
	/**
	 * 
	 * Title: updateByPrimaryKeySelective
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return 
	 */
	List<Quartz> selectForSearch(Quartz quartz);
	/**
	 * 
	 * Title: selectByPrimaryKey
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @param 
	 * @return 
	 */
	Quartz selectByPrimaryKey(String id);
	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: HuZhiYong
	 * @param 
	 * @return 
	 */
	List<Quartz> selectByClass(Quartz quartz);
}
