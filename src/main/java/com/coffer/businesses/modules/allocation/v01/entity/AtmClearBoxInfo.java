package com.coffer.businesses.modules.allocation.v01.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
* Title: AtmClearBoxInfo 
* <p>Description: ATM清机钞箱拆箱信息</p>
* @author wangbaozhong
* @date 2017年11月17日 下午2:12:43
 */
public class AtmClearBoxInfo implements Serializable {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 钞箱编号 */
	private String boxNo;
	/** 钞箱中的现金量 */
	private BigDecimal amount;
	/** 拆箱时间 */
	private Date devanningDate;
	/**
	 * @return the boxNo
	 */
	public String getBoxNo() {
		return boxNo;
	}
	/**
	 * @param boxNo the boxNo to set
	 */
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the devanningDate
	 */
	public Date getDevanningDate() {
		return devanningDate;
	}
	/**
	 * @param devanningDate the devanningDate to set
	 */
	public void setDevanningDate(Date devanningDate) {
		this.devanningDate = devanningDate;
	}
	
	
	
}