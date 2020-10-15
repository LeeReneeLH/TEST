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
 * 代理上缴Service
 * 
 * @author sg
 * @version 2017-08-29
 */
@Service
@Transactional(readOnly = true)
public class AgencyPayService extends CrudService<ClOutMainDao, ClOutMain> {

	@Autowired
	private ClOutDetailDao agencyPayDetailDao;

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
	 * @author sg
	 * @version 2017年8月29日
	 * 
	 * @param AgencyPayMain
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void reverse(ClOutMain agencyPayMain) {

		agencyPayMain.preUpdate();
		// 状态 2:冲正
		agencyPayMain.setStatus(ClearConstant.StatusType.DELETE);
		// 主表状态更新
		dao.updateStatus(agencyPayMain);
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(agencyPayMain.getOutNo());
		// 设置客户Id
		centerAccountsMain.setClientId(agencyPayMain.getrOffice().getId());
		// 设置业务类型
		centerAccountsMain.setBusinessType(agencyPayMain.getBusType());
		// 设置出库金额
		centerAccountsMain.setInAmount(agencyPayMain.getOutAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置账务发生机构
		// 登陆用户
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(agencyPayMain.getrOffice().getId());
		// 设置账务明细
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();
		for (ClOutDetail clOutDetail : agencyPayMain.getClOutDetailList()) {
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
		tellerAccountsMain.setTellerBy(agencyPayMain.getTransManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(agencyPayMain.getTransManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(agencyPayMain.getTransManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(agencyPayMain.getrOffice().getId());
		// 设置客户名称
		tellerAccountsMain.setCustName(agencyPayMain.getrOffice().getName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(agencyPayMain.getOutNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.AGENCY_PAY);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.DELETE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置入库金额
		tellerAccountsMain.setInAmount(agencyPayMain.getOutAmount());
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		// ----------------------end----------------------
	}

	/**
	 * 保存登记信息
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param AgencyPayMain
	 *            主表信息
	 */
	@Transactional(readOnly = false)
	public void save(ClOutMain agencyPayMain) {
		List<CenterAccountsDetail> centerAccountsDetail = Lists.newArrayList();

		// ---------------------------------------------
		// ------------商行取款主表 (CL_OUT_MAIN)的做成----------
		// ---------------------------------------------

		// 生成取款单号
		agencyPayMain.setOutNo(BusinessUtils.getClearNewBusinessNo(ClearConstant.BusinessType.AGENCY_PAY,
				agencyPayMain.getCurrentUser().getOffice()));

		// 业务类型: 03:代理上缴
		agencyPayMain.setBusType(ClearConstant.BusinessType.AGENCY_PAY);
		// 状态 1：登记
		agencyPayMain.setStatus(ClearConstant.StatusType.CREATE);
		// 插入登记机构信息
		agencyPayMain.setrOffice(SysCommonUtils.findOfficeById(agencyPayMain.getrOffice().getId()));
		// 入库总金额
		agencyPayMain.setOutAmount(new BigDecimal(agencyPayMain.getOutAmount().toString().replace(",", "")));
		// 确认人
		User user = UserUtils.getUser();
		agencyPayMain.setMakesureManNo(user.getId());
		agencyPayMain.setMakesureManName(user.getName());

		agencyPayMain.preInsert();

		int intMainResult = dao.insert(agencyPayMain);
		if (intMainResult == 0) {
			String strErrMsg = "上缴单号：" + agencyPayMain.getOutNo();
			logger.error("代理上缴主表-" + strErrMsg + ",保存失败！");
			throw new BusinessException("message.E7004", "", new String[] { agencyPayMain.getOutNo() });
		}

		// ----------------------------------------------
		// ----------商行取款明细表 (CL_OUT_DETAIL)的做成----------
		// ----------------------------------------------
		for (DenominationInfo item : agencyPayMain.getDenominationList()) {
			// 待清分数和已清分数和ATM数都为空的场合，不做插入
			if ((item.getColumnValue1() == null || item.getColumnValue1().equals(""))
					&& (item.getColumnValue2() == null || item.getColumnValue2().equals(""))
					&& (item.getColumnValue3() == null || item.getColumnValue3().equals(""))
					&& (item.getColumnValue4() == null || item.getColumnValue4().equals(""))) {
				continue;
			}

			// BankPayDetail bankPayDetail = new BankPayDetail();
			ClOutDetail agencyPayDetail = new ClOutDetail();
			// 主表交款单号
			agencyPayDetail.setOutNo(agencyPayMain.getOutNo());
			// 币种(人民币)
			agencyPayDetail.setCurrency(Constant.Currency.RMB);
			// 券别
			agencyPayDetail.setDenomination(item.getMoneyKey());
			// 单位
			agencyPayDetail.setUnitId(Constant.Unit.bundle);
			// 完整券数
			if (!("0".equals(item.getColumnValue1())) && !("".equals(item.getColumnValue1()))) {
				agencyPayDetail.setCountWzq(item.getColumnValue1());
			}
			// 残次券数
			if (!("0".equals(item.getColumnValue2())) && !("".equals(item.getColumnValue2()))) {
				agencyPayDetail.setCountCsq(item.getColumnValue2());
			}
			// 已清分数
			if (!("0".equals(item.getColumnValue3())) && !("".equals(item.getColumnValue3()))) {
				agencyPayDetail.setCountYqf(item.getColumnValue3());
			}
			// ATM数
			if (!("0".equals(item.getColumnValue4())) && !("".equals(item.getColumnValue4()))) {
				agencyPayDetail.setCountAtm(item.getColumnValue4());
			}
			// 总数
			int intCountWzq = 0;
			if (item.getColumnValue1() != null && !item.getColumnValue1().equals("")) {
				intCountWzq = Integer.valueOf(item.getColumnValue1());
			}
			int intCountCsq = 0;
			if (item.getColumnValue2() != null && !item.getColumnValue2().equals("")) {
				intCountCsq = Integer.valueOf(item.getColumnValue2());
			}
			int intCountYqf = 0;
			if (item.getColumnValue3() != null && !item.getColumnValue3().equals("")) {
				intCountYqf = Integer.valueOf(item.getColumnValue3());
			}
			int intCountAtm = 0;
			if (item.getColumnValue4() != null && !item.getColumnValue4().equals("")) {
				intCountAtm = Integer.valueOf(item.getColumnValue4());
			}
			agencyPayDetail.setTotalCount(String.valueOf(intCountWzq + intCountCsq + intCountYqf + intCountAtm));

			// 总金额
			agencyPayDetail.setTotalAmt(new BigDecimal(item.getTotalAmt()));
			// 设置账务明细
			CenterAccountsDetail detail = new CenterAccountsDetail();
			// 设置币种
			detail.setCurrency(agencyPayDetail.getCurrency());
			// 设置面值
			detail.setDenomination(agencyPayDetail.getDenomination());
			// 设置单位
			detail.setUnit(agencyPayDetail.getUnitId());
			// 设置数量
			detail.setTotalCount(new BigDecimal(agencyPayDetail.getTotalCount()).multiply(new BigDecimal(-1)));
			// 设置金额
			detail.setTotalAmount(agencyPayDetail.getTotalAmt().multiply(new BigDecimal(-1)));
			centerAccountsDetail.add(detail);

			agencyPayDetail.preInsert();
			// 插入明细表数据
			// 如果总数为零则不插入
			if ((intCountWzq + intCountCsq + intCountYqf + intCountAtm) != 0) {
				int intDetailResult = agencyPayDetailDao.insert(agencyPayDetail);
				if (intDetailResult == 0) {
					String strErrMsg = "上缴单号：" + agencyPayMain.getOutNo();
					logger.error("代理上缴明细-" + strErrMsg + ",保存失败！");
					throw new BusinessException("message.E7005", "", new String[] { agencyPayMain.getOutNo() });
				}
			}
		}
		// 将流水关联到账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置关联账务流水Id
		centerAccountsMain.setBusinessId(agencyPayMain.getOutNo());
		// 设置客户Id
		centerAccountsMain.setClientId(agencyPayMain.getrOffice().getId());
		// 设置业务类型
		centerAccountsMain.setBusinessType(agencyPayMain.getBusType());
		// 设置出库金额
		centerAccountsMain.setOutAmount(agencyPayMain.getOutAmount());
		// 设置出入库类型
		centerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置账务发生机构
		// 登陆用户
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置账务所在机构
		centerAccountsMain.setAofficeId(agencyPayMain.getrOffice().getId());
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
		tellerAccountsMain.setTellerBy(agencyPayMain.getTransManNo());
		// 设置柜员姓名
		tellerAccountsMain.setTellerName(agencyPayMain.getTransManName());
		// 设置柜员类型
		tellerAccountsMain.setTellerType(StoreCommonUtils.getEscortById(agencyPayMain.getTransManNo()).getEscortType());
		// 设置客户ID
		tellerAccountsMain.setCustNo(agencyPayMain.getrOffice().getId());
		// 设置客户名称
		tellerAccountsMain.setCustName(agencyPayMain.getrOffice().getName());
		// 设置流水单号
		tellerAccountsMain.setBussinessId(agencyPayMain.getOutNo());
		// 设置业务类型
		tellerAccountsMain.setBussinessType(ClearConstant.BusinessType.AGENCY_PAY);
		// 设置业务状态
		tellerAccountsMain.setBussinessStatus(ClearConstant.StatusType.CREATE);
		// 设置金额类型
		tellerAccountsMain.setCashType(ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		// 设置出入库类型
		tellerAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置出库金额
		tellerAccountsMain.setOutAmount(agencyPayMain.getOutAmount());
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		tellerAccountsMain.setOffice(userInfo.getOffice());
		/* end */
		ClearCommonUtils.insertTellerAccounts(tellerAccountsMain);
		// ----------------------end----------------------
	}

}