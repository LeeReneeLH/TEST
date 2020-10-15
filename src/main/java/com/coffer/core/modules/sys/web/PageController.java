package com.coffer.core.modules.sys.web;

import com.coffer.core.common.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author yinkai
 * @data 2020年4月7日
 * <p>
 * 统一页面跳转controller
 * </p>
 */
@Controller
@RequestMapping(value = "${adminPath}/page")
public class PageController extends BaseController {

    /**
     * 需要左侧显示机构树的，统一跳转到此路径
     * @param menuName 菜单路径，在数据库中配置
     * @param model 菜单路径放到model，在officeTree中通过el表达式获取，页面中从iframe打开列表页
     * @return
     */
    @RequestMapping(value = "/a")
    public String page(@RequestParam(value = "menuname") String menuName, Model model) {
        model.addAttribute("menuname", menuName);
        if(menuName.equals("/sys/user/")) {
            // 用户管理菜单默认展示所有机构
            model.addAttribute("isAll", true);						/*2020-05-08 hzy  注释掉商户账务列表  机构树显示门店*/
        }else if((menuName.equals("/doorOrder/v01/saveType/list") /*||(menuName.equals("/doorOrder/v01/guestAccounts/merchantlist"))*/)){
        	model.addAttribute("isAll", false);
        	model.addAttribute("isNotType", "8");//不要门店
        }else {
            model.addAttribute("isAll", false);
        }
        return "/modules/doorOrder/v01/officeTree";
    }
}
