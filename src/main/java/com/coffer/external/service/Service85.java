package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.DbConfigConstant;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.DbConfigProperty;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * Title: Service85
 * <p>
 * Description: 数据库属性同步查询接口
 * </p>
 * 
 * @author lihe
 * @date 2018年1月22日 下午4:08:43
 */
@Component("Service85")
@Scope("singleton")
public class Service85 extends HardwardBaseService {

	/**
	 * 数据库属性同步查询接口
	 * 
	 * @author lihe
	 * 
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 1 获取参数
		DbConfigProperty searchCondition = this.getParameter(paramMap);
		// 判断集合是否为空
		if (searchCondition == null) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		} else {

			// 2 数据查询
			List<DbConfigProperty> resultList = SysCommonUtils.findByUpdateDate(searchCondition);
			// 数据填充
			this.fillInReturnData(resultList, map);
			// 添加成功标识
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		}

		// 3数据返回
		return gson.toJson(map);

	}

	/**
	 * 
	 * Title: getParameter
	 * <p>
	 * Description: 获取数据及设置查询条件
	 * </p>
	 * 
	 * @author: lihe
	 * @param paramMap
	 * @return List<dbConfigProperty> 返回类型
	 */
	private DbConfigProperty getParameter(Map<String, Object> paramMap) {

		// 更新时间
		String updateDate = StringUtils.toString(paramMap.get(Parameter.UPDATE_DATE_KEY));
		// 设置查询条件
		DbConfigProperty dbConfigProperty = new DbConfigProperty();
		dbConfigProperty.setStrUpdateDate(updateDate);
		dbConfigProperty.setType(DbConfigConstant.Type.KEY);

		return dbConfigProperty;
	}

	/**
	 * 
	 * Title: fillInReturnData
	 * <p>
	 * Description: 数据填充
	 * </p>
	 * 
	 * @author: lihe
	 * @param resultList
	 *            查询后的结果集
	 * @param rtnMap
	 *            返回数据
	 * @return Map<String,Object> 返回类型
	 */
	private Map<String, Object> fillInReturnData(List<DbConfigProperty> resultList, Map<String, Object> rtnMap) {
		// 遍历查询结果
		Map<String, Object> taskMap = null;
		List<Map<String, Object>> list = Lists.newArrayList();
		for (DbConfigProperty dbConfigProperty : resultList) {
			// 将数据封装成map
			taskMap = Maps.newHashMap();
			// 参数的主键
			taskMap.put(Parameter.PROPERTY_ID_KEY, dbConfigProperty.getId());
			taskMap.put(Parameter.PROPERTY_KEY, dbConfigProperty.getPropertyKey());
			taskMap.put(Parameter.PROPERTY_VALUE_KEY, dbConfigProperty.getPropertyValue());
			taskMap.put(Parameter.REMARKS_KEY, dbConfigProperty.getRemark());
			taskMap.put(Parameter.DEL_FLAG_KEY, dbConfigProperty.getDelFlag());
			taskMap.put(Parameter.UPDATE_DATE_KEY, dbConfigProperty.getStrUpdateDate());
			list.add(taskMap);
		}
		rtnMap.put(Parameter.LIST_KEY, list);
		return rtnMap;
	}
}
