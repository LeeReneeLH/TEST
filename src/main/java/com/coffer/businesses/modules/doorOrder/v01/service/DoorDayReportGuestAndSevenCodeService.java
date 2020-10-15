package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportGuestDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportGuest;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 客户账务日结Service（张家港修改部分，只结算自己）
 * 
 * @author wqj
 * @version 2019-12-9
 */
@Service
@Transactional(readOnly = true)
public class DoorDayReportGuestAndSevenCodeService extends CrudService<DoorDayReportGuestDao, DoorDayReportGuest> {

	@Autowired
	private DoorCenterAccountsMainDao centerAccountsMainDao;

	@Autowired
	private DoorDayReportGuestDao dayReportGuestDao;

	@Autowired
	private DoorDayReportMainDao dayReportMainDao;

	public DoorDayReportGuest get(String id) {
		return super.get(id);
	}

	public List<DoorDayReportGuest> findList(DoorDayReportGuest dayReportGuest) {
		return super.findList(dayReportGuest);
	}

	public Page<DoorDayReportGuest> findPage(Page<DoorDayReportGuest> page, DoorDayReportGuest dayReportGuest) {
		return super.findPage(page, dayReportGuest);
	}

	@Transactional(readOnly = false)
	public void save(DoorDayReportGuest dayReportGuest) {
		super.save(dayReportGuest);
	}

	@Transactional(readOnly = false)
	public void delete(DoorDayReportGuest dayReportGuest) {
		super.delete(dayReportGuest);
	}

	/**
	 * 
	 * @author wqj
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @version 2019年12月9日 （张家港修改部分，只结算自己）
	 *
	 */
	@Transactional(readOnly = false)
	public synchronized void dayAccountReportByGuestAndSevenCode(String windupType, DoorDayReportMain dayReportMain,
			Office office) {
		// 上门收款业务日结（张家港修改部分，只结算自己）
		dayDoorOrderAccountReportAndSevenCode(new DoorCenterAccountsMain(), windupType, dayReportMain, office);
	}

	/**
	 * 
	 * 
	 * @author wqj
	 * @version 2017年9月6日 （张家港修改部分，只结算自己）
	 * 
	 * @param centerAccountsMain
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @return
	 */
	@Transactional(readOnly = false)
	public String dayDoorOrderAccountReportAndSevenCode(DoorCenterAccountsMain centerAccountsMain, String windupType,
			DoorDayReportMain dayReportMain, Office office) {
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DoorCommonUtils.getDayReportMaxDate(office)));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(dayReportMain.getReportDate()));
		// 获取昨日日结
		DoorDayReportGuest dayReportGuest = new DoorDayReportGuest();
		// 设置业务类型
		List<String> dayClearBusinessTypeList = Lists.newArrayList();
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.CENTER_PAID);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.DOOR_ORDER);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.ERROR_HANDLE);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 商户结算主表信息初始化
		// 设置结账日期为昨日
		Date maxdate = DoorCommonUtils.getDayReportMaxDate(office);
		List<DoorDayReportMain> dayReportList = Lists.newArrayList();
		if (maxdate != null) {
			// 设置开始时间
			dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxdate),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置结束时间
			dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(maxdate),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置账务发生机构
			dayReportMain.setOffice(office);
			// 获取昨日日结主表商户数据
			dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
		}
		if (Collections3.isEmpty(dayReportList)) {
			dayReportMain.setBeforeAmount(new BigDecimal(0));
			dayReportMain.setTotalAmount(new BigDecimal(0));
		} else {
			dayReportMain.setBeforeAmount(dayReportList.get(0).getTotalAmount());
			dayReportMain.setTotalAmount(dayReportList.get(0).getTotalAmount());
		}
		dayReportMain.setInCount(new BigDecimal(0));
		dayReportMain.setOutCount(new BigDecimal(0));
		dayReportMain.setInAmount(new BigDecimal(0));
		dayReportMain.setOutAmount(new BigDecimal(0));
		dayReportMain.setWindupType(windupType);
		dayReportMain.setStatus(ClearConstant.AccountsStatus.SUCCESS);
		dayReportMain.setOffice(office);
		// 将昨天结算过的客户信息再次结算
		this.addBeforeAccount(dayReportGuest, centerAccountsMain, DoorOrderConstant.AccountsType.ACCOUNTS_DOOR,
				windupType, dayReportMain, office);
		// 将今天新增额客户信息进行结算（该客户从未日结过）
		this.addTodayAccount(dayReportGuest, centerAccountsMain, DoorOrderConstant.AccountsType.ACCOUNTS_DOOR,
				windupType, dayReportMain, office);
		// 账务日结主表保存
		dayReportMainInsert(dayReportMain, office);
		return "";
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 将昨天结算过的客户信息再次日结
	 * 
	 * @param dayReportGuest
	 * @param centerAccountsMain
	 * @param accountsType
	 *            账务类型
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 */
	@Transactional(readOnly = false)
	public void addBeforeAccount(DoorDayReportGuest dayReportGuest, DoorCenterAccountsMain centerAccountsMain,
			String accountsType, String windupType, DoorDayReportMain dayReportMainByInsert, Office office) {
		// 获取昨日日结主表最后一条记录主键
		DoorDayReportMain dayReportMain = new DoorDayReportMain();

		// 设置结账日期为今天之前的最近一次
		Date maxDayReport = DoorCommonUtils.getDayReportMaxDate(office);
		if (maxDayReport != null) {
			// 设置开始时间
			dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxDayReport),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置结束时间
			dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(maxDayReport),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置发生机构
			dayReportMain.setOffice(office);

			// 获取昨日中心日结表数据
			List<DoorDayReportMain> dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
			// 获取昨日最后一次客户日结列表（所有类型）
			List<DoorDayReportGuest> dayGuestAllTypeList = dayReportList.get(0).getDayReportGuestList();
			List<DoorDayReportGuest> dayReportGuestList = Lists.newArrayList();
			for (DoorDayReportGuest dayReport : dayGuestAllTypeList) {
				if (dayReport.getAccountsType().equals(accountsType)) {
					dayReportGuestList.add(dayReport);
				}
			}
			// (（张家港修改部分，只结算自己）)设置商户机构
			centerAccountsMain.setMerchantOfficeId(office.getId());
			// 从账务表中获取今天的流水
			List<DoorCenterAccountsMain> accountMainList = centerAccountsMainDao
					.getAccountByBusinessTypeForReport(centerAccountsMain);
			// 昨日已结算过 今日还有流水的客户列表
			List<String> clientList = Lists.newArrayList();
			for (DoorDayReportGuest dayGuest : dayReportGuestList) {
				// 用作判断昨天结算过的客户今天有没有流水
				int index = 0;
				for (DoorCenterAccountsMain cenMain : accountMainList) {
					// 昨天结算的客户在今天有流水
					if (dayGuest.getClientId().equals(cenMain.getClientId())) {
						if (!clientList.contains(cenMain.getClientId())) {
							clientList.add(cenMain.getClientId());
							index++;
						}

					}
				}
				// 昨天结算的客户在今天没有流水
				if (index == 0) {
					dayGuest.setId(IdGen.uuid());
					dayGuest.setReportDate(dayReportMainByInsert.getReportDate());
					dayGuest.setInAmount(new BigDecimal(0));
					dayGuest.setOutAmount(new BigDecimal(0));
					dayGuest.setInCount(new BigDecimal(0));
					dayGuest.setOutCount(new BigDecimal(0));
					dayGuest.setBeforeAmount(dayGuest.getTotalAmount());
					dayGuest.setWindupType(windupType);
					dayGuest.setReportMainId(dayReportMainByInsert.getReportId());
					int dayMainInsertResult = dayReportGuestDao.insert(dayGuest);
					if (dayMainInsertResult == 0) {
						String strMessageContent = "账务日结表：" + dayReportGuest.getId() + "更新失败！";
						throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结表" });
					}
				}

			}

			for (int i = 0; i < clientList.size(); i++) {
				String clientId = clientList.get(i);
				// 收入笔数
				int inCount = 0;
				// 支出笔数
				int outCount = 0;
				// 收入金额
				BigDecimal inAmount = new BigDecimal(0);
				// 支出金额
				BigDecimal outAmount = new BigDecimal(0);
				// 昨日余额
				BigDecimal beforeAmount = new BigDecimal(0);

				for (DoorDayReportGuest daygGuest : dayReportGuestList) {
					if (daygGuest.getClientId().equals(clientId)) {
						beforeAmount = daygGuest.getTotalAmount();
					}
				}

				DoorDayReportGuest insertDayGuest = new DoorDayReportGuest();
				// 将昨天的期末余额设置为今天的期初余额
				insertDayGuest.setBeforeAmount(beforeAmount);

				DoorCenterAccountsMain center = new DoorCenterAccountsMain();
				center.setBusinessTypes(centerAccountsMain.getBusinessTypes());
				center.setSearchDateStart(centerAccountsMain.getSearchDateStart());
				center.setSearchDateEnd(centerAccountsMain.getSearchDateEnd());
				// 中心总账设置客户ID
				center.setClientId(clientId);
				// 从账务表中获取今天的流水
				List<DoorCenterAccountsMain> accountList = centerAccountsMainDao
						.getAccountByBusinessTypeForReport(center);

				for (DoorCenterAccountsMain cenMain : accountList) {
					// 为入库流水
					if (cenMain.getInAmount() != null) {
						// 计算收入笔数
						inCount++;
						// 计算收入金额
						inAmount = inAmount.add(cenMain.getInAmount());
					}
					if (cenMain.getOutAmount() != null) {
						// 计算支出笔数
						outCount++;
						// 计算支出金额
						outAmount = outAmount.add(cenMain.getOutAmount());
					}
				}

				// 获取今日余额
				BigDecimal todayTotalAmount = this.calTodayTotalAmount(insertDayGuest.getBeforeAmount(), inAmount,
						outAmount);
				// 设置今日余额
				insertDayGuest.setTotalAmount(todayTotalAmount);
				// 设置今日收入笔数
				insertDayGuest.setInCount(new BigDecimal(inCount));
				// 设置今日支出笔数
				insertDayGuest.setOutCount(new BigDecimal(outCount));
				// 设置今日收入金额
				insertDayGuest.setInAmount(inAmount);
				// 设置今日支出金额
				insertDayGuest.setOutAmount(outAmount);
				// 设置客户账务日结表主键
				insertDayGuest.setId(IdGen.uuid());
				// 设置客户名称
				insertDayGuest.setClientName(StoreCommonUtils.getOfficeById(clientId).getName());
				insertDayGuest.setClientId(clientId);
				// 设置账务类型
				insertDayGuest.setAccountsType(accountsType);
				// 设置结账日期
				insertDayGuest.setReportDate(dayReportMainByInsert.getReportDate());
				// 设置有效标识
				insertDayGuest.setDelFlag(ClearConstant.deleteFlag.Valid);
				// 设置结账方式
				insertDayGuest.setWindupType(windupType);
				// 设置账务日结表主键
				insertDayGuest.setReportMainId(dayReportMainByInsert.getReportId());
				int dayMainInsertResult = dayReportGuestDao.insert(insertDayGuest);
				if (dayMainInsertResult == 0) {
					String strMessageContent = "账务日结表：" + dayReportGuest.getId() + "更新失败！";
					throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结表" });
				}

				// 日结主表数据做成
				dayReportMainByInsert.setInCount(dayReportMainByInsert.getInCount().add(insertDayGuest.getInCount()));
				dayReportMainByInsert
						.setOutCount(dayReportMainByInsert.getOutCount().add(insertDayGuest.getOutCount()));
				dayReportMainByInsert
						.setInAmount(dayReportMainByInsert.getInAmount().add(insertDayGuest.getInAmount()));
				dayReportMainByInsert
						.setOutAmount(dayReportMainByInsert.getOutAmount().add(insertDayGuest.getOutAmount()));
				dayReportMainByInsert
						.setTotalAmount(dayReportMainByInsert.getTotalAmount().add(inAmount.subtract(outAmount)));
				// 设置reportId
				dayReportMainByInsert.setReportId(dayReportMainByInsert.getReportId());
				// 设置结账类型
				dayReportMainByInsert.setWindupType(windupType);
				// 设置结账有效状态
				dayReportMainByInsert.setStatus(ClearConstant.AccountsStatus.SUCCESS);
				// 设置结账日期
				dayReportMainByInsert.setReportDate(dayReportMainByInsert.getReportDate());
				// 设置账务发生机构
				dayReportMainByInsert.setOffice(office);

			}

		}
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 将今天新增额客户信息进行结算（该客户从未日结过）
	 * 
	 * @param dayReportGuest
	 * @param centerAccountsMain
	 * @param accountsType
	 */
	@Transactional(readOnly = false)
	public void addTodayAccount(DoorDayReportGuest dayReportGuest, DoorCenterAccountsMain centerAccountsMain,
			String accountsType, String windupType, DoorDayReportMain dayReportMainByInsert, Office office) {
		// 获取昨日日结主表最后一条记录主键
		DoorDayReportMain dayReportMain = new DoorDayReportMain();

		List<DoorDayReportGuest> dayReportGuestList = Lists.newArrayList();
		// 设置结账日期为昨日
		Date maxDayReport = DoorCommonUtils.getDayReportMaxDate(office);
		if (maxDayReport != null) {
			// 设置开始时间
			dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxDayReport),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置结束时间
			dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(maxDayReport),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置发生机构
			dayReportMain.setOffice(office);

			// 获取昨日中心日结表数据
			List<DoorDayReportMain> dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
			// 获取昨日最后一次客户日结列表（所有类型）
			List<DoorDayReportGuest> dayGuestAllTypeList = Lists.newArrayList();
			if (!Collections3.isEmpty(dayReportList)) {
				dayGuestAllTypeList = dayReportList.get(0).getDayReportGuestList();
			}

			for (DoorDayReportGuest dayReport : dayGuestAllTypeList) {
				if (dayReport.getAccountsType().equals(accountsType)) {
					dayReportGuestList.add(dayReport);
				}
			}
		}

		// (（张家港修改部分，只结算自己）)设置商户机构
		centerAccountsMain.setMerchantOfficeId(office.getId());

		// 从账务表中获取今天的流水
		List<DoorCenterAccountsMain> accountMainList = centerAccountsMainDao
				.getAccountByBusinessTypeForReport(centerAccountsMain);
		// 新增需要结算客户集合
		List<String> clientIdList = Lists.newArrayList();
		for (DoorCenterAccountsMain cenMain : accountMainList) {
			int index = 0;
			for (DoorDayReportGuest dayGuest : dayReportGuestList) {
				if (cenMain.getClientId().equals(dayGuest.getClientId())) {
					index++;
					continue;
				}
			}
			if (index == 0) {
				if (!clientIdList.contains(cenMain.getClientId())) {
					// 获取到所有新增需要结算客户集合
					clientIdList.add(cenMain.getClientId());
				}
			}
		}

		for (int i = 0; i < clientIdList.size(); i++) {
			// 收入笔数
			int inCount = 0;
			// 支出笔数
			int outCount = 0;
			// 收入金额
			BigDecimal inAmount = new BigDecimal(0);
			// 支出金额
			BigDecimal outAmount = new BigDecimal(0);
			String clientId = clientIdList.get(i);
			centerAccountsMain.setClientId(clientId);
			// 从账务表中获取今天的流水
			List<DoorCenterAccountsMain> accountMainListByClientId = centerAccountsMainDao
					.getAccountByBusinessTypeForReport(centerAccountsMain);
			for (DoorCenterAccountsMain centerAccount : accountMainListByClientId) {
				// 为入库流水
				if (centerAccount.getInAmount() != null) {
					// 计算收入笔数
					inCount++;
					// 计算收入金额
					inAmount = inAmount.add(centerAccount.getInAmount());
				}
				if (centerAccount.getOutAmount() != null) {
					// 计算支出笔数
					outCount++;
					// 计算支出金额
					outAmount = outAmount.add(centerAccount.getOutAmount());
				}
			}
			DoorDayReportGuest insertDayGuest = new DoorDayReportGuest();
			// 计算今日余额
			BigDecimal totalAmount = this.calTodayTotalAmount(new BigDecimal(0), inAmount, outAmount);
			// 设置主键
			insertDayGuest.setId(IdGen.uuid());
			// 设置今日余额
			insertDayGuest.setTotalAmount(totalAmount);
			// 设置今日收入笔数
			insertDayGuest.setInCount(new BigDecimal(inCount));
			// 设置今日支出笔数
			insertDayGuest.setOutCount(new BigDecimal(outCount));
			// 设置今日收入金额
			insertDayGuest.setInAmount(inAmount);
			// 设置今日支出金额
			insertDayGuest.setOutAmount(outAmount);
			// 设置昨日余额
			insertDayGuest.setBeforeAmount(new BigDecimal(0));
			// 设置客户账务日结表主键
			insertDayGuest.setId(IdGen.uuid());
			// 设置客户名称
			insertDayGuest.setClientName(StoreCommonUtils.getOfficeById(clientId).getName());
			insertDayGuest.setClientId(clientId);
			// 设置账务类型
			insertDayGuest.setAccountsType(accountsType);
			// 设置结账日期
			insertDayGuest.setReportDate(dayReportMainByInsert.getReportDate());
			// 设置有效标识
			insertDayGuest.setDelFlag(ClearConstant.deleteFlag.Valid);
			// 设置结账方式
			insertDayGuest.setWindupType(windupType);
			// 设置账务日结表主键
			insertDayGuest.setReportMainId(dayReportMainByInsert.getReportId());
			int dayMainInsertResult = dayReportGuestDao.insert(insertDayGuest);
			if (dayMainInsertResult == 0) {
				String strMessageContent = "账务日结表：" + dayReportGuest.getId() + "更新失败！";
				throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结表" });
			}

			// 日结主表数据做成
			dayReportMainByInsert.setInCount(dayReportMainByInsert.getInCount().add(insertDayGuest.getInCount()));
			dayReportMainByInsert.setOutCount(dayReportMainByInsert.getOutCount().add(insertDayGuest.getOutCount()));
			dayReportMainByInsert.setInAmount(dayReportMainByInsert.getInAmount().add(insertDayGuest.getInAmount()));
			dayReportMainByInsert.setOutAmount(dayReportMainByInsert.getOutAmount().add(insertDayGuest.getOutAmount()));
			dayReportMainByInsert
					.setTotalAmount(dayReportMainByInsert.getTotalAmount().add(insertDayGuest.getTotalAmount()));
			// 设置reportId
			dayReportMainByInsert.setReportId(dayReportMainByInsert.getReportId());
			// 设置结账类型
			dayReportMainByInsert.setWindupType(windupType);
			// 设置结账有效状态
			dayReportMainByInsert.setStatus(ClearConstant.AccountsStatus.SUCCESS);
			// 设置结账日期
			dayReportMainByInsert.setReportDate(dayReportMainByInsert.getReportDate());
			// 设置账务发生机构
			dayReportMainByInsert.setOffice(office);
		}

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 计算今日余额
	 * 
	 * @param beforeAmount
	 *            昨日余额
	 * @param inAmount
	 *            收入余额
	 * @param outAmount
	 *            支出余额
	 * @return
	 */
	private BigDecimal calTodayTotalAmount(BigDecimal beforeAmount, BigDecimal inAmount, BigDecimal outAmount) {
		// 今日余额
		BigDecimal todayTotalAmount = new BigDecimal(0);
		// 增加今日收入余额
		todayTotalAmount = beforeAmount.add(inAmount);
		// 减少今日支出余额
		todayTotalAmount = todayTotalAmount.subtract(outAmount);
		return todayTotalAmount;
	}

	/**
	 * @author qipeihong
	 * @version 2017年9月8日 账务日结主表保存
	 * 
	 * 
	 * @param dayReportMain
	 */

	@Transactional(readOnly = false)
	public void dayReportMainInsert(DoorDayReportMain dayReportMain, Office office) {
		int dayMainInsertResult = dayReportMainDao.insert(dayReportMain);
		if (dayMainInsertResult == 0) {
			String strMessageContent = "账务日结主表：" + dayReportMain.getReportId() + "更新失败！";
			throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结主表" });
		}
	}
}