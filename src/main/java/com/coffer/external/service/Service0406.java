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

/**
 * Title: Service0401
 * <p>
 * Description: 数字化大屏-数字化金融服务平台业务统计
 * </p>
 * 
 * @author qph
 * @date 2018年02月1日 下午13:20:10
 */
@Component("Service0406")
@Scope("singleton")
public class Service0406 extends HardwardBaseService {
	
	@Autowired
	private ClearScreenService clearScreenService;
	
	/**
	 *
	 * @author qph
	 * @version 2017-02-05
	 *
	 * @Description 数字化金融服务平台业务统计
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
			String strOfficeId = "";
			if (paramMap != null && paramMap.get("officeId") != null
					&& StringUtils.isNotBlank(paramMap.get("officeId").toString())) {
				strOfficeId = paramMap.get("officeId").toString();
			}
			List<Map<String, Object>> list = clearScreenService.getAllAmountList(strOfficeId);
			map.put("List", list);
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
