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

import com.coffer.businesses.modules.collection.v03.entity.EmpWork;
import com.coffer.businesses.modules.collection.v03.service.EmpWorkService;
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
 * 员工工作量统计Controller
 * 
 * @author wanglin
 * @version 2017-09-04
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/empWork")
public class EmpWorkController extends BaseController {

	@Autowired
	private EmpWorkService empWorkService;

	
	@ModelAttribute
	public EmpWork get(@RequestParam(required = false) String detailId) {
		EmpWork entity = null;
		entity = new EmpWork();
		return entity;
	}

	/**
	 * 根据查询条件，查询统计一览信息
	 * 
	 * @author wanglin
	 * @version 2017-09-04
	 * @param empWork
	 * @return 统计列页面表
	 */
	@RequiresPermissions("collection:empWork:view")
	@RequestMapping(value = { "list", "" })
	public String list(EmpWork empWork, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<EmpWork> page = empWorkService.findPage(new Page<EmpWork>(request, response),
				empWork);
		model.addAttribute("page", page);
		return "modules/collection/v03/employeeWork/empWorkList";
	}

	/**
	 * Excel导出处理
	 * 
	 * @author wanglin
	 * @version 2017年9月11日
	 * @param empWork
	 * @return
	 */
	@RequestMapping(value = "exportExcel")
	public void exportExcel(EmpWork empWork, HttpServletRequest request,
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
		List<EmpWork> list = empWorkService.findAll( empWork);
		
		if (list != null && list.size()>0){
			//合计的设定
			EmpWork empWorkSum = new EmpWork();
			//合计
			empWorkSum.setEmpName(msg.getMessage("report.employeeWork.total", null, locale));
			
			for (EmpWork item : list) {
				empWorkSum.setMachineCount(empWorkSum.getMachineCount() + item.getMachineCount());			//机械清分笔数
				empWorkSum.setMachineAmount(empWorkSum.getMachineAmount().add(item.getMachineAmount()));	//机械清分金额
				empWorkSum.setHandCount(empWorkSum.getHandCount() + item.getHandCount());					//手工清分笔数
				empWorkSum.setHandAmount(empWorkSum.getHandAmount().add(item.getHandAmount()));				//手工清分金额
				empWorkSum.setSumAmount(empWorkSum.getSumAmount().add(item.getSumAmount()));				//合计
				empWorkSum.setDiffCount(empWorkSum.getDiffCount() + item.getDiffCount());					//差错笔数
				empWorkSum.setDiffAmount(empWorkSum.getDiffAmount().add(item.getDiffAmount()));				//差错金额
			}
			list.add(empWorkSum);
		}

		// 模板文件名(员工工作量.xls)
		String fileName = msg.getMessage("report.employeeWork.excel", null, locale) + ".xls";

		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, "sheet");
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, EmpWork.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
		sheetMap.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, titleMap);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
	
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}
	

	
}