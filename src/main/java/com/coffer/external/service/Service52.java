package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.config.Global;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service52
* <p>Description: 原封新券出库接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service52")
@Scope("singleton")
public class Service52 extends HardwardBaseService {
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	
	/**
	 * 
	 * @author LLF
	 * @version 2016年6月1日
	 * 
	 * 
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
		Office office = officeService.get(paramMap.get(Parameter.OFFICE_ID_KEY).toString());
		User user = new User(paramMap.get(Parameter.USER_ID_KEY).toString());
		String userName = paramMap.get(Parameter.USER_NAME_KEY).toString();
		// 生成出库业务流水
		String busiType = Global.getConfig("businessType.pboc.original.banknote");
		String outId = BusinessUtils.getNewBusinessNo(busiType, office);
		
		for (Map<String, Object> itemMap : list) {
			// 由于出库箱袋查询接口已经校验编号正确性，此处不需要验证
			StoOriginalBanknote stoOriginalBanknote = new StoOriginalBanknote();
			stoOriginalBanknote.setId(itemMap.get(Parameter.BOX_NO_KEY).toString());
			stoOriginalBanknote.setOutId(outId);
			stoOriginalBanknote.setCoffice(office);
			stoOriginalBanknote.setOutBy(user);
			stoOriginalBanknote.setOutName(userName);
			stoOriginalBanknote.setOutDate(date);
			// 创建出库回收状态
			stoOriginalBanknote.setRecoverStatus(ExternalConstant.RecoverStatus.NO_RECOVER);
			stoOriginalBanknote.setRoffice(office);
			stoOriginalBanknoteService.update(stoOriginalBanknote);
		}
		// 成功结果
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		return gson.toJson(map);
	}

}
