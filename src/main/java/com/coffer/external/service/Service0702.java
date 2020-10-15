package com.coffer.external.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.businesses.modules.collection.v03.entity.TaskDown;
import com.coffer.businesses.modules.collection.v03.service.TaskConfirmService;
import com.coffer.businesses.modules.collection.v03.service.TaskDownService;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
 * Title: Service0702
 * <p>
 * Description:任务分配登记接口
 * </p>
 * 
 * @author XL
 * @date 2019年7月3日
 */
@Component("Service0702")
@Scope("singleton")
public class Service0702 extends HardwardBaseService {

	@Autowired
	private TaskDownService taskDownService;

	@Autowired
	private TaskConfirmService taskConfirmService;

	@Autowired
	private OfficeService officeService;

	/**
	 * 任务分配登记接口
	 *
	 * @author XL
	 * @version 2019年7月3日
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	@SuppressWarnings("unchecked")
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		// 验证接口输入参数
		String paraCheckResult = checkParam(paramMap);
		// 验证失败的场合，退出
		if (paraCheckResult != null) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, paraCheckResult);
			return setReturnMap(map, serviceNo);
		}
		// 用户编号
		String userId = paramMap.get(Parameter.USER_ID_KEY).toString();
		// 机构编号
		String officeId = paramMap.get(Parameter.OFFICE_ID_KEY).toString();
		// 清分人编号
		String clearManId = paramMap.get(Parameter.CLEARMAN_ID_KEY).toString();
		// 款袋编号列表
		List<Map<String, String>> boxList = (List<Map<String, String>>) paramMap.get(Parameter.LIST_KEY);
		// 任务登记
		for (Map<String, String> boxMap : boxList) {
			String boxNo = boxMap.get(Parameter.BOX_NO_KEY);
			// 根据款袋编号获取待分配任务
			TaskDown taskDown = new TaskDown();
			// 清分机构
			taskDown.setOffice(officeService.get(officeId));
			// 款袋编号
			taskDown.setRfid(boxNo);
			// 设置状态（登记、确认）
			taskDown.setStatusList(Arrays
					.asList(new String[] { DoorOrderConstant.Status.REGISTER, DoorOrderConstant.Status.CONFIRM }));
			// 延长日
			taskDown.setExtendeDay(WeChatConstant.EXTENDE_DAY);
			// 任务列表
			List<TaskDown> taskDownList = taskDownService.findList(taskDown);
			// 验证任务是否存在
			if (Collections3.isEmpty(taskDownList)) {
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99,
						"款袋编号：" + boxNo + "的任务不存在！");
			} else if (taskDownList.size() != 1) {
				// throw new
				// BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99,
				// "款袋编号：" + boxNo + "的任务错误！");
			}
			taskDown = taskDownList.get(0);
			// 验证任务是否被他人分配
			if (DoorOrderConstant.AllotStatus.CONFIRMED.equals(taskDown.getAllotStatus())
					&& (!userId.equals(taskDown.getAllotManNo()))) {
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99,
						"款袋编号：" + boxNo + "的任务已被分配！");
			}
			// 日期
			Date date = new Date();
			// 分配日期
			taskDown.setAllotDate(date);
			// 分配人
			taskDown.setAllotManNo(userId);
			// 清分人员
			taskDown.setClearManNo(clearManId);
			// 登陆人
			User user = UserUtils.get(userId);
			// 清分人
			User clearMan = StoreCommonUtils.getEscortById(clearManId).getUser();
			// 任务更新人
			taskDown.setUpdateBy(user);
			taskDown.setUpdateName(user.getName());
			taskDown.setUpdateDate(date);
			taskDownService.taskAllot(taskDown);
			// 任务确认
			TaskConfirm taskConfirm = taskConfirmService.get(taskDown.getId());
			taskConfirmService.confirmForInterFace(taskConfirm, clearMan, date, officeService.get(officeId));
		}
		// 成功结果
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(map, serviceNo);
	}

	/**
	 * 验证接口输入参数
	 *
	 * @author XL
	 * @version 2019年7月3日
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String checkParam(Map<String, Object> paramMap) {
		String errorMsg = "";
		// 用户编号
		if (paramMap.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY)));
			// 参数异常
			errorMsg = "参数错误--------userId:" + CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
			return errorMsg;
		}
		// 机构编号
		if (paramMap.get(Parameter.OFFICE_ID_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)));
			// 参数异常
			errorMsg = "参数错误--------officeId:" + CommonUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
			return errorMsg;
		}
		// 清分人员编号
		if (paramMap.get(Parameter.CLEARMAN_ID_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.CLEARMAN_ID_KEY).toString())) {
			logger.debug("参数错误--------clearManId:" + CommonUtils.toString(paramMap.get(Parameter.CLEARMAN_ID_KEY)));
			// 参数异常
			errorMsg = "参数错误--------clearManId:" + CommonUtils.toString(paramMap.get(Parameter.CLEARMAN_ID_KEY));
			return errorMsg;
		}
		// 列表
		if (paramMap.get(Parameter.LIST_KEY) == null
				|| Collections3.isEmpty((List<Map<String, String>>) paramMap.get(Parameter.LIST_KEY))) {
			logger.debug("参数错误--------款袋编号列表不能为空");
			// 参数异常
			errorMsg = "参数错误--------款袋编号列表不能为空";
			return errorMsg;
		} else {
			List<Map<String, String>> list = (List<Map<String, String>>) paramMap.get(Parameter.LIST_KEY);
			for (Map<String, String> boxMap : list) {
				if (boxMap == null || StringUtils.isBlank(boxMap.get(Parameter.BOX_NO_KEY))) {
					logger.debug("参数错误--------款袋编号不能为空");
					// 参数异常
					errorMsg = "参数错误--------款袋编号不能为空";
					return errorMsg;
				}
			}
		}
		return null;
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @param map
	 * @param serviceNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo) {
		map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		return gson.toJson(map);
	}
}
