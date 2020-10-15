package com.coffer.core.modules.act.listener;

import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;

public class PbocActTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@Override
	public void notify(DelegateTask delegateTask) {

		String allId = (String) delegateTask.getVariable("allId");
		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(allId);
		String targetOfficeId = "";

		// 根据不同业务类型和状态判断消息目标机构
		switch (pbocAllAllocateInfo.getStatus()) {
			//待审批（接收机构）
		case AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS:
			targetOfficeId = pbocAllAllocateInfo.getAoffice().getId();
			break;
			//待配款（申请下拨业务为接收机构，复点-调拨出库-销毁出库业务为登记机构）
		case AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS:
			if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())) {
				targetOfficeId = pbocAllAllocateInfo.getAoffice().getId();
			} else {
				targetOfficeId = pbocAllAllocateInfo.getRoffice().getId();
			}
			break;
			//驳回（登记机构）
		case AllocationConstant.PbocOrderStatus.REJECT_STATUS:
			targetOfficeId = pbocAllAllocateInfo.getRoffice().getId();
			break;
			//待出入库（申请下拨-申请上缴-代理上缴为接收机构，销毁出库-复点-调拨出库-调拨入库为登记机构）
		case AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_STATUS:
			if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())) {
				targetOfficeId = pbocAllAllocateInfo.getAoffice().getId();
			} else {
				targetOfficeId = pbocAllAllocateInfo.getRoffice().getId();
			}
			break;
			//待交接（申请下拨-申请上缴-代理上缴为接收机构，销毁出库-复点-调拨出库-调拨入库为登记机构）
		case AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_HANDOVER_STATUS:
			if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())) {
				targetOfficeId = pbocAllAllocateInfo.getAoffice().getId();
			} else {
				targetOfficeId = pbocAllAllocateInfo.getRoffice().getId();
			}
			break;
			//清分中（登记机构）
		case AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS:
			targetOfficeId = pbocAllAllocateInfo.getRoffice().getId();
			break;
			//待入库交接（登记机构）
		case AllocationConstant.PbocOrderStatus.RecountingStatus.TO_IN_STORE_HANDOVER_STATUS:
			targetOfficeId = pbocAllAllocateInfo.getRoffice().getId();
			break;
			//完成（申请下拨-申请上缴-代理上缴为登记机构，销毁出库-复点-调拨出库-调拨入库为接收机构）
		case AllocationConstant.PbocOrderStatus.FINISH_STATUS:
			if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(pbocAllAllocateInfo.getBusinessType())) {
				targetOfficeId = pbocAllAllocateInfo.getRoffice().getId();
			} else {
				targetOfficeId = pbocAllAllocateInfo.getAoffice().getId();
			}
			break;
		}
		// 消息添加至队列
		List<String> params = Lists.newArrayList();
		params.add(pbocAllAllocateInfo.getRoffice().getName());
		params.add(allId);
		SysCommonUtils.addMessageQueue(Constant.MessageType.ALLOCATION, pbocAllAllocateInfo.getBusinessType(),
				pbocAllAllocateInfo.getStatus(), params, targetOfficeId, (User) delegateTask.getVariable("createUser"));
	}
}
