package com.coffer.businesses.modules.store.v01.web;

import java.math.BigDecimal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoRelevance;
import com.coffer.businesses.modules.store.v01.service.StoGoodsService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.gson.Gson;

/**
 * 物品管理Controller
 * @author Ray
 * @version 2015-09-10
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/goods/stoGoods")
public class StoGoodsController extends BaseController {

	@Autowired
	private StoGoodsService stoGoodsService;
	
	/** 国际化资源管理器 */
	@Autowired
	protected MessageSource msg;
	
	/**
	 * 取得物品明细
	 * 
	 * @param id
	 * @return
	 */
	@ModelAttribute
	public StoGoods get(@RequestParam(required=false) String id) {
		StoGoods entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoGoodsService.get(id);
		}
		if (entity == null) {
			// 数据不存在的场合
			entity = new StoGoods();
		}
		return entity;
	}
	
	/**
	 * 查询物品列表
	 * 
	 * @param stoGoods
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:goods:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoGoods stoGoods, HttpServletRequest request, HttpServletResponse response, Model model) {
		// if(StringUtils.isEmpty(stoGoods.getGoodsType())){
		// stoGoods.setGoodsType(StoreConstant.GoodType.CURRENCY);
		// }
		Page<StoGoods> page = stoGoodsService.findPage(new Page<StoGoods>(request, response), stoGoods); 
		model.addAttribute("page", page);
		return "modules/store/v01/stoGoods/stoGoodsList";
	}

	/**
	 * 跳转到物品明细画面
	 * 
	 * @param stoGoods
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:goods:view")
	@RequestMapping(value = "form")
	public String form(StoGoods stoGoods, Model model, @RequestParam String goodType) {
		model.addAttribute("stoGoods", stoGoods);
		String url = "";
		if (StoreConstant.GoodType.CURRENCY.equals(stoGoods.getGoodsType())
				|| StoreConstant.GoodType.CURRENCY.equals(goodType)) {
			url = "modules/store/v01/stoGoods/stoGoodsForm";
		} else if (StoreConstant.GoodType.BLANK_BILL.equals(stoGoods.getGoodsType())
				|| StoreConstant.GoodType.BLANK_BILL.equals(goodType)) {
			url = "modules/store/v01/stoGoods/stoBlankBillForm";
		}
		return url;
	}

	/**
	 * 保存物品
	 * 
	 * @param stoGoods
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("store:goods:edit")
	@RequestMapping(value = "save")
	public String save(StoGoods stoGoods, Model model, RedirectAttributes redirectAttributes) {
		// 实体验证
		if (!beanValidator(model, stoGoods)){
			return form(stoGoods, model, "");
		}
		// 添加重空时的实体验证
		if (StoreConstant.GoodType.BLANK_BILL.equals(stoGoods.getGoodsType())
				&& !beanValidator(model, stoGoods.getStoBlankBillSelect())) {
			return form(stoGoods, model, "");
		}
		// 保存
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		try {
			stoGoodsService.saveGoods(stoGoods);
			message = msg.getMessage("message.I0005", null, locale);
			// 操作成功
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/store/v01/goods/stoGoods/?repage";
		} catch (BusinessException be) {
			stoGoods.setId(null);
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			this.addMessage(model, message);
			return form(stoGoods, model, "");
		}
	}

	/**
	 * 删除物品
	 * 
	 * @param stoGoods
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("store:goods:edit")
	@RequestMapping(value = "delete")
	public String delete(StoGoods stoGoods, Model model, RedirectAttributes redirectAttributes) {
		// 保存
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		try {
			stoGoodsService.delete(stoGoods);
			message = msg.getMessage("message.I0005", null, locale);
			// 操作成功
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/store/v01/goods/stoGoods/?repage";
		} catch (BusinessException be) {
			stoGoods.setId(null);
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			this.addMessage(model, message);
			return form(stoGoods, model, "");
		}
	}

	/**
	 * 计算物品价值
	 * 
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "calcGoodsVal")
	@ResponseBody
	public String calcGoodsVal(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "\"");
		Gson gson = new Gson();
		StoRelevance stoRelevance = gson.fromJson(param, StoRelevance.class);
		BigDecimal goodsVal = stoGoodsService.calcGoodsVal(stoRelevance);
		return new Gson().toJson(goodsVal);
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
		return "redirect:" + adminPath + "/store/v01/goods/stoGoods/list?repage";
	}
}
