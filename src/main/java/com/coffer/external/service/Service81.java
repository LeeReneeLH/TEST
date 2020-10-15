package com.coffer.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Maps;

/**
 * 
 * Title: GpsService
 * <p>
 * Description: 车辆实时位置发送接口
 * </p>
 * 
 * @author yanbingxu
 * @date 2017年11月30日 下午4:09:28
 */
@Component("Service81")
@Scope("singleton")
public class Service81 extends HardwardBaseService {

	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, String> location = Maps.newHashMap();
		String excuteDate = DateUtils.getDate("yyyy-MM-dd HH:mm:ss:SSS");
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 车牌号验证
		if (null == paramMap.get(Parameter.CAR_NO_KEY)) {
			logger.error("车牌号为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}
		// 经度验证
		if (null == paramMap.get(Parameter.LONGITUDE_KEY)) {
			logger.error("经度为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}
		// 纬度验证
		if (null == paramMap.get(Parameter.LATITUDE_KEY)) {
			logger.error("纬度为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}
		// 线路规划ID验证
		if (null == paramMap.get(Parameter.ROUTE_PLAN_ID_KEY)) {
			logger.error("线路规划ID为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}
		// 任务标识验证
		if (null == paramMap.get(Parameter.TASK_FLAG_KEY)) {
			logger.error("任务标识为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}
		if (paramMap.get(Parameter.CAR_NO_KEY) != null && paramMap.get(Parameter.LONGITUDE_KEY) != null
				&& paramMap.get(Parameter.LATITUDE_KEY) != null && paramMap.get(Parameter.ROUTE_PLAN_ID_KEY) != null
				&& paramMap.get(Parameter.TASK_FLAG_KEY) != null) {
			// 数据填充
			location.put(Parameter.CAR_NO_KEY, paramMap.get(Parameter.CAR_NO_KEY).toString());
			location.put(Parameter.LONGITUDE_KEY, paramMap.get(Parameter.LONGITUDE_KEY).toString());
			location.put(Parameter.LATITUDE_KEY, paramMap.get(Parameter.LATITUDE_KEY).toString());
			location.put(Parameter.TASK_FLAG_KEY, paramMap.get(Parameter.TASK_FLAG_KEY).toString());
			location.put(Parameter.ROUTE_PLAN_ID_KEY, paramMap.get(Parameter.ROUTE_PLAN_ID_KEY).toString());
			location.put(Parameter.UPLOAD_DATE_KEY, excuteDate);
			// 速度为空时设定默认值
			if (paramMap.get(Parameter.SPEED_KEY) != null) {
				location.put(Parameter.SPEED_KEY, paramMap.get(Parameter.SPEED_KEY).toString());
			} else {
				location.put(Parameter.SPEED_KEY, "100000");
			}
			// gps位置信息添加至对列
			SysCommonUtils.gpsLocationQueueAdd(location);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		}
		return gson.toJson(map);
	}
}
