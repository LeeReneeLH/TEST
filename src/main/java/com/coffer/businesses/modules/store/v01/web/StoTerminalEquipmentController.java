package com.coffer.businesses.modules.store.v01.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.store.v01.entity.StoTerminalEquipment;
import com.coffer.businesses.modules.store.v01.service.StoTerminalEquipmentService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.ServiceException;
import com.coffer.core.common.web.BaseController;

/**
 * 终端设备管理Controller
 * @author yuxixuan
 * @version 2015-12-11
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoTerminalEquipment")
public class StoTerminalEquipmentController extends BaseController {

	@Autowired
	private StoTerminalEquipmentService stoTerminalEquipmentService;

	/**
	 * 查询设备列表
	 * 
	 * @param stoTerminalEquipment
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:stoTerminalEquipment:view")
	@RequestMapping(value = {"list", ""})
	public String list(StoTerminalEquipment stoTerminalEquipment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Locale locale = LocaleContextHolder.getLocale();
		String searchKey = msg.getMessage("store.equipment.searchKey", null, locale);
		if (searchKey.equals(stoTerminalEquipment.getId())) {
			stoTerminalEquipment.setDisplayDialFg(true);
			model.addAttribute("displayDialFg", true);
		}
		Page<StoTerminalEquipment> page = stoTerminalEquipmentService.findPage(new Page<StoTerminalEquipment>(request, response), stoTerminalEquipment); 
		model.addAttribute("page", page);
		return "modules/store/v01/stoTerminalEquipment/stoTerminalEquipmentList";
	}

	/**
	 * 改变设备状态
	 * 
	 */
	@RequestMapping(value = "changeTeStatus")
	public String changeTeStatus(@RequestParam(required = true) String teId, RedirectAttributes redirectAttributes) {

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			stoTerminalEquipmentService.changeTeStatus(teId);
			message = msg.getMessage("message.I1017", null, locale);
		} catch (ServiceException se) {
			message = msg.getMessage("message.E1041", null, locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoTerminalEquipment/list?isSearch=true&repage";
	}
}
