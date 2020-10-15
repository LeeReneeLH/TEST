package com.coffer.businesses.modules.store.v01.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 兑换管理Entity
 * 
 * @author niguoyong
 * @version 2015年9月21日
 */
public class StoExchange extends DataEntity<StoExchange>{

	private static final long serialVersionUID = -7681980097310587209L;
	
	/** 兑换ID **/
	private String exchengId ;
	/** 被兑换物品ID **/
	private StoGoods changeGoods = new StoGoods();
	/** 被兑换物品数量**/
	private Long changeGoodsNum;
	/** 需要兑换物品ID **/
	private String toGoodsId;
	/** 需要兑换物品数量 **/
	private Long toGoodsNum;
	/** 被兑换物品价值 **/
	private BigDecimal amount = new BigDecimal(0);
	/** 兑换物品价值 **/
	private BigDecimal changeAmount= new BigDecimal(0);
	/** 所属金库 **/
	private Office office;
	/** 库存数量 **/
	private Long stoNum;
	
	/** 目标物品详细条件 */
	private StoGoodSelect stoGoodSelect = new StoGoodSelect();
	
	/** 兑换物品详细条件 */
	private StoGoodSelect stoGoodSelectFrom = new StoGoodSelect();
	
	/** 目标物品列表 */
	private List<StoExchangeGood> stoExchangeGoodList = new ArrayList<StoExchangeGood>();
	/** 目标物品保存用 */
	private StoExchangeGood stoExchangeGoodSave = new StoExchangeGood();
	
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;
	private String searchTimeStart;
	private String searchTimeEnd;
	private String createUser;
	
	/** Ajax取物品价值 和取库存数量失败**/
	private String msg;

	/**
	 * @return exchengId
	 */
	public String getId() {
		return exchengId;
	}

	/**
	 * @param exchengId 要设置的 exchengId
	 */
	public void setId(String exchengId) {
		this.exchengId = exchengId;
	}

	/**
	 * @return changeGoodsId
	 */
	public StoGoods getChangeGoods() {
		return changeGoods;
	}

	/**
	 * @param changeGoodsId 要设置的 changeGoodsId
	 */
	public void setChangeGoods(StoGoods changeGoods) {
		this.changeGoods = changeGoods;
	}

	/**
	 * @return changeGoodsNum
	 */
	public Long getChangeGoodsNum() {
		return changeGoodsNum;
	}

	/**
	 * @param changeGoodsNum 要设置的 changeGoodsNum
	 */
	public void setChangeGoodsNum(Long changeGoodsNum) {
		this.changeGoodsNum = changeGoodsNum;
	}

	/**
	 * @return toGoodsid
	 */
	public String getToGoodsId() {
		return toGoodsId;
	}

	/**
	 * @param toGoodsid 要设置的 toGoodsid
	 */
	public void setToGoodsId(String toGoods) {
		this.toGoodsId = toGoods;
	}

	/**
	 * @return toGoodsNum
	 */
	public Long getToGoodsNum() {
		return toGoodsNum;
	}

	/**
	 * @param toGoodsNum 要设置的 toGoodsNum
	 */
	public void setToGoodsNum(Long toGoodsNum) {
		this.toGoodsNum = toGoodsNum;
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
	 * @return office
	 */
	public Office getOffice() {
		return office;
	}

	/**
	 * @param office 要设置的 office
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * @return stoGoodSelect
	 */
	public StoGoodSelect getStoGoodSelect() {
		return stoGoodSelect;
	}

	/**
	 * @param stoGoodSelect 要设置的 stoGoodSelect
	 */
	public void setStoGoodSelect(StoGoodSelect stoGoodSelect) {
		this.stoGoodSelect = stoGoodSelect;
	}

	/**
	 * @return stoGoodSelectFrom
	 */
	public StoGoodSelect getStoGoodSelectFrom() {
		return stoGoodSelectFrom;
	}

	/**
	 * @param stoGoodSelectFrom 要设置的 stoGoodSelectFrom
	 */
	public void setStoGoodSelectFrom(StoGoodSelect stoGoodSelectFrom) {
		this.stoGoodSelectFrom = stoGoodSelectFrom;
	}

	/**
	 * @return createTimeStart
	 */
	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	/**
	 * @param createTimeStart 要设置的 createTimeStart
	 */
	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	/**
	 * @return createTimeEnd
	 */
	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	/**
	 * @param createTimeEnd 要设置的 createTimeEnd
	 */
	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	/**
	 * @return createUser
	 */
	public String getCreateUser() {
		return createUser;
	}

	/**
	 * @param createUser 要设置的 createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
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
	 * @return msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg 要设置的 msg
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return stoExchangeGoodList
	 */
	public List<StoExchangeGood> getStoExchangeGoodList() {
		return stoExchangeGoodList;
	}

	/**
	 * @param stoExchangeGoodList 要设置的 stoExchangeGoodList
	 */
	public void setStoExchangeGoodList(List<StoExchangeGood> stoExchangeGoodList) {
		this.stoExchangeGoodList = stoExchangeGoodList;
	}

	/**
	 * @return changeAmount
	 */
	public BigDecimal getChangeAmount() {
		return changeAmount;
	}

	/**
	 * @param changeAmount 要设置的 changeAmount
	 */
	public void setChangeAmount(BigDecimal changeAmount) {
		this.changeAmount = changeAmount;
	}

	/**
	 * @return the stoExchangeGoodSave
	 */
	public StoExchangeGood getStoExchangeGoodSave() {
		return stoExchangeGoodSave;
	}

	/**
	 * @param stoExchangeGoodSave the stoExchangeGoodSave to set
	 */
	public void setStoExchangeGoodSave(StoExchangeGood stoExchangeGoodSave) {
		this.stoExchangeGoodSave = stoExchangeGoodSave;
	}

	/**
	 * @return searchTimeStart
	 */
	public String getSearchTimeStart() {
		return searchTimeStart;
	}

	/**
	 * @param searchTimeStart 要设置的 searchTimeStart
	 */
	public void setSearchTimeStart(String searchTimeStart) {
		this.searchTimeStart = searchTimeStart;
	}

	/**
	 * @return searchTimeEnd
	 */
	public String getSearchTimeEnd() {
		return searchTimeEnd;
	}

	/**
	 * @param searchTimeEnd 要设置的 searchTimeEnd
	 */
	public void setSearchTimeEnd(String searchTimeEnd) {
		this.searchTimeEnd = searchTimeEnd;
	}
}
