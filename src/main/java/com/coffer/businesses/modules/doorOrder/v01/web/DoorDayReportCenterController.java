package com.coffer.businesses.modules.doorOrder.v01.web;

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

import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportCenter;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorDayReportCenterService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 中心账务日结Controller
 * 
 * @author QPH
 * @version 2017-09-13
 */
@Controller("dayReportCenter")
@RequestMapping(value = "${adminPath}/doorOrder/v01/dayReportCenter")
public class DoorDayReportCenterController extends BaseController {

	@Autowired
	private DoorDayReportCenterService dayReportCenterService;

	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;

	@ModelAttribute
	public DoorDayReportCenter get(@RequestParam(required = false) String id) {
		DoorDayReportCenter entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = dayReportCenterService.get(id);
		}
		if (entity == null) {
			entity = new DoorDayReportCenter();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:dayReportCenter:view")
	@RequestMapping(value = { "list", "" })
	public String list(DoorDayReportCenter dayReportCenter, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<DoorDayReportCenter> page = dayReportCenterService
				.findPage(new Page<DoorDayReportCenter>(request, response), dayReportCenter);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/dayReportCenterList";
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
			@RequestParam(required = false) String reportDate, @RequestParam(required = false) String reportMainId,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Date date = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
			date = sdf.parse(reportDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 设置中心账务
		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 设置业务类型
		List<String> businessTypeList = Lists.newArrayList();
		// 上门收款业务
		if (DoorOrderConstant.AccountsType.ACCOUNTS_DOOR.equals(accountsType)) {
			businessTypeList.add(DoorOrderConstant.BusinessType.DOOR_ORDER);
			businessTypeList.add(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
			businessTypeList.add(DoorOrderConstant.BusinessType.CENTER_PAID);
		}
		centerAccountsMain.setBusinessTypes(businessTypeList);
		// 设置发生机构
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 设置查询时间(起始时间为本次日结的上一次日结时间，结束时间为本次日结时间)
		centerAccountsMain.setSearchDateStart(
				DateUtils.formatDateTime(DoorCommonUtils.getThisDayReportMaxDate(userInfo.getOffice(), date)));
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(date));
		Page<DoorCenterAccountsMain> page = centerAccountsMainService
				.findPageByDetail(new Page<DoorCenterAccountsMain>(request, response), centerAccountsMain, null);
		model.addAttribute("page", page);
		model.addAttribute("reportDate", reportDate);
		return "modules/doorOrder/v01/dayReport/dayReportCenterDetailList";
	}
}