package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositCensusReport;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderAmountDao;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 存款情况列表分析Service
 *
 * @author lihe
 * @version 2019-10-30
 */
@Service
@Transactional(readOnly = true)
public class DepositCensusReportService extends BaseService {

	@Autowired
	DoorOrderAmountDao doorOrderAmountDao;

	/**
	 * 
	 * Title: findPage
	 * <p>
	 * Description: 查询存款情况列表分析列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param depositCensusReport
	 * @return Page<HistoryUseRecords> 返回类型
	 */
	public Page<DepositCensusReport> findPage(Page<DepositCensusReport> page, DepositCensusReport depositCensusReport) {
		List<DepositCensusReport> result = getDepositPool(findList(depositCensusReport));
		depositCensusReport.setPage(page);
		result.addAll(findList(depositCensusReport));
		page.setList(result);
		return page;
	}

	/**
	 * 
	 * Title: findList
	 * <p>
	 * Description: 根据条件查询存款情况列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param depositCensusReport
	 * @return List<HistoryUseRecords> 返回类型
	 */
	public List<DepositCensusReport> findList(DepositCensusReport depositCensusReport) {
		// 获取当前登录人机构
		Office office = UserUtils.getUser().getOffice();
		// 查询设备存款类型数据
		depositCensusReport.setMethod(DoorOrderConstant.MethodType.METHOD_EQUIPMENT);
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())
				|| Constant.OfficeType.STORE.equals(office.getType())) {
			depositCensusReport.getSqlMap().put("dsf",
					"(o2.parent_ids LIKE '%" + office.getParentId() + "%' OR o.id = " + office.getId() + ")");
		} else {
			depositCensusReport.getSqlMap().put("dsf",
					"(o.parent_ids LIKE '%" + office.getId() + "%' OR o.parent_id = " + office.getId() + ")");
		}
		// 查询条件： 开始时间
		if (depositCensusReport.getCreateTimeStart() != null) {
			depositCensusReport.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(depositCensusReport.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (depositCensusReport.getCreateTimeEnd() != null) {
			depositCensusReport.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(depositCensusReport.getCreateTimeEnd())));
		}
		List<DepositCensusReport> list = doorOrderAmountDao.getDepositCensusList(depositCensusReport);
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		}
		return list;
	}

	/**
	 * 
	 * Title: getDepositPool
	 * <p>
	 * Description: 获取合计各项
	 * </p>
	 * 
	 * @author: lihe
	 * @return List<E> 返回类型
	 */
	public List<DepositCensusReport> getDepositPool(List<DepositCensusReport> list) {
		List<DepositCensusReport> result = Lists.newArrayList();
		int size = 0;
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
			size = 1;
		} else {
			size = list.size();
		}
		// 合计行
		DepositCensusReport deCensusReport = new DepositCensusReport();
		// 初始化
		Integer count = 0;
		Integer paperCount = 0;
		Integer forceCount = 0;
		Integer depositors = 0;
		Integer aveCount = 0;
		BigDecimal paperAmount = new BigDecimal(0);
		BigDecimal forceAmount = new BigDecimal(0);
		BigDecimal aveAmount = new BigDecimal(0);
		BigDecimal amount = new BigDecimal(0);
		BigDecimal paperAmountPercent = new BigDecimal(0);
		BigDecimal paperCountPercent = new BigDecimal(0);
		BigDecimal forceAmountPercent = new BigDecimal(0);
		BigDecimal forceCountPercent = new BigDecimal(0);
		// 合计计算
		for (DepositCensusReport depositReport : list) {
			// 存款次数
			count = count + Integer.parseInt(depositReport.getTotalCount());
			// 速存存款次数
			paperCount = paperCount + Integer.parseInt(depositReport.getPaperCount());
			// 强制存款次数
			forceCount = forceCount + Integer.parseInt(depositReport.getForceCount());
			// 平均存款次数
			aveCount = aveCount + Integer.parseInt(depositReport.getAveCount());
			// 存款人数
			depositors = depositors + Integer.parseInt(depositReport.getDepositors());
			// 纸币金额
			paperAmount = paperAmount.add(depositReport.getPaperAmount());
			// 强制金额
			forceAmount = forceAmount.add(depositReport.getForceAmount());
			// 平均每天金额
			aveAmount = aveAmount.add(depositReport.getAveAmount());
			// 总金额
			amount = amount.add(depositReport.getTotalAmount());
			// 速存金额占比
			paperAmountPercent = paperAmountPercent.add(depositReport.getPaperAmountPercent());
			// 速存次数占比
			paperCountPercent = paperCountPercent.add(depositReport.getPaperCountPercent());
			// 强存金额占比
			forceAmountPercent = forceAmountPercent.add(depositReport.getForceAmountPercent());
			// 强存次数占比
			forceCountPercent = forceCountPercent.add(depositReport.getForceCountPercent());
		}
		// 添加合计及各存款类型金额总计
		//deCensusReport.setSettleOffice("合计");
		deCensusReport.setDoorName("合计");
		deCensusReport.setTotalCount(count.toString());
		deCensusReport.setPaperCount(paperCount.toString());
		deCensusReport.setForceCount(forceCount.toString());
		deCensusReport.setAveCount(aveCount.toString());
		deCensusReport.setDepositors(depositors.toString());
		deCensusReport.setPaperAmount(paperAmount);
		deCensusReport.setForceAmount(forceAmount);
		deCensusReport.setForceAmount(forceAmount);
		deCensusReport.setAveAmount(aveAmount);
		deCensusReport.setTotalAmount(amount);
		deCensusReport
				.setPaperAmountPercent(paperAmountPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		deCensusReport
				.setPaperCountPercent(paperCountPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		deCensusReport
				.setForceAmountPercent(forceAmountPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		deCensusReport
				.setForceCountPercent(forceCountPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		result.add(deCensusReport);
		return result;
	}

}