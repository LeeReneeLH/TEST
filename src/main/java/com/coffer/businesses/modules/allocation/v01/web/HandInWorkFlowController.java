package com.coffer.businesses.modules.allocation.v01.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.WorkFlowInfo;
import com.coffer.businesses.modules.allocation.v01.service.HandInWorkFlowService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;

/**
 * 上缴业务工作流
 * 
 * @author xp
 * @version 2017-09-07
 */
@Controller

@RequestMapping(value = "${adminPath}/allocation/v01/handInWorkFlow")
public class HandInWorkFlowController extends BaseController {
	@Autowired
	private HandInWorkFlowService handInWorkFlowService;

	/**
	 * 网点上缴业务工作流查询
	 * 
	 * @author xp
	 * @version 2017年9月7日
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "findStatus")
	public String findStatus(@RequestParam(required = false) String allId,
			@RequestParam(required = false) String backFlag, Model model) {
		// 查询条件
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		allAllocateInfo.setAllId(allId);
		allAllocateInfo.setStatus(AllocationConstant.Status.Onload);
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		// 查询出工作流信息
		WorkFlowInfo workFlowInfo = handInWorkFlowService.findStatus(allAllocateInfo);
		model.addAttribute("workFlowInfo", workFlowInfo);
		model.addAttribute("backFlag", backFlag);
		if (handInWorkFlowService.showOperateHistory(allAllocateInfo).size() != 0) {
			model.addAttribute("pointHandover", handInWorkFlowService.showOperateHistory(allAllocateInfo).get(0));
		}
		return "modules/allocation/v01/out/order/cashhandin/cashHandinWorkFlow";
	}

	/**
	 * 查询已交接和在途的明细
	 * 
	 * @author xp
	 * @version 2017年9月8日
	 * @param allId
	 * @param status
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "findDetail")
	public String findDetail(@RequestParam(required = false) String allId,
			@RequestParam(required = false) String status, Model model) {
		// 查询条件
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		allAllocateInfo.setAllId(allId);
		allAllocateInfo.setStatus(status);
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		if (StringUtils.isNotBlank(status)) {
			// 查询出对应的流水信息
			AllAllocateInfo handIn = handInWorkFlowService.findDetail(allAllocateInfo);

			// 过滤交接明细
			List<AllHandoverDetail> detailList = handIn.getStoreHandover().getDetailList();
			// 交接信息
			List<AllHandoverDetail> handoverList = Lists.newArrayList();
			// 授权信息
			List<AllHandoverDetail> managerList = Lists.newArrayList();
			for (AllHandoverDetail allHandoverDetail : detailList) {
				if (AllocationConstant.OperationType.TURN_OVER.equals(allHandoverDetail.getOperationType())
						|| AllocationConstant.OperationType.ACCEPT.equals(allHandoverDetail.getOperationType())) {
					// 如果是移交或接收
					handoverList.add(allHandoverDetail);
				} else if (AllocationConstant.OperationType.AUTHORIZATION
						.equals(allHandoverDetail.getOperationType())) {
					// 如果是授权
					managerList.add(allHandoverDetail);
				}
			}
			if (Collections3.isEmpty(handoverList)) {
				handoverList = null;
			}
			if (Collections3.isEmpty(managerList)) {
				managerList = null;
			}
			model.addAttribute("handoverList", handoverList);
			model.addAttribute("managerList", managerList);
			model.addAttribute("handIn", handIn);
		}

		// 查询出调拨历史
		List<AllAllocateInfo> allocateInfoList = handInWorkFlowService.findAllAllocateHistory(allAllocateInfo);
		model.addAttribute("allocateInfoList", allocateInfoList);
		return "modules/allocation/v01/out/order/cashhandin/handInWorkFlowDetail";
	}

	/**
	 * 查询已扫描和已登记的信息
	 * 
	 * @author xp
	 * @version 2017年9月11日
	 * @param allId
	 * @param status
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "findAllDetail")
	public String findAllDetail(@RequestParam(required = false) String allId,
			@RequestParam(required = false) String status, Model model) {
		// 查询条件
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		allAllocateInfo.setAllId(allId);
		allAllocateInfo.setStatus(status);
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		// 查询出对应的流水信息
		AllAllocateInfo handIn = handInWorkFlowService.findDetail(allAllocateInfo);
		// 查询出调拨历史
		List<AllAllocateInfo> allocateInfoList = handInWorkFlowService.findAllAllocateHistory(allAllocateInfo);
		model.addAttribute("handIn", handIn);
		model.addAttribute("allocateInfoList", allocateInfoList);
		return "modules/allocation/v01/out/order/cashhandin/handInWorkFlowAllDetail";
	}
	
	/**
	 * 返回上一级页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "backList")
	public String backList(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(required = false) String backFlag) {
		if (AllocationConstant.backType.HAND_IN.equals(backFlag)) {
			// 箱袋
			return "redirect:" + adminPath + "/allocation/v01/boxHandover/handin";
		} else {
			// 现金
			return "redirect:" + adminPath + "/allocation/v01/cashHandin";
		}
	}
}