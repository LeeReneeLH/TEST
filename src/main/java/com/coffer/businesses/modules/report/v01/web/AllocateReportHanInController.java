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
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessCount;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessDegree;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessMoneyAmount;
import com.coffer.businesses.modules.report.v01.entity.ReportCondition;
import com.coffer.businesses.modules.report.v01.service.AllocateReportBusinessService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 网点上缴现金功能
 * 
 * @author xp
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/handinController")
public class AllocateReportHanInController extends BaseController {
	@Autowired
	private AllocateReportBusinessService allocateReportBusinessService;
	@Autowired
	private OfficeService officeService;

	/**
	 * @author xp
	 * @version 2017年8月24日
	 * 
	 *          根据查询条件，查询现金上缴信息
	 * @param reportCondition
	 *            现金上缴信息
	 * @param model
	 * 
	 * @return 现金上缴信息列表页面
	 */
	@RequestMapping(value = { "toGraphPage", "" })
	public String toGraphPage(ReportCondition reportCondition, Model model) {
		model.addAttribute("reportCondition", reportCondition);
		return "modules/report/v01/allocation/cashHandinReport";
	}

	/**
	 * @author xp
	 * @version 2017年8月24日
	 * 
	 *          根据查询条件，导出业务量数据
	 * @param reportCondition
	 *            现金上缴信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 * 
	 * @return 现金上缴信息列表页面
	 */
	@RequestMapping(value = { "exportCashHandinReport" })
	public void exportCashHandinReport(ReportCondition reportCondition, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 导出excel
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Locale locale = LocaleContextHolder.getLocale();
		// 查询条件
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		// 查询业务量
		List<AllocateReportBusinessDegree> handInDegree = allocateReportBusinessService
				.findDegree(alllatticePointHandin);
		// 查询总金额
		List<AllocateReportBusinessCount> handInCount = allocateReportBusinessService.findCount(alllatticePointHandin);
		// 查询物品金额
		List<AllocateReportBusinessMoneyAmount> handInAmount = allocateReportBusinessService
				.findGoods(alllatticePointHandin);
		String officeName = UserUtils.getUser().getOffice().getName();
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		String fileName = msg.getMessage("allocation.export.report.fileName.02", null, locale);

		// sheet1
		Map<String, Object> sheet1Map = Maps.newHashMap();
		sheet1Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("allocation.cash.handinDegree", null, locale));

		Map<String, Object> sheet1TitleMap = putCondition(reportCondition);
		sheet1TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
				msg.getMessage("allocation.cash.handin", null, locale)
						+ msg.getMessage("allocation.cash.handinDegree", null, locale));
		sheet1TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);
		sheet1TitleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTime());

		sheet1Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, handInDegree);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AllocateReportBusinessDegree.class.getName());

		paramList.add(sheet1Map);

		// sheet2
		Map<String, Object> sheet2Map = Maps.newHashMap();
		sheet2Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("allocation.cash.handinCount", null, locale));

		Map<String, Object> sheet2TitleMap = putCondition(reportCondition);
		sheet2TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
				msg.getMessage("allocation.cash.handin", null, locale)
						+ msg.getMessage("allocation.cash.handinCount", null, locale));
		sheet2TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);
		sheet2TitleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTime());

		sheet2Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet2TitleMap);
		sheet2Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, handInCount);
		sheet2Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AllocateReportBusinessCount.class.getName());

		paramList.add(sheet2Map);

		// sheet3
		Map<String, Object> sheet3Map = Maps.newHashMap();
		sheet3Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("allocation.cash.handinGoods", null, locale));

		Map<String, Object> sheet3TitleMap = putCondition(reportCondition);
		sheet3TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
				msg.getMessage("allocation.cash.handin", null, locale)
						+ msg.getMessage("allocation.cash.handinGoods", null, locale));
		sheet3TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);
		sheet3TitleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTime());

		sheet3Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet3TitleMap);
		sheet3Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, handInAmount);
		sheet3Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY,
				AllocateReportBusinessMoneyAmount.class.getName());

		paramList.add(sheet3Map);

		// 导出
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

	/**
	 * @author xp
	 * @version 2017年8月24日
	 * 
	 *          根据查询条件，查询业务量数据并显示到页面图表中
	 * @param reportCondition
	 *            现金上缴信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @return 现金上缴信息列表页面
	 */
	@RequestMapping(value = "graphicalHandInList")
	@ResponseBody
	public String graphicalHandInList(ReportCondition reportCondition, HttpServletRequest request,
			HttpServletResponse response) {
		List<AllocateReportBusinessDegree> handIn = Lists.newArrayList();
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		handIn = allocateReportBusinessService.findDegree(alllatticePointHandin);
		Map<String, Object> jsonData = allocateReportBusinessService.graphicalHandInList(handIn);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * @author xp
	 * @version 2017年9月5日
	 * 
	 * 
	 * @param reportCondition
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "graphicalHistogram")
	@ResponseBody
	public String graphicalHistogram(ReportCondition reportCondition, HttpServletRequest request,
			HttpServletResponse response) {
		List<AllocateReportBusinessCount> handIn = Lists.newArrayList();
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		handIn = allocateReportBusinessService.findCount(alllatticePointHandin);
		Map<String, Object> jsonData = allocateReportBusinessService.graphicalHistogram(handIn);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * @author xp
	 * @version 2017年8月24日
	 * 
	 *          根据查询条件，查询物品金额数据并显示到页面图表中
	 * @param reportCondition
	 *            现金上缴信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * 
	 * @return 现金上缴信息列表页面
	 */
	@RequestMapping(value = { "graphicalHistogramGoods" })
	@ResponseBody
	public String graphicalGoods(ReportCondition reportCondition, HttpServletRequest request,
			HttpServletResponse response) {
		List<AllocateReportBusinessMoneyAmount> handIn = Lists.newArrayList();
		// 返回查询条件
		AllocateReportBusinessDegree alllatticePointHandin = selectCondition(reportCondition);
		handIn = allocateReportBusinessService.findGoods(alllatticePointHandin);
		Map<String, Object> jsonData = allocateReportBusinessService.graphicalGoods(handIn);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(reportCondition.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * @author xp
	 * @version 2017年9月4日 添加查询条件
	 * @param reportCondition
	 *            现金上缴查询信息
	 * 
	 * @return LatticePointHandin
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
		// 如果数字平台登录，机构过滤是否为空
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			if (StringUtils.isNotBlank(reportCondition.getOfficeId())) {
				Office office = officeService.get(reportCondition.getOfficeId());
				if (office != null && Constant.OfficeType.COFFER.equals(office.getType())) {
					alllatticePointHandin.setaOffice(office);
				}
			}
			if (reportCondition.getOffice() != null) {
				if (StringUtils.isNotBlank(reportCondition.getOffice().getId())) {
					Office office = officeService.get(reportCondition.getOffice().getId());
					if (office != null && Constant.OfficeType.COFFER.equals(office.getType())) {
						alllatticePointHandin.setaOffice(office);
					}
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
	 * @author xp
	 * @version 2017年9月4日 将查询条件放入map导出
	 * @param reportCondition
	 *            现金上缴查询信息
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
		// 查询单位
		// 如果数字化金融平台登录
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			if (StringUtils.isNotBlank(reportCondition.getOffice().getId())) {
				// 机构
				Office office = SysCommonUtils.findOfficeById(reportCondition.getOfficeId());
				selectOfficeName = office == null ? "" : office.getName();
			}
		}
		// 如果金库登录
		if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			selectOfficeName = UserUtils.getUser().getOffice().getName();
		}
		// TODO国际化
		map.put(ReportConstant.ReportExportData.START_TIME,
				StringUtils.isBlank(createTimeStart) ? "" : createTimeStart);
		map.put(ReportConstant.ReportExportData.END_TIME, StringUtils.isBlank(createTimeEnd) ? "" : createTimeEnd);
		map.put(ReportConstant.ReportExportData.OFFICE,
				StringUtils.isBlank(selectOfficeName) ? UserUtils.getUser().getOffice().getName() : selectOfficeName);
		map.put(ReportConstant.ReportExportData.DATE_UNIT, filter);
		return map;
	}
}
