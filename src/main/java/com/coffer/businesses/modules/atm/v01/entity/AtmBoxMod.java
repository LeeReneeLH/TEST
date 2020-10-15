package com.coffer.businesses.modules.atm.v01.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 钞箱类型配置Entity
 * 
 * @author Murphy
 * @version 2015-05-15
 */
public class AtmBoxMod extends DataEntity<AtmBoxMod> {

	private static final long serialVersionUID = 1L;
	private String modId; // 主键
	private String modName; // 钞箱类型名
	private Office office; // 机构
	private String atmBrandsNo; // ATM机品牌编号
	private String atmBrandsName;// ATM机品牌名称
	private String atmTypeName;// ATM机型号名称
	private String atmTypeNo; // ATM机型号编号
	private String boxTypeNo; // 钞箱型号
	private String boxType; // 钞箱类型（1：回收箱；2：取款箱；3：存款箱；4：循环箱）
	private String officeId;// 机构id
	
	/** 更新时间 格式 ：yyyyMMddHHmmssSSSSSS */
	private String strUpdateDate;
	
	/**
	 * @return the strUpdateDate
	 */
	public String getStrUpdateDate() {

		if (StringUtils.isBlank(strUpdateDate) && this.updateDate != null) {
			this.strUpdateDate = DateUtils.formatDate(this.updateDate, Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
		}

		return strUpdateDate;
	}

	/**
	 * @param strUpdateDate
	 *            the strUpdateDate to set
	 */
	public void setStrUpdateDate(String strUpdateDate) {
		this.strUpdateDate = strUpdateDate;
	}

	public AtmBoxMod() {
		super();
	}

	public AtmBoxMod(String id) {
		super(id);
	}

	public String getId() {
		return modId;
	}

	public void setId(String modId) {
		this.modId = modId;
	}

	@Length(min = 0, max = 20, message = "钞箱类型名长度必须介于 0 和 20 之间")
	public String getModName() {
		return modName;
	}

	public void setModName(String modName) {
		this.modName = modName;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Length(min = 0, max = 20, message = "ATM机品牌编号长度必须介于 0 和 20 之间")
	public String getAtmBrandsNo() {
		return atmBrandsNo;
	}

	public void setAtmBrandsNo(String atmBrandsNo) {
		this.atmBrandsNo = atmBrandsNo;
	}

	public String getAtmBrandsName() {
		return atmBrandsName;
	}

	public void setAtmBrandsName(String atmBrandsName) {
		this.atmBrandsName = atmBrandsName;
	}

	public String getAtmTypeName() {
		return atmTypeName;
	}

	public void setAtmTypeName(String atmTypeName) {
		this.atmTypeName = atmTypeName;
	}

	@Length(min = 0, max = 20, message = "ATM机型号编号长度必须介于 0 和 20 之间")
	public String getAtmTypeNo() {
		return atmTypeNo;
	}

	public void setAtmTypeNo(String atmTypeNo) {
		this.atmTypeNo = atmTypeNo;
	}

	@Length(min = 0, max = 20, message = "钞箱型号长度必须介于 0 和 20 之间")
	public String getBoxTypeNo() {
		return boxTypeNo;
	}

	public void setBoxTypeNo(String boxTypeNo) {
		this.boxTypeNo = boxTypeNo;
	}

	@Length(min = 0, max = 1, message = "钞箱类型（1：回收箱；2：取款箱；3：存款箱；4：循环箱）长度必须介于 0 和 1 之间")
	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

}