package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 缴存全景Entity
 * 
 * @author lihe
 * @version 2020-05-26
 */
public class DepositPanorama extends DataEntity<DepositPanorama> {

	private static final long serialVersionUID = 1L;
	private Office aOffice; // 所属机构
	private Office vinOffice; // 维护机构
	private Office office; // 当前机构
	@ExcelField(title = "区域", align = 2)
	private String countyName; // 区域名称
	@ExcelField(title = "钞袋号", align = 2)
	private String rfid; // 钞袋号
	@ExcelField(title = "纸币数量", align = 2)
	private Integer paperCount; // 纸币数量
	@ExcelField(title = "纸币金额", align = 2)
	private BigDecimal paperAmount; // 纸币金额
	// @ExcelField(title = "硬币数量", align = 2)
	private Integer coinCount = 0; // 硬币数量
	// @ExcelField(title = "硬币金额", align = 2)
	private BigDecimal coinAmount = new BigDecimal(0.00); // 硬币金额
	@ExcelField(title = "强制金额", align = 2)
	private BigDecimal forceAmount; // 强制金额
	@ExcelField(title = "其他金额", align = 2)
	private BigDecimal otherAmount; // 其他金额
	@ExcelField(title = "存入总量", align = 2)
	private Integer totalCount; // 存入总量
	@ExcelField(title = "总金额", align = 2)
	private BigDecimal totalAmount; // 总金额
	@ExcelField(title = "替换钞袋时间", align = 2)
	private Date changeDate; // 替换钞袋时间
	@ExcelField(title = "上次替换钞袋时间", align = 2)
	private Date lastChangeDate; // 上次替换钞袋时间
	private String orderId; // 订单编号
	/** 页面对应的添加时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的添加时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	@ExcelField(title = "存款机ID", align = 2)
	private String seriesNumber;

	private String doorId; // 门店Id
	private String doorName; // 门店名称
	/** 业务类型列表 修改人：XL 日期：2019-10-09 */
	private String busTypeList;
	private String equipmentId;
	private String equipmentName;

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

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public Integer getPaperCount() {
		return paperCount;
	}

	public void setPaperCount(Integer paperCount) {
		this.paperCount = paperCount;
	}

	public BigDecimal getPaperAmount() {
		return paperAmount;
	}

	public void setPaperAmount(BigDecimal paperAmount) {
		this.paperAmount = paperAmount;
	}

	public Integer getCoinCount() {
		return coinCount;
	}

	public void setCoinCount(Integer coinCount) {
		this.coinCount = coinCount;
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

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Date getLastChangeDate() {
		return lastChangeDate;
	}

	public void setLastChangeDate(Date lastChangeDate) {
		this.lastChangeDate = lastChangeDate;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

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

	public String getBusTypeList() {
		return busTypeList;
	}

	public void setBusTypeList(String busTypeList) {
		this.busTypeList = busTypeList;
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

}