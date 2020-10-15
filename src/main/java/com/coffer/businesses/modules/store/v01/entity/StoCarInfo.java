package com.coffer.businesses.modules.store.v01.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import com.coffer.core.modules.sys.entity.Office;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 车辆管理Entity
 * @author LLF
 * @version 2017-07-30
 */
public class StoCarInfo extends DataEntity<StoCarInfo> {
	
	private static final long serialVersionUID = 1L;
	private String carId;		// 车辆主键
	private String carNo;		// 车牌号码
	private String bindingFlag;		// 绑定线路状态（0：未绑定；1：已绑定）
	private Office office;		// 所属机构
	private List<String> officeList;	// 机构集合
	private String carHeader;    // 车牌开头
	
	/* 追加车辆颜色，型号  修改人:XL 修改日期:2018-09-06 begin */
	private String carColor;
	private String carType;
	/* end */
	
	public String getCarHeader() {
		return carHeader;
	}

	public void setCarHeader(String carHeader) {
		this.carHeader = carHeader;
	}

	public StoCarInfo() {
		super();
	}

	public StoCarInfo(String id){
		super(id);
	}

	public String getId() {
		return carId;
	}

	public void setId(String carId) {
		this.carId = carId;
	}
	
	@Length(min=0, max=20, message="车牌号码长度必须介于 0 和 20 之间")
	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	
	@Length(min=0, max=1, message="绑定线路状态（0：未绑定；1：已绑定）长度必须介于 0 和 1 之间")
	public String getBindingFlag() {
		return bindingFlag;
	}

	public void setBindingFlag(String bindingFlag) {
		this.bindingFlag = bindingFlag;
	}
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public List<String> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<String> officeList) {
		this.officeList = officeList;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}
	
	
}