package com.coffer.businesses.modules.clear.v03.web;

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

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClearingGroup;
import com.coffer.businesses.modules.clear.v03.service.ClearingGroupService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 清分组管理Controller
 * 
 * @author XL
 * @version 2017-08-14
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/clearingGroup")
public class ClearingGroupController extends BaseController {

	@Autowired
	private ClearingGroupService clearingGroupService;

	/**
	 * 根据主键，获取清分组信息
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param id
	 * @return 清分组信息
	 */
	@ModelAttribute
	public ClearingGroup get(@RequestParam(required = false) String id) {
		ClearingGroup entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = clearingGroupService.get(id);
		}
		if (entity == null) {
			entity = new ClearingGroup();
		}
		return entity;
	}

	/**
	 * 获取清分组列表
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @param request
	 * @param response
	 * @param model
	 * @return 清分组列表页面
	 */
	@RequiresPermissions("group:clearingGroup:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClearingGroup clearingGroup, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// User userInfo = UserUtils.getUser();
		// clearingGroup.setOffice(userInfo.getOffice());
		Page<ClearingGroup> page = clearingGroupService.findPage(new Page<ClearingGroup>(request, response),
				clearingGroup);
		// 设置每个分组人数
		clearingGroupService.setGroupNumber(page.getList());
		model.addAttribute("page", page);
		return "modules/clear/v03/clearingGroup/clearingGroupList";
	}

	/**
	 * 跳转至保存页面
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @param model
	 * @return 清分组登记页面
	 */
	@RequiresPermissions("group:clearingGroup:view")
	@RequestMapping(value = "form")
	public String form(ClearingGroup clearingGroup, Model model) {
		// 获取可分配用户
		clearingGroup = clearingGroupService.getSelectedUsers(clearingGroup);
		model.addAttribute("clearingGroup", clearingGroup);
		return "modules/clear/v03/clearingGroup/clearingGroupForm";
	}

	/**
	 * 跳转至查看页面
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @param model
	 * @return 清分组查看页面
	 */
	@RequiresPermissions("group:clearingGroup:view")
	@RequestMapping(value = "view")
	public String view(ClearingGroup clearingGroup, Model model) {
		// 获取可分配用户
		clearingGroup = clearingGroupService.getSelectedUsers(clearingGroup);
		model.addAttribute("clearingGroup", clearingGroup);
		return "modules/clear/v03/clearingGroup/clearingGroupView";
	}

	/**
	 * 保存清分组
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @param model
	 * @param redirectAttributes
	 * @return 清分组列表页面
	 */
	@RequiresPermissions("group:clearingGroup:edit")
	@RequestMapping(value = "save")
	public String save(ClearingGroup clearingGroup, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		if (!beanValidator(model, clearingGroup)) {
			return form(clearingGroup, model);
		}
		clearingGroupService.save(clearingGroup);
		// clearingGroup.getGroupName() 保存成功
		String message = msg.getMessage("message.I7800", new String[] { clearingGroup.getGroupName() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/clear/v03/clearingGroup/?repage";
	}

	/**
	 * 删除清分组
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @param redirectAttributes
	 * @return 清分组列表页面
	 */
	@RequiresPermissions("group:clearingGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(ClearingGroup clearingGroup, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		clearingGroupService.delete(clearingGroup);
		// clearingGroup.getGroupName() 删除成功
		String message = msg.getMessage("message.I7803", new String[] { clearingGroup.getGroupName() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/clear/v03/clearingGroup/?repage";
	}

	/**
	 * 设置清分组启用停用状态
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param clearingGroup
	 * @param redirectAttributes
	 * @return 清分组列表页面
	 */
	@RequiresPermissions("group:clearingGroup:edit")
	@RequestMapping(value = "update")
	public String update(ClearingGroup clearingGroup, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		// 提示信息
		String message = "";
		clearingGroupService.update(clearingGroup);
		if (clearingGroup.getGroupStatus().equals(ClearConstant.ClGroupStatus.STOP)) {
			// clearingGroup.getGroupName() 停用成功
			message = msg.getMessage("message.I7801", new String[] { clearingGroup.getGroupName() }, locale);
			addMessage(redirectAttributes, message);
		} else {
			// clearingGroup.getGroupName() 启用成功
			message = msg.getMessage("message.I7802", new String[] { clearingGroup.getGroupName() }, locale);
			addMessage(redirectAttributes, message);
		}
		return "redirect:" + Global.getAdminPath() + "/clear/v03/clearingGroup/?repage";
	}

	/**
	 * 分组编号一致性
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param groupNo
	 * @param oldNo
	 * @return 编号验证结果
	 */
	@ResponseBody
	@RequiresPermissions("group:clearingGroup:edit")
	@RequestMapping(value = "checkNo")
	public String checkNo(String groupNo, String oldNo) {
		// 更新前后编号是否为空
		if (StringUtils.isBlank(oldNo) && StringUtils.isBlank(groupNo)) {
			return "true";
		}
		// 编号无变化
		if (oldNo.equals(groupNo)) {
			return "true";
		}
		// 查询编号是否存在
		ClearingGroup clearingGroup = new ClearingGroup();
		// 设置清分组编号
		clearingGroup.setGroupNo(groupNo);
		// 设置发生机构
		User userInfo = UserUtils.getUser();
		clearingGroup.setOffice(userInfo.getOffice());
		// 查询分组
		clearingGroup = clearingGroupService.findByNoAndName(clearingGroup);
		// 分组不存在，通过验证
		if (clearingGroup == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 分组名一致性
	 * 
	 * @author XL
	 * @version 2017年8月14日
	 * @param groupName
	 * @param oldNam
	 * @return 清分组名验证结果
	 */
	@ResponseBody
	@RequiresPermissions("group:clearingGroup:edit")
	@RequestMapping(value = "checkName")
	public String checkName(String groupName, String oldName) {
		// 更新前后组名是否为空
		if (StringUtils.isBlank(oldName) && StringUtils.isBlank(groupName)) {
			return "true";
		}
		// 组名无变化
		if (oldName.equals(groupName)) {
			return "true";
		}
		// 查询组名是否存在
		ClearingGroup clearingGroup = new ClearingGroup();
		// 设置分组名称
		clearingGroup.setGroupName(groupName);

		// 设置发生机构
		User userInfo = UserUtils.getUser();
		clearingGroup.setOffice(userInfo.getOffice());
		// 查询分组
		clearingGroup = clearingGroupService.findByNoAndName(clearingGroup);
		// 分组不存在，通过验证
		if (clearingGroup == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 返回一览页面
	 * 
	 * @author XL
	 * @date 2017年9月7日
	 * @param clearingGroup
	 * @param redirectAttributes
	 * @return 一览页面
	 */
	@RequestMapping(value = "back")
	public String back(ClearingGroup clearingGroup, RedirectAttributes redirectAttributes) {
		return "redirect:" + adminPath + "/clear/v03/clearingGroup/list";
	}

}