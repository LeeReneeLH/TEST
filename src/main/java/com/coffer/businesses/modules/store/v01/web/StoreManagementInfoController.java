/**
 * wenjian:    StoreManagementInfoController.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2017年8月9日    wangbaozhong     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2017年8月9日 上午11:18:18
 */
package com.coffer.businesses.modules.store.v01.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.v01.entity.StoreManagementInfo;
import com.coffer.businesses.modules.store.v01.service.StoreManagementInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
* Title: StoreManagementInfoController 
* <p>Description: 库房管理Controller</p>
* @author wangbaozhong
* @date 2017年8月9日 上午11:18:18
*/
@Controller
@RequestMapping(value = "${adminPath}/store/v01/storeManagementInfo")
public class StoreManagementInfoController extends BaseController {
	@Autowired
	private StoreManagementInfoService service;
	
	@ModelAttribute
	public StoreManagementInfo get(String id) {
		if (StringUtils.isNotBlank(id)) {
			return service.get(id);
		} else {
			return new StoreManagementInfo();
		}
	}
	
	/**
	 * 
	 * Title: list
	 * <p>Description: 列表页面查询</p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = {"list", ""})
	public String list(StoreManagementInfo storeManagementInfo, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
			storeManagementInfo.setOffice(UserUtils.getUser().getOffice());
		}
		Page<StoreManagementInfo> page = new Page<StoreManagementInfo>(request, response);

		page = service.findPage(page, storeManagementInfo);

		model.addAttribute("page", page);
		model.addAttribute("storeManagementInfo", storeManagementInfo);
		return "modules/store/v01/storeManagementInfo/storeManagementInfoList";
	}
	
	/**
	 * 
	 * Title: graph
	 * <p>Description: 图形页面</p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "graph")
	public String graph(StoreManagementInfo storeManagementInfo, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
			storeManagementInfo.setOffice(UserUtils.getUser().getOffice());
		}
		List<StoreManagementInfo> storeManagementInfoList = service.findList(storeManagementInfo);
		if (storeManagementInfoList.size() == 0) {
			Locale locale = LocaleContextHolder.getLocale();
			// message.I1029=[提示]：请先初始化库房！
			String message = msg.getMessage("message.I1029", null, locale);
			addMessage(model, message);
		}
		
		model.addAttribute("storeManagementInfoList", storeManagementInfoList);
		return "modules/store/v01/storeManagementInfo/storeGraphList";
	}
	
	/**
	 * 
	 * Title: toCreateStorePage
	 * <p>Description: 跳转到金库创建页面</p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "toCreateStorePage")
	public String toCreateStorePage(StoreManagementInfo storeManagementInfo, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Office curr = UserUtils.getUser().getOffice();
		Office searchConditioni = new Office();
		
		searchConditioni.setParentIds(curr.getParentIds() + curr.getId());
		searchConditioni.setId(curr.getId());
		List<Office> officeList = SysCommonUtils.findOfficeList(searchConditioni);
		
		List<Office> allCofferOffices = Lists.newArrayList();
		List<User> allManagerUsers = Lists.newArrayList();
		
		List<String> userTypeList = Lists.newArrayList();
		userTypeList.add(Constant.SysUserType.COFFER_MANAGER);
		userTypeList.add(Constant.SysUserType.COFFER_OPT);
		userTypeList.add(Constant.SysUserType.CENTRAL_MANAGER);
		userTypeList.add(Constant.SysUserType.CENTRAL_STORE_MANAGER_OPT);
		userTypeList.add(Constant.SysUserType.CENTRAL_ALLOCATE_OPT);
		userTypeList.add(Constant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT);
		userTypeList.add(Constant.SysUserType.CENTRAL_OPT);
		
		for (Office tempOffice : officeList) {
			if (Constant.OfficeType.CLEAR_CENTER.equals(tempOffice.getType())
					|| Constant.OfficeType.CENTRAL_BANK.equals(tempOffice.getType())
					|| Constant.OfficeType.COFFER.equals(tempOffice.getType())
					|| Constant.OfficeType.DIGITAL_PLATFORM.equals(tempOffice.getType())
					|| Constant.OfficeType.OUTLETS.equals(tempOffice.getType())) {
				allCofferOffices.add(tempOffice);
				
			}
		}
		
		List<User> userList = SysCommonUtils.findUserInfoByOfficeId(curr.getId(), null);
		for (User tempUser : userList) {
			if (userTypeList.contains(tempUser.getUserType())) {
				allManagerUsers.add(tempUser);
			}
		}
		
		if (StringUtils.isNotBlank(storeManagementInfo.getId())) {
			List<Office> oldOfficeList = Collections3.extractToList(storeManagementInfo.getStoreCoOfficeAssocationList(), "office");
			storeManagementInfo.setOfficeIdList(Collections3.extractToList(oldOfficeList, "id"));
			storeManagementInfo.setBoxTypeList(Collections3.extractToList(storeManagementInfo.getStoreTypeAssocationList(), "storageType"));
			List<User> oldUserList = Collections3.extractToList(storeManagementInfo.getStoreManagerAssocationList(), "user");
			storeManagementInfo.setUserIdList(Collections3.extractToList(oldUserList, "id"));
		}
		
		model.addAttribute("allCofferOffices", allCofferOffices);
		
		model.addAttribute("allManagerUsers", allManagerUsers);
		
		model.addAttribute("storeManagementInfo", storeManagementInfo);
		return "modules/store/v01/storeManagementInfo/storeManagementInfoForm";
	}
	/**
	 * 
	 * Title: makeStoreInfo
	 * <p>Description: 保存库房信息</p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "makeStoreInfo")
	public String makeStoreInfo(StoreManagementInfo storeManagementInfo, 
			HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		storeManagementInfo.setOffice(UserUtils.getUser().getOffice());
		
		if (!service.checkStoreName(storeManagementInfo.getOldStoreName(), storeManagementInfo.getStoreName())) {
			// message.E1061=[验证失败]库房名【{0}】已经存在！
			message = msg.getMessage("message.E1061", new String[] {storeManagementInfo.getStoreName()}, locale);
			addMessage(model, message);
			model.addAttribute("storeManagementInfo", storeManagementInfo);
			return toCreateStorePage(storeManagementInfo, request, response, model);
		}
		
		service.saveMangementInfo(storeManagementInfo);
		
		// message.I1030=[保存成功]：库房{0}的信息保存成功!
		message = msg.getMessage("message.I1030", new String[] {storeManagementInfo.getStoreName()}, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/store/v01/storeManagementInfo/list?repage";
	}
	
	
	/**
	 * 
	 * Title: setStoreStatus
	 * <p>Description: 设定库房状态 </p>
	 * @author:     wangbaozhong
	 * @param storeManagementInfo
	 * @param request
	 * @param response
	 * @param model
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "setStoreStatus")
	public String setStoreStatus(StoreManagementInfo storeManagementInfo, 
			HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		
		service.setStoreStatus(storeManagementInfo);
		
		// message.I1030=[保存成功]：库房{0}的信息保存成功!
		message = msg.getMessage("message.I1030", new String[] {storeManagementInfo.getStoreName()}, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/store/v01/storeManagementInfo/list?repage";
	}
	
	/**
	 * 
	 * Title: back
	 * <p>Description: 返回到列表页面 </p>
	 * @author:     wangbaozhong
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "back")
	public String back() {
		return "redirect:" + adminPath + "/store/v01/storeManagementInfo/list?repage";
	}
	
}
