package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

public class NowUsingDetailInfo extends DataEntity<NowUsingDetailInfo> {
	private static final long serialVersionUID = 1L;

	private String settlementBatch;// 结算批次
	private String saveBatch;// 存款批次
	private Date saveTime;// 存入时间
	private String userId;// 店员
	private String userName;// 店员姓名
	private String equipmentId;// 存款机ID
	private String area;// 区域
	private Date moenyDate;// 款项日期
	private String exlMoneyDate;//报表显示用款项日期
	private String businessType;// 业务类型
	private String moneyType;// 币种
	private Integer hundred;// 100
	private Integer fifty;// 50
	private Integer twenty;// 20
	private Integer ten;// 10
	private Integer five;// 5
	private Integer two;// 2
	private Integer one;// 1
	private Integer ofive;// 0.5
	private Integer otwo;// 0.2
	private Integer oone;// 0.1
	private Integer paperCount;// 纸币数量
	private BigDecimal paperAmount;// 纸币金额
	private BigDecimal forceAmount;// 强制金额
	private BigDecimal otherAmount;// 其他金额
	private BigDecimal totalAmount;// 总金额
	private String belongTo;// 所属公司
	private String orderId;// 订单编号

	private String bagNo;// 包号

	private Office aOffice; // 归属机构
	private Office vinOffice; // 维护机构ID
	private String pOfficeId; // 上级机构ID

	@ExcelField(title = "2", align = 2, sort = 10)
	public Integer getTwo() {
		return two;
	}

	public void setTwo(Integer two) {
		this.two = two;
	}

	@ExcelField(title = "0.2", align = 2, sort = 10)
	public Integer getOtwo() {
		return otwo;
	}

	public void setOtwo(Integer otwo) {
		this.otwo = otwo;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	@ExcelField(title = "0.5", align = 2, sort = 10)
	public Integer getOfive() {
		return ofive;
	}

	public void setOfive(Integer ofive) {
		this.ofive = ofive;
	}

	@ExcelField(title = "0.1", align = 2, sort = 10)
	public Integer getOone() {
		return oone;
	}

	public void setOone(Integer oone) {
		this.oone = oone;
	}

	@ExcelField(title = "5", align = 2, sort = 10)
	public Integer getFive() {
		return five;
	}

	public void setFive(Integer five) {
		this.five = five;
	}

	public String getBagNo() {
		return bagNo;
	}

	public void setBagNo(String bagNo) {
		this.bagNo = bagNo;
	}

	@ExcelField(title = "结算批次", align = 2, sort = 10)
	public String getSettlementBatch() {
		return settlementBatch;
	}

	public void setSettlementBatch(String settlementBatch) {
		this.settlementBatch = settlementBatch;
	}

	@ExcelField(title = "存款批次", align = 2, sort = 10)
	public String getSaveBatch() {
		return saveBatch;
	}

	public void setSaveBatch(String saveBatch) {
		this.saveBatch = saveBatch;
	}

	@ExcelField(title = "存入时间", align = 2, sort = 10)
	public Date getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(Date saveTime) {
		this.saveTime = saveTime;
	}

	@ExcelField(title = "店员", align = 2, sort = 10)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@ExcelField(title = "店员姓名", align = 2, sort = 10)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@ExcelField(title = "机具编号", align = 2, sort = 10)
	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	
	@ExcelField(title = "区域", align = 2, sort = 10)
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	
	public Date getMoenyDate() {
		return moenyDate;
	}

	public void setMoenyDate(Date moenyDate) {
		this.moenyDate = moenyDate;
	}
	
	@ExcelField(title = "款项日期", align = 2, sort = 10)
	public String getExlMoneyDate() {
		return exlMoneyDate;
	}

	public void setExlMoneyDate(String exlMoneyDate) {
		this.exlMoneyDate = exlMoneyDate;
	}

	@ExcelField(title = "业务类型", align = 2, sort = 10)
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@ExcelField(title = "币种", align = 2, sort = 10)
	public String getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	@ExcelField(title = "100", align = 2, sort = 10)
	public Integer getHundred() {
		return hundred;
	}

	public void setHundred(Integer hundred) {
		this.hundred = hundred;
	}

	@ExcelField(title = "50", align = 2, sort = 10)
	public Integer getFifty() {
		return fifty;
	}

	public void setFifty(Integer fifty) {
		this.fifty = fifty;
	}

	@ExcelField(title = "20", align = 2, sort = 10)
	public Integer getTwenty() {
		return twenty;
	}

	public void setTwenty(Integer twenty) {
		this.twenty = twenty;
	}

	@ExcelField(title = "10", align = 2, sort = 10)
	public Integer getTen() {
		return ten;
	}

	public void setTen(Integer ten) {
		this.ten = ten;
	}

	@ExcelField(title = "纸币数量", align = 2, sort = 10)
	public Integer getPaperCount() {
		return paperCount;
	}

	public void setPaperCount(Integer paperCount) {
		this.paperCount = paperCount;
	}

	@ExcelField(title = "纸币金额", align = 2, sort = 10)
	public BigDecimal getPaperAmount() {
		return paperAmount;
	}

	public void setPaperAmount(BigDecimal paperAmount) {
		this.paperAmount = paperAmount;
	}

	@ExcelField(title = "1", align = 2, sort = 10)
	public Integer getOne() {
		return one;
	}

	public void setOne(Integer one) {
		this.one = one;
	}

	@ExcelField(title = "强制金额", align = 2, sort = 10)
	public BigDecimal getForceAmount() {
		return forceAmount;
	}

	public void setForceAmount(BigDecimal forceAmount) {
		this.forceAmount = forceAmount;
	}
	
	@ExcelField(title = "其他金额", align = 2, sort = 10)
	public BigDecimal getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(BigDecimal otherAmount) {
		this.otherAmount = otherAmount;
	}


	@ExcelField(title = "总金额", align = 2, sort = 10)
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@ExcelField(title = "所属公司", align = 2, sort = 10)
	public String getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(String belongTo) {
		this.belongTo = belongTo;
	}

}
