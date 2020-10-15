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
* Title: Service14 
* <p>Description: 同步物品库存信息</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:40:38
*/
@Component("Service14")
@Scope("singleton")
public class Service14 extends HardwardBaseService {

	@Autowired
	private IpadAjaxService ipadAjaxService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年12月22日
	 * 
	 *  14:同步物品库存信息
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		
		ReceiveEntity entity = new ReceiveEntity();
		entity.setServiceNo(serviceNo);
		entity.setVersionNo(paramMap.get(Parameter.VERSION_NO_KEY).toString());
		if (paramMap.get("searchDate") != null) {
			entity.setSearchDate(paramMap.get("searchDate").toString());
		}
		if (paramMap.get("officeId") != null) {
			entity.setOfficeId(paramMap.get("officeId").toString());
		}
		return ipadAjaxService.getGoodsAndStores(serviceNo, entity);
	}

}
