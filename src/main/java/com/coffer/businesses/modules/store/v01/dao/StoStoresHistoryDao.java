package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 库存流水管理DAO接口
 * 
 * @author LLF
 * @version 2015-09-10
 */
@MyBatisDao
public interface StoStoresHistoryDao extends CrudDao<StoStoresHistory> {

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月14日 根据盘点编号更新库存流水状态
	 * 
	 * @param stockCountId
	 * @return
	 */
	public int updateById(StoStoresHistory stoStoresHistory);

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月23日
	 * 
	 *          根据盘点日期，判断在盘点后是否存在库存变更
	 * @param stoStoresHistory
	 *            需要office和盘点日期
	 * @return
	 */
	public List<StoStoresHistory> getStoreAfterStock(StoStoresHistory stoStoresHistory);
}