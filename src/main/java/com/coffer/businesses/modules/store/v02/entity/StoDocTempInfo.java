/**
 * wenjian:    DocumentTemplateInfo.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2016年9月12日    xq     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2016年9月12日 上午9:18:50
 */
package com.coffer.businesses.modules.store.v02.entity;

import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
* Title: StoDocTempInfo 
* <p>Description: 单据模板信息主表</p>
* @author wangbaozhong
* @date 2016年9月12日 上午9:18:50
*/
public class StoDocTempInfo extends DataEntity<StoDocTempInfo> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	
	/**所属机构**/
	private Office office;
	
	/**单据类型**/
	private String documentType;
	/**商业机构印章类型**/
	private String officeStamperType;
	/**人行机构印章类型**/
	private String pbocOfficeStamperType;
	/**单据人员信息**/
	private StoDocTempUserDetail stoDocTempUserDetail;
	/** 业务类型50：现金上缴；51：现金预约 **/
	private String businessType;
	/** 状态:业务类型51：申请下拨( 20：待审批；21：驳回；22：待配款; 40：待出库；41：待交接；42：待接收)；50：申请上缴(20：待审批；21：驳回；40：待入库；41：待交接);   99：完成 **/
	private String status;
	/**下拨业务状态**/
	private String allocateStatus;
	/**上缴业务状态**/
	private String handinStatus;
	/** 商业机构印章信息 **/
	private StoOfficeStamperInfo stoOfficeStamperInfo;
	/** 商业机构印章ID **/
	private String officeStamperId;
	/** 人行机构印章信息 **/
	private StoOfficeStamperInfo pbocStoOfficeStamperInfo;
	/** 人行机构印章ID **/
	private String pbocOfficeStamperId;
	/** 单据人员信息列表 **/
	private List<StoDocTempUserDetail> docTempUserDetailList = Lists.newArrayList();
	
	/**父机构**/
	private Office parentsOffice;
	
	/**
	 * @return the office
	 */
	public Office getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(Office office) {
		this.office = office;
	}
	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	/**
	 * @return the officeStamperType
	 */
	public String getOfficeStamperType() {
		return officeStamperType;
	}
	/**
	 * @param officeStamperType the officeStamperType to set
	 */
	public void setOfficeStamperType(String officeStamperType) {
		this.officeStamperType = officeStamperType;
	}
	/**
	 * @return the docTempUserDetailList
	 */
	public List<StoDocTempUserDetail> getDocTempUserDetailList() {
		return docTempUserDetailList;
	}
	/**
	 * @param docTempUserDetailList the docTempUserDetailList to set
	 */
	public void setDocTempUserDetailList(List<StoDocTempUserDetail> docTempUserDetailList) {
		this.docTempUserDetailList = docTempUserDetailList;
	}
	/**
	 * @return the stoDocTempUserDetail
	 */
	public StoDocTempUserDetail getStoDocTempUserDetail() {
		return stoDocTempUserDetail;
	}
	/**
	 * @param stoDocTempUserDetail the stoDocTempUserDetail to set
	 */
	public void setStoDocTempUserDetail(StoDocTempUserDetail stoDocTempUserDetail) {
		this.stoDocTempUserDetail = stoDocTempUserDetail;
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
	 * @return the allocateStatus
	 */
	public String getAllocateStatus() {
		return allocateStatus;
	}
	/**
	 * @param allocateStatus the allocateStatus to set
	 */
	public void setAllocateStatus(String allocateStatus) {
		this.allocateStatus = allocateStatus;
	}
	/**
	 * @return the handinStatus
	 */
	public String getHandinStatus() {
		return handinStatus;
	}
	/**
	 * @param handinStatus the handinStatus to set
	 */
	public void setHandinStatus(String handinStatus) {
		this.handinStatus = handinStatus;
	}
	/**
	 * @return the stoOfficeStamperInfo
	 */
	public StoOfficeStamperInfo getStoOfficeStamperInfo() {
		return stoOfficeStamperInfo;
	}
	/**
	 * @param stoOfficeStamperInfo the stoOfficeStamperInfo to set
	 */
	public void setStoOfficeStamperInfo(StoOfficeStamperInfo stoOfficeStamperInfo) {
		this.stoOfficeStamperInfo = stoOfficeStamperInfo;
	}
	/**
	 * @return the officeStamperId
	 */
	public String getOfficeStamperId() {
		return officeStamperId;
	}
	/**
	 * @param officeStamperId the officeStamperId to set
	 */
	public void setOfficeStamperId(String officeStamperId) {
		this.officeStamperId = officeStamperId;
	}
	/**
	 * @return the pbocStoOfficeStamperInfo
	 */
	public StoOfficeStamperInfo getPbocStoOfficeStamperInfo() {
		return pbocStoOfficeStamperInfo;
	}
	/**
	 * @param pbocStoOfficeStamperInfo the pbocStoOfficeStamperInfo to set
	 */
	public void setPbocStoOfficeStamperInfo(StoOfficeStamperInfo pbocStoOfficeStamperInfo) {
		this.pbocStoOfficeStamperInfo = pbocStoOfficeStamperInfo;
	}
	/**
	 * @return the pbocOfficeStamperId
	 */
	public String getPbocOfficeStamperId() {
		return pbocOfficeStamperId;
	}
	/**
	 * @param pbocOfficeStamperId the pbocOfficeStamperId to set
	 */
	public void setPbocOfficeStamperId(String pbocOfficeStamperId) {
		this.pbocOfficeStamperId = pbocOfficeStamperId;
	}
	/**
	 * @return the pbocOfficeStamperType
	 */
	public String getPbocOfficeStamperType() {
		return pbocOfficeStamperType;
	}
	/**
	 * @param pbocOfficeStamperType the pbocOfficeStamperType to set
	 */
	public void setPbocOfficeStamperType(String pbocOfficeStamperType) {
		this.pbocOfficeStamperType = pbocOfficeStamperType;
	}
	/**
	 * @return parentsOffice
	 */
	public Office getParentsOffice() {
		return parentsOffice;
	}
	/**
	 * @param parentsOffice 要设置的 parentsOffice
	 */
	public void setParentsOffice(Office parentsOffice) {
		this.parentsOffice = parentsOffice;
	}
}
