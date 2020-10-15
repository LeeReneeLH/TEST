package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 差错管理Entity
 * 
 * @author XL
 * @version 2019-06-28
 */
public class DoorErrorInfo extends DataEntity<DoorErrorInfo> {

	private static final long serialVersionUID = 1L;
	private String errorNo; // 主键ID
	@ExcelField(title = "拆箱单号", align = 2)
	private String businessId; // 业务流水
	private String outRowNo;// 明细序号
	private String tickertape;// 凭条
	private String custNo; // 客户编号
	@ExcelField(title = "门店名称", align = 2)
	private String custName; // 客户名称
	@ExcelField(title = "差错类型", align = 2)
	private String errorType; // 差错类型(1：假币 2：长款 3：短款)
	@ExcelField(title = "状态", align = 2)
	private String status; // 差错状态(0：登记 4：冲正)
	private Office office; // 账务发生机构
	private String companyId; // 账务归属公司ID
	private String companyName; // 账务归属公司名称
	private String clearManNo; // 清机人编号
	@ExcelField(title = "清分人员", align = 2)
	private String clearManName; // 清机人名称
	private String makesureManNo; // 确认人编号
	@ExcelField(title = "确认人", align = 2)
	private String makesureManName; // 确认人名称
	private BigDecimal inputAmount;// 录入金额
	private BigDecimal checkAmount;// 清点金额
	@ExcelField(title = "差额", align = 2)
	private BigDecimal diffAmount; // 差额
	
	private Date createTimeStart;// 开始时间
	private Date createTimeEnd;// 结束时间
	private String searchDateStart;// 开始时间
	private String searchDateEnd;// 结束时间
	
	
	/*2020-3-5 ZXK begin */
	private String merchantId;// 商户id
	@ExcelField(title = "商户名称", align = 3, sort = 20)
	private String merchantName;// 商户名称
	@ExcelField(title = "差额合计", align = 3, sort = 20)
	private BigDecimal merchantAmount;// 商户差额合计
	@ExcelField(title = "登记日期", align = 2)
	private Date registerDate; 
	
	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	private String doiId; //门店存款id

	public String getDoiId() {
		return doiId;
	}

	public void setDoiId(String doiId) {
		this.doiId = doiId;
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

	public BigDecimal getMerchantAmount() {
		return merchantAmount;
	}

	public void setMerchantAmount(BigDecimal merchantAmount) {
		this.merchantAmount = merchantAmount;
	}

	/*2020-3-5 gzd begin */
	private String officeId; // 机构Id
	@ExcelField(title = "清分中心", align = 2)
	private String officeName; // 机构名称
	private String bagNo; // 包号
	private BigDecimal sumDiffAmount; // 合计差额	
	@ExcelField(title = "登记日期", align = 2)
	private Date createTime; // 登记日期
	private Date lastTime; // 上次清机时间
	private Date thisTime; // 本次清机时间
	@ExcelField(title = "钞袋使用时间", align = 2)
	private String bagNoUseTime; // 钞袋使用时间
	
	

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getBagNoUseTime() {
		return bagNoUseTime;
	}

	public void setBagNoUseTime(String bagNoUseTime) {
		this.bagNoUseTime = bagNoUseTime;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public Date getThisTime() {
		return thisTime;
	}

	public void setThisTime(Date thisTime) {
		this.thisTime = thisTime;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public BigDecimal getSumDiffAmount() {
		return sumDiffAmount;
	}

	public void setSumDiffAmount(BigDecimal sumDiffAmount) {
		this.sumDiffAmount = sumDiffAmount;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getBagNo() {
		return bagNo;
	}

	public void setBagNo(String bagNo) {
		this.bagNo = bagNo;
	}
	/*2020-3-5 gzd end */
	
	public DoorErrorInfo() {
		super();
	}

	public DoorErrorInfo(String id) {
		super(id);
	}

	public String getErrorNo() {
		return errorNo;
	}

	public void setErrorNo(String errorNo) {
		this.errorNo = errorNo;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getOutRowNo() {
		return outRowNo;
	}

	public void setOutRowNo(String outRowNo) {
		this.outRowNo = outRowNo;
	}

	public String getTickertape() {
		return tickertape;
	}

	public void setTickertape(String tickertape) {
		this.tickertape = tickertape;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getClearManNo() {
		return clearManNo;
	}

	public void setClearManNo(String clearManNo) {
		this.clearManNo = clearManNo;
	}

	public String getClearManName() {
		return clearManName;
	}

	public void setClearManName(String clearManName) {
		this.clearManName = clearManName;
	}

	public String getMakesureManNo() {
		return makesureManNo;
	}

	public void setMakesureManNo(String makesureManNo) {
		this.makesureManNo = makesureManNo;
	}

	public String getMakesureManName() {
		return makesureManName;
	}

	public void setMakesureManName(String makesureManName) {
		this.makesureManName = makesureManName;
	}

	public BigDecimal getInputAmount() {
		return inputAmount;
	}

	public void setInputAmount(BigDecimal inputAmount) {
		this.inputAmount = inputAmount;
	}

	public BigDecimal getCheckAmount() {
		return checkAmount;
	}

	public void setCheckAmount(BigDecimal checkAmount) {
		this.checkAmount = checkAmount;
	}

	public BigDecimal getDiffAmount() {
		return diffAmount;
	}

	public void setDiffAmount(BigDecimal diffAmount) {
		this.diffAmount = diffAmount;
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