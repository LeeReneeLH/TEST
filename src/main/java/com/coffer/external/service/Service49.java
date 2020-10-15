package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.coffer.external.hessian.HardwareConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service49
* <p>Description: RFID面值绑定</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service49")
@Scope("singleton")
public class Service49 extends HardwardBaseService {

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;
	
	/**
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          49：RFID绑定面值
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得电文输入参数
		StoRfidDenomination inputParam = getBindingRfidParam(paramMap);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, inputParam.getErrorFlag());
			return gson.toJson(respMap);
		}

		// 查询是否存在已经绑定过的RFID
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfidList(inputParam.getRfidList());

		// 执行查询处理
		List<StoRfidDenomination> stoRfidDenominationList = stoRfidDenominationService
				.findRFIDList(stoRfidDenomination);
		// 已绑定RFID列表
		List<String> updateRfidList = Lists.newArrayList();
		// 错误RFID列表
		List<String> errorRfidList = Lists.newArrayList();
		// 未绑定RFID列表
		List<String> insertRfidList = inputParam.getRfidList();
		// 如果查询结果不为空的场合
		if (stoRfidDenominationList != null && !stoRfidDenominationList.isEmpty()) {
			// 默认绑定的场合，存在已经绑定RFID的场合，返回错误信息；
			for (StoRfidDenomination rfidDenomination : stoRfidDenominationList) {
				updateRfidList.add(rfidDenomination.getRfid());
				insertRfidList.remove(rfidDenomination.getRfid());
				// RFID号已经被使用的场合，不能重新绑定
				if (StoreConstant.RfidUseFlag.use.equals(rfidDenomination.getUseFlag())) {
					errorRfidList.add(rfidDenomination.getRfid());
				}
			}

			// RFID已经成功入库的场合，不能重新绑定
			if (!errorRfidList.isEmpty()) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put("messageInfo",
						"RFID已经入库，不能重新绑定:" + Collections3.convertToString(errorRfidList, Constant.Punctuation.COMMA));
				return gson.toJson(respMap);
			}

			if (HardwareConstant.ReBindingFlag.binding.equals(inputParam.getReBindingFlag())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put("messageInfo", "RFID已经存在绑定信息，是否重新绑定:"
						+ Collections3.convertToString(updateRfidList, Constant.Punctuation.COMMA));

				return gson.toJson(respMap);
			}
		}

		// 执行插入处理：RFID与面值的绑定关系
		inputParam.setRfidList(insertRfidList);

		// // 设置物品ID
		// String goodsId =
		// stoDictService.getPbocGoodsId(StoreConstant.Currency.RMB,
		// StoreConstant.CashType.PAPER,
		// inputParam.getDenomination());
		// inputParam.setGoodsId(goodsId);

		stoRfidDenominationService.insertRfidDenomination(inputParam);

		// 强制再次绑定
		if (HardwareConstant.ReBindingFlag.yes.equals(inputParam.getReBindingFlag()) && !updateRfidList.isEmpty()) {
			stoRfidDenominationService.updateList(updateRfidList, inputParam);
		}

		respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		return gson.toJson(respMap);
	}

	/**
	 * 
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          取得RFID面值绑定接口的输入参数
	 * @param requestMap
	 *            输入参数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private StoRfidDenomination getBindingRfidParam(Map<String, Object> requestMap) {

		StoRfidDenomination inputParam = new StoRfidDenomination();

		// 取得机构
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("officeId")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：officeId 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setOfficeId(StringUtils.toString(requestMap.get("officeId")));
		}

		// // 取得面值
		// if
		// (StringUtils.isBlank(StringUtils.toString(requestMap.get("denomination"))))
		// {
		// inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
		// logger.warn("输入参数错误：denomination 不存在或是空。");
		// return inputParam;
		// } else {
		// inputParam.setDenomination(StringUtils.toString(requestMap.get("denomination")));
		// }

		// 取得物品ID
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("goodsId")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：goodsId 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setGoodsId(StringUtils.toString(requestMap.get("goodsId")));
		}
		// 取得业务类型
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get(Parameter.BUSINESS_TYPE_KEY)))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：业务类型 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setBusinessType(StringUtils.toString(requestMap.get(Parameter.BUSINESS_TYPE_KEY)));
		}

		// 取得再次绑定标识
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("reBindingFlag")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：reBindingFlag 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setReBindingFlag(StringUtils.toString(requestMap.get("reBindingFlag")));
		}

		// 取得用户ID
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("userId")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：userId 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setUserId(StringUtils.toString(requestMap.get("userId")));
		}

		// 取得用户名称
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("userName")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：userName 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setUserName(StringUtils.toString(requestMap.get("userName")));
		}

		// 取得RFID列表
		if (null == requestMap.get("rfidList")) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：userName 不存在。");
			return inputParam;
		} else {
			inputParam.setRfidList((List<String>) requestMap.get("rfidList"));
		}

		return inputParam;
	}
}
