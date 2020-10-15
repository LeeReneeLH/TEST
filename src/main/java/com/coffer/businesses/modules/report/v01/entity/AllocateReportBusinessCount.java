package com.coffer.businesses.modules.report.v01.entity;

import java.io.Serializable;

import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 网点现金上缴总金额报表
 * 
 * @author xp
 * @version 2017-8-24
 */

public class AllocateReportBusinessCount extends ReportCondition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 流水的登记机构：网点
	private Office rOffice;
	private String rOfficeName;
	// 总计
	private String count;
	// 上缴日期

	private String handInDate;

	public AllocateReportBusinessCount() {
		super();
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	@ExcelField(title = "机构", align = 2)
	public String getROfficeName() {
		return rOfficeName;
	}

	public void setROfficeName(String rOfficeName) {
		this.rOfficeName = rOfficeName;
	}

	@ExcelField(title = "总金额", align = 2)
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public void setHandInDate(String handInDate) {
		this.handInDate = handInDate;
	}

	@ExcelField(title = "日期", align = 2)
	public String getHandInDate() {
		return handInDate;
	}

}
