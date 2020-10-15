package com.coffer.businesses.modules.atm.v01.entity;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;

/**
 * ATM品牌参数管理Entity
 * 
 * @author Murphy
 * @version 2015-05-11
 */
public class AtmBrandsInfo extends DataEntity<AtmBrandsInfo> {

	private static final long serialVersionUID = 1L;
	private static final Integer BOX_NUM_INIT = 0;
	private String atmBrandsId; // 主键
	private String atmBrandsNo; // 品牌编号
	private String atmBrandsName; // 品牌名称
	private String atmTypeNo; // 型号编号
	private String atmTypeName; // 型号名称
	private Integer boxNum; // 钞箱总数量
	private String getBoxType; // 取款箱类型
	private Integer getBoxNumber; // 取款箱数量
	private String backBoxType; // 回收箱类型
	private Integer backBoxNumber; // 回收箱数量
	private String cycleBoxType; // 循环箱型号
	private Integer cycleBoxNumber; // 循环箱数量
	private String depositBoxType; // 存款箱型号
	private Integer depositBoxNumber; // 存款箱数量

	public AtmBrandsInfo() {
		super();
		this.getBoxNumber = BOX_NUM_INIT;
		this.backBoxNumber = BOX_NUM_INIT;
		this.cycleBoxNumber = BOX_NUM_INIT;
		this.depositBoxNumber = BOX_NUM_INIT;
		this.boxNum = BOX_NUM_INIT;
	}
	
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

	public AtmBrandsInfo(String id) {
		super(id);
	}

	public String getId() {
		return atmBrandsId;
	}

	public void setId(String id) {
		this.atmBrandsId = id;
	}

	@Length(min = 1, max = 10, message = "品牌编号长度必须介于 1 和 10 之间")
	public String getAtmBrandsNo() {
		return atmBrandsNo;
	}

	public void setAtmBrandsNo(String atmBrandsNo) {
		this.atmBrandsNo = atmBrandsNo;
	}

	@Length(min = 1, max = 50, message = "品牌名称长度必须介于 1 和 50 之间")
	public String getAtmBrandsName() {
		return atmBrandsName;
	}

	public void setAtmBrandsName(String atmBrandsName) {
		this.atmBrandsName = atmBrandsName;
	}

	@Length(min = 1, max = 10, message = "型号编号长度必须介于 1 和 10 之间")
	public String getAtmTypeNo() {
		return atmTypeNo;
	}

	public void setAtmTypeNo(String atmTypeNo) {
		this.atmTypeNo = atmTypeNo;
	}

	@Length(min = 1, max = 50, message = "型号名称长度必须介于 1 和 50 之间")
	public String getAtmTypeName() {
		return atmTypeName;
	}

	public void setAtmTypeName(String atmTypeName) {
		this.atmTypeName = atmTypeName;
	}

	@NotNull(message = "钞箱总数量不能为空")
	public Integer getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(Integer boxNum) {
		this.boxNum = boxNum;
	}

	@Length(min = 0, max = 10, message = "取款箱类型长度必须介于 0 和 10 之间")
	public String getGetBoxType() {
		return getBoxType;
	}

	public void setGetBoxType(String getBoxType) {
		this.getBoxType = getBoxType;
	}

	@NotNull(message = "取款箱数量不能为空")
	public Integer getGetBoxNumber() {
		return getBoxNumber;
	}

	public void setGetBoxNumber(Integer getBoxNumber) {
		this.getBoxNumber = getBoxNumber;
	}

	@Length(min = 0, max = 10, message = "回收箱类型长度必须介于 0 和 10 之间")
	public String getBackBoxType() {
		return backBoxType;
	}

	public void setBackBoxType(String backBoxType) {
		this.backBoxType = backBoxType;
	}

	@NotNull(message = "回收箱数量不能为空")
	public Integer getBackBoxNumber() {
		return backBoxNumber;
	}

	public void setBackBoxNumber(Integer backBoxNumber) {
		this.backBoxNumber = backBoxNumber;
	}

	@Length(min = 0, max = 10, message = "循环箱型号长度必须介于 0 和 10 之间")
	public String getCycleBoxType() {
		return cycleBoxType;
	}

	public void setCycleBoxType(String cycleBoxType) {
		this.cycleBoxType = cycleBoxType;
	}

	@NotNull(message = "循环箱数量不能为空")
	public Integer getCycleBoxNumber() {
		return cycleBoxNumber;
	}

	public void setCycleBoxNumber(Integer cycleBoxNumber) {
		this.cycleBoxNumber = cycleBoxNumber;
	}

	@Length(min = 0, max = 10, message = "存款箱型号长度必须介于 0 和 10 之间")
	public String getDepositBoxType() {
		return depositBoxType;
	}

	public void setDepositBoxType(String depositBoxType) {
		this.depositBoxType = depositBoxType;
	}

	@NotNull(message = "存款箱数量不能为空")
	public Integer getDepositBoxNumber() {
		return depositBoxNumber;
	}

	public void setDepositBoxNumber(Integer depositBoxNumber) {
		this.depositBoxNumber = depositBoxNumber;
	}

	@Override
	public String toString() {
		return "AtmBrandsInfo [atmBrandsId=" + atmBrandsId + ", atmBrandsNo=" + atmBrandsNo + ", atmTypeNo=" + atmTypeNo
				+ ", boxNum=" + boxNum + "]";
	}

}