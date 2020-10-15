package com.coffer.businesses.modules.common.entity;

import java.util.List;
import java.util.Map;

public class ReceiveEntity {
	String versionNo;
	String serviceNo;
	String officeId;
	String userId;
	String loginName;
	String password;
	String searchDate;
	String goodsTypes;
	List<Map<String,Object>> list;
	List<Map<String,Object>> authorizeList;
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getServiceNo() {
		return serviceNo;
	}
	public void setServiceNo(String serviceNo) {
		this.serviceNo = serviceNo;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSearchDate() {
		return searchDate;
	}
	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}
	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	public String getGoodsTypes() {
		return goodsTypes;
	}
	public void setGoodsTypes(String goodsTypes) {
		this.goodsTypes = goodsTypes;
	}
	@Override
	public String toString() {
		return "ReceiveEntity [versionNo=" + versionNo + ", serviceNo=" + serviceNo + ", officeId=" + officeId
				+ ", userId=" + userId + ", loginName=" + loginName + ", password=" + password + ", searchDate="
				+ searchDate + ", goodsTypes=" + goodsTypes + ", list=" + list + ", authorizeList=" + authorizeList
				+ "]";
	}
	/**
	 * @return authorizeList
	 */
	public List<Map<String, Object>> getAuthorizeList() {
		return authorizeList;
	}
	/**
	 * @param authorizeList 要设置的 authorizeList
	 */
	public void setAuthorizeList(List<Map<String, Object>> authorizeList) {
		this.authorizeList = authorizeList;
	}
	
}
