package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 机构存款实体类
 * 
 * @author lihe
 * @version 2019-07-10
 */
public class DepositAmount extends DataEntity<DepositAmount> {

	private static final long serialVersionUID = 1L;
	private String centerId; // 中心ID
	private String centerName; // 中心名称
	private String merchantId; // 商户ID
	private String merchantName; // 商户名称
	private String doorId; // 门店ID
	private String doorName; // 门店名称
	private String depositAmount; // 存款金额
	private String lastDayReportDate; // 上次日结时间
	private int doCount; // 门店数量
	private int deCount; // 存款门店数量
	private int depositCount; // 存款笔数
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	private List<DepositAmount> doorDepositList; // 门店存款信息列表

	/** 业务类型列表 */
	private List<String> businessTypes;

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getCenterName() {
		return centerName;
	}

	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
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

	public String getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
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

	public String getLastDayReportDate() {
		return lastDayReportDate;
	}

	public void setLastDayReportDate(String lastDayReportDate) {
		this.lastDayReportDate = lastDayReportDate;
	}

	public int getDoCount() {
		return doCount;
	}

	public void setDoCount(int doCount) {
		this.doCount = doCount;
	}

	public int getDeCount() {
		return deCount;
	}

	public void setDeCount(int deCount) {
		this.deCount = deCount;
	}

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public int getDepositCount() {
		return depositCount;
	}

	public void setDepositCount(int depositCount) {
		this.depositCount = depositCount;
	}

	public List<DepositAmount> getDoorDepositList() {
		return doorDepositList;
	}

	public void setDoorDepositList(List<DepositAmount> doorDepositList) {
		this.doorDepositList = doorDepositList;
	}

}
