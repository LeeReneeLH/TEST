package com.coffer.businesses.modules.atm.v01.entity;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;

/**
 * ATM机信息维护Entity
 * 
 * @author wxz
 * @version 2017-11-3
 */
public class AtmInfoMaintain extends DataEntity<AtmInfoMaintain> {

	private static final long serialVersionUID = 1L;
	private String atmId; // ATM机编号
	private String tellerId; // 柜员号
	private String vinofficeId; // 维护机构
	private String vinofficeName; // 维护机构名称
	private String aofficeId; // 归属机构
	private String aofficeName; // 归属机构名称
	private String tofficeId; // 所属金库
	private String tofficeName; // 所属金库名称
	private String atmBrandsNo; // 品牌编号
	private String atmBrandsName; // 品牌名称
	private String atmTypeNo; // 型号编号
	private String atmTypeName; // 型号名称
	private String rfid; // RFID
	/** 更新时间 格式 ：yyyyMMddHHmmssSSSSSS */
	private String strUpdateDate;
	
	private String provinceCode;//机构所在省编码
	private String cityCode;//机构所在市编码
	private String countyCode;//机构所在区县编码
	private String longitude;//机构经度
	private String latitude;//机构纬度
	private String address;//装机地址

	/**
	 * @return the strUpdateDate
	 */
	public String getStrUpdateDate() {

		if (StringUtils.isBlank(strUpdateDate) && this.updateDate != null) {
			this.strUpdateDate = DateUtils.formatDate(this.updateDate, Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
		}

		return strUpdateDate;
	}

	/**
	 * @param strUpdateDate
	 *            the strUpdateDate to set
	 */
	public void setStrUpdateDate(String strUpdateDate) {
		this.strUpdateDate = strUpdateDate;
	}

	public AtmInfoMaintain() {
		super();
	}
	
	public AtmInfoMaintain(String id) {
		super(id);
	}

	public String getAtmId() {
		return atmId;
	}

	public void setAtmId(String atmId) {
		this.atmId = atmId;
	}

	public String getTellerId() {
		return tellerId;
	}

	public void setTellerId(String tellerId) {
		this.tellerId = tellerId;
	}

	public String getVinofficeId() {
		return vinofficeId;
	}

	public void setVinofficeId(String vinofficeId) {
		this.vinofficeId = vinofficeId;
	}

	public String getVinofficeName() {
		return vinofficeName;
	}

	public void setVinofficeName(String vinofficeName) {
		this.vinofficeName = vinofficeName;
	}

	public String getAofficeId() {
		return aofficeId;
	}

	public void setAofficeId(String aofficeId) {
		this.aofficeId = aofficeId;
	}

	public String getAofficeName() {
		return aofficeName;
	}

	public void setAofficeName(String aofficeName) {
		this.aofficeName = aofficeName;
	}

	public String getTofficeId() {
		return tofficeId;
	}

	public void setTofficeId(String tofficeId) {
		this.tofficeId = tofficeId;
	}

	public String getTofficeName() {
		return tofficeName;
	}

	public void setTofficeName(String tofficeName) {
		this.tofficeName = tofficeName;
	}

	public String getAtmBrandsNo() {
		return atmBrandsNo;
	}

	public void setAtmBrandsNo(String atmBrandsNo) {
		this.atmBrandsNo = atmBrandsNo;
	}

	public String getAtmBrandsName() {
		return atmBrandsName;
	}

	public void setAtmBrandsName(String atmBrandsName) {
		this.atmBrandsName = atmBrandsName;
	}

	public String getAtmTypeNo() {
		return atmTypeNo;
	}

	public void setAtmTypeNo(String atmTypeNo) {
		this.atmTypeNo = atmTypeNo;
	}

	public String getAtmTypeName() {
		return atmTypeName;
	}

	public void setAtmTypeName(String atmTypeName) {
		this.atmTypeName = atmTypeName;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
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

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
	
	
}