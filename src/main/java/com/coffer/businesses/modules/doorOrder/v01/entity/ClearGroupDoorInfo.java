package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 清机组门店明细Entity
 * @author ZXK
 * @version 2019-07-24
 *
 */
public class ClearGroupDoorInfo extends DataEntity<ClearGroupDoorInfo>{

	private static final long serialVersionUID = 1L;
	private String clearGroupId; //清机组编号
	private String doorId; //门店编号
	private String doorName; //门店名称
	private String equipmentId; //设备编号
	private String equipmentName; //设备名称
	
	public String getClearGroupId() {
		return clearGroupId;
	}
	public void setClearGroupId(String clearGroupId) {
		this.clearGroupId = clearGroupId;
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
	public String getEquipmentId() {
		return equipmentId;
	}
	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	public String getEquipmentName() {
		return equipmentName;
	}
	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}
	

	
	
}