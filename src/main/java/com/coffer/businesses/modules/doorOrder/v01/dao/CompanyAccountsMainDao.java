package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.businesses.modules.doorOrder.v01.entity.CompanyAccountsMain;
import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;

/**
 * 公司账务DAO接口
 * @author XL
 * @version 2019-06-26
 */
@MyBatisDao
public interface CompanyAccountsMainDao extends CrudDao<CompanyAccountsMain> {
	
	/**
	 * 
	 * @author wqj
	 * @version 2019年7月4日
	 * 
	 *          根据公司ID查询账务余额信息
	 * @param CompanyAccountsMain
	 * 
	 * @return
	 */
	public List<CompanyAccountsMain> getAccountByCompanyId(CompanyAccountsMain companyAccountsMain);
	
	/**
	 * 
	 * @author wqj
	 * @version 2019年7月17日
	 * 
	 *          根据公司ID查询代付金额
	 * @param CompanyAccountsMain
	 * 
	 * @return
	 */
	 public CompanyAccountsMain getCompanyPaidAmount(CompanyAccountsMain companyAccountsMain);
	 
	/**
	 * 
	 * @author wqj
	 * @version 2019年7月17日
	 * 
	 *          根据公司ID查询回款金额
	 * @param CompanyAccountsMain
	 * 
	 * @return
	 */
	public CompanyAccountsMain getCompanyBackAmount(CompanyAccountsMain companyAccountsMain);
	
	/**
	 * 
	 * @author wqj
	 * @version 2019年7月29日
	 * 
	 *          根据公司ID查询平台存款总金额
	 * @param CompanyAccountsMain
	 * 
	 * @return
	 */
	public CompanyAccountsMain getHaveSaveAmount(CompanyAccountsMain companyAccountsMain);
}