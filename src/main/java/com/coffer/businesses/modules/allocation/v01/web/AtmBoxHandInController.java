package com.coffer.businesses.modules.allocation.v01.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 钞箱入库Controller
 * 
 * @author sg
 * @version 2017-11-09
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v01/AtmBoxHandIn")
public class AtmBoxHandInController extends BaseController {
	/** 调缴用Service */
	@Autowired
	private AllocationService allocationService;

	/**
	 * 根据流水单号，取得钞箱入库信息
	 * 
	 * @author sg
	 * @version 2017年11月09日
	 * @param 流水单号(allId)
	 * @return 钞箱入库信息
	 */
	@ModelAttribute
	public AllAllocateInfo get(@RequestParam(required = false) String allId) {
		AllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)) {
			entity = allocationService.get(allId);
		}
		if (entity == null) {
			entity = new AllAllocateInfo();
		}
		return entity;
	}

	/**
	 * 根据查询条件，查询一览信息
	 * 
	 * @author sg
	 * @version 2017年11月09日
	 * @param AllAllocateInfo
	 *            钞箱入库信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 钞箱入库信息列表页面
	 */
	@RequiresPermissions("allocation:atmhandin:view")
	@RequestMapping(value = { "list", "" })
	public String list(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 设置业务类型为钞箱入库
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDIN);
		// 查询钞箱入库信息并进行分页
		Page<AllAllocateInfo> page = allocationService.findAtmBoxHand(new Page<AllAllocateInfo>(request, response),
				allAllocateInfo);
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		model.addAttribute("page", page);
		return "modules/allocation/v01/out/box/atmBoxInList";
	}

	/**
	 * 根据流水号查询朝向入库信息
	 * 
	 * @author sg
	 * @version 2017-11-09
	 * 
	 */
	@RequestMapping(value = "getAllAllocateDetail")
	public String getAllAllocateDetail(AllAllocateDetail allAllocateDetail, Model model) {
		// 查询钞箱入库详细
		List<AllAllocateDetail> atmDetailList = allocationService.findDetailByAllId(allAllocateDetail);
		model.addAttribute("atmDetailList", atmDetailList);
		model.addAttribute("AllAllocateDetail", allAllocateDetail);
		return "modules/allocation/v01/out/box/atmBoxInDetail";
	}
}