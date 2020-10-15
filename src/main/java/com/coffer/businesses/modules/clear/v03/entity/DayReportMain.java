package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 账务日结Entity
 * 
 * @author QPH
 * @version 2017-09-08
 */
public class DayReportMain extends DataEntity<DayReportMain> {

	private static final long serialVersionUID = 1L;
	private String reportId; // 主键ID
	private BigDecimal beforeAmount; // 期初余额
	private BigDecimal inCount; // 收入笔数
	@ExcelField(title = "收入金额", align = 2)
	private BigDecimal inAmount; // 收入金额
	private BigDecimal outCount; // 支出笔数
	@ExcelField(title = "支出金额", align = 2)
	private BigDecimal outAmount; // 支出金额
	@ExcelField(title = "期末余额", align = 2)
	private BigDecimal totalAmount; // 期末余额
	private Date reportDate; // 结账日期
	private String windupType; // 结账方式（0：自动结账 1：手动结账）
	private String status; // 状态 （0：有效 1 ：无效）
	private User reportBy; // 日结人
	private String reportName; // 日结人姓名
	/** 中心账务日结列表 */
	private List<DayReportCenter> dayReportCenterList;

	/** 客户账务日结列表 */
	private List<DayReportGuest> dayReportGuestList;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	/* 折线图用  修改人:sg 修改日期:2017-12-05 begin*/
	// 查询状态 0年1季度2月3周
	private String filterCondition;
	// 账务类型(现金业务、备付金业务)
	private String accountsType;
	// 日期
	@ExcelField(title = "日期", align = 2)
	private String handInDate;
	/* end */

	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */

	public DayReportMain() {
		super();
	}

	public DayReportMain(String id) {
		super(id);
	}

	public BigDecimal getBeforeAmount() {
		return beforeAmount;
	}

	public void setBeforeAmount(BigDecimal beforeAmount) {
		this.beforeAmount = beforeAmount;
	}

	public BigDecimal getInCount() {
		return inCount;
	}

	public void setInCount(BigDecimal inCount) {
		this.inCount = inCount;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public BigDecimal getOutCount() {
		return outCount;
	}

	public void setOutCount(BigDecimal outCount) {
		this.outCount = outCount;
	}

	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public List<DayReportCenter> getDayReportCenterList() {
		return dayReportCenterList;
	}

	public void setDayReportCenterList(List<DayReportCenter> dayReportCenterList) {
		this.dayReportCenterList = dayReportCenterList;
	}

	public List<DayReportGuest> getDayReportGuestList() {
		return dayReportGuestList;
	}

	public void setDayReportGuestList(List<DayReportGuest> dayReportGuestList) {
		this.dayReportGuestList = dayReportGuestList;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
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

	public String getWindupType() {
		return windupType;
	}

	public void setWindupType(String windupType) {
		this.windupType = windupType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getReportBy() {
		return reportBy;
	}

	public void setReportBy(User reportBy) {
		this.reportBy = reportBy;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getHandInDate() {
		return handInDate;
	}

	public void setHandInDate(String handInDate) {
		this.handInDate = handInDate;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getAccountsType() {
		return accountsType;
	}

	public void setAccountsType(String accountsType) {
		this.accountsType = accountsType;
	}

	@Override
	public String toString() {
		return "DayReportMain [reportId=" + reportId + ", beforeAmount=" + beforeAmount + ", totalAmount=" + totalAmount
				+ ", reportDate=" + reportDate + ", status=" + status + "]";
	}

}