package com.coffer.businesses.modules.allocation.v02.web;

import java.math.BigDecimal;
import java.util.Arrays;
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

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
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
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;

/**
 * 人行配款Controller
 * 
 * @author wangbaozhong
 * @version 2016-07-01
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocAllocatedQuota")
public class PbocAllocatedQuotaController extends BaseController {
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
			// 查询当前机构
			pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
			// 如果是网点，设置用户的机构号，只能查询当前机构
			if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_STORE_MANAGER_OPT
							.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT
							.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_ALLOCATE_OPT.equals(UserUtils.getUser().getUserType())) {
				// 不显示驳回数据
				pbocAllAllocateInfo.setShowRejectData(AllocationConstant.ShowRejectData.SHOW_NONE);
			} else {
				// 显示驳回数据
				pbocAllAllocateInfo.setShowRejectData(AllocationConstant.ShowRejectData.SHOW_OUT);
			}

			// 管库员 查看时 只显示 待配款 待出库 待交接
			if ((AllocationConstant.SysUserType.CENTRAL_STORE_MANAGER_OPT.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT
							.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_ALLOCATE_OPT.equals(UserUtils.getUser().getUserType()))
					&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
			// 人行管理员 查看时 显示 待配款 待出库 待交接 待接受 完成
			if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_ACCEPT_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
		}

		// 查询条件：出库 开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setScanGateDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：出库 结束时间
		if (pbocAllAllocateInfo.getCreateTimeEnd() != null) {
			pbocAllAllocateInfo.setScanGateDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getCreateTimeEnd())));
		}
		// 人行管理员显示全部信息
		// UPDATE-START  原因：金融平台用户登录时不设置待配款状态  update by SonyYuanYang  2018/03/20
		if (!AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType()) && !Constant.OfficeType.DIGITAL_PLATFORM
				.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
			// UPDATE-END  原因：金融平台用户登录时不设置待配款状态  update by SonyYuanYang  2018/03/20
			// 初始状态设定
			if (bInitFlag != null && bInitFlag == true) {
				// 人行下拨配款列表
				// 初始页面显待配款
				pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
			}
		}
		/* 业务类型设定 */
		// 初始列表页面显示 业务类型为 申请下拨的数据
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION);

		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService
				.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		if (AllocationConstant.PageType.StoreQuotaList.equals(pbocAllAllocateInfo.getPageType())) {
			return "modules/allocation/v02/allocatedQuota/pbocAllocatedQuotaList";
		}
		return "modules/allocation/v02/allocatedQuota/pbocAllocatedQuotaList";
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

		// 查看时设置申请总金额和审批总金额大写
		pbocAllAllocateInfo.setRegisterAmount(
				pbocAllAllocateInfo.getRegisterAmount() == null ? 0d : pbocAllAllocateInfo.getRegisterAmount());
		pbocAllAllocateInfo.setConfirmAmount(
				pbocAllAllocateInfo.getConfirmAmount() == null ? 0d : pbocAllAllocateInfo.getConfirmAmount());
		pbocAllAllocateInfo.setRegisterAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getRegisterAmount()));
		pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));

		if (pbocAllAllocateInfo.getOutstoreAmount() != null) {
			pbocAllAllocateInfo.setOutstoreAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getOutstoreAmount()));
		}

		String currentOfficeId = UserUtils.getUser().getOffice().getId();
		if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
			// 查询出入库物品所处库区位置
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
		// add-start 交接人员照片显示 add by yanbingxu 2018/04/03
		AllocationCommonUtils.pbocHandoverFilter(pbocAllAllocateInfo, model);
		// add-end
		model.addAttribute("printDataList", printDataList);

		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		return "modules/allocation/v02/goodsAllocatedCommon/pbocShowCommonDetail";
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

		String[] allIdArray = allIds.split(Constant.Punctuation.COMMA);
		List<String> allIdList = Arrays.asList(allIdArray);

		try {
			// 更改状态
			pbocAllAllocateInfoService.updateStatusByAllIds(allIdList, UserUtils.getUser(), targetStatus, null);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
			pbocAllAllocateInfo.setPageType(pageType);
			return list(pbocAllAllocateInfo, true, request, response, model);
		}

		// 配款完成后打印取包明细
		return this.printQuotaGoodsAreaInfo(allIdList, model);
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
	public String back(PbocAllAllocateInfo pbocAllAllocateInfo, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		PbocAllAllocateInfo templlAllocateInfo = new PbocAllAllocateInfo();
		templlAllocateInfo.setPageType(pbocAllAllocateInfo.getPageType());
		return list(templlAllocateInfo, true, request, response, model);
	}

	/**
	 * 
	 * Title: toEditQuotaDataPage
	 * <p>
	 * Description: 修改配款物品页面跳转
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "toEditQuotaDataPage")
	public String toEditQuotaDataPage(PbocAllAllocateInfo pbocAllAllocateInfo, Model model) {
		for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
		}

		// //初始化审批物品为申请物品
		// List<PbocAllAllocateItem> approveItemList = Lists.newArrayList();
		// for (PbocAllAllocateItem item :
		// pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
		// PbocAllAllocateItem approveItem = new PbocAllAllocateItem();
		// approveItem.setGoodsId(item.getGoodsId());
		// approveItem.setRegistType(AllocationConstant.RegistType.RegistStore);
		// approveItem.setGoodsName(item.getGoodsName());
		// approveItem.setMoneyNumber(item.getMoneyNumber());
		// approveItem.setMoneyAmount(item.getMoneyAmount());
		// approveItemList.add(approveItem);
		// }
		// pbocAllAllocateInfo.getPbocAllAllocateItemList().addAll(approveItemList);

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(pbocAllAllocateInfo);
		// 缓存用户数据
		UserUtils.putCache(pbocAllAllocateInfo.getAllId(), pbocAllAllocateInfo);
		model.addAttribute("userCacheId", pbocAllAllocateInfo.getAllId());
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		return "modules/allocation/v02/allocatedQuota/pbocAllocatedUpdateForm";
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
		PbocAllAllocateInfo allocatedQuotaSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		for (PbocAllAllocateItem item : allocatedQuotaSession.getPbocAllAllocateItemList()) {
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
			// allocatedOrderSession.getPbocAllAllocateItemList().get(iIndex);
			// item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber()
			// + item.getMoneyNumber());
			// BigDecimal goodsValue =
			// StoreCommonUtils.getGoodsValue(strGoodsId);
			// item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) :
			// goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
			message = msg.getMessage("message.I2014",
					new String[] { allocatedQuotaSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName() },
					locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId",
					allocatedQuotaSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {
			PbocAllAllocateItem item = new PbocAllAllocateItem();
			item.setRegistType(AllocationConstant.RegistType.RegistStore);
			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
					: goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			allocatedQuotaSession.getPbocAllAllocateItemList().add(item);
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(allocatedQuotaSession);
		model.addAttribute("pbocAllAllocateInfo", allocatedQuotaSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/allocatedQuota/pbocAllocatedUpdateForm";
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

		PbocAllAllocateInfo allocatedQuotaSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		int iIndex = 0;
		for (PbocAllAllocateItem item : allocatedQuotaSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
				allocatedQuotaSession.getPbocAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex++;
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(allocatedQuotaSession);
		model.addAttribute("pbocAllAllocateInfo", allocatedQuotaSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/allocatedQuota/pbocAllocatedUpdateForm";
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

		PbocAllAllocateInfo allocatedQuotaSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();

		for (PbocAllAllocateItem item : allocatedQuotaSession.getPbocAllAllocateItemList()) {
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

		PbocAllAllocateInfo allocatedQuotaSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		for (PbocAllAllocateItem item : allocatedQuotaSession.getPbocAllAllocateItemList()) {
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
		pbocAllAllocateInfoService.computeGoodsAmount(allocatedQuotaSession);
		model.addAttribute("pbocAllAllocateInfo", allocatedQuotaSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/allocatedQuota/pbocAllocatedUpdateForm";
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
		PbocAllAllocateInfo allocatedQuotaSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
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

		// 如果没填写审批物品，提示错误信息
		if (allocatedQuotaSession.getConfirmAmount() == null || allocatedQuotaSession.getConfirmAmount() == 0d) {
			// [审批失败]：审批总金额不能为0元！
			message = msg.getMessage("message.E2033", null, locale);
			addMessage(model, message);
			model.addAttribute("userCacheId", userCacheId);
			return "modules/allocation/v02/allocatedQuota/pbocAllocatedUpdateForm";
		}

		try {

			// 数据一致性检查
			pbocAllAllocateInfoService.checkVersion(pbocAllAllocateInfo);

			allocatedQuotaSession.setApprovalBy(UserUtils.getUser());

			// 审批通过后，更改状态，并输出成功消息
			pbocAllAllocateInfoService.reApprovalByAllId(allocatedQuotaSession, UserUtils.getUser(),
					AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
			addMessage(model, message);

		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
		}

		PbocAllAllocateInfo templlAllocateInfo = new PbocAllAllocateInfo();
		return list(templlAllocateInfo, true, request, response, model);
	}

	/**
	 * 
	 * Title: deleteAllocateApprovalInfo
	 * <p>
	 * Description: 删除待配款信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 * @param redirectAttributes
	 * @param request
	 * @param response
	 * @return String 返回类型
	 */
	@RequestMapping(value = "/deleteAllocateApprovalInfo")
	public String deleteAllocateApprovalInfo(PbocAllAllocateInfo pbocAllAllocateInfo,
			RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (!AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(pbocAllAllocateInfo.getStatus())
				&& !AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS
						.equals(pbocAllAllocateInfo.getStatus())) {
			// message.E2020=[删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
			message = msg
					.getMessage("message.E2020",
							new String[] { pbocAllAllocateInfo.getAllId(),
									DictUtils.getDictLabel(pbocAllAllocateInfo.getStatus(), "all_status", "") },
							locale);
		} else {
			try {
				pbocAllAllocateInfoService.deleteQuotaInfo(pbocAllAllocateInfo);
				// message.I2002=[成功]：流水单号{0}删除成功！
				message = msg.getMessage("message.I2002", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			}
		}

		addMessage(redirectAttributes, message);

		return "redirect:" + adminPath + "/allocation/v02/pbocAllocatedQuota/list?repage";
	}

	/**
	 * 打印配款物品和对应库区位置信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 * 
	 * @return 打印页面
	 */
	private String printQuotaGoodsAreaInfo(List<String> allIdList, Model model) {
		String currentOfficeId = UserUtils.getUser().getOffice().getId();

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

		return "modules/allocation/v02/allocatedQuota/printQuotaDetail";
	}

}