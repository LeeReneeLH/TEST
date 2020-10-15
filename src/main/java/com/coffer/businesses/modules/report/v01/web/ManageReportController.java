package com.coffer.businesses.modules.report.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.report.v01.dao.ManageReportDao;
import com.coffer.businesses.modules.report.v01.entity.ManageReport;
import com.coffer.businesses.modules.report.v01.service.ManageReportService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 管理分析报表Controller
 * 
 * @author WQJ
 * @version 2020-1-6
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/manageReport")
public class ManageReportController extends BaseController {
	@Autowired
	private ManageReportService manageReportService;
	@Autowired
	private ManageReportDao manageReportDao;

	/**
	 * 上门收钞情况
	 *
	 * @author WQJ
	 * @version 2020年1月6日
	 * @param
	 * @return
	 */
	@RequiresPermissions("door:manageReport:view")
	@RequestMapping(value = { "collectMoneySituation", "" })
	public String collectMoneySituation(ManageReport manageReport, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		// 默认查看当前年
		if (manageReport.getCreateTimeStart() == null) {
			manageReport.setCreateTimeStart((DateUtils.getYearStart(new Date())));
		}
		if (manageReport.getCreateTimeEnd() == null) {
			manageReport.setCreateTimeEnd(DateUtils.getYearEnd(new Date()));
		}
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			manageReport.getSqlMap().put("dsf", "AND (c.id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			manageReport.getSqlMap().put("dsf", "AND (c.id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		Page<ManageReport> page = manageReportService.collectMoneySituation(new Page<ManageReport>(request, response),
				manageReport);
		model.addAttribute("page", page);
		return "modules/report/v01/manageReport/collectMoneySituationList";
	}

	/**
	 * 卡钞情况
	 *
	 * @author WQJ
	 * @version 2020年1月6日
	 * @param
	 * @return
	 */
	@RequiresPermissions("door:manageReport:view")
	@RequestMapping(value = "stuckCollectSituation")
	public String stuckCollectSituation(ManageReport manageReport, Model model, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		// 查询条件： 开始时间
		if (manageReport.getCreateTimeStart() != null) {
			manageReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(manageReport.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (manageReport.getCreateTimeEnd() != null) {
			manageReport.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(manageReport.getCreateTimeEnd())));
		}
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			manageReport.getSqlMap().put("dsf", "AND (c.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			manageReport.getSqlMap().put("dsf", "AND (c.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		Page<ManageReport> page = manageReportService.stuckCollectSituation(new Page<ManageReport>(request, response),
				manageReport);
		model.addAttribute("page", page);
		return "modules/report/v01/manageReport/stuckCollectSituationList";
	}

	/**
	 * 差错情况
	 *
	 * @author WQJ
	 * @version 2020年1月6日
	 * @param
	 * @return
	 */
	@RequiresPermissions("door:manageReport:view")
	@RequestMapping(value = "errorCollectSituation")
	public String errorCollectSituation(ManageReport manageReport, Model model, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		// 查询条件： 开始时间
		if (manageReport.getCreateTimeStart() != null) {
			//时间格式转换
			String createTimeStart = DateUtils.foramtSearchDate(DateUtils.getDateStart(manageReport.getCreateTimeStart()));
			manageReport.setSearchDateStart(createTimeStart);
			model.addAttribute("createTimeStart", createTimeStart);
		}
		// 查询条件： 结束时间
		if (manageReport.getCreateTimeEnd() != null) {
			//时间格式转换
			String createTimeEnd = DateUtils.foramtSearchDate(DateUtils.getDateEnd(manageReport.getCreateTimeEnd()));
			manageReport.setSearchDateEnd(createTimeEnd);
			model.addAttribute("createTimeEnd",createTimeEnd);
		}
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			manageReport.getSqlMap().put("dsf", "AND (a.cust_no =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			manageReport.getSqlMap().put("dsf", "AND (a.cust_no =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		
		ManageReport total = manageReportDao.errorCollectSituationTotal(manageReport);
		model.addAttribute("total", total.getSaveCount());
		model.addAttribute("longMoney", total.getLongMoney().toString());
		model.addAttribute("shortMoney", total.getShortMoney().toString());
		Page<ManageReport> page = manageReportService.errorCollectSituation(new Page<ManageReport>(request, response),
				manageReport);
		model.addAttribute("page", page);
		return "modules/report/v01/manageReport/errorCollectSituationList";
	}

	/**
	 * 上门收钞情况导出
	 * 
	 * @author WQJ
	 * @version 2020年1月8日
	 */
	@RequestMapping(value = "exportCollectMoneySituation")
	public void exportCollectMoneySituation(ManageReport manageReport, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException, ParseException, IOException {
		// 查询条件： 开始时间
		if (manageReport.getCreateTimeStart() != null) {
			manageReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(manageReport.getCreateTimeStart())));
		}
		// 如果为空，默认当前年
		else {
			manageReport.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getYearStart(new Date())));
		}
		// 查询条件： 结束时间
		if (manageReport.getCreateTimeEnd() != null) {
			manageReport.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(manageReport.getCreateTimeEnd())));
		}
		// 如果为空，默认当前年
		else {
			manageReport.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getYearEnd(new Date())));
		}
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			manageReport.getSqlMap().put("dsf", "AND (c.id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			manageReport.getSqlMap().put("dsf", "AND (c.id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		List<ManageReport> manageReportsList = manageReportDao.collectSituation(manageReport);
		// 列表为空
		if (Collections3.isEmpty(manageReportsList)) {
			manageReportsList.add(new ManageReport());
		}
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 模板文件名 /上门收钞情况.xls
		String fileName = msg.getMessage("report.manageReport.collectMoneySituation", null, locale) + ".xls";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.manageReport.collectMoneySituation", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, ManageReport.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, manageReportsList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"上门收钞情况" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}

	/**
	 * 卡钞情况导出
	 * 
	 * @author WQJ
	 * @version 2020年1月8日
	 */
	@RequestMapping(value = "exportStuckCollectSituation")
	public void exportStuckCollectSituation(ManageReport manageReport, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException, ParseException, IOException {
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			manageReport.getSqlMap().put("dsf", "AND (c.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			manageReport.getSqlMap().put("dsf", "AND (c.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 查询条件： 开始时间
		if (manageReport.getCreateTimeStart() != null) {
			manageReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(manageReport.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (manageReport.getCreateTimeEnd() != null) {
			manageReport.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(manageReport.getCreateTimeEnd())));
		}
		/* end */
		List<ManageReport> manageReportsList = manageReportDao.stuckCollectSituation(manageReport);
		// 列表为空
		if (Collections3.isEmpty(manageReportsList)) {
			manageReportsList.add(new ManageReport());
		}
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 模板文件名 /卡钞情况.xls
		String fileName = msg.getMessage("report.manageReport.stuckCollectSituation", null, locale) + ".xls";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.manageReport.stuckCollectSituation", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, ManageReport.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, manageReportsList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"卡钞情况" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}

	/**
	 * 差错情况导出
	 * 
	 * @author WQJ
	 * @version 2020年1月8日
	 */
	@RequestMapping(value = "exportErrorCollectSituation")
	public void exportErrorCollectSituation(ManageReport manageReport, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException, ParseException, IOException {
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			manageReport.getSqlMap().put("dsf", "AND (a.CUST_NO =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			manageReport.getSqlMap().put("dsf", "AND (a.CUST_NO =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 查询条件： 开始时间
		if (manageReport.getCreateTimeStart() != null) {
			manageReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(manageReport.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (manageReport.getCreateTimeEnd() != null) {
			manageReport.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(manageReport.getCreateTimeEnd())));
		}
		/* end */
		List<ManageReport> manageReportsList = manageReportDao.errorCollectSituation(manageReport);
		// 列表为空
		if (Collections3.isEmpty(manageReportsList)) {
			manageReportsList.add(new ManageReport());
		}
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 模板文件名 /差错情况.xls
		String fileName = msg.getMessage("report.manageReport.errorCollectSituation", null, locale) + ".xls";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.manageReport.errorCollectSituation", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, ManageReport.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, manageReportsList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"差错情况" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}
}
