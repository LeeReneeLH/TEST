package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 商行交款明细 Entity
 * 
 * @author wanglin
 * @version 2017-08-02
 */
public class ClInDetail extends DataEntity<ClInDetail> {

	private static final long serialVersionUID = 1L;
	/** 主键ID */
	private String detailId;
	/** 交款单号 */
	private String inNo;
	/** 币种 */
	private String currency;
	/* 修改属性改为denomination 修改人：xl 修改时间：2017-8-29 begin */
	/** 面值 */
	private String denomination;
	/* end */
	/** 单位 */
	private String unitId;
	/** 待清分数 */
	private String countDqf;
	/** 已清分数 */
	private String countYqf;
	/** 总数 */
	private String totalCount;
	/** 总金额 */
	private BigDecimal totalAmt;
	/** 备注 */
	private String remarks;

	private String moneyDqf; // 待清分金额(打印使用)
	private String moneyYqf; // 已清分金额(打印使用)

	public String getMoneyDqf() {
		return moneyDqf;
	}

	public void setMoneyDqf(String moneyDqf) {
		this.moneyDqf = moneyDqf;
	}

	public String getMoneyYqf() {
		return moneyYqf;
	}

	public void setMoneyYqf(String moneyYqf) {
		this.moneyYqf = moneyYqf;
	}

	public ClInDetail() {
		super();
	}

	public ClInDetail(String id) {
		super(id);
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getInNo() {
		return inNo;
	}

	public void setInNo(String inNo) {
		this.inNo = inNo;
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

	public String getCountDqf() {
		return countDqf;
	}

	public void setCountDqf(String countDqf) {
		this.countDqf = countDqf;
	}

	public String getCountYqf() {
		return countYqf;
	}

	public void setCountYqf(String countYqf) {
		this.countYqf = countYqf;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
