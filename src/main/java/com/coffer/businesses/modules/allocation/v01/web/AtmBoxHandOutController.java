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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 钞箱出库：钞箱出库调拨功能Controller
 * 
 * @author wxz
 * @version 2017-11-09
 */
@Controller
@SessionAttributes({ "allocatedOrderSession" })
@RequestMapping(value = "${adminPath}/allocation/v01/atmBoxHandOut")
public class AtmBoxHandOutController extends BaseController {

	/** 调缴用Service */
	@Autowired
	private AllocationService allocationService;

	/**
	 * @author wxz
	 * @version 2017-11-09
	 *
	 *          根据流水号，取得钞箱出库信息
	 * @param allId
	 *            流水号
	 * @return 钞箱出库信息
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
	 * @author wxz
	 * @version 2017-11-09
	 *
	 *          钞箱出库信息查询
	 * @param allocateInfo
	 *            调拨信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @return 调拨信息列表
	 */
	@RequiresPermissions("allocation:atmBoxHandOut:view")
	@RequestMapping(value = "list")
	public String listHandout(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		// 设置业务种别(钞箱出库)
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDOUT);
		// 查询钞箱出库信息
		Page<AllAllocateInfo> page = allocationService.findAtmBoxHand(new Page<AllAllocateInfo>(request, response),
				allAllocateInfo);
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		model.addAttribute("page", page);
		return "modules/allocation/v01/out/box/atmBoxOutList";
	}

	/**
	 * 根据流水号查询钞箱出库信息
	 * 
	 * @author wxz
	 * @version 2017-11-09
	 * 
	 */
	@RequiresPermissions("allocation:atmBoxHandOut:view")
	@RequestMapping(value = "getAllAllocateDetail")
	public String getAllAllocateDetail(AllAllocateDetail allAllocateDetail, Model model) {
		// 查询钞箱出库详细
		List<AllAllocateDetail> atmDetailList = allocationService.findDetailByAllId(allAllocateDetail);
		model.addAttribute("atmDetailList", atmDetailList);
		model.addAttribute("AllAllocateDetail", allAllocateDetail);
		return "modules/allocation/v01/out/box/atmBoxOutDetail";
	}
	
}
