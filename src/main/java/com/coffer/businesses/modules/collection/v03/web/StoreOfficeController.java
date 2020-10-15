package com.coffer.businesses.modules.collection.v03.web;

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

import com.coffer.businesses.modules.collection.v03.entity.StoreOffice;
import com.coffer.businesses.modules.collection.v03.service.StoreOfficeService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 商户管理Controller
 * 
 * @author wanglin
 * @version 2015-5-13
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/storeOffice")
public class StoreOfficeController extends BaseController {

	@Autowired
	private StoreOfficeService storeOfficeService;

	@ModelAttribute("storeOffice")
	public StoreOffice get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return storeOfficeService.get(id);
		} else {
			return new StoreOffice();
		}
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param StoreOffice
	 * @return
	 */
	@RequiresPermissions("collection:storeOffice:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoreOffice storeOffice, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoreOffice> page = storeOfficeService.findList(new Page<StoreOffice>(request, response), storeOffice);
        model.addAttribute("page", page);
		return "modules/collection/v03/storeOffice/storeOfficeList";
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          登记修改页面
	 * @param StoreOffice
	 * @return
	 */
	@RequiresPermissions("collection:storeOffice:view")
	@RequestMapping(value = "form")
	public String form(StoreOffice storeOffice, Model model) {
		model.addAttribute("storeOffice", storeOffice);
		return "modules/collection/v03/storeOffice/storeOfficeForm";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          查看页面
	 * @param StoreOffice
	 * @return
	 */
	@RequiresPermissions("collection:storeOffice:view")
	@RequestMapping(value = "view")
	public String view(StoreOffice storeOffice, Model model) {
		model.addAttribute("storeOffice", storeOffice);
		return "modules/collection/v03/storeOffice/storeOfficeView";
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          保存记录
	 * @param StoreOffice
	 * @return
	 */
	@RequiresPermissions("collection:storeOffice:edit")
	@RequestMapping(value = "save")
	public String save(StoreOffice storeOffice, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		if (!beanValidator(model, storeOffice)) {
			return form(storeOffice, model);
		}
		
		String message = "";
		try {
			//商户保存
			storeOfficeService.save(storeOffice);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(storeOffice, model);
		}
		
		// 商户保存成功
		message = msg.getMessage("message.I7234", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/collection/v03/storeOffice/list";
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          删除记录
	 * @param StoreOffice
	 * @return
	 */
	@RequiresPermissions("collection:storeOffice:edit")
	@RequestMapping(value = "delete")
	public String delete(StoreOffice storeOffice, RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		storeOfficeService.delete(storeOffice);
		//删除商户成功
		message = msg.getMessage("message.I7235", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/collection/v03/storeOffice/list";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          商户编码验证
	 * @param code
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkCode")
	public String checkStoreOfficeCode(String code, String oldCode) {
		return storeOfficeService.checkStoreOfficeCode(code, oldCode);
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
		return "redirect:" + adminPath + "/collection/v03/storeOffice/list?repage";
	}

	
	/**
	 * 
	 * Title: deleteCheck
	 * <p>Description: 查看要删除的商户下是否有有效用户</p>
	 * @author:     wanghan
	 * @param response
	 * @param id
	 * @return 
	 * @throws JsonProcessingException 
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "deleteCheck")
	public String deleteCheck(HttpServletResponse response, String id) throws JsonProcessingException {

		int userNum = storeOfficeService.getVaildCntByOfficeId(id);

		if (userNum == 0) {
			return renderString(response, "false");
		}
		return renderString(response, "true");

	}
	
	
	
}
