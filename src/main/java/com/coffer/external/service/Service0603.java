package com.coffer.external.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckVisitorInfo;
import com.coffer.businesses.modules.cloudPlatform.v04.service.EyeCheckVisitorInfoService;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

@Component("Service0603")
@Scope("singleton")
public class Service0603 extends HardwardBaseService {

	@Autowired
	OfficeService officeService;
	@Autowired
	EyeCheckVisitorInfoService eyeCheckVisitorInfoService;

	/**
	 * 双目识别人员信息上传
	 * 
	 * @author wangqingjie
	 * @version 2018-12-07
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 接口输入参数验证
		// 身份证号
		if (StringUtils.isBlank(paramMap.get(Parameter.ID_CARD_NO_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "身份证号不能为空！");
			return gson.toJson(map);
		}
		// 人员姓名
		if (StringUtils.isBlank(paramMap.get(Parameter.ESCORT_NAME).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "人员姓名不能为空！");
			return gson.toJson(map);
		}
		// 年龄
		if (StringUtils.isBlank(paramMap.get(Parameter.AGE).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "年龄不能为空！");
			return gson.toJson(map);
		}
		// 性别
		if (StringUtils.isBlank(paramMap.get(Parameter.IDENTITYGENDER).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "性别不能为空！");
			return gson.toJson(map);
		}
		// 机构
		if (StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "使用机构不能为空！");
			return gson.toJson(map);
		}
		// 照片
		/*if (StringUtils.isBlank(paramMap.get(Parameter.PHOTO_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "照片不能为空！");
			return gson.toJson(map);
		}*/
		// 相似度
		if (StringUtils.isBlank(paramMap.get(Parameter.SIMILARITH).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "相似度不能为空！");
			return gson.toJson(map);
		}
		EyeCheckVisitorInfo eyeCheckVisitorInfo = new EyeCheckVisitorInfo();
		eyeCheckVisitorInfo.setVisitorId(IdGen.uuid());
		eyeCheckVisitorInfo.setIdcardNo(paramMap.get(Parameter.ID_CARD_NO_KEY).toString());
		eyeCheckVisitorInfo.setEscortName(paramMap.get(Parameter.ESCORT_NAME).toString());
		eyeCheckVisitorInfo.setAge(paramMap.get(Parameter.AGE).toString());
		eyeCheckVisitorInfo.setOffice(officeService.get(paramMap.get(Parameter.OFFICE_ID_KEY).toString()));
		eyeCheckVisitorInfo.setSimilarity(paramMap.get(Parameter.SIMILARITH).toString());
		//eyeCheckVisitorInfo.setPhoto(Encodes.decodeBase64(paramMap.get(Parameter.PHOTO_KEY).toString()));
	    eyeCheckVisitorInfo.setIdentityGender(paramMap.get(Parameter.IDENTITYGENDER).toString());
		// 执行插入操作
		eyeCheckVisitorInfoService.save(eyeCheckVisitorInfo);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(map);
	}

}
