package com.coffer.core.modules.sys.web;

import java.util.List;
import java.util.Map;

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

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.ProvinceEntity;
import com.coffer.core.modules.sys.service.ProvinceService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
* Title: ProvinceController 
* <p>Description: 省份表控制层</p>
* @author wanghan
* @date 2017年11月1日 上午9:25:22
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/province")
public class ProvinceController extends BaseController{

	@Autowired
	private ProvinceService proService;
	
	@ModelAttribute
	public ProvinceEntity get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return proService.get(id);
		}else{
			return new ProvinceEntity();
		}
	}
	
	/**
	 * 
	 * Title: list
	 * <p>Description: 省份列表页面</p>
	 * @author:     wanghan
	 * @param ProvinceEntity 省份实体类对象
	 * @param request
	 * @param response
	 * @param model
	 * @return 省份实体类对象集合
	 * String    返回类型
	 */
	@RequiresPermissions("sys:province:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProvinceEntity provinceEntity, HttpServletRequest request, HttpServletResponse response, Model model) {
		
        Page<ProvinceEntity> page = proService.findPage(new Page<ProvinceEntity>(request, response), provinceEntity); 
        model.addAttribute("page", page);
		return "modules/sys/provinceList";
	}
	
	/**
	 * 
	 * Title: form
	 * <p>Description: 省份修改/添加页面</p>
	 * @author:     wanghan
	 * @param ProvinceEntity 省份实体类对象
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:province:view")
	@RequestMapping(value = "form")
	public String form(ProvinceEntity provinceEntity, Model model) {
		model.addAttribute("provinceEntity", provinceEntity);
		return "modules/sys/provinceForm";
	}
	
	/**
	 * 
	 * Title: save
	 * <p>Description: 修改或增加省份记录</p>
	 * @author:     wanghan
	 * @param provinceEntity 省份实体类对象
	 * @param model
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:province:edit")
	@RequestMapping(value = "save") 
	public String save(ProvinceEntity provinceEntity, Model model, RedirectAttributes redirectAttributes) {
		proService.save(provinceEntity);
		addMessage(redirectAttributes, "保存省份'" + provinceEntity.getProName() + "'成功");
		return "redirect:" + adminPath + "/sys/province/";
	}
	
	/**
	 * 
	 * Title: delete
	 * <p>Description: 删除省份记录</p>
	 * @author:     wanghan
	 * @param provinceEntity 省份实体类对象
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:province:view")
	@RequestMapping(value = "delete")
	public String delete(ProvinceEntity provinceEntity, RedirectAttributes redirectAttributes) {
		proService.deleteAssociatePro(provinceEntity);
		addMessage(redirectAttributes, "删除省份成功");
		return "redirect:" + adminPath + "/sys/province/";
	}
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 恢复无效省份记录为有效</p>
	 * @author:     wanghan
	 * @param provinceEntity 省份实体类对象
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:province:view")
	@RequestMapping(value = "revert")
	public String revert(ProvinceEntity provinceEntity, RedirectAttributes redirectAttributes) {
		proService.revert(provinceEntity);
		addMessage(redirectAttributes, "恢复省份成功");
		return "redirect:" + adminPath + "/sys/province/";
	}
	
	/**
	 * 
	 * Title: back
	 * <p>Description: 返回省份列表页面</p>
	 * @author:     wanghan
	 * @param response
	 * @param request
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/sys/province/list?repage";
	}
	
	/**
	 * 
	 * Title: getSelect2ProDate
	 * <p>Description: 获取省份下拉菜单数据（有效），用provinceCode接收</p>
	 * @author:     wanghan
	 * @param proEntity 省份实体类对象
	 * @return 返回省份实体类对象集合的json字符串
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "getSelect2ProData")
	public String getSelect2ProData(ProvinceEntity proEntity) {

		List<ProvinceEntity> list = proService.findSelect2ProList(proEntity);
		List<Map<String, String>> proList = Lists.newArrayList();
		Map<String, String> content = null;
		for (ProvinceEntity pro : list) {
			content = Maps.newHashMap();
			content.put("id", pro.getProvinceCode());
			content.put("text", pro.getProName());
			proList.add(content);
		}
		return gson.toJson(proList);
	}
	
	/**
	 * 
	 * Title: getSelect2ProDateName
	 * <p>Description: 获取省份下拉菜单数据，用proName接收</p>
	 * @author:     wanghan
	 * @param proEntity 省份实体类对象
	 * @return  返回省份实体类对象集合的json字符串
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "getSelect2ProDataName")
	public String getSelect2ProDateName(ProvinceEntity proEntity) {

		List<ProvinceEntity> list = proService.findSelect2ProList(proEntity);
		List<Map<String, String>> proList = Lists.newArrayList();
		Map<String, String> content = null;
		for (ProvinceEntity pro : list) {
			content = Maps.newHashMap();
			content.put("id", pro.getProName());
			content.put("text", pro.getProName());
			proList.add(content);
		}
		return gson.toJson(proList);
	}
	
	/**
	 * 
	 * Title: checkProCode
	 * <p>Description: 校验省份编码，不能重复</p>
	 * @author:     wanghan
	 * @param proEntity 省份实体类对象
	 * @return  
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "checkProCode")
	public String checkProCode(ProvinceEntity proEntity,RedirectAttributes redirectAttributes) {
		int proNum;
		//如果省份编码为空返回true
		if (StringUtils.isBlank(proEntity.getProvinceCode())) {
			proNum = 0;
		} else {
			proNum = proService.findProNum(proEntity);
		}

		if (proNum == 0) {
			return "true";
		} else {
			return "false";
		}
	}
	
}
