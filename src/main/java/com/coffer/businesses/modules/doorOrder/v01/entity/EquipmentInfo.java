package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 机具管理Entity
 * 
 * @author 机具管理
 * @version 2019-06-26
 */
public class EquipmentInfo extends DataEntity<EquipmentInfo> {

	private static final long serialVersionUID = 1L;
	private String name; // 机具名称
	private Office aOffice; // 所属机构ID
	private Office vinOffice; // 维护机构ID
	private String merchantId; // 商户ID
	private String pOfficeId; // 上级机构ID
	private String pOfficeName; // 上级机构名称
	private String status; // 状态（0-未绑定；1-已绑定）
	private String isUse; // 启用状态（0-未启用；1-已启用）
	private int count; // 机具数量
	private String surplusAmount; // 设备余额
	private String firstFlag; // 首页机具查询标识

	// by ZXK 2019-7-17 新增字段 start
	private String seriesNumber; // 序列号
	private String type; // 机型
	private String IP; // IP地址
	// end
	private String clientFlag; // 商户查询机具标识
	private String doorFlag; // 门店查询机具标识
	private String connStatus; // 设备连线状态
	private List<String> connStatusList; // 连线状态列表

	/** 页面对应的添加时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的添加时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	
	// -------add by zxk 2019-08-30-----
	/** 上次清机时间 **/
	private String lastDate;
	// 距今天数
	private int intervalTime;
	
	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
	
	private String distanceLastTime; //上次清机到这次清机天数
	
	public String getDistanceLastTime() {
		return distanceLastTime;
	}

	public void setDistanceLastTime(String distanceLastTime) {
		this.distanceLastTime = distanceLastTime;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public EquipmentInfo() {
		super();
	}

	public EquipmentInfo(String id) {
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Office getaOffice() {
		return aOffice;
	}

	public void setaOffice(Office aOffice) {
		this.aOffice = aOffice;
	}

	public Office getVinOffice() {
		return vinOffice;
	}

	public void setVinOffice(Office vinOffice) {
		this.vinOffice = vinOffice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getpOfficeId() {
		return pOfficeId;
	}

	public void setpOfficeId(String pOfficeId) {
		this.pOfficeId = pOfficeId;
	}

	public String getpOfficeName() {
		return pOfficeName;
	}

	public void setpOfficeName(String pOfficeName) {
		this.pOfficeName = pOfficeName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getConnStatus() {
		return connStatus;
	}

	public void setConnStatus(String connStatus) {
		this.connStatus = connStatus;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getSurplusAmount() {
		return surplusAmount;
	}

	public void setSurplusAmount(String surplusAmount) {
		this.surplusAmount = surplusAmount;
	}

	public List<String> getConnStatusList() {
		return connStatusList;
	}

	public void setConnStatusList(List<String> connStatusList) {
		this.connStatusList = connStatusList;
	}

	public String getClientFlag() {
		return clientFlag;
	}

	public void setClientFlag(String clientFlag) {
		this.clientFlag = clientFlag;
	}

	public String getDoorFlag() {
		return doorFlag;
	}

	public void setDoorFlag(String doorFlag) {
		this.doorFlag = doorFlag;
	}

	public String getFirstFlag() {
		return firstFlag;
	}

	public void setFirstFlag(String firstFlag) {
		this.firstFlag = firstFlag;
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

}