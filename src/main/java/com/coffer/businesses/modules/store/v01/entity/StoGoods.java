package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 物品管理Entity
 * @author Ray
 * @version 2015-09-10
 */
public class StoGoods extends DataEntity<StoGoods> {
	
	private static final long serialVersionUID = 1L;
	private String goodsID;
	private String goodsName;		// 物品名
	private String description;		// 描述
	private String goodsType; // 物品类型（01：货币，02：贵重金属，03：重空）
	private BigDecimal goodsVal; // 物品价值
	/** 物品属性 */
	private StoGoodSelect stoGoodSelect;
	/** 重空属性 */
	private StoBlankBillSelect stoBlankBillSelect;
	/** 增量日期查询条件**/
	private String updateDateStr;
	
	public StoGoods() {
		super();
	}

	public StoGoods(String id){
		super(id);
	}
	
	public String getId() {
		return goodsID;
	}

	public void setId(String goodsID) {
		this.goodsID = goodsID;
	}

	@Length(min=0, max=100, message="描述长度必须介于 0 和 100 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public StoGoodSelect getStoGoodSelect() {
		return stoGoodSelect;
	}

	public void setStoGoodSelect(StoGoodSelect stoGoodSelect) {
		this.stoGoodSelect = stoGoodSelect;
	}

	public String getGoodsID() {
		return goodsID;
	}

	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public BigDecimal getGoodsVal() {
		return goodsVal;
	}

	public void setGoodsVal(BigDecimal goodsVal) {
		this.goodsVal = goodsVal;
	}

	public StoBlankBillSelect getStoBlankBillSelect() {
		return stoBlankBillSelect;
	}

	public void setStoBlankBillSelect(StoBlankBillSelect stoBlankBillSelect) {
		this.stoBlankBillSelect = stoBlankBillSelect;
	}

	/**
	 * @return the updateDateStr
	 */
	public String getUpdateDateStr() {
		return updateDateStr;
	}

	/**
	 * @param updateDateStr the updateDateStr to set
	 */
	public void setUpdateDateStr(String updateDateStr) {
		this.updateDateStr = updateDateStr;
	}
}