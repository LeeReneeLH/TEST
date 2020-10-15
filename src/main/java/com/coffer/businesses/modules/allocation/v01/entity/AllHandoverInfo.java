package com.coffer.businesses.modules.allocation.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

/**
 * The persistent class for the ALL_HANDOVER database table.
 * 
 */
public class AllHandoverInfo extends DataEntity<AllHandoverInfo> {
	private static final long serialVersionUID = 1L;

	/** 创建时间 **/
	private Date createDate;
	/** 交接时间 **/
	private Date acceptDate;
	
	/** 交接主键 **/
	private String handoverId;
	/** 交接明细 **/
	private AllHandoverDetail handoverDetail;
	

	private List<AllHandoverDetail> detailList = Lists.newArrayList();
	
	
	public AllHandoverInfo() {
		super();
	}

	public AllHandoverInfo(String id){
		super(id);
	}
	
	public String getId() {
		return handoverId;
	}

	public void setId(String handoverId) {
		this.handoverId = handoverId;
	}
	
	@JsonIgnore
	public String getHandoverId() {
		return handoverId;
	}

	public void setHandoverId(String handoverId) {
		this.handoverId = handoverId;
	}

	@JsonIgnore
	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public List<AllHandoverDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<AllHandoverDetail> detailList) {
		this.detailList = detailList;
	}

	public AllHandoverDetail getHandoverDetail() {
		return handoverDetail;
	}

	public void setHandoverDetail(AllHandoverDetail handoverDetail) {
		this.handoverDetail = handoverDetail;
	}
	
	
	}