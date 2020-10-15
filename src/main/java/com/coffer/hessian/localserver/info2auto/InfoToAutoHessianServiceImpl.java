package com.coffer.hessian.localserver.info2auto;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.ExceptionUtil;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.hessian.localserver.common.BaseHessianConstant;
import com.coffer.hessian.localserver.common.BaseHessianParameter;
import com.coffer.hessian.localserver.common.BaseHessianService;
import com.coffer.hessian.localserver.common.DecoratorService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 自动化用接口实现类（Hessian服务）
 * 
 * @author yuxixuan
 *
 */
public class InfoToAutoHessianServiceImpl implements InfoToAutoHessianService {
	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(Global.class);
	/**
	 * Json实例对象
	 */
	protected static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
			.enableComplexMapKeySerialization().serializeNulls()
			.setDateFormat(Constant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS).setPrettyPrinting().create();

	/**
	 * 接口方法
	 */
	@Override
	public String service(String req) {
		@SuppressWarnings("unchecked")
		Map<String, Object> requestMap = gson.fromJson(req, Map.class);

		String serviceNo = requestMap.get(BaseHessianParameter.SERVICE_NO_KEY).toString();
		long beginTime = System.currentTimeMillis();
		logger.debug("接收JSON:" + req);

		String responseJson = "";

		try {
			// 初始化接口
			BaseHessianService service = SpringContextHolder
					.getBean(InfoToAutoHessianConstant.SERVICE_BEAN_NAME_PREFIX + serviceNo);
			// 初始化本地类
			DecoratorService dService = new DecoratorService(service);
			// 执行接口操作
			responseJson = dService.execute(requestMap);

		} catch (NoSuchBeanDefinitionException ex) {
			ex.printStackTrace();
			Map<String, Object> map = new HashMap<String, Object>();
			// 版本号
			map.put(BaseHessianParameter.VERSION_NO_KEY, requestMap.get(BaseHessianParameter.VERSION_NO_KEY));
			// 服务代码
			map.put(BaseHessianParameter.SERVICE_NO_KEY, serviceNo);
			// 结果标识
			map.put(BaseHessianParameter.RESULT_FLAG_KEY, BaseHessianConstant.RESULT_FLAG_FAILURE);
			// 错误代码
			map.put(BaseHessianParameter.ERROR_NO_KEY, BaseHessianConstant.ERROR_NO_E06);
			// JSON电文
			responseJson = gson.toJson(map);
		} catch (BusinessException be) {
			be.printStackTrace();
			Map<String, Object> map = new HashMap<String, Object>();
			// 版本号
			map.put(BaseHessianParameter.VERSION_NO_KEY, requestMap.get(BaseHessianParameter.VERSION_NO_KEY));
			// 服务代码
			map.put(BaseHessianParameter.SERVICE_NO_KEY, serviceNo);
			// 结果标识
			map.put(BaseHessianParameter.RESULT_FLAG_KEY, BaseHessianConstant.RESULT_FLAG_FAILURE);
			// 错误代码
			map.put(BaseHessianParameter.ERROR_NO_KEY, BaseHessianConstant.ERROR_NO_E99);
			// 错误信息
			map.put(BaseHessianParameter.ERROR_MSG_KEY, be.getMessageContent());
			// LOG
			logger.error(ExceptionUtil.getExceptionTrace(be));

			responseJson = gson.toJson(map);

			// } catch (ServiceException se) {
			// Map<String, Object> map = new HashMap<String, Object>();
			// // 版本号
			// map.put(BaseHessianParameter.VERSION_NO_KEY,
			// requestMap.get(BaseHessianParameter.VERSION_NO_KEY));
			// // 服务代码
			// map.put(BaseHessianParameter.SERVICE_NO_KEY, serviceNo);
			// // 结果标识
			// map.put(BaseHessianParameter.RESULT_FLAG_KEY,
			// BaseHessianConstant.CommonConstant.RESULT_FLAG_FAILURE);
			// // 错误代码
			// map.put(BaseHessianParameter.ERROR_NO_KEY,
			// BaseHessianConstant.CommonConstant.ERROR_NO_E02);
			// // 错误信息
			// map.put(BaseHessianParameter.ERROR_MSG_KEY, se.getMessage());
			// // LOG
			// logger.error(ExceptionUtil.getExceptionTrace(se));
			//
			// // JSON电文
			// responseJson = gson.toJson(map);
		}

		long endTime = System.currentTimeMillis();
		logger.debug("返回JSON:" + responseJson);
		logger.debug("接口耗时：{}ms  开始计时: {}  计时结束：{}", (endTime - beginTime),
				new SimpleDateFormat("HH:mm:ss.SSS").format(beginTime),
				new SimpleDateFormat("HH:mm:ss.SSS").format(endTime));
		return responseJson;
	}

}
