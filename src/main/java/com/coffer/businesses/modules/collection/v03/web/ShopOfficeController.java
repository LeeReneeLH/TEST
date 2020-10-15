package com.coffer.businesses.modules.collection.v03.web;

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

import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.businesses.modules.collection.v03.entity.ShopOffice;
import com.coffer.businesses.modules.collection.v03.entity.StoreOffice;
import com.coffer.businesses.modules.collection.v03.service.ShopOfficeService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 门店管理Controller
 * 
 * @author wanglin
 * @version 2017-11-13
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/shopOffice")
public class ShopOfficeController extends BaseController {

	@Autowired
	private ShopOfficeService shopOfficeService;

	@ModelAttribute("shopOffice")
	public ShopOffice get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return shopOfficeService.get(id);
		} else {
			return new ShopOffice();
		}
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param ShopOffice
	 * @return
	 */
	@RequiresPermissions("collection:shopOffice:view")
	@RequestMapping(value = {"list", ""})
	public String list(ShopOffice shopOffice, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ShopOffice> page = shopOfficeService.findList(new Page<ShopOffice>(request, response), shopOffice);
        model.addAttribute("page", page);
        
		//商户下拉
        StoreOffice storeOffice = new StoreOffice();
        List<SelectItem> storeList = shopOfficeService.findStoreSelect(storeOffice);
		model.addAttribute("storeList", storeList);
        
		return "modules/collection/v03/shopOffice/shopOfficeList";
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          登记修改页面
	 * @param ShopOffice
	 * @return
	 */
	@RequiresPermissions("collection:shopOffice:view")
	@RequestMapping(value = "form")
	public String form(ShopOffice shopOffice, Model model) {
		model.addAttribute("shopOffice", shopOffice);
		
		//商户下拉
		StoreOffice storeOffice = new StoreOffice();
		storeOffice.setEnabledFlag(CollectionConstant.enabledFlagType.OK);
		//修改的场合
		if (StringUtils.isNotBlank(shopOffice.getId())) {
			storeOffice.setId(shopOffice.getStoreId());
		}
		
        List<SelectItem> storeList = shopOfficeService.findStoreSelect(storeOffice);
		model.addAttribute("storeList", storeList);
		
		return "modules/collection/v03/shopOffice/shopOfficeForm";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          查看页面
	 * @param ShopOffice
	 * @return
	 */
	@RequiresPermissions("collection:shopOffice:view")
	@RequestMapping(value = "view")
	public String view(ShopOffice shopOffice, Model model) {
		model.addAttribute("shopOffice", shopOffice);
		return "modules/collection/v03/shopOffice/shopOfficeView";
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          保存记录
	 * @param ShopOffice
	 * @return
	 */
	@RequiresPermissions("collection:shopOffice:edit")
	@RequestMapping(value = "save")
	public String save(ShopOffice shopOffice, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		if (!beanValidator(model, shopOffice)) {
			return form(shopOffice, model);
		}
		String message = "";
		try {
			//门店保存
			shopOfficeService.save(shopOffice);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(shopOffice, model);
		}
		// 门店保存成功
		message = msg.getMessage("message.I7229", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/collection/v03/shopOffice/list";
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          删除记录
	 * @param ShopOffice
	 * @return
	 */
	@RequiresPermissions("collection:shopOffice:edit")
	@RequestMapping(value = "delete")
	public String delete(ShopOffice shopOffice, RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		shopOfficeService.delete(shopOffice);
		//删除门店成功
		message = msg.getMessage("message.I7230", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/collection/v03/shopOffice/list";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          门店编码验证
	 * @param code
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkCode")
	public String checkShopOfficeCode(String code, String oldCode) {
		return shopOfficeService.checkShopOfficeCode(code, oldCode);
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
		return "redirect:" + adminPath + "/collection/v03/shopOffice/list?repage";
	}

	
	/**
	 * 
	 * Title: deleteCheck
	 * <p>Description: 查看要删除的门店下是否有有效用户</p>
	 * @author:     wanglin
	 * @param response
	 * @param id
	 * @return 
	 * @throws JsonProcessingException 
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "deleteCheck")
	public String deleteCheck(HttpServletResponse response, String id) throws JsonProcessingException {

		int userNum = shopOfficeService.getVaildCntByOfficeId(id);

		if (userNum == 0) {
			return renderString(response, "false");
		}
		return renderString(response, "true");

	}
	
	
}
