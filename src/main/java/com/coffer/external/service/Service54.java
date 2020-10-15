package com.coffer.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service54
* <p>Description: 原封箱袋查询接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service54")
@Scope("singleton")
public class Service54 extends HardwardBaseService {

	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	
	/**
	 * 
	 * @author LF 箱袋查询接口
	 * @version 2016-06-01
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		Object boxNo = paramMap.get(Parameter.BOX_NO_KEY);
		if (boxNo != null) {
			StoOriginalBanknote stoOriginalBanknote = stoOriginalBanknoteService.getOriginalBanknoteById(boxNo.toString(), paramMap.get(Parameter.OFFICE_ID_KEY).toString());
			if (stoOriginalBanknote == null || StringUtils.isNotBlank(stoOriginalBanknote.getOutId())) {
				// 此箱袋编号已经出库或者不再库区当中
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY, boxNo.toString() + "此箱袋编号已经出库或者不在库区当中");
			} else {
				map.put(Parameter.ROFFICE_NAME_KEY, stoOriginalBanknote.getRoffice().getName());
				map.put(Parameter.CREATE_NAME_KEY, stoOriginalBanknote.getCreateName());
				map.put(Parameter.CREATE_DATE_KEY, DateUtils.formatDate(stoOriginalBanknote.getCreateDate()));
				// 成功结果
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}
		} else {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}

		return gson.toJson(map);
	}

}
