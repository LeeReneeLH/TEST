package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClearAccountAdjustInfoDao;
import com.coffer.businesses.modules.clear.v03.dao.TellerAccountsMainDao;
import com.coffer.businesses.modules.clear.v03.entity.ClearAccountAdjustInfo;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoEscortInfoDao;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 柜员调账Service
 * 
 * @author dja
 * @version 2018-04-20
 */
@Service
@Transactional(readOnly = true)
public class ClearAccountAdjustInfoService extends CrudService<ClearAccountAdjustInfoDao, ClearAccountAdjustInfo> {

	@Autowired
	private ClearAccountAdjustInfoDao clearAccountAdjustInfoDao;
	
	/**获得柜员账务信息*/
	@Autowired
	private TellerAccountsMainDao tellerAccountsMainDao;
	
	/**获得人员信息*/
	@Autowired
	private StoEscortInfoDao stoEscortInfoDao;

	@Override
	public ClearAccountAdjustInfo get(String accountId) {
		return super.get(accountId);
	}

	public Page<ClearAccountAdjustInfo> findPage(Page<ClearAccountAdjustInfo> page, ClearAccountAdjustInfo clearAccountAdjustInfo) {
		// 查询条件：开始时间		
		clearAccountAdjustInfo.getSqlMap().put("dsf", dataScopeFilter(clearAccountAdjustInfo.getCurrentUser(), "o", null));
		return super.findPage(page, clearAccountAdjustInfo);
	}

	public List<ClearAccountAdjustInfo> findList(ClearAccountAdjustInfo clearAccountAdjustInfo) {
		return super.findList(clearAccountAdjustInfo);
	}
	
	public List<TellerAccountsMain>findTellerAccounts(TellerAccountsMain tellerAccountsMain){
		tellerAccountsMain.getSqlMap().put("dsf", dataScopeFilter(tellerAccountsMain.getCurrentUser(), "o", null));
		return tellerAccountsMainDao.findTellerAccountsList(tellerAccountsMain);
	}
	
	/**
	 * 保存登记信息
	 * 
	 * @author dja
	 * @version 2018年4月23日
	 * @param ClearAccountAdjustInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void save(ClearAccountAdjustInfo clearAccountAdjustInfo) {
		// 生成调账单号
		clearAccountAdjustInfo.setAccountsId(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.ADJUST,
				clearAccountAdjustInfo.getCurrentUser().getOffice()));
		// 金额类型: （01：备付金，02：非备付金）
		clearAccountAdjustInfo.setCashType(clearAccountAdjustInfo.getCashType());
		// 状态：（1：登记）
		clearAccountAdjustInfo.setStatus(ClearConstant.StatusType.CREATE);
		// 交款人
		clearAccountAdjustInfo.setPayTellerName(stoEscortInfoDao.get(clearAccountAdjustInfo.getPayTellerBy()).getEscortName());
		// 收款人 
		clearAccountAdjustInfo.setReTellerName(stoEscortInfoDao.get(clearAccountAdjustInfo.getReTellerBy()).getEscortName());
		// 备注
		clearAccountAdjustInfo.setRemarks(clearAccountAdjustInfo.getRemarks());
		// 插入登记机构信息
		User user = UserUtils.getUser();
		Office office = user.getOffice();
		clearAccountAdjustInfo.setOffice(office);
		// 调账金额
		clearAccountAdjustInfo.setAdjustMoney(new BigDecimal(clearAccountAdjustInfo.getAdjustMoney().toString().replace(",", "")));
		// 插入
		clearAccountAdjustInfo.preInsert();
		clearAccountAdjustInfoDao.insert(clearAccountAdjustInfo);		
		// 交款人柜员账务 
		TellerAccountsMain tellerAccountsMainPay = this.getPayTeller(clearAccountAdjustInfo);		
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMainPay);		
		// 收款人柜员账务 
		TellerAccountsMain tellerAccountsMainRe = this.getReTeller(clearAccountAdjustInfo);	
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMainRe);
	}
	
	/**
	 * 
	 * @author dja
	 * @version 生成交款人柜员账务流水
	 * @version 2018-04-24
	 * @param businessType
	 * @param payTellerBy
	 * @return
	 */
	@Transactional(readOnly = false)
	private TellerAccountsMain getPayTeller(ClearAccountAdjustInfo clearAccountAdjustInfo) {
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
			// 设置交款柜员ID
			tellerAccountsMain.setTellerBy(clearAccountAdjustInfo.getPayTellerBy());
			// 设置交款柜员姓名
			tellerAccountsMain.setTellerName(clearAccountAdjustInfo.getPayTellerName());
			// 设置交款柜员类型
			tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clearAccountAdjustInfo.getPayTellerBy()).getEscortType());
			// 设置客户名称
			tellerAccountsMain.setCustName(clearAccountAdjustInfo.getOffice().getName());
			// 设置客户Id
			tellerAccountsMain.setCustNo(clearAccountAdjustInfo.getOffice().getId());
			// 设置单号
			tellerAccountsMain.setBussinessId(clearAccountAdjustInfo.getAccountsId());
			// 设置业务类型：柜员调账
			tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.ADJUST);
			// 登记
			tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
			// 设置金额类型
			tellerAccountsMain.setCashType(clearAccountAdjustInfo.getCashType());
			// 交款：出库类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
			// 设置调账金额
			tellerAccountsMain.setOutAmount(clearAccountAdjustInfo.getAdjustMoney());
			// 设置发生机构（清分中心）
			tellerAccountsMain.setOffice(clearAccountAdjustInfo.getOffice());
		return tellerAccountsMain;
	}
	
	/**
	 * 
	 * @author dja
	 * @version 生成收款人柜员账务流水
	 * @version 2018-04-24
	 * @param businessType
	 * @param reTellerBy
	 * @return
	 */
	@Transactional(readOnly = false)
	private TellerAccountsMain getReTeller(ClearAccountAdjustInfo clearAccountAdjustInfo) {
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
			// 设置收款柜员ID
			tellerAccountsMain.setTellerBy(clearAccountAdjustInfo.getReTellerBy());
			// 设置收款柜员姓名
			tellerAccountsMain.setTellerName(clearAccountAdjustInfo.getReTellerName());
			// 设置收款柜员类型
			tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clearAccountAdjustInfo.getReTellerBy()).getEscortType());
			// 设置客户名称
			tellerAccountsMain.setCustName(clearAccountAdjustInfo.getOffice().getName());
			// 设置客户Id
			tellerAccountsMain.setCustNo(clearAccountAdjustInfo.getOffice().getId());
			// 设置流水单号
			tellerAccountsMain.setBussinessId(clearAccountAdjustInfo.getAccountsId());
			// 设置业务类型
			tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.ADJUST);
			// 设置业务状态
			tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
			// 设置金额类型
			tellerAccountsMain.setCashType(clearAccountAdjustInfo.getCashType());
			// 收款：入库类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
			// 设置调账金额
			tellerAccountsMain.setInAmount(clearAccountAdjustInfo.getAdjustMoney());
			// 设置发生机构（清分中心）
			tellerAccountsMain.setOffice(clearAccountAdjustInfo.getOffice());
		return tellerAccountsMain;
	}
}