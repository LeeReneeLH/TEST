package com.coffer.businesses.modules.doorOrder.v01.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearAddMoney;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearAddMoneyService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 清机加钞记录Controller
 * 
 * @author ZXK
 * @version 2019-07-23
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/clearAddMoney")
public class ClearAddMoneyController extends BaseController {

	@Autowired
	private ClearAddMoneyService clearAddMoneyService;

	@ModelAttribute
	public ClearAddMoney get(@RequestParam(required = false) String id) {
		ClearAddMoney entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = clearAddMoneyService.get(id);
		}
		if (entity == null) {
			entity = new ClearAddMoney();
		}
		return entity;
	}

	/**
	 * 获得清机加钞记录列表
	 * 
	 * @author ZXK
	 * @version 2019年7月23日
	 * @param clearAddMoney
	 * @param request
	 * @param response
	 * @param model
	 * @return 清机加钞记录一览页面
	 */
	@RequiresPermissions("doorOrder:clearAddMoney:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClearAddMoney clearAddMoney, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ClearAddMoney> page = clearAddMoneyService.findPage(new Page<ClearAddMoney>(request, response), clearAddMoney);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/clearAddMoney/clearAddMoneyList";
	}

	/**
	 * 删除清机加钞记录
	 * 
	 * @author ZXK
	 * @version 2019年7月23日
	 * @param clearAddMoney
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "delete")
	public String delete(ClearAddMoney clearAddMoney, RedirectAttributes redirectAttributes) {
		clearAddMoneyService.delete(clearAddMoney);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearAddMoney/?repage";
	}

}
