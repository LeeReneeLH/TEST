/**
 * File:    PbocApprovalPrintDetail.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2016年8月18日    xq     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2016年8月18日 下午7:41:49
 */
package com.coffer.businesses.modules.allocation.v02.entity;

import java.io.Serializable;
import java.util.Date;

/**
* Title: PbocApprovalPrintDetail 
* <p>Description: 审批打印 明细</p>
* @author wangbaozhong
* @date 2016年8月18日 下午7:41:49
*/
public class PbocApprovalPrintDetail implements Serializable {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	
	/** 登记机构名字 **/
	private String rofficeName;
	
	/** 状态:业务类型51：申请下拨( 20：待审批；21：驳回；22：待配款; 40：待出库；41：待交接；42：待接收)；50：申请上缴(20：待审批；21：驳回；40：待入库；41：待交接);   99：完成 **/
	private String status;
	
	/** 预约日期 **/
	private Date applyDate;
	
	/** 业务类型50：现金上缴；51：现金预约 **/
	private String businessType;
	
	/** 完整券金额 **/
	private Double fullCouponAmount;
	
	/** 损伤券金额 **/
	private Double damagedCouponAmount;
	
	/** 原封新券金额 **/
	private Double originalCouponAmount;
	
	/** 审批金额 **/
	private Double confirmAmount;
	
	/** 审批人名字  */
	private String approvalName;
	
	/** 审批时间  */
	private Date approvalDate;
	
	/** 创建日期  */
	protected Date createDate;
	/**
	 * @return the rofficeName
	 */
	public String getRofficeName() {
		return rofficeName;
	}
	/**
	 * @param rofficeName the rofficeName to set
	 */
	public void setRofficeName(String rofficeName) {
		this.rofficeName = rofficeName;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the applyDate
	 */
	public Date getApplyDate() {
		return applyDate;
	}
	/**
	 * @param applyDate the applyDate to set
	 */
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	/**
	 * @return the businessType
	 */
	public String getBusinessType() {
		return businessType;
	}
	/**
	 * @param businessType the businessType to set
	 */
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	/**
	 * @return the fullCouponAmount
	 */
	public Double getFullCouponAmount() {
		return fullCouponAmount;
	}
	/**
	 * @param fullCouponAmount the fullCouponAmount to set
	 */
	public void setFullCouponAmount(Double fullCouponAmount) {
		this.fullCouponAmount = fullCouponAmount;
	}
	/**
	 * @return the damagedCouponAmount
	 */
	public Double getDamagedCouponAmount() {
		return damagedCouponAmount;
	}
	/**
	 * @param damagedCouponAmount the damagedCouponAmount to set
	 */
	public void setDamagedCouponAmount(Double damagedCouponAmount) {
		this.damagedCouponAmount = damagedCouponAmount;
	}
	/**
	 * @return the confirmAmount
	 */
	public Double getConfirmAmount() {
		return confirmAmount;
	}
	/**
	 * @param confirmAmount the confirmAmount to set
	 */
	public void setConfirmAmount(Double confirmAmount) {
		this.confirmAmount = confirmAmount;
	}
	/**
	 * @return the approvalName
	 */
	public String getApprovalName() {
		return approvalName;
	}
	/**
	 * @param approvalName the approvalName to set
	 */
	public void setApprovalName(String approvalName) {
		this.approvalName = approvalName;
	}
	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return the approvalDate
	 */
	public Date getApprovalDate() {
		return approvalDate;
	}
	/**
	 * @param approvalDate the approvalDate to set
	 */
	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}
	/**
	 * @return the originalCouponAmount
	 */
	public Double getOriginalCouponAmount() {
		return originalCouponAmount;
	}
	/**
	 * @param originalCouponAmount the originalCouponAmount to set
	 */
	public void setOriginalCouponAmount(Double originalCouponAmount) {
		this.originalCouponAmount = originalCouponAmount;
	}
}
