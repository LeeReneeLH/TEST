package com.coffer.businesses.modules.doorOrder.v01.web;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportCenter;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorDayReportCenterService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorDayReportMainService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 账务日结Controller
 * 
 * @author QPH
 * @version 2017-09-08
 */
@Controller("dayReportMain")
@RequestMapping(value = "${adminPath}/doorOrder/v01/dayReportMain")
public class DoorDayReportMainController extends BaseController {

	@Autowired
	private DoorDayReportMainService dayReportMainService;

	@Autowired
	private DoorDayReportCenterService dayReportCenterService;

	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;

	@ModelAttribute
	public DoorDayReportMain get(@RequestParam(required = false) String reportId) {
		DoorDayReportMain entity = null;
		if (StringUtils.isNotBlank(reportId)) {
			entity = dayReportMainService.get(reportId);
		}
		if (entity == null) {
			entity = new DoorDayReportMain();
		}
		return entity;
	}

	@RequestMapping(value = { "list", "" })
	public String list(DoorDayReportMain dayReportMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 初始化开始时间和结束时间
		if (dayReportMain.getCreateTimeStart() == null) {
			dayReportMain.setCreateTimeStart(new Date());
		}
		if (dayReportMain.getCreateTimeEnd() == null) {
			dayReportMain.setCreateTimeEnd(new Date());
		}

		Page<DoorDayReportMain> page = dayReportMainService.findPage(new Page<DoorDayReportMain>(request, response),
				dayReportMain);
		model.addAttribute("dayReportMain", dayReportMain);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/dayReport/dayReportMainList";
	}

	@RequestMapping(value = "form")
	public String form(DoorDayReportMain dayReportMain, Model model) {
		model.addAttribute("dayReportMain", dayReportMain);
		return "modules/doorOrder/v01/dayReport/dayReportMainForm";
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月13日 账务结算查看明细
	 * 
	 * @param dayReportMain
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */

	@RequestMapping(value = "centerView")
	public String centerView(DoorDayReportMain dayReportMain, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		DoorDayReportCenter dayReportCenter = new DoorDayReportCenter();
		dayReportCenter.setReportMainId(dayReportMain.getReportId());
		Page<DoorDayReportCenter> page = dayReportCenterService
				.findPage(new Page<DoorDayReportCenter>(request, response), dayReportCenter);
		model.addAttribute("page", page);
		model.addAttribute("dayReportMain", dayReportMain);
		return "modules/doorOrder/v01/dayReport/dayReportCenterList";
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月13日 手动账务结算
	 * 
	 * @param dayReportMain
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */

	@RequestMapping(value = "report")
	public synchronized String report(DoorDayReportMain dayReportMain, Model model,
			RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 若结账方式为自动结账
			Office office = UserUtils.getUser().getOffice();
			User user = UserUtils.getUser();
			// 张家港中心不走此结算逻辑
			if (!office.getId().equals(Global.getConfig("centerId.zhangjiagang"))) {
				// 主表状态更新
				DoorDayReportMain dayReport = new DoorDayReportMain();
				// 设置日结人
				dayReport.setReportBy(user);
				dayReport.setReportName(user.getName());
				// 设置日结主键
				dayReport.setReportId(IdGen.uuid());
				// 设置日结时间
				dayReport.setReportDate(new Date());
				// 商户结算
				DoorCommonUtils.dayMerchanAccountsReport(ClearConstant.WindupType.WINDUP_MANUAL, dayReport, office);
				// 门店结算
				DoorCommonUtils.dayGuestAccountsReport(ClearConstant.WindupType.WINDUP_MANUAL, dayReport, office);
				// 中心账务结算
				DoorCommonUtils.dayCenterAccountsReport(ClearConstant.WindupType.WINDUP_MANUAL, dayReport, office);
			}

		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
		}
		// 保存成功！
		message = msg.getMessage("message.A1004", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}

	/**
	 * 打印上缴审批明细
	 * 
	 * @author qipeihong
	 * @version 2017年9月14日
	 * 
	 * 
	 * @param allId 流水单号
	 * @param type  业务类型
	 * @return 列表页面
	 */
	/* @RequestMapping(value = "/printDetail") */
	public String printDetail(@RequestParam(value = "reportId", required = true) String reportId,
			@RequestParam(value = "type", required = true) String type, Model model) {

		DoorDayReportMain dayReportMain = dayReportMainService.get(reportId);
		// 设置中心账务
		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 设置业务类型
		List<String> businessTypes = Lists.newArrayList();
		// 清分业务
		if (ClearConstant.AccountsType.ACCOUNTS_CLEAR.equals(type)) {
			businessTypes.add(ClearConstant.BusinessType.BANK_PAY);
			businessTypes.add(ClearConstant.BusinessType.BANK_GET);
			businessTypes.add(ClearConstant.BusinessType.AGENCY_PAY);
		}
		// 复点业务
		if (ClearConstant.AccountsType.ACCOUNTS_COMPLEX.equals(type)) {
			businessTypes.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
			businessTypes.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);

		}
		// 汇总
		if (ClearConstant.AccountsType.ACCOUNTS_PROVISIONS.equals(type)) {
			businessTypes.add(ClearConstant.BusinessType.BANK_PAY);
			businessTypes.add(ClearConstant.BusinessType.BANK_GET);
			businessTypes.add(ClearConstant.BusinessType.AGENCY_PAY);
			businessTypes.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
			businessTypes.add(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
		}
		centerAccountsMain.setBusinessTypes(businessTypes);
		// 设置查询时间
		centerAccountsMain
				.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(dayReportMain.getReportDate())));
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(dayReportMain.getReportDate()));
		// 设置账务发生机构
		User userInfo = UserUtils.getUser();
		centerAccountsMain.setRofficeId(userInfo.getOffice().getId());
		// 根据类型以及时间查询账务流水
		List<DoorCenterAccountsMain> centerAccountsMainList = centerAccountsMainService
				.findListGroupByBusinessType(centerAccountsMain);
		List<DoorCenterAccountsMain> centerAccountsMainsList = Lists.newArrayList();
		// 清点业务
		if (ClearConstant.AccountsType.ACCOUNTS_CLEAR.equals(type)) {
			this.makeListByClear(centerAccountsMainList, centerAccountsMainsList);
		}
		// 复点业务
		else if (ClearConstant.AccountsType.ACCOUNTS_COMPLEX.equals(type)) {
			this.makeListByComplex(centerAccountsMainList, centerAccountsMainsList);
		}
		// 总计业务
		else if (ClearConstant.AccountsType.ACCOUNTS_PROVISIONS.equals(type)) {
			this.makeListByClear(centerAccountsMainList, centerAccountsMainsList);
			this.makeListByComplex(centerAccountsMainList, centerAccountsMainsList);
		}
		// 页面计算合计
		this.calTotal(centerAccountsMainsList, model);
		// 获取离系统日期最近的日期
		Date maxdate = dayReportCenterService.getDayReportMaxDate(dayReportMain, UserUtils.getUser().getOffice());
		// 昨日余额处理
		BigDecimal beforeAmount = new BigDecimal(0);
		if (maxdate != null) {
			DoorDayReportMain dayReportBefore = new DoorDayReportMain();
			dayReportBefore.setSearchDateStart(DateUtils.formatDateTime(DateUtils.getDateStart(maxdate)));
			dayReportBefore.setSearchDateEnd(DateUtils.formatDateTime(DateUtils.getDateEnd(maxdate)));
			dayReportBefore.setOffice(userInfo.getOffice());
			List<DoorDayReportMain> list = dayReportMainService.findListByReportDate(dayReportBefore);
			if (!Collections3.isEmpty(list)) {
				List<DoorDayReportCenter> centerList = list.get(0).getDayReportCenterList();
				if (ClearConstant.AccountsType.ACCOUNTS_PROVISIONS.equals(type)) {
					for (DoorDayReportCenter dayCenter : centerList) {
						if (!dayCenter.getAccountsType().equals(type)) {
							beforeAmount = beforeAmount.add(dayCenter.getTotalAmount());
						}
					}
				} else {
					for (DoorDayReportCenter dayCenter : centerList) {
						if (dayCenter.getAccountsType().equals(type)) {
							beforeAmount = dayCenter.getTotalAmount();
							break;
						}
					}
				}
			}
		}
		model.addAttribute("beforeAmount", beforeAmount);
		// 今日余额处理
		BigDecimal todayAmount = new BigDecimal(0);
		List<DoorDayReportCenter> centerListToday = dayReportMain.getDayReportCenterList();
		// 备付金业务
		if (ClearConstant.AccountsType.ACCOUNTS_PROVISIONS.equals(type)) {
			for (DoorDayReportCenter dayCenter : centerListToday) {
				if (!dayCenter.getAccountsType().equals(type)) {
					todayAmount = todayAmount.add(dayCenter.getTotalAmount());
				}
			}
		} else {
			for (DoorDayReportCenter dayCenter : centerListToday) {
				if (dayCenter.getAccountsType().equals(type)) {
					todayAmount = dayCenter.getTotalAmount();
					break;
				}
			}
		}
		String reportDate = DateUtils.formatDate(dayReportMain.getReportDate(), "yyyy-MM-dd");
		model.addAttribute("reportDate", reportDate);
		model.addAttribute("type", type);
		model.addAttribute("todayAmount", todayAmount);
		model.addAttribute("size", centerAccountsMainsList.size());
		model.addAttribute("centerAccountsMainsList", centerAccountsMainsList);
		// 打印审批明细
		return "modules/doorOrder/v01/dayReport/printDayReportDetail";
	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月15日 处理清点业务打印list
	 * 
	 * @param centerAccountsMainList
	 * @param centerMainList
	 */
	private void makeListByClear(List<DoorCenterAccountsMain> centerAccountsMainList,
			List<DoorCenterAccountsMain> centerMainList) {
		List<String> typeList = Lists.newArrayList();
		// 获取有数据的业务类型
		for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
			typeList.add(accountsMain.getBusinessType());
		}
		// 处理商行交款
		if (typeList.contains(ClearConstant.BusinessType.BANK_PAY)) {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
				// 业务类型为商行交款
				if (accountsMain.getBusinessType().equals(ClearConstant.BusinessType.BANK_PAY)) {
					// 设置业务类型
					centerAccountsMain.setBusinessType(accountsMain.getBusinessType());
					// 设置收入数量
					centerAccountsMain.setInCount(accountsMain.getInCount());
					// 设置支出数量
					centerAccountsMain.setOutCount(accountsMain.getOutCount());
					// 设置收入金额
					centerAccountsMain.setInAmount(accountsMain.getInAmount());
					// 设置支出金额
					centerAccountsMain.setOutAmount(accountsMain.getOutAmount());
					centerMainList.add(centerAccountsMain);
				}

			}
		} else {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			centerAccountsMain.setBusinessType(ClearConstant.BusinessType.BANK_PAY);
			centerMainList.add(centerAccountsMain);
		}

		// 处理商行取款
		if (typeList.contains(ClearConstant.BusinessType.BANK_GET)) {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
				// 业务类型为商行取款
				if (accountsMain.getBusinessType().equals(ClearConstant.BusinessType.BANK_GET)) {
					// 设置业务类型
					centerAccountsMain.setBusinessType(accountsMain.getBusinessType());
					// 设置收入数量
					centerAccountsMain.setInCount(accountsMain.getInCount());
					// 设置支出数量
					centerAccountsMain.setOutCount(accountsMain.getOutCount());
					// 设置收入金额
					centerAccountsMain.setInAmount(accountsMain.getInAmount());
					// 设置支出金额
					centerAccountsMain.setOutAmount(accountsMain.getOutAmount());
					centerMainList.add(centerAccountsMain);
				}
			}
		} else {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			centerAccountsMain.setBusinessType(ClearConstant.BusinessType.BANK_GET);
			centerMainList.add(centerAccountsMain);
		}

		// 处理商行取款
		if (typeList.contains(ClearConstant.BusinessType.AGENCY_PAY)) {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
				// 业务类型为代理上缴
				if (accountsMain.getBusinessType().equals(ClearConstant.BusinessType.AGENCY_PAY)) {
					// 设置业务类型
					centerAccountsMain.setBusinessType(accountsMain.getBusinessType());
					// 设置收入数量
					centerAccountsMain.setInCount(accountsMain.getInCount());
					// 设置支出数量
					centerAccountsMain.setOutCount(accountsMain.getOutCount());
					// 设置收入金额
					centerAccountsMain.setInAmount(accountsMain.getInAmount());
					// 设置支出金额
					centerAccountsMain.setOutAmount(accountsMain.getOutAmount());
					centerMainList.add(centerAccountsMain);
				}
			}
		} else {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			centerAccountsMain.setBusinessType(ClearConstant.BusinessType.AGENCY_PAY);
			centerMainList.add(centerAccountsMain);
		}

	}

	/**
	 * 
	 * 
	 * @author qipeihong
	 * @version 2017年9月15日 处理复点业务打印list
	 * 
	 * @param centerAccountsMainList
	 * @param centerMainList
	 */
	private void makeListByComplex(List<DoorCenterAccountsMain> centerAccountsMainList,
			List<DoorCenterAccountsMain> centerMainList) {
		List<String> typeList = Lists.newArrayList();
		// 获取有数据的业务类型
		for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
			typeList.add(accountsMain.getBusinessType());
		}
		// 处理人民银行复点入库
		if (typeList.contains(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN)) {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
				// 业务类型为人行复点入库
				if (accountsMain.getBusinessType().equals(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN)) {
					// 设置业务类型
					centerAccountsMain.setBusinessType(accountsMain.getBusinessType());
					// 设置收入数量
					centerAccountsMain.setInCount(accountsMain.getInCount());
					// 设置支出数量
					centerAccountsMain.setOutCount(accountsMain.getOutCount());
					// 设置收入金额
					centerAccountsMain.setInAmount(accountsMain.getInAmount());
					// 设置支出金额
					centerAccountsMain.setOutAmount(accountsMain.getOutAmount());
					centerMainList.add(centerAccountsMain);
				}
			}
		} else {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			centerAccountsMain.setBusinessType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_IN);
			centerMainList.add(centerAccountsMain);
		}

		// 人民银行复点出库
		if (typeList.contains(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT)) {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
				// 业务类型为人行复点出库
				if (accountsMain.getBusinessType().equals(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT)) {
					// 设置业务类型
					centerAccountsMain.setBusinessType(accountsMain.getBusinessType());
					// 设置收入数量
					centerAccountsMain.setInCount(accountsMain.getInCount());
					// 设置支出数量
					centerAccountsMain.setOutCount(accountsMain.getOutCount());
					// 设置收入金额
					centerAccountsMain.setInAmount(accountsMain.getInAmount());
					// 设置支出金额
					centerAccountsMain.setOutAmount(accountsMain.getOutAmount());
					centerMainList.add(centerAccountsMain);
				}
			}
		} else {
			DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
			centerAccountsMain.setBusinessType(ClearConstant.BusinessType.PEOPLE_BANK_COMPLEX_POINT_OUT);
			centerMainList.add(centerAccountsMain);
		}
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月15日 计算页面合计
	 * 
	 * 
	 * @param centerAccountsMainList
	 * @param model
	 */
	private void calTotal(List<DoorCenterAccountsMain> centerAccountsMainList, Model model) {
		// 合计收入笔数
		int totalInCount = 0;
		// 合计支出笔数
		int totalOutCount = 0;
		// 合计收入金额
		BigDecimal totalInAmount = new BigDecimal(0);
		// 合计支出金额
		BigDecimal totalOutAmount = new BigDecimal(0);

		for (DoorCenterAccountsMain accountsMain : centerAccountsMainList) {
			// 计算收入笔数
			totalInCount += accountsMain.getInCount();
			// 计算支出笔数
			totalOutCount += accountsMain.getOutCount();
			// 计算收入金额
			if (accountsMain.getInAmount() != null) {
				totalInAmount = totalInAmount.add(accountsMain.getInAmount());
			}
			// 计算支出金额
			if (accountsMain.getOutAmount() != null) {
				totalOutAmount = totalOutAmount.add(accountsMain.getOutAmount());
			}
		}
		model.addAttribute("totalInCount", totalInCount);
		model.addAttribute("totalOutCount", totalOutCount);
		model.addAttribute("totalInAmount", totalInAmount);
		model.addAttribute("totalOutAmount", totalOutAmount);

	}

}