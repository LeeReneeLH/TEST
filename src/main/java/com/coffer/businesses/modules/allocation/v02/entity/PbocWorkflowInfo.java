package com.coffer.businesses.modules.allocation.v02.entity;

import java.io.Serializable;

import com.coffer.businesses.modules.allocation.v01.entity.WorkFlowProperty;

/**
 * 
 * Title: PbocWorkflowInfo
 * <p>
 * Description: 人行调拨工作流程图entity
 * </p>
 * 
 * @author yanbingxu
 * @date 2018年3月26日 上午10:36:25
 */
public class PbocWorkflowInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 流水号 **/
	private String allId;
	/** 待审批 **/
	private WorkFlowProperty toApproval;
	/** 流驳回 **/
	private WorkFlowProperty reject;
	/** 完成 **/
	private WorkFlowProperty finish;
	/** 待出(入)库 **/
	private WorkFlowProperty toInOutStore;
	/** 待接收 **/
	private WorkFlowProperty toAccept;
	/** 待交接 **/
	private WorkFlowProperty toHandover;
	/** 待入库交接(复点管理) **/
	private WorkFlowProperty toInStoreHandover;
	/** 待配款 **/
	private WorkFlowProperty toQuota;
	/** 清分中 **/
	private WorkFlowProperty clearing;

	/**
	 * @return the allId
	 */
	public String getAllId() {
		return allId;
	}

	/**
	 * @param allId
	 *            the allId to set
	 */
	public void setAllId(String allId) {
		this.allId = allId;
	}

	/**
	 * @return the toApproval
	 */
	public WorkFlowProperty getToApproval() {
		return toApproval;
	}

	/**
	 * @param toApproval
	 *            the toApproval to set
	 */
	public void setToApproval(WorkFlowProperty toApproval) {
		this.toApproval = toApproval;
	}

	/**
	 * @return the reject
	 */
	public WorkFlowProperty getReject() {
		return reject;
	}

	/**
	 * @param reject
	 *            the reject to set
	 */
	public void setReject(WorkFlowProperty reject) {
		this.reject = reject;
	}

	/**
	 * @return the finish
	 */
	public WorkFlowProperty getFinish() {
		return finish;
	}

	/**
	 * @param finish
	 *            the finish to set
	 */
	public void setFinish(WorkFlowProperty finish) {
		this.finish = finish;
	}

	/**
	 * @return the toAccept
	 */
	public WorkFlowProperty getToAccept() {
		return toAccept;
	}

	/**
	 * @param toAccept
	 *            the toAccept to set
	 */
	public void setToAccept(WorkFlowProperty toAccept) {
		this.toAccept = toAccept;
	}

	/**
	 * @return the toQuota
	 */
	public WorkFlowProperty getToQuota() {
		return toQuota;
	}

	/**
	 * @param toQuota
	 *            the toQuota to set
	 */
	public void setToQuota(WorkFlowProperty toQuota) {
		this.toQuota = toQuota;
	}

	/**
	 * @return the toHandover
	 */
	public WorkFlowProperty getToHandover() {
		return toHandover;
	}

	/**
	 * @param toHandover
	 *            the toHandover to set
	 */
	public void setToHandover(WorkFlowProperty toHandover) {
		this.toHandover = toHandover;
	}

	/**
	 * @return the toInStoreHandover
	 */
	public WorkFlowProperty getToInStoreHandover() {
		return toInStoreHandover;
	}

	/**
	 * @param toInStoreHandover
	 *            the toInStoreHandover to set
	 */
	public void setToInStoreHandover(WorkFlowProperty toInStoreHandover) {
		this.toInStoreHandover = toInStoreHandover;
	}

	/**
	 * @return the toInOutStore
	 */
	public WorkFlowProperty getToInOutStore() {
		return toInOutStore;
	}

	/**
	 * @param toInOutStore
	 *            the toInOutStore to set
	 */
	public void setToInOutStore(WorkFlowProperty toInOutStore) {
		this.toInOutStore = toInOutStore;
	}

	/**
	 * @return the clearing
	 */
	public WorkFlowProperty getClearing() {
		return clearing;
	}

	/**
	 * @param clearing
	 *            the clearing to set
	 */
	public void setClearing(WorkFlowProperty clearing) {
		this.clearing = clearing;
	}

}
