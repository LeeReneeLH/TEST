package com.coffer.businesses.modules.clear.v03.dao;

import java.math.BigDecimal;
import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 预约清分DAO接口
 * 
 * @author wanglin
 * @version 2015-05-11
 */
@MyBatisDao
public interface OrderClearMainDao extends CrudDao<OrderClearMain> {

	/**
	 * 状态 更新(1：登记，2：接收)
	 * 
	 * @author wanglin
	 * @version 2017年7月11日
	 * @param 预约清分(orderClearMain)
	 * @return
	 */
	public void updateStatus(OrderClearMain orderClearMain);

	/**
	 * 
	 * 
	 * @author
	 * @version 2017年7月11日
	 * @param
	 * @return
	 */
	public List<OrderClearMain> getDetailList(OrderClearMain orderClearMain);

	/**
	 * 获取当日预约金额
	 * 
	 * @author wanglin
	 * @version 2017年7月11日
	 * @param 预约清分(orderClearMain)
	 * @return
	 */
	public BigDecimal getToDayAmount(OrderClearMain orderClearMain);

	/**
	 * 
	 * Title: reserveClearGraph
	 * <p>
	 * Description: 预约清分统计图做成
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public List<OrderClearMain> reserveClearGraph(OrderClearMain orderClearMain);

	/**
	 * 
	 * Title: findClearList
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: sg
	 * @param orderClearMain
	 * @return List<OrderClearMain> 返回类型
	 */
	public OrderClearMain findClearList(OrderClearMain orderClearMain);

	/**
	 * 查询当日微信端预约信息
	 * 
	 * @version 2018-05-21
	 * @author XL
	 * @param orderClearMain
	 * @return OrderClearMain
	 */
	public OrderClearMain getByDateAndOffice(OrderClearMain orderClearMain);

}