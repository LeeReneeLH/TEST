package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.modules.sys.entity.Office;

/**
 * 机构存款实体类
 * 
 * @author lihe
 * @version 2019-07-10
 */
public class OfficeAmount extends Office {

	private static final long serialVersionUID = 1L;
	private String doorId; // 门店Id (查询门店存款类型占比用)
	private String depositAmount; // 存款总金额
	private String depositCount; // 存款笔数
	private String yearDepositAmount; // 年存款总金额
	private String repayAmount; // 代付总金额
	private String yearRepayAmount; // 年代付总金额
	private String backAmount; // 回款总金额
	private String backAmountAll; // 回款总金额(查所有)
	private String unbackAmountAll; // 未回款总金额(查所有)
	private String clientDepositAmount; // 商户存款总金额
	private String doorDepositAmount; // 门店存款总金额
	private String depositMethod; // 存款方式
	private String remitAmount; // 商户汇款金额
	private String unremittedAmount; // 未汇款金额
	private String typeAmount; // 按照类型存款金额
	private String typeName; // 存款类型名称
	private String method; // 存款方式
	private String year; // 年份，按年份查询商户存款金额用
	private String quarter; // 季度，按季度查询存款用
	private String month; // 月份，按年份查询商户存款金额用
	private String day; // 天，按周查询商户存款金额用
	private String one; // 第一季度
	private String two; // 第二季度
	private String three; // 第三季度
	private String four; // 第四季度
	private int count; // 机具数量
	private int exCount; // 连线异常机具数量
	private int elCount; // 其他连线状态机具数量
	private int wholeCount; // 差错总计
	private String businessType; // 业务类型
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	private String lastDayReportDate; // 上次日结时间
	private int doCount; // 门店数量
	private int deCount; // 存款门店数量

	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	/** 业务类型列表 */
	private List<String> businessTypes;

	/** 门店存款排名列表 */
	private List<OfficeAmount> rankingList;

	/** 季度存款列表 */
	private List<String> quarterDepositList;
	
	/** 异常记录条数 */
	private String exceptionCount;
	/** 已处理记录条数 */
	private String processedCount;
	/** 登记记录总数 */
	private String registerCount;
	
	
	public String getRegisterCount() {
		return registerCount;
	}

	public void setRegisterCount(String registerCount) {
		this.registerCount = registerCount;
	}

	public String getExceptionCount() {
		return exceptionCount;
	}

	public void setExceptionCount(String exceptionCount) {
		this.exceptionCount = exceptionCount;
	}


	public String getProcessedCount() {
		return processedCount;
	}

	public void setProcessedCount(String processedCount) {
		this.processedCount = processedCount;
	}

	public String getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
	}

	public String getDepositCount() {
		return depositCount;
	}

	public void setDepositCount(String depositCount) {
		this.depositCount = depositCount;
	}

	public String getYearDepositAmount() {
		return yearDepositAmount;
	}

	public void setYearDepositAmount(String yearDepositAmount) {
		this.yearDepositAmount = yearDepositAmount;
	}

	public String getRepayAmount() {
		return repayAmount;
	}

	public void setRepayAmount(String repayAmount) {
		this.repayAmount = repayAmount;
	}

	public String getYearRepayAmount() {
		return yearRepayAmount;
	}

	public void setYearRepayAmount(String yearRepayAmount) {
		this.yearRepayAmount = yearRepayAmount;
	}

	public String getBackAmount() {
		return backAmount;
	}

	public void setBackAmount(String backAmount) {
		this.backAmount = backAmount;
	}

	public String getBackAmountAll() {
		return backAmountAll;
	}

	public void setBackAmountAll(String backAmountAll) {
		this.backAmountAll = backAmountAll;
	}

	public String getUnbackAmountAll() {
		return unbackAmountAll;
	}

	public void setUnbackAmountAll(String unbackAmountAll) {
		this.unbackAmountAll = unbackAmountAll;
	}

	public String getClientDepositAmount() {
		return clientDepositAmount;
	}

	public void setClientDepositAmount(String clientDepositAmount) {
		this.clientDepositAmount = clientDepositAmount;
	}

	public String getDoorDepositAmount() {
		return doorDepositAmount;
	}

	public String getDepositMethod() {
		return depositMethod;
	}

	public void setDepositMethod(String depositMethod) {
		this.depositMethod = depositMethod;
	}

	public void setDoorDepositAmount(String doorDepositAmount) {
		this.doorDepositAmount = doorDepositAmount;
	}

	public String getRemitAmount() {
		return remitAmount;
	}

	public void setRemitAmount(String remitAmount) {
		this.remitAmount = remitAmount;
	}

	public String getTypeAmount() {
		return typeAmount;
	}

	public void setTypeAmount(String typeAmount) {
		this.typeAmount = typeAmount;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getUnremittedAmount() {
		return unremittedAmount;
	}

	public void setUnremittedAmount(String unremittedAmount) {
		this.unremittedAmount = unremittedAmount;
	}

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public List<OfficeAmount> getRankingList() {
		return rankingList;
	}

	public void setRankingList(List<OfficeAmount> rankingList) {
		this.rankingList = rankingList;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public List<String> getQuarterDepositList() {
		return quarterDepositList;
	}

	public void setQuarterDepositList(List<String> quarterDepositList) {
		this.quarterDepositList = quarterDepositList;
	}

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	public String getThree() {
		return three;
	}

	public void setThree(String three) {
		this.three = three;
	}

	public String getFour() {
		return four;
	}

	public void setFour(String four) {
		this.four = four;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getExCount() {
		return exCount;
	}

	public void setExCount(int exCount) {
		this.exCount = exCount;
	}

	public int getElCount() {
		return elCount;
	}

	public void setElCount(int elCount) {
		this.elCount = elCount;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public int getWholeCount() {
		return wholeCount;
	}

	public void setWholeCount(int wholeCount) {
		this.wholeCount = wholeCount;
	}

	public String getLastDayReportDate() {
		return lastDayReportDate;
	}

	public void setLastDayReportDate(String lastDayReportDate) {
		this.lastDayReportDate = lastDayReportDate;
	}

	public int getDoCount() {
		return doCount;
	}

	public void setDoCount(int doCount) {
		this.doCount = doCount;
	}

	public int getDeCount() {
		return deCount;
	}

	public void setDeCount(int deCount) {
		this.deCount = deCount;
	}

}
