package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0196
 * <p>
 * Description:
 * </p>
 * PDA网点接收同步交接人员信息
 * 
 * @author xp
 * @date 2018年5月14日
 */
@Component("Service0196")
@Scope("singleton")
public class Service0196 extends HardwardBaseService {
	@Autowired
	private AllocationService service;
	@Autowired
	private StoEscortInfoService stoEscortService;

	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 取得参数
		String allId = (String) paramMap.get(Parameter.ALL_ID);
		// 验证流水号
		if (StringUtils.isBlank(allId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "流水号不可以为空！");
			return gson.toJson(respMap);
		}

		// 通过allID查询任务
		AllAllocateInfo allAllocateInfo = service.getAllocate(allId);
		// 返回handOverList
		List<Map<String, Object>> handOverList = Lists.newArrayList();
		if (!(allAllocateInfo == null)) {
			if (!Collections3.isEmpty(allAllocateInfo.getStoreHandover().getDetailList())) {
				int index = 1;
				// 返回结果加入交接
				Map<String, Object> jsonMap = Maps.newHashMap();
				for (AllHandoverDetail handoverDetail : allAllocateInfo.getStoreHandover().getDetailList()) {
					// 如果交接人员操作类型是接收
					if (AllocationConstant.OperationType.ACCEPT.equals(handoverDetail.getOperationType())) {
						StoEscortInfo escort = stoEscortService.get(handoverDetail.getEscortId());
						if (escort.getPhoto() != null && escort.getPhoto().length > 0) {
							jsonMap.put("photo" + index, Encodes.encodeBase64(escort.getPhoto()));
						} else {
							jsonMap.put("photo" + index, null);
						}
						jsonMap.put("escortId" + index, escort.getId());
						jsonMap.put("escortName" + index, escort.getEscortName());
						index++;
					}
				}
				handOverList.add(jsonMap);
			}
		}
		respMap.put("handOverList", handOverList);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
