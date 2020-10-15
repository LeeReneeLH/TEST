package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Map;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 存款异常明细Entity
 * 
 * @author zxk
 * @version 2019-11-12
 */

public class DoorOrderExceptionDetail extends DataEntity<DoorOrderExceptionDetail>{

	private static final long serialVersionUID = 1L;
	private String detailId; //主键
	private String id; //主表id
	private String type; //存款方式
	private String count; //张数
	private String denomination; //面值
	private BigDecimal amount; //金额
	private String currency; //币种
	private Integer rowNo;//序号
	
	/* 接口用 */
	private Map<String, DoorOrderExceptionDetail> map01; //存款方式为 01 现钞
	
	private Map<String, DoorOrderExceptionDetail> map02; //存款方式为 02 钞袋
	
	

	public Map<String, DoorOrderExceptionDetail> getMap01() {
		return map01;
	}
	public void setMap01(Map<String, DoorOrderExceptionDetail> map01) {
		this.map01 = map01;
	}
	public Map<String, DoorOrderExceptionDetail> getMap02() {
		return map02;
	}
	public void setMap02(Map<String, DoorOrderExceptionDetail> map02) {
		this.map02 = map02;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Integer getRowNo() {
		return rowNo;
	}
	public void setRowNo(Integer rowNo) {
		this.rowNo = rowNo;
	}
	public String getDetailId() {
		return detailId;
	}
	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getDenomination() {
		return denomination;
	}
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
	
}
