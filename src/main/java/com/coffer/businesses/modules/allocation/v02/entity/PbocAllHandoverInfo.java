package com.coffer.businesses.modules.allocation.v02.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.coffer.core.common.persistence.DataEntity;

/**
 * 人行交接管理Entity
 * @author LLF
 * @version 2016-05-25
 */
public class PbocAllHandoverInfo extends DataEntity<PbocAllHandoverInfo> {
	
	private static final long serialVersionUID = 1L;
	private String handoverId;		// 主键
	private String allId;		// 流水单号
	
	private String managerUserId;		// 授权人
	private String managerUserName;		// 授权人名称
	private String managerHandType;		// 授权方式（0：密码验证；1：指纹；2：身份证）
	private String managerReason;		// 授权原因（1：指纹不好用；2：身份证不好用）
	private String handoverStatus;		// 任务状态（0：未交接；1：已交接）
	private Date acceptDate;		// 交接时间
	private String scanManagerUserId;		// 库房扫描授权人
	private String scanManagerUserName;		// 库房扫描授权人名称
	private String scanManagerHandType;		// 库房扫描授权时间
	private String scanManagerReason;		// 库房扫描授权原因
	
	/**复点入库授权人员ID**/
	private String rcInManagerUserId;
	/** 复点入库授权人名称  **/
	private String rcInManagerUserName;
	/** 复点入库授权人员签收方式   1：指纹；2：身份证  **/
	private String rcInManagerHandType;
	/** 复点入库授权原因  1：指纹不好用；2：身份证不好用 **/
	private String rcInManagerReason;
	/** 复点入库任务状态  0：未交接；1：已交接 **/
	private String rcInHandoverStatus;
	/** 复点入库交接时间  **/
	private Date rcInAcceptDate;
	/** 复点入库库房扫描授权人 **/
	private String rcInScanManagerUserId;
	/** 复点入库库房扫描授权人名称 **/
	private String rcInScanManagerUserName;
	/** 复点入库库房扫描授权类型 **/
	private String rcInScanManagerHandType;
	/** 复点入库库房扫描授权原因  **/
	private String rcInScanManagerReason;
	/**
	 * 交接人员列表
	 */
	private List<PbocAllHandoverUserDetail> handoverUserDetailList = Lists.newArrayList();
	
	public PbocAllHandoverInfo() {
		super();
	}

	public PbocAllHandoverInfo(String id){
		super(id);
	}

	
	public String getId() {
		return handoverId;
	}

	public void setId(String handoverId) {
		this.handoverId = handoverId;
	}
	
	@Length(min=0, max=64, message="流水单号长度必须介于 0 和 64 之间")
	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}
	
	
	@Length(min=0, max=129, message="授权人长度必须介于 0 和 129 之间")
	public String getManagerUserId() {
		return managerUserId;
	}

	public void setManagerUserId(String managerUserId) {
		this.managerUserId = managerUserId;
	}
	
	@Length(min=0, max=200, message="授权人名称长度必须介于 0 和 200 之间")
	public String getManagerUserName() {
		return managerUserName;
	}

	public void setManagerUserName(String managerUserName) {
		this.managerUserName = managerUserName;
	}
	
	@Length(min=0, max=3, message="授权方式（0：密码验证；1：指纹；2：身份证）长度必须介于 0 和 3 之间")
	public String getManagerHandType() {
		return managerHandType;
	}

	public void setManagerHandType(String managerHandType) {
		this.managerHandType = managerHandType;
	}
	
	@Length(min=0, max=10, message="授权原因（1：指纹不好用；2：身份证不好用）长度必须介于 0 和 10 之间")
	public String getManagerReason() {
		return managerReason;
	}

	public void setManagerReason(String managerReason) {
		this.managerReason = managerReason;
	}
	
	@Length(min=0, max=1, message="任务状态（0：未交接；1：已交接）长度必须介于 0 和 1 之间")
	public String getHandoverStatus() {
		return handoverStatus;
	}

	public void setHandoverStatus(String handoverStatus) {
		this.handoverStatus = handoverStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}
	
	@Length(min=0, max=129, message="库房扫描授权人长度必须介于 0 和 129 之间")
	public String getScanManagerUserId() {
		return scanManagerUserId;
	}

	public void setScanManagerUserId(String scanManagerUserId) {
		this.scanManagerUserId = scanManagerUserId;
	}
	
	@Length(min=0, max=200, message="库房扫描授权人名称长度必须介于 0 和 200 之间")
	public String getScanManagerUserName() {
		return scanManagerUserName;
	}

	public void setScanManagerUserName(String scanManagerUserName) {
		this.scanManagerUserName = scanManagerUserName;
	}
	
	@Length(min=0, max=3, message="库房扫描授权时间长度必须介于 0 和 3 之间")
	public String getScanManagerHandType() {
		return scanManagerHandType;
	}

	public void setScanManagerHandType(String scanManagerHandType) {
		this.scanManagerHandType = scanManagerHandType;
	}
	
	@Length(min=0, max=10, message="库房扫描授权原因长度必须介于 0 和 10 之间")
	public String getScanManagerReason() {
		return scanManagerReason;
	}

	public void setScanManagerReason(String scanManagerReason) {
		this.scanManagerReason = scanManagerReason;
	}

	/**
	 * @return handoverUserDetailList
	 */
	public List<PbocAllHandoverUserDetail> getHandoverUserDetailList() {
		return handoverUserDetailList;
	}

	/**
	 * @param handoverUserDetailList 要设置的 handoverUserDetailList
	 */
	public void setHandoverUserDetailList(List<PbocAllHandoverUserDetail> handoverUserDetailList) {
		this.handoverUserDetailList = handoverUserDetailList;
	}

	/**
	 * @return handoverId
	 */
	public String getHandoverId() {
		return handoverId;
	}

	/**
	 * @param handoverId 要设置的 handoverId
	 */
	public void setHandoverId(String handoverId) {
		this.handoverId = handoverId;
	}

	/**
	 * @return rcInManagerUserId
	 */
	public String getRcInManagerUserId() {
		return rcInManagerUserId;
	}

	/**
	 * @param rcInManagerUserId 要设置的 rcInManagerUserId
	 */
	public void setRcInManagerUserId(String rcInManagerUserId) {
		this.rcInManagerUserId = rcInManagerUserId;
	}

	/**
	 * @return rcInManagerUserName
	 */
	public String getRcInManagerUserName() {
		return rcInManagerUserName;
	}

	/**
	 * @param rcInManagerUserName 要设置的 rcInManagerUserName
	 */
	public void setRcInManagerUserName(String rcInManagerUserName) {
		this.rcInManagerUserName = rcInManagerUserName;
	}

	/**
	 * @return rcInManagerHandType
	 */
	public String getRcInManagerHandType() {
		return rcInManagerHandType;
	}

	/**
	 * @param rcInManagerHandType 要设置的 rcInManagerHandType
	 */
	public void setRcInManagerHandType(String rcInManagerHandType) {
		this.rcInManagerHandType = rcInManagerHandType;
	}

	/**
	 * @return rcInManagerReason
	 */
	public String getRcInManagerReason() {
		return rcInManagerReason;
	}

	/**
	 * @param rcInManagerReason 要设置的 rcInManagerReason
	 */
	public void setRcInManagerReason(String rcInManagerReason) {
		this.rcInManagerReason = rcInManagerReason;
	}

	/**
	 * @return rcInHandoverStatus
	 */
	public String getRcInHandoverStatus() {
		return rcInHandoverStatus;
	}

	/**
	 * @param rcInHandoverStatus 要设置的 rcInHandoverStatus
	 */
	public void setRcInHandoverStatus(String rcInHandoverStatus) {
		this.rcInHandoverStatus = rcInHandoverStatus;
	}

	/**
	 * @return rcInAcceptDate
	 */
	public Date getRcInAcceptDate() {
		return rcInAcceptDate;
	}

	/**
	 * @param rcInAcceptDate 要设置的 rcInAcceptDate
	 */
	public void setRcInAcceptDate(Date rcInAcceptDate) {
		this.rcInAcceptDate = rcInAcceptDate;
	}

	/**
	 * @return rcInScanManagerUserId
	 */
	public String getRcInScanManagerUserId() {
		return rcInScanManagerUserId;
	}

	/**
	 * @param rcInScanManagerUserId 要设置的 rcInScanManagerUserId
	 */
	public void setRcInScanManagerUserId(String rcInScanManagerUserId) {
		this.rcInScanManagerUserId = rcInScanManagerUserId;
	}

	/**
	 * @return rcInScanManagerUserName
	 */
	public String getRcInScanManagerUserName() {
		return rcInScanManagerUserName;
	}

	/**
	 * @param rcInScanManagerUserName 要设置的 rcInScanManagerUserName
	 */
	public void setRcInScanManagerUserName(String rcInScanManagerUserName) {
		this.rcInScanManagerUserName = rcInScanManagerUserName;
	}

	/**
	 * @return rcInScanManagerHandType
	 */
	public String getRcInScanManagerHandType() {
		return rcInScanManagerHandType;
	}

	/**
	 * @param rcInScanManagerHandType 要设置的 rcInScanManagerHandType
	 */
	public void setRcInScanManagerHandType(String rcInScanManagerHandType) {
		this.rcInScanManagerHandType = rcInScanManagerHandType;
	}

	/**
	 * @return rcInScanManagerReason
	 */
	public String getRcInScanManagerReason() {
		return rcInScanManagerReason;
	}

	/**
	 * @param rcInScanManagerReason 要设置的 rcInScanManagerReason
	 */
	public void setRcInScanManagerReason(String rcInScanManagerReason) {
		this.rcInScanManagerReason = rcInScanManagerReason;
	}
	
	
}