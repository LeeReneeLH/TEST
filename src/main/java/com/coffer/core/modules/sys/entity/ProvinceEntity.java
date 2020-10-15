package com.coffer.core.modules.sys.entity;

import com.coffer.core.common.persistence.DataEntity;

public class ProvinceEntity extends DataEntity<ProvinceEntity>{
	
	private static final long serialVersionUID = 1L;
	
	/** 省份编码 **/
	private String provinceCode;
	
	/** 省份地图数据编码**/
	private String proJsonCode;
	
	/** 省份名字 **/
	private String proName;
	
	/** 省份在地图上的经度 **/
	private String longitude;
	
	/** 省份在地图上的纬度 **/
	private String latitude;
	
	/** 所在省份的银行数量 **/
	private String bankNum;
	
	/** 机构ID**/
	private String officeId;
	
	/** 机构名字**/
	private String officeName;
	
	/** 父机构ID**/
	private String parentId;
	
	/** 机构类型**/
	private String type;

	/** 地址（在线地图用）ADD by qph 2018-01-23 **/
	private String address;

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProJsonCode() {
		return proJsonCode;
	}

	public void setProJsonCode(String proJsonCode) {
		this.proJsonCode = proJsonCode;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
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

	public String getBankNum() {
		return bankNum;
	}

	public void setBankNum(String bankNum) {
		this.bankNum = bankNum;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
