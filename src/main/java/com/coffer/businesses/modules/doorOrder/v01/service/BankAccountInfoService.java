package com.coffer.businesses.modules.doorOrder.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.service.CityService;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.entity.BankAccountInfo;
import com.coffer.businesses.modules.doorOrder.v01.dao.BankAccountInfoDao;

/**
 * 银行卡管理Service
 * @author yinkai
 * @version 2019-08-06
 */
@Service
@Transactional(readOnly = true)
public class BankAccountInfoService extends CrudService<BankAccountInfoDao, BankAccountInfo> {
	
	@Autowired
	private CityService cityService;
	
	@Autowired
	private BankAccountInfoDao bankAccountInfoDao;

	public BankAccountInfo get(String id) {
		return super.get(id);
	}
	
	public List<BankAccountInfo> findList(BankAccountInfo bankAccountInfo) {
		return super.findList(bankAccountInfo);
	}
	
	public Page<BankAccountInfo> findPage(Page<BankAccountInfo> page, BankAccountInfo bankAccountInfo) {
		return super.findPage(page, bankAccountInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BankAccountInfo bankAccountInfo) {
		if (bankAccountInfo.getIsNewRecord()){
			bankAccountInfo.preInsert();
			bankAccountInfo.setCityName(cityService.findCityName(bankAccountInfo.getCityCode()));
			bankAccountInfo.setStatus(Constant.BankStatus.NOBIND); 
			dao.insert(bankAccountInfo);
		}else{
			bankAccountInfo.preUpdate();
			bankAccountInfo.setCityName(cityService.findCityName(bankAccountInfo.getCityCode()));
			dao.update(bankAccountInfo);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BankAccountInfo bankAccountInfo) {
		super.delete(bankAccountInfo);
	}
	/**
	 * @author zxk
	 * 2019-8-15
	 * 改变银行卡绑定状态
	 * @param bankAccountInfo
	 */
	@Transactional(readOnly = false)
	public void changeStatus(BankAccountInfo bankAccountInfo) {
		bankAccountInfoDao.changeStatus(bankAccountInfo);
	}
	
	/**
	 * @author zxk
	 * 2019-8-16
	 * 绑定银行卡时检验该商户是否已被绑定
	 * @param bankAccountInfo
	 */
	public List<BankAccountInfo> findByMerchantAndStatus(BankAccountInfo bankAccountInfo){
		return bankAccountInfoDao.findByMerchantAndStatus(bankAccountInfo);
	}
}