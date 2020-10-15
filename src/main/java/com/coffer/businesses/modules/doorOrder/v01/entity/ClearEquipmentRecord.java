package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 清机记录（缴存）Entity
 *
 * @author gzd
 * @version 2020-06-03
 */
public class ClearEquipmentRecord extends DataEntity<ClearEquipmentRecord> {

	private static final long serialVersionUID = 1L;
	private Office aOffice; // 所属机构ID
	private Office vinOffice; // 维护机构ID

	@ExcelField(title = "区域", align = 3)
	private String area;  // 区域
	@ExcelField(title = "所属公司", align = 3, sort = 20)
	private String doorName;  // 门店名称
	private String doorId;  // 门店Id
	@ExcelField(title = "机具编号", align = 2, sort = 50)
	private String seriesNumber;  // 机具序列号
	private String equipmentId;  // 机具编号
	@ExcelField(title = "款袋编号", align = 3, sort = 20)
	private String bagNo;  // 款袋编号
	@ExcelField(title = "装袋批次", align = 3, sort = 20)
	private String batchNo; // 装袋批次
	@ExcelField(title = "速存张数", align = 3, sort = 20)
	private int paperCount; //速存张数
	@ExcelField(title = "速存金额", align = 3, sort = 20)
	private BigDecimal paperAmount;  // 速存金额
	@ExcelField(title = "强制金额", align = 3, sort = 20)
	private BigDecimal forceAmount;  // 强制金额
	@ExcelField(title = "其他金额", align = 3, sort = 20)
	private BigDecimal otherAmount;  // 其他金额
	@ExcelField(title = "总金额", align = 3, sort = 20)
	private BigDecimal totalAmount;  // 总金额
	@ExcelField(title = "装袋时间", align = 3, sort = 20)
	private Date beforeDate;  // 装袋时间
	@ExcelField(title = "卸袋时间", align = 3, sort = 20)
	private Date afterDate;  // 卸袋时间
	@ExcelField(title = "拆箱单号", align = 3, sort = 20)
	private String outNo;  // 拆箱单号
	@ExcelField(title = "清机员", align = 3, sort = 20)
	private String payPeople;  // 清机员
	@ExcelField(title = "状态", align = 3, sort = 20)
	private String status;  // 状态
	@ExcelField(title = "更新人", align = 3, sort = 20)
	private String updateName;  // 更新人
	@ExcelField(title = "更新时间", align = 3, sort = 20)
	private Date updateDate;  // 更新时间
	private String beforeAmount;  // 起始金额
	private String afterAmount;  // 结束金额
	private String tickerTape;  // 凭条号
	private String remarks;  // 业务备注
	private String depositMan;  // 存款人
	private String errorType;  // 差错类型
	/** 开始时间和结束时间（查询用） */
	private String searchDateStart;
	private String searchDateEnd;
	/** 页面对应的开发时间和结束时间（查询用） */
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 钞袋使用时间（导出用） */
	@ExcelField(title = "钞袋使用时间", align = 3, sort = 20)
	private String useDate;
	
	public String getUseDate() {
		return useDate;
	}
	public void setUseDate(String useDate) {
		this.useDate = useDate;
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
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getDoorName() {
		return doorName;
	}
	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}
	public String getDoorId() {
		return doorId;
	}
	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}
	public String getSeriesNumber() {
		return seriesNumber;
	}
	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	public String getEquipmentId() {
		return equipmentId;
	}
	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	public String getBagNo() {
		return bagNo;
	}
	public void setBagNo(String bagNo) {
		this.bagNo = bagNo;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public int getPaperCount() {
		return paperCount;
	}
	public void setPaperCount(int paperCount) {
		this.paperCount = paperCount;
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
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Date getBeforeDate() {
		return beforeDate;
	}
	public void setBeforeDate(Date beforeDate) {
		this.beforeDate = beforeDate;
	}
	public Date getAfterDate() {
		return afterDate;
	}
	public void setAfterDate(Date afterDate) {
		this.afterDate = afterDate;
	}
	public String getOutNo() {
		return outNo;
	}
	public void setOutNo(String outNo) {
		this.outNo = outNo;
	}
	public String getPayPeople() {
		return payPeople;
	}
	public void setPayPeople(String payPeople) {
		this.payPeople = payPeople;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getBeforeAmount() {
		return beforeAmount;
	}
	public void setBeforeAmount(String beforeAmount) {
		this.beforeAmount = beforeAmount;
	}
	public String getAfterAmount() {
		return afterAmount;
	}
	public void setAfterAmount(String afterAmount) {
		this.afterAmount = afterAmount;
	}
	public String getTickerTape() {
		return tickerTape;
	}
	public void setTickerTape(String tickerTape) {
		this.tickerTape = tickerTape;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDepositMan() {
		return depositMan;
	}
	public void setDepositMan(String depositMan) {
		this.depositMan = depositMan;
	}
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
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
		
}