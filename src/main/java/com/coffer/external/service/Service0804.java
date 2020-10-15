package com.coffer.external.service;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Title: Service0804
 * <p>
 * Description:机具状态维护功能接口
 * </p>
 *
 * @author yinkai
 * @date 2019.7.10
 */
@Component("Service0804")
@Scope("singleton")
public class Service0804 extends HardwardBaseService {

    @Autowired
    private EquipmentInfoDao equipmentInfoDao;

    @Override
    @Transactional
    public String execute(Map<String, Object> paramMap) {
        // 检查参数
        String checkParamMsg = checkParam(paramMap);
        if (checkParamMsg != null) {
            throw new BusinessException(ExternalConstant.HardwareInterface.ERROR_NO_E03, checkParamMsg);
        }
        // 提取参数
        String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
        String eqpId = (String) paramMap.get(Parameter.EQUIPMENT_ID_KEY);
        String seriesNumber = (String) paramMap.get(Parameter.SERIES_NUMBER_KEY);
        String eqpType = (String) paramMap.get(Parameter.TYPE_KEY);
        String IP = (String) paramMap.get(Parameter.IP_KEY);
        String connStatus = (String) paramMap.get(Parameter.CONN_STATUS_KEY);
        String loginName = (String) paramMap.get(Parameter.USER_KEY);
        EquipmentInfo equipmentInfo = new EquipmentInfo();
        equipmentInfo.setId(eqpId);
        equipmentInfo.setName(eqpId);
        equipmentInfo.setSeriesNumber(seriesNumber);
        equipmentInfo.setType(eqpType);
        equipmentInfo.setIP(IP);
        equipmentInfo.setConnStatus(connStatus);
        // 根据上传的用户登录名判断设备归属管理中心vinOffice
        User user = UserUtils.getByLoginName(loginName);
        if(user != null) {
            Office office = user.getOffice();
            if(office != null && Constant.OfficeType.CLEAR_CENTER.equals(office.getType())) {
                equipmentInfo.setVinOffice(office);
            }
        }
        // 表中没有此设备相关记录则新增，有则更新
        if (equipmentInfoDao.get(eqpId) == null) {
            equipmentInfo.setStatus(DoorOrderConstant.EquipmentStatus.NOBIND);
            equipmentInfo.setCreateBy(user);
            equipmentInfo.setUpdateBy(user);
            equipmentInfo.setCreateDate(new Date());
            equipmentInfo.setUpdateDate(new Date());
            equipmentInfoDao.insert(equipmentInfo);
        } else {
            equipmentInfo.setUpdateBy(user);
            equipmentInfo.setUpdateDate(new Date());
            equipmentInfoDao.updateByCondition(equipmentInfo);
        }
        Map<String,Object> map = new HashMap<>();
        map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
        map.put(Parameter.ERROR_NO_KEY, null);
        map.put(Parameter.ERROR_MSG_KEY, null);
        return setReturnMap(map, serviceNo);
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
        if (paramMap.get(Parameter.SERIES_NUMBER_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.SERIES_NUMBER_KEY))) {
            logger.debug("参数错误--------" + Parameter.SERIES_NUMBER_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.SERIES_NUMBER_KEY)));
            errorMsg = "参数错误--------" + Parameter.SERIES_NUMBER_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.SERIES_NUMBER_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.IP_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.IP_KEY))) {
            logger.debug("参数错误--------" + Parameter.IP_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.IP_KEY)));
            errorMsg = "参数错误--------" + Parameter.IP_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.IP_KEY));
            return errorMsg;
        }
        if (paramMap.get(Parameter.CONN_STATUS_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.CONN_STATUS_KEY))) {
            logger.debug("参数错误--------" + Parameter.CONN_STATUS_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.CONN_STATUS_KEY)));
            errorMsg = "参数错误--------" + Parameter.CONN_STATUS_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.CONN_STATUS_KEY));
            return errorMsg;
        } else {
            // 连线状态字典校验
            String value = (String) paramMap.get(Parameter.CONN_STATUS_KEY);
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
