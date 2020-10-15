package com.coffer.businesses.modules.clear.v03.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Iterator;
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

import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsDetail;
import com.coffer.businesses.modules.clear.v03.entity.CenterAccountsMain;
import com.coffer.businesses.modules.clear.v03.service.CenterAccountsMainService;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 客户券别Controller
 * 
 * @author wzj
 * @version 2017年9月11日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/businessAccount")
public class BusinessAccountController extends BaseController {

	@Autowired
	private CenterAccountsMainService centerAccountsMainService;

	/**
	 * 根据客户业务账主键，取得一条客户业务账信息
	 * 
	 * @author wzj
	 * @version 2017年9月14日
	 * @param id
	 * @return CenterAccountsMain
	 */
	@ModelAttribute
	public CenterAccountsMain get(@RequestParam(required = false) String id) {
		CenterAccountsMain entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = centerAccountsMainService.get(id);
		}
		if (entity == null) {
			entity = new CenterAccountsMain();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author wzj
	 * @version 2017年9月14日
	 * @param centerAccountsMain
	 * @return String
	 */
	@RequiresPermissions("clear:v03:businessAccount:view")
	@RequestMapping(value = { "list", "" })
	public String businessAccountList(CenterAccountsMain centerAccountsMain, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 机构
		// CenterAccountsMain accountsMain = new CenterAccountsMain();
		if (StringUtils.isNotBlank(centerAccountsMain.getClientId())) {
			centerAccountsMain = centerAccountsMainService.businessAccount(centerAccountsMain);
			model.addAttribute("totalMoney", centerAccountsMain.getTotalAmount());
		}

		model.addAttribute("centerAccountsMain", centerAccountsMain);
		return "modules/clear/v03/businessAccount/businessAccountList";
	}

	/**
	 * 导出页面信息
	 * 
	 * @author wzj
	 * @version 2017年9月14日
	 * @param centerAccountsMain
	 * @return
	 */
	@RequiresPermissions("clear:v03:businessAccount:view")
	@RequestMapping(value = "exportBusinessAccounts")
	public void exportCenterAccounts(CenterAccountsMain centerAccountsMain, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes)
			throws FileNotFoundException, ParseException, IOException {
		// 查询信息
		CenterAccountsMain centerAccounts = new CenterAccountsMain();
		centerAccounts = centerAccountsMainService.businessAccount(centerAccountsMain);
		List<CenterAccountsDetail> dateList = centerAccounts.getCenterAccountsDetailList();
		List<CenterAccountsDetail> list = Lists.newArrayList();
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 编号
		int i = 1;
		// 获得所有面额
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		Map<String, String> map = Maps.newHashMap();
		for (StoDict stoDict : stoDictList) {
			map.put(stoDict.getValue(), stoDict.getLabel());
		}

		for (CenterAccountsDetail lists : dateList) {
			CenterAccountsDetail date = new CenterAccountsDetail();
			// 人民币填充
			if (StringUtils.isNotBlank(lists.getCurrency())) {
				// if (lists.getCurrency().equals("101")) {
				// 人民币
				date.setCurrency(msg.getMessage("clear.report.renminbi", null, locale));
				// }
			}
			// 面值填充
			Iterator<String> keyIterator = map.keySet().iterator();
			String strKey = "";
			while (keyIterator.hasNext()) {
				strKey = keyIterator.next();
				if (StringUtils.isNotBlank(strKey)) {
					if (lists.getDenomination().equals(strKey)) {
						date.setDenomination(map.get(strKey).toString());
						continue;
					}
				}

			}
			// 金额填充
			date.setTotalAmount(lists.getTotalAmount());
			date.setTotal(lists.getTotal());
			// 编号填充
			date.setNo(String.valueOf(i));
			i++;
			list.add(date);
		}
		String officeName = "";
		if (StringUtils.isNotBlank(centerAccountsMain.getClientId())) {
			// 查询条件中有名称
			Office nameOffice = SysCommonUtils.findOfficeById(centerAccountsMain.getClientId());
			officeName = nameOffice.getName();
		}
		// 模板文件名03版本和07版本
		// 客户业务账.xls
		String fileName = msg.getMessage("clear.report.customerServiceAccountxls", null, locale);
		// String fileName = "客户业务账.xlsx";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// 添加总金额
		Map<String, Object> sheet1TitleMap = Maps.newHashMap();
		// 设置sheet页的对应静态数据
		/* 将总金额进行格式化 修改人:sg 修改日期:2017-12-07 begin */
		DecimalFormat d1 = new DecimalFormat("#,##0.00#");
		if (centerAccounts.getTotalAmount() != null) {
			sheet1TitleMap.put("total", d1.format(centerAccounts.getTotalAmount()));
			/* end */
		}
		if (StringUtils.isNotBlank(officeName)) {
			sheet1TitleMap.put("title", officeName);
		} else {
			// 银行名称未定义
			sheet1TitleMap.put("title", msg.getMessage("clear.report.bankUndefined", null, locale));
		}
		sheetMap.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
		// sheet标题(客户业务账)
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("clear.report.customerServiceAccount", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, CenterAccountsDetail.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

}