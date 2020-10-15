package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.User;

/**
 * 清机组人员明细Entity
 * @author ZXK
 * @version 2019-07-24
 *
 */
public class ClearGroupUserInfo extends DataEntity<ClearGroupUserInfo>{

	private static final long serialVersionUID = 1L;
	private String clearGroupId; //清机组编号
	private String userId; //清机人员id
	private String userName; //清机人员名称
	private User user; 
	
	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getClearGroupId() {
		return clearGroupId;
	}
	public void setClearGroupId(String clearGroupId) {
		this.clearGroupId = clearGroupId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
