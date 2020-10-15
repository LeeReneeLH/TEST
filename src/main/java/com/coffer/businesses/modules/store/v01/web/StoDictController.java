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

import com.coffer.businesses.modules.store.StoreConstant.GoodDictType;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.service.StoDictService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 字典Controller
 * @author Ray
 * @version 2015-09-08
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/dict")
public class StoDictController extends BaseController {

	@Autowired
	private StoDictService stoDictService;
	
	/**
	 * 根据ID取得物品字典数据
	 * 
	 * @param id
	 * @return
	 */
	@ModelAttribute
	public StoDict get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return stoDictService.get(id);
		}else{
			return new StoDict();
		}
	}
	
	/**
	 * 取得物品字典列表
	 * 
	 * @param stoDict
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sto:dict:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoDict stoDict, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<StoDict> typeList = stoDictService.findTypeList();
		model.addAttribute("typeList", typeList);
        Page<StoDict> page = stoDictService.findPage(new Page<StoDict>(request, response), stoDict); 
        model.addAttribute("page", page);
		return "modules/store/v01/stoDict/dictList";
	}

	/**
	 * 跳转到物品字典明细画面
	 * 
	 * @param stoDict
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sto:dict:view")
	@RequestMapping(value = "form")
	public String form(StoDict stoDict, Model model) {
		model.addAttribute("stoDict", stoDict);
		return "modules/store/v01/stoDict/dictForm";
	}

	/**
	 * 保存物品字典数据
	 * 
	 * @param stodict
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sto:dict:edit")
	@RequestMapping(value = "save")//@Valid 
	public String save(StoDict stodict, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stodict)){
			return form(stodict, model);
		}
		// 保存
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		try {
			stoDictService.save(stodict);
			message = msg.getMessage("message.I1011", new String[] { stodict.getLabel() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/store/v01/dict/?repage&type=" + stodict.getType();
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			this.addMessage(model, message);
			return form(stodict, model);
		}
	}
	
	/**
	 * 删除物品字典数据
	 * 
	 * @param stoDict
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sto:dict:edit")
	@RequestMapping(value = "delete")
	public String delete(StoDict stoDict, RedirectAttributes redirectAttributes) {
		stoDictService.delete(stoDict);
		addMessage(redirectAttributes, "删除字典成功");
		return "redirect:" + adminPath + "/store/v01/dict/?repage&type="+stoDict.getType();
	}
	
	/**
	 * 恢复物品字典数据
	 * 
	 * @param stoDict
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sto:dict:edit")
	@RequestMapping(value = "revert")
	public String revert(StoDict stoDict, RedirectAttributes redirectAttributes) {
		stoDictService.revert(stoDict);
		addMessage(redirectAttributes, "恢复字典成功");
		return "redirect:" + adminPath + "/store/v01/dict/?repage&type=" + stoDict.getType();
	}

	/**
	 * listData
	 * 
	 * @param type
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "listData")
	public List<StoDict> listData(@RequestParam(required=false) String type) {
		StoDict stoDict = new StoDict();
		stoDict.setType(type);
		return stoDictService.findList(stoDict);
	}

	/**
	 * “添加键值”的操作
	 * 
	 * @param stodict
	 * @param model
	 * @return
	 */
	@RequiresPermissions("sto:dict:edit")
	@RequestMapping(value = "copy")
	public String copy(StoDict stodict, Model model) {
		StoDict dictCopy = new StoDict();
		dictCopy.setType(stodict.getType());
		dictCopy.setSort(stodict.getSort() + 10);
		dictCopy.setDescription(stodict.getDescription());
		// 给“单位”类型数据添加字典数据时，自动带入“关联代码”
		if (GoodDictType.UNIT.equals(stodict.getType())) {
			dictCopy.setRefCode(stodict.getRefCode());
		}
		model.addAttribute("stoDict", dictCopy);
		return "modules/store/v01/stoDict/dictForm";
	}

	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/store/v01/dict/list?repage";
	}
}
