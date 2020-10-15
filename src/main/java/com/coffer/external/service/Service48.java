package com.coffer.external.service;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
* Title: Service48
* <p>Description: </p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service48")
@Scope("singleton")
public class Service48 extends HardwardBaseService {

	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug("接口：" + this.getClass().getName() + "被执行！");
		logger.debug("接口：" + this.getClass().getName() + "未实现！");
		
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
		respMap.put(Parameter.MESSAGE_INFO, "接口未实现！");
		return gson.toJson(respMap);
	}

}
