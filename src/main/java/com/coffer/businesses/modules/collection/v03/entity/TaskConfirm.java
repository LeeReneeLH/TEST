package com.coffer.businesses.modules.collection.v03.entity;

import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 任务确认Entity
 * @author wanglin
 * @version 2017-02-13
 */
public class TaskConfirm extends DataEntity<TaskConfirm> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;			// 预约单号
	private String doorId;			// 门店ID
	private String doorName;		// 门店名称
	private String amount;			// 总金额
	private String acceptBy;		// 任务接收人
	private Date acceptDate;		// 任务接收日期
	private String acceptName;		// 任务接收人员
	private String status;			// 状态（0：登记；1：待清分）
	private String method;			// 申请方式（1：PC端；2：微信端）
	private String orderDate;		// 预约日期
	private String allotStatus;		// 分配状态（0：未分配；1：已分配；2：已确认；3：驳回）
	private String allotManNo;		// 分配人
	private String allotManName;	// 分配人
	private Date allotDate;			// 分配日期
	private String clearManNo;		// 清分人
	private String clearManName;	// 清分人
	private String totalCount;		// 总笔数
	
	//检索用
	private String searchDoorId;		// 门店ID
	private String searchDoorName;		// 门店名称
	private String searchClearManNo;	// 清分人
	private String searchAllotStatus;	// 分配状态
	
	/** 清分中心 修改人：XL 日期：2019-07-04 */
	private Office office;
	
	/** 时间（查询用） 修改人：XL 日期：2019-07-04 */
	private Date createTimeStart;
	private Date createTimeEnd;
	private String searchDateStart;
	private String searchDateEnd;
	
	/** 包号 修改人：XL 日期：2019-07-04 */
	private String rfid;

	public String getSearchDoorId() {
		return searchDoorId;
	}

	public void setSearchDoorId(String searchDoorId) {
		this.searchDoorId = searchDoorId;
	}

	public String getSearchDoorName() {
		return searchDoorName;
	}

	public void setSearchDoorName(String searchDoorName) {
		this.searchDoorName = searchDoorName;
	}

	public TaskConfirm() {
		super();
	}

	public TaskConfirm(String id){
		super(id);
	}

	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}
	
	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getAcceptBy() {
		return acceptBy;
	}

	public void setAcceptBy(String acceptBy) {
		this.acceptBy = acceptBy;
	}
	
	public String getAcceptName() {
		return acceptName;
	}

	public void setAcceptName(String acceptName) {
		this.acceptName = acceptName;
	}
	
	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}


	public String getAllotStatus() {
		return allotStatus;
	}

	public void setAllotStatus(String allotStatus) {
		this.allotStatus = allotStatus;
	}

	public String getAllotManNo() {
		return allotManNo;
	}

	public void setAllotManNo(String allotManNo) {
		this.allotManNo = allotManNo;
	}

	public Date getAllotDate() {
		return allotDate;
	}

	public void setAllotDate(Date allotDate) {
		this.allotDate = allotDate;
	}

	public String getClearManNo() {
		return clearManNo;
	}

	public void setClearManNo(String clearManNo) {
		this.clearManNo = clearManNo;
	}

	public String getClearManName() {
		return clearManName;
	}

	public void setClearManName(String clearManName) {
		this.clearManName = clearManName;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}


	public String getSearchClearManNo() {
		return searchClearManNo;
	}

	public void setSearchClearManNo(String searchClearManNo) {
		this.searchClearManNo = searchClearManNo;
	}

	public String getAllotManName() {
		return allotManName;
	}

	public void setAllotManName(String allotManName) {
		this.allotManName = allotManName;
	}

	public String getSearchAllotStatus() {
		return searchAllotStatus;
	}

	public void setSearchAllotStatus(String searchAllotStatus) {
		this.searchAllotStatus = searchAllotStatus;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
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

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
}