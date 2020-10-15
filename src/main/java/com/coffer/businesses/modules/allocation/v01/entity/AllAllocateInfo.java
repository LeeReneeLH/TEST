package com.coffer.businesses.modules.allocation.v01.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 调缴功能Entity
 * 
 * @author Chengshu
 * @version 2015-05-11
 */
public class AllAllocateInfo extends DataEntity<AllAllocateInfo> {

	private static final long serialVersionUID = 1L;

	/** ***************数据库相关*************** **/
	/** 流水号 **/
	private String allId = "";
	/** 业务类型（） **/
	private String businessType;
	/** 路线ID **/
	private String routeId;
	/** 路线名称 **/
	private String routeName;
	/** 登记机构 */
	private Office rOffice;
	/** 接收机构 */
	private Office aOffice;
	/** 登记个数 **/
	private Integer registerNumber;
	/** 接收个数 **/
	private Integer acceptNumber;
	/** 登记金额 **/
	private BigDecimal registerAmount;
	/** 接收(确认)金额 **/
	private BigDecimal confirmAmount;
	/** 状态 **/
	private String status;
	/** 库房交接ID **/
	private String storeHandoverId;
	/** 网点交接ID **/
	private String pointHandoverId;
	/** 确认人 **/
	private String confirmName;
	/** 确认时间 **/
	private Date confirmDate;
	/** 扫描时间 **/
	private Date scanDate;

	/** ***************业务相关*************** **/
	/** 路线信息 **/
	private StoRouteInfo routeInfo;

	/** 页面类型 (页面跳转条件) **/
	private String pageType;

	/** 页面编辑条件 **/
	private String saveType;

	/** 查询标识 **/
	private String searchFlag;

	/** 任务类型 **/
	private String taskType;

	/** 登记、接受的始末时间（查询用） **/
	private Date acceptTimeStart;
	private Date acceptTimeEnd;
	private Date registerTimeStart;
	private Date registerTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	/** 查询条件(全部查询/只查调拨信息) **/
	private String serachCondition;

	/** 排序 **/
	private String orderBy;
	/** 更新时间 格式 ：yyyyMMddHHmmssSSSSSS */
	private String strUpdateDate;
	/** 现金库入库接收人 **/
	private User clearInReceiveBy;
	/** 现金库入库接收人姓名 **/
	private String clearInReceiveName;
	/** 现金库入库接收日期 **/
	private Date clearInReceiveDate;
	/** 清分登记人 **/
	private User clearInRegisterBy;
	/** 清分登记人姓名 **/
	private String clearInRegisterName;
	/** 清分登记日期 **/
	private Date clearInRegisterDate;
	/** 修改前物品数量map **/
	private Map<String, Long> moneyBeforeMap = Maps.newHashMap();
	/** 业务类型列表 **/
	private List<String> businessTypes;
	/** 状态列表 **/
	private List<String> statuses;
	/** 接收机构列表 **/
	private List<String> aOfficeList;
	/** 登记机构列表 **/
	private List<String> rOfficeList;

	/** 出库金额合计 **/
	private BigDecimal outAmountCount;
	/** 出库金额合计 Key=币种 */
	private Map<String, AllAllocateItem> outAmountCountItemMap = Maps.newTreeMap();
	/** 入库金额合计 **/
	private BigDecimal inAmountCount;
	/** 入库金额合计 Key=币种 **/
	private Map<String, AllAllocateItem> inAmountCountItemMap = Maps.newTreeMap();
	/** 现金登记金额 **/
	private BigDecimal cashAmount;

	/** 当前登陆用户信息 */
	private User loginUser;
	/** 类别 */
	private String classification;
	/** 币种 */
	private String currency;

	/** 扫描标识 */
	private String scanFlag;
	/** 扫描标识 */
	private List<String> scanFlagList;

	/** 箱袋列表 **/
	private List<AllAllocateDetail> boxList;
	/** 押运人员1信息列表 **/
	private StoEscort escortNoOneList;
	/** 押运人员2信息列表 **/
	private StoEscort escortNoTwoList;

	/** 交接表信息 **/
	private AllHandoverInfo allHandoverInfo = new AllHandoverInfo();
	/** 库房交接表信息 **/
	private AllHandoverInfo storeHandover = new AllHandoverInfo();
	/** 网点交接表信息 **/
	private AllHandoverInfo pointHandover = new AllHandoverInfo();
	/** 调拨物品详细列表 (Key:套别-券别-单位) */
	private Map<String, AllAllocateItem> allAllocateItemMap = Maps.newTreeMap();
	/** 流水号列表 **/
	private List<String> allIds;
	/** 调拨物品详细 **/
	private AllAllocateItem allAllocateItem = new AllAllocateItem();
	/** 调缴详细列表 **/
	private List<AllAllocateDetail> allDetailList = Lists.newArrayList();
	/** 调缴详细 */
	private AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
	/** 调拨物品详细列表 */
	private List<AllAllocateItem> allAllocateItemList = Lists.newArrayList();
	/** 交接列表 **/
	private List<AllHandoverInfo> handoverList = Lists.newArrayList();

	/** 调拨物品详细条件 */
	private StoGoodSelect stoGoodSelect = new StoGoodSelect();
	/** 按币种 统计物品总金额 Key=币种 */
	private Map<String, AllAllocateItem> countItemMap = Maps.newTreeMap();

	/** 配款总金额 按币种 统计物品总金额 Key=币种 */
	private Map<String, AllAllocateItem> countQuotaItemMap = Maps.newTreeMap();

	private String boxNo;
	/** 用户ID **/
	private String userId;

	/** 用户名 **/
	private String userName;

	/** 密码 **/
	private String password;

	/** 箱子个数 **/
	private int boxCount = 0;

	/** 尾箱个数 **/
	private int tailBoxCount = 0;

	/** 款箱个数 **/
	private int paragraphBoxCount = 0;

	/** 车牌号码 **/
	private String carNo;

	/** 撤回原因 **/
	private String cancelReason;
	/** 仅显示撤回原因标识 **/
	private boolean displayCancelReasonFlag = false;
	/** 撤回机构 **/
	private Office cancelOffice;
	/** 撤回用户 **/
	private User cancelUser;
	/** 撤回用户姓名（显示用，不关联USER表） **/
	private String cancelUserName;
	/** 撤回日期 **/
	private Date cancelDate;
	/** 临时线路有效标识 **/
	private String tempFlag;

	/** 未审批任务个数 **/
	private int unApproved = 0;
	/** 网点未接收任务个数 **/
	private int unAccept = 0;
	/** 失效任务个数 **/
	private int invalid = 0;
	/** 报表信息start **/
	private List<AllReportInfo> reportInfoList = Lists.newArrayList();
	private String dateFlag;
	private String strDate;
	private String goodsName;
	private Long moneyNumber;
	private Long moneyAmount;
	private String rOfficeName;
	private String aOfficeName;
	private String goodsId;
	private String inoutType;

	/** 登记机构(页面查询使用,数据穿透) */
	private Office searchRoffice;
	/** 接收机构 (页面查询使用,数据穿透) */
	private Office searchAoffice;

	/** 报表信息end **/

	public String getInoutType() {
		return inoutType;
	}

	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getrOfficeName() {
		return rOfficeName;
	}

	public void setrOfficeName(String rOfficeName) {
		this.rOfficeName = rOfficeName;
	}

	public String getaOfficeName() {
		return aOfficeName;
	}

	public void setaOfficeName(String aOfficeName) {
		this.aOfficeName = aOfficeName;
	}

	public String getDateFlag() {
		return dateFlag;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Long getMoneyNumber() {
		return moneyNumber;
	}

	public void setMoneyNumber(Long moneyNumber) {
		this.moneyNumber = moneyNumber;
	}

	public Long getMoneyAmount() {
		return moneyAmount;
	}

	public void setMoneyAmount(Long moneyAmount) {
		this.moneyAmount = moneyAmount;
	}

	public void setDateFlag(String dateFlag) {
		this.dateFlag = dateFlag;
	}

	public AllAllocateInfo() {
		super();
	}

	public List<AllReportInfo> getReportInfoList() {
		return reportInfoList;
	}

	public void setReportInfoList(List<AllReportInfo> reportInfoList) {
		this.reportInfoList = reportInfoList;
	}

	public AllAllocateInfo(String id) {
		super(id);
	}

	@JsonIgnore
	public String getAllId() {
		return allId;
	}

	public void setAllId(String allId) {
		this.allId = allId;
	}

	@JsonIgnore
	public StoRouteInfo getRouteInfo() {
		return routeInfo;
	}

	public void setRouteInfo(StoRouteInfo routeInfo) {
		this.routeInfo = routeInfo;
	}

	@JsonIgnore
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@JsonIgnore
	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	@JsonIgnore
	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	@JsonIgnore
	public Integer getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(Integer registerNumber) {
		this.registerNumber = registerNumber;
	}

	@JsonIgnore
	public Integer getAcceptNumber() {
		return acceptNumber;
	}

	public void setAcceptNumber(Integer acceptNumber) {
		this.acceptNumber = acceptNumber;
	}

	@JsonIgnore
	public BigDecimal getRegisterAmount() {
		return registerAmount;
	}

	public void setRegisterAmount(BigDecimal registerAmount) {
		this.registerAmount = registerAmount;
	}

	@JsonIgnore
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonIgnore
	public List<AllAllocateDetail> getAllDetailList() {
		return allDetailList;
	}

	public void setAllDetailList(List<AllAllocateDetail> allDetailList) {
		this.allDetailList = allDetailList;
	}

	@JsonIgnore
	public List<AllHandoverInfo> getHandoverList() {
		return handoverList;
	}

	public void setHandoverList(List<AllHandoverInfo> handoverList) {
		this.handoverList = handoverList;
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

	public String getSerachCondition() {
		return serachCondition;
	}

	public void setSerachCondition(String serachCondition) {
		this.serachCondition = serachCondition;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public List<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}

	public BigDecimal getOutAmountCount() {
		return outAmountCount;
	}

	public void setOutAmountCount(BigDecimal outAmountCount) {
		this.outAmountCount = outAmountCount;
	}

	public BigDecimal getInAmountCount() {
		return inAmountCount;
	}

	public void setInAmountCount(BigDecimal inAmountCount) {
		this.inAmountCount = inAmountCount;
	}

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public Office getaOffice() {
		return aOffice;
	}

	public void setaOffice(Office aOffice) {
		this.aOffice = aOffice;
	}

	public Map<String, AllAllocateItem> getAllAllocateItemMap() {
		return allAllocateItemMap;
	}

	public AllAllocateDetail getAllAllocateDetail() {
		return allAllocateDetail;
	}

	public void setAllAllocateDetail(AllAllocateDetail allAllocateDetail) {
		this.allAllocateDetail = allAllocateDetail;
	}

	public BigDecimal getConfirmAmount() {
		return confirmAmount;
	}

	public void setConfirmAmount(BigDecimal confirmAmount) {
		this.confirmAmount = confirmAmount;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public List<AllAllocateItem> getAllAllocateItemList() {
		return allAllocateItemList;
	}

	public void setAllAllocateItemList(List<AllAllocateItem> allAllocateItemList) {
		this.allAllocateItemList = allAllocateItemList;
	}

	public void setAllAllocateItemMap(Map<String, AllAllocateItem> allAllocateItemMap) {
		this.allAllocateItemMap = allAllocateItemMap;
	}

	public AllAllocateItem getAllAllocateItem() {
		return allAllocateItem;
	}

	public void setAllAllocateItem(AllAllocateItem allAllocateItem) {
		this.allAllocateItem = allAllocateItem;
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Map<String, AllAllocateItem> getCountItemMap() {
		return countItemMap;
	}

	public void setCountItemMap(Map<String, AllAllocateItem> countItemMap) {
		this.countItemMap = countItemMap;
	}

	public Map<String, AllAllocateItem> getCountQuotaItemMap() {
		return countQuotaItemMap;
	}

	public void setCountQuotaItemMap(Map<String, AllAllocateItem> countQuotaItemMap) {
		this.countQuotaItemMap = countQuotaItemMap;
	}

	public Map<String, AllAllocateItem> getOutAmountCountItemMap() {
		return outAmountCountItemMap;
	}

	public void setOutAmountCountItemMap(Map<String, AllAllocateItem> outAmountCountItemMap) {
		this.outAmountCountItemMap = outAmountCountItemMap;
	}

	public Map<String, AllAllocateItem> getInAmountCountItemMap() {
		return inAmountCountItemMap;
	}

	public void setInAmountCountItemMap(Map<String, AllAllocateItem> inAmountCountItemMap) {
		this.inAmountCountItemMap = inAmountCountItemMap;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStoreHandoverId() {
		return storeHandoverId;
	}

	public void setStoreHandoverId(String storeHandoverId) {
		this.storeHandoverId = storeHandoverId;
	}

	public List<String> getaOfficeList() {
		return aOfficeList;
	}

	public void setaOfficeList(List<String> aOfficeList) {
		this.aOfficeList = aOfficeList;
	}

	public String getScanFlag() {
		return scanFlag;
	}

	public void setScanFlag(String scanFlag) {
		this.scanFlag = scanFlag;
	}

	public List<AllAllocateDetail> getBoxList() {
		return boxList;
	}

	public void setBoxList(List<AllAllocateDetail> boxList) {
		this.boxList = boxList;
	}

	public List<String> getrOfficeList() {
		return rOfficeList;
	}

	public void setrOfficeList(List<String> rOfficeList) {
		this.rOfficeList = rOfficeList;
	}

	public List<String> getScanFlagList() {
		return scanFlagList;
	}

	public void setScanFlagList(List<String> scanFlagList) {
		this.scanFlagList = scanFlagList;
	}

	public int getBoxCount() {
		return boxCount;
	}

	public void setBoxCount(int boxCount) {
		this.boxCount = boxCount;
	}

	public int getTailBoxCount() {
		return tailBoxCount;
	}

	public void setTailBoxCount(int tailBoxCount) {
		this.tailBoxCount = tailBoxCount;
	}

	public int getParagraphBoxCount() {
		return paragraphBoxCount;
	}

	public void setParagraphBoxCount(int paragraphBoxCount) {
		this.paragraphBoxCount = paragraphBoxCount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getConfirmName() {
		return confirmName;
	}

	public void setConfirmName(String confirmName) {
		this.confirmName = confirmName;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}

	public Date getScanDate() {
		return scanDate;
	}

	public void setScanDate(Date scanDate) {
		this.scanDate = scanDate;
	}

	public String getPointHandoverId() {
		return pointHandoverId;
	}

	public void setPointHandoverId(String pointHandoverId) {
		this.pointHandoverId = pointHandoverId;
	}

	public AllHandoverInfo getStoreHandover() {
		return storeHandover;
	}

	public void setStoreHandover(AllHandoverInfo storeHandover) {
		this.storeHandover = storeHandover;
	}

	public AllHandoverInfo getPointHandover() {
		return pointHandover;
	}

	public void setPointHandover(AllHandoverInfo pointHandover) {
		this.pointHandover = pointHandover;
	}

	public AllHandoverInfo getAllHandoverInfo() {
		return allHandoverInfo;
	}

	public void setAllHandoverInfo(AllHandoverInfo allHandoverInfo) {
		this.allHandoverInfo = allHandoverInfo;
	}

	public String getSearchFlag() {
		return searchFlag;
	}

	public void setSearchFlag(String searchFlag) {
		this.searchFlag = searchFlag;
	}

	public StoGoodSelect getStoGoodSelect() {
		return stoGoodSelect;
	}

	public void setStoGoodSelect(StoGoodSelect stoGoodSelect) {
		this.stoGoodSelect = stoGoodSelect;
	}

	public List<String> getAllIds() {
		return allIds;
	}

	public void setAllIds(List<String> allIds) {
		this.allIds = allIds;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public Date getAcceptTimeStart() {
		return acceptTimeStart;
	}

	public void setAcceptTimeStart(Date acceptTimeStart) {
		this.acceptTimeStart = acceptTimeStart;
	}

	public Date getAcceptTimeEnd() {
		return acceptTimeEnd;
	}

	public void setAcceptTimeEnd(Date acceptTimeEnd) {
		this.acceptTimeEnd = acceptTimeEnd;
	}

	public Date getRegisterTimeStart() {
		return registerTimeStart;
	}

	public void setRegisterTimeStart(Date registerTimeStart) {
		this.registerTimeStart = registerTimeStart;
	}

	public Date getRegisterTimeEnd() {
		return registerTimeEnd;
	}

	public void setRegisterTimeEnd(Date registerTimeEnd) {
		this.registerTimeEnd = registerTimeEnd;
	}

	public StoEscort getEscortNoOneList() {
		return escortNoOneList;
	}

	public void setEscortNoOneList(StoEscort escortNoOneList) {
		this.escortNoOneList = escortNoOneList;
	}

	public StoEscort getEscortNoTwoList() {
		return escortNoTwoList;
	}

	public void setEscortNoTwoList(StoEscort escortNoTwoList) {
		this.escortNoTwoList = escortNoTwoList;
	}

	/**
	 * @return the moneyBeforeMap
	 */
	public Map<String, Long> getMoneyBeforeMap() {
		return moneyBeforeMap;
	}

	/**
	 * @param moneyBeforeMap
	 *            the moneyBeforeMap to set
	 */
	public void setMoneyBeforeMap(Map<String, Long> moneyBeforeMap) {
		this.moneyBeforeMap = moneyBeforeMap;
	}

	/**
	 * @return the clearInReceiveBy
	 */
	public User getClearInReceiveBy() {
		return clearInReceiveBy;
	}

	/**
	 * @param clearInReceiveBy
	 *            the clearInReceiveBy to set
	 */
	public void setClearInReceiveBy(User clearInReceiveBy) {
		this.clearInReceiveBy = clearInReceiveBy;
	}

	/**
	 * @return the clearInReceiveName
	 */
	public String getClearInReceiveName() {
		return clearInReceiveName;
	}

	/**
	 * @param clearInReceiveName
	 *            the clearInReceiveName to set
	 */
	public void setClearInReceiveName(String clearInReceiveName) {
		this.clearInReceiveName = clearInReceiveName;
	}

	/**
	 * @return the clearInReceiveDate
	 */
	public Date getClearInReceiveDate() {
		return clearInReceiveDate;
	}

	/**
	 * @param clearInReceiveDate
	 *            the clearInReceiveDate to set
	 */
	public void setClearInReceiveDate(Date clearInReceiveDate) {
		this.clearInReceiveDate = clearInReceiveDate;
	}

	/**
	 * @return the clearInRegisterBy
	 */
	public User getClearInRegisterBy() {
		return clearInRegisterBy;
	}

	/**
	 * @param clearInRegisterBy
	 *            the clearInRegisterBy to set
	 */
	public void setClearInRegisterBy(User clearInRegisterBy) {
		this.clearInRegisterBy = clearInRegisterBy;
	}

	/**
	 * @return the clearInRegisterName
	 */
	public String getClearInRegisterName() {
		return clearInRegisterName;
	}

	/**
	 * @param clearInRegisterName
	 *            the clearInRegisterName to set
	 */
	public void setClearInRegisterName(String clearInRegisterName) {
		this.clearInRegisterName = clearInRegisterName;
	}

	/**
	 * @return the clearInRegisterDate
	 */
	public Date getClearInRegisterDate() {
		return clearInRegisterDate;
	}

	/**
	 * @param clearInRegisterDate
	 *            the clearInRegisterDate to set
	 */
	public void setClearInRegisterDate(Date clearInRegisterDate) {
		this.clearInRegisterDate = clearInRegisterDate;
	}

	/**
	 * @return the strUpdateDate
	 */
	public String getStrUpdateDate() {

		if (StringUtils.isBlank(strUpdateDate) && this.updateDate != null) {
			this.strUpdateDate = DateUtils.formatDate(this.updateDate, Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
		}

		return strUpdateDate;
	}

	/**
	 * @param strUpdateDate
	 *            the strUpdateDate to set
	 */
	public void setStrUpdateDate(String strUpdateDate) {
		this.strUpdateDate = strUpdateDate;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public boolean isDisplayCancelReasonFlag() {
		return displayCancelReasonFlag;
	}

	public void setDisplayCancelReasonFlag(boolean displayCancelReasonFlag) {
		this.displayCancelReasonFlag = displayCancelReasonFlag;
	}

	public Office getCancelOffice() {
		return cancelOffice;
	}

	public void setCancelOffice(Office cancelOffice) {
		this.cancelOffice = cancelOffice;
	}

	public User getCancelUser() {
		return cancelUser;
	}

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}

	public String getCancelUserName() {
		return cancelUserName;
	}

	public void setCancelUserName(String cancelUserName) {
		this.cancelUserName = cancelUserName;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getTempFlag() {
		return tempFlag;
	}

	public void setTempFlag(String tempFlag) {
		this.tempFlag = tempFlag;
	}

	public int getUnApproved() {
		return unApproved;
	}

	public void setUnApproved(int unApproved) {
		this.unApproved = unApproved;
	}

	public int getUnAccept() {
		return unAccept;
	}

	public void setUnAccept(int unAccept) {
		this.unAccept = unAccept;
	}

	public int getInvalid() {
		return invalid;
	}

	public void setInvalid(int invalid) {
		this.invalid = invalid;
	}

	public Office getSearchRoffice() {
		return searchRoffice;
	}

	public void setSearchRoffice(Office searchRoffice) {
		this.searchRoffice = searchRoffice;
	}

	public Office getSearchAoffice() {
		return searchAoffice;
	}

	public void setSearchAoffice(Office searchAoffice) {
		this.searchAoffice = searchAoffice;
	}

	@Override
	public String toString() {
		return "AllAllocateInfo [allId=" + allId + ", businessType=" + businessType + ", routeName=" + routeName
				+ ", status=" + status + ", confirmAmount=" + confirmAmount + ", scanDate=" + scanDate + "]";
	}

}