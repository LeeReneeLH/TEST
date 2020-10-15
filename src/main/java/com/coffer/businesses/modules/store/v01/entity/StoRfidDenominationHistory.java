package com.coffer.businesses.modules.store.v01.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 
* Title: StoRfidDenominationHistory 
* <p>Description: RFID面值绑定信息历史表 Entity</p>
* @author wangbaozhong
* @date 2016年10月13日 上午9:11:37
 */
public class StoRfidDenominationHistory extends DataEntity<StoRfidDenominationHistory>  {
    private static final long serialVersionUID = 1L;
    
    /** 修改后RFID所在机构ID **/
    private String updatedAtOfficeId;
	/** 修改后所在机构名称 **/
    private String updatedAtOfficeName;
    /**RFID面值绑定信息表信息**/
    private StoRfidDenomination oldStoRfidDenomination;
    
	/**
	 * @return the oldStoRfidDenomination
	 */
	public StoRfidDenomination getOldStoRfidDenomination() {
		return oldStoRfidDenomination;
	}
	/**
	 * @param oldStoRfidDenomination the oldStoRfidDenomination to set
	 */
	public void setOldStoRfidDenomination(StoRfidDenomination oldStoRfidDenomination) {
		this.oldStoRfidDenomination = oldStoRfidDenomination;
	}
	/**
	 * @return the updatedAtOfficeId
	 */
	public String getUpdatedAtOfficeId() {
		return updatedAtOfficeId;
	}
	/**
	 * @param updatedAtOfficeId the updatedAtOfficeId to set
	 */
	public void setUpdatedAtOfficeId(String updatedAtOfficeId) {
		this.updatedAtOfficeId = updatedAtOfficeId;
	}
	/**
	 * @return the updatedAtOfficeName
	 */
	public String getUpdatedAtOfficeName() {
		return updatedAtOfficeName;
	}
	/**
	 * @param updatedAtOfficeName the updatedAtOfficeName to set
	 */
	public void setUpdatedAtOfficeName(String updatedAtOfficeName) {
		this.updatedAtOfficeName = updatedAtOfficeName;
	}

}