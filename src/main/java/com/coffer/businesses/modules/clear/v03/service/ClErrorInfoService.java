package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.ClErrorInfoDao;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsDetail;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.ClErrorInfo;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 差错管理Service
 * 
 * @author XL
 * @version 2017年9月7日
 */
@Service
@Transactional(readOnly = true)
public class ClErrorInfoService extends CrudService<ClErrorInfoDao, ClErrorInfo> {

	@Autowired
	private ClErrorInfoDao clErrorInfoDao;

	public ClErrorInfo get(String id) {
		return super.get(id);
	}

	public List<ClErrorInfo> findList(ClErrorInfo clErrorInfo) {
		return super.findList(clErrorInfo);
	}

	/**
	 * 获取差错信息列表
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param page
	 * @param clErrorInfo
	 * @return
	 */
	public Page<ClErrorInfo> findPage(Page<ClErrorInfo> page, ClErrorInfo clErrorInfo) {
		// 查询条件：开始时间
		clErrorInfo.getSqlMap().put("dsf", dataScopeFilter(clErrorInfo.getCurrentUser(), "o", null));
		// 查询条件： 开始时间
		if (clErrorInfo.getCreateTimeStart() != null) {
			clErrorInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clErrorInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clErrorInfo.getCreateTimeEnd() != null) {
			clErrorInfo
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(clErrorInfo.getCreateTimeEnd())));
		}
		return super.findPage(page, clErrorInfo);
	}

	/**
	 * 
	 * @author sg
	 * @version 获取未清分捆数
	 * 
	 * @param orderClearMain
	 * @return
	 */
	public ClErrorInfo findClearList() {
		// 登记用户所属机构
		ClErrorInfo clErrorInfo = new ClErrorInfo();
		User user = UserUtils.getUser();
		clErrorInfo.setCustNo(user.getOffice().getId());
		clErrorInfo.setStatus(ClearConstant.StatusType.CREATE);
		clErrorInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
		clErrorInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		return clErrorInfoDao.findClearList(clErrorInfo);
	}

	/**
	 * 差错信息添加
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void save(ClErrorInfo clErrorInfo) {
		// 设置差错单号
		clErrorInfo.setErrorNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.ERROR_HANDLING,
				clErrorInfo.getCurrentUser().getOffice()));
		// 设置状态：1 登记
		clErrorInfo.setStatus(ClearConstant.StatusType.CREATE);
		// 设置操作类型：现金
		clErrorInfo.setOperateType(ClearConstant.OperationType.CASH);
		// 设置币种：人民币
		clErrorInfo.setCurrency(Constant.Currency.RMB);
		// 插入登记机构信息
		clErrorInfo.setCustNo(clErrorInfo.getCustNo());
		clErrorInfo.setCustName(SysCommonUtils.findOfficeById(clErrorInfo.getCustNo()).getName());
		// 设置现钞备付金管理员
		clErrorInfo.setCheckManName(StoreCommonUtils.getEscortById(clErrorInfo.getCheckManNo()).getEscortName());
		// 设置清分人
		clErrorInfo.setClearManName(UserUtils.get(clErrorInfo.getClearManNo()).getName());
		// 设置确认人
		clErrorInfo.setMakesureManNo(UserUtils.getUser().getId());
		clErrorInfo.setMakesureManName(UserUtils.getUser().getName());
		// 判断更新或插入
		clErrorInfo.preInsert();
		int clErrorResult = clErrorInfoDao.insert(clErrorInfo);
		if (clErrorResult == 0) {
			String strMessageContent = "差错信息：" + clErrorInfo.getErrorNo() + "插入失败！";
			logger.error(strMessageContent);
			throw new BusinessException("message.E7500", "", new String[] { clErrorInfo.getErrorNo() });
		}

		/** 关联到账务 */
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();
		// 设置账务明细
		CenterAccountsDetail detail = new CenterAccountsDetail();
		// 设置币种
		detail.setCurrency(clErrorInfo.getCurrency());
		// 设置面值
		detail.setDenomination(clErrorInfo.getDenomination());
		// 设置单位
		detail.setUnit(Constant.Unit.piece);
		if (ClearConstant.ErrorType.LONG_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置数量
			detail.setTotalCount(new BigDecimal(clErrorInfo.getCount()));
			// 设置金额
			detail.setTotalAmount(clErrorInfo.getErrorMoney());
		}
		if (ClearConstant.ErrorType.SHORT_CURRENCY.equals(clErrorInfo.getErrorType())
				|| ClearConstant.ErrorType.COUNTERFEIT_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置数量
			detail.setTotalCount(new BigDecimal(clErrorInfo.getCount()).negate());
			// 设置金额
			detail.setTotalAmount(clErrorInfo.getErrorMoney().negate());
		}
		centerAccountsDetail.add(detail);

		// 将流水关联到账务
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(clErrorInfo.getErrorNo());
		// 设置客户Id
		centerAccountsMain.setClientId(clErrorInfo.getCustNo());
		// 设置业务类型
		centerAccountsMain.setBusinessType(ClearConstant.BusinessType.ERROR_HANDLING);

		if (ClearConstant.ErrorType.LONG_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置出入库金额
			centerAccountsMain.setInAmount(clErrorInfo.getErrorMoney());
			// 设置出入库类型
			centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		}
		if (ClearConstant.ErrorType.SHORT_CURRENCY.equals(clErrorInfo.getErrorType())
				|| ClearConstant.ErrorType.COUNTERFEIT_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置出入库金额
			centerAccountsMain.setOutAmount(clErrorInfo.getErrorMoney());
			// 设置出入库类型
			centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		}

		// 设置账务发生机构
		// 登陆用户
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(clErrorInfo.getCustNo());
		// 设置账务明细
		centerAccountsMain.setCenterAccountsDetailList(centerAccountsDetail);
		// 设置业务状态
		centerAccountsMain.setBusinessStatus(ClearConstant.StatusType.CREATE);
		ClearCommonUtils.insertAccounts(centerAccountsMain);

		// --------------------------------------------
		// ----------------追加柜员账务 ------------------
		// --------------------------------------------
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
		// 设置柜员ID
		tellerAccountsMain.setTellerBy(clErrorInfo.getCheckManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(clErrorInfo.getCheckManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clErrorInfo.getCheckManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(clErrorInfo.getCustNo());
		// 设置客户名称
		tellerAccountsMain.setCustName(clErrorInfo.getCustName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(clErrorInfo.getErrorNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.ERROR_HANDLING);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_TRUE);
		// 长款
		if (ClearConstant.ErrorType.LONG_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置入库类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
			// 设置入库金额
			tellerAccountsMain.setInAmount(clErrorInfo.getErrorMoney());
		}
		// 短款或假币
		if (ClearConstant.ErrorType.SHORT_CURRENCY.equals(clErrorInfo.getErrorType())
				|| ClearConstant.ErrorType.COUNTERFEIT_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置出库类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
			// 设置出库金额
			tellerAccountsMain.setOutAmount(clErrorInfo.getErrorMoney());
		}
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(clErrorInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
	}

	/**
	 * 冲正处理
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void reverse(ClErrorInfo clErrorInfo) {
		// 更新前设置
		clErrorInfo.preUpdate();
		clErrorInfo.setUpdateName(UserUtils.getUser().getName());
		// 设置状态 :2:冲正
		clErrorInfo.setStatus(ClearConstant.StatusType.DELETE);
		// 状态更新
		clErrorInfoDao.update(clErrorInfo);

		/** 关联到账务 */
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();

		// 设置账务明细
		CenterAccountsDetail detail = new CenterAccountsDetail();
		// 设置币种
		detail.setCurrency(clErrorInfo.getCurrency());
		// 设置面值
		detail.setDenomination(clErrorInfo.getDenomination());
		// 设置单位
		detail.setUnit(Constant.Unit.piece);

		if (ClearConstant.ErrorType.LONG_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置数量
			detail.setTotalCount(new BigDecimal(clErrorInfo.getCount()).negate());
			// 设置金额
			detail.setTotalAmount(clErrorInfo.getErrorMoney().negate());
		}
		if (ClearConstant.ErrorType.SHORT_CURRENCY.equals(clErrorInfo.getErrorType())
				|| ClearConstant.ErrorType.COUNTERFEIT_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置数量
			detail.setTotalCount(new BigDecimal(clErrorInfo.getCount()));
			// 设置金额
			detail.setTotalAmount(clErrorInfo.getErrorMoney());

		}
		centerAccountsDetail.add(detail);

		// 将流水关联到账务
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(clErrorInfo.getErrorNo());
		// 设置客户Id
		centerAccountsMain.setClientId(clErrorInfo.getCustNo());
		// 设置业务类型
		centerAccountsMain.setBusinessType(ClearConstant.BusinessType.ERROR_HANDLING);

		if (ClearConstant.ErrorType.LONG_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置出入库金额
			centerAccountsMain.setOutAmount(clErrorInfo.getErrorMoney());
			// 设置出入库类型
			centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		}
		if (ClearConstant.ErrorType.SHORT_CURRENCY.equals(clErrorInfo.getErrorType())
				|| ClearConstant.ErrorType.COUNTERFEIT_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置出入库金额
			centerAccountsMain.setInAmount(clErrorInfo.getErrorMoney());
			// 设置出入库类型
			centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		}

		// 设置账务发生机构
		// 登陆用户
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(clErrorInfo.getCustNo());
		// 设置账务明细
		centerAccountsMain.setCenterAccountsDetailList(centerAccountsDetail);
		// 设置业务状态
		centerAccountsMain.setBusinessStatus(ClearConstant.StatusType.DELETE);
		ClearCommonUtils.insertAccounts(centerAccountsMain);

		// --------------------------------------------
		// ----------------追加柜员账务 ------------------
		// --------------------------------------------
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
		// 设置柜员ID
		tellerAccountsMain.setTellerBy(clErrorInfo.getCheckManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(clErrorInfo.getCheckManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clErrorInfo.getCheckManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(clErrorInfo.getCustNo());
		// 设置客户名称
		tellerAccountsMain.setCustName(clErrorInfo.getCustName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(clErrorInfo.getErrorNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.ERROR_HANDLING);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_TRUE);
		// 长款
		if (ClearConstant.ErrorType.LONG_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置出库类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
			// 设置出库金额
			tellerAccountsMain.setOutAmount(clErrorInfo.getErrorMoney());
		}
		// 短款或假币
		if (ClearConstant.ErrorType.SHORT_CURRENCY.equals(clErrorInfo.getErrorType())
				|| ClearConstant.ErrorType.COUNTERFEIT_CURRENCY.equals(clErrorInfo.getErrorType())) {
			// 设置入库类型
			tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
			// 设置入库金额
			tellerAccountsMain.setInAmount(clErrorInfo.getErrorMoney());
		}
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(clErrorInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
	}

}