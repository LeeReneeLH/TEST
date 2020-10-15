package com.coffer.external.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
 * Title: Service0195
 * <p>
 * Description:
 * </p>
 * 
 * @author liuyaowen
 * @date 2017年8月9日 上午10:41:10
 */
@Component("Service0195")
@Scope("singleton")
public class Service0195 extends HardwardBaseService {
	@Autowired
	StoEscortInfoService escortService;

	@Override
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
		// 加密操作
		StoEscortInfo escortInfo = escortService.findByRfid(rfid);
		if (escortInfo != null) {
			respMap.put(Parameter.OPT_USER_ID_KEY, escortInfo.getId());
			respMap.put(Parameter.OPT_USER_NAME_KEY, escortInfo.getEscortName());
			respMap.put(Parameter.PHOTO_KEY,
					escortInfo.getPhoto() != null ? Encodes.encodeBase64(escortInfo.getPhoto()) : null);
			respMap.put(Parameter.USER_TYPE_KEY, escortInfo.getEscortType());
		} else {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			respMap.put(Parameter.ERROR_MSG_KEY, "未查询到对应人员信息！");
			return gson.toJson(respMap);
		}
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
