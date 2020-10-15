package com.coffer.businesses.modules.weChat.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.coffer.businesses.modules.doorOrder.v01.entity.DepositInfo;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 门店预约Entity
 * 
 * @author iceman
 * @version 2017-02-13
 */
public class DoorOrderInfo extends DataEntity<DoorOrderInfo> {

	private static final long serialVersionUID = 1L;
	@ExcelField(title = "预约ID", align = 2)
	private String orderId; // 预约ID
	private String doorId; // 门店ID
	@ExcelField(title = "门店", align = 1, sort = 30)
	private String doorName; // door_name
	private String amount; // 总金额
	private String acceptBy; // 任务接收人
	private Date acceptDate; // 任务接收日期
	private String acceptName; // 任务接收人员
	@ExcelField(title = "状态", align = 1, sort = 30)
	private String status; // 状态（0：登记；2：确认；1：分配；3：清分）
	private String amountList; // 预约明细
	private String detailAmount; // 明细金额，画面显示用
	@ExcelField(title = "编号", align = 1, sort = 30)
	private String rfid; // 箱袋编号，画面显示用
	@ExcelField(title = "申请方式", align = 2)
	private String method; // 申请方式（1：PC端；2：微信端；3：PDA端）

	private String versionNo;
	private String serviceNo;
	private String doorCode; // 门店CD
	private String userId; // 当前用户ID
	private String orderDate; // 预约日期
	private String rfidList; // 箱袋编号

	private String authUserName; // 授权用户名
	private String authPassword; // 授权密码

	private String allotStatus; // 分配状态（0：未分配；1：已分配；2：已确认；3：驳回）
	// private String allotManNo; // 分配人
	// private String allotDate; // 分配日期
	// private String clearManNo; // 清分人
	// private String clearManName; // 清分人

	/** 凭条（画面显示用） 修改人：XL 日期：2019-06-26 */
	private String tickertape;
	private String tickertapeList;

	/** 清分中心 修改人：XL 日期：2019-07-04 */
	private Office office;

	/** 状态列表 修改人：XL 日期：2019-07-04 */
	private List<String> statusList = Lists.newArrayList();

	/** 金额明细 修改人：XL 日期：2019-07-24 */
	private String detailList;

	/** 明细编号 修改人：XL 日期：2019-07-26 */
	private String detailIdList;

	/** 存款人、存款日期明细 修改人：yinkai 日期： */
	private String createByList;
	private String createDateList;

	/** 机具信息 修改人：XL 日期：2019-07-29 */
	private String equipmentId;
	@ExcelField(title = "机具编号", align = 1, sort = 30)
	private String equipmentName;

	/** 时间（查询用） 修改人：XL 日期：2019-07-04 */
	private Date createTimeStart;
	private Date createTimeEnd;
	private String searchDateStart;
	private String searchDateEnd;

	/** 查询子列表分页用 添加人：lihe 日期：2019-08-27 */
	private Page<DepositInfo> orderPage;
	private String startDate;
	private String endDate;

	/** 业务类型列表 修改人：XL 日期：2019-10-09 */
	private String busTypeList;

	/** 查询子列表分页用 添加人：GJ 日期：2020-03-06 */
	private String tickertapeAmount;

	/** 缴存全景使用 添加人：lihe 日期：2020-05-29 */
	@ExcelField(title = "速存数量", align = 1, sort = 30)
	private Integer paperCount; // 纸币数量
	@ExcelField(title = "速存金额", align = 1, sort = 30)
	private BigDecimal paperAmount; // 纸币金额
	private Integer coinCount = 0; // 硬币数量
	@ExcelField(title = "硬币金额", align = 1, sort = 30)
	private BigDecimal coinAmount = new BigDecimal(0.00); // 硬币金额
	@ExcelField(title = "强存金额", align = 1, sort = 30)
	private BigDecimal forceAmount; // 强制金额
	@ExcelField(title = "其他金额", align = 1, sort = 30)
	private BigDecimal otherAmount; // 其他金额
	private String totalCount; // 总笔数
	private List<String> methodList; // 存款方式列表
	private String uninitFlag; // 非初始化标记
	
	/** HuZhiYong 日期：2020-06-03 导出用*/
	@ExcelField(title = "总金额", align = 2)
	private BigDecimal amountExcel;
	@ExcelField(title = "登记人", align = 2)
	private String createNameExcel;
	@ExcelField(title = "登记时间", align = 2, sort = 30)
	private String createDateExcel;
	@ExcelField(title = "更新人", align = 2)
	private String updateNameExcel;
	@ExcelField(title = "更新时间", align = 2)
	private String updateDateExcel;
	
	/** add by gj 2020-08-03 */
	private String bagCapacity;
	private String moneyCount;
	public String getMoneyCount() {
		return moneyCount;
	}

	public void setMoneyCount(String moneyCount) {
		this.moneyCount = moneyCount;
	}

	private List<String> eqpIds;
	public List<String> getEqpIds() {
		return eqpIds;
	}

	public void setEqpIds(List<String> eqpIds) {
		this.eqpIds = eqpIds;
	}

	
	public String getBagCapacity() {
		return bagCapacity;
	}

	public void setBagCapacity(String bagCapacity) {
		this.bagCapacity = bagCapacity;
	}

	/** add by gj 2020-08-03 */
	
	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getRfidList() {
		return rfidList;
	}

	public void setRfidList(String rfidList) {
		this.rfidList = rfidList;
	}

	public DoorOrderInfo() {
		super();
	}

	public DoorOrderInfo(String id) {
		super(id);
	}

	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getAcceptBy() {
		return acceptBy;
	}

	public void setAcceptBy(String acceptBy) {
		this.acceptBy = acceptBy;
	}

	public String getAcceptName() {
		return acceptName;
	}

	public void setAcceptName(String acceptName) {
		this.acceptName = acceptName;
	}

	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAmountList() {
		return amountList;
	}

	public void setAmountList(String amountList) {
		this.amountList = amountList;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}

	public String getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	public String getServiceNo() {
		return serviceNo;
	}

	public void setServiceNo(String serviceNo) {
		this.serviceNo = serviceNo;
	}

	public String getDoorCode() {
		return doorCode;
	}

	public void setDoorCode(String doorCode) {
		this.doorCode = doorCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAuthUserName() {
		return authUserName;
	}

	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getAllotStatus() {
		return allotStatus;
	}

	public void setAllotStatus(String allotStatus) {
		this.allotStatus = allotStatus;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getTickertape() {
		return tickertape;
	}

	public void setTickertape(String tickertape) {
		this.tickertape = tickertape;
	}

	public String getTickertapeList() {
		return tickertapeList;
	}

	public void setTickertapeList(String tickertapeList) {
		this.tickertapeList = tickertapeList;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	public String getDetailList() {
		return detailList;
	}

	public void setDetailList(String detailList) {
		this.detailList = detailList;
	}

	public String getDetailIdList() {
		return detailIdList;
	}

	public void setDetailIdList(String detailIdList) {
		this.detailIdList = detailIdList;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public Page<DepositInfo> getOrderPage() {
		return orderPage;
	}

	public void setOrderPage(Page<DepositInfo> orderPage) {
		this.orderPage = orderPage;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getBusTypeList() {
		return busTypeList;
	}

	public void setBusTypeList(String busTypeList) {
		this.busTypeList = busTypeList;
	}

	public String getCreateByList() {
		return createByList;
	}

	public void setCreateByList(String createByList) {
		this.createByList = createByList;
	}

	public String getCreateDateList() {
		return createDateList;
	}

	public void setCreateDateList(String createDateList) {
		this.createDateList = createDateList;
	}

	// 添加字段 gzd 2019-12-12 begin
	private String remarksLast;// 存款备注后两位
	private String remarksList;// 存款备注列表
	
	// 添加字段 ZXK 2020-09-22 begin
	@ExcelField(title = "业务备注", align = 2)
	private String remarks;// 业务备注（微信端使用）
	

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarksList() {
		return remarksList;
	}

	public void setRemarksList(String remarksList) {
		this.remarksList = remarksList;
	}

	public String getRemarksLast() {
		return remarksLast;
	}

	public void setRemarksLast(String remarksLast) {
		this.remarksLast = remarksLast;
	}
	// end

	// 添加字段 gzd 2020-04-16 金额范围 begin
	private String beforeAmount; // 起始金额
	private String afterAmount; // 结束金额

	public String getBeforeAmount() {
		return beforeAmount;
	}

	public void setBeforeAmount(String beforeAmount) {
		this.beforeAmount = beforeAmount;
	}

	public String getAfterAmount() {
		return afterAmount;
	}

	public void setAfterAmount(String afterAmount) {
		this.afterAmount = afterAmount;
	}
	// end

	public void preInsert() {
		// 不限制ID为UUID，调用setIsNewRecord()使用自定义ID
		if (!this.isNewRecord) {
			setId(IdGen.uuid());
		}
		User user = null;
		if (UserUtils.getUser().getId() != null) {
			user = UserUtils.getUser();
		} else {
			user = UserUtils.get(this.userId);
		}

		if (StringUtils.isNotBlank(user.getId())) {
			this.createBy = user;
			this.updateBy = user;
			this.createName = user.getName();
			this.updateName = user.getName();
		}
		this.updateDate = new Date();
		this.createDate = this.updateDate;
	}

	public String getTickertapeAmount() {
		return tickertapeAmount;
	}

	public void setTickertapeAmount(String tickertapeAmount) {
		this.tickertapeAmount = tickertapeAmount;
	}

	public Integer getPaperCount() {
		return paperCount;
	}

	public void setPaperCount(Integer paperCount) {
		this.paperCount = paperCount;
	}

	public BigDecimal getPaperAmount() {
		return paperAmount;
	}

	public void setPaperAmount(BigDecimal paperAmount) {
		this.paperAmount = paperAmount;
	}

	public Integer getCoinCount() {
		return coinCount;
	}

	public void setCoinCount(Integer coinCount) {
		this.coinCount = coinCount;
	}

	public BigDecimal getCoinAmount() {
		return coinAmount;
	}

	public void setCoinAmount(BigDecimal coinAmount) {
		this.coinAmount = coinAmount;
	}

	public BigDecimal getForceAmount() {
		return forceAmount;
	}

	public void setForceAmount(BigDecimal forceAmount) {
		this.forceAmount = forceAmount;
	}

	public BigDecimal getOtherAmount() {
		return otherAmount;
	}

	public void setOtherAmount(BigDecimal otherAmount) {
		this.otherAmount = otherAmount;
	}

	public List<String> getMethodList() {
		return methodList;
	}

	public void setMethodList(List<String> methodList) {
		this.methodList = methodList;
	}

	public String getUninitFlag() {
		return uninitFlag;
	}

	public String getCreateNameExcel() {
		return createNameExcel;
	}

	public void setCreateNameExcel(String createNameExcel) {
		this.createNameExcel = createNameExcel;
	}

	public String getCreateDateExcel() {
		return createDateExcel;
	}

	public void setCreateDateExcel(String createDateExcel) {
		this.createDateExcel = createDateExcel;
	}

	public void setUninitFlag(String uninitFlag) {
		this.uninitFlag = uninitFlag;
	}
	public BigDecimal getAmountExcel() {
		return amountExcel;
	}

	public void setAmountExcel(BigDecimal amountExcel) {
		this.amountExcel = amountExcel;
	}
	
	public String getUpdateNameExcel() {
		return updateNameExcel;
	}

	public void setUpdateNameExcel(String updateNameExcel) {
		this.updateNameExcel = updateNameExcel;
	}

	public String getUpdateDateExcel() {
		return updateDateExcel;
	}

	public void setUpdateDateExcel(String updateDateExcel) {
		this.updateDateExcel = updateDateExcel;
	}
}