package com.coffer.businesses.modules.clear.v03.web;

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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.ClearConfirm;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.service.ClearConfirmService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 清分接收Controller
 * @author wanglin
 * @version 2017-02-13
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/clearConfirm")
public class ClearConfirmController extends BaseController {

	@Autowired
	private ClearConfirmService clearConfirmService;
	
	@ModelAttribute
	public ClearConfirm get(@RequestParam(required=false) String inNo) {
		ClearConfirm entity = null;
		if (StringUtils.isNotBlank(inNo)){
			entity = clearConfirmService.get(inNo);
		}
		if (entity == null){
			entity = new ClearConfirm();
		}
		return entity;
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param ClearConfirm
	 * @return
	 */
	@RequiresPermissions("clear:clearConfirm:view")
	@RequestMapping(value = {"list", ""})
	public String list(ClearConfirm clearConfirm, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		if (clearConfirm.getCreateTimeStart() == null) {
			clearConfirm.setCreateTimeStart(DateUtils.addMonths(new Date(), -1));
		}

		if (clearConfirm.getCreateTimeEnd() == null) {
			clearConfirm.setCreateTimeEnd(new Date());
		}

		// 查询条件： 开始时间
		if (clearConfirm.getCreateTimeStart() != null) {
			clearConfirm.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clearConfirm.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clearConfirm.getCreateTimeEnd() != null) {
			clearConfirm
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearConfirm.getCreateTimeEnd())));
		}
		
		
		Page<ClearConfirm> page = clearConfirmService.findPageList(new Page<ClearConfirm>(request, response), clearConfirm); 
		model.addAttribute("page", page);
		return "modules/clear/v03/orderClear/clearConfirmList";
		
	}

	

	/**
	 * 跳转到查看页面
	 * 
	 * @author wanglin
	 * @version 2017年7月13日
	 * @param clearConfirm
	 *            清分接收信息
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("clear:clearConfirm:view")
	@RequestMapping(value = "view")
	public String view(ClearConfirm clearConfirm, Model model) {
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");

		denomCtrl.setColumnName1("countDqf");
		denomCtrl.setColumnName2("countYqf");
		clearConfirm.setDenominationList(
				ClearCommonUtils.getDenominationList(clearConfirm.getOrderClearDetailList(), denomCtrl));
		model.addAttribute("clearConfirmInfo", clearConfirm);
		model.addAttribute("formType", "1");			//页面区分：1查看
		return "modules/clear/v03/orderClear/orderClearConfirm";
	}
	
	
	/**
	 * 跳转到接收页面
	 * 
	 * @author wanglin
	 * @version 2017年7月13日
	 * @param orderClearInfo
	 *            清分接收信息
	 * @param model
	 * @return 接收页面
	 */
	@RequiresPermissions("clear:clearConfirm:edit")
	@RequestMapping(value = "form")
	public String confirm(ClearConfirm clearConfirm, Model model) {
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");

		denomCtrl.setColumnName1("countDqf");
		denomCtrl.setColumnName2("countYqf");
		clearConfirm.setDenominationList(
				ClearCommonUtils.getDenominationList(clearConfirm.getOrderClearDetailList(), denomCtrl));
		model.addAttribute("clearConfirmInfo", clearConfirm);
		model.addAttribute("formType", "2");			//页面区分：2接收
		return "modules/clear/v03/orderClear/orderClearConfirm";
	}

	/**
	 * 接收处理
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param OrderClearMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:clearConfirm:edit")
	@RequestMapping(value = "confirm")
	public String confirm(ClearConfirm clearConfirm, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 进行接收操作
		clearConfirmService.confirm(clearConfirm);
		// message.I7014=预约单号：{0}接收成功！
		message = msg.getMessage("message.I7014", new String[] { clearConfirm.getInNo() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/clear/v03/clearConfirm/list";
	}
	
	
	/**
	 * 返回到列表页面
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param orderClearInfo
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(ClearConfirm clearConfirm, SessionStatus status) {
		return "redirect:" + adminPath + "/clear/v03/clearConfirm/list?repage";
	}

	
}