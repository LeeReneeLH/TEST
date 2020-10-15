package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 清分接收Entity
 * @author wanglin
 * @version 2017-02-13
 */
public class ClearConfirm extends DataEntity<ClearConfirm> {
	
	private static final long serialVersionUID = 1L;
	
	/** 业务流水 **/
	private String inNo = "";
	/** 状态 1：登记，2：确认 **/
	private String status = "";

	/** 状态名 1：登记，2：确认 **/
	private String statusName = "";
	
	/** 入库总金额 **/
	private BigDecimal inAmount;

	/** 总金额格式化 **/
	private String inAmountFormat = "";

	/** 登记机构 */
	private String registerOffice;
	
	/** 登记人 */
	private String registerBy;
	
	/** 登记日期 */
	private Date registerDate;
	
	/** 登记机构名*/
	private String registerOfficeNm;
	
	/** 登记人 */
	private String registerName;
	
	/** 备注 **/
	private String remarks = "";

	/** 接收人ID */
	private String receiveBy;
	
	/** 接收人名 */
	private String receiveName;
	
	/** 接收日期 */
	private Date receiveDate;
	
	/** 清分机构 */
	private Office rOffice;
	
	/** 预约清分明细 */
	private List<OrderClearDetail> orderClearDetailList = Lists.newArrayList();

	/** 面值列表 */
	private List<DenominationInfo> denominationList = Lists.newArrayList();
	
	
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;

	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;

	private String searchDateEnd;

	/** 状态 (1：登记，2：接收)（查询用） **/
	private String searchStatus;
	
	
	public ClearConfirm() {
		super();
	}

	public ClearConfirm(String id) {
		super(id);
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public String getInNo() {
		return inNo;
	}

	public void setInNo(String inNo) {
		this.inNo = inNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public String getInAmountFormat() {
		return inAmountFormat;
	}

	public void setInAmountFormat(String inAmountFormat) {
		this.inAmountFormat = inAmountFormat;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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


	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}



	public String getRegisterOffice() {
		return registerOffice;
	}

	public void setRegisterOffice(String registerOffice) {
		this.registerOffice = registerOffice;
	}

	public String getRegisterBy() {
		return registerBy;
	}

	public void setRegisterBy(String registerBy) {
		this.registerBy = registerBy;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getRegisterName() {
		return registerName;
	}

	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}

	public String getRegisterOfficeNm() {
		return registerOfficeNm;
	}

	public void setRegisterOfficeNm(String registerOfficeNm) {
		this.registerOfficeNm = registerOfficeNm;
	}

	public String getReceiveBy() {
		return receiveBy;
	}

	public void setReceiveBy(String receiveBy) {
		this.receiveBy = receiveBy;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}




	public List<OrderClearDetail> getOrderClearDetailList() {
		return orderClearDetailList;
	}

	public void setOrderClearDetailList(List<OrderClearDetail> orderClearDetailList) {
		this.orderClearDetailList = orderClearDetailList;
	}

	public List<DenominationInfo> getDenominationList() {
		return denominationList;
	}

	public void setDenominationList(List<DenominationInfo> denominationList) {
		this.denominationList = denominationList;
	}

	@Override
	public String toString() {
		return "clearConfirm [inNo=" + inNo + ", status=" + status + ", inAmount=" + inAmount + "]";
	}
	
}