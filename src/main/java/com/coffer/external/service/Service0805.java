package com.coffer.external.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashAmountDao;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashMainDao;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearAddMoneyDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearGroupMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearPlanInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorOrderExceptionDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.SaveTypeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearAddMoney;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderException;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorOrderExceptionService;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderAmountDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
 * Title: Service0805
 * <p>
 * Description:机具存款信息上传接口
 * </p>
 *
 * @author yinkai
 * @date 2019.7.10
 */
@Component("Service0805")
@Scope("singleton")
public class Service0805 extends HardwardBaseService {

	@Autowired
	private EquipmentInfoDao equipmentInfoDao;

	@Autowired
	private DoorOrderInfoDao doorOrderInfoDao;

	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	@Autowired
	private DoorOrderAmountDao doorOrderAmountDao;

	@Autowired
	private ClearAddMoneyDao clearAddMoneyDao;

	@Autowired
	private ClearPlanInfoDao clearPlanInfoDao;

	@Autowired
	private ClearGroupMainDao clearGroupMainDao;

	@Autowired
	private CheckCashMainDao checkCashMainDao;

	@Autowired
	private CheckCashAmountDao checkCashAmountDao;

	@Autowired
	private SaveTypeDao saveTypeDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private StoDictDao stoDictDao;

	@Autowired
	private OfficeDao officeDao;

	@Autowired
	private DoorOrderExceptionService doorOrderExceptionService;
	
	@Autowired
	private DoorOrderExceptionDao doorOrderExceptionDao;

	public static final String FROM_EQP = "00";

	public static final String FROM_EXCEPTION = "01";

	public static final String FROM_ACCOUNT_CHECKING = "02";

	/**
	 * 存款信息上传接口，信息登记到“门店存款”中，同时登记清机加钞记录，存款流水关联到账务。
	 * 信息来源目前三处：设备端，对账，异常处理，设备端存款需验证设备在用包与上传信息中的
	 * 包是否相符，不符登记到异常中；对账和异常处理可以登记到在用和已收回状态的包中。总逻辑
	 * 共5步：1.验证接口参数，2.验证接口数据业务准确性，3.更新存款主表信息（更新人，金额，笔数等），
	 * 4.添加存款明细、面值明细、清机加钞记录，5.存款流水记账处理
	 *
	 * @param paramMap
	 *            参数
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public synchronized String execute(Map<String, Object> paramMap) {
		String checkParamMsg = checkParam(paramMap);
		if (checkParamMsg != null) {
			throw new BusinessException("E99", checkParamMsg);
		}
		String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
		String eqpId = (String) paramMap.get(Parameter.EQUIPMENT_ID_KEY);
		String bagNo = (String) paramMap.get(Parameter.BAG_NO_KEY);
		String businessType = (String) paramMap.get(Parameter.BUSINESS_TYPE_KEY);
		String tickerTape = (String) paramMap.get(Parameter.TICKER_TAPE_KEY);
		String userId = (String) paramMap.get(Parameter.USER_ID_KEY);
		String currency = (String) paramMap.get(Parameter.CURRENCY_KEY);
		String remarks = (String) paramMap.get(Parameter.REMARKS_KEY);
		String startTime = (String) paramMap.get(Parameter.START_TIME_KEY);
		String endTime = (String) paramMap.get(Parameter.END_TIME_KEY);
		String cost = (String) paramMap.get(Parameter.COST_TIME_KEY);
		List<Map<String, Object>> detail = (List<Map<String, Object>>) paramMap.get(Parameter.DETAIL_KEY);
		// 系统内部参数，判断存款信息来源，目前有设备、异常补入、对账
		String orderFrom = (String) paramMap.get(Parameter.ORDER_FROM);
		// 获取设备操作用户信息
		User user = userDao.get(userId);
		if (user == null) {
			throw new BusinessException("E99", "用户信息有误");
		}
		// 验证清机员
		if (!DoorOrderConstant.SysUserType.SHOP_TELLER.equals(user.getUserType())) {
			throw new BusinessException("E99", "该用户不是店员");
		}
		// 设备信息
		EquipmentInfo equipmentInfo = equipmentInfoDao.get(eqpId);
		if (equipmentInfo == null) {
			throw new BusinessException("E99", "无设备信息");
		}
		// 正常在线设备可以上传存款
		if (!DoorOrderConstant.ConnStatus.NORMAL.equals(equipmentInfo.getConnStatus())) {
			throw new BusinessException("E99", equipmentInfo.getName() + "不可用");
		}
		// 设备维护机构
		Office vinOffice = equipmentInfo.getVinOffice();
		// 设备归属门店
		Office aOffice = equipmentInfo.getaOffice();
		if (vinOffice == null || aOffice == null || StringUtils.isEmpty(vinOffice.getId())
				|| StringUtils.isEmpty(aOffice.getId())) {
			throw new BusinessException("E99", equipmentInfo.getName() + "未绑定机构");
		}
		// 查找设备归属门店的在登记预约
		DoorOrderInfo doorOrderInfoCondition = new DoorOrderInfo();
		doorOrderInfoCondition.setDoorId(equipmentInfo.getaOffice().getId());
		doorOrderInfoCondition.setEquipmentId(equipmentInfo.getId());
		doorOrderInfoCondition.setStatus(DoorOrderConstant.Status.REGISTER);
		List<DoorOrderInfo> doorOrderInfoList = doorOrderInfoDao.findList(doorOrderInfoCondition);
		// 一个门店在登记记录只能有一条
		if (doorOrderInfoList.size() > 1) {
			throw new BusinessException("E99", "在登记数据过多");
		}
		// 验证是否有其他设备的重复包号
		List<String> repeatBagNo = doorOrderInfoDao.getRepeatBagNo(bagNo, equipmentInfo.getId());
		if (!Collections3.isEmpty(repeatBagNo)) {
			throw new BusinessException("E99", "其他设备有重复包号");
		}
		// 存款发生真实时间
		Date realDate;
		try {
			realDate = DateUtils.parseDate(tickerTape, "yyyyMMddHHmmssSSS");
		} catch (ParseException e) {
			realDate = new Date();
		}
		// 系统当前时间
		Date currentDate = new Date();
		// 初始化存款明细
		DoorOrderDetail doorOrderDetailNew = setDoorOrderDetailNew(bagNo, businessType, tickerTape, remarks, startTime,
				endTime, cost, user, realDate);
		// [door_order_amount]插入面值明细，计算总数量和总金额（存款总额，不包含其他存款金额），同时给doorOrderDetailNew赋值
		insertOrderAmount(doorOrderDetailNew, detail, currency);
		// [door_order_info]更新存款主表信息
		DoorOrderInfo doorOrderInfo = updateDoorOrderInfo(bagNo, doorOrderDetailNew, orderFrom, user, equipmentInfo,
				doorOrderInfoList, realDate, currentDate);
		// [door_order_detail]
		insertDoorDetail(doorOrderDetailNew, doorOrderInfo);
		// [clear_add_money]添加清机加钞记录
		insertClearAddMoney(user, equipmentInfo, currentDate, doorOrderDetailNew);
		// [center_accounts_door_main]将流水关联到账务，张家港需要对备注做特殊验证
		insertAccount(tickerTape, remarks, user, equipmentInfo, aOffice, doorOrderInfo,
				new BigDecimal(doorOrderDetailNew.getAmount()));
		// 若这笔存款之前已经登记过异常，将已登记的存款异常状态更改为已处理
		changeExceptionStatus(tickerTape);
		Map<String, Object> map = new HashMap<>(16);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		map.put(Parameter.ERROR_NO_KEY, null);
		map.put(Parameter.ERROR_MSG_KEY, null);
		return setReturnMap(map, serviceNo);
	}

	private DoorOrderDetail setDoorOrderDetailNew(String bagNo, String businessType, String tickerTape, String remarks,
			String startTime, String endTime, String cost, User user, Date realDate) {
		DoorOrderDetail doorOrderDetailNew = new DoorOrderDetail();
		doorOrderDetailNew.setId(IdGen.uuid());
		doorOrderDetailNew.setRfid(bagNo);
		doorOrderDetailNew.setOrderDate(DateUtils.formatDate(realDate));
		doorOrderDetailNew.setTickertape(tickerTape);
		doorOrderDetailNew.setStartTime(DateUtils.parseTimestampToDate(startTime));
		doorOrderDetailNew.setEndTime(DateUtils.parseTimestampToDate(endTime));
		doorOrderDetailNew.setCostTime(cost);
		doorOrderDetailNew.setRemarks(remarks);
		doorOrderDetailNew.setCreateDate(realDate);
		doorOrderDetailNew.setCreateBy(user);
		doorOrderDetailNew.setCreateName(user.getName());
		doorOrderDetailNew.setBusinessType(businessType);
		return doorOrderDetailNew;
	}

	/**
	 * 存款流水关联到账务
	 *
	 * @param tickerTape
	 *            存款批次号
	 * @param remarks
	 *            存款备注
	 * @param user
	 *            存款人
	 * @param equipmentInfo
	 *            存款设备
	 * @param aOffice
	 *            存款发生门店
	 * @param doorOrderInfo
	 *            存款主表信息
	 * @param totalAmountCalculate
	 *            通过明细计算总金额（不包含其他存款）
	 */
	private void insertAccount(String tickerTape, String remarks, User user, EquipmentInfo equipmentInfo,
			Office aOffice, DoorOrderInfo doorOrderInfo, BigDecimal totalAmountCalculate) {
		DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
		// 设置关联账务流水Id
		doorCenterAccountsMain.setBusinessId(doorOrderInfo.getOrderId());
		// 设置客户Id
		doorCenterAccountsMain.setClientId(doorOrderInfo.getDoorId());
		// 设置业务类型
		doorCenterAccountsMain.setBusinessType(ClearConstant.BusinessType.DOOR_ORDER);
		// 设置入库金额(存款总金额-其他存款类型金额)
		doorCenterAccountsMain.setInAmount(totalAmountCalculate);
		// 设置出入库类型
		doorCenterAccountsMain.setInoutType(ClearConstant.AccountsInoutType.ACCOUNTS_IN);
		// 设置账务发生机构（清分中心）
		doorCenterAccountsMain.setRofficeId(equipmentInfo.getVinOffice().getId());
		// 设置账务所在机构（门店）
		doorCenterAccountsMain.setAofficeId(doorOrderInfo.getDoorId());
		// 设置业务状态
		doorCenterAccountsMain.setBusinessStatus(ClearConstant.StatusType.CREATE);
		// 设置账务代付状态(未代付)
		doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
		// 设置凭条
		doorCenterAccountsMain.setTickertape(tickerTape);
		doorCenterAccountsMain.setCreateBy(user);
		doorCenterAccountsMain.setCreateName(user.getName());
		doorCenterAccountsMain.setUpdateBy(user);
		doorCenterAccountsMain.setUpdateName(user.getName());
		// 父机构中包含张家港机构，验证7位码，同时在账务中关联7位码
		String parentIds = officeDao.get(aOffice.getId()).getParentIds();
		if (parentIds.contains(Global.getConfig(DoorOrderConstant.OfficeCode.ZHANGJIAGANG))) {
			int codeSize = Integer.parseInt(Global.getConfig(DoorOrderConstant.SEVEN_CODE_SIZE));
			if (StringUtils.isEmpty(remarks) || remarks.length() != codeSize) {
				throw new BusinessException("E99", "7位码格式不正确");
			}
			int petrolStationCodeSize = Integer.parseInt(Global.getConfig(DoorOrderConstant.PETROL_STATION_CODE_SIZE));
			// 从7位码中截取5位码（油站编码），验证7位码正确
			String petrolCode = remarks.substring(0, petrolStationCodeSize);
			Office office = new Office();
			// 查询条件：机构类型---油站编码、机构编号---5位码
			office.setType(Constant.OfficeType.PETROL_CODE);
			office.setCode(petrolCode);
			List<Office> petrolCodes = officeDao.findAllList(office);
			if (Collections3.isEmpty(petrolCodes)) {
				throw new BusinessException("E99", "油站编码不正确");
			}
			/*
			 * 张家港记录账务具有特殊性，一般机构存款账务发生机构在设备绑定的门店，张家港机构存款账务发生机构在油站编码的门店
			 * 比如在A加油站存款，7位码填的是B加油站的石化，存款流水在A加油站，账务挂在B加油站石化
			 */
			// 设置客户Id
			doorCenterAccountsMain.setClientId(petrolCodes.get(0).getParentId());
			// 设置账务所在机构（门店）
			doorCenterAccountsMain.setAofficeId(petrolCodes.get(0).getParentId());
		}
		doorCenterAccountsMain.setSevenCode(remarks);
		DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
	}

	/**
	 * 更新存款主表信息
	 *
	 * @param bagNo
	 *            存款钞袋号
	 * @param orderFrom
	 *            存款来源
	 * @param user
	 *            存款人
	 * @param equipmentInfo
	 *            存款设备信息
	 * @param orderInfoList
	 *            ？
	 * @param realDate
	 *            存款真实发生时间
	 * @param currentDate
	 *            当前系统时间
	 * @return
	 */
	private DoorOrderInfo updateDoorOrderInfo(String bagNo, DoorOrderDetail doorOrderDetail, String orderFrom,
			User user, EquipmentInfo equipmentInfo, List<DoorOrderInfo> orderInfoList, Date realDate,
			Date currentDate) {
		DoorOrderInfo doorOrderInfo = null;
		if (FROM_ACCOUNT_CHECKING.equals(orderFrom) || FROM_EXCEPTION.equals(orderFrom)) {
			// 存款信息来源是对账或者异常补入，不验证设备当前在用包，可存到在用或已收回状态的包中，如果同时包已收回，需要将存款信息补进拆箱记录中
			DoorOrderInfo infoCondition = new DoorOrderInfo();
			infoCondition.setRfid(bagNo);
			infoCondition.setEquipmentId(equipmentInfo.getId());
			// 传入真实存款时间 判断存款信息应存入哪个包中
			// infoCondition.setRealDate(DateUtils.formatDateTime(realDate));
			infoCondition.getSqlMap().put("dsf", "ORDER BY a.CREATE_DATE DESC ");
			doorOrderInfo = doorOrderInfoDao.getByCondition(infoCondition);
			// 首先验证包在当前系统中的状态
			if (doorOrderInfo == null) {
				throw new BusinessException("E99", "系统中无此包：" + bagNo);
			}
			if (!(DoorOrderConstant.Status.REGISTER.equals(doorOrderInfo.getStatus())
					|| DoorOrderConstant.Status.CONFIRM.equals(doorOrderInfo.getStatus()))) {
				// 如果传入来源的是存款异常
				if (FROM_EXCEPTION.equals(orderFrom)) {
					// 验证 同一机具中是否存在同一包号处于在用或回收状态的包 如果不存在 抛出该包已清分
					// 如果存在 将存款异常存入距离真实存款时间最近的在用或收回包中
					infoCondition.getSqlMap().put("dsf", "and a.create_date > '" + DateUtils.formatDateTime(realDate)
							+ "' AND (a.`STATUS` = '2' or a.`STATUS` = '0')ORDER BY a.CREATE_DATE");
					DoorOrderInfo doorInfo = new DoorOrderInfo();
					doorInfo = doorOrderInfoDao.getByCondition(infoCondition);
					if (doorInfo == null) {
						// 无在用或收回的同一包
						throw new BusinessException("E99", "包[" + doorOrderInfo.getRfid() + "]已清分");
					} else {
						doorOrderInfo = doorInfo;
					}
				} else {
					throw new BusinessException("E99", "包[" + doorOrderInfo.getRfid() + "]已清分");
				}
			}
			doorOrderInfo.setUpdateBy(user);
			doorOrderInfo.setUpdateName(user.getName());
			doorOrderInfo.setUpdateDate(currentDate);
			// 总金额
			BigDecimal amountBig = new BigDecimal(doorOrderInfo.getAmount());
			amountBig = amountBig.add(new BigDecimal(doorOrderDetail.getAmount()));
			doorOrderInfo.setAmount(amountBig.toString());
			// 总笔数
			String totalCountStr = doorOrderInfo.getTotalCount();
			int totalCount = Integer.parseInt(totalCountStr);
			totalCount += 1;
			doorOrderInfo.setTotalCount(Integer.toString(totalCount));
			doorOrderInfoDao.update(doorOrderInfo);
			// 如果包已收回且未清分，需要将存款信息补进拆箱记录中
			if (DoorOrderConstant.Status.CONFIRM.equals(doorOrderInfo.getStatus())) {
				// 拆箱主表查询参数
				CheckCashMain checkCashMain = new CheckCashMain();
				checkCashMain.setOutNo(doorOrderInfo.getOrderId());
				// 在用和已收回状态，可以补入
				checkCashMain.setCheckedMulti(new String[] { "0", "2" });
				List<CheckCashMain> cashMainsList = checkCashMainDao.getByCondition(checkCashMain);
				if (Collections3.isEmpty(cashMainsList)) {
					throw new BusinessException("E99", "包[" + doorOrderInfo.getRfid() + "]已清分");
				}
				// [check_cash_main]
				cashMainsList.get(0).setInputAmount(new BigDecimal(cashMainsList.get(0).getInputAmount())
						.add(new BigDecimal(doorOrderDetail.getAmount())).toString());
				cashMainsList.get(0)
						.setBoxCount(new BigDecimal(cashMainsList.get(0).getBoxCount()).add(BigDecimal.ONE).toString());
				checkCashMainDao.update(cashMainsList.get(0));
				// [check_cash_amount]
				CheckCashAmount checkCashAmount = new CheckCashAmount();
				// 预约单号
				checkCashAmount.setOutNo(doorOrderInfo.getOrderId());
				// 明细序号
				checkCashAmount.setOutRowNo(doorOrderDetail.getDetailId());
				// 录入金额
				checkCashAmount.setInputAmount(doorOrderDetail.getAmount());
				// 清点金额
				checkCashAmount.setCheckAmount("0");
				// 差额
				checkCashAmount.setDiffAmount("0");
				// 凭条
				checkCashAmount.setPackNum(doorOrderDetail.getTickertape());
				// 数据区分（1：分配）
				checkCashAmount.setDataFlag(CollectionConstant.dataFlagType.ALLOT);
				// 启用标识（0：未启用）
				checkCashAmount.setEnabledFlag(CollectionConstant.enabledFlagType.NO);
				Date date = new Date();
				checkCashAmount.setCreateBy(null);
				checkCashAmount.setCreateName(null);
				checkCashAmount.setCreateDate(date);
				checkCashAmount.setUpdateBy(null);
				checkCashAmount.setUpdateName(null);
				checkCashAmount.setUpdateDate(date);
				checkCashAmount.setId(IdGen.uuid());
				checkCashAmountDao.insert(checkCashAmount);
			}
		} else if (FROM_EQP.equals(orderFrom)) {
			// 存款信息来源是设备端，需验证系统中存储的设备当前在用状态包，如非此包，登记到异常中
			if (orderInfoList.size() == 0) {
				// 没有在登记录，创建登记状态的新纪录，每个设备在登记只有一条，清机之后再存款重新生成
				doorOrderInfo = new DoorOrderInfo();
				doorOrderInfo.setId(IdGen.uuid());
				doorOrderInfo.setDoorId(equipmentInfo.getaOffice().getId());
				doorOrderInfo.setDoorName(equipmentInfo.getaOffice().getName());
				doorOrderInfo.setAmount(doorOrderDetail.getAmount());
				doorOrderInfo.setStatus(DoorOrderConstant.Status.REGISTER);
				doorOrderInfo.setMethod(DoorOrderConstant.MethodType.METHOD_EQUIPMENT);
				doorOrderInfo.setAllotStatus(DoorOrderConstant.AllotStatus.UNALLOTTED);
				doorOrderInfo.setRfid(bagNo);
				doorOrderInfo.setTotalCount(Integer.toString(1));
				doorOrderInfo.setEquipmentId(equipmentInfo.getId());
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String orderDateStr = formatter.format(realDate);
				// 预约日期：ORDER_DATE
				doorOrderInfo.setOrderDate(orderDateStr);
				// 预约ID：ORDER_ID
				String orderId = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER,
						doorOrderInfo.getOffice());
				doorOrderInfo.setOrderId(orderId);
				doorOrderInfo.setOffice(equipmentInfo.getVinOffice());
				// 创建人、时间，更新人、时间
				doorOrderInfo.setIsNewRecord(false);
				doorOrderInfo.setDelFlag(Constant.deleteFlag.Valid);
				doorOrderInfo.setCreateBy(user);
				doorOrderInfo.setCreateName(user.getName());
				doorOrderInfo.setCreateDate(realDate);
				doorOrderInfo.setUpdateBy(user);
				doorOrderInfo.setUpdateName(user.getName());
				doorOrderInfo.setUpdateDate(realDate);
				doorOrderInfo.setDelFlag(Constant.deleteFlag.Valid);
				doorOrderInfo.setBagCapacity(Constant.BagCpapcity.TEN_THOUSAND);
				doorOrderInfoDao.insert(doorOrderInfo);
				// 添加清机任务
				insertClearPlanInfo(equipmentInfo, doorOrderInfo, currentDate);
			} else if (orderInfoList.size() == 1) {
				// 有在登记记录，更新总金额、更新时间、总笔数
				doorOrderInfo = orderInfoList.get(0);
				// 验证当前存款包号是否和设备当前存款包号相同
				if (!doorOrderInfo.getRfid().equals(bagNo)) {
					throw new BusinessException("E99", "包号不正确");
				}
				doorOrderInfo.setUpdateBy(user);
				doorOrderInfo.setUpdateName(user.getName());
				doorOrderInfo.setUpdateDate(currentDate);
				// 总金额
				BigDecimal amountBig = new BigDecimal(doorOrderInfo.getAmount());
				amountBig = amountBig.add(new BigDecimal(doorOrderDetail.getAmount()));
				doorOrderInfo.setAmount(amountBig.toString());
				// 总笔数
				String totalCountStr = doorOrderInfo.getTotalCount();
				int totalCount = Integer.parseInt(totalCountStr);
				totalCount += 1;
				doorOrderInfo.setTotalCount(Integer.toString(totalCount));
				doorOrderInfoDao.update(doorOrderInfo);
			}
		} else {
			throw new BusinessException("E99", "存款信息来源不明");
		}
		return doorOrderInfo;
	}

	/**
	 * 添加清机加钞记录
	 *
	 * @param user
	 *            用户信息
	 * @param equipmentInfo
	 *            设备信息
	 * @param currentDate
	 *            当前时间
	 * @param doorOrderDetail
	 *            预约明细
	 * @author yinkai
	 */
	private void insertClearAddMoney(User user, EquipmentInfo equipmentInfo, Date currentDate,
			DoorOrderDetail doorOrderDetail) {
		// 获取设备余额信息
		Map<String, Object> equipmentBalanceInfo = doorOrderInfoDao.getEquipmentBalanceInfo(equipmentInfo.getId());
		BigDecimal surplusAmount = new BigDecimal(0);
		BigDecimal eqpLeftCount = new BigDecimal(0);
		if (equipmentBalanceInfo != null) {
			surplusAmount = (BigDecimal) equipmentBalanceInfo.get("AMOUNT");
			eqpLeftCount = (BigDecimal) equipmentBalanceInfo.get("COUNT_ZHANG");
		}
		ClearAddMoney clearAddMoney = new ClearAddMoney();
		clearAddMoney.setEquipmentId(equipmentInfo.getId());
		clearAddMoney.setSurplusAmount(surplusAmount);
		clearAddMoney.setCount(eqpLeftCount.intValue());
		clearAddMoney.setId(IdGen.uuid());
		clearAddMoney.setEquipmentName(equipmentInfo.getName());
		clearAddMoney.setDoorId(equipmentInfo.getaOffice().getId());
		clearAddMoney.setDoorName(equipmentInfo.getaOffice().getName());
		clearAddMoney.setClearCenterId(equipmentInfo.getVinOffice().getId());
		clearAddMoney.setClearCenterName(equipmentInfo.getVinOffice().getName());
		clearAddMoney.setBagNo(doorOrderDetail.getRfid());
		clearAddMoney.setBagStatus(DoorOrderConstant.BagStatus.USERING);
		clearAddMoney.setAmount(new BigDecimal(doorOrderDetail.getAmount()));
		clearAddMoney.setBusinessId(doorOrderDetail.getTickertape());
		clearAddMoney.setCreateBy(user);
		clearAddMoney.setCreateDate(doorOrderDetail.getCreateDate());
		clearAddMoney.setUpdateBy(user);
		clearAddMoney.setUpdateDate(currentDate);
		clearAddMoney.setType(DoorOrderConstant.ClearStatus.DEPOSIT);
		clearAddMoney.setDelFlag(Constant.deleteFlag.Valid);
		clearAddMoneyDao.insert(clearAddMoney);
	}

	/**
	 * 创建清机任务
	 *
	 * @param equipmentInfo
	 *            设备信息
	 * @param doorOrderInfo
	 *            预约主表信息
	 * @param currentDate
	 *            当前时间
	 */
	private void insertClearPlanInfo(EquipmentInfo equipmentInfo, DoorOrderInfo doorOrderInfo, Date currentDate) {
		// 创建清机任务，生成机制同存款主表数据
		ClearPlanInfo clearPlanInfo = new ClearPlanInfo();
		// 清机加钞记录存款数据关联存款单号
		clearPlanInfo.setPlanId(doorOrderInfo.getOrderId());
		clearPlanInfo.setId(BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.CLEAR_PLAN, null));
		// 通过设备号查请机组
		ClearGroupMain groupByEquipment = clearGroupMainDao.getGroupByEquipment(equipmentInfo.getId());
		if (groupByEquipment == null) {
			throw new BusinessException("E99", "设备所属门店未分配请机组");
		}
		clearPlanInfo.setClearingGroupId(groupByEquipment.getClearGroupId());
		clearPlanInfo.setEquipmentId(equipmentInfo.getId());
		clearPlanInfo.setPlanType(DoorOrderConstant.PlanType.CERTAINLY);
		clearPlanInfo.setStatus(Constant.ClearPlanStatus.UNCOMPLETE);
		clearPlanInfo.setCreateDate(currentDate);
		clearPlanInfo.setUpdateDate(currentDate);
		clearPlanInfoDao.insert(clearPlanInfo);
	}

	/**
	 * 保存[door_order_amount]
	 *
	 * @param doorOrderDetail
	 *            存款批次
	 * @param detail
	 *            存款面值明细
	 * @param currency
	 *            币种
	 */
	private void insertOrderAmount(DoorOrderDetail doorOrderDetail, List<Map<String, Object>> detail, String currency) {
		// 每条amount记录的行标，在存款详细页中用来排序
		int rowNo = 1;
		// 统计本次存款的速存、强存、不记账的总金额和数量
		int coinCount = 0; // 硬币数量
		BigDecimal coinAmount = BigDecimal.ZERO; // 硬币金额
		int paperCount = 0; // 纸币数量
		BigDecimal paperAmount = BigDecimal.ZERO; // 纸币金额
		BigDecimal forceAmount = BigDecimal.ZERO; // 强制金额
		BigDecimal otherAmount = BigDecimal.ZERO; // 其他（不记账）金额
		// 循环三种存款方式（速存、强存、其他），存入
		for (Map<String, Object> detailMap : detail) {
			// 速存部分，计算硬币数量、硬币金额、纸币数量、纸币金额，每一种面值生成一条[door_order_amount]记录，保存每种面值的张数和总额
			String type = (String) detailMap.get("type");
			String saveMethod = DictUtils.getDictLabel(type, "save_method", "");
			switch (type) {
			case DoorOrderConstant.SaveMethod.CASH_SAVE:
				// 面值列表不空，遍历面值列表，每种面值一条记录
				List<Map<String, Object>> denominationList = (List<Map<String, Object>>) detailMap
						.get(Parameter.DENOMINATION_KEY);
				if (denominationList == null) {
					throw new BusinessException("E99", "速存类型必须填写面值明细");
				}
				for (Map<String, Object> denomination : denominationList) {
					// 面值字典校验
					List<String> currencies = Lists.newArrayList();
					currencies.add(Constant.DenominationType.RMB_HDEN);
					currencies.add(Constant.DenominationType.RMB_PDEN);
					Map<String, Object> situation = new HashMap<>(16);
					String id = (String) denomination.get("ID");
					if (StringUtils.isEmpty(id)) {
						throw new BusinessException("E99", "面值不能为空");
					}
					situation.put("currencies", currencies);
					situation.put("id", id);
					List<StoDict> allDenomination = stoDictDao.findAllDenomination(situation);
					if (Collections3.isEmpty(allDenomination)) {
						throw new BusinessException("E99", "面值类型不正确");
					}
					// 查询面值ID
					StoDict stoDict = allDenomination.get(0);
					BigDecimal unitVal = stoDict.getUnitVal();
					// 数字校验
					String countStr = CommonUtils.toString(denomination.get("count"));
					if (StringUtils.isEmpty(countStr)) {
						throw new BusinessException("E99", "面值张数不能为空");
					}
					int count;
					try {
						double countDb = Double.parseDouble(countStr);
						if (countDb % 1 != 0) {
							throw new Exception("非整数");
						}
						count = Integer.parseInt(countStr.substring(0, countStr.indexOf(".")));
					} catch (Exception e) {
						throw new BusinessException("E99", "面值张数不正确");
					}
					// 本面值总额
					BigDecimal detailAmount = unitVal.multiply(new BigDecimal(count));
					DoorOrderAmount doorOrderAmount = new DoorOrderAmount();
					doorOrderAmount.setId(IdGen.uuid());
					doorOrderAmount.setDetailId(doorOrderDetail.getId());
					doorOrderAmount.setCurrency(currency);
					doorOrderAmount.setDenomination((String) denomination.get("ID"));
					doorOrderAmount.setCountZhang(String.valueOf(count));
					doorOrderAmount.setDetailAmount(detailAmount.toString());
					// 存款方式
					if (StringUtils.isBlank(saveMethod)) {
						throw new BusinessException("E99", "存款方式不正确");
					}
					doorOrderAmount.setTypeId(type);
					doorOrderAmount.setRowNo(rowNo++);
					doorOrderAmountDao.insert(doorOrderAmount);
					// 存款总额
					int idIntVal = Integer.parseInt(id);
					if (idIntVal >= 15 && idIntVal <= 27) {
						// 纸币统计
						paperCount += count;
						paperAmount = paperAmount.add(detailAmount);
					} else if (idIntVal >= 28 && idIntVal <= 33) {
						// 硬币统计
						coinCount += count;
						coinAmount = coinAmount.add(detailAmount);
					}
				}
				break;
			case DoorOrderConstant.SaveMethod.BAG_SAVE:
			case DoorOrderConstant.SaveMethod.OTHER_SAVE:
				// 强制存款金额
				Double amountDouble = (Double) detailMap.get(Parameter.AMOUNT_KEY);
				DoorOrderAmount doorOrderAmount = new DoorOrderAmount();
				doorOrderAmount.setId(IdGen.uuid());
				// 存款方式
				if (StringUtils.isBlank(saveMethod)) {
					throw new BusinessException("E99", "存款方式不正确");
				}
				doorOrderAmount.setTypeId(type);
				doorOrderAmount.setDetailId(doorOrderDetail.getId());
				doorOrderAmount.setCurrency(currency);
				doorOrderAmount.setDetailAmount(amountDouble.toString());
				// 序号
				doorOrderAmount.setRowNo(rowNo++);
				doorOrderAmountDao.insert(doorOrderAmount);
				if (DoorOrderConstant.SaveMethod.OTHER_SAVE.equals(type)) {
					otherAmount = otherAmount.add(new BigDecimal(amountDouble.toString()));
				}
				if (DoorOrderConstant.SaveMethod.BAG_SAVE.equals(type)) {
					forceAmount = forceAmount.add(new BigDecimal(amountDouble.toString()));
				}
				break;
			default:
				throw new BusinessException("E99", "未知存款方式类型：" + type);
			}
		}
		// 总金额
		BigDecimal totalAmount = paperAmount.add(coinAmount).add(forceAmount);
		// 速存、强存、其他
		doorOrderDetail.setPaperAmount(paperAmount);
		doorOrderDetail.setPaperCount(paperCount);
		doorOrderDetail.setCoinAmount(coinAmount);
		doorOrderDetail.setCoinCount(coinCount);
		doorOrderDetail.setForceAmount(forceAmount);
		doorOrderDetail.setOtherAmount(otherAmount);
		doorOrderDetail.setAmount(totalAmount.toString());
	}

	/**
	 * 保存[door_order_detail]
	 * 
	 * @param doorOrderDetailNew
	 *            存款批次
	 * @param doorOrderInfo
	 *            ？
	 */
	private void insertDoorDetail(DoorOrderDetail doorOrderDetailNew, DoorOrderInfo doorOrderInfo) {
		// 验证凭条号重复
		List<DoorOrderDetail> details = doorOrderDetailDao.getDetailByTickerTape(doorOrderDetailNew.getTickertape());
		if (details != null) {
			if (details.size() == 1) {
				throw new BusinessException("E99", "凭条重复");
			} else if (details.size() > 1) {
				throw new BusinessException("E99", "存在多个重复的凭条");
			}
		}
		// 查找存款发生商户ID
		String doorId = doorOrderInfo.getDoorId();
		Office door = officeDao.get(doorId);
		Office merchant = door.getParent();
		SaveType type = saveTypeDao.getByTypeCodeAndMerchantId(doorOrderDetailNew.getBusinessType(), merchant.getId());
		if (type == null) {
			throw new BusinessException("E99", "存款类型不正确");
		}
		doorOrderDetailNew.setBusinessType(type.getId());
		doorOrderDetailNew.setDetailId(doorOrderInfo.getTotalCount());
		doorOrderDetailNew.setOrderId(doorOrderInfo.getOrderId());
		doorOrderDetailDao.insert(doorOrderDetailNew);
	}

	/**
	 * 出现存款先异常，后正确的处理 将已登记的存款异常状态更改为已处理
	 *
	 * @param tickerTape
	 *            凭条
	 */
	private void changeExceptionStatus(String tickerTape) {
		DoorOrderException doorOrderException = new DoorOrderException();
		doorOrderException.setTickerTape(tickerTape);
		List<DoorOrderException> list = doorOrderExceptionService.findList(doorOrderException);
		if (!Collections3.isEmpty(list)) {
			list.get(0).setStatus(DoorOrderConstant.ExceptionStatus.PROCESSED);
			list.get(0).setExceptionReason(null);
			list.get(0).setUpdateDate(new Date());
			doorOrderExceptionDao.update(list.get(0));
		}
	}

	/**
	 * 参数校验
	 *
	 * @param paramMap
	 *            参数列表
	 * @return 验证结果
	 */
	private String checkParam(Map<String, Object> paramMap) {
		String errorMsg;
		// 字符串空校验
		if (paramMap.get(Parameter.EQUIPMENT_ID_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.EQUIPMENT_ID_KEY))) {
			logger.debug("参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY)));
			errorMsg = "参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY));
			return errorMsg;
		}
		if (paramMap.get(Parameter.BUSINESS_TYPE_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.BUSINESS_TYPE_KEY))) {
			logger.debug("参数错误--------" + Parameter.BUSINESS_TYPE_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BUSINESS_TYPE_KEY)));
			errorMsg = "参数错误--------" + Parameter.BUSINESS_TYPE_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BUSINESS_TYPE_KEY));
			return errorMsg;
		}
		if (paramMap.get(Parameter.BAG_NO_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.BAG_NO_KEY))) {
			logger.debug("参数错误--------" + Parameter.BAG_NO_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BAG_NO_KEY)));
			errorMsg = "参数错误--------" + Parameter.BAG_NO_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BAG_NO_KEY));
			return errorMsg;
		}
		if (paramMap.get(Parameter.TOTAL_AMOUNT_KEY) == null) {
			logger.debug("参数错误--------" + Parameter.TOTAL_AMOUNT_KEY + ":null");
			errorMsg = "参数错误--------" + Parameter.TOTAL_AMOUNT_KEY + ":null";
			return errorMsg;
		} else {
			try {
				String amountStr = CommonUtils.toString(paramMap.get(Parameter.TOTAL_AMOUNT_KEY));
				new BigDecimal(amountStr);
			} catch (Exception e) {
				logger.debug("参数格式不正确--------" + Parameter.AMOUNT_KEY + ":不是正确的数字格式");
				errorMsg = "参数格式不正确--------" + Parameter.AMOUNT_KEY + ":不是正确的数字格式";
				return errorMsg;
			}
		}
		if (paramMap.get(Parameter.TICKER_TAPE_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.TICKER_TAPE_KEY))) {
			logger.debug("参数错误--------" + Parameter.TICKER_TAPE_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.TICKER_TAPE_KEY)));
			errorMsg = "参数错误--------" + Parameter.TICKER_TAPE_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.TICKER_TAPE_KEY));
			return errorMsg;
		}
		if (paramMap.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.USER_ID_KEY))) {
			logger.debug("参数错误--------" + Parameter.USER_ID_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY)));
			errorMsg = "参数错误--------" + Parameter.USER_ID_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
			return errorMsg;
		}
		if (paramMap.get(Parameter.CURRENCY_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.CURRENCY_KEY))) {
			logger.debug("参数错误--------" + Parameter.CURRENCY_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.CURRENCY_KEY)));
			errorMsg = "参数错误--------" + Parameter.CURRENCY_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.CURRENCY_KEY));
			return errorMsg;
		}
		if (paramMap.get(Parameter.DETAIL_KEY) == null) {
			logger.debug("参数错误--------" + Parameter.DETAIL_KEY + ":null");
			errorMsg = "参数错误--------" + Parameter.DETAIL_KEY + ":null";
			return errorMsg;
		} else {
			List<Map<String, Object>> detailList = (List<Map<String, Object>>) paramMap.get(Parameter.DETAIL_KEY);
			if (Collections3.isEmpty(detailList)) {
				logger.debug("参数错误--------" + Parameter.DETAIL_KEY + "列表不能为空");
				errorMsg = "参数错误--------" + Parameter.DETAIL_KEY + "列表不能为空";
				return errorMsg;
			}
			for (Map<String, Object> detailMap : detailList) {
				if (detailMap.get(Parameter.TYPE_KEY) == null
						|| StringUtils.isEmpty((String) detailMap.get(Parameter.TYPE_KEY))) {
					logger.debug("参数错误--------明细类型不能为空");
					errorMsg = "参数错误--------明细类型不能为空";
					return errorMsg;
				}
				if (detailMap.get(Parameter.AMOUNT_KEY) != null) {
					String amountStr = CommonUtils.toString(detailMap.get(Parameter.AMOUNT_KEY));
					try {
						new BigDecimal(amountStr);
					} catch (Exception e) {
						logger.debug("参数格式不正确--------" + Parameter.AMOUNT_KEY + ":不是正确的数字格式");
						errorMsg = "参数格式不正确--------" + Parameter.AMOUNT_KEY + ":不是正确的数字格式";
						return errorMsg;
					}
				}
			}
		}
		return null;
	}

	private String setReturnMap(Map<String, Object> map, String serviceNo) {
		map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		return gson.toJson(map);
	}
}
