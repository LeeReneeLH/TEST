package com.coffer.businesses.modules.doorOrder.v01.web;

import java.math.BigDecimal;
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

import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositError;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.DepositErrorService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 存款差错管理Controller
 * 
 * @author ZXK
 * @version 2019年7月4日
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/depositError")
public class DepositErrorController extends BaseController {

	@Autowired
	private DepositErrorService epositErrorService;

	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;
	
	/**
	 * 根据存款id，取得差错信息
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param 差错单号
	 *            (id)
	 * @return 差错信息
	 */
	@ModelAttribute
	public DepositError get(@RequestParam(required = false) String id) {
		DepositError entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = epositErrorService.get(id);
		}
		if (entity == null) {
			entity = new DepositError();
		}
		return entity;
	}

	/**
	 * 获取存款差错信息列表
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param depositError
	 * @param request
	 * @param response
	 * @param model
	 * @return 存款差错信息一览页面
	 */
	@RequiresPermissions("doorOrder:v01:depositError:view")
	@RequestMapping(value = { "list", "" })
	public String list(DepositError depositError, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		Page<DepositError> page = epositErrorService.findPage(new Page<DepositError>(request, response), depositError);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/depositError/depositErrorList";
	}

	@RequiresPermissions("doorOrder:v01:depositError:view")
	@RequestMapping(value = "form")
	public String form(DepositError depositError, Model model, String pageFlag) {
		model.addAttribute("depositError", depositError);
		model.addAttribute("pageFlag", pageFlag);
		return "modules/doorOrder/v01/depositError/depositErrorForm";
	}

	/**
	 * 新增存款差错记录
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param depositError
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("doorOrder:v01:depositError:edit")
	@RequestMapping(value = "save")
	public synchronized String save(DepositError depositError, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (!beanValidator(model, depositError)) {
			return form(depositError, model, null);
		}
		try {
			// depositError.setId(IdGen.uuid());
			epositErrorService.save(depositError);
			addMessage(redirectAttributes, "保存成功");
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			// 清空单号
			depositError.setId(null);
			return form(depositError, model, " ");
		}

		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/depositError/?repage";
	}

	/**
	 * 存款记录冲正
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param depositError
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("doorOrder:v01:depositError:edit")
	@RequestMapping(value = "reversal")
	public synchronized String reversal(DepositError depositError, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			//判断存款差错是否已经结算  若已结算  不允许冲正操作
			List<DoorCenterAccountsMain> list = centerAccountsMainService.checkDepositErrorAccount(depositError.getOrderId(),false);
			if(!Collections3.isEmpty(list) && DoorOrderConstant.StatusType.CREATE.equals(list.get(0).getBusinessStatus())){
				//该存款差错已被结算或处理  
				message = msg.getMessage("message.E7501", new String[] {}, locale);
				addMessage(redirectAttributes, message);
			}else{
				epositErrorService.reversal(depositError);
				addMessage(redirectAttributes, "冲正成功");
			}
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
		}
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/depositError/?repage";
	}

	/**
	 * 返回上一级页面
	 * 
	 * @author ZXK
	 * @version 2019年7月17日
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/doorOrder/v01/depositError/list?repage";
	}

	/**
	 * 判断未冲正的带差错存款单号是否存在
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "isOrderExists")
	public boolean isOrderExists(@RequestParam(required = true) String orderId) {
		return epositErrorService.isOrderExists(orderId);
	}

	/**
	 * 根据单号去查找对应单号的门店，保证二者一致性
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getDoorIdByOrderId")
	public String getDoorIdByOrderId(@RequestParam(required = true) String orderId) {
		return epositErrorService.getDoorIdByOrderId(orderId);
	}

	/**
	 * 在单号未冲正的情况下保证该单号下仅有一条差错记录处于登记状态
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getLoginCount")
	public Integer getLoginCount(@RequestParam(required = true) String orderId) {
		return epositErrorService.getLoginCount(orderId);
	}

	/**
	 * 登记差错时，短款金额不能多于存款金额
	 * 
	 * @author GJ
	 * @version 2019年10月24日
	 * @param orderId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "isMoreThanSave")
	public BigDecimal isMoreThanSave(@RequestParam(required = true) String orderId) {
		return epositErrorService.isMoreThanSave(orderId);
	}
}
