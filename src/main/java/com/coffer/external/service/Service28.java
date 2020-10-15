package com.coffer.external.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
 * Title: Service28
 * <p>
 * Description: 库间交接任务查询接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service28")
@Scope("singleton")
public class Service28 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;

	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月14日
	 * 
	 *          库间交接任务查询接口28
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		try {
			String officeId = "";
			String inoutType = "";
			// 验证传递参数
			if (paramMap.get("officeId") == null || StringUtils.isBlank(paramMap.get("officeId").toString())
					|| paramMap.get("inoutType") == null || StringUtils.isBlank(paramMap.get("inoutType").toString())) {
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return gson.toJson(map);
			} else {
				officeId = paramMap.get("officeId").toString();
				inoutType = paramMap.get("inoutType").toString();
			}
			// 根据输入条件 查询交接相关信息
			AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
			String[] types = { AllocationConstant.BusinessType.Between_Cash,
					AllocationConstant.BusinessType.Between_Clear, AllocationConstant.BusinessType.Between_ATM_Add,
					AllocationConstant.BusinessType.Between_ATM_Clear };
			List<String> typeList = Arrays.asList(types);
			allAllocateInfo.setBusinessTypes(typeList);
			allAllocateInfo.setStatus(AllocationConstant.Status.HandoverTodo);
			allAllocateInfo.setrOffice(new Office(officeId));
			// allAllocateInfo.setInoutType(inoutType);
			List<AllAllocateInfo> allAllocateList = allocationService.findAllocation(allAllocateInfo);

			// 查询库房和整点室人员
			List<String> userTypeList = Lists.newArrayList();
			// 金库主管
			userTypeList.add(Constant.SysUserType.COFFER_MANAGER);
			// 金库操作员
			userTypeList.add(Constant.SysUserType.COFFER_OPT);
			// 整点室主管
			userTypeList.add(Constant.SysUserType.CLASSFICATION_MANAGER);
			// 整点室操作员
			userTypeList.add(Constant.SysUserType.CLASSFICATION_OPT);

			List<StoEscortInfo> allUserList = stoEscortInfoService.findEscortInfo(paramMap, userTypeList);

			// 封装交接任务数据
			List<Map<String, Object>> taskList = getTaskToMap(allAllocateList);
			// 封装人员数据
			List<Map<String, Object>> userList = getEscortToMap(allUserList);
			map.put("searchDate", DateUtils.getCurrentMillisecond());
			map.put("taskList", taskList);
			map.put("userList", userList);
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		return gson.toJson(map);
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月14日
	 * 
	 *          人员信息封装
	 * @param escortUserList
	 * @return
	 */
	private List<Map<String, Object>> getEscortToMap(List<StoEscortInfo> escortUserList) {
		List<Map<String, Object>> escortList = new ArrayList<Map<String, Object>>();
		if (escortUserList != null && escortUserList.size() > 0) {
			for (StoEscortInfo stoEscortInfo : escortUserList) {
				Map<String, Object> taskMap = new HashMap<String, Object>();
				taskMap.put("userId", stoEscortInfo.getId());
				taskMap.put("userName", stoEscortInfo.getEscortName());
				taskMap.put("idcardNo", stoEscortInfo.getIdcardNo());
				// 人员类型
				taskMap.put("userType", stoEscortInfo.getEscortType());
				if (stoEscortInfo.getFingerNo1() != null && stoEscortInfo.getFingerNo1().length > 0) {

					taskMap.put("finger1", Encodes.encodeBase64(stoEscortInfo.getFingerNo1()));
				} else {
					taskMap.put("finger1", null);
				}
				if (stoEscortInfo.getFingerNo2() != null && stoEscortInfo.getFingerNo2().length > 0) {

					taskMap.put("finger2", Encodes.encodeBase64(stoEscortInfo.getFingerNo2()));
				} else {
					taskMap.put("finger2", null);
				}
				escortList.add(taskMap);
			}
		}
		return escortList;
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月14日
	 * 
	 *          交接信息封装
	 * @param allAllocateList
	 * @return
	 */
	private List<Map<String, Object>> getTaskToMap(List<AllAllocateInfo> allAllocateList) {
		List<Map<String, Object>> taskList = new ArrayList<Map<String, Object>>();
		if (allAllocateList != null && allAllocateList.size() > 0) {
			for (AllAllocateInfo allAllocate : allAllocateList) {
				Map<String, Object> taskMap = new HashMap<String, Object>();
				taskMap.put("serialorderNo", allAllocate.getAllId());
				taskMap.put("amount", allAllocate.getRegisterAmount());
				taskMap.put("createTime", allAllocate.getCreateDate());
				taskList.add(taskMap);
			}
		}
		return taskList;
	}
}
