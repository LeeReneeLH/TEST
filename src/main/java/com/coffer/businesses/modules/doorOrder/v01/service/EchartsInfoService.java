package com.coffer.businesses.modules.doorOrder.v01.service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.BackAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DepositErrorDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.BackAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.EchartPie;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 中心首页Service
 * 
 * @author lihe
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class EchartsInfoService extends BaseService {

	@Autowired
	OfficeDao officeDao;

	@Autowired
	DoorCenterAccountsMainDao centerAccountsMainDao;

	@Autowired
	DoorOrderDetailDao doorOrderDetailDao;

	@Autowired
	DepositErrorDao depositErrorDao;

	@Autowired
	BackAccountsMainDao backAccountsMainDao;

	@Autowired
	CompanyAccountsMainService companyAccountsMainService;

	@Autowired
	OfficeService officeService;

	@Autowired
	EquipmentInfoDao equipmentInfoDao;
	
	@Autowired
	DoorOrderExceptionService doorOrderExceptionService;

	/** 现金业务类型 */
	private static String[] cashBusinessType = { "74", "81", "82" };

	/** 现金存款类型 */
	private static String[] cashDepositType = { "74", "81", "82" };

	/** 现金差错类型 */
	private static String[] cashErrorType = { "79" };

	/**
	 * 
	 * Title: getCenterBarData
	 * <p>
	 * Description: 获取中心柱形图数据
	 * </p>
	 * 
	 * @author: lihe
	 * @param echartInfo2
	 * @return EchartInfo 返回类型
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public Map<String, Object> getCenterBarData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount office = new OfficeAmount();
		Office userOffice = UserUtils.getUser().getOffice();
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(userOffice.getType())
				|| Constant.OfficeType.STORE.equals(userOffice.getType())) {
			office.getSqlMap().put("dsf", "AND (A.parent_ids LIKE '%" + userOffice.getParentId() + "%' OR A.ID = "
					+ userOffice.getId() + ")");
		} else {
			office.getSqlMap().put("dsf",
					"AND (A.parent_ids LIKE '%" + userOffice.getId() + "%' OR A.ID = " + userOffice.getId() + ")");
		}
		/* 初始化开始时间和结束时间为当前时间 */
		office.setCreateTimeStart(new Date());
		office.setCreateTimeEnd(new Date());
		// 查询条件： 开始时间
		if (office.getCreateTimeStart() != null) {
			office.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(office.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (office.getCreateTimeEnd() != null) {
			office.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(office.getCreateTimeEnd())));
		}
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashBusinessType);
		// 设置业务类型列表
		office.setBusinessTypes(busTypelist);
		// 获取中心首页柱形图数据
		List<OfficeAmount> dAmounts = centerAccountsMainDao.getDepoRemitAmount(office);
		if (Collections3.isEmpty(dAmounts)) {
			dAmounts = Lists.newArrayList();
		}
		List<String> clientIdList = Lists.newArrayList();
		List<String> clientNameList = Lists.newArrayList();
		List<String> clientAmountList = Lists.newArrayList();
		List<String> remitAmountList = Lists.newArrayList();
		List<String> unremittedAmountList = Lists.newArrayList();

		for (OfficeAmount oAmount : dAmounts) {
			// 名称列表
			clientIdList.add(oAmount.getId());
			// 名称列表
			clientNameList.add(oAmount.getName());
			// 存款金额列表
			clientAmountList.add(oAmount.getDepositAmount());
			// 已汇款金额列表
			remitAmountList.add(oAmount.getRemitAmount());
			// 未汇款金额列表
			unremittedAmountList.add(oAmount.getUnremittedAmount());
		}
		rtnMap.put("clientNameList", clientNameList);
		rtnMap.put("clientIdList", clientIdList);
		rtnMap.put("clientAmountList", clientAmountList);
		rtnMap.put("remitAmountList", remitAmountList);
		rtnMap.put("unremittedAmountList", unremittedAmountList);
		return rtnMap;
	}

	/**
	 * 
	 * Title: getCenterPieData
	 * <p>
	 * Description: 根据存款类型获取存款金额绘制扇形图
	 * </p>
	 * 
	 * @author: lihe
	 * @param merchantId
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getCenterPieData(String merchantId) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount officeAmount = new OfficeAmount();
		officeAmount.setId(merchantId);
		/* 初始化开始时间和结束时间为当前时间 */
		officeAmount.setCreateTimeStart(new Date());
		officeAmount.setCreateTimeEnd(new Date());
		// 查询条件： 开始时间
		if (officeAmount.getCreateTimeStart() != null) {
			officeAmount.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(officeAmount.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (officeAmount.getCreateTimeEnd() != null) {
			officeAmount.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(officeAmount.getCreateTimeEnd())));
		}
		Office office = UserUtils.getUser().getOffice();
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())
				|| Constant.OfficeType.STORE.equals(office.getType())) {
			officeAmount.getSqlMap().put("dsf",
					"(o1.parent_ids LIKE '%" + office.getParentId() + "%' OR o1.id = " + office.getId() + ")");
		} else {
			officeAmount.getSqlMap().put("dsf",
					"(o1.parent_ids LIKE '%" + office.getId() + "%' OR o1.parent_id = " + office.getId() + ")");
		}
		// 获取存款金额列表
		List<OfficeAmount> typeAmounts = centerAccountsMainDao.getAmountByType(officeAmount);
		if (Collections3.isEmpty(typeAmounts)) {
			typeAmounts = Lists.newArrayList();
		}
		List<String> typeNameList = Lists.newArrayList();
		List<EchartPie> typeAmountList = Lists.newArrayList();
		// 计算存款金额占比
		for (OfficeAmount oAmount : typeAmounts) {
			typeNameList.add(oAmount.getTypeName());
			EchartPie echartPie = new EchartPie();
			echartPie.setName(oAmount.getTypeName());
			echartPie.setValue(oAmount.getTypeAmount());
			typeAmountList.add(echartPie);
		}
		rtnMap.put("typeNameList", typeNameList);
		rtnMap.put("typeAmountList", typeAmountList);
		return rtnMap;
	}

	/**
	 * 
	 * Title: getCenterLineData
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author: lihe
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getCenterLineData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount officeAmount = new OfficeAmount();
		Office userOffice = UserUtils.getUser().getOffice();
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(userOffice.getType())
				|| Constant.OfficeType.STORE.equals(userOffice.getType())) {
			officeAmount.getSqlMap().put("dsf", "AND (A.parent_ids LIKE '%" + userOffice.getParentId() + "%' OR A.ID = "
					+ userOffice.getId() + ")");
		} else {
			officeAmount.getSqlMap().put("dsf",
					"AND (A.parent_ids LIKE '%" + userOffice.getId() + "%' OR A.ID = " + userOffice.getId() + ")");
		}
		officeAmount.setYear(DateUtils.getYear());
		officeAmount.setDelFlag(Constant.deleteFlag.Valid);
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashDepositType);
		// 设置业务类型列表
		officeAmount.setBusinessTypes(busTypelist);
		List<OfficeAmount> lineAmounts = centerAccountsMainDao.getDepositByMonth(officeAmount);
		if (Collections3.isEmpty(lineAmounts)) {
			lineAmounts = Lists.newArrayList();
		}
		List<String> lineNameList = Lists.newArrayList();
		List<String> lineAmountList = Lists.newArrayList();
		for (OfficeAmount eLine : lineAmounts) {
			lineNameList.add(eLine.getMonth());
			lineAmountList.add(eLine.getDepositAmount());
		}
		rtnMap.put("lineNameList", lineNameList);
		rtnMap.put("lineAmountList", lineAmountList);
		return rtnMap;
	}

	/**
	 * 
	 * Title: getCenterLineData
	 * <p>
	 * Description: 按存款类型获取近一周存款数据
	 * </p>
	 * 
	 * @author: lihe
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getCenterWeekData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount officeAmount = new OfficeAmount();
		Office userOffice = UserUtils.getUser().getOffice();
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(userOffice.getType())
				|| Constant.OfficeType.STORE.equals(userOffice.getType())) {
			officeAmount.getSqlMap().put("dsf", "AND (A.parent_ids LIKE '%" + userOffice.getParentId() + "%' OR A.ID = "
					+ userOffice.getId() + ")");
		} else {
			officeAmount.getSqlMap().put("dsf",
					"AND (A.parent_ids LIKE '%" + userOffice.getId() + "%' OR A.ID = " + userOffice.getId() + ")");
		}
		officeAmount.setDelFlag(Constant.deleteFlag.Valid);
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashDepositType);
		// 设置业务类型列表
		officeAmount.setBusinessTypes(busTypelist);
		// 查询近一周存款情况
		List<OfficeAmount> pieAmounts = centerAccountsMainDao.getDepositByWeek(officeAmount);
		if (Collections3.isEmpty(pieAmounts)) {
			pieAmounts = Lists.newArrayList();
		}
		List<String> pieNameList = Lists.newArrayList();
		List<String> depositList = Lists.newArrayList();
		for (OfficeAmount pLine : pieAmounts) {
			// 获取名称列表
			pieNameList.add(pLine.getDay());
			// 获取存款列表
			depositList.add(pLine.getDepositAmount());
		}
		rtnMap.put("pieNameList", pieNameList);
		rtnMap.put("depositList", depositList);
		return rtnMap;
	}

	/**
	 * 
	 * Title: getCenterLineData
	 * <p>
	 * Description: 按存款类型获取近一周存款数据（暂不用）
	 * </p>
	 * 
	 * @author: lihe
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getCenterWeekData1() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount officeAmount = new OfficeAmount();
		officeAmount.setId(UserUtils.getUser().getOffice().getId());
		officeAmount.setType(Constant.OfficeType.MERCHANT);
		officeAmount.setDelFlag(Constant.deleteFlag.Valid);
		// 查询近一周存款情况
		List<OfficeAmount> pieAmounts = centerAccountsMainDao.getDepositByWeek(officeAmount);
		if (Collections3.isEmpty(pieAmounts)) {
			pieAmounts = Lists.newArrayList();
		}
		List<String> pieNameList = Lists.newArrayList();
		List<String> equipAmountList = Lists.newArrayList();
		List<String> pcAmountList = Lists.newArrayList();
		List<String> pdaAmountList = Lists.newArrayList();
		List<String> weAmountList = Lists.newArrayList();
		for (OfficeAmount pLine : pieAmounts) {
			// 获取名称列表
			if (!pieNameList.contains(pLine.getDay())) {
				pieNameList.add(pLine.getDay());
			}
			// 按类型筛选存款
			switch (pLine.getMethod()) {
			case DoorOrderConstant.MethodType.METHOD_EQUIPMENT:
				equipAmountList.add(pLine.getDepositAmount());
				pcAmountList.add("0");
				pdaAmountList.add("0");
				weAmountList.add("0");
				break;
			case DoorOrderConstant.MethodType.METHOD_PC:
				equipAmountList.add("0");
				pcAmountList.add(pLine.getDepositAmount());
				pdaAmountList.add("0");
				weAmountList.add("0");
				break;
			case DoorOrderConstant.MethodType.METHOD_PDA:
				equipAmountList.add("0");
				pcAmountList.add("0");
				pdaAmountList.add(pLine.getDepositAmount());
				weAmountList.add("0");
				break;
			case DoorOrderConstant.MethodType.METHOD_WECHAT:
				equipAmountList.add("0");
				pcAmountList.add("0");
				pdaAmountList.add("0");
				weAmountList.add(pLine.getDepositAmount());
				break;
			case "0":
				equipAmountList.add("0");
				pcAmountList.add("0");
				pdaAmountList.add("0");
				weAmountList.add("0");
				break;
			}
		}
		rtnMap.put("pieNameList", pieNameList);
		rtnMap.put("equipAmountList", equipAmountList);
		rtnMap.put("pcAmountList", pcAmountList);
		rtnMap.put("pdaAmountList", pdaAmountList);
		rtnMap.put("weAmountList", weAmountList);
		return rtnMap;
	}

	/**
	 * 
	 * Title: getDepositErrorForDay
	 * <p>
	 * Description: 查询各商户今日差错金额
	 * </p>
	 * 
	 * @author: lihe
	 * @return DoorCenterAccountsMain 返回类型
	 */
	public List<DoorCenterAccountsMain> getDepositErrorForDay() {
		OfficeAmount officeAmount = new OfficeAmount();
		Office userOffice = UserUtils.getUser().getOffice();
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(userOffice.getType())
				|| Constant.OfficeType.STORE.equals(userOffice.getType())) {
			officeAmount.getSqlMap().put("dsf", "AND (A.parent_ids LIKE '%" + userOffice.getParentId() + "%' OR A.ID = "
					+ userOffice.getId() + ")");
		} else {
			officeAmount.getSqlMap().put("dsf",
					"AND (A.parent_ids LIKE '%" + userOffice.getId() + "%' OR A.ID = " + userOffice.getId() + ")");
		}
		officeAmount.setDelFlag(Constant.deleteFlag.Valid);
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashErrorType);
		// 设置业务类型列表
		officeAmount.setBusinessTypes(busTypelist);
		/* 初始化开始时间和结束时间为当前时间 */
		officeAmount.setCreateTimeStart(new Date());
		officeAmount.setCreateTimeEnd(new Date());
		// 查询条件： 开始时间
		if (officeAmount.getCreateTimeStart() != null) {
			officeAmount.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(officeAmount.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (officeAmount.getCreateTimeEnd() != null) {
			officeAmount.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(officeAmount.getCreateTimeEnd())));
		}
		List<DoorCenterAccountsMain> result = centerAccountsMainDao.getCenterErrorForDay(officeAmount);
		return result;
	}

	/**
	 * 
	 * Title: getTabsData
	 * <p>
	 * Description: 获取中心各tab数据
	 * </p>
	 * 
	 * @author: lihe
	 * @return OfficeAmount 返回类型
	 */
	public OfficeAmount getTabsData() {

		/*
		 * 1.查询存款汇款总额
		 */
		OfficeAmount office = new OfficeAmount();
		Office userOffice = UserUtils.getUser().getOffice();
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(userOffice.getType())
				|| Constant.OfficeType.STORE.equals(userOffice.getType())) {
			office.getSqlMap().put("dsf", "AND (A.parent_ids LIKE '%" + userOffice.getParentId() + "%' OR A.ID = "
					+ userOffice.getId() + ")");
		} else {
			office.getSqlMap().put("dsf",
					"AND (A.parent_ids LIKE '%" + userOffice.getId() + "%' OR A.ID = " + userOffice.getId() + ")");
		}
		/* 初始化开始时间和结束时间为当前时间 */
		office.setCreateTimeStart(new Date());
		office.setCreateTimeEnd(new Date());
		// 查询条件： 开始时间
		if (office.getCreateTimeStart() != null) {
			office.setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(office.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (office.getCreateTimeEnd() != null) {
			office.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(office.getCreateTimeEnd())));
		}
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashBusinessType);
		// 设置业务类型列表
		office.setBusinessTypes(busTypelist);
		OfficeAmount result = new OfficeAmount();
		result = centerAccountsMainDao.getDepositAllAmount(office);
		/*
		 * 2.查询回款总额
		 */
		BackAccountsMain backAccountsMain = new BackAccountsMain();
		backAccountsMain.setCustNo(UserUtils.getUser().getOffice().getId());
		OfficeAmount bAmountAll = backAccountsMainDao.getBackAmount(backAccountsMain);
		if (bAmountAll == null) {
			bAmountAll = new OfficeAmount();
		}
		/* 初始化开始时间和结束时间为当前时间 */
		backAccountsMain.setCreateTimeStart(new Date());
		backAccountsMain.setCreateTimeEnd(new Date());
		// 查询条件： 开始时间
		if (backAccountsMain.getCreateTimeStart() != null) {
			backAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(backAccountsMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (backAccountsMain.getCreateTimeEnd() != null) {
			backAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(backAccountsMain.getCreateTimeEnd())));
		}
		// 查询回款总额
		OfficeAmount bAmount = backAccountsMainDao.getBackAmount(backAccountsMain);
		if (bAmount == null) {
			bAmount = new OfficeAmount();
		}
		result.setBackAmountAll(bAmountAll.getBackAmount());
		result.setBackAmount(bAmount.getBackAmount());
		BigDecimal companyNotBackAmount = companyAccountsMainService
				.getCompanyNotBackAmount(officeService.getPlatform().get(0).getId());
		result.setUnbackAmountAll(companyNotBackAmount.toString());

		/*
		 * 4.按照机具连线状态查询机具数量
		 */
		EquipmentInfo equipmentInfo = new EquipmentInfo();
		equipmentInfo.setVinOffice(UserUtils.getUser().getOffice());
		List<EquipmentInfo> list = equipmentInfoDao.getCountByConnStatus(equipmentInfo);
		int totalCount = 0;
		int exCount = 0;
		int elCount = 0;
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		} else {
			for (EquipmentInfo equip : list) {
				if (StringUtils.isBlank(equip.getConnStatus())) {
					throw new BusinessException("message.E7213", "", new String[] {});
				}
				if (DoorOrderConstant.ConnStatus.UNUSUAL.equals(equip.getConnStatus())) {
					exCount += equip.getCount();
					totalCount += equip.getCount();
				} else {
					elCount += equip.getCount();
					totalCount += equip.getCount();
				}
			}
		}
		result.setCount(totalCount);
		result.setExCount(exCount);
		result.setElCount(elCount);
		/*
		 * 5.查询异常存款数量
		 */
		OfficeAmount exceptionCount = doorOrderExceptionService.getDoorExceptionCount();
		result.setExceptionCount(exceptionCount.getExceptionCount());
		result.setProcessedCount(exceptionCount.getProcessedCount());
		return result;
	}

	/**
	 * 查询今日差错信息列表
	 *
	 * @author XL
	 * @version 2019年8月19日
	 * @return
	 */
	public List<DoorCenterAccountsMain> getDoorErrorForDay() {
		OfficeAmount officeAmount = new OfficeAmount();
		officeAmount.setId(UserUtils.getUser().getOffice().getParentId());
		/* 初始化开始时间和结束时间为当前时间 */
		officeAmount.setCreateTimeStart(new Date());
		officeAmount.setCreateTimeEnd(new Date());
		// 查询条件： 开始时间
		if (officeAmount.getCreateTimeStart() != null) {
			officeAmount.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(officeAmount.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (officeAmount.getCreateTimeEnd() != null) {
			officeAmount.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(officeAmount.getCreateTimeEnd())));
		}
		List<DoorCenterAccountsMain> result = centerAccountsMainDao.getCenterDoorErrorForDay(officeAmount);
		return result;
	}

}