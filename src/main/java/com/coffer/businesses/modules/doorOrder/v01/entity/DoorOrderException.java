package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;

/**
 * 存款异常信息Entity
 * 
 * @author zxk
 * @version 2019-11-11
 */
public class DoorOrderException extends DataEntity<DoorOrderException> {

	private static final long serialVersionUID = 1L;
	private String eqpId; // 机具编号
	private String bagNo; // 包号
	private String tickerTape; // 凭条号
	private String totalAmount; // 总金额
	private String status; // 状态（0登记，1异常，2已处理）
	private String businessType; // 业务类型
	private String currency; // 币种
	private User user; // 店员id
	private String userName; // 店员姓名
	private String doorId; // 门店编号
	private String doorName; // 门店名称
	private String businessTypeName; // 业务类型名称
	/* 新增字段 备用 */
	private String exceptionReason; // 异常原因
	private String exceptionType; // 异常类型
	private String batchNo; // 批次号
	private String seriesNumber; // 机具序列号(显示用)
	private String remarks; // 存款备注(显示用)
	private String remarksLast; // 存款备注后两位(显示用)

	private String tickertapeList;
	/** 状态列表 */
	private List<String> statusList = Lists.newArrayList();
	/** 金额明细 */
	private String detailList;
	/** 明细编号 */
	private String detailIdList;
	/** 业务类型列表 */
	private String busTypeList;
	
	/** 存款备注列表(七位码) gzd 19-12-13 */
	private String remarksList;

	private String amountList; // 预约明细
	private String detailAmount; // 明细金额，画面显示用

	/* 存款异常明细集合 实现主表对明细表的一对多 */
	private List<DoorOrderExceptionDetail> exceptionDetailList;

	/** 页面对应的开发时间和结束时间（查询用） */
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 开始时间和结束时间（查询用） */
	private String searchDateStart;
	private String searchDateEnd;

	/** 存款时间统计字段 add by yinkai 20200529 */
	private Date startTime;		//开始时间
	private Date endTime;		//结束时间
	private String costTime;	//耗时
	/** 存款时间统计字段 end */
	
	/**页面跳转标识  (1： 其他页面跳转  暂指中心跳转)*/
	private String pageSkipFlag;
	

	public String getPageSkipFlag() {
		return pageSkipFlag;
	}

	public void setPageSkipFlag(String pageSkipFlag) {
		this.pageSkipFlag = pageSkipFlag;
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

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getBusinessTypeName() {
		return businessTypeName;
	}

	public void setBusinessTypeName(String businessTypeName) {
		this.businessTypeName = businessTypeName;
	}

	public String getExceptionReason() {
		return exceptionReason;
	}

	public void setExceptionReason(String exceptionReason) {
		this.exceptionReason = exceptionReason;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public List<DoorOrderExceptionDetail> getExceptionDetailList() {
		return exceptionDetailList;
	}

	public void setExceptionDetailList(List<DoorOrderExceptionDetail> exceptionDetailList) {
		this.exceptionDetailList = exceptionDetailList;
	}

	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}

	public String getAmountList() {
		return amountList;
	}

	public void setAmountList(String amountList) {
		this.amountList = amountList;
	}

	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}

	public String getTickertapeList() {
		return tickertapeList;
	}

	public void setTickertapeList(String tickertapeList) {
		this.tickertapeList = tickertapeList;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	public String getDetailList() {
		return detailList;
	}

	public void setDetailList(String detailList) {
		this.detailList = detailList;
	}

	public String getDetailIdList() {
		return detailIdList;
	}

	public void setDetailIdList(String detailIdList) {
		this.detailIdList = detailIdList;
	}

	public String getBusTypeList() {
		return busTypeList;
	}

	public void setBusTypeList(String busTypeList) {
		this.busTypeList = busTypeList;
	}

	public void setRemarksList(String remarksList) {
		this.remarksList = remarksList;
	}

	public DoorOrderException() {
		super();
	}

	public DoorOrderException(String id) {
		super(id);
	}

	@Length(min = 0, max = 64, message = "机具编号长度必须介于 0 和 64 之间")
	public String getEqpId() {
		return eqpId;
	}

	public void setEqpId(String eqpId) {
		this.eqpId = eqpId;
	}

	@Length(min = 0, max = 64, message = "包号长度必须介于 0 和 64 之间")
	public String getBagNo() {
		return bagNo;
	}

	public void setBagNo(String bagNo) {
		this.bagNo = bagNo;
	}

	@Length(min = 0, max = 64, message = "凭条号长度必须介于 0 和 64 之间")
	public String getTickerTape() {
		return tickerTape;
	}

	public void setTickerTape(String tickerTape) {
		this.tickerTape = tickerTape;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Length(min = 0, max = 1, message = "状态（0登记，1异常，2已处理）长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Length(min = 0, max = 64, message = "业务类型长度必须介于 0 和 64 之间")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@Length(min = 0, max = 2, message = "币种长度必须介于 0 和 2 之间")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Length(min = 0, max = 64, message = "店员姓名长度必须介于 0 和 64 之间")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarksLast() {
		return remarksLast;
	}

	public String getRemarksList() {
		return remarksList;
	}

	public void setRemarksLast(String remarksLast) {
		this.remarksLast = remarksLast;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getCostTime() {
		return costTime;
	}

	public void setCostTime(String costTime) {
		this.costTime = costTime;
	}
}