package com.coffer.businesses.modules.allocation.v02.web;

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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 人行调拨入库Controllor
 * 
 * @author zhengkaiyuan
 * @version 2016年7月11日
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocHorizontalAllocatedInStore")
public class PbocHorizontalAllocatedInStoreController extends BaseController {
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
	 * 跳转至列表界面
	 * 
	 * @author zhengkaiyuan
	 * @version 2016年7月7日
	 *
	 *
	 * @param pbocAllAllocateInfo
	 *            查询条件
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表界面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(PbocAllAllocateInfo pbocAllAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		// UPDATE-START  原因：金融平台机构用户查看时不加限制条件  update by SonyYuanYang  2018/03/20
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM
						.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
			// UPDATE-END  原因：金融平台机构用户查看时不加限制条件  update by SonyYuanYang  2018/03/20
			// 查询接收机构是当前人民银行的数据
			pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
			// 管库员 查看时 只显示 待入库和待交接任务
			if ((AllocationConstant.SysUserType.CENTRAL_STORE_MANAGER_OPT.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT
							.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_ALLOCATE_OPT.equals(UserUtils.getUser().getUserType()))
					&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
			// 人行管理员 查看时 显示 待入库 待交接 完成
			if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
		}
		// 查询条件：预约 开始时间
		if (pbocAllAllocateInfo.getApplyDate() != null) {
			pbocAllAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getApplyDate())));
			pbocAllAllocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getApplyDate())));
		}
		// 查询条件：入库开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setScanGateDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：入库结束时间
		if (pbocAllAllocateInfo.getCreateTimeEnd() != null) {
			pbocAllAllocateInfo.setScanGateDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getCreateTimeEnd())));
		}
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE);
		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService
				.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		return "modules/allocation/v02/horizontalAllocatedInStore/pbocHorizontalAllocatedInStoreList";
	}

	/**
	 * 调拨入库点击详细
	 * 
	 * @author zhengkaiyuan
	 * @version 2016年7月7日
	 *
	 *
	 * @param pbocAllAllocateInfo
	 *            输入参数
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "form")
	public String form(PbocAllAllocateInfo pbocAllAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 生成session的key值
		String userCacheId = UserUtils.createUserCacheId();
		if (StringUtils.isBlank(pbocAllAllocateInfo.getAllId())) {
			// 设定业务类型：销毁出库
			pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE);
			// 初始化用款日期为明日
			pbocAllAllocateInfo.setApplyDate(DateUtils.addDate(new Date(), 1));
		} else {
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

			if (pbocAllAllocateInfo.getInstoreAmount() != null) {
				pbocAllAllocateInfo.setInstoreAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getInstoreAmount()));
			}

			if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
				String currentOfficeId = UserUtils.getUser().getOffice().getId();
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
		}

		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		if (StringUtils.isBlank(pbocAllAllocateInfo.getAllId())) {
			// 缓存用户数据
			UserUtils.putCache(userCacheId, pbocAllAllocateInfo);

			model.addAttribute("userCacheId", userCacheId);
			return "modules/allocation/v02/horizontalAllocatedInStore/pbocInStoreApprovalRegisteForm";
		}
		// add-start 交接人员照片显示 add by yanbingxu 2018/04/03
		AllocationCommonUtils.pbocHandoverFilter(pbocAllAllocateInfo, model);
		// add-end
		return "modules/allocation/v02/goodsAllocatedCommon/pbocShowCommonDetail";
	}

	/**
	 * 
	 * Title: add
	 * <p>
	 * Description: 添加物品
	 * </p>
	 * 
	 * @author: Wangdong
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

		PbocAllAllocateInfo horizontalAllocatedInStoreSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		for (PbocAllAllocateItem item : horizontalAllocatedInStoreSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(strGoodsId)) {
				isExist = true;
				break;
			}
			iIndex++;

		}
		if (isExist == true) {
			// // 累加计算
			// PbocAllAllocateItem item =
			// destroyRegisteOrderSession.getPbocAllAllocateItemList().get(iIndex);
			// item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber()
			// + item.getMoneyNumber());
			// BigDecimal goodsValue =
			// StoreCommonUtils.getGoodsValue(strGoodsId);
			// item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) :
			// goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
			// [提示]：物品列表中已经存在物品【{0}】，如需修改物品数量，请点击物品名称进行修改。
			message = msg.getMessage("message.I2014",
					new String[] {
							horizontalAllocatedInStoreSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName() },
					locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId",
					horizontalAllocatedInStoreSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {
			PbocAllAllocateItem item = new PbocAllAllocateItem();

			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			item.setRegistType(AllocationConstant.RegistType.RegistPoint);
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
					: goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			horizontalAllocatedInStoreSession.getPbocAllAllocateItemList().add(item);
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(horizontalAllocatedInStoreSession);
		// 接收机构不为空时设置为所选机构，为空时设置为本机构
		if (pbocAllAllocateInfo.getAoffice() != null) {
			horizontalAllocatedInStoreSession.setAoffice(pbocAllAllocateInfo.getAoffice());
		} else {
			horizontalAllocatedInStoreSession.setAoffice(pbocAllAllocateInfo.getCurrentUser().getOffice());
		}
		horizontalAllocatedInStoreSession.setApplyDate(pbocAllAllocateInfo.getApplyDate());
		horizontalAllocatedInStoreSession.setCommondNumber(pbocAllAllocateInfo.getCommondNumber());
		horizontalAllocatedInStoreSession.setRoffice(pbocAllAllocateInfo.getRoffice());
		model.addAttribute("pbocAllAllocateInfo", horizontalAllocatedInStoreSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/horizontalAllocatedInStore/pbocInStoreApprovalRegisteForm";
	}

	/**
	 * 跳转至人行审批登记页面
	 * 
	 * @author Wangdong
	 * @version 2016年6月8日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            页面初始化信息
	 * @param model
	 * @return 人行审批登记页面
	 */
	@RequestMapping(value = "/toPbocOrderEditPage")
	public String toPbocOrderEditPage(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "bInitFlag", required = false) Boolean bInitFlag, Model model, String userCacheId) {

		if (bInitFlag == null || bInitFlag == true) {

			// 初始化用款日期为明日
			pbocAllAllocateInfo.setApplyDate(DateUtils.addDate(new Date(), 1));
		}
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/horizontalAllocatedInStore/pbocInStoreApprovalRegisteForm";
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
			Model model, HttpServletRequest request, HttpServletResponse response,PbocAllAllocateInfo pbocAllAllocateInfo) {

		PbocAllAllocateInfo horizontalAllocatedInStoreSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		horizontalAllocatedInStoreSession.setCommondNumber(pbocAllAllocateInfo.getCommondNumber());
		horizontalAllocatedInStoreSession.setAoffice(pbocAllAllocateInfo.getAoffice());
		horizontalAllocatedInStoreSession.setRoffice(pbocAllAllocateInfo.getRoffice());
		int iIndex = 0;
		for (PbocAllAllocateItem item : horizontalAllocatedInStoreSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)) {
				horizontalAllocatedInStoreSession.getPbocAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex++;
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(horizontalAllocatedInStoreSession);
		model.addAttribute("pbocAllAllocateInfo", horizontalAllocatedInStoreSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/horizontalAllocatedInStore/pbocInStoreApprovalRegisteForm";
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

		PbocAllAllocateInfo horizontalAllocatedInStoreSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();
		for (PbocAllAllocateItem item : horizontalAllocatedInStoreSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)) {
				updateGoodsItem = item;
				break;
			}
		}
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("updateGoodsItem", updateGoodsItem);
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
	public String updateGoodsItem(PbocAllAllocateItem updateGoodsItem,PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model) {

		PbocAllAllocateInfo horizontalAllocatedInStoreSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		horizontalAllocatedInStoreSession.setCommondNumber(pbocAllAllocateInfo.getCommondNumber());
		horizontalAllocatedInStoreSession.setAoffice(pbocAllAllocateInfo.getAoffice());
		horizontalAllocatedInStoreSession.setRoffice(pbocAllAllocateInfo.getRoffice());
		for (PbocAllAllocateItem item : horizontalAllocatedInStoreSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(horizontalAllocatedInStoreSession);
		model.addAttribute("pbocAllAllocateInfo", horizontalAllocatedInStoreSession);

		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/horizontalAllocatedInStore/pbocInStoreApprovalRegisteForm";
	}

	/**
	 * 保存人行审批登记信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 * 
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @return 审批登记页面
	 */
	@RequestMapping(value = "/savePbocOrder")
	public String savePbocOrder(@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model,
			SessionStatus status, HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo horizontalAllocatedInStoreSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		
		if (horizontalAllocatedInStoreSession.getRegisterAmount() == null
				|| horizontalAllocatedInStoreSession.getRegisterAmount() == 0d) {
			// [申请失败]：申请总金额不能为0元！
			message = msg.getMessage("message.E2034", null, locale);
			addMessage(model, message);
			return toPbocOrderEditPage(horizontalAllocatedInStoreSession, false, model, userCacheId);
		}
		
		if (horizontalAllocatedInStoreSession.getRoffice().getId().equals(horizontalAllocatedInStoreSession.getAoffice().getId())) {
			// message.E2069=[保存失败]：出库机构与入库机构不能为同一机构！
			message = msg.getMessage("message.E2069", null, locale);
			addMessage(model, message);
			return toPbocOrderEditPage(horizontalAllocatedInStoreSession, false, model, userCacheId);
		}

		if (UserUtils.getUser().getOffice().getId().equals(horizontalAllocatedInStoreSession.getRoffice().getId())) {
			// message.E2046=[保存失败]：出库机构不能指定为当前登录用户所在机构！
			message = msg.getMessage("message.E2046", null, locale);
			addMessage(model, message);
			return toPbocOrderEditPage(horizontalAllocatedInStoreSession, false, model, userCacheId);
		}
		
		horizontalAllocatedInStoreSession.setConfirmAmount(horizontalAllocatedInStoreSession.getRegisterAmount());
		// 作成调拨信息
		horizontalAllocatedInStoreSession.setLoginUser(UserUtils.getUser());

		try {
			pbocAllAllocateInfoService.savePbocApproveRegiste(horizontalAllocatedInStoreSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return toPbocOrderEditPage(horizontalAllocatedInStoreSession, false, model, userCacheId);
		}

		message = msg.getMessage("message.I2011", null, locale);
		addMessage(model, message);
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(horizontalAllocatedInStoreSession.getAoffice().getName());
		paramsList.add(horizontalAllocatedInStoreSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(horizontalAllocatedInStoreSession.getBusinessType(),
				horizontalAllocatedInStoreSession.getStatus(), paramsList, horizontalAllocatedInStoreSession.getCurrentUser().getOffice().getId(), UserUtils.getUser());
		PbocAllAllocateInfo tempAllAllocateInfo = new PbocAllAllocateInfo();
		tempAllAllocateInfo.setPageType(horizontalAllocatedInStoreSession.getPageType());
		return list(tempAllAllocateInfo, request, response, model);
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author zhengkaiyuan
	 * @version 2016年7月7日
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
		return list(templlAllocateInfo, request, response, model);
	}

	/**
	 * 删除 待入库信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年8月30日
	 * 
	 * 
	 * @param allId
	 *            待删除流水单号
	 * @param redirectAttributes
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "delete")
	public String delete(PbocAllAllocateInfo pbocAllAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();

		String message = "";

		// 调拨入库状态 为待入库时可以删除
		if (!AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS
				.equals(pbocAllAllocateInfo.getStatus())) {
			// message.E2020=[删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
			message = msg.getMessage("message.E2020",
					new String[] { pbocAllAllocateInfo.getAllId(),
							DictUtils.getDictLabel(pbocAllAllocateInfo.getStatus(), "pboc_order_handin_status", "") },
					locale);
		} else {
			try {
				// pbocAllAllocateInfoService.delete(pbocAllAllocateInfo);
				pbocAllAllocateInfo.setAllocateOutAllIds(pbocAllAllocateInfo.getAllId());
				pbocAllAllocateInfoService.deleteAllocateIn(pbocAllAllocateInfo);

				// message.I2002=[成功]：流水单号{0}删除成功！
				message = msg.getMessage("message.I2002", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);

			}
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v02/pbocHorizontalAllocatedInStore/list?repage";
	}

	/**
	 * 按调拨命令号查询调拨出库业务流水
	 * 
	 * @author WangBaozhong
	 * @version 2017年4月20日
	 * 
	 * 
	 * @param commondNumber
	 *            调拨命令号
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return 调拨入库登记页面
	 */
	@RequestMapping(value = "searchByCommandId")
	public String searchByCommandId(String commondNumber,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo horizontalAllocatedInStoreSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";

		List<PbocAllAllocateInfo> allocateOutList = pbocAllAllocateInfoService.searchByCommandId(commondNumber);

		if (Collections3.isEmpty(allocateOutList)) {
			// message.I2019=[提示]：调拨命令号【{0}】没有对应调拨出库业务！
			message = msg.getMessage("message.I2019", new String[] { commondNumber }, locale);
			addMessage(model, message);
		} else {
			List<PbocAllAllocateDetail> pbocAllAllocateDetailList = Lists.newArrayList();
			List<PbocAllAllocateItem> pbocAllAllocateItemList = Lists.newArrayList();
			Office aOffice = null;
			Office rOffice = null;
			List<String> notFinishAllIdList = Lists.newArrayList();
			for (PbocAllAllocateInfo allocateInfo : allocateOutList) {
				if (rOffice == null) {
					rOffice = allocateInfo.getRoffice();
				}
				if (aOffice == null) {
					aOffice = allocateInfo.getAoffice();
				}

				if (!AllocationConstant.PbocOrderStatus.FINISH_STATUS.equals(allocateInfo.getStatus())) {

					notFinishAllIdList.add(allocateInfo.getAllId());
				} else {
					// 将调拨出库审批物品，作为调拨入库登记物品明细
					for (PbocAllAllocateItem item : allocateInfo.getPbocAllAllocateItemList()) {
						if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
							StoGoodSelect goodsSelect = StoreCommonUtils.splitGood(item.getGoodsId());
							if (StoreConstant.GoodsClassification.ORIGINAL_COUPON
									.equals(goodsSelect.getClassification())) {
								continue;
							}
							item.setRegistType(AllocationConstant.RegistType.RegistPoint);
							BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(item.getGoodsId());
							item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
									: goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
							item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
							pbocAllAllocateItemList.add(item);
						}
					}

					// 查询出入库物品所处库区位置
					for (PbocAllAllocateDetail allcateDetail : allocateInfo.getPbocAllAllocateDetailList()) {
						StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
						stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());

						stoGoodsLocationInfo.setOutStoreAllId(allocateInfo.getAllId());
						// 获取原封箱信息
						StoOriginalBanknote originalBankNote = StoreCommonUtils.getStoOriginalBanknoteByBoxId(
								allcateDetail.getRfid(), allocateInfo.getRoffice().getId());
						// 原封新券不做为调拨入库物品
						if (originalBankNote != null) {
							continue;
						} else {
							// 原封券以外，则截取rfid前八位
							allcateDetail.setRfid(StringUtils.left(allcateDetail.getRfid(), 8));
						}

						stoGoodsLocationInfo = StoreCommonUtils
								.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
						allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
						pbocAllAllocateDetailList.add(allcateDetail);
					}
					// 记录查询出结果的 流水单号
					horizontalAllocatedInStoreSession.getAllocatefinishAllIdList().add(allocateInfo.getAllId());
				}
			}
			if (!Collections3.isEmpty(notFinishAllIdList)) {
				// message.I2020=[提示]：调拨出库业务【{0}】尚未出库完成！
				message = msg.getMessage("message.I2020", new String[] { notFinishAllIdList.toString() }, locale);
				addMessage(model, message);
			}

			horizontalAllocatedInStoreSession.setPbocAllAllocateDetailList(pbocAllAllocateDetailList);
			horizontalAllocatedInStoreSession.setPbocAllAllocateItemList(pbocAllAllocateItemList);
			horizontalAllocatedInStoreSession.setRoffice(rOffice);
			horizontalAllocatedInStoreSession.setAoffice(aOffice);
		}

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(horizontalAllocatedInStoreSession);
		horizontalAllocatedInStoreSession.setCommondNumber(commondNumber);
		model.addAttribute("pbocAllAllocateInfo", horizontalAllocatedInStoreSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/horizontalAllocatedInStore/pbocInStoreApprovalRegisteForm";
	}
}
