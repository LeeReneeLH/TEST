package com.coffer.businesses.modules.cloudPlatform.v04.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;
import com.coffer.core.modules.sys.entity.Office;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 双目识别访客信息Entity
 * 
 * @author XL
 * @version 2018-12-07
 */
public class EyeCheckVisitorInfo extends DataEntity<EyeCheckVisitorInfo> {

	private static final long serialVersionUID = 1L;
	private String visitorId; // 主键
	private String idcardNo; // 身份证号
	private String escortName; // 人员姓名
	private String identityGender; // 性别
	private String age; // 年龄
	private Office office; // 所属机构ID
	private byte[] photo; // 照片
	private String similarity; // 相似度

	private Date createTimeStart;// 开始时间
	private Date createTimeEnd;// 结束时间
	private String searchDateStart;// 开始时间
	private String searchDateEnd;// 结束时间
	/** 追加人员类型字段 WQJ 2018-12-21 **/
	private String escortType;		// 人员类型（V：访客；E：员工）

	public String getEscortType() {
		return escortType;
	}

	public void setEscortType(String escortType) {
		this.escortType = escortType;
	}

	public EyeCheckVisitorInfo() {
		super();
	}

	public EyeCheckVisitorInfo(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "主键长度必须介于 1 和 64 之间")
	public String getVisitorId() {
		return visitorId;
	}

	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	@Length(min = 0, max = 20, message = "身份证号长度必须介于 0 和 20 之间")
	public String getIdcardNo() {
		return idcardNo;
	}

	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}

	@Length(min = 0, max = 200, message = "人员姓名长度必须介于 0 和 200 之间")
	public String getEscortName() {
		return escortName;
	}

	public void setEscortName(String escortName) {
		this.escortName = escortName;
	}

	@Length(min = 0, max = 4, message = "性别长度必须介于 0 和 4 之间")
	public String getIdentityGender() {
		return identityGender;
	}

	public void setIdentityGender(String identityGender) {
		this.identityGender = identityGender;
	}

	@Length(min = 0, max = 10, message = "年龄长度必须介于 0 和 10 之间")
	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	@Length(min = 0, max = 10, message = "相似度长度必须介于 0 和 10 之间")
	public String getSimilarity() {
		return similarity;
	}

	public void setSimilarity(String similarity) {
		this.similarity = similarity;
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

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}
}