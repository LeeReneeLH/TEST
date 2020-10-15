package com.coffer.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service04 
* <p>Description: 人员身份信息验证接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:36:17
*/
@Component("Service04")
@Scope("singleton")
public class Service04 extends HardwardBaseService {

	@Autowired
	private StoEscortInfoService stoEscortInfoService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年6月9日
	 * 
	 *          人员查询接口
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		String escortJson = "";
		try {
			if (paramMap.get("idcardNo") != null && StringUtils.isNotEmpty(paramMap.get("idcardNo").toString())) {
				StoEscortInfo stoescortinfo = stoEscortInfoService.searchEscort(paramMap);
				if (stoescortinfo != null) {
					escortJson = gson.toJson(stoescortinfo);
					// officeId
					map.put("officeId", stoescortinfo.getOffice().getId());
					map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
				} else {
					// 人员信息不存在
					map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E05);
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

		String json = gson.toJson(map);

		if (StringUtils.isNotEmpty(escortJson)) {
			// 押运人员存在拼接json
			String jsonHead = escortJson.substring(0, escortJson.length() - 1);
			String jsonEnd = json.substring(1, json.length());

			json = jsonHead + "," + jsonEnd;
		}

		// json
		return json;
	}

}
