package com.coffer.businesses.modules.collection.v03.web;

import java.util.Date;
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

import com.coffer.businesses.modules.collection.v03.entity.ClearMan;
import com.coffer.businesses.modules.collection.v03.entity.TaskDown;
import com.coffer.businesses.modules.collection.v03.service.TaskDownService;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 任务分配Controller
 * @author wanglin
 * @version 2017-02-13
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/taskDown")
public class TaskDownController extends BaseController {

	@Autowired
	private TaskDownService taskDownService;
	
	@ModelAttribute
	public TaskDown get(@RequestParam(required=false) String id) {
		TaskDown entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = taskDownService.get(id);
		}
		if (entity == null){
			entity = new TaskDown();
		}
		return entity;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param TaskDown
	 * @return
	 */
	@RequiresPermissions("task:taskDown:view")
	@RequestMapping(value = { "list", "" })
	public String list(TaskDown taskDown, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 延长日的设定
		taskDown.setExtendeDay(WeChatConstant.EXTENDE_DAY);
		Page<TaskDown> page = taskDownService.findPage(new Page<TaskDown>(request, response), taskDown);
		model.addAttribute("page", page);
		return "modules/collection/v03/task/taskDownList";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年2月14日
	 * 
	 *          清分人员选择
	 * @param orderId	预约ID
	 * @return
	 */
	@RequestMapping(value = "toSelectClearMan")
	public String toSelectClearMan(String orderId, Model model) {
		ClearMan clearMan = new ClearMan();
//		clearMan.setUserType(Constant.SysUserType.CLEARING_CENTER_OPT);
		clearMan.setOrderId(orderId);
		model.addAttribute("clearManInfo", clearMan);
		return "modules/collection/v03/task/clearManForm";
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年2月14日
	 *          任务分配
	 * @param orderId	预约ID
	 * @param TaskDown
	 * @return
	 */
	@RequiresPermissions("task:taskDown:allot")
	@RequestMapping(value = "taskAllot")
	public String taskAllot(String clearManNo,TaskDown taskDown, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		taskDown.setAllotDate(new Date());			//分配日期
		User user = UserUtils.getUser();
		taskDown.setAllotManNo(user.getId());		//分配人
		taskDown.setClearManNo(clearManNo);			//清分人员
		taskDown.preUpdate();
		taskDownService.taskAllot(taskDown);
		//成功
		message = msg.getMessage("message.I7209", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:"+Global.getAdminPath()+"/collection/v03/taskDown/?repage&searchAllotStatus=" 
		+ taskDown.getSearchAllotStatus() 
		+ "&searchDoorId=" + taskDown.getSearchDoorId()
		+ "&searchClearManNo=" + taskDown.getSearchClearManNo();
	}

}