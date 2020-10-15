package com.coffer.businesses.modules.allocation.v02.web;

import java.math.BigDecimal;
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

import com.coffer.core.common.config.Global;
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
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;

/**
 * 人行审批登记Controller
 * @author wangbaozhong
 * @version 2016-07-01
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocApprovalRegiste")
public class PbocApprovalRegisteController extends BaseController {
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
	 * 跳转至人行审批登记页面
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 *  
	 * @param pbocAllAllocateInfo	页面初始化信息
	 * @param model
	 * @return	人行审批登记页面
	 */
	@RequestMapping(value="/toPbocOrderEditPage")
	public String toPbocOrderEditPage(PbocAllAllocateInfo pbocAllAllocateInfo, @RequestParam(value="bInitFlag", required=false) Boolean bInitFlag, 
			Model model) {
		
		if (bInitFlag == null || bInitFlag == true) {
			// DELETE-START  原因：清分机构修改为可选，此处不直接设置  delete by SonyYuanYang  2018/03/21
			// 如果不是系统管理员，设置清分机构]
			// if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
				// 设置代理机构
				//Office pbocCenterOffice = BusinessUtils.getPbocCenterByOffice(UserUtils.getUser().getOffice());
				//pbocAllAllocateInfo.setAgentOffice(BusinessUtils.getClearCenterByParentId(pbocCenterOffice.getId()));
			//}
			// DELETE-END  原因：清分机构修改为可选，此处不直接设置  delete by SonyYuanYang  2018/03/21
			// 初始化用款日期为明日
			pbocAllAllocateInfo.setApplyDate(DateUtils.addDate(new Date(), 1));
		
		} 
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		String  userCacheId = UserUtils.createUserCacheId();
		UserUtils.putCache(userCacheId, pbocAllAllocateInfo);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/additionalRecording/pbocApprovalRegisteForm";
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
		
		PbocAllAllocateInfo approvalRegisteOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		String strGoodsId = AllocationCommonUtils.getGoodsKeyFromStoGoodSelect(pbocAllAllocateInfo.getStoGoodSelect());
		// 按业务类型检查出入库物品类别是否正确
		if (this.checkGoodsByBusinessType(pbocAllAllocateInfo.getBusinessType(), pbocAllAllocateInfo.getStoGoodSelect().getClassification()) == false) {
			String strGoodsName = StoreCommonUtils.getGoodsNameById(strGoodsId);
			strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
			//[添加失败]：物品【{0}】，不在【{1}】业务调拨范围内！
			message = msg.getMessage("message.E2039", 
					new String[] {strGoodsName,DictUtils.getDictLabel(pbocAllAllocateInfo.getBusinessType(),
					"all_businessType", "")}, locale);
			addMessage(model, message);
			return toPbocOrderEditPage(approvalRegisteOrderSession, false, model);
		}
		
		int iIndex = 0;
		boolean isExist = false;
		for (PbocAllAllocateItem item : approvalRegisteOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(strGoodsId)) {
				isExist = true;
				break;
			}
			iIndex ++;
			
		}
		if (isExist == true) {
//			// 累加计算
//			PbocAllAllocateItem item = approvalRegisteOrderSession.getPbocAllAllocateItemList().get(iIndex);
//			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber() + item.getMoneyNumber());
//			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
//			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) : goodsValue.multiply(new BigDecimal(item.getMoneyNumber())));
			// [提示]：物品列表中已经存在物品【{0}】，如需修改物品数量，请点击物品名称进行修改。
			message = msg.getMessage("message.I2014", new String[] {approvalRegisteOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsName()}, locale);
			addMessage(model, message);
			model.addAttribute("existsGoodsId", approvalRegisteOrderSession.getPbocAllAllocateItemList().get(iIndex).getGoodsId());
		} else {
			
			PbocAllAllocateItem item  = new PbocAllAllocateItem();
			
			item.setGoodsId(strGoodsId);
			item.setMoneyNumber(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber());
			item.setRegistType(AllocationConstant.RegistType.RegistPoint);
			BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(strGoodsId);
			item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) : goodsValue.multiply(new BigDecimal(pbocAllAllocateInfo.getStoGoodSelect().getMoneyNumber())));
			item.setGoodsName(StoreCommonUtils.getGoodsName(strGoodsId));
			approvalRegisteOrderSession.getPbocAllAllocateItemList().add(item);
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(approvalRegisteOrderSession);
		approvalRegisteOrderSession.setApplyDate(pbocAllAllocateInfo.getApplyDate());
		approvalRegisteOrderSession.setRoffice(pbocAllAllocateInfo.getRoffice());
		approvalRegisteOrderSession.setBusinessType(pbocAllAllocateInfo.getBusinessType());
		// ADD-START  原因：添加物品时将页面选择的清分机构设置到session中  add by SonyYuanYang  2018/03/21
		// 设置清分机构
		if (pbocAllAllocateInfo.getAgentOffice() != null) {
			approvalRegisteOrderSession.setAgentOffice(pbocAllAllocateInfo.getAgentOffice());
		}
		// ADD-END  原因：添加物品时将页面选择的清分机构设置到session中  add by SonyYuanYang  2018/03/21
		model.addAttribute("pbocAllAllocateInfo", approvalRegisteOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		
		return "modules/allocation/v02/additionalRecording/pbocApprovalRegisteForm";
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
		
		PbocAllAllocateInfo approvalRegisteOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		int iIndex = 0;
		for (PbocAllAllocateItem item : approvalRegisteOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(goodsId)) {
				approvalRegisteOrderSession.getPbocAllAllocateItemList().remove(iIndex);
				break;
			}
			iIndex ++;
		}
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(approvalRegisteOrderSession);
		model.addAttribute("pbocAllAllocateInfo", approvalRegisteOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		return "modules/allocation/v02/additionalRecording/pbocApprovalRegisteForm";
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
		
		PbocAllAllocateInfo approvalRegisteOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		PbocAllAllocateItem updateGoodsItem = new PbocAllAllocateItem();
		for (PbocAllAllocateItem item : approvalRegisteOrderSession.getPbocAllAllocateItemList()) {
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
	 * <p>Description: 修改物品数量</p>
	 * @author:     wangbaozhong
	 * @param updateGoodsItem 被修改物品信息
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "updateGoodsItem")
	public String updateGoodsItem(PbocAllAllocateItem updateGoodsItem, @RequestParam(value="userCacheId", required = true)String userCacheId, 
			Model model){
		
		PbocAllAllocateInfo approvalRegisteOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		for (PbocAllAllocateItem item : approvalRegisteOrderSession.getPbocAllAllocateItemList()) {
			if (item.getGoodsId().equals(updateGoodsItem.getGoodsId())) {
				item.setMoneyNumber(updateGoodsItem.getMoneyNumber());
				BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(updateGoodsItem.getGoodsId());
				item.setMoneyAmount(goodsValue == null ? new BigDecimal(0) : goodsValue.multiply(new BigDecimal(updateGoodsItem.getMoneyNumber())));
				break;
			}
		}
		
		// 重新计算物品总价值
		pbocAllAllocateInfoService.computeGoodsAmount(approvalRegisteOrderSession);
		
		model.addAttribute("pbocAllAllocateInfo", approvalRegisteOrderSession);
		model.addAttribute("userCacheId", userCacheId);
		
		return "modules/allocation/v02/additionalRecording/pbocApprovalRegisteForm";
	}
	
	/**
	 * 保存人行审批登记信息
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 *  
	 * @param userCacheId 用户缓存ID
	 * @param model
	 * @return 审批登记页面
	 */
	@RequestMapping(value="/savePbocOrder")
	public String savePbocOrder(@RequestParam(value="userCacheId", required = true)String userCacheId, Model model, SessionStatus status) {
		
		PbocAllAllocateInfo approvalRegisteOrderSession = (PbocAllAllocateInfo)UserUtils.getCache(userCacheId, new PbocAllAllocateInfo());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		for (PbocAllAllocateItem item : approvalRegisteOrderSession.getPbocAllAllocateItemList()) {
			StoGoodSelect goodSelect = StoreCommonUtils.splitGood(item.getGoodsId());
			// 按业务类型检查出入库物品类别是否正确
			if (this.checkGoodsByBusinessType(approvalRegisteOrderSession.getBusinessType(), goodSelect.getClassification()) == false) {
				String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
				strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
				//[审批失败]：物品【{0}】，不在【{1}】业务调拨范围内！
				message = msg.getMessage("message.E2040", 
						new String[] {strGoodsName,DictUtils.getDictLabel(approvalRegisteOrderSession.getBusinessType(),
						"all_businessType", "")}, locale);
				addMessage(model, message);
				return toPbocOrderEditPage(approvalRegisteOrderSession, false, model);
			}
		}
		
		if (approvalRegisteOrderSession.getRegisterAmount() == null || approvalRegisteOrderSession.getRegisterAmount() == 0d) {
			// [申请失败]：申请总金额不能为0元！
			message = msg.getMessage("message.E2034", null, locale);
			addMessage(model, message);
			return toPbocOrderEditPage(approvalRegisteOrderSession, false,  model);
		}
		approvalRegisteOrderSession.setConfirmAmount(approvalRegisteOrderSession.getRegisterAmount());
		// 作成调拨信息
		approvalRegisteOrderSession.setLoginUser(UserUtils.getUser());
		
		try {
			List<PbocAllAllocateItem> itemList = approvalRegisteOrderSession.getPbocAllAllocateItemList();
			
			for(PbocAllAllocateItem item : itemList) {
				// 业务类型为 申请下拨，调拨出库，销毁出库 业务验证物品库存是否充足
				if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(approvalRegisteOrderSession.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(approvalRegisteOrderSession.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(approvalRegisteOrderSession.getBusinessType())) {
					// 取得登记机构所属人行机构
					Office rOffice = SysCommonUtils.findOfficeById(approvalRegisteOrderSession.getRoffice().getId());
					Office pbocCenterOffice = BusinessUtils.getPbocCenterByOffice(rOffice);
					// 判断物品库存是否充足
					PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(), pbocCenterOffice.getId());
					
					if (storeInfo == null) {
						String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
						strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
						//[审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
						message = msg.getMessage("message.E2037", new String[] {item.getGoodsId(), strGoodsName}, locale);
						addMessage(model, message);
						return toPbocOrderEditPage(approvalRegisteOrderSession, false, model);
					}
					String strGoodsName = StringUtils.isBlank(storeInfo.getGoodsName()) ? "" : storeInfo.getGoodsName();
					
					if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L || item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
						//[审批失败]：物品[{0}]库存不足！
						message = msg.getMessage("message.E2038", new String[] {strGoodsName  }, locale);
						addMessage(model, message);
						return toPbocOrderEditPage(approvalRegisteOrderSession, false, model);
					}
					// 判断库区物品是否充足
					long lGoodsNum = StoreCommonUtils.getGoodsNumInStoreAreaByGoodsId(item.getGoodsId(), pbocCenterOffice.getId());
					if (item.getMoneyNumber() > lGoodsNum) {
						//[审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
						message = msg.getMessage("message.E2037", new String[] {item.getGoodsId(), strGoodsName}, locale);
						addMessage(model, message);
						return toPbocOrderEditPage(approvalRegisteOrderSession, false, model);
					}
				}
				
			}
			pbocAllAllocateInfoService.savePbocApproveRegiste(approvalRegisteOrderSession);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return toPbocOrderEditPage(approvalRegisteOrderSession, false, model);
		}
		
		// 申请下拨审批通过时指定库区
 		if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(approvalRegisteOrderSession.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(approvalRegisteOrderSession.getBusinessType())) {
 			// UPDATE-START  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
 			String strRtn = this.bindingGoodsArea(approvalRegisteOrderSession);
 			// UPDATE-END  原因：此处方法参数修改，只对一个流水进行改库存操作  update by SonyYuanYang  2018/03/15
			if (!AllocationConstant.SUCCESS.equals(strRtn)) {
				addMessage(model, strRtn);
			} else {
				message = msg.getMessage("message.I2011", null, locale);
				addMessage(model, message);
			}
			
		} else {
			message = msg.getMessage("message.I2011", null, locale);
			addMessage(model, message);
		}
		return toPbocOrderEditPage(new PbocAllAllocateInfo(), true, model);
	}
	
	/**
	 * 根据流水单号绑定物品与库区位置信息
	 * @author WangBaozhong
	 * @version 2016年6月2日
	 * 
	 *  
	 * @param allIdList 流水单号列表
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
		//TODO
		//pbocAllAllocateInfoService.deleteGoodsAreaDetailByAllId(allId);
		quotaItemList.addAll(temQuotaItemList);
		Map<String, ChangeStoreEntity> entiryMap = Maps.newHashMap();
		ChangeStoreEntity entity = null;
		
		// 判断库存
		for (PbocAllAllocateItem item : quotaItemList) {
			// UPDATE-START  原因：由当前登录人机构修改为接收机构  update by SonyYuanYang  2018/03/15
			PbocStoStoresInfo storeInfo = StoreCommonUtils.getPbocStoStoresInfoByGoodsId(item.getGoodsId(), pbocAllAllocateInfo.getAoffice().getId());
			// UPDATE-END  原因：由当前登录人机构修改为接收机构  update by SonyYuanYang  2018/03/15
			if (storeInfo == null) {
				String strGoodsName = StoreCommonUtils.getGoodsNameById(item.getGoodsId());
				strGoodsName = StringUtils.isBlank(strGoodsName) ? "" : strGoodsName;
				//[审批失败]：物品[{0}:{1}]对应库存信息不存在，请稍后再试或与系统管理员联系！
				message = msg.getMessage("message.E2037", new String[] {item.getGoodsId(), strGoodsName}, locale);
				return message;
			}
			String strGoodsName = StringUtils.isBlank(storeInfo.getGoodsName()) ? "" : storeInfo.getGoodsName();
			
			if (storeInfo.getSurplusStoNum() == null || storeInfo.getSurplusStoNum() == 0L || item.getMoneyNumber() > storeInfo.getSurplusStoNum()) {
				//[审批失败]：物品[{0}]库存不足！
				message = msg.getMessage("message.E2038", new String[] {strGoodsName  }, locale);
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
			//取得绑定调拨物品库区信息列表
			String strDaysInterval = Global.getConfig("store.area.getgoods.days.interval");
			int iDaysInterval = StringUtils.isNotBlank(strDaysInterval) ? Integer.parseInt(strDaysInterval): 0;
			String errorMessageCode = null;
			String errorGoodsId = null;
			String errorAllId = null;
			// UPDATE-START  原因：由当前登录人机构修改为接收机构  update by SonyYuanYang  2018/03/15
			List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = StoreCommonUtils.getBindingAreaInfoToDetail(quotaItemList, 
					iDaysInterval, errorMessageCode, errorGoodsId, errorAllId, pbocAllAllocateInfo.getAoffice().getId());
			// UPDATE-END  原因：由当前登录人机构修改为接收机构  update by SonyYuanYang  2018/03/15
			if (goodsAreaDetailList == null) {
				message = msg.getMessage(errorMessageCode, new String[]{errorAllId, 
						StoreCommonUtils.getPbocGoodsNameByGoodId(errorGoodsId)}, locale);
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
			// UPDATE-START  原因：由当前登录人机构修改为接收机构  update by SonyYuanYang  2018/03/15
			StoreCommonUtils.changePbocSurplusStore(entiryList, pbocAllAllocateInfo.getAoffice().getId());
			// UPDATE-END  原因：由当前登录人机构修改为接收机构  update by SonyYuanYang  2018/03/15
		}
		return message;
	}
	
	/**
	 * 按业务类型检查出入库物品类别是否正确
	 * @author WangBaozhong
	 * @version 2016年7月13日
	 * 
	 * @param businessType 业务类型
	 * @param classification 物品种类
	 * 
	 * @return false : 非当前业务应该出库的物品 ，true:当前业务可以出库的物品
	 */
	private boolean checkGoodsByBusinessType(String businessType, String classification) {
		if ("".equals(Global.getConfig("allocation.businessType.goodsClassification.check"))) {
			return true;
		}
		
		List<String> relationList = Lists.newArrayList();
		if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_ORIGINAL_BANKNOTE_IN_STORE.equals(businessType)) {
			//入库业务与物品类别关系
			relationList = Global.getList("allocation.businessType.goodsClassification.in.store");
		} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
			//出库业务与物品类别关系
			relationList = Global.getList("allocation.businessType.goodsClassification.out.store");
		}
		
		String strbusinessClassificationRelation = "";
		for (String relation : relationList) {
			String relationArray[] = relation.split(Constant.Punctuation.HALF_COLON);
			if (businessType.equals(relationArray[0])) {
				strbusinessClassificationRelation = relationArray[1];
				break;
			}
		}
		
		if (strbusinessClassificationRelation.indexOf(classification) == -1) {
			// 出入库出错物品为原封券时，显示原封券箱号及翻译
			return false;
		}
		
		return true;
	}
	
}