  package com.coffer.businesses.modules.doorOrder.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorErrorInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorErrorInfo;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorErrorInfoService;
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
 * 差错管理Controller
 * 
 * @author XL
 * @version 2019-06-28
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/doorErrorInfo")
public class DoorErrorInfoController extends BaseController {

	@Autowired
	private DoorErrorInfoService doorErrorInfoService;
	@Autowired
	private DoorErrorInfoDao doorErrorInfoDao;

	@ModelAttribute
	public DoorErrorInfo get(@RequestParam(required = false) String id) {
		DoorErrorInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = doorErrorInfoService.get(id);
		}
		if (entity == null) {
			entity = new DoorErrorInfo();
		}
		return entity;
	}

	@RequestMapping(value = { "list", "" })
	public String list(DoorErrorInfo doorErrorInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		return "modules/doorOrder/v01/doorErrorInfo/doorErrorFirst";
	}

	/**
	 * 
	 * 上门收款差错管理列表
	 * 
	 * @author ZXk
	 * @version 2020年3月5日
	 * @param doorErrorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("doororder:doorErrorInfo:view")
	@RequestMapping(value = "merchantList")
	public String merchantList(DoorErrorInfo doorErrorInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		/*
		 * if(Constant.OfficeType.STORE.equals(UserUtils.getUser().getOffice().
		 * getType())){ Page<DoorErrorInfo> page =
		 * doorErrorInfoService.findDoorPage(new Page<DoorErrorInfo>(request,
		 * response), doorErrorInfo); model.addAttribute("page", page); return
		 * "modules/doorOrder/v01/doorErrorInfo/doorErrorInfoDoorList"; }
		 */
		Page<DoorErrorInfo> page = doorErrorInfoService.findMerchantPage(new Page<DoorErrorInfo>(request, response),
				doorErrorInfo);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/doorErrorInfo/doorErrorInfoList";
	}

	/**
	 * 
	 * 商户差错列表导出
	 * 
	 * @author ZXK
	 * @version 2020年3月8日
	 * @param doorErrorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "exportMerchantList")
	public String exportDetail(DoorErrorInfo doorErrorInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		try {
			// 商户差错列表
			List<DoorErrorInfo> doorErrorInfoList = doorErrorInfoService.findMerchantList(doorErrorInfo);
			// 添加合计行数据
			doorErrorInfoList.addAll(doorErrorInfoService.getTotalAmount(doorErrorInfo));
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(doorErrorInfoList)) {
				doorErrorInfoList.add(new DoorErrorInfo());
			}
			// 重新录入差错类型、差错状态
			for (DoorErrorInfo dErrorInfo : doorErrorInfoList) {
				// 差错类型
				if (DoorOrderConstant.ErrorType.LONG_CURRENCY.equals(dErrorInfo.getErrorType())) {
					dErrorInfo.setErrorType(DoorOrderConstant.ErrorTypeName.LONG_CURRENCY);
				} else if (DoorOrderConstant.ErrorType.SHORT_CURRENCY.equals(dErrorInfo.getErrorType())) {
					dErrorInfo.setErrorType(DoorOrderConstant.ErrorTypeName.SHORT_CURRENCY);
				} else {
					dErrorInfo.setErrorType("");
				}
			}
			// 模板文件名
			String fileName = msg.getMessage("report.doorError.merchantList", null, locale) + ".xls";
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("report.doorError.merchantList", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorErrorInfo.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, doorErrorInfoList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("report.doorError.merchantList", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return list(doorErrorInfo, request, response, model);
		}
		return null;
	}

	/**
	 * 
	 * 门店差错列表
	 * 
	 * @author gzd
	 * @version 2020年3月5日
	 * @param doorErrorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("doororder:doorErrorList:view")
	@RequestMapping(value = "doorList")
	public String doorList(DoorErrorInfo doorErrorInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<DoorErrorInfo> page = doorErrorInfoService.findDoorPage(new Page<DoorErrorInfo>(request, response),
				doorErrorInfo);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/doorErrorInfo/doorErrorInfoDoorList";
	}

	/**
	 * 
	 * 门店差错明细
	 * 
	 * @author gzd
	 * @version 2020年3月5日
	 * @param doorErrorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("doororder:doorErrorDetailList:view")
	@RequestMapping(value = "doorDetailList")
	public String doorDetailList(DoorErrorInfo doorErrorInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		/** 初始化查询登记信息*/
		if (StringUtils.isBlank(doorErrorInfo.getStatus()) && StringUtils.isBlank(doorErrorInfo.getCustNo())) {
			doorErrorInfo.setStatus(DoorOrderConstant.Status.REGISTER);
		}
		if ("00".equals(doorErrorInfo.getStatus())) {
			doorErrorInfo.setStatus("");
		}
		Page<DoorErrorInfo> page = doorErrorInfoService.findDoorDetailPage(new Page<DoorErrorInfo>(request, response),
				doorErrorInfo);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/doorErrorInfo/doorErrorInfoDoorDetailList";
	}

	/**
	 * 
	 * 门店差错报表
	 * 
	 * @author gzd
	 * @version 2020年3月9日
	 * @param doorErrorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	// @RequiresPermissions("doorOrder:DoorErrorInfo:export")
	@RequestMapping(value = "doorExport")
	public String doorExport(DoorErrorInfo doorErrorInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		try {
			// 门店差错列表
			List<DoorErrorInfo> doorErrorInfoList = doorErrorInfoService.findDoorList(doorErrorInfo);
			List<DoorErrorInfo> poolList = doorErrorInfoService.getDoorPool(doorErrorInfoList);
			doorErrorInfoList.addAll(poolList);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(doorErrorInfoList)) {
				doorErrorInfoList.add(new DoorErrorInfo());
			}
			// 重新录入差错类型、差错状态
			for (DoorErrorInfo dErrorInfo : doorErrorInfoList) {
				// 差错类型
				if (DoorOrderConstant.ErrorType.LONG_CURRENCY.equals(dErrorInfo.getErrorType())) {
					dErrorInfo.setErrorType(DoorOrderConstant.ErrorTypeName.LONG_CURRENCY);
				} else if (DoorOrderConstant.ErrorType.SHORT_CURRENCY.equals(dErrorInfo.getErrorType())) {
					dErrorInfo.setErrorType(DoorOrderConstant.ErrorTypeName.SHORT_CURRENCY);
				} else {
					dErrorInfo.setErrorType("");
				}
				// 差错状态
				if (DoorOrderConstant.ErrorStatus.CREATE.equals(dErrorInfo.getStatus())) {
					dErrorInfo.setStatus(DoorOrderConstant.ErrorStatusName.CREATE);
				} else if (DoorOrderConstant.ErrorStatus.DELETE.equals(dErrorInfo.getStatus())) {
					dErrorInfo.setStatus(DoorOrderConstant.ErrorStatusName.DELETE);
				} else if (DoorOrderConstant.ErrorStatus.CLEAR_ACCOUNT.equals(dErrorInfo.getStatus())) {
					dErrorInfo.setStatus(DoorOrderConstant.ErrorStatusName.CLEAR_ACCOUNT);
				} else {
					dErrorInfo.setStatus("");
				}
			}
			// 模板文件名 /门店差错报表.xls
			String fileName = msg.getMessage("door.errorInfo.doorExport", null, locale) + ".xls";
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("door.errorInfo.doorExport", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorErrorInfo.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, doorErrorInfoList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.errorInfo.doorExport", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return list(doorErrorInfo, request, response, model);
		}
		return null;
	}

	/**
	 * 
	 * 门店差错明细报表
	 * 
	 * @author gzd
	 * @version 2020年3月9日
	 * @param doorErrorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	// @RequiresPermissions("doorOrder:DoorErrorInfo:export")
	@RequestMapping(value = "doorDetailExport")
	public String doorDetailExport(DoorErrorInfo doorErrorInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		try {
			/** 初始化查询登记信息*/
			if (StringUtils.isBlank(doorErrorInfo.getStatus()) && StringUtils.isBlank(doorErrorInfo.getCustNo())) {
				doorErrorInfo.setStatus(DoorOrderConstant.Status.REGISTER);
			}
			if ("00".equals(doorErrorInfo.getStatus())) {
				doorErrorInfo.setStatus("");
			}
			// 门店差错明细列表
			List<DoorErrorInfo> doorErrorInfoList = doorErrorInfoService.findDoorDetailList(doorErrorInfo);
			List<DoorErrorInfo> poolList = doorErrorInfoService.getDoorPool(doorErrorInfoList);
			for (DoorErrorInfo doorErrorInfoPool : poolList) {
				doorErrorInfoPool.setBusinessId(doorErrorInfoPool.getCustName());
				doorErrorInfoPool.setCustName("");
			}
			doorErrorInfoList.addAll(poolList);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(doorErrorInfoList)) {
				doorErrorInfoList.add(new DoorErrorInfo());
			}
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 重新录入差错类型、差错状态
			for (DoorErrorInfo dErrorInfo : doorErrorInfoList) {
				// 差错类型
				if (DoorOrderConstant.ErrorType.LONG_CURRENCY.equals(dErrorInfo.getErrorType())) {
					dErrorInfo.setErrorType(DoorOrderConstant.ErrorTypeName.LONG_CURRENCY);
				} else if (DoorOrderConstant.ErrorType.SHORT_CURRENCY.equals(dErrorInfo.getErrorType())) {
					dErrorInfo.setErrorType(DoorOrderConstant.ErrorTypeName.SHORT_CURRENCY);
				} else {
					dErrorInfo.setErrorType("");
				}
				// 差错状态
				if (DoorOrderConstant.ErrorStatus.CREATE.equals(dErrorInfo.getStatus())) {
					dErrorInfo.setStatus(DoorOrderConstant.ErrorStatusName.CREATE);
				} else if (DoorOrderConstant.ErrorStatus.DELETE.equals(dErrorInfo.getStatus())) {
					dErrorInfo.setStatus(DoorOrderConstant.ErrorStatusName.DELETE);
				} else if (DoorOrderConstant.ErrorStatus.CLEAR_ACCOUNT.equals(dErrorInfo.getStatus())) {
					dErrorInfo.setStatus(DoorOrderConstant.ErrorStatusName.CLEAR_ACCOUNT);
				} else {
					dErrorInfo.setStatus("");
				}
				dErrorInfo.setCreateTime(dErrorInfo.getCreateDate());
				// 格式化钞袋使用时间
				if (null != dErrorInfo.getLastTime() && null != dErrorInfo.getThisTime()) {
					dErrorInfo.setBagNoUseTime(
							DateUtils.formatDate(dErrorInfo.getLastTime(), "yyyy-MM-dd HH:mm")+"~"+DateUtils.formatDate(dErrorInfo.getThisTime(), "yyyy-MM-dd HH:mm"));
				}
			}
			// 模板文件名 / 门店差错明细报表.xls
			String fileName = msg.getMessage("door.errorInfo.doorDetailExport", null, locale) + ".xls";
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.errorInfo.doorDetailExport", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorErrorInfo.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, doorErrorInfoList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.errorInfo.doorDetailExport", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return list(doorErrorInfo, request, response, model);
		}
		return null;
	}

	/**
	 * 差错情况明细查看
	 * 
	 * @author ZXK
	 * @version 2020-05-08
	 * @param
	 * @return
	 */
	@RequestMapping(value = "errorDetail")
	public String errorDetail(DoorErrorInfo doorErrorInfo, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		Page<DoorErrorInfo> page = doorErrorInfoService.findPage(new Page<DoorErrorInfo>(request, response),
				doorErrorInfo);
		if (!StringUtils.isBlank(doorErrorInfo.getSearchDateStart())) {
			doorErrorInfo.setCreateTimeStart(DateUtils.parseDate(doorErrorInfo.getSearchDateStart()));
		}
		if (!StringUtils.isBlank(doorErrorInfo.getSearchDateEnd())) {
			doorErrorInfo.setCreateTimeEnd(DateUtils.parseDate(doorErrorInfo.getSearchDateEnd()));
		}
		model.addAttribute("page", page);
		model.addAttribute("doorErrorInfo", doorErrorInfo);
		return "modules/report/v01/manageReport/errorSituationDetailList";
	}

	/**
	 * 差错情况明细导出
	 * 
	 * @author ZXK
	 * @version 2020年5月11日
	 */
	@RequestMapping(value = "exportExcel")
	public void exportExcel(DoorErrorInfo doorErrorInfo, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException, ParseException, IOException {
		// 数据过滤
		User userInfo = UserUtils.getUser();
		if (userInfo.getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 清分中心过滤
			doorErrorInfo.setOffice(userInfo.getOffice());
		} else if (StringUtils.isBlank(doorErrorInfo.getCustNo())) {
			// 非清分中心过滤
			doorErrorInfo.setCustNo(userInfo.getOffice().getId());
		}
		// 查询条件： 开始时间
		if (doorErrorInfo.getCreateTimeStart() != null) {
			doorErrorInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(doorErrorInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (doorErrorInfo.getCreateTimeEnd() != null) {
			doorErrorInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorErrorInfo.getCreateTimeEnd())));
		}
		List<DoorErrorInfo> errorList = doorErrorInfoDao.findList(doorErrorInfo);
		// 列表为空
		if (Collections3.isEmpty(errorList)) {
			errorList.add(new DoorErrorInfo());
		} else {
			for (DoorErrorInfo doorError : errorList) {
				// 差错类型 转换
				if (DoorOrderConstant.ErrorType.LONG_CURRENCY.equals(doorError.getErrorType())) {
					doorError.setErrorType(DoorOrderConstant.ErrorExport.LONG);
				} else if (DoorOrderConstant.ErrorType.SHORT_CURRENCY.equals(doorError.getErrorType())) {
					doorError.setErrorType(DoorOrderConstant.ErrorExport.SHORT);
				} else if (DoorOrderConstant.ErrorType.COUNTERFEIT_CURRENCY.equals(doorError.getErrorType())) {
					doorError.setErrorType(DoorOrderConstant.ErrorExport.COUNTERFEIT);
				}
				// 状态 转换
				if (DoorOrderConstant.Status.REGISTER.equals(doorError.getStatus())) {
					doorError.setStatus(DoorOrderConstant.ErrorExport.REGISTER);
				} else if (DoorOrderConstant.Status.REVERSE.equals(doorError.getStatus())) {
					doorError.setStatus(DoorOrderConstant.ErrorExport.REVERSE);
				} 
			}
		}
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 模板文件名 /差错情况.xls
		String fileName = msg.getMessage("report.manageReport.errorDetail", null, locale) + ".xls";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("report.manageReport.errorDetail", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorErrorInfo.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, errorList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"差错明细" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}
}