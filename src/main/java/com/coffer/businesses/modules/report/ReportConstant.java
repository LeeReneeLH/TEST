package com.coffer.businesses.modules.report;

import com.coffer.businesses.common.Constant;

public class ReportConstant extends Constant {

	/**
	 * @author chengshu
	 * 面值
	 */
	public static class Denomination {
		/** 面值01 */
		public static final String D01 = "01";
		/** 面值02 */
		public static final String D02 = "02";
		/** 面值03 */
		public static final String D03 = "03";
		/** 面值04 */
		public static final String D04 = "04";
		/** 面值05 */
		public static final String D05 = "05";
		/** 面值06 */
		public static final String D06 = "06";
		/** 面值07 */
		public static final String D07 = "07";
		/** 面值08 */
		public static final String D08 = "08";
		/** 面值09 */
		public static final String D09 = "09";
		/** 面值10 */
		public static final String D10 = "10";
		/** 面值11 */
		public static final String D11 = "11";
		/** 面值12 */
		public static final String D12 = "12";
		/** 面值13 */
		public static final String D13 = "13";
		/** 面值14 */
		public static final String D14 = "14";
		/** 面值15 */
		public static final String D15 = "15";
		/** 面值16 */
		public static final String D16 = "16";
		/** 面值17 */
		public static final String D17 = "17";
		/** 面值18 */
		public static final String D18 = "18";
		/** 面值19 */
		public static final String D19 = "19";
		/** 面值20 */
		public static final String D20 = "20";
		/** 面值21 */
		public static final String D21 = "21";
		/** 面值22 */
		public static final String D22 = "22";
		/** 面值23 */
		public static final String D23 = "23";
		/** 面值24 */
		public static final String D24 = "24";
		/** 面值25 */
		public static final String D25 = "25";
		/** 面值26 */
		public static final String D26 = "26";
		/** 面值27 */
		public static final String D27 = "27";
		/** 面值28 */
		public static final String D28 = "28";
		/** 面值29 */
		public static final String D29 = "29";
		/** 面值30 */
		public static final String D30 = "30";
	}
	
	/**
	 * @author chengshu
	 * 报表种别
	 */
	public static class ReportType{
		/** 库间现金报表 */
		public static final String CASH_BETWEEN = "cashBetweenReport";
		/** 现金上缴报表 */
		public static final String CASH_HANDIN = "cashHandinReport";
		/** 物品库存报表 */
		public static final String GOODS_INVENTORY = "goodsInventoryReport";
		/** 重空库存报表 */
		public static final String EMPTY_INVENTORY = "emptyInventoryReport";
		/** 重空库存变更报表 */
		public static final String EMPTY_CHANGE = "emptyChangeReport";
		/** 配款报表 */
		public static final String CASH_ORDER = "cashOrderReport";
		/** 物品库存历史报表 */
		public static final String STORE_GRAPH = "storeInfoGraph";

	}

	public static class ReportExportData {
		/** 制表机构 */
		public static final String OFFICE_NAME = "officename";
		/** 制表时间 */
		public static final String NOW_DATE = "nowdate";
		/** 表的标题 */
		public static final String TOP_TITLE = "toptitle";
		/** 筛选条件：机构 */
		public static final String OFFICE = "office";
		/** 筛选条件：开始时间 */
		public static final String START_TIME = "starttime";
		/** 筛选条件：结束时间 */
		public static final String END_TIME = "endtime";
		/** 筛选条件：单位（年/月/日） */
		public static final String DATE_UNIT = "dateunit";
	}
	public static class SheetName {
		/** 业务量统计 */
		public static final String WORK_VOLUME = "业务量统计";
		/** 入库物品统计 */
		public static final String IN_GOODS_VOLUME = "入库物品统计";
		/** 出库物品统计 */
		public static final String OUT_GOODS_VOLUME = "出库物品统计";
		
	}
}
