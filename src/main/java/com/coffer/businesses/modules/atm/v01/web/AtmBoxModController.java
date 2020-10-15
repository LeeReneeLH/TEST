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

import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmBoxModService;
import com.coffer.businesses.modules.atm.v01.service.AtmBrandsInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * 钞箱类型配置Controller
 * 
 * @author wxz
 * @version 2017-11-02
 */
@Controller
@RequestMapping(value = "${adminPath}/atm/v01/atmBoxMod")
public class AtmBoxModController extends BaseController {

	@Autowired
	private AtmBoxModService atmBoxModService;

	@Autowired
	private AtmBrandsInfoService atmBrandsInfoService;

	@ModelAttribute
	public AtmBoxMod get(@RequestParam(required = false) String id) {
		AtmBoxMod entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = atmBoxModService.get(id);
		}
		if (entity == null) {
			entity = new AtmBoxMod();
		}
		return entity;
	}

	/**
	 * 类型配置列表页面
	 * @param atmBoxMod
	 * @param request
	 * @param response
	 * @param model
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBoxMod:view")
	@RequestMapping(value = { "list", "" })
	public String list(AtmBoxMod atmBoxMod, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 查询所有类型配置的详细信息，并进行分页操作
		Page<AtmBoxMod> page = atmBoxModService.findPage(new Page<AtmBoxMod>(request, response), atmBoxMod);

		model.addAttribute("page", page);
		return "modules/atm/v01/atmBoxMod/atmBoxModList";
	}

	/**
	 * 类型配置添加页面
	 * @param atmBoxMod
	 * @param model
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBoxMod:view")
	@RequestMapping(value = "form")
	public String form(AtmBoxMod atmBoxMod, Model model) {
		model.addAttribute("atmBoxMod", atmBoxMod);
		return "modules/atm/v01/atmBoxMod/atmBoxModForm";
	}
	
	/**
	 * 类型配置查看详情
	 * @param atmBoxMod
	 * @param model
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBoxMod:view")
	@RequestMapping(value = "view")
	public String view(AtmBoxMod atmBoxMod, Model model) {
		model.addAttribute("atmBoxMod", atmBoxMod);
		return "modules/atm/v01/atmBoxMod/atmBoxModView";
	}

	/**
	 * 类型配置保存
	 * @param atmBoxMod
	 * @param model
	 * @param redirectAttributes
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBoxMod:edit")
	@RequestMapping(value = "save")
	public String save(AtmBoxMod atmBoxMod, Model model, RedirectAttributes redirectAttributes) {
		// 服务端参数有效性验证
		if (!beanValidator(model, atmBoxMod)) {
			return form(atmBoxMod, model);
		}
		
		Locale locale = LocaleContextHolder.getLocale();
		
		try {
			// 一致性校验(判断id，如果为空则是添加操作，如果不为空则是修改操作)
			if(StringUtils.isNotBlank(atmBoxMod.getId())){
				atmBoxModService.checkVersion(atmBoxMod);
			}
			// 保存
			Office office = UserUtils.getUser().getOffice();
			// 添加当前登陆人所在机构
			atmBoxMod.setOffice(office);
			atmBoxModService.save(atmBoxMod);
			
			// 设置消息国际化
			String message = msg.getMessage("message.I4001", null, locale);
			addMessage(redirectAttributes, message);
		} catch (BusinessException be) {
			// 设置消息国际化
			String message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/atm/v01/atmBoxMod/list?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmBoxMod/list?repage";
	}

	/**
	 * 类型配置删除
	 * @param atmBoxMod
	 * @param redirectAttributes
	 * @author wxz
	 * @version 2017-11-02
	 * @return
	 */
	@RequiresPermissions("atm:atmBoxMod:edit")
	@RequestMapping(value = "delete")
	public String delete(AtmBoxMod atmBoxMod, RedirectAttributes redirectAttributes) {
		// 获取id
		String modId = atmBoxMod.getId();
		Locale locale = LocaleContextHolder.getLocale();
		// 设置消息国际化
		String message = "";
		if (StringUtils.isNotEmpty(modId)) {
			Integer num = StoreCommonUtils.getStoAtmBoxModListSize(modId);
			if (num > 0) {
				message = msg.getMessage("message.E4001", null, locale);
			} else {
				// 删除
				atmBoxModService.delete(atmBoxMod);
				message = msg.getMessage("message.I4002", null, locale);
			}
		}

		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmBoxMod/list?repage";
	}

	/**
	 * 获取满足条件的钞箱类型编号信息
	 * 
	 * @author wxz
	 * @version 2017年11月3日
	 * 
	 * 
	 * @param atmBrandsNo
	 * @param atmTypeNo
	 * @param boxType
	 * @return
	 */
	@RequiresPermissions("atm:atmBoxMod:edit")
	@RequestMapping(value = "getBoxTypeNos")
	@ResponseBody
	public String getBoxTypeNos(String atmBrandsNo, String atmBrandsId, String boxTypeNo) {
		AtmBrandsInfo atmBrandsInfo = new AtmBrandsInfo();
		AtmBoxMod atmBoxMod = new AtmBoxMod();
		List<AtmBrandsInfo> list = Lists.newArrayList();
		List<AtmBoxMod> boxModList = Lists.newArrayList();
		List<String> boxTypeNos = Lists.newArrayList();
		List<String> boxModNos = Lists.newArrayList();
		Map<String, List<String>> map = Maps.newHashMap();
		// 根据品牌编号查询信息
		if (StringUtils.isNotBlank(atmBrandsNo)) {
			atmBrandsInfo.setAtmBrandsNo(atmBrandsNo);
			atmBoxMod.setAtmBrandsNo(atmBrandsNo);
			// 品牌信息
			list = atmBrandsInfoService.findList(atmBrandsInfo);
			// 钞箱类型信息
			boxModList = atmBoxModService.findList(atmBoxMod);
		}
		// 品牌表中钞箱类型信息不为空
		if (!Collections3.isEmpty(list)) {
			for (AtmBrandsInfo brandsInfo : list) {
				if (brandsInfo != null) {
					String getBoxType = brandsInfo.getGetBoxType();
					String backBoxType = brandsInfo.getBackBoxType();
					String clcleBoxType = brandsInfo.getCycleBoxType();
					String depositBoxType = brandsInfo.getDepositBoxType();
					Integer getBoxTypeNum = brandsInfo.getGetBoxNumber();
					Integer backBoxTypeNum = brandsInfo.getBackBoxNumber();
					Integer cycleBoxTypeNum = brandsInfo.getCycleBoxNumber();
					Integer depositBoxTypeNum = brandsInfo.getDepositBoxNumber();
					// 取款箱
					if (StringUtils.isNotBlank(getBoxType) && (getBoxTypeNum != null && getBoxTypeNum > 0)) {
						if (!boxTypeNos.contains(getBoxType)) {
							boxTypeNos.add(getBoxType);
						}
					}
					// 回收箱
					if (StringUtils.isNotBlank(backBoxType) && (backBoxTypeNum != null && backBoxTypeNum > 0)) {
						if (!boxTypeNos.contains(backBoxType)) {
							boxTypeNos.add(backBoxType);
						}
					}
					// 循环箱
					if (StringUtils.isNotBlank(clcleBoxType) && (cycleBoxTypeNum != null && cycleBoxTypeNum > 0)) {
						if (!boxTypeNos.contains(clcleBoxType)) {
							boxTypeNos.add(clcleBoxType);
						}
					}
					// 存款箱
					if (StringUtils.isNotBlank(depositBoxType) && (depositBoxTypeNum != null && depositBoxTypeNum > 0)) {
						if (!boxTypeNos.contains(depositBoxType)) {
							boxTypeNos.add(depositBoxType);
						}
					}
				}
			}
		}
		// 钞箱类型表中已创建钞箱类型不为空
		if (!Collections3.isEmpty(boxModList)) {
			for (AtmBoxMod boxMod : boxModList) {
				if (boxMod != null) {
					boxModNos.add(boxMod.getBoxTypeNo());
				}
			}
			boxTypeNos.removeAll(boxModNos);
		}
		// 以创建的钞箱类型再修改
		if (StringUtils.isNotBlank(atmBrandsId) && StringUtils.isNotBlank(boxTypeNo)) {
			boxTypeNos.add(boxTypeNo);
		}

		map.put("boxTypeNos", boxTypeNos);
		return (new Gson().toJson(map));
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
		return "redirect:" + adminPath + "/atm/v01/atmBoxMod/list?repage";
	}
}
