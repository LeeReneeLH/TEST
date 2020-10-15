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
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.coffer.businesses.modules.doorOrder.v01.entity.NowUsingDetailInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.NowUsingInfo;
import com.coffer.businesses.modules.doorOrder.v01.service.NowUsingDetailInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.NowUsingInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.web.BaseController;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 机具现在使用记录Controller
 *
 * @author guojian
 * @version 2019-10-30
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/NowUsingInfo")
public class NowUsingInfoController extends BaseController {

	@Autowired
	private NowUsingInfoService nowUsingInfoService;

	@Autowired
	private NowUsingDetailInfoService nowUsingDetailInfoService;

	@RequiresPermissions("doorOrder:nowUsingInfo:view")
	@RequestMapping(value = { "list", "" })
	public String findNowUsingInfoList(Model model, @ModelAttribute("nowUsingInfo") NowUsingInfo nowUsingInfo,
			HttpServletRequest request, HttpServletResponse response) {
		Page<NowUsingInfo> page = nowUsingInfoService.findPage(new Page<NowUsingInfo>(request, response), nowUsingInfo);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/nowUsingInfo/nowUsingInfoList";
	}

	@RequestMapping(value = "detail")
	public String getInfoDetail(Model model,
			@ModelAttribute("nowUsingDetailInfo") NowUsingDetailInfo nowUsingDetailInfo, HttpServletRequest request,
			HttpServletResponse response) {
		NowUsingDetailInfo total = nowUsingDetailInfoService.getTotal(nowUsingDetailInfo);
//		Page<NowUsingDetailInfo> page = nowUsingDetailInfoService
//				.findPage(new Page<NowUsingDetailInfo>(request, response), nowUsingDetailInfo);
//		model.addAttribute("page", page);
		List<NowUsingDetailInfo> detailList = nowUsingDetailInfoService.findList(nowUsingDetailInfo);
		model.addAttribute("detailList",detailList);
		model.addAttribute("total", total);
		return "modules/doorOrder/v01/nowUsingInfo/nowUsingInfoDetail";
	}

	@RequestMapping(value = "exportNowUsingInfo")
	public void exportNowUsingInfo(NowUsingInfo nowUsingInfo, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException, ParseException, IOException {
		// 中心账务列表
		List<NowUsingInfo> nowUsingInfoList = nowUsingInfoService.findList(nowUsingInfo);
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 列表为空
		if (CollectionUtils.isEmpty(nowUsingInfoList)) {
			nowUsingInfoList.add(new NowUsingInfo());
		}
		// 模板文件名 /现在使用情况.xls
		String fileName = msg.getMessage("now.excel.fileName", null, locale);
		// 文件名分割后追加时间
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String[] split = fileName.split("\\.");
		String truefileName = split[0] + date + '.' + split[1];
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("now.excel.title", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, NowUsingInfo.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, nowUsingInfoList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName, truefileName);
	}

	@RequestMapping(value = "exportNowUsingDetailInfo")
	public void exportNowUsingDetailInfo(NowUsingDetailInfo nowUsingDetailInfo, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException, ParseException, IOException {
		NowUsingDetailInfo total = nowUsingDetailInfoService.getTotal(nowUsingDetailInfo);
		// 中心账务列表
		List<NowUsingDetailInfo> detailList = nowUsingDetailInfoService.findList(nowUsingDetailInfo);
		for(NowUsingDetailInfo info : detailList) {
			Date moenyDate = info.getMoenyDate();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
			String exlMoneyDate = ft.format(moenyDate);
			info.setExlMoneyDate(exlMoneyDate);
		}
		detailList.add(total);
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 列表为空
		if (CollectionUtils.isEmpty(detailList)) {
			detailList.add(new NowUsingDetailInfo());
		}
		// 模板文件名 /现在使用情况.xls
		String fileName = msg.getMessage("now.excel.detail.fileName", null, locale);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String[] split = fileName.split("\\.");
		String truefileName = split[0] + date + '.' + split[1];
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("now.excel.detail.title", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, NowUsingDetailInfo.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, detailList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName, truefileName);
	}
}
