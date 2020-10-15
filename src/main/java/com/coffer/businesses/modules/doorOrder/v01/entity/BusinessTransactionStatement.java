package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.core.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.coffer.core.modules.sys.entity.User;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 交易报表Entity
 * @author yinkai
 * @version 2020-01-09
 */
public class BusinessTransactionStatement extends DataEntity<BusinessTransactionStatement> {

	/** 客户确认状态：0-未确认，1-已确认，2-未发生长短款 */
	public static final String  CUST_CONFIRM_STATUS_UNCONFIRMED = "0";
	public static final String  CUST_CONFIRM_STATUS_CONFIRMED = "1";
	public static final String  CUST_CONFIRM_STATUS_NOTNEED = "2";

	private static final long serialVersionUID = 1L;
	/** 机具编号 */
	private String eqpid;
	/** 门店（仓库）*/
	private String doorId;
	/** 客户确认状态 */
	@ExcelField(title = "客户确认状态", align = 2)
	private String custConfirm;
	/** 存款批次*/
	@ExcelField(title = "存款批次", align = 2)
	private String inBatch;
	/** 存款日期*/
	//@ExcelField(title = "存款日期", align = 2)
	private Date inDate;
	/** 开始时间*/
	//@ExcelField(title = "开始时间", align = 2)
	private Date startTime;
	/** 结束时间*/
	//@ExcelField(title = "结束时间", align = 2)
	private Date endTime;
	/** 耗时*/
	@ExcelField(title = "耗时", align = 2)
	private String costTime;
	/** 自助存款金额*/
	//@ExcelField(title = "自助存款金额", align = 2)
	private String cashAmount;
	/** 强制存款金额*/
	//@ExcelField(title = "强制存款金额", align = 2)
	private String packAmount;
	/** 总金额*/
	//@ExcelField(title = "总金额", align = 2)
	private String totalAmount;
	/** 上门收款日期*/
	//@ExcelField(title = "上门收款日期", align = 2)
	private Date backDate;
	/** 清分日期*/
	//@ExcelField(title = "清分日期", align = 2)
	private Date clearDate;
	/** 实际清点金额*/
	//@ExcelField(title = "实际清点金额", align = 2)
	private String realClearAmount;
	/** 长款金额*/
	//@ExcelField(title = "长款金额", align = 2)
	private String longCurrencyMoney;
	/** 短款金额*/
	//@ExcelField(title = "短款金额", align = 2)
	private String shortCurrencyMoney;
	/** 差错处理情况*/
	@ExcelField(title = "差错情况处理", align = 2)
	private String errorCheckCondition;

	/** 存款机*/
	private EquipmentInfo equipmentInfo;
	/** 仓库*/
	private Office door;
	/** 店员*/
	private User user;
	/** 存款明细*/
	private DoorOrderDetail doorOrderDetail;

	/**
	 * 查询条件
	 */
	/** 存入日期范围*/
	private Date inStartDate;
	private Date inEndDate;
	/** 上门收款日期范围*/
	private Date backStartDate;
	private Date backEndDate;
	/** 清分日期范围*/
	private Date clearStartDate;
	private Date clearEndDate;
	/** id列表 */
	private String[] businessIds;

	/** 新增 导出用 gzd 2020-05-09 start */
	@ExcelField(title = "存款机ID", align = 2)
	private String seriesNumber;
	@ExcelField(title = "仓库", align = 2)
	private String doorName;
	@ExcelField(title = "店员", align = 2)
	private String loginName;
	@ExcelField(title = "店员姓名", align = 2)
	private String userName;	
	@ExcelField(title = "装运单号", align = 2)
	private String remarks;
	/** 自助存款金额*/
	@ExcelField(title = "自助存款金额", align = 2)
	private BigDecimal cashAmountBD;
	/** 强制存款金额*/
	@ExcelField(title = "强制存款金额", align = 2)
	private BigDecimal packAmountBD;
	/** 总金额*/
	@ExcelField(title = "总金额", align = 2)
	private BigDecimal totalAmountBD;
	/** 实际清点金额*/
	@ExcelField(title = "实际清点金额", align = 2)
	private BigDecimal realClearAmountBD;
	/** 长款金额*/
	@ExcelField(title = "长款金额", align = 2)
	private BigDecimal longCurrencyMoneyBD;
	/** 短款金额*/
	@ExcelField(title = "短款金额", align = 2)
	private BigDecimal shortCurrencyMoneyBD;
	/** 存款日期*/
	@ExcelField(title = "存款日期", align = 2)
	private String inDateEX;
	/** 开始时间*/
	@ExcelField(title = "开始时间", align = 2)
	private String startTimeEX;
	/** 结束时间*/
	@ExcelField(title = "结束时间", align = 2)
	private String endTimeEX;
	/** 上门收款日期*/
	@ExcelField(title = "上门收款日期", align = 2)
	private String backDateEX;
	/** 清分日期*/
	@ExcelField(title = "清分日期", align = 2)
	private String clearDateEX;

	public String getBackDateEX() {
		return backDateEX;
	}

	public void setBackDateEX(String backDateEX) {
		this.backDateEX = backDateEX;
	}

	public String getClearDateEX() {
		return clearDateEX;
	}

	public void setClearDateEX(String clearDateEX) {
		this.clearDateEX = clearDateEX;
	}

	public String getInDateEX() {
		return inDateEX;
	}

	public void setInDateEX(String inDateEX) {
		this.inDateEX = inDateEX;
	}

	public String getStartTimeEX() {
		return startTimeEX;
	}

	public void setStartTimeEX(String startTimeEX) {
		this.startTimeEX = startTimeEX;
	}

	public String getEndTimeEX() {
		return endTimeEX;
	}

	public void setEndTimeEX(String endTimeEX) {
		this.endTimeEX = endTimeEX;
	}

	public BigDecimal getCashAmountBD() {
		return cashAmountBD;
	}

	public void setCashAmountBD(BigDecimal cashAmountBD) {
		this.cashAmountBD = cashAmountBD;
	}

	public BigDecimal getPackAmountBD() {
		return packAmountBD;
	}

	public void setPackAmountBD(BigDecimal packAmountBD) {
		this.packAmountBD = packAmountBD;
	}

	public BigDecimal getTotalAmountBD() {
		return totalAmountBD;
	}

	public void setTotalAmountBD(BigDecimal totalAmountBD) {
		this.totalAmountBD = totalAmountBD;
	}

	public BigDecimal getRealClearAmountBD() {
		return realClearAmountBD;
	}

	public void setRealClearAmountBD(BigDecimal realClearAmountBD) {
		this.realClearAmountBD = realClearAmountBD;
	}

	public BigDecimal getLongCurrencyMoneyBD() {
		return longCurrencyMoneyBD;
	}

	public void setLongCurrencyMoneyBD(BigDecimal longCurrencyMoneyBD) {
		this.longCurrencyMoneyBD = longCurrencyMoneyBD;
	}

	public BigDecimal getShortCurrencyMoneyBD() {
		return shortCurrencyMoneyBD;
	}

	public void setShortCurrencyMoneyBD(BigDecimal shortCurrencyMoneyBD) {
		this.shortCurrencyMoneyBD = shortCurrencyMoneyBD;
	}

	public String getCustConfirm() {
		return custConfirm;
	}

	public void setCustConfirm(String custConfirm) {
		this.custConfirm = custConfirm;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	/** 新增 导出用 gzd 2020-05-09 end */
	
	public BusinessTransactionStatement() {
		super();
	}

	public BusinessTransactionStatement(String id){
		super(id);
	}

	@Length(min=0, max=64, message="机具编号长度必须介于 0 和 64 之间")
	public String getEqpid() {
		return eqpid;
	}

	public void setEqpid(String eqpid) {
		this.eqpid = eqpid;
	}

	@Length(min=0, max=64, message="门店（仓库）长度必须介于 0 和 64 之间")
	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	@Length(min=0, max=64, message="存款批次长度必须介于 0 和 64 之间")
	public String getInBatch() {
		return inBatch;
	}

	public void setInBatch(String inBatch) {
		this.inBatch = inBatch;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getInDate() {
		return inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Length(min=0, max=64, message="耗时长度必须介于 0 和 64 之间")
	public String getCostTime() {
		return costTime;
	}

	public void setCostTime(String costTime) {
		this.costTime = costTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(String cashAmount) {
		this.cashAmount = cashAmount;
	}

	public String getPackAmount() {
		return packAmount;
	}

	public void setPackAmount(String packAmount) {
		this.packAmount = packAmount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getBackDate() {
		return backDate;
	}

	public void setBackDate(Date backDate) {
		this.backDate = backDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getClearDate() {
		return clearDate;
	}

	public void setClearDate(Date clearDate) {
		this.clearDate = clearDate;
	}

	public String getRealClearAmount() {
		return realClearAmount;
	}

	public void setRealClearAmount(String realClearAmount) {
		this.realClearAmount = realClearAmount;
	}

	public String getLongCurrencyMoney() {
		return longCurrencyMoney;
	}

	public void setLongCurrencyMoney(String longCurrencyMoney) {
		this.longCurrencyMoney = longCurrencyMoney;
	}

	public String getShortCurrencyMoney() {
		return shortCurrencyMoney;
	}

	public void setShortCurrencyMoney(String shortCurrencyMoney) {
		this.shortCurrencyMoney = shortCurrencyMoney;
	}

	public String getErrorCheckCondition() {
		return errorCheckCondition;
	}

	public void setErrorCheckCondition(String errorCheckCondition) {
		this.errorCheckCondition = errorCheckCondition;
	}

	public EquipmentInfo getEquipmentInfo() {
		return equipmentInfo;
	}

	public void setEquipmentInfo(EquipmentInfo equipmentInfo) {
		this.equipmentInfo = equipmentInfo;
	}

	public Office getDoor() {
		return door;
	}

	public void setDoor(Office door) {
		this.door = door;
	}

	public DoorOrderDetail getDoorOrderDetail() {
		return doorOrderDetail;
	}

	public void setDoorOrderDetail(DoorOrderDetail doorOrderDetail) {
		this.doorOrderDetail = doorOrderDetail;
	}

	public Date getInStartDate() {
		return inStartDate;
	}

	public void setInStartDate(Date inStartDate) {
		this.inStartDate = inStartDate;
	}

	public Date getInEndDate() {
		return inEndDate;
	}

	public void setInEndDate(Date inEndDate) {
		this.inEndDate = inEndDate;
	}

	public Date getBackStartDate() {
		return backStartDate;
	}

	public void setBackStartDate(Date backStartDate) {
		this.backStartDate = backStartDate;
	}

	public Date getBackEndDate() {
		return backEndDate;
	}

	public void setBackEndDate(Date backEndDate) {
		this.backEndDate = backEndDate;
	}

	public Date getClearStartDate() {
		return clearStartDate;
	}

	public void setClearStartDate(Date clearStartDate) {
		this.clearStartDate = clearStartDate;
	}

	public Date getClearEndDate() {
		return clearEndDate;
	}

	public void setClearEndDate(Date clearEndDate) {
		this.clearEndDate = clearEndDate;
	}

	public String[] getBusinessIds() {
		return businessIds;
	}

	public void setBusinessIds(String[] businessIds) {
		this.businessIds = businessIds;
	}

}