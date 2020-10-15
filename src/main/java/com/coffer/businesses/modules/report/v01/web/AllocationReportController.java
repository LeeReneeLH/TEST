package com.coffer.businesses.modules.report.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.AllocationConstant.BusinessType;
import com.coffer.businesses.modules.allocation.AllocationConstant.InOutCoffer;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateInfoDao;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllReportInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.ReportConstant.ReportExportData;
import com.coffer.businesses.modules.report.ReportConstant.SheetName;
import com.coffer.businesses.modules.report.ReportGraphConstant.GraphType;
import com.coffer.businesses.modules.store.v01.service.StoGoodsService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 库间报表统计：报表统计功能Controller
 * 
 * @author yanbingxu
 * @version 2017-8-26
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/allocation")
public class AllocationReportController extends BaseController {

	/** 调缴用Service */
	@Autowired
	private AllocationService allocationService;
	@Autowired
	private AllAllocateInfoDao allocateInfoDao;
	@Autowired
	private StoGoodsService stoGoodsService;

	/**
	 * @author yanbingxu
	 * @date 2017-8-26
	 * @Description 图形报表页面跳转
	 * @param
	 * @return
	 * @throws
	 */
	@RequestMapping(value = { "cashBetweenECharts", "" })
	public String cashBetweenECharts(AllAllocateInfo allAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) throws ParseException {

		return "modules/report/v01/graph/cashBetweenECharts";

	}

	/**
	 * @author yanbingxu
	 * @date 2017-8-26
	 * @Description 图数据查询
	 * @param
	 * @return
	 * @throws
	 */
	@RequestMapping(value = "searchGraphInfo")
	@ResponseBody
	public String searchGraphInfo(AllAllocateInfo allAllocateInfo, Model model, String type) {
		// 设置业务种别
		if (StringUtils.isEmpty(allAllocateInfo.getBusinessType())) {

			// 业务类型为库间总括(包括清分，配钞，清机)
			allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between);

			// 如果业务种别是空，设置所有库间业务种别
			String[] businessTypes = new String[] { AllocationConstant.BusinessType.Between_Clear,
					AllocationConstant.BusinessType.Between_ATM_Add,
					AllocationConstant.BusinessType.Between_ATM_Clear };

			// 设置业务种别
			allAllocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
		} else {
			List<String> businessTypes = Lists.newArrayList();
			businessTypes.add(allAllocateInfo.getBusinessType());
			allAllocateInfo.setBusinessTypes(businessTypes);
		}

		// 设置当前用户信息
		allAllocateInfo.setLoginUser(UserUtils.getUser());

		// 如果不是金融平台人员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			allAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
		}

		// 设置业务状态为完成
		allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.FINISH_STATUS);

		// 连字符替换下划线
		if (StringUtils.isNotBlank(allAllocateInfo.getDateFlag())) {
			allAllocateInfo.setDateFlag(allAllocateInfo.getDateFlag().replace(Constant.Punctuation.HALF_UNDERLINE,
					Constant.Punctuation.HYPHEN));
		}
		// 查询条件：开始时间
		if (allAllocateInfo.getCreateTimeStart() != null) {
			allAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：结束时间
		if (allAllocateInfo.getCreateTimeEnd() != null) {
			allAllocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allAllocateInfo.getCreateTimeEnd())));
		}
		Map<String, Object> jsonData = null;
		if (StringUtils.equals(type, GraphType.BAR)) {
			jsonData = allocationService.makeBarGraphData(allAllocateInfo);
		}
		if (StringUtils.equals(type, GraphType.LINE)) {
			jsonData = allocationService.makeLineGraphData(allAllocateInfo);
		}

		return gson.toJson(jsonData);
	}

	/**
	 * @author yanbingxu
	 * @date 2017-8-26
	 * @Description 根据查询条件，查询库间现金调拨信息
	 * @param
	 * @return
	 * @throws
	 */
	@RequestMapping(value = { "cashBetweenReport" })
	public String cashBetweenReport(AllAllocateInfo allAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) throws ParseException {

		// 设置业务种别
		if (StringUtils.isEmpty(allAllocateInfo.getBusinessType())) {

			// 业务类型为库间总括(包括清分，配钞，清机)
			allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between);

			// 如果业务种别是空，设置所有库间业务种别
			String[] businessTypes = new String[] { AllocationConstant.BusinessType.Between_Clear,
					AllocationConstant.BusinessType.Between_ATM_Add,
					AllocationConstant.BusinessType.Between_ATM_Clear };

			// 设置业务种别
			allAllocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
		} else {
			List<String> businessTypes = Lists.newArrayList();
			businessTypes.add(allAllocateInfo.getBusinessType());
			allAllocateInfo.setBusinessTypes(businessTypes);
		}

		// 设置当前用户信息
		allAllocateInfo.setLoginUser(UserUtils.getUser());

		// 如果不是金融平台人员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			allAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
		}

		// 设置业务状态为完成
		allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.FINISH_STATUS);

		// 初始化页面信息
		Page<AllAllocateInfo> page = new Page<AllAllocateInfo>(request, response);
		List<AllReportInfo> reportList = Lists.newArrayList();
		List<AllReportInfo> tempReportList = Lists.newArrayList();

		// 根据条件执行查询处理
		if (allAllocateInfo.getBusinessTypes().size() > 1) {
			allAllocateInfo.setInoutType(InOutCoffer.OUT);
			String[] businessTypes = new String[] { BusinessType.Between_Clear, BusinessType.Between_ATM_Add };
			allAllocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
			page = searchPageInfo(allAllocateInfo, page);
			tempReportList = page.getList().get(0).getReportInfoList();
			allAllocateInfo.setInoutType(InOutCoffer.IN);
			businessTypes = new String[] { BusinessType.Between_Clear, BusinessType.Between_ATM_Add,
					BusinessType.Between_ATM_Clear };
			allAllocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
			page = searchPageInfo(allAllocateInfo, page);
			reportList = page.getList().get(0).getReportInfoList();
			reportList.addAll(tempReportList);
			page.getList().get(0).setReportInfoList(reportList);
		} else {
			if (allAllocateInfo.getBusinessTypes().contains(BusinessType.Between_ATM_Clear)) {
				allAllocateInfo.setInoutType(InOutCoffer.IN);
				page = searchPageInfo(allAllocateInfo, page);
			} else {
				page = searchPageInfo(allAllocateInfo, page);
			}
		}

		// 保存Session
		model.addAttribute("page", page);

		return "modules/report/v01/allocation/cashBetweenReport";
	}

	/**
	 * @throws IOException
	 * @throws ParseException
	 * @throws FileNotFoundException
	 * @author yanbingxu
	 * @date 2017-8-26
	 * @Description 现金出入库明细报表导出
	 * @param
	 * @return
	 * @throws
	 */
	@RequestMapping(value = "exportCashBetweenReport")
	public void exportCashBetweenReport(AllAllocateInfo allAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes)
			throws FileNotFoundException, ParseException, IOException {

		// 设置业务种别
		if (StringUtils.isEmpty(allAllocateInfo.getBusinessType())) {
			// 业务类型为库间总括(包括清分，配钞，清机)
			allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between);
			// 如果业务种别是空，设置所有库间业务种别
			String[] businessTypes = new String[] { AllocationConstant.BusinessType.Between_Clear,
					AllocationConstant.BusinessType.Between_ATM_Add,
					AllocationConstant.BusinessType.Between_ATM_Clear };

			// 设置业务种别
			allAllocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
		} else {
			List<String> businessTypes = Lists.newArrayList();
			businessTypes.add(allAllocateInfo.getBusinessType());
			allAllocateInfo.setBusinessTypes(businessTypes);
		}
		
		// 连字符替换下划线
		if (StringUtils.isNotBlank(allAllocateInfo.getDateFlag())) {
			allAllocateInfo.setDateFlag(allAllocateInfo.getDateFlag().replace(Constant.Punctuation.HALF_UNDERLINE,
					Constant.Punctuation.HYPHEN));
		}

		// 查询条件：开始时间
		if (allAllocateInfo.getCreateTimeStart() != null) {
			allAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：结束时间
		if (allAllocateInfo.getCreateTimeEnd() != null) {
			allAllocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allAllocateInfo.getCreateTimeEnd())));
		}

		// 设置当前用户信息
		allAllocateInfo.setLoginUser(UserUtils.getUser());

		// 如果不是金融平台人员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			allAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
		}

		// 设置业务状态为完成
		allAllocateInfo.setStatus(AllocationConstant.BetweenStatus.FINISH_STATUS);

		// 执行导出处理
		exportReport(allAllocateInfo, request, response, ReportConstant.ReportType.CASH_BETWEEN);

	}

	/**
	 * @author yanbingxu
	 * @date 2017-8-26
	 * @Description 入库明细(库间)报表导出
	 * @param
	 * @return
	 * @throws
	 */
	private void exportReport(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			String reportType) throws ParseException, FileNotFoundException, IOException {

		// 报表信息初始化
		List<AllReportInfo> volumeReportList = Lists.newArrayList();
		List<AllReportInfo> inReportList = Lists.newArrayList();
		List<AllReportInfo> outReportList = Lists.newArrayList();
		List<String> officeDataList = Lists.newArrayList();
		List<String> timeDataList = Lists.newArrayList();

		// 业务量报表信息查询
		List<AllAllocateInfo> volumeResultList = allocateInfoDao.findLineGraphData(allAllocateInfo);

		for (AllAllocateInfo result : volumeResultList) {
			if (!officeDataList.contains(result.getrOfficeName())) {
				officeDataList.add(result.getrOfficeName());
			}
			if (!timeDataList.contains(result.getStrDate())) {
				timeDataList.add(result.getStrDate());
			}
		}

		// 初始化输出数据
		for (String time : timeDataList) {
			for (String office : officeDataList) {
				AllReportInfo allReportInfo = new AllReportInfo();
				allReportInfo.setTime(time);
				allReportInfo.setrOfficeName(office);
				allReportInfo.setMoneyAmount(0L);
				volumeReportList.add(allReportInfo);
			}
		}

		// 输出数据处理
		for (AllAllocateInfo result : volumeResultList) {
			for (AllReportInfo volume : volumeReportList) {
				if (volume.getTime().equals(result.getStrDate())
						& volume.getrOfficeName().equals(result.getrOfficeName())) {
					volume.setMoneyAmount(volume.getMoneyAmount() + 1);
					break;
				}
			}
		}

		// 入库物品报表信息查询
		allAllocateInfo.setInoutType(InOutCoffer.IN);
		List<AllAllocateInfo> inResultList = allocateInfoDao.findBarGraphData(allAllocateInfo);

		// 入库物品报表信息处理
		makeGoodsReportData(inResultList, inReportList);

		// 设定查询条件
		allAllocateInfo.setInoutType(InOutCoffer.OUT);
		if (BusinessType.Between_ATM_Clear.equals(allAllocateInfo.getBusinessType())) {
			String[] businessTypes = new String[] { "noResult" };
			allAllocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
		} else {
			if (allAllocateInfo.getBusinessTypes().contains(BusinessType.Between_ATM_Clear)) {
				String[] businessTypes = new String[] { BusinessType.Between_Clear, BusinessType.Between_ATM_Add };
				allAllocateInfo.setBusinessTypes(Arrays.asList(businessTypes));
			}
		}

		// 出库物品报表信息查询
		List<AllAllocateInfo> outResultList = allocateInfoDao.findBarGraphData(allAllocateInfo);

		// 出库物品报表信息处理
		makeGoodsReportData(outResultList, outReportList);

		Locale locale = LocaleContextHolder.getLocale();
		String fileName = null;
		// 模板文件名
		if (StringUtils.isNotBlank(allAllocateInfo.getBusinessType())) {
			fileName = msg.getMessage(
					"allocation.export.report.cashBetween.fileName" + allAllocateInfo.getBusinessType(), null, locale);
		}

		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");

		List<Map<String, Object>> paramList = Lists.newArrayList();

		// sheet1数据填充
		excelSheetInsert(paramList, SheetName.WORK_VOLUME, volumeReportList,fileName,allAllocateInfo);

		// sheet2数据填充
		if (!BusinessType.Between_ATM_Add.equals(allAllocateInfo.getBusinessType())) {
			excelSheetInsert(paramList, SheetName.IN_GOODS_VOLUME, inReportList,fileName,allAllocateInfo);
		}

		// sheet3数据填充
		if (!BusinessType.Between_ATM_Clear.equals(allAllocateInfo.getBusinessType())) {
			excelSheetInsert(paramList, SheetName.OUT_GOODS_VOLUME, outReportList,fileName,allAllocateInfo);
		}

		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);

		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName);

	}

	/**
	 * @author yanbingxu
	 * @date 2017-9-5
	 * @Description sheet数据填充
	 * @param
	 * @return
	 * @throws
	 */
	private void excelSheetInsert(List<Map<String, Object>> paramList, String sheetName,
			List<AllReportInfo> reportList,String titlename,AllAllocateInfo allAllocateInfo) {

		// 设置sheet信息
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, sheetName);
		// 设置标题信息
		Map<String, Object> sheetTitleMap = Maps.newHashMap();
		// 制表机构
		sheetTitleMap.put(ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
		// 制表时间
		sheetTitleMap.put(ReportExportData.NOW_DATE, DateUtils.getDateTime());
		// 时间格式
		sheetTitleMap.put(ReportExportData.DATE_UNIT, DictUtils.getDictLabel(allAllocateInfo.getDateFlag().replace(Constant.Punctuation.HYPHEN,
				Constant.Punctuation.HALF_UNDERLINE), "report_filter_condition", null));
		// 开始时间
		if (allAllocateInfo.getCreateTimeStart() == null) {
			sheetTitleMap.put(ReportExportData.START_TIME, "");
		} else {
			sheetTitleMap.put(ReportExportData.START_TIME, allAllocateInfo.getSearchDateStart());
		}
		// 结束时间
		if (allAllocateInfo.getCreateTimeEnd() == null) {
			sheetTitleMap.put(ReportExportData.END_TIME, "");
		} else {
			sheetTitleMap.put(ReportExportData.END_TIME, allAllocateInfo.getSearchDateEnd());
		}
		// 标题
		sheetTitleMap.put(ReportExportData.TOP_TITLE, titlename.substring(0,6)+sheetName);

		sheetMap.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheetTitleMap);
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, reportList);
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AllReportInfo.class.getName());

		paramList.add(sheetMap);
	}

	/**
	 * @author yanbingxu
	 * @date 2017-9-5
	 * @Description 出入库报表数据处理
	 * @param
	 * @return
	 * @throws
	 */
	private void makeGoodsReportData(List<AllAllocateInfo> resultList, List<AllReportInfo> reportList) {
		List<String> goodDataList = Lists.newArrayList();
		List<String> timeDataList = Lists.newArrayList();
		for (AllAllocateInfo result : resultList) {
			if (StringUtils.isNotBlank(result.getGoodsId())) {
				if (!goodDataList.contains(result.getGoodsId())) {
					goodDataList.add(result.getGoodsId());
				}
				if (!timeDataList.contains(result.getStrDate())) {
					timeDataList.add(result.getStrDate());
				}
			}
		}
		// 初始化输出数据
		for (String time : timeDataList) {
			for (String good : goodDataList) {
				if (StringUtils.isNotBlank(good)) {
					AllReportInfo allReportInfo = new AllReportInfo();
					allReportInfo.setTime(time);
					allReportInfo.setGoodsId(good);
					allReportInfo.setGoodsName(stoGoodsService.get(good).getGoodsName());
					allReportInfo.setMoneyAmount(0L);
					reportList.add(allReportInfo);
				}
			}
		}
		// 输出数据处理
		for (AllAllocateInfo result : resultList) {
			for (AllReportInfo out : reportList) {
				if (out.getTime().equals(result.getStrDate()) & out.getGoodsId().equals(result.getGoodsId())) {
					Long tempAmount = result.getMoneyAmount();
					out.setMoneyAmount(tempAmount + out.getMoneyAmount());
					break;
				}
			}
		}
	}

	/**
	 * @author yanbingxu
	 * @date 2017-8-26
	 * @Description 根据查询条件，查询库间钞箱调拨信息
	 * @param
	 * @return
	 * @throws
	 */
	private Page<AllAllocateInfo> searchPageInfo(AllAllocateInfo allocateInfo, Page<AllAllocateInfo> page)
			throws ParseException {

		page.setPageNo(0);
		// 库间业务查询处理
		if (allocateInfo.getBusinessType().startsWith(AllocationConstant.BusinessType.Between)) {

			// 连字符替换下划线
			if (StringUtils.isNotBlank(allocateInfo.getDateFlag())) {
				allocateInfo.setDateFlag(allocateInfo.getDateFlag().replace(Constant.Punctuation.HALF_UNDERLINE,
						Constant.Punctuation.HYPHEN));
			}

			page = allocationService.findAllocation(page, allocateInfo, false);

			// 编辑页面显示用信息
			setCashBetweenInfo(page);
		}
		return page;
	}

	/**
	 * @author yanbingxu
	 * @date 2017-8-26
	 * @Description 编辑现金调拨信息
	 * @param
	 * @return
	 * @throws
	 */
	private void setCashBetweenInfo(Page<AllAllocateInfo> page) {

		// 定义所有的钞箱信息
		List<AllReportInfo> allReportList = Lists.newArrayList();

		// 取得所有钞箱信息
		for (AllAllocateInfo allocateInfo : page.getList()) {
			// 报表详细实体
			AllReportInfo reportInfo = new AllReportInfo();
			if (StringUtils.isNotBlank(allocateInfo.getGoodsName())) {

				// 日期
				reportInfo.setTime(allocateInfo.getStrDate());
				// 业务种别
				reportInfo.setBusinessType(
						DictUtils.getDictLabel(allocateInfo.getBusinessType(), "all_businessType", null));
				reportInfo.setrOfficeName(allocateInfo.getrOfficeName());
				reportInfo.setaOfficeName(allocateInfo.getaOfficeName());
				reportInfo.setGoodsName(allocateInfo.getGoodsName());
				reportInfo.setGoodsId(allocateInfo.getGoodsId());
				reportInfo.setMoneyNumber(allocateInfo.getMoneyNumber());
				reportInfo.setMoneyAmount(allocateInfo.getMoneyAmount());
				allReportList.add(reportInfo);
			}
		}

		// 设置页面显示用Page信息
		List<AllAllocateInfo> pageInfoList = Lists.newArrayList();
		AllAllocateInfo pageInfo = new AllAllocateInfo();
		pageInfo.setReportInfoList(allReportList);
		pageInfoList.add(pageInfo);
		page.setList(pageInfoList);
	}
}