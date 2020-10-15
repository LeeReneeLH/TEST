package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.app.v01.utils.TokenUtils;
import com.coffer.businesses.modules.doorOrder.app.v01.utils.WeChatUtils;
import com.coffer.businesses.modules.weChat.v03.dao.GuestDao;
import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;

import io.jsonwebtoken.Claims;

/**
 * 登录、绑定service
 *
 * @author yinkai
 * @version 2019年8月27日
 */
@Service
public class MiniProgramService extends BaseService {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private GuestDao guestDao;

	@Autowired
	private UserDao userDao;

	/**
	 * 微信端登录获取用户唯一token
	 *
	 * @param js_code
	 *            小程序端wx.login获得的code
	 * @param encryptedData
	 *            微信用户加密数据
	 * @param iv
	 *            解密偏移量
	 * @return token，平台用户信息（可空）
	 * @author yinkai
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> getToken(@RequestParam("js_code") String jsCode,
			@RequestParam("encryptedData") String encryptedData, @RequestParam("iv") String iv) {
		Map<String, Object> rtnMap = new HashMap<>();
		String code2session = "https://api.weixin.qq.com/sns/jscode2session?" + "appid="
				+ Global.getConfig("miniProgram.appID") + "&secret=" + Global.getConfig("miniProgram.secret")
				+ "&js_code=" + jsCode + "&grant_type=authorization_code";
		// 获取session_key，openid
		ResponseEntity<String> c2sResult = restTemplate.getForEntity(code2session, String.class);
		Map c2sResultMap = gson.fromJson(c2sResult.getBody(), Map.class);
		logger.info("获取session_key:" + c2sResultMap);
		if (c2sResultMap.get("errorcode") != null) {
			return mapReturn(rtnMap, false, "登录失败");
		}
		String sessionKey = (String) c2sResultMap.get("session_key");
		// 通过session_key解密encryptedData，获取unionID，保存登录状态
		String decryptData = WeChatUtils.getDecryptData(encryptedData, sessionKey, iv);
		logger.info("解密数据:" + decryptData);
		HashMap<String, Object> mp = gson.fromJson(decryptData, HashMap.class);
		String unionId = (String) mp.get("unionId");
		Guest guest = new Guest();
		guest.setUnionId(unionId);
		// 通过unionId查询绑定用户信息，未绑定不返回用户信息
		guest = guestDao.getByUnionID(guest);

		boolean bindFlag = false;
		if (guest != null) {
			bindFlag = true;
			// unionId、openId不传给小程序端
			guest.setOpenId(null);
			guest.setUnionId(null);
			User user = userDao.get(guest.getId());
			String gofficeId = guest.getGofficeId();
			String gofficeName = guest.getGofficeName();
			// 是否为门店柜员
			boolean isDoorUser = user.getOffice().getType().equals(Constant.OfficeType.STORE);
			// 所属门店下是否拥有机具
			boolean hasEquipment = false;
			List<String> equipmentIdList = guest.getEquipmentIdList();
			if (!Collections3.isEmpty(equipmentIdList)) {
				hasEquipment = true;
			}
			rtnMap.put("officeId", gofficeId);
			rtnMap.put("officeName", gofficeName);
			rtnMap.put("hasEquipment", hasEquipment);
			rtnMap.put("isDoorUser", isDoorUser);
			rtnMap.put("userInfo", guest);
		}
		// 解密后的数据生成token
		String token = TokenUtils.generateToken(mp);
		rtnMap.put("bindFlag", bindFlag);
		rtnMap.put("token", token);
		return mapReturn(rtnMap, true, null);
	}

	/**
	 * 微信用户和平台用户绑定
	 *
	 * @param token
	 *            通过登录获取的token
	 * @param loginName
	 *            平台用户登录名
	 * @param password
	 *            平台用户登录密码
	 * @return 平台用户信息
	 * @author yinkai
	 */
	@Transactional
	public Map<String, Object> bindUser(String token, String loginName, String password) {
		// 接口返回对象
		Map<String, Object> rtnMap = new HashMap<>();
		// 解析token，获取微信用户信息（主要获得unionId）
		Claims claims = TokenUtils.verify(token);
		if (claims == null) {
			return mapReturn(rtnMap, false, "会话失效");
		}
		User user = new User();
		user.setLoginName(loginName);
		// 验证登录名和密码是否正确
		User userByLoginName = userDao.getByLoginName(user);
		if (userByLoginName == null) {
			return mapReturn(rtnMap, false, "用户名或密码错误");
		}
		boolean validatePassword = SystemService.validatePassword(password, userByLoginName.getPassword());
		if (!validatePassword) {
			return mapReturn(rtnMap, false, "用户名或密码错误");
		}
		// 是否为门店柜员
		boolean isDoorUser = userByLoginName.getOffice().getType().equals(Constant.OfficeType.STORE);
		String officeName = userByLoginName.getOffice().getName();
		String officeId = userByLoginName.getOffice().getId();
		Guest tempGuest = new Guest();
		tempGuest.setId(userByLoginName.getId());
		Guest idGuest = guestDao.getByUnionID(tempGuest);
		// 所属门店下是否拥有机具
		boolean hasEquipment = false;
		if (idGuest != null && !Collections3.isEmpty(idGuest.getEquipmentIdList())) {
			hasEquipment = true;
		}
		Guest guestById = guestDao.get(userByLoginName.getId());
		rtnMap.put("isDoorUser", isDoorUser);
		rtnMap.put("hasEquipment", hasEquipment);
		rtnMap.put("officeId", officeId);
		rtnMap.put("officeName", officeName);
		// guest中有用户信息的情况
		if (guestById != null) {
			if (StringUtils.isNotEmpty(guestById.getUnionId())) {
				if (claims.get("unionId").toString().equals(guestById.getUnionId())) {
					// guest中有平台用户信息且union与自己的相同，正常成功返回
					guestById.setUnionId(null);
					rtnMap.put("userInfo", guestById);
					return mapReturn(rtnMap, true, null);
				} else {
					// guest中有平台用户信息且union与自己的不同，其他微信已绑定此用户，失败返回
					return mapReturn(rtnMap, false, "该用户已与其他微信绑定");
				}
			} else {
				// guest中有用户信息但unionId为空的情况，直接更新unionId
				guestById.setUnionId(claims.get("unionId").toString());
				guestDao.update(guestById);
				guestById.setUnionId(null);
				rtnMap.put("userInfo", guestById);
				return mapReturn(rtnMap, true, null);
			}
		}
		// 平台用户信息
		Guest guest = new Guest();
		guest.setId(userByLoginName.getId());
		guest.setUnionId(claims.get("unionId").toString());
		guest.setGofficeId(userByLoginName.getOffice().getId());
		guest.setGofficeName(userByLoginName.getOffice().getName());
		guest.setGidcardNo(userByLoginName.getIdcardNo());
		guest.setGphone(userByLoginName.getPhone());
		guest.setGname(userByLoginName.getName());
		guest.setGuestType(userByLoginName.getUserType());
		guestDao.insert(guest);
		guest.setUnionId(null);
		rtnMap.put("userInfo", guest);
		return mapReturn(rtnMap, true, null);
	}

	/**
	 * 安全退出（解绑）
	 *
	 * @param userId
	 *            用户ID
	 * @return rtnMap
	 * @author yinkai
	 */
	@Transactional
	public Map<String, Object> removeBind(String userId) {
		// 接口返回对象
		Map<String, Object> rtnMap = new HashMap<>();
		Guest guest = guestDao.get(userId);
		if (guest == null || StringUtils.isBlank(guest.getUnionId())) {
			return mapReturn(rtnMap, false, "此用户尚未绑定微信");
		}
		guest.setUnionId(null);
		guestDao.update(guest);
		return mapReturn(rtnMap, true, null);
	}

	/**
	 * 通过token换取用户信息
	 *
	 * @param token
	 *            微信用户凭证
	 * @return userInfo
	 * @author yinkai
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> getUserInfo(String token, String version) {
		// 接口返回对象
		Map<String, Object> rtnMap = new HashMap<>();
		Claims claims = TokenUtils.verify(token);
		if (claims == null) {
			return mapReturn(rtnMap, false, "会话失效");
		}
		String unionId = claims.get("unionId").toString();
		Guest guest = new Guest();
		guest.setUnionId(unionId);
		Guest guestByUnionID = guestDao.getByUnionID(guest);
		if (guestByUnionID == null) {
			return mapReturn(rtnMap, false, "获取信息失败");
		}
		// 小程序登录流程：getToken没返回userInfo的情况调用getUserInfo，version字段是空的
		if (StringUtils.isNotBlank(version)) {
			int fromVersion = Integer.parseInt(version);
			int currentVersion = guestByUnionID.getVersion();
			if (fromVersion != currentVersion) {
				guestByUnionID.setUnionId(null);
				guestDao.update(guestByUnionID);
				return mapReturn(rtnMap, false, "登录名或密码已被修改，请重新绑定或联系管理员");
			}
		}
		if (StringUtils.isNotBlank(guestByUnionID.getId())) {
			User user = UserUtils.get(guestByUnionID.getId());
			if (user == null) {
				return mapReturn(rtnMap, false, "该用户不存在");
			}
			if (Constant.SysUserType.SYSTEM.equals(user.getUserType())) {
				rtnMap.put("isAdmin", true);
			} else {
				rtnMap.put("isAdmin", false);
			}
		}
		guestByUnionID.setUnionId(null);
		guestByUnionID.setOpenId(null);
		rtnMap.put("userInfo", guestByUnionID);
		return mapReturn(rtnMap, true, null);
	}

	/**
	 * 接口返回参数
	 *
	 * @param rtnMap
	 *            返回参数map
	 * @param resultFlag
	 *            返回结果：true成功，false失败
	 * @param errorMsg
	 *            返回失败消息
	 * @return rtnMap
	 * @author yinkai
	 */
	private Map<String, Object> mapReturn(Map<String, Object> rtnMap, boolean resultFlag, String errorMsg) {
		rtnMap.put("resultFlag", resultFlag ? 0 : 1);
		rtnMap.put("errorMsg", errorMsg);
		return rtnMap;
	}
}
