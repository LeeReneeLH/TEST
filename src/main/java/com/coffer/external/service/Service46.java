package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;

/**
* Title: Service46
* <p>Description: 人行库区变更</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service46")
@Scope("singleton")
public class Service46 extends HardwardBaseService {
	
	@Autowired
    private StoGoodsLocationInfoService goodsLocationService;
	
	/**
     * 人行库区变更
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

        StoGoodsLocationInfo goodsLocationInfo = new StoGoodsLocationInfo();
        User user = new User();

        // 用户ID
        if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY)))) {
            logger.warn("输入参数错误：" + Parameter.USER_ID_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        } else {
            user.setId(StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY)));
        }

        // 用户名称
        if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.USER_NAME_KEY)))) {
            logger.warn("输入参数错误：" + Parameter.USER_NAME_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        } else {
            user.setName(StringUtils.toString(paramMap.get(Parameter.USER_NAME_KEY)));
        }

        // 更新者信息
        goodsLocationInfo.setUpdateBy(user);

        // 金库机构ID
        if (StringUtils.isBlank(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)))) {
            logger.warn("输入参数错误：" + Parameter.OFFICE_ID_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        } else {
            goodsLocationInfo.setOfficeId(StringUtils.toString(paramMap.get(Parameter.OFFICE_ID_KEY)));
        }

        // 箱袋列表
        @SuppressWarnings("unchecked")
        List<Map<String, String>> rfidList = (List<Map<String, String>>) paramMap.get(Parameter.BOX_LIST_KEY);
        if (rfidList == null || rfidList.size() == 0) {
            logger.warn("输入参数错误：" + Parameter.BOX_LIST_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        }

        // 更新RFID库区位置
        StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
        for (Map<String, String> rfidInfo : rfidList) {
            if (rfidInfo == null) {
                break;
            }
            stoGoodsLocationInfo = new StoGoodsLocationInfo();
            stoGoodsLocationInfo.setRfid(StringUtils.toString(rfidInfo.get(Parameter.STORE_ESCORT_RFID)));
            stoGoodsLocationInfo.setStoreAreaId(StringUtils.toString(rfidInfo.get(Parameter.AREA_ID_KEY)));
            stoGoodsLocationInfo.setUpdateBy(user);
            stoGoodsLocationInfo.setUpdateDate(new Date());
            stoGoodsLocationInfo.setOfficeId(goodsLocationInfo.getOfficeId());

            // 执行更新处理
            goodsLocationService.updateRfidStoreArea(stoGoodsLocationInfo);
        }

        // 处理成功
        respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

        return gson.toJson(respMap);
	}

}
