package com.coffer.businesses.modules.doorOrder.v01.web;

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
import org.springframework.web.bind.annotation.RequestMapping;

import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryUseRecords;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryUseRecordsDetail;
import com.coffer.businesses.modules.doorOrder.v01.service.HistoryUseRecordsService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 机具历史使用记录Controller
 *
 * @author lihe
 * @version 2019-10-30
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/historyUseRecords")
public class HistoryUseRecordsController extends BaseController {

	@Autowired
	private HistoryUseRecordsService historyUseRecordsService;

	@RequiresPermissions("doorOrder:historyUseRecords:view")
	@RequestMapping(value = { "list", "" })
	public String list(HistoryUseRecords historyUseRecords, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<HistoryUseRecords> page = historyUseRecordsService.findPage(new Page<HistoryUseRecords>(request, response),
				historyUseRecords);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/historyUseRecords/historyUseRecordsList";
	}

	@RequiresPermissions("doorOrder:historyUseRecords:detail:view")
	@RequestMapping(value = "detailList")
	public String detailList(HistoryUseRecordsDetail historyUseRecordsDetail, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<HistoryUseRecordsDetail> page = historyUseRecordsService
				.findDetailPage(new Page<HistoryUseRecordsDetail>(request, response), historyUseRecordsDetail);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/historyUseRecords/historyUseRecordsDetailList";
	}

	@RequestMapping(value = "export")
	public String export(HistoryUseRecords historyUseRecords, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		try {
			// 历史使用记录列表
			List<HistoryUseRecords> historyUseRecordsList = historyUseRecordsService.findList(historyUseRecords);
			// 合计行
			HistoryUseRecords useRecords = new HistoryUseRecords();
			// 初始化
			Integer paperCount = 0;
			Integer count = 0;
			BigDecimal paperAmount = new BigDecimal(0);
			BigDecimal coinAmount = new BigDecimal(0);
			BigDecimal forceAmount = new BigDecimal(0);
			BigDecimal otherAmount = new BigDecimal(0);
			BigDecimal amount = new BigDecimal(0);
			// 合计计算
			for (HistoryUseRecords records : historyUseRecordsList) {
				// 纸币数量
				paperCount = paperCount + records.getPaperCount();
				// 存入总量
				count = count + records.getCount();
				// 纸币金额
				paperAmount = paperAmount.add(records.getPaperAmount());
				// 硬币金额
				coinAmount = coinAmount.add(records.getCoinAmount());
				// 强制金额
				forceAmount = forceAmount.add(records.getForceAmount());
				// 其他金额
				otherAmount = otherAmount.add(records.getOtherAmount());
				// 总金额
				amount = amount.add(records.getAmount());
			}
			// 添加合计及各存款类型金额总计
			useRecords.setSeriesNumber("合计");
			useRecords.setPaperCount(paperCount);
			useRecords.setCount(count);
			useRecords.setPaperAmount(paperAmount);
			useRecords.setCoinAmount(coinAmount);
			useRecords.setForceAmount(forceAmount);
			useRecords.setOtherAmount(otherAmount);
			useRecords.setAmount(amount);
			historyUseRecordsList.add(useRecords);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(historyUseRecordsList)) {
				historyUseRecordsList.add(new HistoryUseRecords());
			}
			// 模板文件名 /历史使用记录.xls
			String fileName = msg.getMessage("door.historyUseRecords.listTemplate", null, locale);
			// String fileName = "历史使用记录.xlsx";
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.historyUseRecords.list", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, HistoryUseRecords.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, historyUseRecordsList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.historyUseRecords.list", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");

		} catch (Exception e) {
			return list(historyUseRecords, request, response, model);
		}
		return null;

	}

	@RequestMapping(value = "exportDetail")
	public String exportDetail(HistoryUseRecordsDetail historyUseRecords, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		// 查询条件： 开始时间
		if (historyUseRecords.getCreateTimeStart() != null) {
			historyUseRecords.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyUseRecords.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (historyUseRecords.getCreateTimeEnd() != null) {
			historyUseRecords.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyUseRecords.getCreateTimeEnd())));
		}
		try {
			// 存款明细列表
			List<HistoryUseRecordsDetail> historyUseRecordsDetailList = historyUseRecordsService
					.findDetailList(historyUseRecords);
			// 合计行
			HistoryUseRecordsDetail useRecords = new HistoryUseRecordsDetail();
			// 初始化
			Integer count100 = 0;
			Integer count50 = 0;
			Integer count20 = 0;
			Integer count10 = 0;
			Integer count5 = 0;
			Integer count1 = 0;
			BigDecimal paperAmount = new BigDecimal(0);
			BigDecimal coinAmount = new BigDecimal(0);
			BigDecimal forceAmount = new BigDecimal(0);
			BigDecimal otherAmount = new BigDecimal(0);
			BigDecimal amount = new BigDecimal(0);
			// 合计计算
			for (HistoryUseRecordsDetail records : historyUseRecordsDetailList) {
				records.setExlDepositDate(DateUtils.formatDate(records.getDepositDate(), "yyyy-MM-dd"));
				count100 = count100 + records.getHundred();
				count50 = count50 + records.getFifty();
				count20 = count20 + records.getTwenty();
				count10 = count10 + records.getTen();
				count5 = count5 + records.getFive();
				count1 = count1 + records.getOne();
				// 纸币金额
				paperAmount = paperAmount.add(records.getPaperAmount());
				// 硬币金额
				coinAmount = coinAmount.add(records.getCoinAmount());
				// 强制金额
				forceAmount = forceAmount.add(records.getForceAmount());
				// 其他金额
				otherAmount = otherAmount.add(records.getOtherAmount());
				// 总金额
				amount = amount.add(records.getAmount());
			}
			// 添加合计及各存款类型金额总计
			useRecords.setDepositBatches("合计");
			useRecords.setHundred(count100);
			useRecords.setFifty(count50);
			useRecords.setTwenty(count20);
			useRecords.setTen(count10);
			useRecords.setFive(count5);
			useRecords.setOne(count1);
			useRecords.setPaperAmount(paperAmount);
			useRecords.setCoinAmount(coinAmount);
			useRecords.setForceAmount(forceAmount);
			useRecords.setOtherAmount(otherAmount);
			useRecords.setAmount(amount);
			historyUseRecordsDetailList.add(useRecords);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 列表为空
			if (Collections3.isEmpty(historyUseRecordsDetailList)) {
				historyUseRecordsDetailList.add(new HistoryUseRecordsDetail());
			}
			// 模板文件名 /历史使用记录.xls
			String fileName = msg.getMessage("door.historyUseRecords.detailTemplate", null, locale);
			// String fileName = "历史使用记录.xlsx";
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.historyUseRecords.detail", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, HistoryUseRecordsDetail.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, historyUseRecordsDetailList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.historyUseRecords.detailExcel", null, locale)
							+ DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
		} catch (Exception e) {
			return detailList(historyUseRecords, request, response, model);
		}
		return null;
	}

	/**
	 * 返回上一级页面
	 *
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/historyUseRecords/?repage";
	}

}