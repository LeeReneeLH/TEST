package com.coffer.businesses.modules.doorOrder.v01.web;

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

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupMain;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearGroupMainService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 清机组 (主)Controller
 * 
 * @author ZXK
 * @version 2019-07-24
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/clearGroupMain")
public class ClearGroupMainController extends BaseController {

	@Autowired
	private ClearGroupMainService clearGroupMainService;
	@Autowired
	private OfficeService officeService;

	@ModelAttribute
	public ClearGroupMain get(@RequestParam(required = false) String clearGroupId) {
		ClearGroupMain entity = null;
		if (StringUtils.isNotBlank(clearGroupId)) {
			entity = clearGroupMainService.get(clearGroupId);
		}
		if (entity == null) {
			entity = new ClearGroupMain();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:clearGroupMain:view")
	@RequestMapping(value = "form")
	public String form(ClearGroupMain clearGroupMain, Model model, String pageFlag) {
		Office curOffice = new Office();
		curOffice.setId(UserUtils.getUser().getOffice().getId());
		curOffice.setType(Constant.OfficeType.STORE);
		// 查询当前清分中心下的所有门店
		List<Office> allDoors = officeService.findDoorList(curOffice);
		// 门店筛选
		List<Office> doorsList = clearGroupMainService.checkDoorList(allDoors, clearGroupMain);
		model.addAttribute("doorsList", doorsList);
		model.addAttribute("clearGroupMain", clearGroupMain);
		model.addAttribute("pageFlag", pageFlag);
		return "modules/doorOrder/v01/clearGroupMain/clearGroupMainForm";
	}

	/**
	 * 获得清机组信息列表
	 * 
	 * @author ZXK
	 * @version 2019年7月24日
	 * @param ClearGroupMain
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("doorOrder:v01:clearGroupMain:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClearGroupMain clearGroupMain, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ClearGroupMain> page = clearGroupMainService.findPage(new Page<ClearGroupMain>(request, response), clearGroupMain);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/clearGroupMain/clearGroupMainList";
	}

	/**
	 * 保存清机组信息
	 * 
	 * @author ZXK
	 * @version 2019年7月24日
	 * @param clearGroupMain
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("doorOrder:v01:clearGroupMain:edit")
	@RequestMapping(value = "save")
	public String save(ClearGroupMain clearGroupMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (!beanValidator(model, clearGroupMain)) {
			return form(clearGroupMain, model, null);
		}
		if (StringUtils.isBlank(clearGroupMain.getClearGroupId())) {
			if (!clearGroupMainService.findListByName(clearGroupMain).isEmpty()) {
				addMessage(model, "[保存失败]:清机组名称【" + clearGroupMain.getClearGroupName() + "】已存在!");
				return form(clearGroupMain, model, null);
			}
		}
		try {
			if (StringUtils.isNotBlank(clearGroupMain.getClearGroupId())) {
				clearGroupMain.setId(clearGroupMain.getClearGroupId());
			}
			clearGroupMainService.save(clearGroupMain);
			addMessage(redirectAttributes, "保存成功");
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			clearGroupMain.setClearCenterId(null);
			return form(clearGroupMain, model, " ");
		}

		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearGroupMain/?repage";
	}

	/**
	 * 删除清机组信息
	 * 
	 * @author ZXK
	 * @version 2019年7月26日
	 * @param clearGroupMain
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("doorOrder:v01:clearGroupMain:edit")
	@RequestMapping(value = "delete")
	public String delete(ClearGroupMain clearGroupMain, RedirectAttributes redirectAttributes) {
		try {
			clearGroupMainService.delete(clearGroupMain);
			addMessage(redirectAttributes, "删除成功");
		} catch (BusinessException be) {
			addMessage(redirectAttributes, "删除失败!");
		}
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearGroupMain/?repage";
	}

	/**
	 * 清机组名称重复性验证
	 * 
	 * @author ZXK
	 * @version 2019年7月24日
	 * @param clearGroupMain
	 * @return
	 */
	@RequestMapping(value = "checkName")
	@ResponseBody
	public String checkName(ClearGroupMain clearGroupMain, String clearGroupId) {
		if (clearGroupMain == null) {
			return "true";
		} else {
			List<ClearGroupMain> list = clearGroupMainService.findListByName(clearGroupMain);
			// 库里没有此名称
			if (Collections3.isEmpty(list)) {
				return "true";
			}
			ClearGroupMain temp = list.get(0);
			if (StringUtils.isNotBlank(clearGroupId) && !clearGroupId.equals(temp.getClearGroupId())) {
				// 修改 不是自身清机组名称
				return "false";
			}
		}
		return "true";
	}
	
	/**
	 * 跳转至查看页面
	 * 
	 * @author ZXK
	 * @version 2019年10月16日
	 * @param ClearGroupMain
	 * @param model
	 * @return 清机组查看页面
	 */
	@RequiresPermissions("doorOrder:v01:clearGroupMain:view")
	@RequestMapping(value = "view")
	public String view(ClearGroupMain clearGroupMain, Model model) {
		Office curOffice = new Office();
		curOffice.setId(UserUtils.getUser().getOffice().getId());
		curOffice.setType(Constant.OfficeType.STORE);
		// 查询当前清分中心下的所有门店
		List<Office> allDoors = officeService.findDoorList(curOffice);
		model.addAttribute("doorsList", allDoors);
		model.addAttribute("clearGroupMain", clearGroupMain);
		return "modules/doorOrder/v01/clearGroupMain/clearGroupMainView";
	}


	/**
	 * 返回上一级页面
	 * 
	 * @author ZXK
	 * @version 2019年7月24日
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/doorOrder/v01/clearGroupMain/list?repage";
	}
	
}
