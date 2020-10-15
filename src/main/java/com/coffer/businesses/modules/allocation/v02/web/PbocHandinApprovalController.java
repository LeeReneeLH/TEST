package com.coffer.businesses.modules.allocation.v02.web;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.web.bind.support.SessionStatus;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.GoodDictUtils;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocApprovalPrintDetail;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.StoDocTempInfo;
import com.coffer.businesses.modules.store.v02.entity.StoDocTempUserDetail;
import com.coffer.businesses.modules.store.v02.entity.StoOfficeStamperInfo;

/**
 * 人行上缴审批Controller
 * 
 * @author LLF
 * @version 2016-05-25
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocHandinApproval")
public class PbocHandinApprovalController extends BaseController {
	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@ModelAttribute
	public PbocAllAllocateInfo get(@RequestParam(required = false) String allId) {
		PbocAllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)) {
			entity = pbocAllAllocateInfoService.get(allId);
		}
		if (entity == null) {
			entity = new PbocAllAllocateInfo();
		}
		return entity;
	}

	/**
	 * 跳转至列表页面
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            查询条件
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "bInitFlag", required = false) Boolean bInitFlag, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		// UPDATE-START  原因：金融平台机构用户查看时不加限制条件  update by SonyYuanYang  2018/03/20
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM
						.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
			// UPDATE-END  原因：金融平台机构用户查看时不加限制条件  update by SonyYuanYang  2018/03/20
			// 如果是网点，设置用户的机构号，只能查询当前机构
			if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_OPT.equals(UserUtils.getUser().getUserType())) {
				// 不显示驳回数据
				pbocAllAllocateInfo.setShowRejectData(AllocationConstant.ShowRejectData.SHOW_NONE);
				pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
			} else if (AllocationConstant.SysUserType.CLEARING_CENTER_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CLEARING_CENTER_OPT.equals(UserUtils.getUser().getUserType())) {
				// 不显示驳回数据
				pbocAllAllocateInfo.setShowRejectData(AllocationConstant.ShowRejectData.SHOW_NONE);
				pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice().getParent());
				bInitFlag = false;
			} else {
				// 显示驳回数据
				pbocAllAllocateInfo.setShowRejectData(AllocationConstant.ShowRejectData.SHOW_OUT);
				// 否则查询本金库
				pbocAllAllocateInfo.setRoffice(UserUtils.getUser().getOffice());
			}

			if (AllocationConstant.SysUserType.CENTRAL_OPT.equals(UserUtils.getUser().getUserType())) {
				// 审批员 只显示待审批项
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
		}

		// 查询条件：用款/上缴 开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：用款/上缴 结束时间
		if (pbocAllAllocateInfo.getCreateTimeEnd() != null) {
			pbocAllAllocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getCreateTimeEnd())));
		}
		// // 人行管理员显示全部信息
		// if
		// (!AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType()))
		// {
		// // 初始状态设定
		// if (bInitFlag != null && bInitFlag == true) {
		// //人行上缴审批列表
		// // 初始页面显示待审批
		// pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS);
		// }
		// }
		/* 业务类型设定 */
		// 人行上缴审批列表
		if (AllocationConstant.PageType.StoreHandinApprovalList.equals(pbocAllAllocateInfo.getPageType())
				&& StringUtils.isBlank(pbocAllAllocateInfo.getBusinessType())) {
			if (AllocationConstant.SysUserType.CLEARING_CENTER_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CLEARING_CENTER_OPT.equals(UserUtils.getUser().getUserType())) {
				// 清分中心只显示代理上缴
				pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
			} else {
				List<String> businessTypeList = Lists.newArrayList();
				// 初始列表页面显示 业务类型为 申请上缴和代理上缴的数据
				businessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN);
				businessTypeList.add(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
				pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
			}
		}

		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService
				.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		return "modules/allocation/v02/handinApproval/pbocHandinApprovalList";
	}

	/**
	 * 页面跳转
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            申请信息
	 * @param model
	 * @return 跳转页面（查看页面或编辑页面）
	 */
	@RequestMapping(value = "form")
	public String form(PbocAllAllocateInfo pbocAllAllocateInfo, Model model) {

		for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
		}

		if (AllocationConstant.PageType.StoreHandinApprovalEdit.equals(pbocAllAllocateInfo.getPageType())) {
			// 初始化审批物品为申请物品
			List<PbocAllAllocateItem> approveItemList = Lists.newArrayList();
			for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
				PbocAllAllocateItem approveItem = new PbocAllAllocateItem();
				approveItem.setGoodsId(item.getGoodsId());
				approveItem.setRegistType(AllocationConstant.RegistType.RegistStore);
				approveItem.setGoodsName(item.getGoodsName());
				approveItem.setMoneyNumber(item.getMoneyNumber());
				approveItem.setMoneyAmount(item.getMoneyAmount());
				approveItemList.add(approveItem);
			}
			pbocAllAllocateInfo.getPbocAllAllocateItemList().addAll(approveItemList);
		}

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(pbocAllAllocateInfo);
		// 缓存用户数据
		UserUtils.putCache(pbocAllAllocateInfo.getAllId(), pbocAllAllocateInfo);
		model.addAttribute("userCacheId", pbocAllAllocateInfo.getAllId());
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		if (AllocationConstant.PageType.StoreHandinApprovalView.equals(pbocAllAllocateInfo.getPageType())) {
			// 查看时设置申请总金额和审批总金额大写
			pbocAllAllocateInfo.setRegisterAmount(
					pbocAllAllocateInfo.getRegisterAmount() == null ? 0d : pbocAllAllocateInfo.getRegisterAmount());
			pbocAllAllocateInfo.setConfirmAmount(
					pbocAllAllocateInfo.getConfirmAmount() == null ? 0d : pbocAllAllocateInfo.getConfirmAmount());
			pbocAllAllocateInfo.setRegisterAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getRegisterAmount()));
			pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));

			if (pbocAllAllocateInfo.getInstoreAmount() != null) {
				pbocAllAllocateInfo.setInstoreAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getInstoreAmount()));
			}

			if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
				// 填充出入库物品所处库区位置
				for (PbocAllAllocateDetail allcateDetail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
					StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
					stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());
					if (AllocationConstant.InOutCoffer.IN.equals(pbocAllAllocateInfo.getInoutType())) {
						stoGoodsLocationInfo.setInStoreAllId(pbocAllAllocateInfo.getAllId());
					} else {
						stoGoodsLocationInfo.setOutStoreAllId(pbocAllAllocateInfo.getAllId());
					}
					// 原封券以外，则截取rfid前八位
					allcateDetail.setRfid(StringUtils.left(allcateDetail.getRfid(), 8));

					stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
					allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
				}
			}
			// add-start 交接人员照片显示 add by yanbingxu 2018/04/03
			AllocationCommonUtils.pbocHandoverFilter(pbocAllAllocateInfo, model);
			// add-end
			return "modules/allocation/v02/goodsAllocatedCommon/pbocShowCommonDetail";
		}

		return "modules/allocation/v02/handinApproval/pbocHandinApprovalForm";
	}

	/**
	 * 
	 * Title: add
	 * <p>
	 * Description: 添加物品
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 *            提交物品明细
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return String 返回类型
	 */
	@RequestMapping(value = "add")
	public String add(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo handinApprovalSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		for (PbocAllAllocateItem item : handinApprovalSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(strGoodsId)
					&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				isExist = true;
				break;
			}
			iIndex++;

		}
		if (isExist == true) {
			// // 累加计算
			// PbocAllAllocateItem item =
			// handinApprovalSession.getPbocAllAllocateItemList().get(iIndex);
			// item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber()
			// + item.getMoneyNumber());
			// BigDecimal goodsValue =
			// StoreCommonUtils.getGoodsValue(strGoodsId);
			// item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) :
			// goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
			message = msg.getMessage("message.I2014",
					new String[] { handinApprovalSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName() },
					locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId",
					handinApprovalSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {
			PbocAllAllocateItem item = new PbocAllAllocateItem();
			item.setRegistType(AllocationConstant.RegistType.RegistStore);
			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
					: goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			handinApprovalSession.getPbocAllAllocateItemList().add(item);
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(handinApprovalSession);
		model.addAttribute("pbocAllAllocateInfo", handinApprovalSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/handinApproval/pbocHandinApprovalForm";
	}

	/**
	 * 
	 * Title: deleteGoods
	 * <p>
	 * Description: 删除物品信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param goodsId
	 *            物品ID
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return String 返回类型
	 */
	@RequestMapping(value = "deleteGoods")
	public String deleteGoods(String goodsId, @RequestParam(value = "userCacheId", required = true) String userCacheId,
			Model model, HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo handinApprovalSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		int iIndex = 0;
		for (PbocAllAllocateItem item : handinApprovalSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				handinApprovalSession.getPbocAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex++;
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(handinApprovalSession);
		model.addAttribute("pbocAllAllocateInfo", handinApprovalSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/handinApproval/pbocHandinApprovalForm";
	}

	/**
	 * 
	 * Title: toUpdateGoodsItem
	 * <p>
	 * Description: 修改物品数量页面
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param goodsId
	 *            物品信息
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "toUpdateGoodsItem")
	public String toUpdateGoodsItem(String goodsId,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model) {

		PbocAllAllocateInfo handinApprovalSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();
		for (PbocAllAllocateItem item : handinApprovalSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				updateGoodsItem = item;
				break;
			}
		}

		model.addAttribute("updateGoodsItem", updateGoodsItem);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/goodsAllocatedCommon/pbocUpdateGoodsCommonForm";
	}

	/**
	 * 
	 * Title: updateGoodsItem
	 * <p>
	 * Description: 修改物品数量
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param updateGoodsItem
	 *            被修改物品信息
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "updateGoodsItem")
	public String updateGoodsItem(PbocAllAllocateItem updateGoodsItem,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model) {

		PbocAllAllocateInfo handinApprovalSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		for (PbocAllAllocateItem item : handinApprovalSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())
					&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(handinApprovalSession);
		model.addAttribute("pbocAllAllocateInfo", handinApprovalSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/handinApproval/pbocHandinApprovalForm";
	}

	/**
	 * 批量修改业务状态
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 * 
	 * @param allIds
	 *            流水单号列表
	 * @param targetStatus
	 *            目标状态
	 * @param model
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "/batchOperation")
	public String batchOperation(@RequestParam(value = "allIds", required = true) String allIds,
			@RequestParam(value = "targetStatus", required = false) String targetStatus,
			@RequestParam(value = "pageType", required = true) String pageType, Model model, HttpServletRequest request,
			HttpServletResponse response) {

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (StringUtils.isNotBlank(allIds)) {
			String[] paramArray = allIds.split(Constant.Punctuation.COMMA);
			List<String> paramList = Arrays.asList(paramArray);
			List<String> allIdList = Lists.newArrayList();
			List<String> toQuotaAllIdList = Lists.newArrayList();

			try {
				// 如果目标状态是 驳回或者待入库，则当前业务流水状态必须为 待审批，否则提示错误
				if (AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
						|| AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS
								.equals(targetStatus)) {
					for (String strParam : paramList) {
						String[] array = strParam.split(Constant.Punctuation.HALF_COLON);
						PbocAllAllocateInfo paramInfo = new PbocAllAllocateInfo();
						paramInfo.setAllId(array[0]);
						paramInfo.setStrUpdateDate(array[1]);
						// 检查数据一致性
						pbocAllAllocateInfoService.checkVersion(paramInfo);

						PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(array[0]);

						// 如果当前业务已被删除，提示错误信息
						if (AllocationConstant.deleteFlag.Invalid.equals(pbocAllAllocateInfo.getDelFlag())) {
							pbocAllAllocateInfo = new PbocAllAllocateInfo();
							pbocAllAllocateInfo.setPageType(pageType);
							// [操作失败]：业务数据已经被删除，请重新查询后再进行操作！
							message = msg.getMessage("message.E2036", new String[] { pbocAllAllocateInfo.getAllId() },
									locale);
							addMessage(model, message);
							return list(pbocAllAllocateInfo, true, request, response, model);
						}
						pbocAllAllocateInfo.setPageType(pageType);
						if (!AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS
								.equals(pbocAllAllocateInfo.getStatus())) {
							// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
							message = msg.getMessage("message.E2035", new String[] { pbocAllAllocateInfo.getAllId() },
									locale);
							addMessage(model, message);
							return list(pbocAllAllocateInfo, false, request, response, model);
						}
						allIdList.add(pbocAllAllocateInfo.getAllId());
					}
				}
				// 审批通过后，更改状态，并输出成功消息
				pbocAllAllocateInfoService.updateStatusByAllIds(allIdList, UserUtils.getUser(), targetStatus,
						toQuotaAllIdList);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
				addMessage(model, message);
				PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
				pbocAllAllocateInfo.setPageType(pageType);
				return list(pbocAllAllocateInfo, true, request, response, model);
			}
		}

		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setPageType(pageType);
		return list(pbocAllAllocateInfo, true, request, response, model);
	}

	/**
	 * 单独审批或驳回操作
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            调拨信息
	 * @param userCacheId
	 *            用户缓存ID
	 * @param targetStatus
	 *            目标状态
	 * @param model
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "/aloneOption")
	public String aloneOption(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "userCacheId", required = true) String userCacheId,
			@RequestParam(value = "targetStatus", required = true) String targetStatus, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo handinApprovalSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = msg.getMessage("message.I2011", null, locale);
		// 如果当前业务已被删除，提示错误信息
		if (AllocationConstant.deleteFlag.Invalid.equals(pbocAllAllocateInfo.getDelFlag())) {
			// [操作失败]：业务数据已经被删除，请重新查询后再进行操作！
			message = msg.getMessage("message.E2036", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			addMessage(model, message);
			return list(pbocAllAllocateInfo, true, request, response, model);
		}
		// 如果目标状态是 驳回、待配款或者待入库，则当前业务流水状态必须为 待审批，否则提示错误
		if (AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS.equals(targetStatus)
				&& !AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
			// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
			message = msg.getMessage("message.E2035", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			addMessage(model, message);
			return list(pbocAllAllocateInfo, false, request, response, model);
		}
		// 如果没填写审批物品，提示错误信息
		if (!AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
				&& (handinApprovalSession.getConfirmAmount() == null
						|| handinApprovalSession.getConfirmAmount() == 0d)) {
			pbocAllAllocateInfo.setPageType(AllocationConstant.PageType.StoreHandinApprovalEdit);
			// [审批失败]：审批总金额不能为0元！
			message = msg.getMessage("message.E2033", null, locale);
			addMessage(model, message);
			model.addAttribute("userCacheId", userCacheId);
			return "modules/allocation/v02/handinApproval/pbocHandinApprovalForm";
		}

		try {
			// 数据一致性检查
			pbocAllAllocateInfoService.checkVersion(pbocAllAllocateInfo);
			// 审批通过后，更改状态，并输出成功消息
			pbocAllAllocateInfoService.updateStatusByAllId(handinApprovalSession, UserUtils.getUser(), targetStatus);
			addMessage(model, message);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
		}
		// ADD-START  原因：发送通知  add by SonyYuanYang  2018/03/17
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(handinApprovalSession.getRoffice().getName());
		paramsList.add(handinApprovalSession.getAllId());
		// ADD-END  原因：发送通知  add by SonyYuanYang  2018/03/17
		// 驳回时发送给登记机构，待入库时发送给接收机构
		if (AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(handinApprovalSession.getStatus())) {
			SysCommonUtils.allocateMessageQueueAdd(handinApprovalSession.getBusinessType(),
					handinApprovalSession.getStatus(), paramsList, handinApprovalSession.getRoffice().getId(), UserUtils.getUser());
		} else {
			SysCommonUtils.allocateMessageQueueAdd(handinApprovalSession.getBusinessType(),
					handinApprovalSession.getStatus(), paramsList, handinApprovalSession.getAoffice().getId(), UserUtils.getUser());
		}
		
		PbocAllAllocateInfo templlAllocateInfo = new PbocAllAllocateInfo();
		templlAllocateInfo.setPageType(pbocAllAllocateInfo.getPageType());
		return list(templlAllocateInfo, true, request, response, model);
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(PbocAllAllocateInfo pbocAllAllocateInfo, SessionStatus status) {
		// 清空Session
		status.setComplete();
		return "redirect:" + adminPath + "/allocation/v02/pbocHandinApproval/list?bInitFlag=true&pageType="
				+ pbocAllAllocateInfo.getPageType();
	}

	/**
	 * 批量打印
	 * 
	 * @author WangBaozhong
	 * @version 2016年8月18日
	 * 
	 * 
	 * @param allIds
	 *            流水单号列表
	 * @return 列表页面
	 */
	@RequestMapping(value = "/batchPrint")
	public String batchPrint(@RequestParam(value = "allIds", required = true) String allIds, Model model) {

		String[] allIdArray = allIds.split(Constant.Punctuation.COMMA);
		List<String> allIdList = Arrays.asList(allIdArray);
		List<PbocApprovalPrintDetail> printDataList = Lists.newArrayList();
		// 总计流通券
		Double fullCouponAllAmount = 0d;
		// 总计残损券
		Double damagedCouponAllAmount = 0d;
		// 总计金额
		Double allAmount = 0d;

		for (String allId : allIdList) {
			PbocApprovalPrintDetail printDetail = pbocAllAllocateInfoService.getApprovalPrintDetail(allId);
			fullCouponAllAmount = fullCouponAllAmount + printDetail.getFullCouponAmount();
			damagedCouponAllAmount = damagedCouponAllAmount + printDetail.getDamagedCouponAmount();
			allAmount = allAmount + printDetail.getConfirmAmount();
			printDataList.add(printDetail);
		}
		model.addAttribute("printDataList", printDataList);

		model.addAttribute("fullCouponAllAmount", fullCouponAllAmount);
		model.addAttribute("damagedCouponAllAmount", damagedCouponAllAmount);
		model.addAttribute("allAmount", allAmount);
		// 打印审批明细
		return "modules/allocation/v02/handinApproval/printHandinDetail";
	}

	/**
	 * 打印上缴审批明细
	 * 
	 * @author WangBaozhong
	 * @version 2016年8月25日
	 * 
	 * 
	 * @param allId
	 *            流水单号
	 * @return 列表页面
	 */
	@RequestMapping(value = "/printApprovalGoodDetail")
	public String printApprovalGoodDetail(@RequestParam(value = "allId", required = true) String allId, Model model) {

		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(allId);
		pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		// 打印审批明细
		return "modules/allocation/v02/goodsAllocatedCommon/printApprovalGoodDetail";
	}

	/**
	 * 
	 * Title: checkGoodsTypeCnt
	 * <p>
	 * Description: 查询物品类型数量
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param allId
	 * @return String 返回类型
	 */
	@RequestMapping(value = "/checkGoodsTypeCnt")
	@ResponseBody
	public String checkGoodsTypeCnt(@RequestParam(value = "allId", required = true) String allId) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(allId);

		if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
			// 填充出入库物品所处库区位置
			for (PbocAllAllocateDetail allcateDetail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
				StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
				stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());
				if (AllocationConstant.InOutCoffer.IN.equals(pbocAllAllocateInfo.getInoutType())) {
					stoGoodsLocationInfo.setInStoreAllId(pbocAllAllocateInfo.getAllId());
				}
				stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
				allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
			}
		}

		int iFullCoupon = 0;
		int iDamagedCoupon = 0;

		for (PbocAllAllocateDetail detail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
			StoGoodSelect goodsSelect = StoreCommonUtils.splitGood(detail.getGoodsLocationInfo().getGoodsId());

			if (StoreConstant.GoodsClassification.FULL_COUPON.equals(goodsSelect.getClassification())) {
				iFullCoupon++;
			} else if (StoreConstant.GoodsClassification.DAMAGED_COUPON.equals(goodsSelect.getClassification())) {
				iDamagedCoupon++;
			}
		}

		rtnMap.put("iFullCoupon", iFullCoupon);
		rtnMap.put("iDamagedCoupon", iDamagedCoupon);

		return gson.toJson(rtnMap);
	}

	/**
	 * 
	 * Title: printHandinFinishDetail
	 * <p>
	 * Description: 打印上缴明细清单
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param allId
	 * @param classification
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "/printHandinFinishFullCouponDetail")
	public String printHandinFinishFullCouponDetail(@RequestParam(value = "allId", required = true) String allId,
			@RequestParam(value = "classification", required = true) String classification, Model model) {

		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(allId);

		if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
			// 填充出入库物品所处库区位置
			for (PbocAllAllocateDetail allcateDetail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
				StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
				stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());
				if (AllocationConstant.InOutCoffer.IN.equals(pbocAllAllocateInfo.getInoutType())) {
					stoGoodsLocationInfo.setInStoreAllId(pbocAllAllocateInfo.getAllId());
				}
				stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
				allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
			}
		}

		List<PbocAllAllocateItem> itemList = Lists.newArrayList();
		Map<String, PbocAllAllocateItem> itemMap = Maps.newHashMap();
		String goodsId = "";
		for (PbocAllAllocateDetail detail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
			goodsId = detail.getGoodsLocationInfo().getGoodsId();
			StoGoodSelect goodsSelect = StoreCommonUtils.splitGood(goodsId);
			if (classification.equals(goodsSelect.getClassification())) {
				if (itemMap.containsKey(goodsId)) {
					PbocAllAllocateItem item = itemMap.get(goodsId);
					item.setMoneyNumber(item.getMoneyNumber() + 1);
				} else {
					PbocAllAllocateItem item = new PbocAllAllocateItem();
					item.setMoneyNumber(1l);
					item.setGoodsId(goodsId);
					itemMap.put(goodsId, item);
				}
			}

		}

		BigDecimal allMoney = new BigDecimal(0d);

		PbocAllAllocateItem allocateItem = null;
		Iterator<String> keyIterator = itemMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			allocateItem = itemMap.get(keyIterator.next());
			BigDecimal value = StoreCommonUtils.getGoodsValue(allocateItem.getGoodsId());
			allocateItem.setMoneyAmount(value.multiply(new BigDecimal(allocateItem.getMoneyNumber())));
			allocateItem.setGoodsName(StoreCommonUtils.getPbocGoodsNameByGoodId2(allocateItem.getGoodsId()));
			allMoney = allMoney.add(allocateItem.getMoneyAmount());
			itemList.add(allocateItem);
		}
		int iListSize = 8 - itemList.size();
		for (int iIndex = 0; iIndex < iListSize; iIndex++) {
			itemList.add(new PbocAllAllocateItem());
		}

		StoDocTempInfo stoDocTempInfo = StoreCommonUtils.getStoDocTempInfo(pbocAllAllocateInfo.getBusinessType(),
				pbocAllAllocateInfo.getStatus(), pbocAllAllocateInfo.getRoffice().getId());
		if (stoDocTempInfo != null) {
			model.addAttribute("officeStamperInfoId", stoDocTempInfo.getOfficeStamperId());
			model.addAttribute("pbocOfficeStamperInfoId", stoDocTempInfo.getPbocOfficeStamperId());
			for (StoDocTempUserDetail stoDocTempUserDetail : stoDocTempInfo.getDocTempUserDetailList()) {
				if ("101".equals(stoDocTempUserDetail.getResponsibilityType())) {
					model.addAttribute("businessMgr", stoDocTempUserDetail.getEscortId());
				} else if ("102".equals(stoDocTempUserDetail.getResponsibilityType())) {
					model.addAttribute("rechecker", stoDocTempUserDetail.getEscortId());
				} else if ("103".equals(stoDocTempUserDetail.getResponsibilityType())) {
					model.addAttribute("makeTable", stoDocTempUserDetail.getEscortId());
				}

			}
		} else {
			model.addAttribute("pbocOfficeStamperInfoId", "");
			model.addAttribute("officeStamperInfoId", "");
			model.addAttribute("businessMgr", "");
			model.addAttribute("rechecker", "");
			model.addAttribute("makeTable", "");
		}

		// 交接库管员ID
		int userIndex = 1;
		for (PbocAllHandoverUserDetail userDetail : pbocAllAllocateInfo.getPbocAllHandoverInfo()
				.getHandoverUserDetailList()) {
			// 类型为接收人
			if (AllocationConstant.UserType.accept.equals(userDetail.getType())) {
				model.addAttribute("storeMgrId" + userIndex, userDetail.getEscortId());
				userIndex++;
			}

		}
		userIndex--;
		// 交接授权人ID
		if (!StringUtils.isBlank(pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId())) {
			userIndex++;
			model.addAttribute("storeMgrId" + userIndex,
					pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId());
			model.addAttribute("managerUserId", pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId());
		} else {
			model.addAttribute("managerUserId", "");
		}

		StringBuilder remark = new StringBuilder();
		if (StringUtils.isNotBlank(pbocAllAllocateInfo.getRoffice().getCode())) {
			remark.append(pbocAllAllocateInfo.getRoffice().getCode());
			remark.append(Constant.Punctuation.HALF_SPACE);
		}
		remark.append(pbocAllAllocateInfo.getRofficeName());
		Locale locale = LocaleContextHolder.getLocale();
		// 交
		remark.append(msg.getMessage("allocation.handin.lable", null, locale));
		remark.append(GoodDictUtils.getDictLabel(classification, "classification", ""));

		// 设定摘要
		model.addAttribute("remark", remark.toString());
		// 登记机构代码
		model.addAttribute("rofficeCode", pbocAllAllocateInfo.getRoffice().getCode());
		// 交接人员数量
		model.addAttribute("handoverUserSize", userIndex);
		// 登记机构名称
		model.addAttribute("rofficeName", pbocAllAllocateInfo.getRofficeName());
		model.addAttribute("printDataList", itemList);
		// 总金额
		model.addAttribute("allMoney", allMoney);
		// 总金额 (大写)
		model.addAttribute("allMoneyBig", NumToRMB.changeToBig(allMoney.doubleValue()));
		// 打印审批明细
		return "modules/allocation/v02/handinApproval/printHandinFinishDetail";
	}

	/**
	 * 
	 * Title: printHandinFinishDetail
	 * <p>
	 * Description: 打印上缴明细清单
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param allId
	 * @param classification
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "/printHandinFinishDamagedCouponDetail")
	public String printHandinFinishDamagedCouponDetail(@RequestParam(value = "allId", required = true) String allId,
			@RequestParam(value = "classification", required = true) String classification, Model model) {

		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(allId);

		if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
			// 填充出入库物品所处库区位置
			for (PbocAllAllocateDetail allcateDetail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
				StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
				stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());
				if (AllocationConstant.InOutCoffer.IN.equals(pbocAllAllocateInfo.getInoutType())) {
					stoGoodsLocationInfo.setInStoreAllId(pbocAllAllocateInfo.getAllId());
				}
				stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
				allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
			}
		}

		List<PbocAllAllocateItem> itemList = Lists.newArrayList();
		Map<String, PbocAllAllocateItem> itemMap = Maps.newHashMap();
		String goodsId = "";
		for (PbocAllAllocateDetail detail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
			goodsId = detail.getGoodsLocationInfo().getGoodsId();
			StoGoodSelect goodsSelect = StoreCommonUtils.splitGood(goodsId);
			if (classification.equals(goodsSelect.getClassification())) {
				if (itemMap.containsKey(goodsId)) {
					PbocAllAllocateItem item = itemMap.get(goodsId);
					item.setMoneyNumber(item.getMoneyNumber() + 1);
				} else {
					PbocAllAllocateItem item = new PbocAllAllocateItem();
					item.setMoneyNumber(1l);
					item.setGoodsId(goodsId);
					itemMap.put(goodsId, item);
				}
			}

		}

		BigDecimal allMoney = new BigDecimal(0d);

		PbocAllAllocateItem allocateItem = null;
		Iterator<String> keyIterator = itemMap.keySet().iterator();
		while (keyIterator.hasNext()) {
			allocateItem = itemMap.get(keyIterator.next());
			BigDecimal value = StoreCommonUtils.getGoodsValue(allocateItem.getGoodsId());
			allocateItem.setMoneyAmount(value.multiply(new BigDecimal(allocateItem.getMoneyNumber())));
			allocateItem.setGoodsName(StoreCommonUtils.getPbocGoodsNameByGoodId2(allocateItem.getGoodsId()));
			allMoney = allMoney.add(allocateItem.getMoneyAmount());
			itemList.add(allocateItem);
		}
		int iListSize = 8 - itemList.size();
		for (int iIndex = 0; iIndex < iListSize; iIndex++) {
			itemList.add(new PbocAllAllocateItem());
		}

		StoDocTempInfo stoDocTempInfo = StoreCommonUtils.getStoDocTempInfo(pbocAllAllocateInfo.getBusinessType(),
				pbocAllAllocateInfo.getStatus(), pbocAllAllocateInfo.getRoffice().getId());
		if (stoDocTempInfo != null) {
			model.addAttribute("officeStamperInfoId", stoDocTempInfo.getOfficeStamperId());
			model.addAttribute("pbocOfficeStamperInfoId", stoDocTempInfo.getPbocOfficeStamperId());

			for (StoDocTempUserDetail stoDocTempUserDetail : stoDocTempInfo.getDocTempUserDetailList()) {
				if ("101".equals(stoDocTempUserDetail.getResponsibilityType())) {
					model.addAttribute("businessMgr", stoDocTempUserDetail.getEscortId());
				} else if ("102".equals(stoDocTempUserDetail.getResponsibilityType())) {
					model.addAttribute("rechecker", stoDocTempUserDetail.getEscortId());
				} else if ("103".equals(stoDocTempUserDetail.getResponsibilityType())) {
					model.addAttribute("makeTable", stoDocTempUserDetail.getEscortId());
				}

			}
		} else {
			model.addAttribute("pbocOfficeStamperInfoId", "");
			model.addAttribute("officeStamperInfoId", "");
			model.addAttribute("businessMgr", "");
			model.addAttribute("rechecker", "");
			model.addAttribute("makeTable", "");
		}

		// 交接库管员ID
		int userIndex = 1;
		for (PbocAllHandoverUserDetail userDetail : pbocAllAllocateInfo.getPbocAllHandoverInfo()
				.getHandoverUserDetailList()) {
			// 类型为接收人
			if (AllocationConstant.UserType.accept.equals(userDetail.getType())) {
				model.addAttribute("storeMgrId" + userIndex, userDetail.getEscortId());
				userIndex++;
			}

		}
		userIndex--;
		// 交接授权人ID
		if (!StringUtils.isBlank(pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId())) {
			userIndex++;
			model.addAttribute("storeMgrId" + userIndex,
					pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId());
			model.addAttribute("managerUserId", pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId());
		} else {
			model.addAttribute("managerUserId", "");
		}

		StringBuilder remark = new StringBuilder();
		if (StringUtils.isNotBlank(pbocAllAllocateInfo.getRoffice().getCode())) {
			remark.append(pbocAllAllocateInfo.getRoffice().getCode());
			remark.append(Constant.Punctuation.HALF_SPACE);
		}
		remark.append(pbocAllAllocateInfo.getRofficeName());
		Locale locale = LocaleContextHolder.getLocale();
		// 交
		remark.append(msg.getMessage("allocation.handin.lable", null, locale));
		remark.append(GoodDictUtils.getDictLabel(classification, "classification", ""));

		// 设定摘要
		model.addAttribute("remark", remark.toString());

		// 登记机构代码
		model.addAttribute("rofficeCode", pbocAllAllocateInfo.getRoffice().getCode());
		// 交接人员数量
		model.addAttribute("handoverUserSize", userIndex);
		// 登记机构名称
		model.addAttribute("rofficeName", pbocAllAllocateInfo.getRofficeName());
		model.addAttribute("printDataList", itemList);
		// 总金额
		model.addAttribute("allMoney", allMoney);
		// 总金额 (大写)
		model.addAttribute("allMoneyBig", NumToRMB.changeToBig(allMoney.doubleValue()));
		// 打印审批明细
		return "modules/allocation/v02/handinApproval/printHandinFinishDetail";
	}

	/**
	 * 
	 * Title: showOfficeStamperImage
	 * <p>
	 * Description: 显示机构印章图片
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param stamperType
	 *            印章类型
	 * @param request
	 * @param response
	 * @throws IOException
	 *             void 返回类型
	 */
	@RequestMapping(value = "showOfficeStamperImage")
	public void showOfficeStamperImage(@RequestParam(required = true) String officeStamperInfoId,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");

		StoOfficeStamperInfo rtn = StoreCommonUtils.getStoOfficeStamperInfoById(officeStamperInfoId);

		if (rtn != null) {
			byte[] imageBytes = rtn.getOfficeStamper();
			if (imageBytes != null) {
				OutputStream out = response.getOutputStream();
				out.write(imageBytes);
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * 
	 * Title: showOfficeStamperImage
	 * <p>
	 * Description: 显示机构印章图片
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param stamperType
	 *            印章类型
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "showPbocOfficeStamperImage")
	public void showPbocOfficeStamperImage(@RequestParam(required = true) String officeStamperInfoId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");

		StoOfficeStamperInfo rtn = StoreCommonUtils.getStoOfficeStamperInfoById(officeStamperInfoId);

		if (rtn != null) {
			byte[] imageBytes = StoreCommonUtils.addDateStamper(rtn.getOfficeStamper(), new Color(0, 0, 225));
			if (imageBytes != null) {
				OutputStream out = response.getOutputStream();
				out.write(imageBytes);
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * 
	 * Title: showEscortStamperImage
	 * <p>
	 * Description: 显示人员印章图片
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param escortId
	 * @param request
	 * @param response
	 * @throws IOException
	 *             void 返回类型
	 */
	@RequestMapping(value = "showEscortStamperImage")
	public void showEscortStamperImage(@RequestParam(required = true) String escortId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");

		StoEscortInfo stoEscortInfo = StoreCommonUtils.getEscortById(escortId);

		if (stoEscortInfo != null) {
			byte[] imageBytes = stoEscortInfo.getUserStamper();
			if (imageBytes != null) {
				OutputStream out = response.getOutputStream();
				out.write(imageBytes);
				out.flush();
				out.close();
			}
		}

	}

	/**
	 * 
	 * Title: showStamperImageByUserId
	 * <p>
	 * Description: 显示人员印章图片
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param escortId
	 * @param request
	 * @param response
	 * @throws IOException
	 *             void 返回类型
	 */
	@RequestMapping(value = "showStamperImageByUserId")
	public void showStamperImageByUserId(@RequestParam(required = true) String userId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");

		StoEscortInfo stoEscortInfo = StoreCommonUtils.findByUserId(userId);

		if (stoEscortInfo != null) {
			byte[] imageBytes = stoEscortInfo.getUserStamper();
			if (imageBytes != null) {
				OutputStream out = response.getOutputStream();
				out.write(imageBytes);
				out.flush();
				out.close();
			}
		}

	}
}