package com.coffer.external.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.service.ClTaskMainService;
import com.coffer.businesses.modules.clear.v03.service.ClTaskRecoveryService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0280
 * <p>
 * Description: 当前用户为现钞清分中心机构，对清分/复点任务分配和回收接口
 * </p>
 * @author xiaoliang
 * @date 2017年9月19日
 */
@Component("Service0280")
@Scope("singleton")
public class Service0280 extends HardwardBaseService {

	@Autowired
	private ClTaskMainService clTaskMainService;
	@Autowired
	private ClTaskRecoveryService clTaskRecoveryService;
	
	/**
	 * 对清分/复点任务分配和回收
	 * 
	 * @author xiaoliang
	 * @version 2017年9月19日
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> resultmap = Maps.newHashMap();
		//版本号
		String versionNo = (String)paramMap.get(Parameter.VERSION_NO_KEY);
		//服务代码 
		String serviceNo = (String)paramMap.get(Parameter.SERVICE_NO_KEY);
		//验证接口输入参数
		String paraCheckResult = checkClTaskMainParam(paramMap, resultmap);
		// 验证失败的场合，退出
		if(Constant.FAILED.equals(paraCheckResult)){
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return setReturnMap(resultmap, serviceNo,versionNo);
		}
		// 验证清分人员
		if (Constant.FAILED.equals(checkClearEmp(paramMap, resultmap))) {
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			return setReturnMap(resultmap, serviceNo, versionNo);
		}
		// 设置参数
		ClTaskMain clTaskMain = new ClTaskMain();
		// 交接人
		clTaskMain.setJoinManNo(StoreCommonUtils
				.findByUserId(paramMap.get(ClearConstant.ClearingParamKey.TRANS_ID_KEY).toString()).getId());
		// 设置登录人
		clTaskMain.setLoginUser(UserUtils.get(paramMap.get(Parameter.USER_ID_KEY).toString()));
		// 业务类型
		clTaskMain.setBusType(paramMap.get(Parameter.BUSINESS_TYPE_KEY).toString());
		// 任务类型
		clTaskMain.setTaskType(
				paramMap.get(ClearConstant.ClearingParamKey.TASK_TYPE_KEY).toString());
		// 计划类型
		clTaskMain.setPlanType(paramMap.get(ClearConstant.ClearingParamKey.PLAN_TYPE_KEY).toString());
		// 面值
		clTaskMain.setDenomination(paramMap.get(Parameter.DENOMINATION_KEY).toString());
		// 数量
		clTaskMain.setTotalCount(Long.parseLong(paramMap.get(ClearConstant.ClearingParamKey.TASK_NUM_KEY).toString()));
		// 设置物品
		clTaskMain.setCurrency(Constant.Currency.RMB);
		clTaskMain.setClassification(Constant.MoneyType.CIRCULATION_MONEY);
		clTaskMain.setCash(Constant.CashType.PAPER);
		clTaskMain.setUnit(Constant.Unit.bundle);
		clTaskMain.setSets(Constant.SetType.SET_5);
		// 明细设置
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		ClTaskDetail clTaskDetail = new ClTaskDetail();
		// 设置明细ID
		clTaskDetail.setDetailId(IdGen.uuid());
		// 设置员工，职位编号
		clTaskDetail.setEmpNo(paramMap.get(ClearConstant.ClearingParamKey.CLEARING_ID_KEY).toString());
		clTaskDetail.setEmpName(paramMap.get(ClearConstant.ClearingParamKey.CLEARING_NAME_KEY).toString());
		clTaskDetail.setOfficeNo(
				UserUtils.get(paramMap.get(ClearConstant.ClearingParamKey.CLEARING_ID_KEY).toString()).getUserType());
		// 设置明细捆数
		clTaskDetail.setTotalCount(clTaskMain.getTotalCount());
		// 工位类型
		clTaskDetail
				.setWorkingPositionType(paramMap.get(ClearConstant.ClearingParamKey.WORKING_POSITION_TYPE).toString());
		// 设置金额
		clTaskDetailList.add(clTaskDetail);
		// 设置明细
		clTaskMain.setClTaskDetailList(clTaskDetailList);
		// 任务分配
		if (clTaskMain.getTaskType().equals(ClearConstant.TaskType.TASK_DISTRIBUTION)) {
			// 授权标识
			String authorizeFlag = paramMap.get(ClearConstant.ClearingParamKey.AUTHORIZE_FLAG_KEY).toString();
			// 正常清分，未授权，捆数不足，返回内容
			if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())
					&& authorizeFlag.equals(ClearConstant.ClearingParamKey.AUTHORIZE_FALSE)
					&& !(clTaskMainService.checkCountForService(clTaskMain))) {
				logger.debug("--------分配捆数不足，是否继续");
				resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
				resultmap.put(ClearConstant.ClearingParamKey.ERROR_FLAG_KEY,
						ClearConstant.ClearingParamKey.TASK_NUM_ERROR);
				return setReturnMap(resultmap, serviceNo, versionNo);
			}
			clTaskMainService.save(clTaskMain);
		}
		// 任务回收
		if (clTaskMain.getTaskType().equals(ClearConstant.TaskType.TASK_RECOVERY)) {
			clTaskRecoveryService.save(clTaskMain);
		}
		resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(resultmap, serviceNo, versionNo);
	}
	
	/**
	 * 验证 输入参数
	 * 
	 * @author xiaoliang
	 * @version 2017年9月19日
	 * @param paramMap
	 * @param resultmap
	 * @return 处理结果
	 */
	private String checkClTaskMainParam(Map<String, Object> paramMap, Map<String, Object> resultmap) {
		// 机构编号ID
		if (paramMap.get(Parameter.OFFICE_ID_KEY) == null || StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("参数错误--------officeId为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId为空");
			return Constant.FAILED;
		}
		// 清分人员ID
		if (paramMap.get(ClearConstant.ClearingParamKey.CLEARING_ID_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.CLEARING_ID_KEY).toString())) {
			logger.debug("参数错误--------clearingId为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------clearingId为空");
			return Constant.FAILED;
		}
		// 清分人员名字
		if (paramMap.get(ClearConstant.ClearingParamKey.CLEARING_NAME_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.CLEARING_NAME_KEY).toString())) {
			logger.debug("参数错误--------clearingName为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------clearingName为空");
			return Constant.FAILED;
		}
		// 用户编号(交接人员)
		if (paramMap.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("参数错误--------userId为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------userId为空");
			return Constant.FAILED;
		}
		// 用户名称
		if (paramMap.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("参数错误--------userName为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------userName为空");
			return Constant.FAILED;
		}
		// 业务类型
		if (paramMap.get(Parameter.BUSINESS_TYPE_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.BUSINESS_TYPE_KEY).toString())) {
			logger.debug("参数错误--------businessType为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------businessType为空");
			return Constant.FAILED;
		}
		// 任务类型
		if (paramMap.get(ClearConstant.ClearingParamKey.TASK_TYPE_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.TASK_TYPE_KEY).toString())) {
			logger.debug("参数错误--------taskType为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------taskType为空");
			return Constant.FAILED;
		}
		// 计划类型
		if (paramMap.get(ClearConstant.ClearingParamKey.PLAN_TYPE_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.PLAN_TYPE_KEY).toString())) {
			logger.debug("参数错误--------planType为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------planType为空");
			return Constant.FAILED;
		}
		// 面值
		if (paramMap.get(Parameter.DENOMINATION_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.DENOMINATION_KEY).toString())) {
			logger.debug("参数错误--------denomination为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------denomination为空");
			return Constant.FAILED;
		}
		// 数量
		if (paramMap.get(ClearConstant.ClearingParamKey.TASK_NUM_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.TASK_NUM_KEY).toString())) {
			logger.debug("参数错误--------taskNum为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------taskNum为空");
			return Constant.FAILED;
		}
		// 工位类型
		if (paramMap.get(ClearConstant.ClearingParamKey.WORKING_POSITION_TYPE) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.WORKING_POSITION_TYPE).toString())) {
			logger.debug("参数错误--------workingPositionType为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------workingPositionType为空");
			return Constant.FAILED;
		}
		// 授权标识
		if (paramMap.get(ClearConstant.ClearingParamKey.AUTHORIZE_FLAG_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.AUTHORIZE_FLAG_KEY).toString())) {
			logger.debug("参数错误--------authorizeFlag为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------authorizeFlag为空");
			return Constant.FAILED;
		}
		// 交接人员ID
		if (paramMap.get(ClearConstant.ClearingParamKey.TRANS_ID_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.TRANS_ID_KEY).toString())) {
			logger.debug("参数错误--------transId为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------transId为空");
			return Constant.FAILED;
		}
		// 交接人员姓名
		if (paramMap.get(ClearConstant.ClearingParamKey.TRANS_NAME_KEY) == null
				|| StringUtils.isBlank(paramMap.get(ClearConstant.ClearingParamKey.TRANS_NAME_KEY).toString())) {
			logger.debug("参数错误--------transName为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------transName为空");
			return Constant.FAILED;
		}
		return Constant.SUCCESS;
	}

	/**
	 * 验证 清分人员
	 * 
	 * @author xiaoliang
	 * @version 2017年9月19日
	 * @param paramMap
	 * @param resultmap
	 * @return 处理结果
	 */
	private String checkClearEmp(Map<String, Object> paramMap, Map<String, Object> resultmap) {
		User user = UserUtils.get(paramMap.get(ClearConstant.ClearingParamKey.CLEARING_ID_KEY).toString());
		List<String> userTypeList = Arrays.asList(Global.getConfig(Constant.CLEAR_MANAGE_TYPE).split(";"));
		if (!userTypeList.contains(user.getUserType())) {
			logger.debug("--------" + user.getName() + " 不是清分人员");
			resultmap.put(Parameter.ERROR_MSG_KEY, "--------" + user.getName() + "不是清分人员");
			return Constant.FAILED;
		}
		return Constant.SUCCESS;
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @author xiaoliang
	 * @version 2017年9月19日
	 * @param map
	 * @param serviceNo
	 * @param versionNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo,String versionNo){
		map.put(Parameter.VERSION_NO_KEY, versionNo);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		return gson.toJson(map); 
	}
	
	
}




