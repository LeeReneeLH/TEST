package com.coffer.businesses.modules.report.v01.entity;

import java.io.Serializable;

import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 网点现金上缴业务量报表
 * 
 * @author xp
 * @version 2017-8-24
 */

public class AllocateReportBusinessDegree extends ReportCondition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ExcelField(title = "机构", align = 2)
	private String rOfficeName;
	// 上缴日期
	@ExcelField(title = "日期", align = 2)
	private String handInDate;
	// 上缴次数
	@ExcelField(title = "业务量", align = 2)
	private String degree;
	// 总计
	@ExcelField(title = "总金额", align = 2)
	private String count;

	@ExcelField(title = "接收机构", align = 2)
	private String aOfficeName;

	public AllocateReportBusinessDegree() {
		super();
	}

	public String getHandInDate() {
		return handInDate;
	}

	public void setHandInDate(String handInDate) {
		this.handInDate = handInDate;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getROfficeName() {
		return rOfficeName;
	}

	public void setROfficeName(String rOfficeName) {
		this.rOfficeName = rOfficeName;
	}

	/**
	 * @return the count
	 */
	public String getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * @return the aOfficeName
	 */
	public String getAOfficeName() {
		return aOfficeName;
	}

	/**
	 * @param aOfficeName
	 *            the aOfficeName to set
	 */
	public void setAOfficeName(String aOfficeName) {
		this.aOfficeName = aOfficeName;
	}

}
