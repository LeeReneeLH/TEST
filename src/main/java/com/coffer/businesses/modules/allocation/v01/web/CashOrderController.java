package com.coffer.businesses.modules.allocation.v01.web;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.WorkFlowInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.allocation.v01.service.HandInWorkFlowService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@SessionAttributes({ "allocationOrderCash" })
@RequestMapping(value = "${adminPath}/allocation/v01/cashOrder")
public class CashOrderController extends BaseController {
	@Autowired
	private AllocationService allocationService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	@Autowired
	private HandInWorkFlowService handInWorkFlowService;

	/** 现金预约处理种别（添加） */
	private static final String SAVE_TYPE_INSERT = "insert";
	/** 现金预约处理种别（删除） */
	private static final String SAVE_TYPE_DELETE = "delete";

	/**
	 * @author qph
	 * @version 2017年7月6日
	 * 
	 *          根据流水号取得现金预约信息
	 * @param allId
	 *            流水号
	 * @return 现金预约信息
	 */
	@ModelAttribute
	public AllAllocateInfo get(@RequestParam(required = false) String allId) {

		AllAllocateInfo entity = null;
		if (StringUtils.isNoneBlank(allId)) {
			entity = allocationService.getAllocate(allId);
		}
		if (entity == null) {
			entity = new AllAllocateInfo();
		}
		return entity;
	}

	/**
	 * @author qph
	 * @version 2017年7月6日
	 * 
	 *          根据查询条件，查询现金预约信息
	 * @param allocateInfo
	 *            现金预约信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @param status
	 *            Session状态
	 * @return 现金预约信息列表页面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(AllAllocateInfo allocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model, SessionStatus status) {

		model.addAttribute("allocateInfo", allocateInfo);

		// 设置业务种别(现金预约)
		allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);	
		//------------- 增加分组，移除机构设置   XL 2018-12-29 ------------ BEGIN
		/*
		 * // 如果不是系统管理员，设置用户的机构号，只能查询当前机构 if
		 * (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().
		 * getUserType())) { // 如果是网点，设置用户的机构号，只能查询当前机构 String usertype =
		 * UserUtils.getUser().getUserType(); if
		 * (AllocationConstant.SysUserType.BANK_OUTLETS_MANAGER.equals(usertype)
		 * || AllocationConstant.SysUserType.BANK_OUTLETS_OPT.equals(UserUtils.
		 * getUser().getUserType())) {
		 * allocateInfo.setrOffice(UserUtils.getUser().getOffice()); } else { //
		 * 否则查询本金库 allocateInfo.setaOffice(UserUtils.getUser().getOffice()); } }
		 */
		//------------- 增加分组，移除机构设置   XL 2018-12-29 ------------- END
		// 查询现金预约信息
		Page<AllAllocateInfo> page = allocationService
				.findOrderCashAndTempCash(new Page<AllAllocateInfo>(request, response), allocateInfo);
		model.addAttribute("page", page);
		return "modules/allocation/v01/out/order/cashorder/cashOrderList";
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年11月9日 打印页面
	 * 
	 * @param allocateInfo
	 *            打印条件
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "print")
	public String print(AllAllocateInfo allocateInfo, Model model) {
		for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {
			item.setRegistType("");
			String strMapKey = allocationService.getAllAllocateItemMapKey(item);
			allocateInfo.getAllAllocateItemMap().put(strMapKey, item);
		}
		// 重新计算金额
		Map<String, AllAllocateItem> countMap = allocationService
				.countCashByCurrencyFromMap(allocateInfo.getAllAllocateItemMap());
		allocateInfo.setCountItemMap(countMap);
		Office rOffice = StoreCommonUtils.getOfficeById(allocateInfo.getrOffice().getId());
		allocateInfo.setrOffice(rOffice);
		model.addAttribute("allocationSameTradeOrder", allocateInfo);
		return "modules/allocation/v01/out/order/cashorder/printCashOrderSameTradeDetail";
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月1日 行内配款打印页面
	 * 
	 * @param allocateInfo
	 *            打印条件
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "printCashOrder")
	public String printCashOrder(AllAllocateInfo allocateInfo, Model model) {
		// 预约物品
		Map<String, AllAllocateItem> orderMap = Maps.newTreeMap();
		// 配款物品
		Map<String, AllAllocateItem> quotaMap = Maps.newTreeMap();
		AllAllocateItem tempItem = null;
		for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {
			String strMapKey = allocationService.getAllAllocateItemMapKey1(item);
			tempItem = new AllAllocateItem();
			tempItem.setCurrency(item.getscurrency(item.getGoodsId()));
			if (AllocationConstant.confirmFlag.Appointment.equals(item.getConfirmFlag())) {
				tempItem.setMoneyNumber(item.getMoneyNumber());
				tempItem.setMoneyAmount(item.getMoneyAmount());
				orderMap.put(strMapKey, tempItem);
			} else {
				tempItem.setConfirmNumber(item.getMoneyNumber());
				tempItem.setConfirmAmount(item.getMoneyAmount());
				quotaMap.put(strMapKey, tempItem);
			}
			allocateInfo.getAllAllocateItemMap().put(strMapKey, item);
		}

		// 重新预约计算金额
		Map<String, AllAllocateItem> countMap = allocationService.countCashByCurrencyFromMap(orderMap);
		allocateInfo.setCountItemMap(countMap);
		// 重新配款计算金额
		Map<String, AllAllocateItem> countQuotaMap = allocationService.countQuotaCashByCurrencyFromMap(quotaMap);
		allocateInfo.setCountQuotaItemMap(countQuotaMap);

		// 合并预约、配款信息
		allocateInfo = allocationService.unionOrderInfo(allocateInfo);

		Office rOffice = StoreCommonUtils.getOfficeById(allocateInfo.getrOffice().getId());
		allocateInfo.setrOffice(rOffice);
		model.addAttribute("allocationOrder", allocateInfo);
		return "modules/allocation/v01/out/order/cashorder/printCashOrderDetail";
	}

	/**
	 * @author qph
	 * @version 2017年7月6日
	 * 
	 *          根据查询条件，查询现金预约详细信息
	 * @param allocateInfo
	 *            预约信息
	 * @param model
	 *            页面Session信息
	 * @param sessionInitFlag
	 *            页面Session初始化标识
	 * @param status
	 *            Session状态
	 * @return 现金预约详细画面
	 */
	@RequestMapping(value = "form")
	public String form(AllAllocateInfo allocateInfo, Model model, boolean sessionInitFlag, SessionStatus status,
			HttpServletRequest request, HttpServletResponse response) {

		Office loginUserOffice = StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId());
		if (AllocationConstant.PageType.PointAdd.equals(allocateInfo.getPageType())
				&& Constant.TradeFlag.Trade.equals(loginUserOffice.getTradeFlag())) {
			Locale locale = LocaleContextHolder.getLocale();
			// [提示]：同业网点目前不能进行调拨登记业务。
			String message = msg.getMessage("message.I2008", null, locale);
			addMessage(model, message);
			allocateInfo.setSearchFlag("init");
			return list(allocateInfo, request, response, model, status);
		}
		// 判断网店是否已经登记并确认，若已确认，跳转至查看界面。
		if (AllocationConstant.PageType.PointAdd.equals(allocateInfo.getPageType())
				&& allocationService.findAllocation(allocateInfo) != null) {
			List<String> businessTypeList = Lists.newArrayList();
			AllAllocateInfo resultallallocateInfo = new AllAllocateInfo();
			businessTypeList.add(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
			resultallallocateInfo.setBusinessTypes(businessTypeList);

			// 设置用户的机构号，只能查询当前机构
			resultallallocateInfo.setrOffice(UserUtils.getUser().getOffice());

			resultallallocateInfo = allocationService.orderIsExit(resultallallocateInfo);

			if (resultallallocateInfo != null
					&& (AllocationConstant.Status.BetweenConfirm.equals(resultallallocateInfo.getStatus())
							|| AllocationConstant.Status.CashOrderQuotaYes.equals(resultallallocateInfo.getStatus()))) {
				resultallallocateInfo.setPageType(allocateInfo.getPageType());
				Locale locale = LocaleContextHolder.getLocale();
				// [提示]：本机构已有预约已确认信息，只能查看明细，无法重新登记与修改！
				String message = msg.getMessage("message.E2047", new String[] { allocateInfo.getAllId() }, locale);
				addMessage(model, message);
				return view(resultallallocateInfo, model);
			} else if (resultallallocateInfo != null
					&& AllocationConstant.Status.Register.equals(resultallallocateInfo.getStatus())) {
				Locale locale = LocaleContextHolder.getLocale();
				// [提示]：本机构已有预约登记信息，无法重新登记，请修改！
				String message = msg.getMessage("message.E2048", new String[] { allocateInfo.getAllId() }, locale);
				addMessage(model, message);
			}
		}
		// 现金详细Session
		if (!sessionInitFlag) {
			// 预约物品
			Map<String, AllAllocateItem> orderMap = Maps.newTreeMap();
			// 配款物品
			Map<String, AllAllocateItem> quotaMap = Maps.newTreeMap();

			AllAllocateItem tempItem = null;

			if (AllocationConstant.PageType.PointEdit.equals(allocateInfo.getPageType())
					&& allocationService.findAllocation(allocateInfo) == null) {
				Locale locale = LocaleContextHolder.getLocale();
				// [操作失败]：流水单号[{0}]对应调拨信息不存在！
				String message = msg.getMessage("message.E2029", new String[] { allocateInfo.getAllId() }, locale);
				addMessage(model, message);
				allocateInfo.setSearchFlag("init");
				return list(allocateInfo, request, response, model, status);
			}
			if (StringUtils.isNotBlank(allocateInfo.getAllId())) {
				if (AllocationConstant.Status.CashOrderQuotaYes.equals(allocateInfo.getStatus())
						&& AllocationConstant.PageType.PointEdit.equals(allocateInfo.getPageType())) {
					Locale locale = LocaleContextHolder.getLocale();
					// [修改失败]：流水单号[{0}]当前状态为[{1}]，不能修改！
					String message = msg
							.getMessage("message.E2019",
									new String[] { allocateInfo.getAllId(),
											DictUtils.getDictLabel(allocateInfo.getStatus(), "quota_status", "") },
									locale);
					addMessage(model, message);
					allocateInfo.setSearchFlag("init");
					return list(allocateInfo, request, response, model, status);
				}
				for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {
					String strMapKey = allocationService.getAllAllocateItemMapKey1(item);
					tempItem = new AllAllocateItem();
					tempItem.setCurrency(item.getscurrency(item.getGoodsId()));
					if (AllocationConstant.confirmFlag.Appointment.equals(item.getConfirmFlag())) {
						tempItem.setMoneyNumber(item.getMoneyNumber());
						tempItem.setMoneyAmount(item.getMoneyAmount());
						orderMap.put(strMapKey, tempItem);
					} else {
						tempItem.setConfirmNumber(item.getMoneyNumber());
						tempItem.setConfirmAmount(item.getMoneyAmount());
						quotaMap.put(strMapKey, tempItem);
					}
					allocateInfo.getAllAllocateItemMap().put(strMapKey, item);
				}

				// 重新预约计算金额
				Map<String, AllAllocateItem> countMap = allocationService.countCashByCurrencyFromMap(orderMap);
				allocateInfo.setCountItemMap(countMap);
				// 重新配款计算金额
				Map<String, AllAllocateItem> countQuotaMap = allocationService
						.countQuotaCashByCurrencyFromMap(quotaMap);
				allocateInfo.setCountQuotaItemMap(countQuotaMap);

				// 合并预约、配款信息
				allocateInfo = allocationService.unionOrderInfo(allocateInfo);
				allocateInfo.getAllAllocateItem().setAllItemsId("");
				if (AllocationConstant.Status.CashOrderQuotaNo.equals(allocateInfo.getStatus())) {
					// 初期配款金额=预约金额
					allocateInfo.setConfirmAmount(allocateInfo.getRegisterAmount());
				}
				model.addAttribute("allocationOrderCash", allocateInfo);

			} else {
				// 临时预约跳转
				if (AllocationConstant.PageType.PointTempAdd.equals(allocateInfo.getPageType())) {
					model.addAttribute("allocationOrderCash", allocateInfo);
					return "modules/allocation/v01/out/order/cashorder/cashOrderTempDetail";
				}

				// 判断当日是否已经预约，如果已经预约
				AllAllocateInfo tempAllAllocateInfo = new AllAllocateInfo();

				// 设置业务种别(现金预约)
				List<String> businessTypeList = Lists.newArrayList();
				businessTypeList.add(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
				tempAllAllocateInfo.setBusinessTypes(businessTypeList);
				// 设置用户的机构号，只能查询当前机构
				tempAllAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
				// 查询是否有预约信息
				tempAllAllocateInfo = allocationService.orderIsExit(tempAllAllocateInfo);
				if (tempAllAllocateInfo != null) {
					// 登记
					if (AllocationConstant.Status.Register.equals(tempAllAllocateInfo.getStatus())) {
						tempAllAllocateInfo.setPageType(AllocationConstant.PageType.PointEdit);
					}
					// 已确认
					else if (AllocationConstant.Status.BetweenConfirm.equals(tempAllAllocateInfo.getStatus())) {
						tempAllAllocateInfo.setPageType(AllocationConstant.PageType.PointView);
					}

					for (AllAllocateItem item : tempAllAllocateInfo.getAllAllocateItemList()) {
						String strMapKey = item.getGoodsId();
						tempItem = new AllAllocateItem();
						if (item == null || null == item.getCurrency()) {

							tempItem.setCurrency(item.getscurrency(strMapKey));
						} else {
							tempItem.setCurrency(item.getCurrency());
						}
						if (AllocationConstant.confirmFlag.Appointment.equals(item.getConfirmFlag())) {
							tempItem.setMoneyNumber(item.getMoneyNumber());
							tempItem.setMoneyAmount(item.getMoneyAmount());
							orderMap.put(strMapKey, tempItem);
						} else {
							tempItem.setConfirmNumber(item.getMoneyNumber());
							tempItem.setConfirmAmount(item.getMoneyAmount());
							quotaMap.put(strMapKey, tempItem);
						}
						tempAllAllocateInfo.getAllAllocateItemMap().put(strMapKey, item);
					}

					// 重新预约计算金额
					Map<String, AllAllocateItem> countMap = allocationService.countCashByCurrencyFromMap(orderMap);
					tempAllAllocateInfo.setCountItemMap(countMap);
					// 重新配款计算金额
					Map<String, AllAllocateItem> countQuotaMap = allocationService
							.countQuotaCashByCurrencyFromMap(quotaMap);
					tempAllAllocateInfo.setCountQuotaItemMap(countQuotaMap);
					// 合并预约、配款信息
					tempAllAllocateInfo = allocationService.unionOrderInfo(tempAllAllocateInfo);

					model.addAttribute("allocationOrderCash", tempAllAllocateInfo);
					model.addAttribute("allAllocateInfo", tempAllAllocateInfo);
				} else {

					model.addAttribute("allocationOrderCash", allocateInfo);
				}

			}

		}
		if (AllocationConstant.PageType.PointTempAdd.equals(allocateInfo.getPageType())
				|| AllocationConstant.PageType.PointTempEdit.equals(allocateInfo.getPageType())) {
			return "modules/allocation/v01/out/order/cashorder/cashOrderTempDetail";
		}
		return "modules/allocation/v01/out/order/cashorder/cashOrderDetail";
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年7月6日
	 * 
	 *          保存现金预约信息
	 * @param allocateInfo
	 *            现金预约主表信息
	 * @param allocationOrderCash
	 *            登记用信息
	 * @param detailId
	 *            现金预约详细ID
	 * @param saveType
	 *            保存类型
	 * @param model
	 *            页面Session信息
	 * @param status
	 *            页面Session状态
	 * @param redirectAttributes
	 * @return 现金预约信息列表页面
	 */
	@RequestMapping(value = "save")
	public String save(AllAllocateInfo allocateInfo,
			@ModelAttribute("allocationOrderCash") AllAllocateInfo allocationOrderCash,
			@RequestParam(required = false) String detailId, @RequestParam(required = false) String saveType,
			Model model, SessionStatus status, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response) {

		Locale locale = LocaleContextHolder.getLocale();
		if ((AllocationConstant.PageType.PointEdit.equals(allocateInfo.getPageType())
				|| (SAVE_TYPE_DELETE.equals(saveType)
						&& AllocationConstant.PageType.PointEdit.equals(allocationOrderCash.getPageType())))
				&& allocationService.getAllocate(allocateInfo.getAllId()) == null) {
			// [操作失败]：流水单号[{0}]对应调拨信息不存在！
			String message = msg.getMessage("message.E2029", new String[] { allocateInfo.getAllId() }, locale);
			addMessage(model, message);
			allocateInfo.setSearchFlag("init");
			return list(allocateInfo, request, response, model, status);
		}
		if (StringUtils.isNotBlank(allocateInfo.getAllId())
				&& AllocationConstant.Status.CashOrderQuotaYes.equals(allocateInfo.getStatus())
				&& AllocationConstant.PageType.PointEdit.equals(allocationOrderCash.getPageType())) {
			// [修改失败]：流水单号[{0}]当前状态为[{1}]，不能修改！
			String message = msg.getMessage("message.E2019", new String[] { allocateInfo.getAllId(),
					DictUtils.getDictLabel(allocateInfo.getStatus(), "quota_status", "") }, locale);
			addMessage(model, message);
			allocateInfo.setSearchFlag("init");
			return list(allocateInfo, request, response, model, status);
		}

		// 初始化处理结果
		String result = "";

		// 添加预约明细
		if (SAVE_TYPE_INSERT.equals(saveType)) {
			this.itemCopy(allocateInfo);
			result = insert(allocateInfo, allocationOrderCash, model);
			if (!AllocationConstant.SUCCESS.equals(result)) {
				return form(allocateInfo, model, true, status, request, response);
			}
			setStrupdateDate(allocateInfo, allocationOrderCash);
			// 删除预约明细
		} else if (SAVE_TYPE_DELETE.equals(saveType)) {
			this.itemCopy(allocateInfo);
			result = deleteDetail(allocationOrderCash, detailId);
			allocateInfo.setPageType(allocationOrderCash.getPageType());
			model.addAttribute("allAllocateInfo", allocationOrderCash);
			setStrupdateDate(allocateInfo, allocationOrderCash);
		} else {
			if (AllocationConstant.PageType.PointAdd.equals(allocationOrderCash.getPageType())) {

				// 同行预约重复登记检查
				AllAllocateInfo tempAllAllocateInfo = new AllAllocateInfo();

				// 设置业务种别(现金预约)
				List<String> businessTypeList = Lists.newArrayList();
				businessTypeList.add(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
				tempAllAllocateInfo.setBusinessTypes(businessTypeList);
				// 设置用户的机构号，只能查询当前机构
				tempAllAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
				// 查询是否有预约信息
				tempAllAllocateInfo = allocationService.orderIsExit(tempAllAllocateInfo);
				if (tempAllAllocateInfo != null) {
					// [登记失败]：重复登记，请参看流水单号[{0}]登记信息！
					String message = msg.getMessage("message.E2022", new String[] { tempAllAllocateInfo.getAllId() },
							locale);
					addMessage(model, message);
					allocateInfo.setSearchFlag("init");
					allocateInfo.setrOffice(tempAllAllocateInfo.getrOffice());
					allocateInfo.setStatus(tempAllAllocateInfo.getStatus());
					return list(allocateInfo, request, response, model, status);
				}
			}

			// 将goodsId拆分存入item表中

			result = saveAllcation(allocateInfo, allocationOrderCash, model, status, redirectAttributes, request,
					response);
			return result;
		}

		return form(allocateInfo, model, true, status, request, response);
	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月9日
	 * 
	 *          保存现金预约登记信息
	 * @param allocateInfo
	 *            现金预约明细
	 * @param allocationCash
	 *            登记用预约信息
	 * @param model
	 *            页面Session信息
	 * @return 现金预约信息列表画面
	 */
	private String insert(AllAllocateInfo allocateInfo, AllAllocateInfo allocationCash, Model model) {
		// 初始化处理结果
		String message = AllocationConstant.SUCCESS;
		Locale locale = LocaleContextHolder.getLocale();
		// 如果是库房端，验证配款数量。如果不是整数得场合，提示信息
		if (!Pattern.compile("[0-9]*").matcher(Long.toString(allocateInfo.getAllAllocateItem().getMoneyNumber()))
				.matches() || allocateInfo.getAllAllocateItem().getMoneyNumber() == 0L) {
			// [添加失败]：数量不正确！(数量应为正整数)
			message = msg.getMessage("message.E2001", null, locale);
			addMessage(model, message);
			return message;

		}

		// 计算物品价值
		message = this.computeAmount(allocateInfo);
		if (!AllocationConstant.SUCCESS.equals(message)) {
			addMessage(model, message);
			model.addAttribute("allAllocateInfo", allocateInfo);
			return message;
		}

		BigDecimal maxMoney = new BigDecimal(0);
		String strMaxMoneyConfig = Global.getConfig("allocation.max.money");
		strMaxMoneyConfig = StringUtils.isBlank(strMaxMoneyConfig) ? "9999999999999.99" : strMaxMoneyConfig;
		BigDecimal maxMoneyConfig = new BigDecimal(Double.parseDouble(strMaxMoneyConfig));

		String strMapKey = allocationService.getAllAllocateItemMapKey(allocateInfo.getAllAllocateItem());
		BigDecimal registerAmount = new BigDecimal(0);
		if (allocationCash.getAllAllocateItemMap() != null
				&& allocationCash.getAllAllocateItemMap().containsKey(strMapKey)) {
			registerAmount = allocationCash.getAllAllocateItemMap().get(strMapKey).getMoneyAmount();
		}

		maxMoney = maxMoney.add(registerAmount == null ? new BigDecimal(0) : registerAmount)
				.add(allocateInfo.getAllAllocateItem().getMoneyAmount());
		allocateInfo.setRegisterAmount(maxMoney);
		if (maxMoney.compareTo(maxMoneyConfig) == 1) {
			// [添加失败]：添加金额超出上限[{0}]！
			message = msg.getMessage("message.E2009", new String[] { strMaxMoneyConfig }, locale);
			model.addAttribute("allAllocateInfo", allocateInfo);
			addMessage(model, message);
			return message;
		}

		// 插入登记信息
		String Flag = allocationService.setOrderCash(allocateInfo, allocationCash);
		// 已有该物品登记信息
		if (AllocationConstant.FAILED.equals(Flag)) {
			message = msg.getMessage("message.I2014", new String[] { StoreCommonUtils.getGoodsName(strMapKey) },
					locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId", strMapKey);
		}
		return message;
	}

	/**
	 * @author qipeihong
	 * @date 2017年7月31日
	 * 
	 *       删除现金调拨信息
	 * @param allocateInfo
	 *            调拨信息
	 * @param searchCondition
	 *            检索时保存的查询条件
	 * @param status
	 *            页面Session信息,
	 * @param redirectAttributes
	 *            对象重定向传参
	 * @return 调拨信息列表页面
	 */
	@RequestMapping(value = "back")
	public String back(AllAllocateInfo allocateInfo, @ModelAttribute("searchCondition") AllAllocateInfo searchCondition,
			SessionStatus status, RedirectAttributes redirectAttributes,
			@RequestParam(required = false) String pagetype) {
		if (AllocationConstant.PageType.PointAdd.equals(pagetype)
				|| AllocationConstant.PageType.PointEdit.equals(pagetype)
				|| AllocationConstant.PageType.PointTempAdd.equals(pagetype)
				|| AllocationConstant.PageType.PointTempEdit.equals(pagetype)
				|| AllocationConstant.PageType.PointView.equals(pagetype)) {
			// 清空Session
			status.setComplete();
			return "redirect:" + adminPath + "/allocation/v01/cashOrder/list?searchFlag=init";
		}
		return "redirect:" + adminPath + "/allocation/v01/boxHandover/handout";
	}

	/**
	 * 
	 * @author suiwei
	 * @version 2015年9月9日
	 * 
	 *          根据现金预约ID删除预约详细信息
	 * @param allocationCash
	 *            登记用现金预约信息
	 * @param detailId
	 *            现金预约详细ID
	 * @return 处理结果
	 */
	private String deleteDetail(AllAllocateInfo allocationCash, String detailId) {

		// 寻找待删除明细行所在Map中的Key值，以及待删除明细行金额
		Iterator<String> keyIterator = allocationCash.getAllAllocateItemMap().keySet().iterator();
		String strKey = "";

		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(strKey);

			if (StringUtils.isNotEmpty(detailId) && detailId.equals(tempItem.getAllItemsId())) {
				break;
			}
		}
		// 删除明细行
		allocationCash.getAllAllocateItemMap().remove(strKey);

		// 重新预约计算金额
		Map<String, AllAllocateItem> countMap = allocationService
				.countCashByCurrencyFromMap(allocationCash.getAllAllocateItemMap());
		allocationCash.setCountItemMap(countMap);

		return AllocationConstant.SUCCESS;
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月6日
	 * 
	 *          提交现金预约信息
	 * @param allocateInfo
	 *            现金预约明细
	 * @param allocationCash
	 *            登记用现金预约信息
	 * @param model
	 *            页面Session信息
	 * @param status
	 *            页面Session状态
	 * @param redirectAttributes
	 *            页面跳转信息
	 * @return 现金预约信息列表页面
	 */
	private String saveAllcation(AllAllocateInfo allocateInfo, AllAllocateInfo allocationCash, Model model,
			SessionStatus status, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response) {
		Locale locale = LocaleContextHolder.getLocale();
		// 登陆用户
		User userInfo = UserUtils.getUser();
		allocationCash.setLoginUser(userInfo);

		// 如果提交的预约明細不存在的場合
		if (allocationCash.getAllAllocateItemMap().size() == 0) {

			// [保存失败]：请添加明细数据！
			String message = msg.getMessage("message.E2003", null, locale);
			addMessage(model, message);
			return form(allocateInfo, model, true, status, request, response);
		}

		// 网点添加、修改预约明细
		if (AllocationConstant.PageType.PointAdd.equals(allocationCash.getPageType())
				|| AllocationConstant.PageType.PointEdit.equals(allocationCash.getPageType())
				|| AllocationConstant.PageType.PointTempAdd.equals(allocationCash.getPageType())
				|| AllocationConstant.PageType.PointTempEdit.equals(allocationCash.getPageType())) {
			try {
				// 一致性检验
				int result = allocationService.saveOrderAllocation(allocationCash);
				if (result == 0) {
					// [保存失败]：当前机构未设定线路，请联系管理员！
					String message = msg.getMessage("message.E2066", null, locale);
					addMessage(model, message);
					return form(allocateInfo, model, true, status, request, response);
				}

			} catch (BusinessException be) {
				String message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
				addMessage(model, message);
				if (AllocationConstant.PageType.PointTempAdd.equals(allocationCash.getPageType())
						|| AllocationConstant.PageType.PointTempEdit.equals(allocationCash.getPageType())) {
					return "modules/allocation/v01/out/order/cashorder/cashOrderTempDetail";
				}
				return "modules/allocation/v01/out/order/cashorder/cashOrderDetail";
			}
			// 执行保存

		}

		// 清空Session
		status.setComplete();

		// 预约登记保存成功，返回列表页面
		// 流水单号：{0}保存成功！
		String message = msg.getMessage("message.I2001", new String[] { allocationCash.getAllId() }, locale);
		addMessage(redirectAttributes, message);
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allocationCash.getrOffice().getName());
		paramsList.add(allocationCash.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allocationCash.getBusinessType(), allocationCash.getStatus(), paramsList,
				allocationCash.getaOffice().getId(), UserUtils.getUser());
		return "redirect:" + adminPath + "/allocation/v01/cashOrder/?searchFlag=init";
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月14日
	 * 
	 *          将共通详细信息拷贝到调拨物品详细
	 * @param allocateInfo
	 *            页面详细信息
	 */
	private void itemCopy(AllAllocateInfo allocateInfo) {

		// 币种
		allocateInfo.getAllAllocateItem().setCurrency(allocateInfo.getStoGoodSelect().getCurrency());
		// 类别
		allocateInfo.getAllAllocateItem().setClassification(allocateInfo.getStoGoodSelect().getClassification());
		// 软/硬币
		allocateInfo.getAllAllocateItem().setCash(allocateInfo.getStoGoodSelect().getCash());
		// 面值
		allocateInfo.getAllAllocateItem().setDenomination(allocateInfo.getStoGoodSelect().getDenomination());
		// 单位
		allocateInfo.getAllAllocateItem().setUnit(allocateInfo.getStoGoodSelect().getUnit());
		// 套别
		allocateInfo.getAllAllocateItem().setSets(allocateInfo.getStoGoodSelect().getEdition());
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年9月17日
	 * 
	 *          根据物品编号对应的物品价值，计算金额
	 * @param allAllocateInfo
	 *            物品编号信息
	 * @return 物品金额
	 */
	private String computeAmount(AllAllocateInfo allAllocateInfo) {
		String message = AllocationConstant.SUCCESS;
		AllAllocateItem tempItem = allAllocateInfo.getAllAllocateItem();
		// 金额换算
		String strGoodsKey = AllocationCommonUtils.getGoodsKey(tempItem);
		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsKey);
		if (goodsValue != null) {
			// 配款时设定配款金额
			tempItem.setMoneyAmount(goodsValue.multiply(new BigDecimal(tempItem.getMoneyNumber())));
		} else {
			Locale locale = LocaleContextHolder.getLocale();
			String strGoodsName = StoreCommonUtils.getGoodsNameById(strGoodsKey);
			strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
			// [添加失败]：物品[{0}]对应物品信息不存在，请稍后再试或与系统管理员联系！
			message = msg.getMessage("message.E2023", new String[] { strGoodsKey }, locale);
			return message;
		}
		return message;
	}

	/**
	 * @author wangbaozhong
	 * @date 2015年11月04日
	 * 
	 *       删除上缴信息
	 * @param allocateInfo
	 *            调拨信息
	 * @param searchCondition
	 *            检索时保存的查询条件
	 * @param redirectAttributes
	 *            对象重定向传参
	 * @return 上缴信息列表页面
	 */
	@RequestMapping(value = "delete")
	public String delete(AllAllocateInfo allocateInfo,
			@ModelAttribute("searchCondition") AllAllocateInfo searchCondition, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response, Model model, SessionStatus status) {
		// 判断当前流水状态
		Locale locale = LocaleContextHolder.getLocale();
		if (allocationService.findAllocation(allocateInfo) == null) {
			// [操作失败]：流水单号[{0}]对应调拨信息不存在！
			String message = msg.getMessage("message.E2029", new String[] { allocateInfo.getAllId() }, locale);
			addMessage(model, message);
			allocateInfo.setSearchFlag("init");
			return list(allocateInfo, request, response, model, status);
		}
		if (AllocationConstant.Status.CashOrderQuotaYes.equals(allocateInfo.getStatus())) {
			// [删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
			String message = msg.getMessage("message.E2020", new String[] { allocateInfo.getAllId(),
					DictUtils.getDictLabel(allocateInfo.getStatus(), "quota_status", "") }, locale);
			addMessage(model, message);
			allocateInfo.setSearchFlag("init");
			return list(allocateInfo, request, response, model, status);
		}
		// 执行删除处理
		allocationService.deleteCash(allocateInfo);

		// 流水单号：{0}删除成功！
		String message = msg.getMessage("message.I2002", new Object[] { allocateInfo.getAllId() }, locale);
		addMessage(redirectAttributes, message);

		return "redirect:" + adminPath + "/allocation/v01/cashOrder/?searchFlag=init";
	}

	/**
	 * @author qph
	 * @version 2017年7月6日
	 * 
	 *          根据查询条件，查询现金下拨详细信息
	 * @param allocateInfo
	 *            下拨信息
	 * @param model
	 *            页面Session信息
	 * @param sessionInitFlag
	 *            页面Session初始化标识
	 * @param status
	 *            Session状态
	 * @return 现金下拨详细画面
	 */
	@RequestMapping(value = "view")
	public String view(AllAllocateInfo allocateInfo, Model model) {
		String officeType = UserUtils.getUser().getOffice().getType();
		String escortName = "";
		String managerName = "";
		// ADD-START  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		// 交接人员id信息
		List<String> handoverIdList = Lists.newLinkedList();
		// ADD-END  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		if (!"".equals(allocateInfo.getStoreHandoverId())) {
			List<AllHandoverDetail> allHandoverDetailList = allocationService
					.findDetailByHandoverId(allocateInfo.getStoreHandoverId());
			for (AllHandoverDetail detail : allHandoverDetailList) {
				if (AllocationConstant.OperationType.ACCEPT.equals(detail.getOperationType())) {
					// ADD-START  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
					// 添加交接人员id
					handoverIdList.add(detail.getEscortId());
					// ADD-END  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
					if (escortName.isEmpty()) {
						escortName = detail.getEscortName();
					} else {
						escortName = escortName + "," + detail.getEscortName();
					}
				}
				if (AllocationConstant.OperationType.AUTHORIZATION.equals(detail.getOperationType())) {
					managerName = detail.getEscortName();
				}
			}
		}
		// ADD-START  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		model.addAttribute("handoverIdList",handoverIdList);
		// ADD-END  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		model.addAttribute("officeType", officeType);
		model.addAttribute("escortName", escortName);
		model.addAttribute("managerName", managerName);
		model.addAttribute("allAllocateInfo", allocateInfo);
		return "modules/allocation/v01/out/box/boxOutDetail";
	}

	/**
	 * @author qph
	 * @version 2017年7月6日
	 * 
	 *          根据查询条件，查询现金下拨详细信息
	 * @param allocateInfo
	 *            下拨信息
	 * @param model
	 *            页面Session信息
	 * @param sessionInitFlag
	 *            页面Session初始化标识
	 * @param status
	 *            Session状态
	 * @return 现金上缴详细画面
	 */
	@RequestMapping(value = "confirm")
	public String confirm(AllAllocateInfo allocateInfo, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		Locale locale = LocaleContextHolder.getLocale();
		
		// 设置更新人
		User userInfo = UserUtils.getUser();
		allocateInfo.setUpdateBy(userInfo);
		allocateInfo.setUpdateDate(new Date());
		
		if (AllocationConstant.Status.Finish.equals(allocateInfo.getStatus())
				&& allocateInfo.getAllDetailList().size() > 0) {
			// 判断接收方式（PDA接收或者页面接收）
			String outletsScanFlag = allocateInfo.getAllDetailList().get(0).getOutletsScanFlag();
			String message = "";
			// PDA接收
			if (AllocationConstant.ScanFlag.Scan.equals(outletsScanFlag)) {
				message = msg.getMessage("message.E1073", new String[] { allocateInfo.getAllId() }, locale);
			} else {
				// 页面接收
				message = msg.getMessage("message.E1082", new String[] { allocateInfo.getAllId() }, locale);
			}

			addMessage(model, message);
			return list(allocateInfo, request, response, model, null);
		}
		// 更新箱袋信息表
		for (AllAllocateDetail AllAllocateDetail : allocateInfo.getAllDetailList()) {
			StoBoxInfo sto = new StoBoxInfo();
			sto.setId(AllAllocateDetail.getBoxNo());
			// ADD BY WANGBAOZHONG  2017-09-15 START 
			sto.setRfid(AllAllocateDetail.getRfid());
			// ADD BY WANGBAOZHONG  2017-09-15 END
			sto.setUpdateBy(UserUtils.getUser());
			sto.setUpdateDate(new Date());
			// 箱袋状态更新为空箱
			if (AllAllocateDetail.getScanDate() != null
					|| AllocationConstant.TaskType.TEMPORARY_TASK.equals(allocateInfo.getTaskType())) {
				sto.setBoxStatus(Constant.BoxStatus.EMPTY);
				// ADD BY WANGBAOZHONG  2017-09-15 START 
				userInfo.setUpdateBy(userInfo);
				userInfo.setUpdateDate(new Date());
				// ADD BY WANGBAOZHONG  2017-09-15 END
				stoBoxInfoService.updateStatus(sto);
			}
			// 设置接收方式（页面点击接收）
			AllAllocateDetail.setOutletsScanFlag(AllocationConstant.ScanFlag.Additional);
			AllAllocateDetail.setOutletsPdaScanDate(new Date());
			allocationService.updateDetailByRfid(AllAllocateDetail);
		}
		
		// 更新调拨主表
		allocationService.pointconfirm(allocateInfo);
		String message = msg.getMessage("message.I2001", new String[] { allocateInfo.getAllId() }, locale);
		addMessage(model, message);
		return list(allocateInfo, request, response, model, null);

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年8月18日
	 * 
	 *          设置
	 * @param allocateInfo
	 *            页面详细信息
	 */
	private void setStrupdateDate(AllAllocateInfo allocateInfo, AllAllocateInfo allocateInfocash) {
		if (allocateInfo.getStrUpdateDate() != null && allocateInfocash.getStrUpdateDate() != null
				&& allocateInfo.getStrUpdateDate() != allocateInfocash.getStrUpdateDate()) {
			allocateInfo.setStrUpdateDate(allocateInfocash.getStrUpdateDate());
		}

	}

	/**
	 * 
	 * Title: toUpdateGoodsItem
	 * <p>
	 * Description: 修改物品数量页面
	 * </p>
	 * 
	 * @author: qph
	 * @param goodsId
	 *            物品信息
	 * @param allocatedOrderSession
	 *            Session保持物品信息
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "toUpdateGoodsItem")
	public String toUpdateGoodsItem(String goodsId,
			@ModelAttribute("allocationOrderCash") AllAllocateInfo allocationOrderCash, Model model) {

		AllAllocateItem updateGoodsItem = new AllAllocateItem();
		Iterator<String> keyIterator = allocationOrderCash.getAllAllocateItemMap().keySet().iterator();
		String strKey = "";
		// 循环物品列表
		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			if (strKey.equals(goodsId)) {
				AllAllocateItem item = allocationOrderCash.getAllAllocateItemMap().get(strKey);
				updateGoodsItem = item;
				updateGoodsItem.setGoodsName(StoreCommonUtils.getGoodsName(strKey));
				break;
			}

		}

		model.addAttribute("updateGoodsItem", updateGoodsItem);
		return "modules/allocation/v01/out/order/cashorder/UpdateGoodsCommonForm";
	}

	/**
	 * 
	 * Title: updateGoodsItem
	 * <p>
	 * Description: 修改物品数量
	 * </p>
	 * 
	 * @author: qph
	 * @param updateGoodsItem
	 *            被修改物品信息
	 * @param allocatedOrderSession
	 *            Session保持物品信息
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "updateGoodsItem")
	public String updateGoodsItem(AllAllocateItem updateGoodsItem,
			@ModelAttribute("allocationOrderCash") AllAllocateInfo allocationOrderCash, Model model) {

		Iterator<String> keyIterator = allocationOrderCash.getAllAllocateItemMap().keySet().iterator();
		String strKey = "";

		while (keyIterator.hasNext()) {
			strKey = keyIterator.next();
			AllAllocateItem item = allocationOrderCash.getAllAllocateItemMap().get(strKey);
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}
		// 重新计算物品总价值
		this.computeAmount(allocationOrderCash);
		Map<String, AllAllocateItem> countMap = allocationService
				.countCashByCurrencyFromMap(allocationOrderCash.getAllAllocateItemMap());
		allocationOrderCash.setCountItemMap(countMap);
		model.addAttribute("allAllocateInfo", allocationOrderCash);
		if (AllocationConstant.PageType.PointTempAdd.equals(allocationOrderCash.getPageType())
				|| AllocationConstant.PageType.PointTempEdit.equals(allocationOrderCash.getPageType())) {
			return "modules/allocation/v01/out/order/cashorder/cashOrderTempDetail";
		}
		return "modules/allocation/v01/out/order/cashorder/cashOrderDetail";
	}

	/**
	 * 跳转到撤销预约画面
	 * 
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "toCancel")
	public String toCancel(String allId, String taskType, Model model) {
		AllAllocateInfo allocate = allocationService.getAllocate(allId);
		AllAllocateInfo cancelParam = new AllAllocateInfo();
		cancelParam.setStrUpdateDate(allocate.getStrUpdateDate());
		cancelParam.setAllId(allId);
		cancelParam.setTaskType(taskType);
		model.addAttribute("cancelParam", cancelParam);
		return "modules/allocation/v01/out/order/cashhandin/cancelCommonForm";
	}

	/**
	 * 撤销预约
	 * 
	 * @param cancelParam
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "cancel", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String cancel(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 取得撤回原因
		String cancelReason = request.getParameter("cancelReason");
		allAllocateInfo.setCancelReason(cancelReason);
		try {
			// 一致性校验
			allocationService.checkVersion(allAllocateInfo);
			// 执行撤回操作
			allocationService.handOutCancel(allAllocateInfo);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			return message;
		}
		return "success";
	}

	/**
	 * 跳转到显示撤回原因画面
	 * 
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "displayCancelReason")
	public String displayCancelReason(AllAllocateInfo allAllocateInfo, Model model) {
		allAllocateInfo.setDisplayCancelReasonFlag(true);
		model.addAttribute("cancelParam", allAllocateInfo);
		return "modules/allocation/v01/out/order/cashhandin/cancelCommonForm";
	}

	/**
	 * 根据流水号查询历史记录判断图片显示属性
	 * 
	 * @author yanbingxu
	 * @version 2017-09-08
	 * @return
	 */
	@RequestMapping(value = "workflow")
	public String workflow(AllAllocateInfo allocateInfo, Model model, @RequestParam(required = false) String backFlag) {

		WorkFlowInfo workFlowInfo = handInWorkFlowService.findStatus(allocateInfo);
		allocateInfo.setStatus(AllocationConstant.Status.Finish);
		
		model.addAttribute("workFlowInfo", workFlowInfo);
		model.addAttribute("backFlag", backFlag);
		if (handInWorkFlowService.showOperateHistory(allocateInfo).size() != 0) {
			model.addAttribute("pointHandover", handInWorkFlowService.showOperateHistory(allocateInfo).get(0));
		}
		return "modules/allocation/v01/out/order/cashorder/cashOrderWorkFlow";
	}

	/**
	 * 查询操作历史
	 * 
	 * @author yanbingxu
	 * @version 2017-09-08
	 * @return
	 */
	@RequestMapping(value = "showOperateInfo")
	public String showOperateInfo(String status, String allId, Model model) {

		List<AllAllocateInfo> historyList = Lists.newArrayList();
		AllAllocateInfo operateInfo = new AllAllocateInfo();
		List<AllAllocateInfo> recordList = Lists.newArrayList();
		List<AllAllocateInfo> allHistoryList = Lists.newArrayList();

		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		allAllocateInfo.setStatus(status);
		allAllocateInfo.setAllId(allId);
		if(("pointHandover").equals(status)){
			allAllocateInfo.setStatus(AllocationConstant.Status.Finish);
		}

		// 查询单一状态调拨历史表记录
		historyList = handInWorkFlowService.showOperateHistory(allAllocateInfo);
		// 查询历史表所有记录
		allHistoryList = handInWorkFlowService.findAllAllocateHistory(allAllocateInfo);
		// 遍历计入操作历史记录
		if (historyList.size() > 0) {
			operateInfo = historyList.get(0);
			for (AllAllocateInfo history : allHistoryList) {
				AllAllocateInfo tempallocateInfo = new AllAllocateInfo();
				tempallocateInfo.setUpdateDate(history.getUpdateDate());
				tempallocateInfo.setStatus(history.getStatus());
				tempallocateInfo.setUpdateName(history.getUpdateName());
				recordList.add(tempallocateInfo);
			}
		}
		operateInfo.setStatus(status);

		// 扫描门授权信息
		List<AllHandoverDetail> doorManagerList = Lists.newArrayList();
		// 金库交接授权信息
		List<AllHandoverDetail> handoverManagerList = Lists.newArrayList();
		// 金库交接信息
		List<AllHandoverDetail> handoverList = Lists.newArrayList();
		// 网点交接授权信息
		List<AllHandoverDetail> pointHandoverManagerList = Lists.newArrayList();
		// 网点交接信息
		List<AllHandoverDetail> pointHandoverList = Lists.newArrayList();

		//金库交接处理
		for (AllHandoverDetail allHandoverDetail : operateInfo.getStoreHandover().getDetailList()) {
			if (AllocationConstant.OperationType.AUTHORIZATION.equals(allHandoverDetail.getOperationType())) {
				handoverManagerList.add(allHandoverDetail);
			}
			if (AllocationConstant.OperationType.SCANNING_DOOR_AUTHORIZATION
					.equals(allHandoverDetail.getOperationType())) {
				doorManagerList.add(allHandoverDetail);
			}
			if (AllocationConstant.OperationType.TURN_OVER.equals(allHandoverDetail.getOperationType())
					|| AllocationConstant.OperationType.ACCEPT.equals(allHandoverDetail.getOperationType())) {

				handoverList.add(allHandoverDetail);
			}
		}
		
		//网点交接处理
		for (AllHandoverDetail allHandoverDetail : operateInfo.getPointHandover().getDetailList()) {
			if (AllocationConstant.OperationType.AUTHORIZATION.equals(allHandoverDetail.getOperationType())) {
				pointHandoverManagerList.add(allHandoverDetail);
			}
			if (AllocationConstant.OperationType.TURN_OVER.equals(allHandoverDetail.getOperationType())
					|| AllocationConstant.OperationType.ACCEPT.equals(allHandoverDetail.getOperationType())) {

				pointHandoverList.add(allHandoverDetail);
			}
		}

		model.addAttribute("pointHandoverList", pointHandoverList);
		model.addAttribute("pointHandoverManagerList", pointHandoverManagerList);
		model.addAttribute("doorManagerList", doorManagerList);
		model.addAttribute("handoverList", handoverList);
		model.addAttribute("handoverManagerList", handoverManagerList);
		model.addAttribute("recordList", recordList);
		model.addAttribute("handOut", operateInfo);
		return "modules/allocation/v01/out/order/cashorder/orderWorkFlowDetail";
	}
	
	/**
	 * 返回上一级页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "backList")
	public String backList(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = false) String backFlag) {
		if (AllocationConstant.backType.CASH_HAND_OUT.equals(backFlag)) {
			return "redirect:" + adminPath + "/allocation/v01/cashOrder";
		} else {
			return "redirect:" + adminPath + "/allocation/v01/boxHandover/handout";
		}
	}

	/**
	 * 打印商行交款明细
	 * 
	 * @author qph
	 * @version 2018年08月07日
	 * 
	 * 
	 * @param inNo
	 *            业务流水
	 * @return 列表页面
	 */
	@RequestMapping(value = "/printDetail")
	public String printDetail(@RequestParam(value = "allID", required = true) String allID, Model model) {

		AllAllocateInfo allAllocateInfo = allocationService.getAllocate(allID);

		int handoutMan = 1;
		int receiveMan = 1;

		// 获取交接人员
		List<AllHandoverDetail> handoverList = allAllocateInfo.getStoreHandover().getDetailList();
		for (AllHandoverDetail allHandoverInfo : handoverList) {
			StoEscortInfo stoEscort = StoreCommonUtils.getEscortById(allHandoverInfo.getEscortId());
			if (stoEscort != null) {
				if (("90").equals(stoEscort.getEscortType())) {
					if (allAllocateInfo.getBusinessType().equals("30")) {
						model.addAttribute("handoutMan" + handoutMan, stoEscort.getEscortName());
						handoutMan++;
					} else {
						model.addAttribute("receiveMan" + receiveMan, stoEscort.getEscortName());
						receiveMan++;
					}
				} else {
					if (allAllocateInfo.getBusinessType().equals("30")) {
						model.addAttribute("receiveMan" + receiveMan, stoEscort.getEscortName());
						receiveMan++;
					} else {
						model.addAttribute("handoutMan" + handoutMan, stoEscort.getEscortName());
						handoutMan++;
					}
				}
			}

		}
		// 获取款箱状态
		if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfo.getBusinessType())) {
			model.addAttribute("boxType", "入库");
		} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(allAllocateInfo.getBusinessType())) {
			model.addAttribute("boxType", "出库");
		}
		int blankcount = 0;
		// 获取最大页数
		int size = 0;
		// 计算行数
		int count = allAllocateInfo.getAllDetailList().size();
		if (count % 10 == 0) {
			size = count / 10;
		} else {
			size = count / 10 + 1;
		}

		if (count < 10) {
			blankcount = count;
		} else {
			blankcount = count - 10;
		}

		// 获取当前登录人的信息
		User user = UserUtils.getUser();
		model.addAttribute("user", user.getOffice().getName());
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		model.addAttribute("allDetailList", allAllocateInfo.getAllDetailList());
		// 箱袋数量
		model.addAttribute("boxNum", allAllocateInfo.getAllDetailList().size());
		// 转成中文大写
		String strBigAmount = NumToRMB.changeToBig(count);
		String strm = strBigAmount.substring(0, strBigAmount.length() - 2);
		model.addAttribute("strBigAmount", strm);
		model.addAttribute("size", size);
		model.addAttribute("blankcount", blankcount);
		return "modules/allocation/v01/out/box/printBoxDetail";
	}

}
