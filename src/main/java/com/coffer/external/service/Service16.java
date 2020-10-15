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
 * Title: Service16
 * <p>
 * Description: 上传盘点信息
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service16")
@Scope("singleton")
public class Service16 extends HardwardBaseService {
	@Autowired
	private IpadAjaxService ipadAjaxService;
	
	@Override
	public String execute(Map<String, Object> paramMap) {

		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		
		ReceiveEntity entity = new ReceiveEntity();
		entity.setServiceNo(serviceNo);
		entity.setVersionNo(paramMap.get(Parameter.VERSION_NO_KEY).toString());
	
		return ipadAjaxService.uploadStockCountInfo(serviceNo, entity);
	}

}
