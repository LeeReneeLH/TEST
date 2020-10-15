/**
 * wenjian:    StoreCoOfficeAssocation.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月8日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月8日 下午5:12:41
 */
package com.coffer.businesses.modules.store.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
* Title: StoreCoOfficeAssocation 
* <p>Description: 库房机构关联表</p>
* @author wangbaozhong
* @date 2017年8月8日 下午5:12:41
*/
public class StoreCoOfficeAssocation extends DataEntity<StoreCoOfficeAssocation> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 库房管理主表ID */
	private String storeId;
	/** 所属机构 */
	private Office office;
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
	 * @return the office
	 */
	public Office getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(Office office) {
		this.office = office;
	}
}
