package com.coffer.external.hessian;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.core.common.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.ServiceException;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.service.DecoratorService;
import com.coffer.external.service.Service0805;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HardwareServiceNew implements IHardwareService {
	
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(Global.class);

	/**
	 * Json实例对象
	 */
	protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation() // 不导出实体中没有用@Expose注解的属性
			.enableComplexMapKeySerialization() // 支持Map的key为复杂对象的形式
			.serializeNulls().setDateFormat(Constant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS)// 时间转化为特定格式
			// .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)//
			// 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
			.setPrettyPrinting() // 对json结果格式化.
			// .setVersion(1.0)
			// //有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
			// @Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
			// @Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
			.create();

	@Override
	public String service(String req) {
		@SuppressWarnings("unchecked")
		Map<String, Object> requestMap = gson.fromJson(req, Map.class);

		String serviceNo = requestMap.get(Parameter.SERVICE_NO_KEY).toString();
		long beginTime = System.currentTimeMillis();
		logger.debug("接收JSON:" + req);

		String responseJson = "";
		
		try {
			// 初始化接口
			HardwardServiceInterface service = SpringContextHolder.getBean(HardwareConstant.SERVICE_BEAN_NAME_PREFIX + serviceNo);
			// 初始化本地类
			DecoratorService dService = new DecoratorService(service);
			// 如果是存款请求，标记来源为设备
			if(requestMap.get("serviceNo")!= null && "0805".equals(requestMap.get("serviceNo"))){
				requestMap.put(Parameter.ORDER_FROM,Service0805.FROM_EQP);
			}
			// 执行接口操作
			responseJson = dService.execute(requestMap);
			
		} catch (NoSuchBeanDefinitionException ex) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 结果标识
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 错误代码
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E06);
			// 版本号
			map.put(Parameter.VERSION_NO_KEY, requestMap.get(Parameter.VERSION_NO_KEY));
			// 服务代码
			map.put(Parameter.SERVICE_NO_KEY, serviceNo);
			// JSON电文
			responseJson = gson.toJson(map);
		} catch (BusinessException be) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 版本号
			map.put(Parameter.VERSION_NO_KEY, requestMap.get(Parameter.VERSION_NO_KEY));
			// 服务代码
			map.put(Parameter.SERVICE_NO_KEY, serviceNo);
			//若为0805接口，插入存款异常数据
			if(serviceNo.equals("0805")) {
				DoorCommonUtils.saveException(requestMap,be.getMessageContent());
			}
			// 结果标识
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 错误代码
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			// 错误信息
			map.put(Parameter.MESSAGE_INFO, be.getMessageContent() + Constant.Punctuation.HALF_COLON);
			// 错误信息
			map.put(Parameter.ERROR_MSG_KEY, be.getMessageContent());
			// LOG
			logger.error(ExceptionUtil.getExceptionTrace(be));

			responseJson = gson.toJson(map);

		} catch (ServiceException se) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 版本号
			map.put(Parameter.VERSION_NO_KEY, requestMap.get(Parameter.VERSION_NO_KEY));
			// 服务代码
			map.put(Parameter.SERVICE_NO_KEY, serviceNo);
			// 结果标识
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 错误代码
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
			// 错误信息
			map.put(Parameter.ERROR_MSG_KEY, se.getMessage());
			// LOG
			logger.error(ExceptionUtil.getExceptionTrace(se));
			
			// JSON电文
			responseJson = gson.toJson(map);
		}
		
		long endTime = System.currentTimeMillis();
		logger.debug("返回JSON:" + responseJson);
		logger.debug("接口耗时：{}ms  开始计时: {}  计时结束：{}", (endTime - beginTime),
				new SimpleDateFormat("HH:mm:ss.SSS").format(beginTime),
				new SimpleDateFormat("HH:mm:ss.SSS").format(endTime));
		return responseJson;
	}

}