package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Maps;

@Service
public class UserInfoService {

	@Autowired
	private UserDao userDao;

	@Transactional
	public Map<String, Object> update(String userId, String userName, String oldPassword, String newPassword) {
		Map<String, Object> jsonData = Maps.newHashMap();
		User user = UserUtils.get(userId);
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
			if (SystemService.validatePassword(oldPassword, user.getPassword())) {
				// 修改密码
				String entryptPassword = SystemService.entryptPassword(newPassword);
				user.setPassword(entryptPassword);
				user.setLoginName(userName);
				user.preUpdate();
				user.setUpdateBy(user);
				userDao.update(user);
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				jsonData.put(Parameter.ERROR_MSG_KEY, "原密码错误");
			}
		}
		return jsonData;
	}

}
