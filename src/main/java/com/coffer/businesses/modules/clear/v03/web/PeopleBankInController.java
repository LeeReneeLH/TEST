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
import com.coffer.businesses.modules.clear.v03.service.PeopleBankInService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 交接管理：从人行复点入库Controller
 * 
 * @author wzj
 * @version 2017年9月4日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/peopleBankIn")
public class PeopleBankInController extends BaseController {

	/** 从人行复点入库Service */
	@Autowired
	private PeopleBankInService peopleBankInService;

	/**
	 * 根据流水单号，取得从人行复点入库信息
	 * 
	 * @author wzj
	 * @version 2017年9月4日
	 * @param 交款单号(outNo)
	 * @return 从人行复点入库信息
	 */
	@ModelAttribute
	public ClOutMain get(@RequestParam(required = false) String outNo) {
		ClOutMain entity = null;
		if (StringUtils.isNotBlank(outNo)) {
			// 根据交款单号取得单条主表信息
			entity = peopleBankInService.get(outNo);
		}
		if (entity == null) {
			entity = new ClOutMain();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author wzj
	 * @version 2017年9月4日
	 * @param clOutMain
	 *            从人行复点入库信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 从人行复点入库信息列表页面
	 */
	@RequiresPermissions("clear:peopleBankIn:view")
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

		// 设置业务类型为人民银行复点入库
		clOutMain.setBusType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
		// 查询从人行复点入库信息
		Page<ClOutMain> page = peopleBankInService.findPage(new Page<ClOutMain>(request, response), clOutMain);

		model.addAttribute("clOutMain", clOutMain);
		model.addAttribute("page", page);

		return "modules/clear/v03/PeopleBankIn/PeopleBankInList";
	}

	/**
	 * 跳转到登记页面
	 * 
	 * @author wzj
	 * @version 2017年9月4日
	 * @param clOutMain
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("clear:peopleBankIn:add")
	@RequestMapping(value = "form")
	public String form(ClOutMain clOutMain, Model model) {

		/* 判断是否通过保存登记后跳转到登记页面 修改人：wxz 修改时间：2017-10-26 begin */
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
		/* end */

		// 清分中心所属的人民银行的取得
		model.addAttribute("officeId", UserUtils.getUser().getOffice().getParentId());
		// 交接人员
		model.addAttribute("transManType", Global.getConfig(Constant.CONNECT_PERSON_TYPE));
		// 复核人员
		model.addAttribute("checkManType", Global.getConfig(Constant.COMPLEX_PERSON_TYPE));
		return "modules/clear/v03/PeopleBankIn/PeopleBankInForm";
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @author wzj
	 * @version 2017年9月4日
	 * @param clOutMain
	 *            从人行复点入库信息
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("clear:peopleBankIn:view")
	@RequestMapping(value = "view")
	public String view(ClOutMain clOutMain, Model model) {

		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		// 设置面值名称
		denomCtrl.setMoneyKeyName("denomination");
		// 设置待复点
		denomCtrl.setColumnName1("countDqf");
		// 设置已复点
		denomCtrl.setColumnName2("countYqf");
		// 设置总金额
		denomCtrl.setTotalAmtName("totalAmt");
		clOutMain.setDenominationList(ClearCommonUtils.getDenominationList(clOutMain.getClOutDetailList(), denomCtrl));
		model.addAttribute(clOutMain);
		return "modules/clear/v03/PeopleBankIn/PeopleBankInView";
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wzj
	 * @version 2017年9月4日
	 * @param clOutMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:peopleBankIn:add")
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
			peopleBankInService.save(clOutMain);
			// message = "出库单号："+clOutMain.getOutNo()+"保存成功！";
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			/* 清空单号 修改人：wxz 修改时间：2017-10-26 begin */
			clOutMain.setOutNo(null);
			/* end */
			return form(clOutMain, model);
			// return "modules/clear/v03/PeopleBankIn/PeopleBankInForm";
		}
		// message.I7009=出库单号：{0}保存成功！
		message = msg.getMessage("message.I7009", new String[] { clOutMain.getOutNo() }, locale);
		addMessage(redirectAttributes, message);
		/* 通过保存登记后跳转到登记页面,将业务流水传到form页面 修改人：wxz 修改时间：2017-10-26 begin */
		return "redirect:" + adminPath + "/clear/v03/peopleBankIn/form?outNo=" + clOutMain.getOutNo();
		/* end */
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author wzj
	 * @version 2017年9月4日
	 * @param clOutMain
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(ClOutMain clOutMain, SessionStatus status) {
		return "redirect:" + adminPath + "/clear/v03/peopleBankIn/list?repage";
	}

	/**
	 * 冲正登记信息
	 * 
	 * @author wzj
	 * @version 2017年9月4日
	 * @param clOutMain
	 *            从人行复点入库信息
	 * @param redirectAttributes
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:peopleBankIn:reverse")
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
				ClOutMain clOut = peopleBankInService.get(clOutMain);
				clOut.setAuthorizeBy(user);
				clOut.setAuthorizeDate(new Date());
				clOut.setAuthorizeReason(authorizeReason);
				/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
				User userInfo = UserUtils.getUser();
				clOut.setOffice(userInfo.getOffice());
				/* end */
				// 进行冲正操作
				peopleBankInService.reverse(clOut);
				// 弹出冲正成功信息
				message = msg.getMessage("message.I7010", new String[] { clOutMain.getOutNo() }, locale);
				// message = "出库单号：" + clOutMain.getOutNo() + "冲正成功！";
			}
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			return message;
		}
		addMessage(redirectAttributes, message);
		return "success";
	}

	/**
	 * 将数字金额转换为大写金额
	 * 
	 * @author wzj
	 * @version 2017年9月4日
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
	 * 从人行复点入库打印明细
	 * 
	 * @author wxz
	 * @version 2017年9月7日
	 * 
	 * 
	 * @param outNo
	 *            流水单号
	 * @return 列表页面
	 */
	@RequestMapping(value = "/printPeopleBankIn")
	public String printPeopleBankIn(@RequestParam(value = "outNo", required = true) String outNo, Model model) {

		ClOutMain clOutMain = peopleBankInService.get(outNo);

		// 计算行数
		int a = 0;
		List<ClOutDetail> clOutDetail = Lists.newArrayList();
		for (ClOutDetail bankPayDetail : clOutMain.getClOutDetailList()) {
			// 总数String类型转Long
			Long number = Long.valueOf(bankPayDetail.getTotalCount());
			// 求平均值
			BigDecimal avg = bankPayDetail.getTotalAmt().divide(new BigDecimal(number));

			// 判断已清分的数量是否为空
			if (!"".equals(bankPayDetail.getCountYqf()) && bankPayDetail.getCountYqf() != null) {
				Integer yqf = Integer.parseInt(bankPayDetail.getCountYqf());
				String valueYqf = avg.multiply(new BigDecimal(yqf)).toString();
				bankPayDetail.setMoneyYqf(valueYqf);
				ClOutDetail clOutDetails = new ClOutDetail();
				// 设置未清分的捆数
				clOutDetails.setCountYqf(bankPayDetail.getCountYqf());
				// 设置未清分的面值
				clOutDetails.setDenomination(bankPayDetail.getDenomination());
				// 设置未清分的金额
				clOutDetails.setMoneyYqf(valueYqf);
				// 设置币种
				clOutDetails.setCurrency(bankPayDetail.getCurrency());
				clOutDetail.add(clOutDetails);
				a++;
			}

			// 判断待清分的数量是否为空
			if (!"".equals(bankPayDetail.getCountDqf()) && bankPayDetail.getCountDqf() != null) {
				Integer dqf = Integer.parseInt(bankPayDetail.getCountDqf());
				String valueDqf = avg.multiply(new BigDecimal(dqf)).toString();
				bankPayDetail.setMoneyDqf(valueDqf);
				ClOutDetail clOutDetails = new ClOutDetail();
				// 设置未清分的捆数
				clOutDetails.setCountDqf(bankPayDetail.getCountDqf());
				// 设置未清分的面值
				clOutDetails.setDenomination(bankPayDetail.getDenomination());
				// 设置未清分的金额
				clOutDetails.setMoneyDqf(valueDqf);
				// 设置币种
				clOutDetails.setCurrency(bankPayDetail.getCurrency());
				clOutDetail.add(clOutDetails);
				a++;
			}
		}
		// 获取当前登陆用户所在机构
		model.addAttribute("officeName", UserUtils.getUser().getOffice().getName());

		model.addAttribute("clOutMain", clOutMain);
		model.addAttribute("clOutDetail", clOutDetail);
		// 获取最大页数
		int size = 0;
		if (a % 10 == 0) {
			size = a / 10;
		} else {
			size = a / 10 + 1;
		}
		model.addAttribute("size", size);

		// 打印审批明细
		return "modules/clear/v03/PeopleBankIn/printPeopleBankIn";
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
		/* 将入库单号改为出库单号 修改人:sg 修改日期:2017-12-12 begin */
		String message = msg.getMessage("message.I7010", new String[] { clearId }, locale);
		/* end */
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
