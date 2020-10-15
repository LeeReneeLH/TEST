package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
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
 * Title: Service79
 * <p>
 * Description: ATM清机登记接口
 * </p>
 * 
 * @author xp
 * @date 2017年11月9日
 */
@Component("Service79")
@Scope("singleton")
public class Service79 extends HardwardBaseService {

	/**
	 * 
	 * @author xp
	 * @version 2017年11月9日
	 * 
	 *          ATM清机登记
	 * @param paramMap
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	@SuppressWarnings(value = "unchecked")
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 语言环境
		initLocale(paramMap);
		String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
		// 异常箱子信息
		List<String> errorBox = Lists.newArrayList();
		// 未查询到的箱子信息
		List<String> notFindBox = Lists.newArrayList();
		// 调拨信息分组
		Map<String, AllAllocateInfo> allocateMap = Maps.newHashMap();
		// 箱子分组
		List<String> updateBoxList = Lists.newArrayList();
		// 未查询到的物品分组
		List<String> notFindGoods = Lists.newArrayList();
		// 参数
		// 用户id
		String userId = paramMap.get(Parameter.USER_ID_KEY).toString();
		// 取得箱子列表
		List<Map<String, Object>> boxList = (List<Map<String, Object>>) paramMap.get(Parameter.BOX_LIST_KEY);
		// 取得箱子类型
		List<String> boxStatusList = Arrays.asList(Global.getConfig("sto.box.boxtype.atmShow").split(";"));
		// 查询当前登录用户
		User user = UserUtils.get(paramMap.get(Parameter.USER_ID_KEY).toString());
		// 验证json参数
		checkParameter(userId, boxList, map);
		if (map.size() > 0) {
			return setReturnMap(map, serviceNo);
		}
		// 验证User是否为空
		if (user != null && paramMap.get(Parameter.USER_NAME_KEY).equals(user.getName())) {
			// 验证箱子且整理业务数据
			checkBoxAndBusinessData(map, boxList, user, updateBoxList, allocateMap, notFindBox, errorBox, notFindGoods,
					boxStatusList);
			if (map.size() > 0) {
				return setReturnMap(map, serviceNo);
			}
		} else {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7806", null, locale));
			return setReturnMap(map, serviceNo);
		}
		// 执行更新方法
		updateBoxAndAllocate(updateBoxList, allocateMap, user, map);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		// 返回信息
		return setReturnMap(map, serviceNo);
	}

	/**
	 * 验证参数
	 * 
	 * @author xp
	 * @param userId
	 * @param userName
	 * @param boxList
	 */

	private void checkParameter(String userId, List<Map<String, Object>> boxList,
			Map<String, Object> map) {
		// 验证参数
		// 验证userId是否为空
		// 解析用户编号
		if (StringUtils.isBlank(userId)) {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(userId));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7804", null, locale));
		}
		// 验证钞箱列表是否为空
		if (Collections3.isEmpty(boxList)) {
			logger.debug("参数错误--------boxList:" + CommonUtils.toString(boxList));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7800", null, locale));
		}
		// 验证钞箱信息
		for (Map<String, Object> detail : boxList) {
			String rfid = detail.get(Parameter.RFID_KEY).toString();
			String goodsId = detail.get(Parameter.GOODS_ID_KEY).toString();
			String goodsNum = detail.get(Parameter.GOODS_NUM_KEY).toString();
			// 验证箱子的rfid
			if (StringUtils.isBlank(rfid)) {
				logger.debug("参数错误--------boxNo:" + CommonUtils.toString(rfid));
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7801", null, locale));
			}
			// 验证箱子明细中的goodsId
			if (StringUtils.isBlank(goodsId)) {
				logger.debug(
						"参数错误--------goodsId:" + CommonUtils.toString(goodsId));
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7802", null, locale));
			}
			// 验证箱子明细中的goodsNum
			if (StringUtils.isBlank(goodsNum)) {
				logger.debug("参数错误--------goodsNum:" + CommonUtils.toString(goodsNum));
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7803", null, locale));
			}
		}
	}

	/**
	 * @author xp
	 * @param boxList
	 *            验证箱子状态及整理业务数据
	 */
	private void checkBoxAndBusinessData(Map<String, Object> map, List<Map<String, Object>> boxList, User user,
			List<String> updateBoxList,
			Map<String, AllAllocateInfo> allocateMap, List<String> notFindBox, List<String> errorBox,
			List<String> notFindGoods, List<String> boxStatusList) {
		for (Map<String, Object> detail : boxList) {
			StoBoxInfo box = new StoBoxInfo();
			box.setRfid(detail.get(Parameter.RFID_KEY).toString());
			StoBoxInfo stoBoxInfo = StoreCommonUtils.getBoxInfoByRfidAndBoxNo(box);
			String goodsName = StoreCommonUtils.getGoodsNameById(detail.get(Parameter.GOODS_ID_KEY).toString());
			// 验证箱子类型，查询流水
			// 验证箱子状态、类型、物品是否存在
			if (stoBoxInfo != null) {
				if ((Constant.BoxStatus.ATM_BOX_STATUS_CLEAR.equals(stoBoxInfo.getBoxStatus()) 
						|| Constant.BoxStatus.ATM_BOX_STATUS_BACK.equals(stoBoxInfo.getBoxStatus()))
						&& boxStatusList.contains(stoBoxInfo.getBoxType())) {
					// 验证物品是否存在
					if (StringUtils.isNotBlank(goodsName)) {
						updateBoxList.add(stoBoxInfo.getId());
						// 查询每个箱子的批次号 修改人：xl 修改时间：2017-12-13 begin
						String batchNo = AllocationCommonUtils.getPlanIdByBoxNo(stoBoxInfo.getId());
						// end
						if (!allocateMap.containsKey(stoBoxInfo.getOfficeId())) {

							List<AllAllocateItem> itemList = Lists.newArrayList();
							// 设置物品表信息
							AllAllocateItem allAllocateItem = new AllAllocateItem();
							// 设置箱子号
							allAllocateItem.setBoxNo(stoBoxInfo.getId());
							// 设置对应的批次号
							allAllocateItem.setBatchNo(batchNo);
							// 设置物品ID
							allAllocateItem.setGoodsId(detail.get(Parameter.GOODS_ID_KEY).toString());
							// 设置物品数量
							long goodsNum = Long.parseLong(detail.get(Parameter.GOODS_NUM_KEY).toString());
							BigDecimal num = new BigDecimal(detail.get(Parameter.GOODS_NUM_KEY).toString());
							// 设置物品价值
							BigDecimal price = StoreCommonUtils
									.getGoodsValue(detail.get(Parameter.GOODS_ID_KEY).toString());
							// 计算每个箱子的总金额
							BigDecimal amount = num.multiply(price);
							allAllocateItem.setMoneyNumber(goodsNum);
							allAllocateItem.setMoneyAmount(amount);
							// 设置确认状态为未确认
							allAllocateItem.setConfirmFlag(AllocationConstant.BetweenConfirmFlag.UNCONFIRMED);
							itemList.add(allAllocateItem);
							// 设置调拨主表信息
							AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
							// 设置业务类型为ATM清机
							allAllocateInfo.setBusinessType(AllocationConstant.BusinessType.Between_ATM_Clear);
							// 设置接受机构
							allAllocateInfo.setaOffice(stoBoxInfo.getOffice());
							// 设置登记机构
							allAllocateInfo.setrOffice(user.getOffice());
							// 设置当前用户
							allAllocateInfo.setCurrentUser(user);
							// 设置物品信息
							allAllocateInfo.setAllAllocateItemList(itemList);
							// 设置登记数量
							int registerNumber = 1;
							allAllocateInfo.setRegisterNumber(registerNumber);
							// 设置登记金额
							allAllocateInfo.setRegisterAmount(amount);
							allocateMap.put(stoBoxInfo.getOfficeId(), allAllocateInfo);
						} else {
							// 设置物品表信息
							AllAllocateItem allAllocateItem = new AllAllocateItem();
							// 设置箱子号
							allAllocateItem.setBoxNo(stoBoxInfo.getId());
							// 设置对应的批次号
							allAllocateItem.setBatchNo(batchNo);
							// 设置物品ID
							allAllocateItem.setGoodsId(detail.get(Parameter.GOODS_ID_KEY).toString());
							// 设置物品数量
							long goodsNum = Long.parseLong(detail.get(Parameter.GOODS_NUM_KEY).toString());
							BigDecimal num = new BigDecimal(goodsNum);
							// 设置物品价值
							BigDecimal price = StoreCommonUtils
									.getGoodsValue(detail.get(Parameter.GOODS_ID_KEY).toString());
							// 计算每个箱子的总金额
							BigDecimal amount = num.multiply(price);
							allAllocateItem.setMoneyNumber(goodsNum);
							allAllocateItem.setMoneyAmount(amount);
							// 设置确认状态为未确认
							allAllocateItem.setConfirmFlag(AllocationConstant.BetweenConfirmFlag.UNCONFIRMED);
							// 将明细设置到调拨主表中
							AllAllocateInfo allocateInfo = allocateMap.get(stoBoxInfo.getOfficeId());
							// 设置登记数量
							allocateInfo.setRegisterNumber(allocateInfo.getRegisterNumber() + 1);
							// 设置登记金额
							allocateInfo.setRegisterAmount(allocateInfo.getRegisterAmount().add(amount));
							List<AllAllocateItem> itemList = allocateInfo.getAllAllocateItemList();
							itemList.add(allAllocateItem);
						}
					} else {
						notFindGoods.add(detail.get(Parameter.GOODS_ID_KEY).toString());
					}
				} else {
					errorBox.add(detail.get(Parameter.RFID_KEY).toString());
				}
			} else {
					notFindBox.add(detail.get(Parameter.RFID_KEY).toString());
			}
		}

		if (!Collections3.isEmpty(errorBox)) {
			logger.debug("参数错误--------errorBox:" + CommonUtils.toString(errorBox));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7807",
					new String[] { Collections3.convertToString(errorBox, Constant.Punctuation.COMMA) }, locale));
		}
		if (!Collections3.isEmpty(notFindBox)) {
			logger.debug("参数错误--------notFindBox:" + CommonUtils.toString(notFindBox));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7808",
					new String[] { Collections3.convertToString(notFindBox, Constant.Punctuation.COMMA) }, locale));
		}
		if (!Collections3.isEmpty(notFindGoods)) {
			logger.debug("参数错误--------notFindGoods:" + CommonUtils.toString(notFindGoods));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, msg.getMessage("message.E7809",
					new String[] { Collections3.convertToString(notFindGoods, Constant.Punctuation.COMMA) }, locale));
		}
	}

	/**
	 * 更新箱子状态及插入调拨信息
	 * 
	 * @author xp
	 * @param updateBoxList
	 * @param allocateMap
	 * @param user
	 * @param map
	 */
	@Transactional(readOnly = false)
	private void updateBoxAndAllocate(List<String> updateBoxList, Map<String, AllAllocateInfo> allocateMap, User user,
			Map<String, Object> map) {
		// 插入调拨信息
		Iterator<String> iterator = allocateMap.keySet().iterator();
		while (iterator.hasNext()) {
			AllocationCommonUtils.saveRegisterInfo(allocateMap.get(iterator.next()));
		}
		// 更新箱子信息
		StoreCommonUtils.updateBoxStatusBatch(updateBoxList, Constant.BoxStatus.EMPTY, user);
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @author xp
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
