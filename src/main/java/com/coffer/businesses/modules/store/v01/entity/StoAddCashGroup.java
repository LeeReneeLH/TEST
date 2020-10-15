package com.coffer.businesses.modules.store.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 加钞组Entity
 * 
 * @author wanlgu
 * @version 2017-11-09
 */
public class StoAddCashGroup  extends DataEntity<StoAddCashGroup> {

	private static final long serialVersionUID = 1L;
	
	private String groupName;	//加钞组名称
	private String carId;	//车辆ID
	private String carName;	//车辆名称
	private String escortNo1;	//人员1ID
	private String escortName1;	//人员1名称
	private String escortNo2;	//人员2ID
	private String escortName2;	//人员2名称
	
	/** 追加机构 修改人：xl 修改时间：2017-12-29 */
	private Office office;
	/** end */
	
	
	public StoAddCashGroup() {
		super();
	}
	
	public StoAddCashGroup(String id) {
		super(id);
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCarId() {
		return carId;
	}
	public void setCarId(String carId) {
		this.carId = carId;
	}
	public String getCarName() {
		return carName;
	}
	public void setCarName(String carName) {
		this.carName = carName;
	}
	public String getEscortNo1() {
		return escortNo1;
	}
	public void setEscortNo1(String escortNo1) {
		this.escortNo1 = escortNo1;
	}
	public String getEscortName1() {
		return escortName1;
	}
	public void setEscortName1(String escortName1) {
		this.escortName1 = escortName1;
	}
	public String getEscortNo2() {
		return escortNo2;
	}
	public void setEscortNo2(String escortNo2) {
		this.escortNo2 = escortNo2;
	}
	public String getEscortName2() {
		return escortName2;
	}
	public void setEscortName2(String secortName2) {
		this.escortName2 = secortName2;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
}
