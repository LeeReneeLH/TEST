package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

public class NowUsingInfo extends DataEntity<NowUsingInfo> {
	private static final long serialVersionUID = 1L;
	private String equipmentId;// 机具ID
	

	private Office aOffice; // 归属机构
	private String seriesNumber;//机具编号
	private Office vinOffice; // 维护机构ID
	private String pOfficeId; // 上级机构ID
	private String area;// 区域
	private String bagNo;// 钞袋号
	private String boxNo;// 硬币盒号
	private Integer banknoteCount;// 纸币数量
	private BigDecimal banknoteAmount;// 纸币金额
	private Integer coinCount = 0;// 硬币数量
	private BigDecimal coinAmount;// 硬币金额
	private BigDecimal forceAmount;// 强制金额
	private BigDecimal otherAmount;// 其他金额
	private Integer totalCount;// 存入总量
	private BigDecimal totalAmount;// 总金额
	private Date lastChangeTime;// 上次替换款袋时间
	private String belongTo;// 所属公司
	private String orderId;//订单编号
	
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@ExcelField(title = "区域", align = 2, sort = 10)
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	
	@ExcelField(title = "机具编号", align = 2, sort = 10)
	public String getSeriesNumber() {
		return seriesNumber;
	}
	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	@ExcelField(title = "钞袋号", align = 2, sort = 10)
	public String getBagNo() {
		return bagNo;
	}

	public void setBagNo(String bagNo) {
		this.bagNo = bagNo;
	}

	@ExcelField(title = "硬币盒号", align = 2, sort = 10)
	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	@ExcelField(title = "纸币数量", align = 2, sort = 10)
	public Integer getBanknoteCount() {
		return banknoteCount;
	}

	public void setBanknoteCount(Integer banknoteCount) {
		this.banknoteCount = banknoteCount;
	}

	@ExcelField(title = "纸币金额", align = 2, sort = 10)
	public BigDecimal getBanknoteAmount() {
		return banknoteAmount;
	}

	public void setBanknoteAmount(BigDecimal banknoteAmount) {
		this.banknoteAmount = banknoteAmount;
	}

	@ExcelField(title = "硬币数量", align = 2, sort = 10)
	public Integer getCoinCount() {
		return coinCount;
	}

	public void setCoinCount(Integer coinCount) {
		this.coinCount = coinCount;
	}

	@ExcelField(title = "硬币金额", align = 2, sort = 10)
	public BigDecimal getCoinAmount() {
		return coinAmount;
	}

	public void setCoinAmount(BigDecimal coinAmount) {
		this.coinAmount = coinAmount;
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
	
	@ExcelField(title = "存入总量", align = 2, sort = 10)
	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	@ExcelField(title = "总金额", align = 2, sort = 10)
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@ExcelField(title = "上次替换款袋时间", align = 2, sort = 10)
	public Date getLastChangeTime() {
		return lastChangeTime;
	}

	public void setLastChangeTime(Date lastChangeTime) {
		this.lastChangeTime = lastChangeTime;
	}

	@ExcelField(title = "所属公司", align = 2, sort = 10)
	public String getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(String belongTo) {
		this.belongTo = belongTo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
}
