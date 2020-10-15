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
 * 中心总账Controller
 * 
 * @author XL
 * @version 2017年9月11日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/centerAccounts")
public class CenterAccountsMainController extends BaseController {

	@Autowired
	private CenterAccountsMainService centerAccountsMainService;

	/**
	 * 根据中心账务主键，取得中心账务信息
	 * 
	 * @author XL
	 * @version 2017年9月11日
	 * @param accountsId
	 * @return 中心账务信息
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
	 * 获取中心账务列表
	 * 
	 * @author XL
	 * @version 2017年9月11日
	 * @param type
	 * @param centerAccountsMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 中心账务一览页面
	 */
	@RequiresPermissions("clear:v03:centerAccounts:view")
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
		Office office = SysCommonUtils.findOfficeById(centerAccountsMain.getRofficeId());
		if (StringUtils.isBlank(centerAccountsMain.getRofficeId())
				|| (office.getType()).equals(Constant.OfficeType.DIGITAL_PLATFORM)) {
			// 设置发生机构
			User userInfo = UserUtils.getUser();
			centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
			/* 增加商业银行回显 wzj 2017-11-24 begin */
			centerAccountsMain.setRofficeName(userInfo.getOffice().getName());
			/* end */
			centerAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
			/* 没有选择了清分机构 修改人:sg 修改日期:2017-12-08 begin */
			check = true;
			/* end */
		} else {
			/* 增加商业银行回显 wzj 2017-11-24 begin */
			centerAccountsMain.setRofficeId(office.getId());
			centerAccountsMain.setRofficeName(office.getName());
			/* end */
			centerAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + centerAccountsMain.getRofficeId() + "%'");
		}

		Page<CenterAccountsMain> page = centerAccountsMainService
				.findPage(new Page<CenterAccountsMain>(request, response), centerAccountsMain, type);
		/* 没有选择了清分机构页面不显示 修改人:sg 修改日期:2017-12-08 begin */
		if (check) {
			centerAccountsMain.setRofficeId(null);
			centerAccountsMain.setRofficeName(null);
		}
		/* end */
		// 业务类型
		model.addAttribute("type", type);
		model.addAttribute("page", page);
		return "modules/clear/v03/centerAccounts/centerAccountsList";
	}

	/**
	 * 导出页面信息
	 * 
	 * @author XL
	 * @version 2017年9月11日
	 * @param type
	 * @param centerAccountsMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "exportCenterAccounts")
	public void exportCenterAccounts(@RequestParam(value = "type", required = false) String type,
			CenterAccountsMain centerAccountsMain, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException, ParseException, IOException {
		// ADD qph 设置账务发生机构 2017-11-17 begin
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		centerAccountsMain.getSqlMap().put("dsf",
				"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
		// end
		// 中心账务列表
		List<CenterAccountsMain> centerAccountsList = centerAccountsMainService.findList(centerAccountsMain, type);
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
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
		// 模板文件名 /中心总账.xls
		String fileName = msg.getMessage("clear.report.center", null, locale);
		// String fileName = "中心总账.xlsx";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("clear.report.centerName", null, locale));
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