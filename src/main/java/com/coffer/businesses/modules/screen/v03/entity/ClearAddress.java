package com.coffer.businesses.modules.screen.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 清分人员明细Entity
 * @author wanglin
 * @version 2018-01-04
 */
public class ClearAddress extends DataEntity<ClearAddress> {
	
	private static final long serialVersionUID = 1L;
	private String name;			// 名称
	private String longitude;		// 经度
	private String latitude;		// 纬度
	
	public ClearAddress() {
		super();
	}

	public ClearAddress(String id){
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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


}