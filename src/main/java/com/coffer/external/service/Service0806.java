package com.coffer.external.service;

import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearGroupMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.coffer.businesses.common.Constant.SysUserType.*;

/**
 * Title: Service0806
 * <p>
 * Description:机具操作人身份认证接口
 * </p>
 *
 * @author yinkai
 * @date 2019.7.19
 */
@Component("Service0806")
@Scope("singleton")
public class Service0806 extends HardwardBaseService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private OfficeDao officeDao;

    @Autowired
    private EquipmentInfoDao equipmentInfoDao;

    @Autowired
    private ClearGroupMainDao clearGroupMainDao;

    @Override
    @Transactional
    public String execute(Map<String, Object> paramMap) {
        String serviceNo = (String) paramMap.get(Parameter.SERVICE_NO_KEY);
        String loginName = (String) paramMap.get(Parameter.LOGIN_NAME_KEY);
        String password = (String) paramMap.get(Parameter.PASSWORD_KEY);
        String eqpId = (String) paramMap.get(Parameter.EQUIPMENT_ID_KEY);
        // 检查参数
        String checkParamMsg = checkParam(paramMap);
        if (checkParamMsg != null) {
            throw new BusinessException("E99", checkParamMsg);
        }
        User user = new User();
        user.setLoginName(loginName);
        User userByLoginName = userDao.getByLoginName(user);
        // 验证登录名
        if (userByLoginName == null) {
            throw new BusinessException("E99", "用户名或密码错误");
        }
        // 验证密码
        if (!password.equals(userByLoginName.getPassword())) {
            throw new BusinessException("E99", "用户名或密码错误");
        }
        // 验证用户和机构（设备所属机构）
        Office userOffice = userByLoginName.getOffice();
        EquipmentInfo equipmentInfo = equipmentInfoDao.get(eqpId);
        if (equipmentInfo == null) {
            throw new BusinessException("E99", "设备信息有误");
        }
        /*String validateMsg = validateUserAndOrg(userByLoginName, equipmentInfo);
        if (validateMsg != null) {
            throw new BusinessException("E99", validateMsg);
        }*/
        // 返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(Parameter.USER_ID_KEY, userByLoginName.getId());
        resultMap.put(Parameter.USER_NAME_KEY, userByLoginName.getName());
        resultMap.put(Parameter.OFFICE_ID_KEY, userOffice.getId());
        resultMap.put(Parameter.ID_CARD_NO_KEY, userByLoginName.getIdcardNo());
        resultMap.put(Parameter.MOBILE_KEY, userByLoginName.getMobile());
        resultMap.put(Parameter.USER_TYPE_KEY, userByLoginName.getUserType());

        resultMap.put(Parameter.ERROR_MSG_KEY, null);
        resultMap.put(Parameter.ERROR_NO_KEY, null);
        resultMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
        return setReturnMap(resultMap, serviceNo);
    }
    
    /**
     * 验证
     * @param wxUser
     * @param equipmentInfo
     * @return
     */
    public String wxExecute(User wxUser,EquipmentInfo equipmentInfo) {
        return this.validateUserAndOrg(wxUser, equipmentInfo);
    }

    private String validateUserAndOrg(User user, EquipmentInfo equipmentInfo) {
        // 中心相关用户，验证设备维护机构，其他用户（商户、门店）验证设备所属门店
        String errorMsg = null;
        String userType = user.getUserType();
        Office vinOffice = equipmentInfo.getVinOffice();
        Office aOffice = equipmentInfo.getaOffice();
        Office userOffice = user.getOffice();
        if (vinOffice == null || aOffice == null) {
            errorMsg = "设备未绑定机构";
            return errorMsg;
        }
        switch (userType) {
            case COMPANY_MANAGER:
            case ENTERPRISE_MANAGER:
            case CLEARING_CENTER_MANAGER:
            case CLEARING_CENTER_OPT:
                break;
            case CLEARING_CENTER_CLEAR_MAN:
                if (!userOffice.getId().equals(vinOffice.getId())) {
                    errorMsg = "用户所属机构与设备维护机构不符，登录失败";
                    break;
                }
                ClearGroupMain groupInfo = clearGroupMainDao.getGroupInfoByDoorIdAndUserId(user.getId(), aOffice.getId());
                errorMsg = groupInfo == null ? "人员" + user.getName() + "不可对当前设备清机" : null;
                break;
            case SHOP_TELLER:
                // 两步验证:1. 登录用户所属机构是否是当前门店，符合通过
                //         2. 登录用户所属机构是否包含在当前门店的所有父机构中，符合通过
                if (userOffice.getId().equals(aOffice.getId())) {
                    break;
                } else {
                    Office validateOffice = officeDao.validateDoorCompany(userOffice.getId(), equipmentInfo.getaOffice().getId());
                    errorMsg = validateOffice == null ? "人员" + user.getName() + "不可登录当前设备" : null;
                }
                break;
            default:
                errorMsg = "用户权限不足";
        }
        return errorMsg;
    }

    private String checkParam(Map<String, Object> paramMap) {
        String errorMsg = null;
        // 字符串空校验
        if (paramMap.get(Parameter.LOGIN_NAME_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.LOGIN_NAME_KEY))) {
            logger.debug("参数错误--------" + Parameter.LOGIN_NAME_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.LOGIN_NAME_KEY)));
            errorMsg = "参数错误--------" + Parameter.LOGIN_NAME_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.LOGIN_NAME_KEY));
            return errorMsg;
        }
        // 字符串空校验
        if (paramMap.get(Parameter.PASSWORD_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.PASSWORD_KEY))) {
            logger.debug("参数错误--------" + Parameter.PASSWORD_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.PASSWORD_KEY)));
            errorMsg = "参数错误--------" + Parameter.PASSWORD_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.PASSWORD_KEY));
            return errorMsg;
        }
        // 字符串空校验
        if (paramMap.get(Parameter.EQUIPMENT_ID_KEY) == null ||
                StringUtils.isEmpty((String) paramMap.get(Parameter.EQUIPMENT_ID_KEY))) {
            logger.debug("参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY)));
            errorMsg = "参数错误--------" + Parameter.EQUIPMENT_ID_KEY + ":" + CommonUtils.toString(paramMap.get(Parameter.EQUIPMENT_ID_KEY));
            return errorMsg;
        }
        return errorMsg;
    }

    private String setReturnMap(Map<String, Object> map, String serviceNo) {
        map.put(Parameter.VERSION_NO_KEY, ExternalConstant.HardwareInterface.VERSION_NO_01);
        map.put(Parameter.SERVICE_NO_KEY, serviceNo);
        return gson.toJson(map);
    }
}
