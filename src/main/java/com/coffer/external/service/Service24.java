package com.coffer.external.service;

import java.math.BigDecimal;
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
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
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
 * Title: Service24
 * <p>
 * Description: 库外箱袋入库确认接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service24")
@Scope("singleton")
public class Service24 extends HardwardBaseService {

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
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          24 库外箱袋入库确认接口
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

		// 取得箱袋信息
		String checkResult = this.getBoxInOutStoreInfo(paramMap, map);
		if (Constant.FAILED.equals(checkResult)) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(map);
		}
		// 处理箱袋 (强制补录状态下箱袋在此方法中执行)
		AllAllocateInfo boxInfoCondition = updateByScanFlag(paramMap, map);
		// 更新箱袋信息
		if (paramMap.get(Parameter.ROUTE_ID_KEY) != null
				&& StringUtils.isNotBlank(paramMap.get(Parameter.ROUTE_ID_KEY).toString())) {
			allocationService.boxInStoreConfirm(paramMap, boxInfoCondition);
		}
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(map);
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          库外箱袋入库确认接口（检查钞箱编号在本线路中是否存在） 如果存在设定更新条件
	 * @param boxInfo
	 *            钞箱信息
	 * @return true：存在， false：不存在
	 */
	private boolean checkInStoreBoxNoExists(AllAllocateInfo boxInfo) {

		// 根据路线查询钞箱新息
		AllAllocateInfo conditionInfo = new AllAllocateInfo();
		// 查询条件：业务类型= 库外箱袋调拨
		conditionInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		// 设置状态 = 在途
		conditionInfo.setStatus(AllocationConstant.Status.Onload);
		// 取出线路中的钞箱
		List<AllAllocateInfo> routeInfoList = allocationService.findBoxInfoList(conditionInfo);
		if (Collections3.isEmpty(routeInfoList)) {
			String strMessageContent = "不存在需要上缴入库的流水";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
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
			boxInfo.setAllId(routeBoxNoMap.get(inBoxDetail.getBoxNo()).getAllId());
		}
		// 更新条件：业务类型=库外箱袋调拨
		boxInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		// 更新条件:更新状态=在途
		boxInfo.setStatus(AllocationConstant.Status.Onload);

		return true;
	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          从参数取得箱袋信息(出入库)
	 * @param headInfo
	 *            箱袋信息
	 * @return 箱袋信息
	 */
	@SuppressWarnings("unchecked")
	private String getBoxInOutStoreInfo(Map<String, Object> headInfo, Map<String, Object> msgMap) {
		// 授权人信息列表
		List<Map<String, Object>> managerList = null;
		// 箱袋编号列表
		List<Map<String, Object>> boxList = null;
		// 取得箱袋编号列表
		if (headInfo.get(Parameter.BOX_LIST_KEY) != null) {
			boxList = (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY);
			if (Collections3.isEmpty(boxList)) {
				logger.debug("库外箱袋入库(出库)确认接口-------- 箱袋编号列表为空");
				msgMap.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 箱袋编号列表为空");
				return Constant.FAILED;
			} else {
				for (Map<String, Object> map : boxList) {
					if ((map.get(Parameter.RFID_KEY) == null
							|| StringUtils.isBlank(map.get(Parameter.RFID_KEY).toString()))) {
						logger.debug("库外箱袋入库(出库)确认接口--------箱袋RFID为空");
						msgMap.put(Parameter.ERROR_MSG_KEY, "箱袋RFID为空");
						return Constant.FAILED;
					}
					if (map.get(Parameter.SCAN_FLAG_KEY) == null
							|| StringUtils.isBlank(map.get(Parameter.SCAN_FLAG_KEY).toString())) {
						logger.debug("库外箱袋入库(出库)确认接口-------- 扫描状态为空");
						msgMap.put(Parameter.ERROR_MSG_KEY, "扫描状态为空");
						return Constant.FAILED;
					}
				}
			}
		} else {
			logger.debug("库外箱袋入库(出库)确认接口-------- 箱袋编号列表为空");
			msgMap.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 箱袋编号列表为空");
			return Constant.FAILED;
		}
		// 取得授权人信息列表
		if (headInfo.get(Parameter.MANAGER_LIST_KEY) != null) {
			managerList = (List<Map<String, Object>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
			for (Map<String, Object> map : managerList) {
				if (map.get(Parameter.OPT_USER_ID_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.OPT_USER_ID_KEY).toString())) {
					logger.debug("库外箱袋入库(出库)确认接口-------- 授权人ID:" + map.get(Parameter.OPT_USER_ID_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY, "授权人ID有误");
					return Constant.FAILED;
				}
				if (map.get(Parameter.REASON_KEY) == null
						|| StringUtils.isBlank(map.get(Parameter.REASON_KEY).toString())) {
					logger.debug("库外箱袋入库(出库)确认接口-------- 授权原因:" + map.get(Parameter.REASON_KEY));
					msgMap.put(Parameter.ERROR_MSG_KEY, "授权原因有误");
					return Constant.FAILED;
				}
			}
		}
		// 取得更新者ID
		if (headInfo.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("库外箱袋入库(出库)确认接口-------- 更新者ID为空");
			msgMap.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 更新者ID为空");
			return Constant.FAILED;
		}
		// 取得更新者名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("库外箱袋入库(出库)确认接口-------- 更新者名为空");
			msgMap.put(Parameter.ERROR_MSG_KEY, "库外箱袋入库(出库)确认接口-------- 更新者名为空");
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
	private AllAllocateInfo updateByScanFlag(Map<String, Object> headInfo, Map<String, Object> resultmap) {
		AllAllocateItem item = null;
		// 流水号集合（更新库存用）
		List<String> allIdList = Lists.newArrayList();
		// 箱袋编号列表
		List<Map<String, Object>> boxList = null;
		// 授权人信息列表
		List<Map<String, Object>> managerList = null;
		// 设定交接信息
		AllHandoverInfo handoverInfo = new AllHandoverInfo();
		// 设定主表信息
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		// 设定箱袋信息
		AllAllocateDetail boxDetail = null;
		String handoverId = "";
		// 取得路线ID
		if (headInfo.get(Parameter.ROUTE_ID_KEY) != null
				&& StringUtils.isNotBlank(headInfo.get(Parameter.ROUTE_ID_KEY).toString())) {
			allAllocateInfo.setRouteId(headInfo.get(Parameter.ROUTE_ID_KEY).toString());
		}
		// 取得授权人信息列表
		if (headInfo.get(Parameter.MANAGER_LIST_KEY) != null) {
			managerList = (List<Map<String, Object>>) headInfo.get(Parameter.MANAGER_LIST_KEY);
		}

		boxList = (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY);
		for (Map<String, Object> map : boxList) {

			StoBoxInfo boxInfo = new StoBoxInfo();
			boxInfo.setRfid(map.get(Parameter.RFID_KEY).toString());
			// 根据箱号查询箱子
			List<StoBoxInfo> stoboxinfoList = stoBoxInfoService.getBindingBoxInfo(boxInfo);
			if (stoboxinfoList.isEmpty()) {
				logger.debug("箱子信息不存在,RFID:" + boxInfo.getRfid());
				String strMessageContent = "箱子信息不存在,RFID:" + boxInfo.getRfid();
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			StoBoxInfo stoboxinfo = stoboxinfoList.get(0);
			if (stoboxinfo == null) {
				logger.debug("库外箱袋入库(出库)确认接口-------- 未查询到RFID:" + map.get(Parameter.RFID_KEY) + "相关箱袋信息");
				String strMessageContent = "未查询到RFID:" + map.get(Parameter.RFID_KEY) + "相关箱袋信息";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			// 判断箱袋状态是否有误
			if (Constant.BoxStatus.COFFER.equals(stoboxinfo.getBoxStatus())) {
				// 箱子状态在库房，不能补录
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

						handoverDetail.setHandoverId(handoverId);

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
				String strMessageContent = "线路ID为空";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			// 设置箱袋明细
			boxDetail = new AllAllocateDetail();
			boxDetail.setBoxNo(stoboxinfo.getId().toString());
			boxDetail.setRfid(stoboxinfo.getRfid().toString());
			if (!Constant.BoxType.BOX_TAIL.equals(stoboxinfo.getBoxType())) {
				boxDetail.setAmount(stoboxinfo.getBoxAmount());
			}
			boxDetail.setScanFlag(map.get(Parameter.SCAN_FLAG_KEY).toString());
			boxDetail.setScanDate(new Date());
			allAllocateInfo.getAllDetailList().add(boxDetail);
			// 检查钞箱编号在本线路中是否存在
			boolean checkFlag = checkInStoreBoxNoExists(allAllocateInfo);
			if (!checkFlag) {
				logger.debug("RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId() + "的箱袋信息有误");
				String strMessageContent = "RFID为：" + stoboxinfo.getRfid() + "," + "箱号为：" + stoboxinfo.getId()
						+ "的箱袋不存在于任何一条上缴流水中或该箱子所在流水已撤回";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			if (!allIdList.contains(allAllocateInfo.getAllId())) {
				AllAllocateInfo info = allocationService.getAllocate(allAllocateInfo.getAllId());
				// 设置用户
				User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
				User user = new User();
				user.setName(loginUser.getName());
				user.setId(loginUser.getId());
				user.setOffice(info.getaOffice());
				allAllocateInfo.setLoginUser(user);
				item = new AllAllocateItem();
				item.setAllocationInfo(allAllocateInfo);
				// 根据allId查询所有items
				List<AllAllocateItem> itemList = allocationService.getAllocateItemByAllId(item);
				for (AllAllocateItem AllItem : itemList) {
					// 取得goodsId
					String strMapKey = allocationService.getAllAllocateItemMapKey1(AllItem);
					allAllocateInfo.getAllAllocateItemMap().put(strMapKey, AllItem);
				}
				if (!allAllocateInfo.getAllAllocateItemMap().isEmpty()) {
					// 更新库存
					allocationService.updateSurplusStore(allAllocateInfo, allAllocateInfo.getAllAllocateItemMap(),
							allAllocateInfo.getLoginUser().getOffice().getId());
				}
				allIdList.add(allAllocateInfo.getAllId());
			}

		}

		// 设定授权人信息
		if (!handoverId.equals("")) {
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
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          更新扫描标示为补录的箱子
	 * @param map
	 */
	private String updateByAdditional(Map<String, Object> map, Map<String, Object> headInfo) {

		StoBoxInfo boxInfo = new StoBoxInfo();
		boxInfo.setRfid(map.get(Parameter.RFID_KEY).toString());
		// 根据箱号查询箱子
		List<StoBoxInfo> stoBoxInfoList = stoBoxInfoService.getBindingBoxInfo(boxInfo);
		if (Collections3.isEmpty(stoBoxInfoList)) {
			String strMessageContent = "RFID为【" + map.get(Parameter.RFID_KEY).toString() + "】的箱袋信息不存在！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
		StoBoxInfo stoboxinfo = stoBoxInfoList.get(0);
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
		allallocateInfo.setRouteId(stoRouteInfo.getId());

		allallocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);

		List<String> statusList = Lists.newArrayList();
		statusList.add(AllocationConstant.Status.HandoverTodo);
		statusList.add(AllocationConstant.Status.Finish);
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
			// 当天没有该箱子所在机构的调拨任务
			String strMessageContent = "RFID为【" + stoboxinfo.getRfid() + "】,箱号为【" + stoboxinfo.getId()
					+ "】的箱袋归属机构当天没有调拨任务，无法进行入库确认！";
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
					+ "】的箱袋归属机构不在该机构所在线路的最近一条流水中，无法进行入库确认！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		} else {
			// 获取所有箱号
			List<AllAllocateDetail> detailList = allocationService.getBoxNoByAllId(allallocate);
			if (Collections3.isEmpty(detailList)) {
				String strMessageContent = "RFID为【" + stoboxinfo.getRfid() + "】,箱号为【" + stoboxinfo.getId()
						+ "】的箱袋无法进行出入库确认！";
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
					// 更新调拨主表和箱袋表信息
					updateAllocateInfoAndStoBoxInfo(headInfo, detail.getAllId(), boxList);
					return handoverId;

				}
			}
			// 该箱子在Detail表中不存在的情况下，插入一条
			Office roffice = new Office();
			roffice.setId(officeId);
			allallocateInfo.setrOffice(roffice);
			AllAllocateInfo allocatebyrOfficeId = allocationService.getMaxdateByrouteId(allallocateInfo);
			if (allocatebyrOfficeId.getAllId() == null && allocatebyrOfficeId.getAllId().equals("")) {
				String strMessageContent = "流水单号：" + allocate.getAllId() + "对应箱袋RFID为(" + stoboxinfo.getRfid()
						+ "),箱袋编号为(" + stoboxinfo.getId() + ")状态(调拨详细表)更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			} else {
				AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
				allAllocateDetail.setBoxNo(stoboxinfo.getId().toString());
				allAllocateDetail.setAllDetailId(IdGen.uuid());
				allAllocateDetail.setAllId(allocatebyrOfficeId.getAllId());
				allAllocateDetail.setAmount(stoboxinfo.getBoxAmount());
				allAllocateDetail.setRfid(stoboxinfo.getRfid());
				allAllocateDetail.setBoxType(stoboxinfo.getBoxType());
				allAllocateDetail.setOutDate(stoboxinfo.getOutDate());
				allAllocateDetail.setScanFlag(AllocationConstant.ScanFlag.UnknownAdditional);
				allAllocateDetail.setScanDate(new Date());
				boxList.add(stoboxinfo.getId().toString());
				int result = allocationService.insertAdditional(allAllocateDetail);
				if (result == 0) {
					String strMessageContent = "流水单号：" + allocate.getAllId() + "对应箱袋RFID为(" + stoboxinfo.getRfid()
							+ "),箱袋编号为(" + stoboxinfo.getId() + ")状态(调拨详细表)更新失败！";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
				// 更新调拨主表和箱袋表信息
				updateAllocateInfoAndStoBoxInfo(headInfo, allocatebyrOfficeId.getAllId(), boxList);
				return handoverId;
			}
		}

	}

	/**
	 * 
	 * @author liuyaowen
	 * @version 2017年7月14日
	 * 
	 *          更新调拨主表及箱袋信息表
	 * @param map
	 */
	@Transactional(readOnly = false)
	public void updateAllocateInfoAndStoBoxInfo(Map<String, Object> headInfo, String allId, List<String> boxList) {
		// 根据allId查询对应的AllAllocateInfo
		AllAllocateInfo allocateInfo = allocationService.getAllocate(allId);
		// 根据allId查询Detail集合
		List<AllAllocateDetail> allocatedetailList = allocateInfo.getAllDetailList();
		// 更新金额
		BigDecimal amount = new BigDecimal(0);
		for (AllAllocateDetail allocatedetail : allocatedetailList) {
			if (allocatedetail.getAmount() == null) {
				continue;
			} else {
				amount = amount.add(allocatedetail.getAmount()).setScale(2);
			}
		}
		// 设置接收金额
		allocateInfo.setConfirmAmount(amount);
		// 设置接收数量
		allocateInfo.setAcceptNumber(allocatedetailList.size());
		// 设置更新时间
		allocateInfo.setUpdateDate(new Date());
		// 设置更新者信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		allocateInfo.setUpdateBy(loginUser);
		allocationService.update(allocateInfo);
		// 更新箱袋信息
		int iUpdateCnt = StoreCommonUtils.updateBoxStatusBatch(boxList, AllocationConstant.Place.StoreRoom, loginUser);
		if (iUpdateCnt == 0) {
			String strMessageContent = "箱袋位置(箱袋信息表)更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
	}
}
