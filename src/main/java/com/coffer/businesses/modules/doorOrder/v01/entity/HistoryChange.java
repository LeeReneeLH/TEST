package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 历史更换记录Entity
 *
 * @author ZXK
 * @version 2019-10-31
 */
public class HistoryChange extends DataEntity<HistoryChange> {

	private static final long serialVersionUID = 1L;
	private Office aOffice; // 所属机构ID
	private Office vinOffice; // 维护机构ID
	/** 存款机id */
	private String equipmentId;
	/** 区域 */
	@ExcelField(title = "区域", align = 3)
	private String area;
	/** 上一钞袋号 */
	private String lastBagNo;
	/** 钞袋号 */
	private String bagNo;
	/** 批次号(数量) */
	private String batchNo;
	/** 解款员(清机员) */
	private String payPeople;
	/** 替换钞袋时间(清机) */
	private Date changeDate;
	/** 纸币张数 */
	@ExcelField(title = "纸币数量", align = 3, sort = 20)
	private String paperMoneyCount;
	/** 纸币金额 */
	@ExcelField(title = "纸币金额", align = 3, sort = 20)
	private BigDecimal paperMoney;
	/** 强制金额 */
	@ExcelField(title = "强制金额", align = 3, sort = 20)
	private BigDecimal comperMoney;
	/** 其他金额 */
	@ExcelField(title = "其他金额", align = 3, sort = 20)
	private BigDecimal otherMoney;
	/** 数量 */
	@ExcelField(title = "数量", align = 3, sort = 20)
	private Integer count;
	/** 金额 */
	@ExcelField(title = "金额", align = 3, sort = 20)
	private BigDecimal amount;
	/** 归属公司 */
	@ExcelField(title = "归属公司", align = 3, sort = 20)
	private String doorName;
	/** 归属公司编号 */
	private String doorId;

	/** 开始时间和结束时间（查询用） */
	private String searchDateStart;
	private String searchDateEnd;
	/** 页面对应的开发时间和结束时间（查询用） */
	private Date createTimeStart;
	private Date createTimeEnd;
	
	/** 存款机id */
	@ExcelField(title = "存款机ID", align = 2, sort = 50)
	private String seriesNumber;

	public BigDecimal getOtherMoney() {
		return otherMoney;
	}

	public void setOtherMoney(BigDecimal otherMoney) {
		this.otherMoney = otherMoney;
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

	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@ExcelField(title = "前一钞袋号", align = 2, sort = 50)
	public String getLastBagNo() {
		return lastBagNo;
	}

	public void setLastBagNo(String lastBagNo) {
		this.lastBagNo = lastBagNo;
	}

	@ExcelField(title = "钞袋号", align = 2, sort = 50)
	public String getBagNo() {
		return bagNo;
	}

	public void setBagNo(String bagNo) {
		this.bagNo = bagNo;
	}

	@ExcelField(title = "批次号", align = 2, sort = 50)
	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	@ExcelField(title = "CIT-解款员", align = 2, sort = 20)
	public String getPayPeople() {
		return payPeople;
	}

	public void setPayPeople(String payPeople) {
		this.payPeople = payPeople;
	}

	@ExcelField(title = "替换钞袋时间", type = 1, align = 1, sort = 100)
	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public String getPaperMoneyCount() {
		return paperMoneyCount;
	}

	public void setPaperMoneyCount(String paperMoneyCount) {
		this.paperMoneyCount = paperMoneyCount;
	}



	public BigDecimal getPaperMoney() {
		return paperMoney;
	}

	public void setPaperMoney(BigDecimal paperMoney) {
		this.paperMoney = paperMoney;
	}

	public BigDecimal getComperMoney() {
		return comperMoney;
	}

	public void setComperMoney(BigDecimal comperMoney) {
		this.comperMoney = comperMoney;
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

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
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

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	
}