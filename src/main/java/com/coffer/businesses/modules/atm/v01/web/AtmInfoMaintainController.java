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
import com.coffer.businesses.modules.atm.v01.service.AtmInfoMaintainService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * ATM机信息维护Controller
 * 
 * @author wxz
 * @version 2017-11-03
 */
@Controller
@RequestMapping(value = "${adminPath}/atm/v01/atmInfoMaintain")
public class AtmInfoMaintainController extends BaseController {

	@Autowired
	private AtmInfoMaintainService atmInfoMaintainService;

	@ModelAttribute
	public AtmInfoMaintain get(@RequestParam(required = false) String id) {
		AtmInfoMaintain entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = atmInfoMaintainService.get(id);
		}
		if (entity == null) {
			entity = new AtmInfoMaintain();
		}
		return entity;
	}

	/**
	 * ATM机信息维护列表页面
	 * @param atmInfoMaintain
	 * @param request
	 * @param response
	 * @param model
	 * @author wxz
	 * @version 2017-11-03
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:view")
	@RequestMapping(value = { "list", "" })
	public String list(AtmInfoMaintain atmInfoMaintain, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		atmInfoMaintain.getSqlMap().put("dsf", SystemService.dataScopeFilter(UserUtils.getUser(), "o5", ""));
		// 查询所有ATM机维护的详细信息，并进行分页操作
		Page<AtmInfoMaintain> page = atmInfoMaintainService.findPage(new Page<AtmInfoMaintain>(request, response), atmInfoMaintain);
		model.addAttribute("page", page);
		return "modules/atm/v01/atmInfoMaintain/atmInfoMaintainList";
	}

	/**
	 * ATM机信息维护添加页面
	 * @param atmInfoMaintain
	 * @param model
	 * @author wxz
	 * @version 2017-11-03
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:view")
	@RequestMapping(value = "form")
	public String form(AtmInfoMaintain atmInfoMaintain, Model model) {
		
		model.addAttribute("atmInfoMaintain", atmInfoMaintain);
		return "modules/atm/v01/atmInfoMaintain/atmInfoMaintainForm";
	}
	
	/**
	 * ATM机信息维护详情页面
	 * @author wxz
	 * @param atmInfoMaintain
	 * @param model
	 * @version 2017-11-03
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:view")
	@RequestMapping(value = "view")
	public String View(AtmInfoMaintain atmInfoMaintain, Model model) {
		model.addAttribute("atmInfoMaintain", atmInfoMaintain);
		return "modules/atm/v01/atmInfoMaintain/atmInfoMaintainView";
	}

	/**
	 * ATM机信息维护保存
	 * @param atmInfoMaintain
	 * @param model
	 * @param redirectAttributes
	 * @author wxz
	 * @version 2017-11-03
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:edit")
	@RequestMapping(value = "save")
	public String save(AtmInfoMaintain atmInfoMaintain, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atmInfoMaintain)) {
			return form(atmInfoMaintain, model);
		}
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// 一致性校验(判断id，如果为空则是添加操作，如果不为空则是修改操作)
			if(StringUtils.isNotBlank(atmInfoMaintain.getId())){
				atmInfoMaintainService.checkVersion(atmInfoMaintain);
			}
			// 保存
			atmInfoMaintainService.save(atmInfoMaintain);
			
			// 设置消息国际化
			String message = msg.getMessage("message.I4006", null, locale);
			addMessage(redirectAttributes, message);
		} catch (BusinessException be) {
			// 设置消息国际化
			String message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/atm/v01/atmInfoMaintain/list?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmInfoMaintain/list?repage";
	}

	/**
	 * ATM机信息维护删除
	 * @param atmInfoMaintain
	 * @param redirectAttributes
	 * @author wxz
	 * @version 2017-11-03
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:edit")
	@RequestMapping(value = "delete")
	public String delete(AtmInfoMaintain atmInfoMaintain, RedirectAttributes redirectAttributes) {
		atmInfoMaintainService.delete(atmInfoMaintain);
		Locale locale = LocaleContextHolder.getLocale();
		// 设置消息国际化
		String message = msg.getMessage("message.I4007", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmInfoMaintain/list?repage";
	}

	/**
	 * 恢复删除的ATM机信息维护信息
	 * 
	 * @author wxz
	 * @version 2017年11月07日
	 * 
	 * @param atmInfoMaintain
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:edit")
	@RequestMapping(value = "recovery")
	public String recovery(AtmInfoMaintain atmInfoMaintain, RedirectAttributes redirectAttributes) {
		atmInfoMaintain.setDelFlag(AtmInfoMaintain.DEL_FLAG_NORMAL);
		atmInfoMaintainService.save(atmInfoMaintain);
		Locale locale = LocaleContextHolder.getLocale();
		// 设置消息国际化
		String message = msg.getMessage("message.I4008", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmInfoMaintain/list?repage";
	}
	
	/**
	 * 根据选择的品牌编号，获取品牌名称和型号编号
	 * @author wxz
	 * 
	 * @version 2017-11-06
	 * @return
	 */
	@RequestMapping(value = "changeAtmType")
	@ResponseBody
	public String changeAtmType(String param, Model model, RedirectAttributes redirectAttributes){
		// 获取品牌编号
		param = param.replace("&quot;", "");
		List<AtmBrandsInfo> list = null;
		List<Map<String, Object>> typeNoList = Lists.newArrayList();
		AtmBrandsInfo atmBrandsInfo = new AtmBrandsInfo();
		atmBrandsInfo.setAtmBrandsNo(param);
		// 判断品牌编号是否为空
		if (!("".equals(param)) && param != null) {
			list = atmInfoMaintainService.findAtmTypeNameNo(atmBrandsInfo);
			// 判断查询ATM机是否存在详细数据
			if (!Collections3.isEmpty(list)) {
				if (list != null) {
					// 循环遍历取出型号名称和型号编号
					for (AtmBrandsInfo item : list) {
						Map<String, Object> mapTypeNo = Maps.newHashMap();
						mapTypeNo.put("label", item.getAtmTypeName());
						mapTypeNo.put("id", item.getAtmTypeNo());
						typeNoList.add(mapTypeNo);
					}
				}
			}
		}
		return gson.toJson(typeNoList);
	}
	
	/**
	 * 通过输入的ATM机编号，查询是否已经存在
	 * @param request
	 * @param atmId
	 * @author wxz
	 * @version 2017-11-03
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:edit")
	@RequestMapping(value = "atm")
	@ResponseBody
	public String getAtmIdInfo(HttpServletRequest request, String atmId) {
		// 获取ATM机编号
		String oldAtmId = request.getParameter("oldAtmId");
		// 判断ATM机编号是否为空，修改后的ATM机编号是否与之前相等(ATM机编号是否有改动)
		if (StringUtils.isNotBlank(atmId) && atmId.equals(oldAtmId)) {
			return "true";
		}
		if (StringUtils.isNotBlank(atmId)) {
			AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
			atmInfoMaintain.setAtmId(atmId);
			// 获取品牌型号详细信息
			List<AtmInfoMaintain> list = atmInfoMaintainService.findByAtmNoAndTellerId(atmInfoMaintain);
			// 判断是否存在ATM机信息
			if (!Collections3.isEmpty(list)) {
					return "false";
			}
		}
		return "true";
	}
	
	/**
	 * 通过输入的柜员号，查询是否已经存在
	 * @param request
	 * @param tellerId
	 * @author wxz
	 * @version 2017-11-03
	 * @return
	 */
	@RequiresPermissions("atm:atmInfoMaintain:edit")
	@RequestMapping(value = "teller")
	@ResponseBody
	public String getTellerIdInfo(HttpServletRequest request, String tellerId) {
		// 柜员号
		String oldTellerId = request.getParameter("oldTellerId");
		// 判断柜员号是否为空，修改后的柜员号是否与之前相等(柜员号是否有改动)
		if (tellerId != null && tellerId.equals(oldTellerId)) {
			return "true";
		}
		if (StringUtils.isNotBlank(tellerId)) {
			AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
			atmInfoMaintain.setTellerId(tellerId);
			// 获取品牌型号详细信息
			List<AtmInfoMaintain> list = atmInfoMaintainService.findByAtmNoAndTellerId(atmInfoMaintain);

			// 判断是否存在柜员号信息
			if (!Collections3.isEmpty(list)) {
					return "false";
			}
		}
		return "true";
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
		return "redirect:" + adminPath + "/atm/v01/atmInfoMaintain/list?repage";
	}
}
