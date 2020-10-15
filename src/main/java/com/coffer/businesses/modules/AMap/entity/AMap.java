package com.coffer.businesses.modules.AMap.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 
 * @author liuyaowen
 * @version
 *
 */
public class AMap extends DataEntity<AMap> {
	private static final long serialVersionUID = 1L;

	/** 设备号 **/
	private String equipmentNumber;
	/** 经度 **/
	private String longitude;
	/** 纬度 **/
	private String latitude;
	/** 状态 **/
	private String status;

	public String getEquipmentNumber() {
		return equipmentNumber;
	}

	public void setEquipmentNumber(String equipmentNumber) {
		this.equipmentNumber = equipmentNumber;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
