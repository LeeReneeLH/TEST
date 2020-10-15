package com.coffer.businesses.modules.clear.v03.web;

import java.math.BigDecimal;
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

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClOutDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClOutMain;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.service.BankGetService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
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
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 商行取款Controller
 * 
 * @author wxz
 * @version 2017-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/bankGetCtrl")
public class BankGetController extends BaseController {

	@Autowired
	private BankGetService bankGetService;

	/** 根据选择的银行(客户名称)，获取银行的交接员 */
	@Autowired
	private StoEscortInfoService stoEscortInfoService;


	/**
	 * 根据流水单号，取得商行取款信息
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param 取款单号(outNo)
	 * @return 商行取款信息
	 */
	@ModelAttribute
	public ClOutMain get(@RequestParam(required = false) String outNo) {
		ClOutMain entity = null;
		if (StringUtils.isNotBlank(outNo)) {
			entity = bankGetService.get(outNo);
		}
		if (entity == null) {
			entity = new ClOutMain();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param ClOutMain
	 *            商行取款信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 商行取款信息列表页面
	 */
	@RequiresPermissions("clear:bankGetCtrl:view")
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

		/* 设置业务类型为商行取款 修改人：wxz 修改时间：2017-8-29 begin */
		clOutMain.setBusType(ClearConstant.BusinessType.BANK_GET);
		/* end */
		// 查询现金商行交款信息
		Page<ClOutMain> page = bankGetService.findPage(new Page<ClOutMain>(request, response), clOutMain);
		model.addAttribute("clOutMain", clOutMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/bankGet/bankGetList";
	}

	/**
	 * 跳转到登记页面
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param ClOutMain
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("clear:bankGetCtrl:add")
	@RequestMapping(value = "form")
	public String form(ClOutMain clOutMain, Model model) {
		// 判断是否保存后跳转到登记页面
		if (!"".equals(clOutMain.getOutNo()) && clOutMain.getOutNo() != null) {
			model.addAttribute("outNo", clOutMain.getOutNo());
			ClOutMain clOutMainForm = new ClOutMain();
			// 面值列表数据的取得
			clOutMainForm.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("clOutMain", clOutMainForm);
		} else {
			// 面值列表数据的取得
			clOutMain.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("clOutMain", clOutMain);
		}
		List<String> list = Lists.newArrayList();
		model.addAttribute("officeId", list);
		// 交接人员
		model.addAttribute("transManType", Global.getConfig(Constant.CONNECT_PERSON_TYPE));
		// 复核人员
		model.addAttribute("checkManType", Global.getConfig(Constant.COMPLEX_PERSON_TYPE));
		return "modules/clear/v03/bankGet/bankGetForm";
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param ClOutMain
	 *            商行取款信息
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("clear:bankGetCtrl:view")
	@RequestMapping(value = "view")
	public String view(ClOutMain clOutMain, Model model) {
		// 交接人员及复核人员的取得（清分中心操作员）
		/*
		 * StoEscortInfo condition = new StoEscortInfo();
		 * condition.setEscortType(String.valueOf(Constant.SysUserType.
		 * CLEARING_CENTER_OPT)); List<StoEscortInfo> list =
		 * StoreCommonUtils.getStoEscortinfoList(condition);
		 */

		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");
		denomCtrl.setColumnName1("countDqf");
		denomCtrl.setColumnName2("countYqf");
		denomCtrl.setColumnName3("countAtm");
		clOutMain.setDenominationList(
				ClearCommonUtils.getDenominationOutList(clOutMain.getClOutDetailList(), denomCtrl));
		/* model.addAttribute("handUsers", list); */
		model.addAttribute(clOutMain);
		return "modules/clear/v03/bankGet/bankGetView";
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param ClOutMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:bankGetCtrl:add")
	@RequestMapping(value = "save")
	public String save(ClOutMain clOutMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			clOutMain.setOffice(userInfo.getOffice());
			/* end */
			// 保存主表及明细表
			bankGetService.save(clOutMain);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			// 清空单号
			clOutMain.setOutNo(null);
			return form(clOutMain, model);
		}
		// message.I7009=出库单号：{0}保存成功！
		message = msg.getMessage("message.I7009", new String[] { clOutMain.getOutNo() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/clear/v03/bankGetCtrl/form?outNo=" + clOutMain.getOutNo();
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param ClOutMain
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(ClOutMain clOutMain, SessionStatus status) {
		return "redirect:" + adminPath + "/clear/v03/bankGetCtrl/list?repage";
	}

	/**
	 * 冲正登记信息
	 * 
	 * @author wxz
	 * @version 2017年8月24日
	 * @param ClOutMain
	 *            商行取款信息
	 * @param redirectAttributes
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:bankGetCtrl:reverse")
	@RequestMapping(value = "reverse", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String reverse(HttpServletRequest request, ClOutMain clOutMain, RedirectAttributes redirectAttributes) {
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
				ClOutMain clOut = bankGetService.get(clOutMain);
				clOut.setAuthorizeBy(user);
				clOut.setAuthorizeDate(new Date());
				clOut.setAuthorizeReason(authorizeReason);
				/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
				User userInfo = UserUtils.getUser();
				clOut.setOffice(userInfo.getOffice());
				/* end */
				// 进行冲正操作
				bankGetService.reverse(clOut);
				// 弹出冲正成功信息
				message = msg.getMessage("message.I7010", new String[] { clOutMain.getOutNo() }, locale);
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
	 * @author wxz
	 * @version 2017年8月24日
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
	 * 商行取款打印明细
	 * 
	 * @author wxz
	 * @version 2017年9月6日
	 * 
	 * 
	 * @param outNo
	 *            流水单号
	 * @return 列表页面
	 */
	@RequestMapping(value = "/printBankGet")
	public String printBankGet(@RequestParam(value = "outNo", required = true) String outNo, Model model) {
		ClOutMain clOutMain = bankGetService.get(outNo);
		// 计算行数
		int a = 0;
		List<ClOutDetail> clOutDetailList = Lists.newArrayList();
		for (ClOutDetail clOutDetail : clOutMain.getClOutDetailList()) {
			// 求平均值
			BigDecimal avg = clOutDetail.getTotalAmt().divide(new BigDecimal(clOutDetail.getTotalCount()));

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
				a++;
			}

			// 判断待清分的数量是否为空
			if (!"".equals(clOutDetail.getCountDqf()) && clOutDetail.getCountDqf() != null) {
				String valueDqf = avg.multiply(new BigDecimal(clOutDetail.getCountDqf())).toString();
				clOutDetail.setMoneyDqf(valueDqf);
				ClOutDetail clOutDetails = new ClOutDetail();
				// 设置未清分的捆数
				clOutDetails.setCountDqf(clOutDetail.getCountDqf());
				// 设置未清分的面值
				clOutDetails.setDenomination(clOutDetail.getDenomination());
				// 设置未清分的金额
				clOutDetails.setMoneyDqf(valueDqf);
				// 设置币种
				clOutDetails.setCurrency(clOutDetail.getCurrency());
				clOutDetailList.add(clOutDetails);
				a++;
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
				a++;
			}

		}
		// 获取当前登陆用户所在机构
		model.addAttribute("officeName", UserUtils.getUser().getOffice().getName());
		model.addAttribute("clOutMain", clOutMain);
		model.addAttribute("clOutDetailList", clOutDetailList);
		// 获取最大页数
		int size = 0;
		if (a % 10 == 0) {
			size = a / 10;
		} else {
			size = a / 10 + 1;
		}
		model.addAttribute("size", size);
		// 打印审批明细
		return "modules/clear/v03/bankGet/printBankGet";
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
	@RequestMapping(value = "changeBankConnect")
	@ResponseBody
	public String changeBankConnect(String param, Model model, RedirectAttributes redirectAttributes) {
		param = param.replace("&quot;", "");
		List<StoEscortInfo> list = null;
		List<Map<String, Object>> dataList = Lists.newArrayList();
		/*
		 * Map<String, Object> map1 = Maps.newHashMap(); map1.put("label", "请选
		 * 择"); map1.put("id", ""); dataList.add(map1);
		 */
		Office office = StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId());
		if (ClearConstant.JoinPeopleOpen.JOINPEOPLEOPEN_FALSE.equals(office.getJoinManSwitch())
				|| (StringUtils.isEmpty(office.getJoinManSwitch()))) {
			list = StoreCommonUtils.getUsersByTypeAndOffice(Global.getConfig("clear.bankJoinMan.businessType"),
					UserUtils.getUser().getOffice().getId());
		} else {
			// 查询通知
			if (!("".equals(param)) && param != null) {
				list = stoEscortInfoService.findBankUserList(param);
			}
		}
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

	/**
	 * 验证流水日期是否可以冲正
	 * 
	 * @author qph
	 * @version 2018年6月21日
	 * @param clInMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "checkDate")
	@ResponseBody
	public String checkDate(ClOutMain clOutMain, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		if (DateUtils.formatDate(clOutMain.getUpdateDate()).equals(DateUtils.formatDate(new Date()))) {
			// 验证通过，可以冲正
			map.put("result", "success");
		} else {
			// 非本日流水，不可冲正
			map.put("result", "error");
		}
		return gson.toJson(map);
	}
}