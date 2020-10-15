package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportCenterDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportCenter;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
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
public class DoorDayReportCenterService extends CrudService<DoorDayReportCenterDao, DoorDayReportCenter> {

	@Autowired
	private DoorCenterAccountsMainDao centerAccountsMainDao;

	@Autowired
	private DoorDayReportCenterDao dayReportCenterDao;

	@Autowired
	private DoorDayReportMainDao dayReportMainDao;

	public DoorDayReportCenter get(String id) {
		return super.get(id);
	}

	public List<DoorDayReportCenter> findList(DoorDayReportCenter dayReportCenter) {
		return super.findList(dayReportCenter);
	}

	public Page<DoorDayReportCenter> findPage(Page<DoorDayReportCenter> page, DoorDayReportCenter dayReportCenter) {
		return super.findPage(page, dayReportCenter);
	}

	@Transactional(readOnly = false)
	public void save(DoorDayReportCenter dayReportCenter) {
		super.save(dayReportCenter);
	}

	@Transactional(readOnly = false)
	public void delete(DoorDayReportCenter dayReportCenter) {
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
	public synchronized void dayAccountReportByCenter(String windupType, DoorDayReportMain dayReportMain,
			Office office) {

		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 设置开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(getDayReportMaxDate(office)));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(dayReportMain.getReportDate()));
		// 上门收款业务日结
		dayDoorOrderAccountReport(centerAccountsMain, windupType, dayReportMain, office);
		// 账务日结主表保存
		dayReportMainInsert(dayReportMain, office);
	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 中心上门收款账务类型日结
	 * 
	 * @param centerAccountsMain
	 * @param windupType
	 *            结账类型
	 * @param dayReportMainByInsert
	 *            账务日结主表
	 * @return
	 */
	@Transactional(readOnly = false)
	public String dayDoorOrderAccountReport(DoorCenterAccountsMain centerAccountsMain, String windupType,
			DoorDayReportMain dayReportMainByInsert, Office office) {
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
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.CENTER_PAID);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.DOOR_ORDER);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.ERROR_HANDLE);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
		dayClearBusinessTypeList.add(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE);
		centerAccountsMain.setBusinessTypes(dayClearBusinessTypeList);
		// 设置账务发生机构
		centerAccountsMain.setRofficeId(office.getId());
		// 从账务表中获取今天的流水
		List<DoorCenterAccountsMain> accountMainList = centerAccountsMainDao
				.getAccountByBusinessTypeForReport(centerAccountsMain);
		// 若今天有账务流水
		for (DoorCenterAccountsMain accountsMain : accountMainList) {
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
		DoorDayReportCenter dayReportCenter = new DoorDayReportCenter();
		// 昨日余额
		BigDecimal beforeAmount = new BigDecimal(0);
		// 获取昨日日结主表最后一条记录主键
		DoorDayReportMain dayReportMain = new DoorDayReportMain();
		// 设置结账日期为昨日
		Date maxdate = getDayReportMaxDate(office);
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
			// 获取昨日中心日结表数据
			dayReportList = dayReportMainDao.findListByReportDate(dayReportMain);
			if (!Collections3.isEmpty(dayReportList)) {
				// 获取到离今天最近的一条中心账务日结金额作为期初余额
				for (DoorDayReportCenter dayCenter : dayReportList.get(0).getDayReportCenterList()) {
					if (DoorOrderConstant.AccountsType.ACCOUNTS_DOOR.equals(dayCenter.getAccountsType())) {
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
		dayReportCenter.setAccountsType(DoorOrderConstant.AccountsType.ACCOUNTS_DOOR);
		// 设置结账日期
		dayReportCenter.setReportDate(dayReportMainByInsert.getReportDate());
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
		dayReportMainByInsert.setReportDate(dayReportMainByInsert.getReportDate());
		// 设置账务发生机构
		dayReportMainByInsert.setOffice(office);
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
	public void dayReportMainInsert(DoorDayReportMain dayReportMain, Office office) {
		int dayMainInsertResult = dayReportMainDao.insert(dayReportMain);
		if (dayMainInsertResult == 0) {
			String strMessageContent = "账务日结主表：" + dayReportMain.getReportId() + "更新失败！";
			throw new BusinessException("message.A1002", strMessageContent, new String[] { "账务日结主表" });
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
		// 增加今日收入余额
		BigDecimal todayTotalAmount = beforeAmount.add(inAmount);
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
		DoorDayReportMain dayReportMain = new DoorDayReportMain();
		// 设置账务发生机构
		dayReportMain.setOffice(office);
		return dayReportMainDao.getDayReportMaxDate(dayReportMain);

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月14日 获取最大时间
	 * 
	 * @param dayReportMain
	 * @return
	 */

	public Date getDayReportMaxDate(DoorDayReportMain dayReportMain, Office office) {
		dayReportMain.setReportDate(DateUtils.getDateStart(dayReportMain.getReportDate()));
		// 设置发生机构
		dayReportMain.setOffice(office);
		return dayReportMainDao.getDayReportMaxDate(dayReportMain);

	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月3日 获取本次日结的上一次日结时间
	 * 
	 * @return
	 */
	public Date getThisDayReportMaxDate(Office office, Date date) {
		DoorDayReportMain dayReportMain = new DoorDayReportMain();
		dayReportMain.setReportDate(date);
		// 设置账务发生机构
		dayReportMain.setOffice(office);
		return dayReportMainDao.getThisDayReportMaxDate(dayReportMain);
	}
}