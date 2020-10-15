package com.coffer.businesses.modules.clear.v03.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;

/**
 * 清分组管理Entity
 * 
 * @author XL
 * @version 2017-08-14
 */
public class ClearingGroup extends DataEntity<ClearingGroup> {

	private static final long serialVersionUID = 1L;
	private String groupNo; // 分组编号
	private String groupName; // 分组名称
	private String groupType; // 业务类型（08：复点；09：清分）
	private List<ClearingGroupDetail> clearingGroupDetailList = Lists.newArrayList(); // 子表列表
	private List<User> userListSelect = Lists.newArrayList();// 可选择用户集合
	private List<User> userListForm = Lists.newArrayList();// 更新的用户集合
	private Long number;// 人数
	private String groupStatus;// 状态（0：启用；1：停用）
	private String workingPositionType;// 工位类型（01：机械清分；02：清分流水线；03：手工清分）

	/** 发生机构（清分中心） */
	private Office Office;

	/** 页面对应的开始时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开始时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	public ClearingGroup() {
		super();
	}

	public ClearingGroup(String id) {
		super(id);
	}

	public List<ClearingGroupDetail> getClearingGroupDetailList() {
		return clearingGroupDetailList;
	}

	public void setClearingGroupDetailList(List<ClearingGroupDetail> clearingGroupDetailList) {
		this.clearingGroupDetailList = clearingGroupDetailList;
	}

	public List<User> getUserListSelect() {
		return userListSelect;
	}

	public void setUserListSelect(List<User> userListSelect) {
		this.userListSelect = userListSelect;
	}

	public List<User> getUserListForm() {
		return userListForm;
	}

	public void setUserListForm(List<User> userListForm) {
		this.userListForm = userListForm;
	}

	public String getGroupStatus() {
		return groupStatus;
	}

	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	@Length(min = 0, max = 200, message = "编号长度必须介于 0 和 200 之间")
	public String getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}

	@Length(min = 0, max = 200, message = "名称长度必须介于 0 和 200 之间")
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Length(min = 0, max = 2, message = "业务类型（01：清分；02：复点）长度必须介于 0 和 2 之间")
	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getWorkingPositionType() {
		return workingPositionType;
	}

	public void setWorkingPositionType(String workingPositionType) {
		this.workingPositionType = workingPositionType;
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

	public Office getOffice() {
		return Office;
	}

	public void setOffice(Office office) {
		Office = office;
	}


}