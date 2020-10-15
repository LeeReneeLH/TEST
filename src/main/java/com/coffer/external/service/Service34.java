package com.coffer.external.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
* Title: Service34
* <p>Description: 线路修改接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service34")
@Scope("singleton")
public class Service34 extends HardwardBaseService {

	@Autowired
	private StoRouteInfoService stoRouteInfoService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年12月18日
	 * 
	 *          34：线路维护接口
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			if (paramMap.get("routeId") != null && paramMap.get("escortId1") != null
					&& paramMap.get("escortId2") != null) {
				StoRouteInfo stoRouteInfo = stoRouteInfoService.get(paramMap.get("routeId").toString());
				StoRouteInfo historyStoRouteInfo = stoRouteInfoService.get(paramMap.get("routeId").toString());
				if (stoRouteInfo != null && historyStoRouteInfo != null) {

					StoEscortInfo stoEscortInfo1 = new StoEscortInfo();
					stoEscortInfo1.setId(paramMap.get("escortId1").toString());
					stoRouteInfo.setEscortInfo1(stoEscortInfo1);

					StoEscortInfo stoEscortInfo2 = new StoEscortInfo();
					stoEscortInfo2.setId(paramMap.get("escortId2").toString());
					stoRouteInfo.setEscortInfo2(stoEscortInfo2);

					stoRouteInfoService.updateEscortInfoBinding(stoRouteInfo, historyStoRouteInfo);
					stoRouteInfoService.saveInterface(stoRouteInfo);

					respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
				} else {
					// 线路被删除
					respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					respMap.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E26);
				}
			} else {
				// 参数异常
				respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			e.printStackTrace();
			respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(respMap);
		}
		return gson.toJson(respMap);
	}

}
