package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
 * Title: Service0381
 * <p>
 * Description:PDA同步加钞计划接口
 * </p>
 * 
 * @author wxz
 * @date 2017年11月7日 下午15:37:10
 */
@Component("Service0381")
@Scope("singleton")
public class Service0381 extends HardwardBaseService {

	@Autowired
	private AtmPlanInfoService atmPlanInfoService;
	@Autowired
	private AllocationService allocationService;

	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();

		String addPlanId = (String)paramMap.get(Parameter.ADD_PLAN_ID_KEY);

		// 加钞计划ID校验
		if (StringUtils.isBlank(addPlanId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "计划ID为空！");
			return gson.toJson(respMap);
		}
		// 根据加钞计划ID获取明细
		List<Map<String, Object>> atmPlanDetail = atmPlanInfoService.getPDAPlanDetail(addPlanId);
		respMap.put(Parameter.LIST_KEY, atmPlanDetail);

		// 根据加钞计划ID获取钞箱列表
		List<Map<String, Object>> allocateDetail = allocationService.getPDABoxDetail(addPlanId);
		respMap.put(Parameter.BOX_LIST_KEY, allocateDetail);

		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		return gson.toJson(respMap);
	}

}
