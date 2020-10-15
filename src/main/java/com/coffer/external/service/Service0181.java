package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

@Component("Service0181")
@Scope("singleton")
public class Service0181 extends HardwardBaseService {

	@Autowired
	private StoBoxInfoService stoBoxInfoService;

	/**
	 * 
	 * @author xp PDA网点更换机构
	 * @version 2017-07-07
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// try {
		// 创建箱袋基本信息对象
		StoBoxInfo stoBoxInfo = new StoBoxInfo();
		// 解析所属机构编号
		if (paramMap.get(Parameter.OFFICE_ID_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "机构编号不能为空！");
			return gson.toJson(map);
		}
		// 解析箱袋编号
		if (paramMap.get(Parameter.BOX_NO_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.BOX_NO_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "箱袋编号不能为空！");
			return gson.toJson(map);
		}
		// 解析Rfid锁号
		if (paramMap.get(Parameter.RFID_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.RFID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "Rfid锁号不能为空！");
			return gson.toJson(map);
		}
		// 解析箱袋类型
		if (paramMap.get(Parameter.BOX_TYPE_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.BOX_TYPE_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "箱袋类型不能为空！");
			return gson.toJson(map);
		}
		// 解析用户编号
		if (paramMap.get(Parameter.USER_ID_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.USER_ID_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "用户ID不能为空！");
			return gson.toJson(map);
		}
		// 解析系统登录用户姓名
		if (paramMap.get(Parameter.USER_NAME_KEY) == null
				|| StringUtils.isEmpty(paramMap.get(Parameter.USER_NAME_KEY).toString())) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "用户姓名不能为空！");
			return gson.toJson(map);
		}
		// 插入所属机构编号
		Office office = new Office();
		office.setId(paramMap.get(Parameter.OFFICE_ID_KEY).toString());
		stoBoxInfo.setOffice(office);
		// 插入箱袋编号
		stoBoxInfo.setId(paramMap.get(Parameter.BOX_NO_KEY).toString());
		// 插入rfid锁号
		stoBoxInfo.setRfid(paramMap.get(Parameter.RFID_KEY).toString());
		// 插入箱袋类型
		stoBoxInfo.setBoxType(paramMap.get(Parameter.BOX_TYPE_KEY).toString());
		// 插入用户编号
		User updateBy = new User();
		updateBy.setId(paramMap.get(Parameter.USER_ID_KEY).toString());
		stoBoxInfo.setUpdateBy(updateBy);
		stoBoxInfo.setCreateBy(updateBy);
		// 插入系统登录用户姓名
		stoBoxInfo.setUpdateName(paramMap.get(Parameter.USER_NAME_KEY).toString());
		stoBoxInfo.setCreateName(paramMap.get(Parameter.USER_NAME_KEY).toString());
		// 插入系统当前时间
		stoBoxInfo.setUpdateDate(new Date());
		stoBoxInfo.setCreateDate(new Date());
		// 将箱子有效标识改为0
		stoBoxInfo.setDelFlag(Constant.deleteFlag.Valid);
		// 查询箱袋信息
		List<StoBoxInfo> boxList = stoBoxInfoService.getBoxInfo(stoBoxInfo);
		// 判断箱袋状态
		if (Collections3.isEmpty(boxList)) {
			// 箱袋编号不存在，直接插入
			stoBoxInfo.setBoxStatus(Constant.BoxStatus.EMPTY);
			stoBoxInfoService.saveOrUpdate(stoBoxInfo);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} else if (1 == boxList.size()) {
			StoBoxInfo singleboxInfo = Collections3.getFirst(boxList);
			// 查询到一条数据，判断箱子的使用状态，空箱才可以更换机构
			if (singleboxInfo.getBoxStatus().equals(Constant.BoxStatus.EMPTY)) {
				stoBoxInfoService.saveOrUpdate(stoBoxInfo);
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				// 箱子状态不是空箱，直接报警
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY, "业务中的箱子，无法更换所属机构");
				return gson.toJson(map);
			}

		} else {
			// 查询到多条数据，直接报警
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
			map.put(Parameter.ERROR_MSG_KEY, "箱袋和rfid均被绑定，无法更换所属机构");
			return gson.toJson(map);
		}
		// } catch (Exception e) {
		// e.printStackTrace();
		// map.put(Parameter.RESULT_FLAG_KEY,
		// ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		// map.put(Parameter.ERROR_NO_KEY,
		// ExternalConstant.HardwareInterface.ERROR_NO_E02);
		// }
		return gson.toJson(map);

	}
}
