package com.coffer.businesses.modules.collection.v03.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.collection.v03.entity.CustWorkDay;
import com.coffer.businesses.modules.collection.v03.service.CustWorkDayService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * 客户清点量(日)统计Controller
 * 
 * @author wanglin
 * @version 2017-09-04
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/custWorkDay")
public class CustWorkDayController extends BaseController {

	@Autowired
	private CustWorkDayService custWorkDayService;

	
	@ModelAttribute
	public CustWorkDay get(@RequestParam(required = false) String detailId) {
		CustWorkDay entity = null;
		entity = new CustWorkDay();
		return entity;
	}

	/**
	 * 根据查询条件，查询统计一览信息
	 * @param custWorkDay
	 * @return 统计列页面表
	 */
	@RequiresPermissions("collection:custWorkDay:view")
	@RequestMapping(value = { "list", "" })
	public String list(CustWorkDay custWorkDay, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<CustWorkDay> page = custWorkDayService.findPage(new Page<CustWorkDay>(request, response),
				custWorkDay);
		model.addAttribute("page", page);
		return "modules/collection/v03/customerWork/custWorkDayList";
	}

	/**
	 * 行明细页面（面值列表）
	 * @param custWorkDay
	 * @return
	 */
	@RequestMapping(value = "showDetailPar")
	public String showDetailPar(CustWorkDay custWorkDay, Model model) {
		List<CustWorkDay> list = custWorkDayService.findDetailParList(custWorkDay);
		model.addAttribute("data", list);
		return "modules/collection/v03/customerWork/custWorkDayDetailPar";
	}
	
	
	/**
	 * 行明细页面（人员列表）
	 * @param custWorkDay
	 * @return
	 */
	@RequestMapping(value = "showDetailMan")
	public String showDetailMan(CustWorkDay custWorkDay, Model model) {
		List<CustWorkDay> list = custWorkDayService.findDetailManList(custWorkDay);
		model.addAttribute("data", list);
		return "modules/collection/v03/customerWork/custWorkDayDetailMan";
	}
	
	
	/**
	 * Excel导出处理
	 * @param custWorkDay
	 * @return
	 */
	@RequestMapping(value = "exportExcel")
	public void exportExcel(CustWorkDay custWorkDay, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes)
			throws FileNotFoundException, ParseException, IOException {
		Locale locale = LocaleContextHolder.getLocale();
		// 设置标题信息
		Map<String, Object> titleMap = Maps.newHashMap();
		// 制表机构
		titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
		// 制表时间
		titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
		// 查询
		List<CustWorkDay> list = custWorkDayService.findExcelList( custWorkDay);
		
		// 模板文件名
		String fileName = msg.getMessage("report.custWorkDay.excel", null, locale) + ".xls";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, "sheet");
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, CustWorkDay.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
		sheetMap.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, titleMap);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
	
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}
	

	
	
}