package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.businesses.modules.collection.v03.entity.ShopOffice;
import com.coffer.businesses.modules.collection.v03.entity.StoreOffice;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;


/**
 * 门店DAO接口
 * 
 * @author wanglin
 * @version 2017-11-13
 */
@MyBatisDao
public interface ShopOfficeDao extends CrudDao<ShopOffice> {
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 * 
	 * @param office
	 * @return
	 */
	public List<ShopOffice> checkShopOfficeCode(ShopOffice shopOffice);

	/**
	 * 商户下拉列表
	 */
	List<SelectItem> findStoreSelect(StoreOffice storeOffice);
	
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
	
}
