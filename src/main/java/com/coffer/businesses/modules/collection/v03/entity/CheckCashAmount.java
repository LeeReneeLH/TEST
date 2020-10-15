package com.coffer.businesses.modules.collection.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 款箱拆箱每笔明细表Entity
 * @author wanglin
 * @version 2017-02-13
 */
public class CheckCashAmount extends DataEntity<CheckCashAmount> {
	
	private static final long serialVersionUID = 1L;
	private String outNo;					// 拆箱单号
	private String outRowNo;				// 明细序号
	private String detailId;				// 存款批次ID
	private String inputAmount;				// 录入金额
	private String checkAmount;				// 清点金额
	private String diffAmount;				// 差额
	private String confirmUserId;			// 确认人ID
	private String confirmUserNm;			// 确认人名
	private String authorizeUserId;			// 授权人ID
	private String authorizeUserNm;			// 授权人名
	private String packNum;					// 包号
	private String dataFlag;				// 数据区分（0：录入 1：分配）
	private String enabledFlag;				// 启用标识（1：启用 0 ：未启用）
	private String custName;				// 客户名称（门店）

	// 拆箱每笔存款要显示存款备注
	private String oRemarks;				// 门店存款备注
	


	public CheckCashAmount() {
		super();
	}

	public CheckCashAmount(String id){
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

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(String inputAmount) {
		this.inputAmount = inputAmount;
	}

	public String getCheckAmount() {
		return checkAmount;
	}

	public void setCheckAmount(String checkAmount) {
		this.checkAmount = checkAmount;
	}

	public String getDiffAmount() {
		return diffAmount;
	}

	public void setDiffAmount(String diffAmount) {
		this.diffAmount = diffAmount;
	}

	public String getConfirmUserNm() {
		return confirmUserNm;
	}

	public void setConfirmUserNm(String confirmUserNm) {
		this.confirmUserNm = confirmUserNm;
	}

	public String getAuthorizeUserId() {
		return authorizeUserId;
	}

	public void setAuthorizeUserId(String authorizeUserId) {
		this.authorizeUserId = authorizeUserId;
	}

	public String getAuthorizeUserNm() {
		return authorizeUserNm;
	}

	public void setAuthorizeUserNm(String authorizeUserNm) {
		this.authorizeUserNm = authorizeUserNm;
	}

	public String getPackNum() {
		return packNum;
	}

	public void setPackNum(String packNum) {
		this.packNum = packNum;
	}

	public String getDataFlag() {
		return dataFlag;
	}

	public void setDataFlag(String dataFlag) {
		this.dataFlag = dataFlag;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getEnabledFlag() {
		return enabledFlag;
	}

	public void setEnabledFlag(String enabledFlag) {
		this.enabledFlag = enabledFlag;
	}

	public String getConfirmUserId() {
		return confirmUserId;
	}

	public void setConfirmUserId(String confirmUserId) {
		this.confirmUserId = confirmUserId;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getoRemarks() {
		return oRemarks;
	}

	public void setoRemarks(String oRemarks) {
		this.oRemarks = oRemarks;
	}
}