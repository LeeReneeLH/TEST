package com.coffer.businesses.modules.doorOrder.app.v01.web;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.app.v01.service.DoorDayReportAppService;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorMerchanService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 中心账务日结Controller
 * 
 * @author lihe
 * @version 2017-08-20
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/app/v01/dayReportCenter")
public class DoorDayReportCenterAppController extends BaseController {

	@Autowired
	private DayReportDoorMerchanService dayReportDoorMerchanService;
	@Autowired
	private DoorCenterAccountsMainService doorCenterAccountsMainService;

	@Autowired
	private DoorDayReportAppService doorDayReportAppService;

	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();

	@ModelAttribute
	public DayReportDoorMerchan get(@RequestParam(required = false) String id) {
		if (StringUtils.isNotBlank(id)) {
			return dayReportDoorMerchanService.get(id);
		} else {
			return new DayReportDoorMerchan();
		}
	}

	/**
	 * 日终结账首页信息（商户日结列表、差错汇总）
	 *
	 * @author XL
	 * @version 2019年8月22日
	 * @param userId
	 * @param createTimeStart
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "getMerchantDayReportList1", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getMerchantDayReportList1(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart,
			@RequestParam(value = "pageNo") int pageNo, @RequestParam(value = "pageSize") int pageSize,
			@RequestParam(value = "orderBy", required = false) String orderBy) {
		// 返回信息
		Map<String, Object> jsonData = Maps.newHashMap();
		// 用户信息
		User user = UserUtils.get(userId);
		if (user == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户名不存在");
			return gson.toJson(jsonData);
		}
		// 机构信息
		Office office = user.getOffice();
		if (office == null || StringUtils.isBlank(office.getId())) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户未绑定机构");
			return gson.toJson(jsonData);
		}
		// 验证分页参数
		if (pageNo == 0 || pageSize == 0) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "分页参数不能为空");
			return gson.toJson(jsonData);
		}
		// -----------------------------------------------------
		// -------------------- 商户日结统计 ----------------------
		// -----------------------------------------------------
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		// 默认查询
		if (StringUtils.isBlank(createTimeStart)) {
			// 所有未汇款日结
			// dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 start
			List<String> paidStatusList = Lists.newArrayList();
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			dayReportDoorMerchan.setPaidStatusList(paidStatusList);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 end
		} else {
			// 日结时间
			dayReportDoorMerchan.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(DateUtils.parseDate(createTimeStart))));
			dayReportDoorMerchan.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(DateUtils.parseDate(createTimeStart))));
		}
		// 登陆人所属机构
		if (Constant.OfficeType.CLEAR_CENTER.equals(user.getOffice().getType())) {
			dayReportDoorMerchan.setRofficeId(user.getOffice().getId());
		} else if (Constant.OfficeType.STORE.equals(user.getOffice().getType())) {
			dayReportDoorMerchan.setClientForApp(user.getOffice().getId());
		} else {
			// 机构过滤
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND( o2.parent_ids LIKE '%" + user.getOffice().getId()
					+ "%' OR o2.ID =" + user.getOffice().getId() + ")");
		}
		Page<DayReportDoorMerchan> page = new Page<DayReportDoorMerchan>(pageNo, pageSize);
		page.setOrderBy(StringUtils.isNotBlank(orderBy) ? orderBy : "");
		dayReportDoorMerchan.setPage(page);
		page.setList(dayReportDoorMerchanService.findListForApp(dayReportDoorMerchan));
		jsonData.put(Parameter.PAGE_KEY, page);
		dayReportDoorMerchan.setPage(null);
		// 合计金额
		BigDecimal totalAmount = new BigDecimal(0);
		List<DayReportDoorMerchan> list = dayReportDoorMerchanService.findListForApp(dayReportDoorMerchan);
		for (DayReportDoorMerchan dayReportDoor : list) {
			// 日结合计金额计算
			if (dayReportDoor != null && dayReportDoor.getTotalAmount() != null) {
				totalAmount = totalAmount.add(dayReportDoor.getTotalAmount());
			}
		}
		jsonData.put(Parameter.AMOUNT_KEY, totalAmount);
		// -----------------------------------------------------
		// -------------------- 差错统计 ----------------------
		// -----------------------------------------------------
		Map<String, Object> resultMap = dayReportDoorMerchanService.getTotalDoorError(dayReportDoorMerchan);
		jsonData.put(Parameter.ERROR_COUNT_KEY, resultMap.get("errorCount") == null ? 0 : resultMap.get("errorCount"));
		jsonData.put(Parameter.ERROR_AMOUNT_KEY,
				resultMap.get("errorAmount") == null ? 0 : resultMap.get("errorAmount"));
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 根据商户获取门店日结汇总列表
	 *
	 * @author XL
	 * @version 2019年8月30日
	 * @param merchantId
	 * @param reportId
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "getDoorDayReportList1", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getDoorDayReportList1(@RequestParam(value = "merchantId") String merchantId,
			@RequestParam(value = "reportId") String reportId, @RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "pageSize") int pageSize,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "userId", required = false) String userId) {
		// 返回信息
		Map<String, Object> jsonData = Maps.newHashMap();
		// 验证机构
		if (StringUtils.isBlank(merchantId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "机构编号不能为空");
			return gson.toJson(jsonData);
		}
		// 验证日结编号
		if (StringUtils.isBlank(reportId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "日结编号不能为空");
			return gson.toJson(jsonData);
		}
		// 验证分页参数
		if (pageNo == 0 || pageSize == 0) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "分页参数不能为空");
			return gson.toJson(jsonData);
		}
		// 中心账务
		DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
		// 商户
		doorCenterAccountsMain.setMerchantOfficeId(merchantId);
		// 日结
		doorCenterAccountsMain.setReportId(reportId);
		// 业务类型（74-存款，79-差错）
		doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		doorCenterAccountsMain.setBusinessTypes(Arrays.asList(new String[] { DoorOrderConstant.BusinessType.DOOR_ORDER,
				DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE }));
		// 门店用户
		if (StringUtils.isNotBlank(userId)) {
			// 用户信息
			User user = UserUtils.get(userId);
			Office office = user.getOffice();
			if (office != null && Constant.OfficeType.STORE.equals(office.getType())) {
				doorCenterAccountsMain.setClientId(office.getId());
			}
		}
		// 汇总列表
		Page<DoorCenterAccountsMain> page = new Page<DoorCenterAccountsMain>(pageNo, pageSize);
		page.setOrderBy(StringUtils.isNotBlank(orderBy) ? orderBy : "");
		doorCenterAccountsMain.setPage(page);
		page.setList(doorCenterAccountsMainService.findDoorSummaryList(doorCenterAccountsMain));
		jsonData.put(Parameter.PAGE_KEY, page);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * serviceNo: 023 门店日结详细信息列表
	 * 
	 * @author ZXK
	 * @version 2019-8-28
	 * @return
	 */
	@RequestMapping(value = "getDoorDayReportInfoList", produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String getDoorDayReportInfoList(
			@RequestParam(value = "reportId", required = false) String reportId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart,
			@RequestParam(value = "doorId", required = false) String doorId,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		/*
		 * 判断用户是否存在
		 */
		Map<String, Object> jsonData = Maps.newHashMap();
		if (StringUtils.isEmpty(reportId) || StringUtils.isEmpty(doorId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数不能为空");
			return gson.toJson(jsonData);
		}
		
		Page<DoorCenterAccountsMain> page = doorDayReportAppService.getDoorDayReportInfoList(reportId, doorId,createTimeStart,new Page<DoorCenterAccountsMain>(
				pageNo, pageSize));
		jsonData.put(Parameter.DOOR_DAY_REPORT_LIST, page);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 日终结账首页信息（商户列表、商户总金额）
	 * 
	 * @author ZXK
	 * @version 2019年8月22日
	 * @param userId
	 * @param createTimeStart
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "getMerchantDayReportList", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getMerchantDayReportList(@RequestParam(value = "userId") String userId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "pageSize") int pageSize, @RequestParam(value = "orderBy", required = false) String orderBy) {
		// 返回信息
		Map<String, Object> jsonData = Maps.newHashMap();
		// 用户信息
		User user = UserUtils.get(userId);
		if (user == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户名不存在");
			return gson.toJson(jsonData);
		}
		// 机构信息
		Office office = user.getOffice();
		if (office == null || StringUtils.isBlank(office.getId())) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户未绑定机构");
			return gson.toJson(jsonData);
		}
		// 验证分页参数
		if (pageNo == 0 || pageSize == 0) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "分页参数不能为空");
			return gson.toJson(jsonData);
		}
		// -----------------------------------------------------
		// -------------------- 商户日结统计 ----------------------
		// -----------------------------------------------------
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		// 默认查询
		if (StringUtils.isBlank(createTimeStart)) {
			// 所有未汇款日结
			// dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 start
			List<String> paidStatusList = Lists.newArrayList();
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			dayReportDoorMerchan.setPaidStatusList(paidStatusList);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 end
		} else {
			// 日结时间
			dayReportDoorMerchan.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(DateUtils.parseDate(createTimeStart))));
			dayReportDoorMerchan.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(DateUtils.parseDate(createTimeStart))));
		}
		// 数据过滤(中心看上级人行下的所有门店，其他用户正常穿透)
		//User userInfo = UserUtils.getUser();
		if (Constant.OfficeType.CLEAR_CENTER.equals(user.getOffice().getType())) {
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + user.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + user.getOffice().getParentId() + "%')");
		} else {
			dayReportDoorMerchan.getSqlMap().put("dsf", "AND (a.office_id =" + user.getOffice().getId()
					+ " OR o2.parent_ids LIKE '%" + user.getOffice().getId() + "%')");
		}
		Page<DayReportDoorMerchan> page = new Page<DayReportDoorMerchan>(pageNo, pageSize);
		page.setOrderBy(StringUtils.isNotBlank(orderBy) ? orderBy : "");
		dayReportDoorMerchan.setPage(page);
		page.setList(dayReportDoorMerchanService.findMerchanListForApp(dayReportDoorMerchan));

		jsonData.put(Parameter.PAGE_KEY, page);
		dayReportDoorMerchan.setPage(null);
		// 合计金额
		BigDecimal totalAmount = new BigDecimal(0);
		List<DayReportDoorMerchan> list = dayReportDoorMerchanService.findMerchanListForApp(dayReportDoorMerchan);
		for (DayReportDoorMerchan dayReportDoor : list) {
			// 日结合计金额计算
			if (dayReportDoor != null && dayReportDoor.getTotalAmount() != null) {
				totalAmount = totalAmount.add(dayReportDoor.getTotalAmount());
			}
		}
		jsonData.put(Parameter.AMOUNT_KEY, totalAmount);
		// -----------------------------------------------------
		// -------------------- 差错统计 ----------------------
		// -----------------------------------------------------
		Map<String, Object> resultMap = dayReportDoorMerchanService.getTotalDoorError(dayReportDoorMerchan);
		if(resultMap == null){
			 resultMap = new HashMap<String, Object>();
		}
		jsonData.put(Parameter.ERROR_COUNT_KEY, resultMap.get("errorCount") == null ? 0 : resultMap.get("errorCount"));
		jsonData.put(Parameter.ERROR_AMOUNT_KEY, resultMap.get("errorAmount") == null ? 0 : resultMap.get("errorAmount"));
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * 根据商户获取门店日结汇总列表
	 * 
	 * @author ZXK
	 * @version 2019年11月27日
	 * @param merchantId
	 * @param pageNo
	 * @param pageSize
	 * @param orderBy
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "getDoorDayReportList", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getDoorDayReportList(@RequestParam(value = "merchantId") String merchantId,
			@RequestParam(value = "reportId", required = false) String reportId,
			@RequestParam(value = "createTimeStart", required = false) String createTimeStart, @RequestParam(value = "pageNo") int pageNo,
			@RequestParam(value = "pageSize") int pageSize, @RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "userId", required = false) String userId) {
		// 返回信息
		Map<String, Object> jsonData = Maps.newHashMap();
		// 验证机构
		if (StringUtils.isBlank(merchantId)) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "机构编号不能为空");
			return gson.toJson(jsonData);
		}
		// 验证分页参数
		if (pageNo == 0 || pageSize == 0) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "分页参数不能为空");
			return gson.toJson(jsonData);
		}

		// 中心账务
		DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
		// 商户
		doorCenterAccountsMain.setMerchantOfficeId(merchantId);
		// 日结
		// doorCenterAccountsMain.setReportId(reportId);
		// 业务类型（74-存款，79-差错,81-传统,82-电子线下）
		doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		doorCenterAccountsMain.setBusinessTypes(Arrays.asList(new String[] { DoorOrderConstant.BusinessType.DOOR_ORDER,
				DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE, DoorOrderConstant.BusinessType.TRADITIONAL_SAVE,
				DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE}));
		// 门店用户
		if (StringUtils.isNotBlank(userId)) {
			// 用户信息
			User user = UserUtils.get(userId);
			if (user == null) {
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				jsonData.put(Parameter.ERROR_MSG_KEY, "用户名不存在");
				return gson.toJson(jsonData);
			}
			Office office = user.getOffice();
			if (office != null && Constant.OfficeType.STORE.equals(office.getType())) {
				doorCenterAccountsMain.setClientId(office.getId());
			}
		}
		// 默认查询
		if (StringUtils.isBlank(createTimeStart)) {
			// 所有未汇款日结
			// dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 start
			List<String> paidStatusList = Lists.newArrayList();
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			doorCenterAccountsMain.setPaidStatusList(paidStatusList);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 end
		} else {
			// 日结时间
			doorCenterAccountsMain.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(DateUtils.parseDate(createTimeStart))));
			doorCenterAccountsMain.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(DateUtils.parseDate(createTimeStart))));
		}

		// 汇总列表
		Page<DoorCenterAccountsMain> page = new Page<DoorCenterAccountsMain>(pageNo, pageSize);
		page.setOrderBy(StringUtils.isNotBlank(orderBy) ? orderBy : "");
		doorCenterAccountsMain.setPage(page);
		page.setList(doorCenterAccountsMainService.findDoorListByMerchan(doorCenterAccountsMain));

		jsonData.put(Parameter.PAGE_KEY, page);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

}
