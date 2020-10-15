package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service07 
* <p>Description: 查询所有机构信息</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:37:50
*/
@Component("Service07")
@Scope("singleton")
public class Service07 extends HardwardBaseService {
	
	@Autowired
	private OfficeService officeService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年06月09日
	 * 
	 *          查询所有机构信息
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			Office officeInterface = new Office();
			Object officeId = paramMap.get(Parameter.OFFICE_ID_KEY);
			if(officeId != null) {
				officeInterface = officeService.get(officeId.toString());
				if(officeInterface==null) {
					officeInterface = new Office();
				} else {
					// 如果机构类型为清分中心，则查找所属人行机构信息
					if (Constant.OfficeType.CLEAR_CENTER.equals(officeInterface.getType())) {
						officeInterface = StoreCommonUtils.getPbocCenterByOffice(officeInterface);
					}
					officeInterface.setParentIds(officeInterface.getParentIds()+officeInterface.getId());
				}
			}
			List<Office> officeList = officeService.findList(officeInterface);
			if (!Collections3.isEmpty(officeList)) {
				List<Map<String, Object>> list = Lists.newArrayList();
				for (int i = officeList.size() - 1; i >= 0; i--) {
					Office office = officeList.get(i);
					if (Constant.OfficeType.ROOT.equals(office.getType())) {
						officeList.remove(i);
						continue;
					}
					Map<String, Object> officeMap = Maps.newHashMap();
					officeMap.put("officeId", office.getId());
					officeMap.put("officeName", office.getName());
					officeMap.put("type", office.getType());
					officeMap.put("tradeFlag", office.getTradeFlag());
					list.add(officeMap);
				}
				map.put("officeList", list);
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E11);
			}
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		String json = gson.toJson(map);

		// json
		return json;
	}

}
