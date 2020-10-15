package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
* Title: Service51
* <p>Description: 原封新券入库接口 </p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service51")
@Scope("singleton")
public class Service51 extends HardwardBaseService {

	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2016年6月1日
	 * 
	 *          原封新券入库接口
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		List<Map<String, Object>> list = (List<Map<String, Object>>) paramMap.get(Parameter.LIST_KEY);
		// 入库时间
		Date date = new Date();
		Office office = new Office(paramMap.get(Parameter.OFFICE_ID_KEY).toString());
		User user = new User(paramMap.get(Parameter.USER_ID_KEY).toString());
		String userName = paramMap.get(Parameter.USER_NAME_KEY).toString();
		
		// 原封箱袋是否有问题
		List<String> erroList = Lists.newArrayList();
		
		// 校验原封券箱是否正确
		for (Map<String, Object> itemMap : list) {
			StoOriginalBanknote stoOriginalBanknote = stoOriginalBanknoteService.getOriginalBanknoteById(itemMap.get(Parameter.BOX_NO_KEY)
					.toString(), paramMap.get(Parameter.OFFICE_ID_KEY).toString());
			if (stoOriginalBanknote != null) {
				erroList.add(itemMap.get(Parameter.BOX_NO_KEY).toString());
			}
		}

		if (Collections3.isEmpty(erroList)) {
			for (Map<String, Object> itemMap : list) {
				StoOriginalBanknote stoOriginalBanknote = new StoOriginalBanknote();
				stoOriginalBanknote.setId(itemMap.get(Parameter.BOX_NO_KEY).toString());
				stoOriginalBanknote.setSets(itemMap.get(Parameter.SETS_KEY).toString());
				stoOriginalBanknote.setOriginalTranslate(itemMap.get(Parameter.ORIGINAL_TRANSLATE_KEY).toString());
				stoOriginalBanknote.setDenomination(itemMap.get(Parameter.DENOMINATION_KEY).toString());
				stoOriginalBanknote.setAmount(Long.valueOf(itemMap.get(Parameter.AMOUNT_KEY).toString()));
				stoOriginalBanknote.setRoffice(office);
				stoOriginalBanknote.setCreateBy(user);
				stoOriginalBanknote.setCreateName(userName);
				stoOriginalBanknote.setCreateDate(date);
				// 保存原封新券入库信息
				stoOriginalBanknoteService.save(stoOriginalBanknote);
			}
			// 成功结果
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} else {
			// 　回收失败，存在不正确箱子
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "上传失败:"+Collections3.convertToString(erroList, ","));
		}

		return gson.toJson(map);
	}

}
