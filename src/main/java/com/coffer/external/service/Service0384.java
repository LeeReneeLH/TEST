package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0384
 * <p>
 * Description: ATM库外清分出入库查询接口
 * </p>
 * 
 * @author sg
 * @date 2017年11月10日
 */
@Component("Service0384")
@Scope("singleton")
public class Service0384 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private AtmPlanInfoService atmPlanInfoService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;

	/**
	 * 
	 * @author sg ATM库外清分出入库查询接口
	 * @version 2017-11-10
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 业务类型
		String inoutType = StringUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY));
		// 所属机构
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		// 机构Id、业务类型非空校验
		if (StringUtils.isBlank(officeId)) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId");
			return gson.toJson(map);
		}
		// 判断钞箱出入库类型是否为0、1
		if (!(ExternalConstant.Handover.INOUT_TYPE_OUT.equals(inoutType)
				|| ExternalConstant.Handover.INOUT_TYPE_IN.equals(inoutType))) {
			logger.debug("参数错误--------inoutType:" + CommonUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------inoutType");
			return gson.toJson(map);
		}
		// 根据机构ID查询相关信息
		Office office = officeService.get(officeId);
		if (office != null) {
			// 查询条件封装
			AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
			// 设置机构
			Office rOffice = new Office();
			rOffice.setId(office.getId());
			allAllocateInfo.setrOffice(rOffice);
			// 设置业务类型为出库
			if (ExternalConstant.Handover.INOUT_TYPE_OUT.equals(inoutType)) {
				allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDOUT);
			}
			// 设置业务类型为入库
			if (ExternalConstant.Handover.INOUT_TYPE_IN.equals(inoutType)) {
				allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDIN);
			}
			// 调拨状态
			allAllocateInfo.setStatus(AllocationConstant.Status.Register);
			List<AllAllocateInfo> list = allocationService.findAtmBoxList(allAllocateInfo);
			List<Map<String, Object>> jsonslist = Lists.newArrayList();
			// 判断箱子是否有问题
			List<String> errorBoxList = Lists.newArrayList();
			for (AllAllocateInfo allAllocateInfoSearch : list) {
				Map<String, Object> jsonsMap = Maps.newHashMap();
				// 设置加钞计划ID
				jsonsMap.put(Parameter.TASK_NO_KEY, allAllocateInfoSearch.getRouteId());
				// 获取任务名称
				AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
				atmPlanInfo.setAddPlanId(allAllocateInfoSearch.getRouteId());
				List<AtmPlanInfo> atmPlanInfoList = atmPlanInfoService.findAddPlanList(atmPlanInfo);
				if (!Collections3.isEmpty(atmPlanInfoList)) {
					atmPlanInfo = atmPlanInfoList.get(0);
					// 计划计划Name
					jsonsMap.put(Parameter.TASK_NAME_KEY, atmPlanInfo.getAddPlanName());
				} else {
					jsonsMap.put(Parameter.TASK_NAME_KEY, "");
				}
				List<AllAllocateDetail> detailList = allAllocateInfoSearch.getAllDetailList();
				if (Collections3.isEmpty(detailList)) {
					// 箱袋数量
					jsonsMap.put(Parameter.BOX_AMOUNT_KEY, 0);
					// 箱袋List
					jsonsMap.put(Parameter.BOX_LIST_KEY, detailList);
				} else {
					List<Map<String, Object>> jsonlist = Lists.newArrayList();
					for (AllAllocateDetail detailTemp : detailList) {
						Map<String, Object> jsonMap = Maps.newHashMap();
						// 箱袋编号
						jsonMap.put(Parameter.BOX_NO_KEY, detailTemp.getBoxNo());
						// 根据箱号查询箱子信息
						StoBoxInfo stoBoxInfo = stoBoxInfoService.get(detailTemp.getBoxNo());
						if (stoBoxInfo == null) {
							errorBoxList.add(detailTemp.getBoxNo());
						} else {
							// 箱袋状态
							jsonMap.put(Parameter.BOX_STATUS_KEY, stoBoxInfo.getBoxStatus());
							// rfid
							jsonMap.put(Parameter.STORE_ESCORT_RFID, detailTemp.getRfid());
							// 追加钞箱类型名称 LLF 20151124 begin
							jsonMap.put(Parameter.ATM_MOD_NAME,
									stoBoxInfo.getAtmBoxMod() != null ? stoBoxInfo.getAtmBoxMod().getModName() : "");
							// end
							// 扫描状态
							jsonMap.put(Parameter.SCAN_FLAG_KEY, detailTemp.getScanFlag());
							jsonlist.add(jsonMap);
						}
					}
					// 箱袋数量
					jsonsMap.put(Parameter.BOX_AMOUNT_KEY, detailList.size());
					// 箱袋List
					jsonsMap.put(Parameter.BOX_LIST_KEY, jsonlist);
				}
				jsonslist.add(jsonsMap);
			}
			if (!Collections3.isEmpty(errorBoxList)) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E24);
				map.put(Parameter.ERROR_MSG_KEY,
						"箱号boxNo为:" + Collections3.convertToString(errorBoxList, ",") + "不存在数据");
				return gson.toJson(map);
			}
			map.put(Parameter.LIST_KEY, jsonslist);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} else {
			// 机构ID不正确
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E24);
			map.put(Parameter.ERROR_MSG_KEY, "机构ID不正确");
		}
		return gson.toJson(map);
	}

}
