package com.coffer.businesses.modules.store.v01.web;

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

import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoCarInfoService;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 车辆管理Controller
 * 
 * @author LLF
 * @version 2017-07-30
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoCarInfo")
public class StoCarInfoController extends BaseController {

	@Autowired
	private StoCarInfoService stoCarInfoService;
	@Autowired
	private StoRouteInfoService routeInfoService;

	@ModelAttribute
	public StoCarInfo get(@RequestParam(required = false) String id) {
		StoCarInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = stoCarInfoService.get(id);
		}
		if (entity == null) {
			entity = new StoCarInfo();
		}
		return entity;
	}

	@RequiresPermissions("store:v01:stoCarInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(StoCarInfo stoCarInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		String officeType = UserUtils.getUser().getOffice().getType();
		stoCarInfo.setOffice(UserUtils.getUser().getOffice());
		Page<StoCarInfo> page = stoCarInfoService.findPage(new Page<StoCarInfo>(request, response), stoCarInfo);
		model.addAttribute("officeType", officeType);
		model.addAttribute("page", page);
		return "modules/store/v01/stoCarInfo/stoCarInfoList";
	}

	@RequiresPermissions("store:v01:stoCarInfo:view")
	@RequestMapping(value = "form")
	public String form(StoCarInfo stoCarInfo, Model model, String pageFlag) {
		model.addAttribute("stoCarInfo", stoCarInfo);
		model.addAttribute("pageFlag", pageFlag);
		return "modules/store/v01/stoCarInfo/stoCarInfoForm";
	}

	@RequiresPermissions("store:v01:stoCarInfo:edit")
	@RequestMapping(value = "save")
	public String save(StoCarInfo stoCarInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, stoCarInfo)) {
			return form(stoCarInfo, model, null);
		}
		if (StringUtils.isBlank(stoCarInfo.getId())) {
			stoCarInfo.setCarNo(stoCarInfo.getCarHeader() + stoCarInfo.getCarNo());
			if (stoCarInfoService.findList(stoCarInfo).size() > 0) {
				addMessage(model, "[保存失败]:车辆信息【"+stoCarInfo.getCarNo()+"】已存在!");
				stoCarInfo.setCarNo(stoCarInfo.getCarNo().replace(stoCarInfo.getCarHeader(), ""));
				return form(stoCarInfo, model, null);
			}
		}
		stoCarInfo.setOffice(UserUtils.getUser().getOffice());
		stoCarInfoService.save(stoCarInfo);
		addMessage(redirectAttributes, "保存车辆成功");
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoCarInfo/?repage";
	}

	@RequiresPermissions("store:v01:stoCarInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(StoCarInfo stoCarInfo, RedirectAttributes redirectAttributes) {
		List<StoRouteInfo> routeList = routeInfoService.findList(new StoRouteInfo());
		for (StoRouteInfo route : routeList) {
			if (route.getCarNo() != null) {
				if (route.getCarNo().equals(stoCarInfo.getCarNo())) {
					String message = "[删除失败]:车辆已绑定线路【" + route.getRouteName() + "】，不能删除! ";
					addMessage(redirectAttributes, message);
					return "redirect:" + Global.getAdminPath() + "/store/v01/stoCarInfo/?repage";
				}
			}
		}
		stoCarInfoService.delete(stoCarInfo);
		addMessage(redirectAttributes, "删除车辆成功");
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoCarInfo/?repage";
	}

	/**
	 * 车号重复性验证
	 * 
	 * @param stoCarInfo
	 * @return
	 */
	@RequestMapping(value = "checkcarNo")
	@ResponseBody
	public String checkcarNo(StoCarInfo stoCarInfo, String carHeader) {
		if (stoCarInfo == null) {
			return "true";
		} else {
			stoCarInfo.setCarNo(carHeader + stoCarInfo.getCarNo());
			List<StoCarInfo> carList = stoCarInfoService.findList(stoCarInfo);
			// 库里没有此车号
			if (Collections3.isEmpty(carList)) {
				return "true";
			}
			StoCarInfo carTemp = carList.get(0);
			if (StringUtils.isNotBlank(stoCarInfo.getId()) && !stoCarInfo.getId().equals(carTemp.getId())) {
				// 修改 不是自身车牌号
				return "false";
			}
		}
		return "true";
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
		return "redirect:" + adminPath + "/store/v01/stoCarInfo/list?repage";
	}
}