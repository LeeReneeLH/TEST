package com.coffer.businesses.modules.allocation.v01.entity;


public class StoEscort {
	// 主键
	private String escortId; 
	// 人员姓名
	private String escortName; 
	// 指纹2
	private byte[] fingerNo2; 
	// 指纹1
	private byte[] fingerNo1;
	// 身份证号
	private String idcardNo; 
	// 密码
	private String password;
	
	// 照片
	private byte[] photo;
	
	private String rfid;
	
	
	public String getEscortId() {
		return escortId;
	}
	public void setEscortId(String escortId) {
		this.escortId = escortId;
	}
	public String getEscortName() {
		return escortName;
	}
	public void setEscortName(String escortName) {
		this.escortName = escortName;
	}
	public byte[] getFingerNo2() {
		return fingerNo2;
	}
	public void setFingerNo2(byte[] fingerNo2) {
		this.fingerNo2 = fingerNo2;
	}
	public byte[] getFingerNo1() {
		return fingerNo1;
	}
	public void setFingerNo1(byte[] fingerNo1) {
		this.fingerNo1 = fingerNo1;
	}
	public String getIdcardNo() {
		return idcardNo;
	}
	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public byte[] getPhoto() {
		return photo;
	}
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
	public String getRfid() {
		return rfid;
	}
	public void setRfid(String rfid) {
		this.rfid = rfid;
	} 
	
}
