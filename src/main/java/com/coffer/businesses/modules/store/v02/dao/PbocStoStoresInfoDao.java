package com.coffer.businesses.modules.store.v02.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 人民银行库存管理DAO接口
 * @author chengshu
 * @version 2016-05-18
 */
@MyBatisDao
public interface PbocStoStoresInfoDao extends CrudDao<PbocStoStoresInfo> {
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年9月22日
	 * 
	 *  获取最新库存日期信息
	 * @return
	 */
	public Date getMaxStoreDate(PbocStoStoresInfo stoStoresInfo);

	/**
	 * 根据物品Id取得最新的库存信息
	 * 
	 * @author niguoyong
	 * @return
	 */
	public PbocStoStoresInfo getPbocStoStoresInfoByGoodsId(@Param("goodsId") String goodsId,
			@Param("officeId") String officeId, @Param("excludeZeroFg") String excludeZeroFg,
			@Param("dbName") String dbName);
}