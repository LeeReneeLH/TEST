package com.coffer.businesses.modules.clear.v03.web;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClErrorInfo;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.clear.v03.service.ClErrorInfoService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 差错管理Controller
 * 
 * @author XL
 * @version 2017年9月7日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/clErrorInfo")
public class ClErrorInfoController extends BaseController {

	@Autowired
	private ClErrorInfoService clErrorInfoService;

	/**
	 * 根据流水单号，取得差错信息
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param 差错单号(errorNo)
	 * @return 差错信息
	 */
	@ModelAttribute
	public ClErrorInfo get(@RequestParam(required = false) String errorNo) {
		ClErrorInfo entity = null;
		if (StringUtils.isNotBlank(errorNo)) {
			entity = clErrorInfoService.get(errorNo);
		}
		if (entity == null) {
			entity = new ClErrorInfo();
		}
		return entity;
	}

	/**
	 * 获取差错信息列表
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return 差错信息一览页面
	 */
	@RequiresPermissions("clear:v03:clErrorInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClErrorInfo clErrorInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
		// User userInfo = UserUtils.getUser();
		// clErrorInfo.setOffice(userInfo.getOffice());
		/* end */

		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		if (clErrorInfo.getCreateTimeStart() == null) {
			clErrorInfo.setCreateTimeStart(new Date());
		}
		if (clErrorInfo.getCreateTimeEnd() == null) {
			clErrorInfo.setCreateTimeEnd(new Date());
		}
		/* end */
		Page<ClErrorInfo> page = clErrorInfoService.findPage(new Page<ClErrorInfo>(request, response), clErrorInfo);
		model.addAttribute("page", page);
		return "modules/clear/v03/clError/clErrorInfoList";
	}

	/**
	 * 跳转至登记页面
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("clear:v03:clErrorInfo:view")
	@RequestMapping(value = "form")
	public String form(ClErrorInfo clErrorInfo, Model model) {
		// 现钞清分人员、现钞清分管理员
		model.addAttribute("clearManage", Global.getConfig(Constant.CLEAR_MANAGE_TYPE));
		// 现钞差错管理员类型
		model.addAttribute("clearError", Global.getConfig(Constant.CLEAR_PROVISIONS_TYPE));
		clErrorInfo.setFindTime(new Date());
		clErrorInfo.setSeelDate(new Date());
		model.addAttribute("clErrorInfo", clErrorInfo);
		return "modules/clear/v03/clError/clErrorInfoForm";
	}

	/**
	 * 跳转至查看页面
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("clear:v03:clErrorInfo:view")
	@RequestMapping(value = "view")
	public String view(ClErrorInfo clErrorInfo, Model model) {
		model.addAttribute("clErrorInfo", clErrorInfo);
		return "modules/clear/v03/clError/clErrorInfoView";
	}

	/**
	 * 保存一条差错信息
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @return 一览页面
	 */
	@RequiresPermissions("clear:v03:clErrorInfo:edit")
	@RequestMapping(value = "save")
	public String save(ClErrorInfo clErrorInfo, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		// 提示信息
		String message = "";
		try {
			/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
			User userInfo = UserUtils.getUser();
			clErrorInfo.setOffice(userInfo.getOffice());
			/* end */
			clErrorInfoService.save(clErrorInfo);
			// 保存成功
			message = msg.getMessage("message.I7500", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(clErrorInfo, model);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/clear/v03/clErrorInfo/?repage";
	}

	/**
	 * 冲正一条差错信息
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @param request
	 * @param model
	 * @param redirectAttributes
	 * @return 一览页面
	 */
	@RequiresPermissions("clear:v03:clErrorInfo:reverse")
	@RequestMapping(value = "reverse", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String reverse(HttpServletRequest request, ClErrorInfo clErrorInfo, Model model,
			RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, clErrorInfo)) {
			return form(clErrorInfo, model);
		}
		Locale locale = LocaleContextHolder.getLocale();
		// 提示信息
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
			} /* 修改冲正理由长度限制为非空验证 wzj 2017-11-15 begin */
			else if (StringUtils.isBlank(authorizeReason)) {
				throw new BusinessException("message.A1013", "", new String[] {});
				/* end */
			} else {
				// 设置单号
				clErrorInfo.setErrorNo(inNo);
				// 获取差错信息
				ClErrorInfo clError = clErrorInfoService.get(clErrorInfo);
				// 设置授权人
				clError.setAuthorizeBy(user);
				// 设置授权时间
				clError.setAuthorizeDate(new Date());
				// 设置冲正原因
				clError.setAuthorizeReason(authorizeReason);
				/* 设置发生机构 修改人：qph 修改时间：2017-11-14 begin */
				User userInfo = UserUtils.getUser();
				clError.setOffice(userInfo.getOffice());
				/* end */
				clErrorInfoService.reverse(clError);
				// 冲正成功
				message = msg.getMessage("message.I75001", null, locale);
				addMessage(redirectAttributes, message);
			}
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			return message;
		}
		return "success";
	}

	/**
	 * 查询面值
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param denomination
	 * @return 面值
	 */
	@RequestMapping(value = "getDenominationValue")
	@ResponseBody
	public String getDenominationValue(@RequestParam(value = "denomination", required = true) String denomination) {
		// 面值集合
		List<StoDict> list = GoodDictUtils.getDictListWithFg(Constant.DenominationType.RMB_PDEN);
		for (StoDict stoDict : list) {
			if (stoDict.getValue().equals(denomination)) {
				// 返回面值金额
				return gson.toJson(stoDict.getUnitVal());
			}
		}
		return gson.toJson("");
	}

	/**
	 * 跳转至打印页面
	 * 
	 * @author XL
	 * @version 2017年9月7日
	 * @param clErrorInfo
	 * @return 打印页面
	 */
	@RequiresPermissions("clear:v03:clErrorInfo:print")
	@RequestMapping(value = "printClErrorInfo")
	public String printClErrorInfo(ClErrorInfo clErrorInfo, Model model) {
		// 差错金额大写
		String strBigAmount = NumToRMB.changeToBig(clErrorInfo.getErrorMoney().doubleValue());
		model.addAttribute("strBigAmount", strBigAmount);
		model.addAttribute("clErrorInfo", clErrorInfo);
		// 发现人姓名
		model.addAttribute("find", SysCommonUtils
				.findOfficeById(UserUtils.get(clErrorInfo.getClearManNo()).getOffice().getId()).getName());
		return "modules/clear/v03/clError/clErrorPrint";
	}

	/**
	 * 返回一览页面
	 * 
	 * @author XL
	 * @date 2017年9月7日
	 * @param clErrorInfo
	 * @param redirectAttributes
	 * @return 一览页面
	 */
	@RequestMapping(value = "back")
	public String back(ClErrorInfo clErrorInfo, RedirectAttributes redirectAttributes) {
		return "redirect:" + adminPath + "/clear/v03/clErrorInfo/list";
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
	public String reverseList(ClErrorInfo clErrorInfo, HttpServletRequest request, HttpServletResponse response,
			Model model, RedirectAttributes redirectAttributes, @RequestParam(required = false) String clearId) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = msg.getMessage("message.I75001", new String[] { clearId }, locale);
		addMessage(model, message);
		return list(clErrorInfo, request, response, model);
	}

	/**
	 * @author qipeihong 跳转到显示冲正原因画面
	 * 
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "displayAuthorizeReason")
	public String displayAuthorizeReason(ClErrorInfo clErrorInfo, Model model) {
		model.addAttribute("authorizeParam", clErrorInfo);
		return "modules/clear/v03/authorize/showAuthorizeDetail";
	}

	/**
	 * 验证冲正金额和柜员账务余额
	 * 
	 * @author XL
	 * @version 2017年11月02日
	 * @param clErrorInfo
	 * @param request
	 * @param response
	 * @return 验证结果
	 */
	@RequestMapping(value = "checkTellerAccounts")
	@ResponseBody
	public String checkTellerAccounts(ClErrorInfo clErrorInfo, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		boolean flag = true;
		// 长款，验证柜员账务
		if (ClearConstant.ErrorType.LONG_CURRENCY.equals(clErrorInfo.getErrorType())) {
			Office office = StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId());
			if (ClearConstant.clearProvisionsOpen.CLEARPROVISIONSOPEN_FALSE.equals(office.getProvisionsSwitch())) {
				flag = false;
			}
			if (flag) {
				// 获取柜员最新一条账务
				TellerAccountsMain tellerAccountsMain = ClearCommonUtils.getNewestTellerAccounts(
						clErrorInfo.getCheckManNo(), ClearConstant.CashTypeProvisions.PROVISIONS_TRUE);
				// 验证柜员余额和冲正金额
				if (tellerAccountsMain != null
						&& tellerAccountsMain.getTotalAmount().compareTo(clErrorInfo.getErrorMoney()) >= 0) {
					// 验证通过，可以冲正
					map.put("result", "success");
				} else {
					// 余额不足，不可冲正
					map.put("result", "error");
					map.put("transManName", clErrorInfo.getCheckManName());
				}
			} else {
				// 验证通过，可以冲正
				map.put("result", "success");
			}
		} else {
			// 短款或假币，不需验证
			map.put("result", "success");
		}
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
	public String checkDate(ClErrorInfo clErrorInfo, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = Maps.newHashMap();
		if (DateUtils.formatDate(clErrorInfo.getUpdateDate()).equals(DateUtils.formatDate(new Date()))) {
			// 验证通过，可以冲正
			map.put("result", "success");
		} else {
			// 非本日流水，不可冲正
			map.put("result", "error");
		}
		return gson.toJson(map);
	}

	/**
	 * @author sg
	 * @version 2017年11月21日
	 * 
	 *          根据查询条件，导出业务量数据
	 * @param ClErrorInfo
	 * 
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 * 
	 * 
	 */
	@RequestMapping(value = { "exportClErrorInfoReport" })
	public void exportClErrorInfoReport(ClErrorInfo clErrorInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 导出excel
		List<Map<String, Object>> paramList = Lists.newArrayList();
		// 获得当前登录人信息
		User userInfo = UserUtils.getUser();
		// 设置机构为当前登录人机构
		clErrorInfo.setOffice(userInfo.getOffice());
		if (clErrorInfo.getCreateTimeStart() == null) {
			clErrorInfo.setCreateTimeStart(new Date());
		}
		if (clErrorInfo.getCreateTimeEnd() == null) {
			clErrorInfo.setCreateTimeEnd(new Date());
		}
		// 查询条件： 开始时间
		if (clErrorInfo.getCreateTimeStart() != null) {
			clErrorInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clErrorInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clErrorInfo.getCreateTimeEnd() != null) {
			clErrorInfo
					.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(clErrorInfo.getCreateTimeEnd())));
		}
		// 根据条件查询数据
		List<ClErrorInfo> clErrorInfoList = clErrorInfoService.findList(clErrorInfo);
		for (ClErrorInfo clErrorInfos : clErrorInfoList) {
			// 登记类型
			clErrorInfos
					.setSubErrorType(DictUtils.getDictLabel(clErrorInfos.getSubErrorType(), "clear_subError_type", ""));
			// 业务类型
			clErrorInfos.setBusType(DictUtils.getDictLabel(clErrorInfos.getBusType(), "clear_businesstype", ""));
			// 类别
			clErrorInfos.setErrorType(DictUtils.getDictLabel(clErrorInfos.getErrorType(), "clear_error_type", ""));
			// 币种
			clErrorInfos.setCurrency(DictUtils.getDictLabel(clErrorInfos.getCurrency(), "money_currency", ""));
			// 面值
			clErrorInfos.setDenomination(GoodDictUtils.getDictLabel(clErrorInfos.getDenomination(), "cnypden", ""));
			/* 增加登记人和登记时间 wzj 2017-11-22 begin */
			clErrorInfos.setRegistrant(clErrorInfos.getCreateName());
			clErrorInfos.setBoardingTime(clErrorInfos.getCreateDate());
			/* end */
		}
		if (Collections3.isEmpty(clErrorInfoList)) {
			clErrorInfoList.add(new ClErrorInfo());
		}
		// 获取当前登录人的机构
		String officeName = UserUtils.getUser().getOffice().getName();
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		// 差错处理.xls
		String fileName = msg.getMessage("clear.clErrorInfo.errorHandlingxls", null, locale);
		// sheet1
		Map<String, Object> sheet1Map = Maps.newHashMap();
		// sheet页名为差错处理
		sheet1Map.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("clear.clErrorInfo.errorHandling", null, locale));
		Map<String, Object> sheet1TitleMap = Maps.newHashMap();
		// 添加制表机构
		sheet1TitleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, officeName);
		// 添加制表时间
		sheet1TitleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTime());
		sheet1Map.put(ExcelExporterEx.SHEET_TITLE_MAP_KEY, sheet1TitleMap);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, clErrorInfoList);
		sheet1Map.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, ClErrorInfo.class.getName());

		paramList.add(sheet1Map);

		// 导出
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		exportEx.createWorkBook(request, response, templatePath, fileName);
	}

}