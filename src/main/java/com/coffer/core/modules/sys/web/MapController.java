package com.coffer.core.modules.sys.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.MapService;
import com.coffer.core.modules.sys.utils.UserUtils;


/**
 * 
* Title: MapController 
* <p>Description: 地图相关的控制层</p>
* @author wanghan
* @date 2017年11月28日 上午11:15:13
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/map")
public class MapController extends BaseController{
	
	private static final String MAP_SHOW_TYPE_ROUTE = "routeType";
	
	private static final String MAP_SHOW_TYPE_ALLOCATE = "allocateType";
	
	private static final String MAP_SHOW_TYPE_KEY = "showType";
	
	@Autowired
	private MapService mapservice;
	
	/**
	 * 
	 * Title: getMapGraphData
	 * <p>Description: 获取首页工作台地图数据</p>
	 * @author:     wanghan
	 * @return  地图数据以json形式返回
	 * String    返回类型
	 */
	@RequestMapping(value = "getMapGraphData")
	@ResponseBody
	public String getMapGraphData() {

		Map<String, Object> jsonData = mapservice.makeMapGraphData();

		return gson.toJson(jsonData);
	}
	
	/**
	 * 
	 * Title: getRouteLineData
	 * <p>Description: 查询当前用户线路信息</p>
	 * @author:     wangbaozhong
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "getRouteLineData")
	@ResponseBody
	public String getOnLineData() {
		// 线路信息查询
		Map<String, Object> dataMap = null; 
		User curUser = UserUtils.getUser();
		// 平台用户 或金库显示线路
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(curUser.getOffice().getType()) || Constant.OfficeType.COFFER.equals(curUser.getOffice().getType())) {
			dataMap = mapservice.getRouteLineData();
			dataMap.put(MAP_SHOW_TYPE_KEY, MAP_SHOW_TYPE_ROUTE);
		} else {
			dataMap = mapservice.getAllRelData();
			dataMap.put(MAP_SHOW_TYPE_KEY, MAP_SHOW_TYPE_ALLOCATE);
		}
			
		return gson.toJson(dataMap);
	}
	
	//地图经纬度验证，暂时不用
	/*@RequestMapping(value = "checkLongiLati")
	@ResponseBody
	public String checkLongiLati(String longitude,String latitude) {
		
		if((StringUtils.isBlank(longitude) && StringUtils.isNotBlank(latitude)) || (StringUtils.isBlank(latitude) && StringUtils.isNotBlank(longitude))) {
			return "false";
		}else {
			return "true";
		}
	}*/
}
