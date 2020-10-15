package com.coffer.businesses.common.interceptor;

import com.coffer.businesses.modules.doorOrder.app.v01.utils.JSONUtils;
import com.coffer.businesses.modules.doorOrder.app.v01.utils.TokenUtils;
import com.coffer.businesses.modules.weChat.v03.dao.GuestDao;
import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序接口拦截器，用于验证小程序用户信息是否在后台修改
 *
 * @author yinkai
 * @date
 */
public class MiniProgramInterceptor extends BaseService implements HandlerInterceptor {

    @Autowired
    private GuestDao guestDao;

    @Override
    @Transactional(readOnly = false)
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Object> rtnMap = new HashMap<>();
        PrintWriter writer = response.getWriter();
        // 用户在后台被解绑
        String token = request.getHeader("token");
        if (token == null) {
            // 小程序必要token的接口
            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E57);
            rtnMap.put(Parameter.ERROR_MSG_KEY, "token lost");
            writer.write(JSONUtils.gson.toJson(rtnMap));
            return false;
        }
        Claims verify = TokenUtils.verify(token);
        if (verify == null) {
            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E57);
            rtnMap.put(Parameter.ERROR_MSG_KEY, "登录失效，请重新登录");
            writer.write(JSONUtils.gson.toJson(rtnMap));
            return false;
        }
        Object unionId = verify.get("unionId");
        if (unionId == null) {
            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E57);
            rtnMap.put(Parameter.ERROR_MSG_KEY, "登录失效，请重新登录");
            writer.write(JSONUtils.gson.toJson(rtnMap));
            return false;
        }
        Guest guest = new Guest();
        guest.setUnionId(unionId.toString());
        guest = guestDao.getByUnionID(guest);
        if (guest == null) {
            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E57);
            rtnMap.put(Parameter.ERROR_MSG_KEY, "用户已解绑，请重新绑定");
            writer.write(JSONUtils.gson.toJson(rtnMap));
            return false;
        }
        User user = UserUtils.get(guest.getId());
        if (user == null) {
            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E57);
            rtnMap.put(Parameter.ERROR_MSG_KEY, "用户信息失效，请联系管理员（userId：" + guest.getId() + ")");
            writer.write(JSONUtils.gson.toJson(rtnMap));
            return false;
        }
        String version = request.getHeader("version");
        if (version == null) {
            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E57);
            rtnMap.put(Parameter.ERROR_MSG_KEY, "登录失效，请重新登录");
            writer.write(JSONUtils.gson.toJson(rtnMap));
            return false;
        }
        // 用户登录名、密码、身份证号在后台被修改，update日期会变
        int fromVersion = Integer.parseInt(version);
        int currentVersion = user.getVersion();
        if (fromVersion != currentVersion) {
            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E57);
            rtnMap.put(Parameter.ERROR_MSG_KEY, "登录名或密码已被修改，请重新绑定或联系管理员");
            // 后台修改过信息，直接解绑
            guest.setUnionId(null);
            guestDao.update(guest);
            writer.write(JSONUtils.gson.toJson(rtnMap));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
