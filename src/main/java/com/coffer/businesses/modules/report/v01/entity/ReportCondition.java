package com.coffer.businesses.modules.report.v01.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.config.Global;
import com.coffer.core.modules.sys.entity.Office;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 网点现金上缴业务量公用属性
 * 
 * @author xp
 * @version 2017-8-24
 */

public class ReportCondition implements Serializable {

	private static final long serialVersionUID = 1L;
	// 查询的机构
	private Office office;
	// 查询机构ID
	private String officeId;
	// 流水接收机构
	private Office aOffice;
	// 流水的登记机构：网点
	private Office rOffice;
	// 查询状态 0年1季度2月3周
	private String filterCondition;
	// 查询日期的开始时间和结束时间
	private String searchDateStart;
	private String searchDateEnd;
	private Date createTimeStart;
	private Date createTimeEnd;
	// 上缴日期
	private String handInDate;
	// 业务类型
	private List<String> businessTypes;
	// 有效标识 0 有效 1 无效
	private String delFlag;
	// 业务类型
	private String businessType;
	// 现金业务类型 1 常规业务 2 临时现金预约业务
	private String type;
	// 业务状态
	private String status;
	// 业务状态列表
	private List<String> statuses;
	public ReportCondition() {
		super();
	}

	public Office getaOffice() {
		return aOffice;
	}

	public void setaOffice(Office aOffice) {
		this.aOffice = aOffice;
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
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

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getHandInDate() {
		return handInDate;
	}

	public void setHandInDate(String handInDate) {
		this.handInDate = handInDate;
	}

	/**
	 * 获取数据库名称
	 */
	@JsonIgnore
	public String getDbName() {
		return Global.getConfig("jdbc.type");
	}

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}
}
