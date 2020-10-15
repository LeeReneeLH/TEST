package com.coffer.businesses.modules.collection.v03.entity;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 清分人员选择Entity
 * @author wl
 * @version 2017-02-13
 */
public class ClearMan extends DataEntity<ClearMan> {
	
	private static final long serialVersionUID = 1L;
	private String orderId;			//预约ID	
	private String goodsId;	
	private String clearManNo;		//清分人员编号


	public ClearMan() {
		super();
	}

	public ClearMan(String id){
		super(id);
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}



	public String getClearManNo() {
		return clearManNo;
	}

	public void setClearManNo(String clearManNo) {
		this.clearManNo = clearManNo;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}


	
	
}