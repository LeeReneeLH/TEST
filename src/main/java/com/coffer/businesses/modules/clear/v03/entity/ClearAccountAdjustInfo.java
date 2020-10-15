package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 柜员调账Entity
 * @author dja
 * @version 2018-04-20
 */
public class ClearAccountAdjustInfo extends DataEntity<ClearAccountAdjustInfo>{
	
	private static final long serialVersionUID = 1L;
	private String accountsId;		// 业务流水，主键
	private String cashType;		// 金额类型
	private String payTellerBy;		// 缴款柜员编号
	private String payTellerName;   // 缴款柜员姓名
	private String reTellerBy;		// 收款柜员编号
	private String reTellerName;    // 收款柜员姓名
	private BigDecimal adjustMoney; // 调账金额
	private Office office; 		// 业务发生机构（清分中心）
	private String remarks;   //备注
	/** 状态 1：登记，2：冲正 **/
	private String status = "";	
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	private String searchTimeStart;
	private String searchTimeEnd;	
	
	
	public ClearAccountAdjustInfo() {
		super();
	}

	public ClearAccountAdjustInfo(String id){
		super(id);
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAccountsId() {
		return accountsId;
	}
	public void setAccountsId(String accountsId) {
		this.accountsId = accountsId;
	}
	public String getCashType() {
		return cashType;
	}
	public void setCashType(String cashType) {
		this.cashType = cashType;
	}
	public String getPayTellerBy() {
		return payTellerBy;
	}
	public void setPayTellerBy(String payTellerBy) {
		this.payTellerBy = payTellerBy;
	}
	public String getPayTellerName() {
		return payTellerName;
	}
	public void setPayTellerName(String payTellerName) {
		this.payTellerName = payTellerName;
	}
	public String getReTellerBy() {
		return reTellerBy;
	}
	public void setReTellerBy(String reTellerBy) {
		this.reTellerBy = reTellerBy;
	}
	public String getReTellerName() {
		return reTellerName;
	}
	public void setReTellerName(String reTellerName) {
		this.reTellerName = reTellerName;
	}
	public BigDecimal getAdjustMoney() {
		return adjustMoney;
	}
	public void setAdjustMoney(BigDecimal adjustMoney) {
		this.adjustMoney = adjustMoney;
	}
	
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
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
	public String getSearchTimeStart() {
		return searchTimeStart;
	}
	public void setSearchTimeStart(String searchTimeStart) {
		this.searchTimeStart = searchTimeStart;
	}
	public String getSearchTimeEnd() {
		return searchTimeEnd;
	}
	public void setSearchTimeEnd(String searchTimeEnd) {
		this.searchTimeEnd = searchTimeEnd;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
