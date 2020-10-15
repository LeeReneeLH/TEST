package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxDetail;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.ServiceException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service19
 * <p>
 * Description:
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service19")
@Scope("singleton")
public class Service19 extends HardwardBaseService {

	@Autowired
	StoBoxInfoService stoBoxInfoService;
	@Autowired
	AllocationService service;
	/** 网点现金上缴追加交接 修改人：xp 修改时间：2017-10-27 begin */
	@Autowired
	StoEscortInfoService stoEscortInfoService;
	/** end */

	/** 一次执行箱号个数 */
	private static final double LOOP_NUM = 1000;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		// try {
		// 验证接口输入参数
		String paraCheckResult = checkBoxHandoutRegister(paramMap);
		// 验证失败的场合，退出
		if (paraCheckResult != null) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, paraCheckResult);
			return setReturnMap(map, serviceNo);
		}
		// 尾箱出库预约日期Map
		Map<String, Date> boxOutDateMap = Maps.newHashMap();
		// 验证尾箱的出库预约时间
		checkBoxHandinRegisterOutDate(paramMap, map, boxOutDateMap);
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 网点机构ID
		String officeId = paramMap.get(Parameter.OFFICE_ID_KEY).toString();
		Office outletsOffice = StoreCommonUtils.getOfficeById(officeId);
		Office storeOffice = BusinessUtils.getCashCenterByOffice(outletsOffice);

		// 箱袋列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);

		List<StoBoxInfo> allocationBoxList = getBoxList(list, map, officeId);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		/** 网点现金上缴追加交接 修改人：xp 修改时间：2017-10-27 begin */
		// 押运人员列表
		List<Map<String, Object>> escortList = null;
		// 网点接收人员列表
		List<Map<String, Object>> branchList = null;
		// 获取押运人员

		if (paramMap.get(Parameter.ESCORT_LIST_KEY) != null) {
			escortList = (List<Map<String, Object>>) paramMap.get(Parameter.ESCORT_LIST_KEY);
		}
		// 获取网点接收人员
		if (paramMap.get(Parameter.ESCORT_LIST_KEY) != null) {
			branchList = (List<Map<String, Object>>) paramMap.get(Parameter.BRANCH_LIST_KEY);
		}
		// 设置交接主表主表信息
		AllHandoverInfo handoverInfo = new AllHandoverInfo();
		handoverInfo.setHandoverId(IdGen.uuid());
		handoverInfo.setCreateDate(new Date());
		handoverInfo.setAcceptDate(new Date());
		// 设置移交人信息
		if (!Collections3.isEmpty(escortList)) {
			for (Map<String, Object> escortMap : escortList) {
				AllHandoverDetail handoverDetail = new AllHandoverDetail();
				String strId = escortMap.get(Parameter.OPT_USER_ID_KEY).toString();
				String strName = escortMap.get(Parameter.OPT_USER_NAME_KEY).toString();
				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setHandoverId(handoverInfo.getId());
				handoverDetail.setEscortId(strId);
				handoverDetail.setEscortName(strName);
				handoverDetail.setOperationType(AllocationConstant.OperationType.ACCEPT);
				handoverInfo.getDetailList().add(handoverDetail);
			}
		}
		// 设置网点接收人信息
		if (!Collections3.isEmpty(branchList)) {
			for (Map<String, Object> branchMap : branchList) {
				AllHandoverDetail handoverDetail = new AllHandoverDetail();
				String strId = branchMap.get(Parameter.USER_ID_KEY).toString();
				String strName = branchMap.get(Parameter.OPT_USER_NAME_KEY).toString();
				// 设置交接明细表信息
				handoverDetail.setDetailId(IdGen.uuid());
				handoverDetail.setHandoverId(handoverInfo.getId());
				handoverDetail.setEscortId(strId);
				handoverDetail.setEscortName(strName);
				handoverDetail.setOperationType(AllocationConstant.OperationType.TURN_OVER);
				handoverInfo.getDetailList().add(handoverDetail);
			}
		}
		/** end */

		// 上缴信息登记处理
		doBoxHandinRegister(paramMap, allocationBoxList, boxOutDateMap, storeOffice, handoverInfo);

		/** 网点现金上缴追加交接 修改人：xp 修改时间：2017-10-27 begin */
		// 插入交接信息
		service.acceptConfirm(handoverInfo);
		/** end */
		// 成功结果
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(map, serviceNo);
	}

	/**
	 * @author liuyaowen
	 * @version 2017-7-13
	 *
	 * @Description 验证 PDA库房下拨登记接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	private String checkBoxHandoutRegister(Map<String, Object> headInfo) {
		String errorMsg = "";
		// 登记人ID
		if (headInfo.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_ID_KEY).toString())) {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
			// 参数异常
			errorMsg = "参数错误--------userId:" + CommonUtils.toString(headInfo.get(Parameter.USER_ID_KEY));
			return errorMsg;
		}

		// 登记人姓名
		if (headInfo.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.USER_NAME_KEY).toString())) {
			logger.debug("参数错误--------userName:" + CommonUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)));
			// 参数异常
			errorMsg = "参数错误--------userName:" + CommonUtils.toString(headInfo.get(Parameter.USER_NAME_KEY));
			return errorMsg;
		}

		// 金库机构ID
		if (headInfo.get(Parameter.OFFICE_ID_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
			errorMsg = "参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY));
			return errorMsg;
		}

		// 箱袋列表
		if (headInfo.get(Parameter.BOX_LIST_KEY) == null
				|| ((List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY)).size() == 0) {
			logger.debug("参数错误--------boxList:" + CommonUtils.toString(headInfo.get(Parameter.BOX_LIST_KEY)));
			// 参数异常
			errorMsg = "参数错误--------boxList:" + CommonUtils.toString(headInfo.get(Parameter.BOX_LIST_KEY));
			return errorMsg;

		} else {
			for (Map<String, Object> boxMap : (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY)) {
				if ((boxMap.get(Parameter.BOX_NO_KEY) == null
						|| StringUtils.isBlank(boxMap.get(Parameter.BOX_NO_KEY).toString()))) {
					logger.debug("参数错误--------boxList:boxNo" + CommonUtils.toString(boxMap.get(Parameter.BOX_NO_KEY)));
					// 参数异常
					errorMsg = "参数错误--------boxList:boxNo" + CommonUtils.toString(boxMap.get(Parameter.BOX_NO_KEY));
					return errorMsg;
				}
				if ((boxMap.get(Parameter.RFID_KEY) == null
						|| StringUtils.isBlank(boxMap.get(Parameter.RFID_KEY).toString()))) {
					logger.debug("参数错误--------boxList:RFID" + CommonUtils.toString(boxMap.get(Parameter.RFID_KEY)));
					// 参数异常
					errorMsg = "参数错误--------boxList:RFID" + CommonUtils.toString(boxMap.get(Parameter.RFID_KEY));
					return errorMsg;
				}
			}
		}
		return null;
	}

	/**
	 * @author liuyaowen
	 * @version 2017-7-13
	 * 
	 * @Description 验证 PDA库房下拨登记接口 输入参数
	 * @param headInfo
	 *            接口输入参数
	 * @param map
	 *            异常信息
	 * @return 处理结果
	 */
	@SuppressWarnings("unchecked")
	private String checkBoxHandinRegisterOutDate(Map<String, Object> headInfo, Map<String, Object> map,
			Map<String, Date> outDateMap) {

		// 验证尾箱出库预约时间
		List<String> outDateList = Lists.newArrayList();
		// 今天结束时间
		Date todayEnd = DateUtils.getDateEnd(new Date());
		String boxNo = "";
		Date outDate = null;
		StoBoxInfo stoBoxInfo = new StoBoxInfo();
		for (Map<String, Object> boxMap : (List<Map<String, Object>>) headInfo.get(Parameter.BOX_LIST_KEY)) {
			// 取得箱号
			stoBoxInfo.setId(String.valueOf(boxMap.get(Parameter.BOX_NO_KEY)));
			stoBoxInfo.setRfid(String.valueOf(boxMap.get(Parameter.RFID_KEY)));
			List<StoBoxInfo> boxInfoList = stoBoxInfoService.getBindingBoxInfo(stoBoxInfo);
			if (Collections3.isEmpty(boxInfoList)) {
				String strMessageContent = "箱袋编号：" + boxMap.get(Parameter.BOX_NO_KEY).toString() + "的信息不存在";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
			StoBoxInfo boxInfo = boxInfoList.get(0);
			boxNo = boxInfo.getId().toString();
			// 箱子设置出库预约时间的场合
			if (boxMap.get(Parameter.OUT_DATE_KEY) != null
					&& StringUtils.isNotBlank(boxMap.get(Parameter.OUT_DATE_KEY).toString())) {
				// 取得设置的出库预约时间
				outDate = DateUtils.parseDate(boxMap.get(Parameter.OUT_DATE_KEY));
				if (outDate.getTime() < todayEnd.getTime()) {
					outDateList.add(boxNo);
					continue;
				}

				// 没有设置出库预约时间的尾箱，默认出库预约时间是明天
			} else if (Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
				outDate = DateUtils.addDate(new Date(), 1);
			}

			// 保存尾箱的出库预约时间
			outDateMap.put(boxNo, outDate);
		}

		// 存在时间验证不正确的箱袋
		if (outDateList.size() > 0) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY,
					"尾箱的出库预约时间不能早于明天:" + Collections3.convertToString(outDateList, Constant.Punctuation.COMMA));
			return Constant.FAILED;
		}

		return Constant.SUCCESS;
	}

	/**
	 * @author liuyaowen
	 * @version 2017-7-13
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
		for (int i = 0; i < list.size(); i++) {
			boxs[i] = list.get(i).get(Parameter.BOX_NO_KEY).toString();
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
	 * @author liuyaowen
	 * @version 2017-7-13
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
		if (Collections3.isEmpty(stoBoxInfoList)) {
			for (String boxNo : boxList) {
				errorBoxList.add(boxNo);
			}
			return Constant.FAILED;
		}

		// 箱子状态
		String boxStatus = "";
		boxStatus = Constant.BoxStatus.BANK_OUTLETS;
		// 箱子验证
		List<String> stoBoxList = Lists.newArrayList();
		for (StoBoxInfo box : stoBoxInfoList) {
			// 确认箱子位置
			if (!Constant.BoxStatus.EMPTY.equals(box.getBoxStatus()) && !boxStatus.equals(box.getBoxStatus())) {
				errorPlaceList.add(box.getId());
				continue;
			}

			// 验证所属机构
			if (!officeId.equals(box.getOfficeId())) {
				errorOfficeList.add(box.getId());
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
	 * @author liuyaowen
	 * @version 2017-7-13
	 * 
	 * @Description 以机构为单位分组，把同一机构的箱号分到一个组里
	 * @param boxListInput
	 *            接口输入的箱子列表
	 * @param officeBoxTypeMap
	 *            网点上缴箱袋种别
	 * @return 以机构为单位分不同的箱子的组
	 */
	// private Map<String, List<StoBoxInfo>> getBoxGroup(List<StoBoxInfo>
	// boxListInput,
	// Map<String, String> officeBoxTypeMap) {
	//
	// Map<String, List<StoBoxInfo>> boxMap = Maps.newHashMap();
	//
	// List<StoBoxInfo> boxList = null;
	// String officeId = "";
	//
	// // 循环页面输入的箱号
	// for (StoBoxInfo box : boxListInput) {
	// officeId = box.getOfficeId();
	// // 如果箱号已存在，继续添加
	// if (boxMap.containsKey(officeId)) {
	// boxMap.get(officeId).add(box);
	//
	// } else {
	// // 如果箱号不存在，创建新的列表
	// boxList = Lists.newArrayList();
	// boxList.add(box);
	// boxMap.put(officeId, boxList);
	// officeBoxTypeMap.put(officeId, "");
	// }
	// }
	// return boxMap;
	// }

	/**
	 * @author liuyaowen
	 * @version 2017-7-13
	 * 
	 * @Description PDA网点上缴登记处理
	 * @param headInfo
	 *            接口输入参数
	 * @param allocationsMap
	 *            各个机构所属的箱袋列表信息
	 * @param outletsRegistMap
	 *            已经存在的上缴信息
	 * @param boxOutDateMap
	 *            箱子的出库预约时间
	 * @param storeOffice
	 *            网点所属金库机构
	 * @throws ServiceException
	 */
	@Transactional(readOnly = false)
	public void doBoxHandinRegister(Map<String, Object> headInfo, List<StoBoxInfo> boxInfoList,
			Map<String, Date> boxOutDateMap, Office storeOffice, AllHandoverInfo handOverInfo) throws ServiceException {

		// 登记用调拨信息
		AllAllocateInfo allocateInfo = new AllAllocateInfo();
		List<String> boxList = Lists.newArrayList();

		// 更新箱子状态和出库预约时间的箱袋列表
		Map<String, List<String>> boxChangeStatusAndOutdateMap = Maps.newHashMap();
		// 箱子列表
		List<String> boxStatusAndOutdateList = Lists.newArrayList();

		allocateInfo = new AllAllocateInfo();

		// 设置插入信息
		allocateInfo = makeAllocateSaveInfo(boxInfoList, headInfo, storeOffice, boxList, boxOutDateMap, handOverInfo);
		// 执行插入处理
		service.insertAllocation(allocateInfo);
		// 发送通知
		List<String> paramsList = Lists.newArrayList();
		paramsList.add(allocateInfo.getrOffice().getName());
		paramsList.add(allocateInfo.getAllId());
		SysCommonUtils.allocateMessageQueueAdd(allocateInfo.getBusinessType(), allocateInfo.getStatus(), paramsList,
				allocateInfo.getaOffice().getId(), UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString()));
		// 出库时间
		String outDate = "";
		// 更新箱子状态和出库预约时间
		for (StoBoxInfo boxInfo : boxInfoList) {
			// 尾箱的场合
			if (Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
				// 取得出库日期
				outDate = DateUtils.formatDate(boxOutDateMap.get(boxInfo.getId()));
				// 如果该出库日期已经存在，继续追加箱号信息
				if (boxChangeStatusAndOutdateMap.containsKey(outDate)) {
					boxChangeStatusAndOutdateMap.get(outDate).add(boxInfo.getId());

					// 如果改出库日期不存在，追加该出库日期的箱号信息
				} else {
					boxStatusAndOutdateList = Lists.newArrayList();
					boxStatusAndOutdateList.add(boxInfo.getId());
					boxChangeStatusAndOutdateMap.put(outDate, boxStatusAndOutdateList);
				}
			}
		}
		// 设置更新人信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		// 更新箱子出库预约时间
		for (Entry<String, List<String>> entry : boxChangeStatusAndOutdateMap.entrySet()) {
			StoreCommonUtils.updateOutdateBatch(entry.getValue(), DateUtils.parseDate(entry.getKey()), loginUser);
		}
		// 更新箱子状态：在途
		StoreCommonUtils.updateBoxStatusBatch(boxList, Constant.BoxStatus.ONPASSAGE, loginUser);
	}

	/**
	 * @author ChengShu
	 * @date 2015/10/18
	 * 
	 * @Description 设置新做成用下拨信息
	 * @param boxInfoList
	 *            箱袋信息
	 * @param headInfo
	 *            接口输入参数
	 * @param storeOffice
	 *            金库机构
	 * @param registType
	 *            登记种别
	 * @param boxList
	 *            更新状态用箱号列表
	 * @return 下拨信息
	 */
	private AllAllocateInfo makeAllocateSaveInfo(List<StoBoxInfo> boxInfoList, Map<String, Object> headInfo,
			Office storeOffice, List<String> boxList, Map<String, Date> boxOutDateMap, AllHandoverInfo handOverInfo) {

		// 调拨主表
		AllAllocateInfo allocateInfo = new AllAllocateInfo();

		// 取得流水号
		String allId = BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.allocation.cashHandin"),
				storeOffice);
		// 设置详细表信息
		List<AllAllocateDetail> allocateDetailList = setAllocateDetailList(allId, boxInfoList,
				AllocationConstant.ScanFlag.NoScan, boxList, boxOutDateMap);
		// 设置物品表信息
		List<AllAllocateItem> allocateItemList = setAllocateItemList(allId, boxInfoList,
				AllocationConstant.confirmFlag.Confirm);
		BigDecimal amount = new BigDecimal(0);
		if (boxInfoList != null && boxInfoList.size() > 0) {
			for (StoBoxInfo boxInfo : boxInfoList) {
				// 计算总金额
				if (Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
					continue;
				} else {
					amount = amount.add(boxInfo.getBoxAmount());
				}
			}
		}
		// 设置调拨信息
		StoBoxInfo boxInfo = new StoBoxInfo();
		if (boxInfoList.size() > 1) {
			boxInfo = boxInfoList.get(1);
			boxInfo.setBoxAmount(amount);
		} else {
			boxInfo = boxInfoList.get(0);
			boxInfo.setBoxAmount(amount);
		}
		allocateInfo = setAllocateInfo(allId, boxInfo, allocateDetailList, allocateItemList, headInfo, storeOffice,
				handOverInfo);

		return allocateInfo;
	}

	/**
	 * 设置调拨箱袋信息
	 * 
	 * @param allId
	 *            流水号
	 * @param boxInfo
	 *            箱袋列表
	 * @param scan
	 *            扫描状态
	 * @param registType
	 *            登记种别
	 * @param boxList
	 *            更新状态用箱号列表
	 * @return 箱袋列表
	 */
	private List<AllAllocateDetail> setAllocateDetailList(String allId, List<StoBoxInfo> boxInfoList, String scan,
			List<String> boxList, Map<String, Date> boxOutDateMap) {
		// 循环创建下拨情报
		List<AllAllocateDetail> allocateDetailList = Lists.newArrayList();
		// 设置详细表信息
		AllAllocateDetail allocationDetail = null;
		for (StoBoxInfo boxInfo : boxInfoList) {
			allocationDetail = new AllAllocateDetail();
			// 尾箱不设置金额
			if (!Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
				allocationDetail.setAmount(boxInfo.getBoxAmount());
			}
			allocationDetail.setAllDetailId(IdGen.uuid());
			allocationDetail.setAllId(allId);
			allocationDetail.setBoxNo(boxInfo.getId());
			allocationDetail.setRfid(boxInfo.getRfid());
			allocationDetail.setBoxType(boxInfo.getBoxType());
			if (Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
				allocationDetail.setOutDate(boxOutDateMap.get(boxInfo.getId()));
			}
			allocationDetail.setScanFlag(scan);
			allocationDetail.setPdaScanDate(new Date());

			allocateDetailList.add(allocationDetail);

			boxList.add(boxInfo.getId());
		}
		return allocateDetailList;
	}

	/**
	 * 设置调拨物品信息
	 * 
	 * @param allId
	 *            流水号
	 * @param boxInfo
	 *            箱袋列表
	 * @param scan
	 *            扫描状态
	 * @return 箱袋列表
	 */
	private List<AllAllocateItem> setAllocateItemList(String allId, List<StoBoxInfo> boxInfoList, String confirmFlag) {
		// 循环创建情报
		List<AllAllocateItem> allocateItemList = Lists.newArrayList();
		int i = 0;
		// 物品ID集合
		List<String> boxDetailList = new ArrayList<>();
		Map<String, AllAllocateItem> allocateItemMap = Maps.newHashMap();
		AllAllocateItem allocateItem = null;
		String goodsId = "";
		// 设置详细表信息
		for (StoBoxInfo boxInfo : boxInfoList) {
			if (Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
				continue;
			} else {
				// 根据箱号，取得所有箱子明细的实体
				List<StoBoxDetail> stoBoxDetailList = StoreCommonUtils.getBoxDetailList(boxInfo.getId());
				for (StoBoxDetail detail : stoBoxDetailList) {
					// 如果物品ID已存在，更新数量和金额
					if (boxDetailList.contains(detail.getGoodsId())) {
						// 根据物品id取得之前存在map中的物品明细
						AllAllocateItem item = allocateItemMap.get(detail.getGoodsId());
						allocateItem = new AllAllocateItem();
						// 合并数量
						long number = Long.parseLong(detail.getGoodsNum()) + item.getMoneyNumber();
						// 合并金额
						BigDecimal amount = new BigDecimal(detail.getGoodsAmount()).add(item.getMoneyAmount())
								.setScale(2);
						allocateItem.setAllItemsId(IdGen.uuid());
						allocateItem.setAllId(allId);
						allocateItem.setGoodsId(detail.getGoodsId());
						allocateItem.setMoneyNumber(number);
						allocateItem.setMoneyAmount(amount);
						allocateItem.setConfirmFlag(confirmFlag);
						// 拿到存在的物品ID所对应的List下标
						for (AllAllocateItem allitem : allocateItemList) {
							if (allitem.getGoodsId().equals(detail.getGoodsId())) {
								i = allocateItemList.indexOf(allitem);
							}
						}
						// 移除重复的物品ID所对应的实体
						allocateItemList.remove(i);
						// 否则插入一条
					} else {
						// 设置调拨物品明细
						allocateItem = new AllAllocateItem();
						allocateItem.setAllItemsId(IdGen.uuid());
						allocateItem.setAllId(allId);
						allocateItem.setGoodsId(detail.getGoodsId());
						allocateItem.setMoneyNumber(Long.parseLong(detail.getGoodsNum()));
						BigDecimal amount = new BigDecimal(detail.getGoodsAmount()).setScale(2);
						allocateItem.setMoneyAmount(amount);
						allocateItem.setConfirmFlag(confirmFlag);
					}
					goodsId = detail.getGoodsId();
					allocateItemList.add(allocateItem);
					// 将物品ID添加到集合中
					boxDetailList.add(detail.getGoodsId());
					allocateItemMap.put(goodsId, allocateItem);
				}
			}
		}

		return allocateItemList;
	}

	/**
	 * 设置调拨信息
	 * 
	 * @param allId
	 *            流水号
	 * @param boxInfo
	 *            箱袋信息
	 * @param allocateDetailList
	 *            箱袋信息
	 * @param headInfo
	 *            接口输入参数
	 * @param storeOffice
	 *            金库机构
	 * @param registType
	 *            登记种别
	 * @return
	 */
	private AllAllocateInfo setAllocateInfo(String allId, StoBoxInfo boxInfo,
			List<AllAllocateDetail> allocateDetailList, List<AllAllocateItem> allocateItemList,
			Map<String, Object> headInfo, Office storeOffice, AllHandoverInfo handOverInfo) {

		// 登记人ID
		String userId = headInfo.get(Parameter.USER_ID_KEY).toString();
		// 登记人姓名
		String userName = headInfo.get(Parameter.USER_NAME_KEY).toString();

		AllAllocateInfo allocateInfo = new AllAllocateInfo();
		// 流水号
		allocateInfo.setAllId(allId);
		// 路线ID
		allocateInfo.setRouteId(boxInfo.getRouteId());
		// 路线名称
		allocateInfo.setRouteName(boxInfo.getRouteName());
		// 箱袋所属机构
		Office boxOffice = new Office();
		boxOffice.setId(boxInfo.getOfficeId());
		boxOffice.setName(boxInfo.getOfficeName());
		// 登记机构
		allocateInfo.setrOffice(boxOffice);
		// 接收机构
		allocateInfo.setaOffice(storeOffice);
		// 交接状态（0:登记）
		allocateInfo.setStatus(AllocationConstant.Status.Onload);
		// 设置登记金额
		allocateInfo.setRegisterAmount(boxInfo.getBoxAmount());
		// 业务类型:库外调拨
		allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		// 登记个数
		allocateInfo.setRegisterNumber(allocateDetailList.size());
		/** 网点现金上缴追加交接 修改人：xp 修改时间：2017-10-27 begin */
		// 设置网点上缴交接ID
		allocateInfo.setPointHandoverId(handOverInfo.getId());
		/** end */
		// 登记人
		User createBy = new User();
		createBy.setId(userId);
		allocateInfo.setCreateBy(createBy);
		allocateInfo.setUpdateBy(createBy);
		// 登记人姓名
		allocateInfo.setCreateName(userName);
		allocateInfo.setUpdateName(userName);
		// 登记时间
		allocateInfo.setCreateDate(new Date());
		// 更新时间
		allocateInfo.setUpdateDate(new Date());
		// 设置详细表信息
		allocateInfo.setAllDetailList(allocateDetailList);
		// 设置物品表信息
		allocateInfo.setAllAllocateItemList(allocateItemList);
		return allocateInfo;
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
}
