package com.coffer.businesses.modules.store.v02.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 原封新券管理Controller
 * @author LLF
 * @version 2016-05-30
 */
@Controller
@RequestMapping(value = "${adminPath}/v02/stoOriginalBanknote")
public class StoOriginalBanknoteController extends BaseController {

	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	
	@ModelAttribute
	public StoOriginalBanknote get(@RequestParam(required=false) String id) {
		StoOriginalBanknote entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoOriginalBanknoteService.get(id);
		}
		if (entity == null){
			entity = new StoOriginalBanknote();
		}
		return entity;
	}
	
	//@RequiresPermissions("v02:stoOriginalBanknote:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoOriginalBanknote stoOriginalBanknote, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 查询当前机构数据
		stoOriginalBanknote.setRoffice(UserUtils.getUser().getOffice());
		
		Page<StoOriginalBanknote> page = stoOriginalBanknoteService.findPage(new Page<StoOriginalBanknote>(request, response), stoOriginalBanknote); 
		model.addAttribute("page", page);
		model.addAttribute("stoOriginalBanknote", stoOriginalBanknote);
		return "modules/store/v02/stoOriginalBanknote/stoOriginalBanknoteList";
	}
	
	@RequestMapping(value = {"boxList"})
	public String boxList(StoOriginalBanknote stoOriginalBanknote, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 查询当前机构数据
		stoOriginalBanknote.setRoffice(UserUtils.getUser().getOffice());
		Page<StoOriginalBanknote> page = stoOriginalBanknoteService.findPage(new Page<StoOriginalBanknote>(request, response), stoOriginalBanknote); 
		model.addAttribute("page", page);
		model.addAttribute("stoOriginalBanknote", stoOriginalBanknote);
		return "modules/store/v02/stoOriginalBanknote/stoOriginalBanknoteBoxList";
	}
	
	@RequestMapping(value = { "outList" })
	public String outList(StoOriginalBanknote stoOriginalBanknote, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		Page<StoOriginalBanknote> page = stoOriginalBanknoteService.findOutPage(new Page<StoOriginalBanknote>(request,
				response), stoOriginalBanknote);
		model.addAttribute("page", page);
		model.addAttribute("stoOriginalBanknote", stoOriginalBanknote);
		return "modules/store/v02/stoOriginalBanknote/stoOriginalBanknoteOutList";
	}
	
	@RequestMapping(value = { "findDetail" })
	public String findDetail(StoOriginalBanknote stoOriginalBanknote, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		List<StoOriginalBanknote> list = stoOriginalBanknoteService.findList(stoOriginalBanknote);
		model.addAttribute("outId", stoOriginalBanknote.getOutId());
		// 获得出库机构
		model.addAttribute("cofficeName", stoOriginalBanknote.getCofficeName());
		model.addAttribute("totalAmount", stoOriginalBanknote.getTotalAmount());
		model.addAttribute("list", list);
		return "modules/store/v02/stoOriginalBanknote/stoOriginalBanknoteDetailList";
	}

	//@RequiresPermissions("v02:stoOriginalBanknote:view")
	@RequestMapping(value = "form")
	public String form(StoOriginalBanknote stoOriginalBanknote, Model model) {
		model.addAttribute("stoOriginalBanknote", stoOriginalBanknote);
		return "stooriginalbanknote/v02/stoOriginalBanknoteForm";
	}

	//@RequiresPermissions("v02:stoOriginalBanknote:edit")
	@RequestMapping(value = "save")
	public String save(StoOriginalBanknote stoOriginalBanknote, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoOriginalBanknote)){
			return form(stoOriginalBanknote, model);
		}
		stoOriginalBanknoteService.save(stoOriginalBanknote);
		addMessage(redirectAttributes, "保存原封新券管理成功");
		return "redirect:"+Global.getAdminPath()+"/v02/stoOriginalBanknote/?repage";
	}
	
	//@RequiresPermissions("v02:stoOriginalBanknote:edit")
	@RequestMapping(value = "delete")
	public String delete(StoOriginalBanknote stoOriginalBanknote, RedirectAttributes redirectAttributes) {
		stoOriginalBanknoteService.delete(stoOriginalBanknote);
		addMessage(redirectAttributes, "删除原封新券管理成功");
		return "redirect:"+Global.getAdminPath()+"/v02/stoOriginalBanknote/?repage";
	}

}