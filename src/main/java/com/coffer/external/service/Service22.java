package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service22
 * <p>
 * Description:
 * </p>
 * 
 * @author qipeihong
 * @date 2017年7月13日
 */
@Component("Service22")
@Scope("singleton")
public class Service22 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	@Autowired
	private StoRouteInfoService stoRouteInfoService;

	/**
	 * 22：库外箱袋出入库查询接口
	 * 
	 * @author qipeihong
	 * 
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号
		String versionNo = StringUtils.toString(paramMap.get(Parameter.VERSION_NO_KEY));
		// 金库编号
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		// 出入库类型
		String inoutType = StringUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY));

		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

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
		// 根据条件获取明细
		try {
			// 明细list
			List<AllAllocateInfo> stoOutsideBoxInOrOutDetail = Lists.newArrayList();
			// 按照条件查询库外箱袋出入库信息
			stoOutsideBoxInOrOutDetail = allocationService.stoOutsideBoxInOrOutList(inoutType, officeId);
			List<Map<String, Object>> routeList = Lists.newArrayList();
			if (Collections3.isEmpty(stoOutsideBoxInOrOutDetail)) {
				respMap.put("routeList", routeList);
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
				return gson.toJson(respMap);
			}
			// 箱袋所属机构名称
			String strBoxOfficeName = null;
			for (AllAllocateInfo allInfo : stoOutsideBoxInOrOutDetail) {
				Map<String, Object> routeMap = null;
				List<Map<String, Object>> boxList = null;
				// 取得箱袋列表
				StoRouteInfo storouteInfo = stoRouteInfoService.get(allInfo.getRouteId());
				List<AllAllocateDetail> AllAllocateDetailBoxList = allInfo.getBoxList();
				// 判断流水线路是否在routeList中存在
				int index = getindex(routeList, allInfo.getRouteId());
				// 若不存在，则在routeList中新建一条
				if (index == -1) {
					// 线路Map
					routeMap = Maps.newHashMap();
					// 设置线路ID以及线路名称
					routeMap.put(Parameter.ROUTE_ID_KEY, allInfo.getRouteId());
					// 若线路中存在车辆信息 则将车牌号添加在线路名称后面
					if (storouteInfo.getCarNo() == null || "".equals(storouteInfo.getCarNo())) {
						routeMap.put(Parameter.ROUTE_NAME_KEY, storouteInfo.getRouteName());
					} else {
						routeMap.put(Parameter.ROUTE_NAME_KEY,
								storouteInfo.getRouteName() + Constant.Punctuation.HALF_LEFT_ROUND_BRACKETS
										+ storouteInfo.getCarNo() + Constant.Punctuation.HALF_RIGHT_ROUND_BRACKETS);
					}
					boxList = Lists.newArrayList();
					// 若已存在
				} else {
					routeMap = routeList.get(index);
					boxList = (List<Map<String, Object>>) routeMap.get(Parameter.BOX_LIST_KEY);
					routeList.remove(index);
				}
				for (AllAllocateDetail allocateDetail : AllAllocateDetailBoxList) {
					StoBoxInfo boxInfo = new StoBoxInfo();
					boxInfo.setId(allocateDetail.getBoxNo());
					boxInfo.setRfid(allocateDetail.getRfid());

					// 根据箱号查询箱子
					List<StoBoxInfo> stoboxinfoList = stoBoxInfoService.getBindingBoxInfo(boxInfo);
					if (Collections3.isEmpty(stoboxinfoList)) {
						respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
						respMap.put(Parameter.ERROR_MSG_KEY,
								"RFID为：" + boxInfo.getRfid() + "," + "箱号为：" + boxInfo.getId() + "的箱袋信息有误");
						return gson.toJson(respMap);
					}
					StoBoxInfo stoboxinfo = stoboxinfoList.get(0);
					// 箱袋Map
					Map<String, Object> boxMap = Maps.newHashMap();
					// 设置RFID
					boxMap.put(Parameter.RFID_KEY, stoboxinfo.getRfid());
					// 设置箱号
					boxMap.put(Parameter.BOX_NO_KEY, stoboxinfo.getId());
					// 设置箱袋类型
					boxMap.put(Parameter.BOX_TYPE_NAME_KEY,
							DictUtils.getDictLabel(stoboxinfo.getBoxType(), "sto_box_type", null));
					// 设置箱袋所属机构ID
					boxMap.put(Parameter.BOX_OFFICE_ID, allocateDetail.getBoxOfficeId());
					// 设置箱袋所属机构
					strBoxOfficeName = StringUtils.isBlank(allocateDetail.getBoxOfficeId()) ? ""
							: StoreCommonUtils.getOfficeById(allocateDetail.getBoxOfficeId()).getName();
					boxMap.put(Parameter.BOX_OFFICE_NAME, strBoxOfficeName);
					// 设置出库日期
					boxMap.put(Parameter.OUT_DATE_KEY,
							DateUtils.formatDate(stoboxinfo.getOutDate(), Constant.Dates.FORMATE_YYYY_MM_DD));
					// 设置扫描标识
					boxMap.put(Parameter.SCAN_FLAG_KEY, allocateDetail.getScanFlag());
					boxList.add(boxMap);
					routeMap.put(Parameter.BOX_LIST_KEY, boxList);
				}

				routeList.add(routeMap);
			}
			// 返回结果加入箱袋列表
			respMap.put("routeList", routeList);
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(respMap);
	}

	/**
	 * 判断流水线路是否在routeList中存在
	 */
	private Integer getindex(List<Map<String, Object>> routeList, String routeId) {
		for (Map<String, Object> selectmap : routeList) {
			if (selectmap.values().contains(routeId)) {
				int index = routeList.indexOf(selectmap);
				return index;
			}
		}
		return -1;
	}

}
