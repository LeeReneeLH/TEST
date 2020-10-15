package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 清机主表Entity
 * 
 * @author XL
 * @version 2019-06-26
 */
public class ClearPlanInfo extends DataEntity<ClearPlanInfo> {

	private static final long serialVersionUID = 1L;
	private String planId; // 任务ID
	private String clearManNo; // 清机人编号
	private String clearManName; // 清机人名称
	private String status; // 状态（0-清机未完成；1-清机已完成）
	private String amount; // 清机总金额
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	private List<Office> doorList = Lists.newArrayList(); // 门店id列表
	private List<ClearPlanDetail> batchList = Lists.newArrayList(); // 批量插入计划信息列表

	private String distanceLastTime; // 上一次清机时间到这次清机时间的天数

	private String planType;
	private String clearingGroupId;
	private String equipmentId;

	private List<ClearPlanUserDetail> clearPlanUserDetailList = Lists.newArrayList(); // 清机任务人员明细
	private List<ClearPlanInfo> clearPlanInfoList = Lists.newArrayList(); // 清机主表
	
	private Office vinOffice;// 维护机构
	private String equipmentName;// 设备别名
	private String clearingGroupName;// 清机组
	
	/** 上次清机时间  修改人：GJ 日期：2020-03-05  */
	private Date lastClearDate;//上次清机时间

	public String getDistanceLastTime() {
		return distanceLastTime;
	}

	public void setDistanceLastTime(String distanceLastTime) {
		this.distanceLastTime = distanceLastTime;
	}

	public ClearPlanInfo() {
		super();
	}

	public ClearPlanInfo(String id) {
		super(id);
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getClearManNo() {
		return clearManNo;
	}

	public void setClearManNo(String clearManNo) {
		this.clearManNo = clearManNo;
	}

	public String getClearManName() {
		return clearManName;
	}

	public void setClearManName(String clearManName) {
		this.clearManName = clearManName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public List<Office> getDoorList() {
		return doorList;
	}

	public void setDoorList(List<Office> doorList) {
		this.doorList = doorList;
	}

	public List<String> getDoorIdList() {
		List<String> doorIdList = Lists.newArrayList();
		for (Office office : doorList) {
			doorIdList.add(office.getId());
		}
		return doorIdList;
	}

	public void setDoorIdList(List<String> doorIdList) {
		doorList = Lists.newArrayList();
		for (String doorId : doorIdList) {
			Office office = new Office();
			office.setId(doorId);
			doorList.add(office);
		}
	}

	public String getDoorIds() {
		return StringUtils.join(getDoorIdList(), ",");
	}

	public void setDoorIds(String doorIds) {
		doorList = Lists.newArrayList();
		if (doorIds != null) {
			String[] ids = StringUtils.split(doorIds, ",");
			setDoorIdList(Lists.newArrayList(ids));
		}
	}

	public List<ClearPlanDetail> getBatchList() {
		return batchList;
	}

	public void setBatchList(List<ClearPlanDetail> batchList) {
		this.batchList = batchList;
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

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getClearingGroupId() {
		return clearingGroupId;
	}

	public void setClearingGroupId(String clearingGroupId) {
		this.clearingGroupId = clearingGroupId;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public List<ClearPlanUserDetail> getClearPlanUserDetailList() {
		return clearPlanUserDetailList;
	}

	public void setClearPlanUserDetailList(List<ClearPlanUserDetail> clearPlanUserDetailList) {
		this.clearPlanUserDetailList = clearPlanUserDetailList;
	}

	public List<ClearPlanInfo> getClearPlanInfoList() {
		return clearPlanInfoList;
	}

	public void setClearPlanInfoList(List<ClearPlanInfo> clearPlanInfoList) {
		this.clearPlanInfoList = clearPlanInfoList;
	}

	public Office getVinOffice() {
		return vinOffice;
	}

	public void setVinOffice(Office vinOffice) {
		this.vinOffice = vinOffice;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public String getClearingGroupName() {
		return clearingGroupName;
	}

	public void setClearingGroupName(String clearingGroupName) {
		this.clearingGroupName = clearingGroupName;
	}

	public Date getLastClearDate() {
		return lastClearDate;
	}

	public void setLastClearDate(Date lastClearDate) {
		this.lastClearDate = lastClearDate;
	}
}