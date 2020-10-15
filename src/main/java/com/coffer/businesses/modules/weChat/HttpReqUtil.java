package com.coffer.businesses.modules.weChat;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.coffer.businesses.common.utils.weChat.MyX509TrustManager;
import com.coffer.businesses.common.utils.weChat.WeixinUtil;
import com.coffer.businesses.modules.weChat.v03.entity.WechatResult;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StreamUtils;

import net.sf.json.JSONObject;


/**
 * ClassName: WechatMessageUtils
 * 
 * @Description: 微信消息工具类
 * @author qph
 * @date 2018年5月17日 上午10:05:04
 */
public class HttpReqUtil {

	private static int DEFAULT_CONNTIME = 5000;
	private static int DEFAULT_READTIME = 5000;

	/**
	 * http请求
	 * 
	 * @param method
	 *            请求方法GET/POST
	 * @param path
	 *            请求路径
	 * @param timeout
	 *            连接超时时间 默认为5000
	 * @param readTimeout
	 *            读取超时时间 默认为5000
	 * @param data
	 *            数据
	 * @return
	 */
	private static String defaultConnection(String method, String path, int timeout, int readTimeout, String data, String encoding)
			throws Exception {
		String result = "";
		URL url = new URL(path);
		if (url != null) {
			HttpURLConnection conn = getConnection(method, url);
			conn.setConnectTimeout(timeout == 0 ? DEFAULT_CONNTIME : timeout);
			conn.setReadTimeout(readTimeout == 0 ? DEFAULT_READTIME : readTimeout);
			if (data != null && !"".equals(data)) {
				OutputStream output = conn.getOutputStream();
				output.write(data.getBytes(WeChatConstant.DEFAULT_CHARACTER_ENCODING));
				output.flush();
				output.close();
			}
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream input = conn.getInputStream();
				result = StreamUtils.InputStreamTOString(input, encoding);
				conn.disconnect();
			}
		}
		return result;
	}

	/**
	 * 根据url的协议选择对应的请求方式
	 * 
	 * @param method
	 *            请求的方法
	 * @return
	 * @throws IOException
	 */
	private static HttpURLConnection getConnection(String method, URL url) throws IOException {
		HttpURLConnection conn = null;
		if ("https".equals(url.getProtocol())) {
			SSLContext context = null;
			try {
				context = SSLContext.getInstance("SSL", "SunJSSE");
				context.init(new KeyManager[0], new TrustManager[] { new MyX509TrustManager() },
						new java.security.SecureRandom());
			} catch (Exception e) {
				throw new IOException(e);
			}
			HttpsURLConnection connHttps = (HttpsURLConnection) url.openConnection();
			connHttps.setSSLSocketFactory(context.getSocketFactory());
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			conn = connHttps;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setRequestMethod(method);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		return conn;
	}

	/**
	 * 设置参数
	 * 
	 * @param map
	 *            参数map
	 * @param path
	 *            需要赋值的path
	 * @param charset
	 *            编码格式 默认编码为utf-8(取消默认)
	 * @return 已经赋值好的url 只需要访问即可
	 */
	public static String setParmas(Map<String, String> map, String path, String charset) throws Exception {
		String result = "";
		boolean hasParams = false;
		if (path != null && !"".equals(path)) {
			if (MapUtils.isNotEmpty(map)) {
				StringBuilder builder = new StringBuilder();
				Set<Entry<String, String>> params = map.entrySet();
				for (Entry<String, String> entry : params) {
					String key = entry.getKey().trim();
					String value = entry.getValue().trim();
					if (hasParams) {
						builder.append("&");
					} else {
						hasParams = true;
					}
					if (charset != null && !"".equals(charset)) {
						// builder.append(key).append("=").append(URLDecoder.(value,charset));
						builder.append(key).append("=").append(Encodes.urlEncode(value));
					} else {
						builder.append(key).append("=").append(value);
					}
				}
				result = builder.toString();
			}
		}
		return doUrlPath(path, result).toString();
	}

	/**
	 * 设置连接参数
	 * 
	 * @param path
	 *            路径
	 * @return
	 */
	private static URL doUrlPath(String path, String query) throws Exception {
		URL url = new URL(path);
		if (StringUtils.isEmpty(path)) {
			return url;
		}
		if (StringUtils.isEmpty(url.getQuery())) {
			if (path.endsWith("?")) {
				path += query;
			} else {
				path = path + "?" + query;
			}
		} else {
			if (path.endsWith("&")) {
				path += query;
			} else {
				path = path + "&" + query;
			}
		}
		return new URL(path);
	}

	/**
	 * 默认的http请求执行方法,返回
	 * 
	 * @param method 请求的方法 POST/GET
	 * @param path 请求path 路径
	 * @param map 请求参数集合
	 * @param data 输入的数据 允许为空
	 * @return
	 */
	public static String HttpDefaultExecute(String method, String path, Map<String, String> map, String data, String encoding) {
		String result = "";
		try {
			String url = setParmas((TreeMap<String, String>) map, path, "");
			result = defaultConnection(method, url, DEFAULT_CONNTIME, DEFAULT_READTIME, data, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 默认的https执行方法,返回
	 * 
	 * @param method
	 *            请求的方法 POST/GET
	 * @param path
	 *            请求path 路径
	 * @param map
	 *            请求参数集合
	 * @param data
	 *            输入的数据 允许为空
	 * @return
	 */
	public static String HttpsDefaultExecute(String method, String path, Map<String, String> map, String data, String encoding) {
		String result = "";
		try {
			String url = setParmas((TreeMap<String, String>) map, path, "");
			JSONObject json = WeixinUtil.httpRequest(url, method, data);
			result = json.getString("errcode");

			// result = defaultConnection(method, url, DEFAULT_CONNTIME,
			// DEFAULT_READTIME, data, encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 根据返回的头信息返回具体信息
	 * 
	 * @param contentType
	 *            contentType请求头信息
	 * @return Result.type==1 表示文本消息,
	 */
	private static WechatResult contentType(String contentType, HttpURLConnection conn, String savePath, String encoding) {
		WechatResult result = new WechatResult();
		try {
			if (conn != null) {
				InputStream input = conn.getInputStream();
				// 考虑使用switch
				// switch(contentType){
				// case "image/gif": result = inputStreamToMedia(input,
				// savePath, "gif");
				// }
				if (contentType.equals("image/gif")) { // gif图片
					result = inputStreamToMedia(input, savePath, "gif");
				} else if (contentType.equals("image/jpeg")) { // jpg图片
					result = inputStreamToMedia(input, savePath, "jpg");
				} else if (contentType.equals("image/jpg")) { // jpg图片
					result = inputStreamToMedia(input, savePath, "jpg");
				} else if (contentType.equals("image/png")) { // png图片
					result = inputStreamToMedia(input, savePath, "png");
				} else if (contentType.equals("image/bmp")) { // bmp图片
					result = inputStreamToMedia(input, savePath, "bmp");
				} else if (contentType.equals("audio/x-wav")) { // wav语音
					result = inputStreamToMedia(input, savePath, "wav");
				} else if (contentType.equals("audio/x-ms-wma")) { // wma语言
					result = inputStreamToMedia(input, savePath, "wma");
				} else if (contentType.equals("audio/mpeg")) { // mp3语言
					result = inputStreamToMedia(input, savePath, "mp3");
				} else if (contentType.equals("text/plain")) { // 文本信息
					String str = StreamUtils.InputStreamTOString(input, encoding);
					result.setObject(str);
				} else if (contentType.equals("application/json")) { // 返回json格式的数据
					String str = StreamUtils.InputStreamTOString(input, encoding);
					result.setObject(str);
				}
			} else {
				result.setObject("conn is null!");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 默认的下载素材方法
	 * 
	 * @param method
	 *            http方法 POST/GET
	 * @param apiPath
	 *            api路径
	 * @param savePath
	 *            素材需要保存的路径
	 * @return 是否下载成功 Reuslt.success==true 表示下载成功
	 */
	public static WechatResult downMeaterMetod(TreeMap<String, String> params, String method, String apiPath,
			String savePath, String encoding) {
		WechatResult result = new WechatResult();
		try {
			apiPath = setParmas(params, apiPath, "");
			URL url = new URL(apiPath);
			HttpURLConnection conn = getConnection(method, url);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String contentType = conn.getContentType();
				result = contentType(contentType, conn, savePath, encoding);
			} else {
				result.setObject(conn.getResponseCode() + "," + conn.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将字符流转换为图片文件
	 * 
	 * @param input
	 *            字符流
	 * @param savePath
	 *            图片需要保存的路径
	 * @param 类型
	 *            jpg/png等
	 * @return
	 */
	private static WechatResult inputStreamToMedia(InputStream inputStream, String savePath, String type) {
		WechatResult result = new WechatResult();
		File file = null;
		file = new File(savePath);
		String paramPath = file.getParent(); // 路径
		String fileName = file.getName(); //
		String newName = fileName.substring(0, fileName.lastIndexOf(".")) + "." + type;// 根据实际返回的文件类型后缀
		savePath = paramPath + "\\" + newName;
		if (!file.exists()) {
			File dirFile = new File(paramPath);
			dirFile.mkdirs();
		}
		file = new File(savePath);
		try {
			IOUtils.copy(inputStream, new FileOutputStream(file));
			result.setSuccess(true);
			result.setObject("save success!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 改变图片大小、格式
	 * 
	 * @param is
	 * @param os
	 * @param size
	 * @param format
	 * @return
	 * @throws IOException
	 */
	public static OutputStream resizeImage(InputStream inputStream, OutputStream outputStream, int size, String format)
			throws IOException {
		BufferedImage prevImage = ImageIO.read(inputStream);
		double width = prevImage.getWidth();
		double height = prevImage.getHeight();
		double percent = size / width;
		int newWidth = (int) (width * percent);
		int newHeight = (int) (height * percent);
		BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
		Graphics graphics = image.createGraphics();
		graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
		ImageIO.write(image, format, outputStream);
		outputStream.flush();
		return outputStream;
	}

	/**
	 * 获取客户端ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemortIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		// squid的squid.conf 的配制文件中forwarded_for项改为off时
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		// 多级反向代理
		if (ip != null && ip.indexOf(",") > 0 && ip.split(",").length > 1) {
			ip = ip.split(",")[0];
		}
		return ip;
	}




}