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
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.atm.ATMConstant;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
 * Title: Service0387
 * <p>
 * Description: ATM库外清分入库确认接口
 * </p>
 * 
 * @author sg
 * @date 2017年11月13日
 */
@Component("Service0387")
@Scope("singleton")
public class Service0387 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	@Autowired
	private AtmPlanInfoService atmPlanInfoService;

	/**
	 * 
	 * @author sg ATM库外清分入库确认接口
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
		// 加钞计划ID
		String taskNo = StringUtils.toString(paramMap.get(Parameter.TASK_NO_KEY));
		// 所属机构
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		// 用户编号
		String userId = StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
		// 系统登录用户姓名
		String userName = StringUtils.toString(paramMap.get(Parameter.USER_NAME_KEY));
		// 箱袋列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> boxList = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);
		// 验证参数
		checkBoxHandoutRegister(paramMap, map);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 根据机构ID查询相关信息
		Office office = officeService.get(officeId);
		// 设置更新人信息
		User loginUser = UserUtils.get(userId);
		// 查询条件封装
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		// 设置机构
		Office rOffice = new Office();
		rOffice.setId(office.getId());
		allAllocateInfo.setrOffice(rOffice);
		// 设置业务类型为钞箱入库
		allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDIN);
		// 调拨状态
		allAllocateInfo.setStatus(AllocationConstant.Status.Register);
		// 设置加钞计划ID
		allAllocateInfo.setRouteId(taskNo);
		// 根据条件找出相应的信息
		List<AllAllocateInfo> list = allocationService.findAtmBoxList(allAllocateInfo);
		// 验证List是否为空
		checkList(paramMap, map, office, list, loginUser);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		// 设置加钞计划ID
		atmPlanInfo.setAddPlanId(taskNo);
		// 设置加钞计划状态
		atmPlanInfo.setStatus(ATMConstant.PlanStatus.PLAN_USE);
		// 设置修改人ID
		atmPlanInfo.setUpdateBy(loginUser);
		// 设置修改人姓名
		atmPlanInfo.setUpdateName(loginUser.getName());
		// 设置修改时间
		atmPlanInfo.setUpdateDate(new Date());
		// 对加钞计划表对应的状态进行修改
		atmPlanInfoService.updateStatus(atmPlanInfo);
		for (AllAllocateInfo allAllocateInfoSearch : list) {
			// 判断boxList是否为空如果是空不修改扫描时及不产生交间接ID
			if (!Collections3.isEmpty(boxList)) {
				boolean managerFlag = false;
				for (Map<String, Object> mapstr : boxList) {
					String scanFlag = StringUtils.toString(mapstr.get(Parameter.SCAN_FLAG_KEY));
					if (ATMConstant.ScanFlag.NoScan.equals(scanFlag)
							|| ATMConstant.ScanFlag.Additional.equals(scanFlag)) {
						managerFlag = true;
					}
					AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
					// 设置流水号
					allAllocateDetail.setAllId(allAllocateInfoSearch.getAllId());
					// 设置箱号
					allAllocateDetail.setBoxNo(StringUtils.toString(mapstr.get(Parameter.BOX_NO_KEY)));
					// 这只RFID
					allAllocateDetail.setRfid(StringUtils.toString(mapstr.get(Parameter.RFID_KEY)));
					// 设置扫描状态
					allAllocateDetail.setScanFlag(StringUtils.toString(mapstr.get(Parameter.SCAN_FLAG_KEY)));
					/** 已扫描或补录,设置扫描时间,更新钞箱状态 修改人：xl 修改时间：2017-11-24 begin **/
					if (!ATMConstant.ScanFlag.NoScan
							.equals(StringUtils.toString(mapstr.get(Parameter.SCAN_FLAG_KEY)))) {
						allAllocateDetail.setScanDate(new Date());
						// 修改钞箱状态
						StoBoxInfo stoBoxInfos = new StoBoxInfo();
						// 设置箱号
						stoBoxInfos.setId(StringUtils.toString(mapstr.get(Parameter.BOX_NO_KEY)));
						// 设置RFID
						stoBoxInfos.setRfid(StringUtils.toString(mapstr.get(Parameter.RFID_KEY)));
						// 根据箱号从箱袋表中查询
						List<StoBoxInfo> stoBoxInfo = stoBoxInfoService.findList(stoBoxInfos);
						// 获取箱子信息
						StoBoxInfo stoBoxInfoGet = stoBoxInfo.get(0);
						// 获取箱袋状态
						String boxStatus = stoBoxInfoGet.getBoxStatus();
						// 设置更新人信息
						stoBoxInfoGet.setUpdateBy(loginUser);
						// 设置更新日期
						stoBoxInfoGet.setUpdateDate(new Date());
						// 设置更新人姓名
						stoBoxInfoGet.setUpdateName(userName);
						// 如果箱袋状态是24清机在途将其修改为18清点
						if (Constant.BoxStatus.ATM_BOX_STATUS_CLEAR_IN.equals(boxStatus)) {
							stoBoxInfoGet.setBoxStatus(Constant.BoxStatus.ATM_BOX_STATUS_CLEAR);
						}
						// 如果箱袋状态是12在途将其修改为19退库
						if (Constant.BoxStatus.ATM_BOX_STATUS_OUT.equals(boxStatus)) {
							stoBoxInfoGet.setBoxStatus(Constant.BoxStatus.ATM_BOX_STATUS_BACK);
						}
						// 根据条件对钞箱状态进行修改
						stoBoxInfoService.updateAtmStatus(stoBoxInfoGet);
					}
					/** end **/
					// 根据条件对详细表进行修改
					allocationService.updateDetailByBoxNoorRfid(allAllocateDetail);
				}
				AllHandoverInfo allHandoverInfo = new AllHandoverInfo();
				// 设置ID
				allHandoverInfo.setId(IdGen.uuid());
				// 设置创建时间
				allHandoverInfo.setCreateDate(new Date());
				// 新增一条AllHandoverInfo
				allocationService.saveHandover(allHandoverInfo);
				// 需要授权
				if (managerFlag) {
					// 授权人
					List<Map<String, Object>> managerList = (List<Map<String, Object>>) paramMap
							.get(Parameter.MANAGER_LIST_KEY);
					for (Map<String, Object> managerMap : managerList) {
						// 准备插入的条件
						AllHandoverDetail escortIdAllHandoverDetail = new AllHandoverDetail();
						// 设置ID
						escortIdAllHandoverDetail.setDetailId(IdGen.uuid());
						// 设置交接ID
						escortIdAllHandoverDetail.setHandoverId(allHandoverInfo.getHandoverId());
						// 授权人信息
						User loginName = UserUtils
								.getByLoginName(StringUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY)));
						// 设置人员ID
						escortIdAllHandoverDetail.setEscortId(StringUtils.toString(loginName.getId()));
						// 设置人员姓名
						escortIdAllHandoverDetail.setEscortName(loginName.getName());
						// 设置授权方式
						escortIdAllHandoverDetail.setType(ATMConstant.ManagerType.SYSTEM_LOGIN);
						// 设置授权理由
						escortIdAllHandoverDetail
								.setManagerReason(StringUtils.toString(managerMap.get(Parameter.REASON_KEY)));
						// 设置操作类型
						escortIdAllHandoverDetail
								.setOperationType(AllocationConstant.OperationType.SCANNING_DOOR_AUTHORIZATION);
						// 向AllHandoverDetail表中插入数据
						allocationService.AllHandoverDetailInsert(escortIdAllHandoverDetail);
					}
				}
				// 添加allHandoverInfoID
				allAllocateInfoSearch.setStoreHandoverId(allHandoverInfo.getId());
				// 设置扫描时间
				allAllocateInfoSearch.setScanDate(new Date());
			}
			// 如果boxList为空不修改扫描时及不产生交间接ID
			// 根据主表ID查询详细表
			List<AllAllocateDetail> allAllocateDetailList = allocationService
					.getByAllIdscanFlag(allAllocateInfoSearch.getAllId());
			// 设置箱袋数量
			allAllocateInfoSearch.setRegisterNumber(allAllocateDetailList.size());
			// 设置状态
			allAllocateInfoSearch.setStatus(AllocationConstant.AtmBusinessStatus.HandoverTodo);
			// 设置更新人信息
			allAllocateInfoSearch.setUpdateBy(loginUser);
			// 设置更新日期
			allAllocateInfoSearch.setUpdateDate(new Date());
			// 设置更新人姓名
			allAllocateInfoSearch.setUpdateName(userName);
			// 对主表进行修改
			allocationService.updateAtm(allAllocateInfoSearch);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		}
		return setReturnMap(map, serviceNo);
	}

	/**
	 * @author sg
	 * @version 2017-11-13
	 * 
	 * @Description ATM库外清分入库确认接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	private String checkBoxHandoutRegister(Map<String, Object> headInfo, Map<String, Object> map) {
		// 加钞计划ID
		String taskNo = StringUtils.toString(headInfo.get(Parameter.TASK_NO_KEY));
		// 所属机构
		String officeId = StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY));
		// 用户编号
		String userId = StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY));
		// 判断机构ID是否为空
		if (StringUtils.isBlank(officeId)) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId");
			return Constant.FAILED;
		}
		// 判断用户编号是否为空
		if (StringUtils.isBlank(userId)) {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------userId");
			return Constant.FAILED;
		}
		// 判断加钞计划ID是否为空
		if (StringUtils.isBlank(taskNo)) {
			logger.debug("参数错误--------taskNo:" + CommonUtils.toString(headInfo.get(Parameter.TASK_NO_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------taskNo");
			return Constant.FAILED;
		}
		return Constant.SUCCESS;
	}

	/**
	 * @author sg
	 * @version 2017-11-13
	 * 
	 * @Description ATM库外清分入库确认接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	private String checkList(Map<String, Object> headInfo, Map<String, Object> map, Office office,
			List<AllAllocateInfo> list, User loginUser) {
		// 所属机构
		String officeId = StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY));
		// 箱袋列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> boxList = (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY);
		if (loginUser == null) {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------userId");
			return Constant.FAILED;
		}
		if (office != null) {
			// 判断信息是否为空
			if (!Collections3.isEmpty(list)) {
				for (AllAllocateInfo allAllocateInfoSearch : list) {
					// 取得详细信息
					List<AllAllocateDetail> detailList = allAllocateInfoSearch.getAllDetailList();
					// 判断详细信息是否为空
					if (!Collections3.isEmpty(detailList)) {
						// 用于判断箱子是否在详细表中存在
						List<String> errorList = Lists.newArrayList();
						// 用于判断箱子是否存在
						List<String> errorBoxList = Lists.newArrayList();
						// 用于判断RFID是否存在
						List<String> errorBoxRfidList = Lists.newArrayList();
						// 用于判断箱子是否为绑定
						List<String> errorBoxDelFlagList = Lists.newArrayList();
						// 用于判断箱子状态是否为12、24
						List<String> errorBoxStatusList = Lists.newArrayList();
						if (!Collections3.isEmpty(boxList)) {
							for (Map<String, Object> mapstr : boxList) {
								String scanFlag = StringUtils.toString(mapstr.get(Parameter.SCAN_FLAG_KEY));
								if (ATMConstant.ScanFlag.NoScan.equals(scanFlag)
										|| ATMConstant.ScanFlag.Additional.equals(scanFlag)) {
									// 验证授权人
									List<Map<String, Object>> managerList = (List<Map<String, Object>>) headInfo
											.get(Parameter.MANAGER_LIST_KEY);
									if (Collections3.isEmpty(managerList)) {
										map.put(Parameter.RESULT_FLAG_KEY,
												ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
										map.put(Parameter.ERROR_NO_KEY,
												ExternalConstant.HardwareInterface.ERROR_NO_E99);
										map.put(Parameter.ERROR_MSG_KEY, "managerList为空");
										return Constant.FAILED;
									}
									for (Map<String, Object> managerMap : managerList) {
										String id = StringUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY));
										String reason = StringUtils.toString(managerMap.get(Parameter.REASON_KEY));
										if (StringUtils.isBlank(id)) {
											map.put(Parameter.RESULT_FLAG_KEY,
													ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
											map.put(Parameter.ERROR_NO_KEY,
													ExternalConstant.HardwareInterface.ERROR_NO_E99);
											map.put(Parameter.ERROR_MSG_KEY, "授权id为空");
										}
										if (StringUtils.isBlank(reason)) {
											map.put(Parameter.RESULT_FLAG_KEY,
													ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
											map.put(Parameter.ERROR_NO_KEY,
													ExternalConstant.HardwareInterface.ERROR_NO_E99);
											map.put(Parameter.ERROR_MSG_KEY, "授权原因为空");
										}
									}
								 }
								// 判断详细信息中是否包含箱袋列表中的箱号或rfid
								boolean judgment = false;
								for (AllAllocateDetail allAllocateDetail : detailList) {
									// 判断详细信息中是否包含箱袋列表中的箱号或rfid
									if (allAllocateDetail.getRfid().equals(mapstr.get(Parameter.RFID_KEY))
											|| allAllocateDetail.getBoxNo().equals(mapstr.get(Parameter.BOX_NO_KEY))) {
										StoBoxInfo stoBoxInfos = new StoBoxInfo();
										// 设置箱号
										stoBoxInfos.setId(StringUtils.toString(mapstr.get(Parameter.BOX_NO_KEY)));
										// 设置RFID
										stoBoxInfos.setRfid(StringUtils.toString(mapstr.get(Parameter.RFID_KEY)));
										// 根据箱号从箱袋表中查询
										List<StoBoxInfo> stoBoxInfo = stoBoxInfoService.findList(stoBoxInfos);
										// 判断是否存在该箱子
										if (stoBoxInfo.isEmpty()) {
											if (stoBoxInfos.getRfid() == null) {
												errorBoxList
														.add(StringUtils.toString(mapstr.get(Parameter.BOX_NO_KEY)));
											} else {
												errorBoxList.add(StringUtils.toString(mapstr.get(Parameter.RFID_KEY)));
											}
										} else {
											StoBoxInfo stoboxinfo = stoBoxInfo.get(0);
											// 判断是否存在RFID
											if (stoboxinfo == null) {
												errorBoxRfidList
														.add(StringUtils.toString(mapstr.get(Parameter.RFID_KEY)));
											}
											// 判断箱子是否为绑定状态
											if (!"0".equals(stoboxinfo.getDelFlag())) {
												errorBoxDelFlagList
														.add(StringUtils.toString(mapstr.get(Parameter.RFID_KEY)));
											}
											// 判断箱袋状态是否是12：在途或24：清机在途
											if (!(Constant.BoxStatus.ATM_BOX_STATUS_CLEAR_IN
													.equals(stoboxinfo.getBoxStatus())
													|| Constant.BoxStatus.ATM_BOX_STATUS_OUT
															.equals(stoboxinfo.getBoxStatus()))) {
												if (stoboxinfo.getRfid() != null) {
													errorBoxStatusList.add(stoboxinfo.getRfid());
												} else {
													errorBoxStatusList.add(stoboxinfo.getId());
												}
											}
											judgment = true;
											break;
										}
									}

								}
								if (judgment == false) {
									if (mapstr.get(Parameter.BOX_NO_KEY) == null) {
										errorList.add(StringUtils.toString(mapstr.get(Parameter.RFID_KEY)));
									} else {
										errorList.add(StringUtils.toString(mapstr.get(Parameter.BOX_NO_KEY)));
									}
								}

							}
						}
						if (!Collections3.isEmpty(errorList)) {
							map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY,
									"箱号或RFID为" + Collections3.convertToString(errorList, ",") + "有误");
							return Constant.FAILED;
						}
						// 判断箱子是否存在
						if (!Collections3.isEmpty(errorBoxList)) {
							logger.debug("箱袋不存在,BoxNo或RFID为：" + Collections3.convertToString(errorList, ",") + "的信息");
							String strMessageContent = "箱袋不存在,BoxNo或RFID为："
									+ Collections3.convertToString(errorList, ",") + "的信息";
							map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY, strMessageContent);
							return Constant.FAILED;
						}
						// 判断箱子是否存在Rfid
						if (!Collections3.isEmpty(errorBoxRfidList)) {
							logger.debug("ATM库外清分入库确认接口-------- 未查询到RFID:"
									+ Collections3.convertToString(errorBoxRfidList, ",") + "相关箱袋信息");
							String strMessageContent = "ATM库外清分入库确认接口-------- 未查询到RFID:"
									+ Collections3.convertToString(errorBoxRfidList, ",") + "相关箱袋信息";
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY, strMessageContent);
							return Constant.FAILED;
						}
						// 判断箱子是否绑定
						if (!Collections3.isEmpty(errorBoxDelFlagList)) {
							logger.debug("ATM库外清分入库确认接口-------- RFID为:"
									+ Collections3.convertToString(errorBoxDelFlagList, ",") + "箱袋未绑定或删除");
							String strMessageContent = "ATM库外清分入库确认接口-------- RFID为:"
									+ Collections3.convertToString(errorBoxDelFlagList, ",") + "箱袋未绑定或删除";
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY, strMessageContent);
							return Constant.FAILED;
						}
						// 判断箱子状态是够为12、24
						if (!Collections3.isEmpty(errorBoxStatusList)) {
							logger.debug(
									"箱号或RFID为：" + Collections3.convertToString(errorBoxStatusList, ",") + "的箱子状态有误");
							String strMessageContent = "箱号或RFID为："
									+ Collections3.convertToString(errorBoxStatusList, ",") + "的箱子状态有误";
							map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY, strMessageContent);
							return Constant.FAILED;
						}
						return Constant.SUCCESS;

					} else {
						return Constant.SUCCESS;
					}
				}
			} else {
				// 未查到主表信息
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY, "未找到相应信息");
				return Constant.FAILED;
			}
		} else {
			// 机构ID不正确
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "不存在机构ID为：" + officeId + "的数据");
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
