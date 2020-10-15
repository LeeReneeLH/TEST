package com.coffer.businesses.modules.doorOrder.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryChange;
import com.coffer.businesses.modules.doorOrder.v01.service.HistoryChangeService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import freemarker.core.ParseException;

/**
 * 历史更换记录Controller
 * 
 * @author ZXK
 * @version 2019-10-31
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/historyChange")
public class HistoryChangeController extends BaseController {

	@Autowired
	private HistoryChangeService historyChangeService;

	/**
	 * 机具历史更换记录
	 * 
	 * @author ZXK
	 * @version 2019年10月30日
	 * @param historyChange
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 */
	@RequiresPermissions("doorOrder:historyChange:view")
	@RequestMapping(value = { "list", "" })
	public String historyChangeList(HistoryChange historyChange, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<HistoryChange> page = historyChangeService.findHistoryChangePage(new Page<HistoryChange>(request, response), historyChange);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/clearAddMoney/historyChangeList";
	}
	
	/**
	 * 机具历史更换记录明细列表
	 * 
	 * @author ZXK
	 * @version 2019年10月30日
	 * @param historyChange
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 */
	@RequiresPermissions("doorOrder:historyChange:view")
	@RequestMapping(value = "historyChangeInfo")
	public String historyChangeInfo(HistoryChange historyChange, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<HistoryChange> page = historyChangeService.gitHistoryChangePageDetail(new Page<HistoryChange>(request, response), historyChange);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/clearAddMoney/historyChangeDetail";
	}
	
	/**
	 * Excel导出主查询处理
	 * 
	 * @author ZXK
	 * @version 2019-10-31
	 * @param empWorkDetail
	 * @return
	 */
	@RequestMapping(value = "exportExcel")
	public String exportExcel(HistoryChange historyChange, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes, Model model)
			throws FileNotFoundException, ParseException, IOException {
		try {
		Locale locale = LocaleContextHolder.getLocale();
		// 设置标题信息
		Map<String, Object> titleMap = Maps.newHashMap();
		// 制表机构
		titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
		// 制表时间
		titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
		// 查询
		List<HistoryChange> list = historyChangeService.findExcelList(historyChange);
		// 列表为空
		if (Collections3.isEmpty(list)) {
			list.add(new HistoryChange());
		}
		// 模板文件名
		String fileName = msg.getMessage("report.historyChange.excel", null, locale)+".xls";
		
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("report.historyChange.excel", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, HistoryChange.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
	
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"历史更改记录" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}
	catch (Exception e){
		return historyChangeList(historyChange,request,response,model);
	}
		return null;
	}
	/**
	 * Excel导出明细处理
	 * 
	 * @author ZXK
	 * @version 2019-10-31
	 * @param empWorkDetail
	 * @return
	 */
	@RequestMapping(value = "exportDetail")
	public String exportDetail(HistoryChange historyChange, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes, Model model)
			throws FileNotFoundException, ParseException, IOException {
		try {
		Locale locale = LocaleContextHolder.getLocale();
		// 设置标题信息
		Map<String, Object> titleMap = Maps.newHashMap();
		// 制表机构
		titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
		// 制表时间
		titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
		// 查询
		List<HistoryChange> list = historyChangeService.findExcelListDetail(historyChange);
		// 列表为空
				if (Collections3.isEmpty(list)) {
					list.add(new HistoryChange());
				}
		// 模板文件名
		String fileName = msg.getMessage("report.historyChangeDetail.excel", null, locale)+".xls";
		
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("report.historyChangeDetail.excel", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, HistoryChange.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
	
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"更改记录详细" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}
	catch (Exception e){
		return historyChangeList(historyChange,request,response,model);
	}
		return null;
	}
}
