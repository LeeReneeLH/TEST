/**
 * wenjian:    StoreManagerAssocation.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月8日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月8日 下午5:11:18
 */
package com.coffer.businesses.modules.store.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.User;

/**
* Title: StoreManagerAssocation 
* <p>Description: 库房管理人员关联表</p>
* @author wangbaozhong
* @date 2017年8月8日 下午5:11:18
*/
public class StoreManagerAssocation extends DataEntity<StoreManagerAssocation> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 库房管理主表ID */
	private String storeId;
	/** 管库员 */
	private User user;
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
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
