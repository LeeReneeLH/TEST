package com.coffer.external.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service69
* <p>Description: 库区上缴物品RFID查询接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service69")
@Scope("singleton")
public class Service69 extends HardwardBaseService {

	@Autowired
    private StoGoodsLocationInfoService goodsLocationService;
	
	/**
     * 
     * Title: findInStoreGoodsInfo
     * <p>Description: 69 库区上缴物品RFID查询接口</p>
     * @author:     wangbaozhong
     * @param paramMap
     * @return 
     * String    返回类型
     */
	@Override
	public String execute(Map<String, Object> paramMap) {
		logger.debug("69库区上缴物品RFID查询接口 -----------------开始");
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
        StoGoodsLocationInfo locationInfoCondition = new StoGoodsLocationInfo();
        locationInfoCondition.setOfficeId(officeId);
        
        List<StoGoodsLocationInfo> infoList = goodsLocationService.findList(locationInfoCondition);
        
        List<Map<String, Object>> goodsInfoInfoMapList = Lists.newArrayList();
        
        for (StoGoodsLocationInfo info : infoList) {
        	// 0：未使用，1：预订 2：已使用 3: rfid卡替换
        	if (!Constant.deleteFlag.Invalid.equals(info.getDelFlag()) 
        			&& !Constant.deleteFlag.Valid.equals(info.getDelFlag())) {
        		continue;
        	}
        	if (StringUtils.isBlank(info.getInStoreAllId())) {
        		continue;
        	}
        	Map<String, Object> infoMap = Maps.newHashMap();
        	//rfid
        	infoMap.put(Parameter.RFID_KEY, info.getRfid());
        	//使用状态
        	infoMap.put(Parameter.DEL_FLAG_KEY, info.getDelFlag());
        	// 物品ID
        	infoMap.put(Parameter.GOODS_ID_KEY, info.getGoodsId());
        	
        	goodsInfoInfoMapList.add(infoMap);
        	
        }
        respMap.put(Parameter.LIST_KEY, goodsInfoInfoMapList);
        respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
        
        logger.debug("69 库区上缴物品RFID查询接口 -----------------结束");
        return gson.toJson(respMap);
	}

}
