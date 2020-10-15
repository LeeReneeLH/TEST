package com.coffer.businesses.modules.allocation.v01.web;

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

import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 同业调拨Controller
 * 
 * @author yuxixuan
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v01/interBankOffer")
public class InterBankOfferController extends BaseController {

	/** 调缴用Service */
	@Autowired
	private AllocationService allocationService;

	/**
	 * 根据流水号取得同业调拨任务
	 * 
	 * @param allId
	 * @return
	 */
	@ModelAttribute
	public AllAllocateInfo get(@RequestParam(required = false) String allId) {
		AllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)) {
			entity = allocationService.getAllocate(allId);
		}
		if (entity == null) {
			entity = new AllAllocateInfo();
		}
		return entity;
	}

	/**
	 * 根据查询条件取得同业调拨列表
	 * 
	 * @param allAllocateInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("allocation:v01:interBankOffer:view")
	@RequestMapping(value = { "list", "" })
	public String list(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<AllAllocateInfo> page = allocationService.findAllocation(new Page<AllAllocateInfo>(request, response),
				allAllocateInfo, true);
		model.addAttribute("page", page);
		return "modules/allocation/v01/interBankOffer/interBankOfferList.jsp";
	}

	/**
	 * 跳转到同业调拨编辑画面
	 * 
	 * @param allAllocateInfo
	 * @param model
	 * @return
	 */
	@RequiresPermissions("allocation:v01:interBankOffer:view")
	@RequestMapping(value = "form")
	public String form(AllAllocateInfo allAllocateInfo, Model model) {
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		return "modules/allocation/v01/interBankOffer/interBankOfferForm";
	}

	/**
	 * 保存
	 * 
	 * @param allAllocateInfo
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("allocation:v01:interBankOffer:edit")
	@RequestMapping(value = "save")
	public String save(AllAllocateInfo allAllocateInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, allAllocateInfo)) {
			return form(allAllocateInfo, model);
		}
		allocationService.saveAllocation(allAllocateInfo);
		addMessage(redirectAttributes, "保存同业调拨成功");
		return "redirect:" + Global.getAdminPath() + "/allocation/v02/pbocCombineBagDiff/?repage";
	}

	/**
	 * 删除
	 * 
	 * @param pbocCombineBagDiff
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("allocation:v01:interBankOffer:edit")
	@RequestMapping(value = "delete")
	public String delete(AllAllocateInfo allAllocateInfo, RedirectAttributes redirectAttributes) {
		allocationService.deleteCash(allAllocateInfo);
		addMessage(redirectAttributes, "删除同业调拨成功");
		return "redirect:" + Global.getAdminPath() + "/allocation/v02/pbocCombineBagDiff/?repage";
	}
}