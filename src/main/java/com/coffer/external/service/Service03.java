package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
 * Title: Service03
 * <p>
 * Description: 款（钞）箱（袋）与RFID绑定接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:35:24
 */
@Component("Service03")
@Scope("singleton")
public class Service03 extends HardwardBaseService {

	@Autowired
	private StoBoxInfoService boxService;

	/**
	 * 
	 * @author LF 箱袋绑定RFID接口
	 * @version 2015-06-04
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 箱袋号
		String boxNo = StringUtils.toString(paramMap.get(Parameter.BOX_NO_KEY));
		// rfid
		String rfid = StringUtils.toString(paramMap.get(Parameter.RFID_KEY));
		// 用户编号
		String userId = StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
		// 系统登录用户姓名
		String userName = StringUtils.toString(paramMap.get(Parameter.USER_NAME_KEY));
		// 判断箱袋号和rfid是否都为空
		if (StringUtils.isBlank(boxNo) && StringUtils.isBlank(rfid)) {
			logger.debug("参数错误--------boxNo:" + CommonUtils.toString(paramMap.get(Parameter.BOX_NO_KEY)) + "rfid:"
					+ CommonUtils.toString(paramMap.get(Parameter.RFID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------boxNo,rfid");
			return gson.toJson(map);
		}
		StoBoxInfo box = new StoBoxInfo();
		// 设置箱号
		box.setId(boxNo);
		// 设置RFID
		box.setRfid(rfid);
		// 设置更新人信息
		User loginUser = UserUtils.get(userId);
		if (loginUser != null) {
			box.setUpdateBy(loginUser);
		} else {
			logger.debug("参数错误--------userId:" + CommonUtils.toString(paramMap.get(Parameter.USER_ID_KEY)));
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "参数错误--------userId");
			return gson.toJson(map);
		}
		// 设置更新日期
		box.setUpdateDate(new Date());
		// 设置更新人姓名
		box.setUpdateName(userName);
		// 更新当前箱袋绑定RFID
		boxService.updateBoxDelflags(box);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(map);
	}

}
