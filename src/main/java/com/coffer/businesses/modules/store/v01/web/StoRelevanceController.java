package com.coffer.businesses.modules.store.v01.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.store.v01.entity.StoBlankBillSelect;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.businesses.modules.store.v01.service.StoRelevanceService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.ServiceException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

/**
 * 物品关联配置Controller
 * @author yuxixuan
 * @version 2015-09-11
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoRelevance")
public class StoRelevanceController extends BaseController {

	@Autowired
	private StoRelevanceService stoRelevanceService;
	
	/**
	 * 根据组ID查询关联信息
	 * 
	 * @param groupId
	 * @return
	 */
	@ModelAttribute
	public StoRelevance get(@RequestParam(required = false) String groupId) {
		StoRelevance entity = null;
		if (StringUtils.isNotBlank(groupId)) {
			entity = stoRelevanceService.getRelevance(groupId);
		}
		if (entity == null){
			entity = new StoRelevance();
		}
		return entity;
	}
	
	/**
	 * 列表
	 * 
	 * @param stoRelevance
	 * @param isSearch
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:stoRelevance:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoRelevance stoRelevance, @RequestParam(required = false) boolean isSearch,
			HttpServletRequest request, HttpServletResponse response, Model model) {

		Page<StoRelevance> page = new Page<StoRelevance>(request, response);
		// 点击查询
//		if (isSearch) {
			page = stoRelevanceService.findPageRelevance(new Page<StoRelevance>(request, response), stoRelevance);
			model.addAttribute("page", page);
//		}
		return "modules/store/v01/stoRelevance/stoRelevanceList";
	}

	/**
	 * 查看
	 * 
	 * @param stoRelevance
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:stoRelevance:view")
	@RequestMapping(value = "form")
	public String form(StoRelevance stoRelevance, Model model) {
		model.addAttribute("stoRelevance", stoRelevance);
		return "modules/store/v01/stoRelevance/stoRelevanceForm";
	}

	/**
	 * 保存
	 * 
	 * @param stoRelevance
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("store:stoRelevance:edit")
	@RequestMapping(value = "save")
	public String save(StoRelevance stoRelevance, Model model, RedirectAttributes redirectAttributes) {
		// 实体验证
		if (!beanValidator(model, stoRelevance)){
			return form(stoRelevance, model);
		}
		
		Locale locale = LocaleContextHolder.getLocale();
		
		// 验证面值长度
		for (String valid : stoRelevance.getDenomiList()) {
			if (valid.length() != Integer.valueOf(Global.getConfig("denomination.size"))) {
				addMessage(model, msg.getMessage("message.E1068", null, locale));
				return form(stoRelevance, model);
			}		
		}
		
		// 保存
		String message = "";
		try {
			stoRelevanceService.saveRelevance(stoRelevance);
			message = msg.getMessage("message.I1012", null, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/store/v01/stoRelevance/list?isSearch=true&repage";
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			this.addMessage(model, message);
			return form(stoRelevance, model);
		}
	}
	
	/**
	 * 删除
	 * 
	 * @param stoRelevance
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("store:stoRelevance:edit")
	@RequestMapping(value = "delete")
	public String delete(StoRelevance stoRelevance, RedirectAttributes redirectAttributes) {

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			stoRelevanceService.deleteRelevance(stoRelevance);
			message = msg.getMessage("message.I1013", null, locale);
		} catch (ServiceException se) {
			message = msg.getMessage("message.E1031", null, locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoRelevance/list?isSearch=true&repage";
	}

	/**
	 * 获取物品关联币种
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getReleCurrencyList")
	@ResponseBody
	public String getReleCurrencyList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getReleCurrencyList(stoRelevance);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取物品关联类别
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getReleClassificationList")
	@ResponseBody
	public String getReleClassificationList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getReleClassificationList(stoRelevance);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取物品关联套别
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getReleSetsList")
	@ResponseBody
	public String getReleSetsList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getReleSetsList(stoRelevance);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取物品关联材质
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getReleCashList")
	@ResponseBody
	public String getReleCashList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getReleCashList(stoRelevance);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取物品关联面值
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getReleDenominationList")
	@ResponseBody
	public String getReleDenominationList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getReleDenominationList(stoRelevance);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取物品关联单位
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getReleUnitList")
	@ResponseBody
	public String getReleUnitList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getReleUnitList(stoRelevance);
		return new Gson().toJson(stoDictList);
	}
	
	/**
	 * 按照物品面值获取物品关联单位
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getReleUnitListByDem")
	@ResponseBody
	public String getReleUnitListByDem(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		
		List<StoDict> stoDictList = stoRelevanceService.getReleUnitList(stoRelevance);
		List<StoDict> rtnDictList = Lists.newArrayList();
		for (StoDict stoDict : stoDictList) {
			rtnDictList.add(stoDict);
		}
		
		// 纸币
		if ("1".equals(stoRelevance.getCash())) {
			for (StoDict stoDict : stoDictList) {
				// 5角
				if ("08".equals(stoRelevance.getDenomination()) && "110,116,117".contains(stoDict.getValue()) == true) {
					rtnDictList.remove(stoDict);
				} else if ("09".equals(stoRelevance.getDenomination()) && "110,115,117".contains(stoDict.getValue()) == true) {
					// 2角
					rtnDictList.remove(stoDict);
				} else if ("10".equals(stoRelevance.getDenomination()) && "110,115,116".contains(stoDict.getValue()) == true) {
					// 1角
					rtnDictList.remove(stoDict);
				} else if (!"08".equals(stoRelevance.getDenomination()) 
						&& !"09".equals(stoRelevance.getDenomination())
						&& !"10".equals(stoRelevance.getDenomination())
						&& "115,116,117".contains(stoDict.getValue()) == true) {
					rtnDictList.remove(stoDict);
				}
			}
		}
		
		return new Gson().toJson(rtnDictList);
	}

	/**
	 * 根据币种和材质，获取面值选项
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getDenOptions")
	@ResponseBody
	public String getDenOptions(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getDenOptions(stoRelevance);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 根据材质，获取单位选项
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getUnitOptions")
	@ResponseBody
	public String getUnitOptions(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		List<StoDict> stoDictList = stoRelevanceService.getUnitOptions(stoRelevance);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取字典表重空分类
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getBlankBillKindList")
	@ResponseBody
	public String getBlankBillKindList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoBlankBillSelect stoBlankBillSelect = gson.fromJson(param, StoBlankBillSelect.class);
		List<StoDict> stoDictList = stoRelevanceService.getBlankBillKindList(stoBlankBillSelect);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取字典表重空类型
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getBlankBillTypeList")
	@ResponseBody
	public String getBlankBillTypeList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoBlankBillSelect stoBlankBillSelect = gson.fromJson(param, StoBlankBillSelect.class);
		List<StoDict> stoDictList = stoRelevanceService.getBlankBillTypeList(stoBlankBillSelect);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取物品表重空分类
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getBlbiKindList")
	@ResponseBody
	public String getBlbiKindList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoBlankBillSelect stoBlankBillSelect = gson.fromJson(param, StoBlankBillSelect.class);
		List<StoDict> stoDictList = stoRelevanceService.getBlbiKindList(stoBlankBillSelect);
		return new Gson().toJson(stoDictList);
	}

	/**
	 * 获取物品表重空类型
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "getBlbiTypeList")
	@ResponseBody
	public String getBlbiTypeList(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoBlankBillSelect stoBlankBillSelect = gson.fromJson(param, StoBlankBillSelect.class);
		List<StoDict> stoDictList = stoRelevanceService.getBlbiTypeList(stoBlankBillSelect);
		return new Gson().toJson(stoDictList);
	}
	
	/**
	 * 返回上一级页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/store/v01/stoRelevance/list?repage";
	}
}
