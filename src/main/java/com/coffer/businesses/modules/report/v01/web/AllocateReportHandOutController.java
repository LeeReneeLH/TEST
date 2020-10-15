package com.coffer.businesses.modules.report.v01.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessDegree;
import com.coffer.businesses.modules.report.v01.entity.ReportCondition;
import com.coffer.businesses.modules.report.v01.service.AllocateReportBusinessService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 网点下拨现金报表Controller
 * 
 * @author SongYuanYang
 * @version 2017-8-27
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/AllocateReportController")
public class AllocateReportHandOutController extends BaseController {

	@Autowired
	private AllocateReportBusinessService latticePointHandinService;

	/**
	 * @author SongYuanYang
	 * @version 2017年8月31日
	 * 
	 *          根据查询条件，查询现金下拨信息
	 * @param reportCondition
	 *            过滤条件
	 * @param model
	 * 
	 * @return 现金下拨信息列表页面
	 */
	@RequestMapping(value = { "toGraphPage", "" })
	public String toGraphPage(ReportCondition reportCondition, Model model) {
		model.addAttribute("reportCondition", reportCondition);
		return "modules/report/v01/allocation/cashAllocateReport";
	}

	/**
	 * @author SongYuanYang
	 * @version 2017年8月31日
	 * 
	 *          根据查询条件，查询现金下拨信息以折线图显示
	 * @param reportCondition
	 *            过滤条件
	 * 
	 * @return 现金下拨信息列表页面
	 */
	@RequestMapping(value = { "graphicalFoldLine" })
	@ResponseBody
	public String graphicalFoldLine(ReportCondition reportCondition) {
		List<AllocateReportBusinessDegree> handIn = Lists.newArrayList();
		// 设置折线图查询条件
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		// 取得折线图数据
		handIn = latticePointHandinService.findByAllocate(alllatticePointHandin);
		Map<String, Object> jsonData = latticePointHandinService.graphicalFoldLine(handIn);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * @author SongYuanYang
	 * @version 2017年8月31日
	 * 
	 *          根据查询条件，查询现金下拨信息以柱状图显示
	 * @param reportCondition
	 *            过滤条件
	 * @param model
	 * 
	 * @return 现金下拨信息列表页面
	 */
	@RequestMapping(value = { "graphicalColumn" })
	@ResponseBody
	public String graphicalColumn(ReportCondition reportCondition, Model model) {
		List<AllocateReportBusinessDegree> handIn = Lists.newArrayList();
		// 设置柱状图查询条件
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		// 取得柱状图数据
		handIn = latticePointHandinService.findByAllocate(alllatticePointHandin);
		Map<String, Object> jsonData = latticePointHandinService.graphicalColumn(handIn);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * @author SongYuanYang
	 * @version 2017年8月31日
	 * 
	 *          根据查询条件，查询临时现金下拨信息以柱状图显示
	 * @param reportCondition
	 *            过滤条件
	 * @param model
	 * 
	 * @return 现金下拨信息列表页面
	 */
	@RequestMapping(value = { "graphicalColumnTemp" })
	@ResponseBody
	public String graphicalColumnTemp(ReportCondition reportCondition, Model model) {
		List<AllocateReportBusinessDegree> handIn = Lists.newArrayList();
		// 设置柱状图查询条件
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		// 取得柱状图数据
		handIn = latticePointHandinService.findByAllocateFromTemp(alllatticePointHandin);
		Map<String, Object> jsonData = latticePointHandinService.graphicalColumnTemp(handIn);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * @author SongYuanYang
	 * @version 2017年9月1日
	 * 
	 *          根据查询条件，查询现金下拨信息以堆叠柱状图显示
	 * @param reportCondition
	 *            过滤条件
	 * @param latticePointHandin
	 *            现金下拨信息
	 * @param model
	 * 
	 * @return 现金下拨信息列表页面
	 */
	@RequestMapping(value = { "graphicalStackColumn" })
	@ResponseBody
	public String graphicalStackColumn(ReportCondition reportCondition, AllocateReportBusinessDegree latticePointHandin,
			Model model) {
		// 设置堆叠柱状图查询条件
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(latticePointHandin);
		Map<String, Object> jsonData = latticePointHandinService.graphicalStackColumn(alllatticePointHandin);
		return gson.toJson(jsonData);
	}

	/**
	 * @author SongYuanYang
	 * @version 2017年9月1日
	 * 
	 *          添加查询条件
	 * @param reportCondition
	 *            现金下拨信息
	 * 
	 * @return 现金下拨信息列表页面
	 */
	private AllocateReportBusinessDegree selectCondition(ReportCondition reportCondition) {

		AllocateReportBusinessDegree alllatticePointHandin = new AllocateReportBusinessDegree();
		// 格式化filterCondition 年 月 日 季度 周
		String status = reportCondition.getFilterCondition().replace(Constant.Punctuation.HALF_UNDERLINE,
				Constant.Punctuation.HYPHEN);
		alllatticePointHandin.setFilterCondition(status);
		// 查询条件：开始时间
		if (reportCondition.getCreateTimeStart() != null) {
			alllatticePointHandin.setSearchDateStart(
					DateUtils.formatDate(DateUtils.getDateStart(reportCondition.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (reportCondition.getCreateTimeEnd() != null) {
			alllatticePointHandin
					.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(reportCondition.getCreateTimeEnd()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}

		// 如果金融平台用户登录
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			if (StringUtils.isNotBlank(reportCondition.getOfficeId())) {
				Office office = SysCommonUtils.findOfficeById(reportCondition.getOfficeId());
				if (office != null && Constant.OfficeType.COFFER.equals(office.getType())) {
					alllatticePointHandin.setaOffice(office);
				}
			}
		}
		// 如果金库用户登录
		if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			alllatticePointHandin.setaOffice(UserUtils.getUser().getOffice());
		}
		return alllatticePointHandin;
	}

	/**
	 * @author SongYuanYang
	 * @version 2017年9月5日
	 * 
	 *          根据查询条件，导出多条报表数据
	 * @param reportCondition
	 *            过滤条件
	 * @param latticePointHandin
	 *            现金下拨信息
	 * @param request
	 * @param response
	 * @param model
	 * 
	 * @return 现金下拨信息页面
	 */
	@RequestMapping(value = { "exportCashAllocateReport" })
	public void exportCashHandinReport(ReportCondition reportCondition, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Locale locale = LocaleContextHolder.getLocale();
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		// 获取字典标签
		String filter = DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", "");
		List<Map<String, Object>> paramList = Lists.newArrayList();

		// 业务量表excel导出
		Map<String, Object> sheet1Map = Maps.newHashMap();
		sheet1Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("report.point.rule.business", null, locale));
		Map<String, Object> sheet1TitleMap = putCondition(reportCondition);

		// 取得过滤条件
		sheet1TitleMap.put("filterName", filter);
		sheet1TitleMap.put("title", msg.getMessage("report.point.rule.business", null, locale));
		sheet1TitleMap.put("officename", UserUtils.getUser().getOffice().getName());
		sheet1TitleMap.put("nowdate", DateUtils.getDateTime());
		sheet1Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);

		// 查询业务量
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY,
				latticePointHandinService.findByAllocate(alllatticePointHandin));
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AllocateReportBusinessDegree.class.getName());
		paramList.add(sheet1Map);

		// 常规/临时业务量表excel导出
		Map<String, Object> sheet2Map = Maps.newHashMap();
		sheet2Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.ruleOrtemporary.business", null, locale));
		Map<String, Object> sheet2TitleMap = putCondition(reportCondition);

		// 取得过滤条件
		sheet2TitleMap.put("filterName", filter);
		sheet2TitleMap.put("title", msg.getMessage("report.ruleOrtemporary.business", null, locale));
		sheet2TitleMap.put("officename", UserUtils.getUser().getOffice().getName());
		sheet2TitleMap.put("nowdate", DateUtils.getDateTime());
		sheet2Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet2TitleMap);
		List<AllocateReportBusinessDegree> sheet2list = Lists.newArrayList();
		List<AllocateReportBusinessDegree> listRule = latticePointHandinService
				.findByAllBusiness(alllatticePointHandin);
		for (AllocateReportBusinessDegree itemRule : listRule) {
			itemRule.setAOfficeName(ReportGraphConstant.StackColumnLegend.ROUTINE_LINE);
		}
		List<AllocateReportBusinessDegree> listTemp = latticePointHandinService
				.findByAllBusinessFromTemp(alllatticePointHandin);
		for (AllocateReportBusinessDegree itemTemp : listTemp) {
			itemTemp.setAOfficeName(ReportGraphConstant.StackColumnLegend.TEMP_LINE);
		}
		sheet2list.addAll(listRule);
		sheet2list.addAll(listTemp);
		// 查询常规/临时业务量
		sheet2Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, sheet2list);
		sheet2Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AllocateReportBusinessDegree.class.getName());
		paramList.add(sheet2Map);

		// 常规表金额数据excel导出
		Map<String, Object> sheet3Map = Maps.newHashMap();
		sheet3Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.store.approve.money.rule", null, locale));
		Map<String, Object> sheet3TitleMap = putCondition(reportCondition);

		// 取得过滤条件
		sheet3TitleMap.put("filterName", filter);
		sheet3TitleMap.put("title", msg.getMessage("report.store.approve.money.rule", null, locale));
		sheet3TitleMap.put("officename", UserUtils.getUser().getOffice().getName());
		sheet3TitleMap.put("nowdate", DateUtils.getDateTime());
		sheet3Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet3TitleMap);

		// 查询总金额
		sheet3Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY,
				latticePointHandinService.findByAllocate(alllatticePointHandin));
		sheet3Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AllocateReportBusinessDegree.class.getName());
		paramList.add(sheet3Map);

		// 临时表金额数据excel导出
		Map<String, Object> sheet4Map = Maps.newHashMap();
		sheet4Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.store.approve.money.temp", null, locale));
		Map<String, Object> sheet4TitleMap = putCondition(reportCondition);

		// 取得过滤条件
		sheet4TitleMap.put("filterName", filter);
		sheet4TitleMap.put("title", msg.getMessage("report.store.approve.money.temp", null, locale));
		sheet4TitleMap.put("officename", UserUtils.getUser().getOffice().getName());
		sheet4TitleMap.put("nowdate", DateUtils.getDateTime());
		sheet4Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet4TitleMap);

		// 查询临时表审批金额数据
		sheet4Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY,
				latticePointHandinService.findByAllocateFromTemp(alllatticePointHandin));
		sheet4Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AllocateReportBusinessDegree.class.getName());
		paramList.add(sheet4Map);

		// 取得excel表信息
		String templatePath = Global.getConfig("export.template.path");
		String fileName = msg.getMessage("allocation.export.report.fileName.09", null, locale);
		// 导出excel
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

	/**
	 * @author SongYuanYang
	 * @version 2017年9月8日
	 * 
	 *          将查询条件放入map导出
	 * @param reportCondition
	 *            现金下拨查询信息
	 * 
	 * @return map
	 */
	private Map<String, Object> putCondition(ReportCondition reportCondition) {
		Map<String, Object> map = Maps.newHashMap();
		// 获取字典标签
		String filter = DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", "");
		// 时间
		String createTimeStart = DateUtils.foramtSearchDate(reportCondition.getCreateTimeStart());
		String createTimeEnd = DateUtils.formatDate(DateUtils.getDateEnd(reportCondition.getCreateTimeEnd()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);

		String selectOfficeName = "";
		if (StringUtils.isNotBlank(reportCondition.getOfficeId())) {
			// 机构
			Office office = SysCommonUtils.findOfficeById(reportCondition.getOfficeId());
			selectOfficeName = office.getName();
		}
		// TODO国际化
		map.put(ReportConstant.ReportExportData.START_TIME,
				StringUtils.isBlank(createTimeStart) ? "" : createTimeStart);
		map.put(ReportConstant.ReportExportData.END_TIME, StringUtils.isBlank(createTimeEnd) ? "" : createTimeEnd);
		map.put(ReportConstant.ReportExportData.OFFICE, selectOfficeName);
		map.put(ReportConstant.ReportExportData.DATE_UNIT, filter);
		return map;
	}

}
