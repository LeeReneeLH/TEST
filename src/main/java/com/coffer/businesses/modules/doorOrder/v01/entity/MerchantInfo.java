package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 商户日结信息Entity
 * 
 * @author wqj
 * @version 2019-07-10
 */
public class MerchantInfo extends DataEntity<MerchantInfo> {
	private static final long serialVersionUID = 1L;
	private String reportId; // 日结主键ID
	private String officeId; // 商户ID
	private String officeName; // 商户名称
	private String settlementType; // 商户结算类型
	private BigDecimal inAmount;// 商户借入金额
	private BigDecimal outAmount;// 商户贷出金额
	private BigDecimal totalAmount; // 总金额
	private String paidStatus; // 商户代付状态
	private Date reportDate; // 该商户本次日结时间
	private String accountsType;// 商户账务类型

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getAccountsType() {
		return accountsType;
	}

	public void setAccountsType(String accountsType) {
		this.accountsType = accountsType;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
