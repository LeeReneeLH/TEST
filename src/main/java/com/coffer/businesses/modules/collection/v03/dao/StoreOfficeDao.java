package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.StoreOffice;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;


/**
 * 商户DAO接口
 * 
 * @author wanglin
 * @version 2017-11-13
 */
@MyBatisDao
public interface StoreOfficeDao extends CrudDao<StoreOffice> {

	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          门店删除
	 * @param storeOffice
	 * @return
	 */
	public int shopDelete(StoreOffice storeOffice);
	
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          商户编码检查
	 * @param storeOffice
	 * @return
	 */
	public List<StoreOffice> checkStoreOfficeCode(StoreOffice storeOffice);

	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          查询有效用户数
	 * @param id
	 * @return 有效用户数
	 */
	public int getVaildCntByOfficeId(String id);
	

	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 *          门店启用标识的更新(禁用)
	 * @param storeOffice
	 * @return
	 */
	public int updShopEnabledFlag(StoreOffice storeOffice);
	

}
