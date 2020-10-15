package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 汇款记录保存Entity
 * @author WQJ
 * @version 2019-08-14
 */
public class PaidRecord extends DataEntity<PaidRecord> {
	
	private static final long serialVersionUID = 1L;
	private String transactionStatus;		// 交易状态
	private String toAcceptTheNumber;		// 受理编号
	private String tradeSerialNumber;		// 交易流水号
	private BigDecimal paidAmount;		// 汇款金额
	private String merchanOfficeId;		// 商户机构ID
	private String recordOfficeId;		// 产生汇款记录机构ID
	private String merchanOfficeName;		// 商户名称
	private String recordOfficeName;		// 产生汇款记录机构名称
	private String createName;		// create_name
	private String updateName;		// update_name
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	
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

	public PaidRecord() {
		super();
	}

	public PaidRecord(String id){
		super(id);
	}

	@Length(min=0, max=1, message="交易状态长度必须介于 0 和 1 之间")
	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	@Length(min=0, max=64, message="受理编号长度必须介于 0 和 64 之间")
	public String getToAcceptTheNumber() {
		return toAcceptTheNumber;
	}

	public void setToAcceptTheNumber(String toAcceptTheNumber) {
		this.toAcceptTheNumber = toAcceptTheNumber;
	}
	
	@Length(min=0, max=64, message="交易流水号长度必须介于 0 和 64 之间")
	public String getTradeSerialNumber() {
		return tradeSerialNumber;
	}

	public void setTradeSerialNumber(String tradeSerialNumber) {
		this.tradeSerialNumber = tradeSerialNumber;
	}
	
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	
	@Length(min=0, max=64, message="商户机构ID长度必须介于 0 和 64 之间")
	public String getMerchanOfficeId() {
		return merchanOfficeId;
	}

	public void setMerchanOfficeId(String merchanOfficeId) {
		this.merchanOfficeId = merchanOfficeId;
	}
	
	@Length(min=0, max=64, message="产生汇款记录机构ID长度必须介于 0 和 64 之间")
	public String getRecordOfficeId() {
		return recordOfficeId;
	}

	public void setRecordOfficeId(String recordOfficeId) {
		this.recordOfficeId = recordOfficeId;
	}
	
	@Length(min=0, max=200, message="商户名称长度必须介于 0 和 200 之间")
	public String getMerchanOfficeName() {
		return merchanOfficeName;
	}

	public void setMerchanOfficeName(String merchanOfficeName) {
		this.merchanOfficeName = merchanOfficeName;
	}
	
	@Length(min=0, max=200, message="产生汇款记录机构名称长度必须介于 0 和 200 之间")
	public String getRecordOfficeName() {
		return recordOfficeName;
	}

	public void setRecordOfficeName(String recordOfficeName) {
		this.recordOfficeName = recordOfficeName;
	}
	
	@Length(min=0, max=200, message="create_name长度必须介于 0 和 200 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
	
	@Length(min=0, max=200, message="update_name长度必须介于 0 和 200 之间")
	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	
}