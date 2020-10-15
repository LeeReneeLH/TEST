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
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllHandoverInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.PbocStoStoresInfoService;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.task.QueueManager;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.AutoVaultCommunication;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.AutoVaultCommunicationService;
import com.coffer.core.modules.sys.utils.DbConfigUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service59
 * <p>
 * Description: 人行库外出库确认及交接接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service59")
@Scope("singleton")
public class Service59 extends HardwardBaseService {

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@Autowired
	private PbocAllHandoverInfoService pbocAllHandoverInfoService;

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

	@Autowired
	private PbocStoStoresInfoService pbocStoStoresInfoService;

	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;

	@Autowired
	private AutoVaultCommunicationService autoVaultCommunicationService;

	@Autowired
	private QueueManager queueManager;

	/** 判断重复提交静态列表：保存流水号 **/
	// private static List<String> interfaceList = Lists.newArrayList();

	/**
	 * @author wangbaozhong
	 * @version 2016年08月23日
	 * 
	 *          59：人行库外出库确认及交接接口
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {

		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();

		Map<String, Object> respMap = new HashMap<String, Object>();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, serviceNo);

		// 验证是否是重复提交
		// String serialorderNo = initInterface(paramMap, respMap, serviceNo);
		// if (Constant.FAILED.equals(serialorderNo)) {
		// return gson.toJson(respMap);
		// }
		//
		// try {
		// 取得电文传入的参数
		PbocAllAllocateInfo inputParam = getConfirmOutStoreParam(paramMap);

		// 设定业务类型
		String businessType = BusinessUtils.getBusinessTypeFromAllId(inputParam.getAllId());
		inputParam.setBusinessType(businessType);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, inputParam.getErrorFlag());
			return gson.toJson(respMap);
		}

		// 取得交接人员信息
		PbocAllAllocateInfo handoverParam = getHandoverParam(paramMap);
		if (StringUtils.isNotBlank(handoverParam.getErrorFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, handoverParam.getErrorFlag());
			return gson.toJson(respMap);
		}

		// 判断流水号状态(是否是待出库)
		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(inputParam);
		if (pbocAllAllocateInfo == null) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
			return gson.toJson(respMap);
		}
		if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)
				&& !AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_STATUS
						.equals(pbocAllAllocateInfo.getStatus())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
			return gson.toJson(respMap);
		} else if ((AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(businessType))
				&& !AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS
						.equals(pbocAllAllocateInfo.getStatus())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
			return gson.toJson(respMap);
		}

		// 出入库种别：出库
		inputParam.setInoutType(AllocationConstant.InOutCoffer.OUT);
		if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
			// 状态：待出库交接
			inputParam.setStatus(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_OUT_STORE_HANDOVER_STATUS);
		} else {
			// 状态：待交接
			inputParam.setStatus(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_HANDOVER_STATUS);
		}

		// 接收时间
		inputParam.setAcceptDate(new Date());

		// 检查标签物品类别是否符合业务
		List<String> errorBoxList = this.checkGoodsByBusinessType(inputParam.getInoutType(), businessType, paramMap);
		if (!errorBoxList.isEmpty()) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			List<Map<String, String>> mapList = Lists.newArrayList();
			for (String boxNo : errorBoxList) {
				Map<String, String> infoMap = Maps.newHashMap();
				infoMap.put(Parameter.RFID_KEY, boxNo);
				infoMap.put(Parameter.STATUS_KEY, "");
				mapList.add(infoMap);
			}
			respMap.put(Parameter.MESSAGE_INFO,
					"以下箱袋出库与当前业务不符:" + Collections3.convertToString(errorBoxList, Constant.Punctuation.COMMA));

			respMap.put(Parameter.ERROR_LIST_KEY, mapList);
			logger.warn("以下箱袋出库与当前业务不符：" + Collections3.convertToString(errorBoxList, Constant.Punctuation.COMMA));
			return gson.toJson(respMap);
		}

		// 强制出库时，不验证先进先出
		if (Constant.NecessaryOut.NECESSARY_OUT_YES.equals(inputParam.getOutFlag())) {
			// 验证箱子出库日期
			List<Map<String, String>> errorList = pbocAllAllocateInfoService.findIsNecessaryOut(inputParam);
			if (!errorList.isEmpty()) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put(Parameter.MESSAGE_INFO,
						"以下箱袋应优先出库:" + Collections3.convertToString(errorList, Constant.Punctuation.COMMA));

				respMap.put(Parameter.ERROR_LIST_KEY, errorList);
				logger.warn("以下箱袋应优先出库：" + Collections3.convertToString(errorList, Constant.Punctuation.COMMA));
				return gson.toJson(respMap);
			}
		}

		// 更新调拨状态（待交接）
		pbocAllAllocateInfoService.updateAllocateConfirmStatus(inputParam);
		// 插入扫描到的箱袋信息
		pbocAllAllocateInfoService.insertAllocateDetailInfo(inputParam);
		// 插入交接信息
		pbocAllHandoverInfoService.insertHandover(inputParam);
		// 取得参与原封新券出库的业务类型列表
		List<String> originalBusinessList = Global.getList("store.originalBanknote.allocation.out.store.business");
		// 原封箱号列表
		List<String> originalBoxList = Lists.newArrayList();
		if (originalBusinessList.contains(businessType)) {

			String originalBanknoteClassification = Global.getConfig("store.originalBanknote.goodsClassification");
			List<Map<String, String>> inputBoxList = (List<Map<String, String>>) paramMap.get(Parameter.BOX_LIST_KEY);
			for (Map<String, String> outStoreInfoMap : inputBoxList) {
				String goodsId = outStoreInfoMap.get(Parameter.GOODS_ID_KEY);
				String rfid = outStoreInfoMap.get(Parameter.RFID_KEY);
				StoGoodSelect goodsInfo = StoreCommonUtils.splitGood(goodsId);
				// 如果物品是原封新券，则从rfid列表中移除原封箱号
				if (goodsInfo.getClassification().equals(originalBanknoteClassification)) {
					inputParam.getRfidList().remove(rfid);
					originalBoxList.add(rfid);
				}
			}
			List<String> errorOriginalBoxList = Lists.newArrayList();
			// 检查原封箱归属机构及状态
			for (String boxNo : originalBoxList) {
				StoOriginalBanknote stoOriginalBanknote = stoOriginalBanknoteService.getOriginalBanknoteById(boxNo,
						inputParam.getAoffice().getId());

				if (stoOriginalBanknote == null
						|| ExternalConstant.RecoverStatus.NO_RECOVER.equals(stoOriginalBanknote.getRecoverStatus())
						|| ExternalConstant.RecoverStatus.IS_RECOVER.equals(stoOriginalBanknote.getRecoverStatus())) {
					originalBoxList.add(boxNo);
				}
			}
			if (!errorOriginalBoxList.isEmpty()) {

				throw new BusinessException("message.E2041", "以下原封箱不属于当前机构或状态不正确:"
						+ Collections3.convertToString(errorOriginalBoxList, Constant.Punctuation.COMMA), "");
			}
			Office office = new Office();
			office.setId(inputParam.getAoffice().getId());
			for (String boxNo : originalBoxList) {
				StoOriginalBanknote stoOriginalBanknote = new StoOriginalBanknote();
				// 原封箱号
				stoOriginalBanknote.setId(boxNo);
				// 出库流水单号
				stoOriginalBanknote.setOutId(inputParam.getAllId());
				// 出库时间
				stoOriginalBanknote.setOutDate(new Date());
				// 创建出库回收状态
				stoOriginalBanknote.setRecoverStatus(ExternalConstant.RecoverStatus.NO_RECOVER);
				stoOriginalBanknote.setRoffice(office);
				stoOriginalBanknoteService.update(stoOriginalBanknote);
			}

		}

		// 更新RFID状态
		String rfidPosition = Constant.BoxStatus.ONPASSAGE;
		Office atOffice = pbocAllAllocateInfo.getRoffice();
		if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
			rfidPosition = Constant.BoxStatus.CLASSFICATION;
			atOffice = pbocAllAllocateInfo.getAoffice();
		}
		if (AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(businessType)
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(businessType)) {
			atOffice = new Office();
			atOffice.setName(pbocAllAllocateInfo.getAofficeName());
		}

		StoreCommonUtils.updateRfidStatus(inputParam.getRfidList(), rfidPosition, inputParam.getUserId(),
				inputParam.getUserName(), StoreConstant.RfidUseFlag.outStore, atOffice,
				pbocAllAllocateInfo.getBusinessType());

		// 将RFID当前邦定记录表导入历史表
		stoRfidDenominationService.insertInToHistory(inputParam.getRfidList(), atOffice);

		inputParam.getRfidList().addAll(originalBoxList);

		// 更新交接信息
		if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(handoverParam.getBusinessType())) {
			// 复点出入库交接时，按照出入库类型更新交接表数据
			// 更新交接表信息
			pbocAllHandoverInfoService.udateRCHandover(handoverParam);
			// 插入交接人员信息
			pbocAllHandoverInfoService.insertHandoverUserDetail(handoverParam);
			if (AllocationConstant.InOutCoffer.OUT.equals(handoverParam.getInoutType())) {
				// 复点出库交接后，状态改为 清分中
				handoverParam.setStatus(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS);
			} else {
				// 更新调拨状态完成
				handoverParam.setStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
			}

		} else {
			// 更新交接表信息
			pbocAllHandoverInfoService.udateHandover(handoverParam);
			// 插入交接人员信息
			pbocAllHandoverInfoService.insertHandoverUserDetail(handoverParam);
			// 业务状态为 申请上缴，代理上缴，调拨出库，销毁出库，调拨入库 时，交接后状态改为完成
			if (AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(handoverParam.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(handoverParam.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(handoverParam.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(handoverParam.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(handoverParam.getBusinessType())
					|| AllocationConstant.BusinessType.PBOC_ORIGINAL_BANKNOTE_IN_STORE
							.equals(handoverParam.getBusinessType())) {
				// 更新调拨状态完成
				handoverParam.setStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
			} else {
				// 更新调拨状态待接收
				handoverParam.setStatus(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_ACCEPT_STATUS);
			}

		}
		pbocAllAllocateInfoService.updateAllocateConfirmStatus(handoverParam);
		// 更新库存内物品状态
		List<String> reserveRfidList = AllocationCommonUtils.getReserveRfidListByAllId(inputParam.getAllId());
		User updateUser = new User();
		updateUser.setId(inputParam.getUserId());
		updateUser.setName(inputParam.getUserName());
		StoreCommonUtils.updateGoodsOutStoreStatus(inputParam.getAllId(), inputParam.getRfidList(), reserveRfidList,
				updateUser, inputParam.getAoffice().getId());
		// 发送通知
		PbocAllAllocateInfo AllAllocateInfo = pbocAllAllocateInfoService.get(handoverParam.getAllId());
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(AllAllocateInfo.getRoffice().getName());
		paramsList.add(handoverParam.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(AllAllocateInfo.getBusinessType(), handoverParam.getStatus(), paramsList,
				AllAllocateInfo.getRoffice().getId(), updateUser);
		// 审批金额与实际出库金额不符时，还原预剩库存
		// if
		// (AllocationConstant.CheckResult.Different.equals(inputParam.getCheckResult()))
		// {
		if (inputParam.getOutstoreAmount() == null
				|| pbocAllAllocateInfo.getConfirmAmount().compareTo(inputParam.getOutstoreAmount()) != 0) {
			// 合并统计物品数量
			List<ChangeStoreEntity> paramGoodsList = inputParam.getChangeGoodsList();
			Map<String, ChangeStoreEntity> entityMap = Maps.newHashMap();
			for (ChangeStoreEntity changeStoreEntity : paramGoodsList) {
				if (entityMap.containsKey(changeStoreEntity.getGoodsId())) {
					ChangeStoreEntity tempEntity = entityMap.get(changeStoreEntity.getGoodsId());
					tempEntity.setNum(tempEntity.getNum() + changeStoreEntity.getNum());
				} else {
					entityMap.put(changeStoreEntity.getGoodsId(), changeStoreEntity);
				}
			}

			paramGoodsList = Lists.newArrayList();

			for (Map.Entry<String, ChangeStoreEntity> changeStoreEntity : entityMap.entrySet()) {
				paramGoodsList.add(changeStoreEntity.getValue());
			}
			inputParam.setChangeGoodsList(paramGoodsList);
			// 统计待还原预剩库存
			List<ChangeStoreEntity> restoreApproveGoodsList = Lists.newArrayList();
			ChangeStoreEntity changeStoreEntity = null;

			for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {

				if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
					int noGoodsCnt = 0;
					for (ChangeStoreEntity outStoreEntity : inputParam.getChangeGoodsList()) {
						if (outStoreEntity.getGoodsId().equals(item.getGoodsId())
								&& !outStoreEntity.getNum().equals(item.getMoneyNumber())) {

							changeStoreEntity = new ChangeStoreEntity();
							// 物品类型：货币
							changeStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
							changeStoreEntity.setGoodsId(item.getGoodsId());
							changeStoreEntity.setNum(item.getMoneyNumber() - Math.abs(outStoreEntity.getNum()));
							restoreApproveGoodsList.add(changeStoreEntity);
						} else {
							noGoodsCnt++;
						}
					}
					// 出库物品中没有审批物品，审批物品预剩库存全部还原
					if (noGoodsCnt == inputParam.getChangeGoodsList().size()
							|| inputParam.getChangeGoodsList().size() == 0) {
						changeStoreEntity = new ChangeStoreEntity();
						// 物品类型：货币
						changeStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
						changeStoreEntity.setGoodsId(item.getGoodsId());
						changeStoreEntity.setNum(item.getMoneyNumber());
						restoreApproveGoodsList.add(changeStoreEntity);
					}
				}
			}
			// 还原预剩库存
			pbocStoStoresInfoService.changeSurplusStore(restoreApproveGoodsList,
					UserUtils.get(inputParam.getUserId()).getOffice().getId());
		}

		if (!Collections3.isEmpty(inputParam.getChangeGoodsList())) {
			// 更新物品库存
			pbocStoStoresInfoService.changeStore(inputParam.getChangeGoodsList(), inputParam.getAoffice().getId(),
					inputParam.getAllId(), updateUser);
		}

		// 处理成功
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		// 自动化功能开启时保存通信内容并添加至队列
		if (AllocationConstant.AutomaticStoreSwitch.OPEN.equals(DbConfigUtils.getDbConfig("auto.vault.switch"))) {

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put(Parameter.SERVICE_NO_KEY, DbConfigUtils.getDbConfig("auto.vault.serviceNo"));
			resultMap.put(Parameter.SERIAL_NO_KEY, paramMap.get(Parameter.SERIALORDER_NO_KEY));

			List<Map<String, String>> boxList = (List<Map<String, String>>) paramMap.get(Parameter.BOX_LIST_KEY);
			List<Map<String, String>> autoVaultBoxList = Lists.newArrayList();

			for (Map<String, String> boxMap : boxList) {
				Map<String, String> map = Maps.newConcurrentMap();
				map.put(Parameter.RFID_KEY, boxMap.get(Parameter.RFID_KEY));
				map.put(Parameter.GOODS_ID_KEY, boxMap.get(Parameter.GOODS_ID_KEY));
				map.put(Parameter.GOODS_NAME_KEY, boxMap.get(Parameter.GOODS_NAME_KEY));
				autoVaultBoxList.add(map);
			}
			resultMap.put(Parameter.BOX_LIST_KEY, autoVaultBoxList);

			AutoVaultCommunication autoVaultCommunication = new AutoVaultCommunication();
			autoVaultCommunication.setIsNewRecord(true);
			autoVaultCommunication.setId(IdGen.uuid());
			autoVaultCommunication
					.setMessage(DictUtils.getDictLabel(inputParam.getBusinessType(), "all_businessType", null)
							+ inputParam.getAllId());
			autoVaultCommunication.setCreateBy(updateUser);
			autoVaultCommunication.setInJson(gson.toJson(resultMap));
			autoVaultCommunication.setStatus(Constant.CommunicationStatus.TO_BE_SENT);
			autoVaultCommunicationService.save(autoVaultCommunication);
			try {
				queueManager.communicationPut(autoVaultCommunication.getInJson(), autoVaultCommunication.getId());
			} catch (BusinessException businessException) {
				autoVaultCommunication.setStatus(Constant.CommunicationStatus.FAIL);
				SysCommonUtils.updateCommunication(autoVaultCommunication);
			}
		}

		return gson.toJson(respMap);

		// } finally {
		// cleanSerialorderNo(serialorderNo);
		// }
	}
	/**
	 * 执行完成的流水号清除
	 * 
	 * @author: ChengShu
	 * @param inputParam
	 * @param param
	 * @return PbocAllAllocateInfo 返回类型
	 */
	// private void cleanSerialorderNo(String serialorderNo){
	// interfaceList.remove(serialorderNo);
	// }

	/**
	 * @author chengshu
	 * @version 2016年06月16日
	 * 
	 *          重复调用接口屏蔽
	 * @param requestMap
	 * @param serviceNo
	 * @return
	 */
	// public String initInterface(Map<String, Object> headInfo, Map<String,
	// Object> respMap, String serviceNo) {
	//
	// // 流水号
	// if
	// (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY))))
	// {
	// logger.warn("输入参数错误：" + Parameter.SERIALORDER_NO_KEY + " 不存在或是空。");
	// respMap.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// respMap.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E03);
	// return Constant.FAILED;
	//
	// } else {
	// // 流水号+接口编号
	// String key =
	// StringUtils.join(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)),
	// serviceNo);
	// if (interfaceList.contains(key)) {
	// // 重复提交错误发生
	// logger.warn(StringUtils.join("重复提交错误:接口", serviceNo, "; 流水号:" +
	// headInfo.get(Parameter.SERIALORDER_NO_KEY)));
	// respMap.put(Parameter.RESULT_FLAG_KEY,
	// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	// respMap.put(Parameter.ERROR_NO_KEY,
	// ExternalConstant.HardwareInterface.ERROR_NO_E44);
	// return Constant.FAILED;
	//
	// } else {
	// // 保存当前流水号，验证是否重复提交
	// interfaceList.add(key);
	// }
	// }
	//
	// // 返回流水号
	// return StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY));
	// }

	/**
	 * 
	 * @author chengshu
	 * @version 2016年06月01日
	 * 
	 *          取得人行出库确认接口的输入参数
	 * @param requestMap
	 *            输入参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private PbocAllAllocateInfo getConfirmOutStoreParam(Map<String, Object> headInfo) {

		PbocAllAllocateInfo inputParam = new PbocAllAllocateInfo();

		// 流水号
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)))) {
			return checkParamFaile(inputParam, Parameter.SERIALORDER_NO_KEY);
		} else {
			inputParam.setAllId(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)));
		}

		// 用户ID
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)))) {
			return checkParamFaile(inputParam, Parameter.USER_ID_KEY);
		} else {
			inputParam.setUserId(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
		}

		// 用户名称
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)))) {
			return checkParamFaile(inputParam, Parameter.USER_NAME_KEY);
		} else {
			inputParam.setUserName(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)));
		}

		// 金库机构ID
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)))) {
			return checkParamFaile(inputParam, Parameter.OFFICE_ID_KEY);
		} else {
			// 登录机构为金融平台时，设置出库机构为流水中的接收机构
			Office office = SysCommonUtils.findOfficeById(StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
			if (Constant.OfficeType.DIGITAL_PLATFORM.equals(office.getType())) {
				// 复点出库、销毁出库、调拨出库时设置出库机构为流水中登记机构
				PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService
						.get(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)));
				if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(pbocAllAllocateInfo.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE
								.equals(pbocAllAllocateInfo.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE
								.equals(pbocAllAllocateInfo.getBusinessType())) {
					inputParam.setAoffice(pbocAllAllocateInfo.getRoffice());
				} else {
					inputParam.setAoffice(pbocAllAllocateInfo.getAoffice());
				}
			} else {
				inputParam.setAoffice(office);
			}
		}

		// 审批与实际出库金额不符标识
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.CHECK_RESULT_KEY)))) {
			return checkParamFaile(inputParam, Parameter.CHECK_RESULT_KEY);
		} else {
			inputParam.setCheckResult(StringUtils.toString(headInfo.get(Parameter.CHECK_RESULT_KEY)));
		}
		// 备注项
		String remarks = StringUtils.toString(headInfo.get(Parameter.REMARKS_KEY));
		if (StringUtils.isNotBlank(remarks)) {
			inputParam.setRemarks(remarks);
		}

		// 强制出库标识
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.OUT_FLAG_KEY)))) {
			return checkParamFaile(inputParam, Parameter.OUT_FLAG_KEY);
		} else {
			inputParam.setOutFlag(StringUtils.toString(headInfo.get(Parameter.OUT_FLAG_KEY)));
		}

		// 箱袋列表
		List<Map<String, String>> inputBoxList = Lists.newArrayList();
		if (null == headInfo.get(Parameter.BOX_LIST_KEY)) {
			return checkParamFaile(inputParam, Parameter.BOX_LIST_KEY);
		} else {
			inputBoxList = (List<Map<String, String>>) headInfo.get(Parameter.BOX_LIST_KEY);
		}

		// 箱袋列表
		List<PbocAllAllocateDetail> boxList = Lists.newArrayList();
		List<String> rfidList = Lists.newArrayList();
		List<ChangeStoreEntity> changeGoodsList = Lists.newArrayList();

		// 设置箱袋列表
		PbocAllAllocateDetail boxDetail = new PbocAllAllocateDetail();
		ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
		// 出库金额
		BigDecimal outstoreAmount = new BigDecimal(0);
		long goodsNum = 0l;
		for (Map<String, String> boxMap : inputBoxList) {

			if (null == boxMap) {
				break;
			}
			boxDetail = new PbocAllAllocateDetail();
			changeStoreEntity = new ChangeStoreEntity();

			// rfid
			if (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.STORE_ESCORT_RFID)))) {
				return checkParamFaile(inputParam,
						StringUtils.join(Parameter.BOX_LIST_KEY, ".", Parameter.STORE_ESCORT_RFID));
			} else {
				boxDetail.setRfid(boxMap.get(Parameter.STORE_ESCORT_RFID));
			}

			// // 强制出库标识
			// if
			// (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.OUT_FLAG_KEY))))
			// {
			// return checkParamFaile(inputParam,
			// StringUtils.join(Parameter.BOX_LIST_KEY, ".",
			// Parameter.OUT_FLAG_KEY));
			// } else {
			// boxDetail.setStatus(StringUtils.toString(boxMap.get(Parameter.OUT_FLAG_KEY)));
			// }

			// goodsId
			if (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.GOODS_ID_KEY)))) {
				return checkParamFaile(inputParam,
						StringUtils.join(Parameter.BOX_LIST_KEY, ".", Parameter.GOODS_ID_KEY));
			} else {
				changeStoreEntity.setGoodsId(StringUtils.toString(boxMap.get(Parameter.GOODS_ID_KEY)));
			}

			// goodsNum
			if (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.GOODS_NUM_KEY)))) {
				return checkParamFaile(inputParam,
						StringUtils.join(Parameter.BOX_LIST_KEY, ".", Parameter.GOODS_NUM_KEY));
			} else {
				goodsNum = Long.valueOf(boxMap.get(Parameter.GOODS_NUM_KEY));
				changeStoreEntity.setNum(-goodsNum);
			}

			// 物品类型：货币
			changeStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);

			// 计算出库物品金额
			BigDecimal amount = StoreCommonUtils.getGoodsValue(changeStoreEntity.getGoodsId());
			amount = amount.multiply(new BigDecimal(goodsNum));
			outstoreAmount = outstoreAmount.add(amount);

			rfidList.add(StringUtils.toString(boxMap.get(Parameter.STORE_ESCORT_RFID)));
			boxList.add(boxDetail);
			changeGoodsList.add(changeStoreEntity);
		}
		inputParam.setOutstoreAmount(outstoreAmount.doubleValue());
		inputParam.setPbocAllAllocateDetailList(boxList);
		inputParam.setRfidList(rfidList);
		inputParam.setChangeGoodsList(changeGoodsList);

		return inputParam;
	}

	/**
	 * 电文传入参数验证失败返回
	 * 
	 * @author: ChengShu
	 * @param inputParam
	 * @param param
	 * @return PbocAllAllocateInfo 返回类型
	 */
	private PbocAllAllocateInfo checkParamFaile(PbocAllAllocateInfo inputParam, String param) {
		inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
		logger.warn("输入参数错误：" + param + " 不存在或是空。");
		return inputParam;
	}

	/**
	 * @author chengshu
	 * @version 2015年06月03日
	 * 
	 * 
	 *          从参数取得交接人员信息(库外交接任务交接接口)
	 * @param headInfo
	 *            交接人员信息
	 * @return AllAllocateInfo 对象
	 */
	@SuppressWarnings("unchecked")
	private PbocAllAllocateInfo getHandoverParam(Map<String, Object> headInfo) {

		// 移交人信息列表
		List<PbocAllHandoverUserDetail> handoverList = Lists.newArrayList();
		// 接收人信息列表
		List<PbocAllHandoverUserDetail> acceptList = Lists.newArrayList();
		// 授权人信息列表
		List<PbocAllHandoverInfo> managerList = Lists.newArrayList();

		PbocAllAllocateInfo inputParam = new PbocAllAllocateInfo();

		// 取得出入库类型
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY)))) {
			return checkParamFaile(inputParam, Parameter.INOUT_TYPE_KEY);
		} else {
			inputParam.setInoutType(StringUtils.toString(headInfo.get(Parameter.INOUT_TYPE_KEY)));
		}

		// 取得流水号
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)))) {
			return checkParamFaile(inputParam, Parameter.SERIALORDER_NO_KEY);
		} else {
			inputParam.setAllId(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)));
		}

		// 用户ID
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)))) {
			return checkParamFaile(inputParam, Parameter.USER_ID_KEY);
		} else {
			inputParam.setUserId(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
		}

		// 用户名称
		if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)))) {
			return checkParamFaile(inputParam, Parameter.USER_NAME_KEY);
		} else {
			inputParam.setUserName(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)));
		}

		// 电文传入移交人信息
		List<Map<String, String>> handoverInputList = (List<Map<String, String>>) headInfo
				.get(Parameter.HANDOVER_LIST_KEY);
		// 设定移交人信息
		setHandoverUserInfo(inputParam, handoverInputList, handoverList, Parameter.HANDOVER_LIST_KEY);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			return inputParam;
		} else {
			inputParam.setHandoverList(handoverList);
		}

		// 电文传入接收人信息
		List<Map<String, String>> acceptInputList = (List<Map<String, String>>) headInfo.get(Parameter.ACCEPT_LIST_KEY);
		// 设定接收人信息
		setHandoverUserInfo(inputParam, acceptInputList, acceptList, Parameter.ACCEPT_LIST_KEY);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			return inputParam;
		} else {
			inputParam.setAcceptList(acceptList);
		}

		// 电文传入授权人信息
		List<Map<String, String>> managerInputList = (List<Map<String, String>>) headInfo
				.get(Parameter.MANAGER_LIST_KEY);
		setManagerInfo(inputParam, managerInputList, managerList, Parameter.MANAGER_LIST_KEY);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			return inputParam;
		}
		// 设定业务类型
		String businessType = BusinessUtils.getBusinessTypeFromAllId(inputParam.getAllId());
		inputParam.setBusinessType(businessType);
		return inputParam;
	}

	/**
	 * 按业务类型检查出入库物品类别是否正确
	 * 
	 * @author WangBaozhong
	 * @version 2016年7月13日
	 * 
	 * @return 返回错误消息
	 */
	@SuppressWarnings("unchecked")
	private List<String> checkGoodsByBusinessType(String inOutType, String businessType, Map<String, Object> headInfo) {
		List<String> errorList = Lists.newArrayList();
		if ("".equals(Global.getConfig("allocation.businessType.goodsClassification.check"))) {
			return errorList;
		}

		List<Map<String, String>> inputBoxList = (List<Map<String, String>>) headInfo.get(Parameter.BOX_LIST_KEY);

		List<String> relationList = Lists.newArrayList();
		if (AllocationConstant.InOutCoffer.IN.equals(inOutType)) {
			// 入库业务与物品类别关系
			relationList = Global.getList("allocation.businessType.goodsClassification.in.store");
		} else if (AllocationConstant.InOutCoffer.OUT.equals(inOutType)) {
			// 出库业务与物品类别关系
			relationList = Global.getList("allocation.businessType.goodsClassification.out.store");
		}
		// 原封新券类别
		String originalBanknotType = Global.getConfig("store.originalBanknote.goodsClassification");

		String strbusinessClassificationRelation = "";
		for (String relation : relationList) {
			String relationArray[] = relation.split(Constant.Punctuation.HALF_COLON);
			if (businessType.equals(relationArray[0])) {
				strbusinessClassificationRelation = relationArray[1];
				break;
			}
		}

		for (Map<String, String> boxMap : inputBoxList) {
			String goodsId = boxMap.get(Parameter.GOODS_ID_KEY);
			String rfid = boxMap.get(Parameter.RFID_KEY);
			StoGoodSelect goodsInfo = StoreCommonUtils.splitGood(goodsId);
			if (strbusinessClassificationRelation.indexOf(goodsInfo.getClassification()) == -1) {
				// 出入库出错物品为原封券时，显示原封券箱号及翻译
				if (originalBanknotType.equals(goodsInfo.getClassification())) {
					errorList
							.add(rfid + Constant.Punctuation.HALF_COLON + boxMap.get(Parameter.ORIGINAL_TRANSLATE_KEY));
				} else {
					errorList.add(StringUtils.left(rfid, 8));
				}
			}
		}

		return errorList;
	}

	/**
	 * 设置交接人员信息
	 * 
	 * @author: ChengShu
	 * @param inputParam
	 *            电文参数保存实体
	 * @param handoverInputList
	 *            电文传入人员列表
	 * @param handoverList
	 *            保存人员信息列表
	 * @param handoverType
	 *            人员类型
	 * @return PbocAllAllocateInfo 返回类型
	 */
	private PbocAllAllocateInfo setHandoverUserInfo(PbocAllAllocateInfo inputParam,
			List<Map<String, String>> handoverInputList, List<PbocAllHandoverUserDetail> handoverList,
			String handoverType) {
		// 设定交接人信息
		if (!Collections3.isEmpty(handoverInputList)) {

			PbocAllHandoverUserDetail userDetail = new PbocAllHandoverUserDetail();

			for (Map<String, String> map : handoverInputList) {

				if (null == map) {
					break;
				}
				userDetail = new PbocAllHandoverUserDetail();

				// 人员类型
				if (Parameter.HANDOVER_LIST_KEY.equals(handoverType)) {
					// 移交人
					userDetail.setType(AllocationConstant.UserType.handover);
				} else {
					// 接收人
					userDetail.setType(AllocationConstant.UserType.accept);
				}

				// 人员ID
				if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)))) {
					return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.OPT_USER_ID_KEY));
				} else {
					userDetail.setEscortId(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)));
				}

				// 人员名称
				if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_NAME_KEY)))) {
					return checkParamFaile(inputParam,
							StringUtils.join(handoverType, ".", Parameter.OPT_USER_NAME_KEY));
				} else {
					userDetail.setEscortName(StringUtils.toString(map.get(Parameter.OPT_USER_NAME_KEY)));
				}

				// 交接方式
				if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_HAND_TYPE_KEY)))) {
					return checkParamFaile(inputParam,
							StringUtils.join(handoverType, ".", Parameter.OPT_USER_HAND_TYPE_KEY));
				} else {
					userDetail.setHandType(StringUtils.toString(map.get(Parameter.OPT_USER_HAND_TYPE_KEY)));
				}

				handoverList.add(userDetail);
			}
		}

		return inputParam;
	}

	/**
	 * 设置授权人员信息
	 * 
	 * @author: ChengShu
	 * @param inputParam
	 *            电文参数保存实体
	 * @param managerInputList
	 *            电文传入授权人员列表
	 * @param managerList
	 *            保存授权人员信息列表
	 * @param handoverType
	 *            人员类型
	 * @return PbocAllAllocateInfo 返回类型
	 */
	private PbocAllAllocateInfo setManagerInfo(PbocAllAllocateInfo inputParam,
			List<Map<String, String>> managerInputList, List<PbocAllHandoverInfo> managerList, String handoverType) {
		// 设定移交人信息
		if (!Collections3.isEmpty(managerInputList)) {

			managerList = Lists.newArrayList();
			PbocAllHandoverInfo managerInfo = new PbocAllHandoverInfo();

			for (Map<String, String> map : managerInputList) {
				managerInfo = new PbocAllHandoverInfo();

				// 人员ID
				if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)))) {
					return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.OPT_USER_ID_KEY));
				} else {
					User managerUser = UserUtils
							.getByLoginName(StringUtils.toString(map.get(Parameter.OPT_USER_ID_KEY)));
					managerInfo.setManagerUserId(managerUser.getId());
					managerInfo.setManagerUserName(managerUser.getName());
				}

				// 授权原因
				if (StringUtils.isBlank(StringUtils.toString(map.get(Parameter.REASON_KEY)))) {
					return checkParamFaile(inputParam, StringUtils.join(handoverType, ".", Parameter.REASON_KEY));
				} else {
					managerInfo.setManagerReason(StringUtils.toString(map.get(Parameter.REASON_KEY)));
				}

				managerList.add(managerInfo);
			}
			inputParam.setManagerList(managerList);
		}

		return inputParam;
	}
}
