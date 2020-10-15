/**
 * wenjian:    DocmumentTemplateUserDetail.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2016年9月12日    xq     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2016年9月12日 上午9:25:16
 */
package com.coffer.businesses.modules.store.v02.entity;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.DataEntity;

/**
* Title: StoDocTempUserDetail 
* <p>Description: 单据人员信息子表</p>
* @author wangbaozhong
* @date 2016年9月12日 上午9:25:16
*/
public class StoDocTempUserDetail extends DataEntity<StoDocTempUserDetail> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	
	/** 模板信息主表ID **/
	private String docInfoId;
	/** 人员ID **/
	private String escortId;
	/** 人员信息 **/
	private StoEscortInfo stoEscortInfo;
	
	/** 职责类型 **/
	private String responsibilityType;
	/**
	 * @return the docInfoId
	 */
	public String getDocInfoId() {
		return docInfoId;
	}
	/**
	 * @param docInfoId the docInfoId to set
	 */
	public void setDocInfoId(String docInfoId) {
		this.docInfoId = docInfoId;
	}
	/**
	 * @return the escortId
	 */
	public String getEscortId() {
		return escortId;
	}
	/**
	 * @param escortId the escortId to set
	 */
	public void setEscortId(String escortId) {
		this.escortId = escortId;
	}
	/**
	 * @return the responsibilityType
	 */
	public String getResponsibilityType() {
		return responsibilityType;
	}
	/**
	 * @param responsibilityType the responsibilityType to set
	 */
	public void setResponsibilityType(String responsibilityType) {
		this.responsibilityType = responsibilityType;
	}
	/**
	 * @return the stoEscortInfo
	 */
	public StoEscortInfo getStoEscortInfo() {
		return stoEscortInfo;
	}
	/**
	 * @param stoEscortInfo the stoEscortInfo to set
	 */
	public void setStoEscortInfo(StoEscortInfo stoEscortInfo) {
		this.stoEscortInfo = stoEscortInfo;
	}

}
