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
import com.coffer.core.modules.sys.entity.CountyEntity;
import com.coffer.core.modules.sys.service.CityService;
import com.coffer.core.modules.sys.service.CountyService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
/**
 * 
* Title: CountyController 
* <p>Description: 县级表控制层</p>
* @author wanghan
* @date 2017年11月2日 上午10:25:22
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/county")
public class CountyController extends BaseController{

	@Autowired
	private CountyService countyService;
	@Autowired
	private CityService CityService;
	
	@ModelAttribute
	public CountyEntity get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return countyService.get(id);
		} else {
			return new CountyEntity();
		}
	}
	
	/**
	 * 
	 * Title: list
	 * <p>Description: 县/区列表页面</p>
	 * @author:     wanghan 
	 * @param countyEntity 县/区表实体类对象
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:county:view")
	@RequestMapping(value = {"list", ""})
	public String list(CountyEntity countyEntity, HttpServletRequest request, HttpServletResponse response, Model model) {
		
        Page<CountyEntity> page = countyService.findPage(new Page<CountyEntity>(request, response), countyEntity); 
        model.addAttribute("page", page);
		return "modules/sys/countyList";
	}
	
	/**
	 * 
	 * Title: form
	 * <p>Description: 县/区修改/添加页面</p>
	 * @author:     wanghan
	 * @param countyEntity 县/区表实体类对象
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:county:view")
	@RequestMapping(value = "form")
	public String form(CountyEntity countyEntity, Model model) {
		model.addAttribute("countyEntity", countyEntity);
		return "modules/sys/countyForm";
	}
	
	/**
	 * 
	 * Title: save
	 * <p>Description: 保存/修改县/区列表记录</p>
	 * @author:     wanghan
	 * @param countyEntity 县/区表实体类对象
	 * @param model
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:county:edit")
	@RequestMapping(value = "save") 
	public String save(CountyEntity countyEntity, Model model, RedirectAttributes redirectAttributes) {
		countyService.save(countyEntity);
		addMessage(redirectAttributes, "保存县/区'" + countyEntity.getCountyName() + "'成功");
		return "redirect:" + adminPath + "/sys/county/";
	}
	
	/**
	 * 
	 * Title: delete
	 * <p>Description: 逻辑删除县/区</p>
	 * @author:     wanghan
	 * @param countyEntity 县/区表实体类对象
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:county:view")
	@RequestMapping(value = "delete")
	public String delete(CountyEntity countyEntity, RedirectAttributes redirectAttributes) {
		countyService.delete(countyEntity);
		addMessage(redirectAttributes, "删除县/区成功");
		return "redirect:" + adminPath + "/sys/county/";
	}
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 恢复无效县/区记录为有效</p>
	 * @author:     wanghan
	 * @param countyEntity 县/区表实体类对象
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:county:view")
	@RequestMapping(value = "revert")
	public String revert(CountyEntity countyEntity, RedirectAttributes redirectAttributes) {
		int cityNum = CityService.findCityCodeNum(countyEntity.getCityCode());
		if(cityNum == 0) {
			addMessage(redirectAttributes, "当前县/区所属城市无效，无法恢复");
			return "redirect:" + adminPath + "/sys/county/?repage";
		}
		countyService.revert(countyEntity);
		addMessage(redirectAttributes, "恢复县/区成功");
		return "redirect:" + adminPath + "/sys/county/?repage";
	}
	
	/**
	 * 
	 * Title: back
	 * <p>Description: 返回到县/区列表页面</p>
	 * @author:     wanghan
	 * @param response
	 * @param request
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/sys/county/list?repage";
	}
	
	/**
	 * 
	 * Title: getSelect2CountyData
	 * <p>Description: 获取县级下拉菜单有效数据,用countyCode接收/返回参数</p>
	 * @author:     wanghan
	 * @param countyEntity 县级表实体类对象
	 * @return 返回县级表实体类对象集合
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "getSelect2CountyData")
	public String getSelect2CountyData(CountyEntity countyEntity) {

		List<CountyEntity> list = countyService.findSelect2CountyData(countyEntity);
		List<Map<String, String>> countyList = Lists.newArrayList();
		Map<String, String> content;
		for (CountyEntity county : list) {
			content = Maps.newHashMap();
			content.put("id", county.getCountyCode());
			content.put("text", county.getCountyName());
			countyList.add(content);
		}
		return gson.toJson(countyList);
	}

	/**
	 * 
	 * Title: getSelect2CountyDataName
	 * <p>Description: 获取县级下拉菜单数据,用countyName接收/返回参数</p>
	 * @author:     wanghan
	 * @param countyEntity 县级表实体类对象
	 * @return 返回县级表实体类对象集合
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "getSelect2CountyDataName")
	public String getSelect2CountyDataName(CountyEntity countyEntity) {

		List<CountyEntity> list = countyService.findSelect2CountyData(countyEntity);
		List<Map<String, String>> countyList = Lists.newArrayList();
		Map<String, String> content;
		for (CountyEntity county : list) {
			content = Maps.newHashMap();
			content.put("id", county.getCountyName());
			content.put("text", county.getCountyName());
			countyList.add(content);
		}
		return gson.toJson(countyList);
	}
	
	/**
	 * 
	 * Title: checkCountyCode
	 * <p>Description: 校验县/区编码的唯一性</p>
	 * @author:     wanghan
	 * @param countyCode 县/区编码
	 * @return 
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "checkCountyCode")
	public String checkCountyCode(String countyCode) {
		int countyCodeNum;
		//如果省份编码为空返回true
		if (StringUtils.isBlank(countyCode)) {
			countyCodeNum = 0;
		} else {
			countyCodeNum = countyService.findCountyCodeNum(countyCode);
		}

		if (countyCodeNum == 0) {
			return "true";
		} else {
			return "false";
		}
	}
}
