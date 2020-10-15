package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.Encodes;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service78
 * <p>
 * Description: 扫描门同步照片信息
 * </p>
 * 
 * @author xp
 * @date 2017年9月27日
 */
@Component("Service78")
@Scope("singleton")
public class Service78 extends HardwardBaseService {

	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * 
	 * @author xp
	 * @version 2017年9月27日
	 * 
	 *          扫描门同步照片信息
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = Lists.newArrayList();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 判断officeId是否为空
		if (StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId不能为空");
			return gson.toJson(map);
		}

		String officeId = CommonUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
		String searchDate = CommonUtils.toString(paramMap.get(Parameter.SEARCH_DATE_KEY));

		// 查询出对应人员的信息
		List<StoEscortInfo> escortList = stoEscortInfoService.findPhotoList(officeId, searchDate);
		// 进行过滤
		for (StoEscortInfo escortItem : escortList) {
			Map<String, Object> userMap = Maps.newHashMap();
			if (escortItem.getPhoto() != null && escortItem.getPhoto().length > 0) {
				userMap.put(Parameter.PHOTO_KEY, Encodes.encodeBase64(escortItem.getPhoto()));
			} else {
				continue;
			}
			userMap.put(Parameter.ID_CARD_NO_KEY, escortItem.getIdcardNo());
			list.add(userMap);
		}
		if (!Collections3.isEmpty(escortList)) {
			// 返回参数中的searchDate
			map.put(Parameter.SEARCH_DATE_KEY,
					DateUtils.formatDate(escortList.get(escortList.size() - 1).getUpdateDate(),
							Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS));
		} else {
			map.put(Parameter.SEARCH_DATE_KEY, "");
		}
		map.put(Parameter.LIST_KEY, list);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		return gson.toJson(map);
	}

}
