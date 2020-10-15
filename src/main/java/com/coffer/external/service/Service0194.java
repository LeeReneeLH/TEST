package com.coffer.external.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
 * Title: Service0194
 * <p>
 * Description:
 * </p>
 * 
 * @author liuyaowen
 * @date 2017年8月9日 上午10:41:10
 */
@Component("Service0194")
@Scope("singleton")
public class Service0194 extends HardwardBaseService {
	@Autowired
	StoEscortInfoService escortService;
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		String rfid = (String) paramMap.get(Parameter.STORE_ESCORT_RFID);
		if (StringUtils.isBlank(rfid)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "rfid不可以为空！");
			return gson.toJson(respMap);
		}
		// 登记人ID
		String userId = (String) paramMap.get(Parameter.USER_ID_KEY);
		if (userId == null || StringUtils.isBlank(userId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "userId不可以为空！");
			return gson.toJson(respMap);
		}

		// 登记人姓名
		String userName = (String) paramMap.get(Parameter.USER_NAME_KEY);
		if (userName == null || StringUtils.isBlank(userName)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "userName不可以为空！");
			return gson.toJson(respMap);
		}
		StoEscortInfo escortInfo = new StoEscortInfo();
		User user = new User();
		user.setId(userId);
		user.setName(userName);
		escortInfo.setUpdateBy(user);
		escortInfo.setUpdateDate(new Date());
		escortInfo.setRfid(rfid);
		escortService.updateBindingRfid(escortInfo);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
