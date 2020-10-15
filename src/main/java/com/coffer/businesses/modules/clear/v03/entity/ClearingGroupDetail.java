package com.coffer.businesses.modules.clear.v03.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.User;

/**
 * 清分组管理明细Entity
 * 
 * @author XL
 * @version 2017-08-14
 */
public class ClearingGroupDetail extends DataEntity<ClearingGroupDetail> {

	private static final long serialVersionUID = 1L;
	private User user; // 用户编号
	private String userName;// 用户姓名
	private ClearingGroup clearingGroupId; // 清分组id 父类
	private String userType;	// 用户类型
	public ClearingGroupDetail() {
		super();
	}

	public ClearingGroupDetail(String id) {
		super(id);
	}

	public ClearingGroupDetail(ClearingGroup clearingGroupId) {
		this.clearingGroupId = clearingGroupId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Length(min = 0, max = 64, message = "清分组编号长度必须介于 0 和 64 之间")
	public ClearingGroup getClearingGroupId() {
		return clearingGroupId;
	}

	public void setClearingGroupId(ClearingGroup clearingGroupId) {
		this.clearingGroupId = clearingGroupId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}