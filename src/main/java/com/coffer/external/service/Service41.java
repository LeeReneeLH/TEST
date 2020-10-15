package com.coffer.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service41
* <p>Description: 修改登录密码</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service41")
@Scope("singleton")
public class Service41 extends HardwardBaseService {

	@Autowired
	private SystemService systemService;
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2016年4月26日
	 * 
	 *  修改登录密码
	 * @param paramMap 参数信息
	 * @return 更新结果信息
	 */
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		// 用户名判断
		if (paramMap.get(Parameter.USER_ID_KEY) == null || StringUtils.isBlank(paramMap.get(Parameter.USER_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 用户名不能为空
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E38);
			return gson.toJson(map);
		}
		// 取得用户信息
		User user = UserUtils.getByLoginName(paramMap.get(Parameter.USER_ID_KEY).toString());
		// 用户存在判断
		if (user == null) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 用户不存在！ 
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E39);
			return gson.toJson(map);
		}
		
		
		String oldPassword = paramMap.get(Parameter.OLD_PASSWORD_KEY) == null ? "" : paramMap.get(Parameter.OLD_PASSWORD_KEY).toString();
		String newPassword = paramMap.get(Parameter.NEW_PASSWORD_KEY) == null ? "" : paramMap.get(Parameter.NEW_PASSWORD_KEY).toString();
		
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
			
			if (oldPassword.equals(user.getPassword())){
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}else{
				// 修改密码失败，旧密码错误 
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E37);
			}
		} else {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 密码不能为空！ 
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E40);
		}

		return gson.toJson(map);
	}

}
