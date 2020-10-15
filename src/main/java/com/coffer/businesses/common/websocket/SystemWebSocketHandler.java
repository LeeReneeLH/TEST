package com.coffer.businesses.common.websocket;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.coffer.businesses.common.Constant;
//import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.Message;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * Title: SystemWebSocketHandler
 * <p>
 * Description: WebSocket 消息控制类
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年10月16日 下午4:24:53
 */
@Component
public class SystemWebSocketHandler implements WebSocketHandler {

	private static final Logger logger;

//	private static final String USER_WEB_SOCKET_SESSION_CACHE_KEY = "userWebSocketSessionCache";

	private static Map<String, WebSocketSession> wsSessionMap = null;
	
	// websocket消息类型
	private static String type = "type";

	static {
		wsSessionMap = Maps.newConcurrentMap();
	}

	/**
	 * Json实例对象
	 */
	protected Gson gson = new GsonBuilder().create();

	static {
		logger = LoggerFactory.getLogger(SystemWebSocketHandler.class);
		logger.debug("----------static method of SystemWebSocketHandler  is execute!");
	}

	/**
	 * 连接建立后的配置
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.debug("----------afterConnectionEstablished method of SystemWebSocketHandler  is execute!");
		String userId = (String) session.getAttributes().get(Constant.SESSION_USER_ID);
		logger.debug("当前在线用户" + userId);
		addWSSession(userId, session);
	}

	/**
	 * 传输出错时的处理
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		logger.debug("----------handleTransportError method of SystemWebSocketHandler  is execute!");
		if (session.isOpen()) {
			session.close();
		}
		/*wsSessionMap = (Map<String, WebSocketSession>) CacheUtils.get(USER_WEB_SOCKET_SESSION_CACHE_KEY);*/
		Iterator<Entry<String, WebSocketSession>> it = wsSessionMap.entrySet().iterator();
		// 移除Socket会话
		while (it.hasNext()) {
			Entry<String, WebSocketSession> entry = it.next();
			if (entry.getValue().getId().equals(session.getId())) {
				removeWSSession(entry.getKey());
				logger.debug("Socket会话已经移除:用户ID" + entry.getKey());
				break;
			}
		}
	}

	/**
	 * 连接关闭后的处理
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		logger.debug("----------afterConnectionClosed method of SystemWebSocketHandler  is execute!");
		String userId = (String) session.getAttributes().get(Constant.SESSION_USER_ID);
		removeWSSession(userId);
		logger.debug("用户" + userId + "已退出！");
		logger.debug("afterConnectionClosed" + closeStatus.getReason());
	}

	@Override
	public boolean supportsPartialMessages() {
		logger.debug("----------supportsPartialMessages method of SystemWebSocketHandler  is execute!");
		return false;
	}

	/**
	 * 给所有在线用户发送消息
	 *
	 * @param message
	 */
	public void sendMessageToUsers(TextMessage message) {
		logger.debug("----------sendMessageToUsers method of SystemWebSocketHandler  is execute!");
		/*wsSessionMap = (Map<String, WebSocketSession>) CacheUtils.get(USER_WEB_SOCKET_SESSION_CACHE_KEY);*/
		if (wsSessionMap != null) {

			Iterator<String> iterator = wsSessionMap.keySet().iterator();
			WebSocketSession session = null;
			while (iterator.hasNext()) {
				session = wsSessionMap.get(iterator.next());
				if (session != null) {
					try {
						if (session.isOpen()) {
							session.sendMessage(message);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * Title: sendGpsMessageToUser
	 * <p>Description: 发送车辆位置消息</p>
	 * @author:     yanbingxu
	 * @param userId
	 * @param location 
	 * void    返回类型
	 */
	public void sendGpsMessageToUser(String userId, Map<String, String> location) {

		location.put(type, "carTrack");
		
		TextMessage textMessage = new TextMessage(gson.toJson(location));

		WebSocketSession session = getWSSession(userId);
		if (session != null) {
			try {
				if (session.isOpen()) {
					session.sendMessage(textMessage);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 给某个用户发送消息
	 *
	 * @param userId
	 * @param message
	 */
	public void sendMessageToUser(String userId, TextMessage message) {
		logger.debug("----------sendMessageToUser method of SystemWebSocketHandler  is execute!");
		WebSocketSession session = getWSSession(userId);
		if (session != null) {
			try {
				if (session.isOpen()) {
					session.sendMessage(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 给某个用户发送消息
	 *
	 * @param userId
	 *            用户ID
	 * @param message
	 *            消息
	 */
	public void sendStrMessageToUser(String userId, Message message) {

		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("url", message.getUrl());
		resultMap.put("id", message.getId());
		resultMap.put("menuId", message.getMenuId());
		resultMap.put("messageTopic", message.getMessageTopic());
		resultMap.put("createName", message.getCreateName());
		resultMap.put("createDate", DateFormatUtils.format(message.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
		resultMap.put(type, "sysMessage");

		TextMessage textMessage = new TextMessage(gson.toJson(resultMap));
		logger.debug("----------sendMessageToUser method of SystemWebSocketHandler  is execute!");
		WebSocketSession session = getWSSession(userId);
		if (session != null) {
			try {
				if (session.isOpen()) {
					session.sendMessage(textMessage);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * Title: getWSSession
	 * <p>
	 * Description: 根据用户ID获取session
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param userId
	 *            登陆用户ID
	 * @return WebSocketSession 返回类型
	 */
	public static WebSocketSession getWSSession(String userId) {
		/*wsSessionMap = (Map<String, WebSocketSession>) CacheUtils.get(USER_WEB_SOCKET_SESSION_CACHE_KEY);*/
		return wsSessionMap.get(userId);
	}

	/**
	 * 
	 * Title: removeWSSession
	 * <p>
	 * Description: 根据登陆用户ID删除websocketsession
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param userId
	 *            登陆用户ID void 返回类型
	 */
	public static void removeWSSession(String userId) {
		/*wsSessionMap = (Map<String, WebSocketSession>) CacheUtils.get(USER_WEB_SOCKET_SESSION_CACHE_KEY);*/
		if (wsSessionMap.containsKey(userId)) {
			wsSessionMap.remove(userId);
		}
	}

	/**
	 * 
	 * Title: addWSSession
	 * <p>
	 * Description:添加登陆用户Session
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param userId
	 *            用户ID
	 * @param session
	 *            WebSocketSession void 返回类型
	 */
	public static void addWSSession(String userId, WebSocketSession session) {
		/*wsSessionMap = (Map<String, WebSocketSession>) CacheUtils.get(USER_WEB_SOCKET_SESSION_CACHE_KEY);*/

		if (wsSessionMap == null) {
			wsSessionMap = Maps.newConcurrentMap();
			wsSessionMap.put(userId, session);
//			CacheUtils.put(USER_WEB_SOCKET_SESSION_CACHE_KEY, wsSessionMap);
		} else {
			wsSessionMap.put(userId, session);
		}
	}

	/**
	 * 
	 * Title: getOnLineUserIdList
	 * <p>
	 * Description: 获取在线用户数
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @return 在线用户ID列表 List<String> 返回类型
	 */
	public static List<String> getOnLineUserIdList() {
		List<String> rtnList = Lists.newArrayList();
		/*wsSessionMap = (Map<String, WebSocketSession>) CacheUtils.get(USER_WEB_SOCKET_SESSION_CACHE_KEY);*/
		Iterator<String> iterator = wsSessionMap.keySet().iterator();
		WebSocketSession session = null;
		String key = null;
		while (iterator.hasNext()) {
			key = iterator.next();
			session = wsSessionMap.get(key);
			if (session != null) {
				if (session.isOpen()) {
					rtnList.add(key);
				}
			}
		}
		return rtnList;
	}

	/**
	 * 外部消息处理
	 */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

		session.sendMessage(message);
		sendMessageToUsers(new TextMessage(message.getPayload().toString() + DateUtils.getDateTime()));
	}
}
