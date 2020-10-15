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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;

/**
 * 人行复点出入库管理Controller
 * 
 * @author wangbaozhong
 * @version 2016-07-07
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocRecounting")
public class PbocRecountingController extends BaseController {
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
	 * @version 2016年6月22日
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
			if (AllocationConstant.SysUserType.CLEARING_CENTER_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CLEARING_CENTER_OPT.equals(UserUtils.getUser().getUserType())) {
				pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
				bInitFlag = false;
			} else {
				// 否则查询本金库
				pbocAllAllocateInfo.setRoffice(UserUtils.getUser().getOffice());
			}
			// 管库员 查看时 只显示 待配款 待出库 待出库交接 清分中，待入库交接
			if ((AllocationConstant.SysUserType.CENTRAL_STORE_MANAGER_OPT.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT
							.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_ALLOCATE_OPT.equals(UserUtils.getUser().getUserType()))
					&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_IN_STORE_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
			// 人行管理员 查看时 只显示 待配款 待出库 待出库交接 清分中，待入库交接 完成
			if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
				List<String> statusList = Lists.newArrayList();
				statusList.add(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_IN_STORE_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_HANDOVER_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS);
				statusList.add(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
				pbocAllAllocateInfo.setStatuses(statusList);
			}
		}

		// 查询条件：复点出库 开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setScanGateDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：复点出库 结束时间
		if (pbocAllAllocateInfo.getCreateTimeEnd() != null) {
			pbocAllAllocateInfo.setScanGateDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getCreateTimeEnd())));
		}
		// 查询条件：复点入库 开始时间
		if (pbocAllAllocateInfo.getSearchDateStart1() != null) {
			pbocAllAllocateInfo.setInStoreScanGateDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getSearchDateStart1())));
		}
		// 查询条件：复点入库 结束时间
		if (pbocAllAllocateInfo.getSearchDateEnd1() != null) {
			pbocAllAllocateInfo.setInStoreScanGateDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getSearchDateEnd1())));
		}

		// 查询条件：预约 时间
		if (pbocAllAllocateInfo.getApplyDate() != null) {
			pbocAllAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getApplyDate())));
			pbocAllAllocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getApplyDate())));
		}

		/* 业务类型设定 */
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_RE_COUNTING);

		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService
				.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		return "modules/allocation/v02/recounting/pbocRecountingList";
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
		// 生成session的key值
		String userCacheId = UserUtils.createUserCacheId();
		if (StringUtils.isNotBlank(pbocAllAllocateInfo.getAllId())) {
			// 循环设置物品名称
			for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
				item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
			}
			// 重新计算物品总价值
			pbocAllAllocateInfoService.computeRecountingGoodsAmount(pbocAllAllocateInfo);

			if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
				// 填充出入库物品所处库区位置
				for (PbocAllAllocateDetail allcateDetail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
					StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
					stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());
					if (AllocationConstant.InOutCoffer.IN.equals(allcateDetail.getInoutType())) {
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
		}

		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		// 缓存用户数据
		UserUtils.putCache(userCacheId, pbocAllAllocateInfo);

		model.addAttribute("userCacheId", userCacheId);

		if (AllocationConstant.PageType.StoreRecountingView.equals(pbocAllAllocateInfo.getPageType())) {
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
			// add-start 交接人员照片显示 by yanbingxu 2018/04/04
			// 交接信息初始化
			StringBuffer pbocInHandover = new StringBuffer();
			StringBuffer clearInHandover = new StringBuffer();
			StringBuffer pbocOutHandover = new StringBuffer();
			StringBuffer clearOutHandover = new StringBuffer();
			List<String> pbocInHandoverIdList = Lists.newArrayList();
			List<String> clearInHandoverIdList = Lists.newArrayList();
			List<String> pbocOutHandoverIdList = Lists.newArrayList();
			List<String> clearOutHandoverIdList = Lists.newArrayList();
			// 授权人信息初始化
			StringBuffer inAuthorize = new StringBuffer();
			StringBuffer outAuthorize = new StringBuffer();
			List<String> inAuthorizeIdList = Lists.newArrayList();
			List<String> outAuthorizeIdList = Lists.newArrayList();

			List<PbocAllHandoverUserDetail> handoverDetailList = Lists.newArrayList();
			if (pbocAllAllocateInfo.getPbocAllHandoverInfo() != null) {
				// 根据交接id取得交接人员信息
				handoverDetailList = pbocAllAllocateInfo.getPbocAllHandoverInfo().getHandoverUserDetailList();

				// 授权信息
				if (pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId() != null) {
					outAuthorize.append(pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserName());
					outAuthorizeIdList.add(pbocAllAllocateInfo.getPbocAllHandoverInfo().getManagerUserId());
				}
				if (pbocAllAllocateInfo.getPbocAllHandoverInfo().getRcInManagerUserId() != null) {
					inAuthorize.append(pbocAllAllocateInfo.getPbocAllHandoverInfo().getRcInManagerUserName());
					inAuthorizeIdList.add(pbocAllAllocateInfo.getPbocAllHandoverInfo().getRcInManagerUserId());
				}

				// 过滤交接信息
				for (PbocAllHandoverUserDetail handover : handoverDetailList) {
					if (AllocationConstant.inoutType.STOCK_IN.equals(handover.getInoutType())) {
						if (AllocationConstant.UserType.handover.equals(handover.getType())) {
							clearInHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
							clearInHandoverIdList.add(handover.getEscortId());
						}
						if (AllocationConstant.UserType.accept.equals(handover.getType())) {
							pbocInHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
							pbocInHandoverIdList.add(handover.getEscortId());
						}
					}
					if (AllocationConstant.inoutType.STOCK_OUT.equals(handover.getInoutType())) {
						if (AllocationConstant.UserType.handover.equals(handover.getType())) {
							pbocOutHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
							pbocOutHandoverIdList.add(handover.getEscortId());
						}
						if (AllocationConstant.UserType.accept.equals(handover.getType())) {
							clearOutHandover.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
							clearOutHandoverIdList.add(handover.getEscortId());
						}
					}
				}
			}

			model.addAttribute("inAuthorize", inAuthorize);
			model.addAttribute("outAuthorize", outAuthorize);
			model.addAttribute("inAuthorizeIdList", inAuthorizeIdList);
			model.addAttribute("outAuthorizeIdList", outAuthorizeIdList);
			model.addAttribute("pbocInHandover", pbocInHandover);
			model.addAttribute("clearInHandover", clearInHandover);
			model.addAttribute("pbocOutHandover", pbocOutHandover);
			model.addAttribute("clearOutHandover", clearOutHandover);
			model.addAttribute("pbocInHandoverIdList", pbocInHandoverIdList);
			model.addAttribute("clearInHandoverIdList", clearInHandoverIdList);
			model.addAttribute("pbocOutHandoverIdList", pbocOutHandoverIdList);
			model.addAttribute("clearOutHandoverIdList", clearOutHandoverIdList);
			// add-end 交接人员照片显示 by yanbingxu 2018/04/04
			model.addAttribute("printDataList", printDataList);
			return "modules/allocation/v02/recounting/pbocRecountingDetail";
		}

		return "modules/allocation/v02/recounting/pbocRecountingForm";
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
		// 取得session数据
		PbocAllAllocateInfo pbocRecountingSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());
		// 取得用户区域
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber() == null) {
			// [添加失败]：数量不正确！(数量应为正整数)
			message = msg.getMessage("message.E2001", null, locale);
			addMessage(model, message);
			// 重新计算物品总价值
			pbocAllAllocateInfoService.computeRecountingGoodsAmount(pbocRecountingSession);
			model.addAttribute("userCacheId", userCacheId);
			model.addAttribute("pbocAllAllocateInfo", pbocRecountingSession);
			return "modules/allocation/v02/recounting/pbocRecountingForm";
		}
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		for (PbocAllAllocateItem item : pbocRecountingSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(strGoodsId)) {
				isExist = true;
				break;
			}
			iIndex++;

		}
		if (isExist == true) {
			// // 累加计算
			// PbocAllAllocateItem item =
			// pbocRecountingSession.getPbocAllAllocateItemList().get(iIndex);
			// item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber()
			// + item.getMoneyNumber());
			// BigDecimal goodsValue =
			// StoreCommonUtils.getGoodsValue(strGoodsId);
			// item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) :
			// goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
			// [提示]：物品列表中已经存在物品【{0}】，如需修改物品数量，请点击物品名称进行修改。
			message = msg.getMessage("message.I2014",
					new String[] { pbocRecountingSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName() },
					locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId",
					pbocRecountingSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {

			if (pbocRecountingSession.getPbocAllAllocateItemList().size() >= 1) {
				// [提示]：复点登记每次仅能登记一种物品!
				message = msg.getMessage("message.I2015", null, locale);
				addMessage(model, message);
			} else {

				PbocAllAllocateItem item = new PbocAllAllocateItem();

				item.setGoodsId(strGoodsId);
				item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
				item.setRegistType(AllocationConstant.RegistType.RegistStore);
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
				item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
				pbocRecountingSession.getPbocAllAllocateItemList().add(item);
			}
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeRecountingGoodsAmount(pbocRecountingSession);
		// 设置上缴时间
		pbocRecountingSession.setApplyDate(pbocAllAllocateInfo.getApplyDate());
		// ADD-START  原因：添加物品时设置登记机构和接收机构到session中  add by SonyYuanYang  2018/03/17
		if (pbocAllAllocateInfo.getRoffice() != null) {
			// 设置登记机构
			Office rOffice = SysCommonUtils.findOfficeById(pbocAllAllocateInfo.getRoffice().getId());
			pbocRecountingSession.setRoffice(rOffice);
		}
		// 设置接收机构
		Office aOffice = SysCommonUtils.findOfficeById(pbocAllAllocateInfo.getAoffice().getId());
		pbocRecountingSession.setAoffice(aOffice);
		// ADD-END  原因：添加物品时设置登记机构和接收机构到session中  add by SonyYuanYang  2018/03/17
		model.addAttribute("pbocAllAllocateInfo", pbocRecountingSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/recounting/pbocRecountingForm";
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

		PbocAllAllocateInfo pbocRecountingSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		int iIndex = 0;
		for (PbocAllAllocateItem item : pbocRecountingSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)) {
				pbocRecountingSession.getPbocAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex++;
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeRecountingGoodsAmount(pbocRecountingSession);
		model.addAttribute("pbocAllAllocateInfo", pbocRecountingSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/recounting/pbocRecountingForm";
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

		PbocAllAllocateInfo pbocRecountingSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();
		for (PbocAllAllocateItem item : pbocRecountingSession.getPbocAllAllocateItemList()) {
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
	public String updateGoodsItem(PbocAllAllocateItem updateGoodsItem,
			@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model) {

		PbocAllAllocateInfo pbocRecountingSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		for (PbocAllAllocateItem item : pbocRecountingSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}

		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeRecountingGoodsAmount(pbocRecountingSession);
		model.addAttribute("pbocAllAllocateInfo", pbocRecountingSession);
		model.addAttribute("userCacheId", userCacheId);

		return "modules/allocation/v02/recounting/pbocRecountingForm";
	}

	/**
	 * 保存复点出库明细
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月23日
	 * 
	 * 
	 * @param userCacheId
	 *            用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return 返回列表页面
	 */
	@RequestMapping(value = "save")
	public String save(@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo pbocRecountingSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (pbocRecountingSession.getRegisterAmount() == null || pbocRecountingSession.getRegisterAmount() == 0d) {
			// [申请失败]：申请总金额不能为0元！
			message = msg.getMessage("message.E2034", null, locale);
			addMessage(model, message);
			model.addAttribute("pbocAllAllocateInfo", pbocRecountingSession);
			model.addAttribute("userCacheId", userCacheId);
			return "modules/allocation/v02/recounting/pbocRecountingForm";
		}

		pbocRecountingSession.setLoginUser(UserUtils.getUser());
		try {
			pbocAllAllocateInfoService.saveReCountingPbocAllAllocateInfo(pbocRecountingSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			model.addAttribute("pbocAllAllocateInfo", pbocRecountingSession);
			model.addAttribute("userCacheId", userCacheId);
			return "modules/allocation/v02/recounting/pbocRecountingForm";
		}

		// 申请下拨审批通过时指定库区
		// UPDATE-START  原因：此处方法参数修改，按单个流水修改修改库存  update by SonyYuanYang  2018/03/15
		// List<String> allIdList = Lists.newArrayList();
		// allIdList.add(pbocRecountingSession.getAllId());
		//String strRtn = this.bindingGoodsArea(pbocRecountingSession);
		// UPDATE-END  原因：此处方法参数修改，按单个流水修改修改库存  update by SonyYuanYang  2018/03/15
		//if (!AllocationConstant.SUCCESS.equals(strRtn)) {
		//	addMessage(model, strRtn);
		//} else {
			message = msg.getMessage("message.I2011", null, locale);
			addMessage(model, message);
		//}
		// ADD-START  原因：发送通知  add by SonyYuanYang  2018/03/17
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(pbocRecountingSession.getRoffice().getName());
		paramsList.add(pbocRecountingSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(pbocRecountingSession.getBusinessType(),
				pbocRecountingSession.getStatus(), paramsList, pbocRecountingSession.getRoffice().getId(),
				UserUtils.getUser());
		// ADD-END  原因：发送通知  add by SonyYuanYang  2018/03/17
		PbocAllAllocateInfo templlAllocateInfo = new PbocAllAllocateInfo();
		templlAllocateInfo.setPageType(pbocRecountingSession.getPageType());
		return list(templlAllocateInfo, true, request, response, model);
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
	 * 单独审批或驳回操作
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 * 
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
	public String aloneOption(@RequestParam(value = "userCacheId", required = true) String userCacheId, Model model,
			HttpServletRequest request, HttpServletResponse response) {

		PbocAllAllocateInfo pbocRecountingSession = (PbocAllAllocateInfo) UserUtils.getCache(userCacheId,
				new PbocAllAllocateInfo());

		Locale locale = LocaleContextHolder.getLocale();
		String message = msg.getMessage("message.I2011", null, locale);

		// 如果没填写审批物品，提示错误信息
		if ((pbocRecountingSession.getRegisterAmount() == null || pbocRecountingSession.getRegisterAmount() == 0d)) {
			// [审批失败]：审批总金额不能为0元！
			message = msg.getMessage("message.E2033", null, locale);
			addMessage(model, message);
			return form(pbocRecountingSession, model);
		}

		pbocRecountingSession.setLoginUser(UserUtils.getUser());
		try {
			pbocRecountingSession.setStatus(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
			pbocAllAllocateInfoService.saveReCountingPbocAllAllocateInfo(pbocRecountingSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			model.addAttribute("pbocAllAllocateInfo", pbocRecountingSession);
			model.addAttribute("userCacheId", userCacheId);
			return "modules/allocation/v02/recounting/pbocRecountingForm";
		}

		// 申请下拨审批通过时指定库区
		// UPDATE-START  原因：此处方法参数修改，按单个流水修改修改库存  update by SonyYuanYang  2018/03/15
		// List<String> allIdList = Lists.newArrayList();
		// allIdList.add(pbocRecountingSession.getAllId());
		String strRtn = this.bindingGoodsArea(pbocRecountingSession);
		// UPDATE-END  原因：此处方法参数修改，按单个流水修改修改库存  update by SonyYuanYang  2018/03/15
		if (!AllocationConstant.SUCCESS.equals(strRtn)) {
			addMessage(model, strRtn);
		} else {
			message = msg.getMessage("message.I2011", null, locale);
			addMessage(model, message);
		}
		PbocAllAllocateInfo templlAllocateInfo = new PbocAllAllocateInfo();
		templlAllocateInfo.setPageType(pbocRecountingSession.getPageType());
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
	public String back(PbocAllAllocateInfo pbocAllAllocateInfo, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		PbocAllAllocateInfo templlAllocateInfo = new PbocAllAllocateInfo();
		templlAllocateInfo.setPageType(pbocAllAllocateInfo.getPageType());
		return list(templlAllocateInfo, true, request, response, model);
	}

	/**
	 * 
	 * Title: deleteRecountingInfo
	 * <p>
	 * Description: 删除复点信息信息
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

		// 复点出库 待配款 或待出库时可以删除 业务信息
		if (!AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_STATUS
				.equals(pbocAllAllocateInfo.getStatus())
				&& !AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
			// message.E2020=[删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
			message = msg.getMessage("message.E2020",
					new String[] { pbocAllAllocateInfo.getAllId(),
							DictUtils.getDictLabel(pbocAllAllocateInfo.getStatus(), "pboc_recounting_status", "") },
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
		return "redirect:" + adminPath + "/allocation/v02/pbocRecounting/list?repage";
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
	private String bindingGoodsArea(PbocAllAllocateInfo pbocRecountingSession) {
		String message = AllocationConstant.SUCCESS;
		List<PbocAllAllocateItem> quotaItemList = Lists.newArrayList();
		Locale locale = LocaleContextHolder.getLocale();
		// UPDATE-START  原因：此处方法参数修改，按单个流水修改修改库存  update by SonyYuanYang  2018/03/15
		// 取得物品详情
		List<PbocAllAllocateItem> temQuotaItemList = pbocAllAllocateInfoService
				.getQuotaItemListByAllId(pbocRecountingSession.getAllId());
		quotaItemList.addAll(temQuotaItemList);
		// UPDATE-END  原因：此处方法参数修改，按单个流水修改修改库存  update by SonyYuanYang  2018/03/15
		Map<String, ChangeStoreEntity> entiryMap = Maps.newHashMap();
		ChangeStoreEntity entity = null;
		// 判断库存
		for (PbocAllAllocateItem item : quotaItemList) {
			// UPDATE-START  原因：判断登记机构的库存，不在判断当前登录机构的库存  update by SonyYuanYang  2018/03/15
			PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(),
					pbocRecountingSession.getRoffice().getId());
			// UPDATE-END  原因：判断登记机构的库存，不在判断当前登录机构的库存  update by SonyYuanYang  2018/03/15
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
			// UPDATE-START  原因：查询登记机构的库存信息，不在查询当前登录机构的库存信息  update by SonyYuanYang  2018/03/15
			List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = StoreCommonUtils.getBindingAreaInfoToDetail(
					quotaItemList, iDaysInterval, errorMessageCode, errorGoodsId, errorAllId,
					pbocRecountingSession.getRoffice().getId());
			// UPDATE-END  原因：查询登记机构的库存信息，不在查询当前登录机构的库存信息  update by SonyYuanYang  2018/03/15
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
			// UPDATE-START  原因：修改登记机构的库存，不在修改当前登录机构的库存  update by SonyYuanYang  2018/03/15
			StoreCommonUtils.changePbocSurplusStore(entiryList, pbocRecountingSession.getRoffice().getId());
			// UPDATE-END  原因：修改登记机构的库存，不在修改当前登录机构的库存  update by SonyYuanYang  2018/03/15
		}
		return message;
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

		return "modules/allocation/v02/recounting/printRecountingDetail";
	}
}