package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 机具历史使用记录-存款明细Entity
 * 
 * @author lihe
 * @version 2019-11-01
 */
public class HistoryUseRecordsDetail extends DataEntity<HistoryUseRecordsDetail> {

	private static final long serialVersionUID = 1L;
	@ExcelField(title = "存款机ID", align = 2)
	private String equipmentId;
	private String equipmentName; // 机具名称
	// @ExcelField(value = "aoffice.name", title = "归属公司", align = 2)
	private Office aOffice; // 所属机构ID
	private Office vinOffice; // 维护机构ID
	private String pOfficeId; // 上级机构ID
	private String pOfficeName; // 上级机构名称
	private String saveMethodValue; // 存款方式键值
	@ExcelField(title = "业务类型", align = 2)
	private String saveMethod; // 存款方式
	@ExcelField(title = "币种", align = 2)
	private String currency = "cny"; // 币种
	// @ExcelField(title = "结算批次", align = 2)
	private String settleBatches; // 区域名称
	@ExcelField(title = "存款批次", align = 2)
	private String depositBatches; // 钞袋号
	@ExcelField(title = "存入时间", align = 2)
	private Date depositTime; // 存入时间
	@ExcelField(title = "款项日期", align = 2)
	private String exlDepositDate;	//导出用
	private Date depositDate; // 款项日期
	@ExcelField(title = "店员", align = 2)
	private String clerk; // 钞袋号
	@ExcelField(title = "店员姓名", align = 2)
	private String clerkName; // 钞袋号
	@ExcelField(title = "区域", align = 2)
	private String countyName; // 区域名称
	@ExcelField(title = "钞袋号", align = 2)
	private String bagNo; // 钞袋号
	@ExcelField(title = "纸币金额", align = 2)
	private BigDecimal paperAmount; // 纸币金额
	// @ExcelField(title = "硬币金额", align = 2)
	private BigDecimal coinAmount = new BigDecimal(0.00); // 硬币金额
	@ExcelField(title = "强制金额", align = 2)
	private BigDecimal forceAmount; // 强制金额
	@ExcelField(title = "其他金额", align = 2)
	private BigDecimal otherAmount; // 其他金额
	@ExcelField(title = "总金额", align = 2)
	private BigDecimal amount; // 总金额
	@ExcelField(title = "归属公司", align = 2)
	private String officeName; // 归属公司
	// 面值
	@ExcelField(title = "100元", align = 2)
	private Integer hundred;
	@ExcelField(title = "50元", align = 2)
	private Integer fifty;
	@ExcelField(title = "20元", align = 2)
	private Integer twenty;
	@ExcelField(title = "10元", align = 2)
	private Integer ten;
	@ExcelField(title = "5元", align = 2)
	private Integer five;
	@ExcelField(title = "1元", align = 2)
	private Integer one;
	private Integer two;
	private Integer fiveCorners;
	private Integer twoCorners;
	private Integer oneCorners;
	private String orderId; // 订单编号
	/** 页面对应的添加时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的添加时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSaveMethodValue() {
		return saveMethodValue;
	}

	public void setSaveMethodValue(String saveMethodValue) {
		this.saveMethodValue = saveMethodValue;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public Office getaOffice() {
		return aOffice;
	}

	public void setaOffice(Office aOffice) {
		this.aOffice = aOffice;
	}

	public Office getVinOffice() {
		return vinOffice;
	}

	public void setVinOffice(Office vinOffice) {
		this.vinOffice = vinOffice;
	}

	public String getpOfficeId() {
		return pOfficeId;
	}

	public void setpOfficeId(String pOfficeId) {
		this.pOfficeId = pOfficeId;
	}

	public String getpOfficeName() {
		return pOfficeName;
	}

	public void setpOfficeName(String pOfficeName) {
		this.pOfficeName = pOfficeName;
	}

	public String getSettleBatches() {
		return settleBatches;
	}

	public void setSettleBatches(String settleBatches) {
		this.settleBatches = settleBatches;
	}

	public String getDepositBatches() {
		return depositBatches;
	}

	public void setDepositBatches(String depositBatches) {
		this.depositBatches = depositBatches;
	}

	public Date getDepositTime() {
		return depositTime;
	}

	public void setDepositTime(Date depositTime) {
		this.depositTime = depositTime;
	}

	public String getClerk() {
		return clerk;
	}

	public void setClerk(String clerk) {
		this.clerk = clerk;
	}

	public String getClerkName() {
		return clerkName;
	}

	public void setClerkName(String clerkName) {
		this.clerkName = clerkName;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getBagNo() {
		return bagNo;
	}

	public void setBagNo(String bagNo) {
		this.bagNo = bagNo;
	}

	public BigDecimal getPaperAmount() {
		return paperAmount;
	}

	public void setPaperAmount(BigDecimal paperAmount) {
		this.paperAmount = paperAmount;
	}

	public BigDecimal getCoinAmount() {
		return coinAmount;
	}

	public void setCoinAmount(BigDecimal coinAmount) {
		this.coinAmount = coinAmount;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
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

	public String getSaveMethod() {
		return saveMethod;
	}

	public void setSaveMethod(String saveMethod) {
		this.saveMethod = saveMethod;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public Integer getHundred() {
		return hundred;
	}

	public void setHundred(Integer hundred) {
		this.hundred = hundred;
	}

	public Integer getFifty() {
		return fifty;
	}

	public void setFifty(Integer fifty) {
		this.fifty = fifty;
	}

	public Integer getTwenty() {
		return twenty;
	}

	public void setTwenty(Integer twenty) {
		this.twenty = twenty;
	}

	public Integer getTen() {
		return ten;
	}

	public void setTen(Integer ten) {
		this.ten = ten;
	}

	public Integer getFive() {
		return five;
	}

	public void setFive(Integer five) {
		this.five = five;
	}

	public Integer getOne() {
		return one;
	}

	public void setOne(Integer one) {
		this.one = one;
	}

	public Integer getTwo() {
		return two;
	}

	public void setTwo(Integer two) {
		this.two = two;
	}

	public Integer getFiveCorners() {
		return fiveCorners;
	}

	public void setFiveCorners(Integer fiveCorners) {
		this.fiveCorners = fiveCorners;
	}

	public Integer getTwoCorners() {
		return twoCorners;
	}

	public void setTwoCorners(Integer twoCorners) {
		this.twoCorners = twoCorners;
	}

	public Integer getOneCorners() {
		return oneCorners;
	}

	public void setOneCorners(Integer oneCorners) {
		this.oneCorners = oneCorners;
	}

	public String getExlDepositDate() {
		return exlDepositDate;
	}

	public void setExlDepositDate(String exlDepositDate) {
		this.exlDepositDate = exlDepositDate;
	}

}