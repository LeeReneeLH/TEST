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
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0186
 * <p>
 * Description:
 * </p>
 * 
 * @author liuyaowen
 * @date 2017年8月9日 上午10:41:10
 */
@Component("Service0186")
@Scope("singleton")
public class Service0186 extends HardwardBaseService {
	@Autowired
	private AllocationService service;

	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得参数
		String officeId = (String) paramMap.get(Parameter.OFFICE_ID_KEY);
		// 验证机构编号
		if (StringUtils.isBlank(officeId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "机构编号不可以为空！");
			return gson.toJson(respMap);
		}
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		Office office = new Office();
		office.setId(officeId);
		allAllocateInfo.setrOffice(office);
		List<String> statusList = Lists.newArrayList();
		statusList.add(AllocationConstant.Status.BetweenConfirm);
		statusList.add(AllocationConstant.Status.Onload);
		allAllocateInfo.setStatuses(statusList);
		// 设置当天开始时间
		allAllocateInfo.setCreateTimeStart(new Date());
		allAllocateInfo
				.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allAllocateInfo.getCreateTimeStart()),
						AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 设置当天结束时间
		allAllocateInfo.setCreateTimeEnd(new Date());
		allAllocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allAllocateInfo.getCreateTimeEnd()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询该机构所有临时任务
		List<AllAllocateInfo> list = service.findTempAllocateInfoList(allAllocateInfo);
		List<Map<String, Object>> allIdList = Lists.newArrayList();
		for (AllAllocateInfo info : list) {
			Map<String, Object> listmap = new HashMap<>();
			listmap.put(Parameter.ALL_ID, info.getAllId());
			listmap.put(Parameter.TEMP_ROUTE_NAME, info.getRouteName());
			allIdList.add(listmap);
		}
		respMap.put(Parameter.ALL_ID_LIST, allIdList);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
