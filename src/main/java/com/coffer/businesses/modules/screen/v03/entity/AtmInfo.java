package com.coffer.businesses.modules.screen.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 自助设备列表Entity
 * @author wanglin
 * @version 2018-01-04
 */
public class AtmInfo extends DataEntity<AtmInfo> {
	
	private static final long serialVersionUID = 1L;
	private String officeId;		// 机构ID
	private String officeName;		// 机构名
	private String atmAmount;		// 自助设备数量
	private String atmPct;			// 自助设备所占百分比
	
	public AtmInfo() {
		super();
	}

	public AtmInfo(String id){
		super(id);
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

	public String getAtmAmount() {
		return atmAmount;
	}

	public void setAtmAmount(String atmAmount) {
		this.atmAmount = atmAmount;
	}

	public String getAtmPct() {
		return atmPct;
	}

	public void setAtmPct(String atmPct) {
		this.atmPct = atmPct;
	}



}