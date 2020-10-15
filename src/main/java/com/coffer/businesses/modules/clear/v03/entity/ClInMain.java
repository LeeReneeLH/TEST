package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;

/**
 * 商行交款Entity
 * 
 * @author wanglin
 * @version 2017-08-11
 */
public class ClInMain extends DataEntity<ClInMain> {

	private static final long serialVersionUID = 1L;

	/** ***************数据库相关*************** **/

	/** 业务流水 **/
	private String inNo = "";
	/** 状态 1：登记，2：冲正 **/
	private String status = "";

	/** 状态名 1：登记，2：冲正 **/
	private String statusName = "";

	/** 业务类型 **/
	private String busType = "";

	/** 银行交接员编号（A） **/
	private String bankManNoA = "";

	/** 银行交接员（A） **/
	private String bankManNameA = "";

	/** 银行交接员编号（B） **/
	private String bankManNoB = "";

	/** 银行交接员（B） **/
	private String bankManNameB = "";

	/** 入库总金额 **/
	private BigDecimal inAmount;

	/** 总金额格式化 **/
	private String inAmountFormat = "";

	/** 银行交接员编号 **/
	private String transManNo = "";
	/** 银行交接员 **/
	private String transManName = "";

	/** 复合人员编号 **/
	private String checkManNo = "";
	/** 复合人员 **/
	private String checkManName = "";

	/** 确认人编号 **/
	private String makesureManNo = "";
	/** 确认人名称 **/
	private String makesureManName = "";

	/** 备注 **/
	private String remarks = "";

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;

	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;

	private String searchDateEnd;

	/** 商行交款明细 */
	private List<ClInDetail> bankPayDetailList = Lists.newArrayList();

	/** 面值列表 */
	private List<DenominationInfo> denominationList = Lists.newArrayList();

	/** 登记机构 */
	private Office rOffice;
	/** 人员类型 */
	private String userType;
	/** 面值 */
	private String denomination;
	/** 人行捆数 */
	private String countPeopleBank;
	/** 商行捆数 */
	private String countBank;

	/** 授权人id */
	private User authorizeBy;
	/** 授权人姓名 */
	private String authorizeName;
	/** 授权时间 */
	private Date authorizeDate;
	/** 授权原因 */
	private String authorizeReason;

	/** 授权人登录名 */
	private String authorizeLogin;
	/** 授权人登录密码 */
	private String authorizePass;

	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */

	public ClInMain() {
		super();
	}

	public ClInMain(String id) {
		super(id);
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public String getInNo() {
		return inNo;
	}

	public void setInNo(String inNo) {
		this.inNo = inNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getBankManNoA() {
		return bankManNoA;
	}

	public void setBankManNoA(String bankManNoA) {
		this.bankManNoA = bankManNoA;
	}

	public String getBankManNameA() {
		return bankManNameA;
	}

	public void setBankManNameA(String bankManNameA) {
		this.bankManNameA = bankManNameA;
	}

	public String getBankManNoB() {
		return bankManNoB;
	}

	public void setBankManNoB(String bankManNoB) {
		this.bankManNoB = bankManNoB;
	}

	public String getBankManNameB() {
		return bankManNameB;
	}

	public void setBankManNameB(String bankManNameB) {
		this.bankManNameB = bankManNameB;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public String getInAmountFormat() {
		return inAmountFormat;
	}

	public void setInAmountFormat(String inAmountFormat) {
		this.inAmountFormat = inAmountFormat;
	}

	public String getTransManNo() {
		return transManNo;
	}

	public void setTransManNo(String transManNo) {
		this.transManNo = transManNo;
	}

	public String getTransManName() {
		return transManName;
	}

	public void setTransManName(String transManName) {
		this.transManName = transManName;
	}

	public String getCheckManNo() {
		return checkManNo;
	}

	public void setCheckManNo(String checkManNo) {
		this.checkManNo = checkManNo;
	}

	public String getCheckManName() {
		return checkManName;
	}

	public void setCheckManName(String checkManName) {
		this.checkManName = checkManName;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public List<ClInDetail> getBankPayDetailList() {
		return bankPayDetailList;
	}

	public void setBankPayDetailList(List<ClInDetail> bankPayDetailList) {
		this.bankPayDetailList = bankPayDetailList;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public List<DenominationInfo> getDenominationList() {
		return denominationList;
	}

	public void setDenominationList(List<DenominationInfo> denominationList) {
		this.denominationList = denominationList;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getCountPeopleBank() {
		return countPeopleBank;
	}

	public void setCountPeopleBank(String countPeopleBank) {
		this.countPeopleBank = countPeopleBank;
	}

	public String getCountBank() {
		return countBank;
	}

	public void setCountBank(String countBank) {
		this.countBank = countBank;
	}

	public User getAuthorizeBy() {
		return authorizeBy;
	}

	public void setAuthorizeBy(User authorizeBy) {
		this.authorizeBy = authorizeBy;
	}

	public String getAuthorizeName() {
		return authorizeName;
	}

	public void setAuthorizeName(String authorizeName) {
		this.authorizeName = authorizeName;
	}

	public Date getAuthorizeDate() {
		return authorizeDate;
	}

	public void setAuthorizeDate(Date authorizeDate) {
		this.authorizeDate = authorizeDate;
	}

	public String getAuthorizeReason() {
		return authorizeReason;
	}

	public void setAuthorizeReason(String authorizeReason) {
		this.authorizeReason = authorizeReason;
	}

	public String getAuthorizeLogin() {
		return authorizeLogin;
	}

	public void setAuthorizeLogin(String authorizeLogin) {
		this.authorizeLogin = authorizeLogin;
	}

	public String getAuthorizePass() {
		return authorizePass;
	}

	public void setAuthorizePass(String authorizePass) {
		this.authorizePass = authorizePass;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Override
	public String toString() {
		return "clInMain [inNo=" + inNo + ", status=" + status + ", inAmount=" + inAmount + "]";
	}

}