package com.coffer.businesses.modules.store.v01.web;

import java.util.Arrays;
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
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteDetail;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 线路管理Controller
 * 
 * @author niguoyong
 * @version 2015-09-02
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoRouteInfo")
public class StoRouteInfoController extends BaseController {

	@Autowired
	private StoRouteInfoService stoRouteInfoService;
	
	/** 规划线路颜色 */
	private static final String ROUTE_PLAN_COLOR = "routePlanColor";
	/** 车辆经过轨迹颜色 */
	private static final String CAR_TRACK_COLOR = "carTrackColor";
	/** 名称 KEY */
	private static final String KEY_NAME = "name";
	/** 点图片类型 KEY 使用机构类型 */
	private static final String KEY_POINT_TYPE = "pointType";
	/** 经纬度数组 KEY */
	private static final String KEY_LNGLAT = "lnglat";

	@ModelAttribute
	public StoRouteInfo get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			if (stoRouteInfoService.get(id) == null) {
				return new StoRouteInfo();
			} else {
				return stoRouteInfoService.get(id);
			}
		} else {
			return new StoRouteInfo();
		}
	}

	/**
	 * 线路查询
	 * 
	 * @author niguoyong
	 * @date 2015-09-02
	 * 
	 * @Description
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("store:stoRouteInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(StoRouteInfo stoRouteInfo, @RequestParam(required = false) boolean isSearch,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoRouteInfo> page = new Page<StoRouteInfo>(request, response);
		// if (isSearch) {
		User user = UserUtils.getUser();
		// 如果不是系统管理员，只能查看自己所属机构的线路 修改人：xp 修改时间：2017-8-22 begin
		if (!Constant.SysUserType.SYSTEM.equals(user.getUserType())) {
			stoRouteInfo.setCurOffice(user.getOffice());
		}
		// end

		// if (!user.isAdmin()) {
		// stoRouteInfo.setCreateBy(user);
		// }
		page = stoRouteInfoService.findPage(page, stoRouteInfo);
		model.addAttribute("page", page);
		// }
		return "modules/store/v01/stoRouteInfo/stoRouteInfoList";
	}

	@RequiresPermissions("store:stoRouteInfo:view")
	@RequestMapping(value = "form")
	public String form(StoRouteInfo stoRouteInfo, Model model) {
		model.addAttribute("stoRouteInfo", stoRouteInfo);
		List<Office> list = UserUtils.getOfficeList();
		List<Office> listFilter = Lists.newArrayList();
		// 过滤掉人民银行清分中心
		for (Office office : list) {
			if (!(office.getType().equals(Constant.OfficeType.CENTRAL_BANK)
					|| office.getType().equals(Constant.OfficeType.CLEAR_CENTER))) {
				if (office.getType().equals(Constant.OfficeType.COFFER)) {
					office.setParent(UserUtils.getUser().getOffice());
				}
				listFilter.add(office);
			}
		}
		list = listFilter;
		List<Office> routOfficelist = Lists.newArrayList();
		// 验证实体是否为空
		if (stoRouteInfo == null || StringUtils.isBlank(stoRouteInfo.getId())) {
			routOfficelist = stoRouteInfoService.getOfficeNotInRouteOffice(null);
		} else {
			routOfficelist = stoRouteInfoService.getOfficeNotInRouteOffice(stoRouteInfo.getId());
		}
		model.addAttribute("officeList", list);
		model.addAttribute("routOfficelist", routOfficelist);
		return "modules/store/v01/stoRouteInfo/stoRouteInfoForm";
	}

	/**
	 * 线路保存
	 * 
	 * @author niguoyong
	 * @date 2015-09-02
	 * 
	 * @Description
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("store:stoRouteInfo:edit")
	@RequestMapping(value = "save")
	public String save(StoRouteInfo stoRouteInfo, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		// String message = null;
		if (!beanValidator(model, stoRouteInfo) || stoRouteInfo.getStoRouteDetailList().size() == 0) {

			if (stoRouteInfo.getStoRouteDetailList().size() == 0) {
				// 添加明细不能为空
				String message = msg.getMessage("message.E1024", null, locale);
				this.addMessage(model, message);
			}
			return form(stoRouteInfo, model);
		}

		// 如果线路存在，判断是否在业务中
		if (StringUtils.isNotBlank(stoRouteInfo.getId())) {
			// // 通过线路id查询线路
			// StoRouteInfo route =
			// stoRouteInfoService.get(stoRouteInfo.getId());
			// 获得线路明细
			// List<StoRouteDetail> stoRouteDetail =
			// route.getStoRouteDetailList();
			// 传入的线路
			List<StoRouteDetail> routeDetail = stoRouteInfo.getStoRouteDetailList();

			// 调拨信息查询条件
			AllAllocateInfo allocateInfo = new AllAllocateInfo();
			// 调拨种别(上缴和下拨)
			allocateInfo
					.setBusinessTypes(Arrays.asList(new String[] { AllocationConstant.BusinessType.OutBank_Cash_HandIn,
							AllocationConstant.BusinessType.OutBank_Cash_Reservation }));
			// 路线ID
			allocateInfo.setRouteId(stoRouteInfo.getId());
			// 调拨状态（未完成）
			allocateInfo.setStatuses(Arrays.asList(new String[] { AllocationConstant.Status.Register,
					AllocationConstant.Status.HandoverTodo, AllocationConstant.Status.Onload,
					AllocationConstant.Status.BetweenConfirm, AllocationConstant.Status.CashOrderQuotaYes }));
			List<AllAllocateInfo> allocationList = AllocationCommonUtils.findAllocation(allocateInfo);
			List<AllAllocateInfo> allocationFilter = Lists.newArrayList();
			// 过滤掉上缴业务中已扫描的流水
			for (AllAllocateInfo allAllocateInfo : allocationList) {
				if (!(AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfo.getBusinessType())
						&& AllocationConstant.Status.HandoverTodo.equals(allAllocateInfo.getStatus()))) {
					allocationFilter.add(allAllocateInfo);
				}
			}
			allocationList = allocationFilter;
			boolean boo = false;
			String officeName = null;
			for (AllAllocateInfo allAllocate : allocationList) {
				// 如果线路下的某个网点有业务，则不允许撤销此网点
				for (StoRouteDetail detail : routeDetail) {
					if (allAllocate.getrOffice().getId().equals(detail.getOffice().getId())) {
						break;
					} else {
						boo = true;
						officeName = allAllocate.getrOffice().getName();
						break;
					}
				}
				if (boo) {
					break;
				}
			}
			if (boo) {
				String message = msg.getMessage("message.I1032", new Object[] { officeName }, locale);
				addMessage(model, message);
				return form(stoRouteInfo, model);
			} else {
				// // 调拨信息不为空，不可修改该路线
				// if (allocationList != null && allocationList.size() > 0) {
				// String message = msg.getMessage("message.I1031", null,
				// locale);
				// addMessage(redirectAttributes, message);
				// } else {
				// 设置常规线路类型
				stoRouteInfo.setRouteType(Constant.RouteInfo.CONVENTIONAL_ROUTE);
				// 设置归属机构为当前机构
				stoRouteInfo.setCurOffice(UserUtils.getUser().getOffice());
				stoRouteInfoService.save(stoRouteInfo);
				// 保存成功
				String message = msg.getMessage("message.I1007", new Object[] { stoRouteInfo.getRouteName() }, locale);
				addMessage(redirectAttributes, message);
			}
			// }
		} else {
			// 设置常规线路类型
			stoRouteInfo.setRouteType(Constant.RouteInfo.CONVENTIONAL_ROUTE);
			// 设置归属机构为当前机构
			stoRouteInfo.setCurOffice(UserUtils.getUser().getOffice());
			stoRouteInfoService.save(stoRouteInfo);
			// 保存成功
			String message = msg.getMessage("message.I1007", new Object[] { stoRouteInfo.getRouteName() }, locale);
			addMessage(redirectAttributes, message);
		}

		return "redirect:" + Global.getAdminPath() + "/store/v01/stoRouteInfo/list?isSearch=true&repage";
	}

	/**
	 * 返回
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 */
	@RequestMapping(value = "back")
	public String back(StoRouteInfo stoRouteInfo, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoRouteInfo/list?isSearch=true&repage";
	}

	/**
	 * 线路删除
	 * 
	 * @author niguoyong
	 * @date 2015-09-02
	 * 
	 * @Description
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("store:stoRouteInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		// 调拨信息查询条件
		AllAllocateInfo allocateInfo = new AllAllocateInfo();
		// 调拨种别(上缴和下拨)
		allocateInfo.setBusinessTypes(Arrays.asList(new String[] { AllocationConstant.BusinessType.OutBank_Cash_HandIn,
				AllocationConstant.BusinessType.OutBank_Cash_Reservation }));
		// 路线ID
		allocateInfo.setRouteId(id);
		// 调拨状态（未完成）
		allocateInfo.setStatuses(Arrays.asList(new String[] { AllocationConstant.Status.Register,
				AllocationConstant.Status.HandoverTodo, AllocationConstant.Status.Onload,
				AllocationConstant.Status.BetweenConfirm, AllocationConstant.Status.CashOrderQuotaYes }));
		List<AllAllocateInfo> allocationList = AllocationCommonUtils.findAllocation(allocateInfo);
		List<AllAllocateInfo> allocationFilter = Lists.newArrayList();
		// 过滤掉上缴业务中已扫描的流水
		for (AllAllocateInfo allAllocateInfo : allocationList) {
			if (!(AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfo.getBusinessType())
					&& AllocationConstant.Status.HandoverTodo.equals(allAllocateInfo.getStatus()))) {
				allocationFilter.add(allAllocateInfo);
			}
		}
		allocationList = allocationFilter;
		// 调拨信息不为空，不可删除该路线
		if (allocationList != null && allocationList.size() > 0) {
			String message = msg.getMessage("message.I1001", null, locale);
			addMessage(redirectAttributes, message);

		} else {
			// 执行删除
			stoRouteInfoService.delete(id);
			// 删除成功
			String message = msg.getMessage("message.I1008", null, locale);
			addMessage(redirectAttributes, message);
		}

		return "redirect:" + Global.getAdminPath() + "/store/v01/stoRouteInfo/list?isSearch=true&repage";
	}

	/**
	 * 添加网点明细
	 * 
	 * @author niguoyong
	 * @date 2015-09-02
	 * 
	 * @Description
	 * @param stoRouteDetail
	 * @param list
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:stoRouteInfo:edit")
	@ResponseBody
	@RequestMapping(value = "addDetail")
	public String addDetail(StoRouteDetail stoRouteDetail, @ModelAttribute("routeDetailList") List<StoRouteDetail> list,
			Model model) {
		for (StoRouteDetail item : list) {
			if (stoRouteDetail.getOffice().getId().equals(item.getOffice().getId())) {
				// 网点已经存在
				// String message = msg.getMessage("message.E1025", null, null);
				return "";// return this.addMessageByJson(ERROR_MESSAGE_KEY,
							// message);
			}
		}
		stoRouteDetail.setId(IdGen.getIdByTime());
		list.add(stoRouteDetail);
		return "";// this.addMessageByJson("");
	}

	/**
	 * 删除网点明细
	 * 
	 * @author niguoyong
	 * @date 2015-09-02
	 * 
	 * @Description
	 * @param index
	 * @param list
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:stoRouteInfo:edit")
	@ResponseBody
	@RequestMapping(value = "deleteDetail")
	public String deleteDetail(int index, @ModelAttribute("routeDetailList") List<StoRouteDetail> list, Model model) {
		list.remove(index);
		return "";// this.addMessageByJson("");
	}

	/**
	 * 加载明细列表
	 * 
	 * @author niguoyong
	 * @date 2015-09-02
	 * 
	 * @Description
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getList")
	public String getRouteDetailList(Model model) {
		return "modules/store/v01/stoRouteInfo/stoRouteDetailList";
	}

	/**
	 * 查看线路详情
	 * 
	 * @author niguoyong
	 * @date 2015-09-02
	 * 
	 * @Description
	 * @param model
	 * @return
	 */
	@RequiresPermissions("store:stoRouteInfo:view")
	@RequestMapping(value = "see")
	public String see(StoRouteInfo stoRouteInfo, Model model) {
		StoRouteInfo stoRoute = stoRouteInfoService.get(stoRouteInfo);
		List<Office> list = UserUtils.getOfficeList();
		List<Office> routOfficelist = Lists.newArrayList();
		List<StoRouteDetail> StoRouteDetailList = stoRoute.getStoRouteDetailList();
		List<Office> routeOfficelist = Lists.newArrayList();
		// 验证实体是否为空
		if (stoRouteInfo == null || StringUtils.isBlank(stoRouteInfo.getId())) {
			routOfficelist = stoRouteInfoService.getOfficeNotInRouteOffice(null);
		} else {
			routOfficelist = stoRouteInfoService.getOfficeNotInRouteOffice(stoRouteInfo.getId());
		}
		// 对机构进行过滤
		boolean boo = true;
		for (Office office : list) {
			for (StoRouteDetail stoRouteDetail : StoRouteDetailList) {
				Office stoRouteOffice = SysCommonUtils.findOfficeById(stoRouteDetail.getOffice().getId());
				if (office.getId().equals(stoRouteOffice.getId())
						|| office.getId().equals(stoRouteOffice.getParentId())) {
					boo = true;
					break;
				} else {
					boo = false;
				}
			}
			if (boo) {
				routeOfficelist.add(office);
			}
		}
		model.addAttribute("officeList", routeOfficelist);
		model.addAttribute("routOfficelist", routOfficelist);

		model.addAttribute("stoRouteInfo", stoRoute);
		return "modules/store/v01/stoRouteInfo/stoRouteInfoView";
	}
	
	/**
	 * 
	 * Title: getRoutePlanning
	 * <p>Description: 线路规划数据</p>
	 * @author:     yanbingxu
	 * @param routeId
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "getRoutePlanning")
	@ResponseBody
	public String getRoutePlanning(String routeId) {
		// 数据初始化
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> longitudeList = Lists.newArrayList();
		List<String> latitudeList = Lists.newArrayList();
		List<String> lnglatList = Lists.newArrayList();
		List<Map<String, Object>> officeList = Lists.newArrayList();
		Map<String, Object> officeMap = Maps.newHashMap();
		Boolean defaultPlan = true;
		
		if (StringUtils.isNotEmpty(get(routeId).getId())) {
			// 起点金库机构数据
			lnglatList.add(get(routeId).getCurOffice().getLongitude());
			lnglatList.add(get(routeId).getCurOffice().getLatitude());
			officeMap.put(KEY_NAME, get(routeId).getCurOffice().getName());
			officeMap.put(KEY_LNGLAT, lnglatList);
			officeMap.put(KEY_POINT_TYPE, get(routeId).getCurOffice().getType());
			officeList.add(officeMap);
			// 途径网点机构数据
			for (StoRouteDetail routeDetail : get(routeId).getStoRouteDetailList()) {
				officeMap = Maps.newHashMap();
				lnglatList = Lists.newArrayList();
				lnglatList.add(routeDetail.getOffice().getLongitude());
				lnglatList.add(routeDetail.getOffice().getLatitude());
				officeMap.put(KEY_NAME, routeDetail.getOffice().getName());
				officeMap.put(KEY_LNGLAT, lnglatList);
				officeMap.put(KEY_POINT_TYPE, Constant.OfficeType.OUTLETS);
				officeList.add(officeMap);
			}
			// 终点金库机构数据
			officeMap = Maps.newHashMap();
			lnglatList = Lists.newArrayList();
			lnglatList.add(get(routeId).getCurOffice().getLongitude());
			lnglatList.add(get(routeId).getCurOffice().getLatitude());
			officeMap.put(KEY_NAME, get(routeId).getCurOffice().getName());
			officeMap.put(KEY_LNGLAT, lnglatList);
			officeMap.put(KEY_POINT_TYPE, get(routeId).getCurOffice().getType());
			officeList.add(officeMap);
			// 规划后路线坐标
			if (StringUtils.isNotEmpty(get(routeId).getRouteLnglat())) {
				String[] params = get(routeId).getRouteLnglat().split(Constant.Punctuation.COMMA);
				for (int iCount = 0; iCount < params.length; iCount++) {
					if (iCount % 2 == 0) {
						longitudeList.add(params[iCount]);
					} else {
						latitudeList.add(params[iCount]);
					}
				}
				defaultPlan = false;
			}
			// 规划线路颜色
			if (StringUtils.isNotEmpty(get(routeId).getRoutePlanColor())) {
				resultMap.put(ROUTE_PLAN_COLOR, get(routeId).getRoutePlanColor());
			} else {
				resultMap.put(ROUTE_PLAN_COLOR, "00FF00");
			}
			// 车辆经过轨迹颜色
			if (StringUtils.isNotEmpty(get(routeId).getCarTrackColor())) {
				resultMap.put(CAR_TRACK_COLOR, get(routeId).getCarTrackColor());
			} else {
				resultMap.put(CAR_TRACK_COLOR, "888888");
			}
		}
		resultMap.put("officeList", officeList);
		resultMap.put("defaultPlan", defaultPlan);
		resultMap.put("longitudeList", longitudeList);
		resultMap.put("latitudeList", latitudeList);
		return gson.toJson(resultMap);
	}
}
