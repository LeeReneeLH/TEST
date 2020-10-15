package com.coffer.businesses.modules.doorOrder.v01.entity;

public class AjaxDoorOrderInfo {
	private String amount; //金额
	private String totalCount; // 总笔数
	private String bagCapacity; //款袋容量
	private String percent; //百分比
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getBagCapacity() {
		return bagCapacity;
	}
	public void setBagCapacity(String bagCapacity) {
		this.bagCapacity = bagCapacity;
	}
}
