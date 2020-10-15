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
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 库间调拨：库间清机功能Controller
 * 
 * @author SongYuanYang
 * @version 2017年7月6日
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v01/cashBetweenDtoDC")
public class CashBetweenDtoDCController extends BaseController {

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
		
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.DOOR_TO_DOOR_COLLECTION);
		
		// 查询现金调拨信息
		Page<AllAllocateInfo> page = cashBetweenService.findPage(new Page<AllAllocateInfo>(request, response),
				allAllocateInfo);

		model.addAttribute("allAllocateInfo", allAllocateInfo);
		model.addAttribute("page", page);
		return "modules/allocation/v01/between/doorToDoorCollection/cashBetweenListDtoDC";
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
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.DOOR_TO_DOOR_COLLECTION);
		
		Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
		if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
			allAllocateInfo.setrOffice(curOffice);
		}
		//创建缓存id
		String userCacheId = UserUtils.createUserCacheId();
		// 缓存用户数据
		UserUtils.putCache(userCacheId, allAllocateInfo);

		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		return "modules/allocation/v01/between/doorToDoorCollection/clearCenterFormDtoDC";
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
		return "modules/allocation/v01/between/doorToDoorCollection/cashInStoreReceiveDtoDC";
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
		return "modules/allocation/v01/between/doorToDoorCollection/cashShowDetailFormDtoDC";
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
		
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		// 取得物品id
		String goodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(allAllocateInfo.getStoGoodSelect());
		if (StringUtils.isEmpty(StoreCommonUtils.getGoodsName(goodsId))) {
			// message.E2060=[添加失败]：物品[{0}]尚未定义，请稍后再试或与系统管理员联系！
			message = msg.getMessage("message.E2060", new String[] {goodsId}, locale);
			addMessage(model, message);
		} else {
		
			int index = 0;
			boolean isExist = false;
			// 取得明细信息
			List<AllAllocateItem> itemList = allocationSession.getAllAllocateItemList();
			for (AllAllocateItem item : itemList) {
				if (goodsId.equals(item.getGoodsId())
						&& AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
					isExist = true;
					break;
				}
				index++;
			}
			if (isExist) {
				// 当物品重复时弹出通知提示
				message = msg.getMessage("message.I2014", new String[] { StoreCommonUtils.getGoodsName(itemList.get(index).getGoodsId()) }, locale);
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
					return "modules/allocation/v01/between/doorToDoorCollection/clearCenterFormDtoDC";
				}
				
				// 物品不重复时则可以正常添加
				AllAllocateItem allAllocateItem = new AllAllocateItem();
				allAllocateItem.setGoodsId(goodsId);
				allAllocateItem.setConfirmFlag(AllocationConstant.BetweenConfirmFlag.UNCONFIRMED);
				allAllocateItem.setMoneyNumber(allAllocateInfo.getAllAllocateItem().getMoneyNumber());
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
		allocationSession.setaOffice(allAllocateInfo.getaOffice());
		
		cashBetweenService.computeGoodsAmount(allocationSession);
		model.addAttribute("allAllocateInfo", allocationSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v01/between/doorToDoorCollection/clearCenterFormDtoDC";
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
			RedirectAttributes redirectAttributes, 
			@RequestParam(value="strUpdateDate", required = false)String strUpdateDate,
			Model model, HttpServletRequest request, HttpServletResponse response) {
		
		AllAllocateInfo allocationSession = (AllAllocateInfo)UserUtils.getCache(userCacheId, new AllAllocateInfo());
		
		Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
		if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
			allocationSession.setrOffice(curOffice);
		} else {
			allocationSession.setrOffice(allAllocateInfo.getrOffice());
		}
		allocationSession.setaOffice(allAllocateInfo.getaOffice());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (allocationSession.getRegisterAmount() == null
				|| allocationSession.getRegisterAmount().equals(BigDecimal.ZERO)) {
			// message.E2057=[登记失败]：入库登记金额不能为0元！
			message = msg.getMessage("message.E2057", null, locale);
			addMessage(model, message);
			model.addAttribute("userCacheId", userCacheId);
			model.addAttribute("allAllocateInfo", allocationSession);
			return "modules/allocation/v01/between/doorToDoorCollection/clearCenterFormDtoDC";
		}
		// message.I2001=流水单号：{0}保存成功！
		message = msg.getMessage("message.I2001", new String[] {allocationSession.getAllId()}, locale);
		try {
			allocationSession.setStrUpdateDate(strUpdateDate);
			// 保存清分登记信息
			cashBetweenService.saveClearRegisterInfo(allocationSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenDtoDC/list?repage";
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
			return "redirect:" + adminPath + "/allocation/v01/cashBetweenDtoDC/list?repage";
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
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenDtoDC/list?repage";
	}


	/**
	 * 清分中心删除物品信息
	 * 
	 * @author SongYangYang
	 * @version 2017年7月11日
	 * 
	 * @param userCacheId 用户缓存ID
	 * @param goodsId
	 *            物品id
	 * @param model
	 * @return 清分中心登记页面
	 */
	@RequestMapping(value = "deleteClearGoods")
	public String deleteClearGoods(String goodsId,
			@RequestParam(value="userCacheId", required = true)String userCacheId, Model model) {
		
		AllAllocateInfo allocationSession = (AllAllocateInfo)UserUtils.getCache(userCacheId, new AllAllocateInfo());
		
		int index = 0;
		for (AllAllocateItem item : allocationSession.getAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
				// 删除保存的物品
				allocationSession.getAllAllocateItemList().remove(index);
				break;
			}
			index++;
		}
		// 重新计算物品总价值
		cashBetweenService.computeGoodsAmount(allocationSession);
		model.addAttribute("allAllocateInfo", allocationSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v01/between/doorToDoorCollection/clearCenterFormDtoDC";
	}


	/**
	 * 清分中心修改物品数量
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月12日
	 * 
	 * @param updateGoodsItem
	 *            更新的物品信息
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 跳转页面（清分中心登记页面）
	 */
	@RequestMapping(value = "updateClearGoodsItem")
	public String updateClearGoodsItem(AllAllocateItem updateGoodsItem,
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
					&& AllocationConstant.BetweenConfirmFlag.UNCONFIRMED.equals(item.getConfirmFlag())) {
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
					return "modules/allocation/v01/between/doorToDoorCollection/clearCenterFormDtoDC";
				}
				
				// 设置修改的物品数量
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0)
						: goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}
		// 重新计算物品总价值
		cashBetweenService.computeGoodsAmount(allocationSession);
		model.addAttribute("allAllocateInfo", allocationSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v01/between/doorToDoorCollection/clearCenterFormDtoDC";
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
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenDtoDC/list?repage";
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
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenDtoDC/list?repage";
	}

}
