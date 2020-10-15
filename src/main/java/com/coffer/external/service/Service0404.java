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
* Title: Service0404
* <p>Description: 数字化大屏-上门收款服务统计</p>
* @author wanglin
* @date 2018年01月31日 上午10:41:10
*/
@Component("Service0404")
@Scope("singleton")
public class Service0404 extends HardwardBaseService {
	
	@Autowired
	private ClearScreenService clearScreenService;
	/**
	 *
	 * @author wanglin
	 * @version 2017-10-09
	 *
	 * @Description 上门收款服务统计
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		double countSum = 0;
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

			List<Map<String, Object>> doorList = Lists.newArrayList();

			//上门收款列表
			doorList = clearScreenService.findDoorOrderList(strOfficeId);
			map.put("businessList", doorList);
			
			//自助设备总数
			for (int j = 0; j < doorList.size(); j++) {
				countSum  =countSum + Double.valueOf((String) doorList.get(j).get("businessAmount"));
				
			}
			map.put("businessCount", String.valueOf(countSum));
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
