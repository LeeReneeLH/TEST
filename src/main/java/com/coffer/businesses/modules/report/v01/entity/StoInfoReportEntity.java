/**
 * wenjian:    StoInfoReportEntity.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月24日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月24日 下午3:55:52
 */
package com.coffer.businesses.modules.report.v01.entity;

import java.io.Serializable;
import java.util.Date;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
* Title: StoInfoReportEntity 
* <p>Description: 库存报表用Entity</p>
* @author wangbaozhong
* @date 2017年8月24日 下午3:55:52
*/
public class StoInfoReportEntity implements Serializable {
	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 机构 */
	private Office office;
	/** 总金额 */
	private String totalAmount;
	/** 日期 */
	private String strDate;
	/** 物品 */
	private String goodsName;
	/** 查询条件机构ID */
	private String officeId;
	/** 查询条件开始日期*/
	private Date createTimeStart;
	/** 查询条件结束日期*/
	private Date createTimeEnd;
	/** SQL查询条件开始日期*/
	private String searchDateStart;
	/** SQL查询条件结束日期*/
	private String searchDateEnd;
	/** 数据过滤条件*/
	private String filterCondition;

	private String dbName;

	/**
	 * @return the office
	 */
	public Office getOffice() {
		return office;
	}

	/**
	 * @param office the office to set
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * @return the totalAmount
	 */
	@ExcelField(title = "总金额")
	public String getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the strDate
	 */
	@ExcelField(title = "日期")
	public String getStrDate() {
		return strDate;
	}

	/**
	 * @param strDate the strDate to set
	 */
	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	/**
	 * @return the officeId
	 */
	public String getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId the officeId to set
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
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
	 * @return the filterCondition
	 */
	public String getFilterCondition() {
		return filterCondition;
	}

	/**
	 * @param filterCondition the filterCondition to set
	 */
	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	@ExcelField(title = "物品")
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	@JsonIgnore
	public String getDbName() {
		return Global.getConfig("jdbc.type");
	}
}
