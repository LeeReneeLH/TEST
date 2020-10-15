package com.coffer.external.service;

import java.util.Arrays;
import java.util.Date;
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
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0188
 * <p>
 * Description:
 * </p>
 * 
 * @author liuyaowen
 * @date 2017年8月9日 上午10:41:10
 */
@Component("Service0188")
@Scope("singleton")
public class Service0188 extends HardwardBaseService {

	/** 一次执行箱号个数 */
	private static final double LOOP_NUM = 1000;

	@Autowired
	AllocationService service;
	@Autowired
	OfficeService officeService;
	@Autowired
	private StoBoxInfoService stoBoxInfoService;

	@Override
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
		// 流水号
		String allId = paramMap.get(Parameter.ALL_ID).toString();
		// 箱袋列表
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);
		// 需要恢复状态的箱袋
		List<String> reOldBoxNoList = Lists.newArrayList();
		// 根据箱号取得箱子列表，验证箱子是否存在或状态是否正确
		List<StoBoxInfo> allocationBoxList = getBoxList(allId, list, map, officeId, reOldBoxNoList);
		// 验证失败的场合，退出
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 下拨信息登记处理
		doBoxHandoutRegister(paramMap, allocationBoxList, officeId, allId, map, reOldBoxNoList);
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

		// 流水号
		if (headInfo.get(Parameter.ALL_ID) == null || StringUtils.isBlank(headInfo.get(Parameter.ALL_ID).toString())) {
			logger.debug("参数错误--------officeId:" + CommonUtils.toString(headInfo.get(Parameter.ALL_ID)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY,
					"参数错误--------allId:" + CommonUtils.toString(headInfo.get(Parameter.ALL_ID)));
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
	private List<StoBoxInfo> getBoxList(String allId, List<Map<String, Object>> list, Map<String, Object> map,
			String officeId, List<String> reOldBoxNoList) {

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
			checkResult = checkBoxInfo(allId, allocationBoxList, boxList, errorBoxList, errorPlaceList, errorOfficeList,
					stoBoxInfoList, officeId, reOldBoxNoList);
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
	 * @param officeId
	 *            箱子所属机构ID
	 * @return 箱子信息列表
	 */
	private String checkBoxInfo(String allId, List<StoBoxInfo> allocationBoxList, List<String> boxList,
			List<String> errorBoxList, List<String> errorPlaceList, List<String> errorOfficeList,
			List<StoBoxInfo> stoBoxInfoList, String officeId, List<String> reOldBoxNoList) {
		// 验证箱子是否绑定到其他流水中
		for (StoBoxInfo box : stoBoxInfoList) {
			List<AllAllocateInfo> allList = service.getAllIdByBoxNo(box.getId());
			for (AllAllocateInfo info : allList) {
				if (AllocationConstant.Status.CashOrderQuotaYes.equals(info.getStatus())
						|| AllocationConstant.Status.Onload.equals(info.getStatus())
						|| AllocationConstant.Status.HandoverTodo.equals(info.getStatus())) {
					String strMessageContent = "箱号:" + box.getId() + ",RFID" + box.getRfid() + "的箱子已绑定到其他任务中，不可再次绑定";
					throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);

				}
			}
		}
		// 不需要恢复状态的箱袋
		List<String> oldBoxNoList = Lists.newArrayList();
		// 需要验证状态的箱袋
		List<String> newBoxNoList = Lists.newArrayList();
		// 箱袋详细
		AllAllocateDetail detail = new AllAllocateDetail();
		detail.setAllId(allId);
		// 查询流水对应的箱子详细
		List<AllAllocateDetail> detailList = service.findDetailByAllId(detail);
		for (AllAllocateDetail allocateDetail : detailList) {
			// 判断第二次提交的所有箱子是否在明细表中存在
			if (!boxList.contains(allocateDetail.getBoxNo())) {
				// 不存在的箱号添加到List中
				reOldBoxNoList.add(allocateDetail.getBoxNo());
			} else {
				oldBoxNoList.add(allocateDetail.getBoxNo());
			}
		}
		newBoxNoList = Collections3.subtract(boxList, oldBoxNoList);
		List<StoBoxInfo> newStoBoxInfoList = Lists.newArrayList();
		if (!Collections3.isEmpty(newBoxNoList)) {
			// 根据箱号，取得所有箱子的实体
			newStoBoxInfoList = StoreCommonUtils.getBoxListByArray(newBoxNoList);
		}
		// 箱子不存在
		if (0 == stoBoxInfoList.size()) {
			for (String boxNo : newBoxNoList) {
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
		for (StoBoxInfo box : newStoBoxInfoList) {
			// 确认箱子位置
			if (!Constant.BoxStatus.EMPTY.equals(box.getBoxStatus()) && !boxStatus.equals(box.getBoxStatus())) {
				errorPlaceList.add(box.getRfid());
				continue;
			}
			// 验证所属机构
			if (!officeId.equals(box.getOfficeId())) {
				errorOfficeList.add(box.getRfid());
			}
		}
		for (StoBoxInfo box : stoBoxInfoList) {
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
	public void doBoxHandoutRegister(Map<String, Object> headInfo, List<StoBoxInfo> boxList, String officeId,
			String allId, Map<String, Object> map, List<String> reOldBoxNoList) {
		String boxNo = "";
		List<String> boxNoList = Lists.newArrayList();
		for (StoBoxInfo boxInfo : boxList) {
			boxNo = boxInfo.getId();
			boxNoList.add(boxNo);
		}
		// 登记用调拨信息
		AllAllocateInfo allocateInfo = new AllAllocateInfo();
		// 组装主表及detail数据
		allocateInfo = makeAllocateUpdateInfo(boxList, allId, headInfo, allocateInfo);
		// 执行更新处理
		service.updateTempAllocation(allocateInfo);
		// 设置更新人信息
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		if (!Collections3.isEmpty(reOldBoxNoList)) {
			// 更新第二次提交删除的箱子详细状态：在库房
			int updateNewBoxStatusResult = StoreCommonUtils.updateBoxStatusBatch(reOldBoxNoList,
					Constant.BoxStatus.COFFER, loginUser);
			if (updateNewBoxStatusResult == 0) {
				String strMessageContent = "箱袋位置(箱袋信息表)更新失败！";
				throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E99, strMessageContent);
			}
		}
		// 更新详细状态：在途
		int updateBoxStatusResult = StoreCommonUtils.updateBoxStatusBatch(boxNoList, Constant.BoxStatus.ONPASSAGE,
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
	private AllAllocateInfo makeAllocateUpdateInfo(List<StoBoxInfo> boxInfoList, String allId,
			Map<String, Object> headInfo, AllAllocateInfo allocateInfo) {
		// 设置详细表信息
		List<AllAllocateDetail> allocateDetailList = Lists.newArrayList();
		for (StoBoxInfo boxInfo : boxInfoList) {
			AllAllocateDetail allocationBox = new AllAllocateDetail();
			allocationBox.setAllDetailId(IdGen.uuid());
			allocationBox.setAllId(allId);
			allocationBox.setBoxNo(boxInfo.getId());
			allocationBox.setScanFlag(AllocationConstant.ScanFlag.NoScan);
			allocationBox.setRfid(boxInfo.getRfid());
			allocationBox.setBoxType(boxInfo.getBoxType());
			allocationBox.setOutDate(boxInfo.getOutDate());
			allocationBox.setAmount(boxInfo.getBoxAmount());
			allocateDetailList.add(allocationBox);
		}
		allocateInfo.setAllId(allId);
		// 登记个数
		allocateInfo.setRegisterNumber(allocateDetailList.size());

		// 更新人
		User loginUser = UserUtils.get(headInfo.get(Parameter.USER_ID_KEY).toString());
		allocateInfo.setUpdateBy(loginUser);

		// 更新时间
		allocateInfo.setUpdateDate(new Date());
		// 状态
		allocateInfo.setStatus(AllocationConstant.Status.Onload);

		// 设置下拨箱袋列表
		allocateInfo.setAllDetailList(allocateDetailList);

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
