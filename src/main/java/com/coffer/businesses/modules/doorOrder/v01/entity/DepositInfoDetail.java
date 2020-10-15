package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 机构存款实体类
 * 
 * @author lihe
 * @version 2019-07-10
 */
public class DepositInfoDetail extends DataEntity<DepositInfoDetail> {

	private static final long serialVersionUID = 1L;
	private String orderId; // 订单编号
	private String depositAmount; // 存款金额
	private String depositDate; // 存款时间
	private String equipmentId; // 机具编号
	private String depositNo; // 柜员号
	private String depositName; // 柜员名称
	private String tickerNo; // 凭条号
	private String uploadDate; // 图片上传时间
	private String photo; // 凭条图片

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

}
