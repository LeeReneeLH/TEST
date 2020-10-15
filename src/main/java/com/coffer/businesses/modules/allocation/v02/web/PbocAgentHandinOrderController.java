package com.coffer.businesses.modules.allocation.v02.web;

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

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
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

/**
 * 人行代理上缴申请主表管理Controller
 * @author wangbaozhong
 * @version 2016-07-04
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocAgentHandinOrder")
public class PbocAgentHandinOrderController extends BaseController {
	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;
	
	@ModelAttribute
	public PbocAllAllocateInfo get(@RequestParam(required=false) String allId) {
		PbocAllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)){
			entity = pbocAllAllocateInfoService.get(allId);
		}
		if (entity == null){
			entity = new PbocAllAllocateInfo();
		}
		return entity;
	}
	
	/**
	 * 跳转至列表页面
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param pbocAllAllocateInfo 查询条件
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = {"list", ""})
	public String list(PbocAllAllocateInfo pbocAllAllocateInfo, @RequestParam(value="bInitFlag", required=false) Boolean bInitFlag, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
			
			// 如果是网点，设置用户的机构号，只能查询当前机构
			if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_OPT.equals(UserUtils.getUser().getUserType())) {
				pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
			} else {
				// 否则查询本金库
 				pbocAllAllocateInfo.setRoffice(UserUtils.getUser().getOffice());
			}
		}
		
		// 查询条件：预约  开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：预约 结束时间
		if (pbocAllAllocateInfo.getCreateTimeEnd() != null) {
			pbocAllAllocateInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getCreateTimeEnd())));
		}
//		// 人行管理员显示全部信息
//		if (!AllocationConstant.SysUserType.CENTRAL_OPT.equals(UserUtils.getUser().getUserType())) {
//			// 初始状态设定
//			if (bInitFlag != null && bInitFlag == true) {
//				// 人行下拨审批列表
//				// 初始页面显示待审批
//				pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS);
//			}
//		}
		//商行申请列表
		// 初始列表页面显示 业务类型为 申请代理上缴的数据
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
		
		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo); 
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		
		return "modules/allocation/v02/agentHandinOrder/pbocAgentHandinOrderList";
	}

	/**
	 * 页面跳转
	 * @author WangBaozhong
	 * @version 2016年6月28日
	 * 
	 *  
	 * @param pbocAllAllocateInfo 申请信息
	 * @param model
	 * @return 跳转页面（查看页面或编辑页面）
	 */
	@RequestMapping(value = "form")
	public String form(PbocAllAllocateInfo pbocAllAllocateInfo, Model model) {
		// 生成session的key值
		String userCacheId = UserUtils.createUserCacheId();
		if (StringUtils.isBlank(pbocAllAllocateInfo.getAllId())) {
			// 设定业务类型：申请下拨
			pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
			pbocAllAllocateInfo.setRofficeName(UserUtils.getUser().getOffice().getName());
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
		
		if (AllocationConstant.PageType.BussnessApplicationView.equals(pbocAllAllocateInfo.getPageType())) {
			// 查看时设置申请总金额和审批总金额大写
			pbocAllAllocateInfo.setRegisterAmount(pbocAllAllocateInfo.getRegisterAmount() == null ? 0d : pbocAllAllocateInfo.getRegisterAmount());
			pbocAllAllocateInfo.setConfirmAmount(pbocAllAllocateInfo.getConfirmAmount() == null ? 0d : pbocAllAllocateInfo.getConfirmAmount());
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
			List<String> allIdList = Lists.newArrayList();
			allIdList.add(pbocAllAllocateInfo.getAllId());
			List<PbocAllAllocateInfo> printDataList = pbocAllAllocateInfoService.getQuotaGoodsAreaInfo(allIdList);
			for (PbocAllAllocateInfo allocateInfo : printDataList) {
				for (PbocAllAllocateItem item : allocateInfo.getPbocAllAllocateItemList()) {
					for (AllAllocateGoodsAreaDetail areaDetail : item.getGoodsAreaDetailList()) {
						// 原封券以外，则截取rfid前八位
						areaDetail.getGoodsLocationInfo().setRfid(StringUtils.left(areaDetail.getGoodsLocationInfo().getRfid(), 8));
					}
				}
			}
			model.addAttribute("printDataList", printDataList);
		}
		if (AllocationConstant.PageType.BussnessApplicationView.equals(pbocAllAllocateInfo.getPageType())) {
			// add-start 交接人员照片显示 add by yanbingxu 2018/04/03
			AllocationCommonUtils.pbocHandoverFilter(pbocAllAllocateInfo, model);
			// add-end
			return "modules/allocation/v02/goodsAllocatedCommon/pbocShowCommonDetail";
		}
		return "modules/allocation/v02/agentHandinOrder/pbocAgentHandinOrderForm";
	}
	/**
	 * 
	 * Title: add
	 * <p>Description: 添加物品</p>
	 * @author:     wangbaozhong
	 * @param pbocAllAllocateInfo 提交物品明细
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "add")
	public String add(PbocAllAllocateInfo pbocAllAllocateInfo, @RequestParam(value="userCacheId", required = true)String userCacheId,
			Model model, HttpServletRequest request, HttpServletResponse response) {
		
		PbocAllAllocateInfo agentHandinOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		int iIndex = 0;
		boolean isExist = false;
		for (PbocAllAllocateItem item : agentHandinOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(strGoodsId) 
					&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
				isExist = true;
				break;
			}
			iIndex ++;
			
		}
		if (isExist == true) {
//			// 累加计算
//			PbocAllAllocateItem item = agentHandinOrderSession.getPbocAllAllocateItemList().get(iIndex);
//			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber() + item.getMoneyNumber());
//			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
//			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) : goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
			message = msg.getMessage("message.I2014", new String[] {agentHandinOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName()}, locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId", agentHandinOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {
			PbocAllAllocateItem item  = new PbocAllAllocateItem();
			item.setRegistType(AllocationConstant.RegistType.RegistPoint);
			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) : goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			agentHandinOrderSession.getPbocAllAllocateItemList().add(item);
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(agentHandinOrderSession);
		
		agentHandinOrderSession.setApplyDate(pbocAllAllocateInfo.getApplyDate());
		
		agentHandinOrderSession.setAgentOffice(pbocAllAllocateInfo.getAgentOffice());
		
		// ADD-START  原因：金融平台用户登录时设置登记机构  add by SonyYuanYang  2018/04/18
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
			agentHandinOrderSession.setRoffice(SysCommonUtils.findOfficeById(pbocAllAllocateInfo.getRoffice().getId()));
			agentHandinOrderSession.setRofficeName(pbocAllAllocateInfo.getRoffice().getName());
		}
		// ADD-END  原因：金融平台用户登录时设置登记机构  add by SonyYuanYang  2018/04/18
		
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("pbocAllAllocateInfo", agentHandinOrderSession);
		
		return "modules/allocation/v02/agentHandinOrder/pbocAgentHandinOrderForm";
	}
	
	/**
	 * 
	 * Title: deleteGoods
	 * <p>Description: 删除物品信息</p>
	 * @author:     wangbaozhong
	 * @param goodsId 物品ID
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "deleteGoods")
	public String deleteGoods(String goodsId, @RequestParam(value="userCacheId", required = true)String userCacheId,
			Model model, HttpServletRequest request, HttpServletResponse response) {
		
		PbocAllAllocateInfo agentHandinOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		int iIndex = 0;
		for (PbocAllAllocateItem item : agentHandinOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId) 
					&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
				agentHandinOrderSession.getPbocAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex ++;
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(agentHandinOrderSession);
		model.addAttribute("pbocAllAllocateInfo", agentHandinOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		
		return "modules/allocation/v02/agentHandinOrder/pbocAgentHandinOrderForm";
	}
	
	/**
	 * 
	 * Title: toUpdateGoodsItem
	 * <p>Description: 修改物品数量页面</p>
	 * @author:     wangbaozhong
	 * @param goodsId 物品信息
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "toUpdateGoodsItem")
	public String toUpdateGoodsItem(String goodsId, @RequestParam(value="userCacheId", required = true)String userCacheId, Model model) {
		
		PbocAllAllocateInfo agentHandinOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();
		for (PbocAllAllocateItem item : agentHandinOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)
					&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
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
	 * <p>Description: 修改物品数量</p>
	 * @author:     wangbaozhong
	 * @param updateGoodsItem 被修改物品信息
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "updateGoodsItem")
	public String updateGoodsItem(PbocAllAllocateItem updateGoodsItem, @RequestParam(value="userCacheId", required = true)String userCacheId, Model model){
		
		PbocAllAllocateInfo agentHandinOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		for (PbocAllAllocateItem item : agentHandinOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())
					&& AllocationConstant.RegistType.RegistPoint.equals(item.getRegistType())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) : goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}
		
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(agentHandinOrderSession);
		model.addAttribute("pbocAllAllocateInfo", agentHandinOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		
		return "modules/allocation/v02/agentHandinOrder/pbocAgentHandinOrderForm";
	}
	
	/**
	 * 保存申请明细
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param pbocAllAllocateInfo 申请明细信息
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @param request
	 * @param response
	 * @return 返回列表页面
	 */
	@RequestMapping(value = "save")
	public String save(PbocAllAllocateInfo pbocAllAllocateInfo, @RequestParam(value="userCacheId", required = true)String userCacheId,
			Model model, HttpServletRequest request, HttpServletResponse response) {
		
		PbocAllAllocateInfo agentHandinOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		
		if (agentHandinOrderSession.getRegisterAmount() == null || agentHandinOrderSession.getRegisterAmount() == 0d) {
			// [申请失败]：申请总金额不能为0元！
			message = msg.getMessage("message.E2034", null, locale);
			addMessage(model, message);
			return form(agentHandinOrderSession, model);
		}
		if (StringUtils.isNotBlank(pbocAllAllocateInfo.getStatus()) 
				&& !AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
			// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
			message = msg.getMessage("message.E2035", new String[] {pbocAllAllocateInfo.getAllId()}, locale);
			addMessage(model, message);
			return list(pbocAllAllocateInfo, false, request, response, model);
		}
		
		agentHandinOrderSession.setLoginUser(UserUtils.getUser());
		try {
			pbocAllAllocateInfoService.savePbocAllAllocateInfo(agentHandinOrderSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
		}
		// ADD-START  原因：发送通知  add by SonyYuanYang  2018/03/11
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(agentHandinOrderSession.getRoffice().getName());
		paramsList.add(agentHandinOrderSession.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(agentHandinOrderSession.getBusinessType(),
				agentHandinOrderSession.getStatus(), paramsList, agentHandinOrderSession.getAoffice().getId(), UserUtils.getUser());
		// ADD-END  原因：发送通知   add by SonyYuanYang  2018/03/11
		PbocAllAllocateInfo tempAllAllocateInfo = new PbocAllAllocateInfo();
		tempAllAllocateInfo.setPageType(agentHandinOrderSession.getPageType());
		return list(tempAllAllocateInfo, true, request, response, model);
	}
	
	/**
	 * 删除申请
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param pbocAllAllocateInfo 申请信息
	 * @param redirectAttributes
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value = "delete")
	public String delete(PbocAllAllocateInfo pbocAllAllocateInfo, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (!AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS.equals(pbocAllAllocateInfo.getStatus())) {
			// [操作失败]：流水单号：[{0}]对应业务状态已经变更，请重新查询！
			message = msg.getMessage("message.E2035", new String[] {pbocAllAllocateInfo.getAllId()}, locale);
		} else {
			try {
				pbocAllAllocateInfoService.delete(pbocAllAllocateInfo);
				//message.I2002=[成功]：流水单号{0}删除成功！
				message = msg.getMessage("message.I2002", new String[] {pbocAllAllocateInfo.getAllId()}, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
				
			}
		}
		
		addMessage(redirectAttributes, message);
		
		return "redirect:" + adminPath + "/allocation/v02/pbocAgentHandinOrder/list?repage";
	}

	
	
	/**
	 * 返回到列表页面
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
	public String back(PbocAllAllocateInfo pbocAllAllocateInfo, SessionStatus status){
		// 清空Session
		status.setComplete();
		return "redirect:" + adminPath + "/allocation/v02/pbocAgentHandinOrder/list?bInitFlag=true&pageType=" + pbocAllAllocateInfo.getPageType();
	}
	
}