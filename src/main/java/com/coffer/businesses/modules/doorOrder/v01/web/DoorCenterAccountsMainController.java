package com.coffer.businesses.modules.doorOrder.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderAmountService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
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
@Controller("centerAccounts")
@RequestMapping(value = "${adminPath}/doorOrder/v01/centerAccounts")
public class DoorCenterAccountsMainController extends BaseController {

	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;

	@Autowired
	private DoorOrderAmountService doorOrderAmountService;

	/**
	 * 根据中心账务主键，取得中心账务信息
	 * 
	 * @author XL
	 * @version 2017年9月11日
	 * @param accountsId
	 * @return 中心账务信息
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
	@RequiresPermissions("doorOrder:v01:centerAccounts:view")
	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam(value = "type", required = false) String type,
			DoorCenterAccountsMain doorCenterAccountsMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		/* end */
		// 默认现金业务
		if (StringUtils.isBlank(type)) {
			type = ClearConstant.AccountCashProType.CASH;
		}
		/* 判断是否选择了清分机构 修改人:sg 修改日期:2017-12-08 begin */
		boolean check = false;
		/* end */
		Office office = SysCommonUtils.findOfficeById(doorCenterAccountsMain.getRofficeId());
		if (StringUtils.isBlank(doorCenterAccountsMain.getRofficeId())
				|| (office.getType()).equals(Constant.OfficeType.DIGITAL_PLATFORM)) {
			// 设置发生机构
			User userInfo = UserUtils.getUser();
			doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
			/* 增加商业银行回显 wzj 2017-11-24 begin */
			doorCenterAccountsMain.setRofficeName(userInfo.getOffice().getName());
			/* end */
			doorCenterAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
			/* 没有选择了清分机构 修改人:sg 修改日期:2017-12-08 begin */
			check = true;
			/* end */
		} else {
			/* 增加商业银行回显 wzj 2017-11-24 begin */
			doorCenterAccountsMain.setRofficeId(office.getId());
			doorCenterAccountsMain.setRofficeName(office.getName());
			/* end */
			doorCenterAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + doorCenterAccountsMain.getRofficeId() + "%'");
		}
		/* 追加中心总账统计金额 add by lihe */
		List<DoorCenterAccountsMain> tabList = centerAccountsMainService.getAmountByBusinessType(doorCenterAccountsMain,
				type);
		if (Collections3.isEmpty(tabList)) {
			tabList = Lists.newArrayList();
		}
		/* end */

		Page<DoorCenterAccountsMain> page = centerAccountsMainService
				.findPage(new Page<DoorCenterAccountsMain>(request, response), doorCenterAccountsMain, type);
		/* 没有选择了清分机构页面不显示 修改人:sg 修改日期:2017-12-08 begin */
		if (check) {
			doorCenterAccountsMain.setRofficeId(null);
			doorCenterAccountsMain.setRofficeName(null);
		}
		/* end */
		// 业务类型
		model.addAttribute("type", type);
		model.addAttribute("page", page);
		model.addAttribute("tabList", tabList);
		return "modules/doorOrder/v01/centerAccounts/centerAccountsList";
	}

	/**
	 * 通过交易id获取交易面值明细
	 * 
	 * @param detailId
	 * @return
	 */
	@RequestMapping(value = "amountList")
	public String getAmountList(@RequestParam(value = "detailId") String detailId, Model model) {
		List<DoorOrderAmount> amountList = doorOrderAmountService.getAmountList(detailId);
		model.addAttribute("amountList", amountList);
		return "modules/doorOrder/v01/centerAccounts/amountDetailList";
	}

	/**
	 * 导出页面信息
	 * 
	 * @author WQJ
	 * @version 2019年10月30日
	 * @param type
	 * @param doorCenterAccountsMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "exportCenterAccounts")
	public String exportCenterAccounts(@RequestParam(value = "type", required = false) String type,
			DoorCenterAccountsMain doorCenterAccountsMain, HttpServletRequest request, HttpServletResponse response,
			Model model) throws FileNotFoundException, ParseException, IOException {
		try {
			// ADD qph 设置账务发生机构 2017-11-17 begin
			User userInfo = UserUtils.getUser();
			doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
			doorCenterAccountsMain.getSqlMap().put("dsf",
					"OR o.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%'");
			// end
			// 中心账务列表
			List<DoorCenterAccountsMain> centerAccountsList = centerAccountsMainService
					.excelExporterList(doorCenterAccountsMain);
			// 对结果追加处理
			centerAccountsMainService.operaTion(centerAccountsList);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 模板文件名 /交易明细.xls
			String fileName = msg.getMessage("door.report.center", null, locale);
			// 文件名分割后追加时间
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String[] split = fileName.split("\\.");
			String truefileName = split[0] + date + '.' + split[1];
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.report.transactionDetail", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorCenterAccountsMain.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, centerAccountsList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName, truefileName);
		} catch (Exception e) {
			return list(type, doorCenterAccountsMain, request, response, model);
		}
		return null;
	}

}