package com.coffer.businesses.modules.doorOrder.v01.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryUseRecords;
import com.coffer.businesses.modules.doorOrder.v01.entity.HistoryUseRecordsDetail;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 机具历史使用记录Service
 *
 * @author lihe
 * @version 2019-10-30
 */
@Service
@Transactional(readOnly = true)
public class HistoryUseRecordsService extends BaseService {

	@Autowired
	EquipmentInfoDao equipmentInfoDao;

	/**
	 * 
	 * Title: findPage
	 * <p>
	 * Description: 查询机具历史使用记录
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param historyUseRecords
	 * @return Page<HistoryUseRecords> 返回类型
	 */
	public Page<HistoryUseRecords> findPage(Page<HistoryUseRecords> page, HistoryUseRecords historyUseRecords) {

		// 机构过滤
		if (null != UserUtils.getUser()) {
			Office office = UserUtils.getUser().getOffice();
			if (StringUtils.isBlank(office.getType())) {
				throw new BusinessException("message.E5004", "", new String[] {});
			}
			switch (office.getType()) {
			case Constant.OfficeType.CLEAR_CENTER:
				historyUseRecords.setVinOffice(office);
				break;
			case Constant.OfficeType.STORE:
				historyUseRecords.setaOffice(office);
				break;
			default:
				historyUseRecords.getSqlMap().put("dsf",
						"AND (do.parent_ids LIKE '%" + office.getId() + "%' OR do.ID = " + office.getId() + ")");
			}
		}
		// 查询条件： 开始时间
		if (historyUseRecords.getCreateTimeStart() != null) {
			historyUseRecords.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyUseRecords.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (historyUseRecords.getCreateTimeEnd() != null) {
			historyUseRecords.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyUseRecords.getCreateTimeEnd())));
		}
		historyUseRecords.setPage(page);
		page.setList(equipmentInfoDao.getHistoryUseRecords(historyUseRecords));
		return page;
	}

	/**
	 * 
	 * Title: findList
	 * <p>
	 * Description: 查询机具历史使用记录
	 * </p>
	 * 
	 * @author: lihe
	 * @param historyUseRecords
	 * @return List<HistoryUseRecords> 返回类型
	 */
	public List<HistoryUseRecords> findList(HistoryUseRecords historyUseRecords) {
		// 机构过滤
		if (null != UserUtils.getUser()) {
			Office office = UserUtils.getUser().getOffice();
			if (StringUtils.isBlank(office.getType())) {
				throw new BusinessException("message.E5004", "", new String[] {});
			}
			switch (office.getType()) {
			case Constant.OfficeType.CLEAR_CENTER:
				historyUseRecords.setVinOffice(office);
				break;
			case Constant.OfficeType.STORE:
				historyUseRecords.setaOffice(office);
				break;
			default:
				historyUseRecords.getSqlMap().put("dsf",
						"AND (do.parent_ids LIKE '%" + office.getId() + "%' OR do.ID = " + office.getId() + ")");
			}
		}
		// 查询条件： 开始时间
		if (historyUseRecords.getCreateTimeStart() != null) {
			historyUseRecords.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyUseRecords.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (historyUseRecords.getCreateTimeEnd() != null) {
			historyUseRecords.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyUseRecords.getCreateTimeEnd())));
		}
		return equipmentInfoDao.getHistoryUseRecords(historyUseRecords);
	}

	/**
	 * 
	 * Title: findDetailPage
	 * <p>
	 * Description: 查询存款明细列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param historyUseRecords
	 * @return Page<HistoryUseRecords> 返回类型
	 */
	public Page<HistoryUseRecordsDetail> findDetailPage(Page<HistoryUseRecordsDetail> page,
			HistoryUseRecordsDetail historyUseRecordsDetail) {
		// 查询条件： 开始时间
		if (historyUseRecordsDetail.getCreateTimeStart() != null) {
			historyUseRecordsDetail.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(historyUseRecordsDetail.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (historyUseRecordsDetail.getCreateTimeEnd() != null) {
			historyUseRecordsDetail.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(historyUseRecordsDetail.getCreateTimeEnd())));
		}
		// 统计面值及金额
		List<HistoryUseRecordsDetail> historyUseRecordsDetailList = Lists.newArrayList();
		List<HistoryUseRecordsDetail> list2 = getTotalAmount(historyUseRecordsDetail);
		historyUseRecordsDetailList.addAll(list2);
		historyUseRecordsDetail.setPage(page);
		List<HistoryUseRecordsDetail> list1 = findDetailList(historyUseRecordsDetail);
		historyUseRecordsDetailList.addAll(list1);
		page.setList(historyUseRecordsDetailList);
		return page;
	}

	/**
	 * 
	 * Title: findDetailList
	 * <p>
	 * Description: 查询存款明细列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param historyUseRecordsDetail
	 * @return List<HistoryUseRecordsDetail> 返回类型
	 */
	public List<HistoryUseRecordsDetail> findDetailList(HistoryUseRecordsDetail historyUseRecordsDetail) {
		return equipmentInfoDao.getDepositDetail(historyUseRecordsDetail);
	}

	/**
	 * 
	 * Title: getTotalAmount
	 * <p>
	 * Description: 计算合计数据
	 * </p>
	 * 
	 * @author: lihe
	 * @param historyUseRecordsDetail
	 * @return List<HistoryUseRecordsDetail> 返回类型
	 */
	public List<HistoryUseRecordsDetail> getTotalAmount(HistoryUseRecordsDetail historyUseRecordsDetail) {
		List<HistoryUseRecordsDetail> list = findDetailList(historyUseRecordsDetail);
		List<HistoryUseRecordsDetail> result = Lists.newArrayList();
		if (Collections3.isEmpty(list)) {
			list = Lists.newArrayList();
		}
		// 合计行
		HistoryUseRecordsDetail useRecords = new HistoryUseRecordsDetail();
		// 初始化
		Integer count100 = 0;
		Integer count50 = 0;
		Integer count20 = 0;
		Integer count10 = 0;
		Integer count5 = 0;
		Integer count1 = 0;
		BigDecimal paperAmount = new BigDecimal(0);
		BigDecimal coinAmount = new BigDecimal(0);
		BigDecimal forceAmount = new BigDecimal(0);
		BigDecimal otherAmount = new BigDecimal(0);
		BigDecimal amount = new BigDecimal(0);
		// 合计计算
		for (HistoryUseRecordsDetail records : list) {
			count100 = count100 + records.getHundred();
			count50 = count50 + records.getFifty();
			count20 = count20 + records.getTwenty();
			count10 = count10 + records.getTen();
			count5 = count5 + records.getFive();
			count1 = count1 + records.getOne();
			// 纸币金额
			paperAmount = paperAmount.add(records.getPaperAmount());
			// 硬币金额
			coinAmount = coinAmount.add(records.getCoinAmount());
			// 强制金额
			forceAmount = forceAmount.add(records.getForceAmount());
			// 其他金额
			otherAmount = otherAmount.add(records.getOtherAmount());
			// 总金额
			amount = amount.add(records.getAmount());
		}
		useRecords.setDepositBatches("合计");
		useRecords.setHundred(count100);
		useRecords.setFifty(count50);
		useRecords.setTwenty(count20);
		useRecords.setTen(count10);
		useRecords.setFive(count5);
		useRecords.setOne(count1);
		useRecords.setPaperAmount(paperAmount);
		useRecords.setCoinAmount(coinAmount);
		useRecords.setForceAmount(forceAmount);
		useRecords.setOtherAmount(otherAmount);
		useRecords.setAmount(amount);
		result.add(useRecords);
		return result;
	}
}