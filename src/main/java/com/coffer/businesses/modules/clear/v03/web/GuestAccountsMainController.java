package com.coffer.businesses.modules.clear.v03.web;

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
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.service.CenterAccountsMainService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
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
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/guestAccounts")
public class GuestAccountsMainController extends BaseController {

	@Autowired
	private CenterAccountsMainService centerAccountsMainService;

	/**
	 * 根据账务主键，取得客户账务信息
	 * 
	 * @author XL
	 * @version 2017年9月11日
	 * @param accountsId
	 * @return 客户账务信息
	 */
	@ModelAttribute
	public CenterAccountsMain get(@RequestParam(required = false) String accountsId) {
		CenterAccountsMain entity = null;
		if (StringUtils.isNotBlank(accountsId)) {
			entity = centerAccountsMainService.get(accountsId);
		}
		if (entity == null) {
			entity = new CenterAccountsMain();
		}
		return entity;
	}

	/**
	 * 获取客户账务列表
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
	@RequiresPermissions("clear:v03:guestAccounts:view")
	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam(value = "type", required = false) String type,
			CenterAccountsMain centerAccountsMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		if (centerAccountsMain.getCreateTimeStart() == null) {
			centerAccountsMain.setCreateTimeStart(new Date());
		}
		if (centerAccountsMain.getCreateTimeEnd() == null) {
			centerAccountsMain.setCreateTimeEnd(new Date());
		}
		/* end */
		// 默认现金业务
		if (StringUtils.isBlank(type)) {
			type = ClearConstant.AccountCashProType.CASH;
		}
		/* 判断是否选择了清分机构 修改人:sg 修改日期:2017-12-08 begin */
		boolean check = false;
		/* end */
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		Office office = SysCommonUtils.findOfficeById(centerAccountsMain.getRofficeId());
		if (StringUtils.isBlank(centerAccountsMain.getRofficeId())
				|| (office.getType()).equals(Constant.OfficeType.DIGITAL_PLATFORM)) {
			// 设置发生机构
			User userInfo = UserUtils.getUser();
			centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
			centerAccountsMain.setRofficeName(userInfo.getOffice().getName());
			centerAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
			/* 没有选择了清分机构 修改人:sg 修改日期:2017-12-08 begin */
			check = true;
			/* end */
		} else {
			centerAccountsMain.setRofficeId(office.getId());
			centerAccountsMain.setRofficeName(office.getName());
			centerAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + centerAccountsMain.getRofficeId() + "%'");
		}
		/* end */
		Page<CenterAccountsMain> page = new Page<CenterAccountsMain>(request, response);
		// 客户id是否为空
		if (StringUtils.isBlank(centerAccountsMain.getClientId())) {
			// 初始化
			centerAccountsMain.setPage(page);
			List<CenterAccountsMain> defaultList = Lists.newArrayList();
			page.setList(defaultList);
		} else {
			page = centerAccountsMainService.findPage(page, centerAccountsMain, type);
		}
		/* 没有选择了清分机构页面不显示 修改人:sg 修改日期:2017-12-08 begin */
		if (check) {
			centerAccountsMain.setRofficeId(null);
			centerAccountsMain.setRofficeName(null);
		}
		/* end */
		model.addAttribute("type", type);
		model.addAttribute("page", page);
		return "modules/clear/v03/guestAccounts/guestAccountsList";
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
			CenterAccountsMain centerAccountsMain, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) throws FileNotFoundException, ParseException, IOException {
		// 客户账务列表
		List<CenterAccountsMain> centerAccountsList = Lists.newArrayList();
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();

		// add qph 设置发生机构 2017-11-17 begin
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		centerAccountsMain.getSqlMap().put("dsf",
				"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		// end
		// 客户id是否为空
		if (StringUtils.isNotBlank(centerAccountsMain.getClientId())) {
			// 设置发生机构
			centerAccountsList = centerAccountsMainService.findList(centerAccountsMain, type);
		}
		for (CenterAccountsMain centerAccountsReport : centerAccountsList) {
			// 设置业务类型
			String businesstype = DictUtils.getDictLabel(centerAccountsReport.getBusinessType(),
					ClearConstant.ClDictType.CLEAR_BUSINESS_TYPE, "");
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
		if (centerAccountsList != null && centerAccountsList.size() == 0) {
			centerAccountsList.add(new CenterAccountsMain());
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
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, CenterAccountsMain.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, centerAccountsList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

}