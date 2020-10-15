package com.coffer.external.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service01 
* <p>Description: 用户登录接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:34:20
*/
@Component("Service01")
@Scope("singleton")
public class Service01 extends HardwardBaseService {

	@Autowired
	private SystemService systemService;
	
	/**
	 * 
	 * @author LF 用户登录服务接口
	 * @version 2014-11-24
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
		try {
			if (paramMap.get("loginName") != null && paramMap.get("password") != null) {
				User user = systemService.getUserByLoginName(paramMap.get("loginName").toString());
				// 验证用户名和密码
				if (user != null
						&& paramMap.get("password").toString().equals(user.getPassword())) {
					map.put("id", user.getId());
					map.put("loginName", user.getLoginName());
					map.put("name", user.getName());
					//追加用户类型 WQJ 2018-12-24
					map.put("userType", user.getUserType());
					if (user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getId())) {
						map.put("officeId", user.getOffice().getId());
					}
					//机构名称
					if (user.getOffice() != null && StringUtils.isNotBlank(user.getOffice().getName())) {
						map.put("officeName", user.getOffice().getName());
					}
					map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
					if (StringUtils.isNotBlank(user.getCsPermission())) {
						map.put("roleList", setRoleList(user.getCsPermission()));
					}
				} else {
					// 用户名密码错误或用户不存在
					map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put("errorNo", "用户名密码错误或用户不存在");
				}
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
	
	/**
	 * @author LF 获取角色列表
	 * @param roleString
	 * @return
	 */
	public static List<Map<String, Object>> setRoleList(String roleString) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 判断角色列表是否为空
		if (StringUtils.isNotEmpty(roleString)) {
			String[] role = roleString.split(",");
			for (int i = 0; i < role.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (StringUtils.isNotEmpty(role[i])) {
					map.put("role", role[i]);
					list.add(map);
				}
			}
		}

		return list;
	}
}
