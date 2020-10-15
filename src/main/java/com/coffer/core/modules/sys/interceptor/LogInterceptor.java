package com.coffer.core.modules.sys.interceptor;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.LogUtils;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 日志拦截器
 * @author Clark
 * @version 2015-05-25
 */
public class LogInterceptor extends BaseService implements HandlerInterceptor {

	private static final ThreadLocal<Long> startTimeThreadLocal =
			new NamedThreadLocal<Long>("ThreadLocal StartTime");
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
			Object handler) throws Exception {
		if (logger.isDebugEnabled()){
			long beginTime = System.currentTimeMillis();//1、开始时间  
	        startTimeThreadLocal.set(beginTime);		//线程绑定变量（该数据只有当前请求的线程可见）
	        logger.debug("*********************************** 开始 URI: {}",request.getRequestURI());
		} else {
			logger.info("*********************************** 开始 URI: {}", request.getRequestURI());
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null){
			logger.info("ViewName: " + modelAndView.getViewName());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
			Object handler, Exception ex) throws Exception {
		String requestRri = request.getRequestURI();
		String uriPrefix = request.getContextPath() + Global.getAdminPath();
		// 是否保存日志
		if ((StringUtils.startsWith(requestRri, uriPrefix) && this.isSaveLog(requestRri)) || ex != null) {
		
			User user = UserUtils.getUser();
			if (user != null && user.getId() != null) {
				StringBuilder params = new StringBuilder();
				int index = 0;
				for (Object param : request.getParameterMap().keySet()) {
					params.append((index++ == 0 ? "" : "&") + param + "=");
					params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase((String) param, "password") ? ""
							: request.getParameter((String) param), 999));
				}
				
				logger.info("==========机构名:" + user.getOffice().getName());
				logger.info("==========登录名:" + user.getLoginName());
				logger.info("==========IP:" + StringUtils.getRemoteAddr(request));
				logger.info("==========Params:" + params.toString());
				if (ex != null) {
					logger.error("==========Exception:" + ex.toString());
				}
			}
			
			// 保存日志
			LogUtils.saveLog(request, handler, ex, null);
		}
		
		// 打印JVM信息。
		if (logger.isDebugEnabled()){
			long beginTime = startTimeThreadLocal.get();//得到线程绑定的局部变量（开始时间）  
			long endTime = System.currentTimeMillis(); 	//2、结束时间  
//	        logger.debug("最大内存: {}m  已分配内存: {}m  已分配内存中的剩余空间: {}m  最大可用内存: {}m",
//	        		Runtime.getRuntime().maxMemory()/1024/1024, Runtime.getRuntime().totalMemory()/1024/1024, Runtime.getRuntime().freeMemory()/1024/1024, 
//					(Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory())/1024/1024);
			logger.debug("耗时：{}ms  开始计时: {}  计时结束：{}", (endTime - beginTime),
					new SimpleDateFormat("HH:mm:ss.SSS").format(beginTime),
					new SimpleDateFormat("HH:mm:ss.SSS").format(endTime));
	        logger.debug("*********************************** 结束 URI: {}",request.getRequestURI());
	        logger.debug("");
		} else {
			logger.info("*********************************** 结束 URI: {}", request.getRequestURI());
			logger.info("");
		}
		
	}

	/**
	 * @author Clark
	 * @date 2014年11月20日
	 * 
	 * @Description 判断当前操作是否保存日志
	 * @param requestRri
	 * @return
	 */
	private boolean isSaveLog(String requestRri){
		boolean isSave = false;
		// 取得URL后缀
		String[] urlSuffixs = Global.getStringArray("web.logInterceptUri");
		if (urlSuffixs == null || urlSuffixs.length == 0) {
			isSave = true;
		} else {
			String[] rriString = requestRri.split("/");
			if(rriString != null && rriString.length != 0) {
				for (String urlSuffix : urlSuffixs) {
					if (StringUtils.contains(rriString[rriString.length-1], urlSuffix)) {
						isSave = true;
						break;
					}
				}
			}
		}
		
		return isSave;
	}
}
