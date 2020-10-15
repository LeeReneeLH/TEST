package com.coffer.businesses.modules.clear.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 面值列表的控制参数Entity
 * 
 * @author wanglin
 * @version 2017-08-11
 */
public class DenominationCtrl extends DataEntity<DenominationCtrl> {

	private static final long serialVersionUID = 1L;
	/** 券别关联名称 */
	private String moneyKeyName;

	/** 数量列1关联名称 */
	private String columnName1;

	/** 数量列2关联名称 */
	private String columnName2;

	/**
	 * 数量列3关联名称
	 * 
	 * @author wxz
	 * @version 2017年8月25日
	 */
	private String columnName3;
	/**
	 * 数量列4关联名称
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 */
	private String columnName4;
	/** 合计关联名称 */
	private String totalAmtName;

	public DenominationCtrl() {
		super();
	}

	public DenominationCtrl(String id) {
		super(id);
	}

	public String getMoneyKeyName() {
		return moneyKeyName;
	}

	public void setMoneyKeyName(String moneyKeyName) {
		this.moneyKeyName = moneyKeyName;
	}

	public String getColumnName1() {
		return columnName1;
	}

	public void setColumnName1(String columnName1) {
		this.columnName1 = columnName1;
	}

	public String getColumnName2() {
		return columnName2;
	}

	public void setColumnName2(String columnName2) {
		this.columnName2 = columnName2;
	}

	public String getColumnName3() {
		return columnName3;
	}

	public void setColumnName3(String columnName3) {
		this.columnName3 = columnName3;
	}

	public String getTotalAmtName() {
		return totalAmtName;
	}

	public void setTotalAmtName(String totalAmtName) {
		this.totalAmtName = totalAmtName;
	}

	public String getColumnName4() {
		return columnName4;
	}

	public void setColumnName4(String columnName4) {
		this.columnName4 = columnName4;
	}

}