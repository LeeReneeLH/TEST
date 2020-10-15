package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.atm.ATMConstant;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingDetail;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmBindingInfoService;
import com.coffer.businesses.modules.atm.v01.service.AtmInfoMaintainService;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0382
 * <p>
 * Description: PDA上传加钞绑定关系接口
 * </p>
 * 
 * @author xiaoliang
 * @date 2017年11月09日
 */
@Component("Service0382")
@Scope("singleton")
public class Service0382 extends HardwardBaseService {

	@Autowired
	private AtmPlanInfoService atmPlanInfoService;

	@Autowired
	private AtmBindingInfoService atmBindingInfoService;

	@Autowired
	private AllocationService allocationService;

	// @Autowired
	// private StoBoxInfoService stoBoxInfoService;

	@Autowired
	private AtmInfoMaintainService atmInfoMaintainService;

	/**
	 * ATM机与钞箱绑定关系
	 * 
	 * @author xiaoliang
	 * @version 2017年11月09日
	 * @param paramMap
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	@SuppressWarnings("unchecked")
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> resultmap = Maps.newHashMap();
		// 版本号
		String versionNo = (String) paramMap.get(Parameter.VERSION_NO_KEY);
		// 服务代码
		String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
		// 验证参数
		String paraCheckResult = validateParam(paramMap, resultmap);
		// 验证失败的场合，退出
		if (Constant.FAILED.equals(paraCheckResult)) {
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return setReturnMap(resultmap, serviceNo, versionNo);
		}
		// 用户编号
		String userId = (String) paramMap.get(Parameter.USER_ID_KEY);
		// 用户姓名
		String userName = (String) paramMap.get(Parameter.USER_NAME_KEY);
		// 登陆用户
		User user = UserUtils.get(userId);
		// 绑定关系列表
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Parameter.LIST_KEY);
		// 加钞计划Id
		String addPlanId = "";
		for (Map<String, Object> map : list) {
			// 加钞计划Id
			addPlanId = StringUtils.toString(map.get(Parameter.ADD_PLAN_ID_KEY));
			// ATM机编号
			String atmNo = getAtmNo(map);
			// 钞箱列表
			List<Map<String, Object>> detailList = (List<Map<String, Object>>) map.get(Parameter.BOX_LIST_KEY);
			if (Collections3.isEmpty(detailList)) {
				detailList = Lists.newArrayList();
			}
			AtmBindingInfo atmBindingInfo = new AtmBindingInfo();
			// 获取同步的加钞计划
			List<Map<String, Object>> planDetail = atmPlanInfoService.getPDAPlanDetail(addPlanId);
			String atmAccount = "";
			// 获取柜员编号
			for (Map<String, Object> itemMap : planDetail) {
				if (atmNo.equals(itemMap.get(Parameter.ATMNO_KEY))) {
					atmAccount = StringUtils.toString(itemMap.get(Parameter.ATMACCOUNT_KEY));
				}
			}
			if (StringUtils.isNotBlank(atmAccount)) {
				atmBindingInfo.setAtmAccount(atmAccount);
			}
			// 设置押运人员
			if (StringUtils.isNotBlank(StringUtils.toString(map.get(Parameter.ESCORT_ID1_KEY)))) {
				String escort1 = (String) map.get(Parameter.ESCORT_ID1_KEY);
				atmBindingInfo.setEscort1By(escort1);
				atmBindingInfo.setEscort1Name(StoreCommonUtils.getEscortById(escort1).getEscortName());
			}
			if (StringUtils.isNotBlank(StringUtils.toString(map.get(Parameter.ESCORT_ID2_KEY)))) {
				String escort2 = (String) map.get(Parameter.ESCORT_ID2_KEY);
				atmBindingInfo.setEscort2By(escort2);
				atmBindingInfo.setEscort2Name(StoreCommonUtils.getEscortById(escort2).getEscortName());
			}
			// 设置加钞计划Id
			atmBindingInfo.setAddPlanId(addPlanId);
			// 未清点的
			atmBindingInfo.setAtmNo(atmNo);
			List<AtmBindingInfo> AtmBindingInfoList = atmBindingInfoService.findList(atmBindingInfo);
			// 重复绑定
			if (!Collections3.isEmpty(AtmBindingInfoList)) {
				continue;
			} else {
				// 设置创建更新信息
				atmBindingInfo.setCreateBy(user);
				atmBindingInfo.setCreateName(userName);
				atmBindingInfo.setUpdateBy(user);
				atmBindingInfo.setUpdateName(userName);
				// 绑定明细列表
				List<AtmBindingDetail> atmBindingDetailList = Lists.newArrayList();
				// 保存绑定明细
				for (Map<String, Object> itemMap : detailList) {
					String rfid = StringUtils.toString(itemMap.get(Parameter.RFID_KEY));
					String boxNo = StringUtils.substring(rfid,
							rfid.length() - Integer.parseInt(Global.getConfig("boxNo.max.length")));
					AtmBindingDetail atmBindingDetail = new AtmBindingDetail();
					// 设置钞箱编号
					atmBindingDetail.setBoxNo(boxNo);
					// 设置ATM机编号
					atmBindingDetail.setAtmNo(atmNo);
					atmBindingDetailList.add(atmBindingDetail);
				}
				// 设置绑定明细
				atmBindingInfo.setAbdL(atmBindingDetailList);
				atmBindingInfo.setDataType(ATMConstant.CoreAmountMethod.INTERFACE);	//设置数据类型为"0",由接口传入
				// 保存绑定关系
				atmBindingInfoService.save(atmBindingInfo);
			}
		}
		resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(resultmap, serviceNo, versionNo);
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @author xiaoliang
	 * @version 2017年11月10日
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

	/**
	 * 更新钞箱状态
	 * 
	 * @author xiaoliang
	 * @version 2017年11月10日
	 * @param allAllocateDetail
	 * @param status
	 */
	// @Transactional(readOnly = false)
	// private void updateBoxStatus(AllAllocateDetail allAllocateDetail, String
	// status) {
	// if (allAllocateDetail != null) {
	// // 获取钞箱
	// StoBoxInfo detailBox =
	// stoBoxInfoService.get(allAllocateDetail.getBoxNo());
	// if (StringUtils.isNotBlank(status)) {
	// // 设置钞箱状态
	// detailBox.setBoxStatus(status);
	// }
	// // 更新信息
	// detailBox.setUpdateBy(allAllocateDetail.getUpdateBy());
	// detailBox.setUpdateName(allAllocateDetail.getUpdateName());
	// detailBox.setUpdateDate(new Date());
	// stoBoxInfoService.updateStatus(detailBox);
	// }
	// }

	/**
	 * 验证输入参数
	 * 
	 * @author xiaoliang
	 * @version 2017年11月10日
	 * @param paramMap
	 * @param resultmap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String validateParam(Map<String, Object> paramMap, Map<String, Object> resultmap) {
		// 加钞计划id
		String addPlanId = "";
		// 新绑定钞箱列表
		List<Map<String, Object>> boxNewList = Lists.newArrayList();
		// 绑定关系列表
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Parameter.LIST_KEY);
		if (Collections3.isEmpty(list)) {
			logger.debug("参数错误--------list为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------list为空");
			return Constant.FAILED;
		}
		for (Map<String, Object> map : list) {
			// 加钞计划id
			if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.ADD_PLAN_ID_KEY)))) {
				logger.debug("参数错误--------addPlanId为空");
				resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------addPlanId为空");
				return Constant.FAILED;
			}
			// ATM机编号
			if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.ATMNO_KEY)))
					&& StringUtils.isBlank(StringUtils.toString(map.get(Parameter.ATMRFID_KEY)))) {
				logger.debug("参数错误--------atmNo和atmRfid都为空");
				resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------atmNo和atmRfid都为空");
				return Constant.FAILED;
			}
			// 设置加钞计划id
			addPlanId = StringUtils.toString(map.get(Parameter.ADD_PLAN_ID_KEY));
			// 验证加钞计划
			AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
			// 设置加钞计划Id
			atmPlanInfo.setAddPlanId(addPlanId);
			// 设置加钞计划状态
			atmPlanInfo.setStatus(ATMConstant.PlanStatus.PLAN_OUT);
			// 设置atm机编号
			atmPlanInfo.setAtmNo(getAtmNo(map));
			List<AtmPlanInfo> atmPlanInfos = atmPlanInfoService.findList(atmPlanInfo);
			if (Collections3.isEmpty(atmPlanInfos)) {
				logger.debug("--------加钞计划不存在或ATM机不在加钞计划中");
				resultmap.put(Parameter.ERROR_MSG_KEY, "加钞计划不存在或ATM机不在加钞计划中");
				return Constant.FAILED;
			}
			// 钞箱列表
			List<Map<String, Object>> boxList = (List<Map<String, Object>>) map.get(Parameter.BOX_LIST_KEY);
			if (!Collections3.isEmpty(boxList)) {
				// 新绑定钞箱
				boxNewList = Collections3.union(boxNewList, boxList);
				//
				List<String> errorList = Lists.newArrayList();
				// 验证钞箱
				for (Map<String, Object> mapBox : boxList) {
					// 钞箱rfid
					if (StringUtils.isBlank(StringUtils.toString(mapBox.get(Parameter.RFID_KEY)))) {
						logger.debug("参数错误--------rfid为空");
						resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------rfid为空");
						return Constant.FAILED;
					}
					// 验证钞箱
					StoBoxInfo stoBoxInfo = new StoBoxInfo();
					stoBoxInfo.setRfid(mapBox.get(Parameter.RFID_KEY).toString());
					stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
					if (stoBoxInfo == null) {
						logger.debug("--------钞箱不存在");
						resultmap.put(Parameter.ERROR_MSG_KEY,
								"RFID为：" + mapBox.get(Parameter.RFID_KEY).toString() + "的钞箱不存在");
						return Constant.FAILED;
					}
					if (!Constant.BoxStatus.ATM_BOX_STATUS_OUT.equals(stoBoxInfo.getBoxStatus())) {
						errorList.add(stoBoxInfo.getRfid());
					}
				}
				if (errorList.size() > 0) {
					logger.debug("--------RFID为：" + Collections3.convertToString(errorList, ",") + "的钞箱状态不是在途");
					resultmap.put(Parameter.ERROR_MSG_KEY,
							"RFID为：" + Collections3.convertToString(errorList, ",") + "的钞箱状态不是在途");
					return Constant.FAILED;
				}
			}
		}
		// 钞箱入库流水
		AllAllocateInfo allAllocate = new AllAllocateInfo();
		// 钞箱入库明细
		List<AllAllocateDetail> allDetailList = Lists.newArrayList();
		// 加钞计划id
		allAllocate.setRouteId(addPlanId);
		// 登记状态
		allAllocate.setStatus(AllocationConstant.Status.Register);
		allAllocate.setBusinessType(Global.getConfig("businessType.allocation.out.AtmBoxIn"));
		// 钞箱入库列表
		List<AllAllocateInfo> allAllocateInfoList = allocationService.findAllocation(allAllocate);
		if (!Collections3.isEmpty(allAllocateInfoList)) {
			allAllocate = allAllocateInfoList.get(0);
			allDetailList = allAllocate.getAllDetailList();
			// 钞箱是否在入库明细中
			for (Map<String, Object> mapItem : boxNewList) {
				boolean flag = true;
				for (AllAllocateDetail allAllocateDetail : allDetailList) {
					if (allAllocateDetail.getRfid().equals(mapItem.get(Parameter.RFID_KEY))) {
						flag = false;
						break;
					}
				}
				if (flag) {
					logger.debug("--------钞箱不在入库流水中");
					resultmap.put(Parameter.ERROR_MSG_KEY,
							"RFID为：" + mapItem.get(Parameter.RFID_KEY) + "的钞箱不在入库流水中");
					return Constant.FAILED;
				}
			}
		} else {
			logger.debug("--------入库流水不存在");
			resultmap.put(Parameter.ERROR_MSG_KEY, "入库流水不存在");
			return Constant.FAILED;
		}
		return Constant.SUCCESS;
	}

	/**
	 * 获取ATM机编号
	 * 
	 * @author xiaoliang
	 * @version 2017年11月21日
	 * @param map
	 * @return
	 */
	public String getAtmNo(Map<String, Object> map) {
		// ATM机编号
		String atmNo = StringUtils.toString(map.get(Parameter.ATMNO_KEY));
		// ATM机编号
		String atmRfid = StringUtils.toString(map.get(Parameter.ATMRFID_KEY));
		if (StringUtils.isBlank(atmNo)) {
			AtmInfoMaintain atmInfoMaintain = atmInfoMaintainService.findByAtmId(atmRfid);
			if (atmInfoMaintain != null) {
				atmNo = atmInfoMaintain.getAtmId();
			}
		}
		return atmNo;
	}

	
}