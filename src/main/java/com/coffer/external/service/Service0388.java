package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.businesses.modules.store.v01.entity.StoAddCashGroup;
import com.coffer.businesses.modules.store.v01.service.StoAddCashGroupService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0388
 * <p>
 * Description: ATM库外交接任务查询接口
 * </p>
 * 
 * @author sg
 * @date 2017年11月14日
 */
@Component("Service0388")
@Scope("singleton")
public class Service0388 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private AtmPlanInfoService atmPlanInfoService;
	@Autowired
	private StoAddCashGroupService stoAddCashGroupService;

	/**
	 * 
	 * @author sg ATM库外交接任务查询接口
	 * @version 2017-11-14
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 服务代码
		String serviceNo = StringUtils.toString(paramMap.get(Parameter.SERVICE_NO_KEY));
		// 所属机构
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		// 任务类型
		String inoutType = StringUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY));
		// 验证参数
		checkBoxHandoutRegister(paramMap, map);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 查询起始日期 修改人：xl 修改时间：2017-11-27 begin
		String searchDateBegin = StringUtils.toString(paramMap.get(Parameter.SEARCH_DATE_BEGIN_KEY));
		// 查询截止日期
		String searchDateEnd = StringUtils.toString(paramMap.get(Parameter.SEARCH_DATE_END_KEY));
		Date searchDateBeginDate = DateUtils.parseDate(searchDateBegin);
		Date searchDateEndDate = DateUtils.parseDate(searchDateEnd);
		// end
		// 根据机构ID查询相关信息
		Office office = officeService.get(officeId);
		if (office == null) {
			// 机构ID不正确
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "不存在机构ID为：" + officeId + "的数据");
			return setReturnMap(map, serviceNo);
		}

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
		allAllocateInfo.setStatus(AllocationConstant.AtmBusinessStatus.HandoverTodo);
		// 设置起始日期
		allAllocateInfo.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(searchDateBeginDate)));
		// 设置截止日期
		allAllocateInfo.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(searchDateEndDate)));
		// 根据条件找出相应的信息
		List<AllAllocateInfo> list = allocationService.findAtmBoxList(allAllocateInfo);
		// 判断主表中是否存在数据
		// if (Collections3.isEmpty(list)) {
		// map.put(Parameter.RESULT_FLAG_KEY,
		// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		// map.put(Parameter.ERROR_NO_KEY,
		// ExternalConstant.HardwareInterface.ERROR_NO_E99);
		// map.put(Parameter.ERROR_MSG_KEY, "未找到相应信息");
		// return setReturnMap(map, serviceNo);
		// }
		List<Map<String, Object>> resList = Lists.newArrayList();
		// 判断加钞计划是否存在
		List<String> errorAtmPlanList = Lists.newArrayList();
		for (AllAllocateInfo allAllocateInfoList : list) {
			// 加钞计划ID
			String addPlanId = allAllocateInfoList.getRouteId();
			AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
			// 设置加钞计划ID
			atmPlanInfo.setAddPlanId(addPlanId);
			// 根据加钞计划ID查询数据
			List<AtmPlanInfo> atmPlanInfoList = atmPlanInfoService.findList(atmPlanInfo);
			if (Collections3.isEmpty(atmPlanInfoList)) {
				errorAtmPlanList.add(addPlanId);
			} else {
				AtmPlanInfo atmPlanInfos = atmPlanInfoList.get(0);
				// 获得加钞组ID
				String addCashGroupId = atmPlanInfos.getAddCashGroupId();
				// 根据加钞组ID进行查询
				StoAddCashGroup stoAddCashGroup = new StoAddCashGroup();
				stoAddCashGroup.setId(addCashGroupId);
				StoAddCashGroup stoAddCashGroups = stoAddCashGroupService
						.getSingleStoAddCashGroupInfoNoDel(stoAddCashGroup);
				Map<String, Object> mapList = Maps.newHashMap();
				// 判断交接ID是否为空如果为空则不显示信息
				if (!StringUtils.isBlank(allAllocateInfoList.getStoreHandoverId())) {
					// 任务编号
					mapList.put(Parameter.TASK_NO_KEY, allAllocateInfoList.getRouteId());
					// 任务名称
					mapList.put(Parameter.TASK_NAME_KEY, atmPlanInfos.getAddPlanName());
					// 箱袋数量
					mapList.put(Parameter.BOX_NUM_KEY, allAllocateInfoList.getRegisterNumber());
					// 创建时间
					mapList.put(Parameter.CREATE_DATE_KEY, allAllocateInfoList.getCreateDate());
					// 交接ID
					mapList.put(Parameter.HANDOVER_ID_KEY, allAllocateInfoList.getStoreHandoverId());
					// 车辆ID
					mapList.put(Parameter.CAR_ID_KEY, stoAddCashGroups != null ? stoAddCashGroups.getCarId() : "");
					// 车辆名称
					mapList.put(Parameter.CAR_NAME_KEY, stoAddCashGroups != null ? stoAddCashGroups.getCarName() : "");
					// 解款员ID1
					mapList.put(Parameter.ESCORT_ID1_KEY,
							stoAddCashGroups != null ? stoAddCashGroups.getEscortNo1() : "");
					// 解款员姓名1
					mapList.put(Parameter.ESCORT_NAME1_KEY,
							stoAddCashGroups != null ? stoAddCashGroups.getEscortName1() : "");
					// 解款员ID2
					mapList.put(Parameter.ESCORT_ID2_KEY,
							stoAddCashGroups != null ? stoAddCashGroups.getEscortNo2() : "");
					// 解款员姓名2
					mapList.put(Parameter.ESCORT_NAME2_KEY,
							stoAddCashGroups != null ? stoAddCashGroups.getEscortName2() : "");
					resList.add(mapList);
				}

			}
		}
		// 判断加钞计划是否存在
		if (!Collections3.isEmpty(errorAtmPlanList)) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY,
					"加钞计划ID为：" + Collections3.convertToString(errorAtmPlanList, ",") + "加钞计划不存在");
			return setReturnMap(map, serviceNo);
		}
		map.put(Parameter.TASK_LIST_KEY, resList);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(map, serviceNo);
	}

	/**
	 * @author sg
	 * @version 2017-11-13
	 * 
	 * @Description ATM库外交接任务查询接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	private String checkBoxHandoutRegister(Map<String, Object> headInfo, Map<String, Object> map) {
		// 任务类型
		String inoutType = StringUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY));
		// 所属机构
		String officeId = StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY));
		// 开始日期 修改人：xl 修改时间：2017-11-27 begin
		String searchDateBegin = StringUtils.toString(headInfo.get(Parameter.SEARCH_DATE_BEGIN_KEY));
		// 结束日期
		String searchDateEnd = StringUtils.toString(headInfo.get(Parameter.SEARCH_DATE_END_KEY));
		// end
		// 判断机构ID是否为空
		if (StringUtils.isBlank(officeId)) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId");
			return Constant.FAILED;
		}
		// 判断任务类型是否为0、1
		if (!(ExternalConstant.Handover.INOUT_TYPE_OUT.equals(inoutType)
				|| ExternalConstant.Handover.INOUT_TYPE_IN.equals(inoutType))) {
			logger.debug("参数错误--------inoutType:" + CommonUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------inoutType");
			return Constant.FAILED;
		}
		// 验证查询起始日期
		if (StringUtils.isBlank(searchDateBegin)) {
			logger.debug("参数错误--------searchDateBegin");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------searchDateBegin");
			return Constant.FAILED;
		}
		// 验证查询截止日期
		if (StringUtils.isBlank(searchDateEnd)) {
			logger.debug("参数错误--------searchDateEnd");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------searchDateEnd");
			return Constant.FAILED;
		}
		return Constant.SUCCESS;
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @param map
	 * @param serviceNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo) {
		// 版本号
		map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);

		return gson.toJson(map);
	}
}
