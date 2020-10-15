package com.coffer.businesses.common.interceptor;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.coffer.core.modules.sys.interceptor.BaseAopInterceptor;

/**
 * @author Clark
 * @version 2014-11-20
 * 
 * 业务层拦截器
 */
@Aspect
@Component
public class BusinessInterceptor extends BaseAopInterceptor {
	@Override
	@Pointcut("execution(public * com.coffer.*.modules..service.*.*(..)) && @annotation(org.springframework.transaction.annotation.Transactional)")
	protected void pointcut() {
	}
	
	@Override
	@Pointcut("execution(public * com.coffer.*.modules..service.*.*(..))")
	protected void pointcutForThrowing() {
	}
}
