package com.coffer.businesses.modules.clear.v03.dao;

import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 柜员账务DAO接口
 * @author xl
 * @version 2017-10-23
 */
@MyBatisDao
public interface TellerAccountsMainDao extends CrudDao<TellerAccountsMain> {
	
	/**
	 * 查询柜员账务
	 * 
	 * @author XL
	 * @version 2017-10-24
	 * @param tellerAccountsMain
	 * @return
	 */
	public List<TellerAccountsMain> findTellerAccountsList(TellerAccountsMain tellerAccountsMain);

}