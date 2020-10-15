package com.coffer.businesses.modules.store.v01.dao;

import java.math.BigDecimal;

import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 物品管理DAO接口
 * @author Ray
 * @version 2015-09-10
 */
@MyBatisDao
public interface StoGoodsDao extends CrudDao<StoGoods> {

	/**
	 * 计算物品价值
	 * 
	 * @param stoRelevance
	 * @return
	 */
	BigDecimal calcGoodsVal(StoRelevance stoRelevance);

	/**
	 * 恢复逻辑删除的物品
	 * 
	 * @param stoGoods
	 * @return
	 */
	int recover(StoGoods stoGoods);
}