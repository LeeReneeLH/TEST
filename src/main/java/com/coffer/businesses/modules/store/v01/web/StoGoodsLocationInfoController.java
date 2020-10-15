/**
 * @author WangBaozhong
 * @version 2016年5月17日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.web;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.service.StoAreaSettingInfoService;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 库区内物品摆放位置Controller
 * @author WangBaozhong
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/StoGoodsLocationInfo")
public class StoGoodsLocationInfoController extends BaseController {
	
	@Autowired
	private StoGoodsLocationInfoService service;
	
	@Autowired
	private StoAreaSettingInfoService areaSettingService;
	
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public StoGoodsLocationInfo get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return service.get(id);
		} else {
			return new StoGoodsLocationInfo();
		}
	}
	
	/**
	 * 查询库房某区域内物品列表
	 * @author WangBaozhong
	 * @version 2016年5月18日
	 * 
	 *  
	 * @param stoGoodsLocationInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="findAreaGoodInfoList")
	public String findAreaGoodInfoList(StoGoodsLocationInfo stoGoodsLocationInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<StoGoodsLocationInfo> page = new Page<StoGoodsLocationInfo>(request, response);
		
		// 查询条件：开始时间
		if (stoGoodsLocationInfo.getInStoreDateStart() != null) {
			stoGoodsLocationInfo.setSearchDateStart(
					DateUtils.formatDate(
							DateUtils.getDateStart(stoGoodsLocationInfo.getInStoreDateStart()), 
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (stoGoodsLocationInfo.getInStoreDateEnd() != null) {
			stoGoodsLocationInfo.setSearchDateEnd(
					DateUtils.formatDate(
							DateUtils.getDateEnd(stoGoodsLocationInfo.getInStoreDateEnd()), 
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 非金融平台用户登录时仅显示本机构数据
		if (!Constant.OfficeType.DIGITAL_PLATFORM.equals(stoGoodsLocationInfo.getCurrentUser().getOffice().getType())) {
			stoGoodsLocationInfo.setOfficeId(UserUtils.getUser().getOffice().getId());
		} else {
			// 取得下属人行机构id
			List<String> officeIdList = Lists.newArrayList();
			Office office = UserUtils.getUser().getOffice();
			office.setParentIds(office.getParentIds() + office.getId());
			for (Office item : officeService.findByParentIdsLike(office)) {
				officeIdList.add(item.getId());
			}
			UserUtils.clearCache();
			stoGoodsLocationInfo.setOfficeIdList(officeIdList);
		}
		// 设定库区物品使用状态
		if (StringUtils.isBlank(stoGoodsLocationInfo.getDelFlag())) {
			List<String> statusFlagList = Lists.newArrayList();
			statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
			statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED);
			stoGoodsLocationInfo.setStatusFlagList(statusFlagList);
		}
		
		page = service.findPage(page, stoGoodsLocationInfo);
		
		if (StringUtils.isNotBlank(stoGoodsLocationInfo.getStoreAreaId())) {
			StoAreaSettingInfo areaSettingInfo = areaSettingService.getByStoreAreaId(stoGoodsLocationInfo.getStoreAreaId());
			if (areaSettingInfo != null) {
				stoGoodsLocationInfo.setStoreAreaName(areaSettingInfo.getStoreAreaName());
			}
		}
		model.addAttribute("page", page);
		model.addAttribute("stoGoodsLocationInfo", stoGoodsLocationInfo);
		return "modules/store/v01/stoGoodsLocationInfo/stoAreaGoodsInfoList";
	}
	
	/**
	 * 根据流水单号对应业务类型 跳转到对应业务列表页面
	 * @author WangBaozhong
	 * @version 2016年7月14日
	 * 
	 *  
	 * @param allId 流水单号
	 * @param redirectAttributes
	 * @return 目标页面
	 */
	@RequestMapping(value="toListPage")
	public String toListPage(@RequestParam(value="allId", required=true) String allId, RedirectAttributes redirectAttributes) {
		String businessType = BusinessUtils.getBusinessTypeFromAllId(allId);
		String rtnUrl = "";
		// 业务类型 代理上缴 或申请上缴
		if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(businessType) 
				|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(businessType)) {
			rtnUrl = "/allocation/v02/pbocHandinInStore?allId=" + allId;
		} else if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(businessType)) {
			// 申请下拨
			rtnUrl = "/allocation/v02/pbocAllocatedQuota/list?pageType=storeQuotaList&bInitFlag=false&allId=" + allId;
		} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(businessType)) {
			// 调拨入库
			rtnUrl = "/allocation/v02/pbocHorizontalAllocatedInStore?allId=" + allId;
		} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(businessType)) {
			// 调拨出库
			rtnUrl = "/allocation/v02/pbocHorizontalAllocatedOutStore?bInitFlag=false&allId=" + allId;
		} else if (AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(businessType)) {
			// 销毁出库
			rtnUrl = "/allocation/v02/pbocDestroyOutStore/list?bInitFlag=false&pageType=destroyApprovalList&allId=" + allId;
		} else if (AllocationConstant.BusinessType.PBOC_ORIGINAL_BANKNOTE_IN_STORE.equals(businessType)) {
			// 原封新券入库
			rtnUrl = "/allocation/v02/pbocOriginalBankNoteIn?bInitFlag=false&allId=" + allId;
		} else if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
			// 复点管理
			rtnUrl = "/allocation/v02/pbocRecounting?bInitFlag=false&allId=" + allId;
		} else {
			// 返回列表页面
			rtnUrl = "/store/v01/StoGoodsLocationInfo";
			
		}
		return "redirect:" + adminPath + rtnUrl;
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
	@RequestMapping(value = "back")
	public String back(SessionStatus status, RedirectAttributes redirectAttributes) {

		// 清空Session
		status.setComplete();
		return "redirect:" + adminPath + "/store/v01/stoAreaSettingInfo/showAreaGoodsGraph";
	}
}
