package com.coffer.businesses.modules.clear.v03.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.CenterAccountsMainDao;
import com.coffer.businesses.modules.clear.v03.dao.DayReportCenterDao;
import com.coffer.businesses.modules.clear.v03.dao.DayReportMainDao;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.DayReportCenter;
import com.coffer.businesses.modules.clear.v03.entity.DayReportMain;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 中心账务日结Service
 * 
 * @author QPH
 * @version 2017-09-05
 */
@Service
@Transactional(readOnly = true)
public class DayReportCenterService extends CrudService<DayReportCenterDao, DayReportCenter> {
	
	@Autowired
	private CenterAccountsMainDao centerAccountsMainDao;

	@Autowired
	private DayReportCenterDao dayReportCenterDao;

	@Autowired
	private DayReportMainDao dayReportMainDao;

	public DayReportCenter get(String id) {
		return super.get(id);
	}
	
	public List<DayReportCenter> findList(DayReportCenter dayReportCenter) {
		return super.findList(dayReportCenter);
	}
	
	public Page<DayReportCenter> findPage(Page<DayReportCenter> page, DayReportCenter dayReportCenter) {
		return super.findPage(page, dayReportCenter);
	}

	@Transactional(readOnly = false)
	public void save(DayReportCenter dayReportCenter) {
		super.save(dayReportCenter);
	}
	
	@Transactional(readOnly = false)
	public void delete(DayReportCenter dayReportCenter) {
		super.delete(dayReportCenter);
	}
	
	/**
	 * 
	 * @author qipeihong
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @version 2017年9月5日 中心账务日结
	 *
	 */
	@Transactional(readOnly = false)
	public synchronized void dayAccountReportByCenter(String windupType, DayReportMain dayReportMain, Office office) {

		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 清点业务日结
		dayClearAccountReport(centerAccountsMain, windupType, dayReportMain, office);
		// 复点业务日结
		dayComplexAccountReport(centerAccountsMain, windupType, dayReportMain, office);
		// 备付金业务日结
		dayProvisionsAccountReport(centerAccountsMain, windupType, dayReportMain, office);
		// 账务日结主表保存
		dayReportMainInsert(dayReportMain, office);
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
			DayReportMain dayReportMainByInsert, Office office) {
		// 收入笔数
		int inCount = 0;
		// 支出笔数
		int outCount = 0;
		// 收入金额
		BigDecimal inAmount = new BigDecimal(0);
		// 支出金额
		BigDecimal outAmount = new BigDecimal(0);
		// 设置业务类型
		List<String> dayClearBusinessTypeList = Lists.newArrayList();
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.BANK_PAY);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.BANK_GET);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.AGENCY_PAY);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(office.getId());
		// 从账务表中获取今天的流水
		List<CenterAccountsMain> accountMainList = centerAccountsMainDao
				.getAccountByBusinessTypeForReport(centerAccountsMain);
		// 若今天有账务流水
		for (CenterAccountsMain accountsMain : accountMainList) {
			// 为入库流水
			if (accountsMain.getInAmount() != null) {
				// 计算收入笔数
				inCount++;
				// 计算收入金额
				inAmount = inAmount.add(accountsMain.getInAmount());
			}
			if (accountsMain.getOutAmount() != null) {
				// 计算支出笔数
				outCount++;
				// 计算支出金额
				outAmount = outAmount.add(accountsMain.getOutAmount());
			}
		}
		// 需要存入数据库的日结信息
		DayReportCenter dayReportCenter = new DayReportCenter();
		// 昨日余额
		BigDecimal beforeAmount = new BigDecimal(0);
		// 获取昨日日结主表最后一条记录主键
		DayReportMain dayReportMain = new DayReportMain();
		// 设置结账日期为昨日
		Date maxdate = getDayReportMaxDate(office);
		List<DayReportMain> dayReportList = Lists.newArrayList();
		if (maxdate != null) {
			// 设置开始时间
			dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxdate),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置结束时间
			dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(maxdate),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置账务发生机构
			dayReportMain.setOffice(office);
			// 获取昨日中心日结表数据
			dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
			if (!Collections3.isEmpty(dayReportList)) {
				// 获取到离今天最近的一条中心账务日结金额作为期初余额
				for (DayReportCenter dayCenter : dayReportList.get(0).getDayReportCenterList()) {
					if (ClearConstant.AccountsType.ACCOUNTS_CLEAR.equals(dayCenter.getAccountsType())) {
						beforeAmount = dayCenter.getTotalAmount();
					}
				}
			}
		}
		// 获取今日余额
		BigDecimal todayTotalAmount = this.calTodayTotalAmount(beforeAmount, inAmount, outAmount);
		// 设置今日余额
		dayReportCenter.setTotalAmount(todayTotalAmount);
		// 设置今日收入笔数
		dayReportCenter.setInCount(new BigDecimal(inCount));
		// 设置今日支出笔数
		dayReportCenter.setOutCount(new BigDecimal(outCount));
		// 设置今日收入金额
		dayReportCenter.setInAmount(inAmount);
		// 设置今日支出金额
		dayReportCenter.setOutAmount(outAmount);
		// 设置昨日余额
		dayReportCenter.setBeforeAmount(beforeAmount);
		// 设置中心账务日结表主键
		dayReportCenter.setId(IdGen.uuid());
		// 设置账务类型
		dayReportCenter.setAccountsType(ClearConstant.AccountsType.ACCOUNTS_CLEAR);
		// 设置结账日期
		dayReportCenter.setReportDate(new Date());
		// 设置有效标识
		dayReportCenter.setDelFlag(ClearConstant.deleteFlag.Valid);
		// 设置结账方式
		dayReportCenter.setWindupType(windupType);
		// 设置关联主表ID
		dayReportCenter.setReportMainId(dayReportMainByInsert.getReportId());
		int dayCenterInsertResult = dayReportCenterDao.insert(dayReportCenter);
		if (dayCenterInsertResult == 0) {
			String strMessageContent = "账务日结表：" + dayReportCenter.getId() + "更新失败！";
			throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结表" });
		}

		// 设置期初余额
		if (Collections3.isEmpty(dayReportList)) {
			dayReportMainByInsert.setBeforeAmount(new BigDecimal(0));
		} else {
			dayReportMainByInsert.setBeforeAmount(dayReportList.get(0).getTotalAmount());
		}
		dayReportMainByInsert.setInCount(dayReportCenter.getInCount());
		dayReportMainByInsert.setOutCount(dayReportCenter.getOutCount());
		dayReportMainByInsert.setInAmount(dayReportCenter.getInAmount());
		dayReportMainByInsert.setOutAmount(dayReportCenter.getOutAmount());
		dayReportMainByInsert.setTotalAmount(dayReportCenter.getTotalAmount());
		// 设置结账类型
		dayReportMainByInsert.setWindupType(windupType);
		// 设置结账有效状态
		dayReportMainByInsert.setStatus(ClearConstant.AccountsStatus.SUCCESS);
		// 设置结账日期
		dayReportMainByInsert.setReportDate(new Date());
		// 设置账务发生机构
		dayReportMainByInsert.setOffice(office);
		return ClearConstant.SUCCESS;
	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 中心复点账务类型日结
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
			DayReportMain dayReportMainByInsert, Office office) {
		// 收入笔数
		int inCount = 0;
		// 支出笔数
		int outCount = 0;
		// 收入金额
		BigDecimal inAmount = new BigDecimal(0);
		// 支出金额
		BigDecimal outAmount = new BigDecimal(0);
		// 设置业务类型
		List<String> dayClearBusinessTypeList = Lists.newArrayList();
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(office.getId());
		// 从账务表中获取今天的流水
		List<CenterAccountsMain> accountMainList = centerAccountsMainDao
				.getAccountByBusinessTypeForReport(centerAccountsMain);
		// 若今天有账务流水
		for (CenterAccountsMain accountsMain : accountMainList) {
			// 为入库流水
			if (accountsMain.getInAmount() != null) {
				// 计算收入笔数
				inCount++;
				// 计算收入金额
				inAmount = inAmount.add(accountsMain.getInAmount());
			}
			if (accountsMain.getOutAmount() != null) {
				// 计算支出笔数
				outCount++;
				// 计算支出金额
				outAmount = outAmount.add(accountsMain.getOutAmount());
			}
		}
		// 需要存入数据库的日结信息
		DayReportCenter dayReportCenter = new DayReportCenter();
		// 昨日余额
		BigDecimal beforeAmount = new BigDecimal(0);
		// 获取昨日日结主表最后一条记录主键
		DayReportMain dayReportMain = new DayReportMain();
		// 设置结账日期为昨日
		Date maxdate = getDayReportMaxDate(office);
		if (maxdate != null) {
			// 设置开始时间
			dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxdate),
				ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置结束时间
			dayReportMain.setSearchDateEnd(
					DateUtils.formatDate(DateUtils.getDateEnd(maxdate),
							ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置账务发生机构
			dayReportMain.setOffice(office);

			// 获取昨日中心日结表数据
			List<DayReportMain> dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);

			if (!Collections3.isEmpty(dayReportList)) {
				// 获取到离今天最近的一条中心账务日结金额作为期初余额
				for (DayReportCenter dayCenter : dayReportList.get(0).getDayReportCenterList()) {
					if (ClearConstant.AccountsType.ACCOUNTS_COMPLEX.equals(dayCenter.getAccountsType())) {
						beforeAmount = dayCenter.getTotalAmount();
					}
				}
			}
		}
		// 获取今日余额
		BigDecimal todayTotalAmount = this.calTodayTotalAmount(beforeAmount, inAmount, outAmount);
		// 设置今日余额
		dayReportCenter.setTotalAmount(todayTotalAmount);
		// 设置今日收入笔数
		dayReportCenter.setInCount(new BigDecimal(inCount));
		// 设置今日支出笔数
		dayReportCenter.setOutCount(new BigDecimal(outCount));
		// 设置今日收入金额
		dayReportCenter.setInAmount(inAmount);
		// 设置今日支出金额
		dayReportCenter.setOutAmount(outAmount);
		// 设置昨日余额
		dayReportCenter.setBeforeAmount(beforeAmount);
		// 设置中心账务日结表主键
		dayReportCenter.setId(IdGen.uuid());
		// 设置账务类型
		dayReportCenter.setAccountsType(ClearConstant.AccountsType.ACCOUNTS_COMPLEX);
		// 设置结账日期
		dayReportCenter.setReportDate(new Date());
		// 设置有效标识
		dayReportCenter.setDelFlag(ClearConstant.deleteFlag.Valid);
		// 设置结账方式
		dayReportCenter.setWindupType(windupType);
		dayReportCenter.setReportMainId(dayReportMainByInsert.getReportId());
		int dayCenterInsertResult = dayReportCenterDao.insert(dayReportCenter);
		if (dayCenterInsertResult == 0) {
			String strMessageContent = "账务日结表：" + dayReportCenter.getId() + "更新失败！";
			throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结表" });
		}
		// 计算账务日结主表所有数据
		// 设置主表收入笔数
		BigDecimal InCountByMain = dayReportCenter.getInCount().add(dayReportMainByInsert.getInCount());
		// 设置主表收入金额
		BigDecimal InAmountByMain = dayReportCenter.getInAmount().add(dayReportMainByInsert.getInAmount());
		// 设置主表支出笔数
		BigDecimal OutCountByMain = dayReportCenter.getOutCount().add(dayReportMainByInsert.getOutCount());
		// 设置主表支出金额
		BigDecimal OutAmountByMain = dayReportCenter.getOutAmount().add(dayReportMainByInsert.getOutAmount());
		// 设置总金额
		BigDecimal totalAmountByMain = dayReportCenter.getTotalAmount().add(dayReportMainByInsert.getTotalAmount());
		dayReportMainByInsert.setInCount(InCountByMain);
		dayReportMainByInsert.setOutCount(OutCountByMain);
		dayReportMainByInsert.setInAmount(InAmountByMain);
		dayReportMainByInsert.setOutAmount(OutAmountByMain);
		dayReportMainByInsert.setTotalAmount(totalAmountByMain);

		return ClearConstant.SUCCESS;
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
			DayReportMain dayReportMainByInsert, Office office) {
		// 收入笔数
		int inCount = 0;
		// 支出笔数
		int outCount = 0;
		// 收入金额
		BigDecimal inAmount = new BigDecimal(0);
		// 支出金额
		BigDecimal outAmount = new BigDecimal(0);
		// 设置业务类型
		List<String> dayClearBusinessTypeList = Lists.newArrayList();
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PROVISIONS_IN);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.PROVISIONS_OUT);
		dayClearBusinessTypeList.add(ClearConstant.BusinessType.ERROR_HANDLING);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(office.getId());
		// 从账务表中获取今天的流水
		List<CenterAccountsMain> accountMainList = centerAccountsMainDao
				.getAccountByBusinessTypeForReport(centerAccountsMain);
		// 若今天有账务流水
		for (CenterAccountsMain accountsMain : accountMainList) {
			// 为入库流水
			if (accountsMain.getInAmount() != null) {
				// 计算收入笔数
				inCount++;
				// 计算收入金额
				inAmount = inAmount.add(accountsMain.getInAmount());
			}
			if (accountsMain.getOutAmount() != null) {
				// 计算支出笔数
				outCount++;
				// 计算支出金额
				outAmount = outAmount.add(accountsMain.getOutAmount());
			}
		}

		// 需要存入数据库的日结信息
		DayReportCenter dayReportCenter = new DayReportCenter();
		// 昨日余额
		BigDecimal beforeAmount = new BigDecimal(0);
		// 获取昨日日结主表最后一条记录主键
		DayReportMain dayReportMain = new DayReportMain();
		// 设置结账日期为昨日
		Date maxdate = getDayReportMaxDate(office);
		List<DayReportMain> dayReportList = Lists.newArrayList();
		if (maxdate != null) {
			// 设置开始时间
			dayReportMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(maxdate),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			// 设置结束时间
			dayReportMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(maxdate),
					ClearConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));

			// 设置账务发生机构
			dayReportMain.setOffice(office);
			// 获取昨日中心日结表数据
			dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);

			if (!Collections3.isEmpty(dayReportList)) {
				// 获取到离今天最近的一条中心账务日结金额作为期初余额
				for (DayReportCenter dayCenter : dayReportList.get(0).getDayReportCenterList()) {
					if (ClearConstant.AccountsType.ACCOUNTS_PROVISIONS.equals(dayCenter.getAccountsType())) {
						beforeAmount = dayCenter.getTotalAmount();
					}
				}
			}
		}
		// 获取今日余额
		BigDecimal todayTotalAmount = this.calTodayTotalAmount(beforeAmount, inAmount, outAmount);
		// 设置今日余额
		dayReportCenter.setTotalAmount(todayTotalAmount);
		// 设置今日收入笔数
		dayReportCenter.setInCount(new BigDecimal(inCount));
		// 设置今日支出笔数
		dayReportCenter.setOutCount(new BigDecimal(outCount));
		// 设置今日收入金额
		dayReportCenter.setInAmount(inAmount);
		// 设置今日支出金额
		dayReportCenter.setOutAmount(outAmount);
		// 设置昨日余额
		dayReportCenter.setBeforeAmount(beforeAmount);
		// 设置中心账务日结表主键
		dayReportCenter.setId(IdGen.uuid());
		// 设置账务类型
		dayReportCenter.setAccountsType(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		// 设置结账日期
		dayReportCenter.setReportDate(new Date());
		// 设置有效标识
		dayReportCenter.setDelFlag(ClearConstant.deleteFlag.Valid);
		// 设置结账方式
		dayReportCenter.setWindupType(windupType);
		dayReportCenter.setReportMainId(dayReportMainByInsert.getReportId());
		int dayMainInsertResult = dayReportCenterDao.insert(dayReportCenter);
		if (dayMainInsertResult == 0) {
			String strMessageContent = "账务日结表：" + dayReportCenter.getId() + "更新失败！";
			throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结表" });
		}

		// 计算账务日结主表所有数据
		BigDecimal InCountByMain = dayReportCenter.getInCount().add(dayReportMainByInsert.getInCount());
		BigDecimal InAmountByMain = dayReportCenter.getInAmount().add(dayReportMainByInsert.getInAmount());
		BigDecimal OutCountByMain = dayReportCenter.getOutCount().add(dayReportMainByInsert.getOutCount());
		BigDecimal OutAmountByMain = dayReportCenter.getOutAmount().add(dayReportMainByInsert.getOutAmount());
		BigDecimal totalAmountByMain = dayReportCenter.getTotalAmount().add(dayReportMainByInsert.getTotalAmount());
		dayReportMainByInsert.setInCount(InCountByMain);
		dayReportMainByInsert.setOutCount(OutCountByMain);
		dayReportMainByInsert.setInAmount(InAmountByMain);
		dayReportMainByInsert.setOutAmount(OutAmountByMain);
		dayReportMainByInsert.setTotalAmount(totalAmountByMain);

		return ClearConstant.SUCCESS;
	}

	/**
	 * @author qipeihong
	 * @version 2017年9月8日 账务日结主表保存
	 * 
	 * 
	 * @param dayReportMain
	 */

	@Transactional(readOnly = false)
	private void dayReportMainInsert(DayReportMain dayReportMain, Office office) {
		// 获取账务日结主表最新日期
		Date date = dayReportMain.getReportDate();
		DayReportMain dayReport = new DayReportMain();
		// 开始时间
		dayReport.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(date)));
		// 结束时间
		dayReport.setSearchDateEnd(DateUtils.formatDateTime(date));
		// 日结主键
		dayReport.setReportId(dayReportMain.getReportId());
		// 设置有效表示为无效
		dayReport.setStatus(ClearConstant.AccountsStatus.FAILED);
		int dayMainInsertResult = dayReportMainDao.insert(dayReportMain);
		if (dayMainInsertResult == 0) {
			String strMessageContent = "账务日结主表：" + dayReportMain.getReportId() + "更新失败！";
			throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结主表" });
		}
		dayReport.setOffice(office);
		dayReportMainDao.updateStatus(dayReport);

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
	 * @author qipeihong
	 * @version 2017年9月6日 获取最新账务日结日期信息
	 * 
	 * @return 账务日结最近日期
	 */

	public Date getDayReportMaxDate(Office office) {
		DayReportMain dayReportMain = new DayReportMain();
		dayReportMain.setReportDate(DateUtils.getDateStart(new Date()));
		// 设置账务发生机构
		dayReportMain.setOffice(office);
		Date maxdate = dayReportMainDao.getDayReportMaxDate(dayReportMain);
		return maxdate;

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月14日 获取最大时间
	 * 
	 * @param dayReportMain
	 * @return
	 */

	public Date getDayReportMaxDate(DayReportMain dayReportMain, Office office) {
		dayReportMain.setReportDate(DateUtils.getDateStart(dayReportMain.getReportDate()));
		// 设置发生机构
		dayReportMain.setOffice(office);
		Date maxdate = dayReportMainDao.getDayReportMaxDate(dayReportMain);
		return maxdate;

	}

}