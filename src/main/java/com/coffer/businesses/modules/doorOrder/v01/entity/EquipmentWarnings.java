package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 机具报警Entity
 * 
 * @author zhaohaoran
 * @version 2019-07-24
 */
public class EquipmentWarnings extends DataEntity<EquipmentWarnings>{

	private static final long serialVersionUID = 1L;
	private String machNo;				// 机具编号
	private String machName;			// 机具名称
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date warnTime;				// 报警时间
	private String warnTimeSearch;		// 报警时间过滤用
	
	private String warnLevel; 			// 报警级别
	private String warnType; 			// 报警类型
	private String warnCode; 			// 报警码
	private String warnName; 			// 报警信息
	
	private EquipmentInfo equipmentInfo;// 机具信息
	private Office office; 				// 所属机构
	
	private String otype;
	
	
	public String getOtype() {
		return otype;
	}
	public void setOtype(String otype) {
		this.otype = otype;
	}
	//报警时间查询
	private Date createTimeStart;	
	private Date createTimeEnd;		
	private String searchTimeStart;
	private String searchTimeEnd;

	private String centerId;			//中心ID
	private String merchantId;			//商户ID
	private String doorId;				//门店ID
	
	
	private String vinofficeId;
	private String vinofficeName;
	private String aofficeId;
	private String aofficeName;
	
	private String seriesNumber;		//机具对应序列号  hzy 2020/04/15
	
	/** add by gj 2020-09-04 start*/ 
	// 清分机状态
	private String clearStatus;
	// 打印机状态
	private String printerStatus;
	// 仓门状态
	private String doorStatus;

	public String getClearStatus() {
		return clearStatus;
	}

	public void setClearStatus(String clearStatus) {
		this.clearStatus = clearStatus;
	}

	public String getPrinterStatus() {
		return printerStatus;
	}

	public void setPrinterStatus(String printerStatus) {
		this.printerStatus = printerStatus;
	}

	public String getDoorStatus() {
		return doorStatus;
	}

	public void setDoorStatus(String doorStatus) {
		this.doorStatus = doorStatus;
	}
	/** add by gj 2020-09-04 end*/ 
	
	public String getSeriesNumber() {
		return seriesNumber;
	}
	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	public String getAofficeId() {
		return aofficeId;
	}
	public void setAofficeId(String aofficeId) {
		this.aofficeId = aofficeId;
	}
	public String getAofficeName() {
		return aofficeName;
	}
	public void setAofficeName(String aofficeName) {
		this.aofficeName = aofficeName;
	}
	public String getVinofficeId() {
		return vinofficeId;
	}
	public void setVinofficeId(String vinofficeId) {
		this.vinofficeId = vinofficeId;
	}
	public String getVinofficeName() {
		return vinofficeName;
	}
	public void setVinofficeName(String vinofficeName) {
		this.vinofficeName = vinofficeName;
	}
	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	
	public String getDoorId() {
		return doorId;
	}
	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}
	public EquipmentInfo getEquipmentInfo() {
		return equipmentInfo;
	}
	public void setEquipmentInfo(EquipmentInfo equipmentInfo) {
		this.equipmentInfo = equipmentInfo;
	}
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}
	public String getSearchTimeStart() {
		return searchTimeStart;
	}
	public void setSearchTimeStart(String searchTimeStart) {
		this.searchTimeStart = searchTimeStart;
	}
	public String getSearchTimeEnd() {
		return searchTimeEnd;
	}
	public void setSearchTimeEnd(String searchTimeEnd) {
		this.searchTimeEnd = searchTimeEnd;
	}
	public Date getCreateTimeStart() {
		return createTimeStart;
	}
	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}
	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}
	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
	public String getMachNo() {
		return machNo;
	}
	public void setMachNo(String machNo) {
		this.machNo = machNo;
	}
	public String getMachName() {
		return machName;
	}
	public void setMachName(String machName) {
		this.machName = machName;
	}
	public Date getWarnTime() {
		return warnTime;
	}
	public void setWarnTime(Date warnTime) {
		this.warnTime = warnTime;
	}
	public String getWarnLevel() {
		return warnLevel;
	}
	public void setWarnLevel(String warnLevel) {
		this.warnLevel = warnLevel;
	}
	public String getWarnType() {
		return warnType;
	}
	public void setWarnType(String warnType) {
		this.warnType = warnType;
	}
	public String getWarnCode() {
		return warnCode;
	}
	public void setWarnCode(String warnCode) {
		this.warnCode = warnCode;
	}
	public String getWarnName() {
		return warnName;
	}
	public void setWarnName(String warnName) {
		this.warnName = warnName;
	}
	public String getWarnTimeSearch() {
		return warnTimeSearch;
	}
	public void setWarnTimeSearch(String warnTimeSearch) {
		this.warnTimeSearch = warnTimeSearch;
	}
	
}
