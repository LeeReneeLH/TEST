package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.persistence.Page;

/**
 * 机构接口用Entity
 * 
 * @author zxk
 * @version 2019-08-26
 */
public class OfficeApp extends DataEntity<OfficeApp> {

	private static final long serialVersionUID = 1L;

	private String name; // 机构名称

	/** 实现商户对门店信息的一对多 ZXK 日期：2019-08-27 */
	private Page<OfficeApp> doorList;
	/** 机具连接类型列表(异常) **/
	private List<String> list;
	/** 机具连接状态 **/
	private String connStatus;
	/** 实现门店对机具信息的一对多 ZXK 日期：2019-08-20 */
	private List<EquipmentInfo> equipmentInfoList;

	private int eCount; // 商户下机具异常数量

	private int equipmentCount; // 门店下机具数量

	private int merchartEquiCount; // 商户机具总数量
	
	private String type; // 机具清机加钞状态(0 存款  1清机)
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getConnStatus() {
		return connStatus;
	}

	public void setConnStatus(String connStatus) {
		this.connStatus = connStatus;
	}

	public int getMerchartEquiCount() {
		return merchartEquiCount;
	}

	public void setMerchartEquiCount(int merchartEquiCount) {
		this.merchartEquiCount = merchartEquiCount;
	}

	public int getEquipmentCount() {
		return equipmentCount;
	}

	public void setEquipmentCount(int equipmentCount) {
		this.equipmentCount = equipmentCount;
	}

	public int geteCount() {
		return eCount;
	}

	public void seteCount(int eCount) {
		this.eCount = eCount;
	}

	public Page<OfficeApp> getDoorList() {
		return doorList;
	}

	public void setDoorList(Page<OfficeApp> doorList) {
		this.doorList = doorList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EquipmentInfo> getEquipmentInfoList() {
		return equipmentInfoList;
	}

	public void setEquipmentInfoList(List<EquipmentInfo> equipmentInfoList) {
		this.equipmentInfoList = equipmentInfoList;
	}

}
