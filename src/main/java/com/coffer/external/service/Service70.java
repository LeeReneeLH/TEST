package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.coffer.external.hessian.HardwareConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service70
* <p>Description: 单个8位RFID面值查询接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service70")
@Scope("singleton")
public class Service70 extends HardwardBaseService {

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;
	
	/**
	 * 
	 * Title: findRfidDenominationBy8Rfid
	 * <p>Description: 单个8位RFID面值查询接口</p>
	 * @author:     wangbaozhong
	 * @param paramMap
	 * @return 
	 * String    返回类型
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
    	respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
    	respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		StoRfidDenomination inputParam = new StoRfidDenomination();

		// 取得机构
		if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)))) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：officeId 不存在或是空。");
			return gson.toJson(respMap);
		} else {
			inputParam.setOfficeId(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)));
		}

		// 取得出入库种别
		if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY)))) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：inoutType 不存在或是空。");
			return gson.toJson(respMap);
		} else {
			inputParam.setInoutType(StringUtils.toString(paramMap.get(Parameter.INOUT_TYPE_KEY)));
		}
		
		String rfid = StringUtils.toString(paramMap.get(Parameter.RFID_KEY));
		//验证包号
		if(StringUtils.isBlank(rfid)|| HardwareConstant.CardLength.box != rfid.length())
		{
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：rfid 不存在或是空。");
			return gson.toJson(respMap);
		}
		
		List<String> rfidList = Lists.newArrayList();
		rfidList.add(rfid);
		inputParam.setRfidList(rfidList);
		
		//查询箱号信息
		StoRfidDenomination stoRfidDenomination = stoRfidDenominationService.findRfidInfoByBoxNo(rfid);

		List<Map<String, Object>> rfidReturnList = null;
		if (stoRfidDenomination == null || !Constant.deleteFlag.Valid.equals(stoRfidDenomination.getDelFlag())) {
			rfidReturnList = Lists.newArrayList();
			Map<String, Object> rfidDenominationMap = Maps.newHashMap();
			rfidDenominationMap.put(Parameter.RFID_KEY, rfid);
			
			rfidDenominationMap.put(Parameter.AMOUNT_KEY, "");
			// 物品名称
			rfidDenominationMap.put(Parameter.GOODS_NAME_KEY, "");
			rfidDenominationMap.put(Parameter.OFFICE_ID_KEY, "");
			rfidDenominationMap.put(Parameter.OFFICE_NAME_KEY, "");
//			rfidDenominationMap.put("storeId", "");
//			rfidDenominationMap.put("storeName", "");
			rfidDenominationMap.put(Parameter.STORE_AREA_NAME_KEY, "");
			// 设定库区类型
			rfidDenominationMap.put(Parameter.STORE_AREA_TYPE_KEY, "");
			// 设定所属业务类型
			rfidDenominationMap.put(Parameter.BUSINESS_TYPE_KEY, "");
			rfidDenominationMap.put(Parameter.FLAG_KEY, ExternalConstant.RfidErrorFlag.EXIST_ERROR);
			rfidReturnList.add(rfidDenominationMap);
		} else {
			if (!StringUtils.isBlank(stoRfidDenomination.getOfficeId())) {
				Office office = SysCommonUtils.findOfficeById(stoRfidDenomination.getOfficeId());
				stoRfidDenomination.setOffice(office);
			}
			List<StoRfidDenomination> stoRfidDenominationList = Lists.newArrayList();
			stoRfidDenominationList.add(stoRfidDenomination);
			// 编辑RFID返回列表信息
			rfidReturnList = stoRfidDenominationService
					.setRfidAndDenominationResult(stoRfidDenominationList, inputParam);
		}

		respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		respMap.put("rfidList", rfidReturnList);

		return gson.toJson(respMap);
	}

}
