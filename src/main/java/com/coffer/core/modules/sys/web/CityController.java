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
import com.coffer.core.modules.sys.entity.CityEntity;
import com.coffer.core.modules.sys.entity.ProvinceEntity;
import com.coffer.core.modules.sys.service.CityService;
import com.coffer.core.modules.sys.service.ProvinceService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
* Title: CityController 
* <p>Description: 城市表控制层</p>
* @author wanghan
* @date 2017年11月1日 上午9:26:16
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/city")
public class CityController extends BaseController{

	@Autowired
	private ProvinceService proService;
	@Autowired
	private CityService cityService;
	
	@ModelAttribute
	public CityEntity get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return cityService.get(id);
		} else {
			return new CityEntity();
		}
	}
	
	/**
	 * 
	 * Title: list
	 * <p>Description: 城市列表页面</p>
	 * @author:     wanghan 
	 * @param cityEntity 城市表实体类对象
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:city:view")
	@RequestMapping(value = {"list", ""})
	public String list(CityEntity cityEntity, HttpServletRequest request, HttpServletResponse response, Model model) {
		
        Page<CityEntity> page = cityService.findPage(new Page<CityEntity>(request, response), cityEntity); 
        model.addAttribute("page", page);
		return "modules/sys/cityList";
	}
	
	/**
	 * 
	 * Title: form
	 * <p>Description: 城市修改/添加页面</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:city:view")
	@RequestMapping(value = "form")
	public String form(CityEntity cityEntity, Model model) {
		model.addAttribute("cityEntity", cityEntity);
		return "modules/sys/cityForm";
	}
	
	/**
	 * 
	 * Title: save
	 * <p>Description: 保存/修改城市列表记录</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * @param model
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:city:edit")
	@RequestMapping(value = "save") 
	public String save(CityEntity cityEntity, Model model, RedirectAttributes redirectAttributes) {
		cityService.save(cityEntity);
		addMessage(redirectAttributes, "保存城市'" + cityEntity.getCityName() + "'成功");
		return "redirect:" + adminPath + "/sys/city/";
	}
	
	/**
	 * 
	 * Title: delete
	 * <p>Description: 逻辑删除城市</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:city:view")
	@RequestMapping(value = "delete")
	public String delete(CityEntity cityEntity, RedirectAttributes redirectAttributes) {
		cityService.deleteAssociateCity(cityEntity);
		addMessage(redirectAttributes, "删除城市成功");
		return "redirect:" + adminPath + "/sys/city/";
	}
	
	/**
	 * 
	 * Title: revert
	 * <p>Description: 恢复无效城市记录为有效</p>
	 * @author:     wanghan
	 * @param cityEntity 城市表实体类对象
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("sys:city:view")
	@RequestMapping(value = "revert")
	public String revert(CityEntity cityEntity, RedirectAttributes redirectAttributes) {
		ProvinceEntity proEntity = new ProvinceEntity();
		proEntity.setProvinceCode(cityEntity.getProvinceCode());
		int proNum = proService.findProNum(proEntity);
		if(proNum == 0) {
			addMessage(redirectAttributes, "当前城市所属省份无效，无法恢复");
			return "redirect:" + adminPath + "/sys/city/?repage";
		}
		cityService.revert(cityEntity);
		addMessage(redirectAttributes, "恢复城市成功");
		return "redirect:" + adminPath + "/sys/city/?repage";
	}
	
	/**
	 * 
	 * Title: back
	 * <p>Description: 返回到城市列表页面</p>
	 * @author:     wanghan
	 * @param response
	 * @param request
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/sys/city/list?repage";
	}
	
	/**
	 * 
	 * Title: getSelect2CityData
	 * <p>Description: 获取城市下拉菜单数据，用cityCode接收</p>
	 * @author:     wanghan
	 * @param cityEntity 城市实体类对象
	 * @return  返回城市实体类对象集合的json字符串
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "getSelect2CityData")
	public String getSelect2CityData(CityEntity cityEntity) {

		List<CityEntity> list = cityService.findSelect2CityData(cityEntity);
		List<Map<String, String>> cityList = Lists.newArrayList();
		Map<String, String> content;
		for (CityEntity city : list) {
			content = Maps.newHashMap();
			content.put("id", city.getCityCode());
			content.put("text", city.getCityName());
			cityList.add(content);
		}
		return gson.toJson(cityList);
	}
	
	/**
	 * 
	 * Title: getSelect2CityDataName
	 * <p>Description:  获取城市下拉菜单数据，用cityName接收</p>
	 * @author:     wanghan
	 * @param cityEntity 城市实体类对象
	 * @return 返回城市实体类对象集合的json字符串
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "getSelect2CityDataName")
	public String getSelect2CityDataName(CityEntity cityEntity) {

		List<CityEntity> list = cityService.findSelect2CityData(cityEntity);
		List<Map<String, String>> cityList = Lists.newArrayList();
		Map<String, String> content;
		for (CityEntity city : list) {
			content = Maps.newHashMap();
			content.put("id", city.getCityName());
			content.put("text", city.getCityName());
			cityList.add(content);
		}
		return gson.toJson(cityList);
	}
	
	/**
	 * 
	 * Title: checkCityCode
	 * <p>Description: 校验城市编码的唯一性</p>
	 * @author:     wanghan
	 * @param cityCode 城市编码
	 * @return 
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "checkCityCode")
	public String checkCityCode(String cityCode) {
		int cityCodeNum;
		//如果省份编码为空返回true
		if (StringUtils.isBlank(cityCode)) {
			cityCodeNum = 0;
		} else {
			cityCodeNum = cityService.findCityCodeNum(cityCode);
		}

		if (cityCodeNum == 0) {
			return "true";
		} else {
			return "false";
		}
	}
	
	/**
	 * 
	 * Title: checkCityJsonCode
	 * <p>Description: 校验城市地图编码的唯一性</p>
	 * @author:     wanghan
	 * @param cityJsonCode 城市地图编码
	 * @return 
	 * String    返回类型
	 */
	@ResponseBody
	@RequestMapping(value = "checkCityJsonCode")
	public String checkCityJsonCode(String cityJsonCode) {
		int cityJsonCodeNum;
		//如果省份编码为空返回true
		if (StringUtils.isBlank(cityJsonCode)) {
			cityJsonCodeNum = 0;
		} else {
			cityJsonCodeNum = cityService.findCityJsonNum(cityJsonCode);
		}

		if (cityJsonCodeNum == 0) {
			return "true";
		} else {
			return "false";
		}
	}
}
