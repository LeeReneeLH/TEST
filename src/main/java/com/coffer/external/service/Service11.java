package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.core.common.config.Global;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service11 
* <p>Description: 取得箱袋类型信息接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:39:36
*/
@Component("Service11")
@Scope("singleton")
public class Service11 extends HardwardBaseService {

	/**
	 * 
	 * @author LLF 获取箱袋类型接口
	 * @version 2015-06-04
	 * @param paramMap
	 * 
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			// 获取箱袋类型
			String boxTypeShow = Global.getConfig("sto.box.boxtype.show");
			List<Dict> list = DictUtils.getDictList("sto_box_type", true, boxTypeShow);
			map.put("list", list);
			// 返回成功结果代码
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		return gson.toJson(map);
	}

}
