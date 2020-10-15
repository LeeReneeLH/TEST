package com.coffer.businesses.modules.doorOrder.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.doorOrder.v01.entity.BusinessTransactionStatement;
import com.coffer.businesses.modules.doorOrder.v01.service.BusinessTransactionStatementService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import freemarker.core.ParseException;

/**
 * 交易报表Controller
 *
 * @author yinkai
 * @version 2020-01-09
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/businessTransactionStatement")
public class BusinessTransactionStatementController extends BaseController {

	@Autowired
	private BusinessTransactionStatementService businessTransactionStatementService;

	@Autowired
	private DoorOrderInfoService doorOrderInfoService;

	@ModelAttribute
	public BusinessTransactionStatement get(@RequestParam(required = false) String id) {
		BusinessTransactionStatement entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = businessTransactionStatementService.get(id);
		}
		if (entity == null) {
			entity = new BusinessTransactionStatement();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:businessTransactionStatement:view")
	@RequestMapping(value = { "list", "" })
	public String list(BusinessTransactionStatement businessTransactionStatement, HttpServletRequest request,
			HttpServletResponse response, Model model, String doorId) {
		// 机构树doorId赋值 gzd 2020-04-08
		businessTransactionStatement.setDoor(new Office(doorId));
		Page<BusinessTransactionStatement> page = businessTransactionStatementService
				.findPage(new Page<BusinessTransactionStatement>(request, response), businessTransactionStatement);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/businessTransasctionStatement/businessTransactionStatementList";
	}

	@RequiresPermissions("doorOrder:v01:businessTransactionStatement:view")
	@RequestMapping(value = "form")
	public String form(BusinessTransactionStatement businessTransactionStatement, Model model) {
		model.addAttribute("businessTransactionStatement", businessTransactionStatement);
		return "modules/doorOrder/v01/businessTransasctionStatement/businessTransactionStatementForm";
	}

	@RequiresPermissions("doorOrder:v01:businessTransactionStatement:edit")
	@RequestMapping(value = "save")
	public String save(BusinessTransactionStatement businessTransactionStatement, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, businessTransactionStatement)) {
			return form(businessTransactionStatement, model);
		}
		User user = UserUtils.getUser();
		businessTransactionStatement.setCreateBy(user);
		businessTransactionStatement.setCreateName(user.getName());
		businessTransactionStatement.setCreateDate(new Date());
		businessTransactionStatement.setUpdateBy(user);
		businessTransactionStatement.setUpdateName(user.getName());
		businessTransactionStatement.setUpdateDate(new Date());
		businessTransactionStatementService.save(businessTransactionStatement);
		addMessage(redirectAttributes, "保存交易报表成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/businessTransactionStatement/?repage";
	}

	@RequiresPermissions("doorOrder:v01:businessTransactionStatement:edit")
	@RequestMapping(value = "delete")
	public String delete(BusinessTransactionStatement businessTransactionStatement,
			RedirectAttributes redirectAttributes) {
		businessTransactionStatementService.delete(businessTransactionStatement);
		addMessage(redirectAttributes, "删除交易报表成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/businessTransactionStatement/?repage";
	}

	@RequestMapping(value = "transactionDetail")
	@ResponseBody
	public BusinessTransactionStatement getTransactionDetail(String inBatch) {
		return businessTransactionStatementService.getTransactionDetail(inBatch);
	}

	@RequestMapping(value = "checkInBatch")
	@ResponseBody
	public String validateBusinessBatch(@RequestParam(value = "inBatch") String inBatch,
			@RequestParam(value = "id") String id) {
		if (null != id && !"".equals(id)) {
			return "true";
		}
		// 查询是否存在这笔交易
		DoorOrderDetail detailByTickerTape = doorOrderInfoService.getDetailByTickerTape(inBatch);
		// 查询这笔交易是否已经在报表中
		BusinessTransactionStatement statement = new BusinessTransactionStatement();
		statement.setInBatch(inBatch);
		List<BusinessTransactionStatement> txResultList = businessTransactionStatementService.findList(statement);
		// 交易存在且报表中不存在，保存到报表中
		// && Collections3.isEmpty(txResultList)
		if (detailByTickerTape != null && Collections3.isEmpty(txResultList)) {
			return "true";
		}
		return "false";
	}

	@RequestMapping(value = "transactionDetailList")
	public String transactionDetailList(HttpServletRequest request, HttpServletResponse response,
			BusinessTransactionStatement businessTransactionStatement, Model model) {
		String doorId = request.getParameter("doorId");
		if (StringUtils.isNotEmpty(doorId)) {
			model.addAttribute("doorId", doorId);
			businessTransactionStatement.setDoor(new Office(doorId));
		}
		Page<BusinessTransactionStatement> page = businessTransactionStatementService.getTransactionDetailPage(
				new Page<BusinessTransactionStatement>(request, response), businessTransactionStatement);
		model.addAttribute("page", page);
		return "/modules/doorOrder/v01/clearAddMoney/transactionDetailList";
	}

	/**
	 * Excel导出
	 *
	 * @author gzd
	 * @version 2020-05-08
	 * @param businessTransactionStatement
	 * @return
	 */
	@RequestMapping(value = "exportExcel")
	public String exportExcel(BusinessTransactionStatement businessTransactionStatement, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes, Model model, String doorId)
			throws FileNotFoundException, ParseException, IOException {
		try {
			businessTransactionStatement.setDoor(new Office(doorId));
			Locale locale = LocaleContextHolder.getLocale();
			// 设置标题信息
			Map<String, Object> titleMap = Maps.newHashMap();
			// 制表机构
			titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
			// 制表时间
			titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
			// 查询
			List<BusinessTransactionStatement> list = businessTransactionStatementService
					.findList(businessTransactionStatement);
			// 列表为空
			if (Collections3.isEmpty(list)) {
				list.add(new BusinessTransactionStatement());
			} else {
				for (BusinessTransactionStatement bTS : list) {
					// 字段重新赋值
					if (bTS.getEquipmentInfo() != null) {
						bTS.setSeriesNumber(bTS.getEquipmentInfo().getSeriesNumber());
					}
					bTS.setDoorName(bTS.getDoor().getName());
					bTS.setLoginName(bTS.getUser().getLoginName());
					bTS.setUserName(bTS.getUser().getName());
					bTS.setCashAmountBD(
							bTS.getCashAmount() != null ? new BigDecimal(bTS.getCashAmount()) : new BigDecimal(0));
					bTS.setPackAmountBD(
							bTS.getPackAmount() != null ? new BigDecimal(bTS.getPackAmount()) : new BigDecimal(0));
					bTS.setTotalAmountBD(new BigDecimal(bTS.getTotalAmount()));
					bTS.setRealClearAmountBD(new BigDecimal(bTS.getRealClearAmount()));
					bTS.setLongCurrencyMoneyBD(new BigDecimal(bTS.getLongCurrencyMoney()));
					bTS.setShortCurrencyMoneyBD(new BigDecimal(bTS.getShortCurrencyMoney()));
					if ("".equals(bTS.getCustConfirm()) || bTS.getCustConfirm() == null) {
						bTS.setCustConfirm("未发生长短款");
					} else if ("2".equals(bTS.getCustConfirm())) {
						bTS.setCustConfirm("无长短款");
					} else if ("1".equals(bTS.getCustConfirm())) {
						bTS.setCustConfirm("已确认");
					} else if ("0".equals(bTS.getCustConfirm())) {
						bTS.setCustConfirm("未确认");
					}
					bTS.setStartTimeEX(DateUtils.formatDate(bTS.getStartTime(), "HH:mm:ss"));
					bTS.setEndTimeEX(DateUtils.formatDate(bTS.getEndTime(), "HH:mm:ss"));
					bTS.setInDateEX(DateUtils.formatDate(bTS.getInDate(), "yyyy-MM-dd"));
					bTS.setBackDateEX(DateUtils.formatDate(bTS.getBackDate(), "yyyy-MM-dd"));
					bTS.setClearDateEX(DateUtils.formatDate(bTS.getClearDate(), "yyyy-MM-dd"));
				}
			}
			// 模板文件名
			String fileName = msg.getMessage("report.businessTransactionStatement.excel", null, locale) + ".xls";

			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("report.businessTransactionStatement.excel", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY,
					BusinessTransactionStatement.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);

			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					"交易报表" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return list(businessTransactionStatement, request, response, model, doorId);
		}
		return null;
	}

	@RequestMapping("/confirm")
	public String confirm(@RequestParam String businessIds, BusinessTransactionStatement businessTransactionStatement,
			Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, businessTransactionStatement)) {
			return form(businessTransactionStatement, model);
		}
		//String[] businessIdsArr = businessIds.split(",");
		User user = UserUtils.getUser();
		businessTransactionStatement.setUpdateBy(user);
		businessTransactionStatement.setUpdateName(user.getName());
		businessTransactionStatement.setUpdateDate(new Date());
		businessTransactionStatementService.confirm(businessTransactionStatement);
		addMessage(redirectAttributes, "保存交易报表成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/businessTransactionStatement/?repage";
	}
}