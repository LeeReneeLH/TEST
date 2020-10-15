package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 款箱拆箱每笔明细表DAO接口
 * @author wl
 * @version 2017-02-13
 */
@MyBatisDao
public interface CheckCashAmountDao extends CrudDao<CheckCashAmount> {
	

	/**
	 * @author wanglin
	 * @date 2015-09-01
	 *
	 *       根据单号取得最大行号
	 */
	public long getMaxOutRowNo(String outNo);

	/**
	 * @author wanglin
	 * @date 2015-09-01
	 *
	 *       查询未拆箱数 
	 */
	public long findNoBoxCount(String outNo);
	
	
	
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
	
	/**
	 * 
	 * @author wanglin
	 * @date 2017年9月4日
	 * 
	 *          款箱拆箱明细数据取得(PDA)
	 * @param CheckCashAmount
	 * 
	 * @return
	 */
	public List<CheckCashAmount> pdaFindList(CheckCashAmount checkCashAmount);
	
	/**
	 * 删除所有录入金额明细(逻辑删除)
	 *
	 * @author XL
	 * @version 2019年7月11日
	 * @param checkCashAmount
	 * @return
	 */
	public int inputLogicDelete(CheckCashAmount checkCashAmount);
	
}

