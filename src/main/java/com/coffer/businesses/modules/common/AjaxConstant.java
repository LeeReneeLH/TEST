package com.coffer.businesses.modules.common;

import com.coffer.businesses.common.Constant;

/**
 * Ajax请求常量类
 * 
 * @author LLF
 * 
 * @version 2015-09-17
 */
public class AjaxConstant extends Constant{

	public static class Interface {
		/** 版本号 */
		public static final String VERSION_NO_01 = "01";

		/** 结果代码 成功 00 */
		public static final String RESULT_FLAG_ACCESS = "00";
		/** 结果代码 失败 01 */
		public static final String RESULT_FLAG_FAILURE = "01";

		/** 错误代码 数据库异常 E01 */
		public static final String ERROR_NO_E01 = "E01";
		/** 错误代码 处理异常 E02 */
		public static final String ERROR_NO_E02 = "E02";
		/** 错误代码 参数异常 E03 */
		public static final String ERROR_NO_E03 = "E03";
		/** 错误代码 授权人信息不正确 E04 */
		public static final String ERROR_NO_E04 = "E04";
		/** 错误代码 修改库存失败 E05 */
		public static final String ERROR_NO_E05 = "E05";
		/** 错误代码 服务代码不正确 E06 */
		public static final String ERROR_NO_E06 = "E06";
		/** 错误代码  无最新盘点信息，请盘点后更新库存 E07 */
		public static final String ERROR_NO_E07 = "E07";
		/** 错误代码  盘点用户机构不匹配 E08 */
		public static final String ERROR_NO_E08 = "E08";
		
		/** .net封装json数据KEY */
		public static final String REQUEST_JSON_KEY = "data";
	}
	
	public static class StockCount {
		/** 盘点更新库存状态 已更新 */
		public static final String UPDATE_FALG_TRUE = "1";
		/** 盘点更新库存状态 未更新 */
		public static final String UPDATE_FALG_FALSE = "0";
	}
}
