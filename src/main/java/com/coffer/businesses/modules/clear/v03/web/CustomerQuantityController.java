package com.coffer.businesses.modules.clear.v03.web;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.v03.entity.CustomerClearance;
import com.coffer.businesses.modules.clear.v03.service.CustomerClearanceService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 清分管理任务回收量统计Controller
 * 
 * @author wzj
 * @version 2017-09-06
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/customerQuantity")
public class CustomerQuantityController extends BaseController {

	@Autowired
	private CustomerClearanceService customerClearanceService;

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
	 * @version 2017-10-16
	 * @param customerClearance
	 * @return 查询客户清分量信息页面表
	 */
	@RequestMapping(value = { "list", "" })
	public String list(CustomerClearance customerClearance, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		time.setTime(now);
		// 本月一号
		time.set(Calendar.DATE, 1);
		Date firstDate = time.getTime();
		if (customerClearance.getCreateTimeStart() == null) {
			customerClearance.setCreateTimeStart(firstDate);
		}
		if (customerClearance.getCreateTimeEnd() == null) {
			customerClearance.setCreateTimeEnd(now);
		}
		/* end */
		model.addAttribute("customerClearance", customerClearance);
		return "modules/clear/v03/customerQuantity/customerQuantity";
	}

	/**
	 * 查询表格对应数据
	 * 
	 * @author wzj
	 * @param CustomerClearance
	 * @param request
	 * @return String
	 */
	@RequestMapping(value = "getCustomerQuantity")
	@ResponseBody
	public String getCustomerQuantity(CustomerClearance customerClearance, HttpServletRequest request) {
		// 设置查询条件
		setCondition(customerClearance);
		// 商行或人行
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			customerClearance.setCustName(UserUtils.getUser().getOffice().getName());
		}
		Map<String, Object> jsonData = customerClearanceService.findListCustomerQuantity(customerClearance);
		// 查询条件中的年月日周季度
		String status = customerClearance.getFilterCondition();
		// 将查询结果的横线进行切换
		String timeName = status.replace(Constant.Punctuation.HYPHEN, Constant.Punctuation.HALF_UNDERLINE);
		// 返回json数据
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(timeName, "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}

	/**
	 * 设置查询条件
	 * 
	 * @author wzj
	 * @version 2017-10-16
	 * @param customerClearance
	 * @return
	 */
	public Map<String, Object> setCondition(CustomerClearance customerClearance) {
		Map<String, Object> map = Maps.newHashMap();

		// 格式化filterCondition 年 月 日 季度 周
		String status = "";
		// 开始日期
		String createTimeStart = null;
		// 结束日期
		String createTimeEnd = null;
		// 初始为空时给第一个下拉菜单(年)
		if (StringUtils.isBlank(customerClearance.getFilterCondition())) {
			status = "yyyy";
		} else {
			status = customerClearance.getFilterCondition().replace(Constant.Punctuation.HALF_UNDERLINE,
					Constant.Punctuation.HYPHEN);
		}
		// 设置查询条件
		customerClearance.setFilterCondition(status);
		// 获取字典标签
		String timeName = status.replace(Constant.Punctuation.HYPHEN, Constant.Punctuation.HALF_UNDERLINE);
		// 获得过滤条件对应汉字
		String filter = DictUtils.getDictLabel(timeName, "report_filter_condition", "");

		// 查询条件：开始时间
		if (customerClearance.getCreateTimeStart() != null) {
			createTimeStart = DateUtils.formatDate(DateUtils.getDateStart(customerClearance.getCreateTimeStart()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);
			customerClearance.setSearchDateStart(createTimeStart);
		}
		// 查询条件：结束时间
		if (customerClearance.getCreateTimeEnd() != null) {
			createTimeEnd = DateUtils.formatDate(DateUtils.getDateEnd(customerClearance.getCreateTimeEnd()),
					AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS);
			customerClearance.setSearchDateEnd(createTimeEnd);
		}
		// 放入map中
		map.put(ReportConstant.ReportExportData.START_TIME,
				StringUtils.isBlank(createTimeStart) ? "" : createTimeStart);
		map.put(ReportConstant.ReportExportData.END_TIME, StringUtils.isBlank(createTimeEnd) ? "" : createTimeEnd);
		map.put(ReportConstant.ReportExportData.DATE_UNIT, filter);
		return map;
	}

	/**
	 * @author wzj
	 * @version 2017年10月20日
	 * @param CustomerClearance
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "exportQuantityReport" })
	public void exportQuantityReport(CustomerClearance customerClearance, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 导出excel
		List<Map<String, Object>> paramList = Lists.newArrayList();
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// // 查询条件
		Map<String, Object> sheet1TitleMap = setCondition(customerClearance);
		// 商行或人行
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())
				|| Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			customerClearance.setCustName(UserUtils.getUser().getOffice().getName());
		}
		List<String> xAxisDataList = Lists.newArrayList();
		List<CustomerClearance> dateList = customerClearanceService.findListDate(customerClearance, xAxisDataList);
		// 银行类型
		List<String> bankType = Lists.newArrayList();
		// 银行名称
		List<String> custName = Lists.newArrayList();
		// 获得银行名称和类型
		for (CustomerClearance date : dateList) {
			if (!custName.contains(date.getCustName())) {
				custName.add(date.getCustName());
				bankType.add(date.getBusType());
			}
		}
		String fileName = "";
		// 判断是商行还是人行
		if (custName.size() == 1 && bankType.size() == 1) {
			if (bankType.get(0).equals("1")) {
				// 人民银行客户清点量统计图.xls
				fileName = msg.getMessage("clear.report.peopleBank", null, locale);
			} else {
				// 商业银行客户清点量统计图.xls
				fileName = msg.getMessage("clear.report.CommercialBank", null, locale);
			}
		}
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		// sheet页名称
		Map<String, Object> sheet1Map = Maps.newHashMap();
		// 客户清点量统计图
		sheet1Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("clear.report.customerStatistics", null, locale));
		// 客户名称
		sheet1TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE, custName);
		sheet1Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
		// 数据
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, dateList);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, CustomerClearance.class.getName());
		paramList.add(sheet1Map);
		// 导出
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

}