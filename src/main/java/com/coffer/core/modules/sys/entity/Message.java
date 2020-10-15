package com.coffer.core.modules.sys.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;

/**
 * 系统通知Entity
 * 
 * @author yanbingxu
 * @version 2017-9-27
 */
public class Message extends DataEntity<Message> {

	private static final long serialVersionUID = 1L;

	/** 消息主题 **/
	private String messageTopic;
	/** 主体 */
	private String messageBody;
	/** 用户权限 **/
	private String userAuthority;
	/** 用户权限 **/
	private String officeAuthority;
	/** 菜单号 **/
	private String menuId;
	/** 消息类型 **/
	private String messageType;
	/** 业务类型 **/
	private String businessType;
	/** 业务状态 **/
	private String businessStatus;
	/** 消息类型名称 **/
	private String messageTypeName;
	/** 消息格式 **/
	private String messageConstruct;
	/** 链接 **/
	private String url;
	/** 初始显示日期间隔 **/
	private String dateRange;
	/** 初始显示条数 **/
	private int maxNumber;
	/** 撤回备注 **/
	private String cancelReason;
	/** 用户权限list **/
	private List<Dict> userAuthorityList = Lists.newArrayList();
	/** 机构权限list **/
	private List<Office> officeAuthorityList = Lists.newArrayList();
	/** 查询时间 **/
	private Date searchDate;

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserAuthority() {
		return userAuthority;
	}

	public void setUserAuthority(String userAuthority) {
		this.userAuthority = userAuthority;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Dict> getUserAuthorityList() {
		return userAuthorityList;
	}

	public void setUserAuthorityList(List<Dict> userAuthorityList) {
		this.userAuthorityList = userAuthorityList;
	}

	public List<Office> getOfficeAuthorityList() {
		return officeAuthorityList;
	}

	public void setOfficeAuthorityList(List<Office> officeAuthorityList) {
		this.officeAuthorityList = officeAuthorityList;
	}

	public String getMessageTopic() {
		return messageTopic;
	}

	public void setMessageTopic(String messageTopic) {
		this.messageTopic = messageTopic;
	}

	public String getMessageTypeName() {
		return messageTypeName;
	}

	public void setMessageTypeName(String messageTypeName) {
		this.messageTypeName = messageTypeName;
	}

	public String getMessageConstruct() {
		return messageConstruct;
	}

	public void setMessageConstruct(String messageConstruct) {
		this.messageConstruct = messageConstruct;
	}

	public String getOfficeAuthority() {
		return officeAuthority;
	}

	public void setOfficeAuthority(String officeAuthority) {
		this.officeAuthority = officeAuthority;
	}

	public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getBusinessStatus() {
		return businessStatus;
	}

	public void setBusinessStatus(String businessStatus) {
		this.businessStatus = businessStatus;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	/**
	 * @return the maxNumber
	 */
	public int getMaxNumber() {
		return maxNumber;
	}

	/**
	 * @param maxNumber the maxNumber to set
	 */
	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
	}

	/**
	 * @return the searchDate
	 */
	public Date getSearchDate() {
		return searchDate;
	}

	/**
	 * @param searchDate the searchDate to set
	 */
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

}
