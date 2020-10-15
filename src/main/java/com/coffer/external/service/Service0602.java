package com.coffer.external.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.cloudPlatform.CloudPlatformConstant;
import com.coffer.businesses.modules.cloudPlatform.v04.entity.EyeCheckEscortInfo;
import com.coffer.businesses.modules.cloudPlatform.v04.service.EyeCheckEscortInfoService;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

@Component("Service0602")
@Scope("singleton")
public class Service0602 extends HardwardBaseService {

	@Autowired
	EyeCheckEscortInfoService eyeCheckEscortInfoService;
	@Autowired
	OfficeService officeService;

	/**
	 * 双目识别人员信息采集
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
		// 人员类别
		if (StringUtils.isBlank(paramMap.get(Parameter.ESCORT_TYPE).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "人员类别不能为空！");
			return gson.toJson(map);
		}
		// 地址
		if (StringUtils.isBlank(paramMap.get(Parameter.ADDRESS).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "人员地址不能为空！");
			return gson.toJson(map);
		}
		// 机构
		if (StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "使用机构不能为空！");
			return gson.toJson(map);
		}
		// 出生日期
		if (StringUtils.isBlank(paramMap.get(Parameter.IDENTITYBIRTH).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "出生日期不能为空！");
			return gson.toJson(map);
		}
		// 发证机关
		if (StringUtils.isBlank(paramMap.get(Parameter.IDENTITYVISA).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "发证机关不能为空！");
			return gson.toJson(map);
		}
		// 性别
		if (StringUtils.isBlank(paramMap.get(Parameter.IDENTITYGENDER).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "性别不能为空！");
			return gson.toJson(map);
		}
		// 民族
		if (StringUtils.isBlank(paramMap.get(Parameter.IDENTITYNATIONAL).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "民族不能为空！");
			return gson.toJson(map);
		}
		// 照片
		/*if (StringUtils.isBlank(paramMap.get(Parameter.PHOTO_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "照片不能为空！");
			return gson.toJson(map);
		}*/
		// 截止时间（访客必填）
		if (paramMap.get(Parameter.ESCORT_TYPE).toString().equals(CloudPlatformConstant.escortType.VISITOR)) {
			if (StringUtils.isBlank(paramMap.get(Parameter.END_DATE_KEY).toString())) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				map.put(Parameter.ERROR_MSG_KEY, "截止时间不能为空！");
				return gson.toJson(map);
			}
		}
		// 删除标记
		if (StringUtils.isBlank(paramMap.get(Parameter.DEL_FLAG_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "删除标记不能为空！");
			return gson.toJson(map);
		}
		// 根据删除标记执行操作
		// 标记正常，执行登记或更新
		if (paramMap.get(Parameter.DEL_FLAG_KEY).toString().equals(CloudPlatformConstant.delFlag.NORMAL)) {
			// 根据身份证和归属机构判断数据库有无人员信息(有更新，没有插入)
			EyeCheckEscortInfo eyeCheckEscortInfo = eyeCheckEscortInfoService.getEscortFromIdcardAndOfficeId(
					paramMap.get(Parameter.ID_CARD_NO_KEY).toString(),
					paramMap.get(Parameter.OFFICE_ID_KEY).toString());
			// 无，插入
			if (eyeCheckEscortInfo == null) {
				EyeCheckEscortInfo eyeCheckEscortInfoInsert = new EyeCheckEscortInfo();
				eyeCheckEscortInfoInsert.setEscortId(IdGen.uuid());
				eyeCheckEscortInfoInsert.setIdcardNo(paramMap.get(Parameter.ID_CARD_NO_KEY).toString());
				eyeCheckEscortInfoInsert.setEscortName(paramMap.get(Parameter.ESCORT_NAME).toString());
				eyeCheckEscortInfoInsert.setEscortType(paramMap.get(Parameter.ESCORT_TYPE).toString());
				eyeCheckEscortInfoInsert.setAddress(paramMap.get(Parameter.ADDRESS).toString());
				// 使用机构
				eyeCheckEscortInfoInsert.setOffice(officeService.get(paramMap.get(Parameter.OFFICE_ID_KEY).toString()));
				eyeCheckEscortInfoInsert.setIdentityBirth(paramMap.get(Parameter.IDENTITYBIRTH).toString());
				eyeCheckEscortInfoInsert.setIdentityVisa(paramMap.get(Parameter.IDENTITYVISA).toString());
				eyeCheckEscortInfoInsert.setIdentityGender(paramMap.get(Parameter.IDENTITYGENDER).toString());
				eyeCheckEscortInfoInsert.setIdentityNational(paramMap.get(Parameter.IDENTITYNATIONAL).toString());
				//eyeCheckEscortInfoInsert.setPhoto(Encodes.decodeBase64(paramMap.get(Parameter.PHOTO_KEY).toString()));

				// 结束日期（可能需要格式化）
				if ((paramMap.get(Parameter.END_DATE_KEY) != null)
						&& StringUtils.isNotBlank(paramMap.get(Parameter.END_DATE_KEY).toString())) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						eyeCheckEscortInfoInsert
								.setEndDate(simpleDateFormat.parse(paramMap.get(Parameter.END_DATE_KEY).toString()));
					} catch (ParseException px) {
						px.printStackTrace();
					}
				}
				eyeCheckEscortInfoService.save(eyeCheckEscortInfoInsert);
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

			}
			// 有，更新
			else {
				EyeCheckEscortInfo eyeCheckEscortInfoInsert = new EyeCheckEscortInfo();
				eyeCheckEscortInfoInsert.setId(eyeCheckEscortInfo.getEscortId());
				eyeCheckEscortInfoInsert.preUpdate();
				eyeCheckEscortInfoInsert.setEscortId(eyeCheckEscortInfo.getEscortId());
				eyeCheckEscortInfoInsert.setIdcardNo(paramMap.get(Parameter.ID_CARD_NO_KEY).toString());
				eyeCheckEscortInfoInsert.setEscortName(paramMap.get(Parameter.ESCORT_NAME).toString());
				eyeCheckEscortInfoInsert.setEscortType(paramMap.get(Parameter.ESCORT_TYPE).toString());
				eyeCheckEscortInfoInsert.setAddress(paramMap.get(Parameter.ADDRESS).toString());
				// 使用机构
				eyeCheckEscortInfoInsert.setOffice(officeService.get(paramMap.get(Parameter.OFFICE_ID_KEY).toString()));
				eyeCheckEscortInfoInsert.setIdentityBirth(paramMap.get(Parameter.IDENTITYBIRTH).toString());
				eyeCheckEscortInfoInsert.setIdentityVisa(paramMap.get(Parameter.IDENTITYVISA).toString());
				eyeCheckEscortInfoInsert.setIdentityGender(paramMap.get(Parameter.IDENTITYGENDER).toString());
				eyeCheckEscortInfoInsert.setIdentityNational(paramMap.get(Parameter.IDENTITYNATIONAL).toString());
				//eyeCheckEscortInfoInsert.setPhoto(Encodes.decodeBase64(paramMap.get(Parameter.PHOTO_KEY).toString()));
				// 结束日期（可能需要格式化）
				if ((paramMap.get(Parameter.END_DATE_KEY) != null)
						&& StringUtils.isNotBlank(paramMap.get(Parameter.END_DATE_KEY).toString())) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						eyeCheckEscortInfoInsert
								.setEndDate(simpleDateFormat.parse(paramMap.get(Parameter.END_DATE_KEY).toString()));
					} catch (ParseException px) {
						px.printStackTrace();
					}
				}
				eyeCheckEscortInfoService.save(eyeCheckEscortInfoInsert);
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}
		}
		// 标记为删除，执行删除操作
		else if (paramMap.get(Parameter.DEL_FLAG_KEY).toString().equals(CloudPlatformConstant.delFlag.DELETE)) {
			// 根据身份证和归属机构判断数据库有无人员信息
			EyeCheckEscortInfo eyeCheckEscortInfo = new EyeCheckEscortInfo();
			eyeCheckEscortInfo = eyeCheckEscortInfoService.getEscortFromIdcardAndOfficeId(
					paramMap.get(Parameter.ID_CARD_NO_KEY).toString(),
					paramMap.get(Parameter.OFFICE_ID_KEY).toString());
			// 没有信息
			if (eyeCheckEscortInfo == null) {
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY, "人员信息不存在！");
				return gson.toJson(map);
			}
			// 删除信息
			else {
				eyeCheckEscortInfoService.delete(eyeCheckEscortInfo);
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			}
		}
		return gson.toJson(map);

	}

}
