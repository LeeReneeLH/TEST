package com.coffer.businesses.modules.store.v01.entity;

import com.coffer.core.common.persistence.DataEntity;

public class StoBoxDetail extends DataEntity<StoBoxDetail> {

	private static final long serialVersionUID = 1L;

	/** 主键 **/
	private String detailId;
	/** 箱袋编号 **/
	private String boxNo;
	/** 物品ID **/
	private String goodsId;
	/** 物品数量 **/
	private String goodsNum;
	/** 物品金额 **/
	private String goodsAmount;

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}

	public String getGoodsAmount() {
		return goodsAmount;
	}

	public void setGoodsAmount(String goodsAmount) {
		this.goodsAmount = goodsAmount;
	}

}
