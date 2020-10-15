package com.coffer.businesses.modules.doorOrder.v01.entity;

/**
 * 向前端展示机具数据使用的数据格式
 * 
 * @author GJ
 *
 */
public class AjaxEquipmentInfo {
	private String id; // 机具id
	private String name; // 机具名称
	private String seriesNumber; // 序列号
	private String clearStatus; //清分机状态
	private String printerStatus; //打印机状态
	private String doorStatus; //保险柜仓门状态
	private String connStatus; // 机具连线状态
	public String getConnStatus() {
		return connStatus;
	}
	public void setConnStatus(String connStatus) {
		this.connStatus = connStatus;
	}
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
	private AjaxDoorOrderInfo orderInfo; //存款信息
	public AjaxDoorOrderInfo getOrderInfo() {
		return orderInfo;
	}
	public void setOrderInfo(AjaxDoorOrderInfo orderInfo) {
		this.orderInfo = orderInfo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSeriesNumber() {
		return seriesNumber;
	}
	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}
	
	
}
