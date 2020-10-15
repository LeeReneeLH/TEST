package com.coffer.external.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashAmountDao;
import com.coffer.businesses.modules.collection.v03.dao.CheckCashMainDao;
import com.coffer.businesses.modules.collection.v03.dao.TaskConfirmDao;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearAddMoneyDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearGroupMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearPlanInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearAddMoney;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
 * Title: Service0803
 * <p>
 * Description:机具清机完成接口
 * </p>
 *
 * @author yinkai
 * @date 2019.7.10
 */
@Component("Service0803")
@Scope("singleton")
public class Service0803 extends HardwardBaseService {

	@Autowired
	private ClearPlanInfoDao clearPlanInfoDao;

	@Autowired
	private EquipmentInfoDao equipmentInfoDao;

	@Autowired
	private DoorOrderInfoDao doorOrderInfoDao;

	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	@Autowired
	private ClearGroupMainDao clearGroupMainDao;

	@Autowired
	private ClearAddMoneyDao clearAddMoneyDao;

	@Autowired
	private CheckCashAmountDao checkCashAmountDao;

	@Autowired
	private TaskConfirmDao taskConfirmDao;

	@Autowired
	private CheckCashMainDao checkCashMainDao;

	@Autowired
	private UserDao userDao;

	@Override
	@Transactional
	public String execute(Map<String, Object> paramMap) {
		// 检查参数
		String checkParamMsg = checkParam(paramMap);
		if (checkParamMsg != null) {
			throw new BusinessException("E99", checkParamMsg);
		}
		String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
		String eqpId = (String) paramMap.get(Parameter.EQUIPMENT_ID_KEY);
		String userId = (String) paramMap.get(Parameter.USER_ID_KEY);
		String bagNo = (String) paramMap.get(Parameter.BAG_NO_KEY);
		String totalAmountStr = CommonUtils.toString(paramMap.get(Parameter.TOTAL_AMOUNT_KEY));
		String totalCountStr = CommonUtils.toString(paramMap.get(Parameter.TOTAL_COUNT_KEY));
		String bagCapacity = CommonUtils.toString(paramMap.get(Parameter.BAG_CAPACITY));
		Date currentDateTime = new Date();
		int totalCount = Integer.parseInt(totalCountStr.substring(0, totalCountStr.indexOf('.')));
		totalAmountStr = totalAmountStr.replaceAll(",", "");
		BigDecimal totalAmount = new BigDecimal(totalAmountStr);
		Date currentDate = new Date();
		// 查找操作用户信息
		User user = userDao.get(userId);
		if (user == null) {
			throw new BusinessException("E99", "用户信息有误");
		}
		// 验证清机员
		if (!DoorOrderConstant.SysUserType.CLEARING_CENTER_CLEAR_MAN.equals(user.getUserType())) {
			throw new BusinessException("E99", "该用户不是清机员");
		}
		// 查找设备信息
		EquipmentInfo equipmentInfo = equipmentInfoDao.get(eqpId);
		if (equipmentInfo == null) {
			throw new BusinessException("E99", "设备信息有误");
		}
		// 查找当前设备所在门店的任务明细
		Office aOffice = equipmentInfo.getaOffice();
		if (aOffice == null) {
			throw new BusinessException("E99", "设备未绑定门店");
		}
		// 查找设备所属清分中心
		Office vinOffice = equipmentInfo.getVinOffice();
		if (vinOffice == null) {
			throw new BusinessException("E99", "设备没未绑定清分中心");
		}
		// 查询新包号是否在用
	   List<String> repeatBagNo = doorOrderInfoDao.getRepeatBagNo(bagNo,"");
        if (!Collections3.isEmpty(repeatBagNo)) {
        	throw new BusinessException("E99", "该包号已使用");
        }
		// 完成清机任务（条件：未完成，设备ID）
		ClearPlanInfo clearPlanInfo = completeClearPlan(user, equipmentInfo);
		// 修改设备门店预约状态为确认（一个设备只有一个登记状态的预约）
		DoorOrderInfo doorOrderInfo = updateDoorOrderInfo(currentDateTime, user, equipmentInfo);
		// 本次清机信息添加到清机加钞记录表中，清机加钞中记录旧包号、现钞数、金额
		String oldBagNo = doorOrderInfo == null ? "" : doorOrderInfo.getRfid();
		addClearAddMoneyInfo(oldBagNo, currentDateTime, totalCount, totalAmount, user, equipmentInfo, clearPlanInfo);
		// 正常清机，款袋添加到款箱拆箱列表中
		if(doorOrderInfo != null) {
			addCheckCashInfo(currentDateTime, user, equipmentInfo, doorOrderInfo);
		}
		// 清机中不包含信包号参数，不为设备创建新的包信息
		if (StringUtils.isNotEmpty(bagNo)) {
			// 清机时会传新钞袋号，在存款主表中生成新钞袋记录
			doorOrderInfo = new DoorOrderInfo();
			doorOrderInfo.setId(IdGen.uuid());
			doorOrderInfo.setDoorName(equipmentInfo.getaOffice().getName());
			doorOrderInfo.setDoorId(equipmentInfo.getaOffice().getId());
			doorOrderInfo.setAmount("0");
			doorOrderInfo.setStatus(DoorOrderConstant.Status.REGISTER);
			doorOrderInfo.setMethod(DoorOrderConstant.MethodType.METHOD_EQUIPMENT);
			doorOrderInfo.setAllotStatus(DoorOrderConstant.AllotStatus.UNALLOTTED);
			doorOrderInfo.setRfid(bagNo);
			doorOrderInfo.setTotalCount(Integer.toString(0));
			doorOrderInfo.setEquipmentId(eqpId);
			doorOrderInfo.setBagCapacity(bagCapacity);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String orderDateStr = formatter.format(currentDate);
			// 预约日期：ORDER_DATE
			doorOrderInfo.setOrderDate(orderDateStr);
			// 预约ID：ORDER_ID
			String orderId = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER, doorOrderInfo.getOffice());
			doorOrderInfo.setOrderId(orderId);
			doorOrderInfo.setOffice(equipmentInfo.getVinOffice());
			// 创建人、时间，更新人、时间
			User System = new User();
			System.setId("system");
			System.setName("system");
			doorOrderInfo.setIsNewRecord(false);
			doorOrderInfo.setDelFlag(Constant.deleteFlag.Valid);
			doorOrderInfo.setCreateBy(System);
			doorOrderInfo.setCreateName("system");
			doorOrderInfo.setCreateDate(currentDate);
			doorOrderInfo.setUpdateBy(System);
			doorOrderInfo.setUpdateName("system");
			doorOrderInfo.setUpdateDate(currentDate);
			doorOrderInfo.setDelFlag(Constant.deleteFlag.Valid);
			doorOrderInfoDao.insert(doorOrderInfo);
			// 添加清机任务
			insertClearPlanInfo(equipmentInfo, doorOrderInfo, currentDate);
		}
		Map<String, Object> map = new HashMap<>();
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		map.put(Parameter.ERROR_NO_KEY, null);
		map.put(Parameter.ERROR_MSG_KEY, null);
		return setReturnMap(map, serviceNo);
	}

	/**
	 * 生成款箱拆箱记录
	 * 
	 * @param currentDateTime
	 *            当前时间
	 * @param user
	 *            ？？
	 * @param equipmentInfo
	 *            清机设备信息
	 * @param doorOrderInfo
	 *            门店存款主表信息
	 */
	private void addCheckCashInfo(Date currentDateTime, User user, EquipmentInfo equipmentInfo,
			DoorOrderInfo doorOrderInfo) {
		CheckCashMain checkCashMain = new CheckCashMain();
		// 创建虚拟用户
		User vUser = new User();
		vUser.setId("0");
		vUser.setName("系统分配");
		// 预约单号
		checkCashMain.setOutNo(doorOrderInfo.getOrderId());
		// 包号
		checkCashMain.setRfid(doorOrderInfo.getRfid());
		// 门店编号
		checkCashMain.setCustNo(doorOrderInfo.getDoorId());
		// 门店名称
		checkCashMain.setCustName(doorOrderInfo.getDoorName());
		// 拆箱总金额
		checkCashMain.setInputAmount(doorOrderInfo.getAmount());
		// 清点总金额
		checkCashMain.setCheckAmount("0");
		// 差额
		checkCashMain.setDiffAmount("0");
		// 总笔数
		checkCashMain.setBoxCount(doorOrderInfo.getTotalCount());
		// 登记日期
		checkCashMain.setRegDate(currentDateTime);
		// 数据区分（1：分配）
		checkCashMain.setDataFlag(CollectionConstant.dataFlagType.ALLOT);
		checkCashMain.setCreateDate(currentDateTime);
		checkCashMain.setUpdateDate(currentDateTime);
		checkCashMain.setId(IdGen.uuid());
		checkCashMain.setOffice(equipmentInfo.getVinOffice());
		checkCashMainDao.insert(checkCashMain);
		// 款箱拆箱每笔明细表的作成
		List<DoorOrderDetail> orderDetailList = null;
		DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
		// 预约单号
		doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
		// 明细列表
		orderDetailList = doorOrderDetailDao.findList(doorOrderDetail);
		for (DoorOrderDetail itemData : orderDetailList) {
			CheckCashAmount checkCashAmount = new CheckCashAmount();
			// 预约单号
			checkCashAmount.setOutNo(itemData.getOrderId());
			// 明细序号
			checkCashAmount.setOutRowNo(itemData.getDetailId());
			// 录入金额
			checkCashAmount.setInputAmount(itemData.getAmount());
			// 清点金额
			checkCashAmount.setCheckAmount("0");
			// 差额
			checkCashAmount.setDiffAmount("0");
			// 凭条
			checkCashAmount.setPackNum(itemData.getTickertape());
			// 数据区分（1：分配）
			checkCashAmount.setDataFlag(CollectionConstant.dataFlagType.ALLOT);
			// 启用标识（0：未启用）
			checkCashAmount.setEnabledFlag(CollectionConstant.enabledFlagType.NO);
			checkCashAmount.setCreateBy(vUser);
			checkCashAmount.setCreateName(vUser.getName());
			checkCashAmount.setCreateDate(currentDateTime);
			checkCashAmount.setUpdateBy(vUser);
			checkCashAmount.setUpdateName(vUser.getName());
			checkCashAmount.setUpdateDate(currentDateTime);
			checkCashAmount.setId(IdGen.uuid());
			checkCashAmount.setRemarks(itemData.getRemarks());
			checkCashAmount.setDetailId(itemData.getId());
			checkCashAmountDao.insert(checkCashAmount);
		}
		// 分配任务状态的更新
		// 分配状态（2：已确认）
		TaskConfirm taskConfirm = new TaskConfirm();
		taskConfirm.setId(doorOrderInfo.getId());
		taskConfirm.setAllotStatus(CollectionConstant.allotStatusType.CONFIRM_OK);
		taskConfirm.setUpdateBy(user);
		taskConfirm.setUpdateName(user.getName());
		taskConfirm.setUpdateDate(currentDateTime);
		taskConfirm.setOrderDate(doorOrderInfo.getOrderDate());
		taskConfirmDao.updateAllotStatus(taskConfirm);
	}

	/**
	 * 添加清机加钞记录
	 * 
	 * @param bagNo
	 *            款袋号
	 * @param currentDateTime
	 *            当前时间
	 * @param totalCount
	 *            总张数
	 * @param totalAmount
	 *            总金额
	 * @param user
	 *            清机人
	 * @param equipmentInfo
	 *            清机设备
	 * @param unCompletePlan
	 *            未完成任务信息
	 */
	private void addClearAddMoneyInfo(String bagNo, Date currentDateTime, int totalCount, BigDecimal totalAmount,
			User user, EquipmentInfo equipmentInfo, ClearPlanInfo unCompletePlan) {
		ClearAddMoney clearAddMoney = new ClearAddMoney();
		clearAddMoney.setId(IdGen.uuid());
		clearAddMoney.setDoorId(equipmentInfo.getaOffice().getId());
		clearAddMoney.setDoorName(equipmentInfo.getaOffice().getName());
		clearAddMoney.setEquipmentId(equipmentInfo.getId());
		clearAddMoney.setEquipmentName(equipmentInfo.getName());
		clearAddMoney.setClearCenterId(equipmentInfo.getVinOffice().getId());
		clearAddMoney.setClearCenterName(equipmentInfo.getVinOffice().getName());
		clearAddMoney.setBagNo(bagNo);
		clearAddMoney.setCount(totalCount);
		clearAddMoney.setAmount(totalAmount);
		clearAddMoney.setChangeDate(currentDateTime);
		clearAddMoney.setChangeCode(user.getId());
		// 清机类型变为 清机
		clearAddMoney.setType(DoorOrderConstant.ClearStatus.CLEAR);
		// 余额 变为 0
		clearAddMoney.setSurplusAmount(new BigDecimal(0));
		// 添加清机任务id
		clearAddMoney.setBusinessId(unCompletePlan == null ? "" : unCompletePlan.getId());
		clearAddMoneyDao.insert(setCreateAndUpdateData(clearAddMoney, user, currentDateTime));
	}

	private DoorOrderInfo updateDoorOrderInfo(Date currentDateTime, User user, EquipmentInfo equipmentInfo) {
		DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
		doorOrderInfo.setDoorId(equipmentInfo.getaOffice().getId());
		doorOrderInfo.setEquipmentId(equipmentInfo.getId());
		doorOrderInfo.setStatus(DoorOrderConstant.Status.REGISTER);
		doorOrderInfo = doorOrderInfoDao.getByCondition(doorOrderInfo);
		// 设备没有做过存款，可能是开机第一次清机，不必更新存款数据
		if (doorOrderInfo != null) {
			doorOrderInfo.setUpdateBy(user);
			doorOrderInfo.setUpdateDate(currentDateTime);
			doorOrderInfoDao.updateStatusconfirm(doorOrderInfo);
		}
		return doorOrderInfo;
	}

	/**
	 * 完成当前设备的清机任务
	 *
	 * @param user
	 *            用户信息
	 * @param equipmentInfo
	 *            设备信息
	 * @return 清机任务
	 */
	private ClearPlanInfo completeClearPlan(User user, EquipmentInfo equipmentInfo) {
		ClearPlanInfo clearPlanInfo = new ClearPlanInfo();
		clearPlanInfo.setEquipmentId(equipmentInfo.getId());
		clearPlanInfo.setStatus(Constant.ClearPlanStatus.UNCOMPLETE);
		List<ClearPlanInfo> unCompletePlanList = clearPlanInfoDao.getUnCompletePlanList(equipmentInfo.getId());
		if (!Collections3.isEmpty(unCompletePlanList) && unCompletePlanList.size() == 1) {
			ClearPlanInfo unCompleteByEqpId = unCompletePlanList.get(0);
			// 更新任务信息：状态、清机人员、清机时间
			unCompleteByEqpId.setStatus(Constant.ClearPlanStatus.COMPLETE);
			unCompleteByEqpId.setClearManNo(user.getId());
			unCompleteByEqpId.setClearManName(user.getName());
			unCompleteByEqpId.setUpdateBy(user);
			unCompleteByEqpId.setUpdateDate(new Date());
			clearPlanInfoDao.update(unCompleteByEqpId);
			return unCompletePlanList.get(0);
		} else if (unCompletePlanList.size() > 1) {
			throw new BusinessException("E99", "有重复的清机任务");
		}
		return null;
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
	 * 接口参数校验
	 *
	 * @param paramMap
	 *            参数列表
	 * @return 错误信息
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
		if (paramMap.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.USER_ID_KEY))) {
			logger.debug("参数错误--------" + Parameter.USER_ID_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY)));
			errorMsg = "参数错误--------" + Parameter.USER_ID_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
			return errorMsg;
		}
		// 款袋编号校验注释  gzd 2020-06-24 
		if (paramMap.get(Parameter.BAG_NO_KEY) == null
				|| StringUtils.isEmpty((String) paramMap.get(Parameter.BAG_NO_KEY))) {
			logger.debug("参数错误--------" + Parameter.BAG_NO_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BAG_NO_KEY)));
			errorMsg = "参数错误--------" + Parameter.BAG_NO_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BAG_NO_KEY));
			return errorMsg;
		}
		// 数字格式校验
		if (paramMap.get(Parameter.TOTAL_AMOUNT_KEY) == null) {
			logger.debug("参数错误--------" + Parameter.TOTAL_AMOUNT_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.TOTAL_AMOUNT_KEY)));
			errorMsg = "参数错误--------" + Parameter.TOTAL_AMOUNT_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.TOTAL_AMOUNT_KEY));
			return errorMsg;
		} else {
			String totalAmountStr = CommonUtils.toString(paramMap.get(Parameter.TOTAL_AMOUNT_KEY));
			try {
				new BigDecimal(totalAmountStr);
			} catch (Exception e) {
				logger.debug("参数格式不正确--------" + Parameter.TOTAL_AMOUNT_KEY + ":不是正确的数字格式");
				errorMsg = "参数格式不正确--------" + Parameter.TOTAL_AMOUNT_KEY + ":不是正确的数字格式";
				return errorMsg;
			}

		}
		// 数字格式校验
		if (paramMap.get(Parameter.TOTAL_COUNT_KEY) == null) {
			logger.debug("参数错误--------" + Parameter.TOTAL_COUNT_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.TOTAL_COUNT_KEY)));
			errorMsg = "参数错误--------" + Parameter.TOTAL_COUNT_KEY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.TOTAL_COUNT_KEY));
			return errorMsg;
		} else {
			String totalCountStr = CommonUtils.toString(paramMap.get(Parameter.TOTAL_COUNT_KEY));
			try {
				double totalCountDb = Double.parseDouble(totalCountStr);
				doubleIsInteger(totalCountDb);
			} catch (Exception e) {
				logger.debug("参数格式不正确，需要整数--------" + Parameter.TOTAL_COUNT_KEY + ":"
						+ CommonUtils.toString(paramMap.get(Parameter.TOTAL_COUNT_KEY)));
				errorMsg = "参数格式不正确，需要整数--------" + Parameter.TOTAL_COUNT_KEY + ":"
						+ CommonUtils.toString(paramMap.get(Parameter.TOTAL_COUNT_KEY));
				return errorMsg;
			}
		}
		
		if(paramMap.get(Parameter.BAG_CAPACITY) == null) {
			logger.debug("参数错误--------" + Parameter.BAG_CAPACITY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BAG_CAPACITY)));
			errorMsg = "参数错误--------" + Parameter.BAG_CAPACITY + ":"
					+ CommonUtils.toString(paramMap.get(Parameter.BAG_CAPACITY));
			return errorMsg;
		} 
		return null;
	}

	private ClearAddMoney setCreateAndUpdateData(ClearAddMoney clearAddMoney, User user, Date currentDateTime) {
		clearAddMoney.setCreateBy(user);
		clearAddMoney.setCreateDate(currentDateTime);
		clearAddMoney.setCreateName(user.getName());
		clearAddMoney.setUpdateBy(user);
		clearAddMoney.setUpdateDate(currentDateTime);
		clearAddMoney.setUpdateName(user.getName());
		clearAddMoney.setDelFlag(Constant.deleteFlag.Valid);
		return clearAddMoney;
	}

	private String setReturnMap(Map<String, Object> map, String serviceNo) {
		map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		return gson.toJson(map);
	}

	private void doubleIsInteger(double value) throws Exception {
		if (value % 1 != 0) {
			throw new Exception("非整数");
		}
	}
}
