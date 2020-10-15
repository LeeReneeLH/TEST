package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 商户日结导出Entity
 * 
 * @author XL
 * @version 2019年10月30日
 */
public class DayReportExport extends DataEntity<DayReportExport> {

	private static final long serialVersionUID = 1L;

	@ExcelField(title = "存款机ID", align = 2)
	private String equipmentId;// 存款机ID
	@ExcelField(title = "店员", align = 2)
	private String userName;// 店员
	@ExcelField(title = "袋号", align = 2)
	private String boxNo;// 袋号
	@ExcelField(title = "替换钞袋时间", align = 2)
	private Date exchangeTime;// 替换钞袋时间
	@ExcelField(title = "存入时间", align = 2)
	private Date depositTime;// 存入时间
	@ExcelField(title = "结束批次", align = 2)
	private String orderId;// 结束批次
	@ExcelField(title = "批次号", align = 2)
	private String tickertape;// 批次号
	@ExcelField(title = "速存数量", align = 2)
	private Integer countZhang;// 纸币数量
	@ExcelField(title = "速存金额", align = 2)
	private BigDecimal amountZhang;// 纸币金额
	/* @ExcelField(title = "硬币数量", align = 2) */
	private Integer countCoin;// 硬币数量
	/* @ExcelField(title = "硬币金额", align = 2) */
	private BigDecimal amountCoin;// 硬币金额
	@ExcelField(title = "强制金额", align = 2)
	private BigDecimal amountForce;// 强制金额
	@ExcelField(title = "差错金额", align = 2)
	private BigDecimal amountError;// 差错金额
	@ExcelField(title = "总金额", align = 2)
	private BigDecimal amount;// 总金额
	@ExcelField(title = "公司", align = 2)
	private String officeName;// 公司
	@ExcelField(title = "其他金额", align = 2)
	private BigDecimal amountOther;// 其他金额

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public Date getExchangeTime() {
		return exchangeTime;
	}

	public void setExchangeTime(Date exchangeTime) {
		this.exchangeTime = exchangeTime;
	}

	public Date getDepositTime() {
		return depositTime;
	}

	public void setDepositTime(Date depositTime) {
		this.depositTime = depositTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTickertape() {
		return tickertape;
	}

	public void setTickertape(String tickertape) {
		this.tickertape = tickertape;
	}

	public Integer getCountZhang() {
		return countZhang;
	}

	public void setCountZhang(Integer countZhang) {
		this.countZhang = countZhang;
	}

	public BigDecimal getAmountZhang() {
		return amountZhang;
	}

	public void setAmountZhang(BigDecimal amountZhang) {
		this.amountZhang = amountZhang;
	}

	public Integer getCountCoin() {
		return countCoin;
	}

	public void setCountCoin(Integer countCoin) {
		this.countCoin = countCoin;
	}

	public BigDecimal getAmountCoin() {
		return amountCoin;
	}

	public void setAmountCoin(BigDecimal amountCoin) {
		this.amountCoin = amountCoin;
	}

	public BigDecimal getAmountForce() {
		return amountForce;
	}

	public void setAmountForce(BigDecimal amountForce) {
		this.amountForce = amountForce;
	}

	public BigDecimal getAmountError() {
		return amountError;
	}

	public void setAmountError(BigDecimal amountError) {
		this.amountError = amountError;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public BigDecimal getAmountOther() {
		return amountOther;
	}

	public void setAmountOther(BigDecimal amountOther) {
		this.amountOther = amountOther;
	}
}
