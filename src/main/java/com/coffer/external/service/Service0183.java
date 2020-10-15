package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;

@Component("Service0183")
@Scope("singleton")
public class Service0183 extends HardwardBaseService {

	@Autowired
	private StoBoxInfoService service;

	/**
	 * 
	 * @author liuyaowen PDA网点上缴同步接口
	 * @version 2017-07-11
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 取得参数
		String officeId = (String) paramMap.get(Parameter.OFFICE_ID_KEY);
		// 验证机构编号
		if (StringUtils.isBlank(officeId)) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			map.put(Parameter.ERROR_MSG_KEY, "机构编号不可以为空！");
			return gson.toJson(map);
		}
		try {
			StoBoxInfo stoBoxInfo = new StoBoxInfo();
			Office office = new Office();
			office.setId(officeId);
			stoBoxInfo.setOffice(office);
			stoBoxInfo.setDelFlag(Constant.deleteFlag.Valid);
			// 获取箱袋List
			List<StoBoxInfo> boxInfoList = service.findList(stoBoxInfo);
			List<Map<String, Object>> boxList = Lists.newArrayList();
			for (StoBoxInfo boxInfo : boxInfoList) {
				Map<String, Object> listmap = new HashMap<>();
				//在网点状态款箱
				if (AllocationConstant.Place.BankOutlets.equals(boxInfo.getBoxStatus())) {
					listmap.put(Parameter.BOX_NO_KEY, boxInfo.getId());
					listmap.put(Parameter.RFID_KEY, boxInfo.getRfid());
					listmap.put(Parameter.BOX_TYPE_KEY, boxInfo.getBoxType());
				//空箱并且是尾箱状态	
				} else if (AllocationConstant.Place.BlankBox.equals(boxInfo.getBoxStatus())
						&& Constant.BoxType.BOX_TAIL.equals(boxInfo.getBoxType())) {
					listmap.put(Parameter.BOX_NO_KEY, boxInfo.getId());
					listmap.put(Parameter.RFID_KEY, boxInfo.getRfid());
					listmap.put(Parameter.BOX_TYPE_KEY, boxInfo.getBoxType());
				} else {
					continue;
				}

				boxList.add(listmap);
			}
			map.put(Parameter.BOX_LIST_KEY, boxList);
			map.put(Parameter.OFFICE_ID_KEY, officeId);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}

}
