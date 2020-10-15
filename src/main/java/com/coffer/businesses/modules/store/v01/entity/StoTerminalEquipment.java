package com.coffer.businesses.modules.store.v01.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 终端设备管理Entity
 * @author yuxixuan
 * @version 2015-12-11
 */
public class StoTerminalEquipment extends DataEntity<StoTerminalEquipment> {
	
	private static final long serialVersionUID = 1L;
	private String teId;		// 设备编号
	private String teName;		// 设备名称
	private String teType;		// 设备类型
	private String teStatus;		// 设备状态
	private String dialId; // 拨号ID
	private String dialPwd; // 拨号密码
	private Office office;		// 所属机构
	/** 是否显示拨号ID和拨号密码 */
	private boolean displayDialFg;
	
	public StoTerminalEquipment() {
		super();
	}

	public StoTerminalEquipment(String id){
		super(id);
	}

	@Length(min=1, max=64, message="设备编号长度必须介于 1 和 64 之间")
	public String getId() {
		return teId;
	}

	public void setId(String teId) {
		this.teId = teId;
	}
	
	@Length(min=0, max=100, message="设备名称长度必须介于 0 和 100 之间")
	public String getTeName() {
		return teName;
	}

	public void setTeName(String teName) {
		this.teName = teName;
	}
	
	@Length(min=0, max=2, message="设备类型长度必须介于 0 和 2 之间")
	public String getTeType() {
		return teType;
	}

	public void setTeType(String teType) {
		this.teType = teType;
	}
	
	@Length(min = 0, max = 150, message = "拨号ID长度必须介于 0 和 50 之间")
	public String getDialId() {
		return dialId;
	}

	public void setDialId(String dialId) {
		this.dialId = dialId;
	}

	@Length(min = 0, max = 150, message = "拨号密码长度必须介于 0 和 50 之间")
	public String getDialPwd() {
		return dialPwd;
	}

	public void setDialPwd(String dialPwd) {
		this.dialPwd = dialPwd;
	}

	@Length(min=0, max=2, message="设备状态长度必须介于 0 和 2 之间")
	public String getTeStatus() {
		return teStatus;
	}

	public void setTeStatus(String teStatus) {
		this.teStatus = teStatus;
	}
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public boolean isDisplayDialFg() {
		return displayDialFg;
	}

	public void setDisplayDialFg(boolean displayDialFg) {
		this.displayDialFg = displayDialFg;
	}
}