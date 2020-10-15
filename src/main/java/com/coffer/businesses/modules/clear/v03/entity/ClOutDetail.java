package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 商行取款Entity
 * 
 * @author wxz
 * @version 2017-08-24
 */
public class ClOutDetail extends DataEntity<ClOutDetail> {

	private static final long serialVersionUID = 1L;
	private String detailId; // 主键ID
	private String outNo; // 取款单号 父类
	private String currency; // 币种
	private String denomination; // 券别
	private String unitId; // 单位
	private String countWzq; // 完整券数
	private String countCsq; // 残损券数
	private String countDqf; // 待清分数
	private String countYqf; // 已清分数
	private String countAtm; // ATM数
	private String totalCount; // 总数
	private BigDecimal totalAmt; // 总金额
	private String moneyDqf; // 待清分金额(打印使用)
	private String moneyYqf; // 已清分金额(打印使用)
	private String moneyAtm; // ATM金额(打印使用)
	private String moneyWzq; // 完整券金额(打印使用)
	private String moneyCsq; // 残损券金额(打印使用)

	public String getMoneyWzq() {
		return moneyWzq;
	}

	public void setMoneyWzq(String moneyWzq) {
		this.moneyWzq = moneyWzq;
	}

	public String getMoneyCsq() {
		return moneyCsq;
	}

	public void setMoneyCsq(String moneyCsq) {
		this.moneyCsq = moneyCsq;
	}

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

	public String getMoneyAtm() {
		return moneyAtm;
	}

	public void setMoneyAtm(String moneyAtm) {
		this.moneyAtm = moneyAtm;
	}

	public ClOutDetail() {
		super();
	}

	public ClOutDetail(String id) {
		super(id);
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getOutNo() {
		return outNo;
	}

	public void setOutNo(String outNo) {
		this.outNo = outNo;
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

	public String getCountAtm() {
		return countAtm;
	}

	public void setCountAtm(String countAtm) {
		this.countAtm = countAtm;
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

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getCountWzq() {
		return countWzq;
	}

	public void setCountWzq(String countWzq) {
		this.countWzq = countWzq;
	}

	public String getCountCsq() {
		return countCsq;
	}

	public void setCountCsq(String countCsq) {
		this.countCsq = countCsq;
	}

}