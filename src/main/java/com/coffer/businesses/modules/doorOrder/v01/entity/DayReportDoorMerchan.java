package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商户日结Entity
 * 
 * @author wqj
 * @version 2019-07-23
 */
public class DayReportDoorMerchan extends DataEntity<DayReportDoorMerchan> {

	private static final long serialVersionUID = 1L;
	private String reportId; // 日结ID
	private String officeId; // 商户ID
	@ExcelField(title = "门店名称", align = 2)
	private String officeName; // 商户名称
	private String settlementType; // 商户结算类型（01-存款结算，02-差错结算，03-传统结算）
	private BigDecimal inAmount; // 借入金额
	private BigDecimal outAmount; // 贷出金额
	@ExcelField(title = "差错总额", align = 2)
	private BigDecimal totalAmount; // 总金额
	private String paidStatus; // 代付状态（0：已代付；1-未代付） / 处理状态（0-未处理；1-已处理）
	@ExcelField(title = "结算时间", align = 2)
	private Date reportDate; // 日结时间
	private String accountType; // 账务类型
	private String rofficeId;// 结算机构
	private String rofficeName;// 结算机构名称
	@ExcelField(title = "结算人", align = 2)
	private String rname;// 结算人
	@ExcelField(title = "处理金额", align = 2)
	private BigDecimal paidAmount; // 实际汇款金额
	private String tradeSerialNumber;// 交易流水号
	@ExcelField(title = "处理时间", align = 2)
	private Date paidDate;// 汇款时间;

	/** add by zhr start 2019-08-28 */
	private String merchantId; // 商户ID
	private String merchantName; // 商户名称
	private Date startDate;

	/** add by lihe 2019-08-29 */
	private String uploadFlag; // 上传标识 ：0-已上传，1-未上传
	private byte[] photo; // 凭条照片
	private String dayReportDate; // 上次日结时间
	private String clientForApp;// 门店编号（小程序用）

	/** add by XL 2019-10-30 */
	private String dayReportIds;// 商户日结ID列表

	private String paperMoneyCount; // 纸币数量
	private String coinMoneyCount; // 硬币数量
	private BigDecimal paperMoneyTotal; // 纸币金额
	private BigDecimal coinMoneyTotal;// 硬币金额
	private String saveTotal; // 存款总量
	private BigDecimal pack; // 强制存款
	private BigDecimal other; // 其他存款

	/** add by lihe 2019-11-27 */
	private User confirmBy; // 确认人
	private String confirmName;// 确认人名称
	private Date confirmDate; // 确认日期
	private List<String> paidStatusList; // 付款状态列表
	private List<String> settlementTypeList; // 结算类型列表
	private List<String> dayReportIdList; // 日结ID列表
	@ExcelField(title = "差错类型", align = 2)
	private String errorType; // 差错类型
	@ExcelField(title = "处理状态", align = 2)
	private String paidStatusName; // 处理状态名称
	@ExcelField(title = "结算机构", align = 2)
	private String settleOfficeName; // 结算机构名称
	private String uninitDateFlag; // 非初始化时间标记
	private Integer totalCount; // 日结条数
	private Integer unSettledCount; // 未日结条数

	/** add by ZXK 2020-04-16 */
	private String reportMoneyStart; // 日结金额 (开始)
	private String reportMoneyEnd;// 日结金额(结束)

	/** add by gzd 2020-07-28 */
	private BigDecimal acturalReportAmount; // 实际结算金额
	private String acturalReportAmounts; // 实际结算金额集合
	private String paidStatuss; // 代付状态集合
	private String remarksList; // 备注集合

	public String getRemarksList() {
		return remarksList;
	}

	public void setRemarksList(String remarksList) {
		this.remarksList = remarksList;
	}

	public String getPaidStatuss() {
		return paidStatuss;
	}

	public void setPaidStatuss(String paidStatuss) {
		this.paidStatuss = paidStatuss;
	}

	public String getActuralReportAmounts() {
		return acturalReportAmounts;
	}

	public void setActuralReportAmounts(String acturalReportAmounts) {
		this.acturalReportAmounts = acturalReportAmounts;
	}

	public BigDecimal getActuralReportAmount() {
		return acturalReportAmount;
	}

	public void setActuralReportAmount(BigDecimal acturalReportAmount) {
		this.acturalReportAmount = acturalReportAmount;
	}

	public String getReportMoneyStart() {
		return reportMoneyStart;
	}

	public void setReportMoneyStart(String reportMoneyStart) {
		this.reportMoneyStart = reportMoneyStart;
	}

	public String getReportMoneyEnd() {
		return reportMoneyEnd;
	}

	public void setReportMoneyEnd(String reportMoneyEnd) {
		this.reportMoneyEnd = reportMoneyEnd;
	}

	private String sevenCode;

	public String getRofficeName() {
		return rofficeName;
	}

	public void setRofficeName(String rofficeName) {
		this.rofficeName = rofficeName;
	}

	public String getSevenCode() {
		return sevenCode;
	}

	public void setSevenCode(String sevenCode) {
		this.sevenCode = sevenCode;
	}

	public BigDecimal getOther() {
		return other;
	}

	public void setOther(BigDecimal other) {
		this.other = other;
	}

	public String getPaperMoneyCount() {
		return paperMoneyCount;
	}

	public void setPaperMoneyCount(String paperMoneyCount) {
		this.paperMoneyCount = paperMoneyCount;
	}

	public String getCoinMoneyCount() {
		return coinMoneyCount;
	}

	public void setCoinMoneyCount(String coinMoneyCount) {
		this.coinMoneyCount = coinMoneyCount;
	}

	public BigDecimal getPaperMoneyTotal() {
		return paperMoneyTotal;
	}

	public void setPaperMoneyTotal(BigDecimal paperMoneyTotal) {
		this.paperMoneyTotal = paperMoneyTotal;
	}

	public BigDecimal getCoinMoneyTotal() {
		return coinMoneyTotal;
	}

	public void setCoinMoneyTotal(BigDecimal coinMoneyTotal) {
		this.coinMoneyTotal = coinMoneyTotal;
	}

	public String getSaveTotal() {
		return saveTotal;
	}

	public void setSaveTotal(String saveTotal) {
		this.saveTotal = saveTotal;
	}

	public BigDecimal getPack() {
		return pack;
	}

	public void setPack(BigDecimal pack) {
		this.pack = pack;
	}

	public String getDayReportDate() {
		return dayReportDate;
	}

	public void setDayReportDate(String dayReportDate) {
		this.dayReportDate = dayReportDate;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public String getTradeSerialNumber() {
		return tradeSerialNumber;
	}

	public void setTradeSerialNumber(String tradeSerialNumber) {
		this.tradeSerialNumber = tradeSerialNumber;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

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

	public String getRofficeId() {
		return rofficeId;
	}

	public void setRofficeId(String rofficeId) {
		this.rofficeId = rofficeId;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DayReportDoorMerchan() {
		super();
	}

	public DayReportDoorMerchan(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "日结ID长度必须介于 1 和 64 之间")
	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	@Length(min = 0, max = 64, message = "商户名称长度必须介于 0 和 64 之间")
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	@Length(min = 0, max = 2, message = "商户结算类型（74-存款汇总，79-存款差错）长度必须介于 0 和 2 之间")
	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Length(min = 0, max = 1, message = "状态（0：已代付；1-未代付）长度必须介于 0 和 1 之间")
	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	@Length(min = 0, max = 1, message = "账务类型长度必须介于 0 和 1 之间")
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getClientForApp() {
		return clientForApp;
	}

	public void setClientForApp(String clientForApp) {
		this.clientForApp = clientForApp;
	}

	public String getDayReportIds() {
		return dayReportIds;
	}

	public void setDayReportIds(String dayReportIds) {
		this.dayReportIds = dayReportIds;
	}

	public User getConfirmBy() {
		return confirmBy;
	}

	public void setConfirmBy(User confirmBy) {
		this.confirmBy = confirmBy;
	}

	public String getConfirmName() {
		return confirmName;
	}

	public void setConfirmName(String confirmName) {
		this.confirmName = confirmName;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public List<String> getPaidStatusList() {
		return paidStatusList;
	}

	public void setPaidStatusList(List<String> paidStatusList) {
		this.paidStatusList = paidStatusList;
	}

	public List<String> getSettlementTypeList() {
		return settlementTypeList;
	}

	public void setSettlementTypeList(List<String> settlementTypeList) {
		this.settlementTypeList = settlementTypeList;
	}

	public List<String> getDayReportIdList() {
		return dayReportIdList;
	}

	public void setDayReportIdList(List<String> dayReportIdList) {
		this.dayReportIdList = dayReportIdList;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getPaidStatusName() {
		return paidStatusName;
	}

	public void setPaidStatusName(String paidStatusName) {
		this.paidStatusName = paidStatusName;
	}

	public String getSettleOfficeName() {
		return settleOfficeName;
	}

	public void setSettleOfficeName(String settleOfficeName) {
		this.settleOfficeName = settleOfficeName;
	}

	public String getUninitDateFlag() {
		return uninitDateFlag;
	}

	public void setUninitDateFlag(String uninitDateFlag) {
		this.uninitDateFlag = uninitDateFlag;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getUnSettledCount() {
		return unSettledCount;
	}

	public void setUnSettledCount(Integer unSettledCount) {
		this.unSettledCount = unSettledCount;
	}

}