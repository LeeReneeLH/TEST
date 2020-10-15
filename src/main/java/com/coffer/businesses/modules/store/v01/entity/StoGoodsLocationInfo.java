/**
 * @author WangBaozhong
 * @version 2016年5月16日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.User;

/**
 * 库区物品摆放Entity
 * @author WangBaozhong
 *
 */
public class StoGoodsLocationInfo extends DataEntity<StoGoodsLocationInfo> {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 3559324782831089874L;
	
	/**
	 * RFID
	 */
	private String rfid;
	/**
	 * 库房区域ID
	 */
	private String storeAreaId;
	/**
	 * 库房区域名称
	 */
	private String storeAreaName;
	/**
	 * 库区类型 
	 * 01:流通券库  02：原封券库 03：残损未复点券库 04：残损已复点券库
	 */
	private String storeAreaType;
	/**
	 * 物品ID
	 */
	private String goodsId;
	
	/**
	 * 物品名称
	 */
	private String goodsName;
	/**
	 * 物品类型
	 */
	private String goodsType;
	/**
	 * 物品数量
	 */
	private Long goodsNum;
	/**
	 * 总价值
	 */
	private BigDecimal amount;
	/**
	 * 所属金库
	 */
	private String officeId;
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
	
	/**
	 * 现存数量
	 */
	private Long stoNum;
	
	/**
	 * 区域内入库最大日期
	 */
	private Date areaMaxDate;
	/**
	 * 区域内入库最小日期
	 */
	private Date areaMinDate;
	
	/** 页面对应的开始时间和结束时间（查询用） **/
	private Date inStoreDateStart;
	private Date inStoreDateEnd;
	
	/** 页面对应的开始时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	
	/**当前登陆用户信息 */
	private User loginUser;
	
	/**
	 * 物品ID列表
	 */
	private List<String> goodsIdList;
	/** 显示出入库超链接 **/
	private String displayHref;
	
	/** 物品状态列表 **/
	private List<String> statusFlagList;
	
	/** 机构id列表 **/
	private List<String> officeIdList;
	
	/**
	 * @return displayHref
	 */
	public String getDisplayHref() {
		return displayHref;
	}
	/**
	 * @param displayHref 要设置的 displayHref
	 */
	public void setDisplayHref(String displayHref) {
		this.displayHref = displayHref;
	}
	/**
	 * @return rfid
	 */
	public String getRfid() {
		return rfid;
	}
	/**
	 * @param rfid 要设置的 rfid
	 */
	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
	/**
	 * @return storeAreaId
	 */
	public String getStoreAreaId() {
		return storeAreaId;
	}
	/**
	 * @param storeAreaId 要设置的 storeAreaId
	 */
	public void setStoreAreaId(String storeAreaId) {
		this.storeAreaId = storeAreaId;
	}
	/**
	 * @return goodsId
	 */
	public String getGoodsId() {
		return goodsId;
	}
	/**
	 * @param goodsId 要设置的 goodsId
	 */
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	/**
	 * @return goodsType
	 */
	public String getGoodsType() {
		return goodsType;
	}
	/**
	 * @param goodsType 要设置的 goodsType
	 */
	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}
	/**
	 * @return amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount 要设置的 amount
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return officeId
	 */
	public String getOfficeId() {
		return officeId;
	}
	/**
	 * @param officeId 要设置的 officeId
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	/**
	 * @return inStoreAllId
	 */
	public String getInStoreAllId() {
		return inStoreAllId;
	}
	/**
	 * @param inStoreAllId 要设置的 inStoreAllId
	 */
	public void setInStoreAllId(String inStoreAllId) {
		this.inStoreAllId = inStoreAllId;
	}
	/**
	 * @return outStoreAllId
	 */
	public String getOutStoreAllId() {
		return outStoreAllId;
	}
	/**
	 * @param outStoreAllId 要设置的 outStoreAllId
	 */
	public void setOutStoreAllId(String outStoreAllId) {
		this.outStoreAllId = outStoreAllId;
	}
	/**
	 * @return inStoreDate
	 */
	public Date getInStoreDate() {
		return inStoreDate;
	}
	/**
	 * @param inStoreDate 要设置的 inStoreDate
	 */
	public void setInStoreDate(Date inStoreDate) {
		this.inStoreDate = inStoreDate;
	}
	/**
	 * @return outStoreDate
	 */
	public Date getOutStoreDate() {
		return outStoreDate;
	}
	/**
	 * @param outStoreDate 要设置的 outStoreDate
	 */
	public void setOutStoreDate(Date outStoreDate) {
		this.outStoreDate = outStoreDate;
	}
	/**
	 * @return goodsNum
	 */
	public Long getGoodsNum() {
		return goodsNum;
	}
	/**
	 * @param goodsNum 要设置的 goodsNum
	 */
	public void setGoodsNum(Long goodsNum) {
		this.goodsNum = goodsNum;
	}
	/**
	 * @return stoNum
	 */
	public Long getStoNum() {
		return stoNum;
	}
	/**
	 * @param stoNum 要设置的 stoNum
	 */
	public void setStoNum(Long stoNum) {
		this.stoNum = stoNum;
	}
	/**
	 * @return areaMaxDate
	 */
	public Date getAreaMaxDate() {
		return areaMaxDate;
	}
	/**
	 * @param areaMaxDate 要设置的 areaMaxDate
	 */
	public void setAreaMaxDate(Date areaMaxDate) {
		this.areaMaxDate = areaMaxDate;
	}
	/**
	 * @return areaMinDate
	 */
	public Date getAreaMinDate() {
		return areaMinDate;
	}
	/**
	 * @param areaMinDate 要设置的 areaMinDate
	 */
	public void setAreaMinDate(Date areaMinDate) {
		this.areaMinDate = areaMinDate;
	}
	
	/**
	 * @return storeAreaName
	 */
	public String getStoreAreaName() {
		return storeAreaName;
	}
	/**
	 * @param storeAreaName 要设置的 storeAreaName
	 */
	public void setStoreAreaName(String storeAreaName) {
		this.storeAreaName = storeAreaName;
	}
	/**
	 * @return goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}
	/**
	 * @param goodsName 要设置的 goodsName
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	/**
	 * @return inStoreDateStart
	 */
	public Date getInStoreDateStart() {
		return inStoreDateStart;
	}
	/**
	 * @param inStoreDateStart 要设置的 inStoreDateStart
	 */
	public void setInStoreDateStart(Date inStoreDateStart) {
		this.inStoreDateStart = inStoreDateStart;
	}
	/**
	 * @return inStoreDateEnd
	 */
	public Date getInStoreDateEnd() {
		return inStoreDateEnd;
	}
	/**
	 * @param inStoreDateEnd 要设置的 inStoreDateEnd
	 */
	public void setInStoreDateEnd(Date inStoreDateEnd) {
		this.inStoreDateEnd = inStoreDateEnd;
	}
	/**
	 * @return searchDateStart
	 */
	public String getSearchDateStart() {
		return searchDateStart;
	}
	/**
	 * @param searchDateStart 要设置的 searchDateStart
	 */
	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}
	/**
	 * @return searchDateEnd
	 */
	public String getSearchDateEnd() {
		return searchDateEnd;
	}
	/**
	 * @param searchDateEnd 要设置的 searchDateEnd
	 */
	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}
	/**
	 * @return loginUser
	 */
	public User getLoginUser() {
		return loginUser;
	}
	/**
	 * @param loginUser 要设置的 loginUser
	 */
	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
	/**
	 * @return goodsIdList
	 */
	public List<String> getGoodsIdList() {
		return goodsIdList;
	}
	/**
	 * @param goodsIdList 要设置的 goodsIdList
	 */
	public void setGoodsIdList(List<String> goodsIdList) {
		this.goodsIdList = goodsIdList;
	}
	/**
	 * @return the storeAreaType
	 */
	public String getStoreAreaType() {
		return storeAreaType;
	}
	/**
	 * @param storeAreaType the storeAreaType to set
	 */
	public void setStoreAreaType(String storeAreaType) {
		this.storeAreaType = storeAreaType;
	}
	/**
	 * @return the statusFlagList
	 */
	public List<String> getStatusFlagList() {
		return statusFlagList;
	}
	/**
	 * @param statusFlagList the statusFlagList to set
	 */
	public void setStatusFlagList(List<String> statusFlagList) {
		this.statusFlagList = statusFlagList;
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
	
}
