package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 公司账务Entity
 * 
 * @author XL
 * @version 2019-06-26
 */
public class CompanyAccountsMain extends DataEntity<CompanyAccountsMain> {

	private static final long serialVersionUID = 1L;
	private String custNo; // 客户编号
	private String custName; // 客户名称
	private String type; // 业务类型（0-回款;1-代付）
	private String businessId; // 业务对应流水ID
	private String officeId; // 账务发生机构（清分中心）
	private String officeName; // 账务发生机构名称
	private String companyId; // 账务归属机构（公司）
	private String companyName; // 账务归属机构名称（公司）
	private BigDecimal inAmount; // 入库总金额（贷）
	private BigDecimal outAmount; // 出库总金额（借）
	private BigDecimal companyAmount; // 公司账户金额
	private String inoutType; // 出入库类型：0：出库 1：入库
	private String bankCard;// 卡号，前端传参使用
	private String businessStatus;// 业务状态（1-登记，2-冲正）

	public String getBusinessStatus() {
		return businessStatus;
	}

	public void setBusinessStatus(String businessStatus) {
		this.businessStatus = businessStatus;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
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

	public String getInoutType() {
		return inoutType;
	}

	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

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

	public CompanyAccountsMain() {
		super();
	}

	public CompanyAccountsMain(String id) {
		super(id);
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public BigDecimal getCompanyAmount() {
		return companyAmount;
	}

	public void setCompanyAmount(BigDecimal companyAmount) {
		this.companyAmount = companyAmount;
	}

}