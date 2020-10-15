package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0701
 * <p>
 * Description:清分人员查询接口
 * </p>
 * 
 * @author XL
 * @date 2019年7月3日
 */
@Component("Service0701")
@Scope("singleton")
public class Service0701 extends HardwardBaseService {

	/**
	 * 清分人员查询接口
	 *
	 * @author XL
	 * @version 2019年7月3日
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
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
		// 机构编号
		String officeId = paramMap.get(Parameter.OFFICE_ID_KEY).toString();
		// 查询人员信息列表
		List<StoEscortInfo> escortList = StoreCommonUtils
				.getUsersByTypeAndOffice(Constant.SysUserType.CLEARING_CENTER_OPT, officeId);
		// 返回列表
		List<Map<String, String>> clearManList = Lists.newArrayList();
		for (StoEscortInfo stoEscortInfo : escortList) {
			Map<String, String> stoEscortMap = Maps.newHashMap();
			// 人员编号
			stoEscortMap.put(Parameter.OPT_USER_ID_KEY, stoEscortInfo.getId());
			// 人员名称
			stoEscortMap.put(Parameter.OPT_USER_NAME_KEY, stoEscortInfo.getEscortName());
			clearManList.add(stoEscortMap);
		}
		map.put(Parameter.CLEARMAN_LIST_KEY, clearManList);
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
	private String checkParam(Map<String, Object> paramMap) {
		String errorMsg = "";
		// 机构编号
		if (paramMap.get(Parameter.OFFICE_ID_KEY) == null
				|| StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)));
			// 参数异常
			errorMsg = "参数错误--------officeId:" + CommonUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
			return errorMsg;
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
