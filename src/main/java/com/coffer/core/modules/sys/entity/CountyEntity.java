package com.coffer.core.modules.sys.entity;

import com.coffer.core.common.persistence.DataEntity;

public class CountyEntity extends DataEntity<CountyEntity>{

	private static final long serialVersionUID = 1L;
	
	/** 县城编码 **/
	private String countyCode;
	
	/** 县城名字 **/
	private String countyName;
	
	/** 县城在地图上的经度 **/
	private String longitude;
	
	/** 县城在地图上的纬度 **/
	private String latitude;
	
	/** 县城所属省份编码**/
	private String provinceCode;
	
	/** 县城所属省份名称**/
	private String proName;
	
	/** 县城所属城市编码**/
	private String cityCode;
	
	/** 县城所属城市名称**/
	private String cityName;
	
	/** value常量 **/
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

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
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

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getBankNum() {
		return bankNum;
	}

	public void setBankNum(String bankNum) {
		this.bankNum = bankNum;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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
