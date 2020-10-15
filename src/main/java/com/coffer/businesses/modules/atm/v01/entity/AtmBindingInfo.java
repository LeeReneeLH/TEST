package com.coffer.businesses.modules.atm.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;

/**
 * ATM绑定信息Entity
 * 
 * @author XL
 * @version 2017-11-13
 */
public class AtmBindingInfo extends DataEntity<AtmBindingInfo> {

	private static final long serialVersionUID = 1L;
	private String bindingId; // 主键
	private String atmNo; // ATM机编号
	private String atmAccount; // 柜员编号
	private String addPlanId; // 加钞计划ID
	private String atmClearSerial; // 清机流水
	private BigDecimal amount; // 清机金额
	private BigDecimal addAmount;// 加钞金额
	private BigDecimal coreAmount; // 核心清机金额
	private String status; // 清点状态（0：未清点；1：已清点；2：已记账）
	private String recordingStatus; // 补录状态
	private String escort1By;// 加钞人员1
	private String escort1Name;// 加钞人员1姓名
	private String escort2By;// 加钞人员2
	private String escort2Name;// 加钞人员2姓名
	private List<AtmBindingDetail> abdL = Lists.newArrayList();

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	// ATM机名称
	private String atmTypeName;
	
	private Date addDate;	//加钞时间  add by wanglu 2017-11-23
	private Date clearDate;	//清机时间 add by wanglu 2017-11-23

	private String dataType;	//数据来源0：机器；1：补录
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public AtmBindingInfo() {
		super();
	}

	public AtmBindingInfo(String id) {
		super(id);
	}

	// @Length(min=1, max=64, message="主键长度必须介于 1 和 64 之间")
	public String getId() {
		return bindingId;
	}

	public void setId(String bindingId) {
		this.bindingId = bindingId;
	}

	@Length(min = 0, max = 10, message = "ATM机编号长度必须介于 0 和 10 之间")
	public String getAtmNo() {
		return atmNo;
	}

	public void setAtmNo(String atmNo) {
		this.atmNo = atmNo;
	}

	@Length(min = 0, max = 10, message = "柜员编号长度必须介于 0 和 10 之间")
	public String getAtmAccount() {
		return atmAccount;
	}

	public void setAtmAccount(String atmAccount) {
		this.atmAccount = atmAccount;
	}

	@Length(min = 1, max = 64, message = "加钞计划ID长度必须介于 1 和 64 之间")
	public String getAddPlanId() {
		return addPlanId;
	}

	public void setAddPlanId(String addPlanId) {
		this.addPlanId = addPlanId;
	}

	@Length(min = 0, max = 30, message = "清机流水长度必须介于 0 和 30 之间")
	public String getAtmClearSerial() {
		return atmClearSerial;
	}

	public void setAtmClearSerial(String atmClearSerial) {
		this.atmClearSerial = atmClearSerial;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getCoreAmount() {
		return coreAmount;
	}

	public void setCoreAmount(BigDecimal coreAmount) {
		this.coreAmount = coreAmount;
	}

	@Length(min = 1, max = 1, message = "清点状态（0：未清点；1：已清点；2：已记账）长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Length(min = 1, max = 50, message = "创建人名称长度必须介于 1 和 50 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
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

	public String getRecordingStatus() {
		return recordingStatus;
	}

	public void setRecordingStatus(String recordingStatus) {
		this.recordingStatus = recordingStatus;
	}

	public String getEscort1By() {
		return escort1By;
	}

	public void setEscort1By(String escort1By) {
		this.escort1By = escort1By;
	}

	public String getEscort1Name() {
		return escort1Name;
	}

	public void setEscort1Name(String escort1Name) {
		this.escort1Name = escort1Name;
	}

	public String getEscort2By() {
		return escort2By;
	}

	public void setEscort2By(String escort2By) {
		this.escort2By = escort2By;
	}

	public String getEscort2Name() {
		return escort2Name;
	}

	public void setEscort2Name(String escort2Name) {
		this.escort2Name = escort2Name;
	}

	public List<AtmBindingDetail> getAbdL() {
		return abdL;
	}

	public void setAbdL(List<AtmBindingDetail> abdL) {
		this.abdL = abdL;
	}

	public BigDecimal getAddAmount() {
		return addAmount;
	}

	public void setAddAmount(BigDecimal addAmount) {
		this.addAmount = addAmount;
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

	public String getAtmTypeName() {
		return atmTypeName;
	}

	public void setAtmTypeName(String atmTypeName) {
		this.atmTypeName = atmTypeName;
	}

	public String getBindingId() {
		return bindingId;
	}

	public void setBindingId(String bindingId) {
		this.bindingId = bindingId;
	}

	public Date getAddDate() {
		return addDate;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public Date getClearDate() {
		return clearDate;
	}

	public void setClearDate(Date clearDate) {
		this.clearDate = clearDate;
	}

	@Override
	public String toString() {
		return "AtmBindingInfo [bindingId=" + bindingId + ", atmNo=" + atmNo + ", atmAccount=" + atmAccount
				+ ", status=" + status + "]";
	}

}