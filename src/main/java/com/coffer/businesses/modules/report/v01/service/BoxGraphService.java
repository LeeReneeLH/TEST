package com.coffer.businesses.modules.report.v01.service;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.report.v01.dao.BoxReportGraphDao;
import com.coffer.businesses.modules.report.v01.entity.StoBoxInfoGraphEntity;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author wh
 *
 */
@Service
@Transactional(readOnly = true)
public class BoxGraphService extends BaseService {

	@Autowired
	BoxReportGraphDao boxReportGraphDao;

	/**
	 * 
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<StoBoxInfoGraphEntity> findBoxNumGraph(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {
		return boxReportGraphDao.findBoxNumGraph(stoBoxInfoGraphEntity);
	}

	/**
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<StoBoxInfoGraphEntity> findBoxStatusGraph(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {
		return boxReportGraphDao.findBoxStatusGraph(stoBoxInfoGraphEntity);
	}

	/**
	 * 放入各机构箱袋类型和对应的数量的数据
	 * 
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> makeBoxTypeGraphData(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {

		Map<String, Object> rtnMap = Maps.newHashMap();

		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		// StoStoresInfoEntity stoStoresInfo = new StoStoresInfoEntity();
		// stoStoresInfo.setOffice(stoReportInfo.getOffice());
		List<StoBoxInfoGraphEntity> resultList = findBoxNumGraph(stoBoxInfoGraphEntity);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (StoBoxInfoGraphEntity entity : resultList) {
			// 放入legend配置项数据

			String box_type = DictUtils.getDictLabel(entity.getBoxType(), "sto_box_type", "");

			if (!legendDataList.contains(box_type)) {
				legendDataList.add(box_type);
			}

			// 放入x轴数据
			if (!xAxisDataList.contains(entity.getOfficeName())) {
				xAxisDataList.add(entity.getOfficeName());
			}

			// 按类别放入货物数据
			if (!seriesMap.containsKey(box_type)) {

				List<String> dataList = Lists.newArrayList();

				dataList.add(entity.getBoxNum());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, box_type);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, Global.getConfig("report.bar.all"));
				map.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.width"));
				seriesMap.put(box_type, map);
			} else {

				Map<String, Object> map = (Map<String, Object>) seriesMap.get(box_type);
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);

				dataList.add(entity.getBoxNum());
			}
		}

		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}

		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}

	/**
	 * 放入各机构箱袋状态和对应的数量的数据
	 * 
	 * @author wh
	 * @param stoBoxInfoGraphEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> makeBoxStatusGraphData(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {

		Map<String, Object> rtnMap = Maps.newHashMap();

		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		
		List<StoBoxInfoGraphEntity> resultList = findBoxStatusGraph(stoBoxInfoGraphEntity);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (StoBoxInfoGraphEntity entity : resultList) {
			// 放入legend配置项数据

			String box_status = DictUtils.getDictLabel(entity.getBoxStatus(), "sto_box_status", "");

			if (!legendDataList.contains(box_status)) {
				legendDataList.add(box_status);
			}

			// 放入x轴数据
			if (!xAxisDataList.contains(entity.getOfficeName())) {
				xAxisDataList.add(entity.getOfficeName());
			}

			// 按类别放入货物数据
			if (!seriesMap.containsKey(box_status)) {

				List<String> dataList = Lists.newArrayList();

				dataList.add(entity.getBoxNum());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, box_status);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, Global.getConfig("report.bar.all"));
				map.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.width"));
				seriesMap.put(box_status, map);
			} else {

				Map<String, Object> map = (Map<String, Object>) seriesMap.get(box_status);
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);

				dataList.add(entity.getBoxNum());
			}
		}

		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}

		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}

	/**
	 * 箱袋统计图导出
	 * 
	 * @param stoBoxInfoGraphEntity
	 * @param request
	 * @param response
	 * @param msg
	 */
	public void exportBoxGraph(StoBoxInfoGraphEntity stoBoxInfoGraphEntity, HttpServletRequest request,
			HttpServletResponse response, MessageSource msg) {

		// 设置筛选条件
		String selectOfficeName = "";

		if (StringUtils.isNotBlank(stoBoxInfoGraphEntity.getOffice().getId())) {
			selectOfficeName = stoBoxInfoGraphEntity.getOffice().getName();
		} else {
			selectOfficeName = Global.getConfig("report.select.office");
		}
		// 导出excel
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Locale locale = LocaleContextHolder.getLocale();

		// 箱袋库存统计图数据查询
		List<StoBoxInfoGraphEntity> resultList1 = findBoxNumGraph(stoBoxInfoGraphEntity);

		// 遍历resultList1 给 StoBoxInfoGraphEntity的boxTypeName属性赋值
		for (StoBoxInfoGraphEntity entity : resultList1) {
			entity.setBoxTypeName(DictUtils.getDictLabel(entity.getBoxType(), "sto_box_type", ""));
		}

		// 箱袋状态统计图数据查询
		List<StoBoxInfoGraphEntity> resultList2 = findBoxStatusGraph(stoBoxInfoGraphEntity);

		// 遍历resultList2 给 StoBoxInfoGraphEntity的boxStatusName属性赋值
		for (StoBoxInfoGraphEntity entity : resultList2) {
			entity.setBoxStatusName(DictUtils.getDictLabel(entity.getBoxStatus(), "sto_box_status", ""));
		}

		// 获取制表机构
		String officeName = UserUtils.getUser().getOffice().getName();

		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");

		// 取得模板名称
		String fileName = msg.getMessage("report.box.excel", null, locale);

		// sheet1
		Map<String, Object> sheet1Map = Maps.newHashMap();
		sheet1Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("report.box.excel.type", null, locale));

		Map<String, Object> sheet1TitleMap = Maps.newHashMap();

		sheet1TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
				msg.getMessage("report.box.excel.type", null, locale));

		sheet1TitleMap.put(ReportConstant.ReportExportData.OFFICE, selectOfficeName);

		// 制表机构
		sheet1TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);

		// 制表时间
		sheet1TitleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTime());

		sheet1Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, resultList1);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, StoBoxInfoGraphEntity.class.getName());

		paramList.add(sheet1Map);

		// sheet2
		Map<String, Object> sheet2Map = Maps.newHashMap();
		sheet2Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("report.box.excel.status", null, locale));

		Map<String, Object> sheet2TitleMap = Maps.newHashMap();

		sheet2TitleMap.put(ReportConstant.ReportExportData.TOP_TITLE,
				msg.getMessage("report.box.excel.status", null, locale));

		// 制表机构
		sheet2TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);

		// 制表时间
		sheet2TitleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTime());

		sheet2TitleMap.put(ReportConstant.ReportExportData.OFFICE, selectOfficeName);

		sheet2Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet2TitleMap);
		sheet2Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, resultList2);
		sheet2Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, StoBoxInfoGraphEntity.class.getName());

		paramList.add(sheet2Map);

		// 导出
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}
}
