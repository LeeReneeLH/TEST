package com.coffer.businesses.modules.atm.v01.web;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.atm.v01.entity.ExcelExporterAtmPlan;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.businesses.modules.store.v01.entity.StoAddCashGroup;
import com.coffer.businesses.modules.store.v01.service.StoAddCashGroupService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 加钞计划导入Controller
 * 
 * @author XL
 * @version 2017-11-07
 */
@Controller
@RequestMapping(value = "${adminPath}/atm/v01/atmPlanInfo")
public class AtmPlanInfoController extends BaseController {

	@Autowired
	private AtmPlanInfoService atmPlanInfoService;
	
	@Autowired
	private StoAddCashGroupService stoAddCashGroupService;
	@Autowired
	private AllocationService allocationService;


	@ModelAttribute
	public AtmPlanInfo get(@RequestParam(required = false) String id) {
		AtmPlanInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = atmPlanInfoService.get(id);
		}
		if (entity == null) {
			entity = new AtmPlanInfo();
		}
		return entity;
	}

	/**
	 * 跳转至导入页面
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @return 加钞计划导入页面
	 */
	@RequiresPermissions("atm:atmPlanFile")
	@RequestMapping(value = "importFile", method = RequestMethod.GET)
	public String importFile() {
		return "modules/atm/v01/addPlan/addPlanImportForm";
	}

	/**
	 * 导入加钞计划
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param addPlanFile
	 * @param model
	 * @param redirectAttributes
	 * @return 加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanFile")
	@RequestMapping(value = "importFile", method = RequestMethod.POST)
	public String importFile(@RequestParam("addPlanFile") CommonsMultipartFile addPlanFile, Model model,
			RedirectAttributes redirectAttributes) {
		// 消息提示
		Locale locale = LocaleContextHolder.getLocale();
		// 上传文件名
		String fileName = addPlanFile.getFileItem().getName();
		// 验证上传文件是否为空
		if (StringUtils.isBlank(fileName)) {
			addMessage(redirectAttributes, msg.getMessage("message.E4023", null, locale));
			return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/importFile";
		}
		// 验证上传文件类型
		if (!fileName.toLowerCase().endsWith(".xls")) {
			addMessage(redirectAttributes, msg.getMessage("message.E4024", null, locale));
			return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/importFile";
		}
		// 导入加钞计划
		Map<String,Object> map = atmPlanInfoService.importAtmPlanInfo(addPlanFile, validator);
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		StringBuffer msgStr = new StringBuffer();
		// 提示消息组装
		for (String string : msgs) {
				msgStr.append(
					msg.getMessage(string, new String[] { StringUtils.toString(map.get("errorList")) }, locale)
								+ "<br/>");
		}
		addMessage(redirectAttributes, msgStr.toString());
		if (msgStr.toString().contains(msg.getMessage("message.I4011", null, locale))) {
			// 导入成功
			String addPlanId = (String) map.get("addPlanId");
			Page<AtmPlanInfo> page = new Page<AtmPlanInfo>();
			// 获取计划详细
			AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
			atmPlanInfo.setAddPlanId(addPlanId);
			page = atmPlanInfoService.addPlanView(page, atmPlanInfo);
			model.addAttribute("page", page);
			atmPlanInfo = atmPlanInfoService.getView(addPlanId);
			// 加钞计划文件标题头
			model.addAttribute("addPlanName", atmPlanInfo.getAddPlanName());
			model.addAttribute("addPlanId", addPlanId);
			model.addAttribute("boxNum", atmPlanInfo.getBoxNum());
			model.addAttribute("amount", atmPlanInfo.getAddAmount());
			// 钞箱类型汇总
			model.addAttribute("boxTypeCollect", atmPlanInfoService.getBoxNumbyAddPlanId(addPlanId));
			model.addAttribute("importSuccess", true);
			model.addAttribute("message", msgStr.toString());
			return "modules/atm/v01/addPlan/addPlanImportForm";
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/importFile";
	}
	
	/**
	 * 下载导入加钞计划模板
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return 加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanFile")
	@RequestMapping(value = "template")
	public String importFileTemplate(@RequestParam(value = "atmNos", required = true) String atmNos,
			HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		try {
			// 模板名
			String fileName = msg.getMessage("atm.plan.template", null, locale);
			// 取得模板路径
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// 标题
			GregorianCalendar ca = new GregorianCalendar();
			int AM_PM = ca.get(GregorianCalendar.AM_PM);
			String upDown = "";
			if (AM_PM == 1) {
				upDown = "下午";
			} else if (AM_PM == 0) {
				upDown = "上午";
			}
			String titleName = DateUtils.formatDate(new Date()) + msg.getMessage("atm.plan.name", null, locale) + "（"
					+ upDown + "）";
			Map<String, Object> sheetTitleMap = Maps.newHashMap();
			sheetTitleMap.put("title", titleName);
			sheetMap.put(ExcelExporterAtmPlan.SHEET_TITLE_MAP_KEY, sheetTitleMap);
			// sheet名
			sheetMap.put(ExcelExporterAtmPlan.SHEET_NAME_MAP_KEY, msg.getMessage("atm.plan.bill", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterAtmPlan.SHEET_DATA_ENTITY_CLASS_NAME_KEY, AtmPlanInfo.class.getName());
			/* 设置集合	修改人：wxz 2017-12-6 begin */
			sheetMap.put(ExcelExporterAtmPlan.SHEET_DATA_LIST_MAP_KEY, atmPlanInfoService.getDataList(atmNos));
			/* end	*/
			paramList.add(sheetMap);
			ExcelExporterAtmPlan exportEx = new ExcelExporterAtmPlan(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName);
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, msg.getMessage("message.E4016", null, locale));
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/list";
	}
	
	/**
	 * 跳转至加钞计划列表
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return 加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(AtmPlanInfo atmPlanInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtmPlanInfo> page = new Page<AtmPlanInfo>(request, response);
		page = atmPlanInfoService.findAddPlanList(page, atmPlanInfo);
		model.addAttribute("page", page);
		return "modules/atm/v01/addPlan/addPlanList";
	}

	/**
	 * 跳转至加钞计划查看页面
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 * @param addPlanId
	 * @param amount
	 * @param boxNum
	 * @param request
	 * @param response
	 * @param model
	 * @return 加钞计划查看页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "addPlanView")
	public String addPlanView(AtmPlanInfo atmPlanInfo, @RequestParam(required = false) String addPlanId, @RequestParam(required = false) String addCashGroupName,
			@RequestParam(required = false) String amount, @RequestParam(required = false) String boxNum,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtmPlanInfo> page = new Page<AtmPlanInfo>(request, response);
		// 设置加钞计划ID
		if (StringUtils.isNotBlank(addPlanId)) {
			atmPlanInfo.setAddPlanId(addPlanId);
		}
		// 获取计划详细
		page = atmPlanInfoService.addPlanView(page, atmPlanInfo);
		model.addAttribute("page", page);
		if (!Collections3.isEmpty(page.getList())) {
			// 加钞计划文件标题头
			model.addAttribute("addPlanName", page.getList().get(0)
					.getAddPlanName());
			model.addAttribute("addPlanId", addPlanId);
		}
		model.addAttribute("addCashGroupName", addCashGroupName);//wanglu 20171117添加
		model.addAttribute("amount", amount);
		model.addAttribute("boxNum", boxNum);
		// 钞箱类型汇总
		model.addAttribute("boxTypeCollect", atmPlanInfoService.getBoxNumbyAddPlanId(addPlanId));
		return "modules/atm/v01/addPlan/addPlanView";
	}

	/**
	 * 保存加钞计划
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 * @param model
	 * @param redirectAttributes
	 * @return 加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:edit")
	@RequestMapping(value = "save")
	public String save(AtmPlanInfo atmPlanInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atmPlanInfo)) {
			return importFile();
		}
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 保存加钞计划
		atmPlanInfoService.save(atmPlanInfo);
		addMessage(redirectAttributes, msg.getMessage("message.I4011", null, locale));
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/?repage";
	}

	/**
	 * 删除加钞计划
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param addPlanId
	 * @param redirectAttributes
	 * @return 加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam(required = false) String addPlanId, RedirectAttributes redirectAttributes) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		AtmPlanInfo atmPlanInfo = get(null);
		// 设置加钞计划ID
		atmPlanInfo.setAddPlanId(addPlanId);
		/** 添加删除操作的判断条件  修改人 ：wxz 2017-11-23 begin */
		// 根据加钞计划ID查询调拨信息
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		allAllocateInfo.setRouteId(addPlanId);
		List<AllAllocateInfo> allocateInfoList = allocationService.findAtmBoxList(allAllocateInfo);
		// 判断改加钞计划ID是否存在调拨信息，如果不存在进行删除操作
		if(Collections3.isEmpty(allocateInfoList)){
			// 删除加钞计划
			atmPlanInfoService.delete(atmPlanInfo);
			addMessage(redirectAttributes, msg.getMessage("message.I4013", null, locale));
		} else {
			addMessage(redirectAttributes, msg.getMessage("message.E4036", null, locale));
		}
		/** end */
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/?repage";
	}

	/**
	 * 打印加钞计划
	 * 
	 * @author XL
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 * @param addPlanId
	 * @param amount
	 * @param boxNum
	 * @param request
	 * @param response
	 * @param model
	 * @return 打印页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "print")
	public String print(AtmPlanInfo atmPlanInfo, @RequestParam(required = false) String addPlanId, @RequestParam(required = false) String addCashGroupName,
			@RequestParam(required = false) String amount, @RequestParam(required = false) String boxNum,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtmPlanInfo> page = new Page<AtmPlanInfo>(request, response);
		// 设置加钞计划ID
		if (StringUtils.isNotBlank(addPlanId)) {
			atmPlanInfo.setAddPlanId(addPlanId);
		}
		// 获取计划详细
		page = atmPlanInfoService.addPlanView(page, atmPlanInfo);
		model.addAttribute("page", page);
		if (!Collections3.isEmpty(page.getList())) {
			// 加钞计划文件标题头
			model.addAttribute("addPlanName", page.getList().get(0).getAddPlanName());
			model.addAttribute("addPlanId", addPlanId);
		}
		model.addAttribute("addCashGroupName", addCashGroupName);//wanglu 20171117添加
		model.addAttribute("amount", amount);
		model.addAttribute("boxNum", boxNum);
		// 钞箱类型汇总
		model.addAttribute("boxTypeCollect", atmPlanInfoService.getBoxNumbyAddPlanId(addPlanId));
		return "modules/atm/v01/addPlan/addPlanPrint";
	}
	
	/**
	 * 跳转至加钞计划添加加钞组页面
	 * 
	 * @author wanglu
	 * @version 2017年11月07日
	 * @param atmPlanInfo
	 * @param addPlanId
	 * @param amount
	 * @param boxNum
	 * @param request
	 * @param response
	 * @param model
	 * @return 钞计划添加加钞组页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "bindingAddCashGroupForm")
	public String bindingAddCashGroupForm(AtmPlanInfo atmPlanInfo, @RequestParam(required = false) String addPlanId,
			@RequestParam(required = false) String addCashGroupId,@RequestParam(required = false) String addCashGroupName,
			@RequestParam(required = false) String amount, @RequestParam(required = false) String boxNum,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtmPlanInfo> page = new Page<AtmPlanInfo>(request, response);
		// 设置加钞计划ID
		if (StringUtils.isNotBlank(addPlanId)) {
			atmPlanInfo.setAddPlanId(addPlanId);
		}
		// 获取计划详细
		page = atmPlanInfoService.addPlanView(page, atmPlanInfo);
		model.addAttribute("page", page);
		if (!Collections3.isEmpty(page.getList())) {
			// 加钞计划文件标题头
			model.addAttribute("addPlanName", page.getList().get(0).getAddPlanName());
			// 追加显示加钞计划ID 修改人：xl 修改时间：2017-11-27 begin
			model.addAttribute("addPlanId", addPlanId);
			// end
		}
		model.addAttribute("amount", amount);
		model.addAttribute("boxNum", boxNum);
		
		/**
		 * add by wanglu*/
		atmPlanInfo.setAddCashGroupId(addCashGroupId);
		atmPlanInfo.setAddCashGroupName(addCashGroupName);
		model.addAttribute("atmPlanInfo", atmPlanInfo);
		// 钞箱类型汇总
		model.addAttribute("boxTypeCollect", atmPlanInfoService.getBoxNumbyAddPlanId(addPlanId));
		return "modules/atm/v01/addPlan/addCashGroupBindingForm";
	}
	
	/**
	 * 保存加钞计划绑定的加钞组信息并跳转至加钞计划列表页面
	 * 
	 * @author wanglu
	 * @version 2017年11月16日
	 * @param atmPlanInfo
	 * @param request
	 * @param response
	 * @return 加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "bindingAddCashGroup")
	public String bindingAddCashGroup(HttpServletRequest request,HttpServletResponse response, RedirectAttributes redirectAttributes, AtmPlanInfo atmPlanInfo) {
		Locale locale = LocaleContextHolder.getLocale();
		try {
			atmPlanInfo.preUpdate();
			atmPlanInfoService.bindPlanAddCashGroup(atmPlanInfo);
			addMessage(redirectAttributes, msg.getMessage("message.I4038", null, locale));	//绑定加钞组成功
		} catch (NoSuchMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			addMessage(redirectAttributes, msg.getMessage("message.E4042", null, locale));	//绑定加钞组失败
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/?isSearch=true&repage";
	}
	
	/**
	 * 根据传入的加钞组ID查询加钞组信息并跳转至加钞组详情页面
	 * 
	 * @author wanglu
	 * @version 2017年11月16日
	 * @param groupId
	 * @param request
	 * @param response
	 * @param model
	 * @return 加钞组详情页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "getAddCashGroupDetail")
	public String getAddCashGroupDetail(HttpServletRequest request,HttpServletResponse response, Model model, @RequestParam(required = false)String groupId) {
		StoAddCashGroup stoAddCashGroup = new StoAddCashGroup();
		stoAddCashGroup.setId(groupId);
		stoAddCashGroup = stoAddCashGroupService.getSingleStoAddCashGroupInfo(stoAddCashGroup);
		model.addAttribute("stoAddCashGroup", stoAddCashGroup);
		return "modules/atm/v01/addPlan/addCashGroupDetail";
	}
	
	/**
	 * 计划管理下载模版(选择atm机信息)
	 * @author wxz
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "downLoadAtmPlan")
	public String downLoadAtmPlan(HttpServletRequest request, HttpServletResponse response, Model model){
		List<AtmPlanInfo> dataList = atmPlanInfoService.getDataList();
		model.addAttribute("dataList", dataList);
		return "modules/atm/v01/addPlan/downLoadAddPlan";
	}
	
	/**
	 * 跳转至手动生成列表
	 * 
	 * @author wxz
	 * @version 2017年12月6日
	 * @param request
	 * @param response
	 * @param model
	 * @return 手动生成加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "addPlanForm")
	public String addPlanForm(AtmPlanInfo atmPlanInfo, Model model) {
		List<AtmPlanInfo> dataList = atmPlanInfoService.getDataList();
		model.addAttribute("dataList", dataList);
		model.addAttribute("atmPlanInfo", atmPlanInfo);
		return "modules/atm/v01/addPlan/manualAddPlanForm";
	}
	
	/**
	 * 根据atm机编号，获取atm机信息
	 * 
	 * @author wxz
	 * @version 2017年12月6日
	 * @param atmNo
	 *            ATM机编号
	 * @return ATM机相信信息
	 */
	@RequestMapping(value = "changeAtm")
	@ResponseBody
	public String changeAtm(@RequestParam(value = "atmNo", required = true) String atmNo) {
		Map<String,Object> rtnMap = new HashMap<String, Object>();
		// ATM机编号(终端号)是否为空
		if(StringUtils.isNotBlank(atmNo)){
			List<AtmPlanInfo> getAtmPlanList = atmPlanInfoService.getDataList(atmNo);
			rtnMap.put("getAtmPlanList", getAtmPlanList);
		} else {
			List<AtmPlanInfo> getAtmPlanList = Lists.newArrayList();
			rtnMap.put("getAtmPlanList", getAtmPlanList);
		}
		return gson.toJson(rtnMap);
	}
	
	/**
	 * 保存加钞计划(手动生成)
	 * 
	 * @author wxz
	 * @version 2017年12月07日
	 * @param atmPlanInfo
	 * @param model
	 * @param redirectAttributes
	 * @return 加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:edit")
	@RequestMapping(value = "manualSave")
	public String manualSave(AtmPlanInfo atmPlan, Model model, RedirectAttributes redirectAttributes) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		
		// 导入加钞计划
		Map<String,Object> map = atmPlanInfoService.manualAtmPlanInfo(atmPlan, validator);
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		StringBuffer msgStr = new StringBuffer();
		// 提示消息组装
		for (String string : msgs) {
				msgStr.append(
					msg.getMessage(string, new String[] { StringUtils.toString(map.get("errorList")) }, locale)									+ "<br/>");
		}
		addMessage(redirectAttributes, msgStr.toString());
		if (msgStr.toString().contains(msg.getMessage("message.I4011", null, locale))) {
			// 导入成功
			String addPlanId = (String) map.get("addPlanId");
			Page<AtmPlanInfo> page = new Page<AtmPlanInfo>();
			// 获取计划详细
			AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
			atmPlanInfo.setAddPlanId(addPlanId);
			page = atmPlanInfoService.addPlanView(page, atmPlanInfo);
			model.addAttribute("page", page);
			atmPlanInfo = atmPlanInfoService.getView(addPlanId);
			// 加钞计划文件标题头
			model.addAttribute("addPlanName", atmPlanInfo.getAddPlanName());
			model.addAttribute("addPlanId", addPlanId);
			model.addAttribute("boxNum", atmPlanInfo.getBoxNum());
			model.addAttribute("amount", atmPlanInfo.getAddAmount());
			// 钞箱类型汇总
			model.addAttribute("boxTypeCollect", atmPlanInfoService.getBoxNumbyAddPlanId(addPlanId));
			model.addAttribute("importSuccess", true);
			model.addAttribute("message", msgStr.toString());
			return "modules/atm/v01/addPlan/addPlanImportForm";
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/importFile";
	}
	
	/**
	 * 跳转至修改页面
	 * 
	 * @author wxz
	 * @version 2017年12月12日
	 * @param request
	 * @param response
	 * @param model
	 * @return 修改加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "edit")
	public String edit(AtmPlanInfo atmPlanInfo, Model model, RedirectAttributes redirectAttributes) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 根据加钞计划ID查询调拨信息
		AllAllocateInfo allAllocateInfo = new AllAllocateInfo();
		allAllocateInfo.setRouteId(atmPlanInfo.getAddPlanId());
		List<AllAllocateInfo> allocateInfoList = allocationService.findAtmBoxList(allAllocateInfo);
		// 判断改加钞计划ID是否存在调拨信息，如果不存在进行修改操作
		if(Collections3.isEmpty(allocateInfoList)){
			List<AtmPlanInfo> addPlanList = atmPlanInfoService.findList(atmPlanInfo);
			atmPlanInfo.setAddPlanList(addPlanList);
			List<AtmPlanInfo> dataList = atmPlanInfoService.getDataList();
			model.addAttribute("dataList", dataList);
			model.addAttribute("atmPlanInfo", atmPlanInfo);
			return "modules/atm/v01/addPlan/manualAddPlanEdit";
		} else {
			addMessage(redirectAttributes, msg.getMessage("message.E4037", null, locale));
		}
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/?repage";
	}
	
	/**
	 * 修改保存
	 * 
	 * @author wxz
	 * @version 2017年12月13日
	 * @param request
	 * @param response
	 * @param model
	 * @return 保存加钞计划列表页面
	 */
	@RequiresPermissions("atm:atmPlanInfo:view")
	@RequestMapping(value = "editSave")
	public String editSave(AtmPlanInfo atmPlan, Model model, RedirectAttributes redirectAttributes) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		
		// 导入加钞计划
		Map<String,Object> map = atmPlanInfoService.editSaveAtmPlan(atmPlan, validator);
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) map.get("msgs");
		StringBuffer msgStr = new StringBuffer();
		// 提示消息组装
		for (String string : msgs) {
				msgStr.append(
					msg.getMessage(string, new String[] { StringUtils.toString(map.get("errorList")) }, locale)									+ "<br/>");
		}
		addMessage(redirectAttributes, msgStr.toString());
		if (msgStr.toString().contains(msg.getMessage("message.I4011", null, locale))) {
			// 导入成功
			String addPlanId = (String) map.get("addPlanId");
			AtmPlanInfo atmInfo = new AtmPlanInfo();
			atmInfo.setAddPlanId(addPlanId);
			// 查询加钞计划
			List<AtmPlanInfo> atmPlanList = atmPlanInfoService.findList(atmInfo);
			// 判断加钞计划是否被删除
			if(!Collections3.isEmpty(atmPlanList)){
				Page<AtmPlanInfo> page = new Page<AtmPlanInfo>();
				// 获取计划详细
				AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
				atmPlanInfo.setAddPlanId(addPlanId);
				page = atmPlanInfoService.addPlanView(page, atmPlanInfo);
				model.addAttribute("page", page);
				atmPlanInfo = atmPlanInfoService.getView(addPlanId);
				// 加钞计划文件标题头
				model.addAttribute("addPlanName", atmPlanInfo.getAddPlanName());
				model.addAttribute("addPlanId", addPlanId);
				model.addAttribute("boxNum", atmPlanInfo.getBoxNum());
				model.addAttribute("amount", atmPlanInfo.getAddAmount());
				// 钞箱类型汇总
				model.addAttribute("boxTypeCollect", atmPlanInfoService.getBoxNumbyAddPlanId(addPlanId));
				model.addAttribute("importSuccess", true);
				model.addAttribute("message", msgStr.toString());
				return "modules/atm/v01/addPlan/addPlanImportForm";
			} else {
				return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/importFile";
			}
		}
		
		return "redirect:" + Global.getAdminPath() + "/atm/v01/atmPlanInfo/importFile";
	}

}