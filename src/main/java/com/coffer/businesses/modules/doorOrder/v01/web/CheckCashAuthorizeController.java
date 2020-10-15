package com.coffer.businesses.modules.doorOrder.v01.web;

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

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.CheckCashAuthorize;
import com.coffer.businesses.modules.doorOrder.v01.service.CheckCashAuthorizeService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
* Title: CheckCashAuthorizeController 
* <p>Description:授权信息Controller </p>
* @author HaoShijie
* @date 2020年4月29日 上午10:43:11
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/checkCashAuthorize")
public class CheckCashAuthorizeController extends BaseController {
	@Autowired
	private CheckCashAuthorizeService checkCashAuthorizeService;
	
	@ModelAttribute("checkCashAuthorize")
	public CheckCashAuthorize get(@RequestParam(required = false) String id) {
		CheckCashAuthorize entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = checkCashAuthorizeService.get(id);
		}
		if (entity == null) {
			entity = new CheckCashAuthorize();
		}
		return entity;
	}
	/**
	 * 
	 * Title: list
	 * <p>Description: 授权信息列表页面</p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = { "list", "" })
	public String list(CheckCashAuthorize checkCashAuthorize, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 查询条件 : 开始时间
		if (checkCashAuthorize.getCreateTimeStart() != null) {
			checkCashAuthorize.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(checkCashAuthorize.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (checkCashAuthorize.getCreateTimeEnd() != null) {
			checkCashAuthorize.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(checkCashAuthorize.getCreateTimeEnd())));
		}
		User user = UserUtils.getUser();
		checkCashAuthorize.setCurrentUser(user);
		Page<CheckCashAuthorize> page = checkCashAuthorizeService.getAuthorizePage(new Page<CheckCashAuthorize>(request, response), checkCashAuthorize);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/checkAuthorize/checkCashAuthorizeList";
	}
	/**
	 * 
	 * Title: form
	 * <p>Description:授权信息登记页面 </p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("collection:checkCashAuthorize:edit")
	@RequestMapping(value = "form")
	public String form(CheckCashAuthorize checkCashAuthorize, Model model) {
		model.addAttribute("checkCashAuthorize", checkCashAuthorize);
		return "modules/doorOrder/v01/checkAuthorize/checkCashAuthorizeForm";
	}
	/**
	 * 
	 * Title: save
	 * <p>Description:保存授权信息 </p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @param model
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("collection:checkCashAuthorize:edit")
	@RequestMapping(value = "save")
	public String save(CheckCashAuthorize  checkCashAuthorize, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		//验证机构的授权类型是否存在
		if (StringUtils.isBlank(checkCashAuthorize.getId())) {
			CheckCashAuthorize entity = new CheckCashAuthorize();
			entity.setOfficeId(checkCashAuthorize.getOfficeId());
			entity.setType(checkCashAuthorize.getType());
			if (!checkCashAuthorizeService.getAuthorizeList(entity).isEmpty()) {
				// [保存失败]:当前机构已经存在{0}授权配置，请勿重复添加！
				message = msg.getMessage("message.I7602", new String[] { DictUtils.getDictLabel(checkCashAuthorize.getType(), "authorize_type", null) },locale);
				addMessage(model, message);
				return form(checkCashAuthorize, model);
			}
		}else {
			//修改状况
			CheckCashAuthorize  checkAuthorizeType = checkCashAuthorizeService.get(checkCashAuthorize.getId());
			if (!checkAuthorizeType.getType().equals(checkCashAuthorize.getType())) {
				CheckCashAuthorize  type = checkCashAuthorizeService.getByIdForCheckType(checkCashAuthorize);
				if (type != null) {
					// [保存失败]:当前机构已经存在{0}授权配置，请勿重复添加！
					message = msg.getMessage("message.I7602", new String[] { DictUtils.getDictLabel(checkCashAuthorize.getType(), "authorize_type", null) },locale);
					addMessage(model, message);
					return form(checkCashAuthorize, model);
				}
			}			
		}
		checkCashAuthorizeService.save(checkCashAuthorize);
		return "redirect:" + adminPath + "/doorOrder/v01/checkCashAuthorize/list?repage";
	}
	
	/**
	 * 
	 * Title: back
	 * <p>Description:返回 </p>
	 * @author:     HaoShijie
	 * @param response
	 * @param request
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/doorOrder/v01/checkCashAuthorize/list?repage";
	}
	/**
	 * 
	 * Title: delete
	 * <p>Description:删除授权信息 </p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @param redirectAttributes
	 * @return 
	 * String    返回类型
	 */
	@RequiresPermissions("collection:checkCashAuthorize:edit")
	@RequestMapping(value = "delete")
	public String delete(CheckCashAuthorize checkCashAuthorize, RedirectAttributes redirectAttributes) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		checkCashAuthorizeService.delete(checkCashAuthorize);
		//删除授权信息成功
		message = msg.getMessage("message.I7601", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/doorOrder/v01/checkCashAuthorize/list";
	}
	/**
	 * 
	 * Title: pauseAuthorize
	 * <p>Description:关闭授权 </p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @param redirectAttributes
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "pauseAuthorize")
	public String pauseAuthorize(CheckCashAuthorize checkCashAuthorize, RedirectAttributes redirectAttributes, Model model) {
		// 本地化资源
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		checkCashAuthorize.setIsUse(Constant.AuthorizeStatus.CLOSE);
		checkCashAuthorizeService.save(checkCashAuthorize);
		// 操作成功
		message = msg.getMessage("message.I0005", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/doorOrder/v01/checkCashAuthorize/list?repage";
	}
	/**
	 * 
	 * Title: resumeAuthorize
	 * <p>Description:开启授权 </p>
	 * @author:     HaoShijie
	 * @param checkCashAuthorize
	 * @param redirectAttributes
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "resumeAuthorize")
	public String resumeAuthorize(CheckCashAuthorize checkCashAuthorize, RedirectAttributes redirectAttributes, Model model) {
		// 本地化资源
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		checkCashAuthorize.setIsUse(Constant.AuthorizeStatus.OPEN);
		checkCashAuthorizeService.save(checkCashAuthorize);
		// 操作成功
		message = msg.getMessage("message.I0005", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/doorOrder/v01/checkCashAuthorize/list?repage";
	}
	/**
	 * 
	 * Title: makeExpressionTypeOptions
	 * <p>Description:授权类型关联授权金额表达式类型 </p>
	 * @author:     HaoShijie
	 * @param authorizeType
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "makeExpressionTypeOptions")
	@ResponseBody
	public String makeExpressionTypeOptions(String authorizeType) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> dictValueList = Lists.newArrayList();
		List<String> dictLabelList = Lists.newArrayList();
		List<Dict> dictList = null;
		if (DoorOrderConstant.AuthorizeType.CHECK_CASH.equals(authorizeType)) {
			//获取字典列表
			dictList = DictUtils.getDictList("expression_type", true, "1,3,4");
		} else {
			dictList = DictUtils.getDictList("expression_type");
		}
		if (dictList.isEmpty()) {
			dictList = Lists.newArrayList();
		}
		for (Dict dict : dictList) {
			dictValueList.add(dict.getValue());
			dictLabelList.add(dict.getLabel());
		}
		resultMap.put("dictLabelList", dictLabelList);
		resultMap.put("dictValueList", dictValueList);
		return gson.toJson(resultMap);
	}
	
}
