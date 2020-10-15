package com.coffer.external.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service27
* <p>Description: 库间现金出入库查询&确认接口 </p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service27")
@Scope("singleton")
public class Service27 extends HardwardBaseService {
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AllocationService allocationService;
	
	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月13日
	 * 库间现金出入库查询&确认接口27
	 *  
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		try {
			String officeId="";
			String boxNo="";
			String inoutType="";
			// 验证传递参数
			if (paramMap.get("officeId") == null || StringUtils.isBlank(paramMap.get("officeId").toString())
					|| paramMap.get("boxNo") == null || StringUtils.isBlank(paramMap.get("boxNo").toString())
					|| paramMap.get("inoutType") == null || StringUtils.isBlank(paramMap.get("inoutType").toString())
					|| paramMap.get("userId") == null || StringUtils.isBlank(paramMap.get("userId").toString())) {
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return gson.toJson(map);
			} else {
				officeId = paramMap.get("officeId").toString();
				boxNo = paramMap.get("boxNo").toString();
				inoutType = paramMap.get("inoutType").toString();
			}
			// 取得操作用户信息
			User loginUser = systemService.getUser(paramMap.get("userId").toString());
			
			//根据输入条件查询库间调拨详细信息
			AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
			allAllocateInfo.setStatus(AllocationConstant.Status.BetweenConfirm);
			allAllocateInfo.setrOffice(new Office(officeId));
			//allAllocateInfo.setInoutType(inoutType);
			allAllocateInfo.setBoxNo(boxNo);
			String[] types = {AllocationConstant.BusinessType.Between_Cash,
					AllocationConstant.BusinessType.Between_Clear,
					AllocationConstant.BusinessType.Between_ATM_Add,
					AllocationConstant.BusinessType.Between_ATM_Clear};
			List<String> typeList = Arrays.asList(types);
			allAllocateInfo.setBusinessTypes(typeList);
			List<AllAllocateInfo> allAllocateList = allocationService.findAllocation(allAllocateInfo);
			if(allAllocateList!=null && allAllocateList.size()>0){
				AllAllocateInfo allocateInfo = allAllocateList.get(0);
				//allocateInfo.setInoutType(inoutType);
				// 设定操作用户信息
				allocateInfo.setLoginUser(loginUser);
				//更新库间调拨状态并生成交接任务
				updateStatusAndHandover(allocateInfo);
				//封装数据
				map.put("serialorderNo", allocateInfo.getAllId());
				map.put("boxNo", boxNo);
				map.put("amount", allocateInfo.getRegisterAmount());
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}else{
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E99);
				return gson.toJson(map);
			}
			
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		return gson.toJson(map);
	}
	
	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月14日
	 * 更新库间调拨状态并生成交接任务(接口27)
	 *
	 */
	@Transactional(readOnly = false)
	private void updateStatusAndHandover(AllAllocateInfo allocateInfo) {
		//更新库间调拨状态
		AllAllocateDetail allocateDetail = new AllAllocateDetail();
		allocateDetail.setAllId(allocateInfo.getAllId());
		allocateDetail.setScanFlag(AllocationConstant.ScanFlag.Scan);
//		allocateDetail.setPlace(AllocationConstant.InoutType.Out.equals(allocateInfo.getInoutType())?
//				AllocationConstant.Place.ClassficationRoom:AllocationConstant.Place.StoreRoom);
		allocationService.updateAllocateDetailStatus(allocateDetail);
		
		//生成交接任务
//		AllHandoverInfo allHandoverInfo = new AllHandoverInfo();
//		allHandoverInfo.setHandoverId(UUID.randomUUID().toString());
//		allHandoverInfo.setAllocationInfo(allocateInfo);
//		allHandoverInfo.setBusinessType(allocateInfo.getBusinessType());
//		allHandoverInfo.setInoutType(allocateInfo.getInoutType());
//		allHandoverInfo.setHandoverStatus(AllocationConstant.HandoverPlace.Store);
//		allHandoverInfo.setCreateDate(new Date());
//		allHandoverInfo.setDelFlag(Constant.deleteFlag.Valid);
//		allocationService.insertHandoverInfo(allHandoverInfo);
		
		// 更新调拨主表状态
		AllAllocateInfo updateAllocateCondition = new AllAllocateInfo();
		// 设定流水单号
		updateAllocateCondition.setAllId(allocateInfo.getAllId());
		// 设定状态：待交接
		updateAllocateCondition.setStatus(AllocationConstant.Status.HandoverTodo);
		// 设定更新者ID
		updateAllocateCondition.setUpdateBy(allocateInfo.getLoginUser());
		// 设定更新日期
		updateAllocateCondition.setUpdateDate(new Date());
		// 设定更新者名称
		updateAllocateCondition.setUpdateName(allocateInfo.getLoginUser().getName());
		allocationService.updateAllocateInfoStatus(updateAllocateCondition);
	}
}
