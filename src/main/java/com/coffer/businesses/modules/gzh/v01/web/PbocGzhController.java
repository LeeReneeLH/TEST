package com.coffer.businesses.modules.gzh.v01.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.coffer.core.common.web.BaseController;

/**
 * 
* Title: PbocController 
* <p>Description: 人民银行可流通纸币质量监管平台-人民银行端</p>
* @author wangbaozhong
* @date 2016年8月4日 上午9:53:56
 */
@Controller
@RequestMapping(value = "${adminPath}/gzh/v02/pbocGzh")
public class PbocGzhController extends BaseController {

	/**
	 * 
	 * Title: toPbocSccList
	 * <p>Description: 跳转至可疑币浓度列表页面</p>
	 * @author:     wangbaozhong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toPbocSccList")
	public String toPbocSccList(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/pboc/pbocSccList";
	}
	
	/**
	 * 
	 * Title: toPbocGzhList
	 * <p>Description: 跳转至GZH列表页面</p>
	 * @author:     wangbaozhong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toPbocGzhList")
	public String toPbocGzhList(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/pboc/pbocGzhList";
	}
	
	/**
	 * 
	 * Title: toPbocSaGraph
	 * <p>Description: 跳转至统计分析页面</p>
	 * @author:     wangbaozhong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toPbocSaGraph")
	public String toPbocSaGraph(HttpServletRequest request, HttpServletResponse response,Model model) {
		return "modules/gzh/v01/pboc/pbocSaGraph";
	}
	
	/**
	 * 
	 * Title: toPbocSaGraph
	 * <p>Description: 跳转至查看详情页面</p>
	 * @author:     wangbaozhong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toPbocShowDetail")
	public String toPbocShowDetail(HttpServletRequest request, HttpServletResponse response,Model model) {
		return "modules/gzh/v01/pboc/pbocShowDetail";
	}
	
	/**
	 * 
	 * Title: toPbocSaGraph
	 * <p>Description: 跳转至导入详情页面</p>
	 * @author:     wangbaozhong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toPbocImportDetail")
	public String toPbocImportDetail(HttpServletRequest request, HttpServletResponse response,Model model) {
		return "modules/gzh/v01/pboc/pbocImprotDetail";
	}
}
