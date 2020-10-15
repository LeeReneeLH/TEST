package com.coffer.businesses.modules.doorOrder.v01.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderException;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorOrderExceptionService;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.service.StoDictService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Maps;

/**
 * 存款异常信息Controller
 * 
 * @author ZXK
 * @version 2019-11-11
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/doorOrderException")
public class DoorOrderExceptionController extends BaseController {

	@Autowired
	private StoDictService stoDictService;
	@Autowired
	private DoorOrderExceptionService doorOrderExceptionService;
	@Autowired
	private OfficeDao officeDao;

	@ModelAttribute
	public DoorOrderException get(@RequestParam(required = false) String id) {
		DoorOrderException entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = doorOrderExceptionService.get(id);
		}
		if (entity == null) {
			entity = new DoorOrderException();
		}
		return entity;
	}

	@RequiresPermissions("doorOrder:v01:doorOrderException:view")
	@RequestMapping(value = { "list", "" })
	public String list(DoorOrderException doorOrderException, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		User userInfo = UserUtils.getUser();
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			doorOrderException.getSqlMap().put("dsf2", "LEFT JOIN sys_office office ON office.ID = u9.office_id ");// 查询存款人对应的部门parent_ids
			doorOrderException.getSqlMap().put("dsf",
					"AND (a.door_id =" + userInfo.getOffice().getId() + " OR office.parent_ids LIKE '%"
							+ userInfo.getOffice().getParentId() + "%'" + " OR o2.parent_ids LIKE '%"
							+ userInfo.getOffice().getParentId() + "%')");// 查询出当机具没有绑定时
																			// 但存在存款人
																			// 根据存款人所在机构
																			// 查询存款异常信息
		} else if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.DIGITAL_PLATFORM)) {// 判断是否是平台登录
			// 如果是平台登录 显示出无机具无用户的特殊情况的存款异常
			doorOrderException.getSqlMap().put("dsf",
					"AND (a.door_id =" + userInfo.getOffice().getId() + " OR a.door_id IS NULL " + " OR a.door_id ='' "
							+ " OR o2.parent_ids LIKE '%" + userInfo.getOffice().getId() + "%')");
		} else {
			doorOrderException.getSqlMap().put("dsf2", "LEFT JOIN sys_office office ON office.ID = u9.office_id ");// 查询存款人对应的部门parent_ids
			doorOrderException.getSqlMap().put("dsf",
					"AND (a.door_id =" + userInfo.getOffice().getId() + " OR office.parent_ids LIKE '%"
							+ userInfo.getOffice().getId() + "%'" + " OR o2.parent_ids LIKE '%"
							+ userInfo.getOffice().getId() + "%')");// 查询出当机具未绑定时
																	// 但存在存款人
																	// 根据存款人所在机构
																	// 查询存款异常信息
		}
		/** 中心页面跳转 默认查询当天记录 by:zxk 2020-9-21*/
		if ("1".equals(doorOrderException.getPageSkipFlag())) {
			doorOrderException.setCreateTimeStart(new Date());
			doorOrderException.setCreateTimeEnd(new Date());
		}

		Page<DoorOrderException> page = doorOrderExceptionService
				.findPage(new Page<DoorOrderException>(request, response), doorOrderException);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/doorOrderException/doorOrderExceptionList";
	}

	@RequiresPermissions("doorOrder:v01:doorOrderException:view")
	@RequestMapping(value = "form")
	public String form(DoorOrderException doorOrderException, Model model) {
		return "modules/doorOrder/v01/doorOrderException/doorOrderExceptionForm";

	}

	@RequiresPermissions("doorOrder:v01:doorOrderException:view")
	@RequestMapping(value = "detailForm")
	public String detailForm(DoorOrderException doorOrderException, Model model) {
		model.addAttribute("doorOrderException", doorOrderException);
		return "modules/doorOrder/v01/doorOrderException/doorOrderExceptionDetailForm";

	}

	@RequiresPermissions("doorOrder:v01:doorOrderException:edit")
	@RequestMapping(value = "detailUpdate")
	public String detailUpdate(DoorOrderException doorOrderException, Model model) {
		model.addAttribute("doorOrderException", doorOrderException);
		return "modules/doorOrder/v01/doorOrderException/doorOrderExceptionUpdate";

	}

	@RequiresPermissions("doorOrder:v01:doorOrderException:edit")
	@RequestMapping(value = "save")
	public String save(DoorOrderException doorOrderException, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, doorOrderException)) {
			return form(doorOrderException, model);
		}
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 存款信息保存
		try {
			doorOrderExceptionService.save(doorOrderException);
			message = msg.getMessage("message.I7902", null, locale);
		} catch (BusinessException be) {
			//return "【保存失败】 凭条号" + ":" + be.getMessageContent();
			addMessage(model, "【保存失败】 凭条号" + be.getMessageContent());
			return form(doorOrderException, model);
		}
		/*doorOrderExceptionService.save(doorOrderException);
		if (save == null) {
			message = msg.getMessage("message.I7902", null, locale);
		} else {
			addMessage(model, "【保存失败】 凭条号" + save);
			return form(doorOrderException, model);
		}*/
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/doorOrderException/?repage";
	}

	@RequiresPermissions("doorOrder:v01:doorOrderException:edit")
	@RequestMapping(value = "update")
	public String update(DoorOrderException doorOrderException, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, doorOrderException)) {
			return detailUpdate(doorOrderException, model);
		}
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 存款异常信息处理
		//String update = update(doorOrderException);

		try {
			doorOrderExceptionService.update(doorOrderException);
			//message = msg.getMessage("message.I7902", null, locale);
			message = msg.getMessage("message.I7901", new String[] { doorOrderException.getTickerTape() + ":异常信息处理" },locale);
		} catch (BusinessException be) {
			//return "【保存失败】 凭条号" + ":" + be.getMessageContent();
			addMessage(model, "【修改失败】凭条号 " + doorOrderException.getTickerTape() + ":" + be.getMessageContent());
			return detailUpdate(doorOrderExceptionService.get(doorOrderException.getId()), model);
		}
		/*if (update == null) {
			message = msg.getMessage("message.I7901", new String[] { doorOrderException.getTickerTape() + ":异常信息处理" },
					locale);
		} else {
			addMessage(model, "【修改失败】凭条号 " + doorOrderException.getTickerTape() + ":" + update);
			return detailUpdate(doorOrderExceptionService.get(doorOrderException.getId()), model);
		}*/
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/doorOrderException/?repage";
	}

	@RequiresPermissions("doorOrder:v01:doorOrderException:edit")
	@RequestMapping(value = "delete")
	public String delete(DoorOrderException doorOrderException, RedirectAttributes redirectAttributes) {
		doorOrderExceptionService.delete(doorOrderException);
		addMessage(redirectAttributes, "删除存款异常信息成功");
		return "redirect:" + Global.getAdminPath() + "/doorOrder/v01/doorOrderException/?repage";
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author zxk
	 * @version 2017年11月12日
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(DoorOrderException doorOrderException) {
		return "redirect:" + adminPath + "/doorOrder/v01/doorOrderException/list?repage";
	}

	/**
	 * 验证是否存在该包号
	 * 
	 * @author zxk
	 * @version 2019年11月12日
	 * @param bagNo
	 * @return
	 */
	@RequestMapping(value = "checkBagNo")
	@ResponseBody
	public String checkBagNo(String bagNo) {
		DoorOrderException doorOrderException = new DoorOrderException();
		// 设置包号
		doorOrderException.setBagNo(bagNo);
		// 设置状态（登记、确认）
		doorOrderException.setStatusList(
				Arrays.asList(new String[] { DoorOrderConstant.Status.REGISTER, DoorOrderConstant.Status.CONFIRM }));
		// 查询存款异常列表
		List<DoorOrderException> exceptionList = doorOrderExceptionService.findList(doorOrderException);
		if (Collections3.isEmpty(exceptionList)) {
			return "true";
		}
		return "false";
	}

	/**
	 * 验证是否存在该凭条
	 * 
	 * @author zxk
	 * @version 2019年11月12日
	 * @param bagNo
	 * @return
	 */
	@RequestMapping(value = "checkTickerTape")
	@ResponseBody
	public String checkTickerTape(String tickerTape) {
		DoorOrderException doorOrderException = new DoorOrderException();
		// 设置凭条号
		doorOrderException.setTickerTape(tickerTape);
		// 设置凭条号
		List<DoorOrderException> exceptionList = doorOrderExceptionService.findList(doorOrderException);
		if (Collections3.isEmpty(exceptionList)) {
			return "true";
		}
		return "false";
	}

	/**
	 * 获取明细金额
	 * 
	 * @author XL
	 * @version 2019年7月24日
	 * @param count
	 * @param parId
	 * @return
	 */
	@RequestMapping(value = "getAmount")
	@ResponseBody
	public String getAmount(String count, String parId) {
		// 返回内容
		Map<String, Object> resultMap = Maps.newHashMap();
		try {
			// 物品字典
			StoDict stoDict = stoDictService.get(parId);
			// 计算明细金额
			BigDecimal amount = new BigDecimal(count).multiply(stoDict.getUnitVal());
			resultMap.put("amount", amount);
			resultMap.put("result", "OK");
		} catch (Exception e) {
		}
		return gson.toJson(resultMap);
	}

	/**
	 * 门店人员列表
	 * 
	 * @author gzd
	 * @version 2019年11月15日
	 * @param door_id
	 * @return
	 */
	@RequestMapping(value = "getPerson")
	@ResponseBody
	public String getPerson(String officeId) {
		// 返回内容
		List<User> resultMap = new ArrayList<User>();
		// 人员列表
		resultMap = doorOrderExceptionService.getPerson(officeId);

		return gson.toJson(resultMap);
	}

	/**
	 * 七位码
	 * 
	 * @author gzd
	 * @version 2019年12月13日
	 * @param door_id
	 * @return
	 */
	@RequestMapping(value = "getRemarks")
	@ResponseBody
	public String getRemarks(String officeId) {
		if (!officeDao.get(officeId).getParentIds()
				.contains(Global.getConfig(DoorOrderConstant.OfficeCode.ZHANGJIAGANG))) {
			return gson.toJson(false);
		}
		// 返回内容
		List<Office> resultMap = doorOrderExceptionService.getRemarks(officeId);
		// 人员列表
		if(resultMap == null) {
			resultMap = new ArrayList<Office>();
		}
		return gson.toJson(resultMap);
	}

}