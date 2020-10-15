package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service36
* <p>Description: 现金预约查询（列表页面） </p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service36")
@Scope("singleton")
public class Service36 extends HardwardBaseService {
	
	@Autowired
	private AllocationService allocationService;
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 * 36 现金预约查询（列表页面）
	 * @param paramMap 参数
	 * @return 结果信息
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			// 取得查询条件
			AllAllocateInfo allAllocateInfoparam = this.getAllAllocateInfoParamFromMap(paramMap);
			if (allAllocateInfoparam == null) {
				// 参数异常
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			} else {
				// 设置业务种别(现金预约)
				List<String> businessTypeList = Lists.newArrayList();
				businessTypeList.add(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
				allAllocateInfoparam.setBusinessTypes(businessTypeList);
				// 取得预约信息列表
				List<Map<String, Object>> infoList = this.getAllAllocateOrderInfoMap(allAllocateInfoparam);
				// 业务查询列表
				map.put(Parameter.LIST_KEY, infoList);
				// 服务端系统日期
				map.put(Parameter.SERVER_DATE_KEY, DateUtils.formatDate(new Date(), ExternalConstant.DATE_FORMATE_YYYY_MM_DD));
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}

	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  
	 * @param headInfo CS上传信息
	 * @return 列表查询条件
	 */
	private AllAllocateInfo getAllAllocateInfoParamFromMap(Map<String, Object> headInfo) {
		AllAllocateInfo rtnAllAllocateInfo = new AllAllocateInfo();
		// 取得网点机构Id
		if (headInfo.get(Parameter.OFFICE_ID_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("现金预约、上缴查询（列表页面）接口-------- 网点机构Id未指定:" + headInfo.get(Parameter.OFFICE_ID_KEY));
			return null;
		}
		
		// 取得状态
		if (headInfo.get(Parameter.STATUS_KEY) != null && StringUtils.isNotBlank(headInfo.get(Parameter.STATUS_KEY).toString())) {
			List<String> statusList = Lists.newArrayList();
			statusList.add(headInfo.get(Parameter.STATUS_KEY).toString());
			rtnAllAllocateInfo.setStatuses(statusList);
		}
		
		// 取得开始时间
		if (headInfo.get(Parameter.START_DATE_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.START_DATE_KEY).toString())) {
			logger.debug("现金预约、上缴查询（列表页面）接口-------- 开始时间未指定:" + headInfo.get(Parameter.START_DATE_KEY));
			return null;
		}
		Date startDate = DateUtils.parseDate(headInfo.get(Parameter.START_DATE_KEY).toString());
		// 检查开始时间
		if (startDate == null) {
			logger.debug("现金预约、上缴查询（列表页面）接口-------- 开始时间格式不正确(yyyy-MM-dd):" + headInfo.get(Parameter.START_DATE_KEY));
			return null;
		}
		// 取得结束时间
		if (headInfo.get(Parameter.END_DATE_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.END_DATE_KEY).toString())) {
			logger.debug("现金预约、上缴查询（列表页面）接口-------- 结束时间未指定:" + headInfo.get(Parameter.END_DATE_KEY));
			return null;
		}
		Date endDate = DateUtils.parseDate(headInfo.get(Parameter.END_DATE_KEY).toString());
		//检查结束时间
		if (endDate == null) {
			logger.debug("现金预约、上缴查询（列表页面）接口-------- 结束时间格式不正确(yyyy-MM-dd):" + headInfo.get(Parameter.END_DATE_KEY));
			return null;
		}
		
		if (startDate.compareTo(endDate) > 0) {
			logger.debug("现金预约、上缴查询（列表页面）接口-------- 开始日期大于结束日期。开始日期：" + 
						headInfo.get(Parameter.START_DATE_KEY) + ", 结束日期：" + headInfo.get(Parameter.END_DATE_KEY));
			return null;
		}
		
		// 开始时间
		rtnAllAllocateInfo.setCreateTimeStart(startDate);
		rtnAllAllocateInfo.setSearchDateStart(
				DateUtils.formatDate(DateUtils.getDateStart(startDate),	AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 结束时间
		rtnAllAllocateInfo.setCreateTimeEnd(endDate);
		rtnAllAllocateInfo.setSearchDateEnd(
				DateUtils.formatDate(DateUtils.getDateEnd(endDate),	AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		
		Office loginUserOffice = StoreCommonUtils.getOfficeById(headInfo.get(Parameter.OFFICE_ID_KEY).toString());
		// 网点机构ID
		rtnAllAllocateInfo.setrOffice(loginUserOffice);
		
		return rtnAllAllocateInfo;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  取得预约信息列表
	 * @param allAllocateInfoparam 查询参数
	 * @return 预约信息列表
	 */
	private List<Map<String, Object>> getAllAllocateOrderInfoMap(AllAllocateInfo allAllocateInfoparam) {
		// 根据查询条件，取得调拨全部信息
		List<AllAllocateInfo> allocationList = allocationService.findBetweenPageList(allAllocateInfoparam);
		List<Map<String, Object>> infoList = Lists.newArrayList();
		Map<String, Object> allocationMap = null;
		if (!Collections3.isEmpty(allocationList)) {
			for (AllAllocateInfo info : allocationList) {
				allocationMap = Maps.newHashMap();
				// 流水单号
				allocationMap.put(Parameter.SERIALORDER_NO_KEY, info.getAllId());
				// 预约人
				allocationMap.put(Parameter.CREATE_NAME_KEY, info.getCreateName());
				// 预约日期
				allocationMap.put(Parameter.CREATE_DATE_KEY, DateUtils.formatDate(info.getCreateDate(), ExternalConstant.DATE_FORMATE_YYYY_MM_DD));
				// 配款人
				allocationMap.put(Parameter.ACCEPT_NAME_KEY, info.getConfirmName());
				// 配款日期
				allocationMap.put(Parameter.ACCEPT_DATE_KEY, DateUtils.formatDate(info.getConfirmDate(), ExternalConstant.DATE_FORMATE_YYYY_MM_DD));
				// 状态
				allocationMap.put(Parameter.STATUS_KEY, info.getStatus());
				infoList.add(allocationMap);
			}
		}
		
		return infoList;
	}
	
}
