package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoRouteDetail;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service33
* <p>Description: 线路查询接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service33")
@Scope("singleton")
public class Service33 extends HardwardBaseService {

	@Autowired
	private StoRouteInfoService stoRouteInfoService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年12月18日
	 * 
	 *          33：线路查询接口
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			String routeName = paramMap.get("routeName") != null ? paramMap.get("routeName").toString() : "";
			// 设置押运人员
			String escortId = paramMap.get("escortId") != null ? paramMap.get("escortId").toString() : "";
			// 设置机构
			String offcieId = paramMap.get("officeId") != null ? paramMap.get("officeId").toString() : "";
			Office office = new Office();
			office.setId(offcieId);

			StoRouteInfo stoRouteInfo = new StoRouteInfo();
			stoRouteInfo.setRouteName(routeName);
			stoRouteInfo.setEscortId(escortId);
			stoRouteInfo.setOffice(office);

			List<StoRouteInfo> findRoute = stoRouteInfoService.findInterfaceList(stoRouteInfo);
			List<Map<String, Object>> routeList = Lists.newArrayList();
			for (StoRouteInfo stoRouteInfoTemp : findRoute) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("routeId", stoRouteInfoTemp.getId());
				map.put("routeName", stoRouteInfoTemp.getRouteName());
				map.put("officeNum", stoRouteInfoTemp.getDetailNum());
				if (stoRouteInfoTemp.getEscortInfo1() != null) {
					map.put("escortId1", stoRouteInfoTemp.getEscortInfo1().getId());
					map.put("escortName1", stoRouteInfoTemp.getEscortInfo1().getEscortName());
				} else {
					map.put("escortId1", "");
					map.put("escortName1", "");
				}
				if (stoRouteInfoTemp.getEscortInfo2() != null) {
					map.put("escortId2", stoRouteInfoTemp.getEscortInfo2().getId());
					map.put("escortName2", stoRouteInfoTemp.getEscortInfo2().getEscortName());
				} else {
					map.put("escortId2", "");
					map.put("escortName2", "");
				}
				List<Map<String, Object>> offcieList = Lists.newArrayList();
				for (StoRouteDetail stoRouteDetail : stoRouteInfoTemp.getStoRouteDetailList()) {
					Map<String, Object> officeMap = Maps.newHashMap();
					officeMap.put("officeId", stoRouteDetail.getOffice().getId());
					officeMap.put("officeName", stoRouteDetail.getOffice().getName());
					officeMap.put("tradeFlag", stoRouteDetail.getOffice().getTradeFlag());
					offcieList.add(officeMap);
				}
				map.put("officeList", offcieList);
				routeList.add(map);
			}
			respMap.put("routeList", routeList);

			respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			return gson.toJson(respMap);
		}
		return gson.toJson(respMap);
	}

}
