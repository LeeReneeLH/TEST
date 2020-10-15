package com.coffer.external.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service55
 * <p>
 * Description: 指纹登录同步用户信息
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service55")
@Scope("singleton")
public class Service55 extends HardwardBaseService {

	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 
	 * @author LLF
	 * @version 2016年6月3日
	 * 
	 *          指纹登录同步用户信息
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		List<Map<String, Object>> list = Lists.newArrayList();
		List<String> officeTypes = Lists.newArrayList();
		officeTypes.add(Constant.OfficeType.COFFER);
		officeTypes.add(Constant.OfficeType.CLEAR_CENTER);

		List<StoEscortInfo> escortList = stoEscortInfoService.findPbocEscortInfo(paramMap, null, officeTypes);

		for (StoEscortInfo escortItem : escortList) {
			Map<String, Object> userMap = Maps.newHashMap();

			// if (escortItem.getFingerNo1() == null &&
			// escortItem.getFingerNo1().length == 0
			// && escortItem.getFingerNo2() == null &&
			// escortItem.getFingerNo2().length == 0) {
			// continue;
			// }
			User user = escortItem.getUser();
			if (user != null) {
				userMap.put("userId", escortItem.getUser().getId());
				userMap.put("loginName", escortItem.getUser().getLoginName());
				userMap.put("password", escortItem.getUser().getPassword());
				userMap.put("roleList", this.setRoleList(user.getCsPermission()));
				// 改为取得人员表的脸部识别ID
				// userMap.put(Parameter.USER_FACE_ID_KEY,
				// escortItem.getUser().getUserFaceId());
			} else {
				userMap.put("userId", null);
				userMap.put("loginName", null);
				userMap.put("password", null);
				userMap.put("roleList", null);
				userMap.put(Parameter.USER_FACE_ID_KEY, null);
			}
			// 改为取得人员表的脸部识别ID
			userMap.put(Parameter.USER_FACE_ID_KEY, escortItem.getUserFaceId());
			userMap.put("name", escortItem.getEscortName());
			userMap.put("escortId", escortItem.getId());
			userMap.put("handlePassword", escortItem.getPassword());
			userMap.put("idcardNo", escortItem.getIdcardNo());
			userMap.put("rfid", escortItem.getRfid());
			userMap.put("userType", escortItem.getEscortType());
			userMap.put(Parameter.OFFICE_ID_KEY, escortItem.getOffice().getId());
			if (escortItem.getFingerNo1() != null && escortItem.getFingerNo1().length > 0) {
				userMap.put(Parameter.FINGER_NO1_KEY, Encodes.encodeBase64(escortItem.getFingerNo1()));
			} else {
				userMap.put(Parameter.FINGER_NO1_KEY, null);
			}
			if (escortItem.getFingerNo2() != null && escortItem.getFingerNo2().length > 0) {
				userMap.put(Parameter.FINGER_NO2_KEY, Encodes.encodeBase64(escortItem.getFingerNo2()));
			} else {
				userMap.put(Parameter.FINGER_NO2_KEY, null);
			}
			list.add(userMap);
		}
		map.put("searchDate", DateUtils.getCurrentMillisecond());
		map.put(Parameter.LIST_KEY, list);
		map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		return gson.toJson(map);
	}

	/**
	 * @author LF 获取角色列表
	 * @param roleString
	 * @return
	 */
	private List<Map<String, Object>> setRoleList(String roleString) {

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
