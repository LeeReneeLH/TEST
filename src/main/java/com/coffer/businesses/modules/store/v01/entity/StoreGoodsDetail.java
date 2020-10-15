/**
 * wenjian:    StoreGoodsDetail.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月8日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月8日 下午5:19:27
 */
package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;

import com.coffer.core.common.persistence.DataEntity;

/**
* Title: StoreGoodsDetail 
* <p>Description: 库房物品明细表</p>
* @author wangbaozhong
* @date 2017年8月8日 下午5:19:27
*/
public class StoreGoodsDetail extends DataEntity<StoreGoodsDetail> {

	/**serialVersionUID : */
	private static final long serialVersionUID = 1L;
	/** 库房物品信息表ID */
	private String storeGoodsId;
	/** 物品ID **/
	private String goodsId;
	/** 物品数量 **/
	private BigDecimal goodsNum;
	/** 物品数量 **/
	private BigDecimal amount;
	/**
	 * @return the storeGoodsId
	 */
	public String getStoreGoodsId() {
		return storeGoodsId;
	}
	/**
	 * @param storeGoodsId the storeGoodsId to set
	 */
	public void setStoreGoodsId(String storeGoodsId) {
		this.storeGoodsId = storeGoodsId;
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
	 * @return the goodsNum
	 */
	public BigDecimal getGoodsNum() {
		return goodsNum;
	}
	/**
	 * @param goodsNum the goodsNum to set
	 */
	public void setGoodsNum(BigDecimal goodsNum) {
		this.goodsNum = goodsNum;
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
}
