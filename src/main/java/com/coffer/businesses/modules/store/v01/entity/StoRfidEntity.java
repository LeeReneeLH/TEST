package com.coffer.businesses.modules.store.v01.entity;

import java.util.List;

import com.google.common.collect.Lists;

public class StoRfidEntity {

	/** 流水单号 **/
	private String allId;
	
    /** rfid列表 **/
	private List<StoRfidDenomination> rfidDenominationList =Lists.newArrayList();
	
    /** 错误标识 **/
    private String errorFlag;
    
    /** 确认绑定 **/
    private String reBindingFlag;
    
    private String userId;
    
    private String userName;
    
    private List<String> rfidList = Lists.newArrayList();

	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}
	
	public List<StoRfidDenomination> getRfidDenominationList() {
		return rfidDenominationList;
	}

	public void setRfidDenominationList(List<StoRfidDenomination> rfidDenominationList) {
		this.rfidDenominationList = rfidDenominationList;
	}
	
    public String getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        this.errorFlag = errorFlag;
    }
    
    public List<String> getRfidList() {
        return rfidList;
    }

    public void setRfidList(List<String> rfidList) {
        this.rfidList = rfidList;
    }
    
    public String getReBindingFlag() {
        return reBindingFlag;
    }

    public void setReBindingFlag(String reBindingFlag) {
        this.reBindingFlag = reBindingFlag;
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
