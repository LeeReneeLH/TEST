package com.coffer.businesses.modules.AMap.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.modules.AMap.entity.AMap;
import com.coffer.businesses.modules.AMap.service.AMapService;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;

/**
 * 
 * @author liuyaowen
 * @version
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/AMap")
public class AMapController extends BaseController {
	@Autowired
	private AMapService service;
	/**
	 * 
	 * @author liuyaowen
	 * @version 20171016
	 * 
	 *          接收ajax请求
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "initMap")
	public String initMap(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<AMap> mapList = service.findMap();
		List<String> positionList = Lists.newArrayList();
		for (AMap map : mapList) {
			positionList.add(map.getLongitude() + ";" + map.getLatitude());
		}
		model.addAttribute("positionList", positionList);
		return "modules/AMap/map";
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 20171016
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "dynamicMap")
	public String dynamicMap(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<AMap> mapList = service.findMap();
		List<String> positionList = Lists.newArrayList();
		for (AMap map : mapList) {
			positionList.add(map.getLongitude() + "," + map.getLatitude());
		}
		model.addAttribute("positionList", positionList);
		return "modules/AMap/dynamicConditionMap";
	}

	/**
	 * 撤销预约
	 * 
	 * @param cancelParam
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "cancel", produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> cancel(HttpServletRequest request, HttpServletResponse response) {
		List<AMap> mapList = service.findMap();
		List<String> positionList = Lists.newArrayList();
		for (AMap map : mapList) {
			positionList.add(map.getLongitude() + "," + map.getLatitude());
		}
		return positionList;
	}

}
