package com.coffer.businesses.modules.report.v01.entity;

import java.io.Serializable;

import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 网点现金上缴物品金额报表
 * 
 * @author xp
 * @version 2017-8-24
 */

public class AllocateReportBusinessMoneyAmount implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 流水的登记机构：网点
	private Office rOffice;
	private String rOfficeName;
	// 上缴的物品id
	private String goodsId;
	// 物品名称
	private String goodsName;
	// 物品金额
	private String moneyAmount;
	// 上缴日期
	private String handInDate;

	public AllocateReportBusinessMoneyAmount() {
		super();
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	@ExcelField(title = "物品名称", align = 2)
	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	@ExcelField(title = "总金额", align = 2)
	public String getMoneyAmount() {
		return moneyAmount;
	}

	public void setMoneyAmount(String moneyAmount) {
		this.moneyAmount = moneyAmount;
	}

	@ExcelField(title = "日期", align = 2)
	public String getHandInDate() {
		return handInDate;
	}

	public void setHandInDate(String handInDate) {
		this.handInDate = handInDate;
	}

	public String getROfficeName() {
		return rOfficeName;
	}

	public void setROfficeName(String rOfficeName) {
		this.rOfficeName = rOfficeName;
	}

}
