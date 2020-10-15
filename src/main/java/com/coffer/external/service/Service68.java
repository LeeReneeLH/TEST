package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.service.StoAreaSettingInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service68
* <p>Description: 库区设定信息查询接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service68")
@Scope("singleton")
public class Service68 extends HardwardBaseService {
	
	@Autowired
    private StoAreaSettingInfoService areaSettingInfoService;
	
	/**
     * 
     * Title: findAreaSettingInfoList
     * <p>Description: 68 库区设定信息查询接口</p>
     * @author:     wangbaozhong
     * @param paramMap
     * @return 
     * String    返回类型
     */
	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug("68库区设定信息查询接口 -----------------开始");
        Map<String, Object> respMap = new HashMap<String, Object>();
        // 版本号、服务代码
    	respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
    	respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
        
        String officeId = "";
        // 金库机构ID
        if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)))) {
            logger.warn("输入参数错误：" + Parameter.OFFICE_ID_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        } else {
        	officeId = StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY));
        }
        
        List<StoAreaSettingInfo> infoList = areaSettingInfoService.findListByOfficeId(officeId);
        if (infoList.size() == 0) {
        	 logger.warn("68 库区设定信息查询接口 -------------库区信息不存在 ！");
             respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
             respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E24);
             return gson.toJson(respMap);
        }
        List<Map<String, Object>> settingInfoMapList = Lists.newArrayList();
        for (StoAreaSettingInfo areaSettingInfo : infoList) {
        	if (StringUtils.isBlank(areaSettingInfo.getGoodsId()) || Constant.deleteFlag.Invalid.equals(areaSettingInfo.getDelFlag())) {
        		continue;
        	}
        	Map<String, Object> infoMap = Maps.newHashMap();
        	//所属机构ID
        	infoMap.put(Parameter.OFFICE_ID_KEY, areaSettingInfo.getOfficeId());
        	//库区ID
        	infoMap.put(Parameter.AREA_ID_KEY, areaSettingInfo.getId());
        	//库房区域名称
        	infoMap.put(Parameter.STORE_AREA_NAME_KEY, areaSettingInfo.getStoreAreaName());
        	//库区类型
        	infoMap.put(Parameter.STORE_AREA_TYPE_KEY, areaSettingInfo.getStoreAreaType());
        	// 库区类型名称
        	String strStoreAreaTypeName = DictUtils.getDictLabel(areaSettingInfo.getStoreAreaType(), "store_area_type", "");
        	infoMap.put(Parameter.STORE_AREA_TYPE_NAME_KEY, strStoreAreaTypeName);
        	//物品编号
        	infoMap.put(Parameter.GOODS_ID_KEY, areaSettingInfo.getGoodsId());
        	//物品名称
        	infoMap.put(Parameter.GOODS_NAME_KEY, StoreCommonUtils.getGoodsNameById(areaSettingInfo.getGoodsId()));
        	//排序
        	infoMap.put(Parameter.SORT_KEY, areaSettingInfo.getSortKey());
        	settingInfoMapList.add(infoMap);
        }
        respMap.put(Parameter.LIST_KEY, settingInfoMapList);
        respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
        logger.debug("68 库区设定信息查询接口 -----------------结束");
        
        return gson.toJson(respMap);
	}

}
