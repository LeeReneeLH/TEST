package com.coffer.core.modules.sys.web;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
//import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
//import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.doorOrder.app.v01.service.MiniProgramService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.beanvalidator.BeanValidators;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdcardUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.utils.excel.ExportExcel;
import com.coffer.core.common.utils.excel.ExportExcel2003;
import com.coffer.core.common.utils.excel.ImportExcel;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.Role;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 用户Controller
 * 
 * @author Clark
 * @version 2015-06-09
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemService systemService;
	@Autowired
	private StoEscortInfoService stoEscortInfoService;
	@Autowired
	private MiniProgramService miniProgramService;

	@ModelAttribute
	public User get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return systemService.getUser(id);
		} else {
			return new User();
		}
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = { "index" })
	public String index(User user, Model model) {
		return "modules/sys/userIndex";
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = { "list", "" })
	public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		model.addAttribute("userType", user.getCurrentUser().getUserType());
		model.addAttribute("page", page);
		return "modules/sys/userList";
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "form")
	public String form(User user, Model model) {
		if (user.getOffice() == null || user.getOffice().getId() == null) {
			user.setOffice(UserUtils.getUser().getOffice());
		} else {
			user.setNewPassword("");
		}

		if (user.getUserFaceId() == null) {
			user.setInitFaceIdFlag(null);
		}

		model.addAttribute("user", user);
		model.addAttribute("allRoles", systemService.findAllRole());
		return "modules/sys/userForm";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "save")
	public String save(User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		// // 使用统一登录平台验证用户是否存在
		// // 创建与删除用户时使用统一登录平台验证：0：不使用验证，1：使用验证
		// String isUsed = Global.getConfig("used.commonLogin.check");
		// if (StringUtils.isNotBlank(isUsed) && "1".equals(isUsed)) {
		// if (!uiasService.checkAdd(user.getLoginName())) {
		// Locale locale = LocaleContextHolder.getLocale();
		// String message = msg.getMessage("message.common.E9901", null,
		// locale);
		// addMessage(model, message);
		// return form(user, model);
		// }
		// }

		// 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
		user.setOffice(new Office(request.getParameter("office.id")));
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(user.getNewPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
		}
		if (!beanValidator(model, user)) {
			return form(user, model);
		}
		// 修改时验证用户名是否存在
		if (StringUtils.isNotBlank(user.getId())
				&& !"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
			addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
			return form(user, model);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		List<String> roleIdList = user.getRoleIdList();
		for (Role r : systemService.findAllRole()) {
			if (roleIdList.contains(r.getId())) {
				roleList.add(r);
			}
		}
		user.setRoleList(roleList);

		String message = "";
		Locale locale = LocaleContextHolder.getLocale();

		if (StringUtils.isBlank(user.getId())) {
			// 创建：取得身份信息
			StoEscortInfo validstoEscortInfo = stoEscortInfoService.checkIdcardNo(user.getIdcardNo());
			// 判断身份信息是否存在
			if (validstoEscortInfo != null) {
				message = msg.getMessage("message.E1017", new Object[] { user.getIdcardNo() }, locale);
				this.addMessage(model, message);
				return form(user, model);
			}
		}

		// 保存
		try {
			// 保存用户信息
			systemService.saveUser(user);
			// 清除当前用户缓存
			if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
				UserUtils.clearCache();
				// UserUtils.getCacheMap().clear();
			}
			addMessage(redirectAttributes, "保存用户'" + user.getLoginName() + "'成功");
			return "redirect:" + adminPath + "/sys/user/list?repage";

		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			this.addMessage(model, message);
			if (user.getOffice() != null && !StringUtils.isBlank(user.getOffice().getId())) {
				user.setOffice(SysCommonUtils.findOfficeById(user.getOffice().getId()));
			}
			return form(user, model);
		}

	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "delete")
	public String delete(User user, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}

		if (UserUtils.getUser().getId().equals(user.getId())) {
			addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
		} else {
			// // 使用统一登录平台验证用户是否存在
			// // 创建与删除用户时使用统一登录平台验证：0：不使用验证，1：使用验证
			// String isUsed = Global.getConfig("used.commonLogin.check");
			// if (StringUtils.isNotBlank(isUsed) && "1".equals(isUsed)) {
			// if (!uiasService.checkDel(user.getLoginName())) {
			// Locale locale = LocaleContextHolder.getLocale();
			// String message = msg.getMessage("message.common.E9902", null,
			// locale);
			// addMessage(redirectAttributes, message);
			// return "redirect:" + adminPath + "/sys/user/list?repage";
			// }
			// }
			// 修改人：QPH 修改时间 ： 2017-10-27 begin 修改内容：用户删除时验证用户身上是否还有柜员账务
			// 获取柜员用户类型
			List<String> userTypeList = Global.getList("clear.teller.businessTypes");
			// 若删除用户类型为柜员
			if (userTypeList.contains(user.getUserType())) {
				StoEscortInfo stoEscortInfo = stoEscortInfoService.findByUserId(user.getId());
				if (stoEscortInfo != null && StringUtils.isNotBlank(stoEscortInfo.getId())) {
					List<TellerAccountsMain> tellerAccountsMainList = ClearCommonUtils
							.getNewestTellerAccounts(stoEscortInfo.getId());
					if (!Collections3.isEmpty(tellerAccountsMainList)) {
						for (TellerAccountsMain tellerAccountsMain : tellerAccountsMainList) {
							if (BigDecimal.ZERO.compareTo(tellerAccountsMain.getTotalAmount()) != 0) {
								addMessage(redirectAttributes, "删除用户失败, 该用户柜员账务有余额");
								return "redirect:" + adminPath + "/sys/user/list?repage";
							}
						}
					}
				}
			}
			// end
			systemService.deleteUser(user);
			addMessage(redirectAttributes, "删除用户成功");
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 导出用户数据
	 * 
	 * @param user
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(User user, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = "用户数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
			new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 导入用户数据
	 * 
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "import", method = RequestMethod.POST)
	public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<User> list = ei.getDataList(User.class);
			for (User user : list) {
				try {
					if (StringUtils.isNotBlank(user.getLoginName())
							&& !"true".equals(checkLoginName("", user.getLoginName()))) {
						failureMsg.append("<br/>登录名 " + user.getLoginName() + " 已存在; ");
						failureNum++;
						continue;
					}
					if (!IdcardUtils.validateCard(user.getIdcardNo())) {
						failureMsg.append("<br/>用户： " + user.getName() + " 身份证号不正确; ");
						failureNum++;
						continue;
					}
					if (systemService.checkUserByIdCard(user) == null) {
						user.setPassword(SystemService.entryptPassword("123"));
						BeanValidators.validateWithException(validator, user);
						systemService.saveUser(user);
						successNum++;
					} else {
						failureMsg.append("<br/>用户： " + user.getName() + " 身份证号已经注册; ");
						failureNum++;
					}

				} catch (ConstraintViolationException ex) {
					failureMsg.append("<br/>用户： " + user.getName() + " 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList) {
						failureMsg.append(message + "; ");
					}
					failureNum++;
				} catch (BusinessException ex) {
					failureMsg.append("<br/>用户： " + user.getName() + " 导入失败："
							+ msg.getMessage(ex.getMessageCode(), null, locale));
					failureNum++;
				} catch (Exception ex) {
					failureMsg.append("<br/>用户： " + user.getName() + " 导入失败：" + ex.getMessage());
					failureNum++;
				}
			}
			if (failureNum > 0) {
				failureMsg.insert(0, "，失败 " + failureNum + " 条用户，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 " + successNum + " 条用户" + failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入用户失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 下载导入用户数据模板
	 * 
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "用户数据导入模板.xlsx";
			List<User> list = Lists.newArrayList();
			list.add(UserUtils.getUser());
			new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 下载导入用户数据模板
	 * 
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "import/template2")
	public String importFileTemplate2(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = "用户数据导入模板.xls";
			List<User> list = Lists.newArrayList();
			list.add(UserUtils.getUser());

			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path") + fileName;

			InputStream is = request.getSession().getServletContext().getResourceAsStream(templatePath);

			// HSSFWorkbook workBook = new HSSFWorkbook(new XSSFWorkbook(is));//
			// 创建Excel2007文件对象
			Workbook workbook = WorkbookFactory.create(is);

			new ExportExcel2003(workbook, "用户数据", User.class, 2).setDataList(list).write(response, fileName);

			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 验证登录名是否有效
	 * 
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName != null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 验证用户脸部识别ID是否有效
	 * 
	 * @param oldFaceId
	 * @param userFaceId
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "checkFaceId")
	public String checkFaceId(Long oldFaceId, Long userFaceId) {
		String strCurOfficeId = UserUtils.getUser().getOffice().getId();
		if (userFaceId != null && userFaceId.equals(oldFaceId)) {
			return "true";
		} else if (userFaceId != null && UserUtils.getByUserFaceIdOfficeId(userFaceId, strCurOfficeId) == null) {
			// 修改时不做数量检查
			if (oldFaceId == null) {
				String strFaceIdMaxCnt = Global.getConfig("sys.faceId.maxCnt");
				int iFaceIdMaxCnt = StringUtils.isBlank(strFaceIdMaxCnt) ? 300 : Integer.parseInt(strFaceIdMaxCnt);
				int iPbocFaceIdCnt = UserUtils.getPbocFaceIdCntByOfficeId(strCurOfficeId);
				if (iPbocFaceIdCnt >= iFaceIdMaxCnt) {
					return "false";
				}
			}

			return "true";
		}
		return "false";
	}

	/**
	 * 用户信息显示及保存
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "info")
	public String info(User user, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getName())) {
			if (Global.isDemoMode()) {
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			currentUser.setEmail(user.getEmail());
			currentUser.setPhone(user.getPhone());
			currentUser.setMobile(user.getMobile());
			currentUser.setRemarks(user.getRemarks());
			systemService.updateUserInfo(currentUser);
			model.addAttribute("message", "保存用户信息成功");
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfo";
	}

	/**
	 * 返回用户信息
	 * 
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoData")
	public User infoData() {
		return UserUtils.getUser();
	}

	/**
	 * 修改个人用户密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
			if (Global.isDemoMode()) {
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userModifyPwd";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())) {
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			} else {
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId,
			HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = systemService.findUserByOfficeId(officeId);
		for (int i = 0; i < list.size(); i++) {
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_" + e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtils.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
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
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	// @InitBinder
	// public void initBinder(WebDataBinder b) {
	// b.registerCustomEditor(List.class, "roleList", new
	// PropertyEditorSupport(){
	// @Autowired
	// private SystemService systemService;
	// @Override
	// public void setAsText(String text) throws IllegalArgumentException {
	// String[] ids = StringUtils.split(text, ",");
	// List<Role> roles = new ArrayList<Role>();
	// for (String id : ids) {
	// Role role = systemService.getRole(Long.valueOf(id));
	// roles.add(role);
	// }
	// setValue(roles);
	// }
	// @Override
	// public String getAsText() {
	// return Collections3.extractToString((List) getValue(), "id", ",");
	// }
	// });
	// }

	/**
	 * 账号解绑（系统管理员强制解绑 后台解绑）
	 *
	 * @param loginName
	 *            登录名
	 * @return Map<String, Object>
	 * @author lihe
	 */
	@RequestMapping(value = "noBind")
	public String noBind(@RequestParam(value = "userId") String userId, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		Map<String, Object> rtnMap = miniProgramService.removeBind(userId);
		if ("0".equals(rtnMap.get("resultFlag").toString())) {
			addMessage(redirectAttributes, "解绑成功");
		} else {
			String errorMsg = "";
			if (rtnMap.get("errorMsg") == null) {
				errorMsg = "解绑失败";
			} else {
				errorMsg = "解绑失败," + rtnMap.get("errorMsg").toString();
			}
			addMessage(redirectAttributes, errorMsg);
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}
}
