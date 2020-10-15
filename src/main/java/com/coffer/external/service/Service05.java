package com.coffer.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service05 
* <p>Description: 采集人员信息接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:36:34
*/
@Component("Service05")
@Scope("singleton")
public class Service05 extends HardwardBaseService {
	
	@Autowired
	private StoEscortInfoService stoEscortInfoService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年6月9日
	 * 
	 *          采集人员信息接口
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
			if (paramMap.get("escortId") != null && StringUtils.isNotEmpty(paramMap.get("escortId").toString())) {
				// 采集人员信息更新状态
				if (stoEscortInfoService.updateEscortInfo(paramMap)) {
					map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
				} else {
					// 人员被删除
					map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E07);
				}
			} else {
				// 参数异常
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}

}
