package com.coffer.external.common.interceptor;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.coffer.core.modules.sys.interceptor.BaseAopInterceptor;

/**
 * @author Clark
 * @version 2015-06-10
 * 外部通信拦截器
 */
@Aspect
@Component
public class ExternalInterceptor extends BaseAopInterceptor {
	@Override
	@Pointcut("execution(public * com.coffer.external.hessian.*.*(..)) || execution(public * com.coffer.external.service.*.*(..))")
	protected void pointcut() {
	}
	
	@Override
	@Pointcut("execution(public * com.coffer.external.hessian.*.*(..)) || execution(public * com.coffer.external.service.*.*(..))")
	protected void pointcutForThrowing() {
	}
}
