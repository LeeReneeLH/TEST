package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
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
* Title: Service47
* <p>Description: 人行同步库区信息</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service47")
@Scope("singleton")
public class Service47 extends HardwardBaseService {

	@Autowired
    private StoAreaSettingInfoService areaSettingInfoService;
	
	 /**
     * 人行同步库区信息
     * 
     * @author chengshu
     * @version 2016年5月20日
     * 
     * @param paramMap
     *            参数列表
     * @return 返回值列表
     */
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> respMap = new HashMap<String, Object>();
        // 版本号、服务代码
        respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
        respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

        // 查询条件初始化
        StoAreaSettingInfo areaSettingInfo = new StoAreaSettingInfo();

        // 金库机构ID
        if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)))) {
            logger.warn("输入参数错误：" + Parameter.OFFICE_ID_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        } else {
            areaSettingInfo.setOfficeId(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)));
        }

        // 执行查询处理
        List<StoAreaSettingInfo> areaSeetingInfoList = areaSettingInfoService.findList(areaSettingInfo);

        // 组装返回库区信息
        List<Map<String, String>> areaList = Lists.newArrayList();
        Map<String, String> areaMap = Maps.newHashMap();
        for (StoAreaSettingInfo areaInfo : areaSeetingInfoList) {
            areaMap = Maps.newHashMap();

            // 过滤无效
            if(Constant.deleteFlag.Invalid.equals(areaInfo.getDelFlag())){
                continue;
            }
			// 库区类型名称
			String strStoreAreaTypeName = DictUtils.getDictLabel(areaInfo.getStoreAreaType(), "store_area_type", "");
			areaMap.put(Parameter.AREA_ID_KEY, areaInfo.getId());
			// 库区类型
            areaMap.put(Parameter.STORE_AREA_TYPE_KEY, areaInfo.getStoreAreaType());
            //库区类型名称
            areaMap.put(Parameter.STORE_AREA_TYPE_NAME_KEY, strStoreAreaTypeName);
            // 库区类型名称 + " " + 库区名称
            areaMap.put(Parameter.AREA_NAME_KEY, strStoreAreaTypeName + " " + areaInfo.getStoreAreaName());

            areaList.add(areaMap);
        }

        // 返回信息添加库区信息
        respMap.put(Parameter.AREA_LIST_KEY, areaList);

        // 处理成功
        respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

        return gson.toJson(respMap);
	}

}
