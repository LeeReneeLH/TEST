package com.coffer.businesses.modules.allocation.v02.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;

/**
 * 人行调拨主表管理Entity
 * 
 * @author LLF
 * @version 2016-05-25
 */
public class PbocAllAllocateInfo extends DataEntity<PbocAllAllocateInfo> {

	private static final long serialVersionUID = 1L;
	/** 流水单号 **/
	private String allId;

	/** 业务类型50：现金上缴；51：现金预约 **/
	private String businessType;
	/** 出入库类型0：出库；1：入库 **/
	private String inoutType;
	/** 加钞计划ID（备） **/
	private String addPlanId;
	/** 加钞金额（备） **/
	private Integer addAmount;
	/** 线路ID（备） **/
	private String routeId;
	/** 线路名称（备） **/
	private String routeName;
	/** 登记机构 **/
	private Office roffice;
	/** 登记机构名字 **/
	private String rofficeName;
	/** 代理上缴机构 **/
	private Office agentOffice;
	/** 代理上缴机构名称 **/
	private String agentOfficeName;
	/** 接收机构 **/
	private Office aoffice;
	/** 接收机构名字 **/
	private String aofficeName;
	/** 登记数量（备） **/
	private Integer registerNumber;
	/** 接收数量（备） **/
	private Integer acceptNumber;
	/** 申请金额 **/
	private Double registerAmount;
	/** 审批金额 **/
	private Double confirmAmount;
	/** 入库金额 **/
	private Double instoreAmount;
	/** 出库金额 **/
	private Double outstoreAmount;
	/** 用款时间/上缴时间 **/
	private Date applyDate;
	/**
	 * 状态:业务类型51：申请下拨( 20：待审批；21：驳回；22：待配款;
	 * 40：待出库；41：待交接；42：待接收)；50：申请上缴(20：待审批；21：驳回；40：待入库；41：待交接); 99：完成
	 **/
	private String status;

	/** 接收人 **/
	private User acceptBy;
	/** 接收人名称 **/
	private String acceptName;
	/** 接收时间 **/
	private Date acceptDate;
	/** 核对结果0：一致；1：不一致（备） **/
	private String checkResult;
	/** 授权人，核对结果不一致添加（备） **/
	private String authorizer;
	/** 库房交接ID（备） **/
	private String storeHandoverId;
	/** 配款人 **/
	private User quotaPersonBy;
	/** 配款人名称 **/
	private String quotaPersonName;
	/** 配款时间 **/
	private Date quotaDate;

	/** 审批人 */
	private User approvalBy;
	/** 审批人名字 */
	private String approvalName;
	/** 审批时间 */
	private Date approvalDate;

	/** 调拨物品明细 **/
	private List<PbocAllAllocateItem> pbocAllAllocateItemList = Lists.newArrayList();
	/** 交接信息 **/
	private PbocAllHandoverInfo pbocAllHandoverInfo;

	/**
	 * 调拨箱包明细
	 */
	private List<PbocAllAllocateDetail> pbocAllAllocateDetailList = Lists.newArrayList();

	/** 创建时间：开始时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 查询时间：开始时间和结束时间（查询用） **/
	private Date searchDateStart1;
	private Date searchDateEnd1;

	/** 接受时间：开始时间和结束时间（查询用） **/
	private String acceptTimeStart;
	private String acceptTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;

	/** 当前登陆用户信息 */
	private User loginUser;

	/** 用户ID **/
	private String userId;
	/** 用户名称 **/
	private String userName;

	/** 出/入库时间(过扫描门时间) **/
	private Date scanGateDate;

	/** 出入库时间(开始) **/
	private String scanGateDateStart;
	/** 出入库时间(结束) **/
	private String scanGateDateEnd;

	/** 申请金额汉字大写 **/
	private String registerAmountBig;
	/** 审批金额 存在大写 **/
	private String confirmAmountBig;
	/** 入库金额 汉字大写 **/
	private String instoreAmountBig;
	/** 出库金额 汉字大写 **/
	private String outstoreAmountBig;

	/** 错误信息 **/
	private String errorFlag;

	/** RFID列表 **/
	private List<String> rfidList;

	/** 授权人 **/
	private List<PbocAllHandoverInfo> managerList;

	/** 移交人 **/
	private List<PbocAllHandoverUserDetail> handoverList;

	/** 接收人 **/
	private List<PbocAllHandoverUserDetail> acceptList;
	/** 物品变更列表 **/
	private List<ChangeStoreEntity> changeGoodsList;

	/** 库区打印页面合并单元格数量 */
	private Integer printRowSpanNum;

	/** 页面类型 */
	private String pageType;

	/** 业务类型列表 */
	private List<String> businessTypeList;
	/** 查询任务接口使用可见数据状态 */
	private List<String> statuses;

	/** 业务状态区间(大于指定状态) */
	private String statusRight;
	/** 业务状态区间(小于指定状态) */
	private String statusLeft;

	/** 强制出库标识 */
	private String outFlag;

	/** 物品选择明细 */
	private StoGoodSelect stoGoodSelect;
	/** 物品 */
	private StoGoods stoGoods;
	/** 复点入库用过扫描门时间 **/
	private Date inStoreScanGateDate;
	/** 复点入库时间(开始) **/
	private String inStoreScanGateDateStart;
	/** 复点入库时间(结束) **/
	private String inStoreScanGateDateEnd;

	/** 更新时间 格式 ：yyyyMMddHHmmssSSSSSS */
	private String strUpdateDate;

	/** 原封新券物品列表 **/
	private List<StoOriginalBanknote> banknoteItemList;

	/** 显示驳回数据 **/
	private String showRejectData;

	/** 调拨命令号 **/
	private String commondNumber;

	/** 退库标记 **/
	private String cancellingStocksFlag;

	/** 调拨完成流水单号 **/
	private List<String> allocatefinishAllIdList = Lists.newArrayList();

	/** 调拨出库后入库标记 **/
	private String allocateInAfterOutFlag;

	/** 调拨入库对应调拨出库列表 **/
	private String allocateOutAllIds;
	
	/** 流程实例编号 */
	private String processInstanceId; 

	/**
	 * @return printRowSpanNum
	 */
	public Integer getPrintRowSpanNum() {
		return printRowSpanNum;
	}

	/**
	 * @param printRowSpanNum
	 *            要设置的 printRowSpanNum
	 */
	public void setPrintRowSpanNum(Integer printRowSpanNum) {
		this.printRowSpanNum = printRowSpanNum;
	}

	public PbocAllAllocateInfo() {
		super();
	}

	public PbocAllAllocateInfo(String id) {
		super(id);
	}

	@Length(min = 0, max = 2, message = "业务类型50：现金上缴；51：现金预约长度必须介于 0 和 2 之间")
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	@Length(min = 0, max = 1, message = "出入库类型0：出库；1：入库长度必须介于 0 和 1 之间")
	public String getInoutType() {
		return inoutType;
	}

	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	@Length(min = 0, max = 64, message = "加钞计划ID（备）长度必须介于 0 和 64 之间")
	public String getAddPlanId() {
		return addPlanId;
	}

	public void setAddPlanId(String addPlanId) {
		this.addPlanId = addPlanId;
	}

	public Integer getAddAmount() {
		return addAmount;
	}

	public void setAddAmount(Integer addAmount) {
		this.addAmount = addAmount;
	}

	@Length(min = 0, max = 64, message = "线路ID（备）长度必须介于 0 和 64 之间")
	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	@Length(min = 0, max = 200, message = "线路名称（备）长度必须介于 0 和 200 之间")
	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public Office getRoffice() {
		return roffice;
	}

	public void setRoffice(Office roffice) {
		this.roffice = roffice;
	}

	public Integer getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(Integer registerNumber) {
		this.registerNumber = registerNumber;
	}

	public Integer getAcceptNumber() {
		return acceptNumber;
	}

	public void setAcceptNumber(Integer acceptNumber) {
		this.acceptNumber = acceptNumber;
	}

	public Double getRegisterAmount() {
		return registerAmount;
	}

	public void setRegisterAmount(Double registerAmount) {
		this.registerAmount = registerAmount;
	}

	public Double getConfirmAmount() {
		return confirmAmount;
	}

	public void setConfirmAmount(Double confirmAmount) {
		this.confirmAmount = confirmAmount;
	}

	@Length(min = 0, max = 2, message = "状态30：待审批；31：待过门；32：待交接；33：驳回；34：待配款；35：在途；36：完成长度必须介于 0 和 2 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	@Length(min = 0, max = 200, message = "创建人名称长度必须介于 0 和 200 之间")
	public String getCreateName() {
		return createName;
	}

	@Override
	public void setCreateName(String createName) {
		this.createName = createName;
	}

	@Length(min = 0, max = 200, message = "审批人名称长度必须介于 0 和 200 之间")
	public String getAcceptName() {
		return acceptName;
	}

	public void setAcceptName(String acceptName) {
		this.acceptName = acceptName;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	@Override
	@Length(min = 0, max = 200, message = "更新人名称长度必须介于 0 和 200 之间")
	public String getUpdateName() {
		return updateName;
	}

	@Override
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	@Length(min = 0, max = 1, message = "核对结果0：一致；1：不一致（备）长度必须介于 0 和 1 之间")
	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	@Length(min = 0, max = 200, message = "授权人，核对结果不一致添加（备）长度必须介于 0 和 200 之间")
	public String getAuthorizer() {
		return authorizer;
	}

	public void setAuthorizer(String authorizer) {
		this.authorizer = authorizer;
	}

	@Length(min = 0, max = 64, message = "库房交接ID（备）长度必须介于 0 和 64 之间")
	public String getStoreHandoverId() {
		return storeHandoverId;
	}

	public void setStoreHandoverId(String storeHandoverId) {
		this.storeHandoverId = storeHandoverId;
	}

	@Length(min = 0, max = 200, message = "配款人名称长度必须介于 0 和 200 之间")
	public String getQuotaPersonName() {
		return quotaPersonName;
	}

	public void setQuotaPersonName(String quotaPersonName) {
		this.quotaPersonName = quotaPersonName;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getQuotaDate() {
		return quotaDate;
	}

	public void setQuotaDate(Date quotaDate) {
		this.quotaDate = quotaDate;
	}

	public Office getAoffice() {
		return aoffice;
	}

	public void setAoffice(Office aoffice) {
		this.aoffice = aoffice;
	}

	public User getAcceptBy() {
		return acceptBy;
	}

	public void setAcceptBy(User acceptBy) {
		this.acceptBy = acceptBy;
	}

	public PbocAllHandoverInfo getPbocAllHandoverInfo() {
		return pbocAllHandoverInfo;
	}

	public void setPbocAllHandoverInfo(PbocAllHandoverInfo pbocAllHandoverInfo) {
		this.pbocAllHandoverInfo = pbocAllHandoverInfo;
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

	public String getRofficeName() {
		return rofficeName;
	}

	public void setRofficeName(String rofficeName) {
		this.rofficeName = rofficeName;
	}

	public String getAofficeName() {
		return aofficeName;
	}

	public void setAofficeName(String aofficeName) {
		this.aofficeName = aofficeName;
	}

	public String getAcceptTimeStart() {
		return acceptTimeStart;
	}

	public void setAcceptTimeStart(String acceptTimeStart) {
		this.acceptTimeStart = acceptTimeStart;
	}

	public String getAcceptTimeEnd() {
		return acceptTimeEnd;
	}

	public void setAcceptTimeEnd(String acceptTimeEnd) {
		this.acceptTimeEnd = acceptTimeEnd;
	}

	public User getApprovalBy() {
		return approvalBy;
	}

	public void setApprovalBy(User approvalBy) {
		this.approvalBy = approvalBy;
	}

	public String getApprovalName() {
		return approvalName;
	}

	public void setApprovalName(String approvalName) {
		this.approvalName = approvalName;
	}

	public Date getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}

	/**
	 * @return agentOffice
	 */
	public Office getAgentOffice() {
		return agentOffice;
	}

	/**
	 * @param agentOffice
	 *            要设置的 agentOffice
	 */
	public void setAgentOffice(Office agentOffice) {
		this.agentOffice = agentOffice;
	}

	/**
	 * @return agentOfficeName
	 */
	public String getAgentOfficeName() {
		return agentOfficeName;
	}

	/**
	 * @param agentOfficeName
	 *            要设置的 agentOfficeName
	 */
	public void setAgentOfficeName(String agentOfficeName) {
		this.agentOfficeName = agentOfficeName;
	}

	/**
	 * @return applyDate
	 */
	public Date getApplyDate() {
		return applyDate;
	}

	/**
	 * @param applyDate
	 *            要设置的 applyDate
	 */
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	/**
	 * @return quotaPersonBy
	 */
	public User getQuotaPersonBy() {
		return quotaPersonBy;
	}

	/**
	 * @param quotaPersonBy
	 *            要设置的 quotaPersonBy
	 */
	public void setQuotaPersonBy(User quotaPersonBy) {
		this.quotaPersonBy = quotaPersonBy;
	}

	/**
	 * @return pbocAllAllocateItemList
	 */
	public List<PbocAllAllocateItem> getPbocAllAllocateItemList() {
		return pbocAllAllocateItemList;
	}

	/**
	 * @param pbocAllAllocateItemList
	 *            要设置的 pbocAllAllocateItemList
	 */
	public void setPbocAllAllocateItemList(List<PbocAllAllocateItem> pbocAllAllocateItemList) {
		this.pbocAllAllocateItemList = pbocAllAllocateItemList;
	}

	/**
	 * @return pbocAllAllocateDetailList
	 */
	public List<PbocAllAllocateDetail> getPbocAllAllocateDetailList() {
		return pbocAllAllocateDetailList;
	}

	/**
	 * @param pbocAllAllocateDetailList
	 *            要设置的 pbocAllAllocateDetailList
	 */
	public void setPbocAllAllocateDetailList(List<PbocAllAllocateDetail> pbocAllAllocateDetailList) {
		this.pbocAllAllocateDetailList = pbocAllAllocateDetailList;
	}

	/**
	 * @return searchDateStart
	 */
	public String getSearchDateStart() {
		return searchDateStart;
	}

	/**
	 * @param searchDateStart
	 *            要设置的 searchDateStart
	 */
	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	/**
	 * @return searchDateEnd
	 */
	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	/**
	 * @param searchDateEnd
	 *            要设置的 searchDateEnd
	 */
	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	/**
	 * @return allId
	 */
	public String getAllId() {
		return allId;
	}

	/**
	 * @param allId
	 *            要设置的 allId
	 */
	public void setAllId(String allId) {
		this.allId = allId;
	}

	/**
	 * @return loginUser
	 */
	public User getLoginUser() {
		return loginUser;
	}

	/**
	 * @param loginUser
	 *            要设置的 loginUser
	 */
	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
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

	public Date getScanGateDate() {
		return scanGateDate;
	}

	public void setScanGateDate(Date scanGateDate) {
		this.scanGateDate = scanGateDate;
	}

	public List<String> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<String> statuses) {
		this.statuses = statuses;
	}

	/**
	 * @return pageType
	 */
	public String getPageType() {
		return pageType;
	}

	/**
	 * @param pageType
	 *            要设置的 pageType
	 */
	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	/**
	 * @return registerAmountBig
	 */
	public String getRegisterAmountBig() {
		return registerAmountBig;
	}

	/**
	 * @param registerAmountBig
	 *            要设置的 registerAmountBig
	 */
	public void setRegisterAmountBig(String registerAmountBig) {
		this.registerAmountBig = registerAmountBig;
	}

	/**
	 * @return confirmAmountBig
	 */
	public String getConfirmAmountBig() {
		return confirmAmountBig;
	}

	/**
	 * @param confirmAmountBig
	 *            要设置的 confirmAmountBig
	 */
	public void setConfirmAmountBig(String confirmAmountBig) {
		this.confirmAmountBig = confirmAmountBig;
	}

	public String getErrorFlag() {
		return errorFlag;
	}

	public void setErrorFlag(String errorFlag) {
		this.errorFlag = errorFlag;
	}

	public List<String> getRfidList() {
		return rfidList;
	}

	public void setRfidList(List<String> rfidList) {
		this.rfidList = rfidList;
	}

	public List<PbocAllHandoverInfo> getManagerList() {
		return managerList;
	}

	public void setManagerList(List<PbocAllHandoverInfo> managerList) {
		this.managerList = managerList;
	}

	public List<PbocAllHandoverUserDetail> getHandoverList() {
		return handoverList;
	}

	public void setHandoverList(List<PbocAllHandoverUserDetail> handoverList) {
		this.handoverList = handoverList;
	}

	public List<PbocAllHandoverUserDetail> getAcceptList() {
		return acceptList;
	}

	public void setAcceptList(List<PbocAllHandoverUserDetail> acceptList) {
		this.acceptList = acceptList;
	}

	public String getScanGateDateStart() {
		return scanGateDateStart;
	}

	public void setScanGateDateStart(String scanGateDateStart) {
		this.scanGateDateStart = scanGateDateStart;
	}

	public String getScanGateDateEnd() {
		return scanGateDateEnd;
	}

	public void setScanGateDateEnd(String scanGateDateEnd) {
		this.scanGateDateEnd = scanGateDateEnd;
	}

	/**
	 * @return businessTypeList
	 */
	public List<String> getBusinessTypeList() {
		return businessTypeList;
	}

	/**
	 * @param businessTypeList
	 *            要设置的 businessTypeList
	 */
	public void setBusinessTypeList(List<String> businessTypeList) {
		this.businessTypeList = businessTypeList;
	}

	public List<ChangeStoreEntity> getChangeGoodsList() {
		return changeGoodsList;
	}

	public void setChangeGoodsList(List<ChangeStoreEntity> changeGoodsList) {
		this.changeGoodsList = changeGoodsList;
	}

	public Date getSearchDateStart1() {
		return searchDateStart1;
	}

	public void setSearchDateStart1(Date searchDateStart1) {
		this.searchDateStart1 = searchDateStart1;
	}

	public Date getSearchDateEnd1() {
		return searchDateEnd1;
	}

	public void setSearchDateEnd1(Date searchDateEnd1) {
		this.searchDateEnd1 = searchDateEnd1;
	}

	public String getStatusRight() {
		return statusRight;
	}

	public void setStatusRight(String statusRight) {
		this.statusRight = statusRight;
	}

	public String getStatusLeft() {
		return statusLeft;
	}

	public void setStatusLeft(String statusLeft) {
		this.statusLeft = statusLeft;
	}

	/**
	 * @return the outFlag
	 */
	public String getOutFlag() {
		return outFlag;
	}

	/**
	 * @param outFlag
	 *            the outFlag to set
	 */
	public void setOutFlag(String outFlag) {
		this.outFlag = outFlag;
	}

	/**
	 * @return the stoGoodSelect
	 */
	public StoGoodSelect getStoGoodSelect() {
		return stoGoodSelect;
	}

	/**
	 * @param stoGoodSelect
	 *            the stoGoodSelect to set
	 */
	public void setStoGoodSelect(StoGoodSelect stoGoodSelect) {
		this.stoGoodSelect = stoGoodSelect;
	}

	/**
	 * @return the stoGoods
	 */
	public StoGoods getStoGoods() {
		return stoGoods;
	}

	/**
	 * @param stoGoods
	 *            the stoGoods to set
	 */
	public void setStoGoods(StoGoods stoGoods) {
		this.stoGoods = stoGoods;
	}

	/**
	 * @return inStoreScanGateDate
	 */
	public Date getInStoreScanGateDate() {
		return inStoreScanGateDate;
	}

	/**
	 * @param inStoreScanGateDate
	 *            要设置的 inStoreScanGateDate
	 */
	public void setInStoreScanGateDate(Date inStoreScanGateDate) {
		this.inStoreScanGateDate = inStoreScanGateDate;
	}

	/**
	 * @return inStoreScanGateDateStart
	 */
	public String getInStoreScanGateDateStart() {
		return inStoreScanGateDateStart;
	}

	/**
	 * @param inStoreScanGateDateStart
	 *            要设置的 inStoreScanGateDateStart
	 */
	public void setInStoreScanGateDateStart(String inStoreScanGateDateStart) {
		this.inStoreScanGateDateStart = inStoreScanGateDateStart;
	}

	/**
	 * @return inStoreScanGateDateEnd
	 */
	public String getInStoreScanGateDateEnd() {
		return inStoreScanGateDateEnd;
	}

	/**
	 * @param inStoreScanGateDateEnd
	 *            要设置的 inStoreScanGateDateEnd
	 */
	public void setInStoreScanGateDateEnd(String inStoreScanGateDateEnd) {
		this.inStoreScanGateDateEnd = inStoreScanGateDateEnd;
	}

	/**
	 * @return banknoteItemList
	 */
	public List<StoOriginalBanknote> getBanknoteItemList() {
		return banknoteItemList;
	}

	/**
	 * @param banknoteItemList
	 *            要设置的 banknoteItemList
	 */
	public void setBanknoteItemList(List<StoOriginalBanknote> banknoteItemList) {
		this.banknoteItemList = banknoteItemList;
	}

	/**
	 * @return the instoreAmount
	 */
	public Double getInstoreAmount() {
		return instoreAmount;
	}

	/**
	 * @param instoreAmount
	 *            the instoreAmount to set
	 */
	public void setInstoreAmount(Double instoreAmount) {
		this.instoreAmount = instoreAmount;
	}

	/**
	 * @return the instoreAmountBig
	 */
	public String getInstoreAmountBig() {
		return instoreAmountBig;
	}

	/**
	 * @param instoreAmountBig
	 *            the instoreAmountBig to set
	 */
	public void setInstoreAmountBig(String instoreAmountBig) {
		this.instoreAmountBig = instoreAmountBig;
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

	/**
	 * @return the showRejectData
	 */
	public String getShowRejectData() {
		return showRejectData;
	}

	/**
	 * @param showRejectData
	 *            the showRejectData to set
	 */
	public void setShowRejectData(String showRejectData) {
		this.showRejectData = showRejectData;
	}

	/**
	 * @return the commondNumber
	 */
	public String getCommondNumber() {
		return commondNumber;
	}

	/**
	 * @param commondNumber
	 *            the commondNumber to set
	 */
	public void setCommondNumber(String commondNumber) {
		this.commondNumber = commondNumber;
	}

	/**
	 * @return cancellingStocksFlag
	 */
	public String getCancellingStocksFlag() {
		return cancellingStocksFlag;
	}

	/**
	 * @param cancellingStocksFlag
	 *            要设置的 cancellingStocksFlag
	 */
	public void setCancellingStocksFlag(String cancellingStocksFlag) {
		this.cancellingStocksFlag = cancellingStocksFlag;
	}

	/**
	 * @return allocatefinishAllIdList
	 */
	public List<String> getAllocatefinishAllIdList() {
		return allocatefinishAllIdList;
	}

	/**
	 * @param allocatefinishAllIdList
	 *            要设置的 allocatefinishAllIdList
	 */
	public void setAllocatefinishAllIdList(List<String> allocatefinishAllIdList) {
		this.allocatefinishAllIdList = allocatefinishAllIdList;
	}

	/**
	 * @return allocateInAfterOutFlag
	 */
	public String getAllocateInAfterOutFlag() {
		return allocateInAfterOutFlag;
	}

	/**
	 * @param allocateInAfterOutFlag
	 *            要设置的 allocateInAfterOutFlag
	 */
	public void setAllocateInAfterOutFlag(String allocateInAfterOutFlag) {
		this.allocateInAfterOutFlag = allocateInAfterOutFlag;
	}

	/**
	 * @return allocateOutAllIds
	 */
	public String getAllocateOutAllIds() {
		return allocateOutAllIds;
	}

	/**
	 * @param allocateOutAllIds
	 *            要设置的 allocateOutAllIds
	 */
	public void setAllocateOutAllIds(String allocateOutAllIds) {
		this.allocateOutAllIds = allocateOutAllIds;
	}

	/**
	 * @return outstoreAmount
	 */
	public Double getOutstoreAmount() {
		return outstoreAmount;
	}

	/**
	 * @param outstoreAmount
	 *            要设置的 outstoreAmount
	 */
	public void setOutstoreAmount(Double outstoreAmount) {
		this.outstoreAmount = outstoreAmount;
	}

	/**
	 * @return outstoreAmountBig
	 */
	public String getOutstoreAmountBig() {
		return outstoreAmountBig;
	}

	/**
	 * @param outstoreAmountBig
	 *            要设置的 outstoreAmountBig
	 */
	public void setOutstoreAmountBig(String outstoreAmountBig) {
		this.outstoreAmountBig = outstoreAmountBig;
	}

	@Override
	public String toString() {
		return "PbocAllAllocateInfo [allId=" + allId + ", businessType=" + businessType + ", routeName=" + routeName
				+ ", status=" + status + ", registerAmount=" + registerAmount + ", confirmAmount=" + confirmAmount
				+ "]";
	}

	/**
	 * @return the processInstanceId
	 */
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	/**
	 * @param processInstanceId the processInstanceId to set
	 */
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}