package com.coffer.businesses.modules.report.v01.service;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.report.v01.dao.AllocateReportBusinessDao;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessCount;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessDegree;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessMoneyAmount;
import com.coffer.businesses.modules.report.v01.entity.ReportCondition;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 网点现金上缴物品及金额报表
 * 
 * @author xp
 * @version 2017-8-24
 */
@Service
@Transactional(readOnly = true)
public class AllocateReportBusinessService extends BaseService {
	@Autowired
	private AllocateReportBusinessDao allocateReportBusinessDao;

	@Autowired
	private MessageSource msg;
	/**
	 * 网点现金上缴报表 图形化过滤:业务量及总金额
	 * 
	 * @author xp
	 * @version 2017-8-24
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessDegree> findDegree(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findDegree(reportCondition);
	}
	
	/**
	 * 网点现金下拨报表 图形化过滤:每个时间段业务量
	 * 
	 * @author SongYuanYang
	 * @version 2017-9-4
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessDegree> findByAllBusiness(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findByAllBusiness(reportCondition);
	}
	
	/**
	 * 网点现金下拨报表 图形化过滤:每个时间段业务量
	 * 
	 * @author SongYuanYang
	 * @version 2017-9-4
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessDegree> findByAllBusinessFromTemp(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findByAllBusinessFromTemp(reportCondition);
	}

	/**
	 * 网点现金下拨报表 图形化过滤:业务量及总金额
	 * 
	 * @author SongYuanYang
	 * @version 2017-8-31
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessDegree> findByAllocate(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findByAllocate(reportCondition);
	}
	
	/**
	 * 网点现金下拨报表 图形化过滤:临时金额
	 * 
	 * @author SongYuanYang
	 * @version 2017-9-21
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessDegree> findByAllocateFromTemp(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findByAllocateFromTemp(reportCondition);
	}

	/**
	 * 网点现金上缴报表 图形化过滤:总金额
	 * 
	 * @author xp
	 * @version 2017-8-24
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessCount> findCount(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findCount(reportCondition);
	}

	/**
	 * 网点现金上缴报表 图形化过滤:物品及物品金额
	 * 
	 * @author xp
	 * @version 2017-8-24
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessMoneyAmount> findGoods(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findGoods(reportCondition);
	}

	/**
	 * 网点现金上缴报表 图形化过滤:业务量
	 * 
	 * @author xp
	 * @version 2017-8-24
	 */
	@SuppressWarnings(value = "unchecked")
	public Map<String, Object> graphicalHandInList(List<AllocateReportBusinessDegree> handIn) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (AllocateReportBusinessDegree entity : handIn) {
			// 获取机构
			if (!legendDataList.contains(entity.getROfficeName())) {
				legendDataList.add(entity.getROfficeName());
			}
			// 获取上缴日期作为x轴坐标
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			// 获取图形内容
			if (!seriesMap.containsKey(entity.getROfficeName())) {
				String name = entity.getROfficeName();
				String type = ReportGraphConstant.GraphType.LINE;
				List<String> dataList = Lists.newArrayList();
				dataList.add(entity.getDegree());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, name);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, type);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_SMOOTH_KEY, true);
				seriesMap.put(entity.getROfficeName(), map);
			} else {
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getROfficeName());
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				dataList.add(entity.getDegree());
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
	 * 网点现金上缴报表 图形化过滤:总金额
	 * 
	 * @author xp
	 * @version 2017-8-24
	 */
	@SuppressWarnings(value = "unchecked")
	public Map<String, Object> graphicalHistogram(List<AllocateReportBusinessCount> handIn) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (AllocateReportBusinessCount entity : handIn) {
			if (!legendDataList.contains(entity.getROfficeName())) {
				legendDataList.add(entity.getROfficeName());
			}
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			if (!seriesMap.containsKey(entity.getROfficeName())) {
				String name = entity.getROfficeName();
				String type = ReportGraphConstant.GraphType.BAR;
				String stack = ReportGraphConstant.SeriesClass.STACK;
				List<String> dataList = Lists.newArrayList();
				dataList.add(entity.getCount());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, name);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, type);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, stack);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.width"));
				seriesMap.put(entity.getROfficeName(), map);
			} else {
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getROfficeName());
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				dataList.add(entity.getCount());
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
	 * 网点现金上缴报表 图形化过滤:物品及物品金额
	 * 
	 * @author xp
	 * @version 2017-8-24
	 */
	@SuppressWarnings(value = "unchecked")
	public Map<String, Object> graphicalGoods(List<AllocateReportBusinessMoneyAmount> handIn) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (AllocateReportBusinessMoneyAmount entity : handIn) {
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			if (!legendDataList.contains(entity.getGoodsName())) {
				legendDataList.add(entity.getGoodsName());
			}
			if (!seriesMap.containsKey(entity.getGoodsName())) {
				String name = entity.getGoodsName();
				String type = ReportGraphConstant.GraphType.BAR;
				String stack = ReportGraphConstant.SeriesClass.STACK;
				List<String> dataList = Lists.newArrayList();
				dataList.add(entity.getMoneyAmount());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, name);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, type);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, stack);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.width"));
				seriesMap.put(entity.getGoodsName(), map);
			} else {
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getGoodsName());
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				dataList.add(entity.getMoneyAmount());
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
	 * 现金下拨:折线图数据过滤
	 * 
	 * @author SongYuanYang
	 * @version 2017年8月31日
	 * 
	 * @param handIn
	 *            主表list
	 */
	public Map<String, Object> graphicalFoldLine(List<AllocateReportBusinessDegree> handIn) {
		// 初始化
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		for (AllocateReportBusinessDegree entity : handIn) {
			// 提示框List中添加机构
			if (!legendDataList.contains(entity.getROfficeName())) {
				legendDataList.add(entity.getROfficeName());
			}
			// x轴中添加时间
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			// 添加数据，设定属性
			if (!seriesMap.containsKey(entity.getROfficeName())) {
				List<String> dataList = Lists.newArrayList();
				dataList.add(entity.getDegree());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, entity.getROfficeName());
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.LINE);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_SMOOTH_KEY, true);
				seriesMap.put(entity.getROfficeName(), map);
			} else {
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getROfficeName());
				@SuppressWarnings("unchecked")
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				dataList.add(entity.getDegree());
			}
		}
		// 循环加入数据
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
	 * 现金下拨:堆叠柱状图数据过滤
	 * 
	 * @author SongYuanYang
	 * @version 2017年9月1日
	 * 
	 * @param latticePointHandin
	 *            主表数据
	 */
	public Map<String, Object> graphicalStackColumn(AllocateReportBusinessDegree latticePointHandin) {
		// 初始化
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, Object> seriesMap = Maps.newHashMap();
		Map<String, Object> seriesMapTemp = Maps.newHashMap();
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		List<String> dataList = Lists.newArrayList();
		List<String> dataListTemp = Lists.newArrayList();
		for (AllocateReportBusinessDegree entity : allocateReportBusinessDao.findByAllBusiness(latticePointHandin)) {
			// x轴中添加时间
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			// 添加数据
			dataList.add(entity.getDegree());
		}

		for (AllocateReportBusinessDegree entity : allocateReportBusinessDao
				.findByAllBusinessFromTemp(latticePointHandin)) {
			// x轴中添加时间
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			// 添加临时数据，设定类型
			dataListTemp.add(entity.getDegree());
		}
		// 设置常规业务数据属性
		seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
		seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				ReportGraphConstant.StackColumnLegend.ROUTINE_LINE);
		seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
		seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_COORDINATE_SYSTEM_KEY,
				ReportGraphConstant.GraphType.POLAR);
		seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY,
				ReportGraphConstant.StackColumnLegend.ATTRIBUTE_A);
		legendDataList.add(ReportGraphConstant.StackColumnLegend.ROUTINE_LINE);
		seriesDataList.add(seriesMap);
		// 设置临时业务数据属性
		seriesMapTemp.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataListTemp);
		seriesMapTemp.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				ReportGraphConstant.StackColumnLegend.TEMP_LINE);
		seriesMapTemp.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
		seriesMapTemp.put(ReportGraphConstant.SeriesProperties.SERIES_COORDINATE_SYSTEM_KEY,
				ReportGraphConstant.GraphType.POLAR);
		seriesMapTemp.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY,
				ReportGraphConstant.StackColumnLegend.ATTRIBUTE_A);
		legendDataList.add(ReportGraphConstant.StackColumnLegend.TEMP_LINE);
		seriesDataList.add(seriesMapTemp);

		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}

	/**
	 * 现金下拨:柱状图金额常规数据过滤
	 * 
	 * @author SongYuanYang
	 * @version 2017年8月31日
	 * 
	 * @param handIn
	 *            主表list
	 */
	public Map<String, Object> graphicalColumn(List<AllocateReportBusinessDegree> handIn) {
		// 初始化
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();

		for (AllocateReportBusinessDegree entity : handIn) {
			// 提示框List中添加机构
			if (!legendDataList.contains(entity.getROfficeName())) {
				legendDataList.add(entity.getROfficeName());
			}
			// x轴中添加过滤条件
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			// 添加数据，设定类型
			if (!seriesMap.containsKey(entity.getROfficeName())) {
				List<String> dataList = Lists.newArrayList();
				dataList.add(entity.getCount());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, entity.getROfficeName());
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, ReportGraphConstant.SeriesClass.STACK);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.widthSmall"));
				seriesMap.put(entity.getROfficeName(), map);
			} else {
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getROfficeName());
				@SuppressWarnings("unchecked")
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				dataList.add(entity.getCount());
			}

		}
		// 循环加入数据
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
	 * 现金下拨:柱状图金额临时数据过滤
	 * 
	 * @author SongYuanYang
	 * @version 2017年9月21日
	 * 
	 * @param handIn
	 *            主表list
	 */
	public Map<String, Object> graphicalColumnTemp(List<AllocateReportBusinessDegree> handIn) {
		// 初始化
		Map<String, Object> rtnMap = Maps.newHashMap();
		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();

		for (AllocateReportBusinessDegree entity : handIn) {
			// 提示框List中添加机构
			if (!legendDataList.contains(entity.getROfficeName())) {
				legendDataList.add(entity.getROfficeName());
			}
			// x轴中添加过滤条件
			if (!xAxisDataList.contains(entity.getHandInDate())) {
				xAxisDataList.add(entity.getHandInDate());
			}
			// 添加数据，设定类型
			if (!seriesMap.containsKey(entity.getROfficeName())) {
				List<String> dataList = Lists.newArrayList();
				dataList.add(entity.getCount());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, entity.getROfficeName());
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, ReportGraphConstant.SeriesClass.STACK);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.widthSmall"));
				seriesMap.put(entity.getROfficeName(), map);
			} else {
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getROfficeName());
				@SuppressWarnings("unchecked")
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				dataList.add(entity.getCount());
			}

		}
		// 循环加入数据
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
	 * 查询所有调拨业务的金额（首页显示图表）
	 * 
	 * @author xp
	 * @param reportCondition
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AllocateReportBusinessCount> findBusinessCount(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findBusinessCount(reportCondition);
	}

	/**
	 * 首页显示现金业务金额图形化过滤
	 * 
	 * @author xp 2017-10-20
	 * @param allBusiness
	 * @return
	 */
	@SuppressWarnings(value = "unchecked")
	public Map<String, Object> graphicalBusinessCount(List<AllocateReportBusinessCount> allBusiness) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		//设置时间轴数据
		List<String> timeLineDataList = Lists.newArrayList();
		// 设置图例数据
		List<String> legendDataList = Lists.newArrayList();
		// 设置X轴数据
		List<String> xAxisDataList = Lists.newArrayList();
		// 图表类型配置
		Map<String, Map<String, Object>> tableSeriesMap = Maps.newHashMap();
		// 设置基本选项内容数据
		List<Map<String, Object>> baseOptionSeriesDataList = Lists.newArrayList();
		//基本选项饼图配置
		Map<String, Object> pieMap = Maps.newHashMap();
		// 图形数据配置项(最后返回的集合)
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		// 图形内容配置(柱状图、饼图)
		Map<String, Map<String, Object>> barOptionMap = Maps.newLinkedHashMap();
		Map<String, Map<String, Object>> pieOptionMap = Maps.newHashMap();

		// 设置业务名称
		String businessName = null;
		//语言环境
		Locale locale = LocaleContextHolder.getLocale();

		// 获取基本设置表类型配置内容(饼图)
		pieMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, "各业务金额所占百分比");
		pieMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.PIE);
		List<String> location = Lists.newArrayList();
		location.add("75%");//横坐标
		location.add("35%");//纵坐标
		pieMap.put(ReportGraphConstant.SeriesProperties.CENTER, location);
		pieMap.put(ReportGraphConstant.SeriesProperties.RADIUS, "28%");
		baseOptionSeriesDataList.add(pieMap);

		// 获取业务名称作为x轴坐标
		String officeName = UserUtils.getUser().getOffice().getName();
		xAxisDataList.add(officeName);

		// 过滤现金业务（饼图）
		for (AllocateReportBusinessCount entity : allBusiness) {
			// 获取机构
			if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(entity.getBusinessType())) {
				// 设置业务名称为现金上缴
				businessName = msg.getMessage("allocation.cash.handin.firstpage.graph", null, locale);
			} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())) {
				// 如果是常规业务
				if (AllocationConstant.AllocateType.NORMAL_BUSINESS.equals(entity.getType())) {
					// 设置业务名称为常规预约
					businessName = msg.getMessage("allocation.cash.order.firstpage.graph", null, locale);
				} else {
					// 设置业务名称为临时预约
					businessName = msg.getMessage("allocation.cash.temporder.firstpage.graph", null, locale);
				}
			}
			//设置时间轴数据
			if(!timeLineDataList.contains(entity.getHandInDate())){
				timeLineDataList.add(entity.getHandInDate());
			}
			// 设置图例
			if (!legendDataList.contains(businessName)) {
				legendDataList.add(businessName);
			}
			// 获取表内容类型（饼图）
			if (!tableSeriesMap.containsKey(businessName)) {
				// 设置图形内容名称，形状
				Map<String, Object> baseOptionMap = Maps.newHashMap();
				baseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				baseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY,
						ReportGraphConstant.GraphType.BAR);
				baseOptionSeriesDataList.add(baseOptionMap);
				tableSeriesMap.put(businessName, baseOptionMap);
			 }
			// 图形内容配置项(饼图)
			if (!pieOptionMap.containsKey(entity.getHandInDate())) {
				// 饼图
				Map<String, Object> amountMap = Maps.newHashMap();
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, entity.getCount());
				List<Map<String, Object>> pieDataList = Lists.newArrayList();
				pieDataList.add(amountMap);
				Map<String, Object> pieDataMap = Maps.newHashMap();
				pieDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, pieDataList);
				pieOptionMap.put(entity.getHandInDate(), pieDataMap);
			} else {
				// 饼图
				Map<String, Object> amountMap = Maps.newHashMap();
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, entity.getCount());
				List<Map<String, Object>> pieDataList = (List<Map<String, Object>>) pieOptionMap
						.get(entity.getHandInDate()).get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				pieDataList.add(amountMap);
			}
		}
		// 过滤图形数据（柱状图）
		for (AllocateReportBusinessCount entity : allBusiness) {
			// 获取机构
			if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(entity.getBusinessType())) {
				// 设置业务名称为现金上缴
				businessName = msg.getMessage("allocation.cash.handin.firstpage.graph", null, locale);
			} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())) {
				// 如果是常规业务
				if (AllocationConstant.AllocateType.NORMAL_BUSINESS.equals(entity.getType())) {
					// 设置业务名称为现金预约
					businessName = msg.getMessage("allocation.cash.order.firstpage.graph", null, locale);
				} else {
					// 设置业务名称为临时现金预约
					businessName = msg.getMessage("allocation.cash.temporder.firstpage.graph", null, locale);
				}
			}
			// 图形内容配置项(柱状图)
			if (!barOptionMap.containsKey(entity.getHandInDate())) {
				// 柱状图
				Map<String, Object> seriesMap = Maps.newHashMap();
				Map<String, Object> titleMap = Maps.newHashMap();
				List<Map<String, Object>> amountList = Lists.newArrayList();
				Map<String, Object> pieList = pieOptionMap.get(entity.getHandInDate());
				// 各业务的总金额
				Map<String, Object> amountMap = Maps.newHashMap();
				Map<String, Object> barDataMap = Maps.newHashMap();
				String title = msg.getMessage("allocation.cash.firstpage.graph", new String[] {entity.getHandInDate()}, locale);
				titleMap.put(ReportGraphConstant.SeriesProperties.TEXT_KEY,  title);
				seriesMap.put(ReportGraphConstant.SeriesProperties.TITLE_KEY, titleMap);
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, entity.getCount());
				amountList.add(amountMap);
				barDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, amountList);
				// 将对应的饼图数据放入
				List<Map<String, Object>> dataList = Lists.newArrayList();
				dataList.add(pieList);
				dataList.add(barDataMap);
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES, dataList);
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY, Global.getConfig("report.bar.widthSmall"));
				barOptionMap.put(entity.getHandInDate(), seriesMap);
			} else {
				// 柱状图
				Map<String, Object> amountMap = Maps.newHashMap();
				Map<String, Object> barDataMap = Maps.newHashMap();
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, entity.getCount());
				List<Map<String, Object>> amountList = Lists.newArrayList();
				amountList.add(amountMap);
				barDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, amountList);
				List<Map<String, Object>> dataList = (List<Map<String, Object>>) barOptionMap
						.get(entity.getHandInDate())
						.get(ReportGraphConstant.SeriesProperties.SERIES);
				dataList.add(barDataMap);
			}
		}
		// 图形过滤
		Iterator<String> barIterator = barOptionMap.keySet().iterator();
		while (barIterator.hasNext()) {
			seriesDataList.add(barOptionMap.get(barIterator.next()));
		}
		// 返回
		rtnMap.put(ReportGraphConstant.DataGraphList.TIMELINE_DATA_LIST, timeLineDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.BASEOPTION_SERIES_DATA_LIST, baseOptionSeriesDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}
	
	/**
	 * 首页显示登录用户所属机构下各常规业务及临时业务的状态
	 * 
	 * @author xp
	 * @param reportCondition
	 * @return json
	 */
	public List<AllocateReportBusinessDegree> findBusinessStatus(ReportCondition reportCondition) {
		return allocateReportBusinessDao.findBusinessStatus(reportCondition);
	}

	/**
	 * 首页显示登录用户所属机构下各常规业务及临时业务的状态的图形化过滤
	 * 
	 * @author xp
	 * @version 2017-11-6
	 * @param allBusinessStatus
	 * @return
	 */
	public Map<String, Object> graphicalBusinessStatus(List<AllocateReportBusinessDegree> statusList) {
		// 设置返回参数
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 设置图例数据
		List<String> legendDataList = Lists.newArrayList();
		// 设置雷达图坐标数据
		List<Map<String, Object>> radarDataList = Lists.newArrayList();
		// 设置雷达图坐标组件内容
		Map<String, Map<String, Object>> radarMap = Maps.newLinkedHashMap();
		// 设置系列数据列表
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		// 设置系列数据
		Map<String, Map<String, Object>> seriesMap = Maps.newLinkedHashMap();
		// 设置业务名称
		String businessName = null;
		// 设置业务状态名称
		String status = null;
		// 语言环境
		Locale locale = LocaleContextHolder.getLocale();
		//图形系列数据
		List<String> handInList = Lists.newArrayList();
		List<String> handOutList = Lists.newArrayList();
		List<String> tempHandOutList = Lists.newArrayList();
		String[] handInStatusArray = Global.getStringArray("firstPage.handIn.status");
		String[] handOutStatusArray = Global.getStringArray("firstPage.handOut.status");
		String[] tempHandOutStatusArray = Global.getStringArray("firstPage.tempHandOut.status");
		// 设置系列数据//TODO
		// 设置上缴业务数据
		for (String statuscount : handInStatusArray) {
			boolean boo = false;
			for (AllocateReportBusinessDegree entity : statusList) {
				// 如果是现金上缴的业务
				if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(entity.getBusinessType())) {
					if (statuscount.equals(entity.getStatus())) {
						boo = false;
						handInList.add(entity.getDegree());
						break;
					} else {
						boo = true;
					}
				}
			}
			if (boo) {
				handInList.add("0");
			}
		}
		for (String statuscount : handOutStatusArray) {
			boolean boo = false;
			for (AllocateReportBusinessDegree entity : statusList) {
				// 如果是现金
				if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())
						&& AllocationConstant.AllocateType.NORMAL_BUSINESS.equals(entity.getType())) {
					if (statuscount.equals(entity.getStatus())) {
						boo = false;
						handOutList.add(entity.getDegree());
						break;
					} else {
						boo = true;
					}
				}
			}
			if (boo) {
				handOutList.add("0");
			}
		}
		for (String statuscount : tempHandOutStatusArray) {
			boolean boo = false;
			for (AllocateReportBusinessDegree entity : statusList) {
				if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())
						&& AllocationConstant.AllocateType.TEMP_BUSINESS.equals(entity.getType())) {
					if (statuscount.equals(entity.getStatus())) {
						boo = false;
						tempHandOutList.add(entity.getDegree());
						break;
					} else {
						boo = true;
					}
				}
			}
			if (boo) {
				tempHandOutList.add("0");
			}
		}

		// 过滤数据
		for (AllocateReportBusinessDegree entity : statusList) {
			// 获取legend名称
			if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(entity.getBusinessType())) {
				// 设置业务名称为现金上缴
				businessName = msg.getMessage("allocation.cash.handin.firstpage.graph", null, locale);
			} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())) {
				// 如果是常规业务
				if (AllocationConstant.AllocateType.NORMAL_BUSINESS.equals(entity.getType())) {
					// 设置业务名称为常规预约
					businessName = msg.getMessage("allocation.cash.order.firstpage.graph", null, locale);
				} else {
					// 设置业务名称为临时预约
					businessName = msg.getMessage("allocation.cash.temporder.firstpage.graph", null, locale);
				}
			}
			// 设置雷达图指示器内容
			List<Map<String, Object>> indicatorList = Lists.newArrayList();
			Map<String, Object> indicatorMap = Maps.newLinkedHashMap();
			// 获取业务状态名称
			status = DictUtils.getDictLabel(entity.getStatus(),"all_status","");
			// 设置图例内容
			if (!legendDataList.contains(businessName)) {
				legendDataList.add(businessName);
			}
			// 过滤雷达图指示器内容
			String[] statusArray = null;
			if (!radarMap.containsKey(businessName)) {
				// 设置图的位置
				List<String> location = Lists.newArrayList();
				if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(entity.getBusinessType())) {
					// 如果业务类型为上缴，取出对应的配置参数
					statusArray = Global.getStringArray("firstPage.handIn.status");
					location.add("20%");// 横坐标
					location.add("50%");// 纵坐标
				} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())
						&& AllocationConstant.AllocateType.NORMAL_BUSINESS.equals(entity.getType())) {
					// 如果业务类型为常规预约，取出对应的配置参数
					statusArray = Global.getStringArray("firstPage.handOut.status");
					location.add("50%");// 横坐标
					location.add("50%");// 纵坐标
				} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())
						&& AllocationConstant.AllocateType.TEMP_BUSINESS.equals(entity.getType())) {
					// 如果业务类型为临时预约，取出对应的配置参数
					statusArray = Global.getStringArray("firstPage.tempHandOut.status");
					location.add("80%");// 横坐标
					location.add("50%");// 纵坐标
				}
				// 将业务状态名称放入
				for (String businessStatus : statusArray) {
					Map<String, Object> map = Maps.newLinkedHashMap();
					// 业务状态名称
					String statusName = DictUtils.getDictLabel(businessStatus, "all_status", "");
					// 雷达图最大值设置
					String max = Global.getConfig("firstPage.randar.max");
					map.put(ReportGraphConstant.SeriesProperties.TEXT_KEY, statusName);
					map.put(ReportGraphConstant.SeriesProperties.MAX_KEY, max);
					// 放入坐标值指示器集合
					indicatorList.add(map);
				}
				indicatorMap.put(ReportGraphConstant.SeriesProperties.INDICATOR_KEY, indicatorList);
				indicatorMap.put(ReportGraphConstant.SeriesProperties.CENTER, location);
				indicatorMap.put(ReportGraphConstant.SeriesProperties.RADIUS, "80");
				radarMap.put(businessName, indicatorMap);
			}
			// 过滤图形系列内容
			if (!seriesMap.containsKey(businessName)) {
				Map<String, Object> map = Maps.newLinkedHashMap();
				Map<String, Object> tooltipMap = Maps.newHashMap();
				Map<String, Object> itemMap = Maps.newHashMap();
				// 基本设置
				// 区域填充样式
				Map<String, Map<String, Object>> areaStyleMap = Maps.newHashMap();
				Map<String, Object> typeMap = Maps.newHashMap();
				// 类型为雷达图
				tooltipMap.put(ReportGraphConstant.SeriesProperties.TRIGGER_KEY,
						ReportGraphConstant.SeriesProperties.ITEM_KEY);
				typeMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY,
						ReportGraphConstant.SeriesProperties.DEFAULT_KEY);
				areaStyleMap.put(ReportGraphConstant.SeriesProperties.AREASTYLE_KEY, typeMap);
				itemMap.put(ReportGraphConstant.SeriesProperties.NORMAL_KEY, areaStyleMap);
				// 数据设置
				List<Map<String, Object>> dataList = Lists.newArrayList();
				Map<String, Object> dataMap = Maps.newLinkedHashMap();
				dataMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(entity.getBusinessType())) {
					//如果是上缴业务
					dataMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, handInList);
				} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(entity.getBusinessType())) {
					// 如果是预约业务
					if (AllocationConstant.AllocateType.NORMAL_BUSINESS.equals(entity.getType())) {
						// 如果是常规预约业务
						dataMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, handOutList);
						map.put(ReportGraphConstant.DataGraphList.RADARINDEX_LIST, '1');
					} else {
						// 如果是临时预约业务
						dataMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, tempHandOutList);
						map.put(ReportGraphConstant.DataGraphList.RADARINDEX_LIST, '2');
					}
				}
				dataList.add(dataMap);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.RANDAR);
				map.put(ReportGraphConstant.SeriesProperties.TOOLTIP_KEY, tooltipMap);
				map.put(ReportGraphConstant.SeriesProperties.ITEMSTYLE_KEY, itemMap);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				seriesMap.put(businessName, map);
			}
		}
		// 循环雷达坐标指示器，放入列表
		Iterator<String> radarIterator = radarMap.keySet().iterator();

		while (radarIterator.hasNext()) {
			radarDataList.add(radarMap.get(radarIterator.next()));
		}
		// 循环系列数据放入列表
		Iterator<String> seriesIterator = seriesMap.keySet().iterator();

		while (seriesIterator.hasNext()) {
			seriesDataList.add(seriesMap.get(seriesIterator.next()));
		}
		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.RADAR_DATALIST, radarDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}
}
