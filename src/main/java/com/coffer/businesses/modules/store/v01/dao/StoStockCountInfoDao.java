package com.coffer.businesses.modules.store.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.store.v01.entity.StoStockCountInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 盘点管理DAO接口
 * 
 * @author LLF
 * @version 2015-09-22
 */
@MyBatisDao
public interface StoStockCountInfoDao extends CrudDao<StoStockCountInfo> {

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月23日
	 * 
	 *          获取当日最新盘点信息
	 * @param stoStockCountInfo
	 * @return
	 */
	public List<StoStockCountInfo> getMaxStockNoToday(StoStockCountInfo stoStockCountInfo);
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年10月19日
	 * 
	 *  盘点更新库存
	 * @param stoStockCountInfo
	 * @return
	 */
	public int updateStockCountByStockNo(StoStockCountInfo stoStockCountInfo);
}