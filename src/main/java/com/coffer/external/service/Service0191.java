package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0191
 * <p>
 * Description:
 * </p>
 * 网点接受确认接口
 * 
 * @author liuyaowen
 * @date 2017年8月9日 上午10:41:10
 */
@Component("Service0191")
@Scope("singleton")
public class Service0191 extends HardwardBaseService {
	@Autowired
	private AllocationService service;
	@Autowired
	StoBoxInfoService stoBoxInfoService;
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得参数
		String allId = (String) paramMap.get(Parameter.ALL_ID);
		// 验证机构编号
		if (StringUtils.isBlank(allId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "流水号不可以为空！");
			return gson.toJson(respMap);
		}

		// 通过allID查询任务
		AllAllocateInfo allAllocateInfo = service.getAllocate(allId);
		// 返回boxList
		List<Map<String, Object>> boxList = Lists.newArrayList();
		if (!(allAllocateInfo == null)) {
			if (!Collections3.isEmpty(allAllocateInfo.getAllDetailList())) {
				for (AllAllocateDetail allocateDetail : allAllocateInfo.getAllDetailList()) {
					// 返回结果加入箱袋信息
					Map<String, Object> jsonMap = Maps.newHashMap();
					if (AllocationConstant.Status.Onload.equals(allAllocateInfo.getStatus())
							|| AllocationConstant.Status.HandoverTodo.equals(allAllocateInfo.getStatus())) {
						jsonMap.put(Parameter.BOX_NO_KEY, allocateDetail.getBoxNo());
						jsonMap.put(Parameter.RFID_KEY, allocateDetail.getRfid());
						// 获取箱号对应的网店机构ID
						StoBoxInfo stoboxInfo = stoBoxInfoService.get(allocateDetail.getBoxNo());
						jsonMap.put(Parameter.BOX_TYPE_NAME_KEY,
								DictUtils.getDictLabel(stoboxInfo.getBoxType(), "sto_box_type", null));
						boxList.add(jsonMap);
					}
				}
			}
		}
		respMap.put(Parameter.BOX_LIST_KEY, boxList);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
