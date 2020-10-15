package com.coffer.businesses.modules.clear.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 面值列表Entity
 * 
 * @author wanglin
 * @version 2017-08-11
 */
public class DenominationInfo extends DataEntity<DenominationInfo> {

	private static final long serialVersionUID = 1L;
	/** 券别Key */
	private String moneyKey;
	/** 券别名 */
	private String moneyName;
	/** 券别值 */
	private String moneyValue;

	/** 数量列1 */
	private String columnValue1;

	/** 数量列2 */
	private String columnValue2;

	/**
	 * 数量列3
	 * 
	 * @author wxz
	 * @version 2017年8月25日
	 */
	private String columnValue3;

	/**
	 * 数量列4
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 */
	private String columnValue4;

	/** 总金额 */
	private String totalAmt;

	public DenominationInfo() {
		super();
	}

	public DenominationInfo(String id) {
		super(id);
	}

	public String getMoneyKey() {
		return moneyKey;
	}

	public void setMoneyKey(String moneyKey) {
		this.moneyKey = moneyKey;
	}

	public String getMoneyName() {
		return moneyName;
	}

	public void setMoneyName(String moneyName) {
		this.moneyName = moneyName;
	}

	public String getMoneyValue() {
		return moneyValue;
	}

	public void setMoneyValue(String moneyValue) {
		this.moneyValue = moneyValue;
	}

	public String getColumnValue1() {
		return columnValue1;
	}

	public void setColumnValue1(String columnValue1) {
		this.columnValue1 = columnValue1;
	}

	public String getColumnValue2() {
		return columnValue2;
	}

	public void setColumnValue2(String columnValue2) {
		this.columnValue2 = columnValue2;
	}

	public String getColumnValue3() {
		return columnValue3;
	}

	public void setColumnValue3(String columnValue3) {
		this.columnValue3 = columnValue3;
	}

	public String getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;

	}

	public String getColumnValue4() {
		return columnValue4;
	}

	public void setColumnValue4(String columnValue4) {
		this.columnValue4 = columnValue4;
	}

}