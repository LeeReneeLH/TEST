package com.coffer.businesses.modules.allocation.v02.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
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

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 销毁出库主表管理Controller
 * 
 * @author wangdong
 * @version 2016-07-07
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocDestroyOutStore")
public class PbocDestroyOutStoreController extends BaseController {
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
	 * @author WangDong
	 * @version 2016年7月7日
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
			// 查询本金库
			pbocAllAllocateInfo.setRoffice(UserUtils.getUser().getOffice());

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
			// 人行管理员 查看时 只显示 待配款 待出库 待交接 完成
			if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS);
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
		// UPDATE-START  原因：金融平台机构用户查看时不设置只查询待配款业务  update by SonyYuanYang  2018/03/20
		if (!AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType()) && !Constant.OfficeType.DIGITAL_PLATFORM
				.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
			// UPDATE-END  原因：金融平台机构用户查看时不设置只查询待配款业务  update by SonyYuanYang  2018/03/20
			// 初始状态设定
			if (bInitFlag != null && bInitFlag == true) {
				// 销毁出库审批列表
				if (AllocationConstant.PageType.DestroyApprovalList.equals(pbocAllAllocateInfo.getPageType())) {
					// 初始页面显示待配款
					pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
				}
			}
		}
		// 初始列表页面显示 业务类型为 销毁出库的数据
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE);

		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService
				.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		return "modules/allocation/v02/destroyOutStore/pbocDestroyOutStoreList";
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
			// 设定业务类型：销毁出库
			pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE);
			// 初始化用款日期为明日
			pbocAllAllocateInfo.setApplyDate(DateUtils.addDate(new Date(), 1));
			// UPDATE-START  原因：人行登录时初始化父机构，金融平台登录时不初始化  update by SonyYuanYang  2018/03/20
			if (Constant.OfficeType.CENTRAL_BANK.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
				// 人行登录时初始化父机构为销毁机构
				pbocAllAllocateInfo
						.setAoffice(SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getParentId()));
			}
			// UPDATE-END  原因：人行登录时初始化父机构，金融平台登录时不初始化  update by SonyYuanYang  2018/03/20
		} else {
			for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
				item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
			}

			// 重新计算物品总价值
			pbocAllAllocateInfoService.computeGoodsAmount(pbocAllAllocateInfo);
		}
		// 缓存用户数据
		UserUtils.putCache(userCacheId, pbocAllAllocateInfo);

		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		if (AllocationConstant.PageType.DestroyApprovalView.equals(pbocAllAllocateInfo.getPageType())) {
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
			List<String> allIdList = Lists.newArrayList();
			allIdList.add(pbocAllAllocateInfo.getAllId());
			List<PbocAllAllocateInfo> printDataList = pbocAllAllocateInfoService.getQuotaGoodsAreaInfo(allIdList);
			for (PbocAllAllocateInfo allocateInfo : printDataList) {
				for (PbocAllAllocateItem item : allocateInfo.getPbocAllAllocateItemList()) {
					for (AllAllocateGoodsAreaDetail areaDetail : item.getGoodsAreaDetailList()) {
						// 原封券以外，则截取rfid前八位
						areaDetail.getGoodsLocationInfo()
								.setRfid(StringUtils.left(areaDetail.getGoodsLocationInfo().getRfid(), 8));
					}
				}
			}
			// add-start 交接人员照片显示 add by yanbingxu 2018/04/03
			AllocationCommonUtils.pbocHandoverFilter(pbocAllAllocateInfo, model);
			// add-end
			model.addAttribute("printDataList", printDataList);
			return "modules/allocation/v02/goodsAllocatedCommon/pbocShowCommonDetail";
		}
		// 编辑页面
		return "modules/allocation/v02/destroyOutStore/pbocDestroyRegisterForm";
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
		return "modules/allocation/v02/destroyOutStore/pbocDestroyRegisterForm";
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

		PbocAllAllocateInfo destroyRegisteOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		for (PbocAllAllocateItem item : destroyRegisteOrderSession.getPbocAllAllocateItemList()) {
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
					new String[] { destroyRegisteOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName() },
					locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId",
					destroyRegisteOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {

			PbocAllAllocateItem item = new PbocAllAllocateItem();

			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			item.setRegistType(AllocationConstant.RegistType.RegistPoint);
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
					: goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			destroyRegisteOrderSession.getPbocAllAllocateItemList().add(item);
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(destroyRegisteOrderSession);
		// 设置销毁机构
		Office office = SysCommonUtils.findOfficeById(pbocAllAllocateInfo.getAoffice().getId());
		destroyRegisteOrderSession.setAoffice(office);
		// 金融平台时设置登记机构
		if (pbocAllAllocateInfo.getRoffice() != null) {
			destroyRegisteOrderSession.setRoffice(pbocAllAllocateInfo.getRoffice());
		} else {
			destroyRegisteOrderSession.setRoffice(pbocAllAllocateInfo.getCurrentUser().getOffice());
		}
		destroyRegisteOrderSession.setApplyDate(pbocAllAllocateInfo.getApplyDate());
		destroyRegisteOrderSession.setCommondNumber(pbocAllAllocateInfo.getCommondNumber());
		model.addAttribute("pbocAllAllocateInfo", destroyRegisteOrderSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/destroyOutStore/pbocDestroyRegisterForm";
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

		PbocAllAllocateInfo destroyRegisteOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		destroyRegisteOrderSession.setAoffice(pbocAllAllocateInfo.getAoffice());
		destroyRegisteOrderSession.setRoffice(pbocAllAllocateInfo.getRoffice());
		destroyRegisteOrderSession.setCommondNumber(pbocAllAllocateInfo.getCommondNumber());
		int iIndex = 0;
		for (PbocAllAllocateItem item : destroyRegisteOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)) {
				destroyRegisteOrderSession.getPbocAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex++;
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(destroyRegisteOrderSession);
		model.addAttribute("pbocAllAllocateInfo", destroyRegisteOrderSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/destroyOutStore/pbocDestroyRegisterForm";
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

		PbocAllAllocateInfo destroyRegisteOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();
		for (PbocAllAllocateItem item : destroyRegisteOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)) {
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
	public String updateGoodsItem(PbocAllAllocateItem updateGoodsItem, PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model) {

		PbocAllAllocateInfo destroyRegisteOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		destroyRegisteOrderSession.setAoffice(pbocAllAllocateInfo.getAoffice());
		destroyRegisteOrderSession.setRoffice(pbocAllAllocateInfo.getRoffice());
		destroyRegisteOrderSession.setCommondNumber(pbocAllAllocateInfo.getCommondNumber());
		for (PbocAllAllocateItem item : destroyRegisteOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(destroyRegisteOrderSession);
		model.addAttribute("pbocAllAllocateInfo", destroyRegisteOrderSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/destroyOutStore/pbocDestroyRegisterForm";
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
		// 取得数据
		PbocAllAllocateInfo destroyRegisteOrderSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		// 解析用户区域
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (destroyRegisteOrderSession.getRegisterAmount() == null
				|| destroyRegisteOrderSession.getRegisterAmount() == 0d) {
			// [申请失败]：申请总金额不能为0元！
			message = msg.getMessage("message.E2034", null, locale);
			addMessage(model, message);
			return toPbocOrderEditPage(destroyRegisteOrderSession, false, model, userCacheId);
		}
		// 设置确认金额
		destroyRegisteOrderSession.setConfirmAmount(destroyRegisteOrderSession.getRegisterAmount());
		// 作成调拨信息
		destroyRegisteOrderSession.setLoginUser(UserUtils.getUser());
		try {
			// 取得item数据
			List<PbocAllAllocateItem> itemList = destroyRegisteOrderSession.getPbocAllAllocateItemList();
			for (PbocAllAllocateItem item : itemList) {
				// 业务类型为 申请下拨，调拨出库，销毁出库 业务验证物品库存是否充足
				if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION
						.equals(destroyRegisteOrderSession.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE
								.equals(destroyRegisteOrderSession.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
								.equals(destroyRegisteOrderSession.getBusinessType())) {
					// UPDATE-START  原因：最后一个参数由当前登录机构设置为登记机构  update by SonyYuanYang  2018/03/15
					// 判断物品库存是否充足
					PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
							destroyRegisteOrderSession.getRoffice().getId());
					// UPDATE-END  原因：最后一个参数由当前登录机构设置为登记机构  update by SonyYuanYang  2018/03/15
					if (storeInfo == null) {
						String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
						strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
						// [审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
						message = msg.getMessage("message.E2037", new String[] { item.getGoodsId(), strGoodsName },
								locale);
						addMessage(model, message);
						return toPbocOrderEditPage(destroyRegisteOrderSession, false, model, userCacheId);
					}
					String strGoodsName = StringUtils.isBlank(storeInfo.getGoodsName()) ? "" : storeInfo.getGoodsName();

					if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L
							|| item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
						// [审批失败]：物品[{0}]库存不足！
						message = msg.getMessage("message.E2038", new String[] { strGoodsName }, locale);
						addMessage(model, message);
						return toPbocOrderEditPage(destroyRegisteOrderSession, false, model, userCacheId);
					}
					// UPDATE-START  原因：最后一个参数由当前登录机构设置为登记机构  update by SonyYuanYang  2018/03/15
					// 判断库区物品是否充足
					long lGoodsNum = StoreCommonUtils.getGoodsNumInStoreAreaByGoodsId(item.getGoodsId(),
							destroyRegisteOrderSession.getRoffice().getId());
					// UPDATE-END  原因：最后一个参数由当前登录机构设置为登记机构  update by SonyYuanYang  2018/03/15
					if (item.getMoneyNumber() > lGoodsNum) {
						// [审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
						message = msg.getMessage("message.E2037", new String[] { item.getGoodsId(), strGoodsName },
								locale);
						addMessage(model, message);
						return toPbocOrderEditPage(destroyRegisteOrderSession, false, model, userCacheId);
					}
				}

			}
			pbocAllAllocateInfoService.savePbocApproveRegiste(destroyRegisteOrderSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return toPbocOrderEditPage(destroyRegisteOrderSession, false, model, userCacheId);
		}
		// 申请下拨审批通过时指定库区
//		if (AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
//				.equals(destroyRegisteOrderSession.getBusinessType())) {
//			// UPDATE-START  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
//			// List<String> allIdList = Lists.newArrayList();
//			// allIdList.add(destroyRegisteOrderSession.getAllId());
//			String strRtn = this.bindingGoodsArea(destroyRegisteOrderSession);
//			// UPDATE-END  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
//			if (!AllocationConstant.SUCCESS.equals(strRtn)) {
//				addMessage(model, strRtn);
//			} else {
//				message = msg.getMessage("message.I2011", null, locale);
//				addMessage(model, message);
//			}
//		} else {
			message = msg.getMessage("message.I2011", null, locale);
			addMessage(model, message);
//		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(destroyRegisteOrderSession.getRoffice().getName());
		paramsList.add(destroyRegisteOrderSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(destroyRegisteOrderSession.getBusinessType(),
				destroyRegisteOrderSession.getStatus(), paramsList, destroyRegisteOrderSession.getRoffice().getId(), UserUtils.getUser());
		PbocAllAllocateInfo tempAllAllocateInfo = new PbocAllAllocateInfo();
		tempAllAllocateInfo.setPageType(destroyRegisteOrderSession.getPageType());
		return list(tempAllAllocateInfo, true, request, response, model);
	}

	/**
	 * 批量修改业务状态
	 * 
	 * @author WangDong
	 * @version 2016年7月7日
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
			String[] allIdArray = allIds.split(Constant.Punctuation.COMMA);
			List<String> allIdList = Arrays.asList(allIdArray);
			List<String> toQuotaAllIdList = Lists.newArrayList();
			// 如果目标状态是 驳回、待配款，则当前业务流水状态必须为 待审批，否则提示错误
			if ((AllocationConstant.PbocOrderStatus.REJECT_STATUS.equals(targetStatus)
					|| AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(targetStatus))
					&& !AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS.equals(targetStatus)) {
				for (String allId : allIdList) {
					PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(allId);

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
					if (!AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
						// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
						message = msg.getMessage("message.E2035", new String[] { pbocAllAllocateInfo.getAllId() },
								locale);
						addMessage(model, message);
						return list(pbocAllAllocateInfo, false, request, response, model);
					}
				}
			}

			try {
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
				for (String allId : toQuotaAllIdList) {
					// UPDATE-START  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
					this.bindingGoodsArea(pbocAllAllocateInfoService.get(allId));
					// UPDATE-END  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
				}
			}
			// 配款完成后打印取包明细
			return this.printQuotaGoodsAreaInfo(allIdList, model);
		}
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setPageType(pageType);
		return list(pbocAllAllocateInfo, false, request, response, model);
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

		List<PbocAllAllocateInfo> printDataList = pbocAllAllocateInfoService.getQuotaGoodsAreaInfo(allIdList);
		for (PbocAllAllocateInfo allocateInfo : printDataList) {
			for (PbocAllAllocateItem item : allocateInfo.getPbocAllAllocateItemList()) {
				for (AllAllocateGoodsAreaDetail areaDetail : item.getGoodsAreaDetailList()) {
					// 原封券以外，则截取rfid前八位
					areaDetail.getGoodsLocationInfo()
							.setRfid(StringUtils.left(areaDetail.getGoodsLocationInfo().getRfid(), 8));
				}
			}
		}
		model.addAttribute("printDataList", printDataList);

		return "modules/allocation/v02/destroyOutStore/printQuotaDetail";
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
		return "redirect:" + adminPath + "/allocation/v02/pbocDestroyOutStore/list?bInitFlag=true&pageType="
				+ pbocAllAllocateInfo.getPageType();
	}

	/**
	 * 
	 * Title: deleteInfo
	 * <p>
	 * Description: 销毁出库删除
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param pbocAllAllocateInfo
	 * @param redirectAttributes
	 * @param request
	 * @param response
	 * @return String 返回类型
	 */
	@RequestMapping(value = "/deleteInfo")
	public String deleteInfo(PbocAllAllocateInfo pbocAllAllocateInfo, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response) {

		Locale locale = LocaleContextHolder.getLocale();

		String message = "";

		// 销毁出库 待配款 或 待出库状态时可以删除业务信息
		if (!AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(pbocAllAllocateInfo.getStatus())
				&& !AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS
						.equals(pbocAllAllocateInfo.getStatus())) {
			// message.E2020=[删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
			message = msg.getMessage("message.E2020",
					new String[] { pbocAllAllocateInfo.getAllId(),
							DictUtils.getDictLabel(pbocAllAllocateInfo.getStatus(), "pboc_order_quota_status", "") },
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
		return "redirect:" + adminPath + "/allocation/v02/pbocDestroyOutStore/list?repage";
	}

	/**
	 * 根据流水单号绑定物品与库区位置信息
	 * 
	 * @author WangDong
	 * @version 2016年7月7日
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
		// UPDATE-START  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
		// 取得相关物品
		List<PbocAllAllocateItem> temQuotaItemList = pbocAllAllocateInfoService.getQuotaItemListByAllId(pbocAllAllocateInfo.getAllId());
		// UPDATE-END  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
		// TODO
		// pbocAllAllocateInfoService.deleteGoodsAreaDetailByAllId(allId);
		quotaItemList.addAll(temQuotaItemList);
		Map<String, ChangeStoreEntity> entiryMap = Maps.newHashMap();
		ChangeStoreEntity entity = null;

		// 判断库存
		for (PbocAllAllocateItem item : quotaItemList) {
			// UPDATE-START  原因：由当前登录人机构修改为登记机构  update by SonyYuanYang  2018/03/15
			PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
					pbocAllAllocateInfo.getRoffice().getId());
			// UPDATE-END  原因：由当前登录人机构修改为登记机构  update by SonyYuanYang  2018/03/15
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
			// UPDATE-START  原因：由当前登录人机构修改为登记机构  update by SonyYuanYang  2018/03/15
			List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = StoreCommonUtils.getBindingAreaInfoToDetail(
					quotaItemList, iDaysInterval, errorMessageCode, errorGoodsId, errorAllId,
					pbocAllAllocateInfo.getRoffice().getId());
			// UPDATE-END  原因：由当前登录人机构修改为登记机构  update by SonyYuanYang  2018/03/15
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
			StoreCommonUtils.changePbocSurplusStore(entiryList, pbocAllAllocateInfo.getRoffice().getId());
		}
		return message;
	}

}
