package com.coffer.businesses.modules.doorOrder.app.v01.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.coffer.businesses.modules.doorOrder.app.v01.service.UserInfoService;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Maps;

@Controller
@RequestMapping(value = "${adminPath}/doorOrder/app/v01/userInfoApp")
public class UserInfoController extends BaseController {

    @Autowired
    private SystemService systemService;
    @Autowired
    private UserInfoService userInfoService;

    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getUser(id);
        } else {
            return new User();
        }
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public Map<String, Object> update(@RequestParam(value = "userId") String userId,
                                      @RequestParam(value = "userName") String userName,
                                      @RequestParam(value = "oldPassword") String oldPassword,
                                      @RequestParam(value = "newPassword") String newPassword) {
        Map<String, Object> jsonData = Maps.newHashMap();
        /*
         * 1.判断用户
         */
        User user = UserUtils.get(userId);
        if (user == null) {
            jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            jsonData.put(Parameter.ERROR_MSG_KEY, "用户名不存在");
            return jsonData;
        }
        /*
         * 2.update用户信息（用户名和密码）
         */
        jsonData = userInfoService.update(userId, userName, oldPassword, newPassword);

        return jsonData;
    }

}
