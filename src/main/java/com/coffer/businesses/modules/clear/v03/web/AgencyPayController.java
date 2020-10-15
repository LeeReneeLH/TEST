package com.coffer.businesses.modules.clear.v03.web;

import java.math.BigDecimal;
import java.util.Date;
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

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClOutDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.service.AgencyPayService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 代理上缴Controller
 * 
 * @author sg
 * @version 2017-08-29
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/agencyPayCtrl")
public class AgencyPayController extends BaseController {

	/** 调拨用Service */
	@Autowired
	private AgencyPayService agencyPayService;

	/** 打印 */
	@Autowired
	private OfficeDao officeDao;

	/**
	 * 根据流水单号，取得代理上缴信息
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param 取款单号(outNo)
	 * @return 代理上缴信息
	 */
	@ModelAttribute
	public ClOutMain get(@RequestParam(required = false) String outNo) {
		ClOutMain entity = null;
		if (StringUtils.isNotBlank(outNo)) {
			entity = agencyPayService.get(outNo);
		}
		if (entity == null) {
			entity = new ClOutMain();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param AgencyPayMain
	 *            代理上缴信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 代理上缴信息列表页面
	 */
	@RequiresPermissions("clear:agencyPayCtrl:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClOutMain agencyPayMain, HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		if (agencyPayMain.getCreateTimeStart() == null) {
			agencyPayMain.setCreateTimeStart(new Date());
		}
		if (agencyPayMain.getCreateTimeEnd() == null) {
			agencyPayMain.setCreateTimeEnd(new Date());
		}
		/* end */
		// 查询条件： 开始时间
		if (agencyPayMain.getCreateTimeStart() != null) {
			agencyPayMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(agencyPayMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (agencyPayMain.getCreateTimeEnd() != null) {
			agencyPayMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(agencyPayMain.getCreateTimeEnd())));
		}
		agencyPayMain.setBusType(ClearConstant.BusinessType.AGENCY_PAY);
		// 查询现金商行交款信息
		Page<ClOutMain> page = agencyPayService.findPage(new Page<ClOutMain>(request, response), agencyPayMain);
		model.addAttribute("agencyPayMain", agencyPayMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/agencyPay/agencyPayList";
	}

	/**
	 * 跳转到登记页面
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param AgencyPayMain
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("clear:agencyPayCtrl:add")
	@RequestMapping(value = "form")
	public String form(ClOutMain agencyPayMain, Model model) {

		/* 判断是否通过保存登记后跳转到登记页面 修改人：wxz 修改时间：2017-10-26 begin */
		if (!"".equals(agencyPayMain.getOutNo()) && agencyPayMain.getOutNo() != null) {
			model.addAttribute("outNo", agencyPayMain.getOutNo());
			ClOutMain clOutMainForm = new ClOutMain();

			// 面值列表数据的取得
			clOutMainForm.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("agencyPayMain", clOutMainForm);
		} else {
			// 面值列表数据的取得
			agencyPayMain.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("agencyPayMain", agencyPayMain);
		}
		/* end */

		// 交接人员
		model.addAttribute("transManType", Global.getConfig(Constant.CONNECT_PERSON_TYPE));
		// 复核人员
		model.addAttribute("checkManType", Global.getConfig(Constant.COMPLEX_PERSON_TYPE));
		return "modules/clear/v03/agencyPay/agencyPayForm";
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param AgencyPayMain
	 *            代理上缴信息
	 * @param model
	 * @return 查看页面
	 */
	@RequestMapping(value = "view")
	public String view(ClOutMain clOutMain, Model model) {

		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");
		denomCtrl.setColumnName1("countWzq");
		denomCtrl.setColumnName2("countCsq");
		denomCtrl.setColumnName3("countYqf");
		denomCtrl.setColumnName4("countAtm");
		clOutMain.setDenominationList(
				ClearCommonUtils.getDenominationAgencyPayCtrlList(clOutMain.getClOutDetailList(), denomCtrl));
		model.addAttribute(clOutMain);
		return "modules/clear/v03/agencyPay/agencyPayView";
	}

	/**
	 * 保存登记信息
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param AgencyPayMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	// @RequiresPermissions("clear:v03:bankGetCtrl:edit")
	@RequestMapping(value = "save")
	public String save(ClOutMain agencyPayMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";

		try {
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			agencyPayMain.setOffice(userInfo.getOffice());
			/* end */
			// 保存主表及明细表
			agencyPayService.save(agencyPayMain);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			/* 清空单号 修改人：wxz 修改时间：2017-10-26 begin */
			agencyPayMain.setOutNo(null);
			/* end */
			return form(agencyPayMain, model);
		}
		// message.I7011=上缴单号：{0}保存成功！
		message = msg.getMessage("message.I7011", new String[] { agencyPayMain.getOutNo() }, locale);
		addMessage(redirectAttributes, message);
		/* 通过保存登记后跳转到登记页面,将业务流水传到form页面 修改人：wxz 修改时间：2017-10-26 begin */
		return "redirect:" + adminPath + "/clear/v03/agencyPayCtrl/form?outNo=" + agencyPayMain.getOutNo();
		/* end */
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param AgencyPayMain
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(ClOutMain agencyPayMain, SessionStatus status) {
		return "redirect:" + adminPath + "/clear/v03/agencyPayCtrl/list?repage";
	}

	/**
	 * 冲正登记信息
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param AgencyPayMain
	 *            代理上缴信息
	 * @param redirectAttributes
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:agencyPayCtrl:reverse")
	@RequestMapping(value = "reverse", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String reverse(HttpServletRequest request, ClOutMain agencyPayMain, RedirectAttributes redirectAttributes) {
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
				agencyPayMain.setOutNo(outNo);
				ClOutMain clOutMain = agencyPayService.get(agencyPayMain);
				clOutMain.setAuthorizeBy(user);
				clOutMain.setAuthorizeDate(new Date());
				clOutMain.setAuthorizeReason(authorizeReason);
				/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
				User userInfo = UserUtils.getUser();
				clOutMain.setOffice(userInfo.getOffice());
				/* end */
				// 进行冲正操作
				agencyPayService.reverse(clOutMain);
				// 弹出冲正成功信息
				message = msg.getMessage("message.I7012", new String[] { agencyPayMain.getOutNo() }, locale);
			}
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			return message;
		}
		addMessage(redirectAttributes, message);
		return "success";
	}

	/*
	 * //@RequiresPermissions("clear:v03:bankGetCtrl:edit")
	 * 
	 * @RequestMapping(value = "delete") public String delete(ClOutMain
	 * clOutMain, RedirectAttributes redirectAttributes) {
	 * clOutMainService.delete(clOutMain); addMessage(redirectAttributes,
	 * "删除商行取款成功"); return "redirect:"+adminPath+"/clear/v03/clOutMain/?repage";
	 * }
	 */

	/**
	 * 将数字金额转换为大写金额
	 * 
	 * @author sg
	 * @version 2017年8月29日
	 * @param amount
	 *            数字金额
	 * @return 大写金额
	 */
	@RequestMapping(value = "/changRMBAmountToBig")
	@ResponseBody
	public String changRMBAmountToBig(@RequestParam(value = "amount", required = true) String amount) {
		Map<String, String> rtnMap = Maps.newHashMap();
		double dAmount = Double.parseDouble(amount);
		String strBigAmount = NumToRMB.changeToBig(dAmount);
		rtnMap.put("bigAmount", strBigAmount);
		return gson.toJson(rtnMap);
	}

	/**
	 * 打印代理上缴明细
	 * 
	 * @author sg
	 * @version 2016年09月06日
	 * 
	 * 
	 * @param outNo
	 *            业务流水
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:agencyPayCtrl:print")
	@RequestMapping(value = "/printAgencyPayDetail")
	public String printAgencyPayDetail(@RequestParam(value = "outNo", required = true) String outNo, Model model) {

		ClOutMain clOutMain = agencyPayService.get(outNo);
		// 转成中文大写
		String strBigAmount = NumToRMB.changeToBig(clOutMain.getOutAmount().doubleValue());
		// 计算行数
		int count = 0;
		List<ClOutDetail> clOutDetailList = Lists.newArrayList();
		for (ClOutDetail clOutDetail : clOutMain.getClOutDetailList()) {

			// 求平均值
			BigDecimal avg = clOutDetail.getTotalAmt().divide(new BigDecimal(clOutDetail.getTotalCount()));

			// 判断完整券的数量是否为空
			if (!"".equals(clOutDetail.getCountWzq()) && clOutDetail.getCountWzq() != null) {
				String valueWzq = avg.multiply(new BigDecimal(clOutDetail.getCountWzq())).toString();
				clOutDetail.setMoneyWzq(valueWzq);
				ClOutDetail clOutDetails = new ClOutDetail();
				// 设置未清分的捆数
				clOutDetails.setCountWzq(clOutDetail.getCountWzq());
				// 设置未清分的面值
				clOutDetails.setDenomination(clOutDetail.getDenomination());
				// 设置未清分的金额
				clOutDetails.setMoneyWzq(valueWzq);
				// 设置币种
				clOutDetails.setCurrency(clOutDetail.getCurrency());
				clOutDetailList.add(clOutDetails);
				count++;
			}
			// 判断残损券的数量是否为空
			if (!"".equals(clOutDetail.getCountCsq()) && clOutDetail.getCountCsq() != null) {
				String valueCsq = avg.multiply(new BigDecimal(clOutDetail.getCountCsq())).toString();
				clOutDetail.setMoneyCsq(valueCsq);
				ClOutDetail clOutDetails = new ClOutDetail();
				// 设置未清分的捆数
				clOutDetails.setCountCsq(clOutDetail.getCountCsq());
				// 设置未清分的面值
				clOutDetails.setDenomination(clOutDetail.getDenomination());
				// 设置未清分的金额
				clOutDetails.setMoneyCsq(valueCsq);
				// 设置币种
				clOutDetails.setCurrency(clOutDetail.getCurrency());
				clOutDetailList.add(clOutDetails);
				count++;
			}
			// 判断已清分的数量是否为空
			if (!"".equals(clOutDetail.getCountYqf()) && clOutDetail.getCountYqf() != null) {
				String valueYqf = avg.multiply(new BigDecimal(clOutDetail.getCountYqf())).toString();
				clOutDetail.setMoneyYqf(valueYqf);
				ClOutDetail clOutDetails = new ClOutDetail();
				// 设置未清分的捆数
				clOutDetails.setCountYqf(clOutDetail.getCountYqf());
				// 设置未清分的面值
				clOutDetails.setDenomination(clOutDetail.getDenomination());
				// 设置未清分的金额
				clOutDetails.setMoneyYqf(valueYqf);
				// 设置币种
				clOutDetails.setCurrency(clOutDetail.getCurrency());
				clOutDetailList.add(clOutDetails);
				count++;
			}
			// 判断ATM的数量是否为空
			if (!"".equals(clOutDetail.getCountAtm()) && clOutDetail.getCountAtm() != null) {
				String valueAtm = avg.multiply(new BigDecimal(clOutDetail.getCountAtm())).toString();
				clOutDetail.setMoneyAtm(valueAtm);
				ClOutDetail clOutDetails = new ClOutDetail();
				// 设置未清分的捆数
				clOutDetails.setCountAtm(clOutDetail.getCountAtm());
				// 设置未清分的面值
				clOutDetails.setDenomination(clOutDetail.getDenomination());
				// 设置未清分的金额
				clOutDetails.setMoneyAtm(valueAtm);
				// 设置币种
				clOutDetails.setCurrency(clOutDetail.getCurrency());
				clOutDetailList.add(clOutDetails);
				count++;
			}

		}
		// 获取当前登录人所在的机构
		User user = UserUtils.getUser();
		// 获取收款单位

		model.addAttribute("users",
				officeDao.get(officeDao.get(clOutMain.getrOffice().getId()).getParentId()).getName());
		model.addAttribute("user", user.getOffice().getName());
		model.addAttribute("strBigAmount", strBigAmount);
		model.addAttribute("clOutMain", clOutMain);
		model.addAttribute("clOutDetailList", clOutDetailList);
		// 获取最大页数
		int size = 0;
		if (count % 10 == 0) {
			size = count / 10;
		} else {
			size = count / 10 + 1;
		}
		model.addAttribute("size", size);

		// 打印代理上缴明细
		return "modules/clear/v03/agencyPay/printAgencyPayDetail";
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
		String message = msg.getMessage("message.I7012", new String[] { clearId }, locale);
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