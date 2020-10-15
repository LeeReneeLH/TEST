/**
 * wenjian:    DecoratorService.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年7月13日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年7月13日 下午1:01:50
 */
package com.coffer.external.service;

import java.util.Map;

import com.coffer.external.hessian.HardwardBaseService;
import com.coffer.external.hessian.HardwardServiceInterface;

/**
* Title: DecoratorService 
* <p>Description: 装饰类</p>
* @author wangbaozhong
* @date 2017年7月13日 下午1:01:50
*/
public class DecoratorService extends HardwardBaseService implements HardwardServiceInterface {

	private HardwardServiceInterface service;
	
	public DecoratorService(HardwardServiceInterface service) {
		this.service = service;
	}
	/* (非 Javadoc)
	 * @author:     wangbaozhong
	 * <p>Title: execute</p>
	 * <p>Description: </p>
	 * @param paramMap
	 * @return
	 * @see com.coffer.external.hessian.HardwardServiceInterface#execute(java.util.Map)
	*/
	@Override
	public String execute(Map<String, Object> paramMap) {
		service.initLocale(paramMap);
		return this.service.execute(paramMap);
	}

}
