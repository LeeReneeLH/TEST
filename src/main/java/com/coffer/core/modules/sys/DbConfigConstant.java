package com.coffer.core.modules.sys;

/**
 * 系统共通项常量类
 * 
 * @author xp
 * @version 2017-12-21
 */
public class DbConfigConstant {
	/**
	 * 顶级参数id
	 * 
	 * @author xp
	 *
	 */
	public static class AllParent {
		/** 顶级参数id */
		public static final String ALL_PARENT_ID = "1";
		/** 顶级参数的父级id */
		public static final String FIRST_PARENT_ID = "0";
	}

	/**
	 * 参数操作类型
	 * 
	 * @author xp
	 *
	 */
	public static class ConfigType {
		/** 修改 */
		public static final String UPDATE = "update";
		/** 添加 */
		public static final String INSERT = "insert";
	}

	/**
	 * 参数键是否重复验证
	 * 
	 * @author xp
	 *
	 */
	public static class Punctuation {
		public static final String COMMA = ",";
	}

	/**
	 * 添加参数的类型
	 * 
	 * @author xp
	 *
	 */
	public static class Type {
		/** 键值 */
		public static final String KEY = "key";
		/** 分组 */
		public static final String GROUP = "group";
	}

	/**
	 * 有效标识
	 * 
	 * @author xp
	 *
	 */
	public static class deleteFlag {
		/** 有效 */
		public static final String Valid = "0";
		/** 无效 */
		public static final String Invalid = "1";
	}

	/**
	 * 对数据进行增删改的受影响的行数
	 * 
	 * @author xp
	 * @version 2017-12-22
	 */
	public static class sqlResult {
		/** 受影响的行数 */
		public static final int ZERO = 0;
	}

	/**
	 * 返回Boolean值
	 * 
	 * @author xp
	 * @version 2017-12-22
	 */
	public static class checkIfExit {
		public static final String TRUE = "true";
		public static final String FALSE = "false";
	}

	/**
	 * 缓存常量
	 * 
	 * @author xp
	 *
	 */
	public static class DbUtils {
		public static final String DBCONFIG_CACHE_ID_ = "id_";
		public static final String CACHE_DBCONFIG_ALL_LIST = "dbConfigList";
	}

	/**
	 * 网点调拨箱子添加时验证的参数
	 * 
	 * @author xp
	 *
	 */
	public static class outletBoxAdd {
		public static final String ALLOCATION_BOXADDMANAGE_USE = "allocation.boxAddManage.use";
		public static final String USE_BOXNO = "0";
		public static final String USE_RFID = "1";
		public static final String USE_ALL = "2";
	}

	/**
	 * 网点上缴登记箱子是否验证非空
	 * 
	 * @author xp
	 *
	 */
	public static class boxIsEmpty {
		public static final String VALIDATE = "0";
		public static final String NO_VALIDATE = "1";
	}

	/**
	 * otp动态口令验证参数
	 * 
	 * @author qph
	 * @version 2018-7-03
	 */
	public static class otpConstant {
		/** 动态口令更新时间 */
		public static final int UPDATESECONDS = 60;
	}

}
