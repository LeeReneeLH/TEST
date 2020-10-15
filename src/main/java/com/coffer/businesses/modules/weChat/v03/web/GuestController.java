package com.coffer.businesses.modules.weChat.v03.web;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.businesses.modules.weChat.v03.service.GuestService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * 客户授权Controller
 * @author wanglin
 * @version 2017-04-18
 */
@Controller
@RequestMapping(value = "${adminPath}/weChat/v03/Guest")
public class GuestController extends BaseController {

	@Autowired
	private GuestService GuestService;
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public Guest get(@RequestParam(required=false) String id) {
		Guest entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = GuestService.get(id);
		}
		if (entity == null){
			entity = new Guest();
		}
		return entity;
	}
	
	@RequiresPermissions("guest:Guest:view")
	@RequestMapping(value = {"list", ""})
	public String list(Guest Guest, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Guest> page = GuestService.findPage(new Page<Guest>(request, response), Guest); 
		model.addAttribute("page", page);
		return "modules/weChat/v03/guest/guestList";

	}

	@RequiresPermissions("guest:Guest:view")
	@RequestMapping(value = "form")
	public String form(Guest Guest, Model model) {
		
		Date currentTime = new Date();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    //获取当前系统时间
	    String dateString = formatter.format(currentTime);
	    ParsePosition pos1 = new ParsePosition(0);
	    Date currentTime_1 = formatter.parse(dateString, pos1);
		model.addAttribute("sysdate",currentTime_1);
		//获取当前系统时间的后一天，用于授权，授权期限最短为系统时间的后一天
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        Date tomorrow = calendar.getTime();
	    String dateString1 = formatter.format(tomorrow);
	    ParsePosition pos = new ParsePosition(0);
	    Date currentTime_2 = formatter.parse(dateString1, pos);
		//将系统时间的后一天传到前台，作为授权期限的最小值
		model.addAttribute("systomorrow",currentTime_2);
		
		model.addAttribute("Guest", Guest);
		return "modules/weChat/v03/guest/guestForm";
	}

	@RequiresPermissions("guest:Guest:edit")
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public String save(Guest Guest, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, Guest)){
			return form(Guest, model);
		}
		Locale locale = LocaleContextHolder.getLocale();
		// 设置机构名 修改人：XL 2018-05-23 begin
		Office office = officeService.get(Guest.getGofficeId());
		Guest.setGofficeName(office.getName());
		// end
		Guest.setGrantstatus(WeChatConstant.Weixintype.TYPE_ACCESS);
		//判断进行的操作是授权操作还是PC端修改信息
		if(!WeChatConstant.Weixintype.TYPE_EXCEPTION.equals(Guest.getGrantstatus())
				&&!WeChatConstant.MethodType.METHOD_WECHAT.equals(Guest.getMethod())){	
			Guest.setMethod(WeChatConstant.MethodType.METHOD_PC);
			GuestService.save(Guest);
			// 保存客户成功
			String message = msg.getMessage("message.I7202", null, locale);
			addMessage(redirectAttributes, message);
		}else{
			GuestService.save(Guest);
			// 保存授权成功
			String message = msg.getMessage("message.I7203", null, locale);
			addMessage(redirectAttributes, message);
		}
		return "redirect:"+Global.getAdminPath()+"/weChat/v03/Guest/?repage";
		
		
	}
	
	@RequiresPermissions("guest:Guest:edit")
	@RequestMapping(value = "delete")
	public String delete(Guest Guest, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		GuestService.delete(Guest);
		//删除成功
		message = msg.getMessage("message.I0002", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:"+Global.getAdminPath()+"/weChat/v03/Guest/?repage";
	}
	
	
	/**
	 * 查询微信标识是否唯一
	 * @author wanglin
	 * @version 2017-05-17
	 */
	@RequiresPermissions("guest:Guest:edit")
	@RequestMapping(value = "selectopenId")
	@ResponseBody
	public String selectopenId(@RequestParam(required=false) String openId,Model model) {
			Map<String,Object> map = Maps.newHashMap();
			Guest guest=new Guest();
			guest.setOpenId(openId);
			if(!"".equals(openId)){
				
			List<Guest> list=GuestService.findList(guest);
			if(!list.isEmpty()){
				map.put("success","123");
			}
			}
			//通过gson将map传递到前台
			return gson.toJson(map);
		}

	/**
	 * 返回上一级页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/weChat/v03/Guest/list?repage";
	}
	
	
	/**
	 * 根据类型获取机构列表
	 * 
	 * @version 2018-05-23
	 * @author XL
	 * @param param
	 * @param model
	 * @param redirectAttributes
	 */
	@RequestMapping(value = "changeBusType")
	@ResponseBody
	public String changeBankConnect(String param, Model model, RedirectAttributes redirectAttributes) {
		// 机构类型
		param = param.replace("&quot;", "");
		// 机构列表
		List<Office> list = null;
		// 返回列表
		List<Map<String, Object>> dataList = Lists.newArrayList();
		if (StringUtils.isNoneBlank(param)) {
			// 查询机构列表
			list = StoreCommonUtils.getStoCustList(param, true);
			if (!Collections3.isEmpty(list)) {
				for (Office item : list) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("label", item.getName());
					map.put("id", item.getId());
					dataList.add(map);
				}
			}
		}
		return gson.toJson(dataList);
	}
	
}