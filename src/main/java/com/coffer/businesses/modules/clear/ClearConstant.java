/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.clear;

import com.coffer.businesses.common.Constant;

/**
 * @author Lemon
 *
 */
public class ClearConstant extends Constant {

	/**
	 * @author wanglin 业务类型
	 */
	public static class BusinessType {
		/** 商行交款 */
		public static final String BANK_PAY = "01";
		/**
		 * 商行取款
		 * 
		 * @author wxz
		 * @version 2017年8月24日
		 */
		public static final String BANK_GET = "02";
		/**
		 * 代理上缴
		 * 
		 * @author sg
		 * @version 2017年8月29日
		 */
		public static final String AGENCY_PAY = "03";
		/**
		 * 人民银行复点入库
		 * 
		 * @author xl
		 * @version 2017年9月1日
		 */
		public static final String PEOPLE_BANK_COMPLEX_POINT_IN = "04";
		/**
		 * 人民银行复点出库
		 * 
		 * @author wxz
		 * @version 2017年8月29日
		 */
		public static final String PEOPLE_BANK_COMPLEX_POINT_OUT = "05";
		/**
		 * 备付金交入
		 * 
		 * @author xl
		 * @version 2017年8月24日
		 */
		public static final String PROVISIONS_IN = "06";
		/**
		 * 备付金取回
		 * 
		 * @author wzj
		 * @version 2017年8月29日
		 */
		public static final String PROVISIONS_OUT = "07";
		/** 复点 */
		public static final String COMPLEX_POINT = "08";
		/** 清分 */
		public static final String CLEAR = "09";
		/**
		 * 柜员调账
		 * 
		 * @author dja
		 * @version 2018年4月23日
		 */
		public static final String ADJUST = "10";
		/**
		 * 差错处理
		 * 
		 * @author xl
		 * @version 2017年9月13日
		 */
		public static final String ERROR_HANDLING = "70";

		
		/**
		 * ATM钞箱拆箱
		 * 
		 * @author wl
		 * @version 2017年9月13日
		 */
		public static final String ATM_CASH_BOX = "72";
		/**
		 * 预约清分
		 * 
		 * @author qph
		 * @version 2017年12月4日
		 */
		public static final String ORDER_CLEAR = "73";
		
		/**
		 * 门店预约
		 * 
		 * @author XL
		 * @version 2018年5月17日
		 */
		public static final String DOOR_ORDER = "74";

	}

	/**
	 * @author wanglin 状态 (1：登记，2：冲正)
	 */
	public static class StatusType {
		/** 登记 */
		public static final String CREATE = "1";
		/** 冲正 */
		public static final String DELETE = "2";
	}

	/**
	 * @author qipeihong 清分状态 (08：已清分，09：清分中)
	 */
	public static class TranStatus {
		/** 已清分 */
		public static final String CLEARING_COMPLETE = "08";
		/** 清分中 */
		public static final String CLEARING = "09";
	}

	/**
	 * @author qipeihong 任务类型 (01：任务分配，02：任务回收)
	 */
	public static class TaskType {
		/** 任务分配 */
		public static final String TASK_DISTRIBUTION = "01";
		/** 任务回收 */
		public static final String TASK_RECOVERY = "02";
		/** 员工工作量 */
		public static final String STAFF_WORKLOAD = "03";
	}

	/**
	 * @author qipeihong 计划类型 (01： 正常清分（计划内） ，02：重复清分（计划外） 03-抽查)
	 */
	public static class PlanType {
		/** 正常清分（计划内） */
		public static final String NORMAL_CLEAR = "01";
		/** 重复清分（计划外） */
		public static final String REPEAT_CLEAR = "02";
		/** 抽查 */
		public static final String CHECK_CLEAR = "03";
	}

	/**
	 * @author qipeihong 是否抽查 (1：否，2：是)
	 */
	public static class CheckStatus {
		/** 抽查（否） */
		public static final String CHECK_NO = "1";
		/** 抽查（是） */
		public static final String CHECK_YES = "2";
	}

	/**
	 * @author qipeihong 任务种类 (01：普通任务，02：抽查任务)
	 */
	public static class CheckType {
		/** 普通任务 */
		public static final String NORMAL_TASK = "01";
		/** 抽查任务 */
		public static final String CHECK_TASK = "02";
	}

	/**
	 * @author xiaoliang 清分组状态 (0：启用，1：停用)
	 */
	public static class ClGroupStatus {
		/** 启用 */
		public static final String START = "0";
		/** 停用 */
		public static final String STOP = "1";
	}

	/**
	 * @author xiaoliang 工位类型 (01：机械清分，02：清分流水线，03：手工清分)
	 */
	public static class WorkingPositionType {
		/** 机械清分 */
		public static final String MECHANICS_CLEAR = "01";
		/** 清分流水线 */
		public static final String ASSEMBLY_LINE_CLEAR = "02";
		/** 手工清分 */
		public static final String MANUAL_CLEAR = "03";
	}
	/**
	 * @author qipeihong 账务类型：1：清点业务 2：复点业务 3：备付金业务
	 */
	public static class AccountsType {
		/** 清点业务 */
		public static final String ACCOUNTS_CLEAR = "1";
		/** 复点业务 */
		public static final String ACCOUNTS_COMPLEX = "2";
		/** 备付金业务 */
		public static final String ACCOUNTS_PROVISIONS = "3";

	}

	/**
	 * @author qipeihong 账务出入库类型：0：出库 1：入库
	 */
	public static class AccountsInoutType {
		/** 出库 */
		public static final String ACCOUNTS_OUT = "0";
		/** 入库 */
		public static final String ACCOUNTS_IN = "1";

	}

	/**
	 * @author qipeihong 账务结账方式：0：自动结账 1：手动结账
	 */
	public static class WindupType {
		/** 自动结账 */
		public static final String WINDUP_AUTO = "0";
		/** 手动结账 */
		public static final String WINDUP_MANUAL = "1";

	}

	/**
	 * @author qipeihong 账务日结有效状态：0：有效 1：无效
	 */
	public static class AccountsStatus {
		/** 有效 */
		public static final String SUCCESS = "0";
		/** 无效 */
		public static final String FAILED = "1";

	}

	/**
	 * @author xiaoliang 差错类型（00：正常差错；01：半值币；02：错值币）
	 */
	public static class SubErrorType {
		/** 正常差错 */
		public static final String NORMAL_ERROR = "00";
		/** 半值币 */
		public static final String HALF_VALUED_COIN = "01";
		/** 错值币 */
		public static final String WRONG_CURRENCY = "02";
	}

	/**
	 * @author xiaoliang 错款类别 (1:假币 2：长款 3：短款)
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
	 * @author xiaoliang 操作类型 (1:现金 )
	 */
	public static class OperationType {
		/** 现金 */
		public static final String CASH = "1";
	}

	/**
	 * @author xiaoliang 两种账务类型(1:现金 2:备付金)
	 */
	public static class AccountCashProType {
		/** 现金 */
		public static final String CASH = "cash";
		/** 备付金 */
		public static final String PROVISIONS = "pro";
	}

	/**
	 * @author xiaoliang 清分业务字典类型
	 */
	public static class ClDictType {
		/** 业务类型 */
		public static final String CLEAR_BUSINESS_TYPE = "clear_businesstype";
		/** 状态 */
		public static final String CL_STATUS_TYPE = "cl_status_type";
		/** 工位类型*/
		public static final String CLEAR_WORKING_POSITION_TYPE = "clear_working_position_type";
	}

	/**
	 * @author xiaoliang 任务管理接口输入参数
	 */
	public static class ClearingParamKey {
		/** 清分人员ID */
		public static final String CLEARING_ID_KEY = "clearingId";
		/** 清分人员姓名 */
		public static final String CLEARING_NAME_KEY = "clearingName";
		/** 任务类型 */
		public static final String TASK_TYPE_KEY = "taskType";
		/** 计划类型 */
		public static final String PLAN_TYPE_KEY = "planType";
		/** 数量 */
		public static final String TASK_NUM_KEY = "taskNum";
		/** 工位类型 */
		public static final String WORKING_POSITION_TYPE = "workingPositionType";
		/** 授权标识 */
		public static final String AUTHORIZE_FLAG_KEY = "authorizeFlag";
		/** 授权标识 */
		public static final String ERROR_FLAG_KEY = "errorFlag";
		/** 未授权 */
		public static final String AUTHORIZE_FALSE = "01";
		/** 已授权 */
		public static final String AUTHORIZE_TRUE = "02";
		/** 分配捆数不足 */
		public static final String TASK_NUM_ERROR = "01";
		/** 交接人员ID */
		public static final String TRANS_ID_KEY = "transId";
		/** 交接人员姓名 */
		public static final String TRANS_NAME_KEY = "transName";
	}

	/**
	 * @author xiaoliang 柜员账务金额类型 (01：备付金，02：非备付金)
	 */
	public static class CashTypeProvisions {
		/** 备付金 */
		public static final String PROVISIONS_TRUE = "01";
		/** 非备付金 */
		public static final String PROVISIONS_FALSE = "02";
	}

	/**
	 * @author qph 银行交接人员开关 (true：开，false：关)
	 */
	public static class JoinPeopleOpen {
		/** 开 */
		public static final String JOINPEOPLEOPEN_TRUE = "0";
		/** 关 */
		public static final String JOINPEOPLEOPEN_FALSE = "1";
	}
	
	/**
	 * @author qph 银行交接人员开关 (true：开，false：关)
	 */
	public static class clearProvisionsOpen {
		/** 开*/
		public static final String CLEARPROVISIONSOPEN_TRUE = "0";
		/** 关 */
		public static final String CLEARPROVISIONSOPEN_FALSE = "1";
	}


}
