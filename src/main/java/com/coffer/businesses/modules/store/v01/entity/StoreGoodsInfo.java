/**
 * wenjian:    StoreGoodsInfo.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月8日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月8日 下午5:14:55
 */
package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
* Title: StoreGoodsInfo 
* <p>Description: 库房物品信息表</p>
* @author wangbaozhong
* @date 2017年8月8日 下午5:14:55
*/
public class StoreGoodsInfo extends DataEntity<StoreGoodsInfo> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 库房管理主表ID */
	private String storeId;
	/** 库房名称 */
	private String storeName;
	/** RFID */
	private String rfid;
	/** 箱袋编号 */
	private String boxNo;
	/** 箱袋类型 */
	private String boxType;
	/** 钞箱金额 */
	private BigDecimal amount;
	/** 使用机构 */
	private Office office;
	/** 库房物品明细列表 */
	private List<StoreGoodsDetail> storeGoodsDetailList = Lists.newArrayList();
	/**
	 * 入库流水单号
	 */
	private String inStoreAllId;
	/**
	 * 出库流水单号
	 */
	private String outStoreAllId;
	/**
	 * 入库时间
	 */
	private Date inStoreDate;
	/**
	 * 出库时间
	 */
	private Date outStoreDate;
	/**尾箱预约出库时间*/
	private Date outDate;
	
	/** 页面对应的开始时间和结束时间（查询用） **/
	private Date inStoreDateStart;
	private Date inStoreDateEnd;
	
	/** 入库查询开始时间 */
	private String searchDateStart;
	/** 入库查询结束时间 */
	private String searchDateEnd;
	
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
	 * @return the rfid
	 */
	public String getRfid() {
		return rfid;
	}
	/**
	 * @param rfid the rfid to set
	 */
	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
	/**
	 * @return the boxNo
	 */
	public String getBoxNo() {
		return boxNo;
	}
	/**
	 * @param boxNo the boxNo to set
	 */
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	/**
	 * @return the boxType
	 */
	public String getBoxType() {
		return boxType;
	}
	/**
	 * @param boxType the boxType to set
	 */
	public void setBoxType(String boxType) {
		this.boxType = boxType;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
	 * @return the inStoreAllId
	 */
	public String getInStoreAllId() {
		return inStoreAllId;
	}
	/**
	 * @param inStoreAllId the inStoreAllId to set
	 */
	public void setInStoreAllId(String inStoreAllId) {
		this.inStoreAllId = inStoreAllId;
	}
	/**
	 * @return the outStoreAllId
	 */
	public String getOutStoreAllId() {
		return outStoreAllId;
	}
	/**
	 * @param outStoreAllId the outStoreAllId to set
	 */
	public void setOutStoreAllId(String outStoreAllId) {
		this.outStoreAllId = outStoreAllId;
	}
	/**
	 * @return the inStoreDate
	 */
	public Date getInStoreDate() {
		return inStoreDate;
	}
	/**
	 * @param inStoreDate the inStoreDate to set
	 */
	public void setInStoreDate(Date inStoreDate) {
		this.inStoreDate = inStoreDate;
	}
	/**
	 * @return the outStoreDate
	 */
	public Date getOutStoreDate() {
		return outStoreDate;
	}
	/**
	 * @param outStoreDate the outStoreDate to set
	 */
	public void setOutStoreDate(Date outStoreDate) {
		this.outStoreDate = outStoreDate;
	}
	/**
	 * @return the storeGoodsDetailList
	 */
	public List<StoreGoodsDetail> getStoreGoodsDetailList() {
		return storeGoodsDetailList;
	}
	/**
	 * @param storeGoodsDetailList the storeGoodsDetailList to set
	 */
	public void setStoreGoodsDetailList(List<StoreGoodsDetail> storeGoodsDetailList) {
		this.storeGoodsDetailList = storeGoodsDetailList;
	}
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
	 * @return the searchDateStart
	 */
	public String getSearchDateStart() {
		return searchDateStart;
	}
	/**
	 * @param searchDateStart the searchDateStart to set
	 */
	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}
	/**
	 * @return the searchDateEnd
	 */
	public String getSearchDateEnd() {
		return searchDateEnd;
	}
	/**
	 * @param searchDateEnd the searchDateEnd to set
	 */
	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}
	/**
	 * @return the inStoreDateStart
	 */
	public Date getInStoreDateStart() {
		return inStoreDateStart;
	}
	/**
	 * @param inStoreDateStart the inStoreDateStart to set
	 */
	public void setInStoreDateStart(Date inStoreDateStart) {
		this.inStoreDateStart = inStoreDateStart;
	}
	/**
	 * @return the inStoreDateEnd
	 */
	public Date getInStoreDateEnd() {
		return inStoreDateEnd;
	}
	/**
	 * @param inStoreDateEnd the inStoreDateEnd to set
	 */
	public void setInStoreDateEnd(Date inStoreDateEnd) {
		this.inStoreDateEnd = inStoreDateEnd;
	}
	/**
	 * @return the outDate
	 */
	public Date getOutDate() {
		return outDate;
	}
	/**
	 * @param outDate the outDate to set
	 */
	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}
}
