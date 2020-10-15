package com.coffer.businesses.modules.report.v01.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.OrderClearMainDao;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.report.ReportGraphConstant.DataGraphList;
import com.coffer.businesses.modules.report.ReportGraphConstant.GraphType;
import com.coffer.businesses.modules.report.ReportGraphConstant.SeriesProperties;
import com.coffer.businesses.modules.report.v01.dao.ClearReportDao;
import com.coffer.businesses.modules.report.v01.entity.ClearReportAmount;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.StringUtils;
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
public class ClearReportService extends BaseService {
	@Autowired
	private ClearReportDao clearReportDao;

	@Autowired
	private OrderClearMainDao orderClearMainDao;

	@Autowired
	private MessageSource msg;

	public static final String reserve = "预约";
	public static final String accept = "接收";
	public static final String reserveAmount = "预约金额";
	public static final String acceptAmount = "接收金额";
	public static final String reserveVolume = "预约量";
	public static final String acceptVolume = "接收量";

	/**
	 * 清分业务根据用户所属机构显示对应的出入库金额（首页）
	 * 
	 * @author xp
	 * @version 2017-11-16
	 * @param clearReportAmount
	 * @return List
	 */
	@Transactional(readOnly = true)
	public List<ClearReportAmount> findInOrOutAmount(ClearReportAmount clearReportAmount) {
		return clearReportDao.findInOrOutAmount(clearReportAmount);
	}

	/**
	 * 清分出入库金额图形化过滤
	 * 
	 * @version 2017-11-16
	 * @author xp
	 * @param clearReportAmountList
	 * @return map
	 */
	@SuppressWarnings(value = "unchecked")
	public Map<String, Object> inOrOutAmount(List<ClearReportAmount> clearReportAmountList) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 设置时间轴数据
		List<String> timeLineDataList = Lists.newArrayList();
		// 设置图例数据
		List<String> legendDataList = Lists.newArrayList();
		// 设置X轴数据
		List<String> xAxisDataList = Lists.newArrayList();
		// 图表类型配置
		Map<String, Map<String, Object>> tableSeriesMap = Maps.newHashMap();
		// 设置基本选项内容数据
		List<Map<String, Object>> baseOptionSeriesDataList = Lists.newArrayList();
		// 基本选项饼图配置
		Map<String, Object> pieMap = Maps.newHashMap();
		// 图形数据配置项(最后返回的集合)
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		// 图形内容配置(柱状图、饼图)
		Map<String, Map<String, Object>> barOptionMap = Maps.newLinkedHashMap();
		Map<String, Map<String, Object>> pieOptionMap = Maps.newHashMap();
		// 提示框组件
		Map<String, Object> tooltipMap = Maps.newHashMap();
		// 指示器配置项
		Map<String, String> axisPointer = Maps.newHashMap();
		// 设置业务名称
		String businessName = null;
		String stack = null;
		// 语言环境
		Locale locale = LocaleContextHolder.getLocale();

		// 获取业务名称作为x轴坐标
		String officeName = UserUtils.getUser().getOffice().getName();
		xAxisDataList.add(officeName);
		/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
		DecimalFormat d1 = new DecimalFormat("##0.00#");
		/* end */
		// 过滤现金业务（饼图）
		for (ClearReportAmount entity : clearReportAmountList) {
			// 设置业务类型
			if (ClearConstant.BusinessType.BANK_PAY.equals(entity.getBusType())) {
				// 设置业务名称为商行交款
				businessName = msg.getMessage("clear.businessType.bankPay", null, locale);
				stack = msg.getMessage("clear.report.in", null, locale);
			} else if (ClearConstant.BusinessType.BANK_GET.equals(entity.getBusType())) {
				// 设置业务名称为商行取款
				businessName = msg.getMessage("clear.businessType.bankGet", null, locale);
				stack = msg.getMessage("clear.report.out", null, locale);
			} else if (ClearConstant.BusinessType.AGENCY_PAY.equals(entity.getBusType())) {
				// 设置业务名称为代理上缴
				businessName = msg.getMessage("clear.businessType.agencyPay", null, locale);
				stack = msg.getMessage("clear.report.out", null, locale);
			} else if (ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN.equals(entity.getBusType())) {
				// 设置业务名称为从人民银行复点入库
				businessName = msg.getMessage("clear.businessType.peoplebankComplexPointIn", null, locale);
				stack = msg.getMessage("clear.report.out", null, locale);
			} else if (ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT.equals(entity.getBusType())) {
				// 设置业务名称为从人民银行复点出库
				businessName = msg.getMessage("clear.businessType.peoplebankComplexPointOut", null, locale);
				stack = msg.getMessage("clear.report.in", null, locale);
			}
			// 配置指示器
			axisPointer.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY,
					ReportGraphConstant.SeriesProperties.SHADOW_KEY);
			tooltipMap.put(ReportGraphConstant.SeriesProperties.TRIGGER_KEY,
					ReportGraphConstant.SeriesProperties.AXIS_KEY);
			tooltipMap.put(ReportGraphConstant.SeriesProperties.AXISPOINTER_KEY, axisPointer);
			// 设置时间轴数据
			if (!timeLineDataList.contains(entity.getDate())) {
				timeLineDataList.add(entity.getDate());
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
				baseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, stack);
				baseOptionSeriesDataList.add(baseOptionMap);
				tableSeriesMap.put(businessName, baseOptionMap);
			}
			// 图形内容配置项(饼图)
			if (!pieOptionMap.containsKey(entity.getDate())) {
				// 饼图
				Map<String, Object> amountMap = Maps.newHashMap();
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
						d1.format(new BigDecimal(entity.getAmount())).equals("0.00") ? null
								: d1.format(new BigDecimal(entity.getAmount())));
				/* end */
				List<Map<String, Object>> pieDataList = Lists.newArrayList();
				pieDataList.add(amountMap);
				Map<String, Object> pieDataMap = Maps.newHashMap();
				pieDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, pieDataList);
				pieOptionMap.put(entity.getDate(), pieDataMap);
			} else {
				// 饼图
				Map<String, Object> amountMap = Maps.newHashMap();
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
						d1.format(new BigDecimal(entity.getAmount())).equals("0.00") ? null
								: d1.format(new BigDecimal(entity.getAmount())));
				/* end */
				List<Map<String, Object>> pieDataList = (List<Map<String, Object>>) pieOptionMap.get(entity.getDate())
						.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				pieDataList.add(amountMap);
			}
		}
		// 过滤图形数据（柱状图）
		for (ClearReportAmount entity : clearReportAmountList) {
			// 设置业务类型
			if (ClearConstant.BusinessType.BANK_PAY.equals(entity.getBusType())) {
				// 设置业务名称为商行交款
				businessName = msg.getMessage("clear.businessType.bankPay", null, locale);
			} else if (ClearConstant.BusinessType.BANK_GET.equals(entity.getBusType())) {
				// 设置业务名称为商行取款
				businessName = msg.getMessage("clear.businessType.bankGet", null, locale);
			} else if (ClearConstant.BusinessType.AGENCY_PAY.equals(entity.getBusType())) {
				// 设置业务名称为代理上缴
				businessName = msg.getMessage("clear.businessType.agencyPay", null, locale);
			} else if (ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN.equals(entity.getBusType())) {
				// 设置业务名称为从人民银行复点入库
				businessName = msg.getMessage("clear.businessType.peoplebankComplexPointIn", null, locale);
			} else if (ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT.equals(entity.getBusType())) {
				// 设置业务名称为从人民银行复点出库
				businessName = msg.getMessage("clear.businessType.peoplebankComplexPointOut", null, locale);
			}
			// 图形内容配置项(柱状图)
			if (!barOptionMap.containsKey(entity.getDate())) {
				// 柱状图
				Map<String, Object> seriesMap = Maps.newHashMap();
				Map<String, Object> titleMap = Maps.newHashMap();
				List<Map<String, Object>> amountList = Lists.newArrayList();
				Map<String, Object> pieList = pieOptionMap.get(entity.getDate());
				// 各业务的总金额
				Map<String, Object> amountMap = Maps.newHashMap();
				Map<String, Object> barDataMap = Maps.newHashMap();
				String title = entity.getDate() + msg.getMessage("clear.report.inOutAmount.title",
						new String[] { entity.getHandInDate() }, locale);
				titleMap.put(ReportGraphConstant.SeriesProperties.TEXT_KEY, title);
				seriesMap.put(ReportGraphConstant.SeriesProperties.TITLE_KEY, titleMap);
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
						d1.format(new BigDecimal(entity.getAmount())).equals("0.00") ? null
								: d1.format(new BigDecimal(entity.getAmount())));
				/* end */
				amountList.add(amountMap);
				barDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, amountList);
				// 将对应的饼图数据放入
				List<Map<String, Object>> dataList = Lists.newArrayList();
				/* 颠倒顺序 wzj 2017-12-1 begin */
				dataList.add(barDataMap);
				dataList.add(pieList);
				/* end */
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES, dataList);
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.widthSmall"));
				barOptionMap.put(entity.getDate(), seriesMap);
			} else {
				// 柱状图
				Map<String, Object> amountMap = Maps.newHashMap();
				Map<String, Object> barDataMap = Maps.newHashMap();
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
						d1.format(new BigDecimal(entity.getAmount())).equals("0.00") ? null
								: d1.format(new BigDecimal(entity.getAmount())));
				/* end */
				List<Map<String, Object>> amountList = Lists.newArrayList();
				amountList.add(amountMap);
				barDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, amountList);
				List<Map<String, Object>> dataList = (List<Map<String, Object>>) barOptionMap.get(entity.getDate())
						.get(ReportGraphConstant.SeriesProperties.SERIES);
				/* 改变位置 wzj 2017-12-1 begin */
				dataList.add(dataList.size() - 1, barDataMap);
				/* end */
			}
		}
		/* 改变位置 wzj 2017-12-1 begin */
		// 获取基本设置表类型配置内容(饼图)
		pieMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, "各业务所占百分比");
		pieMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.PIE);
		/* 饼图数据格式化 wzj 2017-11-30 begin */
		Map<String, Object> tooltipPieMap = Maps.newHashMap();
		tooltipPieMap.put(ReportGraphConstant.SeriesProperties.TRIGGER_KEY,
				ReportGraphConstant.SeriesProperties.ITEM_KEY);
		/* 去掉({a} <br/>{b} : {c} ({d}%) {c}() 修改人:sg 修改日期:2017-12-04 begin */
		tooltipPieMap.put("formatter", "{a} <br/>{b} : {d}%");
		/* end */
		pieMap.put(ReportGraphConstant.SeriesProperties.TOOLTIP_KEY, tooltipPieMap);
		/* end */
		List<String> location = Lists.newArrayList();
		location.add("75%");// 横坐标
		location.add("35%");// 纵坐标
		pieMap.put(ReportGraphConstant.SeriesProperties.CENTER, location);
		pieMap.put(ReportGraphConstant.SeriesProperties.RADIUS, "28%");
		baseOptionSeriesDataList.add(pieMap);
		/* end */
		// 图形过滤
		Iterator<String> barIterator = barOptionMap.keySet().iterator();
		while (barIterator.hasNext()) {
			seriesDataList.add(barOptionMap.get(barIterator.next()));
		}
		// 返回
		rtnMap.put(ReportGraphConstant.SeriesProperties.TOOLTIP_KEY, tooltipMap);
		rtnMap.put(ReportGraphConstant.DataGraphList.TIMELINE_DATA_LIST, timeLineDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.BASEOPTION_SERIES_DATA_LIST, baseOptionSeriesDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}

	/**
	 * 
	 * Title: reserveClearVolume
	 * <p>
	 * Description: 预约清分业务量统计图
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param orderClearMain
	 * @return Map<String,Object> 返回类型
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> reserveClearVolume(OrderClearMain orderClearMain) {

		Map<String, Object> rtnMap = Maps.newHashMap();

		// 图表数据初始化
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();

		// 查询图表数据
		List<OrderClearMain> resultList = orderClearMainDao.reserveClearGraph(orderClearMain);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();

		// 设置图例数据
		legendDataList.add(reserveVolume);
		legendDataList.add(acceptVolume);

		// 设置x轴数据
		for (OrderClearMain entity : resultList) {
			if (!xAxisDataList
					.contains(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD))) {
				xAxisDataList.add(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD));
			}
		}

		List<String> reserveDataList = Lists.newArrayList();
		List<String> acceptDataList = Lists.newArrayList();

		Map<String, Object> reserveMap = Maps.newHashMap();
		Map<String, Object> acceptMap = Maps.newHashMap();

		// 业务量数据初始化
		String type = GraphType.LINE;
		int iNum;
		for (iNum = 0; iNum < xAxisDataList.size(); iNum++) {
			acceptDataList.add("0");
			reserveDataList.add("0");
		}
		reserveMap.put(SeriesProperties.SERIES_NAME_KEY, reserveVolume);
		reserveMap.put(SeriesProperties.SERIES_TYPE_KEY, type);
		reserveMap.put(SeriesProperties.SERIES_DATA_KEY, reserveDataList);
		reserveMap.put(SeriesProperties.SERIES_SMOOTH_KEY, true);
		seriesMap.put(reserveVolume, reserveMap);
		acceptMap.put(SeriesProperties.SERIES_NAME_KEY, acceptVolume);
		acceptMap.put(SeriesProperties.SERIES_TYPE_KEY, type);
		acceptMap.put(SeriesProperties.SERIES_DATA_KEY, acceptDataList);
		acceptMap.put(SeriesProperties.SERIES_SMOOTH_KEY, true);
		seriesMap.put(acceptVolume, acceptMap);

		// 业务量数据处理
		for (OrderClearMain result : resultList) {
			reserveMap = (Map<String, Object>) seriesMap.get(reserveVolume);
			acceptMap = (Map<String, Object>) seriesMap.get(acceptVolume);
			reserveDataList = (List<String>) reserveMap.get(SeriesProperties.SERIES_DATA_KEY);
			acceptDataList = (List<String>) acceptMap.get(SeriesProperties.SERIES_DATA_KEY);
			for (iNum = 0; iNum < xAxisDataList.size(); iNum++) {
				if (xAxisDataList.get(iNum)
						.equals(DateFormatUtils.format(result.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD))) {
					reserveDataList.set(iNum, StringUtils.toString(Integer.valueOf(reserveDataList.get(iNum)) + 1));
					if (DictUtils.getDictValue(accept, "cl_order_type", null).equals(result.getStatus())) {
						acceptDataList.set(iNum, StringUtils.toString(Integer.valueOf(acceptDataList.get(iNum)) + 1));
					}
				}
			}
		}
		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}
		rtnMap.put(DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}

	/**
	 * 
	 * Title: reserveClearAmount
	 * <p>
	 * Description: 预约清分总金额统计图
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param orderClearMain
	 * @return Map<String,Object> 返回类型
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> reserveClearAmount(OrderClearMain orderClearMain) {

		// 查询图表数据
		List<OrderClearMain> resultList = orderClearMainDao.reserveClearGraph(orderClearMain);

		Map<String, Object> rtnMap = Maps.newHashMap();
		// 设置时间轴数据
		List<String> timeLineDataList = Lists.newArrayList();
		// 设置图例数据
		List<String> legendDataList = Lists.newArrayList();
		// 设置X轴数据
		List<String> xAxisDataList = Lists.newArrayList();
		// 设置基本选项内容数据
		List<Map<String, Object>> baseOptionSeriesDataList = Lists.newArrayList();
		// 基本选项饼图配置
		Map<String, Object> pieMap = Maps.newHashMap();
		// 图形数据配置项(最后返回的集合)
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		// 图形内容配置(柱状图、饼图)
		Map<String, Map<String, Object>> barOptionMap = Maps.newLinkedHashMap();
		Map<String, Map<String, Object>> pieOptionMap = Maps.newHashMap();
		// 提示框组件
		Map<String, Object> tooltipMap = Maps.newHashMap();
		// 指示器配置项
		Map<String, String> axisPointer = Maps.newHashMap();
		// 设置业务名称
		String businessName = null;
		// 获取业务名称作为x轴坐标
		String officeName = UserUtils.getUser().getOffice().getName();
		xAxisDataList.add(officeName);
		/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
		DecimalFormat d1 = new DecimalFormat("##0.00#");
		/* end */
		legendDataList.add(reserveAmount);
		legendDataList.add(acceptAmount);
		// 设置图形内容名称，形状
		Map<String, Object> baseOptionMap = Maps.newHashMap();
		Map<String, Object> tempBaseOptionMap = Maps.newHashMap();
		tempBaseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, reserveAmount);
		tempBaseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
		tempBaseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, reserve);
		baseOptionSeriesDataList.add(tempBaseOptionMap);
		baseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, acceptAmount);
		baseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
		baseOptionMap.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, accept);
		baseOptionSeriesDataList.add(baseOptionMap);
		// 过滤现金业务（饼图）
		for (OrderClearMain entity : resultList) {
			// 设置业务类型
			if (DictUtils.getDictValue(accept, "cl_order_type", null).equals(entity.getStatus())) {
				// 设置业务名称
				businessName = acceptAmount;
			} else {
				businessName = reserveAmount;
			}
			// 配置指示器
			axisPointer.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY,
					ReportGraphConstant.SeriesProperties.SHADOW_KEY);
			tooltipMap.put(ReportGraphConstant.SeriesProperties.TRIGGER_KEY,
					ReportGraphConstant.SeriesProperties.AXIS_KEY);
			tooltipMap.put(ReportGraphConstant.SeriesProperties.AXISPOINTER_KEY, axisPointer);
			// 设置时间轴数据
			if (!timeLineDataList
					.contains(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD))) {
				timeLineDataList
						.add(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD));
			}
			// 图形内容配置项(饼图)
			if (!pieOptionMap
					.containsKey(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD))) {
				// 饼图
				Map<String, Object> amountMap = Maps.newHashMap();
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, d1.format(entity.getInAmount()));
				/* end */
				List<Map<String, Object>> pieDataList = Lists.newArrayList();
				pieDataList.add(amountMap);
				Map<String, Object> tempAmountMap = Maps.newHashMap();
				if (businessName == acceptAmount) {
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, reserveAmount);
					/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
							d1.format(entity.getInAmount()));
					/* end */
					pieDataList.add(tempAmountMap);
				} else {
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, acceptAmount);
					/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, d1.format(0));
					/* end */
					pieDataList.add(tempAmountMap);
				}
				Map<String, Object> pieDataMap = Maps.newHashMap();
				pieDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, pieDataList);
				pieOptionMap.put(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD),
						pieDataMap);
			} else {
				// 饼图
				List<Map<String, Object>> pieDataList = (List<Map<String, Object>>) pieOptionMap
						.get(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD))
						.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				if (businessName == acceptAmount) {
					for (Map<String, Object> map : pieDataList) {
						/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
						map.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
								d1.format((new BigDecimal(
										(String) (map.get(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY))))
												.add(entity.getInAmount())));
						/* end */
					}
				} else {
					for (Map<String, Object> map : pieDataList) {
						if (map.get(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY).equals(reserveAmount)) {
							/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
							map.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
									d1.format((new BigDecimal(
											(String) (map.get(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY))))
													.add(entity.getInAmount())));
							/* end */
						}
					}
				}
			}
		}
		// 过滤图形数据（柱状图）
		for (OrderClearMain entity : resultList) {
			// 设置业务类型
			if (DictUtils.getDictValue(accept, "cl_order_type", null).equals(entity.getStatus())) {
				// 设置业务名称
				businessName = acceptAmount;
			} else {
				businessName = reserveAmount;
			}
			Map<String, Object> pieList = pieOptionMap
					.get(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD));
			// 图形内容配置项(柱状图)
			if (!barOptionMap
					.containsKey(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD))) {
				// 柱状图
				Map<String, Object> seriesMap = Maps.newHashMap();
				Map<String, Object> titleMap = Maps.newHashMap();
				List<Map<String, Object>> amountList = Lists.newArrayList();
				// 各业务的总金额
				Map<String, Object> amountMap = Maps.newHashMap();
				Map<String, Object> barDataMap = Maps.newHashMap();
				String title = DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD)
						+ "预约清分总金额";
				titleMap.put(ReportGraphConstant.SeriesProperties.TEXT_KEY, title);
				seriesMap.put(ReportGraphConstant.SeriesProperties.TITLE_KEY, titleMap);
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, businessName);
				/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
				amountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, d1.format(entity.getInAmount()));
				/* end */
				amountList.add(amountMap);
				Map<String, Object> tempAmountMap = Maps.newHashMap();
				List<Map<String, Object>> tempAmountList = Lists.newArrayList();
				Map<String, Object> tempBarDataMap = Maps.newHashMap();
				if (businessName == acceptAmount) {
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, reserveAmount);
					/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
							d1.format(entity.getInAmount()));
					/* end */
					tempAmountList.add(tempAmountMap);
				} else {
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, acceptAmount);
					/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
					tempAmountMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY, d1.format(0));
					/* end */
					tempAmountList.add(tempAmountMap);
				}
				// 将对应的饼图数据放入
				List<Map<String, Object>> dataList = Lists.newArrayList();

				barDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, amountList);
				dataList.add(barDataMap);
				tempBarDataMap.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, tempAmountList);
				dataList.add(tempBarDataMap);
				/* 颠倒顺序 wzj 2017-12-4 begin */
				dataList.add(pieList);
				/* end */
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES, dataList);
				seriesMap.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.widthSmall"));
				barOptionMap.put(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD),
						seriesMap);
			} else {
				// 柱状图
				List<Map<String, Object>> dataList = (List<Map<String, Object>>) barOptionMap
						.get(DateFormatUtils.format(entity.getRegisterDate(), Constant.Dates.FORMATE_YYYY_MM_DD))
						.get(ReportGraphConstant.SeriesProperties.SERIES);
				if (businessName == acceptAmount) {
					for (Map<String, Object> map : dataList) {
						if (!map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY)
								.equals(pieList.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY))) {
							List<Map<String, Object>> tempList = (List<Map<String, Object>>) map
									.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
							for (Map<String, Object> tempMap : tempList) {
								/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */
								tempMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
										d1.format((new BigDecimal((String) (tempMap
												.get(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY))))
														.add(entity.getInAmount())));
								/* end */
							}
						}
					}
				} else {
					for (Map<String, Object> map : dataList) {
						if (!map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY)
								.equals(pieList.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY))) {
							List<Map<String, Object>> tempList = (List<Map<String, Object>>) map
									.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
							for (Map<String, Object> tempMap : tempList) {
								if (tempMap.get(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY)
										.equals(businessName)) {

									/* 追加小数点 修改人:sg 修改日期:2017-12-04 begin */

									tempMap.put(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY,
											d1.format((new BigDecimal((String) (tempMap
													.get(ReportGraphConstant.SeriesProperties.SERIES_VALUE_KEY))))
															.add(entity.getInAmount())));
									/* end */
								}
							}
						}
					}
				}
			}
		}
		/* 颠倒顺序 wzj 2017-12-4 begin */
		// 获取基本设置表类型配置内容(饼图)
		pieMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, "预约接收比例");
		pieMap.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.PIE);
		/* 饼图数据格式化 wzj 2017-11-30 begin */
		Map<String, Object> tooltipPieMap = Maps.newHashMap();
		tooltipPieMap.put(ReportGraphConstant.SeriesProperties.TRIGGER_KEY,
				ReportGraphConstant.SeriesProperties.ITEM_KEY);
		/* 去掉({a} <br/>{b} : {c} ({d}%) {c}() 修改人:sg 修改日期:2017-12-04 begin */
		tooltipPieMap.put("formatter", "{a} <br/>{b} : {d}%");
		/* end */
		pieMap.put(ReportGraphConstant.SeriesProperties.TOOLTIP_KEY, tooltipPieMap);
		/* end */
		List<String> location = Lists.newArrayList();
		location.add("75%");// 横坐标
		location.add("35%");// 纵坐标
		pieMap.put(ReportGraphConstant.SeriesProperties.CENTER, location);
		pieMap.put(ReportGraphConstant.SeriesProperties.RADIUS, "28%");
		baseOptionSeriesDataList.add(pieMap);
		/* end */
		// 图形过滤
		Iterator<String> barIterator = barOptionMap.keySet().iterator();
		while (barIterator.hasNext()) {
			seriesDataList.add(barOptionMap.get(barIterator.next()));
		}

		// 返回
		rtnMap.put(ReportGraphConstant.SeriesProperties.TOOLTIP_KEY, tooltipMap);
		rtnMap.put(ReportGraphConstant.DataGraphList.TIMELINE_DATA_LIST, timeLineDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.BASEOPTION_SERIES_DATA_LIST, baseOptionSeriesDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
	}
}
