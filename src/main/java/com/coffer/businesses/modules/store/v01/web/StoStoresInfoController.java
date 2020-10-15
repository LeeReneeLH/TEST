package com.coffer.businesses.modules.store.v01.web;

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

import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.service.StoStoresInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 库存管理Controller
 * @author LLF
 * @version 2015-09-09
 */
@Controller
@RequestMapping(value = "${adminPath}/store/stoStoresInfo")
public class StoStoresInfoController extends BaseController {

	@Autowired
	private StoStoresInfoService stoStoresInfoService;
	
	@ModelAttribute
	public StoStoresInfo get(@RequestParam(required=false) String id) {
		StoStoresInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoStoresInfoService.get(id);
		}
		if (entity == null){
			entity = new StoStoresInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("store:stoStoresInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(StoStoresInfo stoStoresInfo,@RequestParam(required = false) boolean isSearch, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<StoStoresInfo> page = new Page<StoStoresInfo>(request, response);
//		if(isSearch) {
			stoStoresInfo.setPage(page);
			page.setList(stoStoresInfoService.findStoStoresInfoList(stoStoresInfo));
			model.addAttribute("page", page);
//		}
		model.addAttribute("stoStoresInfo", stoStoresInfo);
		return "modules/store/v01/stoStores/stoStoresInfoList";
	}
	
	@RequiresPermissions("store:stoStoresInfo:view")
	@RequestMapping(value = { "historyList" })
	public String historyList(StoStoresHistory stoStoresHistory,@RequestParam(required = false) boolean isSearch, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<StoStoresHistory> page = new Page<StoStoresHistory>(request, response);
//		if(isSearch) {
			stoStoresHistory.setPage(page);
			page.setList(stoStoresInfoService.findStoStoresHistoryList(stoStoresHistory));
			model.addAttribute("page", page);
//		}
		model.addAttribute("stoStoresHistory", stoStoresHistory);
		return "modules/store/v01/stoStores/stoStoresHistoryList";
	}

	@RequiresPermissions("store:stoStoresInfo:view")
	@RequestMapping(value = "form")
	public String form(StoStoresInfo stoStoresInfo, Model model) {
		model.addAttribute("stoStoresInfo", stoStoresInfo);
		return "modules/store/stoStoresInfoForm";
	}

	@RequiresPermissions("store:stoStoresInfo:edit")
	@RequestMapping(value = "save")
	public String save(StoStoresInfo stoStoresInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoStoresInfo)){
			return form(stoStoresInfo, model);
		}
		stoStoresInfoService.save(stoStoresInfo);
		addMessage(redirectAttributes, "保存库存保存成功");
		return "redirect:"+Global.getAdminPath()+"/store/stoStoresInfo/?repage";
	}
	
	@RequiresPermissions("store:stoStoresInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(StoStoresInfo stoStoresInfo, RedirectAttributes redirectAttributes) {
		stoStoresInfoService.delete(stoStoresInfo);
		addMessage(redirectAttributes, "删除库存保存成功");
		return "redirect:"+Global.getAdminPath()+"/store/stoStoresInfo/?repage";
	}

}