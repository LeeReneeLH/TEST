package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorDayReportCenterDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportCenter;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorMerchanService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 中心账务日结Service
 * 
 * @author lihe
 * @version 2019-08-21
 */
@Service
@Transactional(readOnly = true)
public class DoorDayReportAppService extends CrudService<DoorDayReportCenterDao, DoorDayReportCenter> {

	@Autowired
	private DayReportDoorMerchanService dayReportDoorMerchanService;

	@Autowired
	private DoorCenterAccountsMainDao doorCenterAccountsMainDao;

	public Map<String, Object> getMerchantDayReportList(String createTimeStart) {
		Map<String, Object> map = Maps.newHashMap();
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		dayReportDoorMerchan.setCreateTimeStart(DateUtils.parseDate(createTimeStart));
		// 查询条件： 开始时间
		if (dayReportDoorMerchan.getCreateTimeStart() != null) {
			dayReportDoorMerchan.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(dayReportDoorMerchan.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		dayReportDoorMerchan.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));

		// dayReportDoorMerchan.getSqlMap().put("dsf",
		// "AND o2.parent_ids LIKE '%" +
		// UserUtils.getUser().getOffice().getParentId() + "%' AND
		// o2.type='9'");
		dayReportDoorMerchan.getSqlMap().put("dsf", "AND o2.parent_ids LIKE '%" + "10000002" + "%' AND o2.type='9'");
		List<DayReportDoorMerchan> list = dayReportDoorMerchanService.findList(dayReportDoorMerchan);
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		}
		map.put(Parameter.RESULT, list);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return map;
	}

	/**
	 * serviceNo: 023 获取门店日结详细信息列表
	 * 
	 * @author zxk
	 * @version 2019-8-28
	 * @return
	 */
	public Page<DoorCenterAccountsMain> getDoorDayReportInfoList(String reportId, String doorId, String createTimeStart,
			Page<DoorCenterAccountsMain> page) {

		DoorCenterAccountsMain doorCenter = new DoorCenterAccountsMain();
		doorCenter.setClientId(doorId);
		doorCenter.setReportId(reportId);
		doorCenter.setDelFlag(Constant.deleteFlag.Valid);
		// 增加可选择类型
		List<String> types = Lists.newArrayList();
		// 门店存款 74
		types.add(DoorOrderConstant.BusinessType.DOOR_ORDER);
		// 存款差错 79
		types.add(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
		// 传统存款 81
		types.add(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
		// 电子线下82
		types.add(DoorOrderConstant.BusinessType.ELECTRONIC_OFFLINE);
		doorCenter.setList(types);
		// 默认查询
		if (StringUtils.isBlank(createTimeStart)) {
			// 所有未汇款日结
			// dayReportDoorMerchan.setPaidStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 start
			List<String> paidStatusList = Lists.newArrayList();
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.TOCONFIRM);
			paidStatusList.add(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			doorCenter.setPaidStatusList(paidStatusList);
			// 汇款添加商户确认 修改人：lihe 2019-11-27 end
		} else {
			// 日结时间
			doorCenter.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(DateUtils.parseDate(createTimeStart))));
			doorCenter.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(DateUtils.parseDate(createTimeStart))));
		}

		List<DoorCenterAccountsMain> list = doorCenterAccountsMainDao.getDoorDayReportInfoList(doorCenter);
		
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		} else {
			for (DoorCenterAccountsMain dca : list) {
				if (dca.getDoorCenterList() != null && dca.getDoorCenterList().get(0) != null) {
					// 增加首条标志
					dca.getDoorCenterList().get(0).setFlag("0");
				}
			}
		}

		int count = list.size(); // 总记录数
		/*List<DoorCenterAccountsMain> newList = list.subList(page.getPageSize() * (page.getPageNo() - 1),
				((page.getPageSize() * page.getPageNo()) > count ? count : (page.getPageSize() * page.getPageNo())));
		if (Collections3.isEmpty(newList)) {
			newList = Lists.newArrayList();
		}*/
		
		
		Page<DoorCenterAccountsMain> pageList = new Page<DoorCenterAccountsMain>();
		
		pageList.setPageNo(page.getPageNo());
		pageList.setPageSize(page.getPageSize());
		pageList.setCount(count);
		pageList.setList(list);
		return pageList;
	}

}