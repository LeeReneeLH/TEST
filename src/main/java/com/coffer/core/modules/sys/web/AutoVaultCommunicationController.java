package com.coffer.core.modules.sys.web;

import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.task.QueueManager;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.AutoVaultCommunication;
import com.coffer.core.modules.sys.service.AutoVaultCommunicationService;


/**
 * 通信Controller
 * 
 * @author SongYuanYang
 * @version 2018年5月4日
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/autoVaultCommunication")
public class AutoVaultCommunicationController extends BaseController {
	
	@Autowired
	private QueueManager queueManager;

	@Autowired
	private AutoVaultCommunicationService autoVaultCommunicationService;

	@ModelAttribute
	public AutoVaultCommunication get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return autoVaultCommunicationService.get(id);
		} else {
			return new AutoVaultCommunication();
		}
	}

	/**
	 * 根据查询条件，查询多条通信信息
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月4日
	 * 
	 * @param autoVaultCommunication
	 *            通信信息
	 * @param request
	 * @param response
	 * @param model
	 * 
	 * @return 通信信息列表页面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(AutoVaultCommunication autoVaultCommunication, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		Page<AutoVaultCommunication> page = autoVaultCommunicationService
				.findPage(new Page<AutoVaultCommunication>(request, response), autoVaultCommunication);
		model.addAttribute("page", page);
		model.addAttribute("autoVaultCommunication", autoVaultCommunication);
		return "modules/sys/autoVaultCommunicationList";
	}

	/**
	 * 保存数据
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月22日
	 * 
	 * @param autoVaultCommunication
	 *            通信信息
	 * @param model
	 * 
	 * @return 通信信息列表页面
	 */
	@RequestMapping(value = { "save" })
	public String save(AutoVaultCommunication autoVaultCommunication, Model model) {
		// 测试信息
		autoVaultCommunication.setId(IdGen.uuid());
		autoVaultCommunication.setStatus("01");
		autoVaultCommunication.setMessage("流水单号:180502220002,业务类型:申请上缴");
		autoVaultCommunication.setInJson(
				"{'serviceNo': '86','allId': '180502220001', 'officeId': '28900020001', 'bussinessType': '50', 'boxList': [{'rfid':''534567}]}");
		//autoVaultCommunication.setOutJson("{'resultFlag': '00', 'serviceNo': '86'}");
		autoVaultCommunication.setCreateBy(autoVaultCommunication.getCurrentUser());
		autoVaultCommunication.setCreateDate(new Date());
		autoVaultCommunication.setUpdateBy(autoVaultCommunication.getCurrentUser());
		autoVaultCommunication.setUpdateDate(new Date());
		autoVaultCommunicationService.saveMessage(autoVaultCommunication);
		model.addAttribute("autoVaultCommunication", autoVaultCommunication);
		return "modules/sys/autoVaultCommunicationList";
	}

	/**
	 * 发送数据
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月23日
	 * 
	 * @param autoVaultCommunication
	 *            通信信息
	 * @param model
	 * 
	 * @return 通信信息列表页面
	 */
	@RequestMapping(value = { "send" })
	public String send(AutoVaultCommunication autoVaultCommunication, RedirectAttributes redirectAttributes) {
		// 本地化资源
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		queueManager.communicationPut(autoVaultCommunication.getInJson(), autoVaultCommunication.getId());
		// 设置通信状态为发送中
		autoVaultCommunication.setStatus(Constant.CommunicationStatus.TO_BE_SENT);
		autoVaultCommunication.setUpdateBy(autoVaultCommunication.getCurrentUser());
		autoVaultCommunication.setUpdateDate(new Date());
		autoVaultCommunicationService.updateStatus(autoVaultCommunication);
		// 操作成功
		message = msg.getMessage("message.I0005", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/sys/autoVaultCommunication/list";
	}

	/**
	 * 显示详细信息
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月22日
	 * 
	 * @param autoVaultCommunication
	 *            通信信息
	 * @param model
	 * 
	 * @return 通信信息详情页面
	 */
	@RequestMapping(value = { "view" })
	public String view(AutoVaultCommunication autoVaultCommunication, Model model) {
		model.addAttribute("autoVaultCommunication", autoVaultCommunication);
		return "modules/sys/autoVaultCommunicationView";
	}
	
	/**
	 * 切换状态  失败<>终止
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月23日
	 * 
	 * @param autoVaultCommunication
	 *            通信信息
	 * @param model
	 * 
	 * @return 通信信息列表页面
	 */
	@RequestMapping(value = "change")
	public String change(AutoVaultCommunication autoVaultCommunication, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			autoVaultCommunicationService.change(autoVaultCommunication);
			message = msg.getMessage("message.I0005", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), null, locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/sys/autoVaultCommunication/list";
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author SongYuanYang
	 * @version 2018年5月22日
	 * 
	 */
	@RequestMapping(value = "back")
	public String back() {
		return "redirect:" + adminPath + "/sys/autoVaultCommunication/list";
	}
}
