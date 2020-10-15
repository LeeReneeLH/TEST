package com.coffer.businesses.modules.weChat.v03.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.utils.weChat.AccessToken;
import com.coffer.businesses.common.utils.weChat.WeixinUtil;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.dao.OrderClearMainDao;
import com.coffer.businesses.modules.clear.v03.entity.ClearConfirm;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.weChat.HttpReqUtil;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.WechatMessageUtils;
import com.coffer.businesses.modules.weChat.v03.dao.GuestDao;
import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.businesses.modules.weChat.v03.entity.TemplateMsgResult;
import com.coffer.businesses.modules.weChat.v03.entity.TextMessage;
import com.coffer.businesses.modules.weChat.v03.entity.WechatTemplateMsg;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.mapper.JsonMapper;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.google.common.collect.Maps;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @author LLF
 * @version 2015年9月17日
 * 
 *          处理ajax请求服务类
 *
 */
@Component
@Scope("singleton")
public class WechatAccountService extends BaseService {

	@Autowired
	protected MessageSource msg;

	@Autowired
	private GuestDao guestDao;

	@Autowired
	private OrderClearMainDao orderClearMainDao;

	public static AccessToken ACCESS_TOKEN = null;

	public static Long ACCESS_TOKEN_TIMING = 0L;
	 
	/**
	 * 
	 * @author LLF
	 * @version 2015年9月17日
	 * 
	 *          服务代码不存在，应答错误消息
	 * @param serviceNo
	 * @return
	 */
	public String erro(String serviceNo) {
		Map<String, Object> map = Maps.newHashMap();

		map.put("serviceNo", serviceNo);
		map.put("versionNo", WeChatConstant.Interface.VERSION_NO_01);
		map.put("resultFlag", WeChatConstant.Interface.RESULT_FLAG_FAILURE);
		map.put("errorNo", WeChatConstant.Interface.ERROR_NO_E06);

		return gson.toJson(map);
	}
	


	/**
	 * 处理模板数据
	 * 
	 * @param accessToken
	 * @author qph
	 * @param request
	 * @return 状态
	 * @throws IOException
	 */
	public String weiXinMessage(HttpServletRequest request) throws IOException {
		Locale locale = LocaleContextHolder.getLocale();
		request.setCharacterEncoding(WeChatConstant.DEFAULT_CHARACTER_ENCODING);
		// xml请求解析
		Map<String, String> requestMap = WechatMessageUtils.xmlToMap(request);
		// 菜单点击事件 XL 2018-05-24 begin
		String event = requestMap.get("Event");
		if ("CLICK".equals(event)) {
			return clickEvent(requestMap, locale);
		}
		// end
		
		// 发送方帐号（open_id）
		String fromUserName = requestMap.get(WeChatConstant.WechatCommonCha.FROMUSERNAME);
		// 公众帐号
		String toUserName = requestMap.get(WeChatConstant.WechatCommonCha.TOUSERNAME);
		// 消息类型
		String msgType = requestMap.get(WeChatConstant.WechatCommonCha.MSGTYPE);
		// 消息内容
		String content = requestMap.get(WeChatConstant.WechatCommonCha.CONTENT);
		// 消息内容关键字
		String keyword = "";
		// 机构ID
		String officeId = "";
		// 返回消息
		TextMessage textMsg = new TextMessage();
		textMsg.setFromUserName(toUserName);
		textMsg.setToUserName(fromUserName);
		textMsg.setMsgType(msgType);
		textMsg.setCreateTime(new Date().toString());
		// 获取到系统当前时间
		Date currentTime = new Date();
		// 根据OpenId查询客户
		List<Guest> guestList = this.checkOpenId(fromUserName);
		if (Collections3.isEmpty(guestList)) {
			String message = msg.getMessage("message.I3002", new String[] {}, locale);
			textMsg.setContent(message);
			return WechatMessageUtils.textMessageToXml(textMsg);
		} else {
			// 获取客户列表
			Guest guest = guestList.get(0);
			officeId = guest.getGofficeId();
			if (WeChatConstant.Weixintype.TYPE_EXCEPTION.equals(guest.getGrantstatus())) {
				String message = msg.getMessage("message.I3002", new String[] {}, locale);
				textMsg.setContent(message);
				return WechatMessageUtils.textMessageToXml(textMsg);
			} else if (guest.getGrantDate().getTime() < currentTime.getTime()) {
				String message = msg.getMessage("message.I3010", new String[] {}, locale);
				textMsg.setContent(message);
				return WechatMessageUtils.textMessageToXml(textMsg);
			} else {
			// 判断发送内容
			if (!StringUtils.isEmpty(content)) {
				// 获取最近一条清分预约详情，以及其他链接
				if (content.equals("1")) {
						return this.makeMessage(textMsg, locale, officeId, fromUserName);
					// 获取当日所有清分
				} else if (content.equals("2")) {
					String Msg = this.findCompleteOrder(officeId,fromUserName);
					textMsg.setContent(Msg);
					return WechatMessageUtils.textMessageToXml(textMsg);
					// 获取当前用户类型
				} else if (content.equals("3")) {
					// 客户模板信息组装
					WechatTemplateMsg wechatTemplateMsg = this.packGuestData(guest, fromUserName);
					String data = JsonMapper.toJsonString(wechatTemplateMsg);
					this.sendTemplate(WeixinUtil
								.getAccessToken(Global.getConfig(WeChatConstant.WechatCommonCha.APPID),
										Global.getConfig(WeChatConstant.WechatCommonCha.APPSECRET), false)
								.getToken(), data);
				} else {
					if (content.length() > 4) {
						keyword = content.substring(0, 4);
					}
						if (keyword.equals(WeChatConstant.ORDER_ID)) {
						// 获取订单编号
						String orderId = content.substring(5, content.length());
						OrderClearMain orderClearMain = new OrderClearMain();
						orderClearMain.setInNo(orderId);
						OrderClearMain order = orderClearMainDao.get(orderClearMain);
						if(order ==null){
								String message = msg.getMessage("message.I3003", new String[] {}, locale);
								textMsg.setContent(message);
							return WechatMessageUtils.textMessageToXml(textMsg);	
						}else{
							// 预约模板信息组装
							WechatTemplateMsg wechatTemplateMsg = this.packOrderData(order, fromUserName);
							String data = JsonMapper.toJsonString(wechatTemplateMsg);
							this.sendTemplate(
										WeixinUtil
												.getAccessToken(Global.getConfig(WeChatConstant.WechatCommonCha.APPID),
														Global.getConfig(WeChatConstant.WechatCommonCha.APPSECRET),
														false)
								.getToken(), data);
							return "";
						}
					} else {
							String message = msg.getMessage("message.I3004", new String[] {}, locale);
							textMsg.setContent(message);
						return WechatMessageUtils.textMessageToXml(textMsg);
					}

				}
			} else {
				return "";
			}
			return "";
		}
		}
	}

	/**
	 * 根据Openid查询用户
	 * 
	 * @author qph
	 * @param openId
	 * @return List<Guest>
	 * @date 2018-05-21
	 */

	public List<Guest> checkOpenId(String openId) {
		Guest guestCheck = new Guest();
		guestCheck.setOpenId(openId);
		// 根据OpenID查询客户
		List<Guest> guestList = guestDao.findListByOpenId(guestCheck);
		return guestList;

	}

	/**
	 * 发送模板消息
	 * 
	 * @param accessToken
	 * @param data
	 * @return 状态
	 */
	public TemplateMsgResult sendTemplate(String accessToken, String data) {
		TemplateMsgResult templateMsgResult = null;
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put(WeChatConstant.WechatCommonCha.ACCESSTOKEN, accessToken);
		// 传输HTTP
		String result = HttpReqUtil.HttpsDefaultExecute(WeChatConstant.POST_METHOD,
				WeChatConstant.SEND_TEMPLATE_MESSAGE, params, data, WeChatConstant.DEFAULT_CHARACTER_ENCODING);
		// 若accesstoken失效
		if (result.equals(WeChatConstant.WechatErrorCode.ERROR_CODE_40001)) {
			String token = WeixinUtil.getAccessToken(Global.getConfig(WeChatConstant.WechatCommonCha.APPID),
					Global.getConfig(WeChatConstant.WechatCommonCha.APPSECRET), true)
					.getToken();
			params.put(WeChatConstant.WechatCommonCha.ACCESSTOKEN, token);
			HttpReqUtil.HttpsDefaultExecute(WeChatConstant.POST_METHOD, WeChatConstant.SEND_TEMPLATE_MESSAGE, params,
					data, WeChatConstant.DEFAULT_CHARACTER_ENCODING);
			return templateMsgResult;
		} else {
			templateMsgResult = (TemplateMsgResult) JsonMapper.fromJsonString(result, TemplateMsgResult.class);
			// log.....
			return templateMsgResult;
		}
	}

	/**
	 * 发送模板消息给所有用户
	 * 
	 * @author qph
	 * @param openId
	 * @return List<Guest>
	 * @date 2018-05-21
	 */
	public String sendTemplateToUsers(ClearConfirm clearConfirm) {
		// 获取登记金库ID
		String officeId = clearConfirm.getRegisterOffice();
		// 获取该订单Id
		String orderId = clearConfirm.getInNo();
		// 获取订单详情
		OrderClearMain orderClearMain = orderClearMainDao.get(orderId);
		Guest guest = new Guest();
		// 获取机构下所有客户
		guest.setGofficeId(officeId);
		List<Guest> guestList = guestDao.findList(guest);
		for (Guest guestEx : guestList) {
			// 预约模板信息组装
			WechatTemplateMsg wechatTemplateMsg = this.packOrderData(orderClearMain, guestEx.getOpenId());
			String data = JsonMapper.toJsonString(wechatTemplateMsg);
			this.sendTemplate(
					WeixinUtil.getAccessToken(Global.getConfig(WeChatConstant.WechatCommonCha.APPID),
							Global.getConfig(WeChatConstant.WechatCommonCha.APPSECRET), false)
							.getToken(),
					data);
		}
		return "";
	}

	/**
	 * 具体模板参数组装
	 * 
	 * @author qph
	 * @param fromUserName
	 * @return
	 * @throws @date
	 *             2018-05-21
	 * 
	 */
	private WechatTemplateMsg packOrderData(OrderClearMain orderClearMain, String fromUserName) {
		Locale locale = LocaleContextHolder.getLocale();
		TreeMap<String, TreeMap<String, String>> params = new TreeMap<String, TreeMap<String, String>>();
		// 根据具体模板参数组装
		params.put("first",
				WechatTemplateMsg.item(msg.getMessage("message.I3005", new String[] {}, locale),
						WeChatConstant.STRING_FORMAT));
		params.put("keyword1", WechatTemplateMsg.item(orderClearMain.getInNo(), WeChatConstant.STRING_FORMAT));
		params.put("keyword2",
				WechatTemplateMsg.item(orderClearMain.getrOffice().getName(), WeChatConstant.STRING_FORMAT));
		params.put("keyword3",
				WechatTemplateMsg.item(orderClearMain.getInAmount().toString(), WeChatConstant.STRING_FORMAT));
		double dAmount = Double.parseDouble(orderClearMain.getInAmount().toString());
		String strBigAmount = NumToRMB.changeToBig(dAmount);
		params.put("keyword4", WechatTemplateMsg.item(strBigAmount, WeChatConstant.STRING_FORMAT));
		params.put("keyword5",
				WechatTemplateMsg.item(DateUtils.formatDate(orderClearMain.getRegisterDate()),
						WeChatConstant.STRING_FORMAT));
		params.put("keyword6", WechatTemplateMsg.item(orderClearMain.getRegisterName(), WeChatConstant.STRING_FORMAT));
		params.put("keyword7", WechatTemplateMsg.item(orderClearMain.getRemarks(), WeChatConstant.STRING_FORMAT));
		params.put("remark",
				WechatTemplateMsg.item(msg.getMessage("message.I3006", new String[] {}, locale),
						WeChatConstant.STRING_FORMAT));
		// 生成模板类
		WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
		wechatTemplateMsg.setTemplate_id(Global.getConfig(WeChatConstant.WechatCommonCha.TESTORDERTEMPLATE));
		wechatTemplateMsg.setTouser(fromUserName);
		// 获取映射网址
		String address = Global.getConfig(WeChatConstant.WechatCommonCha.NETWORKADDRESS);
		wechatTemplateMsg.setUrl("\"" + address + "/frame/main/wechatAccount/getByInNo?inNo="
				+ orderClearMain.getInNo() + "&openId=" + fromUserName);
		wechatTemplateMsg.setData(params);
		return wechatTemplateMsg;
	}

	/**
	 * 具体模板参数组装
	 * 
	 * @author qph
	 * @param fromUserName
	 * @return
	 * @throws @date
	 *             2018-05-21
	 * 
	 */
	private WechatTemplateMsg packGuestData(Guest guest, String fromUserName) {
		Locale locale = LocaleContextHolder.getLocale();
		TreeMap<String, TreeMap<String, String>> params = new TreeMap<String, TreeMap<String, String>>();
		// 根据具体模板参数组装
		params.put("first", WechatTemplateMsg.item(msg.getMessage("message.I3007", new String[] {}, locale),
				WeChatConstant.STRING_FORMAT));
		params.put("keyword1", WechatTemplateMsg.item(guest.getGname(), WeChatConstant.STRING_FORMAT));
		params.put("keyword2", WechatTemplateMsg.item(guest.getGofficeName(), WeChatConstant.STRING_FORMAT));
		params.put("keyword3", WechatTemplateMsg.item(guest.getGidcardNo(), WeChatConstant.STRING_FORMAT));
		params.put("keyword4", WechatTemplateMsg.item(guest.getGphone(), WeChatConstant.STRING_FORMAT));
		params.put("remark", WechatTemplateMsg.item(msg.getMessage("message.I3006", new String[] {}, locale),
				WeChatConstant.STRING_FORMAT));
		// 生成模板类
		WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
		wechatTemplateMsg.setTemplate_id(Global.getConfig(WeChatConstant.WechatCommonCha.TESTORDERTEMPLATE));
		// 获取映射网址
		String address = Global.getConfig(WeChatConstant.WechatCommonCha.NETWORKADDRESS);
		wechatTemplateMsg.setTouser(fromUserName);
		wechatTemplateMsg.setUrl(
				"\"" + address + "/frame/main/wechatAccount/getGuestByOpenId?&openId=" + fromUserName);
		wechatTemplateMsg.setData(params);
		return wechatTemplateMsg;
	}

	/**
	 * 查询已完成订单
	 * 
	 * @author qph
	 * @param
	 * @return String
	 * @date 2018-05-21
	 * 
	 */
	private String findCompleteOrder(String officeId,String fromUserName) {
		Locale locale = LocaleContextHolder.getLocale();
		OrderClearMain orderClearMain = new OrderClearMain();

		if (orderClearMain.getCreateTimeStart() == null) {
			orderClearMain.setCreateTimeStart(new Date());
		}
		if (orderClearMain.getCreateTimeEnd() == null) {
			orderClearMain.setCreateTimeEnd(new Date());
		}
		// 查询条件： 开始时间
		if (orderClearMain.getCreateTimeStart() != null) {
			orderClearMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(orderClearMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (orderClearMain.getCreateTimeEnd() != null) {
			orderClearMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(orderClearMain.getCreateTimeEnd())));
		}
		// 设置机构Id
		orderClearMain.setRegisterOffice(officeId);
		orderClearMain.setStatus(ClearConstant.StatusType.DELETE);
		List<OrderClearMain> orderList = orderClearMainDao.findList(orderClearMain);
		StringBuffer stringbuff = new StringBuffer();
		String address = Global.getConfig(WeChatConstant.WechatCommonCha.NETWORKADDRESS);
		if(Collections3.isEmpty(orderList)){
			String message = msg.getMessage("message.I3008", new String[] {}, locale);
			stringbuff.append(message);
		} else {
			for (OrderClearMain order : orderList) {
				stringbuff
				.append("预约单号：<a href=\"" + address + "/frame/main/wechatAccount/getByInNo?inNo="
						+ order.getInNo() + "&openId=" + fromUserName + "\">" + order.getInNo() + "</a>\n");
				
				
			}

		}
		return stringbuff.toString();
	}

	/**
	 * 查询当日所有订单
	 * 
	 * @author qph
	 * @param
	 * @return String
	 * @date 2018-05-21
	 * 
	 */
	private List<OrderClearMain> findCompleteOrderList(String officeId) {
		OrderClearMain orderClearMain = new OrderClearMain();

		if (orderClearMain.getCreateTimeStart() == null) {
			orderClearMain.setCreateTimeStart(new Date());
		}
		if (orderClearMain.getCreateTimeEnd() == null) {
			orderClearMain.setCreateTimeEnd(new Date());
		}
		// 查询条件： 开始时间
		if (orderClearMain.getCreateTimeStart() != null) {
			orderClearMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(orderClearMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (orderClearMain.getCreateTimeEnd() != null) {
			orderClearMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(orderClearMain.getCreateTimeEnd())));
		}
		// 设置机构Id
		orderClearMain.setRegisterOffice(officeId);
		List<OrderClearMain> orderList = orderClearMainDao.findList(orderClearMain);

		return orderList;
	}
	
	/**
	 * 点击事件
	 * 
	 * @version 2018-05-25
	 * @param requestMap
	 * @param locale
	 * @author XL
	 * @param request
	 */
	public String clickEvent(Map<String, String> requestMap, Locale locale) {
		// 发送方帐号（open_id）
		String fromUserName = requestMap.get(WeChatConstant.WechatCommonCha.FROMUSERNAME);
		// 公众帐号
		String toUserName = requestMap.get(WeChatConstant.WechatCommonCha.TOUSERNAME);
		// 消息类型
		String msgType = WeChatConstant.WechatCommonCha.TEXT;
		// 消息内容
		String content = requestMap.get(WeChatConstant.WechatCommonCha.EVENTKEY);
		// 机构ID
		String officeId = "";
		// 返回消息
		TextMessage textMsg = new TextMessage();
		textMsg.setFromUserName(toUserName);
		textMsg.setToUserName(fromUserName);
		textMsg.setMsgType(msgType);
		textMsg.setCreateTime(new Date().toString());
		// 根据OpenId查询客户
		List<Guest> guestList = this.checkOpenId(fromUserName);
		if (Collections3.isEmpty(guestList)) {
			String message = msg.getMessage("message.I3002", new String[] {}, locale);
			textMsg.setContent(message);
			return WechatMessageUtils.textMessageToXml(textMsg);
		} else {
			// 获取机构
			Guest guest = guestList.get(0);
			officeId = guest.getGofficeId();
			if (WeChatConstant.Weixintype.TYPE_EXCEPTION.equals(guest.getGrantstatus())) {
				String message = msg.getMessage("message.I3002", new String[] {}, locale);
				textMsg.setContent(message);
				return WechatMessageUtils.textMessageToXml(textMsg);
			} else {
				if (content.equals("1")) {
					return this.makeMessage(textMsg, locale, officeId, fromUserName);
				}
			}
		}
		return "";
	}
	
	/**
	 * 返回预约单号以及模板
	 * 
	 * @version 2018-05-29
	 * @param requestMap
	 * @author XL
	 */
	public String makeMessage(TextMessage textMsg, Locale locale, String officeId, String fromUserName) {
		// 查询当日所有系统订单
		List<OrderClearMain> orderList = this.findCompleteOrderList(officeId);
		if (Collections3.isEmpty(orderList)) {
			String message = msg.getMessage("message.I3001", new String[] {}, locale);
			textMsg.setContent(message);
			return WechatMessageUtils.textMessageToXml(textMsg);
		} else {
			OrderClearMain orderClearMain = orderList.get(0);
			// 预约模板信息组装
			WechatTemplateMsg wechatTemplateMsg = this.packOrderData(orderClearMain, fromUserName);

			String data = JsonMapper.toJsonString(wechatTemplateMsg);
			// 发送模板
			this.sendTemplate(WeixinUtil.getAccessToken(Global.getConfig(WeChatConstant.WechatCommonCha.APPID),
					Global.getConfig(WeChatConstant.WechatCommonCha.APPSECRET), false)
					.getToken(), data);
			// 查询已完成订单
			String Msg = this.findCompleteOrder(officeId, fromUserName);
			textMsg.setContent(Msg);
			return WechatMessageUtils.textMessageToXml(textMsg);
		}
	}
	
	/**
	 * 群发推送
	 * 
	 * @author XL
	 * @version 2018-05-25
	 * @param content
	 * @return
	 */
	public void sendMessage(String content) {
		// 获取access_token
		String access_token = WeixinUtil.getAccessToken(Global.getConfig(WeChatConstant.WechatCommonCha.APPID),
				Global.getConfig(WeChatConstant.WechatCommonCha.APPSECRET), false)
				.getToken();
		// 参数
		TreeMap<String, String> params = Maps.newTreeMap();
		params.put(WeChatConstant.WechatCommonCha.ACCESSTOKEN, access_token);
		// 获取关注者openId集合
		List<String> openIdList = getUser(access_token);
		// 发送信息
		for (String string : openIdList) {
			Map<String, Object> dataMap = Maps.newHashMap();
			// 设置接收用户
			dataMap.put("touser", string);
			// 设置消息类型
			dataMap.put("msgtype", "text");
			// 设置推送内容
			Map<String, String> textMap = Maps.newHashMap();
			textMap.put("content", content);
			dataMap.put("text", textMap);
			String data = JsonMapper.toJsonString(dataMap);
			// 发送
			HttpReqUtil.HttpsDefaultExecute(WeChatConstant.POST_METHOD, WeChatConstant.SEND_CUSTOM_MESSAGE, params,
					data, WeChatConstant.DEFAULT_CHARACTER_ENCODING);
		}
		// 群发推送，一天允许一条
		/*
		 * Map<String, Object> dataMap = Maps.newHashMap();
		 * dataMap.put("touser", openIdList); dataMap.put("msgtype", "text");
		 * Map<String,String> textMap=Maps.newHashMap(); textMap.put("content",
		 * "群发消息"); dataMap.put("text", textMap); String data =
		 * JsonMapper.toJsonString(dataMap); data =
		 * JsonMapper.toJsonString(dataMap);
		 * HttpReqUtil.HttpsDefaultExecute(WeChatConstant.POST_METHOD,
		 * WeChatConstant.SEND_MASS_MESSAGE, params, data,
		 * WeChatConstant.DEFAULT_CHARACTER_ENCODING);
		 */
	}
	
	/**
	 * 获取所有关注者openId
	 * 
	 * @author XL
	 * @version 2018-05-25
	 * @param access_token
	 * @return
	 */
	public List<String> getUser(String access_token) {
		// 获取关注者接口
		String requestUrl = WeChatConstant.GET_USER.replace("ACCESS_TOKEN", access_token);
		// 获取关注者信息
		JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, WeChatConstant.GET_METHOD, null);
		JSONArray jsonArray = JSONArray.fromObject(jsonObject.getJSONObject("data").get("openid"));
		// openId集合
		@SuppressWarnings({ "unchecked", "static-access" })
		List<String> openIdList = (List<String>) jsonArray.toCollection(jsonArray, String.class);
		return openIdList;
	}
	
	/**
	 * 创建微信公众号菜单
	 * 
	 * @version 2018-05-25
	 * @author XL
	 * @param jsonPath
	 * @return
	 */
	public static void createMenu(String jsonPath) {
		try {
			//String jsonPath=WechatAccountService.class.getClass().getResource("/").getPath();
			jsonPath=jsonPath.replace("WEB-INF/classes/", "static/weChat/")+"weChatMenu.json";
			// 获取access_token
			String token = WeixinUtil.getAccessToken(Global.getConfig("appID"), Global.getConfig("appSecret"),false).getToken();
			// 菜单创建接口地址
			String requestUrl = WeixinUtil.menu_create_url.replace("ACCESS_TOKEN", token);
			// 设置参数
			String param = readJson(jsonPath);
			// 发起https请求并获取结果
			JSONObject result=WeixinUtil.httpRequest(requestUrl, WeChatConstant.POST_METHOD, param);
			System.out.println("微信自定义菜单创建结果================================================"+result);
		}catch (Exception e) {
			System.out.println("微信自定义菜单创建失败================================================");
		}
	}
	
	/**
	 * 读取JSON文件
	 * 
	 * @version 2018-05-25
	 * @author XL
	 * @param jsonPath
	 * @return
	 * @throws Exception 
	 */
	public static String readJson(String jsonPath) throws Exception {
		String jsonStr = "";
		File file = new File(jsonPath);
		Scanner scanner = null;
		StringBuilder buffer = new StringBuilder();
		scanner = new Scanner(file, WeChatConstant.DEFAULT_CHARACTER_ENCODING);
		while (scanner.hasNextLine()) {
			buffer.append(scanner.nextLine().trim());
		}
		if (scanner != null) {
			scanner.close();
		}
		jsonStr = buffer.toString();
		return jsonStr;
	}
	
	
}   


