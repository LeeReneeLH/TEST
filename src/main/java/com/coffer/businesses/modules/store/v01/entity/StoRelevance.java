package com.coffer.businesses.modules.store.v01.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 物品关联配置Entity
 * 
 * @author yuxixuan
 * @version 2015-09-11
 */
public class StoRelevance extends DataEntity<StoRelevance> {

	private static final long serialVersionUID = 1L;
	private String relevanceId; // 编号
	private String groupId; // 组ID
	private String currency; // 币种
	private String classification; // 类别
	private String sets; // 套别
	private String cash; // 材质
	private String denomination; // 面值
	private String unit; // 单位
	private String bak; // 备用
	/** 面值List */
	private List<String> denomiList;
	/** 单位List */
	private List<String> unitList;
	private String currencyName;
	private String classificationName;
	private String setsName;
	private String cashName;
	/** 面值List */
	private String denomiListJson;
	/** 单位List */
	private String unitListJson;
	/** 币种关联代码 */
	private String currencyRefCode;
	/** 材质关联代码 */
	private String cashRefCode;
	/** ‘币种’保留项，逗号分隔 */
	private String currencyReserve;
	/** ‘币种’保留项，数组 */
	private List<String> currencyReserveList;
	/** ‘币种’移除项，逗号分隔 */
	private String currencyRemove;
	/** ‘币种’移除项，数组 */
	private List<String> currencyRemoveList;
	/** ‘类别’保留项，逗号分隔 */
	private String classificationReserve;
	/** ‘类别’保留项，数组 */
	private List<String> classificationReserveList;
	/** ‘类别’移除项，逗号分隔 */
	private String classificationRemove;
	/** ‘类别’移除项，数组 */
	private List<String> classificationRemoveList;
	/** ‘套别’保留项，逗号分隔 */
	private String editionReserve;
	/** ‘套别’保留项，数组 */
	private List<String> editionReserveList;
	/** ‘套别’移除项，逗号分隔 */
	private String editionRemove;
	/** ‘套别’移除项，数组 */
	private List<String> editionRemoveList;
	/** ‘材质’保留项，逗号分隔 */
	private String cashReserve;
	/** ‘材质’保留项，数组 */
	private List<String> cashReserveList;
	/** ‘材质’移除项，逗号分隔 */
	private String cashRemove;
	/** ‘材质’移除项，数组 */
	private List<String> cashRemoveList;
	/** ‘面值’保留项，逗号分隔 */
	private String denominationReserve;
	/** ‘面值’保留项，数组 */
	private List<String> denominationReserveList;
	/** ‘面值’移除项，逗号分隔 */
	private String denominationRemove;
	/** ‘面值’移除项，数组 */
	private List<String> denominationRemoveList;
	/** ‘单位’保留项，逗号分隔 */
	private String unitReserve;
	/** ‘单位’保留项，数组 */
	private List<String> unitReserveList;
	/** ‘单位’移除项，逗号分隔 */
	private String unitRemove;
	/** ‘单位’移除项，数组 */
	private List<String> unitRemoveList;

	public StoRelevance() {
		super();
	}

	public StoRelevance(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "编号长度必须介于 1 和 64 之间")
	public String getRelevanceId() {
		return relevanceId;
	}

	public void setRelevanceId(String relevanceId) {
		this.relevanceId = relevanceId;
	}

	@Length(min = 0, max = 17, message = "组ID长度必须介于 0 和 17 之间")
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Length(min = 3, max = 3, message = "币种长度必须为3")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Length(min = 2, max = 2, message = "类别长度必须为2")
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	@Length(min = 1, max = 1, message = "套别长度必须为1")
	public String getSets() {
		return sets;
	}

	public void setSets(String sets) {
		this.sets = sets;
	}

	@Length(min = 1, max = 1, message = "材质长度必须为1")
	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	@Length(min = 2, max = 2, message = "面值长度必须为2")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	@Length(min = 3, max = 3, message = "单位长度必须为3")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Length(min = 0, max = 3, message = "备用长度必须介于 0 和 3 之间")
	public String getBak() {
		return bak;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public List<String> getDenomiList() {
		return denomiList;
	}

	public void setDenomiList(List<String> denomiList) {
		this.denomiList = denomiList;
	}

	public List<String> getUnitList() {
		return unitList;
	}

	public void setUnitList(List<String> unitList) {
		this.unitList = unitList;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getClassificationName() {
		return classificationName;
	}

	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}

	public String getSetsName() {
		return setsName;
	}

	public void setSetsName(String setsName) {
		this.setsName = setsName;
	}

	public String getCashName() {
		return cashName;
	}

	public void setCashName(String cashName) {
		this.cashName = cashName;
	}

	public String getDenomiListJson() {
		return denomiListJson;
	}

	public void setDenomiListJson(String denomiListJson) {
		this.denomiListJson = denomiListJson;
	}

	public String getUnitListJson() {
		return unitListJson;
	}

	public void setUnitListJson(String unitListJson) {
		this.unitListJson = unitListJson;
	}

	public String getCurrencyRefCode() {
		return currencyRefCode;
	}

	public void setCurrencyRefCode(String currencyRefCode) {
		this.currencyRefCode = currencyRefCode;
	}

	public String getCashRefCode() {
		return cashRefCode;
	}

	public void setCashRefCode(String cashRefCode) {
		this.cashRefCode = cashRefCode;
	}

	public String getCurrencyReserve() {
		return currencyReserve;
	}

	public void setCurrencyReserve(String currencyReserve) {
		this.currencyReserve = currencyReserve;
	}

	public List<String> getCurrencyReserveList() {
		return currencyReserveList;
	}

	public void setCurrencyReserveList(List<String> currencyReserveList) {
		this.currencyReserveList = currencyReserveList;
	}

	public String getCurrencyRemove() {
		return currencyRemove;
	}

	public void setCurrencyRemove(String currencyRemove) {
		this.currencyRemove = currencyRemove;
	}

	public List<String> getCurrencyRemoveList() {
		return currencyRemoveList;
	}

	public void setCurrencyRemoveList(List<String> currencyRemoveList) {
		this.currencyRemoveList = currencyRemoveList;
	}

	public String getClassificationReserve() {
		return classificationReserve;
	}

	public void setClassificationReserve(String classificationReserve) {
		this.classificationReserve = classificationReserve;
	}

	public List<String> getClassificationReserveList() {
		return classificationReserveList;
	}

	public void setClassificationReserveList(List<String> classificationReserveList) {
		this.classificationReserveList = classificationReserveList;
	}

	public String getClassificationRemove() {
		return classificationRemove;
	}

	public void setClassificationRemove(String classificationRemove) {
		this.classificationRemove = classificationRemove;
	}

	public List<String> getClassificationRemoveList() {
		return classificationRemoveList;
	}

	public void setClassificationRemoveList(List<String> classificationRemoveList) {
		this.classificationRemoveList = classificationRemoveList;
	}

	public String getCashReserve() {
		return cashReserve;
	}

	public void setCashReserve(String cashReserve) {
		this.cashReserve = cashReserve;
	}

	public List<String> getCashReserveList() {
		return cashReserveList;
	}

	public void setCashReserveList(List<String> cashReserveList) {
		this.cashReserveList = cashReserveList;
	}

	public String getCashRemove() {
		return cashRemove;
	}

	public void setCashRemove(String cashRemove) {
		this.cashRemove = cashRemove;
	}

	public List<String> getCashRemoveList() {
		return cashRemoveList;
	}

	public void setCashRemoveList(List<String> cashRemoveList) {
		this.cashRemoveList = cashRemoveList;
	}

	public String getDenominationReserve() {
		return denominationReserve;
	}

	public void setDenominationReserve(String denominationReserve) {
		this.denominationReserve = denominationReserve;
	}

	public List<String> getDenominationReserveList() {
		return denominationReserveList;
	}

	public void setDenominationReserveList(List<String> denominationReserveList) {
		this.denominationReserveList = denominationReserveList;
	}

	public String getDenominationRemove() {
		return denominationRemove;
	}

	public void setDenominationRemove(String denominationRemove) {
		this.denominationRemove = denominationRemove;
	}

	public List<String> getDenominationRemoveList() {
		return denominationRemoveList;
	}

	public void setDenominationRemoveList(List<String> denominationRemoveList) {
		this.denominationRemoveList = denominationRemoveList;
	}

	public String getEditionReserve() {
		return editionReserve;
	}

	public void setEditionReserve(String editionReserve) {
		this.editionReserve = editionReserve;
	}

	public List<String> getEditionReserveList() {
		return editionReserveList;
	}

	public void setEditionReserveList(List<String> editionReserveList) {
		this.editionReserveList = editionReserveList;
	}

	public String getEditionRemove() {
		return editionRemove;
	}

	public void setEditionRemove(String editionRemove) {
		this.editionRemove = editionRemove;
	}

	public List<String> getEditionRemoveList() {
		return editionRemoveList;
	}

	public void setEditionRemoveList(List<String> editionRemoveList) {
		this.editionRemoveList = editionRemoveList;
	}

	public String getUnitReserve() {
		return unitReserve;
	}

	public void setUnitReserve(String unitReserve) {
		this.unitReserve = unitReserve;
	}

	public List<String> getUnitReserveList() {
		return unitReserveList;
	}

	public void setUnitReserveList(List<String> unitReserveList) {
		this.unitReserveList = unitReserveList;
	}

	public String getUnitRemove() {
		return unitRemove;
	}

	public void setUnitRemove(String unitRemove) {
		this.unitRemove = unitRemove;
	}

	public List<String> getUnitRemoveList() {
		return unitRemoveList;
	}

	public void setUnitRemoveList(List<String> unitRemoveList) {
		this.unitRemoveList = unitRemoveList;
	}

}