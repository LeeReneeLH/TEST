/**
 * @author WangBaozhong
 * @version 2016年5月13日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.web;


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
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.service.StoAreaSettingInfoService;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.businesses.modules.store.v01.service.StoGoodsService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.DbConfigUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;


/**
 * 库房区域设置Controller
 * @author WangBaozhong
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoAreaSettingInfo")
public class StoAreaSettingInfoController extends BaseController{
	
	@Autowired
	private StoAreaSettingInfoService service;
	
	@Autowired
	private StoGoodsLocationInfoService goodsLocationInfoservice;
	
	@Autowired
	private StoGoodsService stoGoodsService;
	
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public StoAreaSettingInfo get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return service.getByStoreAreaId(id);
		} else {
			return new StoAreaSettingInfo();
		}
	}
	
	/**
	 * 区域列表页面
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param model
	 * @return 区域列表页面
	 */
	@RequestMapping(value={ "list", "" })
	public String list(StoAreaSettingInfo stoAreaSettingInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<StoAreaSettingInfo> page = new Page<StoAreaSettingInfo>(request, response);
		Office strOffice = UserUtils.getUser().getOffice();
		
		if (stoAreaSettingInfo == null) {
			stoAreaSettingInfo = new StoAreaSettingInfo();
		}
		// 非金融平台用户登录时仅显示本机构数据
		if (!Constant.OfficeType.DIGITAL_PLATFORM.equals(stoAreaSettingInfo.getCurrentUser().getOffice().getType())) {
			stoAreaSettingInfo.setOfficeId(strOffice.getId());
		} else {
			// 取得下属人行机构id
			List<String> officeIdList = Lists.newArrayList();
			strOffice.setParentIds(strOffice.getParentIds() + strOffice.getId());
			for (Office office : officeService.findByParentIdsLike(strOffice)) {
				officeIdList.add(office.getId());
			}
			UserUtils.clearCache();
			stoAreaSettingInfo.setOfficeIdList(officeIdList);
		}
		page = service.findPage(page, stoAreaSettingInfo);
		model.addAttribute("stoAreaSettingInfo", stoAreaSettingInfo);
		model.addAttribute("page", page);
		
		return "modules/store/v01/stoAreaSettingInfo/stoAreaSettingInfoList";
	}
	
	/**
	 * 初始化库房区域
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param stoAreaSettingInfo 库区信息
	 * @param model
	 * @return 库区初始化页面
	 */
	@RequestMapping(value = "initForm")
	public String initForm(StoAreaSettingInfo stoAreaSettingInfo, Model model) {
		model.addAttribute("stoAreaSettingInfo", stoAreaSettingInfo);
		return "modules/store/v01/stoAreaSettingInfo/initStoAreaSettingInfoForm";
	}
	
	/**
	 * 编辑库房区域位置信息
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param stoAreaSettingInfo 库区位置信息
	 * @param model
	 * @return 库区编辑页面
	 */
	@RequestMapping(value = "form")
	public String form(StoAreaSettingInfo stoAreaSettingInfo, Model model) {
		List<StoGoods> stoGoodsList = stoGoodsService.findList(new StoGoods());
		
		model.addAttribute("goodsList", stoGoodsList);
		
		model.addAttribute("stoAreaSettingInfo", stoAreaSettingInfo);
		return "modules/store/v01/stoAreaSettingInfo/stoAreaSettingInfoForm";
	}

	/**
	 * 保存并生成区域
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param stoAreaSettingInfo 区域配置信息
	 * @return 区域列表页面
	 */
	@RequestMapping(value="/makeAreaInfos")
	public String makeAreaInfos(StoAreaSettingInfo stoAreaSettingInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 自动化库房开启时不能进行初始化
		if (AllocationConstant.AutomaticStoreSwitch.OPEN.equals(DbConfigUtils.getDbConfig("auto.vault.switch"))) {
			// [保存失败]自动化库房存在时无法进行初始化库区操作！
			message = msg.getMessage("message.E1093", null, locale);
			addMessage(model, message);
			return initForm(stoAreaSettingInfo, model);
		}
		if (stoAreaSettingInfo.getMaxSaveDays() < stoAreaSettingInfo.getMinSaveDays()) {
			// [保存失败]最大保存日数不能小于最小保存日数！
			message = msg.getMessage("message.E1052", null, locale);
			addMessage(model, message);
			return initForm(stoAreaSettingInfo, model);
		}
		String strOfficeID = UserUtils.getUser().getOffice().getId();
		// 非数字化金融平台时初始化本机构，金融平台时初始化所选机构
		if (!Constant.OfficeType.DIGITAL_PLATFORM.equals(stoAreaSettingInfo.getCurrentUser().getOffice().getType())) {
			stoAreaSettingInfo.setOfficeId(strOfficeID);
		}
		int iGoodsCnt = goodsLocationInfoservice.getGoodsCountByOfficeIdAndAreaType(stoAreaSettingInfo.getOfficeId(), stoAreaSettingInfo.getStoreAreaType());
		// 取得库区类型标签
		String strStoreAreaTypeLable = DictUtils.getDictLabel(stoAreaSettingInfo.getStoreAreaType(), "store_area_type", "");
		if (iGoodsCnt != 0) {
			
			// [保存失败]【{0}】库区内存在物品，不能初始化！
			message = msg.getMessage("message.E1048", new String[] {strStoreAreaTypeLable}, locale);
			addMessage(model, message);
			return initForm(stoAreaSettingInfo, model);
		}
		// 删除当前机构下的所有库房区域信息
		service.deleteAllByOfficeIdAndAreaType(stoAreaSettingInfo.getOfficeId(), stoAreaSettingInfo.getStoreAreaType());
				
		service.saveAreaSettingInfo(stoAreaSettingInfo);
		//[保存成功]：【{0}】库区初始化成功!
		message = msg.getMessage("message.I1019", new String[] {strStoreAreaTypeLable}, locale);
		addMessage(model, message);
		return list(stoAreaSettingInfo, request, response, model);
	}
	
	/**
	 * 保存修改区域信息
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param stoAreaSettingInfo 区域配置信息
	 * @return 区域列表页面
	 */
	@RequestMapping(value="/save")
	public String save(StoAreaSettingInfo stoAreaSettingInfo, HttpServletRequest request, 
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		StoAreaSettingInfo  areaSettingInfo = service.getByStoreAreaId(stoAreaSettingInfo.getId());
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		
		if (areaSettingInfo == null) {
			// [修改失败]库区不存在，请确认后再修改！
			message = msg.getMessage("message.E1050", null, locale);
			
		} else {
			
			if (stoAreaSettingInfo.getMaxSaveDays() < stoAreaSettingInfo.getMinSaveDays()) {
				// [保存失败]最大保存日数不能小于最小保存日数！
				message = msg.getMessage("message.E1052", null, locale);
				addMessage(model, message);
				return form(stoAreaSettingInfo, model);
			}
			
			int iGoodsCntInArea = goodsLocationInfoservice.getGoodsCountByAreaId(stoAreaSettingInfo.getId());
			// 库区内存在物品  设定物品不能为空
			if (iGoodsCntInArea > 0 && StringUtils.isBlank(stoAreaSettingInfo.getGoodsId())) {
				// message.E1060=[修改失败]库区【{0}】内存在物品【{1}】共{2}件,不能修改为空！
				message = msg.getMessage("message.E1060", new String[]{areaSettingInfo.getStoreAreaName(),
						StoreCommonUtils.getGoodsName(areaSettingInfo.getGoodsId()), Integer.toString(iGoodsCntInArea)}, locale);
				addMessage(model, message);
				return form(stoAreaSettingInfo, model);
			}
			// 库区内存在物品  设定物品不能修改
			if (iGoodsCntInArea > 0 && (!StringUtils.isBlank(stoAreaSettingInfo.getGoodsId())
					&& !stoAreaSettingInfo.getGoodsId().equals(areaSettingInfo.getGoodsId()))) {
				// message.E1059=[修改失败]库区【{0}】内存在物品【{1}】共{2}件,不能修改为新值【{3}】！
				message = msg.getMessage("message.E1059", new String[]{areaSettingInfo.getStoreAreaName(),
						StoreCommonUtils.getGoodsName(areaSettingInfo.getGoodsId()), Integer.toString(iGoodsCntInArea),
						StoreCommonUtils.getGoodsName(stoAreaSettingInfo.getGoodsId())}, locale);
				addMessage(model, message);
				return form(stoAreaSettingInfo, model);
			}
			//判断实际库区容量
			if (stoAreaSettingInfo.getMaxCapability() < iGoodsCntInArea) {
				message = msg.getMessage("message.E1051", new String[]{Integer.toString(iGoodsCntInArea)}, locale);
				addMessage(model, message);
				return form(stoAreaSettingInfo, model);
			}
			stoAreaSettingInfo.setIsNewRecord(false);
			service.save(stoAreaSettingInfo);
			// [保存成功]：库区{0}的信息保存成功!
			message = msg.getMessage("message.I1021", new String[] {areaSettingInfo.getStoreAreaName()}, locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/store/v01/stoAreaSettingInfo/list?repage";
	}
	
	/**
	 * 使某库房区域无效或有效
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param stoAreaSettingInfo 区域配置信息
	 * @return 区域列表页面
	 */
	@RequestMapping(value="/changeStatus")
	public String changeStatus(StoAreaSettingInfo stoAreaSettingInfo, HttpServletRequest request, 
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		
		StoAreaSettingInfo  areaSettingInfo = service.getByStoreAreaId(stoAreaSettingInfo.getId());
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		
		
		if (Constant.deleteFlag.Invalid.equals(stoAreaSettingInfo.getDelFlag())) {
			stoAreaSettingInfo.setDelFlag(Constant.deleteFlag.Valid);
		} else {
			int iGoodsCnt = goodsLocationInfoservice.getGoodsCountByAreaId(stoAreaSettingInfo.getId());
			if (iGoodsCnt != 0) {
				
				// [修改失败]库区{0}内存在物品，不能设置无效！
				message = msg.getMessage("message.E1049", new String[]{areaSettingInfo.getStoreAreaName()}, locale);
				addMessage(redirectAttributes, message);
				return "redirect:" + adminPath + "/store/v01/stoAreaSettingInfo/list?repage";
			}
			stoAreaSettingInfo.setDelFlag(Constant.deleteFlag.Invalid);
		}
		
		stoAreaSettingInfo.preUpdate();
		service.delete(stoAreaSettingInfo);
		
		//[修改成功]：库区【{0}】状态变更成功!
		message = msg.getMessage("message.I1020", new String[]{areaSettingInfo.getStoreAreaName()}, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/store/v01/stoAreaSettingInfo/list?repage";
	}
	/**
	 * 返回到列表页面
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param status Session状态
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "backToList")
	public String backToList(SessionStatus status, RedirectAttributes redirectAttributes) {

		// 清空Session
		status.setComplete();
		return "redirect:" + adminPath + "/store/v01/stoAreaSettingInfo/list?repage";
	}
	/**
	 * 返回到区域列表页面
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param status Session状态
	 * @param redirectAttributes
	 * @return 区域列表页面
	 */
	@RequestMapping(value = "back")
	public String back(String storeAreaType, @RequestParam(value="displayHref", required = false) String displayHref, Model model) {

		return showAreaGoodsGraph(storeAreaType, displayHref, model);
	}
	
	/**
	 * 根据库区类型和所属机构 显示实时库区图
	 * @author WangBaozhong
	 * @version 2016年5月17日
	 * 
	 *  
	 * @param model
	 * @param displayHref 是否显示出入库超链接
	 * @return 实时库区页面
	 */
	@RequestMapping(value="/showAreaGoodsGraph")
	public String showAreaGoodsGraph(String storeAreaType, @RequestParam(value="displayHref", required = false) String displayHref, Model model) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (StringUtils.isBlank(storeAreaType)) {
			// [查询失败]：库区类型未指定！
			message = msg.getMessage("message.E1054", null, locale);
			addMessage(model, message);
		} else {
			// 取得库区类型标签
			String strStoreAreaTypeLable = DictUtils.getDictLabel(storeAreaType, "store_area_type", "");
			Office strOffice = UserUtils.getUser().getOffice();
			List<List<StoAreaSettingInfo>> areaGoodsInfoList = service.findAreaStoreInfoByOfficeId(strOffice, storeAreaType);
			if (areaGoodsInfoList == null || areaGoodsInfoList.size() == 0) {
				// [提示]：【{0}】库区尚未初始化！
				message = msg.getMessage("message.I1023", new String[] {strStoreAreaTypeLable}, locale);
				addMessage(model, message);
			}
			model.addAttribute("currentStoreAreaType", storeAreaType);
			model.addAttribute("areaGoodsInfoList", areaGoodsInfoList);
		}
		model.addAttribute("displayHref", displayHref);
		return "modules/store/v01/stoGoodsLocationInfo/stoAreaGoodsGraphForm";
	}
	
	/**
	 * 清理库区物品
	 * 
	 * @author WangBaozhong
	 * @version 2016年5月16日
	 * 
	 *  
	 * @param stoAreaSettingInfo 区域配置信息
	 * @return 区域列表页面
	 */
	@RequestMapping(value="/clearRfidInStore")
	public String clearRfidInStore(StoAreaSettingInfo stoAreaSettingInfo, HttpServletRequest request, 
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		
		Locale locale = LocaleContextHolder.getLocale();
		
		String message = "";
		try {
			
			int unUsedGoodsCnt = goodsLocationInfoservice.getGoodsCountByAreaId(stoAreaSettingInfo.getId());
			if (unUsedGoodsCnt == 0) {
				// message.I1026=[提示]：库区【{0}】没有待清理物品！
				message = msg.getMessage("message.I1026", new String[]{stoAreaSettingInfo.getStoreAreaName()}, locale);
				addMessage(redirectAttributes, message);
				return "redirect:" + adminPath + "/store/v01/stoAreaSettingInfo/list?repage";
			}
			// 清理库区物品
			int updateCnt = goodsLocationInfoservice.clearUnUsedGoodsAtStore(stoAreaSettingInfo.getId(), 
					stoAreaSettingInfo.getGoodsId(), UserUtils.getUser());
			
			// 查询库区内预订物品数量
			int iReservedGoodsCnt = goodsLocationInfoservice.getReservedGoodsCountByAreaId(stoAreaSettingInfo.getId());
			
			// 设定提示消息
			if (iReservedGoodsCnt != 0) {
				// message.I1025=[修改成功]：库区【{0}】清理成功，清理物品在库{1}件，但尚有{2}件物品为预订状态，需清除业务后再进行清理!
				message = msg.getMessage("message.I1025", new String[]{stoAreaSettingInfo.getStoreAreaName(), 
						Integer.toString(updateCnt), Integer.toString(iReservedGoodsCnt)}, locale);
			} else {
				// [修改成功]：库区【{0}】清理成功，清理物品在库{0}件!
				message = msg.getMessage("message.I1024", new String[]{stoAreaSettingInfo.getStoreAreaName(), 
						Integer.toString(updateCnt)}, locale);
			}
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), 
					locale);
		}
		
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/store/v01/stoAreaSettingInfo/list?repage";
	}
}
