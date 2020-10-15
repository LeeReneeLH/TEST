package com.coffer.businesses.modules.store.v01.entity;

/**
 * 修改库存Entity
 * 
 * @author LLF
 * @version 2015-09-09
 */
public class ChangeStoreEntity {

	private String goodsId; // 物品ID
	private Long num; // 变更数量（增加正值；减少负值）
	private String goodType; // 物品类型

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public String getGoodType() {
		return goodType;
	}

	public void setGoodType(String goodType) {
		this.goodType = goodType;
	}

	@Override
	public String toString() {
		return "ChangeStoreEntity [goodsId=" + goodsId + ", num=" + num + ", goodType=" + goodType + "]";
	}

}