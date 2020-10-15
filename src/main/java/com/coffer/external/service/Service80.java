package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service80
 * <p>
 * Description: PDA获取金库下属网点尾箱信息，用于盘点信息对账
 * </p>
 * 
 * @author SongYuanYang
 * @date 2017年12月8日
 */
@Component("Service80")
@Scope("singleton")
public class Service80 extends HardwardBaseService {

	@Autowired
	private StoBoxInfoService stoBoxInfoService;

	/**
	 * <p>
	 * Description: 80 尾箱信息查询接口
	 * </p>
	 * 
	 * @author: SongYuanYang
	 * @param paramMap
	 * @return String 返回类型
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug("80 尾箱信息查询接口 -----------------开始");
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = (List<Map<String, String>>) paramMap.get(Parameter.LIST_KEY);
		// 盘点人验证
		for (Map<String, String> map : list) {
			if (UserUtils.getByLoginName(map.get(Parameter.LOGIN_NAME_KEY)) == null) {
				logger.warn("输入参数错误：" + Parameter.USER_LIST_KEY + " 不存在或为空");
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_MSG_KEY, "用户不存在!");
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return gson.toJson(respMap);
			}
		}
		// 机构id为空时返回错误信息
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		if (StringUtils.isBlank(officeId)) {
			logger.warn("机构id为空");
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_MSG_KEY, "机构id不可为空!");
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
		}
		// 机构不存在时返回错误信息
		if (SysCommonUtils.findOfficeById(officeId) == null) {
			logger.warn("机构不存在");
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_MSG_KEY, "机构不存在!");
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
		}
		// 根据officeId获取在库尾箱信息
		List<StoBoxInfo> infoList = stoBoxInfoService.findTailBoxList(officeId);
		if (infoList.size() == 0) {
			logger.warn("80 尾箱信息查询接口 -------------尾箱信息不存在 ！");
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_MSG_KEY, "尾箱信息不存在 ！");
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E24);
			return gson.toJson(respMap);
		}
		List<Map<String, Object>> TailBoxMapList = Lists.newArrayList();
		for (StoBoxInfo stoBoxInfo : infoList) {
			Map<String, Object> TailBoxMap = Maps.newHashMap();
			// 箱袋编号
			TailBoxMap.put(Parameter.BOX_NO_KEY, stoBoxInfo.getId());
			// 箱袋类型
			TailBoxMap.put(Parameter.BOX_TYPE_KEY, stoBoxInfo.getBoxType());
			// 箱袋状态
			TailBoxMap.put(Parameter.BOX_STATUS_KEY, stoBoxInfo.getBoxStatus());
			// 所属机构
			TailBoxMap.put(Parameter.OFFICE_ID_KEY, stoBoxInfo.getOffice().getId());
			// 钞箱金额
			TailBoxMap.put(Parameter.BOX_AMOUNT_KEY, stoBoxInfo.getBoxAmount());
			// 箱袋RFID号
			TailBoxMap.put(Parameter.RFID_KEY, stoBoxInfo.getRfid());
			TailBoxMapList.add(TailBoxMap);
		}
		respMap.put(Parameter.LIST_KEY, TailBoxMapList);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		logger.debug("80 尾箱信息查询接口 -----------------结束");
		return gson.toJson(respMap);
	}

}
