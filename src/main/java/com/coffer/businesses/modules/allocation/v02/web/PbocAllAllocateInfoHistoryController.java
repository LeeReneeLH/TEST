package com.coffer.businesses.modules.allocation.v02.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocWorkflowInfo;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoHistoryService;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;

/**
 * 
 * Title: PbocWorkflowController
 * <p>
 * Description: 人行调拨工作流程图Controller
 * </p>
 * 
 * @author yanbingxu
 * @date 2018年3月26日 上午10:37:13
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocWorkflow")
public class PbocAllAllocateInfoHistoryController extends BaseController {

	@Autowired
	private PbocAllAllocateInfoHistoryService pbocAllAllocateInfoHistoryService;
	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;
	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 
	 * Title: findApplicationHandinStatus
	 * <p>
	 * Description: 申请上缴（商行上缴）流程图状态查询
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param allId
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "findApplicationHandinStatus")
	public String findApplicationHandinStatus(String allId, Model model) {
		// 查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		// 查询出工作流信息
		PbocWorkflowInfo pbocWorkflowInfo = pbocAllAllocateInfoHistoryService.findStatus(pbocAllAllocateInfo);
		model.addAttribute("pbocWorkflowInfo", pbocWorkflowInfo);
		return "modules/allocation/v02/handinInStore/pbocHandinInStoreWorkflow";
	}

	/**
	 * 
	 * Title: findApplicationAllocationStatus
	 * <p>
	 * Description: 申请调款流程图状态查询
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param allId
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "findApplicationAllocationStatus")
	public String findApplicationAllocationStatus(String allId, Model model) {
		// 查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		// 查询出工作流信息
		PbocWorkflowInfo pbocWorkflowInfo = pbocAllAllocateInfoHistoryService.findStatus(pbocAllAllocateInfo);
		model.addAttribute("pbocWorkflowInfo", pbocWorkflowInfo);
		return "modules/allocation/v02/allocatedQuota/pbocAllocatedQuotaWorkflow";
	}

	/**
	 * 
	 * Title: showApplicationOperateInfo
	 * <p>
	 * Description: 申请上缴,申请调款,商行上缴工作流详细信息查看
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param allId
	 * @param status
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "showApplicationOperateInfo")
	public String showApplicationOperateInfo(String allId, String status, Model model) {
		List<PbocAllAllocateInfo> historyList = Lists.newArrayList();
		PbocAllAllocateInfo operateInfo = new PbocAllAllocateInfo();

		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		if (StringUtils.isNotBlank(status)) {
			pbocAllAllocateInfo.setStatus(status);
			// 当前查看状态信息
			operateInfo = pbocAllAllocateInfoHistoryService.findDetail(pbocAllAllocateInfo);
		} else {
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
			// 当前查看状态信息
			operateInfo = pbocAllAllocateInfoHistoryService.findDetail(pbocAllAllocateInfo);
			operateInfo.setStatus(status);
		}

		// 操作历史信息
		historyList = pbocAllAllocateInfoHistoryService.findAllHistory(pbocAllAllocateInfo);

		List<PbocAllHandoverUserDetail> handoverList = Lists.newArrayList();
		List<String> managerList = Lists.newArrayList();
		
		if (operateInfo.getPbocAllHandoverInfo() != null) {
			managerList.add(operateInfo.getPbocAllHandoverInfo().getManagerUserName());
			for (PbocAllHandoverUserDetail handover : operateInfo.getPbocAllHandoverInfo().getHandoverUserDetailList()) {
				if (AllocationConstant.UserType.accept.equals(handover.getType())
						|| AllocationConstant.UserType.handover.equals(handover.getType())) {
					handoverList.add(handover);
				}
			}
		}

		// 配款信息
		List<String> allIdList = Lists.newArrayList();
		allIdList.add(pbocAllAllocateInfo.getAllId());
		List<PbocAllAllocateInfo> printDataList = pbocAllAllocateInfoService.getQuotaGoodsAreaInfo(allIdList);
		
		model.addAttribute("operateInfo", operateInfo);
		model.addAttribute("historyList", historyList);
		model.addAttribute("managerList", managerList);
		model.addAttribute("handoverList", handoverList);
		model.addAttribute("printDataList", printDataList);
		return "modules/allocation/v02/handinInStore/pbocAllocatedWorkflowDetail";
	}

	/**
	 * 
	 * Title: showRecountingOperateInfo
	 * <p>Description: 复点管理工作流详细信息查看</p>
	 * @author:     yanbingxu
	 * @param allId
	 * @param status
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "showRecountingOperateInfo")
	public String showRecountingOperateInfo(String allId, String status, Model model) {
		List<PbocAllAllocateInfo> historyList = Lists.newArrayList();
		PbocAllAllocateInfo operateInfo = new PbocAllAllocateInfo();

		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		if (StringUtils.isNotBlank(status)) {
			pbocAllAllocateInfo.setStatus(status);
			// 当前查看状态信息
			operateInfo = pbocAllAllocateInfoHistoryService.findDetail(pbocAllAllocateInfo);
		} else {
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
			// 当前查看状态信息
			operateInfo = pbocAllAllocateInfoHistoryService.findDetail(pbocAllAllocateInfo);
			operateInfo.setStatus(status);
		}

		// 操作历史信息
		historyList = pbocAllAllocateInfoHistoryService.findAllHistory(pbocAllAllocateInfo);

		List<PbocAllHandoverUserDetail> inHandoverList = Lists.newArrayList();
		List<String> inManagerList = Lists.newArrayList();
		List<PbocAllHandoverUserDetail> outHandoverList = Lists.newArrayList();
		List<String> outManagerList = Lists.newArrayList();

		// 出入库交接信息过滤
		if (operateInfo.getPbocAllHandoverInfo() != null) {
			inManagerList.add(operateInfo.getPbocAllHandoverInfo().getRcInManagerUserName());
			outManagerList.add(operateInfo.getPbocAllHandoverInfo().getManagerUserName());
			for (PbocAllHandoverUserDetail handover : operateInfo.getPbocAllHandoverInfo().getHandoverUserDetailList()) {
				if (AllocationConstant.inoutType.STOCK_IN.equals(handover.getInoutType())) {
					if (AllocationConstant.UserType.accept.equals(handover.getType())
							|| AllocationConstant.UserType.handover.equals(handover.getType())) {
						inHandoverList.add(handover);
					}
				}
				if (AllocationConstant.inoutType.STOCK_OUT.equals(handover.getInoutType())) {
					if (AllocationConstant.UserType.accept.equals(handover.getType())
							|| AllocationConstant.UserType.handover.equals(handover.getType())) {
						outHandoverList.add(handover);
					}
				}
			}
		}
		
		// 配款信息
		List<String> allIdList = Lists.newArrayList();
		allIdList.add(pbocAllAllocateInfo.getAllId());
		List<PbocAllAllocateInfo> printDataList = pbocAllAllocateInfoService.getQuotaGoodsAreaInfo(allIdList);

		model.addAttribute("operateInfo", operateInfo);
		model.addAttribute("historyList", historyList);
		model.addAttribute("inManagerList", inManagerList);
		model.addAttribute("inHandoverList", inHandoverList);
		model.addAttribute("outManagerList", outManagerList);
		model.addAttribute("outHandoverList", outHandoverList);
		model.addAttribute("printDataList", printDataList);
		return "modules/allocation/v02/recounting/pbocRecountingWorkFlowDetail";
	}
	
	/**
	 * 
	 * Title: findRecountStatus
	 * <p>
	 * Description: 发行基金复点管理工作流
	 * </p>
	 * 
	 * @author: wanghan
	 * @param allId
	 * @param model
	 * @return String 返回类型
	 */
	@RequestMapping(value = "findRecountStatus")
	public String findRecountStatus(@RequestParam(required = false) String allId, Model model) {
		// 查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		// 查询出工作流信息
		PbocWorkflowInfo pbocWorkflowInfo = pbocAllAllocateInfoHistoryService.findStatus(pbocAllAllocateInfo);
		model.addAttribute("pbocWorkflowInfo", pbocWorkflowInfo);

		return "modules/allocation/v02/recounting/pbocRecountingWorkFlow";
	}

	/**
	 * 跳转至销毁出库工作流页面
	 * 
	 * @author SongYuanYang
	 * @version 2018年4月2日
	 * 
	 * @param allId
	 *            流水单号
	 * @param model
	 * @return 工作流页面
	 */
	@RequestMapping(value = "findDestroyOutStoreStatus")
	public String findDestroyOutStoreStatus(@RequestParam(required = false) String allId, Model model) {
		// 查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		// 查询出工作流信息
		PbocWorkflowInfo pbocWorkflowInfo = pbocAllAllocateInfoHistoryService.findStatus(pbocAllAllocateInfo);
		model.addAttribute("pbocWorkflowInfo", pbocWorkflowInfo);
		return "modules/allocation/v02/destroyOutStore/pbocDestroyOutStoreWorkFlow";
	}

	/**
	 * 跳转至调拨入库工作流页面
	 * 
	 * @author SongYuanYang
	 * @version 2018年4月2日
	 * 
	 * @param allId
	 *            流水单号
	 * @param model
	 * @return 工作流页面
	 */
	@RequestMapping(value = "findAllocatedInStoreStatus")
	public String findAllocatedInStoreStatus(@RequestParam(required = false) String allId, Model model) {
		// 查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		// 查询出工作流信息
		PbocWorkflowInfo pbocWorkflowInfo = pbocAllAllocateInfoHistoryService.findStatus(pbocAllAllocateInfo);
		model.addAttribute("pbocWorkflowInfo", pbocWorkflowInfo);
		return "modules/allocation/v02/horizontalAllocatedInStore/pbocAllocatedInStoreWorkFlow";
	}

	/**
	 * 跳转至调拨出库工作流页面
	 * 
	 * @author SongYuanYang
	 * @version 2018年4月2日
	 * 
	 * @param allId
	 *            流水单号
	 * @param model
	 * @return 工作流页面
	 */
	@RequestMapping(value = "findAllocatedOutStoreStatus")
	public String findAllocatedOutStoreStatus(@RequestParam(required = false) String allId, Model model) {
		// 查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		pbocAllAllocateInfo.setAllId(allId);
		// 查询出工作流信息
		PbocWorkflowInfo pbocWorkflowInfo = pbocAllAllocateInfoHistoryService.findStatus(pbocAllAllocateInfo);
		model.addAttribute("pbocWorkflowInfo", pbocWorkflowInfo);
		return "modules/allocation/v02/horizontalAllocatedOutStore/pbocAllocatedOutStoreWorkFlow";
	}

	/**
	 * 跳转至调拨出入库工作流详情页面
	 * 
	 * @author SongYuanYang
	 * @version 2018年4月2日
	 * 
	 * @param allId
	 *            流水单号
	 * @param model
	 * @return 工作流详情页面
	 */
	@RequestMapping(value = "showDetailInfo")
	public String showDetailInfo(@RequestParam(required = true) String allId,
			@RequestParam(required = true) String status, Model model) {
		// 查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		// 设置流水单号
		pbocAllAllocateInfo.setAllId(allId);
		// 设置状态
		pbocAllAllocateInfo.setStatus(status);
		// 查看状态信息
		PbocAllAllocateInfo operateInfo = pbocAllAllocateInfoHistoryService.findDetail(pbocAllAllocateInfo);
		// 待交接状态时查询交接信息
		StringBuilder handoverStaff = null;
		String managerStaff = null;
		if (AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_HANDOVER_STATUS.equals(status)) {
			// 交接人员
			handoverStaff = new StringBuilder();
			// 授权人员
			managerStaff = operateInfo.getPbocAllHandoverInfo().getManagerUserName();
			// 所有交接人员
			List<PbocAllHandoverUserDetail> handoverList = operateInfo.getPbocAllHandoverInfo().getHandoverUserDetailList();
			for (PbocAllHandoverUserDetail handover : handoverList) {
				// 交接人员追加
				handoverStaff.append(handover.getEscortName() + Constant.Punctuation.HALF_SPACE);
			}
		}
		// 操作历史信息
		List<PbocAllAllocateInfo> historyList = pbocAllAllocateInfoHistoryService.findAllHistory(pbocAllAllocateInfo);
		model.addAttribute("handoverStaff", handoverStaff);
		model.addAttribute("managerStaff", managerStaff);
		model.addAttribute("operateInfo", operateInfo);
		model.addAttribute("historyList", historyList);
		return "modules/allocation/v02/horizontalAllocatedOutStore/pbocOutInStoreWorkflowDetail";
	}
	
	/**
	 * 
	 * Title: getPersonInfo
	 * <p>Description: 交接人员图片显示</p>
	 * @author:     yanbingxu
	 * @param allocateInfo
	 * @param personList
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "getPersonInfo")
	public String getPersonInfo(PbocAllAllocateInfo pbocAllAllocateInfo,
			@RequestParam(value = "personList", required = false) List<String> personList, Model model) {

		List<StoEscortInfo> stoEscortList = Lists.newArrayList();

		for (String stoEscortId : personList) {
			stoEscortList.add(stoEscortInfoService.findByUserId(stoEscortId));
		}

		pbocAllAllocateInfo = pbocAllAllocateInfoService.get(pbocAllAllocateInfo.getAllId());

		model.addAttribute("stoEscortList", stoEscortList);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		return "modules/store/v01/stoEscortInfo/handoverPersonInfo";
	}
}
