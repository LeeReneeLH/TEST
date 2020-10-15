package com.coffer.businesses.modules.allocation.v01.entity;

import java.io.Serializable;

/**
 * 工作流Entity
 */

public class WorkFlowProperty implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 状态 **/
	private String status;
	/** 颜色标识 **/
	private boolean colorFlag;
	/** 撤回显示标识 **/
	private boolean showCancelFlag;
	/** 未交接显示标识 **/
	private boolean showHandoverFlag;
	/** 执行显示标识 **/
	private boolean excuteFlag;
	/** 临时线路标识 **/
	private boolean showTempFlag;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isColorFlag() {
		return colorFlag;
	}

	public void setColorFlag(boolean colorFlag) {
		this.colorFlag = colorFlag;
	}

	public boolean isShowCancelFlag() {
		return showCancelFlag;
	}

	public void setShowCancelFlag(boolean showCancelFlag) {
		this.showCancelFlag = showCancelFlag;
	}

	public boolean isShowHandoverFlag() {
		return showHandoverFlag;
	}

	public void setShowHandoverFlag(boolean showHandoverFlag) {
		this.showHandoverFlag = showHandoverFlag;
	}

	public boolean isExcuteFlag() {
		return excuteFlag;
	}

	public void setExcuteFlag(boolean excuteFlag) {
		this.excuteFlag = excuteFlag;
	}

	public boolean isShowTempFlag() {
		return showTempFlag;
	}

	public void setShowTempFlag(boolean showTempFlag) {
		this.showTempFlag = showTempFlag;
	}

}