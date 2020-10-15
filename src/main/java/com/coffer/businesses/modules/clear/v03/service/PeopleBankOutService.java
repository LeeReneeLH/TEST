package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClInDetailDao;
import com.coffer.businesses.modules.clear.v03.dao.ClInMainDao;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsDetail;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.ClInDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClInMain;
import com.coffer.businesses.modules.clear.v03.entity.DenominationInfo;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 从人行复点出库Service
 * 
 * @author wxz
 * @version 2017-08-29
 */
@Service
@Transactional(readOnly = true)
public class PeopleBankOutService extends CrudService<ClInMainDao, ClInMain> {

	@Autowired
	private ClInDetailDao clInDetailDao;

	@Override
	public ClInMain get(String inNo) {
		return super.get(inNo);
	}

	public Page<ClInMain> findPage(Page<ClInMain> page, ClInMain clInMain) {
		// 查询条件：开始时间
		clInMain.getSqlMap().put("dsf", dataScopeFilter(clInMain.getCurrentUser(), "o", null));
		return super.findPage(page, clInMain);
	}

	/**
	 * 冲正处理
	 * 
	 * @author wxz
	 * @version 2017年8月29日
	 * 
	 * @param ClInMain
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void reverse(ClInMain bankPayInfo) {

		bankPayInfo.preUpdate();
		// 状态 2:冲正
		bankPayInfo.setStatus(ClearConstant.StatusType.DELETE);
		// 主表状态更新
		dao.updateStatus(bankPayInfo);
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(bankPayInfo.getInNo());
		// 设置客户Id
		// 登陆用户
		User userInfo = UserUtils.getUser();
		String parentId = StoreCommonUtils.getOfficeById(userInfo.getOffice().getId()).getParentId();
		centerAccountsMain.setClientId(parentId);
		// 设置业务类型
		centerAccountsMain.setBusinessType(bankPayInfo.getBusType());
		// 设置入库金额
		centerAccountsMain.setOutAmount(bankPayInfo.getInAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(parentId);
		// 设置账务明细
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();
		for (ClInDetail bankPayDetail : bankPayInfo.getBankPayDetailList()) {
			// 设置账务明细
			CenterAccountsDetail detail = new CenterAccountsDetail();
			// 设置币种
			detail.setCurrency(bankPayDetail.getCurrency());
			// 设置面值
			detail.setDenomination(bankPayDetail.getDenomination());
			// 设置单位
			detail.setUnit(bankPayDetail.getUnitId());
			// 设置数量
			detail.setTotalCount(new BigDecimal(bankPayDetail.getTotalCount()).negate());
			// 设置金额
			detail.setTotalAmount(bankPayDetail.getTotalAmt().negate());
			centerAccountsDetail.add(detail);
		}
		centerAccountsMain.setCenterAccountsDetailList(centerAccountsDetail);
		// 设置业务状态
		centerAccountsMain.setBusinessStatus(ClearConstant.StatusType.DELETE);
		ClearCommonUtils.insertAccounts(centerAccountsMain);

		// --------------------------------------------
		// 追加柜员账务 修改人：xl 修改时间：2017-10-27 begin
		// --------------------------------------------
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
		// 设置柜员ID
		tellerAccountsMain.setTellerBy(bankPayInfo.getTransManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(bankPayInfo.getTransManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(bankPayInfo.getTransManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(bankPayInfo.getrOffice().getId());
		// 设置客户名称
		tellerAccountsMain.setCustName(bankPayInfo.getrOffice().getName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(bankPayInfo.getInNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.DELETE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置出库金额
		tellerAccountsMain.setOutAmount(bankPayInfo.getInAmount());

		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		// ----------------------end----------------------
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wxz
	 * @version 2017年8月29日
	 * @param allAllocateInfo
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void save(ClInMain bankPayInfo) {
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();
		// ---------------------------------------------
		// ------------人民银行复点出库主表 (CL_IN_MAIN)的做成----------
		// ---------------------------------------------

		// 生成交款单号
		bankPayInfo.setInNo(BusinessUtils.getClearNewBusinessNo(
				ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT, bankPayInfo.getCurrentUser().getOffice()));

		// 业务类型: 05:人民银行复点出库
		bankPayInfo.setBusType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		// 状态 1：登记
		bankPayInfo.setStatus(ClearConstant.StatusType.CREATE);
		// 插入登记机构信息
		bankPayInfo.setrOffice(SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getParentId()));
		// 入库总金额
		bankPayInfo.setInAmount(new BigDecimal(bankPayInfo.getInAmount().toString().replace(",", "")));
		// 确认人
		User user = UserUtils.getUser();
		bankPayInfo.setMakesureManNo(user.getId());
		bankPayInfo.setMakesureManName(user.getName());

		bankPayInfo.preInsert();

		int intMainResult = dao.insert(bankPayInfo);
		if (intMainResult == 0) {
			String strErrMsg = "从人行复点出库单号：" + bankPayInfo.getInNo();
			logger.error("从人行复点出库主表-" + strErrMsg + ",保存失败！");
			throw new BusinessException("message.E7008", "", new String[] { bankPayInfo.getInNo() });
		}

		// ----------------------------------------------
		// ----------人民银行复点出库明细表 (CL_IN_DETAIL)的做成----------
		// ----------------------------------------------
		for (DenominationInfo item : bankPayInfo.getDenominationList()) {
			// 数量为空的场合，不做插入
			if ((item.getColumnValue1() == null || item.getColumnValue1().equals(""))) {
				continue;
			}

			ClInDetail bankPayDetail = new ClInDetail();

			// //主键ID
			// bankPayDetail.setDetailId(IdGen.uuid());

			// 主表交款单号
			bankPayDetail.setInNo(bankPayInfo.getInNo());

			// 币种(人民币)
			bankPayDetail.setCurrency(Constant.Currency.RMB);

			// 面值
			bankPayDetail.setDenomination(item.getMoneyKey());

			// 单位
			bankPayDetail.setUnitId(Constant.Unit.bundle);

			// 总数
			int intCount = 0;
			if (item.getColumnValue1() != null && !item.getColumnValue1().equals("")) {
				intCount = Integer.valueOf(item.getColumnValue1());
			}

			bankPayDetail.setTotalCount(String.valueOf(intCount));

			// 总金额
			bankPayDetail.setTotalAmt(new BigDecimal(item.getTotalAmt()));
			// 设置账务明细
			CenterAccountsDetail detail = new CenterAccountsDetail();
			// 设置币种
			detail.setCurrency(bankPayDetail.getCurrency());
			// 设置面值
			detail.setDenomination(bankPayDetail.getDenomination());
			// 设置单位
			detail.setUnit(bankPayDetail.getUnitId());
			// 设置数量
			detail.setTotalCount(new BigDecimal(bankPayDetail.getTotalCount()));
			// 设置金额
			detail.setTotalAmount(bankPayDetail.getTotalAmt());
			centerAccountsDetail.add(detail);

			bankPayDetail.preInsert();
			// 插入明细表数据
			// 如果总数为零则不插入
			if (intCount != 0) {
				int intDetailResult = clInDetailDao.insert(bankPayDetail);
				if (intDetailResult == 0) {
					String strErrMsg = "从人行复点出库单号：" + bankPayInfo.getInNo();
					logger.error("从人行复点出库明细-" + strErrMsg + ",保存失败！");
					throw new BusinessException("message.E7009", "", new String[] { bankPayInfo.getInNo() });
				}
			}
		}
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(bankPayInfo.getInNo());
		// 设置客户Id
		// 登陆用户
		User userInfo = UserUtils.getUser();
		String parentId = StoreCommonUtils.getOfficeById(userInfo.getOffice().getId()).getParentId();
		centerAccountsMain.setClientId(parentId);
		// 设置业务类型
		centerAccountsMain.setBusinessType(bankPayInfo.getBusType());
		// 设置入库金额
		centerAccountsMain.setInAmount(bankPayInfo.getInAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(parentId);
		// 设置账务明细
		centerAccountsMain.setCenterAccountsDetailList(centerAccountsDetail);
		// 设置业务状态
		centerAccountsMain.setBusinessStatus(ClearConstant.StatusType.CREATE);
		ClearCommonUtils.insertAccounts(centerAccountsMain);

		// --------------------------------------------
		// 追加柜员账务 修改人：xl 修改时间：2017-10-27 begin
		// --------------------------------------------
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
		// 设置柜员ID
		tellerAccountsMain.setTellerBy(bankPayInfo.getTransManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(bankPayInfo.getTransManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(bankPayInfo.getTransManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(bankPayInfo.getrOffice().getId());
		// 设置客户名称
		tellerAccountsMain.setCustName(bankPayInfo.getrOffice().getName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(bankPayInfo.getInNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置入库金额
		tellerAccountsMain.setInAmount(bankPayInfo.getInAmount());
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		// ----------------------end----------------------
	}

}
