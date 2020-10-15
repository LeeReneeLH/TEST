package com.coffer.hessian.localserver.common;

import java.util.Map;

/**
 * 装饰类
 * 
 * @author yuxixuan
 *
 */
public class DecoratorService {

	/**
	 * 对外调用服务统一接口
	 */
	private BaseHessianService service;

	/**
	 * 构造方法
	 * 
	 * @param service
	 */
	public DecoratorService(BaseHessianService service) {
		this.service = service;
	}

	/**
	 * 接口执行方法
	 */
	public String execute(Map<String, Object> paramMap) {
		service.initLocale(paramMap);
		return service.execute(paramMap);
	}

}
