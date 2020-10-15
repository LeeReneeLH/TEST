package com.coffer.businesses.modules.collection.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.businesses.modules.collection.v03.entity.CustUser;
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
public interface CustUserDao extends CrudDao<CustUser> {
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年11月10日
	 * 
	 * 
	 * @param office
	 * @return
	 */
	public List<CustUser> checkCustUserCode(CustUser custUser);

	/**
	 * 商户下拉列表
	 */
	List<SelectItem> findStoreSelect(StoreOffice storeOffice);
	
	/**
	 * 根据登录名获取用户
	 * 
	 * @param CustUser
	 * @return
	 */
	public CustUser getUserByLoginName(CustUser custUser);
	
	/**
	 * 根据身份证取得人员信息
	 * 
	 * @param CustUser
	 * @return
	 */
	public CustUser findByIdcardNo(CustUser custUser);

	
	
	
	
}
