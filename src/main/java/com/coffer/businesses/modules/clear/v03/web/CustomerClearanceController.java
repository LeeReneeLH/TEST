package com.coffer.businesses.modules.clear.v03.web;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.v03.entity.CustomerClearance;
import com.coffer.businesses.modules.clear.v03.entity.ExcelExporterExMo;
import com.coffer.businesses.modules.clear.v03.service.CustomerClearanceService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.CityService;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 清分管理任务回收量统计Controller
 * 
 * @author wzj
 * @version 2017-09-06
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/customerClearance")
public class CustomerClearanceController extends BaseController {

	@Autowired
	private CustomerClearanceService customerClearanceService;
	@Autowired
	private OfficeService officeService;

	@Autowired
	private CityService cityService;

	@ModelAttribute
	public CustomerClearance get(@RequestParam(required = false) String outNo) {
		CustomerClearance entity = null;
		if (StringUtils.isNotBlank(outNo)) {
			entity = customerClearanceService.get(outNo);
		}
		if (entity == null) {
			entity = new CustomerClearance();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询客户清分量信息
	 * 
	 * @author wzj
	 * @version 2017-09-06
	 * @param customerClearance
	 * @return 查询客户清分量信息页面表
	 */
	@RequiresPermissions("clear:v03:customerClearance:view")
	@RequestMapping(value = { "list", "" })
	public String list(CustomerClearance customerClearance, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 设置查询条件
		setCondition(customerClearance);
		// 判断用户类型
		// 商行或人行
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			customerClearance.setCustNo(UserUtils.getUser().getOffice().getId());
		}
		Page<CustomerClearance> page = customerClearanceService
				.findPageList(new Page<CustomerClearance>(request, response), customerClearance);
		if (customerClearance.getOffice() != null) {
			model.addAttribute("officeId", customerClearance.getOffice().getId());
			model.addAttribute("officeName", customerClearance.getOffice().getName());
		}
		// 添加时间
		model.addAttribute("dateStart", DateUtils.parseDate(customerClearance.getSearchDateStart()));
		model.addAttribute("dateEnd", DateUtils.parseDate(customerClearance.getSearchDateEnd()));
		model.addAttribute("page", page);
		return "modules/clear/v03/customerClearance/customerClearanceList";
	}

	/**
	 * @author wzj
	 * @version 2017年9月8日
	 * 
	 *          根据查询条件，导出客户清分量数据
	 * @param customerClearance
	 *            客户清分量
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 * 
	 * @return
	 */
	@RequestMapping(value = { "exportCustomerReport" })
	public void exportCustomerReport(CustomerClearance customerClearance, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 获得当前登录机构的城市编号
		String officeId = UserUtils.getUser().getOffice().getId();
		// 获得登录机构对应城市code
		String cityCore = officeService.get(officeId).getCityCode();
		// 获得城市名称
		String cityName = "";
		if (StringUtils.isNotBlank(cityCore)) {
			cityName = cityService.findCityName(cityCore);
		}
		// 导出excel
		List<Map<String, Object>> paramList = Lists.newArrayList();
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 设置查询条件
		setCondition(customerClearance);
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			customerClearance.setCustNo(UserUtils.getUser().getOffice().getId());
			customerClearance.setrOffice(UserUtils.getUser().getOffice());
		}
		// 查询
		List<CustomerClearance> lists = customerClearanceService.findCustomerList(customerClearance);
		// 无数据时设置一条空数据
		if (Collections3.isEmpty(lists)) {
			lists.add(new CustomerClearance());
		}
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		// 选择03版本excel或者07版本excel
		// 客户清分量结算.xls
		String fileName = msg.getMessage("clear.report.Customer", null, locale);
		// String fileName = "客户清分量结算.xlsx";
		// 设置金额千分位
		DecimalFormat df = new DecimalFormat("#,##0.00#");
		// 第一个sheet
		CustomerClearance customers = new CustomerClearance();
		// 查询条件开始时间
		if (customerClearance.getCreateTimeStart() != null) {
			customers.setSearchDateStart(
					DateUtils.formatDate(DateUtils.getDateStart(customerClearance.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (customerClearance.getCreateTimeEnd() != null) {
			customers.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(customerClearance.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		String customerName = "";
		String customerNo = "";
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			customerName = UserUtils.getUser().getOffice().getName();
			customerNo = UserUtils.getUser().getOffice().getId();
		} else {
			if (StringUtils.isNotBlank(customerClearance.getrOffice().getId())) {
				// 查询条件中有名称
				Office nameOffice = SysCommonUtils.findOfficeById(customerClearance.getrOffice().getId());
				customerName = nameOffice.getName();
				customerNo = nameOffice.getId();
			}
		}
		// 设置用户名称
		customers.setCustNo(customerNo);
		// 设置发生机构
		customers.setOffice(customerClearance.getOffice());
		// 进行查询
		List<CustomerClearance> setDateLists = customerClearanceService.findCustomerAllList(customers);
		CustomerClearance firstSheetCount = customerClearanceService.findCountFirstSheet(customers);
		// 无数据时设置一条空数据
		if (Collections3.isEmpty(setDateLists)) {
			CustomerClearance cust = new CustomerClearance();
			if (StringUtils.isNoneBlank(customerName)) {
				cust.setCustName(customerName);
			}
			setDateLists.add(cust);
		} else {
			// 增加千分位
			for (CustomerClearance dates : setDateLists) {
				BigDecimal count = new BigDecimal(dates.getCount());
				dates.setCount(df.format(count).toString());
			}
		}
		Map<String, Object> sheet0Map = Maps.newHashMap();
		// sheet名称"现钞处理中心清分量结算总表"
		sheet0Map.put(ExcelExporterExMo.SHEET_NAME_MAP_KEY,
				msg.getMessage("clear.report.cashProcessingCenter", null, locale));
		Map<String, Object> sheet0TitleMap = Maps.newHashMap();
		sheet0TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, customerClearance.getOffice().getName());
		// 设置sheet页的对应静态数据(聚龙()现钞处理中心清分量结算总表)
		sheet0TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
				msg.getMessage("clear.report.julong", null, locale) + cityName
						+ msg.getMessage("clear.report.clearComponentAll", null, locale));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
		// 开始时间
		Date startDate = new Date();
		// 结束时间
		Date endDate = new Date();
		// 开始时间为空
		if (StringUtils.isBlank(customerClearance.getSearchDateStart())) {
			customerClearance.setSearchDateStart(DateUtils.formatDate(startDate, "yyyy-MM-dd"));
		}
		// 结束时间为空
		if (StringUtils.isBlank(customerClearance.getSearchDateEnd())) {
			customerClearance.setSearchDateEnd(DateUtils.formatDate(endDate, "yyyy-MM-dd"));
		}
		// 开始时间结束时间进行格式处理，去掉时分秒
		try {
			startDate = sdf.parse(customerClearance.getSearchDateStart());
			endDate = sdf.parse(customerClearance.getSearchDateEnd());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 开始时间
		sheet0TitleMap.put(ReportConstant.ReportExportData.START_TIME, DateUtils.formatDate(startDate, "yyyy-MM-dd"));
		// 结束时间
		sheet0TitleMap.put(ReportConstant.ReportExportData.END_TIME, DateUtils.formatDate(endDate, "yyyy-MM-dd"));
		sheet0Map.put(ExcelExporterExMo.SHEET_TITLE_MAP_KEY, sheet0TitleMap);
		// 第一页总数量
		if (firstSheetCount != null) {
			firstSheetCount.setCustName(msg.getMessage("clear.businessAccount.total", null, locale) + ":");
			setDateLists.add(firstSheetCount);
		}
		// 数据
		sheet0Map.put(ExcelExporterExMo.SHEET_DATA_LIST_MAP_KEY, setDateLists);
		sheet0Map.put(ExcelExporterExMo.SHEET_DATA_ENTITY_CLASS_NAME_KEY, CustomerClearance.class.getName());
		paramList.add(sheet0Map);

		// 循环添加银行名称进行后面sheet的添加
		// 遍历赋值多个sheet（如果为空方法内添加成sheet）
		List<String> listDate = Lists.newArrayList();
		// 一共有几个客户机构
		for (CustomerClearance sheets : lists) {
			if (!listDate.contains(sheets.getCustName())) {
				listDate.add(sheets.getCustName());
			}
		}
		// 遍历机构名称
		for (String officeName : listDate) {
			// 第二个sheet
			CustomerClearance customer = new CustomerClearance();
			if (customerClearance.getCreateTimeStart() != null) {
				customer.setSearchDateStart(
						DateUtils.formatDate(DateUtils.getDateStart(customerClearance.getCreateTimeStart()),
								AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			}
			// 查询条件：结束时间
			if (customerClearance.getCreateTimeEnd() != null) {
				customer.setSearchDateEnd(
						DateUtils.formatDate(DateUtils.getDateEnd(customerClearance.getCreateTimeEnd()),
								AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
			}

			// 数据为空
			if (StringUtils.isBlank(officeName)) {
				if (StringUtils.isNotBlank(customerClearance.getrOffice().getId())) {
					// 查询条件中有名称
					Office nameOffice = SysCommonUtils.findOfficeById(customerClearance.getrOffice().getId());
					officeName = nameOffice.getName();
				} else {
					// 查询条件中无名称(银行名称未定义)
					officeName = msg.getMessage("clear.report.bankUndefined", null, locale);
				}
			}
			// 设置名称
			customer.setCustName(officeName);
			// 设置发生机构
			customer.setOffice(customerClearance.getOffice());
			List<CustomerClearance> setDateList = customerClearanceService.findCustomerList(customer);
			// 查询总数量
			CustomerClearance customerCount = customerClearanceService.findAllCount(customer);
			BigDecimal totalMoney = new BigDecimal(0);
			// 放入空对象
			if (Collections3.isEmpty(setDateList)) {
				CustomerClearance customerNull = new CustomerClearance();
				setDateList.add(customerNull);
			}
			for (CustomerClearance date : setDateList) {
				// 设置导出日期格式
				if (date.getSearchDate() != null) {
					date.setDates(DateUtils.formatDate(date.getSearchDate(), "yyyy-MM-dd"));
				} else {
					date.setDates("");
				}
				// 判断是否为空数据
				if (date.getCount() != null) {
					// 计算总金额
					totalMoney = totalMoney.add(new BigDecimal(date.getCount()));
				}
			}
			Map<String, Object> sheet1Map = Maps.newHashMap();
			sheet1Map.put(ExcelExporterExMo.SHEET_NAME_MAP_KEY, officeName);
			Map<String, Object> sheet1TitleMap = Maps.newHashMap();
			// 设置sheet页的对应静态数据(聚龙()现钞处理中心清分量结算表)
			sheet1TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
					msg.getMessage("clear.report.julong", null, locale) + cityName
							+ msg.getMessage("clear.report.clearComponent", null, locale));
			sheet1TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);
			// 金额总计
			sheet1TitleMap.put("totalMoney", df.format(totalMoney));

			// 开始时间为空
			if (StringUtils.isBlank(customerClearance.getSearchDateStart())) {
				customerClearance.setSearchDateStart(DateUtils.formatDate(startDate, "yyyy-MM-dd"));
			}
			// 结束时间为空
			if (StringUtils.isBlank(customerClearance.getSearchDateEnd())) {
				customerClearance.setSearchDateEnd(DateUtils.formatDate(endDate, "yyyy-MM-dd"));
			}

			sheet1TitleMap.put(ReportConstant.ReportExportData.START_TIME,
					DateUtils.formatDate(startDate, "yyyy-MM-dd"));
			sheet1TitleMap.put(ReportConstant.ReportExportData.END_TIME, DateUtils.formatDate(endDate, "yyyy-MM-dd"));
			sheet1Map.put(ExcelExporterExMo.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
			// 总数量
			if (customerCount != null) {
				// 总计：
				customerCount.setFilterCondition(msg.getMessage("clear.businessAccount.total", null, locale) + ":");
				setDateList.add(customerCount);
			}
			sheet1Map.put(ExcelExporterExMo.SHEET_DATA_LIST_MAP_KEY, setDateList);
			sheet1Map.put(ExcelExporterExMo.SHEET_DATA_ENTITY_CLASS_NAME_KEY, CustomerClearance.class.getName());
			paramList.add(sheet1Map);
		}
		// 导出
		ExcelExporterExMo exportExMo = new ExcelExporterExMo(paramList);
		exportExMo.createWorkBook(request, response, templatePath, fileName);
	}

	/**
	 * 设置查询条件
	 * 
	 * @author wzj
	 * @version 2017-09-06
	 * @param customerClearance
	 * @return
	 */
	public void setCondition(CustomerClearance customerClearance) {
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		// 查询条件：开始时间
		if (customerClearance.getCreateTimeStart() != null) {
			customerClearance.setSearchDateStart(
					DateUtils.formatDate(DateUtils.getDateStart(customerClearance.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		} else {
			// 过去一年
			time.setTime(now);
			/* 修改为过去一个月 wzj 2017-11-15 begin */
			time.add(Calendar.MONTH, -1);
			/* end */
			Date lastYear = time.getTime();
			customerClearance.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(lastYear),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (customerClearance.getCreateTimeEnd() != null) {
			customerClearance
					.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(customerClearance.getCreateTimeEnd()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		} else {
			// 当天
			customerClearance.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(now),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
	}
}