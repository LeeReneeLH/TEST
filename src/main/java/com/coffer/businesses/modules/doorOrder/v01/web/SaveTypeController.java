package com.coffer.businesses.modules.doorOrder.v01.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.businesses.modules.doorOrder.v01.service.SaveTypeService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 款项类型Controller
 * 
 * @author zhaohaoran
 * @date 2019-07-23
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/saveType")
public class SaveTypeController extends BaseController {
	@Autowired
	private SaveTypeService saveTypeService;

	/**
	 * @ModelAttribute认证信息
	 * 
	 * @author zhaohaoran
	 * @date 2019-07-23
	 */
	@ModelAttribute
	public SaveType get(@RequestParam(required = false) String id) {
		SaveType saveType = null;
		if (StringUtils.isNotBlank(id)) {
			saveType = saveTypeService.get(id);
		}
		if (saveType == null) {
			saveType = new SaveType();
		}
		return saveType;
	}

	/**
	 * 所有分页列表
	 * 
	 * @author zhaohaoran
	 * @date 2019-07-23
	 */
	@RequiresPermissions("doorOrder:saveType:view")
	@RequestMapping(value = { "list", "" })
	public String list(SaveType saveType, @RequestParam(required = false) boolean isSearch, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<SaveType> page = saveTypeService.findPage(new Page<SaveType>(request, response), saveType);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/saveType/saveTypeList";
	}

	/**
	 * 跳转添加页面
	 * 
	 * @author zhaohaoran
	 * @date 2019-07-23
	 */
	@RequiresPermissions("doorOrder:saveType:view")
	@RequestMapping(value = "form")
	public String form(SaveType saveType, Model model, String pageFlag) {
		model.addAttribute("saveType", saveType);
		model.addAttribute("pageFlag", pageFlag);
		return "modules/doorOrder/v01/saveType/saveTypeForm";
	}

	/**
	 * 物品添加和修改页面
	 * 
	 * @author zhaohaoran
	 * @date 2019-07-23
	 */
	@RequiresPermissions("doorOrder:saveType:edit")
	@RequestMapping(value = "save")
	public String save(SaveType saveType, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, saveType)) {
			return form(saveType, model, null);
		}
		if (StringUtils.isBlank(saveType.getId())) {
			SaveType st = new SaveType();
			st.setMerchantId(saveType.getMerchantId());
			st.setTypeCode(saveType.getTypeCode());
			if (!Collections3.isEmpty(saveTypeService.findList(st))) {
				addMessage(model, "[保存失败]:存款类型【" + saveType.getTypeCode() + "】已存在!");
				return form(saveType, model, null);
			}
		}
		saveTypeService.save(saveType);
		addMessage(redirectAttributes, "保存成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/saveType/?repage";
	}

	/**
	 * 物品删除
	 * 
	 * @author zhaohaoran
	 * @date 2019-07-23
	 */
	@RequiresPermissions("doorOrder:saveType:edit")
	@RequestMapping(value = "delete")
	public String delete(SaveType saveType, RedirectAttributes redirectAttributes) {
		try {
			saveTypeService.delete(saveType);
			addMessage(redirectAttributes, "删除成功");
		} catch (BusinessException be) {
			addMessage(redirectAttributes, "删除失败!");
		}
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/saveType/?repage";
	}

	/**
	 * 返回
	 * 
	 * @author zhaohaoran
	 * @date 2019-07-23
	 */
	@RequestMapping(value = "back")
	public String back(SaveType saveType, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/saveType/list?isSearch=true&repage";
	}

	/**
	 * 存款类型重复性验证(修改非本身)
	 * 
	 * @author ZXK
	 * @version 2019年8月19日
	 * @param saveType
	 * @return
	 */
	@RequestMapping(value = "checkSaveType")
	@ResponseBody
	public String checkSaveType(SaveType saveType) {
		if (saveType == null) {
			return "true";
		} else {
			SaveType st = new SaveType();
			st.setMerchantId(saveType.getMerchantId());
			st.setTypeCode(saveType.getTypeCode());
			List<SaveType> list = saveTypeService.findList(st);
			// 库里没有此名称
			if (Collections3.isEmpty(list)) {
				return "true";
			}
			SaveType temp = list.get(0);
			if (StringUtils.isNotBlank(saveType.getId()) && !saveType.getId().equals(temp.getId())) {
				// 修改 不是自身类型
				return "false";
			}
		}
		return "true";
	}
}
