package com.coffer.businesses.modules.store.v02.entity;

import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;

/**
 * 机构印章Entity
 * @author Zhengkaiyuan
 * @version 2016-9-9
 */
public class StoOfficeStamperInfo extends DataEntity<StoOfficeStamperInfo> {

	private static final long serialVersionUID = 1L;
	/** 所属机构 **/
	private Office office;
	/** 印章类型 **/
	private String officeStamperType;
	/** 印章数据 **/
	private byte[] officeStamper;
	/** 印章名称 **/
	private String stamperName;
	/** 印章的操作类型 **/
	private String type;
	/** 错误异常代码 **/
	private String errorMsg;
	/** 印章列表 **/
	private List<StoOfficeStamperInfo> stamperList;
	/** 印章高度 **/
	private Integer stamperHeight;
	/** 印章宽度 **/
	private Integer stamperWidth;
	/** 印章是否上传  0-未上传  1-已上传 **/
	private String isStamperUpload;
	/** 操作用户 **/
	private User user;
	/**
	 * @return the office
	 */
	public Office getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(Office office) {
		this.office = office;
	}
	/**
	 * @return the officeStamperType
	 */
	public String getOfficeStamperType() {
		return officeStamperType;
	}
	/**
	 * @param officeStamperType the officeStamperType to set
	 */
	public void setOfficeStamperType(String officeStamperType) {
		this.officeStamperType = officeStamperType;
	}
	/**
	 * @return the officeStamper
	 */
	public byte[] getOfficeStamper() {
		return officeStamper;
	}
	/**
	 * @param officeStamper the officeStamper to set
	 */
	public void setOfficeStamper(byte[] officeStamper) {
		this.officeStamper = officeStamper;
	}
	/**
	 * @return the stamperName
	 */
	public String getStamperName() {
		return stamperName;
	}
	/**
	 * @param stamperName the stamperName to set
	 */
	public void setStamperName(String stamperName) {
		this.stamperName = stamperName;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	/**
	 * @return the stamperList
	 */
	public List<StoOfficeStamperInfo> getStamperList() {
		return stamperList;
	}
	/**
	 * @param stamperList the stamperList to set
	 */
	public void setStamperList(List<StoOfficeStamperInfo> stamperList) {
		this.stamperList = stamperList;
	}
	/**
	 * @return the stamperHeight
	 */
	public Integer getStamperHeight() {
		return stamperHeight;
	}
	/**
	 * @param stamperHeight the stamperHeight to set
	 */
	public void setStamperHeight(Integer stamperHeight) {
		this.stamperHeight = stamperHeight;
	}
	/**
	 * @return the stamperWidth
	 */
	public Integer getStamperWidth() {
		return stamperWidth;
	}
	/**
	 * @param stamperWidth the stamperWidth to set
	 */
	public void setStamperWidth(Integer stamperWidth) {
		this.stamperWidth = stamperWidth;
	}
	/**
	 * @return the isStamperUpload
	 */
	public String getIsStamperUpload() {
		return isStamperUpload;
	}
	/**
	 * @param isStamperUpload the isStamperUpload to set
	 */
	public void setIsStamperUpload(String isStamperUpload) {
		this.isStamperUpload = isStamperUpload;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	

	
}
