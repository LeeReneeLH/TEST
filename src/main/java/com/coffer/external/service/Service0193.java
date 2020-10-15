package com.coffer.external.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
 * Title: Service0193
 * <p>
 * Description:
 * </p>
 * 人员rfid绑定同步接口
 * 
 * @author liuyaowen
 * @date 2017年8月9日 上午10:41:10
 */
@Component("Service0193")
@Scope("singleton")
public class Service0193 extends HardwardBaseService {
	@Autowired
	StoEscortInfoService escortService;
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		String idcardNo = (String) paramMap.get(Parameter.ID_CARD_NO_KEY);
		String loginName = (String) paramMap.get(Parameter.LOGIN_NAME_KEY);
		if (idcardNo == null && loginName == null) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "登录名和身份证号不可都为空！");
			return gson.toJson(respMap);
		}
		String idCard = "";

		if (!StringUtils.isEmpty(idcardNo)) {
			idCard = idcardNo;
		}
		if (!StringUtils.isEmpty(loginName)) {
			User user = UserUtils.getByLoginName(loginName);
			idCard = user.getIdcardNo();
		}
		StoEscortInfo escort = escortService.findLikeByIdcardNo(idCard);
		if (escort == null) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "人员信息不存在，请确认身份证号是否正确！");
			return gson.toJson(respMap);
		}
		respMap.put(Parameter.STORE_ESCORT_RFID, escort.getRfid());
		respMap.put(Parameter.OPT_USER_NAME_KEY, escort.getEscortName());
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
