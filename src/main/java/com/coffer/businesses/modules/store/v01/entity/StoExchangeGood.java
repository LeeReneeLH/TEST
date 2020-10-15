package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 兑换详细Entity
 * 
 * @author niguoyong
 * @version 2015年9月24日
 */
public class StoExchangeGood extends DataEntity<StoExchangeGood>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3856393986235659395L;
	
	private String detailId; // 详细ID
	private String goodsId; // 物品ID
	private String currency; // 币种
	private String classification; // 类别
	private String cash; // 软/硬币
	private String denomination; // 面值
	private String unit; // 单位
	private String edition; // 套别
	private Long num; // 数量
	private BigDecimal moneyAmount= new BigDecimal(0); // 金额
	/**
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency 要设置的 currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return classification
	 */
	public String getClassification() {
		return classification;
	}
	/**
	 * @param classification 要设置的 classification
	 */
	public void setClassification(String classification) {
		this.classification = classification;
	}
	/**
	 * @return cash
	 */
	public String getCash() {
		return cash;
	}
	/**
	 * @param cash 要设置的 cash
	 */
	public void setCash(String cash) {
		this.cash = cash;
	}
	/**
	 * @return denomination
	 */
	public String getDenomination() {
		return denomination;
	}
	/**
	 * @param denomination 要设置的 denomination
	 */
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	/**
	 * @return unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit 要设置的 unit
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * @return edition
	 */
	public String getEdition() {
		return edition;
	}
	/**
	 * @param edition 要设置的 edition
	 */
	public void setEdition(String edition) {
		this.edition = edition;
	}
	/**
	 * @return moneyNumber
	 */
	public Long getNum() {
		return num;
	}
	/**
	 * @param moneyNumber 要设置的 moneyNumber
	 */
	public void setNum(Long num) {
		this.num = num;
	}
	/**
	 * @return moneyAmount
	 */
	public BigDecimal getMoneyAmount() {
		return moneyAmount;
	}
	/**
	 * @param moneyAmount 要设置的 moneyAmount
	 */
	public void setMoneyAmount(BigDecimal moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
	/**
	 * @return goodsId
	 */
	public String getGoodsId() {
		return goodsId;
	}
	/**
	 * @param goodsId 要设置的 goodsId
	 */
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	/**
	 * @return detailId
	 */
	public String getDetailId() {
		return detailId;
	}
	/**
	 * @param detailId 要设置的 detailId
	 */
	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
}
