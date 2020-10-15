package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service58
* <p>Description: 入库RFID列表查询</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service58")
@Scope("singleton")
public class Service58 extends HardwardBaseService {

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;
	
	@Autowired
    private OfficeService officeService;
	
	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

	 /**
     * 58 入库RFID列表查询
     * Title: findInStoreRfidList
     * <p>Description: 查询代理上缴 或 复点入库 清分中心邦定RFID列表</p>
     * @author:     wangbaozhong
     * @param paramMap
     * @return 
     * String    返回类型
     */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = new HashMap<String, Object>();
		// 版本号、服务代码
    	respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
    	respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.SERIALORDER_NO_KEY)))) {
            logger.warn("58 入库RFID列表查询--------流水单号" + Parameter.SERIALORDER_NO_KEY + " 不存在或是空。");
            // 参数异常
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
        }
		// 流水单号
		String allId = StringUtils.toString(paramMap.get(Parameter.SERIALORDER_NO_KEY));
		
		PbocAllAllocateInfo allocateInfo = pbocAllAllocateInfoService.get(allId);
		
		if (allocateInfo == null) {
			logger.warn("58 入库RFID列表查询--------流水单号:" + allId + ", 对应业务数据不存在！");
            // 参数异常
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E46);
			return gson.toJson(respMap);
		}
		
		if (!AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(allocateInfo.getBusinessType())
				&& !AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(allocateInfo.getBusinessType())
				&& !AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(allocateInfo.getBusinessType())
				&& !AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(allocateInfo.getBusinessType())) {
			logger.warn("58 入库RFID列表查询--------流水单号:" + allId + ", 对应业务类型不正确");
            // 参数异常
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E47);
			return gson.toJson(respMap);
		}
		
		if ((AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(allocateInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(allocateInfo.getBusinessType())
				|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(allocateInfo.getBusinessType()))
				&& !AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS.equals(allocateInfo.getStatus())) {
			logger.warn("58 入库RFID列表查询--------流水单号:" + allId + ", 对应业务状态不是待入库！");
            // 参数异常
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E46);
			return gson.toJson(respMap);
		}
		
		if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(allocateInfo.getBusinessType())
				&& !AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS.equals(allocateInfo.getStatus())) {
			logger.warn("58 入库RFID列表查询--------流水单号:" + allId + ", 对应业务状态不是待入库！");
            // 参数异常
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E46);
			return gson.toJson(respMap);
		}
		
		String handinOfficeId = "";
		if (AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(allocateInfo.getBusinessType())) {
			handinOfficeId = allocateInfo.getAoffice().getId();
		} else {
			handinOfficeId = allocateInfo.getRoffice().getId();
		}
		Office office = officeService.get(handinOfficeId);
		
		List<StoRfidDenomination> rfidDenList = stoRfidDenominationService.findUnUsedListByOfficeId(allId);
		List<Map<String, Object>> rfidList = Lists.newArrayList();
		
		BigDecimal rfidAllAmount = new BigDecimal(0d);
		
		for (StoRfidDenomination rfidDetail : rfidDenList) {
			Map<String, Object> rfidInfoMap = Maps.newHashMap();
			// rfid
			rfidInfoMap.put(Parameter.RFID_KEY, rfidDetail.getRfid());
			//物品ID
			rfidInfoMap.put(Parameter.GOODS_ID_KEY, rfidDetail.getGoodsId());
			//物品名称
			String goodsName = StoreCommonUtils.getGoodsName(rfidDetail.getGoodsId());
			rfidInfoMap.put(Parameter.GOODS_NAME_KEY, StringUtils.isBlank(goodsName) ? "" : goodsName);
            // 物品价值
			BigDecimal amount = StoreCommonUtils.getGoodsValue(rfidDetail.getGoodsId());
			rfidAllAmount = rfidAllAmount.add(amount);
			rfidInfoMap.put(Parameter.AMOUNT_KEY, amount == null ? "0.0" : amount.doubleValue());
			//业务类型
			rfidInfoMap.put(Parameter.BUSINESS_TYPE_KEY, rfidDetail.getBusinessType());
			//所属机构编码
			rfidInfoMap.put("officeId", office.getId());
			//所属机构名称
			rfidInfoMap.put("officeName", office.getName());

			rfidList.add(rfidInfoMap);
			
		}
		respMap.put(Parameter.RFID_LIST_KEY, rfidList);
		respMap.put(Parameter.APPROVAL_AMOUNT_KEY, AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(allocateInfo.getBusinessType()) ? allocateInfo.getRegisterAmount() :  allocateInfo.getConfirmAmount());
		respMap.put(Parameter.RFID_ALL_AMOUNT_KEY, rfidAllAmount.doubleValue());
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
