package com.coffer.businesses.modules.allocation.v01.web;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBlankBillSelect;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 库外调拨：重空预约功能Controller
 * 
 * @author wangbaozhong
 * @version 2015年12月10日
 */
@Controller
@SessionAttributes({ "allocationImpBlankDoc" })
@RequestMapping(value = "${adminPath}/allocation/v01/impBlankDocOrder")
public class ImpBlankDocOrderController extends BaseController {

	@Autowired
	private AllocationService allocationService;

	/** 重空预约处理种别（添加） */
	private static final String SAVE_TYPE_INSERT = "insert";
	/** 重空预约处理种别（删除） */
	private static final String SAVE_TYPE_DELETE = "delete";
	/** 重空预约处理种别（修改） */
	private static final String SAVE_TYPE_EDIT = "edit";

	/**
	 * @author wangbaozhong
	 * @version 2015年12月10日
	 * 
	 *          根据流水号取得重空预约信息
	 * @param allId
	 *            流水号
	 * @return 重空预约信息
	 */
	@ModelAttribute
	public AllAllocateInfo get(@RequestParam(required = false) String allId) {

		AllAllocateInfo entity = null;
		if (StringUtils.isNoneBlank(allId)) {
			entity = allocationService.getAllocateBetween(allId);
		}
		if (entity == null) {
			entity = new AllAllocateInfo();
		}
		return entity;
	}

//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @version 2015年12月11日
//	 * 
//	 *          重空预约装配列表页面
//	 * @param allocateInfo
//	 *            查询条件
//	 * @param request
//	 *            页面请求信息
//	 * @param response
//	 *            页面应答信息
//	 * @param model
//	 *            页面Session信息
//	 * @param status
//	 *            Session状态
//	 * @return 重空预约装配列表页面
//	 */
//	@RequestMapping(value = { "list", "" })
//	public String list(AllAllocateInfo allocateInfo, HttpServletRequest request, HttpServletResponse response,
//			Model model, SessionStatus status) {
//
//		model.addAttribute("allocateInfo", allocateInfo);
//
//		// 设置业务种别(重空预约)
//		List<String> businessTypeList = Lists.newArrayList();
//		businessTypeList.add(AllocationConstant.BusinessType.Imp_Blank_Doc_Reservation);
//		allocateInfo.setBusinessTypes(businessTypeList);
//
//		// 设置重空状态
//		String strStatus = allocateInfo.getStatus();
//		if (StringUtils.isNotBlank(strStatus)) {
//			List<String> statusList = Lists.newArrayList();
//			statusList.add(allocateInfo.getStatus());
//			allocateInfo.setStatuses(statusList);
//		}
//
//		// 初始化开始时间和结束时间
//		if (allocateInfo.getCreateTimeStart() == null) {
//			allocateInfo.setCreateTimeStart(new Date());
//		}
//		if (allocateInfo.getCreateTimeEnd() == null) {
//			allocateInfo.setCreateTimeEnd(new Date());
//		}
//		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
//		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
//
//			// 如果是网点，设置用户的机构号，只能查询当前机构
//			if (AllocationConstant.SysUserType.BANK_OUTLETS_MANAGER.equals(UserUtils.getUser().getUserType())
//					|| AllocationConstant.SysUserType.BANK_OUTLETS_OPT.equals(UserUtils.getUser().getUserType())) {
//				allocateInfo.setrOffice(UserUtils.getUser().getOffice());
//			} else {
//				// 否则查询本金库
//				allocateInfo.setaOffice(UserUtils.getUser().getOffice());
//			}
//		}
//
//		// 设置当前日期
//		model.addAttribute("currentDate", new Date());
//
//		// 查询预约信息
//		Page<AllAllocateInfo> page = allocationService.findCash(new Page<AllAllocateInfo>(request, response),
//				allocateInfo);
//
//		model.addAttribute("page", page);
//
//		return "modules/allocation/v01/out/order/impBlankDocOrder/impBlankDocOrderList";
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @version 2015年12月11日
//	 * 
//	 * 
//	 *          根据查询条件，查询重空预约装配详细信息
//	 * @param allocateInfo
//	 *            预约信息
//	 * @param model
//	 *            页面Session信息
//	 * @param sessionInitFlag
//	 *            页面Session初始化标识
//	 * @param status
//	 *            Session状态
//	 * @param request
//	 *            页面请求信息
//	 * @param response
//	 *            页面应答信息
//	 * @return 重空预约装配详细画面
//	 */
//	@RequestMapping(value = "form")
//	public String form(AllAllocateInfo allocateInfo, Model model, boolean sessionInitFlag, SessionStatus status,
//			HttpServletRequest request, HttpServletResponse response) {
//		Office loginUserOffice = StoreCommonUtils.getOfficeById(UserUtils.getUser().getOffice().getId());
//		if (AllocationConstant.PageType.PointAdd.equals(allocateInfo.getPageType())
//				&& Constant.TradeFlag.Trade.equals(loginUserOffice.getTradeFlag())) {
//			Locale locale = LocaleContextHolder.getLocale();
//			// [提示]：同业网点目前不能进行调拨登记业务。
//			String message = msg.getMessage("message.I2008", null, locale);
//			addMessage(model, message);
//			allocateInfo.setSearchFlag("init");
//			return list(allocateInfo, request, response, model, status);
//		}
//		// 判断是否初始化
//		if (!sessionInitFlag) {
//
//			if (StringUtils.isNotBlank(allocateInfo.getAllId())) {
//				if ((AllocationConstant.PageType.PointEdit.equals(allocateInfo.getPageType()) 
//						|| AllocationConstant.PageType.StoreEdit.equals(allocateInfo.getPageType()))
//						&& allocationService.getAllocateBetween(allocateInfo.getAllId()) == null) {
//					Locale locale = LocaleContextHolder.getLocale();
//					// [操作失败]：流水单号[{0}]对应调拨信息不存在！
//					String message = msg.getMessage("message.E2029", new String[]{allocateInfo.getAllId()}, locale);
//					addMessage(model, message);
//					allocateInfo.setSearchFlag("init");
//					return list(allocateInfo, request, response, model, status);
//				}
//				if (AllocationConstant.Status.ImpBlankDocQuotaYes.equals(allocateInfo.getStatus())
//						&& AllocationConstant.PageType.PointEdit.equals(allocateInfo.getPageType())) {
//					Locale locale = LocaleContextHolder.getLocale();
//					// [修改失败]：流水单号[{0}]当前状态为[{1}]，不能修改！
//					String message = msg.getMessage("message.E2019", new String[] { allocateInfo.getAllId(),
//							DictUtils.getDictLabel(allocateInfo.getStatus(), "imp_blk_doc_status", "") }, locale);
//					addMessage(model, message);
//					return list(allocateInfo, request, response, model, status);
//				}
//				for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {
//					String strMapKey = allocationService.getAllAllocateItemMapKey(item);
//					if (AllocationConstant.PageType.StoreEdit.equals(allocateInfo.getPageType())) {
//						item.setAllItemsId(IdGen.uuid());
//					}
//					allocateInfo.getAllAllocateItemMap().put(strMapKey, item);
//				}
//
//				// 合并预约、装配信息
//				allocateInfo = allocationService.unionImpBlankDocOrderInfo(allocateInfo);
//				allocateInfo.getAllAllocateItem().setAllItemsId("");
//				model.addAttribute("allocationImpBlankDoc", allocateInfo);
//			} else { // 判断当日是否已经预约，如果已经预约
//				AllAllocateInfo tempAllAllocateInfo = new AllAllocateInfo();
//
//				// 设置业务种别(重空预约)
//				List<String> businessTypeList = Lists.newArrayList();
//				businessTypeList.add(AllocationConstant.BusinessType.Imp_Blank_Doc_Reservation);
//				tempAllAllocateInfo.setBusinessTypes(businessTypeList);
//
//				// 初始化开始时间和结束时间
//				tempAllAllocateInfo.setCreateTimeStart(new Date());
//				tempAllAllocateInfo.setCreateTimeEnd(new Date());
//
//				// 设置用户的机构号，只能查询当前机构
//				tempAllAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
//
//				tempAllAllocateInfo = allocationService.orderIsExit(tempAllAllocateInfo);
//				if (tempAllAllocateInfo != null) {
//					// 未装配
//					if (AllocationConstant.Status.ImpBlankDocQuotaNo.equals(tempAllAllocateInfo.getStatus())) {
//						tempAllAllocateInfo.setPageType(AllocationConstant.PageType.PointEdit);
//					}
//					// 已装配
//					else if (AllocationConstant.Status.ImpBlankDocQuotaYes.equals(tempAllAllocateInfo.getStatus())) {
//						tempAllAllocateInfo.setPageType(AllocationConstant.PageType.PointView);
//					}
//
//					for (AllAllocateItem item : tempAllAllocateInfo.getAllAllocateItemList()) {
//						String strMapKey = allocationService.getAllAllocateItemMapKey(item);
//						if (AllocationConstant.PageType.StoreEdit.equals(allocateInfo.getPageType())) {
//							item.setAllItemsId(IdGen.uuid());
//						}
//						tempAllAllocateInfo.getAllAllocateItemMap().put(strMapKey, item);
//					}
//
//					// 合并预约、装配信息
//					tempAllAllocateInfo = allocationService.unionImpBlankDocOrderInfo(tempAllAllocateInfo);
//
//					model.addAttribute("allocationImpBlankDoc", tempAllAllocateInfo);
//					model.addAttribute("allAllocateInfo", tempAllAllocateInfo);
//				} else {
//
//					model.addAttribute("allocationImpBlankDoc", allocateInfo);
//				}
//
//			}
//
//		}
//		return "modules/allocation/v01/out/order/impBlankDocOrder/impBlankDocOrderDetail";
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @version 2015年12月11日
//	 * 
//	 *  
//	 *          保存重空预约装配信息
//	 * @param allocateInfo
//	 *            重空预约装配主表信息
//	 * @param allocationImpBlankDoc
//	 *            登记用信息
//	 * @param detailId
//	 *            重空预约装配详细ID
//	 * @param saveType
//	 *            保存类型
//	 * @param model
//	 *            页面Session信息
//	 * @param status
//	 *            页面Session状态
//	 * @param redirectAttributes
//	 * @param request
//	 *            页面请求信息
//	 * @param response
//	 *            页面应答信息
//	 * @return 重空预约装配信息列表页面
//	 */
//	@RequestMapping(value = "save")
//	public String save(AllAllocateInfo allocateInfo,
//			@ModelAttribute("allocationImpBlankDoc") AllAllocateInfo allocationImpBlankDoc,
//			@RequestParam(required = false) String detailId, @RequestParam(required = false) String saveType,
//			Model model, SessionStatus status, RedirectAttributes redirectAttributes, HttpServletRequest request,
//			HttpServletResponse response) {
//
//		Locale locale = LocaleContextHolder.getLocale();
//		if ((AllocationConstant.PageType.PointEdit.equals(allocateInfo.getPageType()) 
//				|| (SAVE_TYPE_DELETE.equals(saveType) && AllocationConstant.PageType.PointEdit.equals(allocationImpBlankDoc.getPageType())))
//				&& allocationService.getAllocateBetween(allocateInfo.getAllId()) == null) {
//			// [操作失败]：流水单号[{0}]对应调拨信息不存在！
//			String message = msg.getMessage("message.E2029", new String[]{allocateInfo.getAllId()}, locale);
//			addMessage(model, message);
//			allocateInfo.setSearchFlag("init");
//			return list(allocateInfo, request, response, model, status);
//		}
//		if (StringUtils.isNotBlank(allocateInfo.getAllId())
//				&& AllocationConstant.Status.ImpBlankDocQuotaYes.equals(allocateInfo.getStatus())
//				&& AllocationConstant.PageType.PointEdit.equals(allocationImpBlankDoc.getPageType())) {
//			// [修改失败]：流水单号[{0}]当前状态为[{1}]，不能修改！
//			String message = msg.getMessage("message.E2019", new String[] { allocateInfo.getAllId(),
//					DictUtils.getDictLabel(allocateInfo.getStatus(), "imp_blk_doc_status", "") }, locale);
//			addMessage(model, message);
//			return list(allocateInfo, request, response, model, status);
//		}
//
//		// 初始化处理结果
//		String result = "";
//
//		// 添加预约明细
//		if (SAVE_TYPE_INSERT.equals(saveType)) {
//			// 将共通详细信息拷贝到调拨物品详细
//			itemCopy(allocateInfo);
//			result = insert(allocateInfo, allocationImpBlankDoc, model);
//			if (!AllocationConstant.SUCCESS.equals(result)) {
//				return form(allocateInfo, model, true, status, request, response);
//			}
//			if (AllocationConstant.PageType.StoreEdit.equals(allocateInfo.getPageType())) {
//				allocateInfo.setAllAllocateItem(new AllAllocateItem());
//				allocateInfo.setStoBlankBillSelect(new StoBlankBillSelect());
//			}
//			// 删除预约明细
//		} else if (SAVE_TYPE_DELETE.equals(saveType)) {
//			// 将共通详细信息拷贝到调拨物品详细
//			itemCopy(allocateInfo);
//			result = deleteDetail(allocationImpBlankDoc, detailId);
//			model.addAttribute("allAllocateInfo", allocationImpBlankDoc);
//			// 修改预约明细
//		} else if (SAVE_TYPE_EDIT.equals(saveType)) {
//			this.itemCopy(allocateInfo);
//			result = editDetail(allocationImpBlankDoc, detailId, model);
//			// 提交预约登记
//		} else {
//			if (AllocationConstant.PageType.PointAdd.equals(allocationImpBlankDoc.getPageType())) {
//				
//				// 同行预约重复登记检查
//				AllAllocateInfo tempAllAllocateInfo = new AllAllocateInfo();
//				
//				// 设置业务种别(现金预约)
//				List<String> businessTypeList = Lists.newArrayList();
//				businessTypeList.add(AllocationConstant.BusinessType.Imp_Blank_Doc_Reservation);
//				tempAllAllocateInfo.setBusinessTypes(businessTypeList);
//				
//				// 初始化开始时间和结束时间
//				tempAllAllocateInfo.setCreateTimeStart(new Date());
//				tempAllAllocateInfo.setCreateTimeEnd(new Date());
//				
//				// 设置用户的机构号，只能查询当前机构
//				tempAllAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
//				
//				tempAllAllocateInfo = allocationService.orderIsExit(tempAllAllocateInfo);
//				if (tempAllAllocateInfo != null) {
//					// [登记失败]：重复登记，请参看流水单号[{0}]登记信息！
//					String message = msg.getMessage("message.E2022", new String[]{tempAllAllocateInfo.getAllId()}, locale);
//					addMessage(model, message);
//					allocateInfo.setSearchFlag("init");
//					allocateInfo.setrOffice(tempAllAllocateInfo.getrOffice());
//					allocateInfo.setStatus(tempAllAllocateInfo.getStatus());
//					return list(allocateInfo, request, response, model, status);
//				}
//			}
//			result = saveAllcation(allocateInfo, allocationImpBlankDoc, model, status, redirectAttributes, request,
//					response);
//			return result;
//		}
//		return form(allocateInfo, model, true, status, request, response);
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @version 2015年12月11日
//	 * 
//	 *          预约、装配明细添加
//	 * @param allocateInfo
//	 *            明细信息
//	 * @param allocationCash
//	 *            明细信息列表
//	 * @param model
//	 * @return 目标页面
//	 */
//	private String insert(AllAllocateInfo allocateInfo, AllAllocateInfo allocationCash, Model model) {
//		// 初始化处理结果
//		String message = AllocationConstant.SUCCESS;
//		Locale locale = LocaleContextHolder.getLocale();
//
//		Long maxNumber = 0L;
//		String strMaxNumberConfig = Global.getConfig("allocation.max.imp.blank.doc.num");
//		strMaxNumberConfig = StringUtils.isBlank(strMaxNumberConfig) ? "999999999999999" : strMaxNumberConfig;
//		Long maxNumberConfig = Long.parseLong(strMaxNumberConfig);
//		
//		String strMapKey = allocationService.getAllAllocateItemMapKey(allocateInfo.getAllAllocateItem());
//		
//		// 如果是库房端，验证装配数量。如果不是整数得场合，提示信息
//		if (AllocationConstant.PageType.StoreEdit.equals(allocateInfo.getPageType())) {
//			if (StringUtils.isEmpty(allocateInfo.getAllAllocateItem().getCurrency())) {
//				// [装配失败]：重空类型不正确！(重空类型不能为空)
//				message = msg.getMessage("message.E2025", null, locale);
//				addMessage(model, message);
//				return message;
//			}
//			if (!Pattern.compile("[0-9]*").matcher(Long.toString(allocateInfo.getAllAllocateItem().getConfirmNumber()))
//					.matches()) {
//				// [装配失败]：装配数量不正确！(数量应为正整数)
//				message = msg.getMessage("message.E2024", null, locale);
//				addMessage(model, message);
//				return message;
//			}
//			Long confirmNumber = 0L;
//			if (allocationCash.getAllAllocateItemMap() != null 
//					&& allocationCash.getAllAllocateItemMap().containsKey(strMapKey)) {
//				confirmNumber = allocationCash.getAllAllocateItemMap().get(strMapKey).getConfirmNumber();
//			}
//			confirmNumber = confirmNumber == null ? 0L : confirmNumber;
//			maxNumber = maxNumber + confirmNumber + allocateInfo.getAllAllocateItem().getConfirmNumber();
//		} else { // 如果是网点端，验证预约数量。如果不是整数得场合，提示信息
//			if (!Pattern.compile("[0-9]*").matcher(Long.toString(allocateInfo.getAllAllocateItem().getMoneyNumber()))
//					.matches()
//					|| allocateInfo.getAllAllocateItem().getMoneyNumber() == 0L) {
//				// [添加失败]：数量不正确！(数量应为正整数)
//				message = msg.getMessage("message.E2001", null, locale);
//				addMessage(model, message);
//				return message;
//			}
//			Long number = 0L;
//			if (allocationCash.getAllAllocateItemMap() != null 
//					&& allocationCash.getAllAllocateItemMap().containsKey(strMapKey)) {
//				number = allocationCash.getAllAllocateItemMap().get(strMapKey).getMoneyNumber();
//			}
//			number = number == null ? 0L : number;
//			maxNumber = maxNumber + number + allocateInfo.getAllAllocateItem().getMoneyNumber();
//		}
//		
//		if (maxNumber > maxNumberConfig) {
//			//[添加失败]：添加重空数量超出上限[{0}]！
//			message = msg.getMessage("message.E2030", new String[] { strMaxNumberConfig }, locale);
//			model.addAttribute("allAllocateInfo", allocateInfo);
//			addMessage(model, message);
//			return message;
//		}
//		// 插入登记信息
//		allocationService.setImpBlankDoc(allocateInfo, allocationCash);
//		return message;
//	}
//
//	/**
//	 * @author wangbaozhong
//	 * @date 2015年12月10日
//	 * 
//	 *       详细页面返回按钮
//	 * @param allocateInfo
//	 *            调拨信息
//	 * @param searchCondition
//	 *            检索时保存的查询条件
//	 * @param status
//	 *            页面Session信息,
//	 * @param redirectAttributes
//	 *            对象重定向传参
//	 * @return 调拨信息列表页面
//	 */
//	@RequestMapping(value = "back")
//	public String back(AllAllocateInfo allocateInfo,
//			@ModelAttribute("searchCondition") AllAllocateInfo searchCondition, SessionStatus status,
//			RedirectAttributes redirectAttributes) {
//
//		// 清空Session
//		status.setComplete();
//		return "redirect:" + adminPath + "/allocation/v01/impBlankDocOrder/list";
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @date 2015年12月10日
//	 * 
//	 *       根据重空预约ID删除预约详细信息
//	 * @param allocationCash
//	 *            登记用重空预约信息
//	 * @param detailId
//	 *            重空预约详细ID
//	 * @return 处理结果
//	 */
//	private String deleteDetail(AllAllocateInfo allocationCash, String detailId) {
//
//		// 寻找待删除明细行所在Map中的Key值，以及待删除明细行金额
//		Iterator<String> keyIterator = allocationCash.getAllAllocateItemMap().keySet().iterator();
//		String strKey = "";
//
//		while (keyIterator.hasNext()) {
//			strKey = keyIterator.next();
//			AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(strKey);
//
//			if (StringUtils.isNotEmpty(detailId) && detailId.equals(tempItem.getAllItemsId())) {
//				break;
//			}
//		}
//		// 删除明细行
//		allocationCash.getAllAllocateItemMap().remove(strKey);
//
//		return AllocationConstant.SUCCESS;
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @date 2015年12月10日
//	 * 
//	 *       编辑重空预约登记信息
//	 * @param allocationCash
//	 *            登记用重空预约信息
//	 * @param detailId
//	 *            重空预约详细ID
//	 * @param model
//	 *            页面跳转信息
//	 * @return 处理结果
//	 */
//	private String editDetail(AllAllocateInfo allocationCash, String detailId, Model model) {
//		// 寻找待修改明细行所在Map中的Key值
//		Iterator<String> keyIterator = allocationCash.getAllAllocateItemMap().keySet().iterator();
//		String strKey = "";
//		// 将修改的信息放入输入项
//		while (keyIterator.hasNext()) {
//			strKey = keyIterator.next();
//			AllAllocateItem tempItem = allocationCash.getAllAllocateItemMap().get(strKey);
//
//			if (StringUtils.isNotEmpty(detailId) && detailId.equals(tempItem.getAllItemsId())) {
//				this.itemReverseCopy(allocationCash, tempItem);
//				model.addAttribute("allAllocateInfo", allocationCash);
//				break;
//			}
//		}
//		return AllocationConstant.SUCCESS;
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @version 2015年12月10日
//	 * 
//	 *          将调拨物品详细拷贝到共通详细信息
//	 * @param allocateInfo
//	 *            页面详细信息
//	 */
//
//	private void itemReverseCopy(AllAllocateInfo allocateInfo, AllAllocateItem item) {
//
//		// 重空分类
//		allocateInfo.getStoBlankBillSelect().setBlankBillKind(item.getCurrency());
//		// 重空类型
//		allocateInfo.getStoBlankBillSelect().setBlankBillType(item.getClassification());
//		
//		AllAllocateItem tempItem = new AllAllocateItem();
//		//重空分类
//		tempItem.setCurrency(item.getCurrency());
//		// 重空类型
//		tempItem.setClassification(item.getClassification());
//		tempItem.setMoneyNumber(item.getMoneyNumber());
//		tempItem.setConfirmNumber(item.getConfirmNumber());
//		tempItem.setAllItemsId(item.getAllItemsId());
//		allocateInfo.setAllAllocateItem(tempItem);
//
//	}
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @version 2015年9月14日
//	 * 
//	 *          将共通详细信息拷贝到调拨物品详细
//	 * @param allocateInfo
//	 *            页面详细信息
//	 */
//	private void itemCopy(AllAllocateInfo allocateInfo) {
//
//		// 重空分类
//		allocateInfo.getAllAllocateItem().setCurrency(allocateInfo.getStoBlankBillSelect().getBlankBillKind());
//		// 重空类型
//		allocateInfo.getAllAllocateItem().setClassification(allocateInfo.getStoBlankBillSelect().getBlankBillType());
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @date 2015年12月10日
//	 * 
//	 *       提交重空预约信息
//	 * @param allocateInfo
//	 *            现金重空明细
//	 * @param allocationImpBlankDoc
//	 *            登记用重空预约信息
//	 * @param model
//	 *            页面Session信息
//	 * @param status
//	 *            页面Session状态
//	 * @param redirectAttributes
//	 *            页面跳转信息
//	 * @return 现金重空信息列表页面
//	 */
//	private String saveAllcation(AllAllocateInfo allocateInfo, AllAllocateInfo allocationImpBlankDoc, Model model,
//			SessionStatus status, RedirectAttributes redirectAttributes, HttpServletRequest request,
//			HttpServletResponse response) {
//		Locale locale = LocaleContextHolder.getLocale();
//		// 登陆用户
//		User userInfo = UserUtils.getUser();
//		allocationImpBlankDoc.setLoginUser(userInfo);
//
//		// 如果提交的预约明细不存在的場合
//		if (allocationImpBlankDoc.getAllAllocateItemMap().size() == 0) {
//
//			// [保存失败]：请添加明细数据！
//			String message = msg.getMessage("message.E2003", null, locale);
//			addMessage(model, message);
//			return form(allocateInfo, model, true, status, request, response);
//		}
//
//		// 网点添加、修改预约明细
//		if (AllocationConstant.PageType.PointAdd.equals(allocationImpBlankDoc.getPageType())
//				|| AllocationConstant.PageType.PointEdit.equals(allocationImpBlankDoc.getPageType())) {
//			// 执行保存
//			allocationService.saveImpBlankDocOrderAllocation(allocationImpBlankDoc);
//		} else if (AllocationConstant.PageType.StoreEdit.equals(allocationImpBlankDoc.getPageType())) {
//			try {
//				Map<String, AllAllocateItem> orderedItemMap = Maps.newHashMap();
//				// 装配修改时，查找已装配项
//				if (AllocationConstant.Status.ImpBlankDocQuotaYes.equals(allocationImpBlankDoc.getStatus())) {
//					orderedItemMap = allocationService.getOrderedItem(allocationImpBlankDoc);
//				}
//				long lOrderedItemNum = 0l;
//				long lStoNum = 0l;
//				Iterator<String> keyIterator = allocationImpBlankDoc.getAllAllocateItemMap().keySet().iterator();
//				String strMapKey = "";
//				while (keyIterator.hasNext()) {
//					strMapKey = keyIterator.next();
//					AllAllocateItem tempItem = allocationImpBlankDoc.getAllAllocateItemMap().get(strMapKey);
//
//					if (tempItem.getConfirmNumber() == 0L) {
//						continue;
//					}
//
//					// 物品库存量判断
//					String strGoodId = AllocationCommonUtils.changeItemToImpBlankDocId(tempItem);
//					StoStoresInfo storeInfo = StoreCommonUtils.getStoStoresInfoByGoodsId(strGoodId, UserUtils.getUser()
//							.getOffice().getId());
//
//					if (storeInfo == null) {
//						String strGoodsName = StoreCommonUtils.getGoodsNameById(strGoodId);
//						strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
//						// [装配失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
//						String message = msg.getMessage("message.E2027", new String[] { strGoodId,
//								strGoodsName }, locale);
//						addMessage(model, message);
//						model.addAttribute("allAllocateInfo", allocationImpBlankDoc);
//						return form(allocateInfo, model, true, status, request, response);
//					}
//
//					if (storeInfo.getStoNum() == null) {
//						// [装配失败]：物品[{0}]库存不足！
//						String message = msg.getMessage("message.E2026",
//								new String[] { storeInfo.getGoodsName() }, locale);
//						model.addAttribute("allAllocateInfo", allocationImpBlankDoc);
//						addMessage(model, message);
//						return form(allocateInfo, model, true, status, request, response);
//					}
//					lStoNum = storeInfo.getStoNum(); // 取得库存
//					if (AllocationConstant.Status.ImpBlankDocQuotaYes.equals(allocationImpBlankDoc.getStatus())
//							&& orderedItemMap.containsKey(strMapKey)) {
//						lOrderedItemNum = orderedItemMap.get(strMapKey).getMoneyNumber(); // 已装配数量
//						lStoNum = lOrderedItemNum + lStoNum; // 临时还原库存数量
//					}
//					if (lStoNum == 0L || tempItem.getConfirmNumber() > lStoNum) {
//						// [装配失败]：物品[{0}]库存不足！
//						String message = msg.getMessage("message.E2026",
//								new String[] { storeInfo.getGoodsName() }, locale);
//						model.addAttribute("allAllocateInfo", allocationImpBlankDoc);
//						addMessage(model, message);
//						return form(allocateInfo, model, true, status, request, response);
//					}
//				}
//
//				// 装配修改时，回滚修改前的库存
//				if (AllocationConstant.Status.ImpBlankDocQuotaYes.equals(allocationImpBlankDoc.getStatus())) {
//					allocationService.impBlankDocStoreRollback(allocationImpBlankDoc);
//				}
//
//				// 库房添加、编辑装配明细
//				allocationService.saveImpBlankDocQuotaAllocation(allocationImpBlankDoc);
//				// 跳转至查看页面
//				AllAllocateInfo viewAllocateInfo = allocationService.getAllocateBetween(allocationImpBlankDoc
//						.getAllId());
//				model.addAttribute("allAllocateInfo", viewAllocateInfo);
//				viewAllocateInfo.setPageType(AllocationConstant.PageType.StoreView);
//				// 清空Session
//				status.setComplete();
//
//				// 流水单号：{0}保存成功！
//				String message = msg.getMessage("message.I2001",
//						new String[] { allocationImpBlankDoc.getAllId() }, locale);
//				addMessage(model, message);
//				return form(viewAllocateInfo, model, false, status, request, response);
//			} catch (BusinessException be) {
//				String message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
//				addMessage(model, message);
//				model.addAttribute("allAllocateInfo", allocationImpBlankDoc);
//				model.addAttribute("allocationImpBlankDoc", allocationImpBlankDoc);
//				return form(allocateInfo, model, true, status, request, response);
//			}
//		}
//		// 清空Session
//		status.setComplete();
//
//		// 预约登记保存成功，返回列表页面
//		// 流水单号：{0}保存成功！
//		String message = msg.getMessage("message.I2001", new String[] { allocationImpBlankDoc.getAllId() },
//				locale);
//		addMessage(redirectAttributes, message);
//		return "redirect:" + adminPath + "/allocation/v01/impBlankDocOrder/list";
//	}
//
//	/**
//	 * 
//	 * @author wangbaozhong
//	 * @version 2015年12月11日 装配结果打印页面
//	 * 
//	 * @param allocateInfo
//	 *            打印条件
//	 * @param model
//	 * @return 打印页面
//	 */
//	@RequestMapping(value = "printOrder")
//	public String printOrder(AllAllocateInfo allocateInfo, Model model) {
//		// 预约物品
//		Map<String, AllAllocateItem> orderMap = Maps.newTreeMap();
//		// 装配物品
//		Map<String, AllAllocateItem> quotaMap = Maps.newTreeMap();
//		AllAllocateItem tempItem = null;
//		for (AllAllocateItem item : allocateInfo.getAllAllocateItemList()) {
//			String strMapKey = allocationService.getAllAllocateItemMapKey(item);
//			if (AllocationConstant.PageType.StoreEdit.equals(allocateInfo.getPageType())) {
//				item.setAllItemsId(IdGen.uuid());
//			}
//			tempItem = new AllAllocateItem();
//			tempItem.setCurrency(item.getCurrency());
//			if (AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
//				tempItem.setMoneyNumber(item.getMoneyNumber());
//				orderMap.put(strMapKey, tempItem);
//			} else {
//				tempItem.setConfirmNumber(item.getMoneyNumber());
//				quotaMap.put(strMapKey, tempItem);
//			}
//			allocateInfo.getAllAllocateItemMap().put(strMapKey, item);
//		}
//
//		// 合并预约、装配信息
//		allocateInfo = allocationService.unionImpBlankDocOrderInfo(allocateInfo);
//
//		Office rOffice = StoreCommonUtils.getOfficeById(allocateInfo.getrOffice().getId());
//		allocateInfo.setrOffice(rOffice);
//		model.addAttribute("allocationOrder", allocateInfo);
//		return "modules/allocation/v01/out/order/impBlankDocOrder/printImpBlankDocOrderDetail";
//	}
//	
//	/**
//	 * @author wangbaozhong
//	 * @date 2015年12月22日
//	 * 
//	 *       删除预约信息
//	 * @param allocateInfo
//	 *            调拨信息
//	 * @param searchCondition
//	 *            检索时保存的查询条件
//	 * @param redirectAttributes
//	 *            对象重定向传参
//	 * @return 重空预约信息列表页面
//	 */
//	@RequestMapping(value = "delete")
//	public String delete(AllAllocateInfo allocateInfo,
//			@ModelAttribute("searchCondition") AllAllocateInfo searchCondition, RedirectAttributes redirectAttributes,
//			HttpServletRequest request, HttpServletResponse response,
//			Model model, SessionStatus status) {
//		// 判断当前流水状态
//		Locale locale = LocaleContextHolder.getLocale();
//		if (allocationService.getAllocateBetween(allocateInfo.getAllId()) == null) {
//			// [操作失败]：流水单号[{0}]对应调拨信息不存在！
//			String message = msg.getMessage("message.E2029", new String[]{allocateInfo.getAllId()}, locale);
//			addMessage(model, message);
//			allocateInfo.setSearchFlag("init");
//			return list(allocateInfo, request, response, model, status);
//		}
//		if (AllocationConstant.Status.ImpBlankDocQuotaYes.equals(allocateInfo.getStatus())) {
//			//[删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
//			String message = msg.getMessage("message.E2020", new String[]{allocateInfo.getAllId(), 
//					DictUtils.getDictLabel(allocateInfo.getStatus(), "imp_blk_doc_status", "")}, locale);
//			addMessage(model, message);
//			return list(allocateInfo, request, response, model, status);
//		}
//		// 执行删除处理
//		allocationService.deleteCash(allocateInfo);
//
//		// 流水单号：{0}删除成功！
//		String message = msg.getMessage("message.I2002", new Object[] { allocateInfo.getAllId() }, locale);
//		addMessage(redirectAttributes, message);
//		
//		return "redirect:" + adminPath + "/allocation/v01/impBlankDocOrder/list";
//	}
}
