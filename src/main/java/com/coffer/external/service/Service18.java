package com.coffer.external.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
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
 * Title: Service18
 * <p>
 * Description:
 * </p>
 * 
 * @author qipeihong
 * @date 2017年7月11日
 */
@Component("Service18")
@Scope("singleton")
public class Service18 extends HardwardBaseService {

	/** 日志对象 */
	private static Logger logger = LoggerFactory.getLogger(Global.class);
	/** 一次执行箱号个数 */
	private static final double LOOP_NUM = 1000;

	@Autowired
	AllocationService service;
	@Autowired
	OfficeService officeService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;

	/**
	 * @author chengshu
	 * @version 2015-10-13
	 * 
	 * @Description PDA库房下拨登记接口
	 * @param headInfo
	 * @param serviceNo
	 * @return 处理结果
	 */
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = Maps.newHashMap();
		String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
		checkBoxHandoutRegister(paramMap, map);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 金库机构ID
		String officeId = paramMap.get(Parameter.OFFICE_ID_KEY).toString();
		// 箱袋列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);

		// 根据箱号取得箱子列表，验证箱子是否存在或状态是否正确
		List<StoBoxInfo> allocationBoxList = getBoxList(list, map, officeId);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 验证箱子出库时间
		// checkBoxOutDate(allocationBoxList, map);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 以机构为单位分组，把同一机构的箱号分到一个组里
		Map<String, String> officeBoxTypeMap = Maps.newHashMap();
		Map<String, List<StoBoxInfo>> allocationsMap = getBoxGroup(allocationBoxList, officeBoxTypeMap);
		// 取得已赔款网点的物品信息
		// Map<String, Map<String, Long>> outletsQuotaMap =
		// getQuotaInfo(officeId, map, allocationsMap);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}

		// 取得已经存在的下拨信息
		Map<String, AllAllocateInfo> outletsRegistMap = getAllocateRegist(allocationsMap, officeId, map,
				allocationBoxList, paramMap, officeBoxTypeMap);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}

		// 下拨信息登记处理
		doBoxHandoutRegister(paramMap, officeId, allocationsMap, outletsRegistMap, map, officeBoxTypeMap);
		// 装箱后删除对应的箱子的明细
		deleteBoxDetail(allocationBoxList);
		// 成功结果
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		return setReturnMap(map, serviceNo);

	}

	/**
	 * @author chengshu
	 * @version 2015-10-13
	 * 
	 * @Description 验证 PDA库房下拨登记接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	private void checkBoxHandoutRegister(Map<String, Object> headInfo, Map<String, Object> map) {

		// 登记人ID
		if (headInfo.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------userId:" + CommonUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
		}

		// 登记人姓名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("参数错误--------userName:" + CommonUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------userName:" + CommonUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)));
		}

		// 金库机构ID
		if (headInfo.get(Parameter.OFFICE_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
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

			}
		}
	}

	/**
	 * @author chengshu
	 * @version 2015-10-13
	 * 
	 * @Description 根据箱号取得箱子列表，验证箱子是否存在或状态是否正确
	 * @param list
	 *            箱号列表
	 * @param map
	 *            处理结果
	 * @param registType
	 *            登记种别
	 * @return 箱子信息列表
	 */
	@Transactional(readOnly = false)
	private List<StoBoxInfo> getBoxList(List<Map<String, Object>> list, Map<String, Object> map, String officeId) {

		// 错误箱号列表
		List<String> errorBoxList = Lists.newArrayList();
		List<String> errorPlaceList = Lists.newArrayList();
		List<String> errorOfficeList = Lists.newArrayList();
		// 循环取得所有的箱号列表
		String[] boxs = new String[list.size()];
		StringBuffer errorRfid = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			StoBoxInfo boxInfo = new StoBoxInfo();
			if (list.get(i).get(Parameter.BOX_NO_KEY) != null) {
				boxInfo.setId(list.get(i).get(Parameter.BOX_NO_KEY).toString());
			}
			if (list.get(i).get(Parameter.RFID_KEY) != null) {
				boxInfo.setRfid(list.get(i).get(Parameter.RFID_KEY).toString());
			}
			// 根据箱号查询箱子
			List<StoBoxInfo> stoboxinfoList = stoBoxInfoService.getBindingBoxInfo(boxInfo);
			if (Collections3.isEmpty(stoboxinfoList)) {
				if (!errorRfid.toString().equals("")) {
					errorRfid.append(Constant.Punctuation.COMMA);
					errorRfid.append(boxInfo.getRfid());
				} else {
					errorRfid.append(boxInfo.getRfid());
				}
				continue;
			}
			StoBoxInfo stoboxinfo = stoboxinfoList.get(0);
			boxs[i] = stoboxinfo.getId().toString();
		}
		if (!errorRfid.toString().equals("")) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "箱袋所属机构没有绑定路线或箱子不存在:" + errorRfid);
			return null;
		}

		// 箱袋列表
		List<StoBoxInfo> allocationBoxList = Lists.newArrayList();
		// 箱子验证结果
		String checkResult = "";
		// 1000个箱子为一组，进行循环处理
		double boxLength = boxs.length;
		double loopNum = Math.ceil((boxLength / LOOP_NUM));
		int boxNum = 0;
		List<String> boxList = Lists.newArrayList();
		for (int i = 1; i < loopNum + 1; i++) {
			// 初始化箱号列表
			boxList = Lists.newArrayList();
			// 如果多次循环的场合
			if (loopNum > 1) {
				// 取得当前组的所有箱号
				for (int j = boxNum; j < i * LOOP_NUM; j++) {
					if (j < boxs.length) {
						boxList.add(boxs[j]);
						boxNum++;
					} else {
						break;
					}
				}
				// 如果只有一次循环的场合
			} else {
				boxList = Arrays.asList(boxs);
			}

			// 根据箱号，取得所有箱子的实体
			List<StoBoxInfo> stoBoxInfoList = StoreCommonUtils.getBoxListByArray(boxList);
			// 验证箱子是否正确
			checkResult = checkBoxInfo(allocationBoxList, boxList, errorBoxList, errorPlaceList, errorOfficeList,
					stoBoxInfoList, officeId);
			if (Constant.FAILED.equals(checkResult)) {
				break;
			}
		}
		// 验证失败，返回错误信息
		if (Constant.FAILED.equals(checkResult)) {
			// 箱子不存在或不在路线上
			if (errorBoxList.size() > 0) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY,
						"箱袋所属机构没有绑定路线或箱子不存在:" + Collections3.convertToString(errorBoxList, Constant.Punctuation.COMMA));
				// 箱子所属位置不正确
			} else if (errorPlaceList.size() > 0) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY,
						"箱袋状态不正确:" + Collections3.convertToString(errorPlaceList, Constant.Punctuation.COMMA));
				// 箱子所属机构不正确
			} else if (errorOfficeList.size() > 0) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY,
						"箱袋不属于该机构:" + Collections3.convertToString(errorOfficeList, Constant.Punctuation.COMMA));
			}
		}

		return allocationBoxList;
	}

	/**
	 * @author ChengShu
	 * @date 2015/10/18
	 * 
	 * @Description 以机构为单位分组，把同一机构的箱号分到一个组里
	 * @param boxListInput
	 *            接口输入的箱子列表
	 * @param officeBoxTypeMap
	 *            网点上缴箱袋种别
	 * @return 以机构为单位分不同的箱子的组
	 */
	private Map<String, List<StoBoxInfo>> getBoxGroup(List<StoBoxInfo> boxListInput,
			Map<String, String> officeBoxTypeMap) {

		Map<String, List<StoBoxInfo>> boxMap = Maps.newHashMap();

		List<StoBoxInfo> boxList = null;
		String officeId = "";
		// 循环页面输入的箱号
		for (StoBoxInfo box : boxListInput) {
			officeId = box.getOfficeId();
			// 如果箱号已存在，继续添加
			if (boxMap.containsKey(officeId)) {
				boxMap.get(officeId).add(box);
				// 追加款箱标识
				if (!StoreConstant.BoxType.BOX_PARAGRAPH.equals(officeBoxTypeMap.get(officeId))
						&& StoreConstant.BoxType.BOX_PARAGRAPH.equals(box.getBoxType())) {
					officeBoxTypeMap.put(officeId, box.getBoxType());
				}

			} else {
				// 如果箱号不存在，创建新的列表
				boxList = Lists.newArrayList();
				boxList.add(box);
				boxMap.put(officeId, boxList);
				officeBoxTypeMap.put(officeId, "");
				// 追加款箱标识
				if (StoreConstant.BoxType.BOX_PARAGRAPH.equals(box.getBoxType())) {
					officeBoxTypeMap.put(officeId, StoreConstant.BoxType.BOX_PARAGRAPH);
				} else {
					officeBoxTypeMap.put(officeId, StoreConstant.BoxType.BOX_TAIL);
				}
			}
		}

		return boxMap;
	}

	/**
	 * 验证箱子的出库日期是否正确
	 * 
	 * @param boxList
	 *            待出库的箱袋信息
	 * @param map
	 *            错误信息
	 */
	@SuppressWarnings("unused")
	private void checkBoxOutDate(List<StoBoxInfo> boxList, Map<String, Object> map) {

		Date tomorrowDate = DateUtils.addDate(new Date(), 1);

		// 第二天出库的开始日期
		Date outDateStart = DateUtils.getDateStart(tomorrowDate);
		// 第二天出库的结束日期
		Date outDateEnd = DateUtils.getDateEnd(tomorrowDate);
		// 出库时间不正确的箱子列表
		List<String> errorBoxList = Lists.newArrayList();

		for (StoBoxInfo boxInfo : boxList) {
			// 如果箱袋出库预约时间大于第二天出库日期
			if (null != boxInfo.getOutDate() && (boxInfo.getOutDate().getTime() < outDateStart.getTime()
					|| boxInfo.getOutDate().getTime() > outDateEnd.getTime())) {
				errorBoxList.add(boxInfo.getId());
			}
		}

		// 设置错误信息
		if (errorBoxList.size() > 0) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY,
					"存在不应该明天出库的箱子:" + Collections3.convertToString(errorBoxList, Constant.Punctuation.COMMA));
		}
	}

	/**
	 * @author ChengShu
	 * @date 2015/10/18
	 * 
	 * @Description 查询所有配款网点的物品信息
	 * @param aOfficeId
	 *            金库机构ID
	 * @param map
	 *            错误信息
	 * @param allocationsMap
	 *            机构箱袋信息
	 * @return 所有配款网点的物品信息
	 */
	@SuppressWarnings("unused")
	private Map<String, Map<String, Long>> getQuotaInfo(String aOfficeId, Map<String, Object> map,
			Map<String, List<StoBoxInfo>> allocationsMap) {

		AllAllocateInfo allocateInfo = new AllAllocateInfo();

		// 查询条件:开始时间
		allocateInfo.setCreateTimeStart(new Date());
		// 查询条件:结束时间
		allocateInfo.setCreateTimeEnd(new Date());

		// 业务种别:现金预约
		allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		// 金库机构
		Office aoffice = new Office();
		aoffice.setId(aOfficeId);
		allocateInfo.setaOffice(aoffice);
		allocateInfo.setStatus(AllocationConstant.Status.BetweenConfirm);
		// 执行查询处理
		List<AllAllocateInfo> quotaInfoList = service.findAllocation(allocateInfo);

		// 初始化物品Map
		Map<String, Map<String, Long>> outletsQuotaMap = Maps.newHashMap();
		Map<String, Long> quotaMap = Maps.newHashMap();

		String officeId = "";

		// 循环页面输入的箱号
		for (AllAllocateInfo allInfo : quotaInfoList) {
			// 初始化
			quotaMap = Maps.newHashMap();
			// 网点机构ID
			officeId = allInfo.getrOffice().getId();
			// 保存库房配款的物品信息
			if (AllocationConstant.Status.BetweenConfirm.equals(allInfo.getStatus())) {
				for (AllAllocateItem item : allInfo.getAllAllocateItemList()) {
					quotaMap.put(AllocationCommonUtils.getGoodsKeybygoodsId(item), item.getMoneyNumber());
				}

				// 网点有预约，但是没有配款的场合
			} else {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY,
						"存在有预约但未配款的机构[" + StoreCommonUtils.getOfficeById(officeId).getName() + "]:" + Collections3
								.convertToString(getBoxList(allocationsMap.get(officeId)), Constant.Punctuation.COMMA));
				return outletsQuotaMap;
			}
			// 保存网点的物品信息
			outletsQuotaMap.put(officeId, quotaMap);
		}

		return outletsQuotaMap;
	}

	/**
	 * @author ChengShu
	 * @date 2015/10/18
	 * 
	 * @Description 取得已经登记的下拨信息
	 * @param allocationsMap
	 *            机构信息
	 * @param officeId
	 *            下拨：金库机构ID/上缴：网点机构ID
	 * @param inoutType
	 *            出入库类型
	 * @param map
	 *            结果Map
	 * @return 已经存在下拨记录的调拨信息
	 */

	private Map<String, AllAllocateInfo> getAllocateRegist(Map<String, List<StoBoxInfo>> allocationsMap,
			String officeId, Map<String, Object> map, List<StoBoxInfo> allocationBoxList, Map<String, Object> headInfo,
			Map<String, String> officeBoxTypeMap) {
		// 验证状态
		Map<String, AllAllocateInfo> outletsRegistMap = Maps.newHashMap();

		for (Map.Entry<String, List<StoBoxInfo>> entry : allocationsMap.entrySet()) {
			
			// 循环创建下拨情报
			AllAllocateInfo allocate = new AllAllocateInfo();
			// 设置状态为已确认和已装箱
			List<String> statusList = Lists.newArrayList();
			statusList.add(AllocationConstant.Status.BetweenConfirm);
			statusList.add(AllocationConstant.Status.CashOrderQuotaYes);
			statusList.add(AllocationConstant.Status.Register);
			allocate.setStatuses(statusList);
			
			Office roffice = StoreCommonUtils.getOfficeById(entry.getKey());
			allocate.setrOffice(roffice);
			List<AllAllocateInfo> allocateList = service.findAllocation(allocate);
			if (!Collections3.isEmpty(allocateList)) {
				if (allocateList.get(0).getStatus().equals(AllocationConstant.Status.Register)) {
					String strMessageContent = "网点：" + roffice.getName() + "已经存在一条已登记流水，无法进行配款，请审批后再试！";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
				}
			}
			// 若该网点不存在流水且扫描箱子为尾箱的情况下 自动生成一条流水
			if (officeBoxTypeMap.get(entry.getKey()).equals(StoreConstant.BoxType.BOX_TAIL)) {
				if (Collections3.isEmpty(allocateList)) {
					allocate.setaOffice(officeService.get(officeId));
					service.savedetailboxAllocationForInterFace(allocate, headInfo);
					outletsRegistMap.put(entry.getKey(), allocate);
				} else {
					AllAllocateInfo allocateInfo = allocateList.get(0);
					outletsRegistMap.put(entry.getKey(), allocateInfo);
				}

			} else {
				if (Collections3.isEmpty(allocateList) || allocateList.get(0).getRegisterAmount() == null) {
					String message = "机构[" + StoreCommonUtils.getOfficeById(entry.getKey()).getName() + "]" + "在日期["
							+ DateUtils.formatDate(new Date(), AllocationConstant.Dates.FORMATE_YYYY_MM_DD) + "]"
							+ "不存在预约确认信息。";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, message);

				} else {
					AllAllocateInfo allocateInfo = allocateList.get(0);
					outletsRegistMap.put(entry.getKey(), allocateInfo);
				}
			}
		}
		AllAllocateInfo allocatebynew = new AllAllocateInfo();
		List<String> statusList1 = Lists.newArrayList();
		statusList1.add(AllocationConstant.Status.BetweenConfirm);
		statusList1.add(AllocationConstant.Status.CashOrderQuotaYes);
		allocatebynew.setStatuses(statusList1);
		Office aoffice = new Office();
		aoffice.setId(officeId);
		allocatebynew.setaOffice(aoffice);
		allocatebynew.getSqlMap().put("dsf", "OR o.parent_ids LIKE '%" + aoffice.getId() + "%'");
		// 查询该金库下所有确认以及已装箱的流水信息
		List<AllAllocateInfo> allocateList = service.findAllocation(allocatebynew);
		List<String> officeList = Lists.newArrayList();
		// 获取接口传入箱子的officeList
		for (Map.Entry<String, List<StoBoxInfo>> entry : allocationsMap.entrySet()) {
			officeList.add(entry.getKey());
		}

		for (AllAllocateInfo allallocateinfo : allocateList) {
			// 若该流水不存在确认人并且登记机构不存在于officeList当中，则删除该流水及明细
			if (!officeList.contains(allallocateinfo.getrOffice().getId())) {
				if (allallocateinfo.getConfirmName() == null || "".equals(allallocateinfo.getConfirmName())) {
					int result = service.deleteCash(allallocateinfo);
					if (result == 0) {
						String strMessageContent = "流水单号：" + allallocateinfo.getAllId() + "删除失败！";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
				} else {
					// 若存在确认人 则修改状态以及接受数量 并且删除明细
					allallocateinfo.setStatus(AllocationConstant.Status.BetweenConfirm);
					allallocateinfo.setRegisterNumber(0);
					allallocateinfo.setUpdateDate(new Date());
					int result = service.updateAndDeleteDetail(allallocateinfo);
					if (result == 0) {
						String strMessageContent = "流水单号：" + allallocateinfo.getAllId() + "更新失败！";
						throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
					}
				}

			}

		}
		return outletsRegistMap;
	}

	/**
	 * @author chengshu
	 * @version 2015-10-13
	 * 
	 * @Description PDA库房下拨登记接口
	 * @param headInfo
	 *            接口输入参数
	 * @param officeId
	 *            金库机构ID
	 * @param allocationsMap
	 *            各个机构所属的箱袋列表信息
	 * @param outletsRegistMap
	 *            已经存在的下拨信息
	 * @param outletsQuotaMap
	 *            已赔款网点的物品信息
	 * @param officeBoxTypeMap
	 *            网点箱袋种别
	 * @param map
	 *            接口返回参数
	 * @throws BusinessException
	 */
	@Transactional(readOnly = false)
	public void doBoxHandoutRegister(Map<String, Object> headInfo, String officeId,
			Map<String, List<StoBoxInfo>> allocationsMap, Map<String, AllAllocateInfo> outletsRegistMap,
			Map<String, Object> map, Map<String, String> officeBoxTypeMap) {

		// 网点机构ID
		String outletsOfficeId = "";
		// 登记用调拨信息
		AllAllocateInfo allocateInfo = new AllAllocateInfo();

		List<String> boxList = Lists.newArrayList();

		// 执行更新处理
		for (Map.Entry<String, List<StoBoxInfo>> entry : allocationsMap.entrySet()) {
			// 网点机构ID
			outletsOfficeId = entry.getKey();
			allocateInfo = new AllAllocateInfo();
			// 如果不存在下拨信息，做成下拨信息
			if (outletsRegistMap.containsKey(outletsOfficeId)) {
				// 设置更新信息
				allocateInfo = makeAllocateUpdateInfo(entry.getValue(), outletsRegistMap, headInfo, boxList);
				// 执行更新处理
				service.updateAllocation(allocateInfo);
				// 发送通知
				List<String> paramsList = Lists.newArrayList();
				paramsList.add(allocateInfo.getrOffice().getName());
				paramsList.add(allocateInfo.getAllId());
				SysCommonUtils.allocateMessageQueueAdd(allocateInfo.getBusinessType(), allocateInfo.getStatus(),
						paramsList, allocateInfo.getaOffice().getId(),
						UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString()));
			}
		}
		// 设置更新人信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		// 更新详细状态：在库房
		int updateBoxStatusResult = StoreCommonUtils.updateBoxStatusBatch(boxList, Constant.BoxStatus.COFFER,
				loginUser);
		if (updateBoxStatusResult == 0) {
			String strMessageContent = "箱袋位置(箱袋信息表)更新失败！";
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
		}
	}

	/**
	 * @author ChengShu
	 * @date 2015/10/18
	 * 
	 * @Description 设置新做成用下拨信息
	 * @param boxInfoList
	 *            箱袋信息
	 * @param outletsRegistMap
	 *            已登记的箱袋信息
	 * @param headInfo
	 *            接口输入参数
	 * @param boxList
	 *            更新状态用箱号列表
	 * @return 下拨信息
	 */
	private AllAllocateInfo makeAllocateUpdateInfo(List<StoBoxInfo> boxInfoList,
			Map<String, AllAllocateInfo> outletsRegistMap, Map<String, Object> headInfo, List<String> boxList) {

		// 取得调拨主表
		AllAllocateInfo allocateInfo = outletsRegistMap.get(boxInfoList.get(0).getOfficeId());

		List<StoBoxInfo> newBoxList = Lists.newArrayList();
		String boxNo = "";
		for (StoBoxInfo boxInfo : boxInfoList) {
			boxNo = boxInfo.getId();
			// 保存箱袋信息
			newBoxList.add(boxInfo);
			boxList.add(boxNo);

		}

		// 设置详细表信息
		List<AllAllocateDetail> allocateDetailList = Lists.newArrayList();
		for (StoBoxInfo boxInfo : newBoxList) {

			AllAllocateDetail allocationBox = new AllAllocateDetail();
			allocationBox.setAllDetailId(IdGen.uuid());
			allocationBox.setAllId(allocateInfo.getAllId());
			allocationBox.setBoxNo(boxInfo.getId());
			allocationBox.setBoxType(boxInfo.getBoxType());
			allocationBox.setOutDate(boxInfo.getOutDate());
			allocationBox.setScanFlag(AllocationConstant.ScanFlag.NoScan);
			allocationBox.setRfid(boxInfo.getRfid());
			allocationBox.setAmount(boxInfo.getBoxAmount());
			allocateDetailList.add(allocationBox);
		}

		// 登记个数
		allocateInfo.setRegisterNumber(allocateDetailList.size());

		// 更新人
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		allocateInfo.setUpdateBy(loginUser);

		// 更新时间
		allocateInfo.setUpdateDate(new Date());
		// 状态
		allocateInfo.setStatus(AllocationConstant.Status.CashOrderQuotaYes);

		// 设置下拨箱袋列表
		allocateInfo.setAllDetailList(allocateDetailList);

		return allocateInfo;
	}

	/**
	 * @author chengshu
	 * @version 2015-10-13
	 * 
	 * @Description 验证箱子是否存在，位置是否正确
	 * @param allocationBoxList
	 *            箱子列表
	 * @param boxList
	 *            接口输入箱号列表
	 * @param errorBoxList
	 *            箱号不存在的箱号列表
	 * @param errorPlaceList
	 *            箱号位置不正确的箱号列表
	 * @param errorOfficeList
	 *            机构不正确的箱号列表
	 * @param stoBoxInfoList
	 *            数据库查询出来箱子列表
	 * @param registType
	 *            登记类型(上缴/下拨)
	 * @param officeId
	 *            箱子所属机构ID
	 * @return 箱子信息列表
	 */
	private String checkBoxInfo(List<StoBoxInfo> allocationBoxList, List<String> boxList, List<String> errorBoxList,
			List<String> errorPlaceList, List<String> errorOfficeList, List<StoBoxInfo> stoBoxInfoList,
			String officeId) {

		// 箱子不存在
		if (0 == stoBoxInfoList.size()) {
			for (String boxNo : boxList) {
				errorBoxList.add(boxNo);
			}
			return Constant.FAILED;
		}
		// 箱子状态
		String boxStatus = "";
		// 设置箱子位置
		boxStatus = Constant.BoxStatus.COFFER;
		// 箱子验证
		List<String> stoBoxList = Lists.newArrayList();
		for (StoBoxInfo box : stoBoxInfoList) {
			// 确认箱子位置
			if (!Constant.BoxStatus.EMPTY.equals(box.getBoxStatus()) && !boxStatus.equals(box.getBoxStatus())) {
				errorPlaceList.add(box.getRfid());
				continue;
			}

			stoBoxList.add(box.getId());
			allocationBoxList.add(box);
		}
		// 位置不正确返回
		if (errorPlaceList.size() > 0) {
			return Constant.FAILED;
		}

		// 箱子机构步正确的场合
		if (errorOfficeList.size() > 0) {
			return Constant.FAILED;
		}

		// 箱子不存在的场合
		if (boxList.size() != stoBoxInfoList.size()) {
			for (String boxNo : boxList) {
				if (!stoBoxList.contains(boxNo)) {
					errorBoxList.add(boxNo);
				}
			}
		}
		// 不存在返回
		if (errorBoxList.size() > 0) {
			return Constant.FAILED;
		}

		return Constant.SUCCESS;
	}

	/**
	 * 把箱子实体改成字符串列表
	 */
	private List<String> getBoxList(List<StoBoxInfo> boxInfoList) {

		List<String> boxList = Lists.newArrayList();
		if (null == boxInfoList || boxInfoList.size() == 0) {
			return boxList;
		}

		for (StoBoxInfo boxInfo : boxInfoList) {
			boxList.add(boxInfo.getId());
		}

		return boxList;
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @param map
	 * @param serviceNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo) {
		map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);

		return gson.toJson(map);
	}

	/**
	 * 装箱后删除装箱的箱子的明细
	 * 
	 * @param map
	 * @param serviceNo
	 * @return
	 */
	private void deleteBoxDetail(List<StoBoxInfo> boxList) {
		for (StoBoxInfo stoBoxInfo : boxList) {
			stoBoxInfoService.deleteBoxDetail(stoBoxInfo.getId());
		}
	}

}
