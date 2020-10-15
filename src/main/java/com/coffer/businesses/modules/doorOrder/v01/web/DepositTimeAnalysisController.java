package com.coffer.businesses.modules.doorOrder.v01.web;

import java.util.Date;
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

import com.coffer.businesses.modules.doorOrder.v01.entity.DepositTimeAnalysis;
import com.coffer.businesses.modules.doorOrder.v01.service.DepositTimeAnalysisService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 存款时间分析报表Controller
 * 
 * @author gzd
 * @version 2020-01-15
 */

@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/depositTimeAnalysis")
public class DepositTimeAnalysisController extends BaseController  {
	
	@Autowired
	private DepositTimeAnalysisService depositTimeAnalysisService;

	@RequiresPermissions("doorOrder:depositTimeAnalysis:view")
	@RequestMapping(value = { "list", "" })
	public String list(DepositTimeAnalysis depositTimeAnalysis, HttpServletRequest request,
			HttpServletResponse response, Model model) {	
		Page<DepositTimeAnalysis> page = depositTimeAnalysisService.findPage(new Page<DepositTimeAnalysis>(request, response), depositTimeAnalysis);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/DepositTimeAnalysis/depositTimeAnalysisList";
	}
	
	@RequiresPermissions("doorOrder:depositTimeAnalysis:export")
	@RequestMapping(value = "export")
	public String export(DepositTimeAnalysis depositTimeAnalysis, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		try {
			// 存款情况分析列表
			List<DepositTimeAnalysis> depositTimeAnalysisList = depositTimeAnalysisService.findList(depositTimeAnalysis);
			List<DepositTimeAnalysis> poolList = depositTimeAnalysisService.getDepositPool(depositTimeAnalysisList);
			depositTimeAnalysisList.addAll(poolList);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(depositTimeAnalysisList)) {
				depositTimeAnalysisList.add(new DepositTimeAnalysis());
			}
			// 模板文件名 /存款时间分析报表.xls
			String fileName = msg.getMessage("door.depositTimeAnalysis.listTemplate", null, locale);
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.depositTimeAnalysis.list", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DepositTimeAnalysis.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, depositTimeAnalysisList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.depositTimeAnalysis.list", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return list(depositTimeAnalysis, request, response, model);
		}
		return null;
	}
}
