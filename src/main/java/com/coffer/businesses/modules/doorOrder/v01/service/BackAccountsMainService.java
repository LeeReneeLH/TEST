package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.BackAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.BackAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.CompanyAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 回款管理Service
 * 
 * @author XL
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class BackAccountsMainService extends CrudService<BackAccountsMainDao, BackAccountsMain> {
	@Autowired
	private BackAccountsMainDao backAccountsMainDao;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private CompanyAccountsMainService companyAccountsMainService;

	public BackAccountsMain get(String id) {
		return super.get(id);
	}

	public List<BackAccountsMain> findList(BackAccountsMain backAccountsMain) {
		return super.findList(backAccountsMain);
	}

	public Page<BackAccountsMain> findPage(Page<BackAccountsMain> page, BackAccountsMain backAccountsMain) {
		return super.findPage(page, backAccountsMain);
	}

	@Transactional(readOnly = false,isolation=Isolation.READ_COMMITTED)
	public void save(BackAccountsMain backAccountsMain) {
		backAccountsMain.setBusinessId(BusinessUtils.getNewBusinessNo(DoorOrderConstant.BusinessType.CENTER_BACK,
				UserUtils.getUser().getOffice()));
		backAccountsMain.setStatus(DoorOrderConstant.StatusType.CREATE);
		super.save(backAccountsMain);
		// 验证回款金额大小，不能超过公司待回款金额
		BigDecimal companyNotBackAmount = companyAccountsMainService
				.getCompanyNotBackAmount(officeService.getPlatform().get(0).getId());
		if (backAccountsMain.getOutAmount().compareTo(companyNotBackAmount) > 0 ) {
			throw new BusinessException("message.A1016", "", new String[] {});
		}
		// 关联到公司账务
		// 设置客户为公司自己
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		companyAccountsMain.setCustNo(backAccountsMain.getCompanyId());
		companyAccountsMain.setCustName(StoreCommonUtils.getOfficeById(backAccountsMain.getCompanyId()).getName());
		// 设置账务发生机构及归属机构ID
		companyAccountsMain.setOfficeId(UserUtils.getUser().getOffice().getId());
		companyAccountsMain.setCompanyId(backAccountsMain.getCompanyId());
		// 设置账务发生机构名称及归属机构名称
		companyAccountsMain
				.setOfficeName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
		companyAccountsMain.setCompanyName(StoreCommonUtils.getOfficeById(backAccountsMain.getCompanyId()).getName());
		// 设置主键
		companyAccountsMain.setId(IdGen.uuid());
		// 设置业务类型
		companyAccountsMain.setType(DoorOrderConstant.BusinessType.CENTER_BACK);
		// 设置出入库类型
		companyAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置业务流水
		companyAccountsMain.setBusinessId(backAccountsMain.getBusinessId());
		// 设置业务状态
		companyAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
		// 设置公司账上入库金额
		companyAccountsMain.setInAmount(backAccountsMain.getOutAmount());
		// 公司账务主表做成
		DoorCommonUtils.insertCompanyAccounts(companyAccountsMain);
	}

	@Transactional(readOnly = false)
	public void delete(BackAccountsMain backAccountsMain) {
		super.delete(backAccountsMain);
	}

	/**
	 * 冲正处理
	 *
	 * @author WQJ
	 * @version 2019年7月8日
	 * @param doorOrderInfo
	 */
	@Transactional(readOnly = false)
	public void reverse(BackAccountsMain backAccountsMain) {
		backAccountsMainDao.update(backAccountsMain);
		// 关联到公司账务
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		// 设置业务流水
		companyAccountsMain.setBusinessId(backAccountsMain.getBusinessId());
		// 设置客户为公司
		companyAccountsMain.setCustNo(backAccountsMain.getCompanyId());
		companyAccountsMain.setCustName(StoreCommonUtils.getOfficeById(backAccountsMain.getCompanyId()).getName());
		// 设置账务发生机构及归属机构ID
		companyAccountsMain.setOfficeId(UserUtils.getUser().getOffice().getId());
		companyAccountsMain.setCompanyId(backAccountsMain.getCompanyId());
		// 设置主键
		companyAccountsMain.setId(IdGen.uuid());
		// 设置业务类型
		companyAccountsMain.setType(DoorOrderConstant.BusinessType.CENTER_BACK);
		// 设置出入库类型
		companyAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置出库金额
		companyAccountsMain.setOutAmount(backAccountsMain.getOutAmount());
		// 设置业务状态为冲正
		companyAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.DELETE);
		DoorCommonUtils.insertCompanyAccounts(companyAccountsMain);
	}
	
	/**
	 * 回款单号列表
	 * 
	 * @author gzd
	 * @version 2019年11月29日
	 * @param door_id
	 * @return
	 */
	public List<BackAccountsMain> getOrderId(String doorId) {
		return backAccountsMainDao.getOrderId(doorId);
	}

}