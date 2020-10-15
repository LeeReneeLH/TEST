package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.entity.StoRfidEntity;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service63
 * <p>
 * Description: PDA RFID查询接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service63")
@Scope("singleton")
public class Service63 extends HardwardBaseService {

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	/**
	 * @author zhengkaiyuan 2016年8月30日 63：根据流水单号查询RFID
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly=true)
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得电文输入参数
		StoRfidEntity inputParam = getPdaRfidBySNOParam(paramMap);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, inputParam.getErrorFlag());
			return gson.toJson(respMap);
		}

		// 验证流水单号对应业务类型是否正确
		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(inputParam.getAllId());
		if (pbocAllAllocateInfo == null) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E14);
			return gson.toJson(respMap);
		} else {
			String bussnessType = pbocAllAllocateInfo.getBusinessType();
			if (!(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(bussnessType)
					|| AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(bussnessType)
					|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(bussnessType)
					|| AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(bussnessType))) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E47);
				return gson.toJson(respMap);
			}
		}
		// 验证流水单号业务状态 是否为待入库
		if (!(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS
				.equals(pbocAllAllocateInfo.getStatus())
				|| AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS
						.equals(pbocAllAllocateInfo.getStatus()))) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
			return gson.toJson(respMap);
		}
		// 查询是否存在已经绑定过的RFID
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setAllId(inputParam.getAllId());

		// 执行查询处理
		List<StoRfidDenomination> stoRfidDenominationList = stoRfidDenominationService.pdaFindList(stoRfidDenomination);
		List<Map<String, Object>> rfidList = Lists.newArrayList();
		for (StoRfidDenomination stoRfidDenominationTemp : stoRfidDenominationList) {
			Map<String, Object> detailMap = Maps.newHashMap();
			detailMap.put(Parameter.RFID_KEY, stoRfidDenominationTemp.getRfid());
			detailMap.put(Parameter.GOODS_NAME_KEY, stoRfidDenominationTemp.getGoodsName());
			detailMap.put(Parameter.GOODS_ID_KEY, stoRfidDenominationTemp.getGoodsId());
			detailMap.put(Parameter.VALUE_KEY, stoRfidDenominationTemp.getValue());
			rfidList.add(detailMap);
		}
		respMap.put(Parameter.RFID_LIST_KEY, rfidList);
		respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

	/**
	 * @author zhengkaiyuan
	 * @version 2016年8月30日 取得流水单号邦定RFID接口的输入参数
	 * 
	 * @param requestMap
	 * @return
	 */
	private StoRfidEntity getPdaRfidBySNOParam(Map<String, Object> requestMap) {
		StoRfidEntity inputParam = new StoRfidEntity();

		// 取得流水单号
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("serialorderNo")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：serialorderNo 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setAllId(StringUtils.toString(requestMap.get("serialorderNo")));
		}
		return inputParam;
	}

}
