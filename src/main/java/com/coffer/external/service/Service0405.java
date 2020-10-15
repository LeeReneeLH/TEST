package com.coffer.external.service;

import java.util.HashMap;
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
* Title: Service0405
* <p>Description: 数字化大屏-数字化金融服务平台业务统计</p>
* @author wanglin
* @date 2018年01月31日 上午10:41:10
*/
@Component("Service0405")
@Scope("singleton")
public class Service0405 extends HardwardBaseService {
	
	@Autowired
	private ClearScreenService clearScreenService;

	
	/**
	 *
	 * @author wanglin
	 * @version 2017-10-09
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
			//查询服务清分业务机构数量
			int intClearCount = clearScreenService.getClearCount(strOfficeId);
			map.put("serviceClearAmount", intClearCount);
	
			//查询服务金库业务机构数量
			int intGoldBankCount = clearScreenService.getGoldBankCount(strOfficeId);
			map.put("serviceCofferAmount", intGoldBankCount);
			
			//查询上门收款门店
			int intDoorCustCount = clearScreenService.getDoorCustCount(strOfficeId);
			//查询上门收款商户 
			int intDoorBusinessCount = clearScreenService.getDoorBusinessCount(strOfficeId);
			map.put("storeAmont", String.valueOf(intDoorCustCount) + "/" + String.valueOf(intDoorBusinessCount));

			//查询服务上门收款
			int intDoorGoldBankCount = clearScreenService.getDoorGoldBankCount(strOfficeId);
			map.put("serviceDoorAmount", intDoorGoldBankCount);

			//查询加钞自助设备(ATM)
			int intAtmCount = clearScreenService.getAtmCount(strOfficeId);
			map.put("atmAmount", intAtmCount);
			
			//查询服务自助设备客户(ATM)
			int intAtmCustCount = clearScreenService.getAtmCustCount(strOfficeId);
			map.put("atmCustAmount", intAtmCustCount);
			
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
