package com.coffer.businesses.modules.doorOrder.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.User;

/**
 * 账务管理Entity
 * 
 * @author QPH
 * @version 2017-09-04
 */
public class DoorCenterAccountsMain extends DataEntity<DoorCenterAccountsMain> {

	private static final long serialVersionUID = 1L;
	private String accountsId; // 账务表主键
	private String clientId; // 客户编号
	@ExcelField(title = "客户名称", align = 2)
	private String clientName; // 客户名称
	private String businessType; // 业务类型
	private String businessId; // 业务对应流水ID
	private String rofficeId; // 账务发生机构（清分中心）
	// @ExcelField(title = "机构名称", align = 2)
	private String rofficeName; // 账务发生机构名称
	private String aofficeId; // 账务归属机构（商业银行）
	private String aofficeName; // 账务归属机构名称
	@ExcelField(title = "收入", align = 2)
	private BigDecimal inAmount; // 入库总金额（借）
	@ExcelField(title = "付出", align = 2)
	private BigDecimal outAmount; // 出库总金额（贷）
	// @ExcelField(title = "余额", align = 2)
	private BigDecimal totalAmount; // 中心剩余总金额
	@ExcelField(title = "客户余额", align = 2)
	private BigDecimal guestTotalAmount; // 中心剩余总金额
	private String inoutType; // 出入库类型：0：出库 1：入库
	private String businessStatus; // 业务状态 1：登记 2：冲正
	@ExcelField(title = "交易日期", align = 2)
	private String createDateReport;

	@ExcelField(title = "存款机ID", align = 2)
	private String equipmentId;
	@ExcelField(title = "区域", align = 2)
	private String area;
	@ExcelField(title = "存款批次", align = 2)
	private String saveMoneyBatch;
	@ExcelField(title = "存款日期", align = 2)
	private String saveMoneyDate;
	@ExcelField(title = "开始时间", align = 2)
	private String saveStartDate;
	@ExcelField(title = "结束时间", align = 2)
	private String saveEndDate;
	@ExcelField(title = "耗时", align = 2)
	private String elapsedTime;
	@ExcelField(title = "店员", align = 2)
	private String people;
	@ExcelField(title = "店员姓名", align = 2)
	private String peopleName;
	@ExcelField(title = "装运单号", align = 2)
	private String shipmentNumber;
	@ExcelField(title = "存款业务类型", align = 2)
	private String saveType;
	@ExcelField(title = "币种", align = 2)
	private String currency;
	@ExcelField(title = "总金额", align = 2)
	private BigDecimal totalMoney;
	@ExcelField(title = "确认转账日期", align = 2)
	private String confirmTransferAccountsDate;
	@ExcelField(title = "确认转账经办人", align = 2)
	private String confirmTransferAccountsPeople;
	@ExcelField(title = "上门收款日期", align = 2)
	private String doorOrderDate;
	@ExcelField(title = "实际清点金额", align = 2)
	private BigDecimal realClearAmount;
	@ExcelField(title = "长款金额", align = 2)
	private BigDecimal longCurrencyMoney;
	@ExcelField(title = "短款金额", align = 2)
	private BigDecimal shortCurrencyMoney;
	@ExcelField(title = "差错处理情况", align = 2)
	private String errorCheckCondition;
	@ExcelField(title = "操作类型", align = 2) // 修改人：gzd 2019.11.27
	private String businessStatusReport; // 业务状态 1：登记 2：冲正
	@ExcelField(title = "业务类型", align = 2)
	private String businessTypeReport; // 业务类型
	private String detailId; // 每笔存款记录ID

	private String paperMoneyCount; // 纸币数量
	private String coinMoneyCount; // 硬币数量
	private BigDecimal paperMoneyTotal; // 纸币金额
	private BigDecimal coinMoneyTotal;// 硬币金额
	private String saveTotal; // 存款总量

	private String reportDate; // 日结时间

	private List<String> paidStatusList; // 付款状态列表

	private String sevenCode; // 门店对应7位码

	private String settlementType; // 商户结算类型（74-存款汇总，79-存款差错）

	/** 代付状态 添加人：lihe 日期：2020-01-10 **/
	private String paidStatus;
	private String uninitDateFlag; // 非初始化时间标记
	private BigDecimal errorDealAmount;// 差错处理金额
	private BigDecimal traditionalAmount;// 传统存款金额
	private BigDecimal electricAmount;// 电子线下金额

	/** 拆箱编号 添加人：wpy 日期：2020-03-9 **/
	private String codeNo;

	/** 包号 添加人：wpy 日期：2020-03-9 **/
	private String packNum;

	/** door_order_info ID 添加人：wpy 日期：2020-03-10 **/
	private String infoId;

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public String getCodeNo() {
		return codeNo;
	}

	public void setCodeNo(String codeNo) {
		this.codeNo = codeNo;
	}

	public String getPackNum() {
		return packNum;
	}

	public void setPackNum(String packNum) {
		this.packNum = packNum;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getSevenCode() {
		return sevenCode;
	}

	public void setSevenCode(String sevenCode) {
		this.sevenCode = sevenCode;
	}

	public List<String> getPaidStatusList() {
		return paidStatusList;
	}

	public void setPaidStatusList(List<String> paidStatusList) {
		this.paidStatusList = paidStatusList;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public BigDecimal getPaperMoneyTotal() {
		return paperMoneyTotal;
	}

	public void setPaperMoneyTotal(BigDecimal paperMoneyTotal) {
		this.paperMoneyTotal = paperMoneyTotal;
	}

	public String getSaveTotal() {
		return saveTotal;
	}

	public void setSaveTotal(String saveTotal) {
		this.saveTotal = saveTotal;
	}

	public String getDetailId() {
		return detailId;
	}

	public String getPaperMoneyCount() {
		return paperMoneyCount;
	}

	public void setPaperMoneyCount(String paperMoneyCount) {
		this.paperMoneyCount = paperMoneyCount;
	}

	public String getCoinMoneyCount() {
		return coinMoneyCount;
	}

	public void setCoinMoneyCount(String coinMoneyCount) {
		this.coinMoneyCount = coinMoneyCount;
	}

	public BigDecimal getCoinMoneyTotal() {
		return coinMoneyTotal;
	}

	public void setCoinMoneyTotal(BigDecimal coinMoneyTotal) {
		this.coinMoneyTotal = coinMoneyTotal;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSaveMoneyBatch() {
		return saveMoneyBatch;
	}

	public void setSaveMoneyBatch(String saveMoneyBatch) {
		this.saveMoneyBatch = saveMoneyBatch;
	}

	public String getSaveMoneyDate() {
		return saveMoneyDate;
	}

	public void setSaveMoneyDate(String saveMoneyDate) {
		this.saveMoneyDate = saveMoneyDate;
	}

	public String getSaveStartDate() {
		return saveStartDate;
	}

	public void setSaveStartDate(String saveStartDate) {
		this.saveStartDate = saveStartDate;
	}

	public String getSaveEndDate() {
		return saveEndDate;
	}

	public void setSaveEndDate(String saveEndDate) {
		this.saveEndDate = saveEndDate;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getPeople() {
		return people;
	}

	public void setPeople(String people) {
		this.people = people;
	}

	public String getPeopleName() {
		return peopleName;
	}

	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}

	public String getShipmentNumber() {
		return shipmentNumber;
	}

	public void setShipmentNumber(String shipmentNumber) {
		this.shipmentNumber = shipmentNumber;
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getConfirmTransferAccountsDate() {
		return confirmTransferAccountsDate;
	}

	public void setConfirmTransferAccountsDate(String confirmTransferAccountsDate) {
		this.confirmTransferAccountsDate = confirmTransferAccountsDate;
	}

	public String getConfirmTransferAccountsPeople() {
		return confirmTransferAccountsPeople;
	}

	public void setConfirmTransferAccountsPeople(String confirmTransferAccountsPeople) {
		this.confirmTransferAccountsPeople = confirmTransferAccountsPeople;
	}

	public String getDoorOrderDate() {
		return doorOrderDate;
	}

	public void setDoorOrderDate(String doorOrderDate) {
		this.doorOrderDate = doorOrderDate;
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public BigDecimal getRealClearAmount() {
		return realClearAmount;
	}

	public void setRealClearAmount(BigDecimal realClearAmount) {
		this.realClearAmount = realClearAmount;
	}

	public BigDecimal getLongCurrencyMoney() {
		return longCurrencyMoney;
	}

	public void setLongCurrencyMoney(BigDecimal longCurrencyMoney) {
		this.longCurrencyMoney = longCurrencyMoney;
	}

	public BigDecimal getShortCurrencyMoney() {
		return shortCurrencyMoney;
	}

	public void setShortCurrencyMoney(BigDecimal shortCurrencyMoney) {
		this.shortCurrencyMoney = shortCurrencyMoney;
	}

	public String getErrorCheckCondition() {
		return errorCheckCondition;
	}

	public void setErrorCheckCondition(String errorCheckCondition) {
		this.errorCheckCondition = errorCheckCondition;
	}

	/** 当前登陆用户信息 */
	private User loginUser;

	/** 清分管理明细列表 */
	private List<DoorCenterAccountsDetail> centerAccountsDetailList;

	/** 业务类型列表 */
	private List<String> businessTypes;

	/** 客户列表 */
	private List<String> clientIdList;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	/** 收入支出笔数（页面用） **/
	private int inCount; // 收入笔数
	private int outCount; // 支出笔数

	/** 凭条 修改人：XL 日期：2019-06-26 */
	private String tickertape;
	/** 状态 修改人：XL 日期：2019-06-26 */
	private String status;
	/** 父级商户机构ID 修改人：wqj 日期：2019-06-26 */
	private String merchantOfficeId;
	/** 父级商户机构Name 修改人：wqj 日期：2019-07-29 */
	private String merchantOfficeName;
	/** 差错账务记录是否已经处理过 **/
	private String isTakeUp;
	/** 日结ID **/
	private String reportId;

	/** 差错类型: 2:长款 3:短款 添加人：lihe 日期：2019-08-06 **/
	private String errorType;
	/** 差错笔数 添加人：lihe 日期：2019-08-06 **/
	private int errorCount;

	/** 接口返回参数 添加人：XL 日期：2019-08-27 **/
	private BigDecimal errorAmount;// 差错金额
	private String doorId;// 门店编号
	private String doorName;// 门店名称
	private Date startDate;// 开始时间
	private Date endDate;// 结束时间

	/** 柜员号 **/
	private String userId;
	/** 柜员名称 **/
	private String userName;
	/** 业务类型 **/
	private String bustype;
	/** 分组时间(年-月) **/
	private String groupDate;

	/** 可选择类型集合 **/
	private List<String> list;
	/** 日期节选(天) **/
	private String day;
	/** 日期节选(时:分) **/
	private String time;
	/** 首条标志 **/
	private String flag;

	/** 上传标志 0-已上传 1-未上传 **/
	private String uploadFlag;

	/** 订单编号 **/
	private String orderId;
	/** 实现分组时间对记录信息的一对多 **/
	private List<DoorCenterAccountsMain> doorCenterList;

	/** add by XL 2019-10-30 */
	private List<String> dayReportIds;// 商户日结ID列表

	/** 统计此笔存款中的现金存款、封包存款的总额 yinkai 20191101 start */
	@ExcelField(title = "速存存款", align = 2)
	private BigDecimal cash; // 现金存款
	@ExcelField(title = "强制存款", align = 2)
	private BigDecimal pack; // 封包存款
	@ExcelField(title = "其他存款", align = 2)
	private BigDecimal other; // 其他存款
	private String rfid;

	public BigDecimal getOther() {
		return other;
	}

	public void setOther(BigDecimal other) {
		this.other = other;
	}

	/** yinkai 20191101 end */

	public String getMerchantOfficeName() {
		return merchantOfficeName;
	}

	public void setMerchantOfficeName(String merchantOfficeName) {
		this.merchantOfficeName = merchantOfficeName;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getIsTakeUp() {
		return isTakeUp;
	}

	public void setIsTakeUp(String isTakeUp) {
		this.isTakeUp = isTakeUp;
	}

	public String getMerchantOfficeId() {
		return merchantOfficeId;
	}

	public void setMerchantOfficeId(String merchantOfficeId) {
		this.merchantOfficeId = merchantOfficeId;
	}

	public DoorCenterAccountsMain() {
		super();
	}

	public DoorCenterAccountsMain(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "账务表主键长度必须介于 1 和 64 之间")
	public String getAccountsId() {
		return accountsId;
	}

	public void setAccountsId(String accountsId) {
		this.accountsId = accountsId;
	}

	@Length(min = 0, max = 64, message = "客户编号长度必须介于 0 和 64 之间")
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Length(min = 0, max = 20, message = "客户名称长度必须介于 0 和 20 之间")
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	@Length(min = 0, max = 2, message = "业务类型长度必须介于 0 和 2 之间")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@Length(min = 0, max = 64, message = "业务对应流水ID长度必须介于 0 和 64 之间")
	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	@Length(min = 0, max = 64, message = "登记机构长度必须介于 0 和 64 之间")
	public String getRofficeId() {
		return rofficeId;
	}

	public void setRofficeId(String rofficeId) {
		this.rofficeId = rofficeId;
	}

	@Length(min = 0, max = 200, message = "登记机构名称长度必须介于 0 和 200 之间")
	public String getRofficeName() {
		return rofficeName;
	}

	public void setRofficeName(String rofficeName) {
		this.rofficeName = rofficeName;
	}

	@Length(min = 0, max = 64, message = "接收机构长度必须介于 0 和 64 之间")
	public String getAofficeId() {
		return aofficeId;
	}

	public void setAofficeId(String aofficeId) {
		this.aofficeId = aofficeId;
	}

	@Length(min = 0, max = 200, message = "接收机构名称长度必须介于 0 和 200 之间")
	public String getAofficeName() {
		return aofficeName;
	}

	public void setAofficeName(String aofficeName) {
		this.aofficeName = aofficeName;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
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

	public List<DoorCenterAccountsDetail> getCenterAccountsDetailList() {
		return centerAccountsDetailList;
	}

	public void setCenterAccountsDetailList(List<DoorCenterAccountsDetail> centerAccountsDetailList) {
		this.centerAccountsDetailList = centerAccountsDetailList;
	}

	public String getInoutType() {
		return inoutType;
	}

	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public BigDecimal getGuestTotalAmount() {
		return guestTotalAmount;
	}

	public void setGuestTotalAmount(BigDecimal guestTotalAmount) {
		this.guestTotalAmount = guestTotalAmount;
	}

	public List<String> getClientIdList() {
		return clientIdList;
	}

	public void setClientIdList(List<String> clientIdList) {
		this.clientIdList = clientIdList;
	}

	public String getBusinessStatus() {
		return businessStatus;
	}

	public void setBusinessStatus(String businessStatus) {
		this.businessStatus = businessStatus;
	}

	public String getCreateDateReport() {
		return createDateReport;
	}

	public void setCreateDateReport(String createDateReport) {
		this.createDateReport = createDateReport;
	}

	public String getBusinessStatusReport() {
		return businessStatusReport;
	}

	public void setBusinessStatusReport(String businessStatusReport) {
		this.businessStatusReport = businessStatusReport;
	}

	public String getBusinessTypeReport() {
		return businessTypeReport;
	}

	public void setBusinessTypeReport(String businessTypeReport) {
		this.businessTypeReport = businessTypeReport;
	}

	public int getInCount() {
		return inCount;
	}

	public void setInCount(int inCount) {
		this.inCount = inCount;
	}

	public int getOutCount() {
		return outCount;
	}

	public void setOutCount(int outCount) {
		this.outCount = outCount;
	}

	public String getTickertape() {
		return tickertape;
	}

	public void setTickertape(String tickertape) {
		this.tickertape = tickertape;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AllAllocateInfo [accountsId=" + accountsId + ", businessType=" + businessType + ", clientId=" + clientId
				+ ", totalAmount=" + totalAmount + ", inoutType=" + inoutType + ", businessId=" + businessId + "]";
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public BigDecimal getErrorAmount() {
		return errorAmount;
	}

	public void setErrorAmount(BigDecimal errorAmount) {
		this.errorAmount = errorAmount;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBustype() {
		return bustype;
	}

	public void setBustype(String bustype) {
		this.bustype = bustype;
	}

	public String getGroupDate() {
		return groupDate;
	}

	public void setGroupDate(String groupDate) {
		this.groupDate = groupDate;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getUploadFlag() {
		return uploadFlag;
	}

	public void setUploadFlag(String uploadFlag) {
		this.uploadFlag = uploadFlag;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<DoorCenterAccountsMain> getDoorCenterList() {
		return doorCenterList;
	}

	public void setDoorCenterList(List<DoorCenterAccountsMain> doorCenterList) {
		this.doorCenterList = doorCenterList;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getPack() {
		return pack;
	}

	public void setPack(BigDecimal pack) {
		this.pack = pack;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public List<String> getDayReportIds() {
		return dayReportIds;
	}

	public void setDayReportIds(List<String> dayReportIds) {
		this.dayReportIds = dayReportIds;
	}

	/* 添加人：gzd 日期：2019-11-27 begin */
	@ExcelField(title = "存款人", align = 2)
	private String cunKuanRen;
	@ExcelField(title = "存款人编号", align = 2)
	private String cunKuanRenId;

	public String getCunKuanRen() {
		return cunKuanRen;
	}

	public void setCunKuanRen(String cunKuanRen) {
		this.cunKuanRen = cunKuanRen;
	}

	public String getCunKuanRenId() {
		return cunKuanRenId;
	}

	public void setCunKuanRenId(String cunKuanRenId) {
		this.cunKuanRenId = cunKuanRenId;
	}
	/* end */

	/** 中心总账对应存款明细 */
	private DoorOrderDetail doorOrderDetail;

	public DoorOrderDetail getDoorOrderDetail() {
		return doorOrderDetail;
	}

	public void setDoorOrderDetail(DoorOrderDetail doorOrderDetail) {
		this.doorOrderDetail = doorOrderDetail;
	}

	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}

	public String getUninitDateFlag() {
		return uninitDateFlag;
	}

	public void setUninitDateFlag(String uninitDateFlag) {
		this.uninitDateFlag = uninitDateFlag;
	}

	public BigDecimal getErrorDealAmount() {
		return errorDealAmount;
	}

	public void setErrorDealAmount(BigDecimal errorDealAmount) {
		this.errorDealAmount = errorDealAmount;
	}

	public BigDecimal getTraditionalAmount() {
		return traditionalAmount;
	}

	public void setTraditionalAmount(BigDecimal traditionalAmount) {
		this.traditionalAmount = traditionalAmount;
	}

	public BigDecimal getElectricAmount() {
		return electricAmount;
	}

	public void setElectricAmount(BigDecimal electricAmount) {
		this.electricAmount = electricAmount;
	}

}