/**
 * wenjian:    StoreManagementInfo.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月8日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月8日 下午5:06:24
 */
package com.coffer.businesses.modules.store.v01.entity;

import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
* Title: StoreManagementInfo 
* <p>Description: 库房管理主表</p>
* @author wangbaozhong
* @date 2017年8月8日 下午5:06:24
*/
public class StoreManagementInfo extends DataEntity<StoreManagementInfo> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 库房名称 */
	private String storeName;
	/** 旧库房名称 */
	private String oldStoreName;
	/** 所属机构 */
	private Office office;
	/** 保存箱袋类型*/
	private List<String> boxTypeList = Lists.newArrayList();
	/** 使用机构列表**/
	private List<String> officeIdList = Lists.newArrayList();
	/** 管库员列表**/
	private List<String> userIdList = Lists.newArrayList();
	
	/** 库房物品类型关联列表 */
	private List<StoreTypeAssocation> storeTypeAssocationList = Lists.newArrayList();
	/** 库房管理人员关联表 */
	private List<StoreManagerAssocation> storeManagerAssocationList = Lists.newArrayList();
	/** 库房机构关联列表 */
	private List<StoreCoOfficeAssocation> storeCoOfficeAssocationList = Lists.newArrayList();
	/** 库房物品信息列表 */
	private List<StoreGoodsInfo> storeGoodsInfoList = Lists.newArrayList();
	
	/**
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}
	/**
	 * @param storeName the storeName to set
	 */
	public void setStoreName(String storeName) {
		this.storeName = storeName;
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
	/**
	 * @return the storeTypeAssocationList
	 */
	public List<StoreTypeAssocation> getStoreTypeAssocationList() {
		return storeTypeAssocationList;
	}
	/**
	 * @param storeTypeAssocationList the storeTypeAssocationList to set
	 */
	public void setStoreTypeAssocationList(List<StoreTypeAssocation> storeTypeAssocationList) {
		this.storeTypeAssocationList = storeTypeAssocationList;
	}
	/**
	 * @return the storeManagerAssocationList
	 */
	public List<StoreManagerAssocation> getStoreManagerAssocationList() {
		return storeManagerAssocationList;
	}
	/**
	 * @param storeManagerAssocationList the storeManagerAssocationList to set
	 */
	public void setStoreManagerAssocationList(List<StoreManagerAssocation> storeManagerAssocationList) {
		this.storeManagerAssocationList = storeManagerAssocationList;
	}
	/**
	 * @return the storeCoOfficeAssocationList
	 */
	public List<StoreCoOfficeAssocation> getStoreCoOfficeAssocationList() {
		return storeCoOfficeAssocationList;
	}
	/**
	 * @param storeCoOfficeAssocationList the storeCoOfficeAssocationList to set
	 */
	public void setStoreCoOfficeAssocationList(List<StoreCoOfficeAssocation> storeCoOfficeAssocationList) {
		this.storeCoOfficeAssocationList = storeCoOfficeAssocationList;
	}
	/**
	 * @return the storeGoodsInfoList
	 */
	public List<StoreGoodsInfo> getStoreGoodsInfoList() {
		return storeGoodsInfoList;
	}
	/**
	 * @param storeGoodsInfoList the storeGoodsInfoList to set
	 */
	public void setStoreGoodsInfoList(List<StoreGoodsInfo> storeGoodsInfoList) {
		this.storeGoodsInfoList = storeGoodsInfoList;
	}
	/**
	 * @return the boxTypeList
	 */
	public List<String> getBoxTypeList() {
		return boxTypeList;
	}
	/**
	 * @param boxTypeList the boxTypeList to set
	 */
	public void setBoxTypeList(List<String> boxTypeList) {
		this.boxTypeList = boxTypeList;
	}
	/**
	 * @return the officeIdList
	 */
	public List<String> getOfficeIdList() {
		return officeIdList;
	}
	/**
	 * @param officeIdList the officeIdList to set
	 */
	public void setOfficeIdList(List<String> officeIdList) {
		this.officeIdList = officeIdList;
	}
	/**
	 * @return the userIdList
	 */
	public List<String> getUserIdList() {
		return userIdList;
	}
	/**
	 * @param userIdList the userIdList to set
	 */
	public void setUserIdList(List<String> userIdList) {
		this.userIdList = userIdList;
	}
	/**
	 * @return the oldStoreName
	 */
	public String getOldStoreName() {
		return oldStoreName;
	}
	/**
	 * @param oldStoreName the oldStoreName to set
	 */
	public void setOldStoreName(String oldStoreName) {
		this.oldStoreName = oldStoreName;
	}
}
