package com.coffer.core.modules.sys.web;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.SysOtp;
import com.coffer.core.modules.sys.entity.SysOtpOffice;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.service.SysOtpOfficeService;
import com.coffer.core.modules.sys.service.SysOtpService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * otp动态口令管理Controller
 * @author qph
 * @version 2018-07-02
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysOtp")
public class SysOtpController extends BaseController {

	@Autowired
	private SysOtpService sysOtpService;
	@Autowired
	private SysOtpOfficeService sysOtpOfficeService;
	@Autowired
	private OfficeService officeService;
	
	@ModelAttribute
	public SysOtp get(@RequestParam(required=false) String id) {
		SysOtp entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = sysOtpService.get(id);
		}
		if (entity == null){
			entity = new SysOtp();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysOtp:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysOtp sysOtp, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysOtp> page = sysOtpService.findPage(new Page<SysOtp>(request, response), sysOtp); 
		model.addAttribute("page", page);
		return "modules/sys/sysOtpList";
	}

	@RequiresPermissions("sys:sysOtp:view")
	@RequestMapping(value = "form")
	public String form(SysOtp sysOtp, Model model) {
		model.addAttribute("sysOtp", sysOtp);
		return "modules/sys/sysOtpForm";
	}

	@RequiresPermissions("sys:sysOtp:edit")
	@RequestMapping(value = "save")
	public String save(SysOtp sysOtp, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysOtp)){
			return form(sysOtp, model);
		}
		sysOtpService.save(sysOtp);
		addMessage(redirectAttributes, "保存令牌成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysOtp/?repage";
	}
	
	@RequiresPermissions("sys:sysOtp:edit")
	@RequestMapping(value = "delete")
	public String delete(SysOtp sysOtp, RedirectAttributes redirectAttributes) {
		sysOtpService.delete(sysOtp);
		addMessage(redirectAttributes, "删除令牌成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysOtp/?repage";
	}

	/**
	 * 绑定用户
	 * 
	 * @author qph
	 * @version 2018年7月3日
	 */
	@RequiresPermissions("sys:sysOtp:edit")
	@RequestMapping(value = "binding")
	public String binding(SysOtp sysOtp, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		User user = UserUtils.get(sysOtp.getBindingManNo());
		try {
			SysOtp otp = new SysOtp();
			otp.setUser(user);
			List<SysOtp> sysOtpList = sysOtpService.findList(otp);
			if (!Collections3.isEmpty(sysOtpList)) {
				if(!sysOtpList.get(0).getTokenId().equals(sysOtp.getTokenId())){
					throw new BusinessException("message.E9016", "", new String[] {});
				}
			}
			sysOtp.setUser(user);
			// 验证动态密码
			boolean flag = sysOtpService.verify(user, sysOtp.getCommand(), sysOtp);
			if (!flag) {
				throw new BusinessException("message.E9017", "", new String[] {});
			}
			// 绑定
			sysOtpService.save(sysOtp);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			/* end */
			return toBindingUser(sysOtp, model);
		} catch (Exception e) {
			message = msg.getMessage("message.E9017", null, locale);
			addMessage(model, message);
			return toBindingUser(sysOtp, model);
		}
		addMessage(redirectAttributes, "绑定用户成功");
		return "redirect:" + Global.getAdminPath() + "/sys/sysOtp/?repage";
	}

	/**
	 * 同步令牌
	 * 
	 * @author qph
	 * @version 2018年7月6日
	 */

	@RequiresPermissions("sys:sysOtp:edit")
	@RequestMapping(value = "Synchronous")
	public String Synchronous(SysOtp sysOtp, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			sysOtpService.synchronous(sysOtp);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			/* end */
			return toSynOtp(sysOtp, model);
		} catch (Exception e) {
			message = msg.getMessage("message.E9018", null, locale);
			addMessage(model, message);
			return toSynOtp(sysOtp, model);
		}
		addMessage(redirectAttributes, "同步令牌成功");
		return "redirect:" + Global.getAdminPath() + "/sys/sysOtp/?repage";
	}

	/**
	 * 跳转用户口令绑定界面
	 * 
	 * @author qph
	 * @version 2018年7月3日
	 */
	@RequestMapping(value = "toBindingUser")
	public String toBindingUser(SysOtp sysOtp, Model model) {
		if (sysOtp.getUser() != null) {
			sysOtp.setBindingManNo(sysOtp.getUser().getId());
			sysOtp.setBindingManName(sysOtp.getUser().getName());
		}
		model.addAttribute("sysOtp", sysOtp);
		return "modules/sys/sysOtpUserBinding";
	}

	/**
	 * 跳转同步otp令牌页面
	 * 
	 * @author qph
	 * @version 2018年7月3日
	 */
	@RequestMapping(value = "toSynOtp")
	public String toSynOtp(SysOtp sysOtp, Model model) {
		model.addAttribute("sysOtp", sysOtp);
		return "modules/sys/sysOtpSynchronous";
	}

	/**
	 * 按机构过滤用户
	 * 
	 * @author qph
	 * @version 2018年7月3日
	 */
	@RequestMapping(value = "selectByOfficeId")
	@ResponseBody
	public String selectByOfficeId(String officeId) {
		// 根据机构Id获取用户列表
		List<User> UserList = UserUtils.getUsersByTypeAndOffice(null, officeId);
		List<Map<String, Object>> dataList = Lists.newArrayList();
		if (StringUtils.isEmpty(officeId)) {
			return "";
		} else {
			if (!Collections3.isEmpty(UserList)) {
				for (User item : UserList) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("label", item.getName());
					map.put("id", item.getId());
					dataList.add(map);
				}
				return gson.toJson(dataList);
			} else {
				return "";
			}

		}
	}

	/**
	 * 通过输入的tokenId，查询是否已经存在
	 * 
	 * @param tokenId
	 * @author qph
	 * @version 2018-07-05
	 * @return
	 */
	@RequestMapping(value = "checkTokenId")
	@ResponseBody
	public String checkTokenId(HttpServletRequest request, String tokenId, String oldTokenId) {
		// 令牌号是否有改动
		if (StringUtils.isNotBlank(tokenId) && tokenId.equals(oldTokenId)) {
			return "true";
		}
		SysOtp sysOtp = new SysOtp();
		// 设置tokenId
		sysOtp.setTokenId(tokenId);
		SysOtp resultOtp = sysOtpService.getSysOtpByTokenId(sysOtp);
		if (resultOtp == null) {
			return "true";
		} else {
			return "false";
		}
	}
	
	/**
	 * 返回上一级页面
	 * 
	 * @author XL
	 * @version 2018-10-29
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + Global.getAdminPath() + "/sys/sysOtp/list?repage";
	}

	/**
	 * 跳转机构选择页面
	 * 
	 * @author XL
	 * @version 2018-10-29
	 * @param sysOtpOffice
	 * @param model
	 * @return 机构选择页面
	 */
	@RequiresPermissions("sys:sysOtp:office")
	@RequestMapping(value = { "formOffice" })
	public String formOffice(SysOtpOffice sysOtpOffice, Model model) {
		// 所有机构列表
		List<Office> officeList = officeService.findAll();
		// 令牌机构管理列表
		List<SysOtpOffice> SysOtpOfficeList = sysOtpOfficeService.findList(sysOtpOffice);
		// 启用令牌机构ID列表
		List<String> officeIdList = Lists.newArrayList();
		for (SysOtpOffice sysOtpOfficeItem : SysOtpOfficeList) {
			officeIdList.add(sysOtpOfficeItem.getOffice().getId());
		}
		model.addAttribute("officeIds", StringUtils.join(officeIdList, ","));
		model.addAttribute("officeList", officeList);
		return "modules/sys/sysOtpOfficeForm";
	}

	/**
	 * 保存启用令牌功能机构
	 * 
	 * @author XL
	 * @version 2018-10-29
	 * @param sysOtpOffice
	 * @param model
	 * @param redirectAttributes
	 * @return 机构选择页面
	 */
	@RequiresPermissions("sys:sysOtp:office")
	@RequestMapping(value = { "saveOffice" })
	public String saveOffice(SysOtpOffice sysOtpOffice, Model model, RedirectAttributes redirectAttributes) {
		// 关闭所有机构令牌功能
		sysOtpOfficeService.deleteAll();
		for (Office officeItem : sysOtpOffice.getOfficeList()) {
			SysOtpOffice sysOtpOfficeInfo = new SysOtpOffice();
			sysOtpOfficeInfo.setOffice(officeItem);
			// 保存(开启机构令牌功能)
			sysOtpOfficeService.save(sysOtpOfficeInfo);
		}
		addMessage(redirectAttributes, "启用令牌机构保存成功");
		return "redirect:" + Global.getAdminPath() + "/sys/sysOtp/formOffice";
	}

}