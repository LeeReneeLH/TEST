/**
 * @author Administrator
 * @date 2014-10-16
 * 
 * @Description 
 */
package com.coffer.businesses.modules.weChat;


import com.coffer.businesses.common.Constant;

/**
 * @author Lemon
 *
 */
public class WeChatConstant extends Constant {

	/**
	 * @author wanglin 业务类型
	 */
	public static class BusinessType {
		/** 款箱拆箱单号 */
		public static final String CASH_BOX = "71";
		
		/**
		 * 预约清分
		 * 
		 * @author XL
		 * @version 2018年5月17日
		 */
		public static final String ORDER_CLEAR = "73";

		/**
		 * 门店预约
		 * 
		 * @author XL
		 * @version 2018年5月17日
		 */
		public static final String DOOR_ORDER = "74";

        /**
         * 清机任务号
         *
         * @author yinkai
         * @version 2019年8月14日
         */
        public static final String CLEAR_PLAN = "81";
		
	}
	
	/** 延长日 */
	public static final String EXTENDE_DAY = "0";
	
	/**
	 * 申请方式类型
	 * @author wanglin
	 *
	 */
	public static class MethodType{
		
		public static final String METHOD_PC = "1";			//PC端
		
		public static final String METHOD_WECHAT = "2";		//微信端
		
		public static final String METHOD_PDA = "3";		//PDA
		
	}
	
	/**
	 * 微信端业务类型
	 * @author wanglin
	 *
	 */
	public static class Weixintype{
		
		public static final String TYPE_ACCESS = "1";			//已授权
		
		public static final String TYPE_EXCEPTION = "2";		//未授权
	}
	
	
	/**
	 * 机构类型
	 * @author qph
	 *
	 */
	public static class officetype{
		public static final String ROOT = "0";							//顶级机构
		
		public static final String TYPE_CONSTRUCTION_BANK = "3";		//建行
		
		public static final String INSTITUTIONS = "7";					//机构
		
		public static final String TYPE_STORE = "8";					//门店
	}
	
	/**
	 * 用户类型
	 * @author qph
	 *
	 */
	public static class GuestType{
		
		public static final String TYPE_NORMAL = "1";			//普通用户
		
		public static final String TYPE_MANAGER = "2";		    //管理员用户
	}
	
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
		/** 错误代码 服务代码不正确 E06 */
		public static final String ERROR_NO_E06 = "E06";
	}
	// 发送模板消息
	public static final String SEND_TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/template/send";

	// 发送消息
	public static final String SEND_CUSTOM_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/custom/send";
	
	// 群发消息
	public static final String SEND_MASS_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/mass/send";
	
	// 获取用户
	public static final String GET_USER ="https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
	
	public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

	public static final String GET_METHOD = "GET";

	public static final String POST_METHOD = "POST";

	public static final String ORDER_ID = "预约单号";

	public static final String STRING_FORMAT = "#000000";
	/**
	 * 预约清分状态
	 * 
	 * @author XL
	 * @version 2018年5月17日
	 */
	public static class OrderClearStatus {
		/** 登记 */
		public static final String REGISTER = "1";
		/** 接收 */
		public static final String RECEPTION = "2";
	}
	
	/**
	 * 回调分支
	 * 
	 * @author XL
	 * @version 2018年5月17日
	 */
	public static class BranchValue {
		/** 授权 */
		public static final String AUTHORIZATION = "authorization";
		/** 预约清分 */
		public static final String ORDER_CLEAR = "orderClear";
		/** 门店预约 */
		public static final String DOOR_ORDER = "doorOrder";
	}
	
	/**
	 * 微信常用标识
	 * 
	 * @author qph
	 * @version 2018年5月28日
	 */

	public static class WechatCommonCha {
		/** accessToken */
		public static final String ACCESSTOKEN = "access_token";
		/** appId */
		public static final String APPID = "appID";
		/** appSecret */
		public static final String APPSECRET = "appSecret";
		/** FromUserName */
		public static final String FROMUSERNAME = "FromUserName";
		/** ToUserName */
		public static final String TOUSERNAME = "ToUserName";
		/** MsgType */
		public static final String MSGTYPE = "MsgType";
		/** Content */
		public static final String CONTENT = "Content";
		/** testOrderTemplate */
		public static final String TESTORDERTEMPLATE = "testOrderTemplate";
		/** signature */
		public static final String SIGNATURE = "signature";
		/** timestamp */
		public static final String TIMESTAMP = "timestamp";
		/** nonce */
		public static final String NONCE = "nonce";
		/** echostr */
		public static final String ECHOSTR = "echostr";
		/** WebAuthoURL */
		public static final String WEBAUTHOURL = "WebAuthoURL";
		/** redirectURL */
		public static final String REDIRECTURL = "redirectURL";
		/** BUSINESSTYPE */
		public static final String BUSINESSTYPE = "BUSINESSTYPE";
		/** REDIRECT_URI */
		public static final String REDIRECT_URI = "REDIRECT_URI";
		/** STATE */
		public static final String STATE = "STATE";
		/** BRANCH_VALUE */
		public static final String BRANCHVALUE = "BRANCH_VALUE";
		/** BRANCH_VALUE */
		public static final String APPIDUP = "APPID";
		/** netWorkAddress */
		public static final String NETWORKADDRESS = "netWorkAddress";
		/** text */
		public static final String TEXT = "text";
		/** EventKey */
		public static final String EVENTKEY = "EventKey";

	}

	/**
	 * 微信返回错误代码
	 * 
	 * @author qph
	 * @version 2018年5月28日
	 */
	public static class WechatErrorCode {
		/** 40001 */
		public static final String ERROR_CODE_40001 = "40001";

	}
}
