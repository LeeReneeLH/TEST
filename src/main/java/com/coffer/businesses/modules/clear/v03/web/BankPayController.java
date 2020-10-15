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
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClInDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClInMain;
import com.coffer.businesses.modules.clear.v03.entity.ClTaskMain;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.clear.v03.service.BankPayService;
import com.coffer.businesses.modules.clear.v03.service.ClTaskMainService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
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
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 交接管理：商行交款Controller
 * 
 * @author wanglin
 * @version 2017年7月6日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/bankPayCtrl")
public class BankPayController extends BaseController {

	/** 调拨用Service */
	@Autowired
	private BankPayService bankPayService;

	/** 打印 */
	@Autowired
	private OfficeDao officeDao;
	/** 根据选择的银行(客户名称)，获取银行的交接员 */
	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	@Autowired
	private ClTaskMainService clTaskMainService;

	/**
	 * 根据流水单号，取得商行交款信息
	 * 
	 * @author WangLin
	 * @version 2017年7月6日
	 * @param 交款单号(inNo)
	 * @return 商行交款信息
	 */
	@ModelAttribute
	public ClInMain get(@RequestParam(required = false) String inNo) {
		ClInMain entity = null;
		if (StringUtils.isNotBlank(inNo)) {
			// 根据交款单号取得单条主表信息
			entity = bankPayService.get(inNo);
		}
		if (entity == null) {
			entity = new ClInMain();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author wanglin
	 * @version 2017年7月6日
	 * @param bankPayInfo
	 *            商行交款信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 商行交款信息列表页面
	 */
	@RequiresPermissions("clear:bankPayCtrl:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClInMain bankPayInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-14 begin */
		if (bankPayInfo.getCreateTimeStart() == null) {
			bankPayInfo.setCreateTimeStart(new Date());
		}
		if (bankPayInfo.getCreateTimeEnd() == null) {
			bankPayInfo.setCreateTimeEnd(new Date());
		}
		/* end */
		// 查询条件： 开始时间
		if (bankPayInfo.getCreateTimeStart() != null) {
			bankPayInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(bankPayInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (bankPayInfo.getCreateTimeEnd() != null) {
			bankPayInfo
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(bankPayInfo.getCreateTimeEnd())));
		}
		/* 设置业务类型为商行交款 修改人：xl 修改时间：2017-8-29 begin */
		bankPayInfo.setBusType(ClearConstant.BusinessType.BANK_PAY);
		/* end */
		// 查询现金商行交款信息
		Page<ClInMain> page = bankPayService.findPage(new Page<ClInMain>(request, response), bankPayInfo);

		model.addAttribute("bankPayInfo", bankPayInfo);
		model.addAttribute("page", page);

		return "modules/clear/v03/bankPay/bankPayList";
	}

	/**
	 * 跳转到登记页面
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param bankPayInfo
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("clear:bankPayCtrl:add")
	@RequestMapping(value = "form")
	public String form(ClInMain bankPayInfo, Model model) {
		/* 判断是否通过保存登记后跳转到登记页面 修改人：wxz 修改时间：2017-10-26 begin */
		if (!"".equals(bankPayInfo.getInNo()) && bankPayInfo.getInNo() != null) {
			model.addAttribute("inNo", bankPayInfo.getInNo());
			ClInMain clInMainForm = new ClInMain();
			// 面值列表数据的取得
			clInMainForm.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("bankPayInfo", clInMainForm);
		} else {
			// 面值列表数据的取得
			bankPayInfo.setDenominationList(ClearCommonUtils.getDenominationList());
			model.addAttribute("bankPayInfo", bankPayInfo);
		}
		/* end */
		// 交接人员
		model.addAttribute("transManType", Global.getConfig(Constant.CONNECT_PERSON_TYPE));
		// 复核人员
		model.addAttribute("checkManType", Global.getConfig(Constant.COMPLEX_PERSON_TYPE));
		return "modules/clear/v03/bankPay/bankPayForm";
	}

	/**
	 * 跳转到查看页面
	 * 
	 * @author wanglin
	 * @version 2017年7月13日
	 * @param bankPayInfo
	 *            商行交款信息
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("clear:bankPayCtrl:view")
	@RequestMapping(value = "view")
	public String view(ClInMain bankPayInfo, Model model) {
		// 交接人员及复核人员的取得（清分中心操作员）
		StoEscortInfo condition = new StoEscortInfo();
		condition.setEscortType(String.valueOf(Constant.SysUserType.CLEARING_CENTER_OPT));
		List<StoEscortInfo> list = StoreCommonUtils.getStoEscortinfoList(condition);

		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		/* 修改属性名为denomination 修改人：wzj 修改时间：2017-8-29 begin */
		denomCtrl.setMoneyKeyName("denomination");
		/* end */
		denomCtrl.setColumnName1("countDqf");
		denomCtrl.setColumnName2("countYqf");
		bankPayInfo.setDenominationList(
				ClearCommonUtils.getDenominationList(bankPayInfo.getBankPayDetailList(), denomCtrl));
		model.addAttribute("handUsers", list);
		model.addAttribute("bankPayInfo", bankPayInfo);
		return "modules/clear/v03/bankPay/bankPayView";
	}

	/**
	 * 保存登记信息
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param ClInMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:bankPayCtrl:add")
	@RequestMapping(value = "save")
	public String save(ClInMain bankPayInfo, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 登陆用户
			User userInfo = UserUtils.getUser();
			bankPayInfo.setOffice(userInfo.getOffice());
			// 保存主表及明细表
			bankPayService.save(bankPayInfo);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			/* 清空单号 修改人：wxz 修改时间：2017-10-26 begin */
			bankPayInfo.setInNo(null);
			/* end */
			return form(bankPayInfo, model);
		}
		// message.I7001=入库单号：{0}保存成功！
		message = msg.getMessage("message.I7001", new String[] { bankPayInfo.getInNo() }, locale);
		addMessage(redirectAttributes, message);
		/* 通过保存登记后跳转到登记页面,将业务流水传到form页面 修改人：wxz 修改时间：2017-10-26 begin */
		return "redirect:" + adminPath + "/clear/v03/bankPayCtrl/form?inNo=" + bankPayInfo.getInNo();
		/* end */
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param bankPayInfo
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(ClInMain bankPayInfo, SessionStatus status) {
		return "redirect:" + adminPath + "/clear/v03/bankPayCtrl/list?repage";
	}

	/**
	 * 冲正登记信息
	 * 
	 * @author wanglin
	 * @version 2017年7月13日
	 * @param bankPayInfo
	 *            商行交款信息
	 * @param redirectAttributes
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:bankPayCtrl:reverse")
	@RequestMapping(value = "reverse", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String reverse(HttpServletRequest request, ClInMain bankPayInfo, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";

		try {
			// 获取业务编号
			String inNo = request.getParameter("clearId");

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
				/* 修改冲正理由长度限制为非空验证 wzj 2017-11-15 begin */
			} else if (StringUtils.isBlank(authorizeReason)) {
				throw new BusinessException("message.A1013", "", new String[] {});
				/* end */
			} else {
				bankPayInfo.setInNo(inNo);
				ClInMain bankPay = bankPayService.get(bankPayInfo);
				bankPay.setAuthorizeBy(user);
				bankPay.setAuthorizeDate(new Date());
				bankPay.setAuthorizeReason(authorizeReason);
				// 获取当前登录人
				User userInfo = UserUtils.getUser();
				bankPay.setOffice(userInfo.getOffice());
				// 进行冲正操作
				bankPayService.reverse(bankPay);
				// 弹出冲正成功信息
				message = msg.getMessage("message.I7002", new String[] { bankPayInfo.getInNo() }, locale);
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
	 * @author wanglin
	 * @version 2016年5月31日
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
	 * 打印商行交款明细
	 * 
	 * @author sg
	 * @version 2016年09月07日
	 * 
	 * 
	 * @param inNo
	 *            业务流水
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:bankPayCtrl:print")
	@RequestMapping(value = "/printBankPayDetail")
	public String printBankPayDetail(@RequestParam(value = "inNo", required = true) String inNo, Model model) {

		ClInMain bankPayInfo = bankPayService.get(inNo);
		// 转成中文大写
		String strBigAmount = NumToRMB.changeToBig(bankPayInfo.getInAmount().doubleValue());
		// 计算行数
		int count = 0;
		List<ClInDetail> clInDetail = Lists.newArrayList();
		for (ClInDetail bankPayDetail : bankPayInfo.getBankPayDetailList()) {
			// 总数String类型转Long
			Long number = Long.valueOf(bankPayDetail.getTotalCount());
			// 求平均值
			BigDecimal avg = bankPayDetail.getTotalAmt().divide(new BigDecimal(number));
			// 判断已清分的数量是否为空
			if (!"".equals(bankPayDetail.getCountYqf()) && bankPayDetail.getCountYqf() != null) {
				Integer yqf = Integer.parseInt(bankPayDetail.getCountYqf());
				String valueYqf = avg.multiply(new BigDecimal(yqf)).toString();
				bankPayDetail.setMoneyYqf(valueYqf);
				ClInDetail clInDetails = new ClInDetail();
				// 设置已清分的捆数
				clInDetails.setCountYqf(bankPayDetail.getCountYqf());
				// 设置已清分的面值
				clInDetails.setDenomination(bankPayDetail.getDenomination());
				// 设置已清分的金额
				clInDetails.setMoneyYqf(valueYqf);
				// 设置币种
				clInDetails.setCurrency(bankPayDetail.getCurrency());
				clInDetail.add(clInDetails);
				count++;
			}
			// 判断待清分的数量是否为空
			if (!"".equals(bankPayDetail.getCountDqf()) && bankPayDetail.getCountDqf() != null) {
				Integer dqf = Integer.parseInt(bankPayDetail.getCountDqf());
				String valueDqf = avg.multiply(new BigDecimal(dqf)).toString();
				bankPayDetail.setMoneyDqf(valueDqf);
				ClInDetail clInDetails = new ClInDetail();
				// 设置未清分的捆数
				clInDetails.setCountDqf(bankPayDetail.getCountDqf());
				// 设置未清分的面值
				clInDetails.setDenomination(bankPayDetail.getDenomination());
				// 设置未清分的金额
				clInDetails.setMoneyDqf(valueDqf);
				// 设置币种
				clInDetails.setCurrency(bankPayDetail.getCurrency());
				clInDetail.add(clInDetails);
				count++;
			}
		}
		// 获取当前登录人的信息
		User user = UserUtils.getUser();
		// 获取收款单位
		model.addAttribute("users",
				officeDao.get(officeDao.get(bankPayInfo.getrOffice().getId()).getParentId()).getName());
		model.addAttribute("user", user.getOffice().getName());
		model.addAttribute("strBigAmount", strBigAmount);
		model.addAttribute("bankPayInfo", bankPayInfo);
		// 数据
		model.addAttribute("clInDetail", clInDetail);
		// 获取最大页数
		int size = 0;
		if (count % 10 == 0) {
			size = count / 10;
		} else {
			size = count / 10 + 1;
		}
		model.addAttribute("size", size);
		// 打印代理上缴明细
		return "modules/clear/v03/bankPay/printBankPayDetail";
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
	public String reverseList(ClInMain bankPayInfo, HttpServletRequest request, HttpServletResponse response,
			Model model, RedirectAttributes redirectAttributes, @RequestParam(required = false) String clearId) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = msg.getMessage("message.I7002", new String[] { clearId }, locale);
		addMessage(model, message);
		return list(bankPayInfo, request, response, model);
	}

	/**
	 * @author qipeihong 跳转到显示冲正原因画面
	 * 
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "displayAuthorizeReason")
	public String displayAuthorizeReason(ClInMain clInMain, Model model) {
		model.addAttribute("authorizeParam", clInMain);
		return "modules/clear/v03/authorize/showAuthorizeDetail";
	}

	/**
	 * 验证冲正金额和柜员账务余额
	 * 
	 * @author XL
	 * @version 2017年10月26日
	 * @param clInMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "checkTellerAccounts")
	@ResponseBody
	public String checkTellerAccounts(ClInMain clInMain, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		// 获取柜员最新一条账务
		TellerAccountsMain tellerAccountsMain = ClearCommonUtils.getNewestTellerAccounts(clInMain.getTransManNo(),
				ClearConstant.CashTypeProvisions.PROVISIONS_FALSE);
		// 获取当前登录清分机构
			Office office = StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId());
		// 验证柜员余额和冲正金额
		if ((tellerAccountsMain != null && tellerAccountsMain.getTotalAmount().compareTo(clInMain.getInAmount()) >= 0)
				|| (ClearConstant.clearProvisionsOpen.CLEARPROVISIONSOPEN_FALSE.equals(office.getProvisionsSwitch())
						|| StringUtils.isEmpty(office.getProvisionsSwitch()))) {
			// 验证通过，可以冲正
			map.put("result", "success");
		} else {
			// 余额不足，不可冲正
			map.put("result", "error");
			map.put("transManName", clInMain.getTransManName());
		}
		return gson.toJson(map);
	}

	/**
	 * 验证冲正捆数和当日可分配捆数
	 * 
	 * @author XL
	 * @version 2017年10月26日
	 * @param clInMain
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "checkReverseCount")
	@ResponseBody
	public String checkReverseCount(ClInMain clInMain, HttpServletRequest request, HttpServletResponse response) {
		List<StoDict> stoDictList = GoodDictUtils.getDictListWithFg("cnypden");
		// 当日入库清分列表
		List<ClInMain> holeBankPayInfolist = Lists.newArrayList();
		// 当日已分配列表
		List<ClTaskMain> holeClTaskMainList = Lists.newArrayList();

		// 查询今日已分配数量
		ClTaskMain info = new ClTaskMain();
		// 设置业务类型为清分
		info.setBusType(ClearConstant.BusinessType.CLEAR);
		// 商行交款入库清分量
		List<ClInMain> bankPayInfoList = clTaskMainService.getDetailList(info);
		// 设置任务类型为任务分配
		info.setTaskType(ClearConstant.TaskType.TASK_DISTRIBUTION);
		// 设置计划类型为正常清分
		info.setPlanType(ClearConstant.PlanType.NORMAL_CLEAR);
		// 设置开始结束时间
		info.setOperateDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(new Date())));
		info.setOperateDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(new Date())));
		// 获得当天清分类型下任务分配流水
		List<ClTaskMain> clTaskMainList = clTaskMainService.findList(info);

		// 遍历出数据字典面值对应的code
		for (int a = 0; a < stoDictList.size(); a++) {
			StoDict tempStoDict = stoDictList.get(a);
			String code = tempStoDict.getValue();
			ClInMain existBankPayInfo = null;
			ClTaskMain existClTaskMain = null;

			// 设置默认为false
			boolean exist = false;
			boolean flag = false;
			// 遍历出数据库当日存入的面值code
			for (int b = 0; b < bankPayInfoList.size(); b++) {
				ClInMain tempBankPayInfo = bankPayInfoList.get(b);
				String keyCode = tempBankPayInfo.getDenomination();
				// 判断查询的面值code是否与数据字典code相等
				if (keyCode.equals(code)) {
					// 判断如果查询结果待清分为空，则页面显示0
					if (tempBankPayInfo.getCountBank() == null) {
						tempBankPayInfo.setCountBank("0");
					}
					existBankPayInfo = tempBankPayInfo;
					exist = true;
				}
			}
			// 如果查询的面值code与数据字典code不相等
			if (exist == false) {
				ClInMain newBankPayInfo = new ClInMain();
				newBankPayInfo.setDenomination(code);
				newBankPayInfo.setCountBank("0");
				holeBankPayInfolist.add(newBankPayInfo);
			} else {
				holeBankPayInfolist.add(existBankPayInfo);
			}

			// 遍历出当日已分配的信息
			for (int b = 0; b < clTaskMainList.size(); b++) {
				ClTaskMain tempClTaskMain = clTaskMainList.get(b);
				String keyCode = tempClTaskMain.getDenomination();
				// 判断查询的面值code是否与数据字典code相等
				if (keyCode.equals(code)) {
					if (existClTaskMain != null) {
						tempClTaskMain.setTotalCount(tempClTaskMain.getTotalCount() + existClTaskMain.getTotalCount());
					}
					existClTaskMain = tempClTaskMain;
					flag = true;
				}
			}
			// 如果查询的面值code与数据字典code不相等
			if (flag == false) {
				ClTaskMain newClTaskMain = new ClTaskMain();
				newClTaskMain.setDenomination(code);
				newClTaskMain.setTotalCount(Long.parseLong("0"));
				holeClTaskMainList.add(newClTaskMain);
			} else {
				holeClTaskMainList.add(existClTaskMain);
			}
		}
		Map<String, Object> map = Maps.newHashMap();
		// 面值及对应的可分配捆数
		Map<String, Long> mapCount = Maps.newHashMap();
		for (int i = 0; i < holeBankPayInfolist.size(); i++) {
			mapCount.put(holeBankPayInfolist.get(i).getDenomination(),
					Long.parseLong(holeBankPayInfolist.get(i).getCountBank())
							- holeClTaskMainList.get(i).getTotalCount());
		}
		// 查询商行交款明细
		List<ClInDetail> detailList = clInMain.getBankPayDetailList();
		// 验证可分配和冲正捆数
		for (ClInDetail clInDetail : detailList) {
			// 冲正捆数大于可分配捆数
			if (Long.parseLong(clInDetail.getCountDqf()) > mapCount.get(clInDetail.getDenomination())) {
				map.put("result", "error");
				return gson.toJson(map);
			}
		}
		map.put("result", "success");
		return gson.toJson(map);
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
	public String checkDate(ClInMain clInMain, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		if (DateUtils.formatDate(clInMain.getUpdateDate()).equals(DateUtils.formatDate(new Date()))) {
			// 验证通过，可以冲正
			map.put("result", "success");
		} else {
			// 非本日流水，不可冲正
			map.put("result", "error");
		}
		return gson.toJson(map);
	}
}
