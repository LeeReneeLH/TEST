package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 回款管理Entity
 * 
 * @author XL
 * @version 2019-06-26
 */
public class BackAccountsMain extends DataEntity<BackAccountsMain> {

	private static final long serialVersionUID = 1L;
	private String custNo; // 客户编号
	private String custName; // 客户名称
	private String businessId; // 业务流水
	private String officeId; // 账务发生机构ID
	private String officeName;// 账务发生机构名称
	private String companyId; // 账务归属公司ID
	private String companyName; // 账务归属公司名称
	private BigDecimal outAmount; // 出库总金额（借）
	private String status; // 状态标识（0-登记；1-完成）
	private byte[] photo; // 凭条照片
	private String bankCard;// 银行卡号
	private String backNumber;//回款单号
	/*添加门店编号 门店名称  gzd 2019-11-29 begin*/
	private String doorId; // 门店编号
	private String doorName; // 门店名称
	
	public String getDoorId() {
		return doorId;
	}
	
	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}
	/*end*/
	/** 剪裁图片坐标X */
	private String x;
	/** 剪裁图片坐标Y */
	private String y;
	/** 剪裁图片宽度 */
	private String w;
	/** 剪裁图片高度 */
	private String h;
	/** 上传图片URL路径 */
	private String picPath;
	/** 上传图片文件名 */
	private String tmpPicFileName;
	/** 是否需要上传图片标志 */
	private String uploadFlag;

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

	public String getBackNumber() {
		return backNumber;
	}

	public void setBackNumber(String backNumber) {
		this.backNumber = backNumber;
	}
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getTmpPicFileName() {
		return tmpPicFileName;
	}

	public void setTmpPicFileName(String tmpPicFileName) {
		this.tmpPicFileName = tmpPicFileName;
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

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public BackAccountsMain() {
		super();
	}

	public BackAccountsMain(String id) {
		super(id);
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
}