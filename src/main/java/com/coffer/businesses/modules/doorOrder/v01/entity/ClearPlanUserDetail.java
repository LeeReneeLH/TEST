package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.User;

/**
 * 清机任务人员明细Entity
 * 
 * @author XL
 * @version 2019-08-12
 */
public class ClearPlanUserDetail extends DataEntity<ClearPlanUserDetail> {

	private static final long serialVersionUID = 1L;
	private String planId; // 清机任务编号
	private User user; // 清机人员

	public ClearPlanUserDetail() {
		super();
	}

	public ClearPlanUserDetail(String id) {
		super(id);
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}