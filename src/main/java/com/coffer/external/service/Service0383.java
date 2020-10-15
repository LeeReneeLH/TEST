package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
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
import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmBoxModService;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.config.Global;
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
 * Title: Service0383
 * <p>
 * Description:PDA库外出库扫描接口
 * </p>
 * 
 * @author qph
 * @date 2017年11月9日
 */
@Component("Service0383")
@Scope("singleton")
public class Service0383 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private StoBoxInfoService stoBoxInfoService;

	@Autowired
	private AtmPlanInfoService atmPlanInfoService;

	@Autowired
	private AtmBoxModService atmBoxModService;

	/**
	 * 
	 * @author qph
	 * @version 2017年11月9日
	 * 
	 *          PDA库外出库扫描接口
	 * @param requestMap
	 * @param serviceNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> respMap = Maps.newHashMap();
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		List<String> errorBoxList = Lists.newArrayList();
		Map<String, Object> modtypeMap = Maps.newHashMap();
		String addPlanId = (String) paramMap.get(Parameter.ADD_PLAN_ID_KEY);
		String officeId = (String) paramMap.get(Parameter.OFFICE_ID_KEY);
		Office office = officeService.get(officeId);
		List<Map<String, Object>> rfidList = Lists.newArrayList();
		if (paramMap.get(Parameter.BOX_LIST_KEY) != null) {
			rfidList = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);
		}
		// 可以执行只清机流程，无箱袋信息
		if (StringUtils.isNotBlank(addPlanId) && office != null) {
			// 验证钞箱机构 修改人：XL 修改时间：2017-12-18 begin
			List<String> errorBoxNoList = Lists.newArrayList();
			for (Map<String, Object> rfidMap : rfidList) {
				StoBoxInfo stoBoxInfo = new StoBoxInfo();
				// 设置RFID
				stoBoxInfo.setRfid((String) rfidMap.get(Parameter.RFID_KEY));
				// 获取钞箱信息
				stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(stoBoxInfo);
				if (stoBoxInfo == null) {
					errorBoxNoList.add((String) rfidMap.get(Parameter.RFID_KEY));
				} else if (!officeId.equals(stoBoxInfo.getOffice().getId())) {
					errorBoxList.add((String) rfidMap.get(Parameter.RFID_KEY));
				}
			}
			if (!Collections3.isEmpty(errorBoxNoList)) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put(Parameter.ERROR_MSG_KEY,
						"编号为：" + Collections3.convertToString(errorBoxNoList, ",") + "的钞箱不存在");
				return gson.toJson(respMap);
			}
			if (!Collections3.isEmpty(errorBoxList)) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put(Parameter.ERROR_MSG_KEY,
						"箱袋：" + Collections3.convertToString(errorBoxList, ",") + "的钞箱机构错误");
				return gson.toJson(respMap);
			}
			// end
			// 查询当前任务是否已经登记
			AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
			allAllocateInfo.setRouteId(addPlanId);
			List<AllAllocateInfo> allAllocateInfoList = allocationService.findAllocation(allAllocateInfo);

			if (Collections3.isEmpty(allAllocateInfoList)) {
				// 未登记封装信息
				// 获取加钞计划加钞金额和加钞数量
				AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
				atmPlanInfo.setAddPlanId(addPlanId);
				List<AtmPlanInfo> atmPlanInfoList = atmPlanInfoService.findAddPlanList(atmPlanInfo);
				if (!Collections3.isEmpty(atmPlanInfoList)) {
					atmPlanInfo = atmPlanInfoList.get(0);
					// 只清机不加钞
					if (atmPlanInfo.getBoxNum() == 0 && Collections3.isEmpty(rfidList)) {
						atmPlanInfo.setStatus(ATMConstant.PlanStatus.PLAN_OUT);
						atmPlanInfoService.updateStatus(atmPlanInfo);
						respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
					} else {
						// 业务种别
						allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.ATM_BOX_HANDOUT);
						// 出入库类型
						// allAllocateInfo.setInoutType(AllocationConstant.InoutType.Out);
						// 登记状态
						allAllocateInfo.setStatus(AllocationConstant.AtmBusinessStatus.Register);
						// 登记机构
						allAllocateInfo.setrOffice(office);
						// 登记时间
						allAllocateInfo.setCreateDate(new Date());
						// 登记人
						User loginUser = UserUtils.get(paramMap.get(Parameter.USER_ID_KEY).toString());
						allAllocateInfo.setCreateBy(loginUser);
						// 更新人
						allAllocateInfo.setUpdateBy(loginUser);
						// 更新时间
						allAllocateInfo.setUpdateDate(new Date());
						// 有效表示(有效)
						allAllocateInfo.setDelFlag(AllocationConstant.deleteFlag.Valid);
						// 设置登记钞箱数量
						allAllocateInfo.setRegisterNumber(atmPlanInfo.getBoxNum());
						// 设置加钞金额总额
						allAllocateInfo.setRegisterAmount(atmPlanInfo.getAddAmount().multiply(new BigDecimal(10000)));
						// 设置登记总额
						allAllocateInfo.setConfirmAmount(atmPlanInfo.getAddAmount().multiply(new BigDecimal(10000)));
						// 箱袋登记明细
						allAllocateInfo.setAllDetailList(getAllAllocateDetail(rfidList, errorBoxList, modtypeMap));

						// 验证箱袋信息是否正确，首先验证箱子编号及状态是否正确；其次验证钞箱类型数量与计划是否匹配
						if (Collections3.isEmpty(errorBoxList)) {
							// 统计加钞计划中钞箱类型数量
							String errorModMessage = validateModNumAndBoxNum(modtypeMap, addPlanId);
							// 验证箱袋类型数量
							if (StringUtils.isBlank(errorModMessage)) {
								// 修改钞箱状态为在途
								updateBoxStatusOut(allAllocateInfo, Constant.BoxStatus.ATM_BOX_STATUS_PREPARE_OUT);

								allocationService.saveAtmAllocation(allAllocateInfo);
								respMap.put(Parameter.RESULT_FLAG_KEY,
										ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
							} else {
								// 箱袋类型数量存在不一致
								respMap.put(Parameter.RESULT_FLAG_KEY,
										ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
								respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E21);
								respMap.put(Parameter.ERROR_MSG_KEY, errorModMessage);
							}
						} else {
							// 将错误箱号返回
							respMap.put(Parameter.RESULT_FLAG_KEY,
									ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
							respMap.put(Parameter.ERROR_MSG_KEY,
									"箱袋：" + Collections3.convertToString(errorBoxList, ",") + "状态有误");
							// respMap.put("errorList", errorBoxList);
						}
					}

				} else {
					// 加钞计划编号异常，查询不到加钞计划
					respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E20);
				}
			} else {
				allAllocateInfo = allAllocateInfoList.get(0);

				if (AllocationConstant.Status.Register.equals(allAllocateInfo.getStatus())) {
					// 箱袋登记明细
					List<AllAllocateDetail> allocationDetailList = getAllAllocateDetailByUpdate(
							allAllocateInfo.getAllDetailList(), rfidList, errorBoxList, modtypeMap);
					// 验证箱袋信息是否正确，首先验证箱子编号及状态是否正确；其次验证钞箱类型数量与计划中类型数量是否匹配
					if (Collections3.isEmpty(errorBoxList)) {
						// 验证箱袋数量
						String erroModMessage = validateModNumAndBoxNum(modtypeMap, addPlanId);
						// 验证箱袋类型数量
						if (StringUtils.isBlank(erroModMessage)) {

							User loginUser = UserUtils.get(paramMap.get(Parameter.USER_ID_KEY).toString());
							// 更新人
							allAllocateInfo.setUpdateBy(loginUser);
							allAllocateInfo.setUpdateDate(new Date());

							/* 分配同一钞箱，状态不恢复空箱状态 修改人：xl 修改时间：2018-01-10 begin */
							for (Iterator<AllAllocateDetail> iterator = allAllocateInfo.getAllDetailList()
									.iterator(); iterator.hasNext();) {
								AllAllocateDetail allAllocateDetail = (AllAllocateDetail) iterator.next();
								for (AllAllocateDetail allAllocateDetailNew : allocationDetailList) {
									// 分配同一钞箱，移除
									if (allAllocateDetail.getRfid().equals(allAllocateDetailNew.getRfid())) {
										iterator.remove();
									}
								}
							}
							/* end */

							// 将原绑定箱袋状态回复加钞状态
							updateBoxStatusOut(allAllocateInfo, Constant.BoxStatus.ATM_BOX_STATUS_EMPTY);

							allAllocateInfo.setAllDetailList(allocationDetailList);

							// 现有钞箱状态修改在途
							updateBoxStatusOut(allAllocateInfo, Constant.BoxStatus.ATM_BOX_STATUS_PREPARE_OUT);

							allocationService.saveAtmAllocation(allAllocateInfo);
							respMap.put(Parameter.RESULT_FLAG_KEY,
									ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
						} else {
							// 箱袋类型数量存在不一致
							respMap.put(Parameter.RESULT_FLAG_KEY,
									ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E21);
							respMap.put(Parameter.ERROR_MSG_KEY, erroModMessage);
						}
					} else {
						// 将错误箱号返回
						respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
						respMap.put(Parameter.ERROR_MSG_KEY,
								"箱袋：" + Collections3.convertToString(errorBoxList, ",") + "状态有误");
					}

				} else {
					// 当前计划已经过扫描门，不可以重复绑定箱袋信息
					respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E16);
				}
			}

		} else {
			// 参数异常
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}

		return gson.toJson(respMap);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年11月22日
	 * 
	 * 
	 * @param rfidList
	 * @param erroBoxList
	 * @param modtypeMap
	 * @return
	 */

	private List<AllAllocateDetail> getAllAllocateDetailByUpdate(List<AllAllocateDetail> allAllocateDetailList,
			List<Map<String, Object>> rfidList, List<String> erroBoxList, Map<String, Object> modtypeMap) {
		List<AllAllocateDetail> allDetailList = Lists.newArrayList();
		List<String> newBoxList = Lists.newArrayList();
		// 获取新加入的箱子
		for (Map<String, Object> itemMap : rfidList) {
			String rfid = (String) itemMap.get(Parameter.RFID_KEY);
			boolean flag = false;
			for (AllAllocateDetail allDetail : allAllocateDetailList) {
				if (allDetail.getRfid().equals(rfid)) {
					flag = true;
				}
			}
			if (!flag) {
				newBoxList.add(rfid);
			}
		}
		// 若没有新加入的箱子
		if (Collections3.isEmpty(newBoxList)) {
			for (Map<String, Object> itemMap : rfidList) {
				String rfid = (String) itemMap.get(Parameter.RFID_KEY);
				AllAllocateDetail allocateDetail = new AllAllocateDetail();
				// 箱袋明细
				allocateDetail.setAllDetailId(IdGen.uuid());
				String boxNo = StringUtils.substring(rfid,
						rfid.length() - Integer.parseInt(Global.getConfig("boxNo.max.length")));
				StoBoxInfo box = stoBoxInfoService.get(boxNo);
				allocateDetail.setBoxNo(box.getId());
				// // 卷种
				// allocateDetail.setDenomination(allocateInfo.getDenomination());
				// // 金额
				// allocateDetail.setAmount(allocateInfo.getCashAmount());
				allocateDetail.setScanFlag(AllocationConstant.ScanFlag.NoScan);
				allocateDetail.setDelFlag(AllocationConstant.deleteFlag.Valid);
				allocateDetail.setStatus(box.getBoxStatus());
				allocateDetail.setRfid(rfid);
				allocateDetail.setBoxType(box.getBoxType());
				allocateDetail.setAmount(box.getBoxAmount());

				// 统计钞箱类型数量
				String key = box.getAtmBoxMod().getId();
				modtypeMap.put(key,
						modtypeMap.get(key) != null ? Integer.parseInt(modtypeMap.get(key).toString()) + 1 : 1);

				allDetailList.add(allocateDetail);
			}
		} else {
			for (String rfid : newBoxList) {
				String boxNo = StringUtils.substring(rfid,
						rfid.length() - Integer.parseInt(Global.getConfig("boxNo.max.length")));
				StoBoxInfo box = stoBoxInfoService.get(boxNo);

				if (box == null || Constant.BoxType.BOX_CAR.equals(box.getBoxType())
						|| (!Constant.BoxStatus.ATM_BOX_STATUS_EMPTY.equals(box.getBoxStatus())
								// &&
								// !Constant.BoxStatus.ATM_BOX_STATUS_CLEAR.equals(box.getBoxStatus())
								&& !Constant.BoxStatus.ATM_BOX_STATUS_BACK.equals(box.getBoxStatus()))
						|| StoBoxInfo.DEL_FLAG_DELETE.equals(box.getDelFlag())) {
					erroBoxList.add(boxNo);
				}
			}
			if (!Collections3.isEmpty(erroBoxList)) {
				return allDetailList;
			} else {
				for (Map<String, Object> itemMap : rfidList) {
					String rfid = (String) itemMap.get(Parameter.RFID_KEY);
					AllAllocateDetail allocateDetail = new AllAllocateDetail();
					// 箱袋明细
					allocateDetail.setAllDetailId(IdGen.uuid());
					String boxNo = StringUtils.substring(rfid,
							rfid.length() - Integer.parseInt(Global.getConfig("boxNo.max.length")));
					StoBoxInfo box = stoBoxInfoService.get(boxNo);
					allocateDetail.setBoxNo(box.getId());
					// // 卷种
					// allocateDetail.setDenomination(allocateInfo.getDenomination());
					// // 金额
					// allocateDetail.setAmount(allocateInfo.getCashAmount());
					allocateDetail.setScanFlag(AllocationConstant.ScanFlag.NoScan);
					allocateDetail.setDelFlag(AllocationConstant.deleteFlag.Valid);
					allocateDetail.setStatus(box.getBoxStatus());
					allocateDetail.setRfid(rfid);
					allocateDetail.setBoxType(box.getBoxType());
					allocateDetail.setAmount(box.getBoxAmount());

					// 统计钞箱类型数量
					String key = box.getAtmBoxMod().getId();
					modtypeMap.put(key,
							modtypeMap.get(key) != null ? Integer.parseInt(modtypeMap.get(key).toString()) + 1 : 1);

					allDetailList.add(allocateDetail);
				}
				return allDetailList;
			}
		}
		return allDetailList;
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年11月9日
	 * 
	 *          封装接口登记箱袋明细
	 * @param rfidList
	 * @return
	 */
	private List<AllAllocateDetail> getAllAllocateDetail(List<Map<String, Object>> rfidList, List<String> erroBoxList,
			Map<String, Object> modtypeMap) {
		List<AllAllocateDetail> allDetailList = Lists.newArrayList();
		for (Map<String, Object> itemMap : rfidList) {
			String rfid = (String) itemMap.get(Parameter.RFID_KEY);
			AllAllocateDetail allocateDetail = new AllAllocateDetail();
			// 箱袋明细
			allocateDetail.setAllDetailId(IdGen.uuid());
			String boxNo = StringUtils.substring(rfid,
					rfid.length() - Integer.parseInt(Global.getConfig("boxNo.max.length")));
			StoBoxInfo box = stoBoxInfoService.get(boxNo);
			if (box != null && !Constant.BoxType.BOX_CAR.equals(box.getBoxType())
					&& (Constant.BoxStatus.ATM_BOX_STATUS_EMPTY.equals(box.getBoxStatus())
							// ||
							// Constant.BoxStatus.ATM_BOX_STATUS_CLEAR.equals(box.getBoxStatus())
							|| Constant.BoxStatus.ATM_BOX_STATUS_BACK.equals(box.getBoxStatus()))
					&& (StoBoxInfo.DEL_FLAG_NORMAL.equals(box.getDelFlag())
							|| StoBoxInfo.DEL_FLAG_AUDIT.equals(box.getDelFlag()))) {
				allocateDetail.setBoxNo(box.getId());
				// // 卷种
				// allocateDetail.setDenomination(allocateInfo.getDenomination());
				// // 金额
				// allocateDetail.setAmount(allocateInfo.getCashAmount());
				allocateDetail.setScanFlag(AllocationConstant.ScanFlag.NoScan);
				allocateDetail.setDelFlag(AllocationConstant.deleteFlag.Valid);
				allocateDetail.setStatus(box.getBoxStatus());
				allocateDetail.setRfid(rfid);
				allocateDetail.setBoxType(box.getBoxType());
				allocateDetail.setAmount(box.getBoxAmount());

				// 统计钞箱类型数量
				String key = box.getAtmBoxMod().getId();
				modtypeMap.put(key,
						modtypeMap.get(key) != null ? Integer.parseInt(modtypeMap.get(key).toString()) + 1 : 1);

				allDetailList.add(allocateDetail);

			} else {
				erroBoxList.add(boxNo);
			}
		}
		return allDetailList;
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年11月9日
	 * 
	 *          验证箱袋数量
	 * @param modtypeMap
	 * @param addPlanId
	 * @return
	 */
	private String validateModNumAndBoxNum(Map<String, Object> modtypeMap, String addPlanId) {
		// 获取当前计划型号统计数据
		List<Map<String, Object>> list = atmPlanInfoService.getATMtypeByAddPlanId(addPlanId);
		// 获取所有钞箱类型配置信息
		List<AtmBoxMod> modList = atmBoxModService.findList(new AtmBoxMod());

		StringBuilder sb = new StringBuilder();

		if (Collections3.isEmpty(list) || Collections3.isEmpty(modList)) {
			return addPlanId + "计划下型号下钞箱类型数量不存在或钞箱类型配置信息不存在！";
		} else {
			for (Map<String, Object> atmPlanInfo : list) {
				// String atmTypeNo =
				// String.valueOf(atmPlanInfo.get("atmTypeNo"));
				String atmPlanName = String.valueOf(atmPlanInfo.get("atmPlanName"));
				// 此型号信息不存在
				// boolean flag = false;
				// 型号下不存在取款箱类型配置
				boolean getFlag = false;
				// 型号下不存在回收箱类型配置信息
				boolean backFLag = false;
				// 型号下不存在存款箱类型配置信息
				boolean dpositeFlag = false;
				// 此型号支持取款箱类型编号
				String boxTypeNo = (String) atmPlanInfo.get(Parameter.BOX_TYPE_KEY);
				Map<String, Object> resultMap = Maps.newHashMap();
				for (AtmBoxMod atmBoxMod : modList) {
					// 存在此型号
					// if (atmTypeNo.equals(atmBoxMod.getAtmTypeNo())) {
					// flag = true;
					//
					// }
					int num = Integer.valueOf(modtypeMap.get(atmBoxMod.getId()) != null
							? modtypeMap.get(atmBoxMod.getId()).toString() : "0");
					if (StringUtils.isNotBlank(boxTypeNo) && boxTypeNo.equals(atmBoxMod.getBoxTypeNo())) {
						if (atmBoxMod.getBoxType().equals("1")) {
							backFLag = true;
							resultMap.put("backBoxNum",
									Integer.parseInt(atmPlanInfo.get(Parameter.BOX_NUM_KEY).toString()) - num);
						}
						if (atmBoxMod.getBoxType().equals("2")) {
							getFlag = true;
							resultMap.put("getBoxNum",
									Integer.parseInt(atmPlanInfo.get(Parameter.BOX_NUM_KEY).toString()) - num);
						}
						if (atmBoxMod.getBoxType().equals("3")) {
							dpositeFlag = true;
							resultMap.put("depositBoxNum",
									Integer.parseInt(atmPlanInfo.get(Parameter.BOX_NUM_KEY).toString()) - num);
						}
					}
				}
				// 验证取款箱数量
				if (getFlag) {
					int getNum = Integer.parseInt(resultMap.get("getBoxNum").toString());
					if (getNum > 0) {
						sb.append("【" + atmPlanName + "，此加钞计划取款箱数量缺少" + getNum + "个！】");
					}
					if (getNum < 0) {
						sb.append("【" + atmPlanName + "，此加钞计划取款箱数量多出" + Math.abs(getNum) + "个！】");
					}
				}
				// 验证回收箱数量
				if (backFLag) {
					int backNum = Integer.parseInt(resultMap.get("backBoxNum").toString());
					if (backNum > 0) {
						sb.append("【" + atmPlanName + "，此加钞计划回收箱数量缺少" + backNum + "个！】");
					}
					if (backNum < 0) {
						sb.append("【" + atmPlanName + "，此加钞计划回收箱数量多出" + Math.abs(backNum) + "个！】");
					}
				}
				// 验证存款箱数量
				if (dpositeFlag) {
					int depositNum = Integer.parseInt(resultMap.get("depositBoxNum").toString());
					if (depositNum > 0) {
						sb.append("【" + atmPlanName + "，此加钞计划存款箱数量缺少" + depositNum + "个！】");
					}
					if (depositNum < 0) {
						sb.append("【" + atmPlanName + "，此加钞计划存款箱数量多出" + Math.abs(depositNum) + "个！】");
					}
				}
				// if (flag) {
				// } else {
				// sb.append("【" + atmTypeName + "，不存在此型号配置信息！】");
				// }
			}
			return sb.toString();
		}
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年11月9日
	 * 
	 *          修改相应钞箱状态
	 * @param allAllocateInfo
	 * @param status
	 */
	private void updateBoxStatusOut(AllAllocateInfo allAllocateInfo, String status) {
		if (allAllocateInfo != null && !Collections3.isEmpty(allAllocateInfo.getAllDetailList())) {
			for (AllAllocateDetail itemAllAllocateDetail : allAllocateInfo.getAllDetailList()) {
				StoBoxInfo detailBox = stoBoxInfoService.get(itemAllAllocateDetail.getBoxNo());
				// 修改相应调较明细状态
				if (StringUtils.isNotBlank(status)) {
					detailBox.setBoxStatus(status);
					/* 设置钞箱更新人及更新时间 修改人：xl 修改时间：2017-12-28 begin */
					detailBox.setUpdateBy(allAllocateInfo.getUpdateBy());
					detailBox.setUpdateDate(allAllocateInfo.getUpdateDate());
					/* end */
				}
				stoBoxInfoService.updateStatus(detailBox);
			}
		}
	}

}
