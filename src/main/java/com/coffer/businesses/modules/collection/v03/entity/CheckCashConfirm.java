package com.coffer.businesses.modules.collection.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 款箱拆箱每笔确认Entity
 * @author wanglin
 * @version 2017-02-13
 */
public class CheckCashConfirm extends DataEntity<CheckCashConfirm> {
	
	private static final long serialVersionUID = 1L;
	private String outNo;					// 拆箱单号
	private String custNo;					// 客户CD
	private String AmountId;				// 每笔ID
	private String inputAmount;				// 每笔录入金额
	private String payValueJoin;			// 每笔面值拼接（券别-张数）
	private String remarks;					// 备注
	private String authUserId;				// 授权用户ID
	private String chkUpdateCnt;			// 更新回数（多用户冲突用）
	
	private String newOutNo;				// 拆箱单号(新)
	private String newAmountId;				// 每笔ID(新)
	
	public CheckCashConfirm() {
		super();
	}

	public CheckCashConfirm(String id){
		super(id);
	}

	public String getOutNo() {
		return outNo;
	}

	public void setOutNo(String outNo) {
		this.outNo = outNo;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getAmountId() {
		return AmountId;
	}

	public void setAmountId(String amountId) {
		AmountId = amountId;
	}

	public String getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(String inputAmount) {
		this.inputAmount = inputAmount;
	}

	public String getPayValueJoin() {
		return payValueJoin;
	}

	public void setPayValueJoin(String payValueJoin) {
		this.payValueJoin = payValueJoin;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAuthUserId() {
		return authUserId;
	}

	public void setAuthUserId(String authUserId) {
		this.authUserId = authUserId;
	}

	public String getNewOutNo() {
		return newOutNo;
	}

	public void setNewOutNo(String newOutNo) {
		this.newOutNo = newOutNo;
	}

	public String getNewAmountId() {
		return newAmountId;
	}

	public void setNewAmountId(String newAmountId) {
		this.newAmountId = newAmountId;
	}

	public String getChkUpdateCnt() {
		return chkUpdateCnt;
	}

	public void setChkUpdateCnt(String chkUpdateCnt) {
		this.chkUpdateCnt = chkUpdateCnt;
	}




	
}