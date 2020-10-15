package com.coffer.businesses.modules.store.v01.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.google.gson.annotations.Expose;

/**
 * 人员管理Entity
 * 
 * @author niguoyong
 * @date 2015-09-06
 */
public class StoEscortInfo extends DataEntity<StoEscortInfo> {

	private static final long serialVersionUID = 1L;
	@Expose
	private String escortId; // 主键
	@Expose
	private String idcardNo; // 身份证号
	@Expose
	private String escortName; // 人员姓名
	@Expose
	private String phone; // 电话
	@Expose
	private String address; // 地址
	@Expose
	private String identityBirth; // 出生日期
	@Expose
	private String identityVisa; // 发证机关
	@Expose
	private String identityGender; // 性别
	@Expose
	private String identityNational; // 民族

	private byte[] fingerNo2; // 指纹2
	private byte[] fingerNo1; // 指纹1
	private byte[] pdaFingerNo1; // PDA指纹1
	private byte[] pdaFingerNo2; // PDA指纹2
	private byte[] photo; // 身份证照片 数据库追加脚本alter table STO_ESCORT_INFO add (PHOTO
							// BLOB);修改时间：2016-04-06 修改人：LLF
	private User user; // 用户ID
	private Office office; // 所属机构ID
	@Expose
	private String escortType; // 人员类型（11：押运人员；02：金库主管；03:情分综合；04：票据交接员；05：金库交接员；06：网点主管；07：网点交接员）
	private String bindingRoute; // 押运人员绑定状态
	private String createName; // 创建人姓名
	private String updateName; // 更新人姓名

	private boolean interfaceUse = false;
	@Expose
	private String password; // 密码
	/** 剪裁图片坐标X */
	private String x;
	/** 剪裁图片坐标Y */
	private String y;
	/** 剪裁图片宽度 */
	private String w;
	/** 剪裁图片高度 */
	private String h;
	/** 是否需要上传图片标志 */
	private String uploadFlag;
	/** 上传图片URL路径 */
	private String picPath;
	/** 上传图片文件名 */
	private String tmpPicFileName;
	/** 人员RFID */
	private String rfid;
	/** 人员RFID绑定标识 */
	private String bindingRfid;
	/** 人员印章 **/
	private byte[] userStamper;
	/** 用户脸部识别ID **/
	private Long userFaceId;
	/** 设定用户脸部识别ID 标识 **/
	private String initFaceIdFlag;
	/** 设定用户脸部采集 标识 **/
	private String bindingFace;

	public StoEscortInfo() {
		super();
	}

	public StoEscortInfo(String id) {
		super(id);
		this.escortId = id;
	}

	// @Length(min = 1, max = 64, message = "主键长度必须介于 1 和 64 之间")
	// public String getEscortId() {
	// return escortId;
	// }
	//
	// public void setEscortId(String escortId) {
	// this.escortId = escortId;
	// }
	@Override
	public String getId() {
		return escortId;
	}

	@Override
	public void setId(String escortId) {
		this.escortId = escortId;
	}

	@Override
	public void preInsert() {
		super.preInsert();
		setId(IdGen.getIdByTime());
	}

	@Length(min = 1, max = 20, message = "身份证号长度必须介于 1 和 20 之间")
	public String getIdcardNo() {
		return idcardNo;
	}

	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}

	@Length(min = 1, max = 20, message = "人员姓名长度必须介于 1 和 20 之间")
	public String getEscortName() {
		return escortName;
	}

	public void setEscortName(String escortName) {
		this.escortName = escortName;
	}

	@Length(min = 1, max = 15, message = "电话长度必须介于 1 和 15 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min = 0, max = 200, message = "地址长度必须介于 0 和 200 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min = 0, max = 20, message = "出生日期长度必须介于 0 和 20 之间")
	public String getIdentityBirth() {
		return identityBirth;
	}

	public void setIdentityBirth(String identityBirth) {
		this.identityBirth = identityBirth;
	}

	@Length(min = 0, max = 1000, message = "发证机关长度必须介于 0 和 1000 之间")
	public String getIdentityVisa() {
		return identityVisa;
	}

	public void setIdentityVisa(String identityVisa) {
		this.identityVisa = identityVisa;
	}

	@Length(min = 0, max = 4, message = "性别长度必须介于 0 和 4 之间")
	public String getIdentityGender() {
		return identityGender;
	}

	public void setIdentityGender(String identityGender) {
		this.identityGender = identityGender;
	}

	@Length(min = 0, max = 40, message = "民族长度必须介于 0 和 40 之间")
	public String getIdentityNational() {
		return identityNational;
	}

	public void setIdentityNational(String identityNational) {
		this.identityNational = identityNational;
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

	public byte[] getPdaFingerNo1() {
		return pdaFingerNo1;
	}

	public void setPdaFingerNo1(byte[] pdaFingerNo1) {
		this.pdaFingerNo1 = pdaFingerNo1;
	}

	public byte[] getPdaFingerNo2() {
		return pdaFingerNo2;
	}

	public void setPdaFingerNo2(byte[] pdaFingerNo2) {
		this.pdaFingerNo2 = pdaFingerNo2;
	}

	public String getTmpPicFileName() {
		return tmpPicFileName;
	}

	public void setTmpPicFileName(String tmpPicFileName) {
		this.tmpPicFileName = tmpPicFileName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Length(min = 0, max = 2, message = "人员类型（02：库房主管；03：清分主管；04：整点员；05：管库员；08：ATM调度）长度必须介于 0 和 2 之间")
	public String getEscortType() {
		return escortType;
	}

	public void setEscortType(String escortType) {
		this.escortType = escortType;
	}

	@Length(min = 0, max = 64, message = "创建人姓名长度必须介于 0 和 64 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	@Length(min = 0, max = 64, message = "更新人姓名长度必须介于 0 和 64 之间")
	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public boolean isInterfaceUse() {
		return interfaceUse;
	}

	public void setInterfaceUse(boolean interfaceUse) {
		this.interfaceUse = interfaceUse;
	}

	/**
	 * @return bing
	 */
	public String getBindingRoute() {
		return bindingRoute;
	}

	/**
	 * @param bing
	 *            要设置的 bing
	 */
	public void setBindingRoute(String bindingRoute) {
		this.bindingRoute = bindingRoute;
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

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getW() {
		return w;
	}

	public void setW(String w) {
		this.w = w;
	}

	public String getH() {
		return h;
	}

	public void setH(String h) {
		this.h = h;
	}

	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getBindingRfid() {
		return bindingRfid;
	}

	public void setBindingRfid(String bindingRfid) {
		this.bindingRfid = bindingRfid;
	}

	/**
	 * @return the userStamper
	 */
	public byte[] getUserStamper() {
		return userStamper;
	}

	/**
	 * @param userStamper
	 *            the userStamper to set
	 */
	public void setUserStamper(byte[] userStamper) {
		this.userStamper = userStamper;
	}

	public Long getUserFaceId() {
		return userFaceId;
	}

	public void setUserFaceId(Long userFaceId) {
		this.userFaceId = userFaceId;
	}

	public String getInitFaceIdFlag() {
		return initFaceIdFlag;
	}

	public void setInitFaceIdFlag(String initFaceIdFlag) {
		this.initFaceIdFlag = initFaceIdFlag;
	}

	public String getbindingFace() {
		return bindingFace;
	}

	public void setbindingFace(String bindingFace) {
		this.bindingFace = bindingFace;
	}
}