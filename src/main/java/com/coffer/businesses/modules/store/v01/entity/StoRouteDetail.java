package com.coffer.businesses.modules.store.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 线路绑定网点Entity
 * 
 * @author niguoyong
 * @version 2015-09-02
 */
public class StoRouteDetail extends DataEntity<StoRouteDetail>{

	private static final long serialVersionUID = 1L;
	private String detailId; // 线路绑定明细ID
	private StoRouteInfo route; // 线路ID
	private Office office; // 网点
	private Integer tailBalance; //尾箱结余

	public StoRouteDetail() {
		super();
	}

	public StoRouteDetail(String detailId) {
		super(detailId);
	}

	/**
	 * @return detailId
	 */
	@Override
	public String getId() {
		return detailId;
	}

	/**
	 * @param detailId 要设置的 detailId
	 */
	@Override
	public void setId(String id) {
		this.detailId = id;
	}

	/**
	 * @return route
	 */
	public StoRouteInfo getRoute() {
		return route;
	}

	/**
	 * @param route 要设置的 route
	 */
	public void setRoute(StoRouteInfo route) {
		this.route = route;
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
	 * @return tailBalance
	 */
	public Integer getTailBalance() {
		return tailBalance;
	}

	/**
	 * @param tailBalance 要设置的 tailBalance
	 */
	public void setTailBalance(Integer tailBalance) {
		this.tailBalance = tailBalance;
	}

	
}
