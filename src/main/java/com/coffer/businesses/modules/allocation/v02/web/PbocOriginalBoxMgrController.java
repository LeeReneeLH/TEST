package com.coffer.businesses.modules.allocation.v02.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;

/**
 * 人行原封箱管理Controller
 * @author wangbaozhong
 * @version 2016-07-11
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocOriginalBoxMgr")
public class PbocOriginalBoxMgrController extends BaseController {
	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	
	@ModelAttribute
	public StoOriginalBanknote get(@RequestParam(required=false) String id) {
		StoOriginalBanknote entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoOriginalBanknoteService.get(id);
		}
		if (entity == null){
			entity = new StoOriginalBanknote();
		}
		return entity;
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(StoOriginalBanknote stoOriginalBanknote, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 查询当前机构数据
		stoOriginalBanknote.setRoffice(UserUtils.getUser().getOffice());
		
		Page<StoOriginalBanknote> page = stoOriginalBanknoteService.findPage(new Page<StoOriginalBanknote>(request, response), stoOriginalBanknote); 
		model.addAttribute("page", page);
		model.addAttribute("stoOriginalBanknote", stoOriginalBanknote);
		return "modules/allocation/v02/originalBoxMgr/pbocOriginalBoxMgrList";
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
			rtnUrl = "/allocation/v02/pbocOriginalBoxMgr";
			
		}
		return "redirect:" + adminPath + rtnUrl;
	}
}