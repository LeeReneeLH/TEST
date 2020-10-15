package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.screen.v03.service.ClearScreenService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
* Title: Service0403
* <p>Description: 数字化大屏-自助设备服务统计</p>
* @author wanglin
* @date 2018年01月31日 上午10:41:10
*/
@Component("Service0403")
@Scope("singleton")
public class Service0403 extends HardwardBaseService {
	
	@Autowired
	private ClearScreenService clearScreenService;

	/**
	 *
	 * @author wanglin
	 * @version 2017-10-09
	 *
	 * @Description 自助设备服务统计
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Long countSum = (long) 0;
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			String strOfficeId = "";
			if (paramMap != null && paramMap.get("officeId") != null
					&& StringUtils.isNotBlank(paramMap.get("officeId").toString())) {
				strOfficeId = paramMap.get("officeId").toString();
			}

			List<Map<String, Object>> AtmList = Lists.newArrayList();

			//自助设备列表
			AtmList = clearScreenService.findAtmCountList(strOfficeId);
			map.put("atmList", AtmList);
			
			//自助设备总数
			for (int j = 0; j < AtmList.size(); j++) {
				countSum  =countSum + Long.valueOf((String) AtmList.get(j).get("atmAmount"));
				
			}
			map.put("atmCount", String.valueOf(countSum));
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		return gson.toJson(map);
	}

}
