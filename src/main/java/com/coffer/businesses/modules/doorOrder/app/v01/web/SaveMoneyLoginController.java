package com.coffer.businesses.modules.doorOrder.app.v01.web;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.app.v01.service.SaveMoneyService;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorOrderExceptionService;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentInfoService;
import com.coffer.businesses.modules.doorOrder.v01.service.SaveTypeService;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.GuestDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.dao.DictDao;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RestController
@RequestMapping(value = "${adminPath}/doorOrder/app/v01/saveMoneyLogin")
public class SaveMoneyLoginController extends BaseController {

	@Autowired
	private OfficeService officeService;

	@Autowired
	private EquipmentInfoService equipmentInfoService;

	@Autowired
	private DoorOrderExceptionService doorOrderExceptionService;

	@Autowired
	private SaveTypeService saveTypeService;

	@Autowired
	private SaveMoneyService saveMoneyService;

	@Autowired
	private DoorOrderInfoService doorOrderInfoService;

	@Autowired
	private OfficeDao officeDao;

	@Autowired
	private GuestDao guestDao;

	private static DictDao dictDao = SpringContextHolder.getBean(DictDao.class);

	public static final String CACHE_DICT_MAP = "dictMap";

	private static StoDictDao stoDictDao = SpringContextHolder.getBean(StoDictDao.class);

	public static final String CACHE_GOOD_DICT_MAP = "goodDictMap";

	/**
	 * @version 获取机构JSON数据。
	 * 
	 * @param extId
	 *            排除的ID
	 * @param type
	 *            等于
	 * @param maxType
	 *            小于等于
	 * @param isAll
	 * @param isNotNeedSubPobc
	 *            是否列出当前人行机构下子人行及其商业机构
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
			@RequestParam(required = false) Long type, @RequestParam(required = false) Long maxType,
			@RequestParam(required = false) Boolean isAll, @RequestParam(required = false) String tradeFlag,
			@RequestParam(required = false) Boolean isNotNeedSubPobc, HttpServletResponse response,
			@RequestParam(required = false) Long minType, @RequestParam(required = false) Boolean clearCenterFilter,
			@RequestParam(required = false) String userId) {
		response.setContentType("application/json; charset=UTF-8");
		// 人民银行的管理员在机构管理画面只能看到“金库”以上级别的机构，“分组”以下的机构不可见。
		// User user = UserUtils.getUser();
		// if (Constant.SysUserType.CENTRAL_MANAGER.equals(user.getUserType()))
		// {
		// maxType = Long.parseLong(Constant.OfficeType.COFFER);
		// }
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Office> list = officeService.findList(isAll);
		Office loginUserOffice = UserUtils.get(userId).getOffice();
		for (int i = 0; i < list.size(); i++) {
			Office e = list.get(i);
			if ((extId == null
					|| (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1))
					&& (type == null || (type != null && Integer.parseInt(e.getType()) == type.intValue()))
					&& (maxType == null || (maxType != null && Integer.parseInt(e.getType()) <= maxType.intValue()))
					&& (minType == null || (minType != null && Integer.parseInt(e.getType()) >= minType.intValue()))
					&& (StringUtils.isBlank(tradeFlag)
							|| (StringUtils.isNotBlank(tradeFlag) && tradeFlag.equals(e.getTradeFlag())))
					&& (isNotNeedSubPobc == null || (isNotNeedSubPobc != null && isNotNeedSubPobc
							&& e.getParentId().equals(loginUserOffice.getId())))
					&& (clearCenterFilter == null
							|| (clearCenterFilter != null && loginUserOffice.getId().equals(e.getId()))
							|| (clearCenterFilter != null && clearCenterFilter
									&& Constant.OfficeType.CLEAR_CENTER.equals(loginUserOffice.getType())
									&& e.getParentIds().indexOf("," + loginUserOffice.getParentId() + ",") != -1)
							|| (clearCenterFilter != null && clearCenterFilter
									&& !Constant.OfficeType.CLEAR_CENTER.equals(loginUserOffice.getType())
									&& e.getParentIds().indexOf("," + loginUserOffice.getId() + ",") != -1))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("pIds", e.getParentIds());
				map.put("name", e.getName());
				map.put("type", e.getType());
				mapList.add(map);
			}
		}
		return mapList;
	}

	/**
	 * 根据门店获取机具、款项类型列表
	 *
	 * @author XL
	 * @version 2019年7月29日
	 * @param doorId
	 * @return
	 */
	@RequestMapping(value = "selectDoorOffice", produces = { "application/json;charset=UTF-8" })
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

	// @RequestMapping(value = "getSaveMethod")
	// @ResponseBody
	// public static List<Dict> getSaveMethod(String type) {
	// @SuppressWarnings("unchecked")
	// Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>)
	// CacheUtils.get(CACHE_DICT_MAP);
	// if (dictMap == null) {
	// dictMap = Maps.newHashMap();
	// for (Dict dict : dictDao.findAllList(new Dict())) {
	// List<Dict> dictList = dictMap.get(dict.getType());
	// if (dictList != null) {
	// dictList.add(dict);
	// } else {
	// dictMap.put(dict.getType(), Lists.newArrayList(dict));
	// }
	// }
	// CacheUtils.put(CACHE_DICT_MAP, dictMap);
	// }
	// List<Dict> dictList = dictMap.get(type);
	// if (dictList == null) {
	// dictList = Lists.newArrayList();
	// }
	// return dictList;
	// }

	/**
	 * 获取款项类型
	 * 
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "getMoneyValue")
	@ResponseBody
	public static List<StoDict> getMoneyValue(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<StoDict>> dictMap = (Map<String, List<StoDict>>) CacheUtils.get(CACHE_GOOD_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			StoDict param = new StoDict();
			param.setDelFlag(null);
			for (StoDict dict : stoDictDao.findList(param)) {
				List<StoDict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_GOOD_DICT_MAP, dictMap);
		}
		List<StoDict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	/**
	 * 登记存款
	 * 
	 * @param doorOrderInfo
	 * @return
	 */
	@RequestMapping(value = "save", produces = { "application/json;charset=UTF-8" })
	@ResponseBody
	public synchronized String save(DoorOrderInfo doorOrderInfo) {
		String message = "";
		Guest guest = new Guest();
		guest.setId(doorOrderInfo.getUserId());
		Guest idGuest = guestDao.getByUnionID(guest);
		if (Collections3.isEmpty(idGuest.getEquipmentIdList())) {
			try {
				// 总金额格式化
				doorOrderInfo.setAmount(doorOrderInfo.getAmount().replace(",", ""));
				// 清分机构
				Office clearCenter = saveMoneyService
						.getClearCenter(UserUtils.get(doorOrderInfo.getUserId()).getOffice().getId());
				doorOrderInfo.setOffice(clearCenter);
				// 款袋编号 gzd 2019-12-13
				if (StringUtils.isBlank(doorOrderInfo.getRfid())) {
					String rfidCreate = doorOrderInfo.getDoorId() + "_"
							+ BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER, clearCenter);
					doorOrderInfo.setRfid(rfidCreate);
				}
				// 存款信息保存
				saveMoneyService.save(doorOrderInfo);
				message = "success";
				logger.debug("小程序存款保存成功！");
			} catch (BusinessException be) {
				message = "fail";
				logger.debug("保存失败，错误信息为：" + be.getMessage());
			}
		} else {
			message = "E01";
			logger.debug("当前登录用户所属机构存在机具，无法存款");
		}
		return message;
	}

	/**
	 * 获取凭条号
	 * 
	 * @return
	 */
	@RequestMapping(value = "getTickertape")
	@ResponseBody
	public String getTickertape(String userId) {
		String tickerTape = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER,
				UserUtils.get(userId).getOffice());
		return gson.toJson(tickerTape);
	}

	/**
	 * 获取业务备注
	 * 
	 * @param officeId
	 * @return
	 */
	@RequestMapping(value = "getRemarks")
	@ResponseBody
	public String getRemarks(String officeId) {
		if (!officeDao.get(officeId).getParentIds()
				.contains(Global.getConfig(DoorOrderConstant.OfficeCode.ZHANGJIAGANG))) {
			return gson.toJson(false);
		}
		// 人员列表
		List<Office> resultMap = doorOrderExceptionService.getRemarks(officeId);
		return gson.toJson(resultMap);
	}

	/**
	 * 获取业务类型
	 * 
	 * @param type
	 * @param flag
	 * @param values
	 * @return
	 */
	@RequestMapping("/getSaveMethod")
	@ResponseBody
	public static List<Dict> getDictList(String type, boolean flag, String values) {
		List<Dict> dictList = getDictList(type);
		if (StringUtils.isBlank(values)) {
			return dictList;
		}

		List<Dict> newDictList = Lists.newArrayList();
		if (flag) {
			// 保留value值
			for (int j = 0; j < dictList.size(); j++) {
				if (values.indexOf(dictList.get(j).getValue()) >= 0) {
					newDictList.add(dictList.get(j));
				}
			}
		} else {
			// 去除value值
			for (int j = 0; j < dictList.size(); j++) {
				if (values.indexOf(dictList.get(j).getValue()) < 0) {
					newDictList.add(dictList.get(j));
				}
			}
		}
		return newDictList;
	}

	public static List<Dict> getDictList(String type) {
		@SuppressWarnings("unchecked")
		Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>) CacheUtils.get(CACHE_DICT_MAP);
		if (dictMap == null) {
			dictMap = Maps.newHashMap();
			for (Dict dict : dictDao.findAllList(new Dict())) {
				List<Dict> dictList = dictMap.get(dict.getType());
				if (dictList != null) {
					dictList.add(dict);
				} else {
					dictMap.put(dict.getType(), Lists.newArrayList(dict));
				}
			}
			CacheUtils.put(CACHE_DICT_MAP, dictMap);
		}
		List<Dict> dictList = dictMap.get(type);
		if (dictList == null) {
			dictList = Lists.newArrayList();
		}
		return dictList;
	}

	@RequestMapping(value = "checkRfid")
	@ResponseBody
	public String checkRfid(String rfid, String userId) {
		DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
		// 设置包号
		doorOrderInfo.setRfid(rfid);
		// 设置机构
		doorOrderInfo.setOffice(UserUtils.get(userId).getOffice());
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

	@RequestMapping(value = "checkDoorUser")
	@ResponseBody
	public String checkDoorUser(String officeId) {
		Office office = officeDao.get(officeId);
		if (office.getType().equals(Constant.OfficeType.STORE)) {
			return "true";
		}
		return "false";
	}

	@RequestMapping(value = "checkHasEquip")
	@ResponseBody
	public String checkHasEquip(String officeId) {
		List<EquipmentInfo> equipList = equipmentInfoService.checkDoorBinded(officeId);
		if (!Collections3.isEmpty(equipList)) {
			return "true";
		}
		return "false";
	}

}
