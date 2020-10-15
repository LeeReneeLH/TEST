package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DayReportDoorMerchanDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.Store;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 门店+七位码日结Service
 * 
 * @author wqj
 * @version 2019-12-9
 */
@Service
@Transactional(readOnly = true)
public class DayReportDoorAndSevenCodeService extends CrudService<DayReportDoorMerchanDao, DayReportDoorMerchan> {
	@Autowired
	private DayReportDoorMerchanDao dayReportDoorMerchanDao;
	@Autowired
	private DoorCenterAccountsMainService centerAccountsMainService;
	@Autowired
	private DoorDayReportCenterService dayReportCenterService;
	@Autowired
	private DoorCenterAccountsMainDao doorCenterAccountsMainDao;
	@Autowired
	private OfficeDao officeDao;
	/** 日结信息列表 */
	public static List<DayReportDoorMerchan> dayReportCountList;
	/** 中心id列表 */
	public static List<String> centerIdsList;

	public DayReportDoorMerchan get(String id) {
		return super.get(id);
	}

	public List<DayReportDoorMerchan> findList(DayReportDoorMerchan dayReportDoorMerchan) {
		return super.findList(dayReportDoorMerchan);
	}

	public Page<DayReportDoorMerchan> findPage(Page<DayReportDoorMerchan> page,
			DayReportDoorMerchan dayReportDoorMerchan) {
		return super.findPage(page, dayReportDoorMerchan);
	}

	@Transactional(readOnly = false)
	public void save(DayReportDoorMerchan dayReportDoorMerchan) {
		super.save(dayReportDoorMerchan);
	}

	@Transactional(readOnly = false)
	public void delete(DayReportDoorMerchan dayReportDoorMerchan) {
		super.delete(dayReportDoorMerchan);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月23日 门店+七位码日结
	 *
	 */
	@Transactional(readOnly = false)
	public synchronized void dayReportByDoorAndSevenCode(String windupType, DoorDayReportMain dayReportMain,
			Office office) {

		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 设置开始时间
		centerAccountsMain
				.setSearchDateStart(DateUtils.formatDateTime(dayReportCenterService.getDayReportMaxDate(office)));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(dayReportMain.getReportDate()));
		// 门店+七位码日结
		dayReportForDoorAndSevenCode(centerAccountsMain, windupType, dayReportMain, office);
	}

	@Transactional(readOnly = false)
	public String dayReportForDoorAndSevenCode(DoorCenterAccountsMain centerAccountsMain, String windupType,
			DoorDayReportMain dayReportMainByInsert, Office office) {
		// 设置业务类型
		List<String> businessTypeList = Lists.newArrayList();
		/** 未日结条数 */
		int unSettledCount = 0;
		// 机具存款业务
		businessTypeList.add(DoorOrderConstant.BusinessType.DOOR_ORDER);
		centerAccountsMain.setBusinessTypes(businessTypeList);
		// 设置查询机构(商户)
		centerAccountsMain.setMerchantOfficeId(office.getId());
		List<DoorCenterAccountsMain> list = centerAccountsMainService.findList(centerAccountsMain);
		// 门店机构七位码列表（临时用）
		List<String> doorSevenCodeList = Lists.newArrayList();
		// 门店实体列表，用于按七位码存储金额（临时用）
		List<Store> doorOffice = Lists.newArrayList();
		// 汇总后的商户实体列表（插入用）
		List<DayReportDoorMerchan> merchantInfosList = Lists.newArrayList();
		// 将该门店七位码去重保存
		for (DoorCenterAccountsMain doorCenterAccountsMain : list) {
			// 去除已经结算过的
			if (doorCenterAccountsMain.getReportId() == null) {
				// 存款类型账务，找出该门店的七位码
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.DOOR_ORDER)) {
					if (!doorSevenCodeList.contains(doorCenterAccountsMain.getSevenCode())) {
						doorSevenCodeList.add(doorCenterAccountsMain.getSevenCode());
						Store store = new Store();
						store.setOfficeId(doorCenterAccountsMain.getClientId());
						store.setAmount(new BigDecimal(0));
						// 新增七位码字段
						store.setSevenCode(doorCenterAccountsMain.getSevenCode());
						doorOffice.add(store);
					}
				}
			}
		}
		// 门店金额按七位码汇总
		for (DoorCenterAccountsMain doorCenterAccountsMain : list) {
			// 去除已经结算过的
			if (doorCenterAccountsMain.getReportId() == null) {
				// 存款类型账务
				if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.DOOR_ORDER)) {
					for (Store store : doorOffice) {
						if (StringUtils.isBlank(store.getSevenCode())) {
							/** 未日结条数累加 */
							unSettledCount++;
							continue;
						}
						if (store.getSevenCode().equals(doorCenterAccountsMain.getSevenCode())
								&& (store.getOfficeId().equals(doorCenterAccountsMain.getClientId()))) {
							store.setAmount(store.getAmount().add(doorCenterAccountsMain.getInAmount()));
						}
					}
				}
			}
		}
		// 遍历门店机构列表
		for (Store store : doorOffice) {
			// 设置日结主键
			String idGen = IdGen.uuid();
			// 关联reportId
			for (DoorCenterAccountsMain doorCenterAccountsMain : list) {
				// 去除已经结算过的
				if (doorCenterAccountsMain.getReportId() == null) {
					// 存款类型账务
					if (doorCenterAccountsMain.getBusinessType().equals(DoorOrderConstant.BusinessType.DOOR_ORDER)) {
						if (store.getSevenCode().equals(doorCenterAccountsMain.getSevenCode())
								&& (store.getOfficeId().equals(doorCenterAccountsMain.getClientId()))) {
							// 将日结ID与账务表中记录关联
							doorCenterAccountsMain.setReportId(idGen);
							doorCenterAccountsMain.preUpdate();
							doorCenterAccountsMainDao.update(doorCenterAccountsMain);
						}
					}
				}
			}
			// 门店机构实体信息
			Office door = StoreCommonUtils.getOfficeById(store.getOfficeId());
			// 新建门店
			DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
			// 门店id
			dayReportDoorMerchan.setOfficeId(door.getId());
			// 门店名称
			dayReportDoorMerchan.setOfficeName(door.getName());
			// 代付状态，待确认
			dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
			// 结算时间
			dayReportDoorMerchan.setReportDate(dayReportMainByInsert.getReportDate());
			// 总金额
			dayReportDoorMerchan.setTotalAmount(store.getAmount());
			// 实际日结金额 gzd 2020-08-24
			dayReportDoorMerchan.setActuralReportAmount(store.getAmount());
			// 主键
			dayReportDoorMerchan.setId(IdGen.uuid());
			// 日结主键
			dayReportDoorMerchan.setReportId(idGen);
			// 结算类型（门店存款）
			dayReportDoorMerchan.setSettlementType(DoorOrderConstant.SettlementType.doorSave);
			// 结算人
			dayReportDoorMerchan.setRname(dayReportMainByInsert.getReportBy() == null ? "系统定时日结"
					: dayReportMainByInsert.getReportBy().getName());
			// 结算机构
			dayReportDoorMerchan.setRofficeId(office.getId());
			// 新增七位码
			dayReportDoorMerchan.setSevenCode(store.getSevenCode());
			// 列表添加
			merchantInfosList.add(dayReportDoorMerchan);
		}
		// 生成一条流水，作为待汇款记录
		for (DayReportDoorMerchan dayReportDoorMerchan : merchantInfosList) {
			dayReportDoorMerchan.preInsert();
			int dayMerchanInsertResult = dayReportDoorMerchanDao.insert(dayReportDoorMerchan);
			if (dayMerchanInsertResult == 0) {
				String strMessageContent = "门店日结表：" + dayReportDoorMerchan.getId() + "更新失败！";
				throw new BusinessException("message.A1002", strMessageContent, new String[] { "门店日结表" });
			}
		}
		/** 设置日结条数及查询推送中心 add by lihe 2020-06-15 */
		dayReportPropelling(office, doorOffice.size(), unSettledCount);
		return ClearConstant.SUCCESS;
	}

	/**
	 * 
	 * Title: dayReportPropelling
	 * <p>
	 * Description: 日结消息推送
	 * </p>
	 * 
	 * @author: lihe
	 * @param office
	 *            日结商户
	 * @param size
	 *            日结条数
	 * @param unSettledCount
	 *            未日结条数 void 返回类型
	 */
	public void dayReportPropelling(Office office, int size, int unSettledCount) {
		DayReportDoorMerchan countMerchan = new DayReportDoorMerchan();
		countMerchan.setOfficeId(office.getId());
		countMerchan.setOfficeName(office.getName());
		// 设置该商户日结条数
		countMerchan.setTotalCount(size);
		// 设置该商户未日结条数
		countMerchan.setUnSettledCount(unSettledCount);
		Office condition = new Office();
		condition.setParentIdList(Arrays.asList(office.getParentIds().split(",")));
		// 根据父级id列表查询清分中心
		Office center = officeDao.getCenterByParentIds(condition);
		if (null != center) {
			countMerchan.setOfficeId(center.getId());
			if (!centerIdsList.contains(center.getId())) {
				centerIdsList.add(center.getId());
			}
		} else {
			countMerchan.setOfficeId("");
		}
		dayReportCountList.add(countMerchan);
		logger.info("------{}日结条数:{}------", office.getName(), size);
		logger.info("------{}未日结条数:{}------", office.getName(), unSettledCount);
	}
}
