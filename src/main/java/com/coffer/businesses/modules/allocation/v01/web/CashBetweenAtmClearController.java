package com.coffer.businesses.modules.allocation.v01.web;

import java.math.BigDecimal;
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
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.service.CashBetweenService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 库间调拨：Atm清机功能Controller
 * 
 * @author SongYuanYang
 * @version 2017年7月6日
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v01/cashBetweenAtmClear")
public class CashBetweenAtmClearController extends BaseController {

	/** 调拨用Service */
	@Autowired
	private CashBetweenService cashBetweenService;

	/**
	 * 根据流水单号，取得调拨信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月6日
	 * 
	 * @param allId
	 *            流水单号
	 * @return 调拨信息
	 */
	@ModelAttribute
	public AllAllocateInfo get(@RequestParam(required = false) String allId) {
		AllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)) {
			// 根据流水单号取得单条主表信息
			entity = cashBetweenService.get(allId);
		}
		if (entity == null) {
			entity = new AllAllocateInfo();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询多条现金调拨信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月6日
	 * 
	 * @param allAllocateInfo
	 *            调拨信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 调拨信息列表页面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 查询条件： 开始时间
		if (allAllocateInfo.getCreateTimeStart() != null) {
			allAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(allAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (allAllocateInfo.getCreateTimeEnd() != null) {
			allAllocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(allAllocateInfo.getCreateTimeEnd())));
		}
		
		Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
		
		if (Constant.OfficeType.COFFER.equals(curOffice.getType())) {
			allAllocateInfo.setaOffice(curOffice);
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
			allAllocateInfo.setrOffice(curOffice);
		}
		
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between_ATM_Clear);
		
		// 查询现金调拨信息
		Page<AllAllocateInfo> page = cashBetweenService.findPage(new Page<AllAllocateInfo>(request, response),
				allAllocateInfo);

		model.addAttribute("allAllocateInfo", allAllocateInfo);
		model.addAttribute("page", page);
		return "modules/allocation/v01/between/atmClear/cashBetweenListAtmClear";
	}

	/**
	 * 跳转到清分中心登记页面
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月20日
	 * 
	 * @param allAllocateInfo
	 *            调拨信息
	 * @param model
	 * @return 清分中心登记页面
	 */
	@RequestMapping(value = "toClearCenterForm")
	public String toClearCenterForm(AllAllocateInfo allAllocateInfo, Model model) {
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between_ATM_Clear);
		
		Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
		if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
			allAllocateInfo.setrOffice(curOffice);
		}
		//创建缓存id
		String userCacheId = UserUtils.createUserCacheId();;
		// 缓存用户数据
		UserUtils.putCache(userCacheId, allAllocateInfo);
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		
		return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
	}

	/**
	 * 跳转到入库接收确认页面
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月13日
	 * 
	 * @param allAllocateInfo
	 *            现金调拨信息
	 * @param model
	 * @return 现金库入库接收确认页面
	 */
	@RequestMapping(value = "toCashInStoreReceive")
	public String toCashInStoreReceive(AllAllocateInfo allAllocateInfo, Model model) {
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		return "modules/allocation/v01/between/atmClear/cashInStoreReceiveAtmClear";
	}

	/**
	 * 跳转到详细信息页面
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月13日
	 * 
	 * @param allAllocateInfo
	 *            现金调拨信息
	 * @param model
	 * @return 清分中心接收确认页面
	 */
	@RequestMapping(value = "cashShowDetail")
	public String cashShowDetail(AllAllocateInfo allAllocateInfo, Model model) {
		// 初始化
		model.addAttribute(allAllocateInfo);
		return "modules/allocation/v01/between/atmClear/cashShowDetailFormAtmClear";
	}

	/**
	 * 清分中心登记页面添加物品
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月7日
	 * 
	 * @param allAllocateInfo
	 *            调拨信息
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 现金库登记页面
	 */
	@RequestMapping(value = "addClear")
	public String addClear(AllAllocateInfo allAllocateInfo, Model model,
			@RequestParam(value="userCacheId", required = true)String userCacheId) {
		
		AllAllocateInfo allocationSession = (AllAllocateInfo)UserUtils.getCache(userCacheId, new AllAllocateInfo());
		allocationSession.setaOffice(allAllocateInfo.getaOffice());
		
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		List<String> boxTypeList = Global.getList("businessType.allocation.between.atmClear.boxType");
		
		// 判断箱袋类型是否正确
		if (StringUtils.isNotBlank(allAllocateInfo.getAllAllocateItem().getBoxNo())) {
			StoBoxInfo stoBoxInfo = new StoBoxInfo();
			stoBoxInfo.setId(allAllocateInfo.getAllAllocateItem().getBoxNo());
			// 根据箱号查询对应的实体类对象
			stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
			
			if (stoBoxInfo == null || boxTypeList.contains(stoBoxInfo.getBoxType()) == false) {
				// message.E1076=[添加失败]{0}对应箱袋类型不正确！
				message = msg.getMessage("message.E1076", new String[] {allAllocateInfo.getAllAllocateItem().getBoxNo()}, locale);
				model.addAttribute("allAllocateInfo", allocationSession);
				addMessage(model, message);
				model.addAttribute("userCacheId", userCacheId);
				return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
			}
			// 验证箱袋所属机构与接收机构是否相符
			if (!allAllocateInfo.getaOffice().getId().equals(stoBoxInfo.getOffice().getId())) {
				Office boxOffice = SysCommonUtils.findOfficeById(stoBoxInfo.getOffice().getId());
				// message.E1077=[添加失败]【{0}】箱袋所属机构【{1}】与接收机构不符！
				message = msg.getMessage("message.E1077", new String[] {allAllocateInfo.getAllAllocateItem().getBoxNo(), boxOffice.getName()}, locale);
				model.addAttribute("allAllocateInfo", allocationSession);
				addMessage(model, message);
				model.addAttribute("userCacheId", userCacheId);
				return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
			}
		}

		// 取得物品id
		String goodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(allAllocateInfo.getStoGoodSelect());
		if (StringUtils.isEmpty(StoreCommonUtils.getGoodsName(goodsId))) {
			// message.E2060=[添加失败]：物品[{0}]尚未定义，请稍后再试或与系统管理员联系！
			message = msg.getMessage("message.E2060", new String[] {goodsId}, locale);
			addMessage(model, message);
		} else {
//			int index = 0;
			boolean isExist = false;
			// 是否填写箱子信息
			boolean isBoxExist = false;
			// 取得明细信息
			List<AllAllocateItem> itemList = allocationSession.getAllAllocateItemList();
			
			for (AllAllocateItem item : itemList) {
				
				if (isBoxExist == false && StringUtils.isNotBlank(item.getBoxNo())) {
					isBoxExist = true;
				}
				/*if (goodsId.equals(item.getGoodsId())
						&& AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
					isExist = true;
					break;
				}*/
				//验证箱号不能重复
				if(StringUtils.isNotBlank(allAllocateInfo.getAllAllocateItem().getBoxNo())) {
					if (allAllocateInfo.getAllAllocateItem().getBoxNo().equals(item.getBoxNo())
							&& AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
						isExist = true;
						break;
					}
				}
//				index++;
			}
			// 验证明细列表中是否存在未添加箱袋信息的物品
			if (itemList.size() > 0
					&& ((isBoxExist == true && StringUtils.isBlank(allAllocateInfo.getAllAllocateItem().getBoxNo()))
							|| (isBoxExist == false
									&& StringUtils.isNotBlank(allAllocateInfo.getAllAllocateItem().getBoxNo())))) {
				//message.E1075=[添加失败]明细列表中存在未添加箱袋信息的物品！
				message = msg.getMessage("message.E1075", null, locale);
				model.addAttribute("allAllocateInfo", allocationSession);
				addMessage(model, message);
				model.addAttribute("userCacheId", userCacheId);
				return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
			}
			
			if (isExist) {
				// 当物品重复时弹出通知提示
				/*message = msg.getMessage("message.I2014", new String[] { StoreCommonUtils.getGoodsName(itemList.get(index).getGoodsId()) }, locale);
				addMessage(model, message);*/
				
				//当箱号重复时弹出通知提示
				message = msg.getMessage("message.I2024", new String[] { allAllocateInfo.getAllAllocateItem().getBoxNo() }, locale);
				addMessage(model, message);
			} else {
				
				// 取得物品价值
				BigDecimal goodValue = StoreCommonUtils.getGoodsValue(goodsId);
				
				//判断登记金额
				cashBetweenService.computeGoodsAmount(allocationSession);
				String strMaxMoneyConfig = Global.getConfig("allocation.max.money");
				strMaxMoneyConfig = StringUtils.isBlank(strMaxMoneyConfig) ? "9999999999999.99" : strMaxMoneyConfig;
				BigDecimal maxMoneyConfig = new BigDecimal(Double.parseDouble(strMaxMoneyConfig));
				BigDecimal maxMoney = new BigDecimal(0);
				maxMoney = maxMoney.add(allocationSession.getRegisterAmount());
				
				maxMoney = maxMoney.add(goodValue.multiply(new BigDecimal(allAllocateInfo.getAllAllocateItem().getMoneyNumber())));
				if (maxMoney.compareTo(maxMoneyConfig) == 1) {
					//[添加失败]：添加金额超出上限[{0}]！
					message = msg.getMessage("message.E2009", new String[] { strMaxMoneyConfig }, locale);
					model.addAttribute("allAllocateInfo", allocationSession);
					addMessage(model, message);
					model.addAttribute("userCacheId", userCacheId);
					return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
				}
				
				
				// 箱号不重复时则可以正常添加
				AllAllocateItem allAllocateItem = new AllAllocateItem();
				allAllocateItem.setGoodsId(goodsId);
				allAllocateItem.setConfirmFlag(AllocationConstant.BetweenConfirmFlag.UNCONFIRMED);
				allAllocateItem.setMoneyNumber(allAllocateInfo.getAllAllocateItem().getMoneyNumber());
				allAllocateItem.setBoxNo(allAllocateInfo.getAllAllocateItem().getBoxNo());
				// 按箱号查询批次号
				String batchNo = AllocationCommonUtils
						.getPlanIdByBoxNo(allAllocateInfo.getAllAllocateItem().getBoxNo());
				allAllocateItem.setBatchNo(batchNo);
				// 计算金额
				allAllocateItem.setMoneyAmount(goodValue == null ? new BigDecimal(0)
						: goodValue.multiply(new BigDecimal(allAllocateItem.getMoneyNumber())));
				itemList.add(allAllocateItem);
			}
		}
		Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
		if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
			allocationSession.setrOffice(curOffice);
		} else {
			allocationSession.setrOffice(allAllocateInfo.getrOffice());
		}
		
		cashBetweenService.computeGoodsAmount(allocationSession);
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allocationSession);
		return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
	}

	/**
	 * 清分中心保存登记信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月24日
	 * 
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "saveClearRegisterInfo")
	public String saveClearRegisterInfo(AllAllocateInfo allAllocateInfo, @RequestParam(value="userCacheId", required = true)String userCacheId,
			@RequestParam(value="strUpdateDate", required = false)String strUpdateDate, RedirectAttributes redirectAttributes,
			Model model, HttpServletRequest request, HttpServletResponse response) {
		
		AllAllocateInfo allocationSession = (AllAllocateInfo) UserUtils.getCache(userCacheId, new AllAllocateInfo());
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		// 取得明细信息
		List<AllAllocateItem> itemList = allocationSession.getAllAllocateItemList();

		List<String> errorBoxList = Lists.newArrayList();
		List<String> registeredBoxList = Lists.newArrayList();
		// 查询修改前已经登记的箱子信息
		if (StringUtils.isNotBlank(allAllocateInfo.getAllId())) {
			// 库间清机业务修改时，还原箱子状态
			AllAllocateInfo tempAllocat = get(allAllocateInfo.getAllId());
			for (AllAllocateItem item : tempAllocat.getAllAllocateItemList()) {
				registeredBoxList.add(item.getBoxNo());
			}
		}
		
		StoBoxInfo stoBoxInfo;
		for (AllAllocateItem item : itemList) {
			// 箱号不为空或已登记时不验证箱子状态
			if (StringUtils.isNotBlank(item.getBoxNo()) && !registeredBoxList.contains(item.getBoxNo())) {
				stoBoxInfo = new StoBoxInfo();
				//stoBoxInfo.setRfid(item.getBoxNo());
				stoBoxInfo.setId(item.getBoxNo());
				// 根据箱号查询对应的实体类对象
				stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
				if (stoBoxInfo != null 
						&& !Constant.BoxStatus.ATM_BOX_STATUS_CLEAR.equals(stoBoxInfo.getBoxStatus())
						&& !Constant.BoxStatus.ATM_BOX_STATUS_BACK.equals(stoBoxInfo.getBoxStatus())) {
					// 箱子不存在或状态不正确
					errorBoxList.add(item.getBoxNo());
				}
			}
		}
		
		//错误箱子不为空，提示错误消息
		if (errorBoxList.size() != 0 ) {

			String[] boxArray = errorBoxList.toArray(new String[]{});
			// message.E1074=[保存失败]{0}对应箱袋信息不存在或箱袋状态不正确！
			message = msg.getMessage("message.E1074", boxArray, locale);
			addMessage(model, message);
			model.addAttribute("userCacheId", userCacheId);
			model.addAttribute("allAllocateInfo", allocationSession);
			return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
		} else {

			Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
			if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
				allocationSession.setrOffice(curOffice);
			} else {
				allocationSession.setrOffice(allAllocateInfo.getrOffice());
			}
			allocationSession.setaOffice(allAllocateInfo.getaOffice());

			if (allocationSession.getRegisterAmount() == null
					|| allocationSession.getRegisterAmount().equals(BigDecimal.ZERO)) {
				// message.E2057=[登记失败]：入库登记金额不能为0元！
				message = msg.getMessage("message.E2057", null, locale);
				addMessage(model, message);
				model.addAttribute("userCacheId", userCacheId);
				model.addAttribute("allAllocateInfo", allocationSession);

				return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
			}
			// message.I2001=流水单号：{0}保存成功！
			message = msg.getMessage("message.I2001", new String[] { allocationSession.getAllId() }, locale);
			try {
				allocationSession.setStrUpdateDate(strUpdateDate);
				// 保存清分登记信息
				cashBetweenService.saveClearRegisterInfo(allocationSession);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			}
		}

		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmClear/list?repage";
	}
	
	/**
	 * 现金库确认接收入库信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月12日
	 * 
	 * @param allAllocateInfo
	 *            调拨信息
	 * @param model
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "saveCashReceiveInfo")
	public String saveCashReceiveInfo(AllAllocateInfo allAllocateInfo, RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpServletResponse response) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (cashBetweenService.get(allAllocateInfo.getAllId()) == null ) {
			// message.E2029=[操作失败]：流水单号[{0}]对应调拨信息不存在！
			message = msg.getMessage("message.E2029", new String[] {allAllocateInfo.getAllId()}, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmClear/list?repage";
		}
		// message.I2001=流水单号：{0}保存成功！
		message = msg.getMessage("message.I2001", new String[] {allAllocateInfo.getAllId()}, locale);
		try {
			// 保存确认信息
			cashBetweenService.saveCashReceiveInStoreInfo(allAllocateInfo);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmClear/list?repage";
	}

	/**
	 * 清分中心删除物品信息
	 * 
	 * @author SongYangYang
	 * @version 2017年7月11日
	 * 
	 * @param userCacheId 用户缓存ID
	 * @param goodsId	boxNo
	 *            物品id		箱号
	 * @param model
	 * @return 清分中心登记页面
	 */
	@RequestMapping(value = "deleteClearGoods")
	public String deleteClearGoods(String goodsId, String boxNo,
			@RequestParam(value="userCacheId", required = true)String userCacheId, Model model) {
		AllAllocateInfo allocationSession = (AllAllocateInfo)UserUtils.getCache(userCacheId, new AllAllocateInfo());
		int index = 0;
		/** 初始化箱号以及删除对应的箱号添加信息 	by:wxz 	2018-1-12	begin*/
		String boxNoValue = "";
		for (AllAllocateItem item : allocationSession.getAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
				// 删除保存的物品
				boxNoValue = allocationSession.getAllAllocateItemList().get(index).getBoxNo();
				// 判断是否是对应的箱号，如果对应则进行删除操作
				if(boxNoValue.equals(boxNo)){
					allocationSession.getAllAllocateItemList().remove(index);
					break;
				}
			}
			index++;
		}
		/**	end	*/
		cashBetweenService.computeGoodsAmount(allocationSession);
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allocationSession);
		return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
	}

	/**
	 * 清分中心修改物品数量
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月12日
	 * 
	 * @param updateGoodsItem
	 *            更新的物品信息
	 * @param boxNo
	 *            箱袋编号
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 跳转页面（清分中心登记页面）
	 */
	@RequestMapping(value = "updateClearGoodsItem")
	public String updateClearGoodsItem(AllAllocateItem updateGoodsItem, @RequestParam(value="boxNo", required = true)String boxNo,
			@RequestParam(value="userCacheId", required = true)String userCacheId, Model model) {
		
		AllAllocateInfo allocationSession = (AllAllocateInfo)UserUtils.getCache(userCacheId, new AllAllocateInfo());
		
		Locale locale = LocaleContextHolder.getLocale();
		//判断登记金额
		cashBetweenService.computeGoodsAmount(allocationSession);
		String strMaxMoneyConfig = Global.getConfig("allocation.max.money");
		strMaxMoneyConfig = StringUtils.isBlank(strMaxMoneyConfig) ? "9999999999999.99" : strMaxMoneyConfig;
		BigDecimal maxMoneyConfig = new BigDecimal(Double.parseDouble(strMaxMoneyConfig));
		BigDecimal maxMoney = new BigDecimal(0);
		maxMoney = maxMoney.add(allocationSession.getRegisterAmount());
		
		// 修改物品数量
		for (AllAllocateItem item : allocationSession.getAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())
					&& AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag()) 
						&& item.getBoxNo().equals(boxNo)) {
				
				// 取得物品价值
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				//减掉既有
				maxMoney = maxMoney.subtract(goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
				//加上修改后的
				maxMoney = maxMoney.add(goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				if (maxMoney.compareTo(maxMoneyConfig) == 1) {
					//[添加失败]：添加金额超出上限[{0}]！
					String message = msg.getMessage("message.E2009", new String[] { strMaxMoneyConfig }, locale);
					model.addAttribute("allAllocateInfo", allocationSession);
					addMessage(model, message);
					model.addAttribute("userCacheId", userCacheId);
					return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
				}
				
				// 设置修改的物品数量
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}
		cashBetweenService.computeGoodsAmount(allocationSession);
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allocationSession);
		return "modules/allocation/v01/between/atmClear/clearCenterFormAtmClear";
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月7日
	 * 
	 * @param allAllocateInfo
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(AllAllocateInfo allAllocateInfo, SessionStatus status) {
		// 清空Session
		status.setComplete();
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmClear/list?repage";
	}

	/**
	 * 删除登记信息
	 * 
	 * @author SongYangYang
	 * @version 2017年7月13日
	 * 
	 * @param allAllocateInfo
	 *            登记信息
	 * @param redirectAttributes
	 * @return 列表页面
	 */
	@RequestMapping(value = "delete")
	public String delete(AllAllocateInfo allAllocateInfo, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (!AllocationConstant.BetweenStatus.TO_IN_STORE_STATUS.equals(allAllocateInfo.getStatus())) {
			// 对应业务状态已经变更，请重新查询！
			message = msg.getMessage("message.E2035", new String[] { allAllocateInfo.getAllId() }, locale);
		} else {
			try {
				// 进行删除操作
				cashBetweenService.delete(allAllocateInfo);
				// 弹出删除成功信息
				message = msg.getMessage("message.I2002", new String[] { allAllocateInfo.getAllId() }, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			}
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmClear/list?repage";
	}

}
