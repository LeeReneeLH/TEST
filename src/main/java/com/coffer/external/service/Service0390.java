package com.coffer.external.service;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.businesses.modules.atm.v01.service.AtmBoxModService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0390
 * <p>
 * Description: ATM钞箱类型配置接口
 * </p>
 * 
 * @author xiaoliang
 * @date 2017年11月27日
 */
@Component("Service0390")
@Scope("singleton")
public class Service0390 extends HardwardBaseService {

	@Autowired
	private AtmBoxModService atmBoxModService;

	@Autowired
	private OfficeService officeService;

	/**
	 * 钞箱类型信息
	 * 
	 * @author xiaoliang
	 * @version 2017年11月27日
	 * @param paramMap
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> resultmap = Maps.newHashMap();
		// 版本号
		String versionNo = (String) paramMap.get(Parameter.VERSION_NO_KEY);
		// 服务代码
		String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
		// 所属机构
		String officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		AtmBoxMod atmBoxModSearch = new AtmBoxMod();
		if (StringUtils.isNotBlank(officeId)) {
			Office office = officeService.get(officeId);
			if (office == null) {
				// 机构ID不正确
				resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				resultmap.put(Parameter.ERROR_MSG_KEY, "不存在机构ID为：" + officeId + "的数据");
				return setReturnMap(resultmap, serviceNo, versionNo);
			}
			resultmap.put(Parameter.OFFICE_ID_KEY, officeId);
			atmBoxModSearch.setOfficeId(officeId);
		}
		// 钞箱类型配置列表
		List<AtmBoxMod> atmBoxModList = atmBoxModService.findList(atmBoxModSearch);
		List<Map<String, Object>> list = Lists.newArrayList();
		for (AtmBoxMod atmBoxMod : atmBoxModList) {
			Map<String, Object> map = Maps.newHashMap();
			// 设置钞箱类型编号
			map.put(Parameter.BOX_TYPE_NO, atmBoxMod.getBoxTypeNo());
			// 设置钞箱类型名称
			map.put(Parameter.ATM_MOD_NAME, atmBoxMod.getModName());
			list.add(map);
		}
		resultmap.put(Parameter.LIST_KEY, list);
		resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(resultmap, serviceNo, versionNo);
	}

	/**
	 * 设置接口返回内容
	 * 
	 * @author xiaoliang
	 * @version 2017年11月27日
	 * @param map
	 * @param serviceNo
	 * @param versionNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo, String versionNo) {
		map.put(Parameter.VERSION_NO_KEY, versionNo);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		return gson.toJson(map);
	}

	
}