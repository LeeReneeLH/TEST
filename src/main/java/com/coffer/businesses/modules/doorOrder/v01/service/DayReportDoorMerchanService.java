package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.BankApiParamConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.DayReportDoorMerchanDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DepositErrorDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorErrorInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.PaidRecordDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.BankAccountInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.CompanyAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportAccountExport;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositError;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorErrorInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.PaidRecord;
import com.coffer.businesses.modules.doorOrder.v01.entity.Store;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 商户日结Service
 * 
 * @author wqj
 * @version 2019-07-23
 */
@Service
@Transactional(readOnly = true)
public class DayReportDoorMerchanService extends CrudService<DayReportDoorMerchanDao, DayReportDoorMerchan> {
	@Autowired
	private DayReportDoorMerchanDao dayReportDoorMerchanDao;
	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;
	@Autowired
	private DoorDayReportCenterService dayReportCenterService;
	@Autowired
	private DoorCenterAccountsMainDao doorCenterAccountsMainDao;
	@Autowired
	private DayReportDoorAndSevenCodeService dayReportSevenCodeService;
	@Autowired
	private BankService bankService;
	@Autowired
	private PaidRecordDao paidRecordDao;
	@Autowired
	private BankAccountInfoService bankAccountInfoService;
	@Autowired
	private DoorErrorInfoDao doorErrorInfoDao;
	@Autowired
	private DepositErrorDao depositErrorDao;

	/** 日结信息列表 */
	public static List<DayReportDoorMerchan> dayReportCountList;

	public DayReportDoorMerchan get(String id) {
		return super.get(id);
	}

	public List<DayReportDoorMerchan> findList(DayReportDoorMerchan dayReportDoorMerchan) {
		return super.findList(dayReportDoorMerchan);
	}

	public Page<DayReportDoorMerchan> findPage(Page<DayReportDoorMerchan> page,
			DayReportDoorMerchan dayReportDoorMerchan) {
		return super.findPage(page, dayReportDoorMerchan);
	}

	@Transactional(readOnly = false)
	public void save(DayReportDoorMerchan dayReportDoorMerchan) {
		super.save(dayReportDoorMerchan);
	}

	@Transactional(readOnly = false)
	public void delete(DayReportDoorMerchan dayReportDoorMerchan) {
		super.delete(dayReportDoorMerchan);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月23日 商户日结
	 *
	 */
	@Transactional(readOnly = false)
	public synchronized void dayAccountReportByMerchan(String windupType, DoorDayReportMain dayReportMain,
			Office office) {

		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 设置开始时间
		centerAccountsMain
				.setSearchDateStart(DateUtils.formatDateTime(dayReportCenterService.getDayReportMaxDate(office)));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(dayReportMain.getReportDate()));
		// 商户日结
		dayReportForMerchan(centerAccountsMain, windupType, dayReportMain, office);
	}

	@Transactional(readOnly = false)
	public String dayReportForMerchan(DoorCenterAccountsMain centerAccountsMain, String windupType,
			DoorDayReportMain dayReportMainByInsert, Office office) {
		// 设置业务类型
		List<String> businessTypeList = Lists.newArrayList();
		/** 未日结条数 */
		int unSettledCount = 0;
		// 存款账务
		businessTypeList.add(DoorOrderConstant.BusinessType.DOOR_ORDER);
		centerAccountsMain.setBusinessTypes(businessTypeList);
		// 设置查询机构(商户)
		centerAccountsMain.setMerchantOfficeId(office.getId());
		List<DoorCenterAccountsMain> list = centerAccountsMainService.findList(centerAccountsMain);
		// 门店机构ID列表（临时用）
		List<String> doorOfficeId = Lists.newArrayList();
		// 门店机构ID列表（插入用）
		// List<String> merchantOfficeId = Lists.newArrayList();
		// 各门店实体列表（临时用）
		List<Store> doorOffice = Lists.newArrayList();
		// 汇总后的商户实体列表（插入用）
		List<DayReportDoorMerchan> merchantInfosList = Lists.newArrayList();
		// 存款类型门店去重
		for (DoorCenterAccountsMain doorCenterAccountsMain : list) {
			// 去除已经结算过的
			if (doorCenterAccountsMain.getReportId() == null) {
				// 存款类型账务
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.DOOR_ORDER)) {
					if (!doorOfficeId.contains(doorCenterAccountsMain.getClientId())) {
						doorOfficeId.add(doorCenterAccountsMain.getClientId());
						Store store = new Store();
						store.setOfficeId(doorCenterAccountsMain.getClientId());
						store.setAmount(new BigDecimal(0));
						doorOffice.add(store);
					}
				}
			}
		}

		// 存款类型门店金额汇总
		for (DoorCenterAccountsMain doorCenterAccountsMain : list) {
			// 去除已经结算过的
			if (doorCenterAccountsMain.getReportId() == null) {
				// 存款类型账务
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.DOOR_ORDER)) {
					for (Store store : doorOffice) {
						if (store.getOfficeId().equals(doorCenterAccountsMain.getClientId())) {
							if (doorCenterAccountsMain.getInAmount() != null) {
								store.setAmount(store.getAmount().add(doorCenterAccountsMain.getInAmount()));
							}
							if (doorCenterAccountsMain.getOutAmount() != null) {
								store.setAmount(store.getAmount().subtract(doorCenterAccountsMain.getOutAmount()));
							}
						}
					}
					// 将日结ID与账务表中记录关联
					doorCenterAccountsMain.setReportId(dayReportMainByInsert.getReportId());
					doorCenterAccountsMain.preUpdate();
					doorCenterAccountsMainDao.update(doorCenterAccountsMain);
				}
			}
		}
		// 遍历门店机构列表
		for (Store store : doorOffice) {
			// 门店机构实体信息
			Office door = StoreCommonUtils.getOfficeById(store.getOfficeId());
			// 新建门店
			DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
			// 门店id
			dayReportDoorMerchan.setOfficeId(door.getId());
			// 门店名称
			dayReportDoorMerchan.setOfficeName(door.getName());
			// 代付状态，待确认
			dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
			// 结算时间
			dayReportDoorMerchan.setReportDate(dayReportMainByInsert.getReportDate());
			// 总金额
			dayReportDoorMerchan.setTotalAmount(store.getAmount());
			// 实际日结金额 gzd 2020-07-29
			dayReportDoorMerchan.setActuralReportAmount(store.getAmount());
			// 主键
			dayReportDoorMerchan.setId(IdGen.uuid());
			// 日结主键
			dayReportDoorMerchan.setReportId(dayReportMainByInsert.getReportId());
			// 结算类型（门店存款）
			dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.doorSave);
			// 结算人
			dayReportDoorMerchan.setRname(dayReportMainByInsert.getReportBy() == null ? "系统定时日结"
					: dayReportMainByInsert.getReportBy().getName());
			// 结算机构
			dayReportDoorMerchan.setRofficeId(office.getId());
			// 列表添加
			merchantInfosList.add(dayReportDoorMerchan);
		}
		// 生成一条流水，作为待汇款记录
		for (DayReportDoorMerchan dayReportDoorMerchan : merchantInfosList) {
			dayReportDoorMerchan.preInsert();
			int dayMerchanInsertResult = dayReportDoorMerchanDao.insert(dayReportDoorMerchan);
			if (dayMerchanInsertResult == 0) {
				String strMessageContent = "门店日结表：" + dayReportDoorMerchan.getId() + "更新失败！";
				throw new BusinessException("message.A1002", strMessageContent, new String[] { "门店日结表" });
			}
		}
		/** 设置日结条数及查询推送中心 add by lihe 2020-06-15 */
		dayReportSevenCodeService.dayReportPropelling(office, doorOffice.size(), unSettledCount);
		return ClearConstant.SUCCESS;
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月11日 公司代付/中心汇款功能
	 *          ————差错处理机制：在保证此次日结金额有剩余的前提下，各门店处理各自之前记录的差错，能处理多少就处理多少。
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	public void paid(String id) {
		User user = UserUtils.getUser();
		// 获取对应结算记录
		DayReportDoorMerchan dayReportDoorMerchan = this.get(id);
		// 设置中心账务
		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 日结主键
		centerAccountsMain.setReportId(dayReportDoorMerchan.getReportId());
		// 门店机构
		centerAccountsMain.setClientId(dayReportDoorMerchan.getOfficeId());
		// 登记机构
		centerAccountsMain.setRofficeId(user.getOffice().getId());
		// 新增七位码
		centerAccountsMain.setSevenCode(dayReportDoorMerchan.getSevenCode());
		// 相关账务记录
		List<DoorCenterAccountsMain> doorCenterAccountsMains = centerAccountsMainService.findList(centerAccountsMain);
		// 各门店机构列表
		List<String> doorOfficeId = Lists.newArrayList();
		// 各门店实体列表
		List<Store> doorOffice = Lists.newArrayList();
		// 各门店去重汇总
		for (DoorCenterAccountsMain door : doorCenterAccountsMains) {
			if (!doorOfficeId.contains(door.getClientId())) {
				doorOfficeId.add(door.getClientId());
				Store store = new Store();
				store.setOfficeId(door.getClientId());
				store.setAmount(new BigDecimal(0));
				doorOffice.add(store);
			}
		}
		// 各门店存款总金额汇总
		for (DoorCenterAccountsMain door : doorCenterAccountsMains) {
			if (door.getBusinessType().equals(DoorOrderConstant.BusinessType.DOOR_ORDER)
					|| door.getBusinessType().equals(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE)) {
				for (Store store : doorOffice) {
					if (store.getOfficeId().equals(door.getClientId())) {
						if (door.getInAmount() != null) {
							store.setAmount(store.getAmount().add(door.getInAmount()));
						}
						if (door.getOutAmount() != null) {
							store.setAmount(store.getAmount().subtract(door.getOutAmount()));
						}
					}
					// 更新各门店存款为已代付
					door.setStatus(DoorOrderConstant.AccountPaidStatus.HASPAID);
					door.preUpdate();
					doorCenterAccountsMainDao.update(door);
				}
			}
		}
		// 实际汇款金额汇总
		BigDecimal paidAmount = new BigDecimal(0);

		// gzd 2020-07-29 汇款金额 begin
		if (dayReportDoorMerchan.getActuralReportAmount() != null) {
			paidAmount = paidAmount.add(dayReportDoorMerchan.getActuralReportAmount());
		} else {
			paidAmount = paidAmount.add(dayReportDoorMerchan.getTotalAmount());
		}
		/*
		 * for (Store store : doorOffice) { paidAmount =
		 * paidAmount.add(store.getAmount()); }
		 */
		// gzd 2020-07-29 汇款金额 end

		// 关联到中心账务
		// 将流水关联到账务(中心汇款业务,客户在中心账务余额减少)
		for (Store store : doorOffice) {
			if (store.getAmount().compareTo(BigDecimal.ZERO) != 0) {
				DoorCenterAccountsMain doorCenterAccountsMainCenterPaid = new DoorCenterAccountsMain();
				// 设置关联账务流水Id
				doorCenterAccountsMainCenterPaid.setBusinessId(
						BusinessUtils.getNewBusinessNo(DoorOrderConstant.BusinessType.CENTER_PAID, user.getOffice()));
				// 设置客户Id
				doorCenterAccountsMainCenterPaid.setClientId(store.getOfficeId());
				// 设置业务类型
				doorCenterAccountsMainCenterPaid.setBusinessType(DoorOrderConstant.BusinessType.CENTER_PAID);
				// 设置出库金额
				// doorCenterAccountsMainCenterPaid.setOutAmount(store.getAmount());
				// gzd 2020-07-29
				doorCenterAccountsMainCenterPaid.setOutAmount(paidAmount);
				// 设置出入库类型
				doorCenterAccountsMainCenterPaid.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
				// 设置账务发生机构
				doorCenterAccountsMainCenterPaid.setRofficeId(user.getOffice().getId());
				// 设置账务所在机构
				doorCenterAccountsMainCenterPaid.setAofficeId(store.getOfficeId());
				// 设置业务状态
				doorCenterAccountsMainCenterPaid.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
				// 设置关联的日结ID
				doorCenterAccountsMainCenterPaid.setReportId(dayReportDoorMerchan.getReportId());
				// 关联到账务
				DoorCommonUtils.insertAccounts(doorCenterAccountsMainCenterPaid);
			}
		}
		// 关联到公司账务表，业务类型为公司代付
		CompanyAccountsMain companyAccountsMain = new CompanyAccountsMain();
		// 生成代付流水
		companyAccountsMain.setBusinessId(
				BusinessUtils.getNewBusinessNo(DoorOrderConstant.BusinessType.COMPANY_PAID, user.getOffice()));
		// 设置中心ID及名称
		companyAccountsMain.setCustNo(UserUtils.getUser().getOffice().getId());
		companyAccountsMain
				.setCustName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
		// 设置账务发生机构及归属机构ID
		companyAccountsMain.setOfficeId(UserUtils.getUser().getOffice().getId());
		companyAccountsMain.setCompanyId(StoreCommonUtils.getPlatform().get(0).getId());
		// 设置账务发生机构名称及归属机构名称
		companyAccountsMain
				.setOfficeName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
		companyAccountsMain.setCompanyName(
				StoreCommonUtils.getOfficeById(StoreCommonUtils.getPlatform().get(0).getId()).getName());
		// 设置主键
		companyAccountsMain.setId(IdGen.uuid());
		// 设置业务类型
		companyAccountsMain.setType(DoorOrderConstant.BusinessType.COMPANY_PAID);
		// 设置出入库类型
		companyAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
		// 设置业务状态
		companyAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
		// 设置出库金额
		companyAccountsMain.setOutAmount(paidAmount);
		// 公司账务主表做成
		DoorCommonUtils.insertCompanyAccounts(companyAccountsMain);

		// 设置商户实际汇款金额并更新记录
		dayReportDoorMerchan.setPaidAmount(paidAmount);
		// 设置代付状态
		dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.HASPAID);
		// 设置汇款时间
		dayReportDoorMerchan.setPaidDate(new Date());
		dayReportDoorMerchan.preUpdate();
		dayReportDoorMerchanDao.update(dayReportDoorMerchan);

		/** 线上汇款部分start wqj **/
		// 汇款金额不为0，线上汇款开关打开。进行线上汇款
		if (paidAmount.compareTo(BigDecimal.ZERO) != 0
				&& (Global.getConfig("online.remittance").equals(Constant.OnlineRemittance.TRUE))) {
			// 账号相关信息获取类
			BankAccountInfo bankAccountInfo = new BankAccountInfo();
			/** 付款人信息 **/
			bankAccountInfo.setOfficeId(StoreCommonUtils.getPlatform().get(0).getId());
			bankAccountInfo.setStatus(Constant.BankStatus.BIND);
			List<BankAccountInfo> paymentList = bankAccountInfoService.findByMerchantAndStatus(bankAccountInfo);
			if (Collections3.isEmpty(paymentList)) {
				throw new BusinessException("message.A1029", "", new String[] {});
			}
			if (StringUtils.isBlank(paymentList.get(0).getAccountNo())) {
				throw new BusinessException("message.A1023", "", new String[] {});
			}
			// 付款人账号
			String paymentAccount = paymentList.get(0).getAccountNo();
			if (StringUtils.isBlank(paymentList.get(0).getAccountName())) {
				throw new BusinessException("message.A1024", "", new String[] {});
			}
			// 付款人账号名称
			String nameOfpayersAccount = paymentList.get(0).getAccountName();
			if (StringUtils.isBlank(paymentList.get(0).getCityCode())) {
				throw new BusinessException("message.A1030", "", new String[] {});
			}
			// 付款人银行卡归属城市代码
			String cityOfPayersAccount = paymentList.get(0).getCityCode();
			if (StringUtils.isBlank(paymentList.get(0).getBankName())) {
				throw new BusinessException("message.A1031", "", new String[] {});
			}
			// 付款人银行卡开户行
			String bankNameOfpayersAccount = paymentList.get(0).getBankName();

			/** 收款人信息 **/
			bankAccountInfo.setOfficeId(dayReportDoorMerchan.getOfficeId());
			bankAccountInfo.setStatus(Constant.BankStatus.BIND);
			List<BankAccountInfo> payeeList = bankAccountInfoService.findByMerchantAndStatus(bankAccountInfo);
			if (Collections3.isEmpty(payeeList)) {
				throw new BusinessException("message.A1032", "", new String[] {});
			}
			if (StringUtils.isBlank(payeeList.get(0).getAccountNo())) {
				throw new BusinessException("message.A1025", "", new String[] {});
			}
			// 收款人账号
			String payeeAccount = payeeList.get(0).getAccountNo();
			if (StringUtils.isBlank(payeeList.get(0).getAccountName())) {
				throw new BusinessException("message.A1026", "", new String[] {});
			}
			// 收款人账号名称
			String nameOfPayee = payeeList.get(0).getAccountName();
			if (StringUtils.isBlank(payeeList.get(0).getCityCode())) {
				throw new BusinessException("message.A1033", "", new String[] {});
			}
			// 付款人银行卡归属城市代码
			String cityOfPayeeAccount = payeeList.get(0).getCityCode();
			if (StringUtils.isBlank(payeeList.get(0).getBankName())) {
				throw new BusinessException("message.A1034", "", new String[] {});
			}
			// 付款人银行卡开户行
			String bankNameOfpayeeAccount = payeeList.get(0).getBankName();
			// 判断是否他行转账
			String bankFlag;
			// 判断是否异地转账
			String placeFlag;
			if (bankNameOfpayersAccount.equals(bankNameOfpayeeAccount)) {
				// 本行转账
				bankFlag = Constant.SUCCESS;
				placeFlag = Constant.SUCCESS;
			} else {
				// 他行转账
				bankFlag = Constant.FAILED;
				if (cityOfPayersAccount.equals(cityOfPayeeAccount)) {
					// 同城转账
					placeFlag = Constant.SUCCESS;
				} else {
					// 异地转账
					placeFlag = Constant.FAILED;
				}
			}
			// 付款金额
			String payAmount = paidAmount.toString();
			if (StringUtils.isBlank(payAmount)) {
				throw new BusinessException("message.A1027", "", new String[] {});
			}
			// 汇款接口
			Map<String, Object> resultMap = bankService.singlePayment(paymentAccount, nameOfpayersAccount, payeeAccount,
					nameOfPayee, payAmount, bankFlag, placeFlag);
			// 获取交易状
			String transactionStatus = resultMap.get("transStatus").toString();
			// 为拒绝，抛出异常信息
			if (transactionStatus.equals(BankApiParamConstant.TransactionStatusValues.REFUSE)) {
				throw new BusinessException("message.A1019", "", new String[] {});
			}
			// 保存交易流水号
			dayReportDoorMerchan.setTradeSerialNumber(resultMap.get("tradeSerialNumber").toString());
			dayReportDoorMerchan.preUpdate();
			dayReportDoorMerchanDao.update(dayReportDoorMerchan);

			// 汇款记录主表做成保存
			PaidRecord paidRecord = new PaidRecord();
			// 主键
			paidRecord.setId(IdGen.uuid());
			// 汇款金额
			paidRecord.setPaidAmount(paidAmount);
			// 交易状态
			paidRecord.setTransactionStatus(transactionStatus);
			// 受理编号
			paidRecord.setToAcceptTheNumber(
					resultMap.get(BankApiParamConstant.SinglePaymentParams.TO_ACCEPT_THE_NUMBER_KEY).toString());
			// 交易流水号
			paidRecord.setTradeSerialNumber(resultMap
					.get(BankApiParamConstant.InquiryOfTransferInformationParams.TRADE_SERIAL_NUMBER_KEY).toString());
			// 商户机构ID
			paidRecord.setMerchanOfficeId(dayReportDoorMerchan.getOfficeId());
			// 商户名称
			paidRecord.setMerchanOfficeName(dayReportDoorMerchan.getOfficeName());
			// 汇款记录发生机构ID
			paidRecord.setRecordOfficeId(UserUtils.getUser().getOffice().getId());
			// 汇款记录发生机构名称
			paidRecord.setRecordOfficeName(UserUtils.getUser().getOffice().getName());
			// 插入之前处理
			paidRecord.preInsert();
			// 插入
			int paidRecordInsertResult = paidRecordDao.insert(paidRecord);
			if (paidRecordInsertResult == 0) {
				String strMessageContent = "汇款记录：" + paidRecord.getId() + "更新失败！";
				throw new BusinessException("message.A1002", strMessageContent, new String[] {});
			}
		}
		/** end **/
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年8月1日 商户汇款现金风险阈值 每个商户汇款时都有实际汇款金额，保证有足够的清机和机具余额能补回这笔汇款
	 * @return
	 */
	@Transactional(readOnly = false)
	public BigDecimal getCashRiskThreshold(String merchantId) {
		// 商户下属门店所有设备的设备余额

		// 获取商户下所有设备清机加钞明细

		// 清机总金额
		return new BigDecimal(0);
	}

	/**
	 * 获取差错笔数及金额汇总
	 *
	 * @author XL
	 * @version 2019年8月23日
	 * @param dayReportDoorMerchan
	 * @return
	 */
	public Map<String, Object> getTotalDoorError(DayReportDoorMerchan dayReportDoorMerchan) {
		return doorCenterAccountsMainDao.getTotalDoorError(dayReportDoorMerchan);
	}

	/**
	 * 根据条件查询日结列表（接口用）
	 *
	 * @author XL
	 * @version 2019年9月1日
	 * @param dayReportDoorMerchan
	 * @return
	 */
	public List<DayReportDoorMerchan> findListForApp(DayReportDoorMerchan dayReportDoorMerchan) {
		return dao.findListForApp(dayReportDoorMerchan);
	}

	/**
	 * 日结主页合计数据计算
	 *
	 * @author WQJ
	 * @version 2019年11月13日
	 * @param DayReportDoorMerchan
	 * @return
	 */
	public List<DayReportDoorMerchan> dayReportMainTotal(DayReportDoorMerchan dayReportDoorMerchan) {
		long l1 = System.currentTimeMillis();
		// 检索用
		DayReportDoorMerchan result = new DayReportDoorMerchan();
		// 显示用
		DayReportDoorMerchan show = new DayReportDoorMerchan();
		// 结果合并用
		List<DayReportDoorMerchan> resultList = Lists.newArrayList();
		// 查询条件
		// 起止时间
		result.setSearchDateStart(dayReportDoorMerchan.getSearchDateStart());
		result.setSearchDateEnd(dayReportDoorMerchan.getSearchDateEnd());
		// 门店ID
		result.setOfficeId(dayReportDoorMerchan.getOfficeId());
		// 代付状态
		result.setPaidStatus(dayReportDoorMerchan.getPaidStatus());
		// 机构过滤条件
		result.setSqlMap(dayReportDoorMerchan.getSqlMap());
		// 结算类型
		result.setSettlementType(dayReportDoorMerchan.getSettlementType());
		// 结果初始值
		// 纸币数量
		show.setPaperMoneyCount(BigDecimal.ZERO.toString());
		// 纸币金额
		show.setPaperMoneyTotal(BigDecimal.ZERO);
		// 硬币数量
		show.setCoinMoneyCount(BigDecimal.ZERO.toString());
		// 硬币金额
		show.setCoinMoneyTotal(BigDecimal.ZERO);
		// 强制金额
		show.setPack(BigDecimal.ZERO);
		// 其他金额
		show.setOther(BigDecimal.ZERO);
		// 存款总量
		show.setSaveTotal(BigDecimal.ZERO.toString());
		// 存款总额
		show.setTotalAmount(BigDecimal.ZERO);
		// 汇款金额
		show.setPaidAmount(BigDecimal.ZERO);
		List<DayReportDoorMerchan> list = dayReportDoorMerchanDao.findList(result);
		for (DayReportDoorMerchan re : list) {
			// 纸币数量和
			show.setPaperMoneyCount(
					new BigDecimal(show.getPaperMoneyCount()).add(new BigDecimal(re.getPaperMoneyCount())).toString());
			// 纸币金额和
			show.setPaperMoneyTotal(show.getPaperMoneyTotal().add(re.getPaperMoneyTotal()));
			// 硬币数量和
			show.setCoinMoneyCount(
					new BigDecimal(show.getCoinMoneyCount()).add(new BigDecimal(re.getCoinMoneyCount())).toString());
			// 硬币金额和
			show.setCoinMoneyTotal(show.getCoinMoneyTotal().add(re.getCoinMoneyTotal()));
			// 强制金额和
			show.setPack(show.getPack().add(re.getPack()));
			// 其他金额和
			show.setOther(show.getOther().add(re.getOther()));
			// 存款总量和
			show.setSaveTotal(new BigDecimal(show.getSaveTotal()).add(new BigDecimal(re.getSaveTotal())).toString());
			// 存款总额
			show.setTotalAmount(show.getTotalAmount().add(re.getTotalAmount()));
			// 汇款金额总和
			if (re.getPaidAmount() != null) {
				show.setPaidAmount(show.getPaidAmount().add(re.getPaidAmount()));
			}

		}
		show.setOfficeName("合计");
		// 结算类型
		show.setSettlementType(dayReportDoorMerchan.getSettlementType());
		resultList.add(show);
		long l2 = System.currentTimeMillis();
		System.out.println("耗时" + (l2 - l1));
		return resultList;
	}

	/**
	 * 批量汇款
	 *
	 * @author XL
	 * @version 2019年11月18日
	 * @param idList
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	public void paid(List<String> idList) {
		for (String id : idList) {
			paid(id);
		}
	}

	/**
	 * 根据条件查询商户展示列表（接口用）
	 *
	 * @author ZXK
	 * @version 2019年11月27日
	 * @param dayReportDoorMerchan
	 * @return
	 */
	public List<DayReportDoorMerchan> findMerchanListForApp(DayReportDoorMerchan dayReportDoorMerchan) {
		return dao.findMerchanListForApp(dayReportDoorMerchan);
	}

	/**
	 * 
	 * Title: confirm
	 * <p>
	 * Description: 商户确认汇款金额
	 * </p>
	 * 
	 * @author: lihe void 返回类型
	 */
	@Transactional(readOnly = false)
	public void confirm(String id) {
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		dayReportDoorMerchan.setId(id);
		dayReportDoorMerchan.setConfirmBy(UserUtils.getUser());
		dayReportDoorMerchan.setConfirmDate(new Date());
		dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
		int confirmResult = dayReportDoorMerchanDao.confirm(dayReportDoorMerchan);
		if (confirmResult == 0) {
			String strMessageContent = "汇款记录：" + dayReportDoorMerchan.getId() + "确认失败！";
			throw new BusinessException("message.A1002", strMessageContent, new String[] {});
		}
	}

	/**
	 * 存款差错类型结算
	 *
	 * @author WQJ
	 * @version 2019年12月3日
	 * @param office
	 * @return
	 */
	@Transactional(readOnly = false)
	public synchronized void saveErrorReport(Office office, Date date, User user) {
		// reportId
		//String idGen = IdGen.uuid();
		// 获取该清分中心所有门店未处理的差错记录
		DoorCenterAccountsMain doorError = new DoorCenterAccountsMain();
		doorError.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		doorError.setRofficeId(office.getId());
		doorError.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
		// 未处理的差错列表
		List<DoorCenterAccountsMain> errorList = centerAccountsMainService.findUntakeErrorList(doorError);
		// 门店机构ID列表（临时用）
		List<String> doorOfficeId = Lists.newArrayList();
		// 各门店实体列表（临时用）
		List<Store> doorOffice = Lists.newArrayList();
		// 汇总后的商户实体列表（插入用）
		List<DayReportDoorMerchan> merchantInfosList = Lists.newArrayList();
		// 差错类型门店去重
		for (DoorCenterAccountsMain error : errorList) {
			// 差错类型账务
			if (error.getBusinessType().equals(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE)) {
				if (!doorOfficeId.contains(error.getClientId())) {
					doorOfficeId.add(error.getClientId());
					Store store = new Store();
					store.setOfficeId(error.getClientId());
					store.setAmount(new BigDecimal(0));
					doorOffice.add(store);
				}
			}
		}

		// 存款类型门店金额汇总
		for (DoorCenterAccountsMain error : errorList) {
			// 存款类型账务
			if (error.getBusinessType().equals(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE)) {
				for (Store store : doorOffice) {
					if (store.getOfficeId().equals(error.getClientId())) {
						if (error.getInAmount() != null) {
							store.setAmount(store.getAmount().add(error.getInAmount()));
						}
						if (error.getOutAmount() != null) {
							store.setAmount(store.getAmount().subtract(error.getOutAmount()));
						}
					}
				}
			}
		}
		// 遍历门店机构列表
		for (Store store : doorOffice) {
			// reportId
			String idGen = IdGen.uuid();
			// 存款类型门店金额汇总
			for (DoorCenterAccountsMain error : errorList) {
				if(error.getClientId().equals(store.getOfficeId())){
					// 将日结ID与账务表中记录关联
					error.setReportId(idGen);
					error.preUpdate();
					error.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.SETTLEMENT);
					doorCenterAccountsMainDao.update(error);
				}
			}
			
			// 门店机构实体信息
			Office door = StoreCommonUtils.getOfficeById(store.getOfficeId());
			// 新建门店
			DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
			// 门店id
			dayReportDoorMerchan.setOfficeId(door.getId());
			// 门店名称
			dayReportDoorMerchan.setOfficeName(door.getName());
			// 状态：未处理
			dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
			// 结算时间
			dayReportDoorMerchan.setReportDate(date);
			// 总金额
			dayReportDoorMerchan.setTotalAmount(store.getAmount());
			// 实际日结金额 gzd 2020-07-29
			dayReportDoorMerchan.setActuralReportAmount(store.getAmount());
			// 主键
			dayReportDoorMerchan.setId(IdGen.uuid());
			// 日结主键
			dayReportDoorMerchan.setReportId(idGen);
			// 结算类型（存款差错）
			dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.saveError);
			// 结算人
			dayReportDoorMerchan.setRname(user.getName());
			// 结算机构
			dayReportDoorMerchan.setRofficeId(office.getId());
			// 列表添加
			merchantInfosList.add(dayReportDoorMerchan);
		}
		// 生成一条流水，作为待汇款记录
		for (DayReportDoorMerchan dayReportDoorMerchan : merchantInfosList) {
			dayReportDoorMerchan.preInsert();
			int dayMerchanInsertResult = dayReportDoorMerchanDao.insert(dayReportDoorMerchan);
			if (dayMerchanInsertResult == 0) {
				String strMessageContent = "门店日结表：" + dayReportDoorMerchan.getId() + "更新失败！";
				throw new BusinessException("message.A1002", strMessageContent, new String[] { "门店日结表" });
			}
		}
	}

	/**
	 * 批量异常处理
	 *
	 * @author WQJ
	 * @version 2019年12月3日
	 * @param idList
	 */
	@Transactional(readOnly = false)
	public void handle(List<String> idList) {
		for (String id : idList) {
			handle(id);
		}
	}

	/**
	 * 异常处理
	 *
	 * @author WQJ
	 * @version 2019年12月3日
	 * @param idList
	 */
	@Transactional(readOnly = false)
	public void handle(String id) {
		User user = UserUtils.getUser();
		// 实体参数，用于寻找某个门店某次结算的所有差错
		DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
		// 获取对应结算记录
		DayReportDoorMerchan dayReportDoorMerchan = this.get(id);
		// 处理过程：判断结算记录长短款，长款从中心扣款，短款向中心补款
		// 长款情况
		if (dayReportDoorMerchan.getTotalAmount().compareTo(BigDecimal.ZERO) >= 0) {
			DoorCenterAccountsMain doorCenterAccountsMainCenterHandle = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMainCenterHandle.setBusinessId(
					BusinessUtils.getNewBusinessNo(DoorOrderConstant.BusinessType.ERROR_HANDLE, user.getOffice()));
			// 设置客户Id
			doorCenterAccountsMainCenterHandle.setClientId(dayReportDoorMerchan.getOfficeId());
			// 设置业务类型
			doorCenterAccountsMainCenterHandle.setBusinessType(DoorOrderConstant.BusinessType.ERROR_HANDLE);
			// 设置出库金额
			doorCenterAccountsMainCenterHandle.setOutAmount(dayReportDoorMerchan.getTotalAmount());
			// 设置出入库类型
			doorCenterAccountsMainCenterHandle.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
			// 设置账务发生机构
			doorCenterAccountsMainCenterHandle.setRofficeId(user.getOffice().getId());
			// 设置账务所在机构
			doorCenterAccountsMainCenterHandle.setAofficeId(dayReportDoorMerchan.getOfficeId());
			// 设置业务状态
			doorCenterAccountsMainCenterHandle.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
			// 设置关联的日结ID
			doorCenterAccountsMainCenterHandle.setReportId(dayReportDoorMerchan.getReportId());
			// 关联到账务
			DoorCommonUtils.insertAccounts(doorCenterAccountsMainCenterHandle);
			// 通过reportId和clientId找差错记录
			doorCenterAccountsMain.setReportId(dayReportDoorMerchan.getReportId());
			doorCenterAccountsMain.setClientId(dayReportDoorMerchan.getOfficeId());
			List<DoorCenterAccountsMain> error = doorCenterAccountsMainDao.getErrorByReportId(doorCenterAccountsMain);
			// 将差错记录更新，结算状态修改为已处理，加上reportId
			for (DoorCenterAccountsMain d : error) {
				d.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.TAKEUP);
				d.preUpdate();
				doorCenterAccountsMainDao.update(d);
			}
			// 设置门店实际差错处理金额并更新记录
			dayReportDoorMerchan.setPaidAmount(dayReportDoorMerchan.getTotalAmount());
			// 设置代付状态
			dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.ErrorIsTakeUp.TAKEUP);
			// 设置汇款时间
			dayReportDoorMerchan.setPaidDate(new Date());
			dayReportDoorMerchan.preUpdate();
			dayReportDoorMerchanDao.update(dayReportDoorMerchan);
		}
		// 短款情况
		else {
			DoorCenterAccountsMain doorCenterAccountsMainCenterHandle = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMainCenterHandle.setBusinessId(
					BusinessUtils.getNewBusinessNo(DoorOrderConstant.BusinessType.ERROR_HANDLE, user.getOffice()));
			// 设置客户Id
			doorCenterAccountsMainCenterHandle.setClientId(dayReportDoorMerchan.getOfficeId());
			// 设置业务类型
			doorCenterAccountsMainCenterHandle.setBusinessType(DoorOrderConstant.BusinessType.ERROR_HANDLE);
			// 设置入库金额(取绝对值)
			doorCenterAccountsMainCenterHandle.setInAmount(dayReportDoorMerchan.getTotalAmount().abs());
			// 设置出入库类型
			doorCenterAccountsMainCenterHandle.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
			// 设置账务发生机构
			doorCenterAccountsMainCenterHandle.setRofficeId(user.getOffice().getId());
			// 设置账务所在机构
			doorCenterAccountsMainCenterHandle.setAofficeId(dayReportDoorMerchan.getOfficeId());
			// 设置业务状态
			doorCenterAccountsMainCenterHandle.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
			// 设置关联的日结ID
			doorCenterAccountsMainCenterHandle.setReportId(dayReportDoorMerchan.getReportId());
			// 关联到账务
			DoorCommonUtils.insertAccounts(doorCenterAccountsMainCenterHandle);
			// 通过reportId和clientId找差错记录
			doorCenterAccountsMain.setReportId(dayReportDoorMerchan.getReportId());
			doorCenterAccountsMain.setClientId(dayReportDoorMerchan.getOfficeId());
			List<DoorCenterAccountsMain> error = doorCenterAccountsMainDao.getErrorByReportId(doorCenterAccountsMain);
			// 将差错记录更新，结算状态修改为已处理，加上reportId
			for (DoorCenterAccountsMain d : error) {
				d.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.TAKEUP);
				d.preUpdate();
				doorCenterAccountsMainDao.update(d);
			}
			// 设置门店实际差错处理金额并更新记录
			dayReportDoorMerchan.setPaidAmount(dayReportDoorMerchan.getTotalAmount().abs());
			// 设置代付状态
			dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.ErrorIsTakeUp.TAKEUP);
			// 设置汇款时间
			dayReportDoorMerchan.setPaidDate(new Date());
			dayReportDoorMerchan.preUpdate();
			dayReportDoorMerchanDao.update(dayReportDoorMerchan);
		}
		// TODO
		// 差错处理后 在相关清点差错或存款差错表中 ， 将对应信息记录状态改为‘平账’，列表中不予显示
		// 设置业务类型
		doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		List<DoorCenterAccountsMain> centerList = doorCenterAccountsMainDao.getDetailByReportId(doorCenterAccountsMain);
		for (DoorCenterAccountsMain centerInfo : centerList) {
			// 中心业务流水
			String businessId = centerInfo.getBusinessId();
			// 查找清点差错表中对应记录
			DoorErrorInfo errorInfo = doorErrorInfoDao.get(businessId);
			if (null != errorInfo) {
				// 状态 设置为‘平账’
				errorInfo.setStatus(DoorOrderConstant.Status.CLEAR_ACCOUNT);
				errorInfo.preUpdate();
				doorErrorInfoDao.update(errorInfo);
			}
			// 查找存款差错表中对应记录
			DepositError de = new DepositError();
			de.setOrderId(businessId);
			de.setStatus(DoorOrderConstant.StatusType.CREATE);
			List<DepositError> depositErrorList = depositErrorDao.findList(de);
			if (!Collections3.isEmpty(depositErrorList)) {
				DepositError depositError = depositErrorList.get(0);
				depositError.setStatus(DoorOrderConstant.StatusType.CLEAR_ACCOUNT);
				depositError.preUpdate();
				depositErrorDao.update(depositError);
			}
		}

	}

	/**
	 * 
	 * Title: batchConfirm
	 * <p>
	 * Description: 商户确认汇款金额
	 * </p>
	 * 
	 * @author: lihe void 返回类型
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
	public void batchConfirm(List<String> idList) {
		for (String id : idList) {
			confirm(id);
		}
	}

	/**
	 * 传统存款类型结算
	 *
	 * @author WQJ
	 * @version 2019年12月17日
	 * @param office
	 * @return
	 */
	@Transactional(readOnly = false)
	public synchronized void traditionalSaveReport(Office office, Date date, User user,
			DayReportDoorMerchan dayMerchan) {
		// 按中心统计日结相关信息
		int totalCount = 0;
		int unSettledCount = 0;
		// 张家港商户走此结算逻辑
		if (office.getParentIds().contains(Global.getConfig("officeId.zhangjiagang"))) {
			// 获取该清分中心所有未代付的传统存款记录
			DoorCenterAccountsMain traditionalSave = new DoorCenterAccountsMain();
			traditionalSave.setBusinessType(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
			traditionalSave.setRofficeId(office.getId());
			traditionalSave.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			/** 按日期结算 add by lihe 2020-04-16 start */
			if (StringUtils.isNotBlank(dayMerchan.getSearchDateStart())) {
				// 设置页面开始时间为搜索结束时间，查询页面点选的时间之前是否存在未结算记录
				traditionalSave.setSaveMoneyDate(dayMerchan.getSearchDateStart());
				// 未代付的传统存款列表
				List<DoorCenterAccountsMain> traditionalList = centerAccountsMainService
						.findNopaidTraditionalSave(traditionalSave);
				// 判断页面点选的时间之前是否存在未结算记录
				if (!Collections3.isEmpty(traditionalList)) {
					throw new BusinessException("message.E5005", "", new String[] {
							DateUtils.formatDate(traditionalList.get(0).getCreateDate(), "yyyy-MM-dd HH:mm") });
				}
				traditionalSave.setSaveMoneyDate(null);
				// 初始化查询时间
				traditionalSave.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStartOrEnd(
						DateUtils.parseDate(dayMerchan.getSearchDateStart()), "yyyy-MM-dd HH:mm", ":00")));
			}
			// 设置查询结束时间
			if (StringUtils.isNotBlank(dayMerchan.getSearchDateEnd())) {
				traditionalSave.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateStartOrEnd(
						DateUtils.parseDate(dayMerchan.getSearchDateEnd()), "yyyy-MM-dd HH:mm", ":59")));
			}
			/** 按日期结算 add by lihe 2020-04-16 end */
			// 未代付的传统存款列表
			List<DoorCenterAccountsMain> traditionalSaveList = centerAccountsMainService
					.findNopaidTraditionalSave(traditionalSave);
			/** 统计结算条数 add by HuZhiYong 2020-08-04 */
			totalCount += traditionalSaveList.size();

			/** 获取该时间段记录对应的冲正记录 add by lihe 2020-06-17 start */
			if (!Collections3.isEmpty(traditionalSaveList)) {
				List<DoorCenterAccountsMain> reverseList = doorCenterAccountsMainDao
						.getReverseList(traditionalSaveList);
				if (!Collections3.isEmpty(reverseList)) {
					traditionalSaveList.addAll(reverseList);
				}
			}
			/** 获取该时间段记录对应的冲正记录 add by lihe 2020-06-17 end */
			// 门店机构七位码列表（临时用）
			List<String> doorSevenCodeList = Lists.newArrayList();
			// 门店实体列表，用于按七位码存储金额（临时用）
			List<Store> doorOffice = Lists.newArrayList();
			// 汇总后的门店实体列表（插入用）
			List<DayReportDoorMerchan> merchantInfosList = Lists.newArrayList();
			// 将该门店七位码去重保存
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 传统存款类型账务，找出该门店的七位码
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE)) {
					if (!doorSevenCodeList.contains(doorCenterAccountsMain.getSevenCode())) {
						doorSevenCodeList.add(doorCenterAccountsMain.getSevenCode());
						Store store = new Store();
						store.setOfficeId(doorCenterAccountsMain.getClientId());
						store.setAmount(new BigDecimal(0));
						// 新增七位码字段
						store.setSevenCode(doorCenterAccountsMain.getSevenCode());
						doorOffice.add(store);
					}
				}
			}
			// 门店金额按七位码汇总
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 传统存款类型账务
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE)) {
					for (Store store : doorOffice) {
						if (store.getSevenCode().equals(doorCenterAccountsMain.getSevenCode())
								&& (store.getOfficeId().equals(doorCenterAccountsMain.getClientId()))) {
							if (doorCenterAccountsMain.getInAmount() != null) {
								store.setAmount(store.getAmount().add(doorCenterAccountsMain.getInAmount()));
							}
							if (doorCenterAccountsMain.getOutAmount() != null) {
								store.setAmount(store.getAmount().subtract(doorCenterAccountsMain.getOutAmount()));
							}
						}
					}
				}
			}
			// 遍历门店机构列表
			for (Store store : doorOffice) {
				// 设置日结主键
				String idGen = IdGen.uuid();
				// 关联reportId
				for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
					// 存款类型账务
					if (doorCenterAccountsMain.getBusinessType()
							.equals(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE)) {
						if (store.getSevenCode().equals(doorCenterAccountsMain.getSevenCode())
								&& (store.getOfficeId().equals(doorCenterAccountsMain.getClientId()))) {
							// 将日结ID与账务表中记录关联
							doorCenterAccountsMain.setReportId(idGen);
							// 设置该传统存款状态为待确认,防止之后重复结算
							doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
							doorCenterAccountsMain.preUpdate();
							doorCenterAccountsMainDao.update(doorCenterAccountsMain);
						}
					}
				}
				// 门店机构实体信息
				Office door = StoreCommonUtils.getOfficeById(store.getOfficeId());
				// 新建门店
				DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
				// 门店id
				dayReportDoorMerchan.setOfficeId(door.getId());
				// 门店名称
				dayReportDoorMerchan.setOfficeName(door.getName());
				// 代付状态，待确认
				dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
				// 结算时间
				dayReportDoorMerchan.setReportDate(date);
				// 总金额
				dayReportDoorMerchan.setTotalAmount(store.getAmount());
				// 实际日结金额 gzd 2020-07-30
				dayReportDoorMerchan.setActuralReportAmount(store.getAmount());
				// 主键
				dayReportDoorMerchan.setId(IdGen.uuid());
				// 日结主键
				dayReportDoorMerchan.setReportId(idGen);
				// 结算类型（传统存款）
				dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.traditionalSave);
				// 结算人
				dayReportDoorMerchan.setRname(user.getName() == null ? "" : user.getName());
				// 结算机构
				dayReportDoorMerchan.setRofficeId(office.getId());
				// 新增七位码
				dayReportDoorMerchan.setSevenCode(store.getSevenCode());
				// 列表添加
				merchantInfosList.add(dayReportDoorMerchan);
			}
			// 生成一条流水，作为待汇款记录
			for (DayReportDoorMerchan dayReportDoorMerchan : merchantInfosList) {
				dayReportDoorMerchan.preInsert();
				int dayMerchanInsertResult = dayReportDoorMerchanDao.insert(dayReportDoorMerchan);
				if (dayMerchanInsertResult == 0) {
					String strMessageContent = "门店日结表：" + dayReportDoorMerchan.getId() + "更新失败！";
					throw new BusinessException("message.A1002", strMessageContent, new String[] { "门店日结表" });
				}
			}
		}
		// 其他机构按门店普通结算
		else {
			// 设置日结主键
			String idGen = IdGen.uuid();
			// 获取该清分中心所有未代付的传统存款记录
			DoorCenterAccountsMain traditionalSave = new DoorCenterAccountsMain();
			traditionalSave.setBusinessType(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
			traditionalSave.setRofficeId(office.getId());
			traditionalSave.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			// 未代付的传统存款列表
			List<DoorCenterAccountsMain> traditionalSaveList = centerAccountsMainService
					.findNopaidTraditionalSave(traditionalSave);
			/** 统计结算条数 add by HuZhiYong 2020-08-04 */
			totalCount += traditionalSaveList.size();
			/** 获取该时间段记录对应的冲正记录 add by lihe 2020-06-17 start */
			if (!Collections3.isEmpty(traditionalSaveList)) {
				List<DoorCenterAccountsMain> reverseList = doorCenterAccountsMainDao
						.getReverseList(traditionalSaveList);
				if (!Collections3.isEmpty(reverseList)) {
					traditionalSaveList.addAll(reverseList);
				}
			}
			/** 获取该时间段记录对应的冲正记录 add by lihe 2020-06-17 end */
			// 门店机构ID列表（临时用）
			List<String> doorOfficeId = Lists.newArrayList();
			// 各门店实体列表（临时用）
			List<Store> doorOffice = Lists.newArrayList();
			// 汇总后的商户实体列表（插入用）
			List<DayReportDoorMerchan> merchantInfosList = Lists.newArrayList();

			// 存款类型门店去重
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 存款类型账务
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE)) {
					if (!doorOfficeId.contains(doorCenterAccountsMain.getClientId())) {
						doorOfficeId.add(doorCenterAccountsMain.getClientId());
						Store store = new Store();
						store.setOfficeId(doorCenterAccountsMain.getClientId());
						store.setAmount(new BigDecimal(0));
						doorOffice.add(store);
					}
				}
			}

			// 存款类型门店金额汇总
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 存款类型账务
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE)) {
					for (Store store : doorOffice) {
						if (store.getOfficeId().equals(doorCenterAccountsMain.getClientId())) {
							if (doorCenterAccountsMain.getInAmount() != null) {
								store.setAmount(store.getAmount().add(doorCenterAccountsMain.getInAmount()));
							}
							if (doorCenterAccountsMain.getOutAmount() != null) {
								store.setAmount(store.getAmount().subtract(doorCenterAccountsMain.getOutAmount()));
							}
						}
					}
					// 将日结ID与账务表中记录关联
					doorCenterAccountsMain.setReportId(idGen);
					// 设置该传统存款状态为待确认,防止之后重复结算
					doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
					doorCenterAccountsMain.preUpdate();
					doorCenterAccountsMainDao.update(doorCenterAccountsMain);
				}
			}
			// 遍历门店机构列表
			for (Store store : doorOffice) {
				// 门店机构实体信息
				Office door = StoreCommonUtils.getOfficeById(store.getOfficeId());
				// 新建门店
				DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
				// 门店id
				dayReportDoorMerchan.setOfficeId(door.getId());
				// 门店名称
				dayReportDoorMerchan.setOfficeName(door.getName());
				// 代付状态，待确认
				dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
				// 结算时间
				dayReportDoorMerchan.setReportDate(date);
				// 总金额
				dayReportDoorMerchan.setTotalAmount(store.getAmount());
				// 实际日结金额 gzd 2020-07-30
				dayReportDoorMerchan.setActuralReportAmount(store.getAmount());
				// 主键
				dayReportDoorMerchan.setId(IdGen.uuid());
				// 日结主键
				dayReportDoorMerchan.setReportId(idGen);
				// 结算类型（传统存款）
				dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.traditionalSave);
				// 结算人
				dayReportDoorMerchan.setRname(user.getName() == null ? "" : user.getName());
				// 结算机构
				dayReportDoorMerchan.setRofficeId(office.getId());
				// 列表添加
				merchantInfosList.add(dayReportDoorMerchan);
			}
			// 生成一条流水，作为待汇款记录
			for (DayReportDoorMerchan dayReportDoorMerchan : merchantInfosList) {
				dayReportDoorMerchan.preInsert();
				int dayMerchanInsertResult = dayReportDoorMerchanDao.insert(dayReportDoorMerchan);
				if (dayMerchanInsertResult == 0) {
					String strMessageContent = "门店日结表：" + dayReportDoorMerchan.getId() + "更新失败！";
					throw new BusinessException("message.A1002", strMessageContent, new String[] { "门店日结表" });
				}
			}
		}
		/** 设置日结条数 add by HuZhiYong 2020-08-05 */
		traditionalReportPropelling(office, totalCount, unSettledCount);
	}

	/**
	 * 
	 * Title: findErrorExportList
	 * <p>
	 * Description: 获取差错日结列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param DayReportDoorMerchan
	 * @return List<DayReportDoorMerchan> 返回类型
	 */
	public List<DayReportDoorMerchan> findErrorList(DayReportDoorMerchan dayReportDoorMerchan) {
		// 查询条件： 开始时间
		if (dayReportDoorMerchan.getCreateTimeStart() != null) {
			dayReportDoorMerchan.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(dayReportDoorMerchan.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (dayReportDoorMerchan.getCreateTimeEnd() != null) {
			dayReportDoorMerchan.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(dayReportDoorMerchan.getCreateTimeEnd())));
		}
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.saveError);
		// 差错类型转换赋值
		if (DoorOrderConstant.ErrorType.LONG_CURRENCY.equals(dayReportDoorMerchan.getErrorType())) {
			dayReportDoorMerchan.setErrorType(DoorOrderConstant.ErrorTypeName.LONG_CURRENCY);
		} else if (DoorOrderConstant.ErrorType.SHORT_CURRENCY.equals(dayReportDoorMerchan.getErrorType())) {
			dayReportDoorMerchan.setErrorType(DoorOrderConstant.ErrorTypeName.SHORT_CURRENCY);
		} else {
			dayReportDoorMerchan.setErrorType("");
		}
		List<DayReportDoorMerchan> errorList = findList(dayReportDoorMerchan);
		// 差错类型转换赋值
		if (DoorOrderConstant.ErrorTypeName.LONG_CURRENCY.equals(dayReportDoorMerchan.getErrorType())) {
			dayReportDoorMerchan.setErrorType(DoorOrderConstant.ErrorType.LONG_CURRENCY);
		} else if (DoorOrderConstant.ErrorTypeName.SHORT_CURRENCY.equals(dayReportDoorMerchan.getErrorType())) {
			dayReportDoorMerchan.setErrorType(DoorOrderConstant.ErrorType.SHORT_CURRENCY);
		} else {
			dayReportDoorMerchan.setErrorType("");
		}
		return errorList;
	}

	/**
	 * 
	 * Title: findErrorPage
	 * <p>
	 * Description: 获取差错日结分页列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param dayReportDoorMerchan
	 * @return Page<DayReportDoorMerchan> 返回类型
	 */
	public Page<DayReportDoorMerchan> findErrorPage(Page<DayReportDoorMerchan> page,
			DayReportDoorMerchan dayReportDoorMerchan) {
		dayReportDoorMerchan.setPage(page);
		page.setList(findErrorList(dayReportDoorMerchan));
		return page;
	}

	/**
	 * 电子线下存款类型结算
	 *
	 * @author WQJ
	 * @version 2020年1月16日
	 * @return
	 */
	@Transactional(readOnly = false)
	public synchronized void offlineSaveReport(Office office, Date date, User user) {
		// 张家港商户走此结算逻辑
		if (office.getParentIds().contains(Global.getConfig("officeId.zhangjiagang"))) {
			// 获取该清分中心所有未代付的电子线下存款记录
			DoorCenterAccountsMain traditionalSave = new DoorCenterAccountsMain();
			traditionalSave.setBusinessType(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE);
			traditionalSave.setRofficeId(office.getId());
			traditionalSave.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			traditionalSave.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
			// 未代付的电子线下存款列表
			List<DoorCenterAccountsMain> traditionalSaveList = centerAccountsMainService
					.findNopaidTraditionalSave(traditionalSave);
			// 门店机构七位码列表（临时用）
			List<String> doorSevenCodeList = Lists.newArrayList();
			// 门店实体列表，用于按七位码存储金额（临时用）
			List<Store> doorOffice = Lists.newArrayList();
			// 汇总后的门店实体列表（插入用）
			List<DayReportDoorMerchan> merchantInfosList = Lists.newArrayList();
			// 将该门店七位码去重保存
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 电子线下类型账务，找出该门店的七位码
				if (doorCenterAccountsMain.getBusinessType()
						.equals(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE)) {
					if (!doorSevenCodeList.contains(doorCenterAccountsMain.getSevenCode())) {
						doorSevenCodeList.add(doorCenterAccountsMain.getSevenCode());
						Store store = new Store();
						store.setOfficeId(doorCenterAccountsMain.getClientId());
						store.setAmount(new BigDecimal(0));
						// 新增七位码字段
						store.setSevenCode(doorCenterAccountsMain.getSevenCode());
						doorOffice.add(store);
					}
				}
			}
			// 门店金额按七位码汇总
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 电子线下存款类型账务
				if (doorCenterAccountsMain.getBusinessType()
						.equals(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE)) {
					for (Store store : doorOffice) {
						if (store.getSevenCode().equals(doorCenterAccountsMain.getSevenCode())
								&& (store.getOfficeId().equals(doorCenterAccountsMain.getClientId()))) {
							store.setAmount(store.getAmount().add(doorCenterAccountsMain.getInAmount()));
						}
					}
				}
			}
			// 遍历门店机构列表
			for (Store store : doorOffice) {
				// 设置日结主键
				String idGen = IdGen.uuid();
				// 关联reportId
				for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
					// 电子线下存款类型账务
					if (doorCenterAccountsMain.getBusinessType()
							.equals(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE)) {
						if (store.getSevenCode().equals(doorCenterAccountsMain.getSevenCode())
								&& (store.getOfficeId().equals(doorCenterAccountsMain.getClientId()))) {
							// 将日结ID与账务表中记录关联
							doorCenterAccountsMain.setReportId(idGen);
							// 设置该电子线下存款状态为待确认,防止之后重复结算
							doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
							doorCenterAccountsMain.preUpdate();
							doorCenterAccountsMainDao.update(doorCenterAccountsMain);
						}
					}
				}
				// 门店机构实体信息
				Office door = StoreCommonUtils.getOfficeById(store.getOfficeId());
				// 新建门店
				DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
				// 门店id
				dayReportDoorMerchan.setOfficeId(door.getId());
				// 门店名称
				dayReportDoorMerchan.setOfficeName(door.getName());
				// 代付状态，待确认
				dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
				// 结算时间
				dayReportDoorMerchan.setReportDate(date);
				// 总金额
				dayReportDoorMerchan.setTotalAmount(store.getAmount());
				// 实际日结金额 gzd 2020-08-24
				dayReportDoorMerchan.setActuralReportAmount(store.getAmount());
				// 主键
				dayReportDoorMerchan.setId(IdGen.uuid());
				// 日结主键
				dayReportDoorMerchan.setReportId(idGen);
				// 结算类型（电子线下存款）
				dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE);
				// 结算人
				dayReportDoorMerchan.setRname(user.getName() == null ? "" : user.getName());
				// 结算机构
				dayReportDoorMerchan.setRofficeId(office.getId());
				// 新增七位码
				dayReportDoorMerchan.setSevenCode(store.getSevenCode());
				// 列表添加
				merchantInfosList.add(dayReportDoorMerchan);
			}
			// 生成一条流水，作为待汇款记录
			for (DayReportDoorMerchan dayReportDoorMerchan : merchantInfosList) {
				dayReportDoorMerchan.preInsert();
				int dayMerchanInsertResult = dayReportDoorMerchanDao.insert(dayReportDoorMerchan);
				if (dayMerchanInsertResult == 0) {
					String strMessageContent = "门店日结表：" + dayReportDoorMerchan.getId() + "更新失败！";
					throw new BusinessException("message.A1002", strMessageContent, new String[] { "门店日结表" });
				}
			}
		}
		// 其他机构按门店普通结算
		else {
			// 设置日结主键
			String idGen = IdGen.uuid();
			// 获取该清分中心所有未代付的电子线下存款记录
			DoorCenterAccountsMain traditionalSave = new DoorCenterAccountsMain();
			traditionalSave.setBusinessType(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE);
			traditionalSave.setRofficeId(office.getId());
			traditionalSave.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			traditionalSave.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
			// 未代付的电子线下存款列表
			List<DoorCenterAccountsMain> traditionalSaveList = centerAccountsMainService
					.findNopaidTraditionalSave(traditionalSave);
			// 门店机构ID列表（临时用）
			List<String> doorOfficeId = Lists.newArrayList();
			// 各门店实体列表（临时用）
			List<Store> doorOffice = Lists.newArrayList();
			// 汇总后的商户实体列表（插入用）
			List<DayReportDoorMerchan> merchantInfosList = Lists.newArrayList();

			// 存款类型门店去重
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 存款类型账务
				if (doorCenterAccountsMain.getBusinessType()
						.equals(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE)) {
					if (!doorOfficeId.contains(doorCenterAccountsMain.getClientId())) {
						doorOfficeId.add(doorCenterAccountsMain.getClientId());
						Store store = new Store();
						store.setOfficeId(doorCenterAccountsMain.getClientId());
						store.setAmount(new BigDecimal(0));
						doorOffice.add(store);
					}
				}
			}

			// 存款类型门店金额汇总
			for (DoorCenterAccountsMain doorCenterAccountsMain : traditionalSaveList) {
				// 存款类型账务
				if (doorCenterAccountsMain.getBusinessType()
						.equals(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE)) {
					for (Store store : doorOffice) {
						if (store.getOfficeId().equals(doorCenterAccountsMain.getClientId())) {
							if (doorCenterAccountsMain.getInAmount() != null) {
								store.setAmount(store.getAmount().add(doorCenterAccountsMain.getInAmount()));
							}
							if (doorCenterAccountsMain.getOutAmount() != null) {
								store.setAmount(store.getAmount().subtract(doorCenterAccountsMain.getOutAmount()));
							}
						}
					}
					// 将日结ID与账务表中记录关联
					doorCenterAccountsMain.setReportId(idGen);
					// 设置该电子线下存款状态为待确认,防止之后重复结算
					doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
					doorCenterAccountsMain.preUpdate();
					doorCenterAccountsMainDao.update(doorCenterAccountsMain);
				}
			}
			// 遍历门店机构列表
			for (Store store : doorOffice) {
				// 门店机构实体信息
				Office door = StoreCommonUtils.getOfficeById(store.getOfficeId());
				// 新建门店
				DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
				// 门店id
				dayReportDoorMerchan.setOfficeId(door.getId());
				// 门店名称
				dayReportDoorMerchan.setOfficeName(door.getName());
				// 代付状态，待确认
				dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
				// 结算时间
				dayReportDoorMerchan.setReportDate(date);
				// 总金额
				dayReportDoorMerchan.setTotalAmount(store.getAmount());
				// 实际日结金额 gzd 2020-08-24
				dayReportDoorMerchan.setActuralReportAmount(store.getAmount());
				// 主键
				dayReportDoorMerchan.setId(IdGen.uuid());
				// 日结主键
				dayReportDoorMerchan.setReportId(idGen);
				// 结算类型（电子线下存款）
				dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE);
				// 结算人
				dayReportDoorMerchan.setRname(user.getName() == null ? "" : user.getName());
				// 结算机构
				dayReportDoorMerchan.setRofficeId(office.getId());
				// 列表添加
				merchantInfosList.add(dayReportDoorMerchan);
			}
			// 生成一条流水，作为待汇款记录
			for (DayReportDoorMerchan dayReportDoorMerchan : merchantInfosList) {
				dayReportDoorMerchan.preInsert();
				int dayMerchanInsertResult = dayReportDoorMerchanDao.insert(dayReportDoorMerchan);
				if (dayMerchanInsertResult == 0) {
					String strMessageContent = "门店日结表：" + dayReportDoorMerchan.getId() + "更新失败！";
					throw new BusinessException("message.A1002", strMessageContent, new String[] { "门店日结表" });
				}
			}
		}
	}

	/**
	 * 获取该中心传统结算最新结算时间，在此时间之前的登记传统存款不可冲正修改。
	 * 
	 * @author wqj
	 * @version 2020年3月13日
	 */
	public Date getTraditionalsaveMaxDate(Office office) {
		return dayReportDoorMerchanDao.getTraditionalsaveMaxDate(office);
	}

	/**
	 * 
	 * Title: getSummation
	 * <p>
	 * Description: 获取日结合计金额
	 * </p>
	 * 
	 * @author: lihe
	 * @param dayReportDoorMerchan
	 * @return DayReportDoorMerchan 返回类型
	 */
	public DayReportDoorMerchan getSummation(DayReportDoorMerchan dayReportDoorMerchan) {
		// 检索用
		DayReportDoorMerchan condition = new DayReportDoorMerchan();
		// 设置条件
		condition = dayReportDoorMerchan;
		// 去掉分页
		condition.setPage(null);
		// 结果初始值
		DayReportDoorMerchan result = dayReportDoorMerchanDao.getSummation(condition);
		if (result == null) {
			result = new DayReportDoorMerchan();
		}
		result.setOfficeName("合计");
		return result;
	}

	/**
	 * 门店日结导出列表
	 *
	 * @author ZXK
	 * @version 2019年12月16日
	 * @param doorCenterAccountsMain
	 * @return
	 */
	public List<DayReportAccountExport> findDayReportAccountExportList(DayReportDoorMerchan dayReportDoorMerchan) {

		return dayReportDoorMerchanDao.findDayReportAccountExportList(dayReportDoorMerchan);
	}

	@Transactional(readOnly = false)
	public void update(DayReportDoorMerchan dayReportDoorMerchan) {
		dayReportDoorMerchanDao.update(dayReportDoorMerchan);
	}

	/**
	 * 
	 * Title: traditionalReportPropelling
	 * <p>
	 * Description: 传统日结消息推送
	 * </p>
	 * 
	 * @author: HuZhiYong
	 * @param office
	 *            日结商户
	 * @param size
	 *            日结条数
	 * @param unSettledCount
	 *            未日结条数 void 返回类型
	 */
	public void traditionalReportPropelling(Office office, int size, int unSettledCount) {
		DayReportDoorMerchan countMerchan = new DayReportDoorMerchan();
		countMerchan.setOfficeId(office.getId());
		countMerchan.setOfficeName(office.getName());
		// 设置日结条数
		countMerchan.setTotalCount(size);
		// 设置未日结条数
		countMerchan.setUnSettledCount(unSettledCount);
		if (dayReportCountList != null) {
			dayReportCountList.add(countMerchan);
		}
		logger.info("------{}日结条数:{}------", office.getName(), size);
		logger.info("------{}未日结条数:{}------", office.getName(), unSettledCount);
	}

}
