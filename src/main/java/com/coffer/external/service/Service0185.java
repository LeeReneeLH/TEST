package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

@Component("Service0185")
@Scope("singleton")
public class Service0185 extends HardwardBaseService {

	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 
	 * @author xp 脸谱信息采集
	 * @version 2017-08-03
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// try {
		// 判断机构编号是否为空
		if (StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "机构编号不能为空！");
			return gson.toJson(map);
		}
		// 判断列表是否为空
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Parameter.ESCORT_LIST);
		if (Collections3.isEmpty(list)) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "脸谱信息列表不能为空！");
			return gson.toJson(map);
		} else {
			for (Map<String, Object> stoEscort : list) {
				if (StringUtils.isEmpty(stoEscort.get(Parameter.USER_FACE_ID_KEY).toString())) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "脸谱Id不能为空！");
					return gson.toJson(map);
				}
				if (StringUtils.isEmpty(stoEscort.get(Parameter.BINDING_FACE).toString())) {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
					map.put(Parameter.ERROR_MSG_KEY, "脸谱采集标识不能为空！");
					return gson.toJson(map);
				}
			}
		}
		// 解析用户编号
		if (StringUtils.isEmpty(paramMap.get(Parameter.USER_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "用户ID不能为空！");
			return gson.toJson(map);
		}
		// 解析系统登录用户姓名
		if (StringUtils.isEmpty(paramMap.get(Parameter.USER_NAME_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "用户姓名不能为空！");
			return gson.toJson(map);
		}
		Office office = new Office();
		office.setId(paramMap.get(Parameter.OFFICE_ID_KEY).toString());
		StoEscortInfo stoEscortInfo = new StoEscortInfo();
		stoEscortInfo.setOffice(office);
		@SuppressWarnings("unused")
		String officeId = paramMap.get(Parameter.OFFICE_ID_KEY).toString();
		// 通过传来的officeid查询该机构下的所有人员
		List<StoEscortInfo> escortInfoList = stoEscortInfoService.findEscortList(stoEscortInfo);
		// 错误信息
		StringBuffer message = new StringBuffer();
		for (Map<String, Object> stoEscort : list) {
			boolean boo = true;
			for (StoEscortInfo escort : escortInfoList) {
				// 如果UserFaceId不为空，则进行下一步判断
				if (escort.getUserFaceId() != null) {
					// 如果存在
					if ((stoEscort.get(Parameter.USER_FACE_ID_KEY).toString())
							.equals(escort.getUserFaceId().toString())) {
						// 设置更新时间
						escort.setUpdateDate(new Date());
						// 设置更新人
						User user = new User();
						user.setId(paramMap.get(Parameter.USER_ID_KEY).toString());
						escort.setUpdateBy(user);
						escort.setUpdateName(paramMap.get(Parameter.USER_NAME_KEY).toString());
						escort.setbindingFace(stoEscort.get(Parameter.BINDING_FACE).toString());
						stoEscortInfoService.update(escort);
						boo = false;
						break;
					}
				}
			}
			if (boo) {
				if (StringUtils.isEmpty(message.toString())) {
					message.append("userFaceId:" + stoEscort.get(Parameter.USER_FACE_ID_KEY) + ",");
				} else {
					message.append(stoEscort.get(Parameter.USER_FACE_ID_KEY) + ",");
				}
			}
		}
		if (StringUtils.isEmpty(message.toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} else {
			message.deleteCharAt(message.length() - 1);
			message.append(" 未查询到");
			throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E51, message.toString());
		}
		// } catch (Exception e) {
		// e.printStackTrace();
		// map.put(Parameter.RESULT_FLAG_KEY,
		// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		// map.put(Parameter.ERROR_NO_KEY,
		// ExternalConstant.HardwareInterface.ERROR_NO_E02);
		// }
		return gson.toJson(map);

	}
}
