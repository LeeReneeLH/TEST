package com.coffer.businesses.modules.weChat.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 预约金额明细
 * 
 * @author XL
 * @version 2019年7月23日
 */
public class DoorOrderAmount extends DataEntity<DoorOrderAmount> {

	private static final long serialVersionUID = 1L;
	private String typeId;// 类型明细编号
	private String detailId; // 预约明细编号
	private String currency; // 币种
	private String denomination; // 券别
	private String unitId; // 单位
	private String parValue;// 面值
	private String countZhang;// 张数
	private String detailAmount;// 明细金额
	private String typeName;// 类型明细名称
	private Integer rowNo;//序号

	public DoorOrderAmount() {
		super();
	}

	public DoorOrderAmount(String id) {
		super(id);
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getParValue() {
		return parValue;
	}

	public void setParValue(String parValue) {
		this.parValue = parValue;
	}

	public String getCountZhang() {
		return countZhang;
	}

	public void setCountZhang(String countZhang) {
		this.countZhang = countZhang;
	}

	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Integer getRowNo() {
		return rowNo;
	}

	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}
}