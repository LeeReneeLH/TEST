package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 调缴功能Entity
 * 
 * @author Chengshu
 * @version 2015-05-11
 */
public class StoReportInfo extends DataEntity<StoReportInfo> {

	private static final long serialVersionUID = 1L;
	

	/** 报表种别 **/
	private String reportType = "";

	/** 日期 **/
	private String time = "";

	/** ----------物品相关---------- **/
	/** 币种 */
	private String currency;
	/** 类别 */
	private String classification;
	/** 套别 */
	private String edition;
	/** 材质 */
	private String cash;
	/** 面值 */
	private String denomination;
	/** 单位 */
	private String unit;
	/** 数量 */
	private Long number;
	/** 流通币金额 */
	private BigDecimal amount = new BigDecimal(0.0d);
	/** 残损币金额 */
	private BigDecimal amountDamage = new BigDecimal(0.0d);
	/** 假币金额 */
	private BigDecimal amountCounterfeit = new BigDecimal(0.0d);
	/** 待整点币 */
	private BigDecimal amountCountwait = new BigDecimal(0.0d);
	/** 所属金库 */
	private Office office;
	/** 所属金库名字 */
	private String officeN;
	/** 箱袋数量 */
	private int boxCount = 0;
	
	/** 重空信息列表 */
	private List<StoEmptyDocument> stoEmptyDocumentList = Lists.newArrayList();
	
	/** 重空变更列表 */
	private List<StoStoresHistory> stoEmptyHistoryList = Lists.newArrayList();
	
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	@ExcelField(title="日期")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	@ExcelField(title="币种")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@ExcelField(title="类别")
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	@ExcelField(title="套别")
	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	@ExcelField(title="材质")
	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	@ExcelField(title="面值")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	@ExcelField(title="单位")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	@ExcelField(title="数量")
	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	@ExcelField(title="流通币金额")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@ExcelField(title="残损币金额")
	public BigDecimal getAmountDamage() {
		return amountDamage;
	}

	public void setAmountDamage(BigDecimal amountDamage) {
		this.amountDamage = amountDamage;
	}

	@ExcelField(title="假币金额")
	public BigDecimal getAmountCounterfeit() {
		return amountCounterfeit;
	}

	public void setAmountCounterfeit(BigDecimal amountCounterfeit) {
		this.amountCounterfeit = amountCounterfeit;
	}

	@ExcelField(title="待整点金额")
	public BigDecimal getAmountCountwait() {
		return amountCountwait;
	}

	public void setAmountCountwait(BigDecimal amountCountwait) {
		this.amountCountwait = amountCountwait;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@ExcelField(title = "机构")
	public String getOfficeN() {
		return officeN;
	}

	public void setOfficeN(String officeN) {
		this.officeN = officeN;
	}

	public int getBoxCount() {
		return boxCount;
	}

	public void setBoxCount(int boxCount) {
		this.boxCount = boxCount;
	}

	public List<StoEmptyDocument> getStoEmptyDocumentList() {
		return stoEmptyDocumentList;
	}

	public void setStoEmptyDocumentList(List<StoEmptyDocument> stoEmptyDocumentList) {
		this.stoEmptyDocumentList = stoEmptyDocumentList;
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

	public List<StoStoresHistory> getStoEmptyHistoryList() {
		return stoEmptyHistoryList;
	}

	public void setStoEmptyHistoryList(List<StoStoresHistory> stoEmptyHistoryList) {
		this.stoEmptyHistoryList = stoEmptyHistoryList;
	}
	
}