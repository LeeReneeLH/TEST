package com.coffer.businesses.modules.report.v01.entity;

import java.io.Serializable;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 箱袋统计图对应的实体类
 * 
 * @author wh
 *
 */
public class StoBoxInfoGraphEntity implements Serializable {

	/** serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 机构 */
	private Office office;
	/** 机构名字 */
	private String officeName;
	/** 箱袋类型(数字) */
	private String boxType;
	/** 箱袋类型(汉字) */
	private String boxTypeName;
	/** 箱袋状态 (数字) */
	private String boxStatus;
	/** 箱袋状态 (汉字) */
	private String boxStatusName;
	/** 箱袋总数 */
	private String boxNum;

	private String dbName;

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@ExcelField(title = "机构", align = 2)
	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	public String getBoxStatus() {
		return boxStatus;
	}

	public void setBoxStatus(String boxStatus) {
		this.boxStatus = boxStatus;
	}

	@ExcelField(title = "数量", align = 2)
	public String getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}

	@ExcelField(title = "箱袋类型", align = 2)
	public String getBoxTypeName() {
		return boxTypeName;
	}

	public void setBoxTypeName(String boxTypeName) {
		this.boxTypeName = boxTypeName;
	}

	@ExcelField(title = "箱袋状态", align = 2)
	public String getBoxStatusName() {
		return boxStatusName;
	}

	public void setBoxStatusName(String boxStatusName) {
		this.boxStatusName = boxStatusName;
	}

	/**
	 * 获取数据库名称
	 */
	@JsonIgnore
	public String getDbName() {
		return Global.getConfig("jdbc.type");
	}
}
