package com.coffer.businesses.modules.clear.v03.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.entity.DayReportCenter;
import com.coffer.businesses.modules.clear.v03.service.CenterAccountsMainService;
import com.coffer.businesses.modules.clear.v03.service.DayReportCenterService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 中心账务日结Controller
 * @author QPH
 * @version 2017-09-13
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/dayReportCenter")
public class DayReportCenterController extends BaseController {

	@Autowired
	private DayReportCenterService dayReportCenterService;
	
	@Autowired
	private CenterAccountsMainService centerAccountsMainService;

	@ModelAttribute
	public DayReportCenter get(@RequestParam(required=false) String id) {
		DayReportCenter entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = dayReportCenterService.get(id);
		}
		if (entity == null){
			entity = new DayReportCenter();
		}
		return entity;
	}
	
	@RequiresPermissions("clear:v03:dayReportCenter:view")
	@RequestMapping(value = {"list", ""})
	public String list(DayReportCenter dayReportCenter, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<DayReportCenter> page = dayReportCenterService.findPage(new Page<DayReportCenter>(request, response), dayReportCenter); 
		model.addAttribute("page", page);
		return "modules/clear/v03/dayReportCenterList";
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月13日 账务结算查看每笔明细
	 * 
	 * @param dayReportMain
	 * @param request
	 * @param response
	 * @param model
	 * @param reportDate
	 *            结账日期
	 * @return
	 */

	@RequestMapping(value = "detailView")
	public String detailView(@RequestParam(required = false) String accountsType,
			@RequestParam(required = false) String reportDate,
			HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Date date = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			date = sdf.parse(reportDate);
		} catch (ParseException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		// 设置中心账务
		CenterAccountsMain centerAccountsMain = new CenterAccountsMain();
		// 设置业务类型
		List<String> businessTypeList = Lists.newArrayList();
		// 清分业务
		if(ClearConstant.AccountsType.ACCOUNTS_CLEAR.equals(accountsType)){
			businessTypeList.add(ClearConstant.BusinessType.BANK_GET);
			businessTypeList.add(ClearConstant.BusinessType.BANK_PAY);
			businessTypeList.add(ClearConstant.BusinessType.AGENCY_PAY);
		}
		// 复点业务
		if(ClearConstant.AccountsType.ACCOUNTS_COMPLEX.equals(accountsType)){
			businessTypeList.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
			businessTypeList.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		} 
		// 备付金业务
		if (ClearConstant.AccountsType.ACCOUNTS_PROVISIONS.equals(accountsType)) {
			businessTypeList.add(ClearConstant.BusinessType.PROVISIONS_IN);
			businessTypeList.add(ClearConstant.BusinessType.PROVISIONS_OUT);
			businessTypeList.add(ClearConstant.BusinessType.ERROR_HANDLING);
		}
		centerAccountsMain.setBusinessTypes(businessTypeList);
		// 设置查询时间
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(date)));
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(date));
		// 设置发生机构
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		Page<CenterAccountsMain> page = centerAccountsMainService
				.findPageByDetail(new Page<CenterAccountsMain>(request, response), centerAccountsMain, null);
		model.addAttribute("page", page);
		return "modules/clear/v03/dayReport/dayReportDetailList";
	}

}