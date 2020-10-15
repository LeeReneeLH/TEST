package com.coffer.businesses.modules.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.core.common.web.BaseController;

/**
 * 
 * @author qipeihong
 * @version 2017年9月21日 控制层共通方法
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/Common")
public class CommonController extends BaseController {
	/**
	 * 显示授权页面
	 * 
	 * @author qipeihong
	 * @version 2017-9-18
	 * 
	 */
	@RequestMapping(value = "showClearAuthorize")
	public String showClearAuthorize(@RequestParam String clearId, Model model) {
		model.addAttribute("clearId", clearId);
		return "modules/clear/v03/authorize/authorizeDetail";
	}

	/**
	 * check
	 * 
	 * @author sg
	 * @version 2017-12-14
	 * 
	 */
	@RequestMapping(value = "checkSession")
	@ResponseBody
	public String checkSession(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return gson.toJson(session.getId());
		} else {
			return gson.toJson("");
		}
	}
}
