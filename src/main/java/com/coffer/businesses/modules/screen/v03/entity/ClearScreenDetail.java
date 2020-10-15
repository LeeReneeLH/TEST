package com.coffer.businesses.modules.screen.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 可视化明细表Entity
 * 
 * @author qph
 * @version 2018-03-02
 */
public class ClearScreenDetail extends DataEntity<ClearScreenDetail> {
	
	private static final long serialVersionUID = 1L;
	
	private String mainId; // 业务主表ID
	
	private String officeId; // 客户机构ID
	
	private String officeName; // 客户机构名称
	
	private String businessType; // 业务类型
	
	private String businessAmount; // 业务金额
	
	private String businessPct; // 业务所占百分比

	public String getMainId() {
		return mainId;
	}

	public void setMainId(String mainId) {
		this.mainId = mainId;
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

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
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