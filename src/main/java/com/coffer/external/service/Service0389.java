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
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
 * Title: Service0389
 * <p>
 * Description: ATM库外交接任务交接接口
 * </p>
 * 
 * @author sg
 * @date 2017年11月15日
 */
@Component("Service0389")
@Scope("singleton")
public class Service0389 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 
	 * @author sg ATM库外交接任务交接接口
	 * @version 2017-11-15
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 服务代码
		String serviceNo = StringUtils.toString(paramMap.get(Parameter.SERVICE_NO_KEY));
		// 所属机构
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		// 任务编号
		String taskNo = StringUtils.toString(paramMap.get(Parameter.TASK_NO_KEY));
		// 交接ID
		String handoverId = StringUtils.toString(paramMap.get(Parameter.HANDOVER_ID_KEY));
		// 移交人列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> handoverList = (List<Map<String, Object>>) paramMap.get(Parameter.HANDOVER_LIST_KEY);
		// 接收人列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> acceptList = (List<Map<String, Object>>) paramMap.get(Parameter.ACCEPT_LIST_KEY);
		// 授权人列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> managerList = (List<Map<String, Object>>) paramMap.get(Parameter.MANAGER_LIST_KEY);
		// 出入库类型
		String inoutType = StringUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY));
		// 用户编号
		String userId = StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
		// 系统登录用户姓名
		String userName = StringUtils.toString(paramMap.get(Parameter.USER_NAME_KEY));
		// 库房人员
		List<Map<String, Object>> checkUserList = Lists.newArrayList();
		// 押运人员
		List<Map<String, Object>> escortList = Lists.newArrayList();
		// 钞箱出库
		if (ExternalConstant.Handover.INOUT_TYPE_OUT.equals(inoutType)) {
			// 库房人员是移交人
			checkUserList = handoverList;
			// 押运人员是接收人
			escortList = acceptList;
		}
		// 钞箱入库
		if (ExternalConstant.Handover.INOUT_TYPE_IN.equals(inoutType)) {
			// 库房人员是接收人
			checkUserList = acceptList;
			// 押运人员是移交人
			escortList = handoverList;
		}
		// 验证参数
		checkBoxHandoutRegister(paramMap, map);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 设置更新人信息
		User loginUser = UserUtils.get(userId);
		// 根据机构ID查询相关信息
		Office office = officeService.get(officeId);
		// 查询条件封装
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		// 设置机构
		allAllocateInfo.setrOffice(office);
		// 调拨状态
		allAllocateInfo.setStatus(AllocationConstant.AtmBusinessStatus.HandoverTodo);
		// 设置任务编号
		allAllocateInfo.setRouteId(taskNo);
		// 设置交接ID
		allAllocateInfo.setStoreHandoverId(handoverId);
		// 根据条件找出相应的信息
		List<AllAllocateInfo> list = allocationService.findAtmBoxList(allAllocateInfo);
		// 验证list是否为空
		checkList(loginUser, office, list, map, paramMap, handoverList, acceptList, managerList);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		for (AllAllocateInfo allAllocateInfoList : list) {
			// 业务类型
			String businessType = allAllocateInfoList.getBusinessType();
			// 修改交接人信息
			AllHandoverInfo storeHandover = new AllHandoverInfo();
			// 设置交接ID
			storeHandover.setHandoverId(handoverId);
			// 设置交接时间
			storeHandover.setAcceptDate(new Date());
			// 根据条件对AllHandoverInfo表进行修改
			allocationService.updateAllHandoverInfo(storeHandover);
			// 库房人员列表
			if (!Collections3.isEmpty(checkUserList)) {
				for (Map<String, Object> checkUserMap : checkUserList) {
					String checkUserId = StringUtils.toString(checkUserMap.get(Parameter.OPT_USER_ID_KEY));
					// 根据UserId查询相关信息
					StoEscortInfo checkUserIdStoEscortInfo = stoEscortInfoService.findByEscortId(checkUserId);
					// 准备插入的条件
					AllHandoverDetail checkUserIdAllHandoverDetail = new AllHandoverDetail();
					// 设置ID
					checkUserIdAllHandoverDetail.setDetailId(IdGen.uuid());
					// 设置交接ID
					checkUserIdAllHandoverDetail.setHandoverId(handoverId);
					// 设置人员ID
					checkUserIdAllHandoverDetail.setEscortId(checkUserIdStoEscortInfo.getId());
					// 设置人员姓名
					checkUserIdAllHandoverDetail.setEscortName(checkUserIdStoEscortInfo.getEscortName());
					// 设置交接方式
					checkUserIdAllHandoverDetail
							.setType(StringUtils.toString(checkUserMap.get(Parameter.HAND_TYPE_KEY)));
					// 判断如果是钞箱入库
					if (AllocationConstant.BusinessType.ATM_BOX_HANDIN.equals(businessType)) {
						// 设置交接类型为接收
						checkUserIdAllHandoverDetail.setOperationType(AllocationConstant.OperationType.ACCEPT);
						// 如果是钞箱出库
					}
					if (AllocationConstant.BusinessType.ATM_BOX_HANDOUT.equals(businessType)) {
						// 设置交接类型为移交
						checkUserIdAllHandoverDetail.setOperationType(AllocationConstant.OperationType.TURN_OVER);
					}
					// 向AllHandoverDetail表中插入数据
					allocationService.AllHandoverDetailInsert(checkUserIdAllHandoverDetail);
				}
			}
			// 押运人员列表
			if (!Collections3.isEmpty(escortList)) {
				for (Map<String, Object> escortMap : escortList) {
					String escortId = StringUtils.toString(escortMap.get(Parameter.OPT_USER_ID_KEY));
					StoEscortInfo escortIdStoEscortInfo = stoEscortInfoService.findByEscortId(escortId);
					// 准备插入的条件
					AllHandoverDetail escortIdAllHandoverDetail = new AllHandoverDetail();
					// 设置ID
					escortIdAllHandoverDetail.setDetailId(IdGen.uuid());
					// 设置交接ID
					escortIdAllHandoverDetail.setHandoverId(handoverId);
					// 设置人员ID
					escortIdAllHandoverDetail.setEscortId(escortIdStoEscortInfo.getId());
					// 设置人员姓名
					escortIdAllHandoverDetail.setEscortName(escortIdStoEscortInfo.getEscortName());
					// 设置交接方式
					escortIdAllHandoverDetail.setType(StringUtils.toString(escortMap.get(Parameter.HAND_TYPE_KEY)));
					// 判断如果是钞箱入库
					if (AllocationConstant.BusinessType.ATM_BOX_HANDIN.equals(businessType)) {
						// 设置交接类型为移交
						escortIdAllHandoverDetail.setOperationType(AllocationConstant.OperationType.TURN_OVER);
						// 如果是钞箱出库
					}
					if (AllocationConstant.BusinessType.ATM_BOX_HANDOUT.equals(businessType)) {
						// 设置交接类型为接收
						escortIdAllHandoverDetail.setOperationType(AllocationConstant.OperationType.ACCEPT);
					}
					// 向AllHandoverDetail表中插入数据
					allocationService.AllHandoverDetailInsert(escortIdAllHandoverDetail);

				}
			}
			// 判断授权人列表是否为空
			if (!Collections3.isEmpty(managerList)) {
				// 授权人列表
				for (Map<String, Object> managerMap : managerList) {
					// 准备插入的条件
					AllHandoverDetail escortIdAllHandoverDetail = new AllHandoverDetail();
					// 设置ID
					escortIdAllHandoverDetail.setDetailId(IdGen.uuid());
					// 设置交接ID
					escortIdAllHandoverDetail.setHandoverId(handoverId);
					// 授权人信息
					User loginName = UserUtils
							.getByLoginName(StringUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY)));
					// 设置人员ID
					escortIdAllHandoverDetail.setEscortId(StringUtils.toString(loginName.getId()));
					// 设置人员姓名
					escortIdAllHandoverDetail.setEscortName(loginName.getName());
					// 设置授权方式
					escortIdAllHandoverDetail.setType(StringUtils.toString(managerMap.get(Parameter.MANAGER_TYPE_KEY)));
					// 设置授权理由
					escortIdAllHandoverDetail
							.setManagerReason(StringUtils.toString(managerMap.get(Parameter.REASON_KEY)));
					// 设置操作类型
					escortIdAllHandoverDetail.setOperationType(AllocationConstant.OperationType.AUTHORIZATION);
					// 向AllHandoverDetail表中插入数据
					allocationService.AllHandoverDetailInsert(escortIdAllHandoverDetail);

				}
			}

			// 设置状态
			allAllocateInfoList.setStatus(AllocationConstant.AtmBusinessStatus.Onload);
			// 设置更新人信息
			allAllocateInfoList.setUpdateBy(loginUser);
			// 设置更新日期
			allAllocateInfoList.setUpdateDate(new Date());
			// 设置更新人姓名
			allAllocateInfoList.setUpdateName(userName);
			// 根据条件对allAllocateInfo表进行修改
			allocationService.updateAtm(allAllocateInfoList);
		}
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return

		setReturnMap(map, serviceNo);
	}

	/**
	 * @author sg
	 * @version 2017-11-13
	 * 
	 * @Description ATM库外交接任务查询接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	private String checkBoxHandoutRegister(Map<String, Object> headInfo, Map<String, Object> map) {
		// 所属机构
		String officeId = StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY));
		// 任务编号
		String taskNo = StringUtils.toString(headInfo.get(Parameter.TASK_NO_KEY));
		// 交接ID
		String handoverId = StringUtils.toString(headInfo.get(Parameter.HANDOVER_ID_KEY));
		// 出入库类型
		String inoutType = StringUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY));
		// 移交人列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> handoverList = (List<Map<String, Object>>) headInfo.get(Parameter.HANDOVER_LIST_KEY);
		// 接收人列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> acceptList = (List<Map<String, Object>>) headInfo.get(Parameter.ACCEPT_LIST_KEY);
		// 授权人列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> managerList = (List<Map<String, Object>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
		// 判断所属机构是否为空
		if (StringUtils.isBlank(officeId)) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId");
			return Constant.FAILED;
		}
		// 判断任务编号是否为空
		if (StringUtils.isBlank(taskNo)) {
			logger.debug("参数错误--------taskNo:" + CommonUtils.toString(headInfo.get(Parameter.TASK_NO_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------taskNo");
			return Constant.FAILED;
		}
		// 判断交接ID是否为空
		if (StringUtils.isBlank(handoverId)) {
			logger.debug("参数错误--------handoverId:" + CommonUtils.toString(headInfo.get(Parameter.HANDOVER_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------handoverId");
			return Constant.FAILED;
		}
		// 判断出入库类型是否为空
		if (StringUtils.isBlank(inoutType)) {
			logger.debug("参数错误--------inoutType:" + CommonUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------inoutType");
			return Constant.FAILED;
		}
		// 验证没有授权人时，必须要有移交人和接收人
		if (Collections3.isEmpty(managerList)) {
			// 判断移交人列表及其内容是否为空
			if (Collections3.isEmpty(handoverList) || handoverList.size() == 0) {
				logger.debug(
						"参数错误--------handoverList:" + CommonUtils.toString(headInfo.get(Parameter.HANDOVER_LIST_KEY)));
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY,
						"参数错误--------handoverList:" + CommonUtils.toString(headInfo.get(Parameter.HANDOVER_LIST_KEY)));
				return Constant.FAILED;

			} else {
				for (Map<String, Object> checkUserMap : handoverList) {
					if (checkUserMap.get(Parameter.HAND_TYPE_KEY) == null
							|| StringUtils.isBlank(checkUserMap.get(Parameter.HAND_TYPE_KEY).toString())) {
						logger.debug("参数错误--------handoverList:handType:"
								+ CommonUtils.toString(checkUserMap.get(Parameter.HAND_TYPE_KEY)));
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
						map.put(Parameter.ERROR_MSG_KEY, "参数错误--------handoverList:handType:"
								+ CommonUtils.toString(checkUserMap.get(Parameter.HAND_TYPE_KEY)));
						return Constant.FAILED;

					}

				}
			}
			// 判断接收人列表及其内容是否为空
			if (Collections3.isEmpty(acceptList) || acceptList.size() == 0) {
				logger.debug("参数错误--------acceptList:" + CommonUtils.toString(headInfo.get(Parameter.ACCEPT_LIST_KEY)));
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY,
						"参数错误--------acceptList:" + CommonUtils.toString(headInfo.get(Parameter.ACCEPT_LIST_KEY)));
				return "error";

			} else {
				for (Map<String, Object> escortMap : acceptList) {
					if (escortMap.get(Parameter.HAND_TYPE_KEY) == null
							|| StringUtils.isBlank(escortMap.get(Parameter.HAND_TYPE_KEY).toString())) {
						logger.debug("参数错误--------acceptList:handType:"
								+ CommonUtils.toString(escortMap.get(Parameter.HAND_TYPE_KEY)));
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
						map.put(Parameter.ERROR_MSG_KEY, "参数错误--------acceptList:handType:"
								+ CommonUtils.toString(escortMap.get(Parameter.HAND_TYPE_KEY)));
						return Constant.FAILED;

					}

				}
			}
		}
		// 设定授权人信息
		if (!Collections3.isEmpty(managerList)) {
			for (Map<String, Object> managerMap : managerList) {
				if (managerMap.get(Parameter.OPT_USER_ID_KEY) == null
						|| StringUtils.isBlank(managerMap.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("参数错误--------managerList:id:"
							+ CommonUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY)));
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "参数错误--------managerList:id:"
							+ CommonUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY)));
					return Constant.FAILED;
				}
				if (managerMap.get(Parameter.REASON_KEY) == null
						|| StringUtils.isBlank(managerMap.get(Parameter.REASON_KEY).toString())) {
					logger.debug("参数错误--------managerList:reason:"
							+ CommonUtils.toString(managerMap.get(Parameter.REASON_KEY)));
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "参数错误--------managerList:reason:"
							+ CommonUtils.toString(managerMap.get(Parameter.REASON_KEY)));
					return Constant.FAILED;
				}
				if (managerMap.get(Parameter.MANAGER_TYPE_KEY) == null
						|| StringUtils.isBlank(managerMap.get(Parameter.MANAGER_TYPE_KEY).toString())) {
					logger.debug("参数错误--------managerList:managerType:"
							+ CommonUtils.toString(managerMap.get(Parameter.MANAGER_TYPE_KEY)));
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "参数错误--------managerList:managerType:"
							+ CommonUtils.toString(managerMap.get(Parameter.MANAGER_TYPE_KEY)));
					return Constant.FAILED;
				}
			}
		}
		return Constant.SUCCESS;
	}

	/**
	 * @author sg
	 * @version 2017-11-13
	 * 
	 * @Description ATM库外交接任务查询接口 验证List
	 * @param headInfo
	 * @return 处理结果
	 */
	private String checkList(User loginUser, Office office, List<AllAllocateInfo> list, Map<String, Object> map,
			Map<String, Object> headInfo, List<Map<String, Object>> handoverList, List<Map<String, Object>> acceptList,
			List<Map<String, Object>> managerList) {
		// 判断用户是否存在
		if (loginUser == null) {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------userId");
			return Constant.FAILED;
		}
		if (office == null) {
			// 机构ID不正确
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY,
					"不存在机构ID为：" + StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)) + "的数据");
			return Constant.FAILED;
		}
		// 判断主表中是否存在数据
		if (Collections3.isEmpty(list)) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "未找到相应信息");
			return Constant.FAILED;
		}
		// 判断库房人员信息是否存在
		if (!Collections3.isEmpty(handoverList)) {
			for (Map<String, Object> checkUserMap : handoverList) {
				String checkUserId = StringUtils.toString(checkUserMap.get(Parameter.OPT_USER_ID_KEY));
				// 根据UserId查询相关信息
				StoEscortInfo checkUserIdStoEscortInfo = stoEscortInfoService.findByEscortId(checkUserId);
				// 判断信息是否为空
				if (checkUserIdStoEscortInfo == null) {
					logger.debug("参数错误--------handoverList:id:"
							+ CommonUtils.toString(checkUserMap.get(Parameter.OPT_USER_ID_KEY)));
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "参数错误--------handoverList:id:"
							+ CommonUtils.toString(checkUserMap.get(Parameter.OPT_USER_ID_KEY)));
					return Constant.FAILED;
				}
			}
		}
		// 判断押运人员信息是否存在
		if (!Collections3.isEmpty(acceptList)) {
			for (Map<String, Object> escortMap : acceptList) {
				String escortId = StringUtils.toString(escortMap.get(Parameter.OPT_USER_ID_KEY));
				StoEscortInfo escortIdStoEscortInfo = stoEscortInfoService.findByEscortId(escortId);
				if (escortIdStoEscortInfo == null) {
					logger.debug("参数错误--------acceptList:id:"
									+ CommonUtils.toString(escortMap.get(Parameter.OPT_USER_ID_KEY)));
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "参数错误--------escortList:userId:"
							+ CommonUtils.toString(escortMap.get(Parameter.OPT_USER_ID_KEY)));
					return Constant.FAILED;
				}
			}
		}
		// 判断授权人信息是否存在
		if (!Collections3.isEmpty(managerList)) {
			// 授权人列表
			for (Map<String, Object> managerMap : managerList) {
				// 授权人信息
				User loginName = UserUtils
						.getByLoginName(StringUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY)));
				if (loginName == null) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
					map.put(Parameter.ERROR_MSG_KEY,
							"授权人ID为：" + StringUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY)) + "有问题！");
					return Constant.FAILED;
				}
			}
		}
		return Constant.SUCCESS;
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @param map
	 * @param serviceNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo) {
		// 版本号
		map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);

		return gson.toJson(map);
	}
}
