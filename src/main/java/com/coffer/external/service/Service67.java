package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant.OfficeType;
import com.coffer.businesses.modules.store.StoreConstant.StamperType;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service67
* <p>Description: 查询所有印章类型接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service67")
@Scope("singleton")
public class Service67 extends HardwardBaseService {

	@Autowired
	private OfficeService officeService;
	
	/**
	 * 67：根据机构获取所有印章类型
	 * @author Zhengkaiyuan
	 * @version 2016年9月19日
	 *
	 *
	 * @param paramMap 
	 * @return 返回类型 String
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.warn("67：印章操作接口--------获取机构信息开始");
		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, serviceNo);
		
		Office office = this.getOfficeForAllStamperType(paramMap, serviceNo);
		
		if (office == null){
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}else {
			String stamperTypeString = null;
			if (office.getType().equals(OfficeType.CENTRAL_BANK)){
				stamperTypeString = StamperType.PBOC_OFFICE_STAMPER_TYPE;
			}else {
				stamperTypeString = StamperType.OFFICE_STAMPER_TYPE;
			}
			List<Map<String, Object>> stamperTypeMapList = Lists.newArrayList();
			// 查询所有印章类型
			List<Dict> stamperTypeList = DictUtils.getDictList(stamperTypeString);
			// 封装返回值
			for (Dict dictTemp : stamperTypeList) {
				Map<String, Object> mapTemp = Maps.newHashMap();
				mapTemp.put(Parameter.DICT_VALUE_KEY, dictTemp.getValue());
				mapTemp.put(Parameter.DICT_LABEL_KEY, dictTemp.getLabel());
				stamperTypeMapList.add(mapTemp);
			}
			respMap.put(Parameter.LIST_KEY, stamperTypeMapList);
		}
		logger.warn("67：印章操作接口--------获取机构信息结束");
		return gson.toJson(respMap);
	}
	/**
	 * 67：根据机构获取所有印章类型获取参数
	 * @author Zhengkaiyuan
	 * @version 2016年9月20日
	 *
	 *
	 * @param requestMap
	 * @param serviceNo
	 * @return
	 */
	private Office getOfficeForAllStamperType(Map<String, Object> requestMap, String serviceNo){
		// 验证机构ID
		Object officeId = requestMap.get(Parameter.OFFICE_ID_KEY);
		// 验证机构ID
		if (officeId == null || StringUtils.isBlank(officeId.toString())) {
			return null;
		}
		return officeService.get(officeId.toString());
	}
}
