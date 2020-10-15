package com.coffer.businesses.common.websocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.utils.StringUtils;

/**
 * 
 * Title: WebSocketHandshakeInterceptor
 * <p>
 * Description: websocket握手拦截器
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年10月16日 下午4:39:55
 */
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

	private static Logger logger = LoggerFactory.getLogger(HandshakeInterceptor.class);

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		logger.debug("----------beforeHandshake is execute!");
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			HttpSession session = servletRequest.getServletRequest().getSession(false);
			// 使用userId区分WebSocketHandler，以便定向发送消息
			String userId = (String) session.getAttribute(Constant.SESSION_USER_ID);
			if (StringUtils.isBlank(userId)) {
				return false;
			}
			attributes.put(Constant.SESSION_USER_ID, userId);
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		logger.debug("----------afterHandshake is execute!");

		System.out.println("++++++++++++++++ HandshakeInterceptor: afterHandshake  ++++++++++++++");

	}
}
