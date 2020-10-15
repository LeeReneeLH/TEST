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

import com.coffer.businesses.modules.store.v01.entity.StoStockCountInfo;
import com.coffer.businesses.modules.store.v01.service.StoStockCountInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 盘点管理Controller
 * @author LLF
 * @version 2015-09-22
 */
@Controller
@RequestMapping(value = "${adminPath}/store/stoStockCountInfo")
public class StoStockCountInfoController extends BaseController {

	@Autowired
	private StoStockCountInfoService stoStockCountInfoService;
	
	@ModelAttribute
	public StoStockCountInfo get(@RequestParam(required=false) String id) {
		StoStockCountInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoStockCountInfoService.get(id);
		}
		if (entity == null){
			entity = new StoStockCountInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("store:stoStockCountInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoStockCountInfo stoStockCountInfo,@RequestParam(required = false) boolean isSearch, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoStockCountInfo> page = new Page<StoStockCountInfo>(request, response);
//		if(isSearch) {
			page = stoStockCountInfoService.findPage(page, stoStockCountInfo); 
			model.addAttribute("page", page);
//		}
		return "modules/store/v01/stoStockCount/stoStockCountInfoList";
	}

	@RequiresPermissions("store:v01:stoStockCountInfo:view")
	@RequestMapping(value = "form")
	public String form(StoStockCountInfo stoStockCountInfo, Model model) {
		model.addAttribute("stoStockCountInfo", stoStockCountInfo);
		return "modules/store/v01/stoStockCountInfoForm";
	}

	@RequiresPermissions("store:v01:stoStockCountInfo:edit")
	@RequestMapping(value = "save")
	public String save(StoStockCountInfo stoStockCountInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoStockCountInfo)){
			return form(stoStockCountInfo, model);
		}
		stoStockCountInfoService.save(stoStockCountInfo);
		addMessage(redirectAttributes, "保存盘点信息成功");
		return "redirect:"+Global.getAdminPath()+"/store/v01/stoStockCountInfo/?repage";
	}
	
	@RequiresPermissions("store:v01:stoStockCountInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(StoStockCountInfo stoStockCountInfo, RedirectAttributes redirectAttributes) {
		stoStockCountInfoService.delete(stoStockCountInfo);
		addMessage(redirectAttributes, "删除盘点信息成功");
		return "redirect:"+Global.getAdminPath()+"/store/v01/stoStockCountInfo/?repage";
	}

}