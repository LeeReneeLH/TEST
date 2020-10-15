package com.coffer.core.modules.sys.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.Message;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.MessageRelevanceService;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 系统通知关联Controller
 * 
 * @author yanbingxu
 * @version 2017-9-27
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/messageRelevance")
public class MessageRelevanceController extends BaseController {

	@Autowired
	private MessageRelevanceService messageRelevanceService;
	@Autowired
	private SystemService systemService;

	/**
	 * 通知关联列表页面
	 * 
	 * @param response
	 * @param request
	 * @param message
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "list" })
	public String relevanceList(Message message, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<Message> page = messageRelevanceService.findRelevanceList(new Page<Message>(request, response), message);
		model.addAttribute("page", page);
		return "modules/sys/msgRelevanceList";
	}

	/**
	 * 通知关联添加页面
	 * 
	 * @param message
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "form")
	public String relevanceForm(Message message, Model model) {
		if (message.getMessageType() != null) {
			message = messageRelevanceService.findRelevance(message.getMessageType());
			String type = message.getMessageType();
			// 消息类型拆分用作显示
			if (type.length() > 1) {
				message.setMessageType(type.substring(0, 2));
				if (type.length() > 3) {
					message.setBusinessType(type.substring(2, 4));
				}
				if (type.length() > 5) {
					message.setBusinessStatus(type.substring(4, 6));
				}
			}
			model.addAttribute("message", message);
			model.addAttribute("msgType", type);
			model.addAttribute("pageType", "form");
		}
		return "modules/sys/msgRelevanceForm";
	}

	/**
	 * 返回通知关联列表页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String relevanceBack(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/sys/messageRelevance/list?repage";
	}

	/**
	 * 通知关联启用/停用
	 * 
	 * @param response
	 * @param request
	 * @param messageType
	 * @return
	 */
	@RequestMapping(value = "validChange")
	public String relevanceValidChange(String messageType, HttpServletResponse response, HttpServletRequest request) {
		messageRelevanceService.relevanceValidChange(messageRelevanceService.findRelevance(messageType));
		return "redirect:" + adminPath + "/sys/messageRelevance/list?repage";
	}

	/**
	 * 
	 * Title: makeBusinessStatusOptions
	 * <p>
	 * Description: 页面业务类型和业务状态关联做成
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param businessType
	 * @return String 返回类型
	 */
	@RequestMapping(value = "makeBusinessStatusOptions")
	@ResponseBody
	public String makeBusinessStatusOptions(String businessType) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> dictValueList = Lists.newArrayList();
		List<String> dictLabelList = Lists.newArrayList();
		List<Dict> dictList = DictUtils.getDictList("all_status");
		List<String> relationList = Global.getList("messageTypeSelect.correspond.statusDict");
		for (String relation : relationList) {
			String relationArray[] = relation.split(Constant.Punctuation.HALF_COLON);
			if (relationArray.length >= 2) {
				if (businessType.equals(relationArray[0])) {
					dictList = DictUtils.getDictList(relationArray[1]);
				}
			}
		}
		for (Dict dict : dictList) {
			dictValueList.add(dict.getValue());
			dictLabelList.add(dict.getLabel());
		}
		resultMap.put("dictLabelList", dictLabelList);
		resultMap.put("dictValueList", dictValueList);
		return gson.toJson(resultMap);
	}

	/**
	 * 
	 * Title: makeBusinessTypeOptions
	 * <p>Description: 页面消息类型和业务类型关联做成</p>
	 * @author:     yanbingxu
	 * @param messageType
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "makeBusinessTypeOptions")
	@ResponseBody
	public String makeBusinessTypeOptions(String messageType) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> dictValueList = Lists.newArrayList();
		List<String> dictLabelList = Lists.newArrayList();
		List<Dict> dictList = DictUtils.getDictList("all_businessType");
		List<String> relationList = Global.getList("messageTypeSelect.correspond.businessDict");
		for (String relation : relationList) {
			String relationArray[] = relation.split(Constant.Punctuation.HALF_COLON);
			if (relationArray.length >= 2) {
				if (messageType.equals(relationArray[0])) {
					dictList = DictUtils.getDictList(relationArray[1]);
				}
			}
		}
		for (Dict dict : dictList) {
			dictValueList.add(dict.getValue());
			dictLabelList.add(dict.getLabel());
		}
		resultMap.put("dictLabelList", dictLabelList);
		resultMap.put("dictValueList", dictValueList);
		return gson.toJson(resultMap);
	}
	
	/**
	 * 保存通知关联
	 * 
	 * @param message
	 * @param pageType
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "save")
	public String save(Message message, String pageType, Model model) {

		Locale locale = LocaleContextHolder.getLocale();

		// 设置菜单编号和URL
		if (StringUtils.isNotBlank(message.getMenuId())) {
			message.setUrl(systemService.getMenu(message.getMenuId()).getHref());
			message.setMenuId(
					// 通过控制页面输入决定数组长度，不用做长度判断
					systemService.getMenu(message.getMenuId()).getParentIds().split(Constant.Punctuation.COMMA)[2]);
		}
		// 设置用户权限
		if (message.getUserAuthorityList() != null) {
			String userAuthority = "";
			for (Dict authority : message.getUserAuthorityList()) {
				userAuthority += authority.getId() + Constant.Punctuation.COMMA;
			}
			message.setUserAuthority(userAuthority);
		}
		// 设置机构权限
		if (message.getOfficeAuthorityList() != null) {
			String officeAuthority = "";
			for (Office office : message.getOfficeAuthorityList()) {
				officeAuthority += office.getId() + Constant.Punctuation.COMMA;
			}
			message.setOfficeAuthority(officeAuthority);
		}

		String type = message.getMessageType();
		if (StringUtils.isNotBlank(message.getBusinessType())) {
			type += message.getBusinessType();
		}
		if (StringUtils.isNotBlank(message.getBusinessStatus())) {
			type += message.getBusinessStatus();
		}
		message.setMessageType(type);

		if ("form".equals(pageType)) {
			messageRelevanceService.update(message);
		} else {
			// 验证消息类型重复性
			if (messageRelevanceService.findRelevance(type) != null) {
				model.addAttribute("warning", msg.getMessage("message.I8001", null, locale));
				message.setMessageType(message.getMessageType().substring(0, 2));
				model.addAttribute("message", message);
				return "modules/sys/msgRelevanceForm";
			}
			messageRelevanceService.save(message);
		}
		return "redirect:" + adminPath + "/sys/messageRelevance/list?repage";
	}

}
