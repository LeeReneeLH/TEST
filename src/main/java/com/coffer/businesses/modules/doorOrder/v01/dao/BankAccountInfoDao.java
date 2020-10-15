package com.coffer.businesses.modules.doorOrder.v01.dao;

import java.util.List;

import com.coffer.core.common.persistence.CrudDao;
import com.coffer.core.common.persistence.annotation.MyBatisDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.BankAccountInfo;

/**
 * 银行卡管理DAO接口
 * @author yinkai
 * @version 2019-08-06
 */
@MyBatisDao
public interface BankAccountInfoDao extends CrudDao<BankAccountInfo> {
	
	/**
	 * @author zxk
	 * 2019-8-15
	 * 改变银行卡绑定状态
	 * @param bankAccountInfo
	 */
	public void changeStatus(BankAccountInfo bankAccountInfo);
	
	/**
	 * @author zxk
	 * 2019-8-16
	 * 绑定银行卡时检验该商户是否已被绑定
	 * @param bankAccountInfo
	 */
	public List<BankAccountInfo> findByMerchantAndStatus(BankAccountInfo bankAccountInfo);
}