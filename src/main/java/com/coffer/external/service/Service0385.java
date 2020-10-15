package com.coffer.external.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.AllocationConstant.BusinessType;
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
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
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
import com.google.common.collect.Maps;

/**
 * Title: Service0385
 * <p>
 * Description:
 * </p>
 * 
 * @author wxz
 * @date 2017年11月10日 下午15:41:10
 */
@Component("Service0385")
@Scope("singleton")
public class Service0385 extends HardwardBaseService {

	@Autowired
	private OfficeService officeService;
	@Autowired
	private AllocationService allocationService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	@Autowired
	private AtmPlanInfoService atmPlanInfoService;

	/**
	 * 
	 * @author wxz
	 * @version 2017年11月10日
	 * 
	 *          0385 库外出库确认接口
	 * @param requestMap
	 * @param serviceNo
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();
		// 获取服务代码
		String serviceNo = StringUtils.toString(paramMap.get(Parameter.SERVICE_NO_KEY));
		// 获取用户编号，系统登陆用户姓名
		String userId = StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
		String userName = StringUtils.toString(paramMap.get(Parameter.USER_NAME_KEY));
		User loginUser = UserUtils.get(userId);

		// 获取通过PDA传输的参数(金库编号，任务编号以及用户编号和用户姓名)
		String taskNo = StringUtils.toString(paramMap.get(Parameter.TASK_NO_KEY));
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		Office office = officeService.get(officeId);
		// 验证参数信息
		checkBoxInfo(paramMap, respMap);
		// 验证失败的场合，退出
		if (respMap.size() > 0) {
			return setReturnMap(respMap, serviceNo);
		}

		AllAllocateInfo allocateInfo = new AllAllocateInfo();
		// 添加查询信息(任务编号)
		allocateInfo.setRouteId(taskNo);
		// 添加查询信息(业务类型)
		allocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDOUT);
		// 添加查询信息(交接状态)
		allocateInfo.setStatus(AllocationConstant.Status.Register);
		// 添加查询信息(机构信息)
		Office rOffice = new Office();
		rOffice.setId(office.getId());
		allocateInfo.setrOffice(rOffice);
		// 获取调拨主表详细信息
		List<AllAllocateInfo> allocateInfoList = allocationService.findAtmBoxList(allocateInfo);
		// 此调拨信息不存在
		if (Collections3.isEmpty(allocateInfoList)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E14);
			respMap.put(Parameter.ERROR_MSG_KEY, "此调拨信息不存在！");
			return gson.toJson(respMap);
		}

		@SuppressWarnings("unchecked")
		// 获取PDA传输的参数boxList
		List<Map<String, Object>> detaiList = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);
		// 创建返回错误信息List
		List<String> errorList = Lists.newArrayList();

		// 插入交接信息
		AllHandoverInfo handoverInfo = new AllHandoverInfo();
		// 生成主键id(uuid)
		handoverInfo.setHandoverId(IdGen.uuid());
		// 插入创建时间(当前系统时间)
		handoverInfo.setCreateDate(new Date());
		// 保存交接信息
		allocationService.saveHandover(handoverInfo);

		// 获取调拨信息，修改状态为已扫描(待交接)
		allocateInfo = allocateInfoList.get(0);
		// 获取详细表详细信息List
		List<AllAllocateDetail> allAllocateDetailList = allocateInfo.getAllDetailList();
		// 插入交接状态
		allocateInfo.setStatus(AllocationConstant.Status.HandoverTodo);
		// 插入库房交接ID
		allocateInfo.setStoreHandoverId(handoverInfo.getId());
		// 设置更新人id
		allocateInfo.setUpdateBy(loginUser);
		// 设置更新人名字
		allocateInfo.setUpdateName(userName);
		// 设置更新时间
		allocateInfo.setUpdateDate(new Date());
		boolean managerFlag = false;
		// 遍历详细信息保存扫描状态
		for (Map<String, Object> itemMap : detaiList) {
			boolean flag = false;
			for (AllAllocateDetail allAllocateDetail : allAllocateDetailList) {
				// 判断传过来的箱袋编号或者rfid是否与查询出的结果相等
				if (allAllocateDetail.getBoxNo().equals(itemMap.get(Parameter.BOX_NO_KEY))
						|| allAllocateDetail.getRfid().equals(itemMap.get(Parameter.STORE_ESCORT_RFID))) {
					allAllocateDetail.setScanFlag(StringUtils.toString(itemMap.get(Parameter.SCAN_FLAG_KEY)));
					flag = true;
					continue;
				}
			}
			// 当前箱袋信息不在调拨明细中
			if (!flag) {
				// 判断BoxNo是否为空
				if (!StringUtils.isBlank(StringUtils.toString(itemMap.get(Parameter.BOX_NO_KEY)))) {
					errorList.add(StringUtils.toString(itemMap.get(Parameter.BOX_NO_KEY)));
				} else {
					// 判断rfid是否为空
					errorList.add(itemMap.get(Parameter.STORE_ESCORT_RFID).toString());
				}
			}
			String scanFlag = StringUtils.toString(itemMap.get(Parameter.SCAN_FLAG_KEY));
			if (ATMConstant.ScanFlag.NoScan.equals(scanFlag) || ATMConstant.ScanFlag.Additional.equals(scanFlag)) {
				managerFlag = true;
			}
		}
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
				escortIdAllHandoverDetail.setHandoverId(handoverInfo.getHandoverId());
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
				escortIdAllHandoverDetail.setManagerReason(StringUtils.toString(managerMap.get(Parameter.REASON_KEY)));
				// 设置操作类型
				escortIdAllHandoverDetail
						.setOperationType(AllocationConstant.OperationType.SCANNING_DOOR_AUTHORIZATION);
				// 向AllHandoverDetail表中插入数据
				allocationService.AllHandoverDetailInsert(escortIdAllHandoverDetail);
			}
		}
		// E99
		if (!Collections3.isEmpty(errorList)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			respMap.put(Parameter.ERROR_MSG_KEY, Collections3.convertToString(errorList, ","));
			return gson.toJson(respMap);
		}
		// 更细明细表详细信息
		for (Map<String, Object> itemMap : detaiList) {
			AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
			// 设置明细表主键id(更新信息使用)
			allAllocateDetail.setAllId(allocateInfo.getAllId());
			// 插入扫描状态
			allAllocateDetail.setScanFlag((String) itemMap.get(Parameter.SCAN_FLAG_KEY));
			// 插入箱袋编号
			allAllocateDetail.setBoxNo((String) itemMap.get(Parameter.BOX_NO_KEY));
			// 插入rfid
			allAllocateDetail.setRfid((String) itemMap.get(Parameter.RFID_KEY));
			// 插入更新人id
			allAllocateDetail.setUpdateBy(loginUser);
			// 插入更新人姓名
			allAllocateDetail.setUpdateName(userName);
			// 插入更新时间
			allAllocateDetail.setUpdateDate(new Date());
			/** 已扫描或补录,设置扫描时间,更新钞箱状态 修改人：xl 修改时间：2017-11-24 begin **/
			if (!ATMConstant.ScanFlag.NoScan.equals((String) itemMap.get(Parameter.SCAN_FLAG_KEY))) {
				allAllocateDetail.setScanDate(new Date());
				StoBoxInfo stoBoxInfo = new StoBoxInfo();
				// 设置rfid(查询需要更新的箱子信息)
				stoBoxInfo.setRfid((String) itemMap.get(Parameter.RFID_KEY));
				// 设置箱袋编号(查询需要更新的箱子信息)
				stoBoxInfo.setId((String) itemMap.get(Parameter.BOX_NO_KEY));
				// 插入箱子状态(在途)
				stoBoxInfo.setBoxStatus(AllocationConstant.Status.Onload);
				// 插入更新人id
				stoBoxInfo.setUpdateBy(loginUser);
				// 插入更新人姓名
				stoBoxInfo.setUpdateName(userName);
				// 插入更新时间
				stoBoxInfo.setUpdateDate(new Date());
				// 更新箱子状态
				stoBoxInfoService.updateAtmStatus(stoBoxInfo);
			}
			/** end **/
			// 更新明细表
			allocationService.updateDetailByBoxNoorRfid(allAllocateDetail);
		}
		// 根据主表ID查询详细表
		List<AllAllocateDetail> allAllocateDetails = allocationService.getByAllIdscanFlag(allocateInfo.getAllId());
		// 设置箱袋数量
		allocateInfo.setRegisterNumber(allAllocateDetails.size());
		// 更新主表信息
		allocationService.updatInfoStatus(allocateInfo);
		// 更新加钞计划表状态
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		// 设置加钞计划id
		atmPlanInfo.setAddPlanId(taskNo);
		// 状态更新为计划出库
		atmPlanInfo.setStatus(ATMConstant.PlanStatus.PLAN_OUT);
		// 插入更新人id
		atmPlanInfo.setUpdateBy(loginUser);
		// 插入更新人姓名
		atmPlanInfo.setUpdateName(userName);
		// 插入更新时间
		atmPlanInfo.setUpdateDate(new Date());
		// 更新加钞计划
		atmPlanInfoService.updateStatus(atmPlanInfo);

		// 新增入库流水
		AllAllocateInfo allInfo = new AllAllocateInfo();
		// 设置主键id(自动生成)
		allInfo.setAllId(
				BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.out.AtmBoxIn"), office));
		// 设置业务类型(钞箱入库)
		allInfo.setBusinessType(BusinessType.ATM_BOX_HANDIN);
		// 设置路线id(实际为PDA传输的任务编号id(taskNo))
		allInfo.setRouteId(allocateInfo.getRouteId());
		// 设置登记机构
		allInfo.setrOffice(allocateInfo.getrOffice());
		// 设置状态(登记)
		allInfo.setStatus(AllocationConstant.Status.Register);
		// 设置创建人id
		allInfo.setCreateBy(allocateInfo.getCreateBy());
		// 设置创建人姓名
		allInfo.setCreateName(allocateInfo.getCreateName());
		// 设置创建时间
		allInfo.setCreateDate(new Date());
		// 设置更新人id
		allInfo.setUpdateBy(allocateInfo.getUpdateBy());
		// 设置更新人姓名
		allInfo.setUpdateName(allocateInfo.getUpdateName());
		// 设置更新时间
		allInfo.setUpdateDate(new Date());
		// 获取明细详细信息
		List<AllAllocateDetail> allInfoDetail = allocateInfo.getAllDetailList();
		/** 入库明细详细信息 创建人：xl 创建时间： 2017-11-22 begin **/
		for (Iterator<AllAllocateDetail> iterator = allInfoDetail.iterator(); iterator.hasNext();) {
			AllAllocateDetail allDetail = (AllAllocateDetail) iterator.next();
			if (AllocationConstant.ScanFlag.Scan.equals(allDetail.getScanFlag())
					|| AllocationConstant.ScanFlag.Additional.equals(allDetail.getScanFlag())) {
				// 设置明细表主键id(uuid)
				allDetail.setAllDetailId(IdGen.uuid());
				// 设置扫描状态(未扫描)
				allDetail.setScanFlag(ATMConstant.ScanFlag.NoScan);
				// 设置扫描时间
				allDetail.setScanDate(null);
				// 设置箱号
				allDetail.setBoxNo(allDetail.getBoxNo());
				// 设置rfid
				allDetail.setRfid(allDetail.getRfid());
				// 设置流水号
				allDetail.setAllId(allInfo.getAllId());
			} else if (ATMConstant.ScanFlag.NoScan.equals(allDetail.getScanFlag())) {
				iterator.remove();
			}
		}
		// 设置登记个数
		allInfo.setRegisterNumber(allInfoDetail.size());
		// 设置明细
		allInfo.setAllDetailList(allInfoDetail);
		/** end **/
		// 保存明细
		allocationService.insertInfoAndDetail(allInfo);

		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		return gson.toJson(respMap);
	}

	/**
	 * @author wxz
	 * @version 2017-11-13
	 * 
	 * @Description 验证 PDA库外出库确认接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	private void checkBoxInfo(Map<String, Object> headInfo, Map<String, Object> map) {

		// 验证参数
		if (headInfo.get(Parameter.OFFICE_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("库外箱袋出库确认接口-------- 机构ID为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 机构ID为空");
		}
		// 判断任务编号是否为空
		if (headInfo.get(Parameter.TASK_NO_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.TASK_NO_KEY).toString())) {
			logger.debug("库外箱袋出库确认接口-------- 任务编号为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 任务编号为空");
		}
		// 箱袋列表
		if (headInfo.get(Parameter.BOX_LIST_KEY) == null
				|| ((List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY)).size() == 0) {
			logger.debug("参数错误--------boxList:" + CommonUtils.toString(headInfo.get(Parameter.BOX_LIST_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------boxList:" + CommonUtils.toString(headInfo.get(Parameter.BOX_LIST_KEY)));

		} else {
			for (Map<String, Object> boxMap : (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY)) {
				// 判断箱袋编号是否为空
				if ((boxMap.get(Parameter.BOX_NO_KEY) == null
						|| StringUtils.isBlank(boxMap.get(Parameter.BOX_NO_KEY).toString()))
						&& (boxMap.get(Parameter.RFID_KEY) == null
								|| StringUtils.isBlank(boxMap.get(Parameter.RFID_KEY).toString()))) {
					logger.debug("参数错误--------boxList:Rfid:" + CommonUtils.toString(boxMap.get(Parameter.RFID_KEY)));
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY,
							"参数错误--------boxList:Rfid:" + CommonUtils.toString(boxMap.get(Parameter.RFID_KEY)));
				}
				String scanFlag = StringUtils.toString(boxMap.get(Parameter.SCAN_FLAG_KEY));
				if (ATMConstant.ScanFlag.NoScan.equals(scanFlag) || ATMConstant.ScanFlag.Additional.equals(scanFlag)) {
					// 验证授权人
					List<Map<String, Object>> managerList = (List<Map<String, Object>>) headInfo
							.get(Parameter.MANAGER_LIST_KEY);
					if (Collections3.isEmpty(managerList)) {
						map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
						map.put(Parameter.ERROR_MSG_KEY, "managerList为空");
					}
					for (Map<String, Object> managerMap : managerList) {
						String id = StringUtils.toString(managerMap.get(Parameter.OPT_USER_ID_KEY));
						String reason = StringUtils.toString(managerMap.get(Parameter.REASON_KEY));
						if (StringUtils.isBlank(reason)) {
							map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY, "授权原因为空");
						}
						if (StringUtils.isBlank(id)) {
							map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							map.put(Parameter.ERROR_MSG_KEY, "授权id为空");
						}
					}
				}
			}
		}

		// 设定箱袋信息
		List<Map<String, Object>> boxList = (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY);
		// 判断箱袋详细信息是否为空
		if (!Collections3.isEmpty(boxList)) {
			for (Map<String, Object> mapList : boxList) {
				StoBoxInfo boxInfo = new StoBoxInfo();
				// 判断箱袋RFID是否为空
				if (!StringUtils.isBlank(StringUtils.toString(mapList.get(Parameter.RFID_KEY)))) {
					boxInfo.setRfid(StringUtils.toString(mapList.get(Parameter.RFID_KEY)));
					// 根据RFID查询箱子
					List<StoBoxInfo> stoboxinfoList = stoBoxInfoService.findList(boxInfo);
					// 判断箱子明细是否为空
					if (stoboxinfoList.isEmpty()) {
						logger.debug("箱袋所属机构没有绑定路线或箱子不存在,RFID:" + boxInfo.getRfid());
						String strMessageContent = "箱袋所属机构没有绑定路线或箱子不存在,RFID:" + boxInfo.getRfid();
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
					StoBoxInfo stoboxinfo = stoboxinfoList.get(0);
					// 判断是否存在箱子数据
					if (stoboxinfo == null) {
						logger.debug("库外箱袋入库(出库)确认接口-------- 未查询到RFID:" + mapList.get(Parameter.RFID_KEY) + "相关箱袋信息");
						String strMessageContent = "库外箱袋入库(出库)确认接口-------- 未查询到RFID:" + mapList.get(Parameter.RFID_KEY)
								+ "相关箱袋信息";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
					// 判断箱袋状态是否有误
					if (!Constant.BoxStatus.ATM_BOX_STATUS_PREPARE_OUT.equals(stoboxinfo.getBoxStatus())) {
						// 箱子状态不为待出库，不能确认
						logger.debug("RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId() + "的箱子状态有误");
						String strMessageContent = "RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId()
								+ "的箱子状态有误";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
				} else {
					boxInfo.setId(StringUtils.toString(mapList.get(Parameter.BOX_NO_KEY)));
					// 根据箱号(boxNo)查询箱子
					List<StoBoxInfo> stoboxinfoList = stoBoxInfoService.findList(boxInfo);
					// 判断箱子明细是否为空
					if (stoboxinfoList.isEmpty()) {
						logger.debug("箱袋所属机构没有绑定路线或箱子不存在,箱袋编号:" + boxInfo.getId());
						String strMessageContent = "箱袋所属机构没有绑定路线或箱子不存在,箱袋编号:" + boxInfo.getId();
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
					StoBoxInfo stoboxinfo = stoboxinfoList.get(0);
					// 判断是否存在箱子数据
					if (stoboxinfo == null) {
						logger.debug("库外箱袋出库确认接口-------- 未查询到箱袋编号:" + mapList.get(Parameter.BOX_NO_KEY) + "相关箱袋信息");
						String strMessageContent = "库外箱袋出库确认接口-------- 未查询到箱袋编号:" + mapList.get(Parameter.BOX_NO_KEY)
								+ "相关箱袋信息";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
					// 判断箱袋状态是否有误
					if (!Constant.BoxStatus.ATM_BOX_STATUS_PREPARE_OUT.equals(stoboxinfo.getBoxStatus())) {
						// 箱子状态不为空箱，不能补录
						logger.debug("RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId() + "的箱子状态有误");
						String strMessageContent = "RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId()
								+ "的箱子状态有误";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
				}
			}
		}
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @param map
	 * @param serviceNo
	 * @author wxz
	 * 
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo) {
		map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);

		return gson.toJson(map);
	}

}
