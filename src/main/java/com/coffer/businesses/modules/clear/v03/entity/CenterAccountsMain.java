package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.User;

/**
 * 账务管理Entity
 * 
 * @author QPH
 * @version 2017-09-04
 */
public class CenterAccountsMain extends DataEntity<CenterAccountsMain> {

	private static final long serialVersionUID = 1L;
	private String accountsId; // 账务表主键
	private String clientId; // 客户编号
	@ExcelField(title = "客户名称", align = 2)
	private String clientName; // 客户名称
	private String businessType; // 业务类型
	private String businessId; // 业务对应流水ID
	private String rofficeId; // 账务发生机构（清分中心）
	@ExcelField(title = "机构名称", align = 2)
	private String rofficeName; // 账务发生机构名称
	private String aofficeId; // 账务归属机构（商业银行）
	private String aofficeName; // 账务归属机构名称
	@ExcelField(title = "收入", align = 2)
	private BigDecimal inAmount; // 入库总金额（借）
	@ExcelField(title = "付出", align = 2)
	private BigDecimal outAmount; // 出库总金额（贷）
	@ExcelField(title = "余额", align = 2)
	private BigDecimal totalAmount; // 中心剩余总金额
	@ExcelField(title = "客户余额", align = 2)
	private BigDecimal guestTotalAmount; // 中心剩余总金额
	private String inoutType; // 出入库类型：0：出库 1：入库
	private String businessStatus; // 业务状态 1：登记 2：冲正

	@ExcelField(title = "操作类型", align = 2)
	private String businessStatusReport; // 业务状态 1：登记 2：冲正
	@ExcelField(title = "业务类型", align = 2)
	private String businessTypeReport; // 业务类型
	@ExcelField(title = "交易日期", align = 2)
	private String createDateReport;

	/** 当前登陆用户信息 */
	private User loginUser;

	/** 清分管理明细列表 */
	private List<CenterAccountsDetail> centerAccountsDetailList;

	/** 业务类型列表 */
	private List<String> businessTypes;

	/** 客户列表 */
	private List<String> clientIdList;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	/** 收入支出笔数（页面用） **/
	private int inCount; // 收入笔数
	private int outCount; // 支出金额

	public CenterAccountsMain() {
		super();
	}

	public CenterAccountsMain(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "账务表主键长度必须介于 1 和 64 之间")
	public String getAccountsId() {
		return accountsId;
	}

	public void setAccountsId(String accountsId) {
		this.accountsId = accountsId;
	}

	@Length(min = 0, max = 64, message = "客户编号长度必须介于 0 和 64 之间")
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Length(min = 0, max = 20, message = "客户名称长度必须介于 0 和 20 之间")
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Length(min = 0, max = 2, message = "业务类型长度必须介于 0 和 2 之间")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@Length(min = 0, max = 64, message = "业务对应流水ID长度必须介于 0 和 64 之间")
	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	@Length(min = 0, max = 64, message = "登记机构长度必须介于 0 和 64 之间")
	public String getRofficeId() {
		return rofficeId;
	}

	public void setRofficeId(String rofficeId) {
		this.rofficeId = rofficeId;
	}

	@Length(min = 0, max = 200, message = "登记机构名称长度必须介于 0 和 200 之间")
	public String getRofficeName() {
		return rofficeName;
	}

	public void setRofficeName(String rofficeName) {
		this.rofficeName = rofficeName;
	}

	@Length(min = 0, max = 64, message = "接收机构长度必须介于 0 和 64 之间")
	public String getAofficeId() {
		return aofficeId;
	}

	public void setAofficeId(String aofficeId) {
		this.aofficeId = aofficeId;
	}

	@Length(min = 0, max = 200, message = "接收机构名称长度必须介于 0 和 200 之间")
	public String getAofficeName() {
		return aofficeName;
	}

	public void setAofficeName(String aofficeName) {
		this.aofficeName = aofficeName;
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

	public List<CenterAccountsDetail> getCenterAccountsDetailList() {
		return centerAccountsDetailList;
	}

	public void setCenterAccountsDetailList(List<CenterAccountsDetail> centerAccountsDetailList) {
		this.centerAccountsDetailList = centerAccountsDetailList;
	}

	public String getInoutType() {
		return inoutType;
	}

	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public BigDecimal getGuestTotalAmount() {
		return guestTotalAmount;
	}

	public void setGuestTotalAmount(BigDecimal guestTotalAmount) {
		this.guestTotalAmount = guestTotalAmount;
	}

	public List<String> getClientIdList() {
		return clientIdList;
	}

	public void setClientIdList(List<String> clientIdList) {
		this.clientIdList = clientIdList;
	}

	public String getBusinessStatus() {
		return businessStatus;
	}

	public void setBusinessStatus(String businessStatus) {
		this.businessStatus = businessStatus;
	}

	public String getCreateDateReport() {
		return createDateReport;
	}

	public void setCreateDateReport(String createDateReport) {
		this.createDateReport = createDateReport;
	}

	public String getBusinessStatusReport() {
		return businessStatusReport;
	}

	public void setBusinessStatusReport(String businessStatusReport) {
		this.businessStatusReport = businessStatusReport;
	}

	public String getBusinessTypeReport() {
		return businessTypeReport;
	}

	public void setBusinessTypeReport(String businessTypeReport) {
		this.businessTypeReport = businessTypeReport;
	}

	public int getInCount() {
		return inCount;
	}

	public void setInCount(int inCount) {
		this.inCount = inCount;
	}

	public int getOutCount() {
		return outCount;
	}

	public void setOutCount(int outCount) {
		this.outCount = outCount;
	}

	@Override
	public String toString() {
		return "AllAllocateInfo [accountsId=" + accountsId + ", businessType=" + businessType + ", clientId=" + clientId
				+ ", totalAmount=" + totalAmount + ", inoutType=" + inoutType + ", businessId=" + businessId + "]";
	}

}