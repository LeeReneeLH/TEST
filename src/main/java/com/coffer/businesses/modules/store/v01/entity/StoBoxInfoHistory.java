package com.coffer.businesses.modules.store.v01.entity;

import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.User;

/**
 * 箱袋状态变更管理
 * 
 * @author xp
 * @version 2017-7-12
 */
public class StoBoxInfoHistory extends DataEntity<StoBoxInfoHistory> {

	private static final long serialVersionUID = 1L;
	private StoBoxInfo stoBoxInfo;// 当前箱袋基本信息
	private User authorizeBy;// 授权人id
	private String authorizer;// 授权人
	private Date privilegedTime;// 授权时间
	private String updateBoxStatus;// 箱子更新后的状态
	// 查询日期的开始时间和结束时间
	private String searchDateStart;
	private String searchDateEnd;
	private Date createTimeStart;
	private Date createTimeEnd;

	public void setStoBoxInfo(StoBoxInfo stoBoxInfo) {
		this.stoBoxInfo = stoBoxInfo;
	}

	public StoBoxInfo getStoBoxInfo() {
		return stoBoxInfo;
	}

	public User getAuthorizeBy() {
		return authorizeBy;
	}

	public void setAuthorizeBy(User authorizeBy) {
		this.authorizeBy = authorizeBy;
	}

	public String getAuthorizer() {
		return authorizer;
	}

	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}

	public Date getPrivilegedTime() {
		return privilegedTime;
	}

	public void setPrivilegedTime(Date privilegedTime) {
		this.privilegedTime = privilegedTime;
	}

	public String getUpdateBoxStatus() {
		return updateBoxStatus;
	}

	public void setUpdateBoxStatus(String updateBoxStatus) {
		this.updateBoxStatus = updateBoxStatus;
	}

	/**
	 * @return the searchDateStart
	 */
	public String getSearchDateStart() {
		return searchDateStart;
	}

	/**
	 * @param searchDateStart the searchDateStart to set
	 */
	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	/**
	 * @return the searchDateEnd
	 */
	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	/**
	 * @param searchDateEnd the searchDateEnd to set
	 */
	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	/**
	 * @return the createTimeStart
	 */
	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	/**
	 * @param createTimeStart the createTimeStart to set
	 */
	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	/**
	 * @return the createTimeEnd
	 */
	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	/**
	 * @param createTimeEnd the createTimeEnd to set
	 */
	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
	

}
