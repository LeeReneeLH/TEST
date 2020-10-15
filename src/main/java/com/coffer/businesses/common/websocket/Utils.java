package com.coffer.businesses.common.websocket;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;

/**
 * 
 * Title: Utils
 * <p>
 * Description: WebSocket共通类
 * </p>
 * 
 * @author yanbingxu
 * @date 2017年11月15日 下午4:48:49
 */
public class Utils {

	private static final Pattern pattern;
	private static Matcher matcher;

	static {
		pattern = Pattern.compile("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}");
	}

	public static int getIntervalByRole(int role) {
		int val = -1;
		if (role < 0) {
			val = 3000;
		} else if (role == 0) {
			val = 2000;
		} else if (role >= 8) {
			val = 0;
		} else if ((role & 0x07) > 0) {
			val = 1000;
		}
		return val;
	}

	public static String getIpAddress(ServerHttpRequest request) {
		HttpHeaders headers = request.getHeaders();
		String ip = null;

		List<String> list = headers.get("X-Real-IP");
		if (list == null) {
			list = headers.get("x-forwarded-for");
		}
		if (list == null) {
			list = headers.get("Proxy-Client-IP");
		}
		if (list == null) {
			list = headers.get("WL-Proxy-Client-IP");
		}
		if (list != null && list.size() > 0) {
			ip = list.get(0);
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddress().getAddress().toString();
		}
		matcher = pattern.matcher(ip);
		if (matcher.find()) {
			ip = matcher.group();
		}
		return ip;
	}

	public static String addQuery(String url, String q) {
		if (q == null || q.length() == 0) {
			return url;
		}
		return url + (url.indexOf("?") == -1 ? "?" : "&") + q;
	}
}
