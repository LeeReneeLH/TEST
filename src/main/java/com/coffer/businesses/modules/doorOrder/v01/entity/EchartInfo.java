package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 首页echarts实体类
 * 
 * @author lihe
 * @version 2019-07-10
 */
public class EchartInfo extends DataEntity<EchartInfo> {

	private static final long serialVersionUID = 1L;
	private BigDecimal clientAmount; // 商户存款总金额
	private List<String> clientNameList; // 商户名称列表
	private List<String> clientAmountList; // 门店名称列表
	private List<String> doorNameList; // 门店名称列表
	private List<String> doorAmountList; // 门店名称列表
	private List<OfficeAmount> rankingList; // 存款排名列表

	public BigDecimal getClientAmount() {
		return clientAmount;
	}

	public void setClientAmount(BigDecimal clientAmount) {
		this.clientAmount = clientAmount;
	}

	public List<String> getClientNameList() {
		return clientNameList;
	}

	public void setClientNameList(List<String> clientNameList) {
		this.clientNameList = clientNameList;
	}

	public List<String> getDoorNameList() {
		return doorNameList;
	}

	public void setDoorNameList(List<String> doorNameList) {
		this.doorNameList = doorNameList;
	}

	public List<OfficeAmount> getRankingList() {
		return rankingList;
	}

	public void setRankingList(List<OfficeAmount> rankingList) {
		this.rankingList = rankingList;
	}

	public List<String> getClientAmountList() {
		return clientAmountList;
	}

	public void setClientAmountList(List<String> clientAmountList) {
		this.clientAmountList = clientAmountList;
	}

	public List<String> getDoorAmountList() {
		return doorAmountList;
	}

	public void setDoorAmountList(List<String> doorAmountList) {
		this.doorAmountList = doorAmountList;
	}

}
