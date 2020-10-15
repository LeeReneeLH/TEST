package com.coffer.businesses.modules.collection.v03.entity;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 上门收款-门店Entity
 * 
 * @author wanglin
 * @version 2017-11-13
 */
public class ShopOffice extends DataEntity<ShopOffice>  {

	private static final long serialVersionUID = 1L;
	
	private String storeId; 	// 商户ID
	private String storeCode; 	// 商户编码
	private String storeNm; 	// 商户名
	private String code; 		// 门店编码
	private String oldCode; 	// 门店编码
	private String name; 		// 门店名称
	private String master;		// 负责人 
	private String address; 	// 联系地址
	private String zipCode; 	// 邮政编码
	private String phone; 		// 电话
	private String fax; 		// 传真
	private String email; 		// 邮箱
	private String enabledFlag;	// 启用标识（1：启用；0：禁用）

	public ShopOffice() {
		super();
	}

	public ShopOffice(String id) {
		super(id);
		this.id = id;
	}


	
	
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreCode() {
		return storeCode;
	}

	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}

	public String getStoreNm() {
		return storeNm;
	}

	public void setStoreNm(String storeNm) {
		this.storeNm = storeNm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min = 0, max = 255)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Length(min = 0, max = 100)
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	@Length(min = 0, max = 200)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Length(min = 0, max = 200)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Length(min = 0, max = 200)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Length(min = 0, max = 15)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public String getEnabledFlag() {
		return enabledFlag;
	}

	public void setEnabledFlag(String enabledFlag) {
		this.enabledFlag = enabledFlag;
	}

	public String getOldCode() {
		return oldCode;
	}

	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}


}