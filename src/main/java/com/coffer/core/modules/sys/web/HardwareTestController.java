package com.coffer.core.modules.sys.web;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.caucho.hessian.client.HessianProxyFactory;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.web.BaseController;
import com.coffer.external.hessian.IHardwareService;



/**
 * 接口测试窗体Controller
 * @author zhengkaiyuan
 * @version 2016年9月27日
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/HardwareTest")
public class HardwareTestController extends BaseController{
	/**
	 * 进行接口调用
	 * @author Zhengkaiyuan
	 * @version 2016年9月27日
	 *
	 *
	 * @param input 输入Json
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "test")
	public String test(String input , HttpServletRequest request, HttpServletResponse response, Model model) {
		String output = "";
		String timeMessage = "";
		if(input != null){
			try {
				//对获取的Json进行格式化处理
				String inJson = input.replaceAll("&quot;", "'");
				//获取调试地址
				String url = Global.getConfig("test_hardware_service_url");
				//进行接口调用    并进行时间计算
				NumberFormat numberFormat = new DecimalFormat("接口调用时间：#.###秒");
				Date dateStart = new Date();
				long start = dateStart.getTime();
				output = this.test(url, inJson);
				Date dateStop = new Date();
				long stop = dateStop.getTime();
				timeMessage = numberFormat.format((double)(stop - start)/1000);
			} catch (Exception e) {
				//异常处理
				output = "输入不正确：\n" + e.getMessage();
			}
		}
		//添加返回的参数
		model.addAttribute("input", input);
		model.addAttribute("output", output);
		model.addAttribute("timeMessage", timeMessage);
		return "modules/sys/hardwareTest";
	}
	/**
	 * 接口调用函数
	 * @author Zhengkaiyuan
	 * @version 2016年9月27日
	 *
	 *
	 * @param url 地址
	 * @param inJson 输入Json
	 * @return 输出Json String类型
	 * @throws Exception
	 */
	private String test(String url, String inJson) throws Exception{
		HessianProxyFactory factory = new HessianProxyFactory();
		IHardwareService s;
		s = (IHardwareService) factory.create(IHardwareService.class, url);
		String outJson = s.service(inJson);
		return outJson;
	}
}
