package com.coffer.businesses.modules.clear.v03.web;

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
import com.coffer.businesses.modules.clear.v03.entity.EmpWorkStatistics;
import com.coffer.businesses.modules.clear.v03.service.EmpWorkStatisticsService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 员工工作量统计Controller
 * 
 * @author XL
 * @version 2017-09-04
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/empWorkStatistics")
public class EmpWorkStatisticsController extends BaseController {

	@Autowired
	private EmpWorkStatisticsService empWorkStatisticsService;

	@ModelAttribute
	public EmpWorkStatistics get(@RequestParam(required = false) String detailId) {
		EmpWorkStatistics entity = null;
		if (StringUtils.isNotBlank(detailId)) {
			entity = empWorkStatisticsService.get(detailId);
		}
		if (entity == null) {
			entity = new EmpWorkStatistics();
		}
		return entity;
	}

	/**
	 * 获取员工工作量统计列表
	 * 
	 * @author XL
	 * @version 2017-09-04
	 * @param empWorkStatistics
	 * @param request
	 * @param response
	 * @param model
	 * @return 任务回收统计列页面表
	 */
	@RequiresPermissions("clear:v03:empWorkStatistics:view")
	@RequestMapping(value = { "list", "" })
	public String list(EmpWorkStatistics empWorkStatistics, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		if (empWorkStatistics.getOperateTimeStart() == null) {
			empWorkStatistics.setOperateTimeStart(new Date());
		}
		if (empWorkStatistics.getOperateTimeEnd() == null) {
			empWorkStatistics.setOperateTimeEnd(new Date());
		}
		/* end */
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		User userInfo = UserUtils.getUser();
		// empWorkStatistics.setOffice(userInfo.getOffice());
		/* end */
		/* 改变查询方法 wzj 2017-11-15 begin */
		// 查询用户（现钞清分员，清分管理员类型）
		String[] userTypeArray = Global.getConfig(Constant.CLEAR_MANAGE_TYPE).split(";");
		// 名称存储list
		List<User> empNameList = Lists.newArrayList();
		// 添加人员
		// qph 2018-05-02 begin 增加数据穿透
		for (int i = 0; i < userTypeArray.length; i++) {
			empNameList = Collections3.union(empNameList,
					UserUtils.getUsersByTypeAndOfficeByfilter(userTypeArray[i], userInfo.getOffice().getId()));
		}
		// end
		// 查询对应机械清分手动清分和复点的数据并进行分页
		Page<EmpWorkStatistics> page = empWorkStatisticsService
				.findPageAllList(new Page<EmpWorkStatistics>(request, response), empWorkStatistics);
		// 员工名称list
		model.addAttribute("empNameList", empNameList);
		/* end */
		model.addAttribute("page", page);
		return "modules/clear/v03/empWorkStatistics/empWorkStatisticsList";
	}

	/**
	 * @author sg
	 * @version 2017年11月21日
	 * 
	 *          根据查询条件，导出业务量数据
	 * @param ClErrorInfo
	 * 
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 * 
	 * 
	 */
	@RequestMapping(value = { "exportEmpWorkInfoReport" })
	public void exportEmpWorkInfoReport(EmpWorkStatistics empWorkStatistics, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 查询条件： 开始时间
		if (empWorkStatistics.getOperateTimeStart() != null) {
			empWorkStatistics.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(empWorkStatistics.getOperateTimeStart())));
		}
		// 查询条件： 结束时间
		if (empWorkStatistics.getOperateTimeEnd() != null) {
			empWorkStatistics.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(empWorkStatistics.getOperateTimeEnd())));
		}

		// 导出excel
		List<Map<String, Object>> paramList = Lists.newArrayList();
		// 获得当前登录人信息
		User userInfo = UserUtils.getUser();
		// 设置机构为当前登录人机构
		empWorkStatistics.setOffice(userInfo.getOffice());

		// 根据条件查询数据
		List<EmpWorkStatistics> empWorkInfoList = empWorkStatisticsService.findEmpWorkStatisticsList(empWorkStatistics);
		// 无数据时设置一条空数据
		if (Collections3.isEmpty(empWorkInfoList)) {
			// 添加一条新数据
			EmpWorkStatistics empWork = new EmpWorkStatistics();
			// 如果输入框中输入员工名称
			if (StringUtils.isNoneBlank(empWorkStatistics.getEmpNo())) {
				User newUser = UserUtils.get(empWorkStatistics.getEmpNo());
				empWork.setEmpName(newUser.getName());
			}
			empWorkInfoList.add(empWork);
		}
		// 序号
		int number = 1;
		// 添加序号
		for (EmpWorkStatistics emp : empWorkInfoList) {
			emp.setNumber(String.valueOf(number));
			number++;
		}
		// 开始时间为空
		if (StringUtils.isBlank(empWorkStatistics.getSearchDateStart())) {
			empWorkStatistics.setSearchDateStart("");
		}
		// 结束时间为空
		if (StringUtils.isBlank(empWorkStatistics.getSearchDateEnd())) {
			empWorkStatistics.setSearchDateEnd("");
		}

		// 获取当前登录人的机构
		String officeName = UserUtils.getUser().getOffice().getName();
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		// 员工工作量结算.xls
		String fileName = msg.getMessage("clear.report.empWorkStatisticsBalanceExcel", null, locale);
		// sheet1
		Map<String, Object> sheet1Map = Maps.newHashMap();
		// sheet页名为员工工作量计算
		sheet1Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("clear.report.empWorkStatisticsBalance", null, locale));
		Map<String, Object> sheet1TitleMap = Maps.newHashMap();
		// 添加制表机构
		sheet1TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);
		// 添加开始时间
		sheet1TitleMap.put(ReportConstant.ReportExportData.START_TIME, empWorkStatistics.getSearchDateStart());
		// 添加结束时间
		sheet1TitleMap.put(ReportConstant.ReportExportData.END_TIME, empWorkStatistics.getSearchDateEnd());
		sheet1Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
		// 添加数据
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, empWorkInfoList);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, EmpWorkStatistics.class.getName());
		paramList.add(sheet1Map);
		// 导出
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

}