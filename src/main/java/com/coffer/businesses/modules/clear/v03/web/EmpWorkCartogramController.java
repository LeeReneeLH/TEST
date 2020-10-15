package com.coffer.businesses.modules.clear.v03.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.v03.entity.EmpWorkStatistics;
import com.coffer.businesses.modules.clear.v03.service.EmpWorkStatisticsService;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.google.common.collect.Lists;

/**
 * 员工工作量统计图Controller
 * 
 * @author wxz
 * @version 2017-10-16
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/empWorkCartogram")
public class EmpWorkCartogramController extends BaseController {

	@Autowired
	private EmpWorkStatisticsService empWorkStatisticsService;

	/**
	 * 根据查询条件，查询回收明细统计一览信息
	 * 
	 * @author wxz
	 * @version 2017-10-16
	 * @param ReportCondition
	 * @return 任务回收统计列页面表
	 */
	@RequestMapping(value = { "toGraphPage", "" })
	public String toGraphPage(EmpWorkStatistics empWorkStatistics, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		time.setTime(now);
		// 本月一号
		time.set(Calendar.DATE, 1);
		Date firstDate = time.getTime();
		if (empWorkStatistics.getOperateTimeStart() == null) {
			empWorkStatistics.setOperateTimeStart(firstDate);
		}
		if (empWorkStatistics.getOperateTimeEnd() == null) {
			empWorkStatistics.setOperateTimeEnd(now);
		}
		/* end */
		model.addAttribute("empWorkStatistics", empWorkStatistics);
		return "modules/clear/v03/empWorkCartogram/empWorkCartogram";
	}
	
	/**
	 * @author wxz
	 * @version 2017年10月17日
	 * 
	 *          根据查询条件，查询清分类型数据统计并显示到页面图表中
	 * @param reportCondition
	 *            清分类型信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @return 员工工作量信息列表页面
	 */
	@RequestMapping(value = "graphicalHandInList")
	@ResponseBody
	public String graphicalHandInList(EmpWorkStatistics empWorkStatistics, HttpServletRequest request,
			HttpServletResponse response) {
		List<EmpWorkStatistics> handClear = Lists.newArrayList();
		List<EmpWorkStatistics> handPoint = Lists.newArrayList();
		EmpWorkStatistics alllatticePointHandin = selectCondition(empWorkStatistics);
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		// User userInfo = UserUtils.getUser();
		// alllatticePointHandin.setOffice(userInfo.getOffice());
		// handIn =
		// empWorkStatisticsService.findWorkingType(alllatticePointHandin);
		/* 修改查询数据调用方法 wzj 2017-12-5 begin */
		/* 查询清分数据 */
		handClear = empWorkStatisticsService.findWorkClearingList(alllatticePointHandin);
		/* 查询复点数据 */
		handPoint = empWorkStatisticsService.findWorkPointList(alllatticePointHandin);
		/* end */
		Map<String, Object> jsonData = empWorkStatisticsService.graphicalHandInList(handClear, handPoint);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(empWorkStatistics.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);
	}
	
	/**
	 * @author wxz
	 * @version 2017年10月17日
	 * 
	 *          根据查询条件，查询人员数据统计并显示到页面图表中
	 * @param reportCondition
	 *            人员姓名信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @return 员工工作量信息列表页面
	 */
	@RequestMapping(value = "peopleHandInList")
	@ResponseBody
	public String peopleHandInList(EmpWorkStatistics empWorkStatistics, HttpServletRequest request,
			HttpServletResponse response) {
		List<EmpWorkStatistics> handIn = Lists.newArrayList();
		EmpWorkStatistics alllatticePointHandin = selectCondition(empWorkStatistics);
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		// User userInfo = UserUtils.getUser();
		// alllatticePointHandin.setOffice(userInfo.getOffice());
		handIn = empWorkStatisticsService.findPeopleList(alllatticePointHandin);
		Map<String, Object> jsonData = empWorkStatisticsService.peopleHandInList(handIn);
		jsonData.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(empWorkStatistics.getFilterCondition(), "report_filter_condition", ""));
		return gson.toJson(jsonData);

	}
	
	/**
	 * @author wxz
	 * @version 2017年10月17日 添加查询条件
	 * @param reportCondition
	 *            员工工作量统计查询信息
	 * 
	 * @return LatticePointHandin
	 */
	private EmpWorkStatistics selectCondition(EmpWorkStatistics empWorkStatistics) {
		EmpWorkStatistics alllatticePointHandin = new EmpWorkStatistics();
		// 格式化filterCondition 年 月 日 季度 周
		/*String status = empWorkStatistics.getFilterCondition().replace(Constant.Punctuation.HALF_UNDERLINE,
				Constant.Punctuation.HYPHEN);
		alllatticePointHandin.setFilterCondition(status);*/
		
		// 查询条件：开始时间
		if (empWorkStatistics.getOperateTimeStart() != null) {
			alllatticePointHandin.setSearchDateStart(
					DateUtils.formatDate(DateUtils.getDateStart(empWorkStatistics.getOperateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (empWorkStatistics.getOperateTimeEnd() != null) {
			alllatticePointHandin
					.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(empWorkStatistics.getOperateTimeEnd()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 如果数字平台登录，机构过滤是否为空
	/*	if (Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
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
		}*/
		return alllatticePointHandin;
	}
	

}