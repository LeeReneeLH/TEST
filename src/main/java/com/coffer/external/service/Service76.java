package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoreCoOfficeAssocation;
import com.coffer.businesses.modules.store.v01.entity.StoreManagementInfo;
import com.coffer.businesses.modules.store.v01.entity.StoreManagerAssocation;
import com.coffer.businesses.modules.store.v01.entity.StoreTypeAssocation;
import com.coffer.businesses.modules.store.v01.service.StoreManagementInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service76
 * <p>
 * Description: 库房管理基础数据同步接口(PDA)
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年8月10日 上午09:01:10
 */
@Component("Service76")
@Scope("singleton")
public class Service76 extends HardwardBaseService {

	@Autowired
	private StoreManagementInfoService storeManagementInfoService;
	
	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug(this.getClass().getName() + "库房管理基础数据同步接口(PDA) -----------------开始");
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		try {

			if (paramMap.get(Parameter.OFFICE_ID_KEY) == null
					|| StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				return gson.toJson(respMap);
			}
			StoreManagementInfo param = new StoreManagementInfo();
			Office paramOffice = new Office();
			paramOffice.setId(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY).toString()));
			param.setOffice(paramOffice);
			param.setDelFlag(Constant.deleteFlag.Valid);
			
			// 查询库房信息列表
			List<StoreManagementInfo> infoList = storeManagementInfoService.findList(param);
			
			Map<String, Object> storeMap = null;
			Map<String, Object> innerMap = null;
			List<Map<String, Object>> rtnList = Lists.newArrayList();
			List<Map<String, Object>> innerList = null;
			for (StoreManagementInfo info : infoList) {
				storeMap = Maps.newHashMap();
				//库房ID 
				storeMap.put(Parameter.STORE_ID_KEY, info.getId());
				//库房名称
				storeMap.put(Parameter.STORE_NAME_KEY, info.getStoreName());
				// 所属机构ID
				storeMap.put(Parameter.OFFICE_ID_KEY, info.getOffice().getId());
				// 有效状态
				storeMap.put(Parameter.DEL_FLAG_KEY, info.getDelFlag());
				// 业务类型列表
				innerList = Lists.newArrayList();
				for (StoreTypeAssocation typeEntity :  info.getStoreTypeAssocationList()) {
					innerMap = Maps.newHashMap();
					innerMap.put(Parameter.BOX_TYPE_KEY, typeEntity.getStorageType());
					innerMap.put(Parameter.BOX_TYPE_NAME_KEY, DictUtils.getDictLabel(typeEntity.getStorageType(), "sto_box_type", ""));
					innerList.add(innerMap);
				}
				storeMap.put(Parameter.TYPE_LIST_KEY, innerList);
				//库管员列表
				innerList = Lists.newArrayList();
				for (StoreManagerAssocation managerEntity : info.getStoreManagerAssocationList()) {
					innerMap = Maps.newHashMap();
					innerMap.put(Parameter.USER_ID_KEY, managerEntity.getUser().getId());
					innerMap.put(Parameter.USER_NAME_KEY, managerEntity.getUser().getName());
					innerList.add(innerMap);
				}
				storeMap.put(Parameter.USER_LIST_KEY, innerList);
				// 物品所属机构列表
				innerList = Lists.newArrayList();
				for (StoreCoOfficeAssocation coOfficeEntity : info.getStoreCoOfficeAssocationList()) {
					innerMap = Maps.newHashMap();
					innerMap.put(Parameter.OFFICE_ID_KEY, coOfficeEntity.getOffice().getId());
					innerMap.put(Parameter.OFFICE_NAME_KEY, coOfficeEntity.getOffice().getName());
					innerList.add(innerMap);
				}
				storeMap.put(Parameter.OFFICE_LIST_KEY, innerList);
				
				rtnList.add(storeMap);
			}
			
			respMap.put(Parameter.LIST_KEY, rtnList);
			
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}
		logger.debug(this.getClass().getName() + "库房管理基础数据同步接口(PDA) -----------------结束");
		return gson.toJson(respMap);
	}
	

}
