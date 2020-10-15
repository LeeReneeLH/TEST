package com.coffer.businesses.modules.doorOrder.v01.web;

import java.math.BigDecimal;
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
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.doorOrder.BankApiParamConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.BankAccountInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.CompanyAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.BankAccountInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.BankService;
import com.coffer.businesses.modules.doorOrder.v01.service.CompanyAccountsMainService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 公司账务Controller
 * 
 * @author XL
 * @version 2019-06-26
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/companyAccountsMain")
public class CompanyAccountsMainController extends BaseController {

	@Autowired
	private CompanyAccountsMainService companyAccountsMainService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private BankService bankService;
	@Autowired
	private BankAccountInfoService bankAccountInfoService;

	@ModelAttribute
	public CompanyAccountsMain get(@RequestParam(required = false) String id) {
		CompanyAccountsMain entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = companyAccountsMainService.get(id);
		}
		if (entity == null) {
			entity = new CompanyAccountsMain();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:companyAccountsMain:view")
	@RequestMapping(value = { "list", "" })
	public String list(CompanyAccountsMain companyAccountsMain, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 查询条件： 开始时间
		if (companyAccountsMain.getCreateTimeStart() != null) {
			companyAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(companyAccountsMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (companyAccountsMain.getCreateTimeEnd() != null) {
			companyAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(companyAccountsMain.getCreateTimeEnd())));
		}
		/* end */
		// 数据过滤
		User userInfo = UserUtils.getUser();
		if (StringUtils.isBlank(companyAccountsMain.getOfficeId())) {
			companyAccountsMain.getSqlMap().put("dsf", "AND (a.office_id =" + userInfo.getOffice().getId()
					+ " OR o7.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getId() + "%')");
		}

		Page<CompanyAccountsMain> page = companyAccountsMainService
				.findPage(new Page<CompanyAccountsMain>(request, response), companyAccountsMain);
		model.addAttribute("page", page);
		model.addAttribute("companyAccountsMain", companyAccountsMain);
		// 获取公司待回款金额
		BigDecimal companyNotBackAmount = companyAccountsMainService
				.getCompanyNotBackAmount(officeService.getPlatform().get(0).getId());
		model.addAttribute("companyNotBackAmount", companyNotBackAmount);
		return "modules/doorOrder/v01/companyAccounts/companyAccountsMainList";
	}

	@RequiresPermissions("doorOrder:v01:companyAccountsMain:view")
	@RequestMapping(value = "form")
	public String form(CompanyAccountsMain companyAccountsMain, Model model) {
		model.addAttribute("companyAccountsMain", companyAccountsMain);
		return "modules/doorOrder/v01/companyAccounts/companyAccountsMainForm";
	}

	@RequiresPermissions("doorOrder:v01:companyAccountsMain:edit")
	@RequestMapping(value = "save")
	public synchronized String save(CompanyAccountsMain companyAccountsMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (!beanValidator(model, companyAccountsMain)) {
			return form(companyAccountsMain, model);
		}
		try {
			// 生成存款流水
			companyAccountsMain.setBusinessId(BusinessUtils.getNewBusinessNo(
					DoorOrderConstant.BusinessType.COMPANY_SAVE_CASH, UserUtils.getUser().getOffice()));
			// 设置客户为清分中心
			companyAccountsMain.setCustNo(UserUtils.getUser().getOffice().getId());
			companyAccountsMain
					.setCustName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
			// 设置账务发生机构及归属机构ID
			companyAccountsMain.setOfficeId(UserUtils.getUser().getOffice().getId());
			companyAccountsMain.setCompanyId(companyAccountsMain.getCompanyId());
			// 设置账务发生机构名称及归属机构名称
			companyAccountsMain
					.setOfficeName(StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId()).getName());
			companyAccountsMain
					.setCompanyName(StoreCommonUtils.getOfficeById(companyAccountsMain.getCompanyId()).getName());
			companyAccountsMainService.save(companyAccountsMain);
			addMessage(redirectAttributes, "保存公司账务成功!");
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/companyAccountsMain/?repage";
		}
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/companyAccountsMain/?repage";
	}

	@RequiresPermissions("doorOrder:v01:companyAccountsMain:edit")
	@RequestMapping(value = "delete")
	public String delete(CompanyAccountsMain companyAccountsMain, RedirectAttributes redirectAttributes) {
		companyAccountsMainService.delete(companyAccountsMain);
		addMessage(redirectAttributes, "删除公司账务成功!");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/companyAccountsMain/?repage";
	}

	/**
	 * 获取机构银行卡号和待存款金额
	 * 
	 * @author wangqingjie
	 * @version 2019-7-10
	 */
	@ResponseBody
	@RequestMapping(value = "getStayAmount")
	public String getBankCard(String officeId) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 返回结果
		Map<String, Object> result = new HashMap<>();
		// 如果进行线上汇款，通过银企之连接口查询公司账户余额，并计算。
		if (Global.getConfig("online.remittance").equals(Constant.OnlineRemittance.TRUE)) {
			// 平台公司账户余额
			BigDecimal companyAmount = DoorCommonUtils
					.getCompanyTotalAmount(StoreCommonUtils.getPlatform().get(0).getId());
			// 获取公司账户实际金额
			try {
				// 公司卡号信息
				BankAccountInfo bankAccountInfo = new BankAccountInfo();
				bankAccountInfo.setOfficeId(officeId);
				bankAccountInfo.setStatus(Constant.BankStatus.BIND);
				List<BankAccountInfo> list = bankAccountInfoService.findByMerchantAndStatus(bankAccountInfo);

				if (!Collections3.isEmpty(list)) {
					String bankCard = list.get(0).getAccountNo();
					// 卡号
					result.put("bankCard", bankCard);
					Map<String, Object> obj = bankService.accountBalanceInquiry(bankCard);
					BigDecimal trueAmount = new BigDecimal(
							obj.get(BankApiParamConstant.AccountBalanceInquiryParams.ACCOUNT_BALANCE_KEY).toString());
					// 存款金额
					result.put("amount", trueAmount.subtract(companyAmount));
				} else {
					throw new BusinessException("message.A1035", "", new String[] {});
				}
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
				result.put("errorMessage", message);
			}
		}
		return gson.toJson(result);
	}
}