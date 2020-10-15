package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service06 
* <p>Description: 人员身份信息采集接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:37:32
*/
@Component("Service06")
@Scope("singleton")
public class Service06 extends HardwardBaseService {
	
	@Autowired
	private SystemService systemService;
	
	/**
	 * 
	 * @author LF
	 * @version 2014-12-15
	 * 
	 * @Description 用户授权接口
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
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> userList = (List<Map<String, Object>>) paramMap.get("authorizeList");
			//登陆用户所属机构
			String loginUserOfficeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
			
			List<Map<String,Object>> list = Lists.newArrayList();
			
			if (!Collections3.isEmpty(userList)) {
				for (Map<String, Object> userMap : userList) {
					Map<String,Object> resultUserMap = Maps.newHashMap();
					// 循环授权人，验证身份
					if (userMap.get("loginName") != null && userMap.get("password") != null) {
						User user = systemService.getUserByLoginName(userMap.get("loginName").toString());
						// 验证用户名和密码
						if (user != null && userMap.get("password").toString().equals(user.getPassword())) {
							// 用户权限验证
							if (StringUtils.isNotBlank(user.getUserType())
									&& Global.getConfig("user.csPermission.authorization").contains(user.getUserType())
									&& loginUserOfficeId.equals(user.getOffice().getId())) {
								map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
							} else {
								// 用户权限不足
								map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
								map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E10);
								break;
							}
						} else {
							// 用户名密码错误或用户不存在
							map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
							map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E09);
							break;
						}
						
						resultUserMap.put(Parameter.LOGIN_NAME_KEY,user.getLoginName());
						resultUserMap.put(Parameter.OPT_USER_NAME_KEY,user.getName());
						list.add(resultUserMap);
						
					} else {
						// 参数异常
						map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
						map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
						break;
					}
				}
				
				map.put(Parameter.LIST_KEY, list);
			} else {
				// 参数异常
				map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E03);
			}
		} catch (Exception e) {
			// 处理异常
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}

}
