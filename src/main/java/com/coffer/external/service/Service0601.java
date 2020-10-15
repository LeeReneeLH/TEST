package com.coffer.external.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoCarInfoService;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0601
 * <p>
 * Description: 安防技控获取线路接口
 * </p>
 * 
 * @author XiaoLiang
 * @date 2018年9月6日
 */
@Component("Service0601")
@Scope("singleton")
public class Service0601 extends HardwardBaseService {

	@Autowired
	private StoRouteInfoService stoRouteInfoservice;
	@Autowired
	private StoCarInfoService stoCarInfoService;
	@Autowired
	private AllocationService allocationService;

	/**
	 * 安防技控获取线路
	 * 
	 * @author XL
	 * @version 2018-09-06
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> resultmap = Maps.newHashMap();
		// 版本号
		String versionNo = (String) paramMap.get(Parameter.VERSION_NO_KEY);
		// 服务代码
		String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
		// 调拨信息
		AllAllocateInfo allocateInfo = new AllAllocateInfo();
		// 设置为当天
		Date currentDate = new Date();
		allocateInfo.setCreateTimeStart(currentDate);
		allocateInfo.setCreateTimeEnd(currentDate);
		// 调拨种别(上缴和下拨)
		allocateInfo.setBusinessTypes(Arrays.asList(new String[] { AllocationConstant.BusinessType.OutBank_Cash_HandIn,
				AllocationConstant.BusinessType.OutBank_Cash_Reservation }));
		// 状态列表
		allocateInfo.setStatuses(Arrays
				.asList(new String[] { AllocationConstant.Status.Onload, AllocationConstant.Status.HandoverTodo }));
		// 当日所有调拨任务（常规和临时）
		List<AllAllocateInfo> allAllocateInfoList = allocationService.findAllocationAndTempList(allocateInfo);
		// 过滤掉上缴业务中已扫描的流水
		List<AllAllocateInfo> allocationFilter = Lists.newArrayList();
		for (AllAllocateInfo allAllocateInfo : allAllocateInfoList) {
			if (!(AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfo.getBusinessType())
					&& AllocationConstant.Status.HandoverTodo.equals(allAllocateInfo.getStatus()))) {
				allocationFilter.add(allAllocateInfo);
			}
		}
		allAllocateInfoList = allocationFilter;
		// 线路列表
		List<Map<String, Object>> routeList = Lists.newArrayList();
		// 当日常规任务
		if (!Collections3.isEmpty(allAllocateInfoList)) {
			// 线路id列表
			List<String> routeIdList = Lists.newArrayList();
			for (AllAllocateInfo allAllocateInfo : allAllocateInfoList) {
				// 线路信息
				Map<String, Object> routeMap = Maps.newHashMap();
				// 线路名称
				String routeName = null;
				// 车牌编号
				String carNo = null;
				// 车辆列表
				List<Map<String, Object>> carList = Lists.newArrayList();
				// 押运人员列表
				List<Map<String, Object>> escortList = Lists.newArrayList();
				// 常规线路
				if (AllocationConstant.TaskType.ROUTINET_TASK.equals(allAllocateInfo.getTaskType())) {
					// 线路id
					String routeId = allAllocateInfo.getRouteId();
					// 是否存在线路id列表中
					if (routeIdList.contains(routeId)) {
						continue;
					}
					routeIdList.add(routeId);
					// 获取线路信息
					StoRouteInfo stoRouteInfo = stoRouteInfoservice.get(routeId);
					// 线路名称
					routeName = stoRouteInfo.getRouteName();
					// 车牌编号
					carNo = stoRouteInfo.getCarNo();
					// 人员信息列表
					List<StoEscortInfo> escortInfoList = Arrays.asList(
							new StoEscortInfo[] { stoRouteInfo.getEscortInfo1(), stoRouteInfo.getEscortInfo2() });
					for (StoEscortInfo stoEscortInfo : escortInfoList) {
						// 线路绑定押运人员
						if (stoEscortInfo != null && StringUtils.isNotBlank(stoEscortInfo.getEscortName())) {
							Map<String, Object> escortMap = Maps.newHashMap();
							// 押运人员姓名
							escortMap.put(Parameter.ESCORT_NAME_KEY, stoEscortInfo.getEscortName());
							escortList.add(escortMap);
						}
					}
				}
				// 临时线路
				if (AllocationConstant.TaskType.TEMPORARY_TASK.equals(allAllocateInfo.getTaskType())) {
					// 线路名称
					routeName = allAllocateInfo.getRouteName();
					// 车牌编号
					carNo = allAllocateInfo.getCarNo();
				}
				// 线路绑定车辆
				if (StringUtils.isNotBlank(carNo)) {
					StoCarInfo stoCarInfo = stoCarInfoService.getByCarNo(carNo);
					Map<String, Object> carMap = Maps.newHashMap();
					carMap.put(Parameter.CAR_NO_KEY, stoCarInfo.getCarNo());
					carMap.put(Parameter.CAR_COLOR_KEY, stoCarInfo.getCarColor());
					carMap.put(Parameter.CAR_TYPE_KEY, stoCarInfo.getCarType());
					carList.add(carMap);
				}
				routeMap.put(Parameter.ROUTE_NAME_KEY, routeName);
				routeMap.put(Parameter.ESCORT_LIST_KEY, escortList);
				routeMap.put(Parameter.CAR_LIST_KEY, carList);
				routeList.add(routeMap);
			}
		}
		resultmap.put(Parameter.ROUTE_LIST_KEY, routeList);
		resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(resultmap, serviceNo, versionNo);
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @author XL
	 * @version 2018-09-06
	 * @param map
	 * @param serviceNo
	 * @param versionNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo, String versionNo) {
		map.put(Parameter.VERSION_NO_KEY, versionNo);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		return gson.toJson(map);
	}
}