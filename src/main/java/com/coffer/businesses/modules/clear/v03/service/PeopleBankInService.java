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
import com.coffer.businesses.modules.clear.v03.dao.ClOutDetailDao;
import com.coffer.businesses.modules.clear.v03.dao.ClOutMainDao;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsDetail;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.ClOutDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
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
 * 从人行复点入库Service
 * 
 * @author wzj
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class PeopleBankInService extends CrudService<ClOutMainDao, ClOutMain> {

	@Autowired
	private ClOutDetailDao clOutDetailDao;

	@Override
	public ClOutMain get(String outNo) {
		return super.get(outNo);
	}

	public Page<ClOutMain> findPage(Page<ClOutMain> page, ClOutMain clOutMain) {
		// 查询条件：开始时间
		clOutMain.getSqlMap().put("dsf", dataScopeFilter(clOutMain.getCurrentUser(), "o", null));
		return super.findPage(page, clOutMain);
	}

	/**
	 * 冲正处理
	 * 
	 * @author wzj
	 * @version 2017年09月04日
	 * 
	 * @param ClOutMain
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void reverse(ClOutMain clOutMain) {

		clOutMain.preUpdate();
		// 状态 2:冲正
		clOutMain.setStatus(ClearConstant.StatusType.DELETE);
		// 主表状态更新
		dao.updateStatus(clOutMain);
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(clOutMain.getOutNo());
		// 设置客户Id
		// 登陆用户
		User userInfo = UserUtils.getUser();
		String parentId = StoreCommonUtils.getOfficeById(userInfo.getOffice().getId()).getParentId();
		centerAccountsMain.setClientId(parentId);
		// 设置业务类型
		centerAccountsMain.setBusinessType(clOutMain.getBusType());
		// 设置出库金额
		centerAccountsMain.setInAmount(clOutMain.getOutAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(parentId);
		// 设置账务明细
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();
		for (ClOutDetail clOutDetail : clOutMain.getClOutDetailList()) {
			// 设置账务明细
			CenterAccountsDetail detail = new CenterAccountsDetail();
			// 设置币种
			detail.setCurrency(clOutDetail.getCurrency());
			// 设置面值
			detail.setDenomination(clOutDetail.getDenomination());
			// 设置单位
			detail.setUnit(clOutDetail.getUnitId());
			// 设置数量
			detail.setTotalCount(new BigDecimal(clOutDetail.getTotalCount()));
			// 设置金额
			detail.setTotalAmount(clOutDetail.getTotalAmt());
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
		tellerAccountsMain.setTellerBy(clOutMain.getTransManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(clOutMain.getTransManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clOutMain.getTransManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(clOutMain.getrOffice().getId());
		// 设置客户名称
		tellerAccountsMain.setCustName(clOutMain.getrOffice().getName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(clOutMain.getOutNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.DELETE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置入库金额
		tellerAccountsMain.setInAmount(clOutMain.getOutAmount());
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		// ----------------------end----------------------
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wzj
	 * @version 2017年09月04日
	 * @param ClOutMain
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void save(ClOutMain clOutMain) {
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();
		// 从人行复点入库主表
		// 生成交款单号
		clOutMain.setOutNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN,
				clOutMain.getCurrentUser().getOffice()));
		// 业务类型: 04:人民银行复点入库
		clOutMain.setBusType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 状态 1：登记
		clOutMain.setStatus(ClearConstant.StatusType.CREATE);
		// 插入登记机构信息
		clOutMain.setrOffice(SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getParentId()));
		// 入库总金额
		clOutMain.setOutAmount(new BigDecimal(clOutMain.getOutAmount().toString().replace(",", "")));
		// 确认人
		User user = UserUtils.getUser();
		clOutMain.setMakesureManNo(user.getId());
		clOutMain.setMakesureManName(user.getName());
		clOutMain.preInsert();
		int intMainResult = dao.insert(clOutMain);
		if (intMainResult == 0) {
			String strErrMsg = "从人行复点入库单号：" + clOutMain.getOutNo();
			logger.error("从人行复点入库主表-" + strErrMsg + ",保存失败！");
			throw new BusinessException("message.E7006", "", new String[] { clOutMain.getOutNo() });
		}
		// 从人行复点入库明细表
		for (DenominationInfo item : clOutMain.getDenominationList()) {
			if ((item.getColumnValue1() == null || item.getColumnValue1().equals(""))
					&& (item.getColumnValue2() == null || item.getColumnValue2().equals(""))) {
				continue;
			}
			ClOutDetail clOutDetail = new ClOutDetail();
			// 主表交款单号
			clOutDetail.setOutNo(clOutMain.getOutNo());

			// 币种(人民币)
			clOutDetail.setCurrency(Constant.Currency.RMB);
			// 面值
			clOutDetail.setDenomination(item.getMoneyKey());
			// 单位
			clOutDetail.setUnitId(Constant.Unit.bundle);
			/* 空值验证 wzj 2017-11-17 begin */
			// 待清分数
			if (!("0".equals(item.getColumnValue1()))) {
				clOutDetail.setCountDqf(item.getColumnValue1());
			}
			// 已清分数
			if (!("0".equals(item.getColumnValue2()))) {
				clOutDetail.setCountYqf(item.getColumnValue2());
			}
			// 总数
			int intCountDqf = 0;
			if (item.getColumnValue1() != null && !item.getColumnValue1().equals("")) {
				intCountDqf = Integer.valueOf(item.getColumnValue1());
			}
			int intCountYqf = 0;
			if (item.getColumnValue2() != null && !item.getColumnValue2().equals("")) {
				intCountYqf = Integer.valueOf(item.getColumnValue2());
			}
			/* end */
			// 总数
			clOutDetail.setTotalCount(String.valueOf(intCountDqf + intCountYqf));
			// 总金额
			clOutDetail.setTotalAmt(new BigDecimal(item.getTotalAmt()));
			// 设置账务明细
			CenterAccountsDetail detail = new CenterAccountsDetail();
			// 设置币种
			detail.setCurrency(clOutDetail.getCurrency());
			// 设置面值
			detail.setDenomination(clOutDetail.getDenomination());
			// 设置单位
			detail.setUnit(clOutDetail.getUnitId());
			// 设置数量
			detail.setTotalCount(new BigDecimal(clOutDetail.getTotalCount()).negate());
			// 设置金额
			detail.setTotalAmount(clOutDetail.getTotalAmt().negate());
			centerAccountsDetail.add(detail);
			clOutDetail.preInsert();
			// 插入明细表数据
			// 如果总数为零则不插入
			/* 修改判断条件 wzj 2017-11-17 begin */
			if ((intCountDqf + intCountYqf) != 0) {
				/* end */
				int intDetailResult = clOutDetailDao.insert(clOutDetail);
				if (intDetailResult == 0) {
					String strErrMsg = "从人行复点入库单号：" + clOutMain.getOutNo();
					logger.error("从人行复点入库明细-" + strErrMsg + ",保存失败！");
					throw new BusinessException("message.E7007", "", new String[] { clOutMain.getOutNo() });
				}
			}
		}
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(clOutMain.getOutNo());
		// 登陆用户
		User userInfo = UserUtils.getUser();
		String parentId = StoreCommonUtils.getOfficeById(userInfo.getOffice().getId()).getParentId();
		centerAccountsMain.setClientId(parentId);
		// 设置业务类型
		centerAccountsMain.setBusinessType(clOutMain.getBusType());
		// 设置出库金额
		centerAccountsMain.setOutAmount(clOutMain.getOutAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
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
		tellerAccountsMain.setTellerBy(clOutMain.getTransManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(clOutMain.getTransManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(clOutMain.getTransManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(clOutMain.getrOffice().getId());
		// 设置客户名称
		tellerAccountsMain.setCustName(clOutMain.getrOffice().getName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(clOutMain.getOutNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置出库金额
		tellerAccountsMain.setOutAmount(clOutMain.getOutAmount());
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		// ----------------------end----------------------
	}

}
