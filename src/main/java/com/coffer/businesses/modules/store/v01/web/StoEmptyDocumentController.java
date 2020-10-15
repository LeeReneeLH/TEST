/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.store.v01.entity.StoEmptyDocument;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.service.StoEmptyDocumentService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;

/**
 * 重空管理Controller
 * 
 * @author LLF
 * @version 2015-12-11
 */
@Controller
@SessionAttributes({ "emptyDocumentList" })
@RequestMapping(value = "${adminPath}/store/v01/stoEmptyDocument")
public class StoEmptyDocumentController extends BaseController {

	@Autowired
	private StoEmptyDocumentService stoEmptyDocumentService;

	@ModelAttribute
	public StoEmptyDocument get(@RequestParam(required = false) String id) {
		StoEmptyDocument entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = stoEmptyDocumentService.get(id);
		}
		if (entity == null) {
			entity = new StoEmptyDocument();
		}
		return entity;
	}

	@RequiresPermissions("store:stoEmptyDocument:view")
	@RequestMapping(value = { "list", "" })
	public String list(StoEmptyDocument stoEmptyDocument, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<StoEmptyDocument> page = stoEmptyDocumentService.findPage(new Page<StoEmptyDocument>(request, response),
				stoEmptyDocument);
		StoGoods stoGoods = new StoGoods();
		if(stoEmptyDocument != null) {
			stoGoods.setStoBlankBillSelect(stoEmptyDocument.getStoBlankBillSelect());
		}
		model.addAttribute("stoGoods", stoGoods);
		model.addAttribute("page", page);
		return "modules/store/v01/stoEmptyDocument/stoEmptyDocumentList";
	}

	@RequiresPermissions("store:stoEmptyDocument:edit")
	@RequestMapping(value = "form")
	public String form(StoEmptyDocument stoEmptyDocument, Model model) {
		model.addAttribute("stoEmptyDocument", stoEmptyDocument);
		model.addAttribute("stoGoods", stoEmptyDocument.getStoGoods());
		model.addAttribute("emptyDocumentList", Lists.newArrayList());
		return "modules/store/v01/stoEmptyDocument/stoEmptyDocumentForm";
	}

	@RequiresPermissions("store:stoEmptyDocument:edit")
	@RequestMapping(value = "save")
	public String save(StoEmptyDocument stoEmptyDocument,
			@ModelAttribute("emptyDocumentList") List<StoEmptyDocument> emptyDocumentList, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoEmptyDocument)) {
			return form(stoEmptyDocument, model);
		}
		if (Collections3.isEmpty(emptyDocumentList)) {
			Locale locale = LocaleContextHolder.getLocale();
			addMessage(model, msg.getMessage("message.E1038", new String[] {}, locale));
			return "modules/store/v01/stoEmptyDocument/stoEmptyDocumentForm";
		}
		String message = "";
		try {
			stoEmptyDocumentService.save(emptyDocumentList);
			message = msg.getMessage("message.I1014", null, null);
		} catch (BusinessException e) {
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), null);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEmptyDocument/?repage";
	}

	@RequiresPermissions("store:stoEmptyDocument:edit")
	@RequestMapping(value = "delete")
	public String delete(StoEmptyDocument stoEmptyDocument, RedirectAttributes redirectAttributes) {
		String message = "";
		try {
			stoEmptyDocumentService.delete(stoEmptyDocument);
			message = msg.getMessage("message.I1015", null, null);
			
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), null);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoEmptyDocument/?repage";
	}

	@RequiresPermissions("store:stoEmptyDocument:edit")
	@ResponseBody
	@RequestMapping(value = "addDetail")
	public String addDetail(StoEmptyDocument stoEmptyDocument,
			@ModelAttribute("emptyDocumentList") List<StoEmptyDocument> emptyDocumentList, Model model) {
		// 验证列表中区间不重复
		boolean flag = true;
		long start = stoEmptyDocument.getStartNumber().longValue();
		long end = stoEmptyDocument.getEndNumber().longValue();
		String kind = stoEmptyDocument.getStoBlankBillSelect().getBlankBillKind();
		String type = stoEmptyDocument.getStoBlankBillSelect().getBlankBillType();
		for (StoEmptyDocument stoEmptyDocumentTemp : emptyDocumentList) {
			String kindTemp = stoEmptyDocumentTemp.getStoBlankBillSelect().getBlankBillKind();
			String typeTemp = stoEmptyDocumentTemp.getStoBlankBillSelect().getBlankBillType();
			if (kind.equals(kindTemp) && type.equals(typeTemp)) {
				long startTemp = stoEmptyDocumentTemp.getStartNumber().longValue();
				long endTemp = stoEmptyDocumentTemp.getEndNumber().longValue();
				if ((start >= startTemp && start <= endTemp) || (end >= startTemp && end <= endTemp)
						|| (start <= startTemp && end >= endTemp)) {
					flag = false;
					break;
				}
			}
		}
		if (flag) {
			emptyDocumentList.add(stoEmptyDocument);
		} else {
			String message = msg.getMessage("message.E1039", null, null);
			return addMessageByJson(ERROR_MESSAGE_KEY, message);
		}
		model.addAttribute("emptyDocumentList", emptyDocumentList);

		String message = msg.getMessage("message.I1016", null, null);
		return addMessageByJson(MESSAGE_KEY, message);
	}

	@RequestMapping(value = "getList")
	public String getDetailList(Model model) {
		return "modules/store/v01/stoEmptyDocument/stoEmptyDocumentDetailList";
	}

	@RequiresPermissions("store:stoEmptyDocument:edit")
	@ResponseBody
	@RequestMapping(value = "deleteDetail")
	public String deleteDetail(int index, @ModelAttribute("emptyDocumentList") List<StoEmptyDocument> list, Model model) {
		list.remove(index);

		String message = msg.getMessage("message.I1015", null, null);
		return addMessageByJson(MESSAGE_KEY, message);
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
		return "redirect:" + adminPath + "/store/v01/stoEmptyDocument/list?repage";
	}
}
