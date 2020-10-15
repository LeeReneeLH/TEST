package com.coffer.businesses.modules.allocation.v01.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 交接明细
 * 
 * @author liuyaowen
 * @version 2017-06-30
 *
 */
public class AllHandoverDetail extends DataEntity<AllHandoverDetail> {

	private static final long serialVersionUID = 1L;

	/** 交接主键 **/
	private String detailId;
	/** 交接主表主键 **/
	private String handoverId;
	/** 人员ID **/
	private String escortId;
	/** 人员姓名 **/
	private String escortName;
	/** 授权方式（0：系统登录、1：人脸识别、2：身份证） **/
	private String type;
	/** 授权原因（1：人脸识别不好用，2：身份证不好用） **/
	private String managerReason;
	/** 操作类型（0: 移交、1：接收、2：授权、3：扫描门授权） **/
	private String operationType;

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getHandoverId() {
		return handoverId;
	}

	public void setHandoverId(String handoverId) {
		this.handoverId = handoverId;
	}

	public String getEscortId() {
		return escortId;
	}

	public void setEscortId(String escortId) {
		this.escortId = escortId;
	}

	public String getEscortName() {
		return escortName;
	}

	public void setEscortName(String escortName) {
		this.escortName = escortName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getManagerReason() {
		return managerReason;
	}

	public void setManagerReason(String managerReason) {
		this.managerReason = managerReason;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

}
