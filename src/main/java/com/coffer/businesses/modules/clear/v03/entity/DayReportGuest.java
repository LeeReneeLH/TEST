package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 客户账务日结Entity
 * 
 * @author QPH
 * @version 2017-09-04
 */
public class DayReportGuest extends DataEntity<DayReportGuest> {

	private static final long serialVersionUID = 1L;
	private String clientId; // 客户编号
	private String clientName; // 客户名称
	private String accountsType; // 账务类型
	private BigDecimal beforeAmount; // 期初余额
	private BigDecimal inCount; // 收入笔数
	private BigDecimal inAmount; // 收入金额
	private BigDecimal outCount; // 支出笔数
	private BigDecimal outAmount; // 支出金额
	private BigDecimal totalAmount; // 期末余额
	private Date reportDate; // 结账日期
	private String windupType; // 结账方式（0：自动结账 1：手动结账）
	private String reportMainId; // 账务日结主表主键
	/** 账务类型列表 */
	private List<String> accountsTypes;

	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	/* 追加status属性 修改人:sg 修改日期:2017-12-20 begin */
	private String status;

	/* end */

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

	public DayReportGuest() {
		super();
	}

	public DayReportGuest(String id) {
		super(id);
	}

	@Length(min = 0, max = 64, message = "客户编号长度必须介于 0 和 64 之间")
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Length(min = 0, max = 20, message = "客户名称长度必须介于 0 和 20 之间")
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
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

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}