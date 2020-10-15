package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositTimeAnalysis;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 存款时间分析报表Service
 * 
 * @author gzd
 * @version 2020-01-15
 */

@Service
@Transactional(readOnly = true)
public class DepositTimeAnalysisService extends BaseService  {
	
	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;
	
	/**
	 * 
	 * Title: findPage
	 * <p>
	 * Description: 
	 * </p>
	 * 
	 * @author: gzd
	 * @param page
	 * @return Page<> 返回类型
	 */
	public Page<DepositTimeAnalysis> findPage(Page<DepositTimeAnalysis> page, DepositTimeAnalysis depositTimeAnalysis) {
		List<DepositTimeAnalysis> result = getDepositPool(findList(depositTimeAnalysis));
		depositTimeAnalysis.setPage(page);
		result.addAll(findList(depositTimeAnalysis));
		page.setList(result);
		return page;
	}

	public List<DepositTimeAnalysis> findList(DepositTimeAnalysis depositTimeAnalysis) {
		// 获取当前登录人机构
		Office office = UserUtils.getUser().getOffice();
		/*// 查询设备存款类型数据
		DepositTimeAnalysis.setMethod(DoorOrderConstant.MethodType.METHOD_EQUIPMENT);*/
		// 机构过滤
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())
				|| Constant.OfficeType.STORE.equals(office.getType())) {
			depositTimeAnalysis.getSqlMap().put("dsf",
					"(o2.parent_ids LIKE '%" + office.getParentId() + "%' OR o.id = " + office.getId() + ")");
		} else {
			depositTimeAnalysis.getSqlMap().put("dsf",
					"(o.parent_ids LIKE '%" + office.getId() + "%' OR o.parent_id = " + office.getId() + ")");
		}
		// 查询条件： 开始时间
		if (depositTimeAnalysis.getCreateTimeStart() != null) {
			depositTimeAnalysis.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(depositTimeAnalysis.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (depositTimeAnalysis.getCreateTimeEnd() != null) {
			depositTimeAnalysis.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(depositTimeAnalysis.getCreateTimeEnd())));
		}
		List<DepositTimeAnalysis> list = doorOrderDetailDao.getDepositTimeAnalysisList(depositTimeAnalysis);
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		}
		return list;
	}

	public List<DepositTimeAnalysis> getDepositPool(List<DepositTimeAnalysis> list) {
		List<DepositTimeAnalysis> result = Lists.newArrayList();
		int size = 0;
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
			size = 1;
		} else {
			size = list.size();
		}
		// 合计行
		DepositTimeAnalysis depositTimeReport = new DepositTimeAnalysis();
		// 初始化
		Integer totalCount = 0;
		Integer ltFiveCount = 0;
		Integer fiveToTenCount = 0;
		Integer tenToFifteenCount = 0;
		Integer fifteenToTwentyCount = 0;
		Integer gtTwentyCount = 0;
		BigDecimal ltFivePercent = new BigDecimal(0);
		BigDecimal fiveToTenPercent = new BigDecimal(0);
		BigDecimal tenToFifteenPercent = new BigDecimal(0);
		BigDecimal fifteenToTwentyPercent = new BigDecimal(0);
		BigDecimal gtTwentyPercent = new BigDecimal(0);
		// 合计计算
		for (DepositTimeAnalysis depositReport : list) {
			// 存款次数
			totalCount = totalCount + Integer.parseInt(depositReport.getTotalCount());
			// 小于5分钟存款次数
			ltFiveCount = ltFiveCount + Integer.parseInt(depositReport.getLtFiveCount());
			// 5~10分钟存款次数
			fiveToTenCount = fiveToTenCount + Integer.parseInt(depositReport.getFiveToTenCount());
			// 10~15分钟存款次数
			tenToFifteenCount = tenToFifteenCount + Integer.parseInt(depositReport.getTenToFifteenCount());
			// 15~20分钟存款次数
			fifteenToTwentyCount = fifteenToTwentyCount + Integer.parseInt(depositReport.getFifteenToTwentyCount());
			// 大于20分钟存款次数
			gtTwentyCount = gtTwentyCount + Integer.parseInt(depositReport.getGtTwentyCount());
			// 小于5分钟存款占比
			ltFivePercent = ltFivePercent.add(depositReport.getLtFivePercent());
			// 5~10分钟存款占比
			fiveToTenPercent = fiveToTenPercent.add(depositReport.getFiveToTenPercent());
			// 10~15分钟存款占比
			tenToFifteenPercent = tenToFifteenPercent.add(depositReport.getTenToFifteenPercent());
			// 15~20分钟存款占比
			fifteenToTwentyPercent = fifteenToTwentyPercent.add(depositReport.getFifteenToTwentyPercent());
			// 大于20分钟存款占比
			gtTwentyPercent = gtTwentyPercent.add(depositReport.getGtTwentyPercent());
		}
		// 添加合计及各存款类型金额总计
		depositTimeReport.setDoorName("合计");
		depositTimeReport.setTotalCount(totalCount.toString());
		depositTimeReport.setLtFiveCount(ltFiveCount.toString());
		depositTimeReport.setFiveToTenCount(fiveToTenCount.toString());
		depositTimeReport.setTenToFifteenCount(tenToFifteenCount.toString());
		depositTimeReport.setFifteenToTwentyCount(fifteenToTwentyCount.toString());
		depositTimeReport.setGtTwentyCount(gtTwentyCount.toString());
		depositTimeReport.setLtFivePercent(ltFivePercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		depositTimeReport.setFiveToTenPercent(fiveToTenPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		depositTimeReport.setTenToFifteenPercent(tenToFifteenPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		depositTimeReport.setFifteenToTwentyPercent(fifteenToTwentyPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		depositTimeReport.setGtTwentyPercent(gtTwentyPercent.divide(new BigDecimal(size), 4, BigDecimal.ROUND_HALF_UP));
		result.add(depositTimeReport);
		return result;
	}
	
}
