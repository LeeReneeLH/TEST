package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
 * Title: Service26
 * <p>
 * Description: 库外交接任务交接接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service26")
@Scope("singleton")
public class Service26 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 * 
	 *          26 库外交接任务交接接口
	 * @param paramMap
	 *            交接人员信息
	 * @return 更新结果信息
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得交接人员信息
		AllAllocateInfo handoverInfo = this.getOutHandoverInfoFromMap(paramMap, map);
		if (handoverInfo == null) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(map);
		} else {
			// update start by wangbaozhong 2017/11/16 根据开关 判断是否验证交接人员与线路绑定
			String checkBinding = Global.getConfig("handover.route.escort.banding.check");
			if (Constant.StrBooleanVal.TRUE_VALUE.equals(checkBinding)
					&& Collections3.isEmpty((List<Map<String, Object>>) paramMap.get(Parameter.MANAGER_LIST_KEY))
					&& this.checkEscortInfo(paramMap, map) == false) {
				// 判断押运人员是否归属当前线路
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				// [交接失败]：线路信息不存在或当前路线押运人员信息已变更！
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E36);
				return gson.toJson(map);
			}
			// update end by wangbaozhong 2017/11/16 根据开关 判断是否验证交接人员与线路绑定
			// 更新交接人员信息
			allocationService.updateOutHandoverInfo(handoverInfo);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		}
		return gson.toJson(map);
	}

	/**
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 * 
	 *          从参数取得交接人员信息(库外交接任务交接接口)
	 * @param headInfo
	 *            交接人员信息
	 * @return AllAllocateInfo 对象
	 */
	@SuppressWarnings("unchecked")
	private AllAllocateInfo getOutHandoverInfoFromMap(Map<String, Object> headInfo, Map<String, Object> msgMap) {

		// 移交人信息列表
		List<Map<String, Object>> handoverList = null;
		// 接收人信息列表
		List<Map<String, Object>> acceptList = null;
		// 授权人信息列表
		List<Map<String, Object>> managerList = null;
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		// 设定移交人信息
		AllHandoverInfo handoverInfo = new AllHandoverInfo();
		AllHandoverDetail handoverDetail = null;
		// 取得交接ID
		if (headInfo.get(Parameter.HANDOVER_ID_KEY) != null
				&& StringUtils.isNotBlank(headInfo.get(Parameter.HANDOVER_ID_KEY).toString())) {
			allAllocateInfo.setStoreHandoverId(headInfo.get(Parameter.HANDOVER_ID_KEY).toString());
			handoverInfo.setHandoverId(headInfo.get(Parameter.HANDOVER_ID_KEY).toString());
		} else {
			logger.debug("库外交接任务交接接口-------- 交接ID:" + headInfo.get(Parameter.HANDOVER_ID_KEY));
			msgMap.put(Parameter.ERROR_MSG_KEY, "库外交接任务交接接口-------- 交接ID:" + headInfo.get(Parameter.HANDOVER_ID_KEY));
			return null;
		}

		String strInOutType = null;
		// 取得出入库类型
		if (headInfo.get(Parameter.INOUT_TYPE_KEY) != null
				&& StringUtils.isNotBlank(headInfo.get(Parameter.INOUT_TYPE_KEY).toString())
				&& (AllocationConstant.InoutType.In.equals(headInfo.get(Parameter.INOUT_TYPE_KEY).toString())
						|| AllocationConstant.InoutType.Out
								.equals(headInfo.get(Parameter.INOUT_TYPE_KEY).toString()))) {
			strInOutType = headInfo.get(Parameter.INOUT_TYPE_KEY).toString();
		} else {
			logger.debug("库外交接任务交接接口-------- 出入库类型:" + headInfo.get(Parameter.INOUT_TYPE_KEY));
			msgMap.put(Parameter.ERROR_MSG_KEY, "库外交接任务交接接口-------- 出入库类型:" + headInfo.get(Parameter.INOUT_TYPE_KEY));
			return null;
		}

		// 取得更新者ID
		if (headInfo.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("库外交接任务交接接口-------- 更新者ID:" + headInfo.get(Parameter.USER_ID_KEY));
			msgMap.put(Parameter.ERROR_MSG_KEY, "库外交接任务交接接口-------- 更新者ID:" + headInfo.get(Parameter.USER_ID_KEY));
			return null;
		}
		// 取得更新者名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("库外交接任务交接接口-------- 更新者名:" + headInfo.get(Parameter.USER_NAME_KEY));
			msgMap.put(Parameter.ERROR_MSG_KEY, "库外交接任务交接接口-------- 更新者名:" + headInfo.get(Parameter.USER_NAME_KEY));
			return null;
		}
		User loginUser = new User();
		loginUser.setId(headInfo.get(Parameter.USER_ID_KEY).toString());
		loginUser.setName(headInfo.get(Parameter.USER_NAME_KEY).toString());
		allAllocateInfo.setLoginUser(loginUser);

		String strBudName = null;
		String strBudId = null;
		String strBudHandType = null;

		// 移交人信息列表
		if (headInfo.get(Parameter.HANDOVER_LIST_KEY) != null) {
			handoverList = (List<Map<String, Object>>) headInfo.get(Parameter.HANDOVER_LIST_KEY);
		}
		// 接收人信息列表
		if (headInfo.get(Parameter.ACCEPT_LIST_KEY) != null) {
			acceptList = (List<Map<String, Object>>) headInfo.get(Parameter.ACCEPT_LIST_KEY);
		}
		// 授权人信息列表
		if (headInfo.get(Parameter.MANAGER_LIST_KEY) != null) {
			managerList = (List<Map<String, Object>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
		}

		// 验证没有授权人时，必须要有移交人和接收人
		if (Collections3.isEmpty(managerList)) {
			if (Collections3.isEmpty(handoverList)) {
				logger.debug("库外交接任务交接接口-------- 移交人信息列表:" + headInfo.get(Parameter.HANDOVER_LIST_KEY));
				msgMap.put(Parameter.ERROR_MSG_KEY,
						"库外交接任务交接接口-------- 移交人信息列表:" + headInfo.get(Parameter.HANDOVER_LIST_KEY));
				return null;
			}
			if (Collections3.isEmpty(acceptList)) {
				logger.debug("库外交接任务交接接口-------- 接收人信息列表:" + headInfo.get(Parameter.ACCEPT_LIST_KEY));
				msgMap.put(Parameter.ERROR_MSG_KEY,
						"库外交接任务交接接口-------- 接收人信息列表:" + headInfo.get(Parameter.ACCEPT_LIST_KEY));
				return null;
			}
		}

		// 设置移交人信息
		if (!Collections3.isEmpty(handoverList)) {
			for (Map<String, Object> map : handoverList) {
				handoverDetail = new AllHandoverDetail();
				if (map.get(Parameter.OPT_USER_ID_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 移交人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY,
							"库外交接任务交接接口-------- 移交人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_NAME_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_NAME_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 移交人名:" + map.get(Parameter.OPT_USER_NAME_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY,
							"库外交接任务交接接口-------- 移交人名:" + map.get(Parameter.OPT_USER_NAME_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_HAND_TYPE_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 移交人员签收方式:" + map.get(Parameter.OPT_USER_HAND_TYPE_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY,
							"库外交接任务交接接口-------- 移交人员签收方式:" + map.get(Parameter.OPT_USER_HAND_TYPE_KEY));
					return null;
				}

				strBudId = map.get(Parameter.OPT_USER_ID_KEY).toString();
				strBudName = map.get(Parameter.OPT_USER_NAME_KEY).toString();
				strBudHandType = map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString();

				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setHandoverId((String) headInfo.get(Parameter.HANDOVER_ID_KEY));
				handoverDetail.setEscortId(strBudId.toString());
				handoverDetail.setEscortName(strBudName.toString());
				handoverDetail.setOperationType(AllocationConstant.OperationType.TURN_OVER);
				handoverInfo.getDetailList().add(handoverDetail);
			}
		}

		// 设定接收人信息
		if (!Collections3.isEmpty(acceptList)) {
			strBudName = null;
			strBudId = null;
			strBudHandType = null;
			for (Map<String, Object> map : acceptList) {
				handoverDetail = new AllHandoverDetail();
				if (map.get(Parameter.OPT_USER_ID_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 接收人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY,
							"库外交接任务交接接口-------- 接收人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_NAME_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_NAME_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 接收人名:" + map.get(Parameter.OPT_USER_NAME_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY,
							"库外交接任务交接接口-------- 接收人名:" + map.get(Parameter.OPT_USER_NAME_KEY));
					return null;
				}
				if (map.get(Parameter.OPT_USER_HAND_TYPE_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 接收人员签收方式:" + map.get(Parameter.OPT_USER_HAND_TYPE_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY,
							"库外交接任务交接接口-------- 接收人员签收方式:" + map.get(Parameter.OPT_USER_HAND_TYPE_KEY));
					return null;
				}
				strBudId = map.get(Parameter.OPT_USER_ID_KEY).toString();
				strBudName = map.get(Parameter.OPT_USER_NAME_KEY).toString();
				strBudHandType = map.get(Parameter.OPT_USER_HAND_TYPE_KEY).toString();

				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setHandoverId((String) headInfo.get(Parameter.HANDOVER_ID_KEY));
				handoverDetail.setEscortId(strBudId.toString());
				handoverDetail.setEscortName(strBudName.toString());
				handoverDetail.setOperationType(AllocationConstant.OperationType.ACCEPT);
				handoverInfo.getDetailList().add(handoverDetail);
			}

		}

		// 设定授权人信息
		if (!Collections3.isEmpty(managerList)) {
			strBudName = null;
			strBudId = null;
			strBudHandType = null;
			StringBuilder strBudReason = new StringBuilder();
			for (Map<String, Object> map : managerList) {
				handoverDetail = new AllHandoverDetail();
				if (map.get(Parameter.OPT_USER_ID_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 授权人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY,
							"库外交接任务交接接口-------- 授权人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					return null;
				}
				if (map.get(Parameter.REASON_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.REASON_KEY).toString())) {
					logger.debug("库外交接任务交接接口-------- 授权原因:" + map.get(Parameter.REASON_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY, "库外交接任务交接接口-------- 授权原因:" + map.get(Parameter.REASON_KEY));
					return null;
				}

				// 授权原因
				strBudReason.append(map.get(Parameter.REASON_KEY).toString());

				// 取得授权人用户名
				User user = UserUtils.getByLoginName(map.get(Parameter.OPT_USER_ID_KEY).toString());

				if (user == null) {
					logger.debug(
							"库外交接任务交接接口接口-------- 授权人信息取得失败。授权人ID=" + map.get(Parameter.OPT_USER_ID_KEY).toString());
					msgMap.put(Parameter.ERROR_MSG_KEY, "库外交接任务交接接口-------- 授权人信息取得失败" + map.get(Parameter.REASON_KEY));
					return null;
				}
				StoEscortInfo stoEscortInfo = stoEscortInfoService.findByUserId(user.getId());
				if (stoEscortInfo == null) {
					logger.debug(
							"库外交接任务交接接口接口-------- 授权人信息取得失败。授权人ID=" + map.get(Parameter.OPT_USER_ID_KEY).toString());
					msgMap.put(Parameter.ERROR_MSG_KEY, "库外交接任务交接接口-------- 授权人信息取得失败" + map.get(Parameter.REASON_KEY));
					return null;
				}
				// 授权人ID
				strBudId = stoEscortInfo.getId();
				if (!map.get(Parameter.MANAGER_TYPE_KEY).toString().isEmpty()) {
					strBudHandType = map.get(Parameter.MANAGER_TYPE_KEY).toString();
				}
				strBudName = stoEscortInfo.getEscortName();

				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setHandoverId((String) headInfo.get(Parameter.HANDOVER_ID_KEY));
				handoverDetail.setEscortId(strBudId.toString());
				handoverDetail.setEscortName(strBudName.toString());
				handoverDetail.setOperationType(AllocationConstant.OperationType.AUTHORIZATION);
				handoverDetail.setType(strBudHandType.toString());
				handoverDetail.setManagerReason(strBudReason.toString());
				handoverInfo.getDetailList().add(handoverDetail);
			}

		}
		handoverInfo.setAcceptDate(new Date());
		if (AllocationConstant.InoutType.In.equals(strInOutType)) {
			// 入库变更流水状态为已完成
			allAllocateInfo.setStatus(AllocationConstant.Status.Finish);
		} else {
			// 出库变更流水状态为在途
			allAllocateInfo.setStatus(AllocationConstant.Status.Onload);
		}
		allAllocateInfo.setAllHandoverInfo(handoverInfo);

		return allAllocateInfo;
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2016年1月27日
	 * 
	 *          判断押运人员是否归属当前线路
	 * @param headInfo
	 *            押运人员信息
	 * @return true：属于当前线路，false：不属于当前线路
	 */
	@SuppressWarnings("unchecked")
	public boolean checkEscortInfo(Map<String, Object> headInfo, Map<String, Object> msgMap) {
		// 根据线路Id查询线路信息
		StoRouteInfo stoRouteInfo = StoreCommonUtils.getRouteInfoById(headInfo.get(Parameter.ROUTE_ID_KEY).toString());
		if (stoRouteInfo == null) {
			logger.debug("库外交接任务交接接口-------- 路线ID(对应线路信息不存在):" + headInfo.get(Parameter.ROUTE_ID_KEY));
			msgMap.put(Parameter.ERROR_MSG_KEY, "[交接失败]：线路信息不存在或当前路线押运人员信息已变更！");
			return false;
		}
		if (stoRouteInfo.getEscortInfo1() == null || stoRouteInfo.getEscortInfo2() == null) {
			logger.debug("库外交接任务交接接口-------- 路线ID(当前线路没有押运人员):" + headInfo.get(Parameter.ROUTE_ID_KEY));
			msgMap.put(Parameter.ERROR_MSG_KEY, "[交接失败]：线路信息不存在或当前路线押运人员信息已变更！");
			return false;
		}

		// 押运人员信息列表
		String strEscortKey = "";
		if (AllocationConstant.InoutType.In.equals(headInfo.get(Parameter.INOUT_TYPE_KEY).toString())) {
			strEscortKey = Parameter.HANDOVER_LIST_KEY;
		} else {
			strEscortKey = Parameter.ACCEPT_LIST_KEY;
		}

		if (headInfo.get(strEscortKey) != null) {
			List<Map<String, Object>> escortList = (List<Map<String, Object>>) headInfo.get(strEscortKey);
			if (!Collections3.isEmpty(escortList)) {
				String strEscortId = null;
				for (Map<String, Object> map : escortList) {
					// 押运人ID
					strEscortId = map.get(Parameter.OPT_USER_ID_KEY).toString();

					if (!strEscortId.equals(stoRouteInfo.getEscortInfo1().getId())
							&& !strEscortId.equals(stoRouteInfo.getEscortInfo2().getId())) {
						logger.debug("库外交接任务交接接口-------- 押运人员ID(不在当前线路上):" + map.get(Parameter.OPT_USER_ID_KEY));
						msgMap.put(Parameter.ERROR_MSG_KEY, "[交接失败]：线路信息不存在或当前路线押运人员信息已变更！");
						return false;
					}

				}
			}
		}
		return true;
	}

}
