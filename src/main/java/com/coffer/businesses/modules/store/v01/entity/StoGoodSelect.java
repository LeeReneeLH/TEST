package com.coffer.businesses.modules.store.v01.entity;

import java.io.Serializable;

public class StoGoodSelect implements Serializable {

	private static final long serialVersionUID = 8886937044379886460L;
	private String currency; // 币种
	private String classification; // 类别
	private String cash; // 软/硬币
	private String denomination; // 面值
	private String unit; // 单位
	private String edition; // 套别
	private Long moneyNumber; // 数量

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public Long getMoneyNumber() {
		return moneyNumber;
	}

	public void setMoneyNumber(Long moneyNumber) {
		this.moneyNumber = moneyNumber;
	}
}
