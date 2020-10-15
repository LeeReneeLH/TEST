package com.coffer.businesses.modules.doorOrder.v01.service;

import java.lang.reflect.InvocationTargetException;
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
 * 门店首页Service
 * 
 * @author lihe
 * @version 2019-06-26
 */
@Service
@Transactional(readOnly = true)
public class EchartsDoorService extends BaseService {

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
	public Map<String, Object> getDoorBarData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount office = new OfficeAmount();
		Office userOffice = UserUtils.getUser().getOffice();
		office.setId(userOffice.getId());
		office.setType(Constant.OfficeType.MERCHANT);
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
		List<String> clientNameList = Lists.newArrayList();
		List<String> clientAmountList = Lists.newArrayList();
		List<String> remitAmountList = Lists.newArrayList();
		List<String> unremittedAmountList = Lists.newArrayList();

		for (OfficeAmount oAmount : dAmounts) {
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
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getDoorPieData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount officeAmount = new OfficeAmount();
		officeAmount.setId(UserUtils.getUser().getOffice().getParentId());
		officeAmount.setDoorId(UserUtils.getUser().getOffice().getId());
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
	public Map<String, Object> getDoorLineData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount officeAmount = new OfficeAmount();
		officeAmount.setId(UserUtils.getUser().getOffice().getId());
		officeAmount.setType(Constant.OfficeType.MERCHANT);
		officeAmount.setYear(DateUtils.getYear());
		officeAmount.setDelFlag(Constant.deleteFlag.Valid);
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashDepositType);
		// 设置业务类型列表
		officeAmount.setBusinessTypes(busTypelist);
		List<OfficeAmount> lineAmounts = centerAccountsMainDao.getDoorDepositByMonth(officeAmount);
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
	public Map<String, Object> getDoorWeekData() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		OfficeAmount officeAmount = new OfficeAmount();
		officeAmount.setId(UserUtils.getUser().getOffice().getId());
		officeAmount.setType(Constant.OfficeType.MERCHANT);
		officeAmount.setDelFlag(Constant.deleteFlag.Valid);
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashDepositType);
		// 设置业务类型列表
		officeAmount.setBusinessTypes(busTypelist);
		// 查询近一周存款情况
		List<OfficeAmount> pieAmounts = centerAccountsMainDao.getDepositByWeek(officeAmount);
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
	 * Title: getDepositErrorForDay
	 * <p>
	 * Description: 查询各商户今日差错金额
	 * </p>
	 * 
	 * @author: lihe
	 * @return DepositError 返回类型
	 */
	public List<DoorCenterAccountsMain> getDoorErrorForDay() {
		OfficeAmount officeAmount = new OfficeAmount();
		officeAmount.setId(UserUtils.getUser().getOffice().getId());
		officeAmount.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		// 共用商户差错查询sql标识（查询订单编号用）
		officeAmount.setDoorId(UserUtils.getUser().getOffice().getId());
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
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashErrorType);
		// 设置业务类型列表
		officeAmount.setBusinessTypes(busTypelist);
		officeAmount.getSqlMap().put("dsf", dataScopeFilter(officeAmount.getCurrentUser(), "o", null));
		return centerAccountsMainDao.getClientErrorForDay(officeAmount);
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
		// 1.查询存款汇款总额
		OfficeAmount office = new OfficeAmount();
		Office userOffice = UserUtils.getUser().getOffice();
		office.setId(userOffice.getId());
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
		office.getSqlMap().put("dsf", dataScopeFilter(office.getCurrentUser(), "D", null));
		OfficeAmount result = centerAccountsMainDao.getClientDepoRepay(office);

		/*
		 * 2.按照机具连线状态查询机具数量
		 */
		EquipmentInfo equipmentInfo = new EquipmentInfo();
		equipmentInfo.setaOffice(UserUtils.getUser().getOffice());
		List<EquipmentInfo> list = equipmentInfoDao.getCountByConnStatus(equipmentInfo);
		int totalCount = 0;
		int exCount = 0;
		int elCount = 0;
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
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		}
		
		// 3 查询存款异常数量
		OfficeAmount excAmount = doorOrderExceptionService.getDoorExceptionCount();
		result.setCount(totalCount);
		result.setExCount(exCount);
		result.setElCount(elCount);
		result.setExceptionCount(excAmount.getExceptionCount());
		result.setProcessedCount(excAmount.getProcessedCount());
		return result;
	}

	/**
	 * 
	 * Title: getCenterEquipmentCounts
	 * <p>
	 * Description: 按照连线状态获取中心机具数量
	 * </p>
	 * TODO
	 * 
	 * @author: lihe
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getCenterEquipmentCounts() {
		Map<String, Object> rtnMap = Maps.newHashMap();
		EquipmentInfo equipmentInfo = new EquipmentInfo();
		equipmentInfo.setVinOffice(UserUtils.getUser().getOffice());
		List<EquipmentInfo> list = equipmentInfoDao.getCountByConnStatus(equipmentInfo);
		return rtnMap;
	}

	/**
	 * 
	 * Title: getClientEquipmentCounts
	 * <p>
	 * Description: 按照连线状态获取商户机具数量
	 * </p>
	 * TODO
	 * 
	 * @author: lihe
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getClientEquipmentCounts() {
		EquipmentInfo equipmentInfo = new EquipmentInfo();
		equipmentInfo.setMerchantId(UserUtils.getUser().getOffice().getId());
		List<EquipmentInfo> list = equipmentInfoDao.getCountByConnStatus(equipmentInfo);
		return null;
	}

	/**
	 * 
	 * Title: getDoorEquipmentCounts
	 * <p>
	 * Description: 按照连线状态获取门店机具数量
	 * </p>
	 * TODO
	 * 
	 * @author: lihe
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getDoorEquipmentCounts() {
		EquipmentInfo equipmentInfo = new EquipmentInfo();
		equipmentInfo.setaOffice(UserUtils.getUser().getOffice());
		List<EquipmentInfo> list = equipmentInfoDao.getCountByConnStatus(equipmentInfo);
		return null;
	}

}