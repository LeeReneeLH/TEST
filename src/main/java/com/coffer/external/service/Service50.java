package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
* Title: Service50
* <p>Description: RFID面值查询</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service50")
@Scope("singleton")
public class Service50 extends HardwardBaseService {
	
	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;
	
	/**
	 * 
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          50：查询RFID与面值绑定信息
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得电文输入参数
		StoRfidDenomination inputParam = getRfidDenominationParam(paramMap);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, inputParam.getErrorFlag());
			return gson.toJson(respMap);
		}

		// 执行查询处理
		List<StoRfidDenomination> rfidDenominationList = stoRfidDenominationService.findListWithStore(inputParam);

		// 编辑RFID返回列表信息
		List<Map<String, Object>> rfidReturnList = stoRfidDenominationService
				.setRfidAndDenominationResult(rfidDenominationList, inputParam);

		respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		respMap.put("rfidList", rfidReturnList);

		return gson.toJson(respMap);
	}
	
	/**
	 * 
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          取得RFID面值查询接口的输入参数
	 * @param requestMap
	 *            输入参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private StoRfidDenomination getRfidDenominationParam(Map<String, Object> requestMap) {

		StoRfidDenomination inputParam = new StoRfidDenomination();

		// 取得机构
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("officeId")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：officeId 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setOfficeId(StringUtils.toString(requestMap.get("officeId")));
		}

		// 取得出入库种别
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("inoutType")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：inoutType 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setInoutType(StringUtils.toString(requestMap.get("inoutType")));
		}

		// 取得RFID列表
		if (null == requestMap.get("rfidList")) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：rfidList 不存在。");
			return inputParam;
		} else {
			inputParam.setRfidList((List<String>) requestMap.get("rfidList"));
		}

		return inputParam;
	}

}
