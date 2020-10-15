package com.coffer.businesses.modules.screen.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 自助设备列表Entity
 * @author wanglin
 * @version 2018-01-04
 */
public class DoorInfo extends DataEntity<DoorInfo> {
	
	private static final long serialVersionUID = 1L;
	private String officeId;		//机构ID
	private String officeName;		//机构名
	private String businessAmount;	//业务金额
	private String businessPct;		//业务所占比例
	
	public DoorInfo() {
		super();
	}

	public DoorInfo(String id){
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

	public String getBusinessAmount() {
		return businessAmount;
	}

	public void setBusinessAmount(String businessAmount) {
		this.businessAmount = businessAmount;
	}

	public String getBusinessPct() {
		return businessPct;
	}

	public void setBusinessPct(String businessPct) {
		this.businessPct = businessPct;
	}

}