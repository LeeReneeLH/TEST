package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 清机组Entity
 * 
 * @author ZXK
 * @version 2019-07-24
 */
public class ClearGroupMain extends DataEntity<ClearGroupMain>{

	private static final long serialVersionUID = 1L;
	private String clearGroupId; //清机组编号
	private String clearGroupName; //清机组名称
	private String clearCenterId; //清分中心编号
	private String clearCenterName; //清分中心名称
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	
	private List<StoEscortInfo> userList = Lists.newArrayList(); // 拥有用户列表
	private List<Office> doorList = Lists.newArrayList(); //拥有门店列表
	
	public List<StoEscortInfo> getUserList() {
		return userList;
	}
	public void setUserList(List<StoEscortInfo> userList) {
		this.userList = userList;
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
	public String getClearGroupId() {
		return clearGroupId;
	}
	public void setClearGroupId(String clearGroupId) {
		this.clearGroupId = clearGroupId;
	}
	public String getClearGroupName() {
		return clearGroupName;
	}
	public void setClearGroupName(String clearGroupName) {
		this.clearGroupName = clearGroupName;
	}
	public String getClearCenterId() {
		return clearCenterId;
	}
	public void setClearCenterId(String clearCenterId) {
		this.clearCenterId = clearCenterId;
	}
	public String getClearCenterName() {
		return clearCenterName;
	}
	public void setClearCenterName(String clearCenterName) {
		this.clearCenterName = clearCenterName;
	}
	
 }
