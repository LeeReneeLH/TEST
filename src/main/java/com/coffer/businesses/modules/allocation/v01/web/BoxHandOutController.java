package com.coffer.businesses.modules.allocation.v01.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 库外调拨：箱袋下拨调拨功能Controller
 * 
 * @author Qipeihong
 * @version 2017-07-30
 */
@Controller
@SessionAttributes({ "allocatedOrderSession" })
@RequestMapping(value = "${adminPath}/allocation/v01/boxHandover")
public class BoxHandOutController extends BaseController {

	/** 调缴用Service */
	@Autowired
	private AllocationService allocationService;

	/**
	 * @author Qipeihong
	 * @version 2017-08-01
	 *
	 *          根据流水号，取得调缴信息
	 * @param id
	 *            流水号
	 * @return 调缴信息
	 */
	@ModelAttribute
	public AllAllocateInfo get(@RequestParam(required = false) String allId) {
		AllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)) {
			entity = allocationService.getAllocate(allId);
		}
		if (entity == null) {
			entity = new AllAllocateInfo();
		}
		return entity;
	}

	/**
	 * @author Qipeihong
	 * @version 2017-08-01
	 *
	 *          箱袋下拨信息查询
	 * @param allocateInfo
	 *            调拨信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @return 调拨信息列表
	 */
	@RequiresPermissions("allocation:handout:view")
	@RequestMapping(value = "handout")
	public String listHandout(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 设置业务种别(库外箱袋调拨)
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
			if (AllocationConstant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
				allAllocateInfo.setaOffice(UserUtils.getUser().getOffice());
			}
		}
		String officeType=UserUtils.getUser().getOffice().getType();
		model.addAttribute("officeType", officeType);
		// 执行查询处理
		listOut(allAllocateInfo, request, response, model);
		return "modules/allocation/v01/out/box/handoutList";
	}

	/**
	 * @author Qipeihong
	 * @version 2017-08-01
	 *
	 *          箱袋上缴信息查询
	 * @param allocateInfo
	 *            调拨信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @return 调拨信息列表
	 */
	@RequestMapping(value = "handin")
	public String listHandin(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		// 设置业务种别(现金上缴)
		List<String> businessTypeList = Lists.newArrayList();
		businessTypeList.add(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		allAllocateInfo.setBusinessTypes(businessTypeList);
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		// 设置交接状态
		String sStatus = allAllocateInfo.getStatus();
		if (StringUtils.isNotBlank(sStatus)) {
			List<String> statusList = Lists.newArrayList();
			statusList.add(allAllocateInfo.getStatus());
			allAllocateInfo.setStatuses(statusList);
		}
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
			// 金库的场合
			if (AllocationConstant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
				allAllocateInfo.setaOffice(UserUtils.getUser().getOffice());
			}
		}
		String officeType=UserUtils.getUser().getOffice().getType();
		model.addAttribute("officeType", officeType);
		// 执行查询处理
		listIn(allAllocateInfo, request, response, model);
		return "modules/allocation/v01/out/box/handinList";
	}

	/**
	 * @author Qipeihong
	 * @version 2017-08-01
	 *
	 *          箱袋调拨详细
	 * @param allocateInfo
	 *            调拨信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @return 箱袋详细信息
	 */
	@RequiresPermissions("allocation:handout:view")
	@RequestMapping(value = "detail")
	public String detail(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		Map<String, Map<String, StringBuilder>> boxMap = Maps.newHashMap();
		for (AllAllocateDetail boxDetail : allAllocateInfo.getAllDetailList()) {
			setDetailBoxList(boxMap, boxDetail);
		}

		// 设置箱袋详细信息
		List<AllAllocateDetail> detailList = Lists.newArrayList();
		for (Entry<String, Map<String, StringBuilder>> boxEntry : boxMap.entrySet()) {
			for (Entry<String, StringBuilder> placeEntry : boxEntry.getValue().entrySet()) {
				AllAllocateDetail detail = new AllAllocateDetail();
				detail.setBoxType(placeEntry.getKey());

				String boxNo = CommonUtils.toString(placeEntry.getValue());
				String[] boxNoArray = boxNo.split(AllocationConstant.Punctuation.COMMA);
				if (boxNoArray.length > 4) {
					StringBuilder strBud = new StringBuilder();
					int iIndex = 1;
					for (String tempNo : boxNoArray) {
						strBud.append(tempNo);
						if (iIndex != boxNoArray.length) {
							strBud.append(AllocationConstant.Punctuation.COMMA);
						}
						if (iIndex % 4 == 0) {
							strBud.append("<BR />");
						}
						iIndex++;
					}
					detail.setBoxNo(strBud.toString());
				} else {
					detail.setBoxNo(boxNo);
				}
				detail.setPlace(boxEntry.getKey());
				detailList.add(detail);
			}
		}

		// 设置箱子信息
		allAllocateInfo.setAllDetailList(detailList);
		model.addAttribute("allocationInfo", allAllocateInfo);
		return "modules/allocation/v01/out/box/boxInDetail";
	}

	/**
	 * @author Qipeihong
	 * @version 2017-08-01
	 *
	 *          以箱袋状态为单位设置箱袋调拨详细
	 * @param boxMap
	 *            保存箱袋详细信息
	 * @param boxDetail
	 *            箱袋详细信息
	 * @param place
	 *            箱袋状态
	 */
	private void setDetailBoxList(Map<String, Map<String, StringBuilder>> boxMap, AllAllocateDetail boxDetail) {

		String place = boxDetail.getPlace();

		// 初始化
		if (null == boxMap.get(place)) {
			Map<String, StringBuilder> newMap = Maps.newHashMap();
			boxMap.put(place, newMap);
		}

		Map<String, StringBuilder> placeMap = boxMap.get(place);
		// 箱子种别已经存在的场合
		if (placeMap.containsKey(boxDetail.getBoxType())) {
			placeMap.get(boxDetail.getBoxType()).append(AllocationConstant.Punctuation.COMMA)
					.append(boxDetail.getBoxNo());

			// 箱子种别不存在的场合
		} else {
			StringBuilder sb = new StringBuilder();
			placeMap.put(boxDetail.getBoxType(), sb.append(boxDetail.getBoxNo()));
		}
	}

	/**
	 * @author Qipeihong
	 * @version 2017-08-01
	 *
	 *          箱袋上缴信息查询
	 * @param allocateInfo
	 *            调拨信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @return 调拨信息列表
	 */
	private void listIn(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<AllAllocateInfo> page = allocationService.findAllocationPage(new Page<AllAllocateInfo>(request, response),
				allAllocateInfo, true);
		model.addAttribute("page", page);
	}

	/**
	 * @author Qipeihong
	 * @version 2017-08-01
	 *
	 *          箱袋下拨信息查询
	 * @param allocateInfo
	 *            调拨信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @return 调拨信息列表
	 */
	private void listOut(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<AllAllocateInfo> page = allocationService
				.findAllocationAndTemp(new Page<AllAllocateInfo>(request, response), allAllocateInfo);
		model.addAttribute("page", page);
	}

	/**
	 * 
	 * @author Qipeihong
	 * @version 2017-08-01
	 * 
	 *          删除箱袋信息
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("allocation:handout:view")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam String id, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		// 语言环境
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";

		// 流水号
		String allId = id;
		AllAllocateInfo allAllocateInfo = allocationService.getAllocate(allId);
		if (null == allAllocateInfo || StringUtils.isBlank(allAllocateInfo.getAllId())) {
			message = msg.getMessage("message.I2010", null, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/allocation/v01/boxHandover/handout";
		}

		if (!AllocationConstant.Status.Register.equals(allAllocateInfo.getStatus())) {
			// [删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
			message = msg.getMessage("message.E2020", new String[] { allAllocateInfo.getAllId(),
					DictUtils.getDictLabel(allAllocateInfo.getStatus(), "all_status", "") }, locale);
			addMessage(model, message);

			allAllocateInfo.setSearchFlag("init");
			return listHandout(allAllocateInfo, request, response, model);
		}

		// 取得该网点的物品信息
		List<AllAllocateInfo> itemList = allocationService.getQuotaInfoByrOffice(allAllocateInfo);

		// 设置物品信息
		ChangeStoreEntity storeEntity = null;
		List<ChangeStoreEntity> entiryList = Lists.newArrayList();
		if (null != itemList && itemList.size() > 0) {
			AllAllocateInfo itemAllocateInfo = itemList.get(0);

			// 保存库房配款的物品信息
			if (AllocationConstant.Status.CashOrderQuotaYes.equals(itemAllocateInfo.getStatus())) {
				for (AllAllocateItem item : itemAllocateInfo.getAllAllocateItemList()) {
					// 保存物品信息
					if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
						storeEntity = new ChangeStoreEntity();
						storeEntity.setGoodsId(AllocationCommonUtils.getGoodsKey(item));
						storeEntity.setNum(item.getMoneyNumber());
						entiryList.add(storeEntity);
					}
				}
			}
		}

		// 执行删除
		try {
			// 删除下拨信息
			allocationService.deleteByAllId(allAllocateInfo, entiryList);
			message = msg.getMessage("message.I2010", null, locale);
		} catch (BusinessException e) {
			message = e.getMessage();
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/allocation/v01/boxHandover/handout";
	}

	/**
	 * 页面跳转
	 * 
	 * @author qph
	 * @version 2017年7月31日
	 * 
	 * 
	 * @param allAllocateInfo
	 *            申请信息
	 * @param model
	 * @return 跳转页面（查看页面或编辑页面）
	 */
	@RequestMapping(value = "form")
	public String form(AllAllocateInfo AllAllocateInfo, Model model, SessionStatus status, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isBlank(AllAllocateInfo.getAllId())) {
			// 设定业务类型：申请下拨
			AllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
			// 设置登记机构
			AllAllocateInfo.setrOffice(UserUtils.getUser().getOffice());
		} else {
			for (AllAllocateItem item : AllAllocateInfo.getAllAllocateItemList()) {
				item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
			}
			// 初始化审批物品为申请物品
			List<AllAllocateItem> approveItemList = Lists.newArrayList();
			for (AllAllocateItem item : AllAllocateInfo.getAllAllocateItemList()) {
				AllAllocateItem approveItem = new AllAllocateItem();
				approveItem.setGoodsId(item.getGoodsId());
				approveItem.setGoodsName(item.getGoodsName());
				approveItem.setConfirmFlag(AllocationConstant.confirmFlag.Confirm);
				approveItem.setMoneyNumber(item.getMoneyNumber());
				approveItem.setMoneyAmount(item.getMoneyAmount());
				approveItemList.add(approveItem);
			}
			AllAllocateInfo.getAllAllocateItemList().addAll(approveItemList);

			// 重新计算物品总价值
			allocationService.computation(AllAllocateInfo);
		}
		model.addAttribute("allocatedOrderSession", AllAllocateInfo);
		model.addAttribute("allAllocateInfo", AllAllocateInfo);
		return "modules/allocation/v01/out/box/boxOutForm";

	}

	/**
	 * 
	 * Title: add
	 * <p>
	 * Description: 添加物品
	 * </p>
	 * 
	 * @author: qph
	 * @param allAllocateInfo
	 *            提交物品明细
	 * @param allocatedOrderSession
	 *            Session保持物品明细
	 * @param model
	 * @param request
	 * @param response
	 * @return String 返回类型
	 */
	@RequestMapping(value = "add")
	public String add(AllAllocateInfo AllAllocateInfo,
			@ModelAttribute("allocatedOrderSession") AllAllocateInfo allocatedOrderSession, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 通过币种等属性获取goodsId
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(AllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		for (AllAllocateItem item : allocatedOrderSession.getAllAllocateItemList()) {
			if (item.getGoodsId().equals(strGoodsId)
					&& AllocationConstant.confirmFlag.Confirm.equals(item.getConfirmFlag())) {
				isExist = true;
				break;
			}
			iIndex++;
		}
		if (isExist == true) {
			message = msg.getMessage("message.I2014",
					new String[] { allocatedOrderSession.getAllAllocateItemList().get(iIndex).getGoodsName() }, locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId",
					allocatedOrderSession.getAllAllocateItemList().get(iIndex).getGoodsId());
		} else {
			AllAllocateItem item = new AllAllocateItem();
			item.setConfirmFlag(AllocationConstant.confirmFlag.Confirm);

			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(AllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			if (goodsValue == null) {
				message = msg.getMessage("message.E2023", new String[] { strGoodsId }, locale);
				addMessage(model, message);
				model.addAttribute("allAllocateInfo", allocatedOrderSession);
				return "modules/allocation/v01/out/box/boxOutForm";
			}
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
					: goodsValue.multiply(new BigDecimal(AllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			allocatedOrderSession.getAllAllocateItemList().add(item);
		}
		// 重新计算物品总价值
		allocationService.computation(allocatedOrderSession);
		model.addAttribute("allAllocateInfo", allocatedOrderSession);
		return "modules/allocation/v01/out/box/boxOutForm";

	}

	/**
	 *
	 * 
	 * @author qph
	 * @version 2017年7月31日
	 * 
	 * 
	 * @param allAllocateInfo
	 *            调拨信息
	 * @param targetStatus
	 *            目标状态
	 * @param model
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "/aloneOption")
	public String aloneOption(AllAllocateInfo allAllocateInfo,
			@ModelAttribute("allocatedOrderSession") AllAllocateInfo allocatedOrderSession,
			@RequestParam(value = "targetStatus", required = true) String targetStatus, Model model,
			HttpServletRequest request, HttpServletResponse response, SessionStatus status,
			RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = msg.getMessage("message.I2011", null, locale);
		// 如果没填写审批物品，提示错误信息
		if (AllocationConstant.Status.Register.equals(targetStatus) && (allocatedOrderSession.getConfirmAmount() == null
				|| allocatedOrderSession.getConfirmAmount() == BigDecimal.ZERO)) {
			// [审批失败]：审批总金额不能为0元！
			message = msg.getMessage("message.E2033", null, locale);
			addMessage(model, message);
			return "modules/allocation/v01/out/box/boxOutForm";
		}
		try {
			// 一致性检验
			allocationService.checkVersion(allocatedOrderSession);
			// 审批通过后，更改状态，并输出成功消息
			allocationService.saveAllcation(allAllocateInfo, allocatedOrderSession, model, status, redirectAttributes,
					request, response);
			message = msg.getMessage("message.I2001", new String[] { allocatedOrderSession.getAllId() }, locale);
			addMessage(model, message);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return "modules/allocation/v01/out/box/boxOutForm";
		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allocatedOrderSession.getrOffice().getName());
		paramsList.add(allocatedOrderSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allocatedOrderSession.getBusinessType(),
				allocatedOrderSession.getStatus(), paramsList, allocatedOrderSession.getaOffice().getId(),
				UserUtils.getUser());
		return view(allocatedOrderSession, model);
	}

	/**
	 * @author qph
	 * @version 2017年7月30日
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
	@RequestMapping(value = "view")
	public String view(AllAllocateInfo allocateInfo, Model model) {
		String officeType = UserUtils.getUser().getOffice().getType();
		// 押运人员
		String escortName = "";
		// 授权人
		String managerName = "";
		List<String> handoverIdList = Lists.newLinkedList();
		if (!"".equals(allocateInfo.getStoreHandoverId())) {
			List<AllHandoverDetail> allHandoverDetailList = allocationService
					.findDetailByHandoverId(allocateInfo.getStoreHandoverId());
			for (AllHandoverDetail detail : allHandoverDetailList) {
				if (AllocationConstant.OperationType.ACCEPT.equals(detail.getOperationType())) {
					handoverIdList.add(detail.getEscortId());
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
		model.addAttribute("handoverIdList",handoverIdList);
		model.addAttribute("officeType", officeType);
		model.addAttribute("escortName", escortName);
		model.addAttribute("managerName", managerName);
		model.addAttribute("allAllocateInfo", allocateInfo);
		return "modules/allocation/v01/out/box/boxOutDetail";
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
			@ModelAttribute("allocatedOrderSession") AllAllocateInfo allocatedOrderSession, Model model) {

		AllAllocateItem updateGoodsItem = new AllAllocateItem();
		for (AllAllocateItem item : allocatedOrderSession.getAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.confirmFlag.Confirm.equals(item.getConfirmFlag())) {
				updateGoodsItem = item;
				break;
			}
		}
		updateGoodsItem.setGoodsName(StoreCommonUtils.getGoodsName(updateGoodsItem.getGoodsId()));
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
			@ModelAttribute("allocatedOrderSession") AllAllocateInfo allocationOrderCash, Model model) {

		for (AllAllocateItem item : allocationOrderCash.getAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())
					&& AllocationConstant.confirmFlag.Confirm.equals(item.getConfirmFlag())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}

		// 重新计算物品总价值
		allocationService.computation(allocationOrderCash);
		model.addAttribute("allAllocateInfo", allocationOrderCash);

		return "modules/allocation/v01/out/box/boxOutForm";
	}

	/**
	 * 
	 * Title: deleteGoods
	 * <p>
	 * Description: 删除物品信息
	 * </p>
	 * 
	 * @author: qph
	 * @param goodsId
	 *            物品ID
	 * @param allocatedOrderSession
	 *            Session保持物品信息
	 * @param model
	 * @param request
	 * @param response
	 * @return String 返回类型
	 */
	@RequestMapping(value = "deleteGoods")
	public String deleteGoods(String goodsId,
			@ModelAttribute("allocatedOrderSession") AllAllocateInfo allocatedOrderSession, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		int iIndex = 0;
		for (AllAllocateItem item : allocatedOrderSession.getAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.confirmFlag.Confirm.equals(item.getConfirmFlag())) {
				allocatedOrderSession.getAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex++;
		}
		// 重新计算物品总价值
		allocationService.computation(allocatedOrderSession);
		model.addAttribute("allAllocateInfo", allocatedOrderSession);
		return "modules/allocation/v01/out/box/boxOutForm";

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
	@RequiresPermissions("allocation:handout:view")
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
