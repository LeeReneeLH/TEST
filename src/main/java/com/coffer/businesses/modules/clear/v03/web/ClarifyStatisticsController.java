package com.coffer.businesses.modules.clear.v03.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.DayReportMain;
import com.coffer.businesses.modules.clear.v03.service.CenterAccountsMainService;
import com.coffer.businesses.modules.clear.v03.service.DayReportMainService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.report.v01.entity.ClearReportAmount;
import com.coffer.businesses.modules.report.v01.service.ClearReportService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * 
 * @author sg
 * @version 2017年10月16日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/ClarifyStatisticsCtrl")
public class ClarifyStatisticsController extends BaseController {

	@Autowired
	private DayReportMainService dayReportMainService;

	@Autowired
	private CenterAccountsMainService centerAccountsMainService;
	@Autowired
	private ClearReportService clearReportService;
	@Autowired
	private OfficeService officeService;

	/**
	 * @author sg
	 * @version 2017年10月16日
	 * 
	 * 
	 * @param dayReportMain
	 * 
	 * @param model
	 * 
	 * @return 账务信息列表页面
	 */
	@RequestMapping(value = { "toGraphPage", "" })
	public String toGraphPage(DayReportMain dayReportMain, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		time.setTime(now);
		// 本月一号
		time.set(Calendar.DATE, 1);
		Date firstDate = time.getTime();
		if (dayReportMain.getCreateTimeStart() == null) {
			dayReportMain.setCreateTimeStart(firstDate);
		}
		if (dayReportMain.getCreateTimeEnd() == null) {
			dayReportMain.setCreateTimeEnd(now);
		}
		/* end */
		/* 将reportCondition修改为dayReportMain wzj 2017-11-15 begin */
		model.addAttribute("dayReportMain", dayReportMain);
		/* end */
		return "modules/clear/v03/clarifyStatistics/clarifyStatisticsList";
	}

	/**
	 * @author sg
	 * @version 2017年10月16日
	 * 
	 * 
	 * @param dayReportMain
	 * 
	 * 
	 * @return 账务信息列表页面
	 */
	@RequestMapping(value = { "graphicalFoldLine" })
	@ResponseBody
	public String graphicalFoldLine(DayReportMain dayReportMain) {
		List<DayReportMain> handIn = Lists.newArrayList();
		// 用于获取余额
		List<DayReportMain> handIns = Lists.newArrayList();
		// 设置折线图查询条件
		DayReportMain dayReportMains = selectCondition(dayReportMain);

		// 设置发生机构
		User userInfo = UserUtils.getUser();
		dayReportMains.setOffice(userInfo.getOffice());
		// 设置类型为有效
		dayReportMains.setStatus(ClearConstant.AccountsStatus.SUCCESS);
		// 设置账务类型不为备付金
		dayReportMains.setAccountsType(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		// 判断用户类型
		// 商行或人行
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			// 取得折线图数据
			handIn = dayReportMainService.findChartsList(dayReportMains);
			// 清分中心及平台
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// 取得折线图数据
			handIn = dayReportMainService.findChartList(dayReportMains);
			handIns = dayReportMainService.findChartaList(dayReportMains);
			for (DayReportMain dayReport : handIn) {
				for (DayReportMain dayReports : handIns) {
					// 判断日期是否一样
					Calendar c = Calendar.getInstance();
					c.setTime(dayReports.getReportDate());
					String year = String.valueOf(c.get(Calendar.YEAR));
					// 如果是按周查询
					if ("yyyy-IW".equals(dayReportMains.getFilterCondition())) {
						// 判断当前时间是今年的第几周
						int i = c.get(Calendar.WEEK_OF_YEAR);
						if (dayReport.getHandInDate().equals(year + "-" + String.valueOf(i))) {
							dayReport.setTotalAmount(dayReports.getTotalAmount());
							dayReport.setBeforeAmount(dayReports.getBeforeAmount());
							break;
						}
						// 如果是按季度查询
					} else if ("yyyy-Q".equals(dayReportMains.getFilterCondition())) {
						int i = (c.get(Calendar.MONTH) + 1);
						// 判断当前时间是今年的第几季度
						int quarter = 0;
						if (1 <= i && i <= 3) {
							quarter = 1;
						} else if (4 <= i && i <= 6) {
							quarter = 2;
						} else if (7 <= i && i <= 9) {
							quarter = 3;
						} else {
							quarter = 4;
						}
						if (dayReport.getHandInDate().equals(year + "-" + String.valueOf(quarter))) {
							dayReport.setTotalAmount(dayReports.getTotalAmount());
							dayReport.setBeforeAmount(dayReports.getBeforeAmount());
							break;
						}
					} else {
						if (dayReport.getHandInDate().equals(DateUtils.formatDate(dayReports.getReportDate(),
								dayReportMains.getFilterCondition().replaceAll("mm", "MM")))) {
							dayReport.setTotalAmount(dayReports.getTotalAmount());
							dayReport.setBeforeAmount(dayReports.getBeforeAmount());
							break;
						}
					}
				}
			}
		}

		// 将数据写入折线图的字段中
		Map<String, Object> jsonData = dayReportMainService.graphicalFoldLine(handIn, dayReportMains);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(dayReportMain.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * @author sg
	 * @version 2017年10月16日
	 * 
	 *          添加查询条件
	 * @param dayReportMain
	 *            账务信息
	 * 
	 * @return 账务信息列表页面
	 */
	private DayReportMain selectCondition(DayReportMain dayReportMain) {

		DayReportMain dayReportMains = new DayReportMain();
		// 格式化filterCondition 年 月 日 季度 周
		String status = dayReportMain.getFilterCondition().replace(Constant.Punctuation.HALF_UNDERLINE,
				Constant.Punctuation.HYPHEN);
		dayReportMains.setFilterCondition(status);
		// 查询条件：开始时间
		if (dayReportMain.getCreateTimeStart() != null) {
			dayReportMains
					.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(dayReportMain.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (dayReportMain.getCreateTimeEnd() != null) {
			dayReportMains.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(dayReportMain.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// add qph 设置发生时间 2017-11-17 begin
		dayReportMains.setOffice(dayReportMain.getOffice());
		// end

		// 如果金融平台用户登录
		/*
		 * if (Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().
		 * getOffice().getType())) { if
		 * (StringUtils.isNotBlank(reportCondition.getOfficeId())) { Office
		 * office =
		 * SysCommonUtils.findOfficeById(reportCondition.getOfficeId()); if
		 * (office != null &&
		 * Constant.OfficeType.COFFER.equals(office.getType())) {
		 * alllatticePointHandin.setaOffice(office); } } }
		 */
		// 如果金库用户登录
		/*
		 * if
		 * (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().
		 * getType())) {
		 * alllatticePointHandin.setaOffice(UserUtils.getUser().getOffice()); }
		 */
		return dayReportMains;
	}

	/**
	 * @author sg
	 * @version 2017年10月18日
	 * 
	 *          根据查询条件，导出业务量数据
	 * @param dayReportMain
	 * 
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 * 
	 * 
	 */
	@RequestMapping(value = { "exportCashHandinReport" })
	public void exportCashHandinReport(DayReportMain dayReportMain, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// add qph 设置发生机构 2017-11-17 begin
		User userInfo = UserUtils.getUser();
		// 导出excel
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Locale locale = LocaleContextHolder.getLocale();
		dayReportMain.setOffice(userInfo.getOffice());
		// 查询条件
		DayReportMain alllatticePointHandin = selectCondition(dayReportMain);
		// 设置类型为有效
		alllatticePointHandin.setStatus(ClearConstant.AccountsStatus.SUCCESS);
		// 设置账务类型不为备付金
		alllatticePointHandin.setAccountsType(ClearConstant.AccountsType.ACCOUNTS_PROVISIONS);
		// 查询业务量
		List<DayReportMain> handInDegree = Lists.newArrayList();
		// 查询余额
		List<DayReportMain> handInDegrees = Lists.newArrayList();
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			// 取得折线图数据
			handInDegree = dayReportMainService.findChartsList(alllatticePointHandin);
			// 清分中心及平台
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// 取得折线图数据
			handInDegree = dayReportMainService.findChartList(alllatticePointHandin);
			handInDegrees = dayReportMainService.findChartaList(alllatticePointHandin);
			for (DayReportMain dayReport : handInDegree) {
				for (DayReportMain dayReports : handInDegrees) {
					// 判断日期是否一样
					Calendar c = Calendar.getInstance();
					c.setTime(dayReports.getReportDate());
					String year = String.valueOf(c.get(Calendar.YEAR));
					// 如果是按周查询
					if ("yyyy-IW".equals(alllatticePointHandin.getFilterCondition())) {
						// 判断当前时间是今年的第几周
						int i = c.get(Calendar.WEEK_OF_YEAR);
						if (dayReport.getHandInDate().equals(year + "-" + String.valueOf(i))) {
							dayReport.setTotalAmount(dayReports.getTotalAmount());
							dayReport.setBeforeAmount(dayReports.getBeforeAmount());
							break;
						}
						// 如果是按季度查询
					} else if ("yyyy-Q".equals(alllatticePointHandin.getFilterCondition())) {
						int i = (c.get(Calendar.MONTH) + 1);
						// 判断当前时间是今年的第几季度
						int quarter = 0;
						if (1 <= i && i <= 3) {
							quarter = 1;
						} else if (4 <= i && i <= 6) {
							quarter = 2;
						} else if (7 <= i && i <= 9) {
							quarter = 3;
						} else {
							quarter = 4;
						}
						if (dayReport.getHandInDate().equals(year + "-" + String.valueOf(quarter))) {
							dayReport.setTotalAmount(dayReports.getTotalAmount());
							dayReport.setBeforeAmount(dayReports.getBeforeAmount());
							break;
						}
					} else {
						if (dayReport.getHandInDate().equals(DateUtils.formatDate(dayReports.getReportDate(),
								alllatticePointHandin.getFilterCondition().replaceAll("mm", "MM")))) {
							dayReport.setTotalAmount(dayReports.getTotalAmount());
							dayReport.setBeforeAmount(dayReports.getBeforeAmount());
							break;
						}
					}
				}
			}
		}
		// 将查询当天的时间转化成字符型
		String searchDateStart = DateUtils.formatDate(DateUtils.getDateStart(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);
		String searchDateEnd = DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// add qph 设置发生机构 2017-11-17 begin
		// centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 判断用户类型
		// 商行或人行
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			// 取得折线图数据
			centerAccountsMain.setClientId(userInfo.getOffice().getId());
			// 清分中心及平台
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
			centerAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		}
		// 业务类型为现金
		List<String> busTypelists = Lists.newArrayList();
		busTypelists = Arrays.asList(Global.getStringArray("accounts.businessType.cash"));
		centerAccountsMain.setBusinessTypes(busTypelists);
		// 查询条件：开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询条件：结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询当天所有的收入和支出
		CenterAccountsMain list = centerAccountsMainService.findSumList(centerAccountsMain);
		// 用于添加新数据
		DayReportMain dayReportMaina = new DayReportMain();
		// 判断业务量是否为空
		if (!Collections3.isEmpty(handInDegree)) {
			DayReportMain dayReportMains = handInDegree.get(handInDegree.size() - 1);
			if (list != null) {
				// 判断当日是否存在收入和支出如果没有记为0
				if (list.getInAmount() == null) {
					list.setInAmount(new BigDecimal(0));
				}
				if (list.getOutAmount() == null) {
					list.setOutAmount(new BigDecimal(0));
				}
				// 判断是否需要显示出当天的数据
				if ("yyyy-mm-dd".equals(alllatticePointHandin.getFilterCondition())
						&& ((alllatticePointHandin.getSearchDateEnd() == null
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd()))
								|| searchDateStart.equals(alllatticePointHandin.getSearchDateStart())
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd())
								|| (alllatticePointHandin.getSearchDateStart() != null
										&& alllatticePointHandin.getSearchDateEnd() == null))) {
					// 判断时候存在今天数据
					if (DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD)
							.equals(handInDegree.get(handInDegree.size() - 1).getHandInDate())) {
						dayReportMains.setInAmount(list.getInAmount());
						dayReportMains.setOutAmount(list.getOutAmount());
						dayReportMains.setTotalAmount(
								dayReportMains.getBeforeAmount().add(list.getInAmount().subtract(list.getOutAmount())));
					} else {
						dayReportMaina.setInAmount(list.getInAmount());
						dayReportMaina.setOutAmount(list.getOutAmount());
						dayReportMaina.setTotalAmount(
								dayReportMains.getTotalAmount().add(list.getInAmount().subtract(list.getOutAmount())));
						dayReportMaina.setHandInDate(
								DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD));
						handInDegree.add(dayReportMaina);

					}
				} else if ((!"yyyy-mm-dd".equals(alllatticePointHandin.getFilterCondition()))
						&& ((alllatticePointHandin.getSearchDateEnd() == null
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd()))
								|| searchDateStart.equals(alllatticePointHandin.getSearchDateStart())
								|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd())
								|| (alllatticePointHandin.getSearchDateStart() != null
										&& alllatticePointHandin.getSearchDateEnd() == null))) {
					// 过滤条件为日查询今天数据
					DayReportMain dayReportMaind = new DayReportMain();
					dayReportMaind.setSearchDateStart(searchDateStart);
					dayReportMaind.setSearchDateEnd(searchDateEnd);
					dayReportMaind.setFilterCondition("yyyy-mm-dd");
					// add qph 设置发生机构 2017-11-17 begin
					dayReportMaind.setOffice(userInfo.getOffice());
					// end
					List<DayReportMain> dayReportMaindList = dayReportMainService.findChartList(dayReportMaind);
					// 判断今天是否有结算
					if (Collections3.isEmpty(dayReportMaindList)) {
						dayReportMains.setInAmount(dayReportMains.getInAmount().add(list.getInAmount()));
						dayReportMains.setOutAmount(dayReportMains.getOutAmount().add(list.getOutAmount()));
						dayReportMains.setTotalAmount(
								dayReportMains.getTotalAmount().add(list.getInAmount().subtract(list.getOutAmount())));
					} else {
						for (DayReportMain entity : dayReportMaindList) {
							if (entity.getInAmount() == null) {
								entity.setInAmount(new BigDecimal(0));
							}
							if (entity.getOutAmount() == null) {
								entity.setOutAmount(new BigDecimal(0));
							}
							dayReportMains.setInAmount(dayReportMains.getInAmount().subtract(entity.getInAmount())
									.add(list.getInAmount()));
							dayReportMains.setOutAmount(dayReportMains.getOutAmount().subtract(entity.getOutAmount())
									.add(list.getOutAmount()));
							dayReportMains.setTotalAmount(dayReportMains.getBeforeAmount()
									.add(list.getInAmount().subtract(list.getOutAmount())));
						}

					}
				}

			}
		} else {
			// 判断查询日期是否包含今天
			if (searchDateStart.equals(alllatticePointHandin.getSearchDateStart())
					|| searchDateEnd.equals(alllatticePointHandin.getSearchDateEnd())
					|| alllatticePointHandin.getSearchDateEnd() == null) {
				CenterAccountsMain centerAccountsMains = new CenterAccountsMain();

				// add qph 设置发生机构 2017-11-17 begin
				/*
				 * centerAccountsMains.setRofficeId(userInfo.getOffice().getId()
				 * );
				 */
				// end
				// 商行或人行
				if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
						|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
					centerAccountsMains.setClientId(userInfo.getOffice().getId());
					// 清分中心及平台
				} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())
						|| Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
					centerAccountsMains.setRofficeId(userInfo.getOffice().getId());
					centerAccountsMains.getSqlMap().put("dsf",
							"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
					// end
				}
				// 业务类型
				List<String> busTypelist = Lists.newArrayList();
				// 现金业务
				busTypelist = Arrays.asList(Global.getStringArray("accounts.businessType.cash"));
				centerAccountsMains.setBusinessTypes(busTypelist);
				// 获取现金业务余额
				BigDecimal cash = new BigDecimal(0);
				List<CenterAccountsMain> centerAccountsMainList = centerAccountsMainService
						.findList(centerAccountsMains);
				if (!Collections3.isEmpty(centerAccountsMainList)) {
					cash = centerAccountsMainList.get(0).getTotalAmount();
				}
				// 备付金业务
				// busTypelist =
				// Arrays.asList(Global.getStringArray("accounts.businessType.pro"));
				// centerAccountsMains.setBusinessTypes(busTypelist);
				// BigDecimal pro = new BigDecimal(0);
				// List<CenterAccountsMain> centerAccountsMainLists =
				// centerAccountsMainService
				// .findList(centerAccountsMains);
				// if (!Collections3.isEmpty(centerAccountsMainLists)) {
				// pro = centerAccountsMainLists.get(0).getTotalAmount();
				// }
				BigDecimal totelAmounts = cash;
				if (list != null) {
					dayReportMaina.setInAmount(list.getInAmount());
					dayReportMaina.setOutAmount(list.getOutAmount());
				} else {
					dayReportMaina.setInAmount(new BigDecimal(0));
					dayReportMaina.setOutAmount(new BigDecimal(0));
				}
				dayReportMaina.setTotalAmount(totelAmounts);
				dayReportMaina.setHandInDate(
						DateUtils.getDate(alllatticePointHandin.getFilterCondition().replaceAll("mm", "MM")));
				handInDegree.add(dayReportMaina);

			}
		}
		if (Collections3.isEmpty(handInDegree)) {
			handInDegree.add(new DayReportMain());
		}
		// 获取当前登录人的机构
		String officeName = UserUtils.getUser().getOffice().getName();
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		// 清分中心统计.xls
		String fileName = msg.getMessage("clear.report.clarifyStatisticxls", null, locale);
		// sheet1
		Map<String, Object> sheet1Map = Maps.newHashMap();
		// 清分中心统计
		sheet1Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("clear.report.clarifyStatistic", null, locale));

		Map<String, Object> sheet1TitleMap = putCondition(dayReportMain);
		sheet1TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
				msg.getMessage("clear.report.clarifyStatistic", null, locale));
		sheet1TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);
		sheet1TitleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTime());

		sheet1Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, handInDegree);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DayReportMain.class.getName());

		paramList.add(sheet1Map);

		// 导出
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

	/**
	 * @author sg
	 * @version 2017年10月16日
	 * 
	 *          将查询条件放入map导出
	 * @param dayReportMain
	 *            账务结算查询信息
	 * 
	 * @return map
	 */
	private Map<String, Object> putCondition(DayReportMain dayReportMain) {
		Map<String, Object> map = Maps.newHashMap();
		// 获取字典标签
		String filter = DictUtils.getDictLabel(dayReportMain.getFilterCondition(), "report_filter_condition", "");
		// 时间
		String createTimeStart = DateUtils.foramtSearchDate(dayReportMain.getCreateTimeStart());
		String createTimeEnd = DateUtils.formatDate(DateUtils.getDateEnd(dayReportMain.getCreateTimeEnd()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);

		/*
		 * String selectOfficeName = ""; if
		 * (StringUtils.isNotBlank(dayReportMain.get.getOfficeId())) { // 机构
		 * Office office =
		 * SysCommonUtils.findOfficeById(dayReportMain.getOfficeId());
		 * selectOfficeName = office.getName(); }
		 */
		// TODO国际化
		map.put(ReportConstant.ReportExportData.START_TIME,
				StringUtils.isBlank(createTimeStart) ? "" : createTimeStart);
		map.put(ReportConstant.ReportExportData.END_TIME, StringUtils.isBlank(createTimeEnd) ? "" : createTimeEnd);
		// map.put(ReportConstant.ReportExportData.OFFICE, selectOfficeName);
		map.put(ReportConstant.ReportExportData.DATE_UNIT, filter);
		return map;
	}

	/**
	 * @author sg
	 * @version 2017年12月11日
	 * 
	 * 
	 * @param dayReportMain
	 * 
	 * @param model
	 * 
	 * @return 账务信息列表页面
	 */
	@RequestMapping(value = { "clearReportAmount" })
	public String clearReportAmount(DayReportMain dayReportMain, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		time.setTime(now);
		// 本月一号
		time.set(Calendar.DATE, 1);
		Date firstDate = time.getTime();
		if (dayReportMain.getCreateTimeStart() == null) {
			dayReportMain.setCreateTimeStart(firstDate);
		}
		if (dayReportMain.getCreateTimeEnd() == null) {
			dayReportMain.setCreateTimeEnd(now);
		}
		/* end */
		/* 将reportCondition修改为dayReportMain wzj 2017-11-15 begin */
		model.addAttribute("dayReportMain", dayReportMain);
		/* end */
		return "modules/clear/v03/clarifyStatistics/clearAmount";
	}

	/**
	 * 清分出入库总金额统计图
	 * 
	 * @author wxz
	 * @version 2017年11月27日
	 * @return
	 */
	@RequestMapping(value = "clearAmount")
	@ResponseBody
	public String graphicalClearList(ClearReportAmount clearAmount, HttpServletRequest request,
			HttpServletResponse response) {
		User user = UserUtils.getUser();
		ClearReportAmount clearReportAmount = selectConditionClear(clearAmount);
		// 设置查询出的日期格式
		if (Constant.jdbcType.ORACLE.equals(clearReportAmount.getDbName())) {
			clearReportAmount.setFilterCondition(AllocationConstant.Dates.FORMATE_YYYY_MM_DD);
		} else if (Constant.jdbcType.MYSQL.equals(clearReportAmount.getDbName())) {
			clearReportAmount.setFilterCondition(Global.getConfig("firstPage.mysql.findDate.format"));
		}
		/*
		 * // 设置开始日期和结束日期(间隔时间为一年) // 查询条件：开始时间
		 * clearReportAmount.setSearchDateStart(DateUtils.formatDate(
		 * DateUtils.getDateStart( DateUtils.addDate(new Date(),
		 * Integer.parseInt(Global.getConfig("firstPage.date.interval")))),
		 * AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS)); // 查询条件：结束时间
		 * clearReportAmount.setSearchDateEnd(DateUtils.formatDate(DateUtils.
		 * getDateEnd(new Date()),
		 * AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		 */
		// 设置查询的有效标识为有效
		clearReportAmount.setDelFlag(Constant.deleteFlag.Valid);
		// 设置查询的流水状态为登记
		clearReportAmount.setStatus(ClearConstant.StatusType.CREATE);
		// 设置查询的出入库类型
		List<String> inList = Global.getList("accounts.businessType.in");
		clearReportAmount.setInStatuses(inList);
		List<String> outList = Global.getList("accounts.businessType.out");
		clearReportAmount.setOutStatuses(outList);
		// 判断用户类型
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())) {
			// 如果是人行
			Office office = new Office();
			office.setType(Constant.OfficeType.COFFER);
			office.setParentIds(user.getOffice().getParentIds());
			office.setDelFlag(Constant.deleteFlag.Valid);
			List<Office> officeList = officeService.findList(office);
			clearReportAmount.setOfficeList(officeList);
			// 设置人行清分出入库类型
			inList = Global.getList("accounts.businessType.peopleBankIn");
			outList = Global.getList("accounts.businessType.peopleBankOut");
			clearReportAmount.setInStatuses(inList);
			clearReportAmount.setOutStatuses(outList);
			/* 修改人:sg 修改日期:201-12-06 begin */
			// clearReportAmount.setCustNo(user.getOffice().getId());
			/* end */
		} else if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置流水客户编号为当前用户所属机构
			clearReportAmount.setCustNo(user.getOffice().getId());
			// 设置人行清分出入库类型
			inList = Global.getList("accounts.businessType.cofferIn");
			outList = Global.getList("accounts.businessType.cofferOut");
			clearReportAmount.setInStatuses(inList);
			clearReportAmount.setOutStatuses(outList);
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())) {
			// 如果是清分中心用户登录
			// List<Office> officeList =
			// StoreCommonUtils.getStoCustList("1,3",false);
			List<Office> officeList = Lists.newArrayList();
			officeList.add(UserUtils.getUser().getOffice());
			// 添加所需客户
			clearReportAmount.setrOfficeList(officeList);
		}
		// 查询清分出入库业务
		List<ClearReportAmount> clearAmountList = clearReportService.findInOrOutAmount(clearReportAmount);
		// 进行图形化过滤
		Map<String, Object> jsonData = clearReportService.inOrOutAmount(clearAmountList);
		return gson.toJson(jsonData);
	}

	/**
	 * @author wxz
	 * @version 2017年11月27日 添加查询条件
	 * @param reportCondition
	 *            清分出入库总金额查询信息
	 * 
	 * @return LatticePointHandin
	 */
	private ClearReportAmount selectConditionClear(ClearReportAmount clearReportAmount) {
		ClearReportAmount alllatticePointHandin = new ClearReportAmount();
		// 格式化filterCondition 年 月 日 季度 周
		/* 修改人:sg 修改日期:2017-12-11 begin */
		// String status =
		// clearReportAmount.getFilterCondition().replace(Constant.Punctuation.HALF_UNDERLINE,
		// Constant.Punctuation.HYPHEN);
		// alllatticePointHandin.setFilterCondition(status);
		/* end */

		// 查询条件：开始时间
		if (clearReportAmount.getCreateTimeStart() != null) {
			alllatticePointHandin.setSearchDateStart(
					DateUtils.formatDate(DateUtils.getDateStart(clearReportAmount.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (clearReportAmount.getCreateTimeEnd() != null) {
			alllatticePointHandin
					.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(clearReportAmount.getCreateTimeEnd()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		return alllatticePointHandin;
	}
}
