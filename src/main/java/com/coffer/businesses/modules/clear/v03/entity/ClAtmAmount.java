package com.coffer.businesses.modules.clear.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * ATM钞箱拆箱每笔明细表Entity
 * @author wanglin
 * @version 2017-02-13
 */
public class ClAtmAmount extends DataEntity<ClAtmAmount> {
	
	private static final long serialVersionUID = 1L;
	private String outNo;					// 拆箱单号
	private String outRowNo;				// 明细序号
	private String checkAmount;				// 清点金额
	private String packNum;					// 包号
	


	public ClAtmAmount() {
		super();
	}

	public ClAtmAmount(String id){
		super(id);
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


	public String getCheckAmount() {
		return checkAmount;
	}

	public void setCheckAmount(String checkAmount) {
		this.checkAmount = checkAmount;
	}


	public String getPackNum() {
		return packNum;
	}

	public void setPackNum(String packNum) {
		this.packNum = packNum;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}




	
	
}