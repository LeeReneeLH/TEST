package com.coffer.external.service;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.SaveTypeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: Service0807
 * <p>
 * Description:开机同步接口
 * </p>
 *
 * @author yinkai
 * @date 2019.7.23
 */
@Component("Service0807")
@Scope("singleton")
public class Service0807 extends HardwardBaseService {

    @Autowired
    private EquipmentInfoDao equipmentInfoDao;

    @Autowired
    private OfficeDao officeDao;

    @Autowired
    private SaveTypeDao saveTypeDao;

    @Override
    @Transactional
    public String execute(Map<String, Object> paramMap) {
        String eqpId = (String) paramMap.get(Parameter.EQUIPMENT_ID_KEY);
        String status = (String) paramMap.get(Parameter.STATUS_KEY);
        String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
        // 检查参数
        String checkParamMsg = checkParam(paramMap);
        if (checkParamMsg != null) {
            throw new BusinessException("E99", checkParamMsg);
        }
        EquipmentInfo equipmentInfo = equipmentInfoDao.get(eqpId);
        if (equipmentInfo == null) {
            throw new BusinessException("E99", "设备信息有误");
        }
        // 保存设备状态
        equipmentInfo.setConnStatus(status);
        equipmentInfoDao.update(equipmentInfo);
        // 返回系统时间
        Long serverTime = System.currentTimeMillis();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(Parameter.SERVER_TIME_KEY, serverTime);
        // 查询设备所属商户
        Office merchantByEqpId = officeDao.getMerchantByEqpId(equipmentInfo.getId());
        if (merchantByEqpId == null) {
            throw new BusinessException("E99", "设备未绑定门店");
        }
        // 查询商户存款类型
        SaveType saveType = new SaveType();
        saveType.setMerchantId(merchantByEqpId.getId());
        List<SaveType> typeList = saveTypeDao.findList(saveType);
        if (Collections3.isEmpty(typeList)) {
            throw new BusinessException("E99", "该商户没有设定存款类型");
        }
        // 存款类型列表
        List<Map<String, Object>> businessType = Lists.newArrayList();
        for (SaveType type : typeList) {
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("typeNo", type.getTypeCode());
            typeMap.put("typeName", type.getTypeName());
            businessType.add(typeMap);
        }
        resultMap.put("businessType", businessType);
        resultMap.put(Parameter.ERROR_NO_KEY, null);
        resultMap.put(Parameter.ERROR_MSG_KEY, null);
        resultMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
        return setReturnMap(resultMap, serviceNo);
    }

    private String checkParam(Map<String, Object> paramMap) {
        String errorMsg;
        // 字符串空校验
        if (paramMap.get(Parameter.EQUIPMENT_ID_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.EQUIPMENT_ID_KEY))) {
            logger.debug("参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY)));
            errorMsg = "参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.STATUS_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.STATUS_KEY))) {
            logger.debug("参数错误--------" + Parameter.STATUS_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.STATUS_KEY)));
            errorMsg = "参数错误--------" + Parameter.STATUS_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.STATUS_KEY));
            return errorMsg;
        } else {
            // 连线状态字典校验
            String value = (String) paramMap.get(Parameter.STATUS_KEY);
            String connStatus = DictUtils.getDictLabel(value, "CONN_STATUS", null);
            if (connStatus == null) {
                logger.debug("参数错误--------连线状态不正确：" + value);
                errorMsg = "参数错误--------连线状态不正确：" + value;
                return errorMsg;
            }
        }
        return null;
    }

    private String setReturnMap(Map<String, Object> map, String serviceNo) {
        map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
        map.put(Parameter.SERVICE_NO_KEY, serviceNo);
        return gson.toJson(map);
    }
}
