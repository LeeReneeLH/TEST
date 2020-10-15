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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearEquipmentRecord;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearEquipmentRecordService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import freemarker.core.ParseException;

/**
 * 清机记录Controller
 * 
 * @author gzd
 * @version 2020-06-03
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/clearEquipmentRecord")
public class ClearEquipmentRecordController extends BaseController {

	@Autowired
	private ClearEquipmentRecordService clearEquipmentRecordService;
	
	/**
	 * （缴存）清机记录
	 * 
	 * @author gzd
	 * @version 2020年05月27日
	 * @param clearEquipmentRecord
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 */
	@RequestMapping(value = "clearEquipmentRecordList")
	public String clearEquipmentRecordList(ClearEquipmentRecord clearEquipmentRecord, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ClearEquipmentRecord> page = clearEquipmentRecordService.getClearEquipmentRecordPage(new Page<ClearEquipmentRecord>(request, response), clearEquipmentRecord);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/clearAddMoney/clearEquipmentRecord";
	}
	
	
	/**
	 * Excel导出明细处理
	 * 
	 * @author gzd
	 * @version 2020-06-03
	 * @param ClearEquipmentRecord
	 * @return
	 */
	@RequestMapping(value = "exportDetail")
	public String exportDetail(ClearEquipmentRecord clearEquipmentRecord, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes, Model model)
			throws FileNotFoundException, ParseException, IOException {
		try {
		Locale locale = LocaleContextHolder.getLocale();
		// 设置标题信息
		Map<String, Object> titleMap = Maps.newHashMap();
		// 制表机构
		titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
		// 制表时间
		titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
		// 查询
		List<ClearEquipmentRecord> list = clearEquipmentRecordService.findExcelList(clearEquipmentRecord);
		// 合计行
		ClearEquipmentRecord cERPool = new ClearEquipmentRecord();
		int paperCount = 0;
		BigDecimal paperAmount = new BigDecimal(0);
		BigDecimal forceAmount = new BigDecimal(0);
		BigDecimal otherAmount = new BigDecimal(0);
		BigDecimal totalAmount = new BigDecimal(0);
		// 列表为空
		if (Collections3.isEmpty(list)) {
			list.add(new ClearEquipmentRecord());
		}
		for (ClearEquipmentRecord cER : list) {
			paperCount += cER.getPaperCount();
			paperAmount = paperAmount.add(cER.getPaperAmount());
			forceAmount = forceAmount.add(cER.getForceAmount());
			otherAmount = otherAmount.add(cER.getOtherAmount());
			totalAmount = totalAmount.add(cER.getTotalAmount());
			if(!"3".equals(cER.getStatus())){
				cER.setOutNo(null);
			}
			cER.setStatus(DictUtils.getDictLabels(cER.getStatus(),"sys_clear_type","未命名"));
			Date date = new Date();
			if(cER.getAfterDate()!=null){
				date=cER.getAfterDate();
			}
			cER.setUseDate(DateUtils.formatDate(cER.getBeforeDate(), "yyyy-MM-dd HH:mm:ss")+"~"+DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss"));
		}
		cERPool.setBagNo("合计");
		cERPool.setPaperCount(paperCount);
		cERPool.setPaperAmount(paperAmount);
		cERPool.setForceAmount(forceAmount);
		cERPool.setOtherAmount(otherAmount);
		cERPool.setTotalAmount(totalAmount);
		list.add(cERPool);
		ClearEquipmentRecord clearEquipmentRecordTitle = list.get(0);
		// 区域
		titleMap.put("area", clearEquipmentRecordTitle.getArea());
		// 所属公司
		titleMap.put("doorname", clearEquipmentRecordTitle.getDoorName());
		// 机具编号
		titleMap.put("seriesnumber", clearEquipmentRecordTitle.getSeriesNumber());
			
		// 模板文件名
		String fileName = msg.getMessage("report.clearEquipmentRecord.excel", null, locale)+".xls";
		
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		sheetMap.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, titleMap);
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("report.clearEquipmentRecord.excel", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, ClearEquipmentRecord.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
	
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"清机记录" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}
	catch (Exception e){
		return clearEquipmentRecordList(clearEquipmentRecord,request,response,model);
	}
		return null;
	}
}
