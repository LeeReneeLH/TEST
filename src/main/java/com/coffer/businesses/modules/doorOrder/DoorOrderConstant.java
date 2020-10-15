package com.coffer.businesses.modules.doorOrder;

import com.coffer.businesses.common.Constant;

/**
 * 上门收款常量类
 * 
 * @author XL
 * @version 2019年6月26日
 */
public class DoorOrderConstant extends Constant {

	/**
	 * @author wqj 中心账务类型：4：上门收款账务
	 */
	public static class AccountsType {
		/** 上门收款中心账务 */
		public static final String ACCOUNTS_DOOR = "4";

	}

	/**
	 * @author wqj 上门收款业务类型
	 */
	public static class BusinessType {
		/**
		 * 门店存款
		 * 
		 * @author XL
		 * @version 2018年5月17日
		 */
		public static final String DOOR_ORDER = "74";

		/**
		 * 公司代付
		 * 
		 * @author WQJ
		 * @version 2019年6月28日
		 */
		public static final String COMPANY_PAID = "75";

		/**
		 * 中心回款
		 * 
		 * @author WQJ
		 * @version 2019年6月28日
		 */
		public static final String CENTER_BACK = "76";

		/**
		 * 公司存款
		 * 
		 * @author WQJ
		 * @version 2019年6月28日
		 */
		public static final String COMPANY_SAVE_CASH = "77";

		/**
		 * 存款差错
		 * 
		 * @author WQJ
		 * @version 2019年6月28日
		 */
		public static final String DEPOSIT_ERROR_SAVE = "79";

		/**
		 * 中心汇款
		 * 
		 * @author WQJ
		 * @version 2019年6月28日
		 */
		public static final String CENTER_PAID = "78";

		/**
		 * 差错处理
		 * 
		 * @author WQJ
		 * @version 2019年12月3日
		 */
		public static final String ERROR_HANDLE = "80";
		/**
		 * 传统存款
		 * 
		 * @author WQJ
		 * @version 2019年12月17日
		 */
		public static final String TRADITIONAL_SAVE = "81";

		/**
		 * 电子线下
		 * 
		 * @author WQJ
		 * @version 2020年1月15日
		 */
		public static final String ELECTRONIC_OFFLINE = "82";
	}

	/**
	 * @author WQJ 账务代付状态(0:已代付 1:未代付)
	 */
	// public static class AccountPaidStatus {
	// /** 已代付 */
	// public static final String HASPAID = "0";
	// /** 未代付 */
	// public static final String NOTHASPAID = "1";
	// }

	/**
	 * @author WQJ 账务代付状态(0:已代付 1:待确认2未代付)
	 */
	public static class AccountPaidStatus {
		/** 已代付 */
		public static final String HASPAID = "0";
		/** 待确认 */
		public static final String TOCONFIRM = "1";
		/** 未代付 */
		public static final String NOTHASPAID = "2";
	}

	/**
	 * @author wqj 上门收款业务字典类型
	 */
	public static class ClDictType {
		/** 业务类型 */
		public static final String DOORORDER_BUSINESS_TYPE = "door_businesstype";
		/** 状态类型 HZY 2020/06/04 */
		public static final String SYS_CLEAR_TYPE = "sys_clear_type";
		/** 申请方式类型 HZY 2020/06/04 */
		public static final String DOOR_METHOD_TYPE = "door_method_type";
	}

	/**
	 * 状态类型
	 * 
	 * @author XL
	 * @version 2019年7月2日
	 */
	public static class Status {
		/** 在用 */
		public static final String REGISTER = "0";
		/** 分配 */
		public static final String TASK_DOWN = "1";
		/** 已收回 */
		public static final String CONFIRM = "2";
		/** 已清分 */
		public static final String CLEAR = "3";
		/** 冲正 */
		public static final String REVERSE = "4";
		/** 已平账 add:zxk */
		public static final String CLEAR_ACCOUNT = "5";
	}

	/**
	 * 预约方式类型
	 * 
	 * @author XL
	 * @version 2019年7月2日
	 */
	public static class MethodType {
		/** PC端 */
		public static final String METHOD_PC = "1";
		/** 微信端 */
		public static final String METHOD_WECHAT = "2";
		/** PDA */
		public static final String METHOD_PDA = "3";
		/** 存款设备 */
		public static final String METHOD_EQUIPMENT = "4";
	}

	/**
	 * 分配状态
	 *
	 * @author yinkai
	 * @date 2019年7月3日
	 */
	public static class AllotStatus {
		/** 未分配 */
		public static final String UNALLOTTED = "0";
		/** 已分配 */
		public static final String ALLOTTED = "1";
		/** 已确认 */
		public static final String CONFIRMED = "2";
		/** 驳回 */
		public static final String REJECT = "3";
	}

	/**
	 * @author wqj 账务出入库类型：0：出库 1：入库
	 */
	public static class AccountsInoutType {
		/** 出库 */
		public static final String ACCOUNTS_OUT = "0";
		/** 入库 */
		public static final String ACCOUNTS_IN = "1";

	}

	/**
	 * @author wanglin 状态 (1：登记，2：冲正)
	 */
	public static class StatusType {
		/** 登记 */
		public static final String CREATE = "1";
		/** 冲正 */
		public static final String DELETE = "2";
		/** 已平账 add:zxk */
		public static final String CLEAR_ACCOUNT = "5";
	}

	/**
	 * 差错类型
	 * 
	 * @author XL
	 * @version 2019年7月8日
	 */
	public static class ErrorType {
		/** 假币 */
		public static final String COUNTERFEIT_CURRENCY = "1";
		/** 长款 */
		public static final String LONG_CURRENCY = "2";
		/** 短款 */
		public static final String SHORT_CURRENCY = "3";
	}

	/**
	 * 差错记录是否已经处理
	 * 
	 * @author WQJ
	 * @version 2019年7月18日
	 */
	public static class ErrorIsTakeUp {
		/** 已处理 */
		public static final String TAKEUP = "0";
		/** 未处理 */
		public static final String NOTTAKEUP = "1";
		/** 已结算 */
		public static final String SETTLEMENT = "2";

	}

	/**
	 * 清机加钞款袋状态类型
	 * 
	 * @author ZXK
	 * @version 2019年7月30日
	 */
	public static class BagStatus {
		/** 使用完成 */
		public static final String FINISH = "1";
		/** 正在使用 */
		public static final String USERING = "0";

	}

	/**
	 * 清机加钞管理--清机类型
	 * 
	 * @author ZXK
	 * @version 2019年7月30日
	 */
	public static class ClearStatus {
		/** 存款 */
		public static final String DEPOSIT = "0";
		/** 清机 */
		public static final String CLEAR = "1";
		/** 冲正 */
		public static final String DELETE = "2";

	}

	/**
	 * 机具管理--连线状态
	 * 
	 * @author lihe
	 * @version 2019年8月1日
	 */
	public static class ConnStatus {
		/** 正常 */
		public static final String NORMAL = "01";
		/** 停用 */
		public static final String PAUSED = "02";
		/** 关机 */
		public static final String SHUT_DOWN = "03";
		/** 故障锁定 */
		public static final String BREAK_DOWN = "04";
		/** 异常 */
		public static final String UNUSUAL = "05";
	}

	/**
	 * 清机任务--任务类型
	 * 
	 * @author yinkai
	 * @version 2019年8月12日
	 */
	public static class PlanType {
		/** 固定任务 */
		public static final String CERTAINLY = "01";
		/** 临时任务 */
		public static final String TEMPORARILY = "02";
	}

	/**
	 * 存款方式字典value值
	 * 
	 * @author WQJ
	 * @version 2019年11月2日
	 */
	public static class SaveMethod {
		/** 速存存款 */
		public static final String CASH_SAVE = "01";
		/** 强制存款 */
		public static final String BAG_SAVE = "02";
		/** 其他存款 */
		public static final String OTHER_SAVE = "03";
		/** 微信存款 */
		public static final String WECHAT_SAVE = "04";
	}

	/**
	 * 存款方式字典类型
	 * 
	 * @author WQJ
	 * @version 2019年11月2日
	 */
	public static class SaveMethodType {
		/** 存款方式 */
		public static final String SAVE_METHOD_TYPE = "save_method";
	}

	/**
	 * 存款异常状态类型
	 * 
	 * @author zxk
	 * @version 2019年11月14日
	 */
	public static class ExceptionStatus {
		/** 登记 */
		public static final String REGISTER = "0";
		/** 异常 */
		public static final String CONFIRM = "1";
		/** 已处理 */
		public static final String PROCESSED = "2";
	}

	/**
	 * 存款异常类型
	 * 
	 * @author wqj
	 * @version 2019年11月14日
	 */
	public static class ExceptionType {
		/** 存款异常 */
		public static final String SAVEERROR = "1";
		/** 数据缺失异常 */
		public static final String DATAMISSING = "2";
	}

	/**
	 * 存款方式字典名称
	 * 
	 * @author ZXK
	 * @version 2019年11月18日
	 */
	public static class SaveMethodName {
		/** 速存存款 */
		public static final String CASH_SAVE = "速存存款";
		/** 强制存款 */
		public static final String BAG_SAVE = "强制存款";
		/** 其他存款 */
		public static final String OTHER_SAVE = "其他存款";

	}

	/**
	 * 存款异常币种常量
	 * 
	 * @author ZXK
	 * @version 2019年11月20日
	 */
	public static class Currency {
		/** 人民币 */
		public static final String CNY = "1";

	}

	/**
	 * 接口参数 0805 常量
	 * 
	 * @author ZXK
	 * @version 2019年11月20日
	 */
	public static class ServiceParameter {
		/** 接口编号名称 */
		public static final String SERVICE_NO = "serviceNo";
		/** 接口编号代码 */
		public static final String NO = "0805";
		/** 机具编号 */
		public static final String EQUIPMENT_ID = "eqpId";
		/** 存款类型 */
		public static final String BUSINESS_TYPE = "businessType";
		/** 包号 */
		public static final String BAG_NO = "bagNo";
		/** 总金额 */
		public static final String TOTAL_AMOUNT = "totalAmount";
		/** 凭条号 */
		public static final String TICKER_TAPE = "tickerTape";
		/** 用户ID */
		public static final String USER_ID = "userId";
		/** 币种 */
		public static final String CURRENCY = "currency";
		/** 明细列表 */
		public static final String DETAIL = "detail";

		/** 存款方式 */
		public static final String TYPE = "type";
		/** 金额 */
		public static final String AMOUNT = "amount";
		/** 面值 */
		public static final String DENOMINATION = "denomination";
		/** 张数 */
		public static final String COUNT = "count";
		/** 面值ID */
		public static final String ID = "ID";
		/** 存款备注 */
		public static final String REMARKS_KEY = "remarks";
		/** 开始时间 */
		public static final String START_TIME_KEY = "startTime";
		/** 结束时间 */
		public static final String END_TIME_KEY = "endTime";
		/** 耗时 */
		public static final String COST_TIME_KEY = "costTime";
	}

	/**
	 * 结算类型
	 * 
	 * @author wqj
	 * @version 2019年12月2日
	 */
	public static class SettlementType {
		/** 门店存款 */
		public static final String doorSave = "01";
		/** 存款差错 */
		public static final String saveError = "02";
		/** 传统存款 */
		public static final String traditionalSave = "03";
		/** 电子线下 */
		public static final String ELECTRONIC_OFFLINE_SAVE = "04";
	}

	/**
	 * 机构编码常亮
	 * 
	 * @author yinkai
	 * @version 2019年12月11日
	 */
	public static class OfficeCode {
		public static final String ZHANGJIAGANG = "officeId.zhangjiagang";
	}

	/** 7位码长度配置 */
	public static final String SEVEN_CODE_SIZE = "sevenCode.size";

	/** 油站编码长度配置 */
	public static final String PETROL_STATION_CODE_SIZE = "petrolStationCode.size";

	/**
	 * 差错类型名称
	 * 
	 * @author ZXK
	 * @version 2020年3月9日
	 */
	public static class ErrorTypeName {
		/** 假币1 */
		public static final String COUNTERFEIT_CURRENCY = "假币";
		/** 长款2 */
		public static final String LONG_CURRENCY = "长款";
		/** 短款3 */
		public static final String SHORT_CURRENCY = "短款";
	}

	/**
	 * 差错状态
	 * 
	 * @author gzd
	 * @version 2020年3月9日
	 */
	public static class ErrorStatus {
		/** 登记 0 */
		public static final String CREATE = "0";
		/** 冲正 4 */
		public static final String DELETE = "4";
		/** 已平账 5 */
		public static final String CLEAR_ACCOUNT = "5";
	}

	/**
	 * 差错状态名称
	 * 
	 * @author gzd
	 * @version 2020年3月9日
	 */
	public static class ErrorStatusName {
		/** 登记 0 */
		public static final String CREATE = "登记";
		/** 冲正 4 */
		public static final String DELETE = "冲正";
		/** 已平账 5 */
		public static final String CLEAR_ACCOUNT = "已平账";
	}

	/**
	 *  
	 * 
	 * @author ZXK
	 * @version 2020年4月7日
	 */
	public static class ExportDayReport {
		/** 壳牌客户账号 */
		public static final String shellAccount = "32250198903600000244";
		/** 往来款 */
		public static final String useName = "往来款";
	}

	/**
	 * 系统间对账相关常量
	 *
	 * @author WQJ
	 * @version 2020年3月5日
	 */
	public static class CheckAccount {
		/** 机具ID列表 */
		public static final String EQUIPMENTIDS = "equipmentIds";
		/** 返回数据列表 */
		public static final String DATA = "data";
		/** 设备ID */
		public static final String EQPID = "eqpId";
		/** 当前设备存款总金额 */
		public static final String AMOUNT = "amount";
		/** 异常信息 */
		public static final String ERRORMESSAGE = "系统未上传";
		/** 机具总金额请求路径 */
		public static final String URLEQUIP = "http://di.szyh.test.jljrfw.cn/rcd/user/dalian/loadEqpsAmount";
		/** 机具每笔存款信息请求路径 */
		public static final String URLSAVE = "http://di.szyh.test.jljrfw.cn/rcd/user/dalian/loadEqpsDetail";
		/** 结算时间 */
		public static final String ENDTIME = "endTime";

	}

	/**
	 * 机构对应权限级别 权限强度 从低往高(0-7)
	 *
	 * @author ZXK
	 * @version 2020年5月20日
	 */
	public static class AuthorizeRank {
		/** 顶级机构0 */
		public static final String ROOT_RANK = "0";
		/** 云平台7 */
		public static final String DIGITAL_PLATFORM_RANK = "1";
		/** 区域运行商1 */
		public static final String CENTRAL_BANK_RANK = "2";
		/** 清分中心6 */
		public static final String CLEAR_CENTER_RANK = "3";
		/** 公司3 */
		public static final String COFFER_RANK = "4";
		/** 商户 9 */
		public static final String MERCHANT_RANK = "5";
		/** 门店 8 */
		public static final String STORE_RANK = "6";
		/** 油站编码10 */
		public static final String PETROL_CODE_RANK = "7";

	}

	/**
	 * 授权类型
	 * 
	 *
	 * @author ZXK
	 * @version 2020年5月20日
	 */
	public static class AuthorizeType {
		/** 拆箱 */
		public static final String CHECK_CASH = "0";
		/** 日结汇款 */
		public static final String REPORT_PAID = "1";

	}

	/**
	 * 清点差错导出常量
	 * 
	 * @author ZXK
	 * @version 2020年5月12日
	 */
	public static class ErrorExport {
		/** 登记 */
		public static final String REGISTER = "登记";
		/** 冲正 */
		public static final String REVERSE = "冲正";
		/** 长款 */
		public static final String LONG = "长款";
		/** 短款 */
		public static final String SHORT = "短款";
		/** 假币 */
		public static final String COUNTERFEIT = "假币";
		/** 已平账 */
		public static final String CLEAR_ACCOUNT = "已平账";
	}

	/**
	 * 日结汇款标准常量
	 * 
	 *
	 * @author ZXK
	 * @version 2020年7月29日
	 */
	public static class ReportPaid {
		/** 日结汇款 */
		public static final String REPORT_PAID = "6";
		/** 清分汇款 */
		public static final String CLEAR_PAID = "7";

	}

	/**
	 * 清分机状态
	 * 
	 * 1正常，2故障，3掉线
	 * 
	 * @author GJ
	 *
	 */
	public static class ClearMachineStatus {
		// 正常
		public static final String NORMAL = "1";
		// 故障
		public static final String FAULT = "2";
		// 掉线
		public static final String OUTLINE = "3";
	}

	/**
	 * 打印机状态
	 * 
	 * 1正常，2故障，3掉线，4缺纸
	 * 
	 * @author GJ
	 *
	 */
	public static class PrinterStatus {
		// 正常
		public static final String NORMAL = "1";
		// 故障
		public static final String FAULT = "2";
		// 掉线
		public static final String OUTLINE = "3";
		// 缺纸
		public static final String NOPAPER = "4";
	}

	/**
	 * 仓门状态
	 * 
	 * 1正常，2故障，3掉线，4打开
	 * 
	 * @author GJ
	 *
	 */
	public static class DoorStatus {
		// 正常
		public static final String NORMAL = "1";
		// 故障
		public static final String FAULT = "2";
		// 掉线
		public static final String OUTLINE = "3";
		// 打开
		public static final String OPEN = "4";
	}
}
