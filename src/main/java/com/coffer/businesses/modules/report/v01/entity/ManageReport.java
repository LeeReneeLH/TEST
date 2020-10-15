package com.coffer.businesses.modules.report.v01.entity;

import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 管理分析报表相关信息Entity
 * 
 * @author WQJ
 * @version 2020-1-6
 */
public class ManageReport extends DataEntity<ManageReport> {
	private static final long serialVersionUID = 1L;
	private String doorId; // 门店ID
	@ExcelField(title = "仓库名称", align = 2)
	private String doorName; // door_name
	private String collectCount; // 收钞次数
	@ExcelField(title = "卡钞次数", align = 2)
	private String stuckCount; // 卡钞次数
	@ExcelField(title = "存款笔数", align = 2)
	private String saveCount; // 存款笔数
	@ExcelField(title = "长款金额", align = 2)
	private String longMoney; // 长款金额
	@ExcelField(title = "短款金额", align = 2)
	private String shortMoney; // 短款金额
	@ExcelField(title = "合计", align = 2)
	private String totalMoney; // 合计
	private String month; // 月份
	@ExcelField(title = "一月", align = 2)
	private String january; // 一月
	@ExcelField(title = "二月", align = 2)
	private String february; // 二月
	@ExcelField(title = "三月", align = 2)
	private String march; // 三月
	@ExcelField(title = "四月", align = 2)
	private String april; // 四月
	@ExcelField(title = "五月", align = 2)
	private String may; // 五月
	@ExcelField(title = "六月", align = 2)
	private String june; // 六月
	@ExcelField(title = "七月", align = 2)
	private String july; // 七月
	@ExcelField(title = "八月", align = 2)
	private String august; // 八月
	@ExcelField(title = "九月", align = 2)
	private String septemper; // 九月
	@ExcelField(title = "十月", align = 2)
	private String october; // 十月
	@ExcelField(title = "十一月", align = 2)
	private String november; // 十一月
	@ExcelField(title = "十二月", align = 2)
	private String december; // 十二月

	public String getJanuary() {
		return january;
	}

	public void setJanuary(String january) {
		this.january = january;
	}

	public String getFebruary() {
		return february;
	}

	public void setFebruary(String february) {
		this.february = february;
	}

	public String getMarch() {
		return march;
	}

	public void setMarch(String march) {
		this.march = march;
	}

	public String getApril() {
		return april;
	}

	public void setApril(String april) {
		this.april = april;
	}

	public String getMay() {
		return may;
	}

	public void setMay(String may) {
		this.may = may;
	}

	public String getJune() {
		return june;
	}

	public void setJune(String june) {
		this.june = june;
	}

	public String getJuly() {
		return july;
	}

	public void setJuly(String july) {
		this.july = july;
	}

	public String getAugust() {
		return august;
	}

	public void setAugust(String august) {
		this.august = august;
	}

	public String getSeptemper() {
		return septemper;
	}

	public void setSeptemper(String septemper) {
		this.septemper = septemper;
	}

	public String getOctober() {
		return october;
	}

	public void setOctober(String october) {
		this.october = october;
	}

	public String getNovember() {
		return november;
	}

	public void setNovember(String november) {
		this.november = november;
	}

	public String getDecember() {
		return december;
	}

	public void setDecember(String december) {
		this.december = december;
	}

	/** 时间查询用 **/
	private Date createTimeStart;
	private Date createTimeEnd;
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

	public String getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(String collectCount) {
		this.collectCount = collectCount;
	}

	public String getStuckCount() {
		return stuckCount;
	}

	public void setStuckCount(String stuckCount) {
		this.stuckCount = stuckCount;
	}

	public String getErrorCount() {
		return saveCount;
	}

	public void setErrorCount(String errorCount) {
		this.saveCount = errorCount;
	}

	public String getLongMoney() {
		return longMoney;
	}

	public void setLongMoney(String longMoney) {
		this.longMoney = longMoney;
	}

	public String getShortMoney() {
		return shortMoney;
	}

	public void setShortMoney(String shortMoney) {
		this.shortMoney = shortMoney;
	}
	public String getSaveCount() {
		return saveCount;
	}

	public void setSaveCount(String saveCount) {
		this.saveCount = saveCount;
	}

	public String getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(String totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
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