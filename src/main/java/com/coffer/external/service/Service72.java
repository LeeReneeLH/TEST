package com.coffer.external.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service72
* <p>Description: 数据字典关联关系同步接口（对外）</p>
* @author wangbaozhong
* @date 2017年7月10日 上午10:41:10
*/
@Component("Service72")
@Scope("singleton")
public class Service72 extends HardwardBaseService {

	@Autowired
	@Qualifier("Service15")
	private Service15 service;
	
	@Override
	public String execute(Map<String, Object> paramMap) {

		// TODO 握手加密验证信息
		
		return service.execute(paramMap);
	}

}
