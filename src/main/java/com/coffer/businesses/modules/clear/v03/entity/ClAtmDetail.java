package com.coffer.businesses.modules.clear.v03.entity;


import com.coffer.core.common.persistence.DataEntity;

/**
 * ATM钞箱拆箱每笔面值明细表Entity
 * @author wanglin
 * @version 2017-02-13
 */
public class ClAtmDetail extends DataEntity<ClAtmDetail> {
	
	private static final long serialVersionUID = 1L;
	private String outNo;					// 拆箱单号
	private String outRowNo;				// 明细序号
	private String currency;				// 币种
	private String denomination;			// 券别
	private String unitId;					// 单位
	private String countZhang;				// 张数
	private String detailAmount;			// 明细金额
	private String parValue;				// 面值
	
	

	public ClAtmDetail() {
		super();
	}

	public ClAtmDetail(String id){
		super(id);
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

	public String getOutNo() {
		return outNo;
	}

	public void setOutNo(String outNo) {
		this.outNo = outNo;
	}

	public String getOutRowNo() {
		return outRowNo;
	}

	public void setOutRowNo(String outRowNo) {
		this.outRowNo = outRowNo;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}


	public String getCountZhang() {
		return countZhang;
	}

	public void setCountZhang(String countZhang) {
		this.countZhang = countZhang;
	}

	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}

	public String getParValue() {
		return parValue;
	}

	public void setParValue(String parValue) {
		this.parValue = parValue;
	}


	
	
}