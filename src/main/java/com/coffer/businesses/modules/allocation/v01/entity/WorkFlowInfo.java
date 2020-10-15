package com.coffer.businesses.modules.allocation.v01.entity;

import java.io.Serializable;

/**
 * 工作流Entity
 */
public class WorkFlowInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 流水号 **/
	private String allId;
	/** 登记 **/
	private WorkFlowProperty register;
	/** 已确认 **/
	private WorkFlowProperty confirm;
	/** 现金预约已装箱 */
	private WorkFlowProperty packScan;
	/** 待交接（已过扫描门，未指纹交接） */
	private WorkFlowProperty doorScan;
	/** 在途（已指纹交接） */
	private WorkFlowProperty onload;
	/** 已完成（指纹交接完成） */
	private WorkFlowProperty finish;

	public WorkFlowProperty getRegister() {
		return register;
	}

	public void setRegister(WorkFlowProperty register) {
		this.register = register;
	}

	public WorkFlowProperty getConfirm() {
		return confirm;
	}

	public void setConfirm(WorkFlowProperty confirm) {
		this.confirm = confirm;
	}

	public WorkFlowProperty getPackScan() {
		return packScan;
	}

	public void setPackScan(WorkFlowProperty packScan) {
		this.packScan = packScan;
	}

	public WorkFlowProperty getDoorScan() {
		return doorScan;
	}

	public void setDoorScan(WorkFlowProperty doorScan) {
		this.doorScan = doorScan;
	}

	public WorkFlowProperty getOnload() {
		return onload;
	}

	public void setOnload(WorkFlowProperty onload) {
		this.onload = onload;
	}

	public WorkFlowProperty getFinish() {
		return finish;
	}

	public void setFinish(WorkFlowProperty finish) {
		this.finish = finish;
	}

	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}

}