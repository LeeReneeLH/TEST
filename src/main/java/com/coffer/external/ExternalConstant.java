package com.coffer.external;

/**
 * Webservices常量类
 * 
 * @author LF
 * 
 * @version 2014-11-21
 */
public class ExternalConstant {

	public static class HardwareInterface {
		/** 版本号 */
		public static final String VERSION_NO_01 = "01";

		/** 结果代码 成功 00 */
		public static final String RESULT_FLAG_SUCCESS = "00";
		/** 结果代码 失败 01 */
		public static final String RESULT_FLAG_FAILURE = "01";

		/** 错误代码 数据库异常 E01 */
		public static final String ERROR_NO_E01 = "E01";
		/** 错误代码 处理异常 E02 */
		public static final String ERROR_NO_E02 = "E02";
		/** 错误代码 参数异常 E03 */
		public static final String ERROR_NO_E03 = "E03";
		/** 错误代码 用户名密码错误或此用户不存在 E04 */
		public static final String ERROR_NO_E04 = "E04";
		/** 错误代码 押运人员不存在 E05 */
		public static final String ERROR_NO_E05 = "E05";
		/** 错误代码 服务代码不正确，正常范围是【01-07】 E06 */
		public static final String ERROR_NO_E06 = "E06";
		/** 错误代码当前押运人员已经被删除，请重新验证 */
		public static final String ERROR_NO_E07 = "E07";
		/** 错误代码 当前箱袋已经被删除，请刷新箱袋列表 */
		public static final String ERROR_NO_E08 = "E08";
		/** 错误代码 授权用户名或密码不正确 */
		public static final String ERROR_NO_E09 = "E09";
		/** 错误代码 授权用户权限不足 */
		public static final String ERROR_NO_E10 = "E10";
		/** 错误代码 机构信息查询错误 */
		public static final String ERROR_NO_E11 = "E11";
		/** 错误代码 押运人员未签到，不能出库 */
		public static final String ERROR_NO_E12 = "E12";
		/** 扫描到的RFID与流水单号不符 */
		public static final String ERROR_NO_E13 = "E13";
		/** 任务流水单号不正确 */
		public static final String ERROR_NO_E14 = "E14";
		/** 错误代码 箱袋状态不是出库待交接，无法指纹交接 */
		public static final String ERROR_NO_E15 = "E15";
		/** 错误代码 该任务存在登记出库信息 ，不能重复提交 */
		public static final String ERROR_NO_E16 = "E16";
		/** 查询不到该RFID的出入库登记信息 */
		public static final String ERROR_NO_E17 = "E17";
		/** 该网点存在已登记未接收的上缴信息，不能重复提交 */
		public static final String ERROR_NO_E18 = "E18";
		/** 该绑定关系已经上传 */
		public static final String ERROR_NO_E19 = "E19";
		/** 该加钞计划编号不存在 */
		public static final String ERROR_NO_E20 = "E20";
		/** 出库钞箱类型数量，与加钞计划中类型数量不一致 */
		public static final String ERROR_NO_E21 = "E21";
		/** 物品更新失败 */
		public static final String ERROR_NO_E22 = "E22";
		/** 流水单号交接状态不正确 */
		public static final String ERROR_NO_E23 = "E23";
		/** 数据库中不存在检索的信息 */
		public static final String ERROR_NO_E24 = "E24";
		/** 终端设备不可用 */
		public static final String ERROR_NO_E25 = "E25";
		/** 线路已经删除 */
		public static final String ERROR_NO_E26 = "E26";
		/** [修改失败]：流水单号[{0}]当前状态为[{1}]，不能修改！ **/
		public static final String ERROR_NO_E27 = "E27";
		/** [修改失败]：流水单号[{0}]对应箱袋信息已登记，不能修改！ **/
		public static final String ERROR_NO_E28 = "E28";
		/** [登记失败]：重复登记，请参看流水单号[{0}]登记信息！ **/
		public static final String ERROR_NO_E29 = "E29";
		/** [删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！ **/
		public static final String ERROR_NO_E30 = "E30";
		/** [修改失败]：流水单号[{0}]对应调拨信息不存在！ **/
		public static final String ERROR_NO_E31 = "E31";
		/** [修改失败]：流水单号[{0}]与当前业务种别不一致，不能修改！ **/
		public static final String ERROR_NO_E32 = "E32";
		/** [删除失败]：流水单号[{0}]对应箱袋信息已登记，不能删除！ **/
		public static final String ERROR_NO_E33 = "E33";
		/** [删除失败]：流水单号[{0}]对应调拨信息不存在！ **/
		public static final String ERROR_NO_E34 = "E34";
		/** [登记失败]：物品[{0}]对应物品信息不存在！ **/
		public static final String ERROR_NO_E35 = "E35";
		/** [交接失败]：线路信息不存在或当前路线押运人员信息已变更！ **/
		public static final String ERROR_NO_E36 = "E36";
		/** [修改失败]：修改密码失败，旧密码错误！ **/
		public static final String ERROR_NO_E37 = "E37";
		/** [修改失败]：用户名不能为空！ **/
		public static final String ERROR_NO_E38 = "E38";
		/** [修改失败]：用户不存在！ **/
		public static final String ERROR_NO_E39 = "E39";
		/** [修改失败]：密码不能为空！ **/
		public static final String ERROR_NO_E40 = "E40";
		/** [入库登记失败]：库区容量不足，请扩充库区容量后再入库！ **/
		public static final String ERROR_NO_E41 = "E41";
		/** 流水号的状态不正确(不是待出、入库的状态) **/
		public static final String ERROR_NO_E42 = "E42";
		/** 库区容量不足，请扩充库区容量后再入库 **/
		public static final String ERROR_NO_E43 = "E43";
		/** 接口重复提交 **/
		public static final String ERROR_NO_E44 = "E44";
		/** 库区位置不足 **/
		public static final String ERROR_NO_E45 = "E45";
		/** 流水单号 对应业务数据不存在，或状态不正确 **/
		public static final String ERROR_NO_E46 = "E46";
		/** 流水单号 对应业务类型不正确 **/
		public static final String ERROR_NO_E47 = "E47";
		/** 审批金额 与绑卡物品总金额不符 **/
		public static final String ERROR_NO_E48 = "E48";
		/** 原有RFID编号不存在 **/
		public static final String ERROR_NO_E49 = "E49";
		/** 新绑定RFID在系统中已存在，不允许替换 **/
		public static final String ERROR_NO_E50 = "E50";
		/** 人脸信息采集未查询到 **/
		public static final String ERROR_NO_E51 = "E51";
		/** 用户操作所选金库权限不足 **/
		public static final String ERROR_NO_E52 = "E52";
		/** 箱袋类型不在库房设定范围内 **/
		public static final String ERROR_NO_E53 = "E53";
		/** 箱袋所属机构不在库房设定范围内 **/
		public static final String ERROR_NO_E54 = "E54";
		/** 应箱袋明细信息不存在 **/
		public static final String ERROR_NO_E55 = "E55";
		/** 库房无效 **/
		public static final String ERROR_NO_E56 = "E56";
		/** 用户微信解绑或信息修改，微信端重新登录 **/
		public static final String ERROR_NO_E57 = "E57";
		/** 自定义信息 */
		public static final String ERROR_NO_E99 = "E99";
	}

	/**
	 * @author wyj
	 * 
	 */
	public static class Allocation {

		/**
		 * 出入标示：出库
		 */
		public static final String INOUT_TYPE_OUT = "0";
		/**
		 * 出入标示：入库
		 */
		public static final String INOUT_TYPE_IN = "1";
		/**
		 * 扫描门位置:现金库
		 */
		public static final String ADDR_FLAG_STORE = "0";
		/**
		 * 扫描门位置:整点室
		 */
		public static final String ADDR_FLAG_CLASSIFICATION = "1";
		/**
		 * 交接状态:未交接
		 */
		public static final String TASK_FLAG_TODO = "0";
		/**
		 * 交接状态:已交接
		 */
		public static final String TASK_FLAG_FINSHED = "1";
		// /**
		// * 交接人类型:交接发出人
		// */
		// public static final String USER_TYPE_SEND = "1";
		// /**
		// * 交接人类型:交接发出授权人
		// */
		// public static final String USER_TYPE_SEND_MANAGER = "2";
		// /**
		// * 交接人类型:交接接收人
		// */
		// public static final String USER_TYPE_ACCEPT = "3";
		// /**
		// * 交接人类型:交接接收授权人
		// */
		// public static final String USER_TYPE_ACCEPT_MANAGER = "4";
	}

	public static class Handover {

		/**
		 * 出入标示：出库
		 */
		public static final String INOUT_TYPE_OUT = "0";
		/**
		 * 出入标示：入库
		 */
		public static final String INOUT_TYPE_IN = "1";
		/**
		 * 扫描门位置:现金库
		 */
		public static final String ADDR_FLAG_STORE = "0";
		/**
		 * 扫描门位置:整点室
		 */
		public static final String ADDR_FLAG_CLASSIFICATION = "1";
		/**
		 * 交接状态:未交接
		 */
		public static final String TASK_FLAG_TODO = "0";
		/**
		 * 交接状态:已交接
		 */
		public static final String TASK_FLAG_FINSHED = "1";
		/**
		 * 交接人类型:交接发出人
		 */
		public static final String USER_TYPE_SEND = "1";
		/**
		 * 交接人类型:交接发出授权人
		 */
		public static final String USER_TYPE_SEND_MANAGER = "2";
		/**
		 * 交接人类型:交接接收人
		 */
		public static final String USER_TYPE_ACCEPT = "3";
		/**
		 * 交接人类型:交接接收授权人
		 */
		public static final String USER_TYPE_ACCEPT_MANAGER = "4";
	}

	/**
	 * 物品检参数查用
	 * 
	 * @author wangbaozhong
	 *
	 */
	public static class GoodsParamCheck {

		/** 币种 长度 **/
		public static final int INT_LENGTH_CURRENCY = 3;
		/** 类别 长度 **/
		public static final int INT_LENGTH_CLASSIFICATION = 2;
		/** 套别 长度 **/
		public static final int INT_LENGTH_SETS = 1;
		/** 材质 长度 **/
		public static final int INT_LENGTH_CASH = 1;
		/** 面值 长度 **/
		public static final int INT_LENGTH_DENOMINATION = 2;
		/** 单位 长度 **/
		public static final int INT_LENGTH_UNIT = 3;

		/** 物品金额最大长值Key **/
		public static final String GOODS_MAX_MONEY_CONFIG_KEY = "allocation.max.money";
		/** 物品金额最大长值 **/
		public static final String GOODS_MAX_MONEY_DEFAULT_VALUE = "9999999999999.99";
	}

	/**
	 * 
	 * @author LLF 回收标识
	 *
	 */
	public static class RecoverStatus {
		/** 未回收 */
		public static final String NO_RECOVER = "0";

		/** 已回收 */
		public static final String IS_RECOVER = "1";
	}

	/**
	 * @author chengshu RFID错误标识
	 */
	public static class RfidErrorFlag {
		/** 正常 */
		public static final String SUCCESS = "0";

		/** 不存在 */
		public static final String EXIST_ERROR = "1";

		/** 所属机构不正确 */
		public static final String OFFICE_ERROR = "2";

		/** 箱袋状态不正确 */
		public static final String STATUS_ERROR = "3";
	}

	/**
	 * 
	 * Title: OperationFlag
	 * <p>
	 * Description: 操作标识
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2016年9月23日 下午3:46:45
	 */
	public static class OperationFlag {
		/** 更新操作 **/
		public static final String UPDATE_FLAG = "0";
		/** 插入操作 **/
		public static final String INSERT_FLAG = "1";
	}

	/**
	 * 
	 * Title: SearchScopeFlag
	 * <p>
	 * Description: 查询范围标识
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2017年7月12日 下午4:25:14
	 */
	public static class SearchScopeFlag {
		/** 范围：自身 **/
		public static final String SCOPE_SELF = "0";
		/** 范围：全部 **/
		public static final String SCOPE_ALL = "1";
	}

	/**
	 * 
	 * Title: ExtractEntityFiled
	 * <p>
	 * Description: 提取Entity 域英文名
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2017年7月13日 上午8:46:43
	 */
	public static class ExtractEntityFiled {
		/** 域英文名： ID **/
		public static final String EXTRACT_FILED_ID = "id";
	}
	
	/**
	 * 
	* Title: ConstraintFlag 
	* <p>Description: 强制标识</p>
	* @author wangbaozhong
	* @date 2017年8月10日 下午12:46:16
	 */
	public static class ConstraintFlag {
		/** 强制标识：有效*/
		public static final String CONSTRAINT_VALID = "0";
	}

	public static final String STR_COMMA = ",";
	// 套别为空
	public static final String NULL_SETS = "-";
	// 调拨日期格式
	public static final String DATE_FORMATE_YYYY_MM_DD = "yyyy-MM-dd";
	/** PDA同步人员类型常量 */
	public static final String PDA_SYNCHRONOUS_ESCORT_TYPE="pda.synchronous.escortType";
	/** PDA同步用户类型常量 */
	public static final String PDA_SYNCHRONOUS_USER_TYPE="pda.synchronous.userType";
}
