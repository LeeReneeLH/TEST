package com.coffer.businesses.modules.doorOrder.v01.web;

import java.math.BigDecimal;
import java.util.Arrays;
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
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant.AccountPaidStatus;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportAccountExport;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportExport;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorMerchanService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 门店日结Controller
 * 
 * @author wqj
 * @version 2019-07-23
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/dayReportDoorMerchan")
public class DoorDayReportDoorMerchanController extends BaseController {
	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;
	@Autowired
	private DayReportDoorMerchanService dayReportDoorMerchanService;
	@Autowired
	private OfficeService officeService;

	@ModelAttribute
	public DayReportDoorMerchan get(@RequestParam(required = false) String id) {
		DayReportDoorMerchan entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = dayReportDoorMerchanService.get(id);
		}
		if (entity == null) {
			entity = new DayReportDoorMerchan();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:dayReportDoorMerchan:view")
	@RequestMapping(value = { "list", "" })
	public String list(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		/* 初始化开始时间和结束时间为当前时间 wzj 2017-11-15 begin */
		// 查询条件： 开始时间
		if (dayReportDoorMerchan.getCreateTimeStart() != null) {
			// 查询时间精确到分钟 变更人：lihe 2020-04-16
			// dayReportDoorMerchan
			// .setSearchDateStart(DateUtils.foramtSearchDate(dayReportDoorMerchan.getCreateTimeStart()));
			dayReportDoorMerchan.setSearchDateStart(DateUtils.formatDateTime(
					DateUtils.getDateStartOrEnd(dayReportDoorMerchan.getCreateTimeStart(), "yyyy-MM-dd HH:mm", ":00")));
		}
		/** 初始化查询非已代付状态记录(优化查询速度) add by lihe 2020-05-21 */
		if (dayReportDoorMerchan.getCreateTimeStart() == null
				&& !"0".equals(dayReportDoorMerchan.getUninitDateFlag())) {
			dayReportDoorMerchan.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getLastMonthFromNow("week")));
			dayReportDoorMerchan.setCreateTimeStart(DateUtils.parseDate(dayReportDoorMerchan.getSearchDateStart()));
		}
		// 查询条件： 结束时间
		if (dayReportDoorMerchan.getCreateTimeEnd() != null) {
			// 查询时间精确到分钟 变更人：lihe 2020-04-16
			// dayReportDoorMerchan.setSearchDateEnd(DateUtils.foramtSearchDate(dayReportDoorMerchan.getCreateTimeEnd()));
			dayReportDoorMerchan.setSearchDateEnd(DateUtils.formatDateTime(
					DateUtils.getDateStartOrEnd(dayReportDoorMerchan.getCreateTimeEnd(), "yyyy-MM-dd HH:mm", ":59")));
		}
		/* end */
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getParentId() + "%')");
		} else {
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + userInfo.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		}
		// 默认显示门店存款结算记录
		if (StringUtils.isBlank(dayReportDoorMerchan.getSettlementType())) {
			dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.doorSave);
		}
		Page<DayReportDoorMerchan> page = dayReportDoorMerchanService
				.findPage(new Page<DayReportDoorMerchan>(request, response), dayReportDoorMerchan);

		// 存款结算类型，计算合计数据
		if (dayReportDoorMerchan.getSettlementType().equals(DoorOrderConstant.SettlementType.doorSave)
				|| dayReportDoorMerchan.getSettlementType().equals(DoorOrderConstant.SettlementType.traditionalSave)
				|| dayReportDoorMerchan.getSettlementType()
						.equals(DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE)) {
			List<DayReportDoorMerchan> total = Lists.newArrayList();
			// 日结合计方法变更:原为dayReportMainTotal,改为getSummation 变更人：lihe 2020-04-15
			DayReportDoorMerchan result = dayReportDoorMerchanService.getSummation(dayReportDoorMerchan);
			total.add(result);
			total.addAll(page.getList());
			page.setList(total);
		}
		model.addAttribute("page", page);
		model.addAttribute("settlementType", dayReportDoorMerchan.getSettlementType());
		return "modules/doorOrder/v01/dayReport/dayReportDoorMerchanList";
	}

	@RequiresPermissions("doorOrder:v01:dayReportDoorError:view")
	@RequestMapping(value = "errorList")
	public String errorList(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<DayReportDoorMerchan> page = dayReportDoorMerchanService
				.findErrorPage(new Page<DayReportDoorMerchan>(request, response), dayReportDoorMerchan);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/dayReport/dayReportDoorErrorList";
	}

	@RequiresPermissions("doorOrder:v01:dayReportDoorMerchan:view")
	@RequestMapping(value = "form")
	public String form(DayReportDoorMerchan dayReportDoorMerchan, Model model) {
		model.addAttribute("dayReportDoorMerchan", dayReportDoorMerchan);
		return "modules/doorOrder/v01/dayReport/dayReportDoorMerchanForm";
	}

	@RequiresPermissions("doorOrder:v01:dayReportDoorMerchan:edit")
	@RequestMapping(value = "save")
	public String save(DayReportDoorMerchan dayReportDoorMerchan, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, dayReportDoorMerchan)) {
			return form(dayReportDoorMerchan, model);
		}
		dayReportDoorMerchanService.save(dayReportDoorMerchan);
		addMessage(redirectAttributes, "保存门店日结列表成功！");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}

	@RequiresPermissions("doorOrder:v01:dayReportDoorMerchan:edit")
	@RequestMapping(value = "delete")
	public String delete(DayReportDoorMerchan dayReportDoorMerchan, RedirectAttributes redirectAttributes) {
		dayReportDoorMerchanService.delete(dayReportDoorMerchan);
		addMessage(redirectAttributes, "删除门店日结列表成功！");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}

	/**
	 * 
	 * @author WQJ
	 * @version 2019年7月24日 商户结算后查看结算相关的每笔明细
	 *
	 */

	@RequestMapping(value = "detailView")
	public String detailView(DoorCenterAccountsMain centerAccountsMain, DayReportDoorMerchan dayReportDoorMerchan,
			@RequestParam(required = false) String reportId, @RequestParam(required = false) String clientId,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		// 设置中心账务
		// DoorCenterAccountsMain door = new DoorCenterAccountsMain();
		// 日结主键
		centerAccountsMain.setReportId(reportId);
		// 门店机构
		centerAccountsMain.setClientId(clientId);
		Page<DoorCenterAccountsMain> page = centerAccountsMainService
				.findPageByDetail(new Page<DoorCenterAccountsMain>(request, response), centerAccountsMain, null);
		// 存款结算类型，计算合计数据
		if (dayReportDoorMerchan.getSettlementType().equals(DoorOrderConstant.SettlementType.doorSave)
				|| dayReportDoorMerchan.getSettlementType().equals(DoorOrderConstant.SettlementType.traditionalSave)
				|| dayReportDoorMerchan.getSettlementType()
						.equals(DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE)) {
			List<DoorCenterAccountsMain> total = Lists.newArrayList();
			List<DoorCenterAccountsMain> resultList = centerAccountsMainService.dayReportTotal(centerAccountsMain);
			total.addAll(resultList);
			total.addAll(page.getList());
			page.setList(total);
		}
		model.addAttribute("centerAccountsMain", centerAccountsMain);
		model.addAttribute("page", page);
		model.addAttribute("settlementType", dayReportDoorMerchan.getSettlementType());
		return "modules/doorOrder/v01/dayReport/dayReportMerchanDetailList";
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月10日 公司代付/中心汇款功能
	 * 
	 */
	@RequestMapping(value = "paid")
	public synchronized String paid(@RequestParam(required = false) String id, HttpServletRequest request,
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 日结记录
		DayReportDoorMerchan dayReportDoorMerchan = dayReportDoorMerchanService.get(id);
		// 判断该笔结算记录是否已经代付过
		if (dayReportDoorMerchan.getPaidStatus().equals(DoorOrderConstant.AccountPaidStatus.HASPAID)) {
			return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/dayReportDoorMerchan/?repage";
		}
		// 获取商户机构ID
		String officeId = dayReportDoorMerchan.getOfficeId();
		try {
			dayReportDoorMerchanService.paid(id);
			// 保存成功！
			message = msg.getMessage("message.A1015",
					new String[] { StoreCommonUtils.getOfficeById(officeId).getName() }, locale);
			addMessage(redirectAttributes, message);
			// 销毁session中保存的汇款记录ID
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
		}
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}

	/**
	 * 返回
	 * 
	 * @author wqj
	 * @version 2019-07-09
	 */
	@RequestMapping(value = "back")
	public String back(DayReportDoorMerchan dayReportDoorMerchan, Model model, RedirectAttributes redirectAttributes) {
		// 传递结算方式参数
		redirectAttributes.addAttribute("settlementType", dayReportDoorMerchan.getSettlementType());
		if (DoorOrderConstant.SettlementType.saveError.equals(dayReportDoorMerchan.getSettlementType())) {
			return "redirect:" + Global.getAdminPath()
					+ "/doorOrder/v01/dayReportDoorMerchan/errorList?isSearch=true&repage";
		} else {
			return "redirect:" + Global.getAdminPath()
					+ "/doorOrder/v01/dayReportDoorMerchan/list?isSearch=true&repage";
		}
	}

	/**
	 * 门店日结导出列表
	 *
	 * @author XL
	 * @version 2019年10月30日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "exportDayReport")
	public String exportDayReport(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// 查询条件
			DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
			// 选中日结列表ID
			doorCenterAccountsMain.setDayReportIds(
					Arrays.asList(dayReportDoorMerchan.getDayReportIds().split(Constant.Punctuation.COMMA, -1)));
			// 门店日结导出列表
			List<DayReportExport> dayReportExportList = centerAccountsMainService
					.findDayReportExportList(doorCenterAccountsMain);
			// 合计行
			DayReportExport dayReportExportTotal = new DayReportExport();
			// 初始化
			BigDecimal amountZhang = new BigDecimal(0);
			BigDecimal amountForce = new BigDecimal(0);
			BigDecimal amountError = new BigDecimal(0);
			BigDecimal amountOther = new BigDecimal(0);
			BigDecimal amount = new BigDecimal(0);
			// 合计计算
			for (DayReportExport dayReportExport : dayReportExportList) {
				// 张数金额
				amountZhang = amountZhang.add(dayReportExport.getAmountZhang());
				// 强制金额
				amountForce = amountForce.add(dayReportExport.getAmountForce());
				// 其他金额
				amountOther = amountOther.add(dayReportExport.getAmountOther());
				// 差错金额
				amountError = amountError.add(dayReportExport.getAmountError());
				// 总金额
				amount = amount.add(dayReportExport.getAmount());
			}
			dayReportExportTotal.setEquipmentId("合计");
			dayReportExportTotal.setAmountZhang(amountZhang);
			dayReportExportTotal.setAmountForce(amountForce);
			dayReportExportTotal.setAmountOther(amountOther);
			dayReportExportTotal.setAmountError(amountError);
			dayReportExportTotal.setAmount(amount);
			dayReportExportList.add(dayReportExportTotal);
			// 模板文件名
			String fileName = msg.getMessage("door.accountManage.merchanReportTemplate", null, locale);
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.accountManage.merchanReport", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DayReportExport.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, dayReportExportList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.accountManage.merchanReport", null, locale) + DateUtils.getDate() + ".xls");
		} catch (Exception e) {
			return list(dayReportDoorMerchan, request, response, model);
		}
		return null;
	}

	/**
	 * 
	 * 中心汇款功能
	 * 
	 * @author XL
	 * @version 2019年11月18日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "paidByIds")
	public synchronized String paidByIds(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 汇款ID列表
			List<String> idList = Arrays
					.asList(dayReportDoorMerchan.getDayReportIds().split(Constant.Punctuation.COMMA, -1));
			// 商户日结列表
			List<DayReportDoorMerchan> dayReportDoorMerchanList = Lists.newArrayList();
			// 汇款金额
			BigDecimal payAmount = new BigDecimal(0);
			for (String id : idList) {
				// 日结信息
				DayReportDoorMerchan dayReport = dayReportDoorMerchanService.get(id);
				if (dayReport != null) {
					dayReportDoorMerchanList.add(dayReport);
					if(dayReport.getActuralReportAmount()!=null){
						payAmount = payAmount.add(dayReport.getActuralReportAmount());
					}else{
						payAmount = payAmount.add(dayReport.getTotalAmount());
					}
					// 判断该笔结算记录商户是否已确认
					if (DoorOrderConstant.AccountPaidStatus.TOCONFIRM.equals(dayReport.getPaidStatus())) {
						throw new BusinessException("message.A1039", "", new String[] {});
						// 判断该笔结算记录是否已经代付过
					} else if (!DoorOrderConstant.AccountPaidStatus.NOTHASPAID.equals(dayReport.getPaidStatus())) {
						throw new BusinessException("message.A1036", "", new String[] {});
					}
				}
			}
			/*  hzy  2020/06/15 
			for (DayReportDoorMerchan dayReport : dayReportDoorMerchanList) {
				// 判断该笔结算记录商户是否已确认
				if (DoorOrderConstant.AccountPaidStatus.TOCONFIRM.equals(dayReport.getPaidStatus())) {
					throw new BusinessException("message.A1039", "", new String[] {});
					// 判断该笔结算记录是否已经代付过
				} else if (!DoorOrderConstant.AccountPaidStatus.NOTHASPAID.equals(dayReport.getPaidStatus())) {
					throw new BusinessException("message.A1036", "", new String[] {});
				}
			}*/
			// 公司余额查询
			Office company = officeService.getPlatform().get(0);
			BigDecimal companyAmount = DoorCommonUtils.getCompanyTotalAmount(company.getId());
			if (payAmount.compareTo(companyAmount) == 1) {
				// 公司余额不足
				throw new BusinessException("message.A1014", "", new String[] { company.getName(), "现金" });
			}
			dayReportDoorMerchanService.paid(idList);
			message = msg.getMessage("message.A1038", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		} catch (Exception e) {
			message = msg.getMessage("message.A1037", null, locale);
		}
		addMessage(redirectAttributes, message);
		// 传递结算方式参数
		redirectAttributes.addAttribute("settlementType", dayReportDoorMerchan.getSettlementType());
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}

	/**
	 * 
	 * Title: confirm
	 * <p>
	 * Description: 商户确认汇款金额
	 * </p>
	 * 
	 * @author: lihe
	 * @return String 返回类型
	 */
	@RequestMapping(value = "confirm")
	public String confirm(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 确认ID列表
			List<String> idList = Arrays
					.asList(dayReportDoorMerchan.getDayReportIds().split(Constant.Punctuation.COMMA, -1));
			// 商户日结列表
			List<DayReportDoorMerchan> dayReportDoorMerchanList = Lists.newArrayList();
			// 确认金额
			BigDecimal payAmount = new BigDecimal(0);
			for (String id : idList) {
				// 日结信息
				DayReportDoorMerchan dayReport = dayReportDoorMerchanService.get(id);
				if (dayReport != null) {
					dayReportDoorMerchanList.add(dayReport);
					payAmount = payAmount.add(dayReport.getTotalAmount());
					if (!DoorOrderConstant.AccountPaidStatus.TOCONFIRM.equals(dayReport.getPaidStatus())) {
						throw new BusinessException("message.A1045", "", new String[] {});
					}
				}
			}
			/*  hzy  2020/06/18  */
			/*for (DayReportDoorMerchan dayReport : dayReportDoorMerchanList) {
				// 判断该笔结算记录商户是否已确认
				if (!DoorOrderConstant.AccountPaidStatus.TOCONFIRM.equals(dayReport.getPaidStatus())) {
					throw new BusinessException("message.A1045", "", new String[] {});
				}
			}*/
			dayReportDoorMerchanService.batchConfirm(idList);
			message = msg.getMessage("message.A1044", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		} catch (Exception e) {
			message = msg.getMessage("message.A1046", null, locale);
		}
		addMessage(redirectAttributes, message);
		// 传递结算方式参数
		redirectAttributes.addAttribute("settlementType", dayReportDoorMerchan.getSettlementType());
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}

	/**
	 * 
	 * 差错结算功能
	 * 
	 * @author WQJ
	 * @version 2019年12月3日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "saveErrorReport")
	public synchronized String saveErrorReport(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 该中心所有门店差错结算
			Office office = UserUtils.getUser().getOffice();
			User user = UserUtils.getUser();
			Date date = new Date();
			dayReportDoorMerchanService.saveErrorReport(office, date, user);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			// 传递结算方式参数，跳转到差错结算页面
			redirectAttributes.addAttribute("settlementType", DoorOrderConstant.SettlementType.saveError);
			return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
		}
		message = msg.getMessage("message.A1040", null, locale);
		addMessage(redirectAttributes, message);
		// 传递结算方式参数，跳转到差错结算页面
		redirectAttributes.addAttribute("settlementType", DoorOrderConstant.SettlementType.saveError);
		return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/errorList?repage";
	}

	/**
	 * 
	 * 差错处理功能
	 * 
	 * @author WQJ
	 * @version 2019年12月3日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "handleByIds")
	public synchronized String handleByIds(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 差错ID列表
			List<String> idList = Arrays
					.asList(dayReportDoorMerchan.getDayReportIds().split(Constant.Punctuation.COMMA, -1));
			// 差错结算列表
			List<DayReportDoorMerchan> dayReportDoorMerchanList = Lists.newArrayList();
			for (String id : idList) {
				// 日结信息
				DayReportDoorMerchan dayReport = dayReportDoorMerchanService.get(id);
				if (dayReport != null) {
					dayReportDoorMerchanList.add(dayReport);
					// 判断该笔差错记录是否已经处理过
					if (!DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP.equals(dayReport.getPaidStatus())) {
						throw new BusinessException("message.A1041", "", new String[] {});
					}
				}
			}
			/*for (DayReportDoorMerchan dayReport : dayReportDoorMerchanList) {
				// 判断该笔差错记录是否已经处理过
				if (!DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP.equals(dayReport.getPaidStatus())) {
					throw new BusinessException("message.A1041", "", new String[] {});
				}
			}*/
			dayReportDoorMerchanService.handle(idList);
			message = msg.getMessage("message.A1042", null, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		} catch (Exception e) {
			message = msg.getMessage("message.A1043", null, locale);
		}
		addMessage(redirectAttributes, message);
		// 传递结算方式参数，跳转到差错结算页面
		redirectAttributes.addAttribute("settlementType", DoorOrderConstant.SettlementType.saveError);
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/dayReportDoorMerchan/errorList?repage";
	}

	/**
	 * 门店日结导出账户相关列表
	 *
	 * @author ZXK
	 * @version 2019年12月16日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "exportDayReportAccount")
	public String exportDayReportAccount(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// 选中日结列表ID
			if ("".equals(dayReportDoorMerchan.getDayReportIds())) {
				// 无id列表 日结列表设置为null(全选)
				dayReportDoorMerchan.setDayReportIdList(null);
				// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
				User user = UserUtils.getUser();
				if (Constant.OfficeType.CLEAR_CENTER.equals(user.getOffice().getType())) {
					dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + user.getOffice().getId()
							+ " OR o2.parent_ids LIKE '%" + user.getOffice().getParentId() + "%')");
				} else {
					dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + user.getOffice().getId()
							+ " OR o2.parent_ids LIKE '%" + user.getOffice().getId() + "%')");
				}
				// 结算类型(条件)
				//dayReportDoorMerchan.setSettlementType(dayReportDoorMerchan.getSettlementType());
				// 查询条件： 开始时间
				if (dayReportDoorMerchan.getCreateTimeStart() != null) {
					dayReportDoorMerchan.setSearchDateStart(
							DateUtils.foramtSearchDate(DateUtils.getDateStart(dayReportDoorMerchan.getCreateTimeStart())));
				}
				// 查询条件： 结束时间
				if (dayReportDoorMerchan.getCreateTimeEnd() != null) {
					dayReportDoorMerchan.setSearchDateEnd(
							DateUtils.foramtSearchDate(DateUtils.getDateEnd(dayReportDoorMerchan.getCreateTimeEnd())));
				}

			} else {
				dayReportDoorMerchan.setDayReportIdList(
						Arrays.asList(dayReportDoorMerchan.getDayReportIds().split(Constant.Punctuation.COMMA, -1)));
			}

			// 门店日结导出列表
			List<DayReportAccountExport> dayReportExportList = dayReportDoorMerchanService
					.findDayReportAccountExportList(dayReportDoorMerchan);
			// 合计行
			DayReportAccountExport dayReportExportTotal = new DayReportAccountExport();
			// 初始化
			BigDecimal amount = new BigDecimal(0);
			// 序号
			Integer number = 1;
			// 合计计算
			for (DayReportAccountExport dayReportExport : dayReportExportList) {
				// 序号
				dayReportExport.setNumber(number.toString());
				number++;
				// 收款方行别代码
				dayReportExport.setPayeeBankCode("01");
				// 用途(往来款)
				dayReportExport.setUse(DoorOrderConstant.ExportDayReport.useName);
				// 是否短信通知收款人
				dayReportExport.setShortNote("0");
				// 收款方联行号
				// 空值信息
				dayReportExport.setPayeeBankRelevance("");
				dayReportExport.setClientSerialNo("");
				dayReportExport.setPayeePhone("");
				dayReportExport.setShortNoteInfo("");
				// 总金额
				amount = amount.add(dayReportExport.getAmount());
				// 壳牌(32250198903600000244)在导出时 七位码前需要加'JYZ'
				String payeeAccountId = dayReportExport.getPayeeAccountId();
				if (DoorOrderConstant.ExportDayReport.shellAccount.equals(payeeAccountId)) {
					dayReportExport.setRemark("JYZ" + dayReportExport.getRemark());
				}
			}
			dayReportExportTotal.setNumber("合计");
			dayReportExportTotal.setAmount(amount);
			dayReportExportList.add(dayReportExportTotal);
			// 模板文件名
			String fileName = msg.getMessage("door.accountManage.bankAccountInfoTemplate", null, locale);
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.accountManage.bankAccountInfo", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DayReportAccountExport.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, dayReportExportList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.accountManage.bankAccountInfo", null, locale) + DateUtils.getDate() + ".xls");
		} catch (Exception e) {
			return list(dayReportDoorMerchan, request, response, model);
		}
		return null;
	}

	/**
	 * 
	 * 传统存款结算功能
	 * 
	 * @author WQJ
	 * @version 2019年12月17日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "traditionalSaveReport")
	public synchronized String traditionalSaveReport(DayReportDoorMerchan dayReportDoorMerchan,
			HttpServletRequest request, HttpServletResponse response, Model model,
			RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 该中心所有门店传统存款结算
			Office office = UserUtils.getUser().getOffice();
			User user = UserUtils.getUser();
			Date date = new Date();
			dayReportDoorMerchanService.traditionalSaveReport(office, date, user, dayReportDoorMerchan);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			// 传递结算方式参数，跳转到传统存款结算页面
			redirectAttributes.addAttribute("settlementType", DoorOrderConstant.SettlementType.traditionalSave);
			return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
		}
		message = msg.getMessage("message.A1047", null, locale);
		addMessage(redirectAttributes, message);
		// 传递结算方式参数，跳转到传统存款结算页面
		redirectAttributes.addAttribute("settlementType", DoorOrderConstant.SettlementType.traditionalSave);
		return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}

	/**
	 * 登录用户验证是否是七位码(张家港)机构
	 * 
	 * @author ZXK
	 * @version 2019年12月18日
	 * @param
	 * @return
	 */
	@RequestMapping(value = "checkSevenCode")
	@ResponseBody
	public String getRemarks() {
		User user = UserUtils.getUser();
		if (!user.getOffice().getParentIds().contains(Global.getConfig(DoorOrderConstant.OfficeCode.ZHANGJIAGANG))) {
			return gson.toJson(false);
		}
		return gson.toJson(true);
	}

	/**
	 * 门店日结导出列表
	 *
	 * @author lihe
	 * @version 2019年12月31日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "exportErrorList")
	public String exportErrorList(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// 选中日结列表ID
			if (StringUtils.isNotBlank(dayReportDoorMerchan.getDayReportIds())) {
				dayReportDoorMerchan.setDayReportIdList(
						Arrays.asList(dayReportDoorMerchan.getDayReportIds().split(Constant.Punctuation.COMMA, -1)));
			}
			// 差错日结导出列表
			List<DayReportDoorMerchan> dayReportExportList = dayReportDoorMerchanService
					.findErrorList(dayReportDoorMerchan);
			// 合计行
			DayReportDoorMerchan dayReport = new DayReportDoorMerchan();
			// // 初始化
			BigDecimal totalAmount = new BigDecimal(0);
			BigDecimal paidAmount = new BigDecimal(0);
			// 合计计算
			for (DayReportDoorMerchan dayReportMerchan : dayReportExportList) {
				// 差错金额
				if (null != dayReportMerchan.getTotalAmount()) {
					totalAmount = totalAmount.add(dayReportMerchan.getTotalAmount());
				}
				// 处理金额
				if (null != dayReportMerchan.getPaidAmount()) {
					paidAmount = paidAmount.add(dayReportMerchan.getPaidAmount());
				}
			}
			dayReport.setOfficeName("合计");
			dayReport.setTotalAmount(totalAmount);
			dayReport.setPaidAmount(paidAmount);
			dayReportExportList.add(dayReport);
			// 模板文件名
			String fileName = msg.getMessage("door.accountManage.errorReportTemplate", null, locale);
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
					msg.getMessage("door.accountManage.errorReport", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DayReportDoorMerchan.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, dayReportExportList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName,
					msg.getMessage("door.accountManage.errorReport", null, locale) + DateUtils.getDate() + ".xls");
		} catch (Exception e) {
			return errorList(dayReportDoorMerchan, request, response, model);
		}
		return null;
	}

	/**
	 * 
	 * 电子线下存款结算功能
	 * 
	 * @author WQJ
	 * @version 2019年12月17日
	 * @param dayReportDoorMerchan
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "offlineSaveReport")
	public synchronized String offlineSaveReport(DayReportDoorMerchan dayReportDoorMerchan, HttpServletRequest request,
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 该中心所有门店电子线下存款结算
			Office office = UserUtils.getUser().getOffice();
			User user = UserUtils.getUser();
			Date date = new Date();
			dayReportDoorMerchanService.offlineSaveReport(office, date, user);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(redirectAttributes, message);
			// 传递结算方式参数，跳转到电子线下结算页面
			redirectAttributes.addAttribute("settlementType", DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE);
			return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
		}
		message = msg.getMessage("message.A1049", null, locale);
		addMessage(redirectAttributes, message);
		// 传递结算方式参数，跳转到电子线下结算页面
		redirectAttributes.addAttribute("settlementType", DoorOrderConstant.SettlementType.ELECTRONIC_OFFLINE_SAVE);
		return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}
	
	/**
	 * 
	 * 保存实际结算金额、备注
	 * 
	 * @author GJ
	 * @version 2020年05月08日
	 * @param dayReportDoorMerchan
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@RequiresPermissions("doorOrder:v01:dayReportActuralAmout:view")
	@RequestMapping(value = "saveActuralReprotAmount")
	public String saveActuralReprotAmount(DayReportDoorMerchan dayReportDoorMerchan, RedirectAttributes redirectAttributes) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		//是否存在状态改变的数据
		boolean flag = false;
		User updateUser = UserUtils.getUser();
		Date updateDate = new Date();
		// 确认ID列表
		List<String> idList = Arrays
				.asList(dayReportDoorMerchan.getDayReportIds().split(Constant.Punctuation.COMMA, -1));
		// 金额列表
		List<String> amountList = Arrays
				.asList(dayReportDoorMerchan.getActuralReportAmounts().split(Constant.Punctuation.COMMA, -1));
		//保存前代付状态列表
		List<String> statusList = Arrays
				.asList(dayReportDoorMerchan.getPaidStatuss().split(Constant.Punctuation.COMMA, -1));
		//备注列表
		List<String> remarksList = Arrays
				.asList(dayReportDoorMerchan.getRemarksList().split(Constant.Punctuation.COMMA, -1));
		// 商户日结列表
//		List<DayReportDoorMerchan> dayReportDoorMerchanList = Lists.newArrayList();
		for (int i = 0; i < idList.size(); i++) {
			// 日结信息
			DayReportDoorMerchan dayReport = dayReportDoorMerchanService.get(idList.get(i));
			if (dayReport != null) {
				if(statusList.get(i + 1).equals(dayReport.getPaidStatus())) {
					//是否更新与更新相关信息
					boolean updateFlag = false;
					//若前端传过来的实际日结金额不为空
					if(StringUtils.isNotBlank(amountList.get(i))) {
						//若后台当前保存状态为空或前后台数据不一致状态下更新与与更新相关信息
						if(dayReport.getActuralReportAmount() == null || 
								(dayReport.getActuralReportAmount() != null && !dayReport.getActuralReportAmount().equals(new BigDecimal(amountList.get(i))))) {
							updateFlag = true;
						}
						BigDecimal amount = new BigDecimal(amountList.get(i)); 
						dayReport.setActuralReportAmount(amount);
					} else {
						if(dayReport.getActuralReportAmount() != null) {
							updateFlag = true;
						}
						dayReport.setActuralReportAmount(null);
					}
					if(StringUtils.isNotBlank(remarksList.get(i))) {
						if(dayReport.getRemarks() == null || 
								(dayReport.getRemarks() != null && !dayReport.getRemarks().equals(remarksList.get(i)))) {
							updateFlag = true;
						}
						dayReport.setRemarks(remarksList.get(i));
					} else {
						if(dayReport.getRemarks() != null) {
							updateFlag = true;
						}
						dayReport.setRemarks(null);
					}
					if(updateFlag) {
						dayReport.setUpdateBy(updateUser);
						dayReport.setUpdateDate(updateDate);
						dayReport.setUpdateName(updateUser.getName());
					}
//					dayReportDoorMerchanList.add(dayReport);
					dayReportDoorMerchanService.update(dayReport);
				} else {
					if(statusList.get(i + 1).equals(AccountPaidStatus.TOCONFIRM)) {
						flag = true;
					}
				}
			}
		}
		String message = "保存成功!";
		if(flag) {
			message = "部分数据保存失败,代付状态已发生改变!";
		}
		addMessage(redirectAttributes, message);
		// 传递结算方式参数
		redirectAttributes.addAttribute("settlementType", dayReportDoorMerchan.getSettlementType());
		return "redirect:" + adminPath + "/doorOrder/v01/dayReportDoorMerchan/?repage";
	}
	
}