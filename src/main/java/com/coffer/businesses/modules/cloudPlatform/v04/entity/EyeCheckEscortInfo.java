package com.coffer.businesses.modules.cloudPlatform.v04.entity;

import org.hibernate.validator.constraints.Length;
import com.coffer.core.modules.sys.entity.Office;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 双目识别人员信息Entity
 * @author XL
 * @version 2018-12-07
 */
public class EyeCheckEscortInfo extends DataEntity<EyeCheckEscortInfo> {
	
	private static final long serialVersionUID = 1L;
	private String escortId;		// 主键
	private String idcardNo;		// 身份证号
	private String escortName;		// 人员姓名
	private String phone;		// 电话
	private String address;		// 地址
	private String identityBirth;		// 出生日期
	private String identityVisa;		// 发证机关
	private String identityGender;		// 性别
	private String identityNational;		// 民族
	private Office office;		// 所属机构ID
	private String escortType;		// 人员类型（V：访客；E：员工）
	private byte[] photo;		// 照片
	private Date endDate;		// 有效截止时间
	
	public EyeCheckEscortInfo() {
		super();
	}

	public EyeCheckEscortInfo(String id){
		super(id);
	}

	@Length(min=1, max=64, message="主键长度必须介于 1 和 64 之间")
	public String getEscortId() {
		return escortId;
	}

	public void setEscortId(String escortId) {
		this.escortId = escortId;
	}
	
	@Length(min=0, max=20, message="身份证号长度必须介于 0 和 20 之间")
	public String getIdcardNo() {
		return idcardNo;
	}

	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}
	
	@Length(min=0, max=200, message="人员姓名长度必须介于 0 和 200 之间")
	public String getEscortName() {
		return escortName;
	}

	public void setEscortName(String escortName) {
		this.escortName = escortName;
	}
	
	@Length(min=0, max=15, message="电话长度必须介于 0 和 15 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=0, max=200, message="地址长度必须介于 0 和 200 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Length(min=0, max=20, message="出生日期长度必须介于 0 和 20 之间")
	public String getIdentityBirth() {
		return identityBirth;
	}

	public void setIdentityBirth(String identityBirth) {
		this.identityBirth = identityBirth;
	}
	
	@Length(min=0, max=1000, message="发证机关长度必须介于 0 和 1000 之间")
	public String getIdentityVisa() {
		return identityVisa;
	}

	public void setIdentityVisa(String identityVisa) {
		this.identityVisa = identityVisa;
	}
	
	@Length(min=0, max=4, message="性别长度必须介于 0 和 4 之间")
	public String getIdentityGender() {
		return identityGender;
	}

	public void setIdentityGender(String identityGender) {
		this.identityGender = identityGender;
	}
	
	@Length(min=0, max=40, message="民族长度必须介于 0 和 40 之间")
	public String getIdentityNational() {
		return identityNational;
	}

	public void setIdentityNational(String identityNational) {
		this.identityNational = identityNational;
	}
	
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=0, max=2, message="人员类型（V：访客；E：员工）长度必须介于 0 和 2 之间")
	public String getEscortType() {
		return escortType;
	}

	public void setEscortType(String escortType) {
		this.escortType = escortType;
	}
	
	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
}