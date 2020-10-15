package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 整体存款情况分析报表Entity
 * 
 * @author lihe
 * @version 2020-01-06
 */
public class DepositCensusReport extends DataEntity<DepositCensusReport> {

	private static final long serialVersionUID = 1L;

	private String doorId; // 门店ID
	@ExcelField(title = "仓库", align = 2)
	private String doorName; // 门店名称
	private Office office; // 归属机构
	@ExcelField(title = "结算办", align = 2)
	private String settleOffice; // 结算办名称
	@ExcelField(title = "存款金额", align = 2)
	private BigDecimal totalAmount;// 存款总金额
	@ExcelField(title = "存款次数", align = 2)
	private String totalCount;// 存款总次数
	@ExcelField(title = "存款人数", align = 2)
	private String depositors;// 存款人数
	@ExcelField(title = "存款天数", align = 2)
	private String depositDays;// 存款天数
	@ExcelField(title = "金额", align = 2)
	private BigDecimal paperAmount; // 速存金额
	@ExcelField(title = "金额", align = 2)
	private BigDecimal forceAmount; // 强制金额
	private BigDecimal otherAmount; // 其他金额
	@ExcelField(title = "存款次数", align = 2)
	private String paperCount; // 速存存款次数
	@ExcelField(title = "存款次数", align = 2)
	private String forceCount; // 强制存款次数
	private String otherCount; // 其他存款次数
	@ExcelField(title = "占比", align = 2)
	private BigDecimal paperAmountPercent; // 速存金额占比
	@ExcelField(title = "占比", align = 2)
	private BigDecimal forceAmountPercent; // 强制金额占比
	private BigDecimal otherAmountPercent; // 其他金额占比
	@ExcelField(title = "占比", align = 2)
	private BigDecimal paperCountPercent; // 速存次数存款占比
	@ExcelField(title = "占比", align = 2)
	private BigDecimal forceCountPercent; // 强制次数存款占比
	private BigDecimal otherCountPercent;// 其他存款次数占比
	@ExcelField(title = "金额", align = 2)
	private BigDecimal aveAmount;// 平均每天存款金额
	@ExcelField(title = "次数", align = 2)
	private String aveCount; // 平均每天存款次数
	private String method; // 存款方式
	private List<String> methodList; // 存款方式列表
	/** 页面对应的添加时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的添加时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getSettleOffice() {
		return settleOffice;
	}

	public void setSettleOffice(String settleOffice) {
		this.settleOffice = settleOffice;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getDepositors() {
		return depositors;
	}

	public void setDepositors(String depositors) {
		this.depositors = depositors;
	}

	public String getDepositDays() {
		return depositDays;
	}

	public void setDepositDays(String depositDays) {
		this.depositDays = depositDays;
	}

	public BigDecimal getPaperAmount() {
		return paperAmount;
	}

	public void setPaperAmount(BigDecimal paperAmount) {
		this.paperAmount = paperAmount;
	}

	public BigDecimal getForceAmount() {
		return forceAmount;
	}

	public void setForceAmount(BigDecimal forceAmount) {
		this.forceAmount = forceAmount;
	}

	public BigDecimal getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(BigDecimal otherAmount) {
		this.otherAmount = otherAmount;
	}

	public String getPaperCount() {
		return paperCount;
	}

	public void setPaperCount(String paperCount) {
		this.paperCount = paperCount;
	}

	public String getForceCount() {
		return forceCount;
	}

	public void setForceCount(String forceCount) {
		this.forceCount = forceCount;
	}

	public String getOtherCount() {
		return otherCount;
	}

	public void setOtherCount(String otherCount) {
		this.otherCount = otherCount;
	}

	public BigDecimal getPaperAmountPercent() {
		return paperAmountPercent;
	}

	public void setPaperAmountPercent(BigDecimal paperAmountPercent) {
		this.paperAmountPercent = paperAmountPercent;
	}

	public BigDecimal getForceAmountPercent() {
		return forceAmountPercent;
	}

	public void setForceAmountPercent(BigDecimal forceAmountPercent) {
		this.forceAmountPercent = forceAmountPercent;
	}

	public BigDecimal getOtherAmountPercent() {
		return otherAmountPercent;
	}

	public void setOtherAmountPercent(BigDecimal otherAmountPercent) {
		this.otherAmountPercent = otherAmountPercent;
	}

	public BigDecimal getPaperCountPercent() {
		return paperCountPercent;
	}

	public void setPaperCountPercent(BigDecimal paperCountPercent) {
		this.paperCountPercent = paperCountPercent;
	}

	public BigDecimal getForceCountPercent() {
		return forceCountPercent;
	}

	public void setForceCountPercent(BigDecimal forceCountPercent) {
		this.forceCountPercent = forceCountPercent;
	}

	public BigDecimal getOtherCountPercent() {
		return otherCountPercent;
	}

	public void setOtherCountPercent(BigDecimal otherCountPercent) {
		this.otherCountPercent = otherCountPercent;
	}

	public BigDecimal getAveAmount() {
		return aveAmount;
	}

	public void setAveAmount(BigDecimal aveAmount) {
		this.aveAmount = aveAmount;
	}

	public String getAveCount() {
		return aveCount;
	}

	public void setAveCount(String aveCount) {
		this.aveCount = aveCount;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<String> getMethodList() {
		return methodList;
	}

	public void setMethodList(List<String> methodList) {
		this.methodList = methodList;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

}