package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 汇款时各门店实际金额Entity
 * 
 * @author wqj
 * @version 2019-07-10
 */
public class Store extends DataEntity<Store> {
	private String officeId; // 门店ID
	private BigDecimal amount; // 门店存款总金额
	private String sevenCode; //门店对应七位码

	public String getSevenCode() {
		return sevenCode;
	}

	public void setSevenCode(String sevenCode) {
		this.sevenCode = sevenCode;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
