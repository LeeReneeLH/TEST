package com.coffer.external.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.coffer.external.hessian.HardwareConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service08
 * <p>
 * Description: 选择机构下的行内人员接口信息
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:38:16
 */
@Component("Service08")
@Scope("singleton")
public class Service08 extends HardwardBaseService {

	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	@Autowired
	private SystemService systemService;

	/**
	 *
	 * @author LLF
	 * @version 2015年06月09日
	 *
	 *          选择机构下的行内人员接口信息
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();

		Map<String, Object> map = new HashMap<String, Object>();
		List<StoEscortInfo> list = new ArrayList<StoEscortInfo>();
		List<String> escortTypes = new ArrayList<String>();
		List<String> userTypes = new ArrayList<String>();
		List<User> sysuserList = new ArrayList<User>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		try {

			// 查询escort表中用户
			/*
			 * String[] eTypes = {Constant.SysUserType.COFFER_MANAGER,
			 * Constant.SysUserType.COFFER_OPT, Constant.SysUserType.CLASSFICATION_MANAGER,
			 * Constant.SysUserType.CLASSFICATION_OPT,
			 * Constant.SysUserType.BANK_OUTLETS_MANAGER,
			 * Constant.SysUserType.BANK_OUTLETS_OPT, Constant.SysUserType.CENTRAL_MANAGER,
			 * Constant.SysUserType.CENTRAL_OPT,
			 * Constant.SysUserType.CLEARING_CENTER_MANAGER,
			 * Constant.SysUserType.CLEARING_CENTER_OPT,
			 * Constant.SysUserType.CENTRAL_ALLOCATE_OPT,
			 * Constant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT,
			 * Constant.SysUserType.CENTRAL_STORE_MANAGER_OPT }; escortTypes =
			 * Arrays.asList(eTypes);
			 */
			escortTypes = Global.getList(ExternalConstant.PDA_SYNCHRONOUS_ESCORT_TYPE);
			list = stoEscortInfoService.findEscortInfo(paramMap, escortTypes);
			// 查询sys_user表中用户
			/*
			 * String[] types = {Constant.SysUserType.COFFER_MANAGER,
			 * Constant.SysUserType.COFFER_OPT, Constant.SysUserType.CLASSFICATION_MANAGER,
			 * Constant.SysUserType.CLASSFICATION_OPT,
			 * Constant.SysUserType.BANK_OUTLETS_MANAGER,
			 * Constant.SysUserType.BANK_OUTLETS_OPT, Constant.SysUserType.CENTRAL_MANAGER,
			 * Constant.SysUserType.CENTRAL_OPT,
			 * Constant.SysUserType.CLEARING_CENTER_MANAGER,
			 * Constant.SysUserType.CLEARING_CENTER_OPT,
			 * Constant.SysUserType.CENTRAL_ALLOCATE_OPT,
			 * Constant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT,
			 * Constant.SysUserType.CENTRAL_STORE_MANAGER_OPT }; userTypes =
			 * Arrays.asList(types);
			 */
			userTypes = Global.getList(ExternalConstant.PDA_SYNCHRONOUS_USER_TYPE);
			sysuserList = systemService.findUserInfoByOfficeId(paramMap, userTypes);

			List<Map<String, Object>> userList = getUserToMap(list, sysuserList, serviceNo);
			map.put("userList", userList);
			map.put("searchDate", DateUtils.getCurrentMillisecond());
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}

	/**
	 * 
	 * @author chenxin
	 * @version 2015年10月10日
	 * 
	 *          将用户信息封装为接口结构(Escort和sysuser)
	 * @param list
	 * @param userlist
	 * @param serviceNo
	 * @return
	 */
	private List<Map<String, Object>> getUserToMap(List<StoEscortInfo> list, List<User> userInfoList,
			String serviceNo) {
		List<Map<String, Object>> userList = Lists.newArrayList();
		if (!Collections3.isEmpty(list)) {
			for (StoEscortInfo stoEscortInfo : list) {
				Map<String, Object> userMap = Maps.newHashMap();
				userMap.put("userId", stoEscortInfo.getId());
				userMap.put("name", stoEscortInfo.getEscortName());
				userMap.put("idcardNo", stoEscortInfo.getIdcardNo());
				userMap.put("rfid", stoEscortInfo.getRfid());
				userMap.put("delFlag", stoEscortInfo.getDelFlag());
				if (HardwareConstant.ServiceNo.service_no_S12.equals(serviceNo)) {
					if (stoEscortInfo.getFingerNo1() != null && stoEscortInfo.getFingerNo1().length > 0) {

						userMap.put("finger1", Encodes.encodeBase64(stoEscortInfo.getFingerNo1()));
					} else {
						userMap.put("finger1", null);
					}
					if (stoEscortInfo.getFingerNo2() != null && stoEscortInfo.getFingerNo2().length > 0) {

						userMap.put("finger2", Encodes.encodeBase64(stoEscortInfo.getFingerNo2()));
					} else {
						userMap.put("finger2", null);
					}
				} else if (HardwareConstant.ServiceNo.service_no_S08.equals(serviceNo)) {
					// 取得机构ID
					userMap.put(Parameter.OFFICE_ID_KEY, stoEscortInfo.getOffice().getId());
					userMap.put("userType", stoEscortInfo.getEscortType());
					if (stoEscortInfo.getPdaFingerNo1() != null && stoEscortInfo.getPdaFingerNo1().length > 0) {

						userMap.put("pdaFinger1", Encodes.encodeBase64(stoEscortInfo.getPdaFingerNo1()));
					} else {
						userMap.put("pdaFinger1", null);
					}
					if (stoEscortInfo.getPdaFingerNo2() != null && stoEscortInfo.getPdaFingerNo2().length > 0) {

						userMap.put("pdaFinger2", Encodes.encodeBase64(stoEscortInfo.getPdaFingerNo2()));
					} else {
						userMap.put("pdaFinger2", null);
					}
					if (stoEscortInfo.getPhoto() != null && stoEscortInfo.getPhoto().length > 0) {
						userMap.put("photo", Encodes.encodeBase64(stoEscortInfo.getPhoto()));
					} else {
						userMap.put("photo", null);
					}
					userMap.put("handlePassword", stoEscortInfo.getPassword());
				}

				userList.add(userMap);
			}
		}

		if (!Collections3.isEmpty(userInfoList)) {
			for (User user : userInfoList) {
				Map<String, Object> userMap = Maps.newHashMap();
				userMap.put("userId", user.getId());
				userMap.put("idcardNo", user.getIdcardNo());
				userMap.put("delFlag", user.getDelFlag());
				if (HardwareConstant.ServiceNo.service_no_S12.equals(serviceNo)) {

				} else if (HardwareConstant.ServiceNo.service_no_S08.equals(serviceNo)) {
					// 取得机构ID
					userMap.put(Parameter.OFFICE_ID_KEY, user.getOffice().getId());
					userMap.put("loginName", user.getLoginName());
					userMap.put("password", user.getPassword());
					userMap.put("userType", user.getUserType());
				}
				userMap.put("name", user.getName());
				userList.add(userMap);
			}
		}

		return userList;
	}
}
