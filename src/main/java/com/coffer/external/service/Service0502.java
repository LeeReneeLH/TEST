package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.collection.v03.service.CheckCashService;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service0502
* <p>Description: 门店拆箱数据取得</p>
* @author wanglin
* @date 2017年10月6日 上午10:41:10
*/
@Component("Service0502")
@Scope("singleton")
public class Service0502 extends HardwardBaseService {
	
	@Autowired
	private CheckCashService checkCashService;
	
	/**
	 *
	 * @author wanglin
	 * @version 2017-10-09
	 *
	 * @Description 门店拆箱数据查询接口
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
			CheckCashMain checkCashMain = new CheckCashMain();
			checkCashMain.setCustNo("");
			if (paramMap != null && paramMap.get("custId") != null
					&& StringUtils.isNotBlank(paramMap.get("custId").toString())) {
				checkCashMain.setCustNo(paramMap.get("custId").toString());
			}
			
			
			List<CheckCashMain> list = checkCashService.PdaMainFindList(checkCashMain);
			List<Map<String, Object>> resultList = Lists.newArrayList();
			if (list != null){
				for (CheckCashMain itemInfo : list) {
					Map<String, Object> resultMap = Maps.newHashMap();
					resultMap.put("outNo", itemInfo.getOutNo());				//拆箱单号
					resultMap.put("inputAmount", itemInfo.getInputAmount());	//录入金额
					resultMap.put("checkAmount", itemInfo.getCheckAmount());	//实际金额
					resultMap.put("diffAmount", itemInfo.getDiffAmount());		//差额
					resultList.add(resultMap);
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
