/**
 * @author WangBaozhong
 * @version 2016年5月13日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.entity;

import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;

/**
 * 库房区域设定Entity
 * @author WangBaozhong
 *
 */
public class StoAreaSettingInfo extends DataEntity<StoAreaSettingInfo> {

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 998981981120388443L;
	
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
	 * 所属机构ID
	 */
	private String officeId;

	/**
	 * 行位置
	 */
	private Integer xPosition;
	/**
	 * 列位置
	 */
	private Integer yPosition;
	/**
	 * 总行数
	 */
	private Integer rowCnt;
	/**
	 * 总列数
	 */
	private Integer colCnt;
	/**
	 * 最大容量
	 */
	private Integer maxCapability;
	/**
	 * 最大保存日数
	 */
	private Integer maxSaveDays;
	
	/**
	 * 最小保存日数
	 */
	private Integer minSaveDays;
	
	/**
	 * 现存数量
	 */
	private Long stoNum;
	
	/**
	 * 预警标识
	 */
	private String slamCode;
	
	/**
	 * 区域内入库最大日期
	 */
	private Date areaMaxDate;
	/**
	 * 区域内入库最小日期
	 */
	private Date areaMinDate;
	
	/**
	 * 库区实际使用量
	 */
	private Long actualStorage;
	
	/**
	 * 库区实际剩余量
	 */
	private Long surplusStorage;
	
	/**
	 * 排序
	 */
	private String orderBy;
	/**
	 * 库区摆放物品ID
	 */
	private String goodsId;
	
	/**
	 * 库位排序
	 */
	private Integer sortKey;
	
	/**
	 * 
	 * 在库物品数量
	 * 
	 */
	private Long goodsCnt;
	
	/**
	 * 
	 * 机构id列表
	 * 
	 */
	private List<String> officeIdList;
	
	/**
	 * 
	 * 机构id名称
	 * 
	 */
	private String officeName;

	/**
     * 库区内物品信息
     */
    private List<StoGoodsLocationInfo> goodsLocationInfoList = Lists.newArrayList();
	
	/**
	 * @return surplusStorage
	 */
	public Long getSurplusStorage() {
		return surplusStorage;
	}

	/**
	 * @param surplusStorage 要设置的 surplusStorage
	 */
	public void setSurplusStorage(Long surplusStorage) {
		this.surplusStorage = surplusStorage;
	}

	/**
	 * @return actualStorage
	 */
	public Long getActualStorage() {
		return actualStorage;
	}

	/**
	 * @param actualStorage 要设置的 actualStorage
	 */
	public void setActualStorage(Long actualStorage) {
		this.actualStorage = actualStorage;
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
	 * @return xPosition
	 */
	public Integer getxPosition() {
		return xPosition;
	}

	/**
	 * @param xPosition 要设置的 xPosition
	 */
	public void setxPosition(Integer xPosition) {
		this.xPosition = xPosition;
	}

	/**
	 * @return yPosition
	 */
	public Integer getyPosition() {
		return yPosition;
	}

	/**
	 * @param yPosition 要设置的 yPosition
	 */
	public void setyPosition(Integer yPosition) {
		this.yPosition = yPosition;
	}

	/**
	 * @return rowCnt
	 */
	public Integer getRowCnt() {
		return rowCnt;
	}

	/**
	 * @param rowCnt 要设置的 rowCnt
	 */
	public void setRowCnt(Integer rowCnt) {
		this.rowCnt = rowCnt;
	}

	/**
	 * @return colCnt
	 */
	public Integer getColCnt() {
		return colCnt;
	}

	/**
	 * @param colCnt 要设置的 colCnt
	 */
	public void setColCnt(Integer colCnt) {
		this.colCnt = colCnt;
	}

	/**
	 * @return maxCapability
	 */
	public Integer getMaxCapability() {
		return maxCapability;
	}

	/**
	 * @param maxCapability 要设置的 maxCapability
	 */
	public void setMaxCapability(Integer maxCapability) {
		this.maxCapability = maxCapability;
	}

	/**
	 * @return maxSaveDays
	 */
	public Integer getMaxSaveDays() {
		return maxSaveDays;
	}

	/**
	 * @param maxSaveDays 要设置的 maxSaveDays
	 */
	public void setMaxSaveDays(Integer maxSaveDays) {
		this.maxSaveDays = maxSaveDays;
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
	 * @return slamCode
	 */
	public String getSlamCode() {
		return slamCode;
	}

	/**
	 * @param slamCode 要设置的 slamCode
	 */
	public void setSlamCode(String slamCode) {
		this.slamCode = slamCode;
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
	 * @return minSaveDays
	 */
	public Integer getMinSaveDays() {
		return minSaveDays;
	}

	/**
	 * @param minSaveDays 要设置的 minSaveDays
	 */
	public void setMinSaveDays(Integer minSaveDays) {
		this.minSaveDays = minSaveDays;
	}

	/**
	 * @return orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * @param orderBy 要设置的 orderBy
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

    public List<StoGoodsLocationInfo> getGoodsLocationInfoList() {
        return goodsLocationInfoList;
    }

    public void setGoodsLocationInfoList(List<StoGoodsLocationInfo> goodsLocationInfoList) {
        this.goodsLocationInfoList = goodsLocationInfoList;
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
	 * @return the goodsId
	 */
	public String getGoodsId() {
		return goodsId;
	}

	/**
	 * @param goodsId the goodsId to set
	 */
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	
	
    /**
	 * @return the sortKey
	 */
	public Integer getSortKey() {
		return sortKey;
	}

	/**
	 * @param sortKey the sortKey to set
	 */
	public void setSortKey(Integer sortKey) {
		this.sortKey = sortKey;
	}

	/**
	 * @return the goodsCnt
	 */
	public Long getGoodsCnt() {
		return goodsCnt;
	}

	/**
	 * @param goodsCnt the goodsCnt to set
	 */
	public void setGoodsCnt(Long goodsCnt) {
		this.goodsCnt = goodsCnt;
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
	 * @return the officeName
	 */
	public String getOfficeName() {
		return officeName;
	}

	/**
	 * @param officeName the officeName to set
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

}
