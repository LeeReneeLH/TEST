package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.screen.v03.service.ClearScreenService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
 * Title: Service0401
 * <p>
 * Description: 数字化大屏-同步机构信息
 * </p>
 * 
 * @author qph
 * @date 2018年02月1日 下午13:20:10
 */
@Component("Service0401")
@Scope("singleton")
public class Service0401 extends HardwardBaseService {
	
	@Autowired
	private ClearScreenService clearScreenService;
	
	/**
	 *
	 * @author qph
	 * @version 2017-02-01
	 *
	 * @Description 同步机构信息
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
			List<Map<String, Object>> officeList = clearScreenService.getOfficeListByService();
			map.put("officeList", officeList);
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
