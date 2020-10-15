package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 箱袋管理Entity
 * 
 * @author niguoyong
 * @version 2015-09-01
 */
public class StoBoxInfo extends DataEntity<StoBoxInfo> {

	private static final long serialVersionUID = 1L;

	@Expose
	private String boxNo; // 箱袋编号
	@Expose
	private String boxType; // 箱袋类型
	private String boxUse; // 箱袋用途
	private String modId; // 钞箱配置ID
	private AtmBoxMod atmBoxMod; // 钞箱配置类型
	private String boxStatus; // 箱袋状态
	private Office office; // 使用机构
	private String denomination;// 钞箱面值
	private BigDecimal pieceNum; // 钞箱张数
	private BigDecimal boxAmount; // 钞箱金额
	private Integer seqNo; // 序列号
	private String atmBoxType; // 钞箱类型
	private Date outDate; // 出库预约时间
	private String outDateStart; // 出库预约时间开始
	private String outDateEnd; // 出库预约时间结束
	@Expose
	@SerializedName("rfid")
	private String rfid; // 钞箱RFID码
	private Integer boxNum; // 箱袋数量
	private String searchBoxNo; // 箱袋数量
	private String atmBoxStatus; // 钞箱状态
	private String carStatus;// 小车状态
	private String billStatus;// 票据状态
	private String routeId;// 路线ID
	private String routeName;// 路线名称
	// private ColBranchInfo branch;

	// 接口使用
	@Expose
	private String officeName; // 机构名称
	@Expose
	private String boxStatusName; // 箱子状态名称
	@Expose
	private String boxTypeName; // 箱子类型名称
	@Expose
	@SerializedName("outDate")
	private String outTime; // 箱子类型名称

	private String no; // 序号
	private String addPlanId; // 加钞计划
	private String atmNo; // 设备编号
	private String atmAddress; // 设备地址
	private String atmAccount; // 柜员号
	private String remark; // 备注
	private String boxStatusUsed; // 箱子状态：在使用
	private String boxStatusOnload; // 箱子状态：在途
	private String boxStatusClearOnload; // 箱子状态：清机在途
	private List<String> officeIds; // 使用机构
	private List<String> boxNos; // 箱号列表
	private int boxCount = 0; // 箱袋数量
	private List<StoBoxDetail> stoBoxDetail;// 箱袋明细
	/* 追加更新时间 修改人:sg 修改日期:2017-11-08 begin */
	/** 更新时间 格式 ：yyyyMMddHHmmssSSSSSS */
	private String strUpdateDate;
	/* end */
	/* 追加所有状态 修改人:xl 修改日期:2018-01-03 begin */
	private String allBoxStatus;// 所有状态
	/* end */
	private Office searchOffice; // 使用机构 (页面查询使用,数据穿透)

	public List<StoBoxDetail> getStoBoxDetail() {
		return stoBoxDetail;
	}

	public void setStoBoxDetail(List<StoBoxDetail> stoBoxDetail) {
		this.stoBoxDetail = stoBoxDetail;
	}

	/**
	 * @return officeId
	 */
	public String getOfficeId() {
		return officeId;
	}

	/**
	 * @param officeId
	 *            要设置的 officeId
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	private String officeId; // 使用机构

	/**
	 * @return officeIds
	 */
	public List<String> getOfficeIds() {
		return officeIds;
	}

	/**
	 * @param officeIds
	 *            要设置的 officeIds
	 */
	public void setOfficeIds(List<String> officeIds) {
		this.officeIds = officeIds;
	}

	public StoBoxInfo() {
		super();
	}

	public StoBoxInfo(String id) {
		super(id);
	}

	/**
	 * @return boxNo
	 */
	@ExcelField(title = "箱袋编号")
	public String getId() {
		return boxNo;
	}

	/**
	 * @param boxNo
	 *            要设置的 boxNo
	 */
	public void setId(String id) {
		this.boxNo = id;
	}

	/**
	 * @return boxType
	 */
	@NotNull(message = "箱袋类型不能为空")
	@ExcelField(title = "箱袋类型")
	public String getBoxType() {
		return boxType;
	}

	/**
	 * @param boxType
	 *            要设置的 boxType
	 */
	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	/**
	 * @return boxUse
	 */
	public String getBoxUse() {
		return boxUse;
	}

	/**
	 * @param boxUse
	 *            要设置的 boxUse
	 */
	public void setBoxUse(String boxUse) {
		this.boxUse = boxUse;
	}

	/**
	 * @return boxStatus
	 */
	@ExcelField(title = "箱袋状态", align = 2, sort = 40, value = "boxStatus")
	public String getBoxStatus() {
		return boxStatus;
	}

	/**
	 * @param boxStatus
	 *            要设置的 boxStatus
	 */
	public void setBoxStatus(String boxStatus) {
		this.boxStatus = boxStatus;
	}

	/**
	 * @return office
	 */
	@NotNull(message = "归属机构不能为空")
	public Office getOffice() {
		return office;
	}

	/**
	 * @param office
	 *            要设置的 office
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * @return denomination
	 */
	public String getDenomination() {
		return denomination;
	}

	/**
	 * @param denomination
	 *            要设置的 denomination
	 */
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	/**
	 * @return pieceNum
	 */
	public BigDecimal getPieceNum() {
		return pieceNum;
	}

	/**
	 * @param pieceNum
	 *            要设置的 pieceNum
	 */
	public void setPieceNum(BigDecimal pieceNum) {
		this.pieceNum = pieceNum;
	}

	/**
	 * @return atmBoxAmount
	 */
	public BigDecimal getBoxAmount() {
		return boxAmount;
	}

	/**
	 * @param atmBoxAmount
	 *            要设置的 atmBoxAmount
	 */
	public void setBoxAmount(BigDecimal boxAmount) {
		this.boxAmount = boxAmount;
	}

	/**
	 * @return seqNo
	 */
	public Integer getSeqNo() {
		return seqNo;
	}

	/**
	 * @param seqNo
	 *            要设置的 seqNo
	 */
	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	/**
	 * @return rFID
	 */
	public String getRfid() {
		return rfid;
	}

	/**
	 * @param rFID
	 *            要设置的 rFID
	 */
	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	/**
	 * @return boxNum
	 */
	// @Size(min=1,max=3,message= "箱袋数量长度必须介于 1 和 3 之间")
	public Integer getBoxNum() {
		return boxNum;
	}

	/**
	 * @param boxNum
	 *            要设置的 boxNum
	 */
	public void setBoxNum(Integer boxNum) {
		this.boxNum = boxNum;
	}

	/**
	 * @return searchBoxNo
	 */
	public String getSearchBoxNo() {
		return searchBoxNo;
	}

	/**
	 * @param searchBoxNo
	 *            要设置的 searchBoxNo
	 */
	public void setSearchBoxNo(String searchBoxNo) {
		this.searchBoxNo = searchBoxNo;
	}

	/**
	 * @return atmBoxStatus
	 */
	public String getAtmBoxStatus() {
		return atmBoxStatus;
	}

	/**
	 * @param atmBoxStatus
	 *            要设置的 atmBoxStatus
	 */
	public void setAtmBoxStatus(String atmBoxStatus) {
		this.atmBoxStatus = atmBoxStatus;
	}

	/**
	 * @return carStatus
	 */
	public String getCarStatus() {
		return carStatus;
	}

	/**
	 * @param carStatus
	 *            要设置的 carStatus
	 */
	public void setCarStatus(String carStatus) {
		this.carStatus = carStatus;
	}

	/**
	 * @return billStatus
	 */
	public String getBillStatus() {
		return billStatus;
	}

	/**
	 * @param billStatus
	 *            要设置的 billStatus
	 */
	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getBoxStatusName() {
		return boxStatusName;
	}

	public void setBoxStatusName(String boxStatusName) {
		this.boxStatusName = boxStatusName;
	}

	@ExcelField(title = "序号")
	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getAddPlanId() {
		return addPlanId;
	}

	public void setAddPlanId(String addPlanId) {
		this.addPlanId = addPlanId;
	}

	@ExcelField(title = "设备编号")
	public String getAtmNo() {
		return atmNo;
	}

	public void setAtmNo(String atmNo) {
		this.atmNo = atmNo;
	}

	@ExcelField(title = "网点名称")
	public String getAtmAddress() {
		return atmAddress;
	}

	public void setAtmAddress(String atmAddress) {
		this.atmAddress = atmAddress;
	}

	@ExcelField(title = "柜员号")
	public String getAtmAccount() {
		return atmAccount;
	}

	public void setAtmAccount(String atmAccount) {
		this.atmAccount = atmAccount;
	}

	@ExcelField(title = "备注")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBoxStatusUsed() {
		return boxStatusUsed;
	}

	public void setBoxStatusUsed(String boxStatusUsed) {
		this.boxStatusUsed = boxStatusUsed;
	}

	public String getBoxStatusOnload() {
		return boxStatusOnload;
	}

	public void setBoxStatusOnload(String boxStatusOnload) {
		this.boxStatusOnload = boxStatusOnload;
	}

	public String getBoxStatusClearOnload() {
		return boxStatusClearOnload;
	}

	public void setBoxStatusClearOnload(String boxStatusClearOnload) {
		this.boxStatusClearOnload = boxStatusClearOnload;
	}

	/**
	 * @return atmBoxMod
	 */
	public AtmBoxMod getAtmBoxMod() {
		return atmBoxMod;
	}

	/**
	 * @param atmBoxMod
	 *            要设置的 atmBoxMod
	 */
	public void setAtmBoxMod(AtmBoxMod atmBoxMod) {
		this.atmBoxMod = atmBoxMod;
	}

	/**
	 * @return modId
	 */
	public String getModId() {
		return modId;
	}

	/**
	 * @param modId
	 *            要设置的 modId
	 */
	public void setModId(String modId) {
		this.modId = modId;
	}

	/**
	 * @return atmBoxType
	 */
	public String getAtmBoxType() {
		return atmBoxType;
	}

	/**
	 * @param atmBoxType
	 *            要设置的 atmBoxType
	 */
	public void setAtmBoxType(String atmBoxType) {
		this.atmBoxType = atmBoxType;
	}

	@ExcelField(title = "归属机构")
	public String getOfficenm() {
		return office.getName();
	}

	public List<String> getBoxNos() {
		return boxNos;
	}

	public void setBoxNos(List<String> boxNos) {
		this.boxNos = boxNos;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getBoxTypeName() {
		return boxTypeName;
	}

	public void setBoxTypeName(String boxTypeName) {
		this.boxTypeName = boxTypeName;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public int getBoxCount() {
		return boxCount;
	}

	public void setBoxCount(int boxCount) {
		this.boxCount = boxCount;
	}

	public String getOutDateStart() {
		return outDateStart;
	}

	public void setOutDateStart(String outDateStart) {
		this.outDateStart = outDateStart;
	}

	public String getOutDateEnd() {
		return outDateEnd;
	}

	public void setOutDateEnd(String outDateEnd) {
		this.outDateEnd = outDateEnd;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	/* 追加更新时间 修改人:sg 修改日期:2017-11-08 begin */
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
	/* end */

	public String getAllBoxStatus() {
		return allBoxStatus;
	}

	public void setAllBoxStatus(String allBoxStatus) {
		this.allBoxStatus = allBoxStatus;
	}

	public Office getSearchOffice() {
		return searchOffice;
	}

	public void setSearchOffice(Office searchOffice) {
		this.searchOffice = searchOffice;
	}

}
