package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.service.CheckCashService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service0504
* <p>Description: 门店-预约数据明细取得</p>
* @author wanglin
* @date 2017年10月6日 上午10:41:10
*/
@Component("Service0504")
@Scope("singleton")
public class Service0504 extends HardwardBaseService {

	public static final String ERROR_NO_E80 = "E80";
	
	@Autowired
	private CheckCashService checkCashService;
	
	/**
	 *
	 * @author wanglin
	 * @version 2017-10-09
	 *
	 * @Description 门店拆箱数据明细查询接口
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
			CheckCashAmount checkCashcAmount = new CheckCashAmount();
			checkCashcAmount.setOutNo("");
			if (paramMap != null && paramMap.get("outNo") != null
					&& StringUtils.isNotBlank(paramMap.get("outNo").toString())) {
				checkCashcAmount.setOutNo(paramMap.get("outNo").toString());
			}
			List<CheckCashAmount> list = checkCashService.PdaAmountFindList(checkCashcAmount);
			List<Map<String, Object>> resultList = Lists.newArrayList();
			
			//数据存在检查
			if (list == null || list.size() <1){
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ERROR_NO_E80);
				return gson.toJson(map);
			}
			//数据做成
			map.put("outNo", "");
			map.put("custName", "");
			for (CheckCashAmount itemInfo : list) {
				map.put("outNo", itemInfo.getOutNo());						//拆箱单号
				map.put("custName", itemInfo.getCustName());				//客户名称
				Map<String, Object> resultMap = Maps.newHashMap();
				resultMap.put("boxNo", CommonUtils.toString(itemInfo.getPackNum())); //包号
				resultMap.put("inputAmount", itemInfo.getInputAmount());	//录入金额
				resultMap.put("checkAmount", itemInfo.getCheckAmount());	//实际金额
				resultMap.put("diffAmount", itemInfo.getDiffAmount());		//差额
				resultList.add(resultMap);
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
