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
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service23
 * <p>
 * Description: 库外箱袋出库确认接口
 * </p>
 * 
 * @author qipeihong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service23")
@Scope("singleton")
public class Service23 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	@Autowired
	private StoRouteInfoService stoRouteInfoService;
	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月17日
	 * 
	 *          23 库外箱袋出库确认接口
	 * @param paramMap
	 *            箱袋信息
	 * @return 更新结果
	 */
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 验证参数信息
		String checkResult = this.getBoxInOutStoreInfo(paramMap, map);
		if (Constant.FAILED.equals(checkResult)) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(map);
		}
		// 处理箱袋 (取得箱袋信息并且强制补录状态下箱袋在此方法中执行)
		AllAllocateInfo boxInfoCondition = updateByScanFlag(paramMap, map);
		if (paramMap.get(Parameter.ROUTE_ID_KEY) != null
				&& StringUtils.isNotBlank(paramMap.get(Parameter.ROUTE_ID_KEY).toString())) {
			// 正常状态下
			allocationService.boxOutStoreConfirm(paramMap, boxInfoCondition);
		}
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(map);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月13日
	 * 
	 *          库外箱袋入库确认接口（检查钞箱编号在本线路中是否存在） 如果存在设定更新条件
	 * @param boxInfo
	 *            钞箱信息
	 * @return true：存在， false：不存在
	 */
	@Transactional(readOnly = true)
	public boolean checkInStoreBoxNoExists(AllAllocateInfo boxInfo) {
		// 根据路线查询钞箱新息
		AllAllocateInfo conditionInfo = new AllAllocateInfo();
		// 查询条件：线路ID
		conditionInfo.setRouteId(boxInfo.getRouteId());
		// 查询条件：业务类型= 现金预约
		conditionInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		// 设置状态 = 已装箱
		conditionInfo.setStatus(AllocationConstant.Status.CashOrderQuotaYes);
		// 取出线路中的钞箱
		List<AllAllocateInfo> routeInfoList = allocationService.findBoxInfoList(conditionInfo);
		// key= 箱钞编号 value=调拨详细表主键
		Map<String, AllAllocateDetail> routeBoxNoMap = Maps.newHashMap();
		for (AllAllocateInfo info : routeInfoList) {
			for (AllAllocateDetail boxDetail : info.getAllDetailList()) {
				routeBoxNoMap.put(boxDetail.getBoxNo(), boxDetail);
			}
		}
		// 检查钞箱编号在本线路中是否存在
		for (AllAllocateDetail inBoxDetail : boxInfo.getAllDetailList()) {
			if (!routeBoxNoMap.containsKey(inBoxDetail.getBoxNo())) {
				return false;
			} else {
				// 如果存在，设置主键ID
				inBoxDetail.setAllDetailId(routeBoxNoMap.get(inBoxDetail.getBoxNo()).getAllDetailId());
				// 设置流水号
				inBoxDetail.setAllId(routeBoxNoMap.get(inBoxDetail.getBoxNo()).getAllId());
			}
		}
		// 更新条件：业务类型=现金预约
		boxInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		// 更新条件:更新状态=已装箱
		boxInfo.setStatus(AllocationConstant.Status.CashOrderQuotaYes);
		return true;
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月14日
	 * 
	 *          从参数取得箱袋信息(出入库)
	 * @param headInfo
	 *            箱袋信息
	 * @return 箱袋信息
	 */
	@SuppressWarnings("unchecked")
	private String getBoxInOutStoreInfo(Map<String, Object> headInfo, Map<String, Object> resultmap) {
		// 授权人信息列表
		List<Map<String, Object>> managerList = null;
		// 箱袋编号列表
		List<Map<String, Object>> boxList = null;
		// 取得箱袋编号列表
		if (headInfo.get(Parameter.BOX_LIST_KEY) != null) {
			boxList = (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY);
			if (Collections3.isEmpty(boxList)) {
				return Constant.SUCCESS;
			} else {
				for (Map<String, Object> map : boxList) {
					if ((map.get(Parameter.RFID_KEY) == null
							|| StringUtils.isBlank(map.get(Parameter.RFID_KEY).toString()))) {
						logger.debug("库外箱袋入库(出库)确认接口--------箱袋RFID为空");
						resultmap.put(Parameter.ERROR_MSG_KEY, "箱袋RFID为空");
						return Constant.FAILED;
					}
					if (map.get(Parameter.SCAN_FLAG_KEY) == null
							|| StringUtils.isBlank(map.get(Parameter.SCAN_FLAG_KEY).toString())) {
						logger.debug("库外箱袋入库(出库)确认接口-------- 扫描状态为空");
						resultmap.put(Parameter.ERROR_MSG_KEY, "扫描状态为空");
						return Constant.FAILED;
					}
				}
			}
		} else {
			logger.debug("库外箱袋入库(出库)确认接口-------- 箱袋编号列表为空");
			return Constant.SUCCESS;
		}
		// 取得授权人信息列表
		if (headInfo.get(Parameter.MANAGER_LIST_KEY) != null) {
			managerList = (List<Map<String, Object>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
			for (Map<String, Object> map : managerList) {
				if (map.get(Parameter.OPT_USER_ID_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库外箱袋入库(出库)确认接口-------- 授权人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					resultmap.put(Parameter.ERROR_MSG_KEY, "授权人ID有误");
					return Constant.FAILED;
				}
				if (map.get(Parameter.REASON_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.REASON_KEY).toString())) {
					logger.debug("库外箱袋入库(出库)确认接口-------- 授权原因:" + map.get(Parameter.REASON_KEY));
					resultmap.put(Parameter.ERROR_MSG_KEY, "授权原因有误");
					return Constant.FAILED;
				}
			}
		}
		// 取得更新者ID
		if (headInfo.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("库外箱袋入库(出库)确认接口-------- 更新者ID为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 更新者ID为空");
			return Constant.FAILED;
		}
		// 取得更新者名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("库外箱袋入库(出库)确认接口-------- 更新者名为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 更新者名为空");
			return Constant.FAILED;
		}
		return Constant.SUCCESS;
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年8月4日
	 * 
	 *          通过扫描标识分类设置allocateInfo信息
	 * @param map
	 */

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public AllAllocateInfo updateByScanFlag(Map<String, Object> headInfo, Map<String, Object> resultmap) {
		// 授权人信息列表
		List<Map<String, Object>> managerList = null;
		// 设定交接信息
		AllHandoverInfo handoverInfo = new AllHandoverInfo();

		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		// 取得路线ID
		if (headInfo.get(Parameter.ROUTE_ID_KEY) != null
				&& StringUtils.isNotBlank(headInfo.get(Parameter.ROUTE_ID_KEY).toString())) {
			allAllocateInfo.setRouteId(headInfo.get(Parameter.ROUTE_ID_KEY).toString());
		}
		// 取得授权人信息列表
		if (headInfo.get(Parameter.MANAGER_LIST_KEY) != null) {
			managerList = (List<Map<String, Object>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
		}
		// 设定箱袋信息
		AllAllocateDetail boxDetail = null;
		String handoverId = "";
		List<Map<String, Object>> boxList = (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY);
		if (!Collections3.isEmpty(boxList)) {
			for (Map<String, Object> map : boxList) {
				StoBoxInfo boxInfo = new StoBoxInfo();
				boxInfo.setRfid(map.get(Parameter.RFID_KEY).toString());
				// 根据箱号查询箱子
				List<StoBoxInfo> stoboxinfoList = stoBoxInfoService.getBindingBoxInfo(boxInfo);
				if (stoboxinfoList.isEmpty()) {
					logger.debug("箱袋所属机构没有绑定路线或箱子不存在,RFID:" + boxInfo.getRfid());
					String strMessageContent = "箱袋所属机构没有绑定路线或箱子不存在,RFID:" + boxInfo.getRfid();
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				StoBoxInfo stoboxinfo = stoboxinfoList.get(0);
				if (stoboxinfo == null) {
					logger.debug("库外箱袋入库(出库)确认接口-------- 未查询到RFID:" + map.get(Parameter.RFID_KEY) + "相关箱袋信息");
					String strMessageContent = "库外箱袋入库(出库)确认接口-------- 未查询到RFID:" + map.get(Parameter.RFID_KEY)
							+ "相关箱袋信息";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				// 判断箱袋状态是否有误
				if (Constant.BoxStatus.ONPASSAGE.equals(stoboxinfo.getBoxStatus())
						|| Constant.BoxStatus.BANK_OUTLETS.equals(stoboxinfo.getBoxStatus())) {
					// 箱子状态为在途或者在网点，不能补录
					logger.debug("RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId() + "的箱子状态有误");
					String strMessageContent = "RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId()
							+ "的箱子状态有误";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				// 未扫描箱袋不更新
				if (AllocationConstant.ScanFlag.NoScan.equals(map.get(Parameter.SCAN_FLAG_KEY).toString())) {
					continue;
				}
				// 强制补录箱袋处理
				if (AllocationConstant.ScanFlag.UnknownAdditional.equals(map.get(Parameter.SCAN_FLAG_KEY).toString())) {
					// 箱袋处理
					handoverId = updateByAdditional(map, headInfo);
					// 强制补录授权处理
					if (Collections3.isEmpty(managerList)) {
						logger.debug("RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId() + "的授权列表为空");
						String strMessageContent = "RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId()
								+ "的授权列表为空";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					} else {
						for (Map<String, Object> managerMap : managerList) {
							String strBudId = null;
							String strBudName = null;
							String strBudHandType = null;
							String strBudReason = null;
							AllHandoverDetail handoverDetail = new AllHandoverDetail();
							// 授权原因
							strBudReason = managerMap.get(Parameter.REASON_KEY).toString();

							// 取得授权人用户名
							User user = UserUtils.getByLoginName(managerMap.get(Parameter.OPT_USER_ID_KEY).toString());
							if (user == null) {
								logger.debug("库外箱袋入库(出库)确认接口-------- 授权人信息取得失败。授权人ID="
										+ managerMap.get(Parameter.OPT_USER_ID_KEY).toString());
								String strMessageContent = "授权人信息取得失败。";
								throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99,
										strMessageContent);
							}
							StoEscortInfo stoEscortInfo = stoEscortInfoService.findByUserId(user.getId());
							// 授权人ID
							strBudId = stoEscortInfo.getId();
							strBudHandType = AllocationConstant.HandoverType.SystemLogin;
							strBudName = stoEscortInfo.getEscortName();
							// 设置
							handoverDetail.setHandoverId(handoverId);

							// 设置交接明细表信息
							handoverDetail.setDetailId(IdGen.uuid());
							handoverDetail.setEscortId(strBudId.toString());
							handoverDetail.setEscortName(strBudName.toString());
							handoverDetail.setEscortName(strBudName.toString());
							handoverDetail
									.setOperationType(AllocationConstant.OperationType.SCANNING_DOOR_AUTHORIZATION);
							handoverDetail.setType(strBudHandType.toString());
							handoverDetail.setManagerReason(strBudReason.toString());
							handoverInfo.getDetailList().add(handoverDetail);
						}
						// 设置交接表内容
						allAllocateInfo.setAllHandoverInfo(handoverInfo);
						User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
						allAllocateInfo.setLoginUser(loginUser);
						// 补录未知箱子情况下
						allocationService.insertHandoverDetail(allAllocateInfo);
					}
					continue;
				}
				// 正常状态下routeId为空
				if (!AllocationConstant.ScanFlag.UnknownAdditional.equals(map.get(Parameter.SCAN_FLAG_KEY).toString())
						&& (headInfo.get(Parameter.ROUTE_ID_KEY) == null
								|| StringUtils.isBlank(headInfo.get(Parameter.ROUTE_ID_KEY).toString()))) {

					logger.debug("库外箱袋出库(入库)确认接口-------- 线路ID:" + map.get(Parameter.ROUTE_ID_KEY));
					String strMessageContent = "库外箱袋出库(入库)确认接口-------- 线路ID:" + map.get(Parameter.ROUTE_ID_KEY);
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				// 设置箱袋明细
				boxDetail = new AllAllocateDetail();
				boxDetail.setBoxNo(stoboxinfo.getId().toString());
				boxDetail.setRfid(stoboxinfo.getRfid().toString());
				boxDetail.setScanFlag(map.get(Parameter.SCAN_FLAG_KEY).toString());
				boxDetail.setScanDate(new Date());
				allAllocateInfo.getAllDetailList().add(boxDetail);
				// 检查钞箱编号在本线路中是否存在
				boolean checkFlag = checkInStoreBoxNoExists(allAllocateInfo);
				if (!checkFlag) {
					logger.debug("RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId() + "的箱袋信息有误");
					String strMessageContent = "RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId()
							+ "的箱袋信息有误";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}

			}
		}
		// 设定授权人信息
		if (!StringUtils.isBlank(handoverId)) {
			return allAllocateInfo;
		}
		if (!Collections3.isEmpty(managerList)) {
			String handoverInfoId = null;
			for (Map<String, Object> map : managerList) {
				String strBudId = null;
				String strBudName = null;
				String strBudHandType = null;
				String strBudReason = null;
				AllHandoverDetail handoverDetail = new AllHandoverDetail();
				// 授权原因
				strBudReason = map.get(Parameter.REASON_KEY).toString();

				// 取得授权人用户名
				User user = UserUtils.getByLoginName(map.get(Parameter.OPT_USER_ID_KEY).toString());
				if (user == null) {
					logger.debug(
							"库外箱袋入库(出库)确认接口-------- 授权人信息取得失败。授权人ID=" + map.get(Parameter.OPT_USER_ID_KEY).toString());
					String strMessageContent = "授权人信息取得失败。";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				StoEscortInfo stoEscortInfo = stoEscortInfoService.findByUserId(user.getId());
				// 授权人ID
				strBudId = stoEscortInfo.getId();
				strBudHandType = AllocationConstant.HandoverType.SystemLogin;
				strBudName = stoEscortInfo.getEscortName();
				if (handoverInfoId != null) {
					handoverDetail.setHandoverId(handoverInfoId);
				} else {
					// 设置交接主表主表信息
					handoverInfoId = IdGen.uuid();
					handoverInfo.setHandoverId(handoverInfoId);
					handoverInfo.setCreateDate(new Date());
					handoverDetail.setHandoverId(handoverInfoId);
				}
				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setEscortId(strBudId.toString());
				handoverDetail.setEscortName(strBudName.toString());
				handoverDetail.setEscortName(strBudName.toString());
				handoverDetail.setOperationType(AllocationConstant.OperationType.SCANNING_DOOR_AUTHORIZATION);
				handoverDetail.setType(strBudHandType.toString());
				handoverDetail.setManagerReason(strBudReason.toString());
				handoverInfo.getDetailList().add(handoverDetail);
			}
		} else {
			// 设置交接主表主表信息
			handoverInfo.setHandoverId(IdGen.uuid());
			handoverInfo.setCreateDate(new Date());
		}
		// 设置交接表内容
		allAllocateInfo.setAllHandoverInfo(handoverInfo);
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		allAllocateInfo.setLoginUser(loginUser);

		return allAllocateInfo;
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月14日
	 * 
	 *          更新扫描标示为补录的箱子
	 * @param map
	 */
	@Transactional(readOnly = false)
	public String updateByAdditional(Map<String, Object> map, Map<String, Object> headInfo) {
		StoBoxInfo boxInfo = new StoBoxInfo();
		if (map.get(Parameter.RFID_KEY) != null) {
			boxInfo.setRfid(map.get(Parameter.RFID_KEY).toString());
		}
		// 根据箱号查询箱子
		List<StoBoxInfo> stoboxinfoList = stoBoxInfoService.getBindingBoxInfo(boxInfo);
		if (Collections3.isEmpty(stoboxinfoList)) {
			String strMessageContent = "RFID为【" + map.get(Parameter.RFID_KEY).toString() + "】的箱袋信息不存在！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		StoBoxInfo stoboxinfo = stoboxinfoList.get(0);
		// 拿到箱子的归属机构
		String officeId = stoboxinfo.getOffice().getId();
		// 根据机构查询线路
		StoRouteInfo stoRouteInfo = stoRouteInfoService.searchStoRouteInfoByOfficeId(officeId);
		//当前箱号所属机构未绑定线路提示
		if (stoRouteInfo == null) {
			super.initLocale(headInfo);
			// message.E2067=[操作失败]：箱袋所属机构未绑定线路！
			String strMessageContent = msg.getMessage("message.E2067", new String[] { boxInfo.getRfid() }, locale);
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		
		AllAllocateInfo allallocateInfo = new AllAllocateInfo();
		// 设置线路Id
		allallocateInfo.setRouteId(stoRouteInfo.getId());
		// 设置业务类型为现金下拨(预约)
		allallocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		// 设置状态为已扫描或者在途
		List<String> statusList = Lists.newArrayList();
		statusList.add(AllocationConstant.Status.HandoverTodo);
		statusList.add(AllocationConstant.Status.Onload);
		allallocateInfo.setStatuses(statusList);
		// 设置当天开始时间
		allallocateInfo.setCreateTimeStart(new Date());
		allallocateInfo
				.setSearchDateStart(DateUtils.formatDate(DateUtils.getDateStart(allallocateInfo.getCreateTimeStart()),
						AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 设置当天结束时间
		allallocateInfo.setCreateTimeEnd(new Date());
		allallocateInfo.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(allallocateInfo.getCreateTimeEnd()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 根据业务类型、状态、线路Id查询最近一条流水
		AllAllocateInfo allocate = allocationService.getMaxdateByrouteId(allallocateInfo);
		if (allocate == null) {
			// 最近一条任务重不包含该箱子所在的机构，不能追加
			String strMessageContent = "RFID为【" + stoboxinfo.getRfid() + "】,箱号为【" + stoboxinfo.getId()
					+ "】的箱袋归属机构不存在相对应的流水，无法进行出库确认！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		// 拿到最近一条流水的交接ID
		String handoverId = allocate.getStoreHandoverId();
		// 根据交接Id获取同一批次流水
		List<AllAllocateInfo> allocateList = allocationService.getAllAllocateInfoByHandoverId(handoverId);
		List<String> allIdsList = Lists.newArrayList();
		boolean flag = false;
		for (AllAllocateInfo info : allocateList) {
			// 获取所有AllID
			allIdsList.add(info.getAllId());
			// 判断该批次所有流水中是否包含该箱子的归属机构
			if (officeId.equals(info.getrOffice().getId())) {
				flag = true;
				continue;
			}
		}
		AllAllocateInfo allallocate = new AllAllocateInfo();
		allallocate.setAllIds(allIdsList);
		// 判断箱子归属机构在最近的一条任务重是否有预约信息
		List<String> boxList = Lists.newArrayList();
		if (!flag) {
			// 最近一条任务重不包含该箱子所在的机构，不能追加
			String strMessageContent = "RFID为【" + stoboxinfo.getRfid() + "】,箱号为【" + stoboxinfo.getId()
					+ "】的箱袋归属机构不在该机构所在线路的最近一条流水中，无法进行出库确认！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		} else {
			// 通过交接ID获取的所有allId来获取所有箱号
			List<AllAllocateDetail> detailList = allocationService.getBoxNoByAllId(allallocate);
			if (Collections3.isEmpty(detailList)) {
				String strMessageContent = "RFID为【" + stoboxinfo.getRfid() + "】,箱号为【" + stoboxinfo.getId()
						+ "】的箱袋无法进行出库确认！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			for (AllAllocateDetail detail : detailList) {
				if (detail.getBoxNo().equals(stoboxinfo.getId())) {
					detail.setScanFlag(AllocationConstant.ScanFlag.UnknownAdditional);
					detail.setScanDate(new Date());
					boxList.add(stoboxinfo.getId().toString());
					int result = allocationService.updateDetailStatusByBoxNo(detail);
					if (result == 0) {
						String strMessageContent = "流水单号：" + allocate.getAllId() + "对应箱袋RFID为(" + stoboxinfo.getRfid()
								+ "),箱袋编号为(" + stoboxinfo.getId() + ")状态(调拨详细表)更新失败！";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
					// 更新调拨主表及箱袋信息表
					updateAllocateInfoAndStoBoxInfo(headInfo, detail.getAllId(), boxList);
					return handoverId;

				}
			}
			Office roffice = new Office();
			roffice.setId(officeId);
			allallocateInfo.setrOffice(roffice);
			// 通过线路以及登记机构获取到最近一条流水
			AllAllocateInfo allocatebyrOfficeId = allocationService.getMaxdateByrouteId(allallocateInfo);
			if (allocatebyrOfficeId.getAllId() == null && allocatebyrOfficeId.getAllId().equals("")) {
				String strMessageContent = "流水单号：" + allocate.getAllId() + "对应箱袋RFID为(" + stoboxinfo.getRfid()
						+ "),箱袋编号为(" + stoboxinfo.getId() + ")状态(调拨详细表)更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			} else {
				AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
				allAllocateDetail.setBoxNo(stoboxinfo.getId().toString());
				allAllocateDetail.setAllDetailId(IdGen.uuid());
				// 流水主键设置为最近一条流水的主键
				allAllocateDetail.setAllId(allocatebyrOfficeId.getAllId());
				allAllocateDetail.setAmount(stoboxinfo.getBoxAmount());
				allAllocateDetail.setBoxType(stoboxinfo.getBoxType());
				allAllocateDetail.setOutDate(stoboxinfo.getOutDate());
				allAllocateDetail.setRfid(stoboxinfo.getRfid());
				allAllocateDetail.setScanFlag(AllocationConstant.ScanFlag.UnknownAdditional);
				allAllocateDetail.setScanDate(new Date());
				boxList.add(stoboxinfo.getId().toString());
				// 新增一条调拨详细流水
				int result = allocationService.insertAdditional(allAllocateDetail);
				if (result == 0) {
					String strMessageContent = "流水单号：" + allocate.getAllId() + "对应箱袋RFID为(" + stoboxinfo.getRfid()
							+ "),箱袋编号为(" + stoboxinfo.getId() + ")状态(调拨详细表)更新失败！";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				// 更新调拨主表及箱袋信息表
				updateAllocateInfoAndStoBoxInfo(headInfo, allocatebyrOfficeId.getAllId(), boxList);
				return handoverId;
			}
		}

	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年7月27日
	 * 
	 *          更新调拨主表及箱袋信息表
	 * @param map
	 */
	@Transactional(readOnly = false)
	public void updateAllocateInfoAndStoBoxInfo(Map<String, Object> headInfo, String allId, List<String> boxList) {
		// 通过 AllId获取流水
		AllAllocateInfo allocateInfo = allocationService.getAllocate(allId);
		// 通过AllId获取所有该流水下的调拨详细
		List<AllAllocateDetail> allocatedetailList = allocateInfo.getAllDetailList();
		allocateInfo.setRegisterNumber(allocatedetailList.size());
		// 设置更新时间
		allocateInfo.setUpdateDate(new Date());
		// 设置更新者信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		allocateInfo.setUpdateBy(loginUser);
		// 更新调拨主表
		allocationService.update(allocateInfo);
		// 更新箱袋信息
		int iUpdateCnt = StoreCommonUtils.updateBoxStatusBatch(boxList, AllocationConstant.Place.onPassage, loginUser);
		if (iUpdateCnt == 0) {
			String strMessageContent = "箱袋位置(箱袋信息表)更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
	}
}
