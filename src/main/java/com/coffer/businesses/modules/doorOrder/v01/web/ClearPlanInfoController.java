package com.coffer.businesses.modules.doorOrder.v01.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearGroupUserInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanDetail;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearPlanUserDetail;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearGroupMainService;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearPlanDetailService;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearPlanInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearPlanUserDetailService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 清机主表Controller
 * 
 * @author XL
 * @version 2019-06-26
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/clearPlanInfo")
public class ClearPlanInfoController extends BaseController {

	@Autowired
	private ClearPlanInfoService clearPlanInfoService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private ClearPlanDetailService clearPlanDetailService;

	@Autowired
	private ClearGroupMainService clearGroupMainService;

	@Autowired
	private ClearPlanUserDetailService clearPlanUserDetailService;

	@ModelAttribute
	public ClearPlanInfo get(@RequestParam(required = false) String id) {
		ClearPlanInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = clearPlanInfoService.get(id);
		}
		if (entity == null) {
			entity = new ClearPlanInfo();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:clearPlanInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClearPlanInfo clearPlanInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		/* 初始化开始时间和结束时间为当前时间 */
		// if (clearPlanInfo.getCreateTimeStart() == null) {
		// clearPlanInfo.setCreateTimeStart(new Date());
		// }
		// if (clearPlanInfo.getCreateTimeEnd() == null) {
		// clearPlanInfo.setCreateTimeEnd(new Date());
		// }
		Page<ClearPlanInfo> page = clearPlanInfoService.findClearPage(new Page<ClearPlanInfo>(request, response),
				clearPlanInfo);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/clearPlanInfo/clearPlanInfoList";
	}

	@RequiresPermissions("doorOrder:v01:clearPlanInfo:view")
	@RequestMapping(value = "form")
	public String form(ClearPlanInfo clearPlanInfo, Model model) {
		// 修改页面
		if (clearPlanInfo != null && StringUtils.isNotBlank(clearPlanInfo.getId())) {
			ClearPlanUserDetail clearPlanUserDetail = new ClearPlanUserDetail();
			clearPlanUserDetail.setPlanId(clearPlanInfo.getPlanId());
			// 清机人员列表
			clearPlanInfo.setClearPlanUserDetailList(clearPlanUserDetailService.findList(clearPlanUserDetail));
			for (ClearPlanUserDetail clearPlanUser : clearPlanInfo.getClearPlanUserDetailList()) {
				clearPlanUser.setId(StoreCommonUtils.findByUserId(clearPlanUser.getUser().getId()).getId());
			}
			// 机具列表
			clearPlanInfo.getClearPlanInfoList().add(clearPlanInfo);
		}
		// 清机任务
		ClearPlanInfo clearPlan = new ClearPlanInfo();
		// 清分中心
		clearPlan.setVinOffice(UserUtils.getUser().getOffice());
		// 未完成
		clearPlan.setStatus(Constant.ClearPlanStatus.UNCOMPLETE);
		// 清机任务列表
		List<ClearPlanInfo> clearPlanList = clearPlanInfoService.findList(clearPlan);
		// 机具详细信息显示
		for (ClearPlanInfo clPlan : clearPlanList) {
			EquipmentInfo equipmentInfo = DoorCommonUtils.getTimeDistanceLastTime(clPlan.getEquipmentId(),clPlan.getId());
			clPlan.setEquipmentName(equipmentInfo.getSeriesNumber() + "__" + equipmentInfo.getaOffice().getName() + "__"
					+ equipmentInfo.getDistanceLastTime());
		}
		model.addAttribute("clearPlanList", clearPlanList);
		model.addAttribute("clearPlanInfo", clearPlanInfo);
		return "modules/doorOrder/v01/clearPlanInfo/clearPlanInfoForm";
	}

	@RequiresPermissions("doorOrder:v01:clearPlanInfo:edit")
	@Transactional(readOnly = false)
	@RequestMapping(value = "save")
	public String save(ClearPlanInfo clearPlanInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, clearPlanInfo)) {
			return form(clearPlanInfo, model);
		}

		// 设置清机主表信息及保存
		String planId = IdGen.uuid();
		clearPlanInfo.setPlanId(planId);
		clearPlanInfo.setStatus(Constant.ClearPlanStatus.UNCOMPLETE);
		clearPlanInfo.setDelFlag(Constant.deleteFlag.Valid);
		clearPlanInfoService.save(clearPlanInfo);
		// 判断门店列表是否为空
		if (Collections3.isEmpty(clearPlanInfo.getDoorList())) {
			throw new BusinessException("message.E7208", "", new String[] {});
		}
		// 设置清机明细表信息及保存
		// ClearPlanInfo clearInfo = new ClearPlanInfo();
		// List<ClearPlanDetail> detailList = Lists.newArrayList();
		for (Office doorDetail : clearPlanInfo.getDoorList()) {
			if (StringUtils.isBlank(doorDetail.getId())) {
				throw new BusinessException("message.E7205", "", new String[] {});
			}
			// 根据门店ID查询门店名称
			String doorName = officeService.getOfficeNameById(doorDetail.getId());
			ClearPlanDetail clearPlanDetail = new ClearPlanDetail();
			// clearPlanDetail.preInsert();
			clearPlanDetail.setClearNo(planId);
			clearPlanDetail.setStatus(Constant.ClearPlanStatus.UNCOMPLETE);
			clearPlanDetail.setDoorId(doorDetail.getId());
			clearPlanDetail.setDoorName(doorName);
			clearPlanDetail.setClearManNo(clearPlanInfo.getClearManNo());
			clearPlanDetail.setClearManName(clearPlanInfo.getClearManName());
			clearPlanDetail.setDelFlag(Constant.deleteFlag.Valid);
			// detailList.add(clearPlanDetail);
			clearPlanDetailService.save(clearPlanDetail);
		}
		// clearInfo.setBatchList(detailList);
		// 批量插入清机任务明细
		// clearPlanInfoService.batchInsert(clearInfo);
		addMessage(redirectAttributes, "保存清机任务成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearPlanInfo/?repage";
	}

	@RequiresPermissions("doorOrder:v01:clearPlanInfo:edit")
	@Transactional(readOnly = false)
	@RequestMapping(value = "saveTemp")
	public String saveTemp(ClearPlanInfo clearPlanInfo, Model model, RedirectAttributes redirectAttributes) {
		// 清机任务列表
		for (ClearPlanInfo clearPlan : clearPlanInfo.getClearPlanInfoList()) {
			// 查询原任务
			ClearPlanInfo clearPlanTemp = clearPlanInfoService.get(clearPlan);
			// 清空清机组
			clearPlanTemp.setClearingGroupId(null);
			// 任务类型（临时任务）
			clearPlanTemp.setPlanType(DoorOrderConstant.PlanType.TEMPORARILY);
			// 更新任务主表
			clearPlanInfoService.save(clearPlanTemp);
			// 清空原清机人员明细
			clearPlanUserDetailService.deleteByPlanId(clearPlanTemp.getPlanId());
			// 清机人员明细
			List<ClearPlanUserDetail> clearPlanUserDetailList = clearPlanInfo.getClearPlanUserDetailList();
			for (ClearPlanUserDetail clearPlanUserDetail : clearPlanUserDetailList) {
				ClearPlanUserDetail clearPlanUserDetailInfo = new ClearPlanUserDetail();
				// 主键
				clearPlanUserDetailInfo.setId(IdGen.uuid());
				// 新纪录
				clearPlanUserDetailInfo.setIsNewRecord(true);
				// 清机计划编号
				clearPlanUserDetailInfo.setPlanId(clearPlanTemp.getPlanId());
				// 人员信息
				clearPlanUserDetailInfo.setUser(StoreCommonUtils.getEscortById(clearPlanUserDetail.getId()).getUser());
				clearPlanUserDetailService.save(clearPlanUserDetailInfo);
			}
		}
		addMessage(redirectAttributes, "保存清机任务成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearPlanInfo/?repage";
	}

	@RequiresPermissions("doorOrder:v01:clearPlanInfo:edit")
	@Transactional(readOnly = false)
	@RequestMapping(value = "delete")
	public String delete(ClearPlanInfo clearPlanInfo, RedirectAttributes redirectAttributes) {
		if (StringUtils.isBlank(clearPlanInfo.getPlanId())) {
			throw new BusinessException("message.E7209", "", new String[] {});
		}
		ClearPlanDetail clearPlanDetail = new ClearPlanDetail();
		clearPlanDetail.setClearNo(clearPlanInfo.getPlanId());
		clearPlanDetail.setDelFlag(Constant.deleteFlag.Invalid);
		// 删除清机主表任务
		clearPlanInfoService.delete(clearPlanInfo);
		// 删除清机明细表任务
		clearPlanDetailService.delete(clearPlanDetail);
		addMessage(redirectAttributes, "删除清机任务成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearPlanInfo/?repage";
	}

	/**
	 * 查看清机任务明细
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "detail")
	public String detail(ClearPlanInfo clearPlanInfo, HttpServletResponse response, HttpServletRequest request,
			Model model) {
		// 固定任务
		if (clearPlanInfo != null && DoorOrderConstant.PlanType.CERTAINLY.equals(clearPlanInfo.getPlanType())) {
			// 清机组人员列表
			for (ClearGroupUserInfo clearGroupUserInfo : clearGroupMainService
					.findUserByGroupId(clearPlanInfo.getClearingGroupId())) {
				ClearPlanUserDetail clearPlanUserDetail = new ClearPlanUserDetail();
				clearPlanUserDetail.setId(clearGroupUserInfo.getUserId());
				clearPlanInfo.getClearPlanUserDetailList().add(clearPlanUserDetail);
			}

		} else {
			// 临时任务
			ClearPlanUserDetail clearPlanUserDetail = new ClearPlanUserDetail();
			clearPlanUserDetail.setPlanId(clearPlanInfo.getPlanId());
			// 查询清机人员明细
			clearPlanInfo.setClearPlanUserDetailList(clearPlanUserDetailService.findList(clearPlanUserDetail));
			for (ClearPlanUserDetail clearPlanUser : clearPlanInfo.getClearPlanUserDetailList()) {
				clearPlanUser.setId(StoreCommonUtils.findByUserId(clearPlanUser.getUser().getId()).getId());
			}
		}
		// 机具信息
		clearPlanInfo.getClearPlanInfoList().add(clearPlanInfo);
		// 清机列表
		ClearPlanInfo clearPlan = new ClearPlanInfo();
		clearPlan.setId(clearPlanInfo.getId());
		clearPlan.setVinOffice(UserUtils.getUser().getOffice());
		List<ClearPlanInfo> clearPlanList = clearPlanInfoService.findList(clearPlan);
		// 机具详细信息显示
		for (ClearPlanInfo clPlan : clearPlanList) {
			EquipmentInfo equipmentInfo = DoorCommonUtils.getTimeDistanceLastTime(clPlan.getEquipmentId(),clPlan.getId());
			clPlan.setEquipmentName(equipmentInfo.getSeriesNumber() + "__" + equipmentInfo.getaOffice().getName() + "__"
					+ equipmentInfo.getDistanceLastTime());
		}
		model.addAttribute("clearPlanList", clearPlanList);
		model.addAttribute("clearPlanInfo", clearPlanInfo);
		return "modules/doorOrder/v01/clearPlanInfo/clearPlanInfoDetail";
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
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/clearPlanInfo/?repage";
	}

}