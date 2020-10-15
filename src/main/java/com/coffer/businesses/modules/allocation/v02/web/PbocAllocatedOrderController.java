package com.coffer.businesses.modules.allocation.v02.web;

import java.math.BigDecimal;
import java.util.Arrays;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.entity.PbocApprovalPrintDetail;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;

/**
 * 人行下拨申请审批主表管理Controller
 * 
 * @author wangbaozhong
 * @version 2016-06-28
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocAllocatedOrder")
public class PbocAllocatedOrderController extends BaseController {
	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

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
				statusList.add(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
		}

		// 查询条件：预约 开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：预约 结束时间
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
		// // 人行下拨审批列表
		// if
		// (AllocationConstant.PageType.StoreApprovalList.equals(pbocAllAllocateInfo.getPageType()))
		// {
		// // 初始页面显示待审批
		// pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS);
		// }
		// }
		// }
		// 商行申请列表
		// 初始列表页面显示 业务类型为 申请下拨的数据
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION);

		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService
				.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		if (AllocationConstant.PageType.StoreApprovalList.equals(pbocAllAllocateInfo.getPageType())) {
			return "modules/allocation/v02/allocatedApproval/pbocAllocatedApprovalList";
		}
		return "modules/allocation/v02/allocatedOrder/pbocAllocatedOrderList";
	}

	/**
	 * 页面跳转
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月28日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            申请信息
	 * @param model
	 * @return 跳转页面（查看页面或编辑页面）
	 */
	@RequestMapping(value = "form")
	public String form(PbocAllAllocateInfo pbocAllAllocateInfo, Model model) {
		// 生成session的key值
		String userCacheId = UserUtils.createUserCacheId();
		if (StringUtils.isBlank(pbocAllAllocateInfo.getAllId())) {
			// 设定业务类型：申请下拨
			pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION);
			// 设定登记机构
			pbocAllAllocateInfo.setRofficeName(UserUtils.getUser().getOffice().getName());
		} else {
			for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
				item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
			}

			if (AllocationConstant.PageType.StoreApprovalEdit.equals(pbocAllAllocateInfo.getPageType())) {
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
		}
		// 缓存用户数据
		UserUtils.putCache(userCacheId, pbocAllAllocateInfo);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		model.addAttribute("userCacheId", userCacheId);

		if (AllocationConstant.PageType.StoreApprovalView.equals(pbocAllAllocateInfo.getPageType())
				|| AllocationConstant.PageType.BussnessApplicationView.equals(pbocAllAllocateInfo.getPageType())) {
			// 查看时设置申请总金额和审批总金额大写
			pbocAllAllocateInfo.setRegisterAmount(
					pbocAllAllocateInfo.getRegisterAmount() == null ? 0d : pbocAllAllocateInfo.getRegisterAmount());
			pbocAllAllocateInfo.setConfirmAmount(
					pbocAllAllocateInfo.getConfirmAmount() == null ? 0d : pbocAllAllocateInfo.getConfirmAmount());
			pbocAllAllocateInfo.setRegisterAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getRegisterAmount()));
			pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));

			String currentOfficeId = UserUtils.getUser().getOffice().getId();
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
					// 获取原封箱信息
					StoOriginalBanknote originalBankNote = StoreCommonUtils
							.getStoOriginalBanknoteByBoxId(allcateDetail.getRfid(), currentOfficeId);
					if (originalBankNote != null) {
						allcateDetail.setStoOriginalBanknote(originalBankNote);
					} else {
						// 原封券以外，则截取rfid前八位
						allcateDetail.setRfid(StringUtils.left(allcateDetail.getRfid(), 8));
					}

					stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
					allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
				}
			}
			List<String> allIdList = Lists.newArrayList();
			allIdList.add(pbocAllAllocateInfo.getAllId());
			List<PbocAllAllocateInfo> printDataList = pbocAllAllocateInfoService.getQuotaGoodsAreaInfo(allIdList);
			for (PbocAllAllocateInfo allocateInfo : printDataList) {
				for (PbocAllAllocateItem item : allocateInfo.getPbocAllAllocateItemList()) {
					for (AllAllocateGoodsAreaDetail areaDetail : item.getGoodsAreaDetailList()) {
						// 获取原封箱信息
						StoOriginalBanknote originalBankNote = StoreCommonUtils.getStoOriginalBanknoteByBoxId(
								areaDetail.getGoodsLocationInfo().getRfid(), currentOfficeId);
						if (originalBankNote != null) {
							areaDetail.setStoOriginalBanknote(originalBankNote);
						} else {
							// 原封券以外，则截取rfid前八位
							areaDetail.getGoodsLocationInfo()
									.setRfid(StringUtils.left(areaDetail.getGoodsLocationInfo().getRfid(), 8));
						}
					}

				}
			}
			model.addAttribute("printDataList", printDataList);
		}
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(pbocAllAllocateInfo.getPageType())) {
			return "modules/allocation/v02/allocatedApproval/pbocAllocatedApprovalForm";
		} else if (AllocationConstant.PageType.StoreApprovalView.equals(pbocAllAllocateInfo.getPageType())
				|| AllocationConstant.PageType.BussnessApplicationView.equals(pbocAllAllocateInfo.getPageType())) {
			// add-start 交接人员照片显示 add by yanbingxu 2018/04/03
			AllocationCommonUtils.pbocHandoverFilter(pbocAllAllocateInfo, model);
			// add-end			
			return "modules/allocation/v02/goodsAllocatedCommon/pbocShowCommonDetail";
		}
		return "modules/allocation/v02/allocatedOrder/pbocAllocatedOrderForm";
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
	 * @param model
	 * @param request
	 * @param response
	 * @return String 返回类型
	 */
	@RequestMapping(value = "add")
	public String add(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo allocatedOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(strGoodsId)
						&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
					isExist = true;
					break;
				}
				iIndex++;

			}
		} else {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(strGoodsId)
						&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
					isExist = true;
					break;
				}
				iIndex++;

			}
		}
		if (isExist == true) {
			// // 累加计算
			// PbocAllAllocateItem item =
			// allocatedOrderSession.getPbocAllAllocateItemList().get(iIndex);
			// item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber()
			// + item.getMoneyNumber());
			// BigDecimal goodsValue =
			// StoreCommonUtils.getGoodsValue(strGoodsId);
			// item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) :
			// goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
			message = msg.getMessage("message.I2014",
					new String[] { allocatedOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName() },
					locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId",
					allocatedOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {
			PbocAllAllocateItem item = new PbocAllAllocateItem();
			if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
				item.setRegistType(AllocationConstant.RegistType.RegistStore);
			} else {
				item.setRegistType(AllocationConstant.RegistType.RegistPoint);
			}
			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
					: goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			allocatedOrderSession.getPbocAllAllocateItemList().add(item);
		}
		allocatedOrderSession.setApplyDate(pbocAllAllocateInfo.getApplyDate());
		// ADD-START  原因：金融平台用户登录时设置登记机构  add by SonyYuanYang  2018/04/18
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
			allocatedOrderSession.setRoffice(SysCommonUtils.findOfficeById(pbocAllAllocateInfo.getRoffice().getId()));
			allocatedOrderSession.setRofficeName(pbocAllAllocateInfo.getRoffice().getName());
		}
		// ADD-END  原因：金融平台用户登录时设置登记机构  add by SonyYuanYang  2018/04/18
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(allocatedOrderSession);
		model.addAttribute("pbocAllAllocateInfo", allocatedOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
			return "modules/allocation/v02/allocatedApproval/pbocAllocatedApprovalForm";
		}
		return "modules/allocation/v02/allocatedOrder/pbocAllocatedOrderForm";
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
		PbocAllAllocateInfo allocatedOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		int iIndex = 0;
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(goodsId)
						&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
					allocatedOrderSession.getPbocAllAllocateItemList().remove(iIndex);
					break;
				}
				iIndex++;
			}
		} else {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(goodsId)
						&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
					allocatedOrderSession.getPbocAllAllocateItemList().remove(iIndex);
					break;
				}
				iIndex++;
			}
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(allocatedOrderSession);
		model.addAttribute("pbocAllAllocateInfo", allocatedOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
			return "modules/allocation/v02/allocatedApproval/pbocAllocatedApprovalForm";
		}
		return "modules/allocation/v02/allocatedOrder/pbocAllocatedOrderForm";
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
		PbocAllAllocateInfo allocatedOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(goodsId)
						&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
					updateGoodsItem = item;
					break;
				}
			}
		} else {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(goodsId)
						&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
					updateGoodsItem = item;
					break;
				}
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
		PbocAllAllocateInfo allocatedOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())
						&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
					item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
					BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
					item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
							: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
					break;
				}
			}
		} else {
			for (PbocAllAllocateItem item : allocatedOrderSession.getPbocAllAllocateItemList()) {
				if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())
						&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
					item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
					BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
					item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
							: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
					break;
				}
			}
		}

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(allocatedOrderSession);
		model.addAttribute("pbocAllAllocateInfo", allocatedOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		if (AllocationConstant.PageType.StoreApprovalEdit.equals(allocatedOrderSession.getPageType())) {
			return "modules/allocation/v02/allocatedApproval/pbocAllocatedApprovalForm";
		}
		return "modules/allocation/v02/allocatedOrder/pbocAllocatedOrderForm";
	}

	/**
	 * 保存申请明细
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            申请明细信息
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return 返回列表页面
	 */
	@RequestMapping(value = "save")
	public String save(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		PbocAllAllocateInfo allocatedOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";

		if (allocatedOrderSession.getRegisterAmount() == null || allocatedOrderSession.getRegisterAmount() == 0d) {
			// [申请失败]：申请总金额不能为0元！
			message = msg.getMessage("message.E2034", null, locale);
			addMessage(model, message);
			return form(allocatedOrderSession, model);
		}
		if (StringUtils.isNotBlank(pbocAllAllocateInfo.getStatus())
				&& !AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
			// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
			message = msg.getMessage("message.E2035", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			addMessage(model, message);
			return list(pbocAllAllocateInfo, false, request, response, model);
		}

		allocatedOrderSession.setLoginUser(UserUtils.getUser());

		try {
			pbocAllAllocateInfoService.savePbocAllAllocateInfo(allocatedOrderSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
		}
		// ADD-START  原因：发送通知  add by SonyYuanYang  2018/03/11
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allocatedOrderSession.getRoffice().getName());
		paramsList.add(allocatedOrderSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allocatedOrderSession.getBusinessType(),
				allocatedOrderSession.getStatus(), paramsList, allocatedOrderSession.getAoffice().getId(), UserUtils.getUser());
		// ADD-END  原因：发送通知   add by SonyYuanYang  2018/03/11
		PbocAllAllocateInfo tempAllAllocateInfo = new PbocAllAllocateInfo();
		tempAllAllocateInfo.setPageType(allocatedOrderSession.getPageType());
		return list(tempAllAllocateInfo, true, request, response, model);
	}

	/**
	 * 删除申请
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            申请信息
	 * @param redirectAttributes
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "delete")
	public String delete(PbocAllAllocateInfo pbocAllAllocateInfo, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (!AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
			// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
			message = msg.getMessage("message.E2035", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
		} else {
			try {
				pbocAllAllocateInfoService.delete(pbocAllAllocateInfo);
				// message.I2002=[成功]：流水单号{0}删除成功！
				message = msg.getMessage("message.I2002", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			}
		}
		addMessage(redirectAttributes, message);

		return "redirect:" + adminPath + "/allocation/v02/pbocAllocatedOrder/list?repage";
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
				// 如果目标状态是 驳回、待配款，则当前业务流水状态必须为 待审批，否则提示错误
				if ((AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
						|| AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus))
						&& !AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS
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

			// 审批通过时指定库区
			if (AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus)
					&& toQuotaAllIdList.size() > 0) {
				// UPDATE-START  原因：此处考虑到金融机构批量修改状态时，可能操作不同人行机构流水，此方法参数修改为实体类，一个流水对应进行一次库区修改  update by SonyYuanYang  2018/03/15
				for (String allId : toQuotaAllIdList) {
					this.bindingGoodsArea(pbocAllAllocateInfoService.get(allId));
				}
				// UPDATE-END  原因：此处考虑到金融机构批量修改状态时，可能操作不同人行机构流水，此方法参数修改为实体类，一个流水对应进行一次库区修改  update by SonyYuanYang  2018/03/15
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
		PbocAllAllocateInfo allocatedOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
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
		if ((AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
				|| AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus))
				&& !AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
			// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
			message = msg.getMessage("message.E2035", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			addMessage(model, message);
			return list(pbocAllAllocateInfo, false, request, response, model);
		}
		// 如果没填写审批物品，提示错误信息
		if (!AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
				&& (allocatedOrderSession.getConfirmAmount() == null
						|| allocatedOrderSession.getConfirmAmount() == 0d)) {
			pbocAllAllocateInfo.setPageType(AllocationConstant.PageType.StoreApprovalEdit);
			// [审批失败]：审批总金额不能为0元！
			message = msg.getMessage("message.E2033", null, locale);
			addMessage(model, message);
			model.addAttribute("userCacheId", userCacheId);
			return "modules/allocation/v02/allocatedApproval/pbocAllocatedApprovalForm";
		}

		try {
			// 数据一致性检查
			pbocAllAllocateInfoService.checkVersion(pbocAllAllocateInfo);
			// 审批通过后，更改状态，并输出成功消息
			pbocAllAllocateInfoService.updateStatusByAllId(allocatedOrderSession, UserUtils.getUser(), targetStatus);
//			addMessage(model, message);

//			// 审批通过时指定库区
//			if (AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus)) {
//				// UPDATE-START  原因：此处考虑到金融机构批量修改状态时，可能操作不同人行机构流水，此方法参数修改为实体类，一个流水对应进行一次库区修改  update by SonyYuanYang  2018/03/15
//				String strRtn = this.bindingGoodsArea(allocatedOrderSession);
//				if (!AllocationConstant.SUCCESS.equals(strRtn)) {
//					addMessage(model, strRtn);
//				}
//				// UPDATE-END  原因：此处考虑到金融机构批量修改状态时，可能操作不同人行机构流水，此方法参数修改为实体类，一个流水对应进行一次库区修改  update by SonyYuanYang  2018/03/15
//			}

		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allocatedOrderSession.getRoffice().getName());
		paramsList.add(allocatedOrderSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allocatedOrderSession.getBusinessType(),
				allocatedOrderSession.getStatus(), paramsList, allocatedOrderSession.getAoffice().getId(), UserUtils.getUser());
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
		return "redirect:" + adminPath + "/allocation/v02/pbocAllocatedOrder/list?bInitFlag=true&pageType="
				+ pbocAllAllocateInfo.getPageType();
	}

	/**
	 * 接收确认
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            调拨信息
	 * @param targetStatus
	 *            目标状态
	 * @param model
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "/confirmAccept")
	public String confirmAccept(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "targetStatus", required = true) String targetStatus, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		// 更新箱包位置
		List<String> rfidList = Lists.newArrayList();
		String originalBanknoteClassification = Global.getConfig("store.originalBanknote.goodsClassification");
		for (PbocAllAllocateDetail allocateDetail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
			// 根据出库流水单号和RFID，取得出库时rfid所在库区信息
			StoGoodsLocationInfo condition = new StoGoodsLocationInfo();
			condition.setOutStoreAllId(allocateDetail.getAllId());
			condition.setRfid(allocateDetail.getRfid());
			StoGoodsLocationInfo rfidInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(condition);
			// 根据RFID所绑定的物品ID 过滤出原封新券
			StoGoodSelect goodsInfo = StoreCommonUtils.splitGood(rfidInfo.getGoodsId());
			// 如果物品是原封新券，则从rfid列表中移除原封箱号
			if (goodsInfo.getClassification().equals(originalBanknoteClassification)) {
				continue;
			}

			rfidList.add(allocateDetail.getRfid());
		}
		User user = UserUtils.getUser();

		StoreCommonUtils.updateRfidStatus(rfidList, Constant.BoxStatus.BUSSNESS_BANK, user.getId(), user.getName(),
				StoreConstant.RfidUseFlag.businessBankAccept, user.getOffice(), pbocAllAllocateInfo.getBusinessType());
		// 记录rfid变更历史
		stoRfidDenominationService.insertInToHistory(rfidList, user.getOffice());

		// 更新业务流水状态
		pbocAllAllocateInfoService.updateStatusByAllId(pbocAllAllocateInfo, user, targetStatus);
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(pbocAllAllocateInfo.getRoffice().getName());
		paramsList.add(pbocAllAllocateInfo.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(pbocAllAllocateInfo.getBusinessType(),
				pbocAllAllocateInfo.getStatus(), paramsList, pbocAllAllocateInfo.getRoffice().getId(), UserUtils.getUser());
		PbocAllAllocateInfo tempAllAllocateInfo = new PbocAllAllocateInfo();
		tempAllAllocateInfo.setPageType(pbocAllAllocateInfo.getPageType());
		return list(tempAllAllocateInfo, false, request, response, model);
	}

	/**
	 * 根据流水单号绑定物品与库区位置信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月2日
	 * 
	 * 
	 * @param allIdList
	 *            流水单号列表
	 * @return 成功返回success，失败返回错误消息
	 */
	private String bindingGoodsArea(PbocAllAllocateInfo pbocAllAllocateInfo) {
		String message = AllocationConstant.SUCCESS;
		List<PbocAllAllocateItem> quotaItemList = Lists.newArrayList();
		Locale locale = LocaleContextHolder.getLocale();
		// UPDATE-START  原因：此处考虑到金融机构批量修改状态时，可能操作不同人行机构流水，此方法参数修改为实体类，一个流水对应进行一次库区修改  update by SonyYuanYang  2018/03/15
		// for (String allId : allIdList) {
		List<PbocAllAllocateItem> temQuotaItemList = pbocAllAllocateInfoService.getQuotaItemListByAllId(pbocAllAllocateInfo.getAllId());
		// TODO
		// pbocAllAllocateInfoService.deleteGoodsAreaDetailByAllId(allId);
		quotaItemList.addAll(temQuotaItemList);
		// }
		// UPDATE-END  原因：此处考虑到金融机构批量修改状态时，可能操作不同人行机构流水，此方法参数修改为实体类，一个流水对应进行一次库区修改  update by SonyYuanYang  2018/03/15
		Map<String, ChangeStoreEntity> entiryMap = Maps.newHashMap();
		ChangeStoreEntity entity = null;

		// 判断库存
		for (PbocAllAllocateItem item : quotaItemList) {
			// UPDATE-START  原因：参数由当前机构修改为接收机构  update by SonyYuanYang  2018/03/15
			PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
					pbocAllAllocateInfo.getAoffice().getId());
			// UPDATE-END  原因：参数由当前机构修改为接收机构  update by SonyYuanYang  2018/03/15
			if (storeInfo == null) {
				String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
				strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
				// [审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
				message = msg.getMessage("message.E2037", new String[] { item.getGoodsId(), strGoodsName }, locale);
				return message;
			}
			String strGoodsName = StringUtils.isBlank(storeInfo.getGoodsName()) ? "" : storeInfo.getGoodsName();

			if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L
					|| item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
				// [审批失败]：物品[{0}]库存不足！
				message = msg.getMessage("message.E2038", new String[] { strGoodsName }, locale);
				return message;
			}
			// 按物品统计数量
			if (!entiryMap.containsKey(item.getGoodsId())) {
				entity = new ChangeStoreEntity();
				entity.setGoodsId(item.getGoodsId());
				entity.setNum(item.getMoneyNumber());
				entiryMap.put(item.getGoodsId(), entity);
			} else {
				entity = entiryMap.get(item.getGoodsId());
				entity.setNum(entity.getNum() + item.getMoneyNumber());
			}

		}

		if (quotaItemList.size() > 0) {
			// 取得绑定调拨物品库区信息列表
			String strDaysInterval = Global.getConfig("store.area.getgoods.days.interval");
			int iDaysInterval = StringUtils.isNotBlank(strDaysInterval) ? Integer.parseInt(strDaysInterval) : 0;
			String errorMessageCode = null;
			String errorGoodsId = null;
			String errorAllId = null;
			// UPDATE-START  原因：最后一个参数由当前机构修改为接收机构  update by SonyYuanYang  2018/03/15
			List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = StoreCommonUtils.getBindingAreaInfoToDetail(
					quotaItemList, iDaysInterval, errorMessageCode, errorGoodsId, errorAllId,
					pbocAllAllocateInfo.getAoffice().getId());
			// UPDATE-END  原因：最后一个参数由当前机构修改为接收机构  update by SonyYuanYang  2018/03/15
			if (goodsAreaDetailList == null) {
				message = msg.getMessage(errorMessageCode,
						new String[] { errorAllId, StoreCommonUtils.getPbocGoodsNameByGoodId(errorGoodsId) }, locale);
				return message;
			}

			pbocAllAllocateInfoService.insertToGoodsAreaDetail(goodsAreaDetailList);

			// 减掉预剩库存
			List<ChangeStoreEntity> entiryList = Lists.newArrayList();
			Iterator<String> keyIterator = entiryMap.keySet().iterator();
			while (keyIterator.hasNext()) {
				entity = entiryMap.get(keyIterator.next());
				entity.setNum(-entity.getNum());
				entiryList.add(entity);
			}
			StoreCommonUtils.changePbocSurplusStore(entiryList, pbocAllAllocateInfo.getAoffice().getId());
		}
		return message;
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
		Double originalCouponAllAmount = 0d;
		// 总计金额
		Double allAmount = 0d;

		for (String allId : allIdList) {
			PbocApprovalPrintDetail printDetail = pbocAllAllocateInfoService.getApprovalPrintDetail(allId);
			fullCouponAllAmount = fullCouponAllAmount + printDetail.getFullCouponAmount();
			originalCouponAllAmount = originalCouponAllAmount + printDetail.getOriginalCouponAmount();
			allAmount = allAmount + printDetail.getConfirmAmount();
			printDataList.add(printDetail);
		}
		model.addAttribute("printDataList", printDataList);

		model.addAttribute("fullCouponAllAmount", fullCouponAllAmount);
		model.addAttribute("originalCouponAllAmount", originalCouponAllAmount);
		model.addAttribute("allAmount", allAmount);
		// 打印审批明细
		return "modules/allocation/v02/allocatedApproval/printAllocateApprovalDetail";
	}

	/**
	 * 打印调款审批明细
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
}