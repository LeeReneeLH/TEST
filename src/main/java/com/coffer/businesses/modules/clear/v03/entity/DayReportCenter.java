package com.coffer.businesses.modules.clear.v03.entity;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 账务日结Entity
 * @author QPH
 * @version 2017-09-04
 */
public class DayReportCenter extends DataEntity<DayReportCenter> {
	
	private static final long serialVersionUID = 1L;
	private String accountsType; // 账务类型(现金业务、备付金业务)
	private BigDecimal beforeAmount; // 期初余额
	private BigDecimal inCount; // 收入笔数
	private BigDecimal inAmount; // 收入金额
	private BigDecimal outCount; // 支出笔数
	private BigDecimal outAmount; // 支出金额
	private BigDecimal totalAmount; // 期末余额
	private Date reportDate;		// 结账日期
	private String windupType; // 结账方式（0：自动结账 1：手动结账）
	private String reportMainId; // 账务日结主表主键
	
	/** 账务类型列表 */
	private List<String> accountsTypes;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	public DayReportCenter() {
		super();
	}

	public DayReportCenter(String id){
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

	public String getAccountsType() {
		return accountsType;
	}

	public void setAccountsType(String accountsType) {
		this.accountsType = accountsType;
	}

	public List<String> getAccountsTypes() {
		return accountsTypes;
	}

	public void setAccountsTypes(List<String> accountsTypes) {
		this.accountsTypes = accountsTypes;
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

	public String getReportMainId() {
		return reportMainId;
	}

	public void setReportMainId(String reportMainId) {
		this.reportMainId = reportMainId;
	}
	
}