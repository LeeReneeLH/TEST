package com.coffer.external.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service74
 * <p>
 * Description: 用户信息同步接口(对外)
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月10日 上午10:41:10
 */
@Component("Service74")
@Scope("singleton")
public class Service74 extends HardwardBaseService {

	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug(this.getClass().getName() + "用户信息同步接口(对外) -----------------开始");
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		// TODO 握手加密验证信息
		
		try {

			if (paramMap.get(Parameter.OFFICE_ID_KEY) == null
					|| StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return gson.toJson(respMap);
			}
			// 获取增量日期查询条件
			String lastSearchDate = StringUtils.toString(paramMap.get(Parameter.SEARCH_DATE_KEY));
			// 指定机构
			String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
			// 查询机构范围标识
			String searchScopeFlag = StringUtils.toString(paramMap.get(Parameter.SEARCH_SCOPE_FLAG_KEY));
			// 查询范围未指定时，设定查询范围为全部查询
			searchScopeFlag = StringUtils.isBlank(searchScopeFlag) ? ExternalConstant.SearchScopeFlag.SCOPE_ALL
					: searchScopeFlag;

			List<User> userInfoList = Lists.newArrayList();

			if (ExternalConstant.SearchScopeFlag.SCOPE_ALL.equals(searchScopeFlag)) {
				// 查询参数机构及其下属机构
				Office officeInterface = SysCommonUtils.findOfficeById(officeId.toString());
				String strParentIds = officeInterface.getParentIds() + officeInterface.getId();
				List<Office> searchOfficeList = SysCommonUtils.findOfficeListForInterface(officeId, strParentIds, null);
				// 提取机构ID列表
				@SuppressWarnings("unchecked")
				List<String> searchOfficeIdList = Collections3.extractToList(searchOfficeList,
						ExternalConstant.ExtractEntityFiled.EXTRACT_FILED_ID);
				// 查询用户信息列表
				userInfoList.addAll(SysCommonUtils.findUserInfoByOfficeIds(lastSearchDate, searchOfficeIdList));
			} else {
				// 查询参数机构下用户信息列表
				userInfoList.addAll(SysCommonUtils.findUserInfoByOfficeId(officeId, lastSearchDate));
			}

			// 格式化输出
			respMap.put(Parameter.OFFICE_ID_KEY, paramMap.get(Parameter.OFFICE_ID_KEY));

			List<Map<String, Object>> rtnMapList = Lists.newArrayList();
			Map<String, Object> innerMap = null;
			for (User tempUser : userInfoList) {
				innerMap = Maps.newHashMap();
				innerMap.put(Parameter.USER_ID_KEY, tempUser.getId());
				innerMap.put(Parameter.LOGIN_NAME_KEY, tempUser.getLoginName());
				innerMap.put(Parameter.PASSWORD_KEY, tempUser.getPassword());
				innerMap.put(Parameter.USER_NAME_KEY, tempUser.getName());
				innerMap.put(Parameter.ID_CARD_NO_KEY, tempUser.getIdcardNo());
				innerMap.put(Parameter.USER_FACE_ID_KEY, tempUser.getUserFaceId());
				innerMap.put(Parameter.USER_TYPE_KEY, tempUser.getUserType());
				innerMap.put(Parameter.OFFICE_ID_KEY, tempUser.getOffice().getId());
				innerMap.put(Parameter.DEL_FLAG_KEY, tempUser.getDelFlag());
				rtnMapList.add(innerMap);
			}
			respMap.put(Parameter.LIST_KEY, rtnMapList);
			// 指定最后查询日期
			if (userInfoList.size() > 0) {
				respMap.put(Parameter.SEARCH_DATE_KEY,
						DateUtils.formatDate(userInfoList.get(userInfoList.size() - 1).getUpdateDate(),
								Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS));
			} else {
				respMap.put(Parameter.SEARCH_DATE_KEY,
						DateUtils.formatDate(new Date(), Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS));
			}
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		logger.debug(this.getClass().getName() + "用户信息同步接口(对外) -----------------结束");
		return gson.toJson(respMap);
	}

}
