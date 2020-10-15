package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 机具历史使用记录Entity
 * 
 * @author 机具历史使用记录
 * @version 2019-10-30
 */
public class HistoryUseRecords extends DataEntity<HistoryUseRecords> {

	private static final long serialVersionUID = 1L;
	@ExcelField(title = "存款机ID", align = 2)
	private String id;
	private String name; // 机具名称
	// @ExcelField(value = "aoffice.name", title = "归属公司", align = 2)
	private Office aOffice; // 所属机构ID
	private Office vinOffice; // 维护机构ID
	private String pOfficeId; // 上级机构ID
	private String pOfficeName; // 上级机构名称
	@ExcelField(title = "区域", align = 2)
	private String countyName; // 区域名称
	@ExcelField(title = "钞袋号", align = 2)
	private String bagNo; // 钞袋号
	// @ExcelField(title = "硬币盒号", align = 2)
	private String coinBoxNo; // 硬币盒号
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
	private Integer count; // 存入总量
	@ExcelField(title = "总金额", align = 2)
	private BigDecimal amount; // 总金额
	@ExcelField(title = "替换钞袋时间", align = 2)
	private Date changeDate; // 替换钞袋时间
	@ExcelField(title = "上次替换钞袋时间", align = 2)
	private Date lastChangeDate; // 上次替换钞袋时间
	@ExcelField(title = "归属公司", align = 2)
	private String officeName; // 归属公司
	private String orderId; // 订单编号
	/** 页面对应的添加时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的添加时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	@ExcelField(title = "存款机ID", align = 2)
	private String seriesNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getCoinBoxNo() {
		return coinBoxNo;
	}

	public void setCoinBoxNo(String coinBoxNo) {
		this.coinBoxNo = coinBoxNo;
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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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

}