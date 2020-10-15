package com.coffer.businesses.modules.collection.v03.web;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.businesses.modules.collection.v03.service.TaskConfirmService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 任务确认Controller
 * 
 * @author wanglin
 * @version 2017-02-13
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/taskConfirm")
public class TaskConfirmController extends BaseController {

	@Autowired
	private TaskConfirmService taskConfirmService;

	@ModelAttribute
	public TaskConfirm get(@RequestParam(required = false) String id) {
		TaskConfirm entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = taskConfirmService.get(id);
		}
		if (entity == null) {
			entity = new TaskConfirm();
		}
		return entity;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param TaskConfirm
	 * @return
	 */
	@RequiresPermissions("task:taskConfirm:view")
	@RequestMapping(value = { "list", "" })
	public String list(TaskConfirm taskConfirm, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 清分人员
		User user = UserUtils.getUser();
		//StoEscortInfo stoEscortInfo = StoreCommonUtils.findByUserId(user.getId());
		//if (stoEscortInfo != null) {
			//taskConfirm.setClearManNo(stoEscortInfo.getId());
		//}
		taskConfirm.setAllotStatus(CollectionConstant.allotStatusType.CONFIRM_OK);
		Page<TaskConfirm> page = taskConfirmService.findPage(new Page<TaskConfirm>(request, response), taskConfirm);
		model.addAttribute("page", page);
		return "modules/collection/v03/task/taskConfirmList";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年2月14日 任务确认
	 * @param TaskConfirm
	 * @return
	 */
	@RequiresPermissions("task:taskConfirm:edit")
	@RequestMapping(value = "confirm")
	public String confirm(TaskConfirm taskConfirm, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 进行确认操作
			taskConfirmService.confirm(taskConfirm);
			// 弹出确认成功信息
			message = msg.getMessage("message.I7210", null, locale);
			// message = msg.getMessage("message.I7210", new String[] {
			// taskConfirm.getOrderId() }, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/collection/v03/taskConfirm/?repage&searchAllotStatus="
				+ taskConfirm.getSearchAllotStatus() + "&searchDoorId=" + taskConfirm.getSearchDoorId()
				+ "&searchClearManNo=" + taskConfirm.getSearchClearManNo();
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年2月14日 任务驳回
	 * @param TaskConfirm
	 * @return
	 */
	@RequiresPermissions("task:taskConfirm:edit")
	@RequestMapping(value = "reject")
	public String reject(TaskConfirm taskConfirm, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 进行驳回操作
			taskConfirmService.reject(taskConfirm);
			// 弹出确认成功信息
			message = msg.getMessage("message.I7211", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/collection/v03/taskConfirm/?repage&searchAllotStatus="
				+ taskConfirm.getSearchAllotStatus() + "&searchDoorId=" + taskConfirm.getSearchDoorId()
				+ "&searchClearManNo=" + taskConfirm.getSearchClearManNo();
	}

}