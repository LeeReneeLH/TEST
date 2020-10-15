package com.coffer.core.modules.sys.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.weChat.WeixinUtil;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.security.shiro.session.SessionDAO;
import com.coffer.core.common.servlet.ValidateCodeServlet;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.CookieUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.entity.WxUserInfo;
import com.coffer.core.modules.sys.security.FormAuthenticationFilter;
import com.coffer.core.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.service.Service0806;
import com.google.common.collect.Maps;

import net.sf.json.JSONObject;

/**
 * 登录Controller
 * 
 * @author Clark
 * @version 2015-6-8
 */
@Controller
public class LoginController extends BaseController {

	@Autowired
	private SessionDAO sessionDAO;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private EquipmentInfoDao equipmentInfoDao;
	
	@Autowired
	private Service0806 service0806;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired(required = false)
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		RedisSerializer<?> stringSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringSerializer);
		redisTemplate.setValueSerializer(stringSerializer);
		redisTemplate.setHashKeySerializer(stringSerializer);
		redisTemplate.setHashValueSerializer(stringSerializer);
		this.redisTemplate = redisTemplate;
	}
//	@Autowired
//	private UiasService uiasService;

//	/**
//	 * 自动登录
//	 */
//	@RequestMapping(value = "${adminPath}/autoLogin", method = RequestMethod.GET)
//	public String autologin(HttpServletRequest request, HttpServletResponse response, Model model) {
//		String userid = uiasService.checkLogin(request);
//		model.addAttribute("username", userid);
//		model.addAttribute("password", "");
//
//		return "modules/sys/autoLogin";
//	}

	/**
	 * 管理登录
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		Principal principal = UserUtils.getPrincipal();

		// // 默认页签模式
		// String tabmode = CookieUtils.getCookie(request, "tabmode");
		// if (tabmode == null){
		// CookieUtils.setCookie(response, "tabmode", "1");
		// }

		if (logger.isDebugEnabled()) {
			logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}

		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
			CookieUtils.setCookie(response, "LOGINED", "false");
		}

		// 如果已经登录，则跳转到管理首页
		if (principal != null && !principal.isMobileLogin()) {
			return "redirect:" + adminPath;
		}
		// String view;
		// view = "/WEB-INF/views/modules/sys/sysLogin.jsp";
		// view = "classpath:";
		// view +=
		// "jar:file:/D:/GitHub/jeesite/src/main/webapp/WEB-INF/lib/jeesite.jar!";
		// view += "/"+getClass().getName().replaceAll("\\.",
		// "/").replace(getClass().getSimpleName(), "")+"view/sysLogin";
		// view += ".jsp";
		return "modules/sys/sysLogin";
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
	public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
		Principal principal = UserUtils.getPrincipal();

		// 如果已经登录，则跳转到管理首页
		if (principal != null) {
			return "redirect:" + adminPath;
		}

		String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		boolean rememberMe = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
		boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		String message = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

		if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
			message = "用户或密码错误, 请重试.";
		}

		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
		model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);

		if (logger.isDebugEnabled()) {
			logger.debug("login fail, active session size: {}, message: {}, exception: {}", sessionDAO
					.getActiveSessions(false).size(), message, exception);
		}

		// 非授权异常，登录失败，验证码加1。
		if (!UnauthorizedException.class.getName().equals(exception)) {
			model.addAttribute("isValidateCodeLogin", isValidateCodeLogin(username, true, false));
		}

		// 验证失败清空验证码
		request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

		// 如果是手机登录，则返回JSON字符串
		if (mobile) {
			return renderString(response, model);
		}

		return "modules/sys/sysLogin";
	}

	/**
	 * 登录成功，进入管理首页
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "${adminPath}")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		Principal principal = UserUtils.getPrincipal();

		// 登录成功后，验证码计算器清零
		isValidateCodeLogin(principal.getLoginName(), false, true);

		if (logger.isDebugEnabled()) {
			logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}

		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
			String logined = CookieUtils.getCookie(request, "LOGINED");
			if (StringUtils.isBlank(logined) || "false".equals(logined)) {
				CookieUtils.setCookie(response, "LOGINED", "true");
			} else if (StringUtils.equals(logined, "true")) {
				UserUtils.getSubject().logout();
				return "redirect:" + adminPath + "/login";
			}
		}

		// 如果是手机登录，则返回JSON字符串
		if (principal.isMobileLogin()) {
			if (request.getParameter("login") != null) {
				return renderString(response, principal);
			}
			if (request.getParameter("index") != null) {
				return "modules/sys/sysIndex";
			}
			return "redirect:" + adminPath + "/login";
		}
		//websocket建立session
		User user = UserUtils.getUser();
		HttpSession session = request.getSession(false);
		session.setAttribute(Constant.SESSION_USER_ID, user.getId());

		// // 登录成功后，获取上次登录的当前站点ID
		// UserUtils.putCache("siteId",
		// StringUtils.toLong(CookieUtils.getCookie(request, "siteId")));

		// System.out.println("==========================a");
		// try {
		// byte[] bytes =
		// com.coffer.core.common.utils.FileUtils.readFileToByteArray(
		// com.coffer.core.common.utils.FileUtils.getFile("c:\\sxt.dmp"));
		// UserUtils.getSession().setAttribute("kkk", bytes);
		// UserUtils.getSession().setAttribute("kkk2", bytes);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// // for (int i=0; i<1000000; i++){
		// // //UserUtils.getSession().setAttribute("a", "a");
		// // request.getSession().setAttribute("aaa", "aa");
		// // }
		// System.out.println("==========================b");
		return "modules/sys/sysIndex";
	}

	/**
	 * 获取主题方案
	 */
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotBlank(theme)) {
			CookieUtils.setCookie(response, "theme", theme);
		} else {
			theme = CookieUtils.getCookie(request, "theme");
		}
		return "redirect:" + request.getParameter("url");
	}

	/**
	 * 是否是验证码登录
	 * 
	 * @param useruame
	 *            用户名
	 * @param isFail
	 *            计数加1
	 * @param clean
	 *            计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
		Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
		if (loginFailMap == null) {
			loginFailMap = Maps.newHashMap();
			CacheUtils.put("loginFailMap", loginFailMap);
		}
		Integer loginFailNum = loginFailMap.get(useruame);
		if (loginFailNum == null) {
			loginFailNum = 0;
		}
		if (isFail) {
			loginFailNum++;
			loginFailMap.put(useruame, loginFailNum);
		}
		if (clean) {
			loginFailMap.remove(useruame);
		}
		return loginFailNum >= 3;
	}
	
	/**
	 * 微信扫码登录系统
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping("${adminPath}/wxLogin")
	public void wxLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.info("====================================== 扫码回调方法开始 ======================================");
		try {
			String unionId = null;
			String code = request.getParameter("code");
			String uuid = request.getParameter("uuid");
			String eqpId = request.getParameter("eqpId");
			String requestUrl = Global.getConfig("CodeGetAccessTokenURL");
			requestUrl = requestUrl.replace("APPID", Global.getConfig("publicPlatform.appId"));
			requestUrl = requestUrl.replace("SECRET", Global.getConfig("publicPlatform.secret"));
			requestUrl = requestUrl.replace("CODE", code);
			JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "POST", null);
			if (jsonObject != null) {
				//获取unionId
				unionId = jsonObject.getString("unionid");
				//封装结果集
				Map<String, Object> resultMap = new HashMap<String, Object>();
				EquipmentInfo equipmentInfo = equipmentInfoDao.get(eqpId);
				if (equipmentInfo != null) {
					 User user = userDao.getUserByUnionId(unionId);
						if (user != null) {
							String errorMsg = service0806.wxExecute(user, equipmentInfo);
							if (!StringUtils.isEmpty(errorMsg)) {
								resultMap.put("resultFlag", "01");
								resultMap.put("errorMsg", errorMsg + "!");
								resultMap.put("data", "");
								logger.info("====================================== " + errorMsg + " ======================================");
							} else {
								WxUserInfo wxUser = new WxUserInfo();
								wxUser.setUserId(user.getId());
								wxUser.setUserName(user.getName());
								wxUser.setIdCardNo(user.getIdcardNo());
								wxUser.setLoginName(user.getLoginName());
								wxUser.setMobile(user.getMobile());
								wxUser.setOfficeId(user.getOffice().getId());
								wxUser.setPassword(user.getPassword());
								wxUser.setUserType(user.getUserType());
								resultMap.put("resultFlag", "00");
								resultMap.put("errorMsg", "");
								resultMap.put("data", wxUser);
							}
						} else {
							resultMap.put("resultFlag", "01");
							resultMap.put("errorMsg", "用户信息有误!");
							resultMap.put("data", "");
							logger.info("====================================== 用户信息有误 ======================================");
						}
				} else {
					resultMap.put("resultFlag", "01");
					resultMap.put("errorMsg", "设备信息有误!");
					resultMap.put("data", "");
					logger.info("====================================== 设备信息有误 ======================================");
				}
				this.setRedisTemplate(redisTemplate);
				redisTemplate.opsForHash().put("LOGIN_USER_MAP", uuid, gson.toJson(resultMap));
			}
		} catch(Exception e) {
			logger.info("====================================== 扫码回调方法错误,错误原因" + e.getMessage() +" ======================================");
		}
		logger.info("====================================== 扫码回调方法结束 ======================================");
	}
	
//	@RequestMapping("${adminPath}/logginPolling")
//	@ResponseBody
//	public String loginPolling(HttpServletRequest request, HttpServletResponse response) {
//		JSONObject jsonObject = new JSONObject();
//		try {
//			String uuid = request.getParameter("uuid");
//			String user = (String) redisTemplate.opsForHash().get("LOGIN_USER_MAP", uuid);
//			if (user != null) {
//				jsonObject = JSONObject.fromObject(user);
//			}
//		} catch (Exception e) {
//			jsonObject.put("resultFlag", "01");
//			jsonObject.put("errorMessage", e);
//		}
//		return gson.toJson(jsonObject);
//	}
}
