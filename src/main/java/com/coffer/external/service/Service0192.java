package com.coffer.external.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
 * Title: Service0192
 * <p>
 * Description:
 * </p>
 * 网点接受提交接口
 * 
 * @author liuyaowen
 * @date 2017年8月9日 上午10:41:10
 */
@Component("Service0192")
@Scope("singleton")
public class Service0192 extends HardwardBaseService {
	@Autowired
	AllocationService service;
	@Autowired
	StoEscortInfoService stoEscortInfoService;
	@Autowired
	StoBoxInfoService stoBoxInfoService;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得流水号
		String allId = (String) paramMap.get(Parameter.ALL_ID);
		// 验证流水编号
		if (StringUtils.isBlank(allId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "流水号不可以为空！");
			return gson.toJson(respMap);
		}
		// 查询对应的流水
		AllAllocateInfo allAllocateInfo = service.getAllocate(allId);
		if (allAllocateInfo != null) {
			// 判断流水状态
			if (AllocationConstant.Status.Finish.equals(allAllocateInfo.getStatus())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put(Parameter.ERROR_MSG_KEY, "当前流水已通过页面接收完成！");
				return gson.toJson(respMap);
			}
		} else {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			respMap.put(Parameter.ERROR_MSG_KEY, "未查询到流水！");
			return gson.toJson(respMap);
		}
		// 取得机构编号
		String officeId = (String) paramMap.get(Parameter.OFFICE_ID_KEY);
		// 验证机构编号
		if (StringUtils.isBlank(officeId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "机构编号不可以为空！");
			return gson.toJson(respMap);
		}

		// 箱袋编号列表
		List<Map<String, Object>> boxList = null;
		// 押运人员列表
		List<Map<String, Object>> escortList = null;
		// 网点接收人员列表
		List<Map<String, Object>> branchList = null;
		// 验证箱袋列表
		boxList = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);
		if (!Collections3.isEmpty(boxList)) {
			for (Map<String, Object> map : boxList) {
				// 验证rfid和boxNo
				if ((map.get(Parameter.RFID_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.RFID_KEY).toString()))
						|| (map.get(Parameter.BOX_NO_KEY) == null
								|| StringUtils.isBlank(map.get(Parameter.BOX_NO_KEY).toString()))) {
					respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					respMap.put(Parameter.ERROR_MSG_KEY, "箱袋RFID或箱袋编号为空");
					return gson.toJson(respMap);
				}
			}
		}
		// 获取押运人员
		if (paramMap.get(Parameter.ESCORT_LIST_KEY) != null) {
			escortList = (List<Map<String, Object>>) paramMap.get(Parameter.ESCORT_LIST_KEY);
		}
		// 获取网点接收人员
		if (paramMap.get(Parameter.ESCORT_LIST_KEY) != null) {
			branchList = (List<Map<String, Object>>) paramMap.get(Parameter.BRANCH_LIST_KEY);
		}
		// 取得授权人姓名
		String managerLoginName = (String) paramMap.get("managerLoginName");
		// 登记人ID
		String userId = (String) paramMap.get(Parameter.USER_ID_KEY);
		if (userId == null || StringUtils.isBlank(userId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------userId:" + CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY)));
			return gson.toJson(respMap);
		}

		// 登记人姓名
		String userName = (String) paramMap.get(Parameter.USER_NAME_KEY);
		if (userName == null || StringUtils.isBlank(userName)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------userName:" + CommonUtils.toString(paramMap.get(Parameter.USER_NAME_KEY)));
			return gson.toJson(respMap);
		}
		// 设置交接主表主表信息
		AllHandoverInfo handoverInfo = new AllHandoverInfo();
		handoverInfo.setHandoverId(IdGen.uuid());
		handoverInfo.setCreateDate(new Date());
		handoverInfo.setAcceptDate(new Date());
		// 设置移交人信息
		if (!Collections3.isEmpty(escortList)) {
			for (Map<String, Object> map : escortList) {
				AllHandoverDetail handoverDetail = new AllHandoverDetail();
				String strId = map.get(Parameter.OPT_USER_ID_KEY).toString();
				String strName = map.get(Parameter.OPT_USER_NAME_KEY).toString();
				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setHandoverId(handoverInfo.getId());
				handoverDetail.setEscortId(strId);
				handoverDetail.setEscortName(strName);
				handoverDetail.setOperationType(AllocationConstant.OperationType.TURN_OVER);
				handoverInfo.getDetailList().add(handoverDetail);
			}
		}
		// 设置网点接收人信息
		if (!Collections3.isEmpty(branchList)) {
			for (Map<String, Object> map : branchList) {
				AllHandoverDetail handoverDetail = new AllHandoverDetail();
				String strId = map.get(Parameter.USER_ID_KEY).toString();
				String strName = map.get(Parameter.OPT_USER_NAME_KEY).toString();
				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setHandoverId(handoverInfo.getId());
				handoverDetail.setEscortId(strId);
				handoverDetail.setEscortName(strName);
				handoverDetail.setOperationType(AllocationConstant.OperationType.ACCEPT);
				handoverInfo.getDetailList().add(handoverDetail);
			}
		}
		// 设置授权人信息
		if (managerLoginName != null) {
			AllHandoverDetail handoverDetail = new AllHandoverDetail();
			// 取得授权人用户名
			User user = UserUtils.getByLoginName(managerLoginName);
			if (user == null) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				respMap.put(Parameter.ERROR_MSG_KEY, "授权人信息不存在！");
				return gson.toJson(respMap);
			}
			StoEscortInfo stoEscortInfo = stoEscortInfoService.findByUserId(user.getId());
			// 设置交接明细表信息
			handoverDetail.setDetailId(IdGen.uuid());
			handoverDetail.setHandoverId(handoverInfo.getId());
			handoverDetail.setEscortId(stoEscortInfo.getId());
			handoverDetail.setEscortName(stoEscortInfo.getEscortName());
			handoverDetail.setType(AllocationConstant.HandoverType.SystemLogin);
			handoverDetail.setManagerReason(AllocationConstant.managerReason.BOX_NUMBER_WRONG);
			handoverDetail.setOperationType(AllocationConstant.OperationType.AUTHORIZATION);
			handoverInfo.getDetailList().add(handoverDetail);
		}

		for (AllAllocateDetail AllAllocateDetail : allAllocateInfo.getAllDetailList()) {
			// 查询已扫描的箱子设置扫描状态及扫描时间
			if (!Collections3.isEmpty(boxList)) {
				for (Map<String, Object> map : boxList) {
					if ((AllAllocateDetail.getRfid().equals(map.get(Parameter.RFID_KEY).toString()))
							|| (AllAllocateDetail.getBoxNo().equals(map.get(Parameter.BOX_NO_KEY).toString()))) {
						// 设置扫描状态为已扫描
						AllAllocateDetail.setOutletsScanFlag(AllocationConstant.ScanFlag.Scan);
						// 设置网点PDA扫描日期
						AllAllocateDetail.setOutletsPdaScanDate(new Date());
						// 更新调拨明细表
						service.updateDetailByRfid(AllAllocateDetail);
					}
				}
			}
			StoBoxInfo sto = new StoBoxInfo();
			sto.setId(AllAllocateDetail.getBoxNo());
			sto.setRfid(AllAllocateDetail.getRfid());
			sto.setUpdateBy(UserUtils.getUser());
			sto.setUpdateDate(new Date());
			// 箱袋状态更新为空箱
			if (AllAllocateDetail.getScanDate() != null
					|| AllocationConstant.TaskType.TEMPORARY_TASK.equals(allAllocateInfo.getTaskType())) {
				sto.setBoxStatus(Constant.BoxStatus.EMPTY);
				stoBoxInfoService.updateStatus(sto);
			}
		}

		User user = new User();
		user.setId(userId);
		user.setName(userName);
		allAllocateInfo.setPointHandoverId(handoverInfo.getId());
		allAllocateInfo.setUpdateBy(user);
		allAllocateInfo.setUpdateDate(new Date());
		service.pointconfirm(allAllocateInfo);
		service.acceptConfirm(handoverInfo);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}