package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
/**
 * 
* Title: CheckCashAuthorize 
* <p>Description:授权Entity </p>
* @author HaoShijie
* @date 2020年4月28日 下午1:39:44
 */
public class CheckCashAuthorize extends DataEntity<CheckCashAuthorize> {
	private static final long serialVersionUID = 1L;
	private String id;                // 授权编号
	private String officeId;         // 授权机构编号
	private String officeName;       // 授权机构名称
	private BigDecimal amount;        // 授权金额
	private String isUse;             // 是否设置授权标识 0：开启 1：关闭
	private String type;              // 授权类型 0:拆箱
	private String officeType;        // 机构类型 6:清分中心，7:数字化平台，8:门店，9:商户，10:油站编码
	private String expressionType;    // 表达式类型 1:大于，2:小于，3:等于，4:大于等于，5:小于等于
	private String authorizeRank;     //权限级别
	
	private List<String> lists;   //门店相关机构集合
	
	public List<String> getLists() {
		return lists;
	}

	public void setLists(List<String> lists) {
		this.lists = lists;
	}

	public String getAuthorizeRank() {
		return authorizeRank;
	}

	public void setAuthorizeRank(String authorizeRank) {
		this.authorizeRank = authorizeRank;
	}

	public String getExpressionType() {
		return expressionType;
	}

	public void setExpressionType(String expressionType) {
		this.expressionType = expressionType;
	}

	public String getOfficeType() {
		return officeType;
	}

	public void setOfficeType(String officeType) {
		this.officeType = officeType;
	}

	//时间查询用
	private Date createTimeStart;
	private Date createTimeEnd;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

}
