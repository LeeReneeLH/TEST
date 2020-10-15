package com.coffer.businesses.modules.clear.v03.dao;

import com.coffer.businesses.modules.clear.v03.entity.ClAtmAmount;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * ATM钞箱拆箱每笔面值表DAO接口
 * @author wl
 * @version 2017-02-13
 */
@MyBatisDao
public interface ClAtmAmountDao extends CrudDao<ClAtmAmount> {
	
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
	public int logicDelete(ClAtmAmount checkCashAmount);
	
	
	
}

