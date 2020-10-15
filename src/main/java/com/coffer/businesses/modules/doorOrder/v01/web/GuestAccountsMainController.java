package com.coffer.businesses.modules.doorOrder.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 客户帐务Controller
 * 
 * @author XL
 * @version 2017年9月12日
 */
@Controller("guestaccountsmaincontroller")
@RequestMapping(value = "${adminPath}/doorOrder/v01/guestAccounts")
public class GuestAccountsMainController extends BaseController {

	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;

	/**
	 * 根据账务主键，取得客户账务信息
	 * 
	 * @author XL
	 * @version 2017年9月11日
	 * @param accountsId
	 * @return 客户账务信息
	 */
	@ModelAttribute
	public DoorCenterAccountsMain get(@RequestParam(required = false) String accountsId) {
		DoorCenterAccountsMain entity = null;
		if (StringUtils.isNotBlank(accountsId)) {
			entity = centerAccountsMainService.get(accountsId);
		}
		if (entity == null) {
			entity = new DoorCenterAccountsMain();
		}
		return entity;
	}

	/**
	 * 获取客户(门店)账务列表
	 * 
	 * @author XL
	 * @version 2017年9月13日
	 * @param type
	 * @param centerAccountsMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 客户帐务一览页面
	 */
	@RequiresPermissions("doorOrder:v01:guestAccounts:view")
	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam(value = "type", required = false) String type,
			DoorCenterAccountsMain doorCenterAccountsMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 默认现金业务
		if (StringUtils.isBlank(type)) {
			type = ClearConstant.AccountCashProType.CASH;
		}
		// 设置发生机构
		User userInfo = UserUtils.getUser();
		doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
		doorCenterAccountsMain.setRofficeName(userInfo.getOffice().getName());
		doorCenterAccountsMain.getSqlMap().put("dsf", "OR a.client_id = " + UserUtils.getUser().getOffice().getId()
				+ " OR q.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		/* 追加中心总账统计金额 add by lihe */
		List<DoorCenterAccountsMain> tabList = centerAccountsMainService.getAmountByBusinessType(doorCenterAccountsMain,
				type);
		if (Collections3.isEmpty(tabList)) {
			tabList = Lists.newArrayList();
		}
		/* end */
		Page<DoorCenterAccountsMain> page = new Page<DoorCenterAccountsMain>(request, response);
		page = centerAccountsMainService.findGuestAccountsPage(page, doorCenterAccountsMain, type);
		model.addAttribute("type", type);
		model.addAttribute("page", page);
		model.addAttribute("tabList", tabList);
		return "modules/doorOrder/v01/guestAccounts/doorAccountsList";
	}

	/**
	 * 获取客户(商户)账务列表
	 * 
	 * @author XL
	 * @version 2017年9月13日
	 * @param type
	 * @param centerAccountsMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 客户帐务一览页面
	 */

	@RequiresPermissions("doorOrder:v01:guestAccounts:view")
	@RequestMapping(value = { "merchantlist" })
	public String getMerchantlist(@RequestParam(value = "type", required = false) String type,
			DoorCenterAccountsMain doorCenterAccountsMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 默认现金业务
		if (StringUtils.isBlank(type)) {
			type = ClearConstant.AccountCashProType.CASH;
		}
		User user = UserUtils.getUser();
		// 商户用户
		if (user.getOffice().getType().equals(Constant.OfficeType.MERCHANT)) {
			doorCenterAccountsMain.setMerchantOfficeId(UserUtils.getUser().getOffice().getId());
		} else {
			doorCenterAccountsMain.setRofficeId(UserUtils.getUser().getOffice().getId());
			doorCenterAccountsMain.getSqlMap().put("dsf",
					"OR q.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		}
		Page<DoorCenterAccountsMain> page = new Page<DoorCenterAccountsMain>(request, response);
		page = centerAccountsMainService.findPageByMerchant(page, doorCenterAccountsMain, type);
		model.addAttribute("type", type);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/guestAccounts/merchantAccountsList";
	}

	/**
	 * 导出页面信息
	 * 
	 * @author XL
	 * @version 2017年9月13日
	 * @param type
	 * @param centerAccountsMain
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "exportGuestAccounts")
	public void exportGuestAccounts(@RequestParam(value = "type", required = false) String type,
			DoorCenterAccountsMain doorCenterAccountsMain, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) throws FileNotFoundException, ParseException, IOException {
		// 客户账务列表
		List<DoorCenterAccountsMain> centerAccountsList = Lists.newArrayList();
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();

		// add qph 设置发生机构 2017-11-17 begin
		User userInfo = UserUtils.getUser();
		doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
		doorCenterAccountsMain.getSqlMap().put("dsf",
				"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		// end
		// 客户id是否为空
		if (StringUtils.isNotBlank(doorCenterAccountsMain.getClientId())) {
			// 设置发生机构
			centerAccountsList = centerAccountsMainService.findList(doorCenterAccountsMain, type);
		}
		for (DoorCenterAccountsMain centerAccountsReport : centerAccountsList) {
			// 设置业务类型
			String businesstype = DictUtils.getDictLabel(centerAccountsReport.getBusinessType(),
					DoorOrderConstant.ClDictType.DOORORDER_BUSINESS_TYPE, "");
			centerAccountsReport.setBusinessTypeReport(businesstype);
			// 设置操作类型
			String businessstatus = DictUtils.getDictLabel(centerAccountsReport.getBusinessStatus(),
					ClearConstant.ClDictType.CL_STATUS_TYPE, "");
			centerAccountsReport.setBusinessStatusReport(businessstatus);
			// 设置交易时间
			centerAccountsReport.setCreateDateReport(DateUtils.formatDateTime(centerAccountsReport.getCreateDate()));
			// 设置入库，出库默认值
			if (centerAccountsReport.getInAmount() == null) {
				centerAccountsReport.setInAmount(new BigDecimal(0));
			}
			if (centerAccountsReport.getOutAmount() == null) {
				centerAccountsReport.setOutAmount(new BigDecimal(0));
			}
		}
		// 列表为空
		if (centerAccountsList != null && Collections3.isEmpty(centerAccountsList)) {
			centerAccountsList.add(new DoorCenterAccountsMain());
		}
		// 模板文件名/客户账务.xls
		String fileName = msg.getMessage("clear.report.customerAccountsxls", null, locale);
		// String fileName = "客户账务.xlsx";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题(客户账务)
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("clear.report.customerAccounts", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorCenterAccountsMain.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, centerAccountsList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

	/**
	 * 导出页面信息
	 * 
	 * @author gzd
	 * @version 2019年11月27日
	 * @param type
	 * @param centerAccountsMain
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "guestAccountsExport")
	public void guestAccountsExport(@RequestParam(value = "type", required = false) String type,
			DoorCenterAccountsMain doorCenterAccountsMain, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) throws FileNotFoundException, ParseException, IOException {
		// 客户账务列表
		List<DoorCenterAccountsMain> centerAccountsList = Lists.newArrayList();
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();

		// add qph 设置发生机构 2017-11-17 begin
		User userInfo = UserUtils.getUser();
		doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
		doorCenterAccountsMain.getSqlMap().put("dsf",
				"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		// end
		// 客户id是否为空
		if (StringUtils.isNotBlank(doorCenterAccountsMain.getClientId())) {
			// 设置发生机构
			centerAccountsList = centerAccountsMainService.excelExporterList(doorCenterAccountsMain);
			// 对结果追加处理
			centerAccountsMainService.operaTion(centerAccountsList);
		}
		/*
		 * for (DoorCenterAccountsMain centerAccountsReport :
		 * centerAccountsList) { // 设置业务类型 String businesstype =
		 * DictUtils.getDictLabel(centerAccountsReport.getBusinessType(),
		 * DoorOrderConstant.ClDictType.DOORORDER_BUSINESS_TYPE, "");
		 * centerAccountsReport.setBusinessTypeReport(businesstype); // 设置操作类型
		 * String businessstatus =
		 * DictUtils.getDictLabel(centerAccountsReport.getBusinessStatus(),
		 * ClearConstant.ClDictType.CL_STATUS_TYPE, "");
		 * centerAccountsReport.setBusinessStatusReport(businessstatus); //
		 * 设置交易时间
		 * centerAccountsReport.setCreateDateReport(DateUtils.formatDateTime(
		 * centerAccountsReport.getCreateDate())); // 设置入库，出库默认值 if
		 * (centerAccountsReport.getInAmount() == null) {
		 * centerAccountsReport.setInAmount(new BigDecimal(0)); } if
		 * (centerAccountsReport.getOutAmount() == null) {
		 * centerAccountsReport.setOutAmount(new BigDecimal(0)); } // 设置存款人
		 * String cunKuanRen = centerAccountsReport.getCreateName();
		 * centerAccountsReport.setCunKuanRen(cunKuanRen); // 设置存款人编号 String
		 * cunKuanRenId = centerAccountsReport.getCreateBy().getLoginName();
		 * centerAccountsReport.setCunKuanRenId(cunKuanRenId); }
		 */
		// 列表为空
		if (centerAccountsList != null && Collections3.isEmpty(centerAccountsList)) {
			centerAccountsList.add(new DoorCenterAccountsMain());
		}
		// 模板文件名/门店账务.xls
		String fileName = msg.getMessage("report.doorAccountsExport.excel", null, locale) + ".xls";
		// String fileName = "客户账务.xlsx";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题(客户账务)
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.doorAccountsExport.excel", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorCenterAccountsMain.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, centerAccountsList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"门店账务" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}

}