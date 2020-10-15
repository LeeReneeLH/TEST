package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 *存款时间分析报表Entity
 * 
 * @author gzd
 * @version 2020-01-15
 */

public class DepositTimeAnalysis extends DataEntity<DepositTimeAnalysis> {
	
	private static final long serialVersionUID = 1L;
	
	private String doorId; // 门店ID
	@ExcelField(title = "仓库", align = 2)
	private String doorName; // 门店名称
	private Office office; // 归属机构
	@ExcelField(title = "结算办", align = 2)
	private String settleOffice; // 结算办名称
	@ExcelField(title = "存款次数", align = 2)
	private String totalCount;// 存款总次数
	@ExcelField(title = "存款次数", align = 2)
	private String ltFiveCount; // 小于5分钟次数
	@ExcelField(title = "存款次数", align = 2)
	private String fiveToTenCount; // 5至10分钟次数
	@ExcelField(title = "存款次数", align = 2)
	private String tenToFifteenCount; // 10至15分钟次数
	@ExcelField(title = "存款次数", align = 2)
	private String fifteenToTwentyCount; // 15至20分钟次数
	@ExcelField(title = "存款次数", align = 2)
	private String gtTwentyCount; // 大于20分钟次数
	@ExcelField(title = "占比", align = 2)
	private BigDecimal ltFivePercent; // 小于5分钟占比
	@ExcelField(title = "占比", align = 2)
	private BigDecimal fiveToTenPercent; // 5至10分钟占比
	@ExcelField(title = "占比", align = 2)
	private BigDecimal tenToFifteenPercent; // 10至15分钟占比
	@ExcelField(title = "占比", align = 2)
	private BigDecimal fifteenToTwentyPercent; // 15至20分钟占比
	@ExcelField(title = "占比", align = 2)
	private BigDecimal gtTwentyPercent; // 大于20分钟占比
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
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getLtFiveCount() {
		return ltFiveCount;
	}
	public void setLtFiveCount(String ltFiveCount) {
		this.ltFiveCount = ltFiveCount;
	}
	public String getFiveToTenCount() {
		return fiveToTenCount;
	}
	public void setFiveToTenCount(String fiveToTenCount) {
		this.fiveToTenCount = fiveToTenCount;
	}
	public String getTenToFifteenCount() {
		return tenToFifteenCount;
	}
	public void setTenToFifteenCount(String tenToFifteenCount) {
		this.tenToFifteenCount = tenToFifteenCount;
	}
	public String getFifteenToTwentyCount() {
		return fifteenToTwentyCount;
	}
	public void setFifteenToTwentyCount(String fifteenToTwentyCount) {
		this.fifteenToTwentyCount = fifteenToTwentyCount;
	}
	public String getGtTwentyCount() {
		return gtTwentyCount;
	}
	public void setGtTwentyCount(String gtTwentyCount) {
		this.gtTwentyCount = gtTwentyCount;
	}
	public BigDecimal getLtFivePercent() {
		return ltFivePercent;
	}
	public void setLtFivePercent(BigDecimal ltFivePercent) {
		this.ltFivePercent = ltFivePercent;
	}
	public BigDecimal getFiveToTenPercent() {
		return fiveToTenPercent;
	}
	public void setFiveToTenPercent(BigDecimal fiveToTenPercent) {
		this.fiveToTenPercent = fiveToTenPercent;
	}
	public BigDecimal getTenToFifteenPercent() {
		return tenToFifteenPercent;
	}
	public void setTenToFifteenPercent(BigDecimal tenToFifteenPercent) {
		this.tenToFifteenPercent = tenToFifteenPercent;
	}
	public BigDecimal getFifteenToTwentyPercent() {
		return fifteenToTwentyPercent;
	}
	public void setFifteenToTwentyPercent(BigDecimal fifteenToTwentyPercent) {
		this.fifteenToTwentyPercent = fifteenToTwentyPercent;
	}
	public BigDecimal getGtTwentyPercent() {
		return gtTwentyPercent;
	}
	public void setGtTwentyPercent(BigDecimal gtTwentyPercent) {
		this.gtTwentyPercent = gtTwentyPercent;
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
