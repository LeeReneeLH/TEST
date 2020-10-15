/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.collection;

import com.coffer.businesses.common.Constant;

/**
 * @author Lemon
 *
 */
public class CollectionConstant extends Constant {

	/**
	 * 分配状态（0：未分配；1：已分配；2：已确认；3：驳回）
	 * 
	 * @author wl
	 *
	 */
	public static class allotStatusType {
		public static final String ALLOT_NO = "0"; // 未分配
		public static final String ALLOT_OK = "1"; // 已分配
		public static final String CONFIRM_OK = "2"; // 已确认
		public static final String CONFIRM_NO = "3"; // 驳回
	}

	/**
	 * 数据区分（0：录入 1：分配）
	 * 
	 * @author wl
	 *
	 */
	public static class dataFlagType {
		public static final String INPUT = "0"; // 录入
		public static final String ALLOT = "1"; // 分配
	}

	/**
	 * 启用标识（1：启用 0 ：未启用）
	 * 
	 * @author wl
	 *
	 */
	public static class enabledFlagType {
		public static final String NO = "0"; // 未启用
		public static final String OK = "1"; // 启用
	}

	/**
	 * 门店预约-状态（0：登记；2：确认；1：分配；3：清分）
	 * 
	 * @author wl
	 *
	 */
	public static class statusType {
		public static final String REGISTER = "0"; // 登记
		public static final String CONFIRM = "2"; // 确认
		public static final String ALLOT = "1"; // 分配
		public static final String CLEAR = "3"; // 清分
	}

	/**
	 * 预约清分-状态 (1：登记，2：接收)
	 * 
	 * @author wl
	 *
	 */
	public static class clearStatusType {
		public static final String REGISTER = "1"; // 登记
		public static final String CONFIRM = "2"; // 确认
	}

	/**
	 * 清分状态 (0：未清分，1：已清分) date 2020-05-20
	 * 
	 * @author lihe
	 */
	public static class CheckStatus {
		public static final String CHECKED = "0"; // 未清分
		public static final String UNCHECKED = "1"; // 已清分
	}
}
