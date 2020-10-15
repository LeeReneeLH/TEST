package com.coffer.businesses.modules.allocation.v02.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.WorkFlowProperty;
import com.coffer.businesses.modules.allocation.v02.dao.PbocAllAllocateInfoHistoryDao;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocWorkflowInfo;
import com.coffer.core.common.service.BaseService;

/**
 * 
 * Title: PbocAllAllocateInfoHistoryService
 * <p>
 * Description: 人行调拨工作流Service
 * </p>
 * 
 * @author yanbingxu
 * @date 2018年3月26日 上午9:57:45
 */
@Service
@Transactional(readOnly = true)
public class PbocAllAllocateInfoHistoryService extends BaseService {

	@Autowired
	private PbocAllAllocateInfoHistoryDao pbocAllAllocateInfoHistoryDao;

	/**
	 * 
	 * Title: showOperateHistory
	 * <p>
	 * Description: 单一操作历史查询
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param pbocAllAllocateInfo
	 * @return List<PbocAllAllocateInfo> 返回类型
	 */
	@Transactional(readOnly = true)
	public List<PbocAllAllocateInfo> showOperateHistory(PbocAllAllocateInfo pbocAllAllocateInfo) {
		return pbocAllAllocateInfoHistoryDao.showOperateHistory(pbocAllAllocateInfo);
	}

	/**
	 * 
	 * Title: findAllHistory
	 * <p>
	 * Description: 全部操作历史查询
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param pbocAllAllocateInfo
	 * @return List<PbocAllAllocateInfo> 返回类型
	 */
	@Transactional(readOnly = true)
	public List<PbocAllAllocateInfo> findAllHistory(PbocAllAllocateInfo pbocAllAllocateInfo) {
		return pbocAllAllocateInfoHistoryDao.findStatus(pbocAllAllocateInfo);
	}

	/**
	 * 
	 * Title: findDetail
	 * <p>
	 * Description: 详细信息查询
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param pbocAllAllocateInfo
	 * @return PbocAllAllocateInfo 返回类型
	 */
	@Transactional(readOnly = true)
	public PbocAllAllocateInfo findDetail(PbocAllAllocateInfo pbocAllAllocateInfo) {
		return pbocAllAllocateInfoHistoryDao.findDetail(pbocAllAllocateInfo);
	}

	/**
	 * 
	 * Title: findStatus
	 * <p>
	 * Description: 流程图状态查询
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param pbocAllAllocateInfo
	 * @return PbocWorkflowInfo 返回类型
	 */
	@Transactional(readOnly = true)
	public PbocWorkflowInfo findStatus(PbocAllAllocateInfo pbocAllAllocateInfo) {
		PbocWorkflowInfo pbocWorkflowInfo = new PbocWorkflowInfo();
		// 将流水ID设置到工作流中
		pbocWorkflowInfo.setAllId(pbocAllAllocateInfo.getAllId());
		// 查询业务的工作流
		List<PbocAllAllocateInfo> pbocAllAllocateInfoList = pbocAllAllocateInfoHistoryDao
				.findStatus(pbocAllAllocateInfo);
		// 过滤数据
		for (PbocAllAllocateInfo info : pbocAllAllocateInfoList) {
			WorkFlowProperty status = new WorkFlowProperty();
			// 图片颜色
			status.setColorFlag(true);
			// 设置当前流水状态
			status.setStatus(info.getStatus());
			switch (info.getStatus()) {
			// 待审批
			case AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				pbocWorkflowInfo.setToApproval(status);
				break;
			// 待配款
			case AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				pbocWorkflowInfo.setToQuota(status);
				break;
			// 驳回
			case AllocationConstant.PbocOrderStatus.REJECT_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				pbocWorkflowInfo.setReject(status);
				pbocWorkflowInfo.setToInOutStore(status);
				break;
			// 待入(出)库
			case AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				pbocWorkflowInfo.setToInOutStore(status);
				break;
			// 申请下拨业务为待接收，复点业务为清分中
			case AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_ACCEPT_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(info.getBusinessType())) {
					pbocWorkflowInfo.setClearing(status);
				}
				if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(info.getBusinessType())) {
					pbocWorkflowInfo.setToAccept(status);
				}
				break;
			// 待交接
			case AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				pbocWorkflowInfo.setToHandover(status);
				break;
			// 待入库交接
			case AllocationConstant.PbocOrderStatus.RecountingStatus.TO_IN_STORE_HANDOVER_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				pbocWorkflowInfo.setToInStoreHandover(status);
				break;
			// 完成
			case AllocationConstant.PbocOrderStatus.FINISH_STATUS:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				pbocWorkflowInfo.setFinish(status);
				break;
			}
		}
		return pbocWorkflowInfo;
	}
}
