package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 物品字典Entity
 * @author Ray
 * @version 2015-09-08
 */
public class StoDict extends DataEntity<StoDict> {
	
	private static final long serialVersionUID = 1L;
	private String label;		// 标签名
	private String value;		// 数据值
	private String type;		// 类型
	private String description;		// 描述
	private Long sort; // 排序（升序）
	private BigDecimal unitVal; // 单位价值
	private String refCode; // 关联代码
	
	public StoDict() {
		super();
	}

	public StoDict(String id){
		super(id);
	}

	@Length(min=0, max=100, message="标签名长度必须介于 0 和 100 之间")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Length(min=0, max=3, message="数据值长度必须介于 0 和 3 之间")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Length(min=0, max=100, message="类型长度必须介于 0 和 100 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=100, message="描述长度必须介于 0 和 100 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}

	public BigDecimal getUnitVal() {
		return unitVal;
	}

	public void setUnitVal(BigDecimal unitVal) {
		this.unitVal = unitVal;
	}

	@Length(min = 0, max = 10, message = "关联代码长度必须介于 0 和 10 之间")
	public String getRefCode() {
		return refCode;
	}

	public void setRefCode(String refCode) {
		this.refCode = refCode;
	}
}