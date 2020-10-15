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

import com.coffer.businesses.modules.doorOrder.v01.entity.DepositCensusReport;
import com.coffer.businesses.modules.doorOrder.v01.service.DepositCensusReportService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 整体存款情况分析报表Controller
 *
 * @author lihe
 * @version 2020-01-07
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/depositCensusReport")
public class DepositCensusReportController extends BaseController {

	@Autowired
	private DepositCensusReportService depositCensusReportService;

	@RequiresPermissions("doorOrder:depositCensusReport:view")
	@RequestMapping(value = { "list", "" })
	public String list(DepositCensusReport depositCensusReport, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<DepositCensusReport> page = depositCensusReportService
				.findPage(new Page<DepositCensusReport>(request, response), depositCensusReport);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/DepositCensusReport/depositCensusReportList";
	}

	@RequiresPermissions("doorOrder:depositCensusReport:export")
	@RequestMapping(value = "export")
	public String export(DepositCensusReport depositCensusReport, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		try {
			// 存款情况分析列表
			List<DepositCensusReport> depositCensusList = depositCensusReportService.findList(depositCensusReport);
			List<DepositCensusReport> poolList = depositCensusReportService.getDepositPool(depositCensusList);
			depositCensusList.addAll(poolList);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(depositCensusList)) {
				depositCensusList.add(new DepositCensusReport());
			}
			// 模板文件名 /存款情况分析报表.xls
			String fileName = msg.getMessage("door.depositCensusReport.listTemplate", null, locale);
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.depositCensusReport.list", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DepositCensusReport.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, depositCensusList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.depositCensusReport.list", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return list(depositCensusReport, request, response, model);
		}
		return null;
	}
}