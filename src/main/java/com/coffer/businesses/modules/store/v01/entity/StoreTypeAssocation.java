/**
 * wenjian:    StoreTypeAssocation.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月8日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月8日 下午5:09:21
 */
package com.coffer.businesses.modules.store.v01.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
* Title: StoreTypeAssocation 
* <p>Description: 库房物品类型关联表</p>
* @author wangbaozhong
* @date 2017年8月8日 下午5:09:21
*/
public class StoreTypeAssocation extends DataEntity<StoreTypeAssocation> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 库房管理主表ID */
	private String storeId;
	/** 存放物品类型 */
	private String storageType;
	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return storeId;
	}
	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	/**
	 * @return the storageType
	 */
	public String getStorageType() {
		return storageType;
	}
	/**
	 * @param storageType the storageType to set
	 */
	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}
}
