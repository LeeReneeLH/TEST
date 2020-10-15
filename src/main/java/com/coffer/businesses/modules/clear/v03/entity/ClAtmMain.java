package com.coffer.businesses.modules.clear.v03.entity;


import java.util.Date;
import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.DenominationInfo;
import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;
/**
 * ATM钞箱拆箱主表Entity
 * @author wanglin
 * @version 2017-02-13
 */
public class ClAtmMain extends DataEntity<ClAtmMain> {
	
	private static final long serialVersionUID = 1L;
	private String outNo;				// 拆箱单号
	private String custNo;				// 机构编号
	private String custName;			// 机构名称
	private String status;				// 
	private String inputAmount;			// 清机金额
	private String cashBoxCode;			// 钞箱编号
	private String checkAmount;			// 清点总金额
	private String diffAmount;			// 差额
	private String boxCount;			// 拆箱数量
	private Date regDate;				// 登记日期
	private String remarks;				// 备注
	private String updateCnt;			// 更新回数
	private String cashPlanId;			// 加钞计划ID
	
	private String[] amountInfo;		// 每笔金额信息（传参用）
	private String[] amountDetailInfo;	// 每笔金额对应面值信息（传参用）
	private String detailAmount; 		// 明细金额，画面显示用
	
	/** 面值列表 */
	private List<DenominationInfo> denominationList = Lists.newArrayList();
	
	/** 每笔列表 */
	private List<ClAtmAmount> amountList = Lists.newArrayList();
	
	/** 每笔面值列表 */
	private List<ClAtmDetail> amountDetailList = Lists.newArrayList();
	
	
	//检索用
	private String searchCustNo;		// 机构ID
	private String searchCustName;		// 机构名称
	private Date regTimeStart;
	private Date regTimeEnd;
	private String searchRegDateStart;	// 登记日期（开始）
	private String searchRegDateEnd;	// 登记日期（结束）
	private String searchClearManNo;	// 清分人

	public ClAtmMain() {
		super();
	}

	public ClAtmMain(String id){
		super(id);
	}


	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}



	public String getSearchClearManNo() {
		return searchClearManNo;
	}

	public void setSearchClearManNo(String searchClearManNo) {
		this.searchClearManNo = searchClearManNo;
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

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
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

	public String getBoxCount() {
		return boxCount;
	}

	public void setBoxCount(String boxCount) {
		this.boxCount = boxCount;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSearchCustNo() {
		return searchCustNo;
	}

	public void setSearchCustNo(String searchCustNo) {
		this.searchCustNo = searchCustNo;
	}

	public String getSearchCustName() {
		return searchCustName;
	}

	public void setSearchCustName(String searchCustName) {
		this.searchCustName = searchCustName;
	}

	public String getSearchRegDateStart() {
		return searchRegDateStart;
	}

	public void setSearchRegDateStart(String searchRegDateStart) {
		this.searchRegDateStart = searchRegDateStart;
	}

	public String getSearchRegDateEnd() {
		return searchRegDateEnd;
	}

	public void setSearchRegDateEnd(String searchRegDateEnd) {
		this.searchRegDateEnd = searchRegDateEnd;
	}

	public Date getRegTimeStart() {
		return regTimeStart;
	}

	public void setRegTimeStart(Date regTimeStart) {
		this.regTimeStart = regTimeStart;
	}

	public Date getRegTimeEnd() {
		return regTimeEnd;
	}

	public void setRegTimeEnd(Date regTimeEnd) {
		this.regTimeEnd = regTimeEnd;
	}


	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}


	public List<DenominationInfo> getDenominationList() {
		return denominationList;
	}

	public void setDenominationList(List<DenominationInfo> denominationList) {
		this.denominationList = denominationList;
	}


	public String getCashBoxCode() {
		return cashBoxCode;
	}

	public void setCashBoxCode(String cashBoxCode) {
		this.cashBoxCode = cashBoxCode;
	}

	public List<ClAtmAmount> getAmountList() {
		return amountList;
	}

	public void setAmountList(List<ClAtmAmount> amountList) {
		this.amountList = amountList;
	}

	public List<ClAtmDetail> getAmountDetailList() {
		return amountDetailList;
	}

	public void setAmountDetailList(List<ClAtmDetail> amountDetailList) {
		this.amountDetailList = amountDetailList;
	}

	public String getUpdateCnt() {
		return updateCnt;
	}

	public void setUpdateCnt(String updateCnt) {
		this.updateCnt = updateCnt;
	}

	public String getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(String inputAmount) {
		this.inputAmount = inputAmount;
	}

	public String getCashPlanId() {
		return cashPlanId;
	}

	public void setCashPlanId(String cashPlanId) {
		this.cashPlanId = cashPlanId;
	}

	public String[] getAmountInfo() {
		return amountInfo;
	}

	public void setAmountInfo(String[] amountInfo) {
		this.amountInfo = amountInfo;
	}

	public String[] getAmountDetailInfo() {
		return amountDetailInfo;
	}

	public void setAmountDetailInfo(String[] amountDetailInfo) {
		this.amountDetailInfo = amountDetailInfo;
	}

	
}