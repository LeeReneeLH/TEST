/**
 * @author WangBaozhong
 * @version 2016年5月23日
 * 
 * 
 */
package com.coffer.businesses.modules.allocation.v02.entity;

import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.persistence.DataEntity;

/**
 * 调拨物品与库区位置关联表Entity
 * @author WangBaozhong
 *
 */
public class AllAllocateGoodsAreaDetail extends DataEntity<AllAllocateGoodsAreaDetail>{
	
	/** 序列化ID */
	private static final long serialVersionUID = -7524712589451850757L;
	
	/** 缴调流水号 */
	private String allId;

	/** 调拨物品明细ID */
	private String allItemsId;

	/** 物品位置ID */
	private String goodsLocationId;

	/** 是否必须出库 0：必须，1：非必须 */
	private String isNecessaryOut;
	
    /** RFID */
    private String rfid;
    /**
     * 所在库区名称
     */
    private String storeAreaName;
	
	/**库区物品摆放**/
	private StoGoodsLocationInfo goodsLocationInfo;
	/**原封箱信息**/
	private StoOriginalBanknote stoOriginalBanknote;
	
	/**
	 * @return goodsLocationInfo
	 */
	public StoGoodsLocationInfo getGoodsLocationInfo() {
		return goodsLocationInfo;
	}
	/**
	 * @param goodsLocationInfo 要设置的 goodsLocationInfo
	 */
	public void setGoodsLocationInfo(StoGoodsLocationInfo goodsLocationInfo) {
		this.goodsLocationInfo = goodsLocationInfo;
	}
	/**
	 * @return allItemsId
	 */
	public String getAllItemsId() {
		return allItemsId;
	}
	/**
	 * @param allItemsId 要设置的 allItemsId
	 */
	public void setAllItemsId(String allItemsId) {
		this.allItemsId = allItemsId;
	}
	/**
	 * @return goodsLocationId
	 */
	public String getGoodsLocationId() {
		return goodsLocationId;
	}
	/**
	 * @param goodsLocationId 要设置的 goodsLocationId
	 */
	public void setGoodsLocationId(String goodsLocationId) {
		this.goodsLocationId = goodsLocationId;
	}
	/**
	 * @return isNecessaryOut
	 */
	public String getIsNecessaryOut() {
		return isNecessaryOut;
	}
	/**
	 * @param isNecessaryOut 要设置的 isNecessaryOut
	 */
	public void setIsNecessaryOut(String isNecessaryOut) {
		this.isNecessaryOut = isNecessaryOut;
	}
	/**
	 * @return allId
	 */
	public String getAllId() {
		return allId;
	}
	/**
	 * @param allId 要设置的 allId
	 */
	public void setAllId(String allId) {
		this.allId = allId;
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
	 * @return stoOriginalBanknote
	 */
	public StoOriginalBanknote getStoOriginalBanknote() {
		return stoOriginalBanknote;
	}
	/**
	 * @param stoOriginalBanknote 要设置的 stoOriginalBanknote
	 */
	public void setStoOriginalBanknote(StoOriginalBanknote stoOriginalBanknote) {
		this.stoOriginalBanknote = stoOriginalBanknote;
	}
	/**
	 * @return the storeAreaName
	 */
	public String getStoreAreaName() {
		return storeAreaName;
	}
	/**
	 * @param storeAreaName the storeAreaName to set
	 */
	public void setStoreAreaName(String storeAreaName) {
		this.storeAreaName = storeAreaName;
	}
}
