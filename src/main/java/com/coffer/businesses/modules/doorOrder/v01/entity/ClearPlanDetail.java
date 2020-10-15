package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 清机明细Entity
 * 
 * @author XL
 * @version 2019-06-26
 */
public class ClearPlanDetail extends DataEntity<ClearPlanDetail> {

	private static final long serialVersionUID = 1L;
	private String clearNo; // 任务单号
	private String doorId; // 门店ID
	private String doorName; // 门店名称
	private String clearManNo; // 清机人
	private String clearManName; // 清机人名称
	private String status; // 状态（0-完成；1-未完成）

	public ClearPlanDetail() {
		super();
	}

	public ClearPlanDetail(String id) {
		super(id);
	}

	public String getClearNo() {
		return clearNo;
	}

	public void setClearNo(String clearNo) {
		this.clearNo = clearNo;
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

}