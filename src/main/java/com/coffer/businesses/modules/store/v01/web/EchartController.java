package com.coffer.businesses.modules.store.v01.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.modules.store.v01.service.StoReportPrintService;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @author niguoyong
 * @date 2015-09-06
 * 
 * @Description 人员管理Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/echart")
public class EchartController extends BaseController {

	@Autowired
	private StoReportPrintService stoReportPrintService;
	
	@RequiresPermissions("store:echart:view")
	@RequestMapping(value = { "pie", "" })
	public String pie(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		return "modules/store/echart/pie";
	}
	
	@RequestMapping(value = { "getData"})
	@SuppressWarnings("unchecked")
	@ResponseBody
	public String getData(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> dataMap = stoReportPrintService.reportMessage();
		List<Map<String,Object>> list = (List<Map<String, Object>>) dataMap.get("list");
		List<Map<String,Object>> outList = (List<Map<String, Object>>) dataMap.get("outlets_list");
		List<String> titleList = Lists.newArrayList();
		List<Map<String,Object>> dataList = Lists.newArrayList();
		Locale locale = LocaleContextHolder.getLocale();
		for(Map<String,Object> temp : list) {
			String name = msg.getMessage(temp.get("name").toString(), null, locale);
			name = "在途"+name;
			titleList.add(name);
			
			Map<String,Object> data = Maps.newHashMap();
			data.put("name", name);
			data.put("value", temp.get("num"));
			dataList.add(data);
		}
		
		for(Map<String,Object> temp : outList) {
			String name = msg.getMessage(temp.get("name").toString(), null, locale);
			name = "在网点"+name;
			titleList.add(name);
			
			Map<String,Object> data = Maps.newHashMap();
			data.put("name", name);
			data.put("value", temp.get("num"));
			dataList.add(data);
		}
		Map<String,Object> jsonData = Maps.newHashMap();
		jsonData.put("title", titleList);
		jsonData.put("data", dataList);
		return new Gson().toJson(jsonData);
	}
}
