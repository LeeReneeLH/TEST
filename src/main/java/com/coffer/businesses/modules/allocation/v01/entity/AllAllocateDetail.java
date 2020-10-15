package com.coffer.businesses.modules.allocation.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 调拨详细表Entity
 * 
 * @author Chengshu
 * @version 2015-05-11
 */
public class AllAllocateDetail extends DataEntity<AllAllocateDetail> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private String allDetailId;
	/**
	 * 缴调流水号
	 */
	private String allId;
	/**
	 * 箱号
	 */
	private String boxNo;
	/**
	 * 箱袋类型
	 */
	private String boxType;
	/**
	 * 金额
	 */
	private BigDecimal amount;
	/**
	 * 扫描状态（0：未扫描，1：已扫描，）
	 */
	private String scanFlag;
	/**
	 * 箱袋位置
	 */
	private String place;
	/**
	 * 路线ID
	 */
	private String routeId;
	
	/**
	 * 箱袋所属机构ID
	 */
	private String boxOfficeId;
	
	/** 流水号 **/
	private AllAllocateInfo allocationInfo;
	/** 面额 **/
	private String denomination;
	
	
	/** 箱袋状态 **/
	private String status;
	/** 备注 **/
	private String remark;
	
	/** 出入库 **/
	private String inOutType;
	
	/** 出库预约时间 **/
	private Date outDate;
	
	
	/** 日期 **/
	private String time = "";
	/** 交接日期 **/
	private String handoverTime = "";
	/** 出入库日期 **/
	private String inOutTime = "";
	/** 钞箱编号 **/
	private String atmBoxId = "";
	/** 出库数量 **/
	private String outCount = "";
	/** 入库数量 **/
	private String inCount = "";
	/** 交接人姓名 **/
	private String handoverUserName = "";
	/** 押运人姓名 **/
	private String escortUserName = "";
	
	/** 出库金额合计 **/
	private BigDecimal outAmountCount;
	/** 入库金额合计 **/
	private BigDecimal inAmountCount;
	
	/** 物品ID **/
	private String goodsId;
	/** 物品数量 **/
	private String goodsNum;
	/** 物品金额 **/
	private BigDecimal goodsAmount;
	/** 扫描时间 **/
	private Date scanDate;
	/** PDA扫描时间 **/
	private Date pdaScanDate;
	
	private String rfid;
	
	/** 网点交接箱子扫描状态 0：未扫描 1：已扫描 */
	private String outletsScanFlag = "0";

	/** 网点交接PDA扫描时间 */
	private Date outletsPdaScanDate;

	public AllAllocateDetail() {
		super();
	}

	public AllAllocateDetail(String id){
		super(id);
	}

	public AllAllocateDetail(AllAllocateInfo allocationInfo){
		this.allocationInfo = allocationInfo;
	}
	
	public String getId() {
		return allDetailId;
	}

	public void setId(String allDetailId) {
		this.allDetailId = allDetailId;
	}
	
	public String getAllDetailId() {
		return allDetailId;
	}

	public void setAllDetailId(String allDetailId) {
		this.allDetailId = allDetailId;
	}

	@JsonIgnore
	public AllAllocateInfo getAllocationInfo() {
		return allocationInfo;
	}

	public void setAllocationInfo(AllAllocateInfo allocationInfo) {
		this.allocationInfo = allocationInfo;
	}


	
	@JsonIgnore
	@ExcelField(title = "券别")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	@JsonIgnore
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@JsonIgnore
	public String getScanFlag() {
		return scanFlag;
	}

	public void setScanFlag(String scanFlag) {
		this.scanFlag = scanFlag;
	}
	
	@JsonIgnore
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@ExcelField(title = "备注")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@ExcelField(title = "钞箱类型")
	public String getBoxType() {
		return boxType;
	}

	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}

	@ExcelField(title = "出入库")
	public String getInOutType() {
		return inOutType;
	}

	public void setInOutType(String inOutType) {
		this.inOutType = inOutType;
	}

	@ExcelField(title = "交接时间")
	public String getHandoverTime() {
		return handoverTime;
	}

	public void setHandoverTime(String handoverTime) {
		this.handoverTime = handoverTime;
	}

	@ExcelField(title = "出入库时间")
	public String getInOutTime() {
		return inOutTime;
	}

	public void setInOutTime(String inOutTime) {
		this.inOutTime = inOutTime;
	}

	@ExcelField(title = "钞箱编号")
	public String getAtmBoxId() {
		return atmBoxId;
	}

	public void setAtmBoxId(String atmBoxId) {
		this.atmBoxId = atmBoxId;
	}

	@ExcelField(title = "钞箱数量(出库)")
	public String getOutCount() {
		return outCount;
	}

	public void setOutCount(String outCount) {
		this.outCount = outCount;
	}

	@ExcelField(title = "钞箱数量(入库)")
	public String getInCount() {
		return inCount;
	}

	public void setInCount(String inCount) {
		this.inCount = inCount;
	}

	@ExcelField(title = "移交人")
	public String getHandoverUserName() {
		return handoverUserName;
	}

	public void setHandoverUserName(String handoverUserName) {
		this.handoverUserName = handoverUserName;
	}

	@ExcelField(title = "接收人")
	public String getEscortUserName() {
		return escortUserName;
	}

	public void setEscortUserName(String escortUserName) {
		this.escortUserName = escortUserName;
	}

	@ExcelField(title = "日期")
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@ExcelField(title = "金额(出库)")
	public BigDecimal getOutAmountCount() {
		return outAmountCount;
	}

	public void setOutAmountCount(BigDecimal outAmountCount) {
		this.outAmountCount = outAmountCount;
	}

	@ExcelField(title = "金额(入库)")
	public BigDecimal getInAmountCount() {
		return inAmountCount;
	}

	public void setInAmountCount(BigDecimal inAmountCount) {
		this.inAmountCount = inAmountCount;
	}

	/**
	 * 取得箱号
	 * 
	 * @return boxNo 箱号
	 */
	public String getBoxNo() {
		return boxNo;
	}

	/**
	 * 设定箱号
	 * 
	 * @param boxNo
	 *            要设置的 boxNo
	 */
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	/**
	 * 取得缴调流水号
	 * 
	 * @return allId 缴调流水号
	 */
	public String getAllId() {
		return allId;
	}

	/**
	 * 设定缴调流水号
	 * 
	 * @param allId
	 *            要设置的 allId
	 */
	public void setAllId(String allId) {
		this.allId = allId;
	}

	/**
	 * 取得箱袋位置
	 * 
	 * @return place 箱袋位置
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * 设定箱袋位置
	 * 
	 * @param place
	 *            要设置的 place
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	public String getBoxOfficeId() {
		return boxOfficeId;
	}

	public void setBoxOfficeId(String boxOfficeId) {
		this.boxOfficeId = boxOfficeId;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public Date getOutDate() {
		return outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}

	public BigDecimal getGoodsAmount() {
		return goodsAmount;
	}

	public void setGoodsAmount(BigDecimal goodsAmount) {
		this.goodsAmount = goodsAmount;
	}

	public Date getScanDate() {
		return scanDate;
	}

	public void setScanDate(Date scanDate) {
		this.scanDate = scanDate;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public Date getPdaScanDate() {
		return pdaScanDate;
	}

	public void setPdaScanDate(Date pdaScanDate) {
		this.pdaScanDate = pdaScanDate;
	}

	public String getOutletsScanFlag() {
		return outletsScanFlag;
	}

	public void setOutletsScanFlag(String outletsScanFlag) {
		this.outletsScanFlag = outletsScanFlag;
	}

	public Date getOutletsPdaScanDate() {
		return outletsPdaScanDate;
	}

	public void setOutletsPdaScanDate(Date outletsPdaScanDate) {
		this.outletsPdaScanDate = outletsPdaScanDate;
	}
	
}