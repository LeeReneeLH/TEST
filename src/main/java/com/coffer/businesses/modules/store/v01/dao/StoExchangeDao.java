package com.coffer.businesses.modules.store.v01.dao;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v01.entity.StoExchange;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 兑换管理DAO接口
 * 
 * @author niguoyong
 * @version 2015年9月21日
 */
@MyBatisDao
public interface StoExchangeDao extends CrudDao<StoExchange> {

	/**
	 * 明细信息查询
	 * 
	 * @param id 兑换ID
	 * @return
	 */
	StoExchange getDetailById(@Param("exchengId")String exchengId);

}
