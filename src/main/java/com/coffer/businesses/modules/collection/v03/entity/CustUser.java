package com.coffer.businesses.modules.collection.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 上门收款-客户Entity
 * 
 * @author wanglin
 * @version 2017-11-13
 */
public class CustUser extends DataEntity<CustUser>  {

	private static final long serialVersionUID = 1L;

	private String storeId; 	// 商户ID
	private String storeName; 	// 商户名
	private String loginName; 	// 登录名
	private String password; 	// 密码
	private String no; 			// 工号
	private String name; 		// 姓名
	private String idcardNo; 	// 身份证号
	private String phone; 		// 电话
	private String mobile; 		// 手机
	private String email; 		// 邮箱
	private String userType; 	// 客户类型(1:系统管理员,2:商户管理员)
	private String remarks; 	// 备注
	private String newPassword; // 新密码
	private String oldLoginName;// 原登录名
	
	public CustUser() {
		super();
	}

	public CustUser(String id) {
		super(id);
		this.id = id;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
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

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdcardNo() {
		return idcardNo;
	}

	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getOldLoginName() {
		return oldLoginName;
	}

	public void setOldLoginName(String oldLoginName) {
		this.oldLoginName = oldLoginName;
	}


}