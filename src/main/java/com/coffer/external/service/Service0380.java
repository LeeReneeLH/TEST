package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.atm.ATMConstant;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
 * Title: Service0380
 * <p>
 * Description:PDA查询加钞计划ID接口
 * </p>
 * 
 * @author wxz
 * @date 2017年11月7日 下午15:37:10
 */
@Component("Service0380")
@Scope("singleton")
public class Service0380 extends HardwardBaseService {


	@Autowired
	private OfficeService officeService;
	@Autowired
	private AtmPlanInfoService atmPlanInfoService;


	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();

		// 判断机构ID校验
		String officeId = (String)paramMap.get(Parameter.OFFICE_ID_KEY);
		String searchType = (String) paramMap.get(Parameter.SEARCH_TYPE_KEY);
		Office office = officeService.get(officeId);
		if (office == null || StringUtils.isBlank(office.getName())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "机构不存在！");
			respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
			respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
			return gson.toJson(respMap);
		} 
		// 判断查询类型校验
		if (StringUtils.isBlank(searchType)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "查询类型为空！");
			respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
			respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
			return gson.toJson(respMap);
		}
		// 获取对应机构下的加钞计划
		Map<String, Object> dataMap = Maps.newHashMap();
		dataMap.put(Parameter.OFFICE_ID_KEY, officeId);
		dataMap.put(Parameter.DB_NAME_KEY, Global.getConfig("jdbc.type"));
		// 判断查询类型是否不为空且为1(清分查询)
		if (StringUtils.isNotBlank(searchType) && ATMConstant.SearchType.CLEAR_SEARCH.equals(searchType)) {
			dataMap.put(Parameter.STATUS_KEY, ATMConstant.PlanStatus.PLAN_CREATE);
		}
		// 判断查询类型是否不为空且为0(押运查询)
		if (StringUtils.isNotBlank(searchType) && ATMConstant.SearchType.ESCORT_SEARCH.equals(searchType)) {
			dataMap.put(Parameter.STATUS_KEY, ATMConstant.PlanStatus.PLAN_OUT);
			dataMap.put(Parameter.TODAY_KEY, DateUtils.getDate());
		}

		List<Map<String, Object>> atmPlanList = atmPlanInfoService.getPDAPlanList(dataMap);
		respMap.put(Parameter.LIST_KEY, atmPlanList);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		return gson.toJson(respMap);
	}

}
