package com.coffer.external.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service75
 * <p>
 * Description: 机构信息同步接口(对外)
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月13日 上午14:01:10
 */
@Component("Service75")
@Scope("singleton")
public class Service75 extends HardwardBaseService {

	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug(this.getClass().getName() + "机构信息同步接口(对外) -----------------开始");
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		// TODO 握手加密验证信息
		
		try {

			if (paramMap.get(Parameter.OFFICE_ID_KEY) == null
					|| StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return gson.toJson(respMap);
			}
			// 获取增量日期查询条件
			String lastSearchDate = StringUtils.toString(paramMap.get(Parameter.SEARCH_DATE_KEY));
			// 指定机构
			String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));

			// 查询参数机构及其下属机构
			Office officeInterface = SysCommonUtils.findOfficeById(officeId.toString());
			String strParentIds = officeInterface.getParentIds() + officeInterface.getId();
			List<Office> searchOfficeList = SysCommonUtils.findOfficeListForInterface(officeId, strParentIds, lastSearchDate);

			// 格式化输出
			List<Map<String, Object>> rtnMapList = Lists.newArrayList();
			Map<String, Object> innerMap = null;
			for (Office tempOffice : searchOfficeList) {
				innerMap = Maps.newHashMap();
				innerMap.put(Parameter.OFFICE_ID_KEY, tempOffice.getId());
				innerMap.put(Parameter.OFFICE_NAME_KEY, tempOffice.getName());
				innerMap.put(Parameter.OFFICE_TYPE_KEY, tempOffice.getType());
				innerMap.put(Parameter.OFFICE_PARENT_ID_KEY, tempOffice.getParentId());
				innerMap.put(Parameter.OFFICE_PARENT_IDS_KEY, tempOffice.getParentIds());
				rtnMapList.add(innerMap);
			}
			respMap.put(Parameter.LIST_KEY, rtnMapList);
			// 指定最后查询日期
			if (searchOfficeList.size() > 0) {
				respMap.put(Parameter.SEARCH_DATE_KEY,
						DateUtils.formatDate(searchOfficeList.get(searchOfficeList.size() - 1).getUpdateDate(),
								Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS));
			} else {
				respMap.put(Parameter.SEARCH_DATE_KEY,
						DateUtils.formatDate(new Date(), Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS));
			}
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		logger.debug(this.getClass().getName() + "机构信息同步接口(对外) -----------------结束");
		return gson.toJson(respMap);
	}

}
