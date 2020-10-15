package com.coffer.businesses.modules.allocation.v01.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.dao.AllAllocateInfoHistoryDao;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.WorkFlowInfo;
import com.coffer.businesses.modules.allocation.v01.entity.WorkFlowProperty;
import com.coffer.core.common.service.BaseService;
import com.google.common.collect.Lists;

/**
 * 上缴业务工作流
 * 
 * @author xp
 * @version 2017-09-07
 */
@Service
@Transactional(readOnly = true)
public class HandInWorkFlowService extends BaseService {
	@Autowired
	private AllAllocateInfoHistoryDao allAllocateInfoHistoryDao;

	/**
	 * 查询上缴业务各阶段状态信息
	 * 
	 * @author xp
	 * @version 2017年9月7日
	 * @param allAllocateInfo
	 * @return
	 */
	@Transactional(readOnly = true)
	public WorkFlowInfo findStatus(AllAllocateInfo allAllocateInfo) {
		WorkFlowInfo workFlowInfo = new WorkFlowInfo();
		// 将流水ID设置到工作流中
		workFlowInfo.setAllId(allAllocateInfo.getAllId());
		// 查询业务的工作流
		List<AllAllocateInfo> allAllocateInfoList = allAllocateInfoHistoryDao.findStatus(allAllocateInfo);
		// 计数器
		int index = 0;
		// 状态列表
		List<String> statusList = Lists.newArrayList();
		// 获取状态列表
		for (AllAllocateInfo history : allAllocateInfoList) {
			statusList.add(history.getStatus());
		}
		// 过滤数据
		for (AllAllocateInfo handIn : allAllocateInfoList) {
			WorkFlowProperty status = new WorkFlowProperty();
			// 图片颜色
			status.setColorFlag(true);
			// 设置当前流水状态
			status.setStatus(handIn.getStatus());
			switch (handIn.getStatus()) {
			case AllocationConstant.Status.Register:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				workFlowInfo.setRegister(status);
				break;
			case AllocationConstant.Status.BetweenConfirm:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				workFlowInfo.setConfirm(status);
				break;
			case AllocationConstant.Status.Onload:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 临时线路状态设置
				if (handIn.getTempFlag().equals(AllocationConstant.TaskType.TEMPORARY_TASK)) {
					status.setShowTempFlag(true);
					workFlowInfo.setPackScan(status);
				}
				// 设置当前工作流对应每点的对象
				if (handIn.getTempFlag().equals(AllocationConstant.TaskType.ROUTINET_TASK)) {
					workFlowInfo.setOnload(status);
				}
				break;
			case AllocationConstant.Status.CashOrderQuotaYes:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				workFlowInfo.setPackScan(status);
				break;
			case AllocationConstant.Status.CANCEL_STATUS:
				// 上缴流程
				if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfo.getBusinessType())) {
					// 撤回标识
					status.setShowCancelFlag(true);
					// 设置当前工作流对应每点的对象
					workFlowInfo.setOnload(status);
				}
				// 下拨流程
				if (AllocationConstant.BusinessType.OutBank_Cash_Reservation
						.equals(allAllocateInfo.getBusinessType())) {
					if (allAllocateInfoList.size() > 1 && index > 0) {
						if (AllocationConstant.Status.CashOrderQuotaYes
								.equals(allAllocateInfoList.get(index - 1).getStatus())) {
							workFlowInfo.getPackScan().setShowCancelFlag(true);
						} else {
							workFlowInfo.getConfirm().setShowCancelFlag(true);
						}
					}
				}
				break;
			case AllocationConstant.Status.HandoverTodo:
				// 上缴流程
				if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfo.getBusinessType())) {
					// 未交接标识
					status.setShowHandoverFlag(true);
					// 执行标识(√ x)
					status.setExcuteFlag(true);
					// 设置当前工作流对应每点的对象
					workFlowInfo.setDoorScan(status);
				}
				// 下拨流程
				if (AllocationConstant.BusinessType.OutBank_Cash_Reservation
						.equals(allAllocateInfo.getBusinessType())) {
					if (statusList.contains(AllocationConstant.Status.HandoverTodo)
							&& !statusList.contains(AllocationConstant.Status.Onload)) {
						status.setShowHandoverFlag(true);
					}
					// 执行标识(√ x)
					status.setExcuteFlag(true);
					workFlowInfo.setDoorScan(status);
				}
				break;
			case AllocationConstant.Status.Finish:
				// 执行标识(√ x)
				status.setExcuteFlag(true);
				// 设置当前工作流对应每点的对象
				workFlowInfo.setFinish(status);
				break;
			}
			index++;
		}
		return workFlowInfo;
	}

	/**
	 * 查询上缴过程每个阶段的人员，时间，物品等信息
	 * 
	 * @author xp
	 * @version 2017年9月8日
	 * @param allAllocateInfo
	 * @return
	 */
	@Transactional(readOnly = true)
	public AllAllocateInfo findDetail(AllAllocateInfo allAllocateInfo) {
		return allAllocateInfoHistoryDao.findDetail(allAllocateInfo);
	}

	/**
	 * 查询调拨历史
	 * 
	 * @author xp
	 * @version 2017年9月13日
	 * @param allAllocateInfo
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AllAllocateInfo> findAllAllocateHistory(AllAllocateInfo allAllocateInfo) {
		// 查询上缴业务的工作流
		return allAllocateInfoHistoryDao.findStatus(allAllocateInfo);
	}
	
	/**
	 * 查询历史操作记录
	 * 
	 * @author yanbingxu
	 * @version 2017-09-06
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<AllAllocateInfo> showOperateHistory(AllAllocateInfo allAllocateInfo) {
		return allAllocateInfoHistoryDao.showOperateHistory(allAllocateInfo);
	}
}
