package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.CenterAccountsMainDao;
import com.coffer.businesses.modules.clear.v03.dao.DayReportGuestDao;
import com.coffer.businesses.modules.clear.v03.dao.DayReportMainDao;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.DayReportGuest;
import com.coffer.businesses.modules.clear.v03.entity.DayReportMain;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 客户账务日结Service
 * 
 * @author QPH
 * @version 2017-09-04
 */
@Service
@Transactional(readOnly = true)
public class DayReportGuestService extends CrudService<DayReportGuestDao, DayReportGuest> {

	@Autowired
	private CenterAccountsMainDao centerAccountsMainDao;

	@Autowired
	private DayReportGuestDao dayReportGuestDao;

	@Autowired
	private DayReportMainDao dayReportMainDao;

	public DayReportGuest get(String id) {
		return super.get(id);
	}

	public List<DayReportGuest> findList(DayReportGuest dayReportGuest) {
		return super.findList(dayReportGuest);
	}

	public Page<DayReportGuest> findPage(Page<DayReportGuest> page, DayReportGuest dayReportGuest) {
		return super.findPage(page, dayReportGuest);
	}

	@Transactional(readOnly = false)
	public void save(DayReportGuest dayReportGuest) {
		super.save(dayReportGuest);
	}

	@Transactional(readOnly = false)
	public void delete(DayReportGuest dayReportGuest) {
		super.delete(dayReportGuest);
	}

	/**
	 * 
	 * @author qipeihong
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @version 2017年9月6日 客户账务日结
	 *
	 */
	@Transactional(readOnly = false)
	public synchronized void dayAccountReportByGuest(String windupType, DayReportMain dayReportMain, Office office) {

		// 清点业务日结
		dayClearAccountReport(new CenterAccountsMain(), windupType, dayReportMain, office);
		// 复点业务日结
		dayComplexAccountReport(new CenterAccountsMain(), windupType, dayReportMain, office);
		// 备付金业务日结
		dayProvisionsAccountReport(new CenterAccountsMain(), windupType, dayReportMain, office);
	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 中心清点账务类型日结
	 * 
	 * @param centerAccountsMain
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @return
	 */
	@Transactional(readOnly = false)
	private String dayClearAccountReport(CenterAccountsMain centerAccountsMain, String windupType,
			DayReportMain dayReportMain, Office office) {
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 获取昨日日结
		DayReportGuest dayReportGuest = new DayReportGuest();
		// 设置业务类型
		List<String> dayClearBusinessTypeList = Lists.newArrayList();
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.BANK_GET);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.BANK_PAY);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.AGENCY_PAY);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 将昨天结算过的客户信息再次结算
		this.addBeforeAccount(dayReportGuest, centerAccountsMain, ClearConstant.AccountsType.ACCOUNTS_CLEAR, windupType,
				dayReportMain, office);
		// 将今天新增额客户信息进行结算（该客户从未日结过）
		this.addTodayAccount(dayReportGuest, centerAccountsMain, ClearConstant.AccountsType.ACCOUNTS_CLEAR, windupType,
				dayReportMain, office);
		return "";
	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 中心备付金账务类型日结
	 * 
	 * @param centerAccountsMain
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @return
	 */
	@Transactional(readOnly = false)
	private String dayComplexAccountReport(CenterAccountsMain centerAccountsMain, String windupType,
			DayReportMain dayReportMain, Office office) {
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 获取昨日日结
		DayReportGuest dayReportGuest = new DayReportGuest();
		// 设置业务类型
		List<String> dayClearBusinessTypeList = Lists.newArrayList();
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 将昨天结算过的客户信息再次结算
		this.addBeforeAccount(dayReportGuest, centerAccountsMain, ClearConstant.AccountsType.ACCOUNTS_COMPLEX,
				windupType, dayReportMain, office);
		// 将今天新增额客户信息进行结算（该客户从未日结过）
		this.addTodayAccount(dayReportGuest, centerAccountsMain, ClearConstant.AccountsType.ACCOUNTS_COMPLEX,
				windupType, dayReportMain, office);
		return "";

	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 中心备付金账务类型日结
	 * 
	 * @param centerAccountsMain
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @return
	 */
	@Transactional(readOnly = false)
	private String dayProvisionsAccountReport(CenterAccountsMain centerAccountsMain, String windupType,
			DayReportMain dayReportMain, Office office) {
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 获取昨日日结
		DayReportGuest dayReportGuest = new DayReportGuest();
		// 设置业务类型
		List<String> dayClearBusinessTypeList = Lists.newArrayList();
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PROVISIONS_IN);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PROVISIONS_OUT);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.ERROR_HANDLING);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 将昨天结算过的客户信息再次结算
		this.addBeforeAccount(dayReportGuest, centerAccountsMain, ClearConstant.AccountsType.ACCOUNTS_PROVISIONS,
				windupType, dayReportMain, office);
		// 将今天新增额客户信息进行结算（该客户从未日结过）
		this.addTodayAccount(dayReportGuest, centerAccountsMain, ClearConstant.AccountsType.ACCOUNTS_PROVISIONS,
				windupType, dayReportMain, office);
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
	private void addBeforeAccount(DayReportGuest dayReportGuest, CenterAccountsMain centerAccountsMain,
			String accountsType, String windupType, DayReportMain dayReportMainByInsert, Office office) {
		// 获取昨日日结主表最后一条记录主键
		DayReportMain dayReportMain = new DayReportMain();

		// 设置结账日期为今天之前的最近一次
		Date maxDayReport = ClearCommonUtils.getDayReportMaxDate(office);
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
			List<DayReportMain> dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
			// 获取昨日最后一次客户日结列表（所有类型）
			List<DayReportGuest> dayGuestAllTypeList = dayReportList.get(0).getDayReportGuestList();
			List<DayReportGuest> dayReportGuestList = Lists.newArrayList();
			for (DayReportGuest dayReport : dayGuestAllTypeList) {
				if (dayReport.getAccountsType().equals(accountsType)) {
					dayReportGuestList.add(dayReport);
				}
			}
			// 设置发生机构
			centerAccountsMain.setRofficeId(office.getId());
			// 从账务表中获取今天的流水
			List<CenterAccountsMain> accountMainList = centerAccountsMainDao
					.getAccountByBusinessTypeForReport(centerAccountsMain);
			// 昨日已结算过 今日还有流水的客户列表
			List<String> clientList = Lists.newArrayList();
			for (DayReportGuest dayGuest : dayReportGuestList) {
				// 用作判断昨天结算过的客户今天有没有流水
				int index = 0;
				for (CenterAccountsMain cenMain : accountMainList) {
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
					dayGuest.setReportDate(new Date());
					dayGuest.setInAmount(new BigDecimal(0));
					dayGuest.setOutAmount(new BigDecimal(0));
					dayGuest.setInCount(new BigDecimal(0));
					dayGuest.setInCount(new BigDecimal(0));
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

				for (DayReportGuest daygGuest : dayReportGuestList) {
					if (daygGuest.getClientId().equals(clientId)) {
						beforeAmount = daygGuest.getTotalAmount();
					}
				}

				DayReportGuest insertDayGuest = new DayReportGuest();
				// 将昨天的期末余额设置为今天的期初余额
				insertDayGuest.setBeforeAmount(beforeAmount);

				CenterAccountsMain center = new CenterAccountsMain();
				center.setBusinessTypes(centerAccountsMain.getBusinessTypes());
				center.setSearchDateStart(centerAccountsMain.getSearchDateStart());
				center.setSearchDateEnd(centerAccountsMain.getSearchDateEnd());
				// 中心总账设置客户ID
				center.setClientId(clientId);
				// 设置发生机构
				center.setRofficeId(office.getId());
				// 从账务表中获取今天的流水
				List<CenterAccountsMain> accountList = centerAccountsMainDao.getAccountByBusinessTypeForReport(center);

				for (CenterAccountsMain cenMain : accountList) {
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
				insertDayGuest.setReportDate(new Date());
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
	private void addTodayAccount(DayReportGuest dayReportGuest, CenterAccountsMain centerAccountsMain,
			String accountsType, String windupType, DayReportMain dayReportMainByInsert, Office office) {
		// 获取昨日日结主表最后一条记录主键
		DayReportMain dayReportMain = new DayReportMain();

		List<DayReportGuest> dayReportGuestList = Lists.newArrayList();
		// 设置结账日期为昨日
		Date maxDayReport = ClearCommonUtils.getDayReportMaxDate(office);
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
			List<DayReportMain> dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
			// 获取昨日最后一次客户日结列表（所有类型）
			List<DayReportGuest> dayGuestAllTypeList = Lists.newArrayList();
			if (!Collections3.isEmpty(dayReportList)) {
				dayGuestAllTypeList = dayReportList.get(0).getDayReportGuestList();
			}

			for (DayReportGuest dayReport : dayGuestAllTypeList) {
				if (dayReport.getAccountsType().equals(accountsType)) {
					dayReportGuestList.add(dayReport);
				}
			}
		}

		// 设置账务发生机构
		centerAccountsMain.setRofficeId(office.getId());

		// 从账务表中获取今天的流水
		List<CenterAccountsMain> accountMainList = centerAccountsMainDao
				.getAccountByBusinessTypeForReport(centerAccountsMain);
		// 新增需要结算客户集合
		List<String> clientIdList = Lists.newArrayList();
		for (CenterAccountsMain cenMain : accountMainList) {
			int index = 0;
			for (DayReportGuest dayGuest : dayReportGuestList) {
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
			List<CenterAccountsMain> accountMainListByClientId = centerAccountsMainDao
					.getAccountByBusinessType(centerAccountsMain);
			for (CenterAccountsMain centerAccount : accountMainListByClientId) {
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
			DayReportGuest insertDayGuest = new DayReportGuest();
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
			insertDayGuest.setReportDate(new Date());
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
	 * 
	 * @author sg
	 * @version 2017年12月20日 查询商业银行备付金昨日余额
	 * 
	 * @param beforeAmount
	 *            昨日余额
	 * @param inAmount
	 *            收入余额
	 * @param outAmount
	 *            支出余额
	 * @return
	 */
	public List<DayReportGuest> findAccountByAccountsType() {
		DayReportGuest dayReportGuest = new DayReportGuest();
		User user = UserUtils.getUser();
		dayReportGuest.setAccountsType(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		dayReportGuest.setClientId(user.getOffice().getId());
		dayReportGuest.setStatus(ClearConstant.AccountsStatus.SUCCESS);
		return dayReportGuestDao.findAccountByAccountsType(dayReportGuest);
	}
}