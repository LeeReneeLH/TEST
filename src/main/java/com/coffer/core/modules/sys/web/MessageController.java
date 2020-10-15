package com.coffer.core.modules.sys.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.Message;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.MessageService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.google.common.collect.Lists;

import net.sf.json.JSON;

/**
 * 系统通知Controller
 * 
 * @author yanbingxu
 * @version 2017-9-27
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/message")
public class MessageController extends BaseController {

	@Autowired
	private MessageService messageService;

	/**
	 * 获取通知信息及相关权限
	 * 
	 * @param id
	 * @return
	 */
	public Message get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			Message message = messageService.getMessage(id);
			List<Dict> userAuthorityList = Lists.newArrayList();
			List<Office> officeAuthorityList = Lists.newArrayList();
			for (String authority : messageService.findUserAuthority(message.getId())) {
				userAuthorityList.addAll(DictUtils.getDictList("sys_user_type", true, authority));
			}
			for (String authority : messageService.findOfficeAuthority(message.getId())) {
				officeAuthorityList.add(SysCommonUtils.findOfficeById(authority));
			}
			message.setUserAuthorityList(userAuthorityList);
			message.setOfficeAuthorityList(officeAuthorityList);
			return message;
		} else {
			return new Message();
		}
	}

	/**
	 * 通知列表页面
	 * 
	 * @param response
	 * @param request
	 * @param message
	 * @param model
	 * @return
	 */
	@RequiresPermissions(value = { "sys:message:view" })
	@RequestMapping(value = { "list", "" })
	public String list(Message message, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Message> page = messageService.findList(new Page<Message>(request, response), message);
		model.addAttribute("page", page);
		return "modules/sys/messageList";
	}

	/**
	 * 通知添加页面
	 * 
	 * @param message
	 * @param model
	 * @return
	 */
	@RequiresPermissions(value = { "sys:message:edit" })
	@RequestMapping(value = "form")
	public String form(Message message, Model model) {
		if (message.getId() != null) {
			message = get(message.getId());
			model.addAttribute("pageType", "form");
		}
		model.addAttribute("message", message);
		return "modules/sys/messageForm";
	}

	/**
	 * 通知详情页面
	 * 
	 * @param message
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "view")
	public String view(Message message, Model model) {
		message = get(message.getId());
		model.addAttribute("message", message);
		return "modules/sys/messageView";
	}

	/**
	 * 返回通知列表页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/sys/message/list?repage";
	}

	/**
	 * 保存通知
	 * 
	 * @param message
	 * @param pageType
	 * @return
	 */
	@RequestMapping(value = "save")
	public String save(Message message, String pageType) {

		// 选择特定用户情况设置用户权限
		if (message.getUserAuthorityList() != null) {
			StringBuilder userAuthority = new StringBuilder();
			for (Dict authority : message.getUserAuthorityList()) {
				userAuthority.append(authority.getId());
				userAuthority.append(Constant.Punctuation.COMMA);
			}
			message.setUserAuthority(userAuthority.toString());
		}
		// 选择特定机构情况设置机构权限
		if (message.getOfficeAuthorityList() != null) {
			StringBuilder officeAuthority = new StringBuilder();
			for (Office office : message.getOfficeAuthorityList()) {
				officeAuthority.append(office.getId());
				officeAuthority.append(Constant.Punctuation.COMMA);
			}
			message.setOfficeAuthority(officeAuthority.toString());
		}

		if ("form".equals(pageType)) {
			messageService.update(message);
		} else {
			messageService.pushMessage(message, null, null, null, null);
		}
		return "redirect:" + adminPath + "/sys/message/list?repage";
	}

	/**
	 * 通知盒子
	 * 
	 * @return
	 */
	@RequestMapping(value = { "boxList" })
	@ResponseBody
	public String boxList(Message message) {
		return gson.toJson(messageService.findMessageByAuthority(message));
	}

	/**
	 * 通知变为已读
	 * 
	 * @author: yanbingxu
	 * @param messageId
	 */
	@RequestMapping(value = { "readMessage" })
	public void readMessage(String messageId) {
		messageService.insertReadUser(messageId);
	}

	/**
	 * 忽略全部消息
	 * 
	 * @author: yanbingxu
	 * @param messageId
	 */
	@RequestMapping(value = { "readAllMessage" })
	public void readAllMessage() {
		messageService.readAllMessage();
	}

	/**
	 * 通知撤回
	 * 
	 * @author: yanbingxu
	 * @param id
	 * @param cancelReason
	 * @return
	 */
	@RequestMapping(value = { "cancel" })
	@ResponseBody
	public String cancel(String id, String cancelReason) {
		Message message = get(id);
		message.setCancelReason(cancelReason);
		message.setMessageTopic("[撤回]" + message.getMessageTopic());
		messageService.cancel(message);
		messageService.wsExcute(message);
		return "success";
	}

}
