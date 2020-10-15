package com.coffer.businesses.common;

/**
 * 常量类
 * 
 * @author Clark
 * @date 2014年5月11日
 */
public class Constant {

	public static final String SESSION_USER_ID = "userId";

	/**
	 * 异常消息国际化标识key前缀
	 */
	public static final String MESSAGE_COMMON_ERROR_PREFIX = "message.";

	/** 处理成功失败相关（0：成功/1：失败） */
	public static final String SUCCESS = "0";
	public static final String FAILED = "1";

	/** 图形常量 */
	public static class Punctuation {
		/** 符号：半角逗号 */
		public static final String COMMA = ",";
		/** 符号：半角空格 */
		public static final String HALF_SPACE = " ";
		/** 符号：半角分号 */
		public static final String HALF_SEMICOLONE = ";";
		/** 符号：下划线 */
		public static final String HALF_UNDERLINE = "_";
		/** 符号：半角冒号 */
		public static final String HALF_COLON = ":";
		/** 符号：半角左圆括号 **/
		public static final String HALF_LEFT_ROUND_BRACKETS = "(";
		/** 符号：半角右圆括号 **/
		public static final String HALF_RIGHT_ROUND_BRACKETS = ")";
		/** 连字符 **/
		public static final String HYPHEN = "-";
	}

	/** 图形常量 */
	public static class Charts {
		public static final String PAGE_DATA = "pageData";
		public static final String TITLE_TEXT = "titleText";
		public static final String TITLE_SUB_TEXT = "titleSubText";
		public static final String SERIES_NAME = "seriesName";
		public static final String LEGEND_NAME = "legendName";
		public static final String LEGEND_DATA = "legendData";
		public static final String DATA = "data";
	}

	/** 日期常量 */
	public static class Dates {
		public static final String DATE_MIN = "1900-01-01";
		public static final String DATE_MAX = "9999-12-31";
		public static final String FORMATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
		public static final String FORMATE_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
		public static final String FORMATE_YYYY_MM_DD = "yyyy-MM-dd";
		public static final String FORMATE_YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
		public static final String FORMATE_YYYYMMDDHHMMSSSSSSSS = "yyyyMMddHHmmssSSSSSS";
		/**
		 * 日期格式化：YYYYMMDD
		 */
		public static final String FORMATE_YYYYMMDD = "yyyyMMdd";
		/**
		 * 日期格式化：YYMMDD
		 */
		public static final String FORMATE_YYMMDD = "yyMMdd";
	}

	/** 箱袋类型 */
	public static class BoxType {
		/** 款箱 */
		public static final String BOX_PARAGRAPH = "11";
		/** 尾箱 */
		public static final String BOX_TAIL = "12";
		/** 款包 */
		public static final String BOX_BAG = "13";
		/** 钞箱 */
		public static final String BOX_NOTE = "14";

		/** 票据_稽核包 */
		public static final String BOX_BILL_1 = "21";
		/** 票据_重空凭证 */
		public static final String BOX_BILL_2 = "22";
		/** 票据_交换包 */
		public static final String BOX_BILL_3 = "23";

		/** 小车 */
		public static final String BOX_CAR = "91";
		/* 追加钞箱类型及ATM机 修改人：sg 修改日期：2017-11-07 begin */
		/**
		 * 回收箱
		 */
		public static final String BOX_BACK = "41";
		/**
		 * 取款箱
		 */
		public static final String BOX_GET = "42";
		/**
		 * 存款箱
		 */
		public static final String BOX_DEPOSITE = "43";
		/**
		 * 循环箱
		 */
		public static final String BOX_CYCLE = "44";
		/** ATM机 */
		public static final String BOX_ATM = "99";
		/* end */

	}

	/** 箱袋状态 */
	public static class BoxStatus {
		/** 空箱 */
		public static final String EMPTY = "10";
		/** 在网点 */
		public static final String BANK_OUTLETS = "11";
		/** 在途 */
		public static final String ONPASSAGE = "12";
		/** 在库房 */
		public static final String COFFER = "13";
		/** 在整点室 */
		public static final String CLASSFICATION = "14";
		/** 在票据室 */
		public static final String BILL = "15";
		/** 在商行 */
		public static final String BUSSNESS_BANK = "25";
		/**
		 * ATM: 10 空箱/16加钞/12在途/17 在用 /18清点
		 * /19退库/20加钞寄库/21清点寄库/22退库寄库/23待出库/24清机在途
		 */
		public static final String ATM_BOX_STATUS_ADD_MONEY = "16";
		public static final String ATM_BOX_STATUS_USE = "17";
		public static final String ATM_BOX_STATUS_CLEAR = "18";
		public static final String ATM_BOX_STATUS_BACK = "19";
		public static final String ATM_BOX_STATUS_ADD_STORE = "20";
		public static final String ATM_BOX_STATUS_CLEAR_STORE = "21";
		public static final String ATM_BOX_STATUS_BACK_STORE = "22";
		public static final String ATM_BOX_STATUS_PREPARE_OUT = "23";
		public static final String ATM_BOX_STATUS_CLEAR_IN = "24";

		/**
		 * ATM: 1加钞/3 在用 /4清点 /5 退库/6 加钞寄库/7清点寄库/8退库寄库/9待出库/10 空箱/12在途
		 * 
		 * author qph 2017-10-20 add
		 */
		public static final String ATM_BOX_STATUS_EMPTY = "10";
		public static final String ATM_BOX_STATUS_OUT = "12";
		// public static final String ATM_BOX_STATUS_CLEAR_IN = "10";
	}

	/** 机构类型 */
	public static class OfficeType {
		/// ** 机构类型 0：顶级机构；1：总金库；2：分金库；3：支行；4：网点；5：分组 */
		// public static final String ROOT = "0";
		// public static final String HEAD_COFFERS = "1";
		// public static final String BRANCH_COFFERS = "2";
		// public static final String SUB_BRANCH = "3";
		// public static final String OUTLETS = "4";
		// public static final String GROUP = "5";
		// 0：顶级机构
		public static final String ROOT = "0";
		// 1：人民银行
		public static final String CENTRAL_BANK = "1";
		// 2：商业银行
		public static final String COMMERCIAL_BANK = "2";
		// 3：金库
		public static final String COFFER = "3";
		// 4：网点
		public static final String OUTLETS = "4";
		// 5：分组
		public static final String GROUP = "5";
		// 6：清分中心
		public static final String CLEAR_CENTER = "6";
		// 7：数字化平台
		public static final String DIGITAL_PLATFORM = "7";
		// 8：门店
		public static final String STORE = "8";
		// 9：商户
		public static final String MERCHANT = "9";
		// 10：油站编码
		public static final String PETROL_CODE = "10";

	}

	/** 网点上缴类型 */
	public static class outletsIntoFlag {
		/**
		 * 上缴金库
		 */
		public static final String HAND_IN = "0";
		/**
		 * 押运寄存
		 */
		public static final String ESCORT_ACCEPT = "1";
	}

	/** 系统用户类型 */
	public static class SysUserType {
		/**
		 * 系统用户类型 (10：系统管理员、 11：金库主管、 12：金库操作员、 13：整点室主管、 14：整点室操作员、 15：网点主管、
		 * 16：网点操作员、 17：票据室主管、 18：票据室操作员)
		 */
		/** 10 系统管理员 */
		public static final String SYSTEM = "10";
		/** 11 金库主管 */
		public static final String COFFER_MANAGER = "11";
		/** 12 金库操作员 */
		public static final String COFFER_OPT = "12";
		/** 13 整点室主管 */
		public static final String CLASSFICATION_MANAGER = "13";
		/** 14 整点室操作员 */
		public static final String CLASSFICATION_OPT = "14";
		/** 15 网点主管 */
		public static final String BANK_OUTLETS_MANAGER = "15";
		/** 16 网点操作员 */
		public static final String BANK_OUTLETS_OPT = "16";
		/** 17 票据室主管 */
		public static final String BILL_MANAGER = "17";
		/** 18 票据室操作员 */
		public static final String BILL_OPT = "18";
		/** 19 人民银行管理员 */
		public static final String CENTRAL_MANAGER = "19";
		/** 20 人民审批员 */
		public static final String CENTRAL_OPT = "20";

		/** 21 清分中心管理员 */
		public static final String CLEARING_CENTER_MANAGER = "21";
		/** 22 清分中心操作员 */
		public static final String CLEARING_CENTER_OPT = "22";

		/** 23 人民银行调拨员 */
		public static final String CENTRAL_ALLOCATE_OPT = "23";
		/** 24 人民银行管库员 */
		public static final String CENTRAL_STORE_MANAGER_OPT = "24";
		/** 25 人民银行复点管理员 */
		public static final String CENTRAL_RECOUNT_MANAGER_OPT = "25";
		/** 40 清分中心清机员 */
		public static final String CLEARING_CENTER_CLEAR_MAN = "40";
		/** 41 商户管理员 */
		public static final String ENTERPRISE_MANAGER = "41";
        /** 42 门店柜员 */
        public static final String SHOP_TELLER = "42";
        /** 43 公司管理员 */
        public static final String COMPANY_MANAGER = "43";
		/** 90 押运人员 */
		public static final String ESCORT = "90";
	}

	/**
	 * @author Clark 消息代码 例如：E1012：E代表错误消息，10：代表模块编号，12：代表消息编号
	 *         10：库房管理，20：调缴管理，30：整点清分，40：ATM配钞，50：上门收款
	 */
	public static class MessageCode {
		public static final String SUCCESS = "message.I0005";
		public static final String E1001 = "message.common.E1001";
	}

	/**
	 * 机构款箱属于库外库内寄存
	 * 
	 * @author LLF
	 *
	 */
	public static class sysIntoCofferFlag {
		/* 库内寄库 */
		public static final String inFlag = "0";
		/* 库外寄库 */
		public static final String outFlag = "1";
	}

	/**
	 * 有效表示
	 * 
	 * @author chengshu
	 */
	public static class deleteFlag {
		/* 有效 */
		public static final String Valid = "0";
		/* 无效 */
		public static final String Invalid = "1";
		/* 换卡 */
		public static final String Replace = "3";
	}

	/**
	 * 补齐字符串方向
	 * 
	 * @author yuxixuan
	 *
	 */
	public static class fillStrDirection {
		/** 左侧补齐 */
		public static final String FILL_STR_LEFT = "left";
		/** 右侧补齐 */
		public static final String FILL_STR_RIGHT = "right";
	}

	/**
	 * 
	 * 线路常量
	 * 
	 * @author niguoyong
	 */
	public class RouteInfo {

		/**
		 * 常规线路
		 */
		public static final String CONVENTIONAL_ROUTE = "0";
		/**
		 * 临时线路
		 */
		public static final String TEMPORARY_ROUTE = "1";

		/**
		 * 临时线路命名头
		 */
		public static final String TEMPORARY_ROUTE_NAME_HEAD = "临时线路";

		/**
		 * 线路不可以重复绑定车辆
		 */
		public static final String ROUTE_DOUBLE_BINDING_CAR = "0";

		/**
		 * 线路不可以重复绑定人员
		 */
		public static final String ROUTE_DOUBLE_BINDING_ESCORT = "0";
	}

	/**
	 * 
	 * @author LF 押运人员常量
	 * 
	 */
	public class Escort {
		/**
		 * 押运人员RFID生成类型
		 */
		public static final String RFID_TYPE = "8";
		/**
		 * 未绑定RFID
		 */
		public static final String UN_BINDING_FLAG = "0";
		/**
		 * 绑定Rfid
		 */
		public static final String BINDING_FLAG = "1";
		/**
		 * 人员闲置
		 */
		public static final String UN_TASK_FLAG = "0";
		/**
		 * 任务中
		 */
		public static final String TASK_FLAG = "1";
		/**
		 * 人员未绑定线路
		 */
		public static final String UN_BINDING_ROUTE = "0";
		/**
		 * 人员绑定线路
		 */
		public static final String BINDING_ROUTE = "1";
		/**
		 * 押运人员是否使用RFID开关
		 */
		public static final String USER_RFID = "1";
		/**
		 * 线路是否使用人员开关
		 */
		public static final String USER_ESCORT = "1";
		/**
		 * 所有人员List
		 */
		public static final String ALL_ESCORT_LIST = "allEscortList";
		/**
		 * 未绑定线路人员List
		 */
		public static final String NO_BINDING_ESCORT_LIST = "noBindingEscortList";

		public static final String RFID_ESCORT_TYPE = "A";

	}

	/**
	 * 类别
	 * 
	 * @author wangbaozhong
	 *
	 */
	public static class MoneyType {
		/** ATM币 */
		public static final String ATM_MONEY = "01";
		/** 流通币 */
		public static final String CIRCULATION_MONEY = "02";
		/** 残损币 **/
		public static final String DEMANGED_MONEY = "03";
		/** 假币 **/
		public static final String COUNTERFEIT_MONEY = "04";
		/** 待整点币 */
		public static final String COUNTWAIT_MONEY = "05";
		/** 发行基金 */
		public static final String ISSUE_FUND = "06";

		/** 现金类别列表 */
		public static final String[] MoneyTypeList = { ATM_MONEY, CIRCULATION_MONEY, DEMANGED_MONEY, COUNTERFEIT_MONEY,
				COUNTWAIT_MONEY };
	}

	/**
	 * @author chengshu 币种
	 */
	public static class Currency {
		/** 人民币 */
		public static final String RMB = "101";
		/** 美元 */
		public static final String USD = "102";
		/** 欧元 */
		public static final String EUR = "103";
		/** 英镑 */
		public static final String GBP = "104";
		/** 日元 */
		public static final String JPY = "105";
		/** 港币 */
		public static final String HKD = "106";
	}

	/**
	 * @author chengshu 面值种别：纸币/硬币
	 */
	public static class DenominationType {
		/** 人民币：纸币 */
		public static final String RMB_PDEN = "cnypden";
		/** 人民币：硬币 */
		public static final String RMB_HDEN = "cnyhden";

		/** 美元：纸币 */
		public static final String USD_PDEN = "usdpden";
		/** 美元：硬币 */
		public static final String USD_HDEN = "usdhden";

		/** 欧元：纸币 */
		public static final String EUR_PDEN = "eurpden";
		/** 欧元：硬币 */
		public static final String EUR_HDEN = "eurhden";

		/** 英镑：纸币 */
		public static final String GBP_PDEN = "gbppden";
		/** 英镑：硬币 */
		public static final String GBP_HDEN = "gbphden";

		/** 日元：纸币 */
		public static final String JPY_PDEN = "jpypden";
		/** 日元：硬币 */
		public static final String JPY_HDEN = "jpyhden";

		/** 港币：纸币 */
		public static final String HKD_PDEN = "hkdpden";
		/** 港币：硬币 */
		public static final String HKD_HDEN = "hkdhden";
	}

	/**
	 * 类别
	 * 
	 * @author wangbaozhong
	 *
	 */
	public static class CashType {
		/** 纸币 **/
		public static final String PAPER = "1";
		/** 硬币 **/
		public static final String COIN = "2";
	}

	/**
	 * 套别
	 * 
	 * @author chengshu
	 *
	 */
	public static class SetType {
		/** 忽略套别 **/
		public static final String SET_0 = "0";
		/** 4套 **/
		public static final String SET_4 = "4";
		/** 5套 **/
		public static final String SET_5 = "5";
	}

	/**
	 * 单位
	 * 
	 * @author chengshu
	 *
	 */
	public static class Unit {
		/** 捆 **/
		public static final String bundle = "101";
		/** 把 **/
		public static final String handful = "102";
		/** 张 **/
		public static final String piece = "103";
		/** 盒 **/
		public static final String box = "104";
		/** 卷 **/
		public static final String reel = "105";
		/** 枚 **/
		public static final String coin = "106";
		/** 一元箱 **/
		public static final String coin_box_1 = "107";
		/** 五角箱 **/
		public static final String coin_box_05 = "108";
		/** 一角箱 **/
		public static final String coin_box_01 = "109";

		/** 包 **/
		public static final String bag = "110";
	}

	/**
	 * 同业标识
	 * 
	 * @author wangbaozhong
	 *
	 */
	public static class TradeFlag {
		/** 行内 */
		public static final String Bank = "0";
		/** 同业 */
		public static final String Trade = "1";
	}

	/**
	 * 压缩图片参数设置
	 * 
	 * @author LLF
	 *
	 */
	public static class ImageSeting {
		/** 宽度 */
		public static final String weight = "image.setting.weight";
		/** 高度 */
		public static final String height = "image.setting.height";
		/** 格式 */
		public static final String zipFormat = "image.setting.zip.format";
		/** 上传图片标记 YES */
		public static final String UPLOAD_FLAG_YES = "yes";
		/** 不上传图片标记 */
		public static final String UPLOAD_FLAG_NO = "no";
		/** 文件保存目录相对路径 */
		public static final String IMAGE_BASE_PATH = "upload";
		/** 文件的目录名 */
		public static final String IMAGE_DIR_NAME = "images";
		/** 图片文件类型 */
		public static final String IMAGE_TYPE = "jpg";

		/** 图片文件URL */
		public static final String PIC_URL_KEY = "picUrl";
		/** 图片上传成功标识 */
		public static final String RTN_FLG_SUCCESS_KEY = "success";
		/** 图片上传失败标识 */
		public static final String RTN_FLG_FAILED_KEY = "failed";
		/** 图片上传状态 */
		public static final String RTN_STATUS_FLG_KEY = "status";
		/** 图片上传消息 */
		public static final String MSG_KEY = "msg";
		/** 上传图片文件名 */
		public static final String PIC_FILE_NAME = "picFileName";
		/** 文件名与扩展名间的圆点 */
		public static final String FILE_DOT = ".";
	}

	/**
	 * 库存区域报警颜色
	 * 
	 * @author WangBaozhong
	 *
	 */
	public static class SlamCode {
		/** 黄色警告 */
		public static final String SLAM_COLOR_YELLOW = "area_yellow";
		/** 红色警告 */
		public static final String SLAM_COLOR_RED = "area_red";
		/** 无警告 */
		public static final String SLAM_COLOR_NONE = "area";
		/** 不可用 */
		public static final String SLAM_COLOR_INVILD = "area_invild";
	}

	/**
	 * 是否必须出库标记
	 * 
	 * @author WangBaozhong
	 *
	 */
	public static class NecessaryOut {
		/** 0：必须 **/
		public static final String NECESSARY_OUT_YES = "0";
		/** 1：非必须 **/
		public static final String NECESSARY_OUT_NO = "1";
	}

	/**
	 * 出入库类型
	 * 
	 * @author chengshu
	 */
	public static class InoutType {
		/** 出库 */
		public static final String Out = "0";
		/** 入库 */
		public static final String In = "1";
	}

	/**
	 * 消息总括
	 * 
	 * @author yanbingxu
	 */
	public static class MessageType {
		/** 系统广播 */
		public static final String BROADCAST = "01";
		/** 调拨业务 */
		public static final String ALLOCATION = "02";
		/** 清分业务 */
		public static final String CLEAR = "03";
		/* 追加消息类型 异常业务 修改人：hzy 修改日期：2020-04-14 begin */
		/** 异常业务 */
		public static final String EXCEPTION = "04";
		/** 异常业务 存款异常 类型 */
		public static final String EXCEPTIONTYPE = "10";
		/** 异常业务 机具报警 类型 */
		public static final String EQUEXCEPTIONTYPE = "11";
		/* 追加消息类型 异常业务 修改人：hzy 修改日期：2020-04-14 end */
		/* 追加消息类型 日结业务 修改人：lihe 修改日期：2020-06-12 begin */
		/** 日结业务 */
		public static final String DAY_REPORT = "05";
		/** 日结完成 */
		public static final String DAY_REPORT_FINISH = "0";
		/** 日结完成状态 */
		public static final String DAY_REPORT_STATUS = "12";
		/* 追加消息类型 日结业务 修改人：lihe 修改日期：2020-06-12 end */
	}

	/** 每次执行更新数量 */
	public static final int NUMBER_PER_UPDATE = 500;

	/** 现钞交接管理员和现钞复合员类型 */
	public static final String CONNECT_COMPLEX_TYPE = "connect.complex.businessType";

	/** 现钞交接管理员类型 */
	public static final String CONNECT_PERSON_TYPE = "connect.person.businessType";

	/** 现钞复合员类型 */
	public static final String COMPLEX_PERSON_TYPE = "complex.person.businessType";

	/** 现钞清分人员、现钞清分管理员 */
	public static final String CLEAR_MANAGE_TYPE = "clear.manage.businessType";

	/** 现钞差错管理员类型 */
	public static final String CLEAR_ERROR_TYPE = "clear.error.businessType";

	/** 备付金管理员类型 */
	public static final String CLEAR_PROVISIONS_TYPE = "clear.userType.provision";

	/** 柜员类型 */
	public static final String CLEAR_TELLER_BUSINESSTYPE = "clear.teller.businessType";

	/**
	 * 数据库类型
	 * 
	 * @author xp 2017-10-20
	 */

	public static class jdbcType {
		/** oracle */
		public static final String ORACLE = "oracle";
		/** mysql */
		public static final String MYSQL = "mysql";
	}

	/**
	 * 
	 * Title: StrBooleanVal
	 * <p>
	 * Description: 真假值常量
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2017年11月16日 下午4:58:17
	 */
	public static class StrBooleanVal {
		/** 为真时 */
		public static final String TRUE_VALUE = "0";
		/** 为假时 */
		public static final String FALSE_VALUE = "1";
	}

	/**
	 * 
	 * Title: FirstPage
	 * <p>
	 * Description: 首页用常量
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2017年11月28日 上午9:27:08
	 */
	public static class FirstPage {
		/** 在线地图 */
		public static final String MAP_ONLINE = "0";
		/** 离线地图 */
		public static final String MAP_OFFLINE = "1";
	}

	/**
	 * 
	 * Title: AtmBindingInfoVlaue
	 * <p>
	 * Description: 加钞绑定用常量
	 * </p>
	 * 
	 * @author wangbaozhong
	 * @date 2017年11月28日 上午9:27:08
	 */
	public static class AtmBindingInfoValue {
		/** 数据来源，0：来自接口 */
		public static final String DATA_TYPE_INTERFACE = "0";
		/** 数据来源，1：页面补录 */
		public static final String DATA_TYPE_PAGEINPUT = "1";
	}

	/**
	 * 
	 * Title: MapPointValue
	 * <p>
	 * Description: 地图markpoint项data数据的value常量
	 * </p>
	 * 
	 * @author wanghan
	 * @date 2017年11月29日 下午1:27:59
	 */
	public static class MapPointValue {
		/** 平台value值常量 */
		public static final String PLATFORM_VALUE = "80";
		/** 人行value值常量 */
		public static final String GENTRAL_VALUE = "20";
		/** 金库value值常量 */
		public static final String COFFER_VALUE = "60";
		/** 网点value值常量 */
		public static final String OUTLETS_VALUE = "2";
		/** 清分中心value值常量 */
		public static final String CLEAR_VALUE = "70";
		/** 当前机构value值常量 */
		public static final String CURRENT_OFFICE_VALUE = "99";
	}

	/**
	 * 金额类型 备付金01，非备付金02
	 * 
	 * @author dja
	 * @date 2018年4月27日
	 */
	public static class ClearCashType {
		/** 备付金类型 01 */
		public static final String EXESSRESERVE = "01";
		/** 非备付金类型 02 */
		public static final String CASH = "02";
	}

	/**
	 * 人员类型 现钞交接管理员类型31，备付金管理员类型 34
	 * 
	 * @author dja
	 * @date 2018年4月27日
	 */
	/** 现钞交接管理员类型 */
	public static final String CONNECT_PERSON = "31";
	/** 备付金管理员类型 */
	public static final String CLEAR_PROVISIONS = "34";

	/**
	 * 通信状态
	 * 
	 * @author SongYuanYang
	 * @date 2018年5月22日
	 */
	public static class CommunicationStatus {
		/** 成功 */
		public static final String SUCCESS = "00";
		/** 失败 */
		public static final String FAIL = "01";
		/** 待发送 */
		public static final String TO_BE_SENT = "02";
		/** 终止 */
		public static final String TERMINATION = "03";
	}

	/**
	 * 绑定状态
	 * 
	 * @author lihe
	 * @date 2019年6月27日
	 */
	public static class EquipmentStatus {
		/** 已绑定 */
		public static final String BIND = "1";
		/** 未绑定 */
		public static final String NOBIND = "0";
	}

    /**
     * 设备启用状态
     *
     * @author yinkai
     * @date 2019年7月1日
     */
    public static class IsUse {
        /** 设备启用 */
        public static final String IS_USE = "1";
        /** 设备停用 */
        public static final String IS_NOT_USE = "0";
    }

    /**
     * 清机任务状态
     *
     * @author yinkai
     * @date 2019年7月1日
     */
    public static class ClearPlanStatus {
        /** 撤销 */
        public static final String REVERSE = "2";
        /** 任务完成 */
        public static final String COMPLETE = "1";
        /** 未完成 */
        public static final String UNCOMPLETE = "0";
    }
    
    /**
     * 是否进行线上汇款
     *
     * @author WQJ
     * @date 2019年8月14日
     */
    public static class OnlineRemittance {
        /** 是 */
        public static final String TRUE = "0";
        /** 否 */
        public static final String FALSE = "1";
    }
    
    /**
	 * 银行卡绑定状态
	 * 
	 * @author zxk
	 * @date 2019年8月15日
	 */
	public static class BankStatus {
		/** 已绑定 */
		public static final String BIND = "1";
		/** 未绑定 */
		public static final String NOBIND = "0";
	}

	public static class HealthStatus {
		/** 健康监测结果：tomcat正常，数据库异常 */
		public static final String MIDDLEWARE1DB0 = "Middleware1DB0";
		/** 健康监测结果：tomcat正常，数据库正常 */
		public static final String MIDDLEWARE1DB1 = "Middleware1DB1";
	}

	/**
	 * 用户角色常量
	 * @author yinkai
	 */
	public static class RoleId {
		public static final String CLEAR = "719db84f31f64db4a23d2959a5f67899";
	}
	/**
	  * 未绑定机构常量
	  *
	  * @author hzy
	  * @date 2020年04月14日
	  */
	public static class  NoBindOffice{
		/** 未知 */
		public static final String UNKNOWN = "未知";
		/** 未绑定机构*/
		public static final String NOBINDOFFICE = "未绑定机构";
		/** 未知门店机具*/
		public static final String UNKNOWNEQU = "未知门店机具";
	}

	 /**
	  * 发送机构名称
	  *
	  * @author hzy
	  * @date 2020年04月14日
	  */
	public static class OfficeId {
		/**商资易汇企业云服务平台 */
		public static final String SZYH = "10000001";
	}
	 /**
	  * 授权管理常量
	  *
	  * @author haoShijie
	  * @date 2020年05月18日
	  */
	public static class AuthorizeStatus {
        /** 授权开启 */
        public static final String OPEN = "0";
        /** 授权关闭 */
        public static final String CLOSE = "1";
    }
	
	/**
	  * 款袋容量默认值
	  *
	  * @author gj
	  * @date 2020年09月11日
	  */
	public static class BagCpapcity {
		/** 容量10000 */
		public static final String TEN_THOUSAND = "10000";
	}
	
}
