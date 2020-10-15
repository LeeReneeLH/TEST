package com.coffer.external.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service0501
* <p>Description: 门店-预约数据取得</p>
* @author wanglin
* @date 2017年10月6日 上午10:41:10
*/
@Component("Service0501")
@Scope("singleton")
public class Service0501 extends HardwardBaseService {
	
	@Autowired
	private DoorOrderInfoService doorOrderInfoService;
	
	/**
	 *
	 * @author wanglin
	 * @version 2017-10-09
	 *
	 * @Description 门店预约查询接口
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
			String strDoorId = "";
			if (paramMap != null && paramMap.get("custId") != null
					&& StringUtils.isNotBlank(paramMap.get("custId").toString())) {
				strDoorId = paramMap.get("custId").toString();
			}
			//获取到系统当前时间
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = formatter.format(currentTime);

			//获取该门店当天的所有请求
			List<Map<String, Object>> resultList = Lists.newArrayList();
			DoorOrderInfo doorOrderInfo1=doorOrderInfoService.getByorderdate(dateString, strDoorId);
			map.put("keyId", "");		//主键
			map.put("orderId", "");		//预约单号
			if (doorOrderInfo1 != null){
				map.put("keyId", doorOrderInfo1.getId());				//主键
				map.put("orderId", doorOrderInfo1.getOrderId());		//预约单号
				String[] amountList = doorOrderInfo1.getAmountList().split(",",-1);
				String[] rfidList = doorOrderInfo1.getRfidList().split(",",-1);
				if (amountList.length >0){
					for(int i=1;i<amountList.length;i++) {
						Map<String, Object> resultMap = Maps.newHashMap();
						resultMap.put("amount", amountList[i]);			//金额
						resultMap.put("boxNo", CommonUtils.toString(rfidList[i]));			//包号
						resultList.add(resultMap);
					}
				}

				
			}
			map.put("list", resultList);
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
