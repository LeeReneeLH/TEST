package com.coffer.businesses.modules.clear.v03.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.entity.DenominationInfo;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearDetail;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.clear.v03.service.OrderClearService;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 交接管理：预约清分Controller
 * 
 * @author wanglin
 * @version 2017年7月6日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/orderClear")
public class OrderClearController extends BaseController {

	/** 调拨用Service */
	@Autowired
	private OrderClearService orderClearService;


	/**
	 * 根据流水单号，取得预约清分信息
	 * 
	 * @author WangLin
	 * @version 2017年7月6日
	 * @param 交款单号(inNo)
	 * @return 预约清分信息
	 */
	@ModelAttribute
	public OrderClearMain get(@RequestParam(required = false) String inNo) {
		OrderClearMain entity = null;
		if (StringUtils.isNotBlank(inNo)) {
			// 根据交款单号取得单条主表信息
			entity = orderClearService.get(inNo);
			// 面值列表数据的取得
			entity.setDenominationList(ClearCommonUtils.getDenominationList());
		}
		if (entity == null) {
			entity = new OrderClearMain();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author wanglin
	 * @version 2017年7月6日
	 * @param orderClearInfo
	 *            预约清分信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 预约清分信息列表页面
	 */
	@RequiresPermissions("clear:orderClear:view")
	@RequestMapping(value = { "list", "" })
	public String list(OrderClearMain orderClearInfo, HttpServletRequest request, HttpServletResponse response, Model model) {

		if (orderClearInfo.getCreateTimeStart() == null) {
			orderClearInfo.setCreateTimeStart(DateUtils.addMonths(new Date(), -1));
		}
		if (orderClearInfo.getCreateTimeEnd() == null) {
			orderClearInfo.setCreateTimeEnd(new Date());
		}

		// 查询条件： 开始时间
		if (orderClearInfo.getCreateTimeStart() != null) {
			orderClearInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(orderClearInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (orderClearInfo.getCreateTimeEnd() != null) {
			orderClearInfo
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(orderClearInfo.getCreateTimeEnd())));
		}
		
		// 查询现金预约清分信息
		Page<OrderClearMain> page = orderClearService.findPageList(new Page<OrderClearMain>(request, response), orderClearInfo);

		model.addAttribute("orderClearInfo", orderClearInfo);
		model.addAttribute("page", page);

		return "modules/clear/v03/orderClear/orderClearList";
	}

	/**
	 * 跳转到登记页面
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param orderClearInfo
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("clear:orderClear:edit")
	@RequestMapping(value = "form")
	public String form(OrderClearMain orderClearInfo, Model model) {
		
		if (StringUtils.isBlank(orderClearInfo.getInNo())) {
			// 面值列表数据的取得
			orderClearInfo.setDenominationList(ClearCommonUtils.getDenominationList());
		}else{
			// 面值列表数据的取得
			DenominationCtrl denomCtrl = new DenominationCtrl();
			denomCtrl.setMoneyKeyName("denomination");

			denomCtrl.setColumnName1("countDqf");
			denomCtrl.setColumnName2("countYqf");
			orderClearInfo.setDenominationList(
					ClearCommonUtils.getDenominationList(orderClearInfo.getOrderClearDetailList(), denomCtrl));
			
		}
		model.addAttribute("orderClearInfo", orderClearInfo);
		
		//当日所属商行面值合计
		List<DenominationInfo> totalDenomList = dayDenomSum(orderClearInfo.getRegisterOffice(),orderClearInfo.getRegisterDate());
		model.addAttribute("totalDenomList", totalDenomList);
		
		return "modules/clear/v03/orderClear/orderClearForm";
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @author wanglin
	 * @version 2017年7月13日
	 * @param orderClearInfo
	 *            预约清分信息
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("clear:orderClear:view")
	@RequestMapping(value = "view")
	public String view(OrderClearMain orderClearInfo, Model model) {
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");

		denomCtrl.setColumnName1("countDqf");
		denomCtrl.setColumnName2("countYqf");
		orderClearInfo.setDenominationList(
				ClearCommonUtils.getDenominationList(orderClearInfo.getOrderClearDetailList(), denomCtrl));
		model.addAttribute("orderClearInfo", orderClearInfo);

		//当日所属商行面值合计
		List<DenominationInfo> totalDenomList = dayDenomSum(orderClearInfo.getRegisterOffice(),orderClearInfo.getRegisterDate());
		model.addAttribute("totalDenomList", totalDenomList);

		return "modules/clear/v03/orderClear/orderClearView";
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param OrderClearMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:orderClear:edit")
	@RequestMapping(value = "save")
	public String save(OrderClearMain orderClearInfo, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			orderClearInfo.setMethod(WeChatConstant.MethodType.METHOD_PC);
			// 保存主表及明细表
			orderClearService.save(orderClearInfo);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			orderClearInfo.setInNo(null);
			return form(orderClearInfo, model);
		}
		// message.I7001=预约单号：{0}保存成功！
		message = msg.getMessage("message.I7013", new String[] { orderClearInfo.getInNo() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/clear/v03/orderClear/list";
	}

	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          删除记录
	 * @param StoreOffice
	 * @return
	 */
	@RequiresPermissions("clear:orderClear:edit")
	@RequestMapping(value = "delete")
	public String delete(OrderClearMain orderClearInfo, RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		orderClearInfo.preUpdate();
		orderClearService.delete(orderClearInfo);
		//删除商户成功
		message = msg.getMessage("message.I0002", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/clear/v03/orderClear/list";
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
	public String back(OrderClearMain orderClearInfo, SessionStatus status) {
		return "redirect:" + adminPath + "/clear/v03/orderClear/list?repage";
	}



	/**
	 * 将数字金额转换为大写金额
	 * 
	 * @author wanglin
	 * @version 2016年5月31日
	 * @param amount
	 *            数字金额
	 * @return 大写金额
	 */
	@RequestMapping(value = "/changRMBAmountToBig")
	@ResponseBody
	public String changRMBAmountToBig(@RequestParam(value = "amount", required = true) String amount) {

		Map<String, String> rtnMap = new HashMap<String, String>();
		double dAmount = Double.parseDouble(amount);
		String strBigAmount = NumToRMB.changeToBig(dAmount);
		rtnMap.put("bigAmount", strBigAmount);
		return gson.toJson(rtnMap);
	}

	/**
	 * 当日所属商行面值合计
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param orderClearInfo
	 * @param status
	 * @return list
	 */
	private List<DenominationInfo> dayDenomSum(String registerOffice,Date registerDate) {
		//当日预约金额合计
		OrderClearMain orderClearInfoTemp = new OrderClearMain();
		orderClearInfoTemp.setRegisterOffice(registerOffice);	//登记机构
		orderClearInfoTemp.setRegisterDate(registerDate);		//登记日
		// 查询现金预约清分信息
		List<OrderClearDetail> totalList = orderClearService.findListByOffice(orderClearInfoTemp);
		// 面值列表数据的取得
		DenominationCtrl denomSumCtrl = new DenominationCtrl();
		denomSumCtrl.setMoneyKeyName("denomination");
		denomSumCtrl.setColumnName1("countDqf");
		denomSumCtrl.setColumnName2("countYqf");
		List<DenominationInfo> totalDenomList = ClearCommonUtils.getDenominationList(totalList, denomSumCtrl);
		return totalDenomList;
	}

	
	
}
