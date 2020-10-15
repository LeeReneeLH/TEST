package com.coffer.businesses.modules.atm.v01.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.businesses.modules.atm.v01.service.AtmBrandsInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * ATM品牌型号管理Controller
 * 
 * @author wxz
 * @version 2017-11-02
 */
@Controller
@RequestMapping(value = "${adminPath}/atm/v01/atmBrandsInfo")
public class AtmBrandsInfoController extends BaseController {

	@Autowired
	private AtmBrandsInfoService atmBrandsInfoService;

	@ModelAttribute
	public AtmBrandsInfo get(@RequestParam(required = false) String id) {
		AtmBrandsInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = atmBrandsInfoService.get(id);
		}
		if (entity == null) {
			entity = new AtmBrandsInfo();
		}
		return entity;
	}

	/**
	 * 品牌型号列表页面
	 * @param atmBrandsInfo
	 * @param request
	 * @param response
	 * @param model
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(AtmBrandsInfo atmBrandsInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 查询所有品牌型号的详细信息，并进行分页操作
		Page<AtmBrandsInfo> page = atmBrandsInfoService.findPage(new Page<AtmBrandsInfo>(request, response), atmBrandsInfo);
		model.addAttribute("page", page);
		return "modules/atm/v01/atmBrandsInfo/atmBrandsInfoList";
	}

	/**
	 * 品牌型号添加页面
	 * @param atmBrandsInfo
	 * @param model
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:view")
	@RequestMapping(value = "form")
	public String form(AtmBrandsInfo atmBrandsInfo, Model model) {
		model.addAttribute("atmBrandsInfo", atmBrandsInfo);
		return "modules/atm/v01/atmBrandsInfo/atmBrandsInfoForm";
	}
	
	/**
	 * 品牌型号详情页面
	 * @author wxz
	 * @param atmBrandsInfo
	 * @param model
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:view")
	@RequestMapping(value = "view")
	public String View(AtmBrandsInfo atmBrandsInfo, Model model) {
		model.addAttribute("atmBrandsInfo", atmBrandsInfo);
		return "modules/atm/v01/atmBrandsInfo/atmBrandsInfoView";
	}

	/**
	 * 品牌型号保存
	 * @param atmBrandsInfo
	 * @param model
	 * @param redirectAttributes
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:edit")
	@RequestMapping(value = "save")
	public String save(AtmBrandsInfo atmBrandsInfo, Model model, RedirectAttributes redirectAttributes) {
		// 服务端参数有效性验证
		if (!beanValidator(model, atmBrandsInfo)) {
			return form(atmBrandsInfo, model);
		}
		
		Locale locale = LocaleContextHolder.getLocale();
		
		try {
			// 一致性校验(判断id，如果为空则是添加操作，如果不为空则是修改操作)
			if(StringUtils.isNotBlank(atmBrandsInfo.getId())){
				atmBrandsInfoService.checkVersion(atmBrandsInfo);
			}
			// 保存
			atmBrandsInfoService.save(atmBrandsInfo);
			
			// 设置消息国际化
			String message = msg.getMessage("message.I4003", null, locale);
			addMessage(redirectAttributes, message);
		} catch (BusinessException be) {
			// 设置消息国际化
			String message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/atm/v01/atmBrandsInfo/list?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmBrandsInfo/list?repage";
	}

	/**
	 * 品牌型号删除
	 * @param atmBrandsInfo
	 * @param redirectAttributes
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(AtmBrandsInfo atmBrandsInfo, RedirectAttributes redirectAttributes) {
		// 删除
		atmBrandsInfoService.delete(atmBrandsInfo);
		Locale locale = LocaleContextHolder.getLocale();
		// 设置消息国际化
		String message = msg.getMessage("message.I4005", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmBrandsInfo/list?repage";
	}

	/**
	 * 通过品牌编号，查询品牌名称
	 * @param atmBrandsNo
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:edit")
	@RequestMapping(value = "atmBrand")
	@ResponseBody
	public String getAtmBrandInfo(String atmBrandsNo) {
		// 判断品牌编号是否为空
		if (StringUtils.isNotBlank(atmBrandsNo)) {
			AtmBrandsInfo atmBrandsInfo = this.get(null);
			atmBrandsInfo.setAtmBrandsNo(atmBrandsNo);
			// 查询品牌型号的详细信息
			List<AtmBrandsInfo> list = atmBrandsInfoService.findList(atmBrandsInfo);
			Map<String, String> map = Maps.newHashMap();
			String atmBrandsName = null;
			// 判断是否存在详细信息
			if (list != null && !Collections3.isEmpty(list)) {
				atmBrandsInfo = list.get(0);
				if (atmBrandsInfo != null) {
					// 获取品牌名称
					atmBrandsName = atmBrandsInfo.getAtmBrandsName();
				}
			}
			map.put("atmBrandsName", atmBrandsName);
			return new Gson().toJson(map);
		} else {
			return null;
		}
	}

	/**
	 * 通过输入的机型编号，查询是否已经存在
	 * @param request
	 * @param atmTypeNo
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:edit")
	@RequestMapping(value = "atmType")
	@ResponseBody
	public String getAtmTypeInfo(HttpServletRequest request, String atmTypeNo) {
		// 机型编号
		String atmBrandsNo = request.getParameter("atmBrandsNo");
		String oldAtmTypeNo = request.getParameter("oldAtmTypeNo");
		// 判断型号编号是否为空，是否相等
		if (atmTypeNo != null && atmTypeNo.equals(oldAtmTypeNo)) {
			return "true";
		}
		if (StringUtils.isNotBlank(atmBrandsNo) && StringUtils.isNotBlank(atmTypeNo)) {
			AtmBrandsInfo atmBrandsInfo = this.get(null);
			atmBrandsInfo.setAtmTypeNo(atmTypeNo);
			// 获取品牌型号详细信息
			List<AtmBrandsInfo> list = atmBrandsInfoService.findList(atmBrandsInfo);

			if (list != null && !Collections3.isEmpty(list)) {
				atmBrandsInfo = list.get(0);
				if (atmBrandsInfo != null) {
					return "false";
				}
			}
		}
		return "true";
	}

	/**
	 * 恢复删除的品牌信息
	 * 
	 * @author wxz
	 * @version 2017-11-02
	 * 
	 * 
	 * @param atmBrandsInfo
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:edit")
	@RequestMapping(value = "recovery")
	public String recovery(AtmBrandsInfo atmBrandsInfo, RedirectAttributes redirectAttributes) {
		atmBrandsInfo.setDelFlag(AtmBrandsInfo.DEL_FLAG_NORMAL);
		// 保存
		atmBrandsInfoService.save(atmBrandsInfo);
		Locale locale = LocaleContextHolder.getLocale();
		// 设置消息国际化
		String message = msg.getMessage("message.I4004", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmBrandsInfo/list?repage";
	}
	
	/**
	 * 根据状态显示型号
	 * @author wxz
	 * @version 2017-12-26
	 * 
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:view")
	@RequestMapping(value = "changeByStatus")
	@ResponseBody
	public String changeByStatus(String param){
		// 获取状态
		param = param.replace("&quot;", "");
		List<Map<String, Object>> dataList = Lists.newArrayList();
		
		AtmBrandsInfo atmBrandsInfo = new AtmBrandsInfo();
		atmBrandsInfo.setDelFlag(param);
		// 获取ATM型号信息
		List<AtmBrandsInfo> atmList = atmBrandsInfoService.findDistinctAtmTypeList(atmBrandsInfo);
		// 遍历ATM型号信息
		for(AtmBrandsInfo atm : atmList){
			// 型号名称拼接 (格式 ： 型号名称：型号编号)
			atm.setAtmTypeName(atm.getAtmTypeName() + " : " +atm.getAtmTypeNo());
		}
		// 初始化select2下拉列表 添加请选择
		Map<String, Object> mapSelect = Maps.newHashMap();
		mapSelect.put("label", "请选择");
		mapSelect.put("id", "");
		dataList.add(mapSelect);
		// 将ATM型号信息存放
		for (AtmBrandsInfo item : atmList) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("label", item.getAtmTypeName());
			map.put("id", item.getAtmTypeNo());
			dataList.add(map);
		}
		return gson.toJson(dataList);
	}
	
	/**
	 * 品牌型号删除判断(查询要删除的品牌型号是否已创建ATM机)
	 * @author wxz
	 * @version 2018年1月4日
	 * @param checkId
	 * @return
	 */
	@RequiresPermissions("atm:atmBrandsInfo:edit")
	@RequestMapping(value = "checkDel")
	@ResponseBody
	public String checkDel(String checkId){
		// 查询品牌型号相信信息
		AtmBrandsInfo atmBrandsInfo = atmBrandsInfoService.get(checkId);
		
		// 获取品牌编号和型号编号
		AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
		atmInfoMaintain.setAtmBrandsNo(atmBrandsInfo.getAtmBrandsNo());
		atmInfoMaintain.setAtmTypeNo(atmBrandsInfo.getAtmTypeNo());
		
		// 获取ATM机信息列表
		List<AtmInfoMaintain> atmInfo = atmBrandsInfoService.findByNo(atmInfoMaintain);
		// 判断ATM机信息是否为空
		if(Collections3.isEmpty(atmInfo)){
			return "true";
		} else {
			return "false";
		}
	}
	
	/**
	 * 返回上一级页面
	 * 
	 * @author wxz
	 * @version 2018年1月4日
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/atm/v01/atmBrandsInfo/list?repage";
	}
}
