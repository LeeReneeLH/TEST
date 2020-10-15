package com.coffer.businesses.modules.store.v01.web;

import java.util.HashMap;
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
import com.coffer.businesses.modules.store.v01.entity.StoAddCashGroup;
import com.coffer.businesses.modules.store.v01.entity.StoCarInfo;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoAddCashGroupService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;
/**
 * 加钞组管理Controller
 * 
 * @author wanglu
 * @version 2017-11-09
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoAddCashGroup")
public class StoAddCashGroupController extends BaseController {
	
	@Autowired
	private StoAddCashGroupService stoAddCashGroupService;
	
	@ModelAttribute
	public StoAddCashGroup get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return stoAddCashGroupService.get(id);
		} else {
			return new StoAddCashGroup();
		}
	}
	
	/**
	 * 加钞组信息检索
	 * 
	 * @author wanglu
	 * @version 2017-11-09
	 */
	@RequiresPermissions("store:stoAddCashGroup:view")
	@RequestMapping(value = { "list", "" })
	public String list(@RequestParam(required = false) boolean isSearch, StoAddCashGroup stoAddCashGroup, 
			HttpServletRequest request, HttpServletResponse response, Model model){
		// 添加机构筛选条件 修改人：xl 修改时间：2017-12-29 begin
		stoAddCashGroup.setOffice(UserUtils.getUser().getOffice());
		// end
		Page<StoAddCashGroup> page = stoAddCashGroupService.findPage(new Page<StoAddCashGroup>(request, response), stoAddCashGroup);
		model.addAttribute("page", page);
		return "modules/store/v01/stoAddCashGroup/stoAddCashGroupList";
	}
	
	/**
	 * 加钞组信息添加/修改跳转
	 * 
	 * @author wanglu
	 * @version 2017-11-09
	 */
	@RequiresPermissions("store:stoAddCashGroup:view")
	@RequestMapping(value = { "form" })
	public String form(@RequestParam(required = false)String id, 
			HttpServletRequest request, HttpServletResponse response, Model model,StoAddCashGroup stoAddCashGroup) {
		Map<String, Object> carInfoParameterMap = new HashMap<String, Object>();	//获取车辆信息的查询条件
		carInfoParameterMap.put("carDelFlag", Constant.deleteFlag.Valid);
		carInfoParameterMap.put("addCashGroupDelFlag", Constant.deleteFlag.Valid);
		
		/* 添加机构查询条件 修改人：xl 修改日期：2017-12-27 begin */
		carInfoParameterMap.put("office", UserUtils.getUser().getOffice());
		/* end */

		Map<String, Object> escortInfoParameterMap = new HashMap<String, Object>();	//获取押运员信息的查询条件
		/* 添加机构查询条件 修改人：wxz 修改日期：2017-12-29 begin */
		escortInfoParameterMap.put("office", UserUtils.getUser().getOffice());
		/* end */
		escortInfoParameterMap.put("addCashGroupDelFlag", Constant.deleteFlag.Valid);
		escortInfoParameterMap.put("escortDelFlag", Constant.deleteFlag.Valid);
		escortInfoParameterMap.put("escortType", Global.getConfig("escort.type.escort"));
		
		List<StoCarInfo> stoCarInfoList = stoAddCashGroupService.getUnSetCarInfo(carInfoParameterMap);	//获取未分配押运员的车辆信息列表
		
		List<StoEscortInfo> stoEscortInfoList = stoAddCashGroupService.getUnCheeseEscortInfo(escortInfoParameterMap);	//获取为分配车辆的押运员信息列表
		
		model.addAttribute("stoCarInfoList", stoCarInfoList);
		model.addAttribute("stoEscortInfoList", stoEscortInfoList);
		model.addAttribute("stoAddCashGroup", stoAddCashGroup);
		return "modules/store/v01/stoAddCashGroup/stoAddCashGroupForm";
	}
	
	/**
	 * 加钞组信息查看
	 * 
	 * @author wanglu
	 * @version 2017-11-09
	 */
	@RequiresPermissions("store:stoAddCashGroup:view")
	@RequestMapping(value = { "view" })
	public String view(@RequestParam(required = false)String id, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		StoAddCashGroup stoAddCashGroup = new StoAddCashGroup();
		/*id不为空时，根据id查出单条加钞组信息，进入form页面*/
		if(StringUtils.isNotEmpty(id)){
			stoAddCashGroup.setId(id);
			stoAddCashGroup = stoAddCashGroupService.getSingleStoAddCashGroupInfo(stoAddCashGroup);
		}
		model.addAttribute("stoAddCashGroup", stoAddCashGroup);
		return "modules/store/v01/stoAddCashGroup/stoAddCashGroupView";
	}
	
	/**
	 * 加钞组信息添加
	 * 
	 * @author wanglu
	 * @version 2017-11-09
	 */
	@RequiresPermissions("store:stoAddCashGroup:edit")
	@RequestMapping(value = { "save" })
	public String saveStoAddCashGroupInfo(StoAddCashGroup stoAddCashGroup, 
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		stoAddCashGroupService.save(stoAddCashGroup);
		addMessage(redirectAttributes, msg.getMessage("message.I9001", null, locale));	//保存加钞组成功
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoAddCashGroup/list?isSearch=true&repage";
	}
	
	/**
	 * 加钞组信息删除（逻辑删除）
	 * 
	 * @author wanglu
	 * @version 2017-11-09
	 */
	@RequiresPermissions("store:stoAddCashGroup:edit")
	@RequestMapping(value = { "delete" })
	public String deleteStoAddCashGroupInfo(@RequestParam(required = false)String id, StoAddCashGroup stoAddCashGroupA,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		StoAddCashGroup stoAddCashGroup = new StoAddCashGroup();
		stoAddCashGroup.setId(id);
		stoAddCashGroupService.delete(stoAddCashGroup);
		// 删除加钞组提示消息 修改人：xl 修改时间：2017-12-14 begin
		Locale locale = LocaleContextHolder.getLocale();
		addMessage(redirectAttributes, msg.getMessage("message.I9002", null, locale));
		// end
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoAddCashGroup/list?isSearch=true&repage";
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
		return "redirect:" + adminPath + "/store/v01/stoAddCashGroup/list?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "checkGroupName")
	public String checkGroupName(String groupName, String oldGroupName) {
		StoAddCashGroup stoAddCashGroup = new StoAddCashGroup();
		stoAddCashGroup.setGroupName(groupName);
		// 添加机构筛选条件 修改人：xl 修改时间：2017-12-29 begin
		stoAddCashGroup.setOffice(UserUtils.getUser().getOffice());
		// end
		List<StoAddCashGroup> stoAddCashGroupList = stoAddCashGroupService.getStoAddCashGroupList(stoAddCashGroup);
		if (groupName != null & groupName.equals(oldGroupName)) {
			return "true";//加钞组名称未做修改时直接返回"true"
		} else if (groupName != null && stoAddCashGroupList != null && stoAddCashGroupList.size() == 0) {
			return "true";
		}
		return "false";
	}
	
	/**
	 * 获取单条加钞组信息
	 * @author wanglu
	 * @version 2017-11-16
	 * @param StoAddCashGroup
	 * @return StoAddCashGroup
	 */
	private StoAddCashGroup getSingleAddCashGroup(StoAddCashGroup stoAddCashGroup) {
		return stoAddCashGroupService.getSingleStoAddCashGroupInfo(stoAddCashGroup);
	}
}
