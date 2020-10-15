package com.coffer.businesses.modules.collection.v03.web;

import java.util.List;
import java.util.Locale;

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

import com.coffer.businesses.modules.collection.CollectionConstant;
import com.coffer.businesses.modules.collection.v03.entity.CustUser;
import com.coffer.businesses.modules.collection.v03.entity.SelectItem;
import com.coffer.businesses.modules.collection.v03.entity.StoreOffice;
import com.coffer.businesses.modules.collection.v03.service.CustUserService;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 客户管理Controller
 * 
 * @author wanglin
 * @version 2017-11-13
 */
@Controller
@RequestMapping(value = "${adminPath}/collection/v03/custUser")
public class CustUserController extends BaseController {

	@Autowired
	private CustUserService custUserService;

	@ModelAttribute("custUser")
	public CustUser get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return custUserService.get(id);
		} else {
			return new CustUser();
		}
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param CustUser
	 * @return
	 */
	@RequiresPermissions("collection:custUser:view")
	@RequestMapping(value = {"list", ""})
	public String list(CustUser custUser, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<CustUser> page = custUserService.findList(new Page<CustUser>(request, response), custUser);
        model.addAttribute("page", page);
        
		//商户下拉
        StoreOffice storeOffice = new StoreOffice();
        List<SelectItem> storeList = custUserService.findStoreSelect(storeOffice);
		model.addAttribute("storeList", storeList);
        
		return "modules/collection/v03/custUser/custUserList";
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          登记修改页面
	 * @param CustUser
	 * @return
	 */
	@RequiresPermissions("collection:custUser:view")
	@RequestMapping(value = "form")
	public String form(CustUser custUser, Model model) {
		if (custUser.getId() != null){
			custUser.setNewPassword("");
		}
		model.addAttribute("custUser", custUser);
		//商户下拉
		StoreOffice storeOffice = new StoreOffice();
		storeOffice.setEnabledFlag(CollectionConstant.enabledFlagType.OK);
		//修改的场合
		if (StringUtils.isNotBlank(custUser.getId())) {
			storeOffice.setId(custUser.getStoreId());
		}
		
        List<SelectItem> storeList = custUserService.findStoreSelect(storeOffice);
		model.addAttribute("storeList", storeList);
		
		return "modules/collection/v03/custUser/custUserForm";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          查看页面
	 * @param CustUser
	 * @return
	 */
	@RequiresPermissions("collection:custUser:view")
	@RequestMapping(value = "view")
	public String view(CustUser custUser, Model model) {
		model.addAttribute("custUser", custUser);
		return "modules/collection/v03/custUser/custUserView";
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          保存记录
	 * @param CustUser
	 * @return
	 */
	@RequiresPermissions("collection:custUser:edit")
	@RequestMapping(value = "save")
	public String save(CustUser custUser, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		
		// 如果新密码为空，则不更换密码
		if (StringUtils.isNotBlank(custUser.getNewPassword())) {
			custUser.setPassword(SystemService.entryptPassword(custUser.getNewPassword()));
		}
		
		
		if (!beanValidator(model, custUser)) {
			return form(custUser, model);
		}
		String message = "";
		try {
			//保存
			custUserService.save(custUser);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(custUser, model);
		}
		// 客户保存成功
		message = msg.getMessage("message.I7232", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/collection/v03/custUser/list";
	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          删除记录
	 * @param CustUser
	 * @return
	 */
	@RequiresPermissions("collection:custUser:edit")
	@RequestMapping(value = "delete")
	public String delete(CustUser custUser, RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		custUserService.delete(custUser);
		//删除客户成功
		message = msg.getMessage("message.I7233", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/collection/v03/custUser/list";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          门店编码验证
	 * @param code
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkCode")
	public String checkCustUserCode(String code, String oldCode) {
		return custUserService.checkCustUserCode(code, oldCode);
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
		return "redirect:" + adminPath + "/collection/v03/custUser/list?repage";
	}

	
//	/**
//	 * 
//	 * Title: deleteCheck
//	 * <p>Description: 查看要删除的门店下是否有有效用户</p>
//	 * @author:     wanglin
//	 * @param response
//	 * @param id
//	 * @return 
//	 * @throws JsonProcessingException 
//	 * String    返回类型
//	 */
//	@ResponseBody
//	@RequestMapping(value = "deleteCheck")
//	public String deleteCheck(HttpServletResponse response, String id) throws JsonProcessingException {
//
//		int userNum = custUserService.getVaildCntByOfficeId(id);
//
//		if (userNum == 0) {
//			return renderString(response, "false");
//		}
//		return renderString(response, "true");
//
//	}
	
	/**
	 * 验证登录名是否有效
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && custUserService.getUserByLoginName(loginName) == null) {
			return "true";
		}
		return "false";
	}
	
	
	/**
	 * @author wanglin
	 * @date 2017-11-06
	 * 
	 *       验证身份证号信息是否存在
	 * 
	 * @param id
	 * @param oldIdcardNo
	 * @param idcardNo
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkIdcardNo")
	public String checkIdcardNo(String id, String oldIdcardNo, String idcardNo) {
		String strReturn = custUserService.checkIdcardNo(id , oldIdcardNo , idcardNo);
		return strReturn;
	}

	
	
	
	
	
}
