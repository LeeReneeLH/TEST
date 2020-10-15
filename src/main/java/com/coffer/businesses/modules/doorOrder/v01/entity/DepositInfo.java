package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 机构存款实体类
 * 
 * @author lihe
 * @version 2019-07-10
 */
public class DepositInfo extends DataEntity<DepositInfo> {

	private static final long serialVersionUID = 1L;
	private String doorId; // 门店编号
	private String doorName; // 门店名称
	private String orderId; // 订单编号
	private String depositAmount; // 存款金额
	private String depositDate; // 存款时间
	private String equipmentId; // 机具编号
	private String depositNo; // 柜员号
	private String depositName; // 柜员名称
	private String tickerNo; // 凭条号
	private String uploadDate; // 图片上传时间
	private String photo; // 凭条图片
	/** 业务类型列表 */
	private List<String> businessTypes;
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	/** 是否需要上传图片标志 */
	private String uploadFlag;
	
	/** add by guojian start 2020-05-25 */
	/** 显示包号 **/
	private String rfid;
	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	/** add by guojian end 2020-05-25 */

	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	private List<DepositInfoDetail> detailList;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
	}

	public String getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(String depositDate) {
		this.depositDate = depositDate;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getDepositNo() {
		return depositNo;
	}

	public void setDepositNo(String depositNo) {
		this.depositNo = depositNo;
	}

	public String getDepositName() {
		return depositName;
	}

	public void setDepositName(String depositName) {
		this.depositName = depositName;
	}

	public String getTickerNo() {
		return tickerNo;
	}

	public void setTickerNo(String tickerNo) {
		this.tickerNo = tickerNo;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
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

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public List<DepositInfoDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<DepositInfoDetail> detailList) {
		this.detailList = detailList;
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

}
