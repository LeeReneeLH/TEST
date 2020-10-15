package com.coffer.businesses.modules.allocation.v02.entity;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.google.common.collect.Lists;

/**
 * 人行调拨物品管理Entity
 * @author LLF
 * @version 2016-05-25
 */
public class PbocAllAllocateItem extends DataEntity<PbocAllAllocateItem> {
	
	private static final long serialVersionUID = 1L;
	private String allItemsId;		// 主键
	private String allId;		// 流水单号
	private String boxNo;		// 箱袋编号
	private String registType;		// 登记类型10：人行审批；11：商行登记
	private String currency;		// 币种
	private String classification;		// 类型
	private String cash;		// 材质
	private String denomination;		// 面值
	private String unit;		// 单位
	private Long moneyNumber;		// 数量
	private BigDecimal moneyAmount;		// 金额
	private String sets;		// 套别
	private String bak;		// 备用
	/** 审批数量 */
	private Long confirmNumber;
	/** 审批金额 */
	private BigDecimal confirmAmount;
	/** 物品ID **/
	private String goodsId;
	/** 物品名称 **/
	private String goodsName;
	/**
	 * 调拨物品与库区位置关联
	 */
	private List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = Lists.newArrayList();
	/**
	 * @return goodsAreaDetailList
	 */
	public List<AllAllocateGoodsAreaDetail> getGoodsAreaDetailList() {
		return goodsAreaDetailList;
	}

	/**
	 * @param goodsAreaDetailList 要设置的 goodsAreaDetailList
	 */
	public void setGoodsAreaDetailList(List<AllAllocateGoodsAreaDetail> goodsAreaDetailList) {
		this.goodsAreaDetailList = goodsAreaDetailList;
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

	public PbocAllAllocateItem() {
		super();
	}

	public PbocAllAllocateItem(String id){
		super(id);
	}

	
	
	@Length(min=0, max=64, message="流水单号长度必须介于 0 和 64 之间")
	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}
	
	@Length(min=0, max=64, message="箱袋编号长度必须介于 0 和 64 之间")
	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	
	@Length(min=0, max=2, message="登记类型10：人行审批；11：商行登记长度必须介于 0 和 2 之间")
	public String getRegistType() {
		return registType;
	}

	public void setRegistType(String registType) {
		this.registType = registType;
	}
	
	@Length(min=0, max=3, message="币种长度必须介于 0 和 3 之间")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@Length(min=0, max=3, message="类型长度必须介于 0 和 3 之间")
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	@Length(min=0, max=1, message="材质长度必须介于 0 和 1 之间")
	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}
	
	@Length(min=0, max=2, message="面值长度必须介于 0 和 2 之间")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	
	@Length(min=0, max=3, message="单位长度必须介于 0 和 3 之间")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public Long getMoneyNumber() {
		return moneyNumber;
	}

	public void setMoneyNumber(Long moneyNumber) {
		this.moneyNumber = moneyNumber;
	}
	
	public BigDecimal getMoneyAmount() {
		return moneyAmount;
	}

	public void setMoneyAmount(BigDecimal moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
	
	@Length(min=0, max=1, message="套别长度必须介于 0 和 1 之间")
	public String getSets() {
		return sets;
	}

	public void setSets(String sets) {
		this.sets = sets;
	}
	
	@Length(min=0, max=3, message="备用长度必须介于 0 和 3 之间")
	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}
	
	@Length(min=0, max=50, message="创建人名称长度必须介于 0 和 50 之间")
	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}
	
	@Length(min=0, max=50, message="更新人名称长度必须介于 0 和 50 之间")
	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	/**
	 * @return confirmNumber
	 */
	public Long getConfirmNumber() {
		return confirmNumber;
	}

	/**
	 * @param confirmNumber 要设置的 confirmNumber
	 */
	public void setConfirmNumber(Long confirmNumber) {
		this.confirmNumber = confirmNumber;
	}

	/**
	 * @return confirmAmount
	 */
	public BigDecimal getConfirmAmount() {
		return confirmAmount;
	}

	/**
	 * @param confirmAmount 要设置的 confirmAmount
	 */
	public void setConfirmAmount(BigDecimal confirmAmount) {
		this.confirmAmount = confirmAmount;
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
}