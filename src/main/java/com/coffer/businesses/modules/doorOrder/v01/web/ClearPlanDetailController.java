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

import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanDetail;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearPlanDetailService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 清机明细Controller
 * 
 * @author XL
 * @version 2019-06-26
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/clearPlanDetail")
public class ClearPlanDetailController extends BaseController {

	@Autowired
	private ClearPlanDetailService clearPlanDetailService;

	@ModelAttribute
	public ClearPlanDetail get(@RequestParam(required = false) String id) {
		ClearPlanDetail entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = clearPlanDetailService.get(id);
		}
		if (entity == null) {
			entity = new ClearPlanDetail();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:clearPlanDetail:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClearPlanDetail clearPlanDetail, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<ClearPlanDetail> page = clearPlanDetailService.findPage(new Page<ClearPlanDetail>(request, response),
				clearPlanDetail);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/clearPlanDetailList";
	}

	@RequiresPermissions("doorOrder:v01:clearPlanDetail:view")
	@RequestMapping(value = "form")
	public String form(ClearPlanDetail clearPlanDetail, Model model) {
		model.addAttribute("clearPlanDetail", clearPlanDetail);
		return "modules/doorOrder/v01/clearPlanDetailForm";
	}

	@RequiresPermissions("doorOrder:v01:clearPlanDetail:edit")
	@RequestMapping(value = "save")
	public String save(ClearPlanDetail clearPlanDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, clearPlanDetail)) {
			return form(clearPlanDetail, model);
		}
		clearPlanDetailService.save(clearPlanDetail);
		addMessage(redirectAttributes, "保存清机明细成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearPlanDetail/?repage";
	}

	@RequiresPermissions("doorOrder:v01:clearPlanDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(ClearPlanDetail clearPlanDetail, RedirectAttributes redirectAttributes) {
		clearPlanDetailService.delete(clearPlanDetail);
		addMessage(redirectAttributes, "删除清机明细成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearPlanDetail/?repage";
	}

}