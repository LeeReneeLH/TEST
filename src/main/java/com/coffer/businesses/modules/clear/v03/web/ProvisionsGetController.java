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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.service.ProvisionsGetService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 备付金取回Controller
 * 
 * @author wzj
 * @version 2017-08-29
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/provisionsOut")
public class ProvisionsGetController extends BaseController {

	@Autowired
	private ProvisionsGetService provisionsGetService;

	/** 根据选择的银行(客户名称)，获取银行的交接员 */
	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	@ModelAttribute
	public ClOutMain get(@RequestParam(required = false) String outNo) {
		ClOutMain entity = null;
		if (StringUtils.isNotBlank(outNo)) {
			entity = provisionsGetService.get(outNo);
		}
		if (entity == null) {
			entity = new ClOutMain();
		}
		return entity;
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @author wzj
	 * @version 2017年8月30日
	 * @param clOutMain
	 *            备付金信息
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("v03:provisionsOut:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClOutMain clOutMain, HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		if (clOutMain.getCreateTimeStart() == null) {
			clOutMain.setCreateTimeStart(new Date());
		}
		if (clOutMain.getCreateTimeEnd() == null) {
			clOutMain.setCreateTimeEnd(new Date());
		}
		/* end */
		// 查询条件： 开始时间
		if (clOutMain.getCreateTimeStart() != null) {
			clOutMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clOutMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clOutMain.getCreateTimeEnd() != null) {
			clOutMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(clOutMain.getCreateTimeEnd())));
		}
		clOutMain.setBusType(ClearConstant.BusinessType.PROVISIONS_OUT);

		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		// User userInfo = UserUtils.getUser();
		// clOutMain.setOffice(userInfo.getOffice());
		/* end */

		Page<ClOutMain> page = provisionsGetService.findPage(new Page<ClOutMain>(request, response), clOutMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/provisionsGet/provisionsGetList";
	}

	/**
	 * 跳转到登记页面
	 * 
	 * @author wzj
	 * @version 2017年8月30日
	 * @param clOutMain
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("v03:provisionsOut:edit")
	@RequestMapping(value = "form")
	public String form(ClOutMain clOutMain, Model model) {
		
		/* 判断是否通过保存登记后跳转到登记页面 修改人：wxz 修改时间：2017-10-26 begin */
		if(!"".equals(clOutMain.getOutNo()) && clOutMain.getOutNo() != null){
			model.addAttribute("outNo",clOutMain.getOutNo());
			
			ClOutMain clOutMainForm = new ClOutMain();
			// 面值列表数据的取得
			clOutMainForm.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("clOutMain", clOutMainForm);
		} else {
			// 面值列表数据的取得
			clOutMain.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("clOutMain", clOutMain);
		}
		/* end */
		
		// 交接人员
		model.addAttribute("transManType", Global.getConfig(Constant.CLEAR_PROVISIONS_TYPE));
		// 复核人员
		model.addAttribute("checkManType", Global.getConfig(Constant.CONNECT_PERSON_TYPE));
		return "modules/clear/v03/provisionsGet/provisionsGetForm";
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wzj
	 * @version 2017年8月30日
	 * @param clOutMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequiresPermissions("v03:provisionsOut:edit")
	@RequestMapping(value = "save")
	public String save(ClOutMain clOutMain, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, clOutMain)) {
			return form(clOutMain, model);
		}
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			clOutMain.setOffice(userInfo.getOffice());
			/* end */
			provisionsGetService.save(clOutMain);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			/*	清空单号 修改人：wxz 修改时间：2017-10-26 begin	*/
			clOutMain.setOutNo(null);
			/* end */
			return form(clOutMain, model);
		}
		// 备付金取回保存成功!
		message = msg.getMessage("message.I7400", null, locale);
		addMessage(redirectAttributes, message);
		/* 通过保存登记后跳转到登记页面,将业务流水传到form页面   修改人：wxz 修改时间：2017-10-26 begin */
		return "redirect:" + adminPath + "/clear/v03/provisionsOut/form?outNo=" + clOutMain.getOutNo();
		/* end */
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @author wzj
	 * @version 2017年8月30日
	 * @param clOutMain
	 *            备付金信息
	 * @param model
	 * @return 查看页面
	 */
	@RequestMapping(value = "view")
	public String view(ClOutMain clOutMain, Model model) {

		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		// 设置面值名称
		denomCtrl.setMoneyKeyName("denomination");
		// 设置总数
		denomCtrl.setColumnName1("totalCount");
		// 设置总金额
		denomCtrl.setTotalAmtName("totalAmt");
		clOutMain.setDenominationList(
				ClearCommonUtils.getDenominationOutList(clOutMain.getClOutDetailList(), denomCtrl));
		model.addAttribute(clOutMain);
		return "modules/clear/v03/provisionsGet/provisionsGetView";
	}

	/**
	 * 将数字金额转换为大写金额
	 * 
	 * @author wzj
	 * @version 2017年8月30日
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
	 * 冲正登记信息
	 * 
	 * @author wzj
	 * @version 2017年8月30日
	 * @param clOutMain
	 *            备付金信息
	 * @param redirectAttributes
	 * @return 列表页面
	 */
	@RequestMapping(value = "reverse", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String reverse(HttpServletRequest request, ClOutMain clOutMain, Model model,
			RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";

		try {
			// 获取业务编号
			String outNo = request.getParameter("clearId");
			// 获取授权登录名
			String loginName = request.getParameter("authorizeLogin");
			// 获取授权登录密码
			String password = request.getParameter("authorizePass");
			// 获取授权理由
			String authorizeReason = request.getParameter("authorizeReason");
			User user = UserUtils.getByLoginName(loginName);
			// 用户名密码验证
			if (user == null || !user.getPassword().equals(SystemService.entryptPassword(password))) {
				throw new BusinessException("message.A1007", "", new String[] {});
				// 用户权限验证
			} else if (!(user.getUserType().equals(Global.getConfig("clear.userType.executive")))) {
				throw new BusinessException("message.A1008", "", new String[] {});
			} /* 修改冲正理由长度限制为非空验证 wzj 2017-11-15 begin */
			else if (StringUtils.isBlank(authorizeReason)) {
				throw new BusinessException("message.A1013", "", new String[] {});
				/* end */
			} else {
				clOutMain.setOutNo(outNo);
				ClOutMain clOut = provisionsGetService.get(clOutMain);
				clOut.setAuthorizeBy(user);
				clOut.setAuthorizeDate(new Date());
				clOut.setAuthorizeReason(authorizeReason);
				/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
				User userInfo = UserUtils.getUser();
				clOut.setOffice(userInfo.getOffice());
				/* end */
				provisionsGetService.reverse(clOut);
				// 弹出冲正成功信息
				message = msg.getMessage("message.I7401", null, locale);
			}
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			return message;
		}

		addMessage(redirectAttributes, message);
		return "success";
	}

	/**
	 * 打印备用金存取凭证
	 * 
	 * @author sg
	 * @version 2016年09月11日
	 * 
	 * 
	 * @param outNo
	 *            业务流水
	 * @return 列表页面
	 */
	// @RequiresPermissions("clear:provisionsOut:print")
	@RequestMapping(value = "/printProvisionsGetDetail")
	public String printProvisionsGetDetail(@RequestParam(value = "outNo", required = true) String outNo, Model model) {

		ClOutMain clOutMain = provisionsGetService.get(outNo);
		// 转成中文大写
		String strBigAmount = NumToRMB.changeToBig(clOutMain.getOutAmount().doubleValue());
		// 获取当前登录人所在的机构
		User user = UserUtils.getUser();
		model.addAttribute("user", user.getOffice().getName());
		model.addAttribute("strBigAmount", strBigAmount);
		model.addAttribute("clOutMain", clOutMain);

		// 打印代理上缴明细
		return "modules/clear/v03/provisionsGet/printProvisionsGetDetail";
	}

	/**
	 * 根据选择的银行(客户名称)，获取银行的交接员
	 * 
	 * @author wxz
	 * @param officeId
	 *            客户id(officeId)
	 * @version 2017-9-19
	 * @return
	 */
	/*
	 * @RequestMapping(value = "changeBankConnect")
	 * 
	 * @ResponseBody public List<StoEscortInfo> changeBankConnect(@RequestParam
	 * String officeId) { List<StoEscortInfo> list = null; if
	 * (!("".equals(officeId)) && officeId != null) { list =
	 * stoEscortInfoService.findBankUserList(officeId); } return list; }
	 */

	@RequestMapping(value = "changeBankConnect")
	@ResponseBody
	public String changeBankConnect(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "");
		List<StoEscortInfo> list = null;
		List<Map<String, Object>> dataList = Lists.newArrayList();
		/*
		 * Map<String, Object> map1 = Maps.newHashMap(); map1.put("label",
		 * "请选择"); map1.put("id", ""); dataList.add(map1);
		 */
		if (!("".equals(param)) && param != null) {
			list = stoEscortInfoService.findBankUserList(param);
			if (!Collections3.isEmpty(list)) {
				if (list != null) {
					for (StoEscortInfo item : list) {
						Map<String, Object> map = Maps.newHashMap();
						map.put("label", item.getEscortName());
						map.put("id", item.getId());
						dataList.add(map);
					}
				}
			}
		}

		return gson.toJson(dataList);
	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年10月9日 冲正成功跳转
	 * 
	 * @param clOutMain
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "reverseList")
	public String reverseList(ClOutMain clOutMain, HttpServletRequest request, HttpServletResponse response,
			Model model, RedirectAttributes redirectAttributes, @RequestParam(required = false) String clearId) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = msg.getMessage("message.I7010", new String[] { clearId }, locale);
		addMessage(model, message);
		return list(clOutMain, request, response, model);
	}

	/**
	 * @author qipeihong 跳转到显示冲正原因画面
	 * 
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "displayAuthorizeReason")
	public String displayAuthorizeReason(ClOutMain clOutMain, Model model) {
		model.addAttribute("authorizeParam", clOutMain);
		return "modules/clear/v03/authorize/showAuthorizeDetail";
	}

}