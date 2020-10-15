package com.coffer.businesses.modules.report;

import com.coffer.businesses.common.Constant;

/**
 * 
 * Title: ReportGraphConstant
 * <p>
 * Description: 报表图形常量
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年8月29日 上午11:21:19
 */
public class ReportGraphConstant extends Constant {
	/** 地图数据json信息 map key **/
	public static final String MAP_JSON_INFO_KEY = "mapJsonInfo";
	/**
	 * 
	 * Title: GraphType
	 * <p>
	 * Description: 图形类型
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2017年8月29日 下午2:13:14
	 */
	public static class GraphType {
		/** 线图 **/
		public static final String LINE = "line";
		/** 饼图 */
		public static final String PIE = "pie";
		/** 柱状图 */
		public static final String BAR = "bar";
		/** 堆叠柱状图坐标 */
		public static final String POLAR = "polar";
		/** 雷达图坐标 */
		public static final String RANDAR = "radar";
	}

	/**
	 * 
	 * Title: SeriesProperties
	 * <p>
	 * Description: 图形Series属性
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2017年8月29日 下午2:13:55
	 */
	public static class SeriesProperties {
		/** 名称 */
		public static final String SERIES_NAME_KEY = "name";

		/** 类型 */
		public static final String SERIES_TYPE_KEY = "type";
		/** 数据 */
		public static final String SERIES_DATA_KEY = "data";
		/** 平滑 */
		public static final String SERIES_SMOOTH_KEY = "smooth";
		/** 柱状图宽度 */
		public static final String SERIES_BAR_WIDTH_KEY = "barWidth";
		/** 坐标系 */
		public static final String SERIES_COORDINATE_SYSTEM_KEY = "coordinateSystem";

		/** 总量（柱状图） */
		public static final String SERIES_STACK_KEY = "stack";
		/** （柱状图） */
		public static final String SERIES_LABEL_KEY = "label";
		/** 半径范围（饼图） */
		public static final String RADIUS = "radius";
		/** 位置（饼图） */
		public static final String CENTER = "center";
		/** 标题 */
		public static final String TITLE_KEY = "title";
		/** 文本 */
		public static final String TEXT_KEY = "text";
		/** 值 */
		public static final String SERIES_VALUE_KEY = "value";
		/** series */
		public static final String SERIES = "series";
		/** 地图series项的mapType key */
		public static final String SERIES_MAPTYPE_KEY="mapType";
		/** 全国地图常量  */
		public static final String MAP_JSON_CHINA="全国";
		/** 最大值（雷达图使用） */
		public static final String MAX_KEY = "max";
		/** 坐标值指示器（雷达图使用） */
		public static final String INDICATOR_KEY = "indicator";
		/** tooltip（雷达图使用） */
		public static final String TOOLTIP_KEY = "tooltip";
		/** tooltip的trigger（雷达图使用） */
		public static final String TRIGGER_KEY = "trigger";
		/** tooltip的axisPointer（雷达图使用） */
		public static final String AXISPOINTER_KEY = "axisPointer";
		/** trigger的内容（雷达图使用） */
		public static final String ITEM_KEY = "item";
		/** trigger的内容（柱状图使用） */
		public static final String AXIS_KEY = "axis";
		/** trigger的内容（柱状图使用） */
		public static final String SHADOW_KEY = "shadow";
		/** 区域填充样式（雷达图使用） */
		public static final String AREASTYLE_KEY = "areaStyle";
		/** 区域填充样式的内容（雷达图使用） */
		public static final String DEFAULT_KEY = "default";
		/** 折线拐点标志的样式内容（雷达图使用） */
		public static final String NORMAL_KEY = "normal";
		/** 折线拐点标志的样式（雷达图使用） */
		public static final String ITEMSTYLE_KEY = "itemStyle";
	}
	
	public static class SeriesClass {
		/** 柱状图（数据合并） */
		public static final String STACK = "stack";
	}
	
	public static class StackColumnLegend {
		/** 常规线路 */
		public static final String ROUTINE_LINE= "常规线路";
		/** 临时线路 */
		public static final String TEMP_LINE= "临时线路";
		/** stack属性 */
		public static final String ATTRIBUTE_A= "a";
	}

	/**
	 * 
	 * Title: dataGraphList Description: 图形数据
	 * 
	 * @author wanghan
	 *
	 */
	public static class DataGraphList {
		/** 放legend数据的量 */
		public static final String LEGEND_DATE_LIST = "legendDataList";

		/** 放图形x轴数据的量 */
		public static final String XAXIS_DATE_LIST = "xAxisDataList";

		/** 放series数据的量 */
		public static final String SERIES_DATE_LIST = "seriesDataList";

		/** 放timeline数据的量 */
		public static final String TIMELINE_DATA_LIST = "timelineDataList";

		/** 放baseOption数据的量 */
		public static final String BASEOPTION_SERIES_DATA_LIST = "baseOptionSeriesList";

		/** 放basicOption数据的量 */
		public static final String BASICOPTION_SERIES_DATA_LIST = "basicOptionSeriesList";
		
		/** 放地图geoCoordMap项数据引用的常量*/
		public static final String MAP_GEOCOORMAP="geoCoordMap";
		
		/** 放地图makeline项的data数据引用的常量*/
		public static final String MAP_LINEDATALIST="lineDataList";
		
		/** 放地图makePoint项的data数据引用的常量*/
		public static final String MAP_POINTDATALIST="pointDataList";
		/** 放雷达图的数据常量 */
		public static final String RADAR_DATALIST = "radarDataList";
		/** 放雷达图的数据常量 */
		public static final String RADARINDEX_LIST = "radarIndex";

	}
}
