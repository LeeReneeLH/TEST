package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service29
* <p>Description: 库间交接任务交接接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service29")
@Scope("singleton")
public class Service29 extends HardwardBaseService {
	
	@Autowired
	private AllocationService allocationService;
	
	/**
	 * @author wangbaozhong
	 * @version 2015年10月13日
	 * 
	 * 
	 * 29 库间交接任务交接接口
	 * @param paramMap
	 *            交接人员信息
	 * @return 更新结果信息
	 */
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		// 取得交接人员信息
		AllAllocateInfo handoverInfo = this.getHandoverInfoFromMap(paramMap);
		if (handoverInfo == null) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		} else {
			// 调拨状态判断
			if (AllocationConstant.Status.HandoverTodo.equals(handoverInfo.getStatus())) {
				// 更新交接人员信息
				//allocationService.updateHandoverInfo(handoverInfo);
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E23);
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			}
		}

		return gson.toJson(map);
	}
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年10月13日
	 * 
	 * 
	 * 从参数取得交接人员信息(库间交接任务查询接口)
	 * @param headInfo
	 *            交接人员信息
	 * @return AllAllocateInfo 对象
	 */
	@SuppressWarnings("unchecked")
	private AllAllocateInfo getHandoverInfoFromMap(Map<String, Object> headInfo) {

		// 移交人信息列表
		List<Map<String, Object>> handoverList = null;
		// 接收人信息列表
		List<Map<String, Object>> acceptList = null;
		// 授权人信息列表
		List<Map<String, Object>> managerList = null;
		String strSerialorderNo = "";
		// 取得流水号
		if (headInfo.get(Parameter.SERIALORDER_NO_KEY) != null
				&& StringUtils.isNotBlank(headInfo.get(Parameter.SERIALORDER_NO_KEY).toString())) {
			strSerialorderNo = headInfo.get(Parameter.SERIALORDER_NO_KEY).toString();
		} else {
			logger.debug("库间交接任务交接接口-------- 流水号:" + headInfo.get(Parameter.SERIALORDER_NO_KEY));
			return null;
		}
		// 库间交接信息
		AllAllocateInfo betweenAllocateInfo = allocationService.getAllocateBetween(strSerialorderNo);

		if (betweenAllocateInfo == null || betweenAllocateInfo.getAllAllocateItemList().size() == 0) {
			logger.debug("库间交接任务交接接口-------- 流水号:" + strSerialorderNo + "对应调拨信息不存在！");
			return null;
		}
		
		// 取得更新者ID
		if (headInfo.get(Parameter.USER_ID_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("库间交接任务交接接口-------- 更新者ID:" + headInfo.get(Parameter.USER_ID_KEY));
			return null;
		}
		// 取得更新者名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("库间交接任务交接接口-------- 更新者名:" + headInfo.get(Parameter.USER_NAME_KEY));
			return null;
		}
		User loginUser = new User();
		loginUser.setId(headInfo.get(Parameter.USER_ID_KEY).toString());
		loginUser.setName(headInfo.get(Parameter.USER_NAME_KEY).toString());
		betweenAllocateInfo.setLoginUser(loginUser);

		StringBuilder strBudName = new StringBuilder();
		StringBuilder strBudId = new StringBuilder();
		StringBuilder strBudHandType = new StringBuilder();

		// 移交人信息列表
		if (headInfo.get(Parameter.HANDOVER_LIST_KEY) != null) {
			handoverList = (List<Map<String, Object>>) headInfo.get(Parameter.HANDOVER_LIST_KEY);
			if (Collections3.isEmpty(handoverList)) {
				logger.debug("库间交接任务交接接口-------- 移交人信息列表:" + headInfo.get(Parameter.HANDOVER_LIST_KEY));
				return null;
			}
		}

		// 设定移交人信息
		int iIndex = 0;
		if (!Collections3.isEmpty(handoverList)) {
			
			for (Map<String, Object> map : handoverList) {
				if (map.get(Parameter.OPT_USER_ID_KEY) == null || StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 移交人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_NAME_KEY) == null || StringUtils.isBlank(map.get(Parameter.OPT_USER_NAME_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 移交人名:" + map.get(Parameter.OPT_USER_NAME_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_HAND_TYPE_KEY) == null || StringUtils.isBlank(map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 移交人员签收方式:" + map.get(Parameter.OPT_USER_HAND_TYPE_KEY));
					return null;
				}
				
				strBudId.append(map.get(Parameter.OPT_USER_ID_KEY).toString());
				strBudName.append(map.get(Parameter.OPT_USER_NAME_KEY).toString());
				strBudHandType.append(map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString());
				if (iIndex != (handoverList.size() - 1)) {
					strBudId.append(ExternalConstant.STR_COMMA);
					strBudName.append(ExternalConstant.STR_COMMA);
					strBudHandType.append(ExternalConstant.STR_COMMA);
				}
				iIndex++;
			}
//			betweenAllocateInfo.getAllHandoverInfo().setHandoverUserId(strBudId.toString());
//			betweenAllocateInfo.getAllHandoverInfo().setHandoverUserName(strBudName.toString());
//			betweenAllocateInfo.getAllHandoverInfo().setHandoverHandType(strBudHandType.toString());
		}

		// 接收人信息列表
		if (headInfo.get(Parameter.ACCEPT_LIST_KEY) != null) {
			acceptList = (List<Map<String, Object>>) headInfo.get(Parameter.ACCEPT_LIST_KEY);
			if (Collections3.isEmpty(acceptList)) {
				logger.debug("库间交接任务交接接口-------- 接收人信息列表:" + headInfo.get(Parameter.ACCEPT_LIST_KEY));
				return null;
			}
		}
		
		// 设定接收人信息
		if (!Collections3.isEmpty(acceptList)) {
			strBudName = new StringBuilder();
			strBudId = new StringBuilder();
			strBudHandType = new StringBuilder();
			iIndex = 0;
			for (Map<String, Object> map : acceptList) {
				if (map.get(Parameter.OPT_USER_ID_KEY) == null || StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 接收人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_NAME_KEY) == null || StringUtils.isBlank(map.get(Parameter.OPT_USER_NAME_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 接收人名:" + map.get(Parameter.OPT_USER_NAME_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_HAND_TYPE_KEY) == null || StringUtils.isBlank(map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 接收人员签收方式:" + map.get(Parameter.OPT_USER_HAND_TYPE_KEY));
					return null;
				}
				
				strBudId.append(map.get(Parameter.OPT_USER_ID_KEY).toString());
				strBudName.append(map.get(Parameter.OPT_USER_NAME_KEY).toString());
				strBudHandType.append(map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString());
				if (iIndex != (acceptList.size() - 1)) {
					strBudId.append(ExternalConstant.STR_COMMA);
					strBudName.append(ExternalConstant.STR_COMMA);
					strBudHandType.append(ExternalConstant.STR_COMMA);
				}
				iIndex++;
			}
//			betweenAllocateInfo.getAllHandoverInfo().setAcceptUserId(strBudId.toString());
//			betweenAllocateInfo.getAllHandoverInfo().setAcceptUserName(strBudName.toString());
//			betweenAllocateInfo.getAllHandoverInfo().setAcceptHandType(strBudHandType.toString());
		}

		// 如果移交人或接收人 只有一人录入指纹或都没有录入指纹 并且没有授权人员信息列表
		if ((handoverList == null || handoverList.size() < 2 
				|| acceptList == null || acceptList.size() < 2) 
				&& headInfo.get(Parameter.MANAGER_LIST_KEY) == null) {
			logger.debug("库间交接任务交接接口-------- 授权人信息列表:" + headInfo.get(Parameter.MANAGER_LIST_KEY));
			return null;
		}
		
		// 授权人信息列表
		if (headInfo.get(Parameter.MANAGER_LIST_KEY) != null) {
			managerList = (List<Map<String, Object>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
			if (Collections3.isEmpty(managerList)) {
				logger.debug("库间交接任务交接接口-------- 授权人信息列表:" + headInfo.get(Parameter.MANAGER_LIST_KEY));
				return null;
			}
		}
		// 设定授权人信息
		if (managerList != null) {
			strBudName = new StringBuilder();
			strBudId = new StringBuilder();
			strBudHandType = new StringBuilder();
			StringBuilder strBudReason = new StringBuilder();
			iIndex = 0;
			for (Map<String, Object> map : managerList) {
				if (map.get(Parameter.OPT_USER_ID_KEY) == null || StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 授权人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					return null;
				}
				if (map.get(Parameter.REASON_KEY) == null || StringUtils.isBlank(map.get(Parameter.REASON_KEY).toString())) {
					logger.debug("库间交接任务交接接口-------- 授权原因:" + map.get(Parameter.REASON_KEY));
					return null;
				}
				
				// 授权原因
				strBudReason.append(map.get(Parameter.REASON_KEY).toString());
				// 取得授权人用户名
				User user = UserUtils.getByLoginName(map.get(Parameter.OPT_USER_ID_KEY).toString());
				
				if (user == null) {
					logger.debug("库间交接任务交接接口-------- 授权人信息取得失败。授权人ID=" + map.get(Parameter.OPT_USER_ID_KEY).toString());
					return null;
				}
				// 授权人ID
				strBudId.append(user.getId());
				strBudName.append(user.getName());
				strBudHandType.append(AllocationConstant.HandoverType.SystemLogin);
				if (iIndex != (managerList.size() - 1)) {
					strBudId.append(ExternalConstant.STR_COMMA);
					strBudName.append(ExternalConstant.STR_COMMA);
					strBudHandType.append(ExternalConstant.STR_COMMA);
					strBudReason.append(ExternalConstant.STR_COMMA);
				}
				iIndex++;
			}
//			betweenAllocateInfo.getAllHandoverInfo().setManagerUserId(strBudId.toString());
//			betweenAllocateInfo.getAllHandoverInfo().setManagerUserName(strBudName.toString());
//			betweenAllocateInfo.getAllHandoverInfo().setHandoverHandType(strBudHandType.toString());
//			betweenAllocateInfo.getAllHandoverInfo().setManagerReason(strBudReason.toString());
		}

		
		// 交接表任务状态
		//betweenAllocateInfo.getAllHandoverInfo().setHandoverStatus(AllocationConstant.Handover.TaskFlag.Finished);
		return betweenAllocateInfo;
	}
}
