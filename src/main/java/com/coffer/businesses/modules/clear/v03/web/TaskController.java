package com.coffer.businesses.modules.clear.v03.web;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.service.ClTaskMainService;
import com.coffer.businesses.modules.clear.v03.service.ClTaskRecoveryService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 清分、复点任务分配和回收Controller
 * 
 * @author wzj
 * @version 2017-12-29
 */
@Controller
@RequestMapping(value = "${adminPath}/task")
public class TaskController extends BaseController {

	@Autowired
	private ClTaskMainService clTaskMainService;
	@Autowired
	private ClTaskRecoveryService clTaskRecoveryService;
	@Autowired
	private StoEscortInfoService stoEscortInfoService;
	/**
	 * Json实例对象
	 */
	protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
			.enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
			.serializeNulls().setDateFormat(Constant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS)// 时间转化为特定格式
			// .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//
			// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
			.setPrettyPrinting() // 对json结果格式化.
			// .setVersion(1.0)
			// //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
			// @Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
			// @Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
			.create();

	@RequestMapping(value = "checkType")
	@ResponseBody
	public String checkType(@RequestParam String params) throws UnsupportedEncodingException {
		// Map<String, Object> paramMap = dates();
		/*将json转成Map 修改人:sg 修改日期:2017-12-27 begin*/
		@SuppressWarnings("unchecked")
		Map<String, Object> requestMap = gson.fromJson(params, Map.class);
		String param = StringUtils.toString(requestMap.get("param"));
		/*end*/
		String returnDate = "";
		if (param.equals("saveManualTaskDetailOut")) {
			// 复点任务分配
			returnDate = saveManualTaskDetailOut(requestMap);
		} else if (param.equals("saveSortingDetailOut")) {
			// 清分任务分配
			returnDate = saveSortingDetailOut(requestMap);
		} else if (param.equals("saveRecoverDetailOut")) {
			// 清分任务回收
			returnDate = saveRecoverDetailOut(requestMap);
		} else if (param.equals("saveRecoverManualDetailOut")) {
			// 复点任务回收
			returnDate = saveRecoverManualDetailOut(requestMap);
		}
		return returnDate;
	}

	/**
	 * 复点任务分配
	 * 
	 * @author wzj
	 * @version 2017年12月19日
	 * @param paramMap
	 * @param resultmap
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	public String saveManualTaskDetailOut(Map<String, Object> paramMap) {
		Map<String, Object> resultmap = Maps.newHashMap();

		// 验证接口输入参数
		String paraCheckResult = checkClTaskMainParam(paramMap, resultmap);
		// 验证失败的场合，退出
		if (Constant.FAILED.equals(paraCheckResult)) {
			resultmap.put("statusText", "系统异常");
			return gson.toJson(resultmap);
		}
		// 设置参数
		ClTaskMain clTaskMain = new ClTaskMain();

		// 复点
		clTaskMain.setBusType("08");
		// 任务分配
		clTaskMain.setTaskType("01");
		// 当前登录机构
		clTaskMain.setOffice(UserUtils.getUser().getOffice());
		// 设置物品
		clTaskMain.setCurrency(Constant.Currency.RMB);
		clTaskMain.setClassification(Constant.MoneyType.CIRCULATION_MONEY);
		clTaskMain.setCash(Constant.CashType.PAPER);
		clTaskMain.setUnit(Constant.Unit.bundle);
		clTaskMain.setSets(Constant.SetType.SET_5);
		// 设置当前登录用户
		clTaskMain.setLoginUser(UserUtils.getUser());
		// 任务计划
		clTaskMain.setPlanType(StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("plan_type")));
		// 交接人编号
		clTaskMain.setJoinManNo(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("id")));
		// 交接人姓名
		clTaskMain.setJoinManName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("text")));
		// 操作人编号
		clTaskMain.setOperatorBy(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("id")));
		// 操作人姓名
		clTaskMain.setOperatorName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("text")));
		// 券别编号
		clTaskMain.setDenomination(
				StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("valueKey")));

		// 明细设置
		long counts = 0;
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		for (Map<String, Object> maps : (List<Map<String, Object>>) paramMap.get("userTaskList")) {
			// 设置明细ID
			ClTaskDetail clTaskDetail = new ClTaskDetail();
			clTaskDetail.setDetailId(IdGen.uuid());
			clTaskDetail.setEmpNo(maps.get("user_code").toString());
			clTaskDetail.setEmpName(maps.get("user_name").toString());
			clTaskDetail.setWorkingPositionType(maps.get("station_id").toString());
			clTaskDetail.setTotalCount(Long.parseLong((maps.get("count").toString())));
			clTaskDetailList.add(clTaskDetail);
			counts = counts + Long.parseLong(maps.get("count").toString());
		}
		// 设置明细
		clTaskMain.setClTaskDetailList(clTaskDetailList);
		clTaskMain.setTotalCount(counts);
		// 任务分配

		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())
				&& !(clTaskMainService.checkCountForService(clTaskMain))) {
			logger.debug("--------分配捆数不足，是否继续");
			resultmap.put(Parameter.STATUS_KEY, "0");
			resultmap.put("statusText", "分配捆数不足，是否继续");
			return gson.toJson(resultmap);
		}
		clTaskMainService.saveDate(clTaskMain);
		resultmap.put(Parameter.STATUS_KEY, "1");
		resultmap.put("statusText", "保存成功!");
		return gson.toJson(resultmap);
	}

	/**
	 * 清分任务分配
	 * 
	 * @author wzj
	 * @version 2017年12月19日
	 * @param paramMap
	 * @param resultmap
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	public String saveSortingDetailOut(Map<String, Object> paramMap) {

		Map<String, Object> resultmap = Maps.newHashMap();
		// 验证接口输入参数
		String paraCheckResult = checkClTaskMainParam(paramMap, resultmap);
		// 验证失败的场合，退出
		if (Constant.FAILED.equals(paraCheckResult)) {
			resultmap.put("statusText", "系统异常");
			return gson.toJson(resultmap);
		}
		// 设置参数
		ClTaskMain clTaskMain = new ClTaskMain();

		// 清分
		clTaskMain.setBusType("09");
		// 任务分配
		clTaskMain.setTaskType("01");
		// 当前登录机构
		clTaskMain.setOffice(UserUtils.getUser().getOffice());
		// 设置物品
		clTaskMain.setCurrency(Constant.Currency.RMB);
		clTaskMain.setClassification(Constant.MoneyType.CIRCULATION_MONEY);
		clTaskMain.setCash(Constant.CashType.PAPER);
		clTaskMain.setUnit(Constant.Unit.bundle);
		clTaskMain.setSets(Constant.SetType.SET_5);
		// 设置当前登录用户
		clTaskMain.setLoginUser(UserUtils.getUser());
		// 任务计划
		clTaskMain.setPlanType(StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("plan_type")));
		// 交接人编号
		clTaskMain.setJoinManNo(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("id")));
		// 交接人姓名
		clTaskMain.setJoinManName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("text")));
		// 操作人编号
		clTaskMain.setOperatorBy(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("id")));
		// 操作人姓名
		clTaskMain.setOperatorName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("text")));
		// 券别编号
		clTaskMain.setDenomination(
				StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("valueKey")));

		// 明细设置
		long counts = 0;
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		for (Map<String, Object> maps : (List<Map<String, Object>>) paramMap.get("userTaskList")) {
			// 设置明细ID
			ClTaskDetail clTaskDetail = new ClTaskDetail();
			clTaskDetail.setDetailId(IdGen.uuid());
			clTaskDetail.setEmpNo(maps.get("user_code").toString());
			clTaskDetail.setEmpName(maps.get("user_name").toString());
			clTaskDetail.setWorkingPositionType(maps.get("station_id").toString());
			clTaskDetail.setTotalCount(Long.parseLong((maps.get("count").toString())));
			clTaskDetailList.add(clTaskDetail);
			counts = counts + Long.parseLong(maps.get("count").toString());
		}
		// 设置明细
		clTaskMain.setClTaskDetailList(clTaskDetailList);
		clTaskMain.setTotalCount(counts);
		// 任务分配

		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())
				&& !(clTaskMainService.checkCountForService(clTaskMain))) {
			logger.debug("--------分配捆数不足，是否继续");
			resultmap.put(Parameter.STATUS_KEY, "0");
			resultmap.put("statusText", "分配捆数不足，是否继续");
			return gson.toJson(resultmap);
		}
		clTaskMainService.saveDate(clTaskMain);
		resultmap.put(Parameter.STATUS_KEY, "1");
		resultmap.put("statusText", "保存成功!");
		return gson.toJson(resultmap);
	}

	/**
	 * 清分任务回收
	 * 
	 * @author wzj
	 * @version 2017年12月19日
	 * @param paramMap
	 * @param resultmap
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	public String saveRecoverDetailOut(Map<String, Object> paramMap) {

		Map<String, Object> resultmap = Maps.newHashMap();
		// 验证接口输入参数
		String paraCheckResult = checkClTaskMainParam(paramMap, resultmap);
		// 验证失败的场合，退出
		if (Constant.FAILED.equals(paraCheckResult)) {
			resultmap.put("statusText", "系统异常");
			return gson.toJson(resultmap);
		}
		// 设置参数
		ClTaskMain clTaskMain = new ClTaskMain();

		// 复点
		clTaskMain.setBusType("09");
		// 任务回收
		clTaskMain.setTaskType("02");
		// 当前登录机构
		clTaskMain.setOffice(UserUtils.getUser().getOffice());
		// 设置物品
		clTaskMain.setCurrency(Constant.Currency.RMB);
		clTaskMain.setClassification(Constant.MoneyType.CIRCULATION_MONEY);
		clTaskMain.setCash(Constant.CashType.PAPER);
		clTaskMain.setUnit(Constant.Unit.bundle);
		clTaskMain.setSets(Constant.SetType.SET_5);
		// 设置当前登录用户
		clTaskMain.setLoginUser(UserUtils.getUser());
		// 任务计划
		clTaskMain.setPlanType(StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("plan_type")));
		// 交接人编号
		clTaskMain.setJoinManNo(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("id")));
		// 交接人姓名
		clTaskMain.setJoinManName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("text")));
		// 操作人编号
		clTaskMain.setOperatorBy(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("id")));
		// 操作人姓名
		clTaskMain.setOperatorName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("text")));
		// 券别编号
		clTaskMain.setDenomination(
				StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("valueKey")));

		// 明细设置
		long counts = 0;
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		for (Map<String, Object> maps : (List<Map<String, Object>>) paramMap.get("userTaskList")) {
			// 设置明细ID
			ClTaskDetail clTaskDetail = new ClTaskDetail();
			clTaskDetail.setDetailId(IdGen.uuid());
			clTaskDetail.setEmpNo(maps.get("user_code").toString());
			clTaskDetail.setEmpName(maps.get("user_name").toString());
			clTaskDetail.setWorkingPositionType(maps.get("station_id").toString());
			clTaskDetail.setTotalCount(Long.parseLong((maps.get("count").toString())));
			clTaskDetailList.add(clTaskDetail);
			counts = counts + Long.parseLong(maps.get("count").toString());
		}
		// 设置明细
		clTaskMain.setClTaskDetailList(clTaskDetailList);
		clTaskMain.setTotalCount(counts);
		// 任务分配

		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())
				&& !(clTaskMainService.checkCountForService(clTaskMain))) {
			logger.debug("--------分配捆数不足，是否继续");
			resultmap.put(Parameter.STATUS_KEY, "0");
			resultmap.put("statusText", "分配捆数不足，是否继续");
			return gson.toJson(resultmap);
		}
		clTaskRecoveryService.saveDate(clTaskMain);
		resultmap.put(Parameter.STATUS_KEY, "1");
		resultmap.put("statusText", "保存成功!");
		return gson.toJson(resultmap);
	}

	/**
	 * 复点任务回收
	 * 
	 * @author wzj
	 * @version 2017年12月19日
	 * @param paramMap
	 * @param resultmap
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")

	public String saveRecoverManualDetailOut(Map<String, Object> paramMap) {

		Map<String, Object> resultmap = Maps.newHashMap();
		// 验证接口输入参数
		String paraCheckResult = checkClTaskMainParam(paramMap, resultmap);
		// 验证失败的场合，退出
		if (Constant.FAILED.equals(paraCheckResult)) {
			resultmap.put("statusText", "系统异常");
			return gson.toJson(resultmap);
		}
		// 设置参数
		ClTaskMain clTaskMain = new ClTaskMain();

		// 复点
		clTaskMain.setBusType("08");
		// 任务回收
		clTaskMain.setTaskType("02");
		// 当前登录机构
		clTaskMain.setOffice(UserUtils.getUser().getOffice());
		// 设置物品
		clTaskMain.setCurrency(Constant.Currency.RMB);
		clTaskMain.setClassification(Constant.MoneyType.CIRCULATION_MONEY);
		clTaskMain.setCash(Constant.CashType.PAPER);
		clTaskMain.setUnit(Constant.Unit.bundle);
		clTaskMain.setSets(Constant.SetType.SET_5);
		// 设置当前登录用户
		clTaskMain.setLoginUser(UserUtils.getUser());
		// 任务计划
		clTaskMain.setPlanType(StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("plan_type")));
		// 交接人编号
		clTaskMain.setJoinManNo(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("id")));
		// 交接人姓名
		clTaskMain.setJoinManName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS3"))).get("text")));
		// 操作人编号
		clTaskMain.setOperatorBy(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("id")));
		// 操作人姓名
		clTaskMain.setOperatorName(StringUtils.toString(
				((Map<String, Object>) (((Map<String, Object>) paramMap.get("dataList")).get("cS1"))).get("text")));
		// 券别编号
		clTaskMain.setDenomination(
				StringUtils.toString(((Map<String, Object>) paramMap.get("dataList")).get("valueKey")));

		// 明细设置
		long counts = 0;
		List<ClTaskDetail> clTaskDetailList = Lists.newArrayList();
		for (Map<String, Object> maps : (List<Map<String, Object>>) paramMap.get("userTaskList")) {
			// 设置明细ID
			ClTaskDetail clTaskDetail = new ClTaskDetail();
			clTaskDetail.setDetailId(IdGen.uuid());
			clTaskDetail.setEmpNo(maps.get("user_code").toString());
			clTaskDetail.setEmpName(maps.get("user_name").toString());
			clTaskDetail.setWorkingPositionType(maps.get("station_id").toString());
			clTaskDetail.setTotalCount(Long.parseLong((maps.get("count").toString())));
			clTaskDetailList.add(clTaskDetail);
			counts = counts + Long.parseLong(maps.get("count").toString());
		}
		// 设置明细
		clTaskMain.setClTaskDetailList(clTaskDetailList);
		clTaskMain.setTotalCount(counts);
		// 任务分配

		if (ClearConstant.PlanType.NORMAL_CLEAR.equals(clTaskMain.getPlanType())
				&& !(clTaskMainService.checkCountForService(clTaskMain))) {
			logger.debug("--------分配捆数不足，是否继续");
			resultmap.put(Parameter.STATUS_KEY, "0");
			resultmap.put("statusText", "分配捆数不足，是否继续");
			return gson.toJson(resultmap);
		}
		clTaskRecoveryService.saveDate(clTaskMain);
		resultmap.put(Parameter.STATUS_KEY, "1");
		resultmap.put("statusText", "保存成功!");
		return gson.toJson(resultmap);
	}

	/**
	 * 验证 输入参数
	 * 
	 * @author wzj
	 * @version 2017年12月19日
	 * @param paramMap
	 * @param resultmap
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	private String checkClTaskMainParam(Map<String, Object> paramMap, Map<String, Object> resultmap) {

		// dataList
		Map<String, Object> dataListMap = (Map<String, Object>) paramMap.get("dataList");
		if (dataListMap == null) {
			logger.debug("参数错误--------dataList:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------dataList为空");
			return Constant.FAILED;
		}
		// 任务计划
		String planType = StringUtils.toString(dataListMap.get("plan_type"));
		if (!(ClearConstant.PlanType.NORMAL_CLEAR.equals(planType)
				|| ClearConstant.PlanType.REPEAT_CLEAR.equals(planType)
				|| ClearConstant.PlanType.CHECK_CLEAR.equals(planType))) {
			logger.debug("参数错误--------planType:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------planType不是正常清分，重复清分，抽查中的一种");
			return Constant.FAILED;
		}
		// 交接人员编号
		String joinManNo = StringUtils.toString(dataListMap.get("join_man_no"));
		if (StringUtils.isBlank(joinManNo)) {
			logger.debug("参数错误--------joinManNo:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------joinManNo为空");
			return Constant.FAILED;
		}
		// 交接人员下拉集合
		Map<String, Object> cS3 = (Map<String, Object>) dataListMap.get("cS3");
		if (cS3 == null) {
			logger.debug("参数错误--------cS3:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------cS3为空");
			return Constant.FAILED;
		} else {
			StoEscortInfo stoEscortIn = stoEscortInfoService.get(joinManNo);
			if (stoEscortIn == null || Constant.deleteFlag.Invalid.equals(stoEscortIn.getDelFlag())
					|| StringUtils.isBlank(stoEscortIn.getEscortName())) {
				logger.debug("参数错误--------joinManNo:");
				resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------joinManNo");
				return Constant.FAILED;
			}
			if (!(joinManNo.equals(cS3.get("id")) && stoEscortIn.getEscortName().equals(cS3.get("text")))) {
				logger.debug("参数错误--------cS3:");
				resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------cS3中的数据与表中不同");
				return Constant.FAILED;
			}
		}
		// 操作人（确认人）
		String operatorNo = StringUtils.toString(dataListMap.get("operator_no"));
		if (StringUtils.isBlank(operatorNo)) {
			logger.debug("参数错误--------operatorNo:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------operatorNo为空");
			return Constant.FAILED;
		}
		// 交接人员下拉集合
		Map<String, Object> cS1 = (Map<String, Object>) dataListMap.get("cS1");
		if (cS1 == null) {
			logger.debug("参数错误--------cS1:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------cS1为空");
			return Constant.FAILED;
		} else {
			StoEscortInfo stoEscortIn = stoEscortInfoService.get(operatorNo);
			if (stoEscortIn == null || Constant.deleteFlag.Invalid.equals(stoEscortIn.getDelFlag())
					|| StringUtils.isBlank(stoEscortIn.getEscortName())) {
				logger.debug("参数错误--------operatorNo:");
				resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------operatorNo");
				return Constant.FAILED;
			}
			if (!(operatorNo.equals(cS1.get("id")) && stoEscortIn.getEscortName().equals(cS1.get("text")))) {
				logger.debug("参数错误--------cS1:");
				resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------cS1中的数据与表中不同");
				return Constant.FAILED;
			}
		}
		// 券别编号
		String valueKey = StringUtils.toString(dataListMap.get("valueKey"));
		if (StringUtils.isBlank(valueKey)) {
			logger.debug("参数错误--------valueKey:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------valueKey为空");
			return Constant.FAILED;
		}
		// 清分人员相关LIST集合
		List<Map<String, Object>> userTaskList = (List<Map<String, Object>>) paramMap.get("userTaskList");
		if (Collections3.isEmpty(userTaskList)) {
			logger.debug("参数错误--------userTaskList:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------userTaskList为空");
			return Constant.FAILED;
		}
		List<String> errorList = Lists.newArrayList();
		List<String> errorsList = Lists.newArrayList();
		for (Map<String, Object> userTask : userTaskList) {
			// 清分人员编号
			String userCode = StringUtils.toString(userTask.get("user_code"));
			// 清分人员姓名
			String userName = StringUtils.toString(userTask.get("user_name"));
			// 工位类型编号
			String stationId = StringUtils.toString(userTask.get("station_id"));
			// 分配捆数
			String count = StringUtils.toString(userTask.get("count"));
			if (StringUtils.isBlank(userCode) || StringUtils.isBlank(userName) || StringUtils.isBlank(stationId)
					|| StringUtils.isBlank(count)) {
				logger.debug("参数错误--------userTaskList:");
				resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------userTaskList存在空数据");
				return Constant.FAILED;
			}
			// clearingGroupService.getClGroupDetailById(userCode);
			User clearingGroupDetail = UserUtils.get(userCode);
			if (clearingGroupDetail == null || "1".equals(clearingGroupDetail.getDelFlag())
					|| StringUtils.isBlank(clearingGroupDetail.getName())) {
				errorList.add(userCode);
			} else if (!clearingGroupDetail.getName().equals(userName)) {
				errorsList.add(userCode);

			}
		}
		if (!Collections3.isEmpty(errorList)) {
			logger.debug("参数错误--------userTaskList:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------没有清分人员编号为" + Collections3.convertToString(errorList, ",") + "的数据");
			return Constant.FAILED;
		}
		if (!Collections3.isEmpty(errorsList)) {
			logger.debug("参数错误--------userTaskList:");
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			resultmap.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------清分人员编号为" + Collections3.convertToString(errorsList, ",") + "的清分人员姓名与表中不同");
			return Constant.FAILED;
		}
		return Constant.SUCCESS;
	}

	// 测试数据后期删除
	// public Map<String, Object> dates() {
	// Map<String, Object> result = Maps.newHashMap();
	// Map<String, Object> resultmap = Maps.newHashMap();
	// Map<String, Object> cs3 = Maps.newHashMap();
	// cs3.put("id", "20171211102609935");
	// cs3.put("text", "张杰");
	// Map<String, Object> cs1 = Maps.newHashMap();
	// cs1.put("id", "20171211160238090");
	// cs1.put("text", "余锦渡");
	// resultmap.put("plan_type", "01");
	// resultmap.put("join_man_no", "20171211102609935");
	// resultmap.put("cS3", cs3);
	// resultmap.put("operator_no", "20171211160238090");
	// resultmap.put("cS1", cs1);
	// resultmap.put("valueKey", "10");
	// List<Object> userTaskList = Lists.newArrayList();
	// Map<String, Object> user1 = Maps.newHashMap();
	// user1.put("user_code", "f84a7ec18b544b56a1e64515e7094087");
	// user1.put("user_name", "昌旦");
	// user1.put("station_id", "01");
	// user1.put("count", "1");
	// Map<String, Object> user2 = Maps.newHashMap();
	// user2.put("user_code", "1633fef395bf48fa83e7cf21a528416f");
	// user2.put("user_name", "付尧楚浛");
	// user2.put("station_id", "01");
	// user2.put("count", "2");
	// userTaskList.add(user1);
	// userTaskList.add(user2);
	// result.put("dataList", resultmap);
	// result.put("userTaskList", userTaskList);
	//
	// return result;
	// }
}