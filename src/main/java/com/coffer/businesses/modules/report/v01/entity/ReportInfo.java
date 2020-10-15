package com.coffer.businesses.modules.report.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.businesses.modules.allocation.v01.entity.AllReportInfo;
import com.coffer.businesses.modules.store.v01.entity.StoReportInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfoEntity;
import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;

/**
 * 清机加钞查询Entity
 * 
 * @author LLF
 * @version 2015-05-22
 */
public class ReportInfo extends DataEntity<ReportInfo> {

	private static final long serialVersionUID = 1L;
	/** 历史报表筛选条件 *///
	private StoInfoReportEntity stoInfoReportEntity;
	/** 库存报表筛选条件 */
	
	private StoStoresInfoEntity storesInfoEntity;
	/** 查询时间 */
	private Date searchDate;
	
	/** 调拨报表列表 */
	private List<AllReportInfo> allReportList = Lists.newArrayList();
	
	/** 重空信息列表 */
	private List<StoReportInfo> stoReportInfo = Lists.newArrayList();
	
	/** 重空信息列表 */
	private List<StoStoresInfo> stoStoresInfoList = Lists.newArrayList();
	
	/** 重空变更列表 */
	private List<StoStoresHistory> stoEmptyHistoryList = Lists.newArrayList();
	
	/** 物品库存历史报表 */
	private List<StoInfoReportEntity> stoInfoReportEntityList = Lists.newArrayList();
	
	/** 物品库存表 */
	private List<StoStoresInfoEntity> stoStoresInfoEntityList = Lists.newArrayList();

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	public List<AllReportInfo> getAllReportList() {
		return allReportList;
	}

	public void setAllReportList(List<AllReportInfo> allReportList) {
		this.allReportList = allReportList;
	}

	public List<StoReportInfo> getStoReportInfo() {
		return stoReportInfo;
	}

	public void setStoReportInfo(List<StoReportInfo> stoReportInfo) {
		this.stoReportInfo = stoReportInfo;
	}

	public List<StoStoresInfo> getStoStoresInfoList() {
		return stoStoresInfoList;
	}

	public void setStoStoresInfoList(List<StoStoresInfo> stoStoresInfoList) {
		this.stoStoresInfoList = stoStoresInfoList;
	}

	public List<StoStoresHistory> getStoEmptyHistoryList() {
		return stoEmptyHistoryList;
	}

	public void setStoEmptyHistoryList(List<StoStoresHistory> stoEmptyHistoryList) {
		this.stoEmptyHistoryList = stoEmptyHistoryList;
	}

	public StoInfoReportEntity getStoInfoReportEntity() {
		return stoInfoReportEntity;
	}

	public void setStoInfoReportEntity(StoInfoReportEntity stoInfoReportEntity) {
		this.stoInfoReportEntity = stoInfoReportEntity;
	}

	public StoStoresInfoEntity getStoresInfoEntity() {
		return storesInfoEntity;
	}

	public void setStoresInfoEntity(StoStoresInfoEntity storesInfoEntity) {
		this.storesInfoEntity = storesInfoEntity;
	}

	public List<StoInfoReportEntity> getStoInfoReportEntityList() {
		return stoInfoReportEntityList;
	}

	public void setStoInfoReportEntityList(List<StoInfoReportEntity> stoInfoReportEntityList) {
		this.stoInfoReportEntityList = stoInfoReportEntityList;
	}

	public List<StoStoresInfoEntity> getStoStoresInfoEntityList() {
		return stoStoresInfoEntityList;
	}

	public void setStoStoresInfoEntityList(List<StoStoresInfoEntity> stoStoresInfoEntityList) {
		this.stoStoresInfoEntityList = stoStoresInfoEntityList;
	}
	
}