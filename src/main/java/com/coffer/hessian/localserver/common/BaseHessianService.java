package com.coffer.hessian.localserver.common;

import java.util.Map;

/**
 * 对外调用服务统一接口
 * 
 * @author yuxixuan
 *
 */
public interface BaseHessianService {

	/**
	 * 接口执行方法
	 * 
	 * @param paramMap
	 * @return
	 */
	String execute(Map<String, Object> paramMap);

	/**
	 * 初始化本地化类
	 * 
	 * @param paramMap
	 */
	void initLocale(Map<String, Object> paramMap);
}
