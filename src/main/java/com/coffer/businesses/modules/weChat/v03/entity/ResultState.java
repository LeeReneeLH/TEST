package com.coffer.businesses.modules.weChat.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

public class ResultState extends DataEntity<ResultState> {

	private static final long serialVersionUID = 1L;

	private int errcode; // 状态
	private String errmsg; // 信息

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

}
