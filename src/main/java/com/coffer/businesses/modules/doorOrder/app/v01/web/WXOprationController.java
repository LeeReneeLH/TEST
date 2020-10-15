package com.coffer.businesses.modules.doorOrder.app.v01.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coffer.businesses.modules.doorOrder.app.v01.service.MiniProgramService;
import com.coffer.core.common.web.BaseController;

/**
 * @author lihe a小程序wx接口服务端
 * @version 2019-08-20
 */

@RestController
@RequestMapping(value = "${adminPath}/wxOpration")
public class WXOprationController extends BaseController {

	@Autowired
	private MiniProgramService miniProgramService;

	/**
	 * 获取用户token
	 *
	 * @param js_code
	 *            小程序通过wx.login获取的code，发送到服务端获取session_key
	 * @return 用户token
	 * @author yinkai
	 */
	@RequestMapping(value = "/getToken")
	public Map<String, Object> getToken(@RequestParam(value = "js_code") String jsCode,
			@RequestParam(value = "encryptedData") String encryptedData, @RequestParam(value = "iv") String iv) {
		return miniProgramService.getToken(jsCode, encryptedData, iv);
	}

	/**
	 * 平台用户和微信小程序绑定
	 *
	 * @param token
	 *            下发到小程序的带有unionId的密文
	 * @param loginName
	 *            平台用户登录名
	 * @param password
	 *            平台用户的密码
	 * @author yinkai
	 */
	@RequestMapping(value = "/bindUser")
	public Map<String, Object> bindUser(@RequestParam(value = "token") String token,
			@RequestParam(value = "loginName") String loginName, @RequestParam(value = "password") String password) {
		return miniProgramService.bindUser(token, loginName, password);
	}

	/**
	 * 安全退出（解绑）
	 *
	 * @param userId
	 *            用户ID
	 * @return 解绑成功
	 * @author yinkai
	 */
	@RequestMapping(value = "/removeBind")
	public Map<String, Object> removeBind(@RequestParam(value = "userId") String userId) {
		return miniProgramService.removeBind(userId);
	}

	/**
	 * 获取用户信息
	 *
	 * @param token
	 *            凭证
	 * @return userInfo
	 * @author yinkai
	 */
	@RequestMapping(value = "/getUserInfo")
	public Map<String, Object> getUserInfo(@RequestParam(value = "token") String token, String version) {
		return miniProgramService.getUserInfo(token, version);
	}

}
