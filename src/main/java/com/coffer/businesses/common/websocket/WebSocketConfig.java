package com.coffer.businesses.common.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 
 * Title: WebSocketConfig
 * <p>
 * Description: Websocket服务配置类
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年10月16日 下午4:36:47
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	private static Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		logger.debug("----------registerWebSocketHandlers is execute!");
		// registry.addHandler(systemWebSocketHandler(),
		// "/webSocketServer").setAllowedOrigins("*")
		// .addInterceptors(new WebSocketHandshakeInterceptor());

		registry.addHandler(systemWebSocketHandler(), "/sockjs/webSocketServer").setAllowedOrigins("*")
				.addInterceptors(new WebSocketHandshakeInterceptor()).withSockJS();
	}

	@Bean
	public WebSocketHandler systemWebSocketHandler() {
		logger.debug("----------systemWebSocketHandler is execute!");
		return new SystemWebSocketHandler();
	}

}
