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
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

/**
* Title: Service53
* <p>Description: 原封新券回收接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service53")
@Scope("singleton")
public class Service53 extends HardwardBaseService {

	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2016年6月1日
	 * 
	 *          原封新券回收接口
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
		// 出库时间
		Date date = new Date();
		Office office = null;
		if (paramMap.get(Parameter.OFFICE_ID_KEY) != null && !StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			office = new Office(paramMap.get(Parameter.OFFICE_ID_KEY).toString());
		}
		User user = new User(paramMap.get(Parameter.USER_ID_KEY).toString());
		String userName = paramMap.get(Parameter.USER_NAME_KEY).toString();
		// 原封箱袋是否有问题
		List<String> erroList = Lists.newArrayList();
		Office rOffice = UserUtils.get(user.getId()).getOffice();
		// 校验回收原封券箱是够正确
		for (Map<String, Object> itemMap : list) {
			StoOriginalBanknote stoOriginalBanknote = stoOriginalBanknoteService.getOriginalBanknoteById(itemMap.get(Parameter.BOX_NO_KEY)
					.toString(), rOffice.getId());
			if (stoOriginalBanknote == null
					|| !ExternalConstant.RecoverStatus.NO_RECOVER.equals(stoOriginalBanknote.getRecoverStatus())) {
				erroList.add(itemMap.get(Parameter.BOX_NO_KEY).toString());
			}
		}
		
		if (Collections3.isEmpty(erroList)) {
			for (Map<String, Object> itemMap : list) {
				StoOriginalBanknote stoOriginalBanknote = new StoOriginalBanknote();
				stoOriginalBanknote.setId(itemMap.get(Parameter.BOX_NO_KEY).toString());
				stoOriginalBanknote.setHoffice(office);
				stoOriginalBanknote.setRecoverBy(user);
				stoOriginalBanknote.setRecoverName(userName);
				stoOriginalBanknote.setRecoverDate(date);
				stoOriginalBanknote.setRoffice(rOffice);
				// 创建出库回收状态
				stoOriginalBanknote.setRecoverStatus(ExternalConstant.RecoverStatus.IS_RECOVER);

				stoOriginalBanknoteService.update(stoOriginalBanknote);
			}
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} else {
			// 　回收失败，存在不正确箱子
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "上传失败，原封箱不属于当前机构或状态不正确:" + Collections3.convertToString(erroList, ","));
		}

		return gson.toJson(map);
	}

}
