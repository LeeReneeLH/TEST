package com.coffer.external.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.common.entity.ReceiveEntity;
import com.coffer.businesses.modules.common.service.IpadAjaxService;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service13 
* <p>Description: 同步物品字典</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:40:18
*/
@Component("Service13")
@Scope("singleton")
public class Service13 extends HardwardBaseService {
	
	@Autowired
	private IpadAjaxService ipadAjaxService;

	/**
	 * 
	 * @author LLF
	 * @version 2015年12月22日
	 * 
	 *  13:同步物品字典
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		
		ReceiveEntity entity = new ReceiveEntity();
		entity.setServiceNo(serviceNo);
		entity.setVersionNo(paramMap.get(Parameter.VERSION_NO_KEY).toString());
		if(paramMap.get("searchDate")!=null) {
			entity.setSearchDate(paramMap.get("searchDate").toString());
		}
		return ipadAjaxService.getGoodsDictInfo(serviceNo, entity);
	}

}
