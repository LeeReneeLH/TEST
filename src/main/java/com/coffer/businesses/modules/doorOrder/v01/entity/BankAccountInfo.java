package com.coffer.businesses.modules.doorOrder.v01.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 银行卡管理Entity
 * 
 * @author yinkai
 * @version 2019-08-06
 */
public class BankAccountInfo extends DataEntity<BankAccountInfo> {

	private static final long serialVersionUID = 1L;
	private String accountNo; // 银行卡号
	private String officeId; // 归属机构ID
	private String accountName; // 账户名称
	private String cityCode; // 城市编号
	private String cityName; // 城市名称
	private String bankName; // 开户行名称
	private String createName; // 创建人名称
	private String updateName; // 更新人名称

	private String provinceCode;// 省份编码
	private String countyCode;// 区县编码
	private String status;// 绑定状态(0,绑定 1,解绑)

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	public BankAccountInfo() {
		super();
	}

	public BankAccountInfo(String id) {
		super(id);
	}

	@Length(min = 0, max = 64, message = "银行卡号长度必须介于 0 和 64 之间")
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	@Length(min = 0, max = 255, message = "账户名称长度必须介于 0 和 255 之间")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Length(min = 0, max = 8, message = "城市编号长度必须介于 0 和 8 之间")
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	@Length(min = 0, max = 255, message = "城市名称长度必须介于 0 和 255 之间")
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Length(min = 0, max = 255, message = "开户行名称长度必须介于 0 和 255 之间")
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Length(min = 0, max = 100, message = "创建人名称长度必须介于 0 和 100 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	@Length(min = 0, max = 100, message = "更新人名称长度必须介于 0 和 100 之间")
	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

}