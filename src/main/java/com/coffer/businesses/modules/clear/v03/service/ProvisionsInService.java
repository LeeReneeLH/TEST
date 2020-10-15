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
import com.coffer.businesses.modules.store.v01.dao.StoEscortInfoDao;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 备付金交入Service
 * 
 * @author xiaoliang
 * @version 2017-08-24
 */
@Service
@Transactional(readOnly = true)
public class ProvisionsInService extends CrudService<ClInMainDao, ClInMain> {

	@Autowired
	private ClInDetailDao clInDetailDao;
	@Autowired
	private ClInMainDao clInMainDao;
	@Autowired
	private StoEscortInfoDao stoEscortInfoDao;

	@Override
	public ClInMain get(String id) {
		ClInMain bankPayInfo = super.get(id);
		return bankPayInfo;
	}

	public Page<ClInMain> findPage(Page<ClInMain> page, ClInMain clInMain) {
		// 查询条件：开始时间
		clInMain.getSqlMap().put("dsf", dataScopeFilter(clInMain.getCurrentUser(), "o", null));
		return super.findPage(page, clInMain);
	}

	/**
	 * 保存登记信息
	 * 
	 * @author XL
	 * @version 2017-08-24
	 * @param bankPayInfo
	 */
	@Transactional(readOnly = false)
	public void save(ClInMain bankPayInfo) {
		/* 增加try catch修改人：wzj 修改时间：2017-8-30 begin */
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();
		/* end */
		// 生成单号
		bankPayInfo.setInNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.PROVISIONS_IN,
				bankPayInfo.getCurrentUser().getOffice()));
		// 设置状态 ：1：登记
		bankPayInfo.setStatus(ClearConstant.StatusType.CREATE);
		// 业务类型: 06:备付金交入
		bankPayInfo.setBusType(ClearConstant.BusinessType.PROVISIONS_IN);
		// 插入登记机构信息
		bankPayInfo.setrOffice(SysCommonUtils.findOfficeById(bankPayInfo.getrOffice().getId()));
		// 银行交接人A
		bankPayInfo.setBankManNameA(stoEscortInfoDao.get(bankPayInfo.getBankManNoA()).getEscortName());
		// 银行交接人B
		bankPayInfo.setBankManNameB(stoEscortInfoDao.get(bankPayInfo.getBankManNoB()).getEscortName());
		// 设置确认人
		User user = UserUtils.getUser();
		bankPayInfo.setMakesureManNo(user.getId());
		bankPayInfo.setMakesureManName(user.getName());
		// 插入主表信息
		bankPayInfo.preInsert();
		
		int intMainResult = clInMainDao.insert(bankPayInfo);
		if (intMainResult == 0) {
			String strErrMsg = "备付金交入单号：" + bankPayInfo.getInNo();
			logger.error("备付金交入主表-" + strErrMsg + ",保存失败！");
			throw new BusinessException("message.E7400", "", new String[] { bankPayInfo.getInNo() });
		}
		
		// 明细
		for (DenominationInfo item : bankPayInfo.getDenominationList()) {
			if (item.getColumnValue1() == null || item.getColumnValue1().equals("")
					|| Integer.parseInt(item.getColumnValue1()) == 0) {
				continue;
			}
			ClInDetail bankPayDetail = new ClInDetail();
			// 主表交款单号
			bankPayDetail.setInNo(bankPayInfo.getInNo());

			// 币种(人民币)
			bankPayDetail.setCurrency(Constant.Currency.RMB);

			// 面值
			bankPayDetail.setDenomination(item.getMoneyKey());

			// 单位
			bankPayDetail.setUnitId(Constant.Unit.piece);

			// 总数
			bankPayDetail.setTotalCount(item.getColumnValue1());

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
			/* 增加try catch修改人：wzj 修改时间：2017-8-30 begin */
			// 插入明细表数据
			int intDetailResult = clInDetailDao.insert(bankPayDetail);
			if (intDetailResult == 0) {
				String strErrMsg = "备付金交入单号：" + bankPayInfo.getInNo();
				logger.error("备付金交入明细-" + strErrMsg + ",保存失败！");
				throw new BusinessException("message.E7401", "", new String[] { bankPayInfo.getInNo() });
			}
		}
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(bankPayInfo.getInNo());
		// 设置客户Id
		centerAccountsMain.setClientId(bankPayInfo.getrOffice().getId());
		// 设置业务类型
		centerAccountsMain.setBusinessType(bankPayInfo.getBusType());
		// 设置入库金额
		centerAccountsMain.setInAmount(bankPayInfo.getInAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置账务发生机构
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(bankPayInfo.getrOffice().getId());
		// 设置账务明细
		centerAccountsMain.setCenterAccountsDetailList(centerAccountsDetail);
		// 设置业务状态
		centerAccountsMain.setBusinessStatus(ClearConstant.StatusType.CREATE);
		ClearCommonUtils.insertAccounts(centerAccountsMain);
		/* end */

		// --------------------------------------------
		// --------------- 追加柜员账务-------------------
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
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.PROVISIONS_IN);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_TRUE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置入库金额
		tellerAccountsMain.setInAmount(bankPayInfo.getInAmount());
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
	}

	/**
	 * 冲正处理
	 * 
	 * @author XL
	 * @version 2017-08-24
	 * @param bankPayInfo
	 */
	@Transactional(readOnly = false)
	public void reverse(ClInMain bankPayInfo) {
		// 更新前设置
		bankPayInfo.preUpdate();
		bankPayInfo.setUpdateName(UserUtils.getUser().getName());
		// 设置状态 :2:冲正
		bankPayInfo.setStatus(ClearConstant.StatusType.DELETE);
		// 主表状态更新
		clInMainDao.updateStatus(bankPayInfo);
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(bankPayInfo.getInNo());
		// 设置客户Id
		centerAccountsMain.setClientId(bankPayInfo.getrOffice().getId());
		// 设置业务类型
		centerAccountsMain.setBusinessType(bankPayInfo.getBusType());
		// 设置入库金额
		centerAccountsMain.setOutAmount(bankPayInfo.getInAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置账务发生机构
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(bankPayInfo.getrOffice().getId());
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
		// --------------- 追加柜员账务-------------------
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
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.PROVISIONS_IN);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.DELETE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_TRUE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置出库金额
		tellerAccountsMain.setOutAmount(bankPayInfo.getInAmount());
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
	}

}