package com.coffer.external.hessian;

import java.util.Map;

/** * 
 * Title: HardwardServiceInterface
 * <p>
 * Description:对外调用服务统一接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:29:54
 */
public interface HardwardServiceInterface {
	/**
	 * 
	 * Title: execute
	 * <p>Description: 接口执行方法</p>
	 * @author:     wangbaozhong
	 * @param paramMap 参数
	 * @return 
	 * String    返回类型
	 */
	String execute(Map<String, Object> paramMap);
	
	void initLocale(Map<String, Object> paramMap);
}
