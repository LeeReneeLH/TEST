package com.coffer.businesses.modules.clear.v03.web;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.ClErrorInfo;
import com.coffer.businesses.modules.clear.v03.entity.ClInMain;
import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
import com.coffer.businesses.modules.clear.v03.entity.DayReportGuest;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.clear.v03.service.BankGetService;
import com.coffer.businesses.modules.clear.v03.service.CenterAccountsMainService;
import com.coffer.businesses.modules.clear.v03.service.ClErrorInfoService;
import com.coffer.businesses.modules.clear.v03.service.DayReportGuestService;
import com.coffer.businesses.modules.clear.v03.service.OrderClearService;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 备付金交入Controller
 * 
 * @author wzj
 * @version 2017-12-19
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/blankPage")
public class BlankPageController extends BaseController {
	@Autowired
	private OrderClearService orderClearService;

	@Autowired
	private BankGetService bankGetService;

	@Autowired
	private ClErrorInfoService clErrorInfoService;

	@Autowired
	private DayReportGuestService dayReportGuestService;

	@Autowired
	private CenterAccountsMainService centerAccountsMainService;

	@Autowired
	private OfficeService officeService;

	/**
	 * 根据流水单号，取得备付金信息
	 * 
	 * @author wzj
	 * @version 2017年12月19日
	 * @param 单号(id)
	 * @return 备付金信息
	 */
	@ModelAttribute
	public ClInMain get(@RequestParam(required = false) String inNo) {
		ClInMain entity = null;
		if (StringUtils.isNotBlank(inNo)) {
		}
		if (entity == null) {

		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author wzj
	 * @version 2017年8月25日
	 * @param ClInMain
	 *            备付金信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 备付金信息列表页面
	 */
	// @RequiresPermissions("v03:blankPage:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClInMain bankPayInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 预约清分未清分各面值捆数及金额
		OrderClearMain orderClearMain = orderClearService.findClearList();
		if (orderClearMain == null) {
			orderClearMain = new OrderClearMain();
		}
		// 代理上缴完整券及残损券各面值捆数和金额
		// 登记用户所属机构
		ClOutMain clOutMainas = new ClOutMain();
		User user = UserUtils.getUser();
		clOutMainas.setCustNo(user.getOffice().getId());
		clOutMainas.setBusType(ClearConstant.BusinessType.AGENCY_PAY);
		clOutMainas.setStatus(ClearConstant.StatusType.CREATE);
		clOutMainas.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
		clOutMainas.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		ClOutMain clOutMain = bankGetService.findClearList(clOutMainas);
		if (clOutMain == null) {
			clOutMain = new ClOutMain();
		}
		// 商行取款已清分和未清分各面值捆数及金额
		ClOutMain clOutMains = bankGetService.findClearLists();
		if (clOutMains == null) {
			clOutMains = new ClOutMain();
		}
		// 差错管理各面值数量及金额
		ClErrorInfo clErrorInfo = clErrorInfoService.findClearList();
		if (clErrorInfo == null) {
			clErrorInfo = new ClErrorInfo();
			clErrorInfo.setErrorMoney(new BigDecimal(0));
		}
		// 查询当前机构备付金昨日余额
		BigDecimal beforeAmount = new BigDecimal(0);
		List<DayReportGuest> dayReportGuestList = dayReportGuestService.findAccountByAccountsType();
		if (!Collections3.isEmpty(dayReportGuestList)) {
			if (DateUtils.formatDate(new Date(), "yyyy-MM-dd")
					.equals(DateUtils.formatDate(dayReportGuestList.get(0).getReportDate(), "yyyy-MM-dd"))) {
				beforeAmount = dayReportGuestList.get(0).getBeforeAmount();
			} else {
				beforeAmount = dayReportGuestList.get(0).getTotalAmount();
			}

		}
		// 查询今日当前机构备付金收入和支出
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 查询条件：开始时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询条件：结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 判断用户类型
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setClientId(userInfo.getOffice().getId());
		// 业务类型为备付金
		List<String> busTypelists = Lists.newArrayList();
		busTypelists = Arrays.asList(Global.getStringArray("accounts.businessType.pro"));
		centerAccountsMain.setBusinessTypes(busTypelists);
		// 查询当天所有的收入和支出
		List<CenterAccountsMain> list = centerAccountsMainService.findList(centerAccountsMain);
		// 收入
		BigDecimal inAmount = new BigDecimal(0);
		// 支出
		BigDecimal outAmount = new BigDecimal(0);
		for (CenterAccountsMain lists : list) {
			if (lists.getInAmount() == null) {
				lists.setInAmount(new BigDecimal(0));
			}
			if (lists.getOutAmount() == null) {
				lists.setOutAmount(new BigDecimal(0));
			}
			inAmount = inAmount.add(lists.getInAmount());
			outAmount = outAmount.add(lists.getOutAmount());
		}
		// 查询当前机构备付金当前余额
		BigDecimal totalAmount = beforeAmount;
		totalAmount = totalAmount.add(inAmount.subtract(outAmount));
		model.addAttribute("orderClearMain", orderClearMain);
		model.addAttribute("clOutMain", clOutMain);
		model.addAttribute("clOutMains", clOutMains);
		model.addAttribute("clErrorInfo", clErrorInfo);
		model.addAttribute("beforeAmount", beforeAmount);
		model.addAttribute("totalAmount", totalAmount);
		return "modules/clear/v03/blankPage/blankPageList";
	}

	/**
	 * 
	 * 
	 * @author sg
	 * @version 2017年12月25日
	 * @param ClOutMain
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	// @RequiresPermissions("v03:blankPage:view")
	@RequestMapping(value = { "peoplePageList" })
	public String peoplePageList(ClOutMain bankPayInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		ClOutMain clOutMain = new ClOutMain();
		clOutMain.setBusType(ClearConstant.BusinessType.AGENCY_PAY);
		clOutMain.setStatus(ClearConstant.StatusType.CREATE);
		// 查询条件（日）
		clOutMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
		clOutMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		List<ClOutMain> clOutMainDayList = bankGetService.findPeopleList(clOutMain);
		ClOutMain clOutMainDay = bankGetService.findClearList(clOutMain);
		// 查询条件（月）
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		time.setTime(now);
		// 本月一号
		time.set(Calendar.DATE, 1);
		// 将小时至0
		time.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		time.set(Calendar.MINUTE, 0);
		// 将秒至0
		time.set(Calendar.SECOND, 0);
		// 将毫秒至0
		time.set(Calendar.MILLISECOND, 0);
		Date firstDate = time.getTime();
		clOutMain.setSearchDateStart(DateUtils.foramtSearchDate(firstDate));
		clOutMain.setSearchDateEnd(DateUtils.foramtSearchDate(now));
		List<ClOutMain> clOutMainMouthList = bankGetService.findPeopleList(clOutMain);
		ClOutMain clOutMainMouth = bankGetService.findClearList(clOutMain);
		model.addAttribute("clOutMainDayList", clOutMainDayList);
		model.addAttribute("clOutMainMouthList", clOutMainMouthList);
		model.addAttribute("clOutMainDay", clOutMainDay);
		model.addAttribute("clOutMainMouth", clOutMainMouth);
		return "modules/clear/v03/blankPage/peopleBankPage";
	}

	/**
	 * @author sg
	 * @version 2017年12月26日
	 * 
	 * 
	 * @param
	 * 
	 * 
	 * @return
	 */
	@RequestMapping(value = { "peopleChart" })
	@ResponseBody
	public String peopleChart(String param) {
		// 设置图例数据
		List<String> legendDataList = Lists.newArrayList();
		// 图形数据配置项
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		ClOutMain clOutMain = new ClOutMain();
		clOutMain.setBusType(ClearConstant.BusinessType.AGENCY_PAY);
		clOutMain.setStatus(ClearConstant.StatusType.CREATE);
		List<ClOutMain> clOutMainList = Lists.newArrayList();
		// 查询条件（日）
		clOutMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(new Date())));
		clOutMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		clOutMainList = bankGetService.findPeopleList(clOutMain);

		for (ClOutMain clOutMains : clOutMainList) {
			// 根据机构ID获取机构信息
			Office office = officeService.get(clOutMains.getCustNo());
			legendDataList.add(office.getName());
			Map<String, Object> seriesMap = Maps.newHashMap();
			seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, office.getName());
			seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
			List<BigDecimal> totalList = Lists.newArrayList();
			totalList.add((clOutMains.getTotalAmt() == null ? new BigDecimal(0) : clOutMains.getTotalAmt())
					.add((clOutMains.getTotalAmtc() == null ? new BigDecimal(0) : clOutMains.getTotalAmtc())));
			seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, totalList);
			Map<String, Object> labelMap = Maps.newHashMap();
			labelMap.put("show", true);
			labelMap.put("position", "top");
			DecimalFormat d = new DecimalFormat("#,##0.00#");
			labelMap.put("formatter", d.format(totalList.get(0)));
			Map<String, Object> normalMap = Maps.newHashMap();
			normalMap.put(ReportGraphConstant.SeriesProperties.SERIES_LABEL_KEY, labelMap);
			Map<String, Object> itemStyleMap = Maps.newHashMap();
			itemStyleMap.put(ReportGraphConstant.SeriesProperties.NORMAL_KEY, normalMap);
			seriesMap.put(ReportGraphConstant.SeriesProperties.ITEMSTYLE_KEY, itemStyleMap);
			seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY, "30");
			seriesDataList.add(seriesMap);
		}

		Map<String, Object> barMap = Maps.newHashMap();
		barMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		barMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return gson.toJson(barMap);
	}

	/**
	 * @author sg
	 * @version 2017年12月26日
	 * 
	 * 
	 * @param
	 * 
	 * 
	 * @return
	 */
	@RequestMapping(value = { "peopleMouthChart" })
	@ResponseBody
	public String peopleMouthChart(String param) {
		// 设置图例数据
		List<String> legendDataList = Lists.newArrayList();
		// 设置X轴数据
		List<String> xAxisDataList = Lists.newArrayList();
		// 图形数据配置项
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		ClOutMain clOutMain = new ClOutMain();
		clOutMain.setBusType(ClearConstant.BusinessType.AGENCY_PAY);
		clOutMain.setStatus(ClearConstant.StatusType.CREATE);
		// 查询条件（月）
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		time.setTime(now);
		// 本月一号
		time.set(Calendar.DATE, 1);
		// 将小时至0
		time.set(Calendar.HOUR_OF_DAY, 0);
		// 将分钟至0
		time.set(Calendar.MINUTE, 0);
		// 将秒至0
		time.set(Calendar.SECOND, 0);
		// 将毫秒至0
		time.set(Calendar.MILLISECOND, 0);
		Date firstDate = time.getTime();
		clOutMain.setSearchDateStart(DateUtils.foramtSearchDate(firstDate));
		clOutMain.setSearchDateEnd(DateUtils.foramtSearchDate(now));
		List<ClOutMain> clOutMainMouthList = bankGetService.findPeopleByDayList(clOutMain);
		// 设置x轴数据
		for (ClOutMain clOutMains : clOutMainMouthList) {
			if (!xAxisDataList.contains(clOutMains.getHandInDate())) {
				xAxisDataList.add(clOutMains.getHandInDate());
			}
			// 根据机构ID获取机构信息
			Office office = officeService.get(clOutMains.getCustNo());
			// 设置图例
			if (!legendDataList.contains(office.getName())) {
				legendDataList.add(office.getName());
			}
		}
		for (String legendData : legendDataList) {
			List<ClOutMain> clOutMainList = Lists.newArrayList();
			for (ClOutMain clOutMains : clOutMainMouthList) {
				// 根据机构ID获取机构信息
				Office office = officeService.get(clOutMains.getCustNo());
				if (legendData.equals(office.getName())) {
					clOutMainList.add(clOutMains);
				}
			}
			if (!Collections3.isEmpty(clOutMainList)) {
				Map<String, Object> seriesMap = Maps.newHashMap();
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, legendData);
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.LINE);
				List<BigDecimal> totalList = Lists.newArrayList();
				for (int i = 0; i < xAxisDataList.size(); i++) {
					ClOutMain clOutMaint = new ClOutMain();
					if (!xAxisDataList.get(i).equals(clOutMainList.get(i).getHandInDate())) {
						totalList.add(new BigDecimal(0));
						clOutMainList.add(i, clOutMaint);
					} else {
						totalList.add((clOutMainList.get(i).getTotalAmt() == null ? new BigDecimal(0)
								: clOutMainList.get(i).getTotalAmt())
										.add((clOutMainList.get(i).getTotalAmtc() == null ? new BigDecimal(0)
												: clOutMainList.get(i).getTotalAmtc())));
						if (i == clOutMainList.size() - 1) {
							seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, totalList);
							seriesDataList.add(seriesMap);
							break;
						}
					}
				}
			}
		}
		Map<String, Object> barMap = Maps.newHashMap();
		barMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		barMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		barMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return gson.toJson(barMap);
	}
}