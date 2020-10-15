package com.coffer.businesses.modules.store.v01.web;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.atm.AtmCommonUtils;
import com.coffer.businesses.modules.atm.v01.entity.AtmBoxMod;
import com.coffer.businesses.modules.store.v01.entity.StoBoxDetail;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfoHistory;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.stoBoxInfoHistoryService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * 箱袋信息管理Controller
 * 
 * @author niguoyong
 * @version 2015-09-01
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoBoxInfo")
public class StoBoxInfoController extends BaseController {

	@Autowired
	private StoBoxInfoService boxService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private stoBoxInfoHistoryService boxHistoryService;
	@Autowired
	private AllocationService allocationService;

	@ModelAttribute
	public StoBoxInfo get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return boxService.get(id);
		} else {
			return new StoBoxInfo();
		}
	}

	/**
	 * 箱袋信息检索
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 */
	@RequiresPermissions("store:stoBoxInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(StoBoxInfo stoBoxInfo, @RequestParam(required = false) boolean isSearch,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StoBoxInfo> page = new Page<StoBoxInfo>(request, response);
		// 点击查询
		// if (isSearch) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()) {
			stoBoxInfo.setCreateBy(user);
		}
		// 小车时箱袋状态编辑
		if (Constant.BoxType.BOX_CAR.equals(stoBoxInfo.getBoxType())) {
			stoBoxInfo.setBoxStatus(stoBoxInfo.getCarStatus());
		} else if (Constant.BoxType.BOX_NOTE.equals(stoBoxInfo.getBoxType())) {
			stoBoxInfo.setBoxStatus(stoBoxInfo.getAtmBoxStatus());
		}
		/* 追加如果箱袋类型是钞箱或者是小车 修改人：sg 修改日期：2017-11-03 begin */
		String[] atmCar = Global.getConfig("sto.box.boxtype.atmShow").split(";");
		for (int i = 0; i < atmCar.length; i++) {
			if (atmCar[i].equals(stoBoxInfo.getBoxType())) {
				stoBoxInfo.setBoxStatus(stoBoxInfo.getAtmBoxStatus());
				break;
			}
		}
		/* end */
		/* 如果没有箱袋类型是所有钞箱状态 修改人：xl 修改日期：2018-01-03 begin */
		if (StringUtils.isBlank(stoBoxInfo.getBoxType())) {
			stoBoxInfo.setBoxStatus(stoBoxInfo.getAllBoxStatus());
		}
		/* end */
		stoBoxInfo.setId(stoBoxInfo.getSearchBoxNo());
		page = boxService.findPage(page, stoBoxInfo);
		model.addAttribute("page", page);
		// }
		return "modules/store/v01/stoBoxInfo/stoBoxInfoList";
	}

	/**
	 * 款箱信息添加
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 */
	@RequiresPermissions("store:stoBoxInfo:view")
	@RequestMapping(value = "form")
	public String form(StoBoxInfo stoBoxInfo, Model model) {
		model.addAttribute("stoBoxInfo", stoBoxInfo);
		return "modules/store/v01/stoBoxInfo/stoBoxInfoForm";
		/* end */
	}

	/**
	 * 钞箱信息添加
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 */
	@RequiresPermissions("store:stoBoxInfo:view")
	@RequestMapping(value = "formATM")
	public String formATM(StoBoxInfo stoBoxInfo, Model model) {
		/* 跳转到新的form页 修改人:sg 修改日期:2017-11-01 begin */
		if (stoBoxInfo.getOffice() == null) {
			// 获取当前登录人机构
			User user = UserUtils.getUser();
			stoBoxInfo.setOffice(user.getOffice());
		}
		model.addAttribute("stoBoxInfo", stoBoxInfo);
		return "modules/store/v01/stoBoxInfo/stoATMBoxInfoForm";
		/* end */
	}

	/* 修改人:sg 修改日期:2017-11-01 begin */
	/**
	 * 箱袋信息保存
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 */

	 @RequiresPermissions("store:stoBoxInfo:edit")
	 @RequestMapping(value = "save")
	public String save(StoBoxInfo box, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, box)) {
			return form(box, model);
		}
		// 箱袋信息保存
		int valiadateNum = boxService.saveInfo(box, UserUtils.getUser().getOffice());
		String message = "";
		if (valiadateNum < 0) {
			Locale locale = LocaleContextHolder.getLocale();
			message = msg.getMessage("message.I1002", null, locale);
		} else {
			String boxTypeValue = DictUtils.getDictLabel(box.getBoxType(), "sto_box_type", "");
			message = msg.getMessage("message.E1015", new String[] { boxTypeValue, String.valueOf(valiadateNum) },
					null);
		}
		/** end **/
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoBoxInfo/list?isSearch=true&repage";
	 }
	/* end */
	/**
	 * @author sg
	 * @date 2017-11-02
	 * 
	 * @Description 保存钞箱信息
	 * @param 参数
	 */
	@RequiresPermissions("store:stoBoxInfo:edit")
	@RequestMapping(value = "saveATM")
	public String saveATM(StoBoxInfo box, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, box)) {
			return form(box, model);
		}
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";

		try {
			// 一致性检验
			if (box.getId() != null) {
				boxService.checkVersion(box);
			}
			// 箱袋信息保存
			int valiadateNum = boxService.saveInfo(box, UserUtils.getUser().getOffice());

			if (valiadateNum < 0) {
				// 保存成功
				message = msg.getMessage("message.I1002", null, locale);
			} else {
				// 保存失败
				String boxTypeValue = DictUtils.getDictLabel(box.getBoxType(), "sto_box_type", "");
				message = msg.getMessage("message.E1015", new String[] { boxTypeValue, String.valueOf(valiadateNum) },
						null);
			}
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			/*
			 * addMessage(model, message); return form(box, model);
			 */
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoBoxInfo/list?isSearch=true&repage";
	}

	/**
	 * 返回
	 * 
	 * @author niguoyong
	 * @version 2015-09-23
	 */
	@RequestMapping(value = "back")
	public String back(StoBoxInfo box, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoBoxInfo/list?isSearch=true&repage";
	}

	/**
	 * 箱袋信息删除
	 * 
	 * @author niguoyong
	 * @version 2015-09-01
	 */
	@RequiresPermissions("store:stoBoxInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(@RequestParam String id, RedirectAttributes redirectAttributes) {
		String message = "";
		// 语言环境
		Locale locale = LocaleContextHolder.getLocale();
		AllAllocateDetail allAllocateDetail = new AllAllocateDetail();
		allAllocateDetail.setBoxNo(id);
		// 通过箱袋编号查找调拨明细信息
		List<AllAllocateDetail> allAllocateDetailList = allocationService.findAllocateDetailByNo(allAllocateDetail);
		// 该箱袋没有调拨信息,可以执行删除操作
		if (allAllocateDetailList.size() == 0) {
			// 箱袋表和箱袋历史表删除记录数
			int flag = boxService.realDelete(id);
			int falg = boxHistoryService.realDeleteHistory(id);
			// 记录数大于0，删除成功
			if (flag > 0 && falg > 0) {
				message = msg.getMessage("message.I1003", new Object[] { id }, locale);
			}
			// 记录数等于0，删除失败，或数据早已被删除
			else {
				message = msg.getMessage("message.E1016", new Object[] { id }, locale);
			}
		}
		// 箱袋存在调拨信息，不可删除
		else {
			message = msg.getMessage("message.E1066", new Object[] { id }, locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoBoxInfo/list?isSearch=true&repage";
	}

	/**
	 * 根据钞箱类型获取钞箱配置信息
	 * 
	 * @author niguoyong
	 * @version 2015-09-15
	 * 
	 * 
	 * @param boxType
	 * @return
	 */
	@RequiresPermissions("store:stoBoxInfo:edit")
	@RequestMapping(value = "getAtmBoxMod")
	@ResponseBody
	public String getAtmBoxModInfo(String atmBoxType) {
		Map<String, List<AtmBoxMod>> map = Maps.newHashMap();
		List<AtmBoxMod> list = Lists.newArrayList();
		if (StringUtils.isNotBlank(atmBoxType)) {
			// atmBoxType.substring(1);
			/* 将钞箱类型从41,42,43,44转化成1,2,3,4修改人：sg 修改时间：2017-11-02 begin */
			list = AtmCommonUtils.getAtmBoxModInfo(atmBoxType.substring(1));
			/* end */
		}
		map.put("atmBoxMods", list);
		return new Gson().toJson(map);
	}

	/**
	 * 根据箱号查询箱袋信息
	 * 
	 * @author xp
	 * @version 2017-7-5
	 * 
	 */
	@RequestMapping(value = "getStoBoxDetail")
	public String getStoBoxDetail(StoBoxDetail stoBoxDetail, Model model) {
		List<StoBoxDetail> boxDetailList = boxService.findStoBoxDetailList(stoBoxDetail);
		model.addAttribute("boxDetailList", boxDetailList);
		model.addAttribute("boxNo", stoBoxDetail);
		return "modules/store/v01/stoBoxInfo/stoBoxInfoShowDetail";
	}

	/**
	 * 显示箱袋状态页面
	 * 
	 * @author xp
	 * @version 2017-7-10
	 * 
	 */
	@RequestMapping(value = "stoBoxInfoShowBoxStatus")
	/* 追加箱袋类型boxType 修改人：sg 修改日期：2017-11-03 */
	public String stoBoxInfoShowBoxStatus(@RequestParam String boxNo, @RequestParam String boxStatus,
			@RequestParam String boxType, Model model) {
		StoBoxInfo stoBoxInfo = new StoBoxInfo();
		stoBoxInfo.setId(boxNo);
		stoBoxInfo.setBoxStatus(boxStatus);
		stoBoxInfo.setBoxType(boxType);
		/* end */
		StoBoxInfoHistory stoBoxInfoHistory = new StoBoxInfoHistory();
		stoBoxInfoHistory.setStoBoxInfo(stoBoxInfo);
		model.addAttribute("stoBoxInfoHistory", stoBoxInfoHistory);
		return "modules/store/v01/stoBoxInfo/stoBoxInfoShowBoxStatus";
	}

	/**
	 * 授权人修改箱袋状态
	 * 
	 * @author xp
	 * @version 2017-7-12
	 * 
	 */
	@RequestMapping(value = "stoBoxInfoUpdateStatus")
	public String stoBoxInfoUpdateStatus(HttpServletRequest request, RedirectAttributes redirectAttributes) {
		// 验证用户是否存在,验证用户类型，只有金库管理员或系统管理员才可更改状态
		User LoginUser = new User();
		LoginUser.setLoginName(request.getParameter("authorizer"));
		User user = UserUtils.getByLoginName(request.getParameter("authorizer"));
		if (user == null
				|| !user.getPassword().equals(SystemService.entryptPassword(request.getParameter("authorizeBy")))) {
			addMessage(redirectAttributes, "失败：用户不存在或密码错误");
			return "redirect:" + adminPath + "/store/v01/stoBoxInfo/list";
		} else if (!(user.getUserType().equals(Constant.SysUserType.SYSTEM)
				|| user.getUserType().equals(Constant.SysUserType.COFFER_MANAGER))) {
			addMessage(redirectAttributes, "失败：用户权限不足");
			return "redirect:" + adminPath + "/store/v01/stoBoxInfo/list";
		} else {
			// 设置箱袋基本信息
			LoginUser.setId(user.getId());
			StoBoxInfo stoBoxInfo = boxService.get(request.getParameter("boxNo"));
			// 设置箱袋授权信息
			StoBoxInfoHistory stoBoxInfoHistory = new StoBoxInfoHistory();
			stoBoxInfo.setBoxStatus(request.getParameter("boxStatus"));
			stoBoxInfoHistory.setUpdateBoxStatus(request.getParameter("boxStatus"));
			stoBoxInfoHistory.setAuthorizer(user.getName());
			stoBoxInfoHistory.setPrivilegedTime(new Date());
			stoBoxInfoHistory.setAuthorizeBy(LoginUser);
			stoBoxInfo.setUpdateBy(UserUtils.getUser());
			stoBoxInfo.setUpdateDate(new Date());
			stoBoxInfoHistory.setStoBoxInfo(stoBoxInfo);
			// 向sto_box_info_history中插入数据
			boxHistoryService.insertAuthorizerInfo(stoBoxInfoHistory);
			// 向sto_box_info中修改数据
			boxHistoryService.saveOrUpdate(stoBoxInfo);
			// 如果更改后状态为空箱，要删除箱袋明细
			if (Constant.BoxStatus.EMPTY.equals(stoBoxInfo.getBoxStatus())) {
				boxService.deleteBoxDetail(stoBoxInfo.getId());
			}
			// 返回信息
			addMessage(redirectAttributes, "箱袋 " + stoBoxInfo.getId() + "状态更新成功");
			return "redirect:" + adminPath + "/store/v01/stoBoxInfo/list";
		}
	}

	/**
	 * 跳到箱袋调拨历史页面
	 * 
	 * @author SongYuanYang
	 * @version 2017年9月13日
	 * 
	 * @param rfid
	 * @param boxNo
	 * @param model
	 * @return 箱袋调拨历史页面
	 */
	@RequestMapping(value = "getStoBoxHistoryGraph")
	public String getStoBoxHistoryGraph(StoBoxInfoHistory stoBoxInfoHistory,
			@RequestParam(value = "rfid", required = false) String rfid,
			@RequestParam(value = "boxNo", required = false) String boxNo, Model model) {
		if (stoBoxInfoHistory.getCreateTimeStart() == null || stoBoxInfoHistory.getCreateTimeEnd() == null) {
			Date endDate = new Date();
			Date beginDate = DateUtils.addDate(endDate, -2);
			stoBoxInfoHistory.setCreateTimeStart(beginDate);
			stoBoxInfoHistory.setCreateTimeEnd(endDate);
		}
		// 查询条件：开始时间
		if (stoBoxInfoHistory.getCreateTimeStart() != null) {
			stoBoxInfoHistory.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(stoBoxInfoHistory.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (stoBoxInfoHistory.getCreateTimeEnd() != null) {
			stoBoxInfoHistory.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(stoBoxInfoHistory.getCreateTimeEnd())));
		}
		// 设置查询条件
		StoBoxInfo stoBoxInfo = new StoBoxInfo();
		stoBoxInfo.setRfid(rfid);
		stoBoxInfo.setId(boxNo);
		stoBoxInfoHistory.setStoBoxInfo(stoBoxInfo);
		// 取得箱袋历史信息
		List<StoBoxInfoHistory> historyList = boxHistoryService.getStoBoxHistory(stoBoxInfoHistory);
		// 设置RFID及箱号
		model.addAttribute("rfid", rfid);
		model.addAttribute("boxNo", boxNo);
		// 取得首条信息
		if (!Collections3.isEmpty(historyList)) {
			StoBoxInfoHistory firstData = historyList.get(0);
			Office office = officeService.get(firstData.getStoBoxInfo().getOffice().getId());
			model.addAttribute("initBindOfficeName", office.getName());
			model.addAttribute("initBindDate", firstData.getStoBoxInfo().getCreateDate());
			model.addAttribute("initBindName", firstData.getStoBoxInfo().getCreateName());
			model.addAttribute("stoBoxInfoHistory", stoBoxInfoHistory);
			model.addAttribute("historyList", historyList);
		}
		return "/modules/store/v01/stoBoxInfo/stoBoxHistoryGraph";
	}

	// public String stoBoxInfoSearchUser(@RequestParam String boxNo,
	// @RequestParam String boxStatus,
	// @RequestParam String updateName, @RequestParam String password,
	// HttpServletRequest request,
	// RedirectAttributes redirectAttributes) {
	// public String stoBoxInfoSearchUser(StoBoxInfo stoBoxInfo,
	// RedirectAttributes redirectAttributes) {
	//
	// User LoginUser = stoBoxInfo.getUpdateBy();
	// LoginUser.setLoginName(stoBoxInfo.getUpdateName());
	// User user = userDao.getByLoginName(LoginUser);
	// if (user == null || !user.getPassword().equals(LoginUser.getPassword()))
	// {
	// addMessage(redirectAttributes, "用户信息错误");
	// return "redirect:" + adminPath + "/store/v01/stoBoxInfo/list";
	// } else {
	// LoginUser.setId(user.getId());
	// stoBoxInfo.setUpdateBy(LoginUser);
	// boxService.saveOrUpdate(stoBoxInfo);
	// addMessage(redirectAttributes, "箱袋'" + stoBoxInfo.getId() + "'更新成功");
	// return "redirect:" + adminPath + "/store/v01/stoBoxInfo/list";
	// }
	// }
	
	/**
	 * 箱袋精确信息添加页面
	 * 
	 * @author XL
	 * @version 2018-12-18
	 */
	@RequiresPermissions("store:stoBoxInfo:view")
	@RequestMapping(value = "formAccurate")
	public String formAccurate(StoBoxInfo stoBoxInfo, Model model) {
		model.addAttribute("stoBoxInfo", stoBoxInfo);
		return "modules/store/v01/stoBoxInfo/stoBoxAccurateInfoForm";
	}

	/**
	 * 箱袋精确信息保存
	 * 
	 * @author XL
	 * @version 2018-12-18
	 */
	@RequiresPermissions("store:stoBoxInfo:edit")
	@RequestMapping(value = "saveAccurate")
	public String saveAccurate(StoBoxInfo box, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		// 提示信息
		String message = "";
		// 箱袋编号
		String boxNo = BusinessUtils.fillBoxNo(box.getSearchBoxNo());		
		// 箱袋编号是否存在
		if (boxService.get(boxNo) != null) {
			// 该编号存在，创建失败
			message = msg.getMessage("message.E1094", new String[] { boxNo }, locale);
			addMessage(model, message);
			return formAccurate(box, model);
		}
		box.setId(boxNo);
		box.setRfid(BusinessUtils.fillOfficeId(box.getOffice().getId()) + boxNo);
		box.setIsNewRecord(true);
		box.setBoxStatus(Constant.BoxStatus.EMPTY);
		box.setDelFlag(StoEscortInfo.DEL_FLAG_AUDIT);
		// 箱袋信息保存
		boxService.save(box);
		message = msg.getMessage("message.I1002", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/store/v01/stoBoxInfo/list?isSearch=true&repage";
	}
}
