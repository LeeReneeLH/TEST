package com.coffer.businesses.modules.report.v01.entity;

import java.io.Serializable;
import java.util.List;

import com.coffer.core.modules.sys.entity.Office;

/**
 * 清分业务报表
 * 
 * @author xp
 * @version 2017-11-16
 */

public class ClearReportAmount extends ReportCondition implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/** 业务完成日期 */
	/* 修改date为dates wzj 2017-12-12 begin */
	private String dates;
	/* end */
	/** 出入库的金额 */
	private String amount;
	/** 入库业务类型 */
	private List<String> inStatuses;
	/** 出库业务类型 */
	private List<String> outStatuses;
	/** 出入库业务状态 */
	private String status;
	/** 发生机构列表 */
	private List<Office> rOfficeList;
	/** 人行下子机构列表 */
	private List<Office> officeList;
	/** 客户编号 */
	private String custNo;
	/** 业务类型 */
	private String busType;

	public ClearReportAmount() {
		super();
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public List<String> getInStatuses() {
		return inStatuses;
	}

	public void setInStatuses(List<String> inStatuses) {
		this.inStatuses = inStatuses;
	}

	public List<String> getOutStatuses() {
		return outStatuses;
	}

	public void setOutStatuses(List<String> outStatuses) {
		this.outStatuses = outStatuses;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Office> getrOfficeList() {
		return rOfficeList;
	}

	public void setrOfficeList(List<Office> rOfficeList) {
		this.rOfficeList = rOfficeList;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getDate() {
		return dates;
	}

	public void setDate(String dates) {
		this.dates = dates;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public List<Office> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Office> officeList) {
		this.officeList = officeList;
	}

}
