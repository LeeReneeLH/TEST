package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 款项类型Entity
 * 
 * @author zhaohaoran
 * @version 2019-07-23
 */
public class SaveType extends DataEntity<SaveType>{
	
	private static final long serialVersionUID = 1L;
	private String typeCode;			// 存款类型代码
	private String typeName;			// 存款类型名称
	private String merchantId; 			// 商户编号
	private String merchantName; 		// 商户名称
	private Office office; 				// 所属机构
	
	
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	
}
