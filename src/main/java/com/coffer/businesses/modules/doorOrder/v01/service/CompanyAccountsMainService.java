package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.CompanyAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.CompanyAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 公司账务Service
 * 
 * @author XL
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class CompanyAccountsMainService extends CrudService<CompanyAccountsMainDao, CompanyAccountsMain> {
	@Autowired
	private CompanyAccountsMainDao companyAccountsMainDao;

	public CompanyAccountsMain get(String id) {
		return super.get(id);
	}

	public List<CompanyAccountsMain> findList(CompanyAccountsMain companyAccountsMain) {
		return super.findList(companyAccountsMain);
	}

	public Page<CompanyAccountsMain> findPage(Page<CompanyAccountsMain> page, CompanyAccountsMain companyAccountsMain) {
		return super.findPage(page, companyAccountsMain);
	}

	@Transactional(readOnly = false)
	public void save(CompanyAccountsMain companyAccountsMain) {
		// 设置主键
		companyAccountsMain.setId(IdGen.uuid());
		// 设置业务类型
		companyAccountsMain.setType(DoorOrderConstant.BusinessType.COMPANY_SAVE_CASH);
		// 设置出入库类型
		companyAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置业务状态
		companyAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
		// 公司账务主表做成
		DoorCommonUtils.insertCompanyAccounts(companyAccountsMain);
		// 此平台在公司是否有待回款金额判断，若有，处理后再进行存款
		// 获取此平台公司待回款金额
		// BigDecimal companyNotBackAmount =
		// this.getCompanyNotBackAmount(officeService.getPlatform().get(0).getId());
		// if (companyNotBackAmount.compareTo(new BigDecimal(0)) != 0) {
		// throw new BusinessException("message.A1018", "", new String[] {});
		// }
	}

	@Transactional(readOnly = false)
	public void delete(CompanyAccountsMain companyAccountsMain) {
		super.delete(companyAccountsMain);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月4日 公司账务主表做成
	 * 
	 * @param companyAccountsMain
	 */

	@Transactional(readOnly = false)
	public synchronized void insertCompanyAccounts(CompanyAccountsMain companyAccountsMain) {
		// 若账务出入库类型为出库业务
		if (companyAccountsMain.getInoutType().equals(ClearConstant.AccountsInoutType.ACCOUNTS_OUT)) {
			// 验证该公司是否存在余额
			String result = checkCompanyAccounts(companyAccountsMain.getCompanyId(),
					companyAccountsMain.getOutAmount());
			// 余额不足
			if (result.equals(Constant.FAILED)) {
				if (DoorOrderConstant.BusinessType.CENTER_BACK.equals(companyAccountsMain.getType())
						|| DoorOrderConstant.BusinessType.COMPANY_PAID.equals(companyAccountsMain.getType())
						|| DoorOrderConstant.BusinessType.COMPANY_SAVE_CASH.equals(companyAccountsMain.getType())) {
					throw new BusinessException("message.A1014", "", new String[] {
							StoreCommonUtils.getOfficeById(companyAccountsMain.getCompanyId()).getName(), "现金" });
				}
			}
		}

		// 获取公司账务余额
		BigDecimal companyTotalAmount = this.getCompanyTotalAmount(companyAccountsMain.getCompanyId());
		// 计算新的公司账务余额
		if (companyAccountsMain.getInoutType().equals(ClearConstant.AccountsInoutType.ACCOUNTS_IN)) {
			companyTotalAmount = companyTotalAmount.add(companyAccountsMain.getInAmount());

		} else {
			companyTotalAmount = companyTotalAmount.subtract(companyAccountsMain.getOutAmount());

		}
		// 设置中心名称
		companyAccountsMain
				.setCustName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
		// 设置账务发生机构名称以及账务所属机构名称
		companyAccountsMain.setOfficeName(StoreCommonUtils.getOfficeById(companyAccountsMain.getOfficeId()).getName());
		companyAccountsMain
				.setCompanyName(StoreCommonUtils.getOfficeById(companyAccountsMain.getCompanyId()).getName());
		// 设置账务主表主键
		companyAccountsMain.setId(IdGen.uuid());
		// 设置公司账务余额
		companyAccountsMain.setCompanyAmount(companyTotalAmount);
		// 有效标识
		companyAccountsMain.setDelFlag(ClearConstant.deleteFlag.Valid);
		companyAccountsMain.preInsert();
		int insertResult = companyAccountsMainDao.insert(companyAccountsMain);
		if (insertResult == 0) {
			String strMessageContent = "公司账务主表：" + companyAccountsMain.getId() + "更新失败！";
			logger.error(strMessageContent);
			throw new BusinessException("message.E7700", "", new String[] { companyAccountsMain.getId() });
		}
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月4日
	 * 
	 *          获取公司最新账务信息，判断剩余金额是否满足出库业务需求
	 * @param clientId,outAmount
	 * @return
	 */
	@Transactional(readOnly = false)
	public String checkCompanyAccounts(String companyId, BigDecimal outAmount) {
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		BigDecimal amount = new BigDecimal(0);
		// 设置公司ID
		companyAccountsMain.setCompanyId(companyId);
		// 通过公司ID获取信息
		List<CompanyAccountsMain> accountList = companyAccountsMainDao.getAccountByCompanyId(companyAccountsMain);
		// 若公司余额足够
		if (!Collections3.isEmpty(accountList)) {
			amount = amount.add(accountList.get(0).getCompanyAmount());
		}
		if (outAmount.compareTo(amount) > 0) {
			return Constant.FAILED;
		} else {
			return Constant.SUCCESS;

		}

	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月4日 获取公司账务余额
	 * 
	 * @return
	 */
	@Transactional(readOnly = false)
	public BigDecimal getCompanyTotalAmount(String companyId) {
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		// 设置公司ID
		companyAccountsMain.setCompanyId(companyId);
		// 通过公司ID获取账务信息
		List<CompanyAccountsMain> accountList = companyAccountsMainDao.getAccountByCompanyId(companyAccountsMain);
		if (!Collections3.isEmpty(accountList)) {
			return accountList.get(0).getCompanyAmount();
		} else {
			return new BigDecimal(0);			
		}

	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月17日 获取公司账务代付金额
	 * @return
	 */
	@Transactional(readOnly = false)
	public BigDecimal getCompanyPaidAmount(String companyId) {
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		// 设置公司ID
		companyAccountsMain.setCompanyId(companyId);
		// 设置机构过滤
		User userInfo = UserUtils.getUser();
		companyAccountsMain.setOfficeId(userInfo.getOffice().getId());
		companyAccountsMain.getSqlMap().put("dsf",
				"OR o7.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		// 通过公司ID获取账务信息
		CompanyAccountsMain main = null;
		main = companyAccountsMainDao.getCompanyPaidAmount(companyAccountsMain);
		if (main == null) {
			return new BigDecimal(0);
		} else {
			return main.getOutAmount();
		}
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月17日 获取公司账务回款金额
	 * @return
	 */
	@Transactional(readOnly = false)
	public BigDecimal getCompanyBackAmount(String companyId) {
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		// 设置公司ID
		companyAccountsMain.setCompanyId(companyId);
		// 设置机构过滤
		User userInfo = UserUtils.getUser();
		companyAccountsMain.setOfficeId(userInfo.getOffice().getId());
		companyAccountsMain.getSqlMap().put("dsf",
				"OR o7.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		CompanyAccountsMain main = null;
		// 通过公司ID获取账务信息
		main = companyAccountsMainDao.getCompanyBackAmount(companyAccountsMain);
		if (main == null) {
			return new BigDecimal(0);
		} else {
			// 处理冲正的回款信息
			if (main.getOutAmount() != null) {
				return main.getInAmount().subtract(main.getOutAmount());
			}
			return main.getInAmount();
		}
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月17日 获取公司账务待回款金额
	 * @return
	 */
	@Transactional(readOnly = false)
	public BigDecimal getCompanyNotBackAmount(String companyId) {
		BigDecimal paidAmount = getCompanyPaidAmount(companyId);
		BigDecimal backAmount = getCompanyBackAmount(companyId);
		return paidAmount.subtract(backAmount);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月17日 获取公司在平台的存款总额
	 * @return
	 */
	public BigDecimal getHaveSaveAmount(String companyId) {
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		// 设置公司ID
		companyAccountsMain.setCompanyId(companyId);
		CompanyAccountsMain main = null;
		main = dao.getHaveSaveAmount(companyAccountsMain);
		return main.getInAmount();
	}
}