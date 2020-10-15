package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service40
* <p>Description: 同步押运人员信息</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service40")
@Scope("singleton")
public class Service40 extends HardwardBaseService {

	/**
	 *
	 * @author LF
	 * @version 2015-06-09
	 *
	 * @Description 押运人员批量查询接口
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
			List<StoEscortInfo> list = StoreCommonUtils.getStoEscortinfoList(Constant.Escort.ALL_ESCORT_LIST);
			List<Map<String, Object>> escortList = Lists.newArrayList();
			for (StoEscortInfo stoEscortInfo : list) {
				Map<String, Object> escortMap = Maps.newHashMap();
				escortMap.put("escortId", stoEscortInfo.getId());
				escortMap.put("escortName", stoEscortInfo.getEscortName());
				escortMap.put("bindingRoute", stoEscortInfo.getBindingRoute());
//				escortMap.put("officeId", stoEscortInfo.getOffice().getId());
//				escortMap.put("officeName", stoEscortInfo.getOffice().getName());
				escortList.add(escortMap);
			}
			map.put("list", escortList);
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
