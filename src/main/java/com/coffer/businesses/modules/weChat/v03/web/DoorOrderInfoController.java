package com.coffer.businesses.modules.weChat.v03.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearAddMoneyService;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearPlanInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.SaveTypeService;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.store.v01.service.StoDictService;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 门店存款Controller
 * 
 * @author wanglin
 * @version 2017-02-13
 */
@Controller
@RequestMapping(value = "${adminPath}/weChat/v03/doorOrderInfo")
public class DoorOrderInfoController extends BaseController {

	@Autowired
	private DoorOrderInfoService doorOrderInfoService;
	@Autowired
	private DoorOrderInfoDao doorOrderInfodao;
	@Autowired
	private StoDictService stoDictService;
	@Autowired
	private EquipmentInfoService equipmentInfoService;
	@Autowired
	private SaveTypeService saveTypeService;
	@Autowired
	private ClearAddMoneyService clearAddMoneyService;
	@Autowired
	private ClearPlanInfoService clearPlanInfoService;

	@ModelAttribute
	public DoorOrderInfo get(@RequestParam(required = false) String id) {
		DoorOrderInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = doorOrderInfoService.get(id);
		}
		if (entity == null) {
			entity = new DoorOrderInfo();
		}
		return entity;
	}

	@RequiresPermissions("door:doorOrderInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(DoorOrderInfo doorOrderInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<DoorOrderInfo> page = doorOrderInfoService.findPage(new Page<DoorOrderInfo>(request, response),
				doorOrderInfo);
		model.addAttribute("page", page);
		return "modules/weChat/v03/door/doorOrderInfoList";

	}

	@RequiresPermissions("door:doorOrderInfo:edit")
	@RequestMapping(value = "form")
	public String form(DoorOrderInfo doorOrderInfo, Model model) {
		model.addAttribute("doorOrderInfo", doorOrderInfo);
		return "modules/weChat/v03/door/doorOrderInfoForm";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年2月14日
	 * 
	 *          待清分只能查看
	 * @param DoorOrderInfo
	 * @return
	 */
	@RequiresPermissions("door:doorOrderInfo:view")
	@RequestMapping(value = "doorOrderDetailForm")
	public String doorOrderDetailForm(DoorOrderInfo doorOrderInfo, Model model) {
		model.addAttribute("doorOrderInfo", doorOrderInfo);
		return "modules/weChat/v03/door/doorOrderDetailForm";
	}

	// /**
	// *
	// * @author wanglin
	// * @version 2017年2月14日
	// *
	// * 授权验证处理
	// * @param DoorOrderInfo
	// * @return
	// */
	//// @RequiresPermissions("door:doorOrderInfo:view")
	// @RequestMapping(value = "authorize")
	// @ResponseBody
	// public String authorize(DoorOrderInfo doorOrderInfo, Model model) {
	// Map<String, Object> messageMap = Maps.newHashMap();
	// String message = "";
	// //密文密码的取得
	// String strPassword =
	// SystemService.entryptPassword(doorOrderInfo.getAuthPassword());
	//
	// //操做管理员的验证
	// message =
	// doorOrderInfoService.authorization(doorOrderInfo.getAuthUserName(),
	// strPassword);
	//
	// messageMap.put(MESSAGE_KEY, message);
	// return gson.toJson(messageMap);
	// }

	/**
	 * 门店存款登记
	 *
	 * @author XL
	 * @version 2019年6月28日
	 * @param doorOrderInfo
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("door:doorOrderInfo:edit")
	@RequestMapping(value = "save")
	public synchronized String save(DoorOrderInfo doorOrderInfo, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 数据校验
			if (!beanValidator(model, doorOrderInfo)) {
				return form(doorOrderInfo, model);
			}
			// 总金额格式化
			doorOrderInfo.setAmount(doorOrderInfo.getAmount().replace(",", ""));
			// 清分机构
			doorOrderInfo.setOffice(UserUtils.getUser().getOffice());
			// 款袋编号 gzd 2019-12-13
			if (StringUtils.isBlank(doorOrderInfo.getRfid())) {
				String rfidCreate = doorOrderInfo.getDoorId() + "_" + BusinessUtils
						.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER, UserUtils.getUser().getOffice());
				doorOrderInfo.setRfid(rfidCreate);
			}
			// 存款信息保存
			doorOrderInfoService.save(doorOrderInfo);
			message = msg.getMessage("message.I7201", new String[] { doorOrderInfo.getOrderId() }, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/weChat/v03/doorOrderInfo/?repage";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年2月14日
	 * 
	 *          门店预约删除
	 * @param DoorOrderInfo
	 * @return
	 */
	@RequiresPermissions("door:doorOrderInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(DoorOrderInfo doorOrderInfo, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		doorOrderInfoService.delete(doorOrderInfo);
		// 删除成功
		message = msg.getMessage("message.I0002", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/weChat/v03/doorOrderInfo/?repage";
	}

	/**
	 * 返回到列表页面
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param bankPayInfo
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(DoorOrderInfo doorOrderInfo) {
		return "redirect:" + adminPath + "/weChat/v03/doorOrderInfo/list?repage";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年4月21日 Ajax判断门店今天是否已经进行过申请
	 * @param DoorOrderInfo
	 * @return
	 */
	@RequiresPermissions("door:doorOrderInfo:edit")
	@RequestMapping(value = "selectByDoorMsg")
	@ResponseBody
	public String selectByDoorMsg(@RequestParam(required = false) String doorId, Model model) {
		if (doorId != null) {
			String[] doorid = doorId.split(",");
			// 获取PC端到需要进行预约的门店编号
			doorId = doorid[doorid.length - 1];
			DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
			// 获取当前系统日期
			Date currentTime = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = formatter.format(currentTime);
			// 通过门店编号及系统日期查询该门店当天是否进行过预约申请
			doorOrderInfo.setDoorId(doorId);
			doorOrderInfo.setOrderDate(dateString);
			doorOrderInfo.setCreateDate(currentTime);
			List<DoorOrderInfo> list = doorOrderInfodao.getByorderDate1(doorOrderInfo);
			Map<String, Object> map = Maps.newHashMap();
			// 判断该门店当天是否又预约申请
			if (!list.isEmpty()) {
				map.put("success", "123");
			}
			// 通过gson将map传递到前台
			return gson.toJson(map);
		}
		return null;
	}

	/**
	 * 冲正处理（传统存款可以冲正，机具存款不可以）
	 *
	 * @author XL
	 * @version 2019年7月2日
	 * @param doorOrderInfo
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("door:doorOrderInfo:edit")
	@RequestMapping(value = "reverse")
	public synchronized String reverse(DoorOrderInfo doorOrderInfo, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 状态同步，判断是否已冲正
		// 判断该箱是否已经被拆过
		if (doorOrderInfo.getStatus().equals(DoorOrderConstant.Status.REVERSE)) {
			message = msg.getMessage("message.I7244", new String[] { doorOrderInfo.getOrderId() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + Global.getAdminPath() + "/weChat/v03/doorOrderInfo/?repage";
		}
		try {
			// 设备存款
			if (DoorOrderConstant.MethodType.METHOD_EQUIPMENT.equals(doorOrderInfo.getMethod())
					&& DoorOrderConstant.Status.REGISTER.equals(doorOrderInfo.getStatus())) {
				// 清机加钞余额清零
				clearAddMoneyService.setSurplusAmountEmpty(doorOrderInfo.getEquipmentId());
				// 撤销设备的清机任务
				clearPlanInfoService.reversePlan(doorOrderInfo.getEquipmentId());
			}
			// 设置状态
			doorOrderInfo.setStatus(DoorOrderConstant.Status.REVERSE);
			// 冲正处理
			doorOrderInfoService.reverse(doorOrderInfo);
			// 提示信息
			message = msg.getMessage("message.I7242", new String[] { doorOrderInfo.getOrderId() }, locale);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/weChat/v03/doorOrderInfo/?repage";
	}

	/**
	 * 验证是否存在该包号
	 *
	 * @author XL
	 * @version 2019年7月10日
	 * @param rfid
	 * @return
	 */
	@RequestMapping(value = "checkRfid")
	@ResponseBody
	public String checkRfid(String rfid) {
		DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
		// 设置包号
		doorOrderInfo.setRfid(rfid);
		// 设置机构
		doorOrderInfo.setOffice(UserUtils.getUser().getOffice());
		// 设置状态（登记、确认）
		doorOrderInfo.setStatusList(
				Arrays.asList(new String[] { DoorOrderConstant.Status.REGISTER, DoorOrderConstant.Status.CONFIRM }));
		// 查询预约列表
		List<DoorOrderInfo> doorOrderList = doorOrderInfoService.findList(doorOrderInfo);
		if (Collections3.isEmpty(doorOrderList)) {
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
	 * 获取凭条金额明细
	 *
	 * @author XL
	 * @version 2019年7月25日
	 * @param id
	 * @param tickertape
	 * @return
	 */
	@RequestMapping(value = "getDetailList")
	@ResponseBody
	public String getDetailList(String id, String detailId) {
		// 返回内容
		Map<String, Object> resultMap = Maps.newHashMap();
		// 金额明细列表
		List<Map<String, Object>> detailList = Lists.newArrayList();
		try {
			// 金额明细列表查询
			List<DoorOrderAmount> doorOrderAmountList = doorOrderInfoService.getDetailList(id, detailId);
			for (DoorOrderAmount doorOrderAmount : doorOrderAmountList) {
				Map<String, Object> mapDetail = Maps.newHashMap();
				// 存款方式
				mapDetail.put("saveMethod", DictUtils.getDictLabel(doorOrderAmount.getTypeId(), "save_method", null));
				// 物品字典
				StoDict stoDict = stoDictService.get(doorOrderAmount.getDenomination());
				// 面值
				mapDetail.put("parValue", stoDict != null ? stoDict.getLabel() : "");
				// 面值id gzd 2019-12-24
				mapDetail.put("parValueId", StringUtils.toString(doorOrderAmount.getDenomination()));
				// 张数
				mapDetail.put("count", StringUtils.toString(doorOrderAmount.getCountZhang()));
				// 金额
				mapDetail.put("amount", StringUtils.toString(doorOrderAmount.getDetailAmount()));
				detailList.add(mapDetail);
			}
			resultMap.put("detailList", detailList);
			resultMap.put("result", "OK");
		} catch (Exception e) {
		}
		return gson.toJson(resultMap);
	}

	/**
	 * 根据门店获取机具、款项类型列表
	 *
	 * @author XL
	 * @version 2019年7月29日
	 * @param doorId
	 * @return
	 */
	@RequestMapping(value = "selectDoorOffice")
	@ResponseBody
	public String selectDoorOffice(String doorId) {
		Map<String, Object> resultMap = Maps.newHashMap();
		try {
			// 机具编号
			EquipmentInfo equipmentInfo = new EquipmentInfo();
			// 维护机构
			Office doorOffice = SysCommonUtils.findOfficeById(doorId);
			equipmentInfo.setaOffice(doorOffice);
			equipmentInfo.setStatus(Constant.EquipmentStatus.BIND);
			// 根据门店查询机具列表
			List<EquipmentInfo> equipmentInfoList = equipmentInfoService.findList(equipmentInfo);
			List<Map<String, Object>> equipmentList = Lists.newArrayList();
			for (EquipmentInfo equipment : equipmentInfoList) {
				Map<String, Object> mapEquipment = Maps.newHashMap();
				// 机具编号
				mapEquipment.put("id", StringUtils.toString(equipment.getId()));
				// 机具名称
				mapEquipment.put("name", StringUtils.toString(equipment.getName()));
				// gzd 19.11.19 传seriesName字段： 序列号
				mapEquipment.put("seriesNumber", StringUtils.toString(equipment.getSeriesNumber()));
				equipmentList.add(mapEquipment);
			}
			resultMap.put("equipmentList", equipmentList);
			// 款项类型
			SaveType saveTypeInfo = new SaveType();
			// 商户编号
			saveTypeInfo.setMerchantId(doorOffice.getParentId());
			// 根据门店查询款项类型列表
			List<SaveType> saveTypeInfoList = saveTypeService.findList(saveTypeInfo);
			List<Map<String, Object>> savetTypeList = Lists.newArrayList();
			for (SaveType saveType : saveTypeInfoList) {
				Map<String, Object> mapSaveType = Maps.newHashMap();
				// 类型编号
				mapSaveType.put("id", StringUtils.toString(saveType.getId()));
				// 类型名称
				mapSaveType.put("name", StringUtils.toString(saveType.getTypeName()));
				// gzd 19.11.18 传typeCode字段
				mapSaveType.put("typeCode", StringUtils.toString(saveType.getTypeCode()));
				savetTypeList.add(mapSaveType);
			}
			resultMap.put("savetTypeList", savetTypeList);
			resultMap.put("result", "OK");
		} catch (Exception e) {
		}
		return gson.toJson(resultMap);
	}

	/**
	 * 凭条号
	 * 
	 * @author gzd
	 * @version 2019年12月13日
	 * @param door_id
	 * @return
	 */
	@RequestMapping(value = "getTickertape")
	@ResponseBody
	public String getTickertape() {
		String tickerTape = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER,
				UserUtils.getUser().getOffice());
		return gson.toJson(tickerTape);
	}

}