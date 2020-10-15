package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.OrderClearDetail;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 预约清分明细表DAO接口
 * @author wanglin
 * @version 2017-08-07
 */
@MyBatisDao
public interface OrderClearDetailDao extends CrudDao<OrderClearDetail>{
	/**
	 * 通过机构ID查询面值合计
	 * 
	 * @author wanglin
	 * @version 2017年7月11日
	 * @param orderClearDetail
	 * @return
	 */
	public List<OrderClearDetail> findListByOffice(OrderClearMain orderClearMain);
	
}
