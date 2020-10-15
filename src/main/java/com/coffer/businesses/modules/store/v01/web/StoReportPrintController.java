package com.coffer.businesses.modules.store.v01.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoReportPrintService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.tools.excel.template.ExcelConstant;
import com.coffer.tools.excel.template.ExcelExporter;

/**
 * 库房报表管理Controller
 * 
 * @author niguoyong
 * @version 2015-09-15
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoReportPrint")
public class StoReportPrintController extends BaseController {

	@Autowired
	private StoReportPrintService stoReportPrintService;

	@RequiresPermissions("store:stoReportPrint:view")
	@RequestMapping(value = { "print", "" })
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> map = stoReportPrintService.reportMessage();
		model.addAttribute("reportMap", map);
		model.addAttribute("date", new Date());

		return "modules/store/v01/stoReportPrint/stoReportPrint";
	}

	@RequiresPermissions("store:stoReportPrint:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException {
		List<StoBoxInfo> lists = stoReportPrintService.findInTransitBoxInfos();
		Locale locale = LocaleContextHolder.getLocale();
		// 国际化配置中定义
		// 取得导出文件名
		String fileName = null;
		// 模板路径
		String templatePath = null;
		// 模板类型为97~2003
		if (ExcelConstant.EXCEL_TYPE_03.equals(Global.getConfig("excel.export.template.type"))) {
			fileName = msg.getMessage("store.export.report.fileName.03", null, locale);
			templatePath = Global.getConfig("export.template.store.warehouseReport.03");
		}
		// 模板类型为2007
		else if (ExcelConstant.EXCEL_TYPE_07.equals(Global.getConfig("excel.export.template.type"))) {
			fileName = msg.getMessage("store.export.report.fileName.07", null, locale);
			templatePath = Global.getConfig("export.template.store.warehouseReport.07");
		}

		// 设定模板路径与数据
		ExcelExporter excelExport = new ExcelExporter(StoBoxInfo.class);
		excelExport.setTemplatePath(templatePath);
		excelExport.setDataList(lists);

		// 根据箱袋类型更改箱袋状态
		List<Map<String, Object>> fieldLists = excelExport.getFieldsList();
		for (Map<String, Object> obj : fieldLists) {
			String boxType = (String) obj.get("boxtype");
			String boxStatus = (String) obj.get("boxstatus");
			// 根据状态码获取字典值
			if (boxType.equals(Constant.BoxType.BOX_NOTE)) {
				boxStatus = DictUtils.getDictLabel(boxStatus, "sto_box_status", "");
			} else {
				boxStatus = DictUtils.getDictLabel(boxStatus, "sto_box_status", "");
			}
			boxType = DictUtils.getDictLabel(boxType, "sto_box_type", "");

			obj.put("boxtype", boxType);
			obj.put("boxstatus", boxStatus);
		}
		// 报表导出
		excelExport.setFieldsList(fieldLists);
		excelExport.export(request, response, fileName);
		return null;
	}

}
