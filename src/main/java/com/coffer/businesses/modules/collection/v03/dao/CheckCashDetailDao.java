package com.coffer.businesses.modules.collection.v03.dao;

import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashDetail;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 款箱拆箱每笔明细表DAO接口
 * @author wl
 * @version 2017-02-13
 */
@MyBatisDao
public interface CheckCashDetailDao extends CrudDao<CheckCashDetail> {
	

	/**
	 * 逻辑删除
	 * 
	 * @author wanglin
	 * @date 2017-09-02
	 * 
	 * @Description
	 * @param checkCashAmount
	 * @return
	 */
	public int logicDelete(CheckCashAmount checkCashAmount);
	
}

