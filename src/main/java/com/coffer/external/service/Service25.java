package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service25
 * <p>
 * Description: 库外交接任务查询接口
 * </p>
 * 
 * @author qipeihong
 * @date 2017年7月17日
 */
@Component("Service25")
@Scope("singleton")
public class Service25 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;

	/**
	 * 25：库外交接任务查询接口
	 * 
	 * @author qipeihong
	 * 
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号
		String versionNo = StringUtils.toString(paramMap.get(Parameter.VERSION_NO_KEY));
		// 金库编号
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		// 出入库类型
		String inoutType = StringUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY));
		// 开始日期 修改人：xp 修改时间：2017-7-30 begin
		String searchDateBegin = StringUtils.toString(paramMap.get(Parameter.SEARCH_DATE_BEGIN_KEY));
		// 结束日期
		String searchDateEnd = StringUtils.toString(paramMap.get(Parameter.SEARCH_DATE_END_KEY));
		// end
		respMap.put(Parameter.VERSION_NO_KEY, versionNo);
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 机构Id和版本号非空校验
		// 机构Id和版本号非空校验
		if (StringUtils.isBlank(versionNo)) {
			logger.debug("参数错误--------versionNo:" + CommonUtils.toString(paramMap.get(Parameter.VERSION_NO_KEY)));
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "参数错误--------versionNo");
			return gson.toJson(respMap);
		}
		if (StringUtils.isBlank(officeId)) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)));
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId");
			return gson.toJson(respMap);
		}
		if (StringUtils.isBlank(inoutType)) {
			logger.debug("参数错误--------inoutType:" + CommonUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY)));
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "参数错误--------inoutType");
			return gson.toJson(respMap);
		}
		if (StringUtils.isBlank(searchDateBegin)) {
			logger.debug("参数错误--------searchDateBegin:"
					+ CommonUtils.toString(paramMap.get(Parameter.SEARCH_DATE_BEGIN_KEY)));
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "参数错误--------searchDateBegin");
			return gson.toJson(respMap);
		}
		if (StringUtils.isBlank(searchDateEnd)) {
			logger.debug(
					"参数错误--------searchDateEnd:" + CommonUtils.toString(paramMap.get(Parameter.SEARCH_DATE_END_KEY)));
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.ERROR_MSG_KEY, "参数错误--------searchDateEnd");
			return gson.toJson(respMap);
		}
		// 根据条件获取明细
		try {
			// 明细list
			List<AllAllocateInfo> stoOutsideHandoverTaskDetail = Lists.newArrayList();
			// 按照条件查询库外交接任务信息
			stoOutsideHandoverTaskDetail = allocationService.stoOutsideHandoverList(officeId, inoutType,
					searchDateBegin, searchDateEnd);
			// jsonList
			List<Map<String, Object>> jsonlist = Lists.newArrayList();
			// 遍历查询结果
			for (AllAllocateInfo allAllocateInfo : stoOutsideHandoverTaskDetail) {
				if (StringUtils.isBlank(allAllocateInfo.getRouteId())) {
					continue;
				}
				Map<String, Object> jsonMap = Maps.newHashMap();
				// add by liuyaowen begin
				List<Map<String, Object>> boxJsonList = Lists.newArrayList();
				for (AllAllocateDetail detail : allAllocateInfo.getAllDetailList()) {
					Map<String, Object> boxJsonMap = Maps.newHashMap();
					boxJsonMap.put("boxNo", detail.getBoxNo());
					boxJsonMap.put("boxTypeName", DictUtils.getDictLabel(detail.getBoxType(), "sto_box_type", null));
					boxJsonMap.put("officeName", allAllocateInfo.getrOfficeName());
					boxJsonMap.put("rfid", detail.getRfid());
					boxJsonList.add(boxJsonMap);
				}
				jsonMap.put("boxList", boxJsonList);
				// end
				// 将数据封装成list
				jsonMap.put("handoverId", allAllocateInfo.getStoreHandoverId());
				jsonMap.put("taskId", allAllocateInfo.getAllId());
				jsonMap.put("routeId", allAllocateInfo.getRouteId());
				jsonMap.put("routeName", allAllocateInfo.getRouteName());
				jsonMap.put("boxNum",
						allAllocateInfo.getAllDetailList() == null ? '0' : allAllocateInfo.getAllDetailList().size());
				if (allAllocateInfo.getEscortNoOneList() != null) {
					// 解款员1信息存在
					jsonMap.put("escortId1", allAllocateInfo.getEscortNoOneList().getEscortId());
					jsonMap.put("idcardNo1", allAllocateInfo.getEscortNoOneList().getIdcardNo());
					jsonMap.put("escortName1", allAllocateInfo.getEscortNoOneList().getEscortName());

				} else {
					// 解款员1信息不存在
					jsonMap.put("escortId1", "");
					jsonMap.put("idcardNo1", "");
					jsonMap.put("escortName1", "");
				}
				if (allAllocateInfo.getEscortNoTwoList() != null) {
					// 解款员2信息存在
					jsonMap.put("escortId2", allAllocateInfo.getEscortNoTwoList().getEscortId());
					jsonMap.put("idcardNo2", allAllocateInfo.getEscortNoTwoList().getIdcardNo());
					jsonMap.put("escortName2", allAllocateInfo.getEscortNoTwoList().getEscortName());

				} else {
					// 解款员2信息不存在
					jsonMap.put("escortId2", "");
					jsonMap.put("idcardNo2", "");
					jsonMap.put("escortName2", "");
				}
				// 创建时间
				jsonMap.put("createTime", allAllocateInfo.getCreateDate());
				jsonlist.add(jsonMap);
			}

			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			respMap.put("taskList", jsonlist);
		} catch (Exception e) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(respMap);
	}

}
