package com.coffer.businesses.modules.common;

import java.util.List;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;

/**
 * 业务工具类
 * 
 * @author Murphy
 * @version 2014-11-07
 */
public class AjaxBusinessUtils extends BusinessUtils {

	/** 系统服务层 */
	private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);

	public static String getUserNameByUserId(String userId, String officeId) {
		List<User> userList = systemService.findUserByOfficeId(officeId);
		String[] userIds = userId.split(",");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < userIds.length; i++) {
			for (User user : userList) {
				if (userIds[i].equals(user.getId())) {
					sb.append(user.getName());
					continue;
				}
			}
			sb.append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月23日
	 * 
	 * 
	 * @param loginName
	 * @param password
	 * @param flag
	 *            true为管理员授权；false为用户
	 * @return
	 */
	public static User validateLoginUserInfo(String loginName, String password, boolean flag) {

		User user = systemService.getUserByLoginName(loginName);
		if (user != null && user.getPassword().equals(password)) {
			if (flag) {// 授权
				// 用户权限验证
				if (StringUtils.isBlank(user.getUserType())
						|| !Global.getConfig("user.csPermission.authorization").contains(user.getUserType())) {
					return null;
				}
			}
			return user;
		} else {
			return null;
		}
	}
}
