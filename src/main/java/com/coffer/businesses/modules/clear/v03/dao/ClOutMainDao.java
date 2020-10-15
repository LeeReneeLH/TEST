package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 商行取款DAO接口
 * 
 * @author wxz
 * @version 2017-08-24
 */
@MyBatisDao
public interface ClOutMainDao extends CrudDao<ClOutMain> {

	/**
	 * 状态 更新(1：登记，2：冲正)
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param 商行取款(ClOutMain)
	 * @return
	 */
	public void updateStatus(ClOutMain clOutMain);

	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:代理上缴
	 * </p>
	 * 
	 * @author: sg
	 * @param ClOutMain
	 * @return OrderClearMain 返回类型
	 */
	public ClOutMain findClearList(ClOutMain clOutMain);

	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:商行取款
	 * </p>
	 * 
	 * @author: sg
	 * @param ClOutMain
	 * @return OrderClearMain 返回类型
	 */
	public ClOutMain findClearLists(ClOutMain clOutMain);

	/**
	 * 
	 * Title: findPeopleList
	 * <p>
	 * Description:代理上缴
	 * </p>
	 * 
	 * @author: sg
	 * @param ClOutMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public List<ClOutMain> findPeopleList(ClOutMain clOutMain);

	/**
	 * 
	 * Title: findPeopleList
	 * <p>
	 * Description:代理上缴（统计图）
	 * </p>
	 * 
	 * @author: sg
	 * @param ClOutMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public List<ClOutMain> findPeopleByDayList(ClOutMain clOutMain);
}