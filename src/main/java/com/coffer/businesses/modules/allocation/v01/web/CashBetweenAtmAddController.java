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
import com.google.common.collect.Lists;

/**
 * 库间调拨：ATM加钞功能Controller
 * 
 * @author SongYuanYang
 * @version 2017年7月6日
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v01/cashBetweenAtmAdd")
public class CashBetweenAtmAddController extends BaseController {

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
			allAllocateInfo.setrOffice(curOffice);
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(curOffice.getType())) {
			allAllocateInfo.setaOffice(curOffice);
		}
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between_ATM_Add);
		// 查询现金调拨信息
		Page<AllAllocateInfo> page = cashBetweenService.findPage(new Page<AllAllocateInfo>(request, response),
				allAllocateInfo);

		model.addAttribute("allAllocateInfo", allAllocateInfo);
		model.addAttribute("page", page);
		
		return "modules/allocation/v01/between/atmAdd/cashBetweenListAtmAdd";
	}

	/**
	 * 跳转到现金库登记页面
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月7日
	 * 
	 * @param allAllocateInfo
	 *            调拨信息
	 * @param model
	 * @return 现金库登记页面
	 */
	@RequestMapping(value = "toCashOutStoreForm")
	public String toCashOutStoreForm(AllAllocateInfo allAllocateInfo,  Model model) {
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between_ATM_Add);
		Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
		if (Constant.OfficeType.COFFER.equals(curOffice.getType())) {
			allAllocateInfo.setrOffice(curOffice);
		}
		//创建缓存id
		String userCacheId = UserUtils.createUserCacheId();
		// 缓存用户数据
		UserUtils.putCache(userCacheId, allAllocateInfo);
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
	}

	/**
	 * 跳转到接收确认页面
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月10日
	 * 
	 * @param allocateInfo
	 *            现金调拨信息
	 * @param model
	 * @return 清分中心接收确认页面
	 */
	@RequestMapping(value = "toClearCenterReceiveForm")
	public String toClearCenterReceiveForm(AllAllocateInfo allAllocateInfo, Model model) {
		// 设定返回值
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		return "modules/allocation/v01/between/atmAdd/clearCenterReceiveFormAtmAdd";
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
		return "modules/allocation/v01/between/atmAdd/cashShowDetailFormAtmAdd";
	}

	/**
	 * 现金库登记页面添加物品
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
	@RequestMapping(value = "addRegister")
	public String addRegister(AllAllocateInfo allAllocateInfo, Model model,
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
				if (goodsId.equals(item.getGoodsId())) {
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
					return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
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
		if (Constant.OfficeType.COFFER.equals(curOffice.getType())) {
			allocationSession.setrOffice(curOffice);
		} else {
			allocationSession.setrOffice(allAllocateInfo.getrOffice());
		}
		
		allocationSession.setaOffice(allAllocateInfo.getaOffice());
		
		cashBetweenService.computeGoodsAmount(allocationSession);
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allocationSession);
		return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
	}


	/**
	 * 现金库保存登记信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月7日
	 * 
	 * @param userCacheId 用户缓存ID
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = "saveRegisterInfo")
	public String saveRegisterInfo(AllAllocateInfo allAllocateInfo, @RequestParam(value="userCacheId", required = true)String userCacheId, 
			@RequestParam(value="strUpdateDate", required = false)String strUpdateDate, Model model, RedirectAttributes redirectAttributes,
			HttpServletRequest request, HttpServletResponse response) {
		
		AllAllocateInfo allocationSession = (AllAllocateInfo)UserUtils.getCache(userCacheId, new AllAllocateInfo());
		
		Office curOffice = SysCommonUtils.findOfficeById(UserUtils.getUser().getOffice().getId());
		if (Constant.OfficeType.COFFER.equals(curOffice.getType())) {
			allocationSession.setrOffice(curOffice);
		} else {
			allocationSession.setrOffice(allAllocateInfo.getrOffice());
		}
		allocationSession.setaOffice(allAllocateInfo.getaOffice());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 判断登记总金额是否为0
		if (allocationSession.getRegisterAmount() == null
				|| BigDecimal.ZERO.equals(allocationSession.getRegisterAmount())) {
			// 登记总金额不能为0元！
			message = msg.getMessage("message.E2054", null, locale);
			addMessage(model, message);
			model.addAttribute("userCacheId", userCacheId);
			model.addAttribute("allAllocateInfo", allocationSession);
			return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
		}

		try {
			allocationSession.setStrUpdateDate(strUpdateDate);
			// 保存主表信息及物品表信息
			cashBetweenService.saveRegisterInfo(allocationSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			model.addAttribute("userCacheId", userCacheId);
			model.addAttribute("allAllocateInfo", allocationSession);
			return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allocationSession.getrOffice().getName());
		paramsList.add(allocationSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allocationSession.getBusinessType(),
				allocationSession.getStatus(), paramsList, allocationSession.getaOffice().getId(), UserUtils.getUser());
		// message.I2001=流水单号：{0}保存成功！
		message = msg.getMessage("message.I2001", new String[] {allocationSession.getAllId()}, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmAdd/list?repage";
	}

	/**
	 * 清分中心保存接收确认信息
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月11日
	 * 
	 * @param allAllocateInfo
	 *            主表信息
	 * @param model
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "saveConfirmInfo")
	public String saveConfirmInfo(AllAllocateInfo allAllocateInfo, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes,
			HttpServletResponse response) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		
		if (cashBetweenService.get(allAllocateInfo.getAllId()) == null ) {
			// message.E2029=[操作失败]：流水单号[{0}]对应调拨信息不存在！
			message = msg.getMessage("message.E2029", new String[] {allAllocateInfo.getAllId()}, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmAdd/list?repage";
		}
		
		if (allAllocateInfo.getConfirmAmount() == null || allAllocateInfo.getConfirmAmount().equals(BigDecimal.ZERO)) {
			// 接收金额不能为0元！
			message = msg.getMessage("message.E2055", null, locale);
			addMessage(model, message);
			return toClearCenterReceiveForm(allAllocateInfo, model);
		}
		if (allAllocateInfo.getConfirmAmount().compareTo(allAllocateInfo.getRegisterAmount()) != 0) {
			// 接收金额与登记金额不符！
			message = msg.getMessage("message.E2056", null, locale);
			addMessage(model, message);
			return toClearCenterReceiveForm(allAllocateInfo, model);
		}
		// message.I2001=流水单号：{0}保存成功！
		message = msg.getMessage("message.I2001", new String[] {allAllocateInfo.getAllId()}, locale);
		try {
		    // 保存确认信息
		    cashBetweenService.saveConfirmInfo(allAllocateInfo);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allAllocateInfo.getrOffice().getName());
		paramsList.add(allAllocateInfo.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allAllocateInfo.getBusinessType(),
				allAllocateInfo.getStatus(), paramsList, allAllocateInfo.getaOffice().getId(), UserUtils.getUser());
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmAdd/list?repage";
	}

	

	/**
	 * 现金库删除登记物品信息
	 * 
	 * @author SongYangYang
	 * @version 2017年7月7日
	 * 
	 * @param userCacheId 用户缓存ID
	 * @param goodsId
	 *            物品id
	 * @param model
	 * @return 现金库登记页面
	 */
	@RequestMapping(value = "deleteRegisterGoods")
	public String deleteRegisterGoods(String goodsId,
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
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("allAllocateInfo", allocationSession);
		return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
	}

	

	/**
	 * 现金库修改物品数量
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月7日
	 * 
	 * @param updateGoodsItem
	 *            更新的物品信息
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 跳转页面（现金库登记页面）
	 */
	@RequestMapping(value = "updateRegisterGoodsItem")
	public String updateRegisterGoodsItem(AllAllocateItem updateGoodsItem,
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
					return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
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
		return "modules/allocation/v01/between/atmAdd/cashOutStoreFormAtmAdd";
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
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmAdd/list?repage";
	}

	/**
	 * 打印库间调拨明细
	 * 
	 * @author SongYuanYang
	 * @version 2017年7月14日
	 * 
	 * @param model
	 * @param allId
	 *            流水单号
	 * @return 列表页面
	 */
	@RequestMapping(value = "printRegisterGoodsDetail")
	public String printRegisterGoodsDetail(@RequestParam(value = "allId", required = true) String allId, Model model) {
		// 取得主表信息
		AllAllocateInfo allAllocateInfo = cashBetweenService.get(allId);
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		// 打印登记明细
		return "modules/allocation/v01/between/atmAdd/printRegisterGoodsDetail";
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
		if (!AllocationConstant.BetweenStatus.TO_ACCEPT_STATUS.equals(allAllocateInfo.getStatus())) {
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
		return "redirect:" + adminPath + "/allocation/v01/cashBetweenAtmAdd/list?repage";
	}

}
