package com.coffer.businesses.modules.doorOrder.v01.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.EchartInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentWarnings;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;
import com.coffer.businesses.modules.doorOrder.v01.service.EchartsClientService;
import com.coffer.businesses.modules.doorOrder.v01.service.EchartsDoorService;
import com.coffer.businesses.modules.doorOrder.v01.service.EchartsInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentWarningsService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;

/**
 * 中心首页数据统计Controller
 * 
 * @author lihe
 * @version 2019-07-08
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/fristPage")
public class FristPageController extends BaseController {

	@Autowired
	private EchartsInfoService echartsInfoService;

	@Autowired
	private EchartsClientService echartsClientService;

	@Autowired
	private EchartsDoorService echartsDoorService;
	
	@Autowired
	private EquipmentWarningsService equipmentWarningsService;

	/** 中心首页 start */
	@RequestMapping(value = { "list", "" })
	public String list(EchartInfo echartInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<DoorCenterAccountsMain> errorList = echartsInfoService.getDepositErrorForDay();
		int count = 0;
		if (Collections3.isEmpty(errorList)) {
			errorList = Lists.newArrayList();
		} else {
			for (DoorCenterAccountsMain doorMain : errorList) {
				count += doorMain.getErrorCount();
			}
		}
		OfficeAmount officeAmount = echartsInfoService.getTabsData();
		EquipmentWarnings warning = new EquipmentWarnings();
		List<EquipmentWarnings> warningList = equipmentWarningsService.findWarningList(warning);
		officeAmount.setWholeCount(count);
		model.addAttribute("officeAmount", officeAmount);
		model.addAttribute("depositErrorList", errorList);
		model.addAttribute("warningList", warningList);
		return "modules/doorOrder/v01/firstPage/centerFirstPage";
	}

	@RequestMapping(value = "centerBar")
	@ResponseBody
	public String getCenterBarData() {
		Map<String, Object> jsonData = echartsInfoService.getCenterBarData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "centerPie")
	@ResponseBody
	public String getCenterPieData(String merchantId) {
		Map<String, Object> jsonData = echartsInfoService.getCenterPieData(merchantId);
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "centerLine")
	@ResponseBody
	public String getCenterLineData() {
		Map<String, Object> jsonData = echartsInfoService.getCenterLineData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "centerWeekPie")
	@ResponseBody
	public String getCenterWeekPieData() {
		Map<String, Object> jsonData = echartsInfoService.getCenterWeekData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "centerEquipmentCounts")
	@ResponseBody
	public String getCenterEquipmentCounts() {
		Map<String, Object> jsonData = echartsDoorService.getCenterEquipmentCounts();
		return gson.toJson(jsonData);
	}

	/** 中心首页 end */

	/** 商户首页 start */
	@RequestMapping(value = { "clientChart" })
	public String clientChart(EchartInfo echartInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		OfficeAmount officeAmount = echartsClientService.getTabsData();
		List<DoorCenterAccountsMain> errorList = echartsClientService.getClientErrorForDay();
		EquipmentWarnings warning = new EquipmentWarnings();
		List<EquipmentWarnings> warningList = equipmentWarningsService.findWarningList(warning);
		int count = 0;
		if (Collections3.isEmpty(errorList)) {
			errorList = Lists.newArrayList();
		} else {
			for (DoorCenterAccountsMain doorMain : errorList) {
				count += doorMain.getErrorCount();
			}
		}
		officeAmount.setWholeCount(count);
		model.addAttribute("depositErrorList", errorList);
		model.addAttribute("officeAmount", officeAmount);
		model.addAttribute("warningList", warningList);
		return "modules/doorOrder/v01/firstPage/clientFirstPage";
	}

	@RequestMapping(value = "clientBar")
	@ResponseBody
	public String getClientBarData() {
		Map<String, Object> jsonData = echartsClientService.getClientBarData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "clientPie")
	@ResponseBody
	public String getClientPieData(String merchantId) {
		Map<String, Object> jsonData = echartsClientService.getClientPieData(merchantId);
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "clientLine")
	@ResponseBody
	public String getClientLineData() {
		Map<String, Object> jsonData = echartsClientService.getClientLineData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "clientWeekPie")
	@ResponseBody
	public String getClientWeekPieData() {
		Map<String, Object> jsonData = echartsClientService.getClientWeekData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "clientEquipmentCounts")
	@ResponseBody
	public String getClientEquipmentCounts() {
		Map<String, Object> jsonData = echartsDoorService.getClientEquipmentCounts();
		return gson.toJson(jsonData);
	}

	/** 商户首页 end */

	/** 门店首页 start */
	@RequestMapping(value = { "doorChart" })
	public String doorChart(EchartInfo echartInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		OfficeAmount officeAmount = echartsDoorService.getTabsData();
		List<DoorCenterAccountsMain> errorList = echartsDoorService.getDoorErrorForDay();
		EquipmentWarnings warning = new EquipmentWarnings();
		List<EquipmentWarnings> warningList = equipmentWarningsService.findWarningList(warning);
		int count = 0;
		if (Collections3.isEmpty(errorList)) {
			errorList = Lists.newArrayList();
		} else {
			for (DoorCenterAccountsMain doorMain : errorList) {
				count += doorMain.getErrorCount();
			}
		}
		officeAmount.setWholeCount(count);
		model.addAttribute("depositErrorList", errorList);
		model.addAttribute("officeAmount", officeAmount);
		model.addAttribute("warningList", warningList);
		return "modules/doorOrder/v01/firstPage/doorFirstPage";
	}

	@RequestMapping(value = "doorBar")
	@ResponseBody
	public String getDoorBarData() {
		Map<String, Object> jsonData = echartsDoorService.getDoorBarData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "doorPie")
	@ResponseBody
	public String getDoorPieData() {
		Map<String, Object> jsonData = echartsDoorService.getDoorPieData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "doorLine")
	@ResponseBody
	public String getDoorLineData() {
		Map<String, Object> jsonData = echartsDoorService.getDoorLineData();
		return gson.toJson(jsonData);
	}

	@RequestMapping(value = "doorEquipmentCounts")
	@ResponseBody
	public String getDoorEquipmentCounts() {
		Map<String, Object> jsonData = echartsDoorService.getDoorEquipmentCounts();
		return gson.toJson(jsonData);
	}
	/** 门店首页 end */

}