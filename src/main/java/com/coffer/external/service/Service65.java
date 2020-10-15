package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.coffer.external.hessian.HardwareConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service65
 * <p>
 * Description: PDA包号查询接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service65")
@Scope("singleton")
public class Service65 extends HardwardBaseService {

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

	/**
	 * @author cai xiaojie
	 * @version 2016年9月9日 65：PDA包号查询接口
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		String boxNo = StringUtils.toString(paramMap.get("boxNo"));
		// 验证包号
		if (StringUtils.isBlank(boxNo) || HardwareConstant.CardLength.box != boxNo.length()) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
		}

		// 查询箱号信息
		List<StoRfidDenomination> stoRfidDenominationList = stoRfidDenominationService.findListByBoxNo(boxNo);
		List<Map<String, Object>> rfidList = Lists.newArrayList();
		for (StoRfidDenomination stoRfidDenominationTemp : stoRfidDenominationList) {
			Map<String, Object> detailMap = Maps.newHashMap();
			detailMap.put(Parameter.RFID_KEY, stoRfidDenominationTemp.getRfid());
			detailMap.put(Parameter.GOODS_NAME_KEY, stoRfidDenominationTemp.getGoodsName());
			detailMap.put(Parameter.GOODS_ID_KEY, stoRfidDenominationTemp.getGoodsId());
			rfidList.add(detailMap);
		}
		respMap.put(Parameter.RFID_LIST_KEY, rfidList);
		respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		return gson.toJson(respMap);
	}

}
