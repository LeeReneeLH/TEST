package com.coffer.businesses.modules.gzh.v01.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.coffer.core.common.web.BaseController;

/**
 * 
* Title: ComBankGzhController 
* <p>Description: 人民银行可流通纸币质量监管平台-商业银行端 </p>
* @author wangbaozhong
* @date 2016年8月4日 上午9:58:41
 */
@Controller
@RequestMapping(value = "${adminPath}/gzh/v02/comBankGzh")
public class ComBankGzhController extends BaseController {

	@RequestMapping(value = "first")
	public String initFirst(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/";
	}
	/**
	 * 
	 * Title: toComBankfakenoteList
	 * <p>Description: 跳转至假币列表页面</p>
	 * @author:     wangdong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankfakenoteList")
	public String toComBankfakenoteList(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/comBankfakenoteList";
	}
	/**
	 * 
	 * Title: toComBankgzhqueryList
	 * <p>Description: 跳转至冠字号查询列表页面</p>
	 * @author:     wangdong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankgzhqueryList")
	public String toComBankgzhqueryList(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/comBankgzhqueryList";
	}
	
	/**
	 * 
	 * Title: toComBankdamagednoteList
	 * <p>Description: 跳转至残损币列表页面</p>
	 * @author:     wangdong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankdamagednoteList")
	public String toComBankdamagednoteList(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/comBankdamagednoteList";
	}
	/**
	 * 
	 * Title: toComBankRegister
	 * <p>Description: 跳转至登记页面</p>
	 * @author:     wangdong
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankRegister")
	public String toComBankRegister(HttpServletRequest request, HttpServletResponse response,Model model) {
		return "modules/gzh/v01/comBank/comBankRegister";
	}
	/**
	 * 
	 * Title: toComBankfsnList
	 * <p>Description: 跳转至FSN列表界面</p>
	 * @author:     zhengkaiyuan
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankfsnList")
	public String toComBankfsnList(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/comBankfsnList";
	}
	/**
	 * 
	 * Title: toComBankgzhList
	 * <p>Description: 跳转至GZH列表界面</p>
	 * @author:     zhengkaiyuan
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankgzhList")
	public String toComBankgzhList(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/comBankgzhList";
	}
	/**
	 * 
	 * Title: toComBankgzhUpload
	 * <p>Description: 跳转至GZH上传界面</p>
	 * @author:     zhengkaiyuan
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankgzhUpload")
	public String toComBankgzhUpload(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/comBankgzhUpload";
	}
	/**
	 * 
	 * Title: toComBankfsnUpload
	 * <p>Description: 跳转至FSN上传界面</p>
	 * @author:     zhengkaiyuan
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toComBankfsnUpload")
	public String toComBankf(HttpServletRequest request, HttpServletResponse response,Model model) {
		
		return "modules/gzh/v01/comBank/comBankfsnUpload";
	}
	
}
