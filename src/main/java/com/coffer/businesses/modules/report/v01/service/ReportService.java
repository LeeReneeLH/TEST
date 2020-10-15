package com.coffer.businesses.modules.report.v01.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.allocation.v01.entity.AllReportInfo;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.v01.entity.ReportInfo;
import com.coffer.businesses.modules.report.v01.entity.StoInfoReportEntity;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoReportInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfoEntity;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.template.ExcelConstant;
import com.coffer.tools.excel.template.ExcelExporter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 报表Service
 * @author Chengshu
 * @version 2015-05-07
 */
@Service
@Transactional(readOnly = true)
public class ReportService extends BaseService {
	
	/** EXCEL2003版后缀名 */
	private static final String EXCEL03 = ".xls";
	/** EXCEL2007版后缀名 */
	private static final String EXCEL07 = ".xlsx";
	
	/** 导出文件名 */
	private static final String OUT_FILE_NAME = "1";
	/** 模板文件名 */
	private static final String TEMP_FILE_NAME = "2";

	/**
	 * @author chengshu
	 * @version 2015/05/07
	 *
	 * 报表导出
	 * @param page 报表详细信息
	 * @param reportType 报表种别
	 * @param request 页面请求信息
	 * @param response 页面应答信息
	 * @param currency 币种
	 */
	public void exportReport(ReportInfo reportInfo, String reportType, HttpServletRequest request,
			HttpServletResponse response, String currency, MessageSource msg) {
		// 获取当前用户
		User user = UserUtils.getUser();
		// 设置标题信息
		Map<String, Object> titleMap = Maps.newHashMap();
		// 制表机构
		titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
		// 制表时间
		titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
		Locale locale = LocaleContextHolder.getLocale();
		// 设置标题
		titleMap.put(ReportConstant.ReportExportData.TOP_TITLE, Global.getConfig("report.history.goods"));
		// 设置筛选条件
		// 机构筛选
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {
			titleMap.put("office", user.getOffice().getName());

		} else {
			if (StringUtils.isNotBlank(reportInfo.getStoInfoReportEntity().getOfficeId())) {
				Office offc = reportInfo.getStoInfoReportEntity().getOffice();
				titleMap.put(ReportConstant.ReportExportData.OFFICE, offc.getName());
			} else {
				titleMap.put(ReportConstant.ReportExportData.OFFICE, Global.getConfig("report.select.office"));
			}

		}

		// 时间筛选

		// 开始时间
		if (reportInfo.getStoInfoReportEntity().getCreateTimeStart() != null) {

			titleMap.put(ReportConstant.ReportExportData.START_TIME, DateUtils.foramtSearchDate(
					DateUtils.getDateStart(reportInfo.getStoInfoReportEntity().getCreateTimeStart()))
			);
		} else {
			titleMap.put(ReportConstant.ReportExportData.START_TIME, "");
		}
		// 结束时间
		if (reportInfo.getStoInfoReportEntity().getCreateTimeEnd() != null) {

			titleMap.put(ReportConstant.ReportExportData.END_TIME, DateUtils
					.foramtSearchDate(DateUtils.getDateEnd(reportInfo.getStoInfoReportEntity().getCreateTimeEnd()))
			);
		} else {
			titleMap.put(ReportConstant.ReportExportData.END_TIME, "");
		}

		// 单位
		if (StringUtils.isNotBlank(reportInfo.getStoInfoReportEntity().getFilterCondition())) {

			titleMap.put(ReportConstant.ReportExportData.DATE_UNIT,
					DictUtils.getDictLabel(
							reportInfo.getStoInfoReportEntity().getFilterCondition()
									.replace(Constant.Punctuation.HYPHEN, Constant.Punctuation.HALF_UNDERLINE),
					"report_filter_condition", ""));

		}

		// 导出文件名
		String fileName = getTempFileName(currency, locale, reportType, msg, OUT_FILE_NAME);
		// 模板文件名
		String templateFileName = getTempFileName(currency, locale, reportType, msg, TEMP_FILE_NAME);
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");

		// 导出处理
		ExcelExporter excelExport = initExcelExporter(reportType);

		excelExport.setTemplatePath(templatePath + templateFileName);

		excelExport.setDataList(getReportList(reportInfo, reportType));
		excelExport.setTitleMap(titleMap);
		excelExport.export(request, response, fileName);
	}
	
	/**
	 * @author wh 物品库存页面导出
	 * @param reportInfo
	 * @param reportType
	 * @param request
	 * @param response
	 * @param currency
	 * @param msg
	 */
	public void exportReportBar(ReportInfo reportInfo, String reportType, HttpServletRequest request,
			HttpServletResponse response, String currency, MessageSource msg) {

		// 设置标题信息
		Map<String, Object> titleMap = Maps.newHashMap();

		// 制表机构
		titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());

		// 制表时间
		titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
		Locale locale = LocaleContextHolder.getLocale();

		// 设置标题
		titleMap.put(ReportConstant.ReportExportData.TOP_TITLE, Global.getConfig("report.stores.goods"));

		// 设置筛选条件
		// 机构筛选
		if ("".equals(reportInfo.getStoresInfoEntity().getOffice().getId())) {

			titleMap.put(ReportConstant.ReportExportData.OFFICE, Global.getConfig("report.select.office"));
		} else {

			titleMap.put(ReportConstant.ReportExportData.OFFICE,
					reportInfo.getStoresInfoEntity().getOffice().getName());
		}

		// 导出文件名
		String fileName = getTempFileName(currency, locale, reportType, msg, OUT_FILE_NAME);
		// 模板文件名
		String templateFileName = getTempFileName(currency, locale, reportType, msg, TEMP_FILE_NAME);
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");

		// 导出处理
		ExcelExporter excelExport = initExcelExporter(reportType);

		excelExport.setTemplatePath(templatePath + templateFileName);

		excelExport.setDataList(getReportList(reportInfo, reportType));
		excelExport.setTitleMap(titleMap);
		excelExport.export(request, response, fileName);
	}


	/**
	 * 取得报表数据列表
	 * @param reportType
	 * @return
	 */
	private List<?> getReportList(ReportInfo reportInfo, String reportType){
		
		// 物品库存报表
		if(ReportConstant.ReportType.GOODS_INVENTORY.equals(reportType)){
			List<StoStoresInfoEntity> list = reportInfo.getStoStoresInfoEntityList();
			StoStoresInfoEntity sto = new StoStoresInfoEntity();
			list.add(sto);
			return list;


		// 重空库存报表
		}else if(ReportConstant.ReportType.EMPTY_INVENTORY.equals(reportType)){
			return reportInfo.getStoStoresInfoList();

		// 重空变更报表
		}else if(ReportConstant.ReportType.EMPTY_CHANGE.equals(reportType)){
			return reportInfo.getStoEmptyHistoryList();

		// 调拨相关报表
		}else if(ReportConstant.ReportType.CASH_BETWEEN.equals(reportType)
				|| ReportConstant.ReportType.CASH_HANDIN.equals(reportType)
				|| ReportConstant.ReportType.CASH_ORDER.equals(reportType)){
			return reportInfo.getAllReportList();

			// 历史报表
		} else if (ReportConstant.ReportType.STORE_GRAPH.equals(reportType)) {

			List<StoInfoReportEntity> list = reportInfo.getStoInfoReportEntityList();
			StoInfoReportEntity sto = new StoInfoReportEntity();
			list.add(sto);
			return list;

			// 调拨相关报表
		}

		return null;
	}
	
	/**
	 * 初始化报表导出类
	 * @param reportType
	 * @return
	 */
	private ExcelExporter initExcelExporter(String reportType){

		// 物品库存报表
		if(ReportConstant.ReportType.GOODS_INVENTORY.equals(reportType)){
			return new ExcelExporter(StoStoresInfoEntity.class);

		// 重空库存报表
		}else if(ReportConstant.ReportType.EMPTY_INVENTORY.equals(reportType)){
			return new ExcelExporter(StoStoresInfo.class);

		// 重空变更报表
		}else if(ReportConstant.ReportType.EMPTY_CHANGE.equals(reportType)){
			return new ExcelExporter(StoStoresHistory.class);

		// 调拨相关报表
		}else if(ReportConstant.ReportType.CASH_BETWEEN.equals(reportType)
				|| ReportConstant.ReportType.CASH_HANDIN.equals(reportType)
				|| ReportConstant.ReportType.CASH_ORDER.equals(reportType)){
			return new ExcelExporter(AllReportInfo.class);

			// 历史报表
		} else if (ReportConstant.ReportType.STORE_GRAPH.equals(reportType)) {
			return new ExcelExporter(StoInfoReportEntity.class);

		}

		return null;
	}
	
	/**
	 * @author chengshu
	 * @version 2015/06/17
	 *
	 * 编辑物品库存报表信息
	 * @param stoStoresInfo 页面信息
	 * @param goodsList 物品库存列表
	 * @param page 页面显示信息
	 * @return 报表信息
	 */
	public List<StoReportInfo> setGoodsStoresInfo(StoStoresInfo stoStoresInfo, List<StoStoresInfo> goodsList,
			Page<StoReportInfo> page) {
	
		// 报表详细实体
		StoReportInfo reportTotal = new StoReportInfo();
	
		List<StoReportInfo> reportList = Lists.newArrayList();
		// 面值种别
		String denominationType = "";
		// 取得所有钞箱信息
		for (StoStoresInfo goodsInfo : goodsList) {
	
			// 报表详细实体
			StoReportInfo reportInfo = new StoReportInfo();
	
			// 日期
			/*
			 * reportInfo.setTime( DateUtils.formatDate(stoStoresInfo.getCreateDate(),
			 * StoreConstant.Dates.FORMATE_YYYY_MM_DD));
			 */
			reportInfo.setTime(DateUtils.formatDate(goodsInfo.getCreateDate(), StoreConstant.Dates.FORMATE_YYYY_MM_DD));

			StoGoodSelect stoGoodSelect = splitGood(goodsInfo.getGoodsId());
			// 机构
			reportInfo.setOffice(goodsInfo.getOffice());
			// 机构名字
			reportInfo.setOfficeN(goodsInfo.getOffice().getName());
			// 币种
			reportInfo.setCurrency(GoodDictUtils.getDictLabel(stoGoodSelect.getCurrency(), "currency", null));
			// 类别
			reportInfo.setClassification(
					GoodDictUtils.getDictLabel(stoGoodSelect.getClassification(), "classification", null));
			// 套别
			reportInfo.setEdition(GoodDictUtils.getDictLabel(stoGoodSelect.getEdition(), "edition", null));
			// 材质
			reportInfo.setCash(GoodDictUtils.getDictLabel(stoGoodSelect.getCash(), "cash", null));
			// 面值
			denominationType = GoodDictUtils.getDenominationType(stoGoodSelect.getCurrency(), stoGoodSelect.getCash());
			reportInfo.setDenomination(GoodDictUtils.getDictLabel(stoGoodSelect.getDenomination(), denominationType, null));
			// 单位
			reportInfo.setUnit(GoodDictUtils.getDictLabel(stoGoodSelect.getUnit(), "c_unit", null));
			// 数量
			reportInfo.setNumber(goodsInfo.getStoNum());
	
			// 残损币金额
			if (StoreConstant.MoneyType.DEMANGED_MONEY.equals(stoGoodSelect.getClassification())) {
				reportInfo.setAmountDamage(goodsInfo.getAmount());
				reportTotal.setAmountDamage(StoreCommonUtils.addBigDecimal(reportTotal.getAmountDamage(), goodsInfo.getAmount()));
	
				// 假币金额
			} else if (StoreConstant.MoneyType.COUNTERFEIT_MONEY.equals(stoGoodSelect.getClassification())) {
				reportInfo.setAmountCounterfeit(goodsInfo.getAmount());
				reportTotal.setAmountCounterfeit(StoreCommonUtils.addBigDecimal(reportTotal.getAmountCounterfeit(), goodsInfo.getAmount()));
	
				// 待整点币金额
			} else if (StoreConstant.MoneyType.COUNTWAIT_MONEY.equals(stoGoodSelect.getClassification())) {
				reportInfo.setAmountCountwait(goodsInfo.getAmount());
				reportTotal.setAmountCountwait(StoreCommonUtils.addBigDecimal(reportTotal.getAmountCountwait(), goodsInfo.getAmount()));
	
				// 流通币金额
			} else {
				reportInfo.setAmount(goodsInfo.getAmount());
				reportTotal.setAmount(StoreCommonUtils.addBigDecimal(reportTotal.getAmount(), goodsInfo.getAmount()));
			}
	
			// 详细信息添加到列表里
			reportList.add(reportInfo);
		}
	
		reportTotal.setTime("合计");
		reportList.add(reportTotal);
	
		return reportList;
	}

	/**
	 * @author chengshu
	 * @version 2015/09/28
	 *
	 * 取得报表模板名称
	 * @param currency 币种
	 * @param locale 国际化
	 * @param reportType 报表种别
	 * @param msg 报表种别
	 * @param fileType 文件种别
	 * @return 报表模板名称
	 */
	private String getTempFileName(String currency, Locale locale, String reportType, MessageSource msg, String fileType) {

		String fileName = "";

		// 业务种别：库间现金调拨
		if (ReportConstant.ReportType.CASH_BETWEEN.equals(reportType)) {
			fileName = getFileName(reportType, locale,
					msg.getMessage("report.fileName.cashBetweenReport", null, locale), 
					currency, fileType);

			// 业务种别：现金上缴
		} else if (ReportConstant.ReportType.CASH_HANDIN.equals(reportType)) {
			fileName = getFileName(reportType, locale,
					msg.getMessage("report.fileName.cashHandinReport", null, locale), 
					currency, fileType);

			// 业务种别：配款信息
		} else if (ReportConstant.ReportType.CASH_ORDER.equals(reportType)) {
			fileName = getFileName(reportType, locale,
					msg.getMessage("report.fileName.cashOrderReport", null, locale), 
					null, fileType);

			// 业务种别：现金库存
		} else if (ReportConstant.ReportType.GOODS_INVENTORY.equals(reportType)) {
			fileName = getFileName(reportType, locale,
					msg.getMessage("report.fileName.goodsStoresReport", null, locale), 
					currency, fileType);

			// 业务种别：重空库存
		} else if (ReportConstant.ReportType.EMPTY_INVENTORY.equals(reportType)) {
			fileName = getFileName(reportType, locale,
					msg.getMessage("report.fileName.importantEmptyInventoryReport", null, locale), 
					null, fileType);

			// 业务种别：重空变更
		} else if (ReportConstant.ReportType.EMPTY_CHANGE.equals(reportType)) {
			fileName = getFileName(reportType, locale,
					msg.getMessage("report.fileName.importantEmptyChangeReport", null, locale), 
					null, fileType);
			// 库存历史变化
		} else if (ReportConstant.ReportType.STORE_GRAPH.equals(reportType)) {
			fileName = getFileName(reportType, locale, msg.getMessage("report.fileName.goodsStoresGraph", null, locale),
					null, fileType);

		}
		return fileName;
	}
	
	/**
	 * @author chengshu
	 * @version 2015/09/28
	 *
	 * 取得报表模板名称
	 * @param reportType 报表种别
	 * @param locale 国际化
	 * @param name 模板名称
	 * @param currency 币种
	 * @param fileType 文件种别
	 * @return 报表模板名称
	 */
	private String getFileName(String reportType, Locale locale, String name, String currency, String fileType) {

		StringBuffer fileName = new StringBuffer();

		// 模板名称
		fileName.append(name);

		// 货币库存报表场合，导出文件与模板文件不一样
		// 库存表
		if (ReportConstant.ReportType.GOODS_INVENTORY.equals(reportType)) {
			if (OUT_FILE_NAME.equals(fileType)) {
				fileName.append("");
			}
       
			// 币种
		} else if (StringUtils.isNoneBlank(currency)) {
			fileName.append(GoodDictUtils.getDictLabel(currency, "currency", null));
		}
		// 历史报表
		if (ReportConstant.ReportType.STORE_GRAPH.equals(reportType)) {
			if (OUT_FILE_NAME.equals(fileType)) {
				fileName.append("");
			}

		// 币种
		} else if (StringUtils.isNoneBlank(currency)) {
			fileName.append(GoodDictUtils.getDictLabel(currency, "currency", null));
		}

		// 添加后缀名
		if (ExcelConstant.EXCEL_TYPE_03.equals(Global.getConfig("excel.export.template.type"))) {
			fileName.append(EXCEL03);
		} else if (ExcelConstant.EXCEL_TYPE_07.equals(Global.getConfig("excel.export.template.type"))) {
			fileName.append(EXCEL07);
		}

		return fileName.toString();
	}

	/**
	 * 分割物品ID
	 * @param goodsID
	 * @return
	 */
	private StoGoodSelect splitGood(String goodsID) {
		StoGoodSelect stoGoodSelect = new StoGoodSelect();
		// 币种
		stoGoodSelect.setCurrency(StringUtils.mid(goodsID, 0, 3));
		// 类别
		stoGoodSelect.setClassification(StringUtils.mid(goodsID, 3, 2));
		// 套别
		stoGoodSelect.setEdition(StringUtils.mid(goodsID, 5, 1));
		// 软/硬币
		stoGoodSelect.setCash(StringUtils.mid(goodsID, 6, 1));
		// 面值
		stoGoodSelect.setDenomination(StringUtils.mid(goodsID, 7, 2));
		// 单位
		stoGoodSelect.setUnit(StringUtils.mid(goodsID, 9, 3));

		return stoGoodSelect;
	}
}
