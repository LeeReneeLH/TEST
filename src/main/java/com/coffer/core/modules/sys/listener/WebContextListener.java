package com.coffer.core.modules.sys.listener;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

import com.coffer.core.modules.sys.service.SystemService;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (!SystemService.printKeyLoadMessage()){
			return null;
		}
		
//		AbstractHessianServiceExporter serviceExporter = new HessianServiceExporter();
//		serviceExporter.setPort(Integer.valueOf(Global.getConfig("hardware.service.port")));
//		serviceExporter.setServiceInterface(IHardwareService.class);
//		serviceExporter.setService(new HardwareService());
//		serviceExporter.startUp();
//		System.out.println("============启动硬件接口服务============");
		
		return super.initWebApplicationContext(servletContext);
	}
}
