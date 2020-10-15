package com.coffer.businesses.modules.collection.v03.entity;

import java.util.Date;
import java.util.List;

import com.coffer.businesses.modules.clear.v03.entity.DenominationInfo;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 款箱拆箱主表Entity
 * 
 * @author wanglin
 * @version 2017-02-13
 */
public class CheckCashMain extends DataEntity<CheckCashMain> {

	private static final long serialVersionUID = 1L;
	private String outNo; // 拆箱单号
	private String custNo; // 客户编号
	private String custName; // 客户名称
	private String status; //
	private String inputAmount; // 拆箱总金额
	private String checkAmount; // 清点总金额
	private String diffAmount; // 差额
	private String boxCount; // 拆箱数量
	private Date regDate; // 登记日期
	private String dataFlag; // 数据区分（0：录入 1：分配）
	//private String remarks; // 备注     hzy  2020/06/24
	private String noBoxCount; // 未拆箱数量
	private String curUserType; // 用户类型(当前用户)
	private String curUserId; // 用户ID(当前用户)
	private String clearOpt; // 清分中心操作员区分
	private String updateCnt; // 更新回数

	private String detailAmount; // 明细金额，画面显示用

	/** add by lihe 2020-03-11 start */
	private Date lastTime; // 上次清机时间
	private Date thisTime; // 本次清机时间
	private String tickertape; // 凭条号
	/** add by lihe 2020-03-11 end */
	
	/** add by gj 2020-08-27 start */
	private String errorStatus; // 凭条号
	/** add by gj 2020-08-27 end */

	/** 面值（纸币）列表 */
	private List<DenominationInfo> denominationList = Lists.newArrayList();

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	/** 每笔列表 */
	private List<CheckCashAmount> amountList = Lists.newArrayList();

	/** 每笔面值列表 */
	private List<CheckCashDetail> amountDetailList = Lists.newArrayList();

	// 检索用
	private String searchCustNo; // 门店ID
	private String searchCustName; // 门店名称
	private Date regTimeStart;
	private Date regTimeEnd;
	private String searchRegDateStart; // 登记日期（开始）
	private String searchRegDateEnd; // 登记日期（结束）
	private String uninitDateFlag; // 非初始化时间标记
	private String searchClearManNo; // 清分人
	private String oRemarks; // 存款备注
	private String checked; // 已拆未拆
	private String[] checkedMulti; // 已拆未拆(多个状态，[door_order_info]的钞袋使用状态）

	/** 金额列表（页面传值用） 修改人：XL 日期：2019-07-11 */
	private String amountListStr;

	/** 授权用户编号 修改人：XL 日期：2019-07-11 */
	private String authUserId;

	/** 清分中心 修改人：XL 日期：2019-07-12 */
	private Office office;

	/** 款袋编号 修改人：XL 日期：2019-07-12 */
	private String rfid;

	/** 面值（硬币）列表 修改人：XL 日期：2019-08-06 */
	private List<DenominationInfo> cnyhdenList = Lists.newArrayList();

	/** 存款差错金额 修改人：WQJ 日期：2019-10-23 */
	private String saveErrorMoney;

	/** 实际存款金额 修改人：WQJ 日期：2019-10-23 */
	private String trueSumMoney;

	/** 本次清机时间 修改人：GJ 日期：2020-03-05 */
	private Date currentClearDate;

	/** 上次清机时间 修改人：GJ 日期：2020-03-05 */
	private Date lastClearDate;

	/** 款袋使用时间 修改人：GJ 日期：2020-03-05 */
	private String packNumUseTime;

	/** 机具Id 修改人：GJ 日期：2020-03-05 */
	private String equipmentId;

	public String getSaveErrorMoney() {
		return saveErrorMoney;
	}

	public void setSaveErrorMoney(String saveErrorMoney) {
		this.saveErrorMoney = saveErrorMoney;
	}

	public String getTrueSumMoney() {
		return trueSumMoney;
	}

	public void setTrueSumMoney(String trueSumMoney) {
		this.trueSumMoney = trueSumMoney;
	}

	public CheckCashMain() {
		super();
	}

	public CheckCashMain(String id) {
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

	public String getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(String inputAmount) {
		this.inputAmount = inputAmount;
	}

	public List<CheckCashAmount> getAmountList() {
		return amountList;
	}

	public void setAmountList(List<CheckCashAmount> amountList) {
		this.amountList = amountList;
	}

	public List<CheckCashDetail> getAmountDetailList() {
		return amountDetailList;
	}

	public void setAmountDetailList(List<CheckCashDetail> amountDetailList) {
		this.amountDetailList = amountDetailList;
	}

	public String getNoBoxCount() {
		return noBoxCount;
	}

	public void setNoBoxCount(String noBoxCount) {
		this.noBoxCount = noBoxCount;
	}

	public String getCurUserType() {
		return curUserType;
	}

	public void setCurUserType(String curUserType) {
		this.curUserType = curUserType;
	}

	public String getCurUserId() {
		return curUserId;
	}

	public void setCurUserId(String curUserId) {
		this.curUserId = curUserId;
	}

	public String getClearOpt() {
		return clearOpt;
	}

	public void setClearOpt(String clearOpt) {
		this.clearOpt = clearOpt;
	}

	public String getUpdateCnt() {
		return updateCnt;
	}

	public void setUpdateCnt(String updateCnt) {
		this.updateCnt = updateCnt;
	}

	public String getAmountListStr() {
		return amountListStr;
	}

	public void setAmountListStr(String amountListStr) {
		this.amountListStr = amountListStr;
	}

	public String getAuthUserId() {
		return authUserId;
	}

	public void setAuthUserId(String authUserId) {
		this.authUserId = authUserId;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public List<DenominationInfo> getCnyhdenList() {
		return cnyhdenList;
	}

	public void setCnyhdenList(List<DenominationInfo> cnyhdenList) {
		this.cnyhdenList = cnyhdenList;
	}

	public String getoRemarks() {
		return oRemarks;
	}

	public void setoRemarks(String oRemarks) {
		this.oRemarks = oRemarks;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String[] getCheckedMulti() {
		return checkedMulti;
	}

	public void setCheckedMulti(String[] checkedMulti) {
		this.checkedMulti = checkedMulti;
	}

	public Date getCurrentClearDate() {
		return currentClearDate;
	}

	public void setCurrentClearDate(Date currentClearDate) {
		this.currentClearDate = currentClearDate;
	}

	public Date getLastClearDate() {
		return lastClearDate;
	}

	public void setLastClearDate(Date lastClearDate) {
		this.lastClearDate = lastClearDate;
	}

	public String getPackNumUseTime() {
		return packNumUseTime;
	}

	public void setPackNumUseTime(String packNumUseTime) {
		this.packNumUseTime = packNumUseTime;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public Date getThisTime() {
		return thisTime;
	}

	public void setThisTime(Date thisTime) {
		this.thisTime = thisTime;
	}

	public String getTickertape() {
		return tickertape;
	}

	public void setTickertape(String tickertape) {
		this.tickertape = tickertape;
	}

	public String getUninitDateFlag() {
		return uninitDateFlag;
	}

	public void setUninitDateFlag(String uninitDateFlag) {
		this.uninitDateFlag = uninitDateFlag;
	}

}