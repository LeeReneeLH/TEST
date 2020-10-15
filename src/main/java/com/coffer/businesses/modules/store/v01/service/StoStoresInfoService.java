package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.report.ReportGraphConstant;
import com.coffer.businesses.modules.report.v01.entity.StoInfoReportEntity;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsDao;
import com.coffer.businesses.modules.store.v01.dao.StoStoresHistoryDao;
import com.coffer.businesses.modules.store.v01.dao.StoStoresInfoDao;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoReportInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfoEntity;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 库存管理Service
 * 
 * @author LLF
 * @version 2015-09-09
 */
@Service
@Transactional(readOnly = true)
public class StoStoresInfoService extends CrudService<StoStoresInfoDao, StoStoresInfo> {

	@Autowired
	StoStoresHistoryDao stoStoresHistoryDao;
	@Autowired
	StoGoodsDao stoGoodsDao;

	public StoStoresInfo get(String id) {
		return super.get(id);
	}

	public List<StoStoresInfo> findList(StoStoresInfo stoStoresInfo) {
		return super.findList(stoStoresInfo);
	}

	public List<StoStoresInfo> findStoStoresInfoList(StoStoresInfo stoStoresInfo) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		User user = UserUtils.getUser();

		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {



				stoStoresInfo.setOffice(user.getOffice());

				stoStoresInfo.getSqlMap().put("dsf", dataScopeFilter(user, "o6", null));



		}

		// int pageNo = stoStoresInfo.getPage().getPageNo();
		List<StoStoresInfo> stoStoresInfoList = findList(stoStoresInfo);

		return stoStoresInfoList;
	}
	
	public List<StoStoresHistory> findStoStoresHistoryList(StoStoresHistory stoStoresHistory) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		User user = UserUtils.getUser();
		stoStoresHistory.setOffice(user.getOffice());
		stoStoresHistory.getSqlMap().put("dsf", dataScopeFilter(user, "o7", null));
		
		if (stoStoresHistory != null && stoStoresHistory.getCreateTimeStart() != null) {
			stoStoresHistory.setCreateTimeStart(DateUtils.getDateStart(stoStoresHistory.getCreateTimeStart()));
		} else {
			stoStoresHistory.setCreateTimeStart(DateUtils.getDateStart(new Date()));
		}
		if (stoStoresHistory != null && stoStoresHistory.getCreateTimeEnd() != null) {
			stoStoresHistory.setCreateTimeEnd(DateUtils.getDateEnd(stoStoresHistory.getCreateTimeEnd()));
		} else {
			stoStoresHistory.setCreateTimeEnd(DateUtils.getDateEnd(new Date()));
		}
		return stoStoresHistoryDao.findList(stoStoresHistory);
	}

	public Page<StoStoresInfo> findPage(Page<StoStoresInfo> page, StoStoresInfo stoStoresInfo) {
		return super.findPage(page, stoStoresInfo);
	}

	@Transactional(readOnly = false)
	public void save(StoStoresInfo stoStoresInfo) {
		super.save(stoStoresInfo);
	}

	@Transactional(readOnly = false)
	public void delete(StoStoresInfo stoStoresInfo) {
		super.delete(stoStoresInfo);
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月10日
	 * 
	 * 
	 * @param list
	 * @param officeId所属金库
	 * @param businessId业务流水
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public synchronized String changeStore(List<ChangeStoreEntity> list, String officeId, String businessId, User user) {

		String storeOfficeId = user.getOffice().getId();

		if (!Collections3.isEmpty(list)) {
			// 获取最新库存信息
			Map<String, Object> listMap = getNewestStoreInfo(storeOfficeId);
			List<StoStoresInfo> stoStoresInfoList = (List<StoStoresInfo>) listMap.get("stoStoresInfoList");
			boolean flag = (boolean) listMap.get("flag");
			// 获取所有物品信息
			List<StoGoods> goodsList = stoGoodsDao.findAllList(null);
			// 获取业务类型：调拨、盘点、兑换等
			String stoStatus = businessId.substring(businessId.length() - 6, businessId.length() - 4);
			Date date = new Date();
			// 当日无库存年复制没有物品变更的物品库存信息
			if (!flag) {
				for (StoStoresInfo temp : stoStoresInfoList) {
					boolean storeFlag = true;
					for (ChangeStoreEntity changeStoreEntity : list) {
						if (temp.getGoodsId().equals(changeStoreEntity.getGoodsId())) {
							storeFlag = false;
							break;
						}
					}
					if (storeFlag) {
						temp.setStoId(IdGen.uuid());
						temp.setCreateDate(date);
						dao.insert(temp);
					}
				}
			}

			// 根据库存变更信息，修改库存，库存不存在物品insert，存在物品update变更数量，库存流水产生记录
			for (ChangeStoreEntity changeStoreEntity : list) {
				// 获取物品相应信息
				String goodsName = "";
				String goodsType = "";
				BigDecimal amountUnit = new BigDecimal("0");
				for (StoGoods stoGoods : goodsList) {
					if (changeStoreEntity.getGoodsId().equals(stoGoods.getGoodsID())) {
						goodsName = stoGoods.getGoodsName();
						goodsType = stoGoods.getGoodsType();
						amountUnit = stoGoods.getGoodsVal();
						break;
					}
				}

				boolean exist = false;// 此物品是否存在库存中
				StoStoresInfo stoStoresInfoTemp = new StoStoresInfo();
				// 初始化数据
				stoStoresInfoTemp.setStoId(IdGen.uuid());
				stoStoresInfoTemp.setGoodsId(changeStoreEntity.getGoodsId());
				stoStoresInfoTemp.setGoodsName(goodsName);
				stoStoresInfoTemp.setStoNum(changeStoreEntity.getNum());
				stoStoresInfoTemp.setOffice(new Office(storeOfficeId));
				stoStoresInfoTemp.setGoodsType(goodsType);
				stoStoresInfoTemp.setCreateBy(user);
				stoStoresInfoTemp.setCreateName(user != null ? user.getName() : "");

				Long historyNum = 0L;
				for (StoStoresInfo temp : stoStoresInfoList) {
					if (temp.getGoodsId().equals(changeStoreEntity.getGoodsId())) {
						// 物品存在修改库存，封装entity
						// 如果库存量<变更量
						historyNum = temp.getStoNum();
						long num = temp.getStoNum() + changeStoreEntity.getNum();
						if (num < 0) {
							// 存在物品变更数量大于库存量
							throw new BusinessException("message.E1032", "", new String[] {
									temp.getGoodsName(), temp.getStoNum().toString(),
									changeStoreEntity.getNum().toString() });
						}
						temp.setStoNum(num);
						stoStoresInfoTemp = temp;
						exist = true;
						break;
					}
				}
				// 当日库存 不存在insert entity
				if (!flag) {
					if (stoStoresInfoTemp.getStoNum() >= 0) {
						stoStoresInfoTemp.setStoId(IdGen.uuid());
						BigDecimal bigNum = new BigDecimal(stoStoresInfoTemp.getStoNum());
						stoStoresInfoTemp.setAmount(amountUnit.multiply(bigNum));
						stoStoresInfoTemp.setCreateDate(date);
						dao.insert(stoStoresInfoTemp);
					} else {
						// 库存中不存在物品，还要执行减库存动作 错误
						throw new BusinessException("message.E1033", "", changeStoreEntity.getGoodsId());
					}
				} else { // 当日存在库存
					if (exist) {
						// 库存存在，update entity
						BigDecimal bigNum = new BigDecimal(stoStoresInfoTemp.getStoNum());
						stoStoresInfoTemp.setAmount(amountUnit.multiply(bigNum));
						stoStoresInfoTemp.setCreateDate(date);
						dao.update(stoStoresInfoTemp);
					} else {
						if (changeStoreEntity.getNum() >= 0) {
							BigDecimal bigNum = new BigDecimal(stoStoresInfoTemp.getStoNum());
							stoStoresInfoTemp.setAmount(amountUnit.multiply(bigNum));
							stoStoresInfoTemp.setCreateDate(date);
							dao.insert(stoStoresInfoTemp);
						} else {
							// 库存中不存在物品，还要执行减库存动作 错误
							throw new BusinessException("message.E1033", "",
									changeStoreEntity.getGoodsId());
						}
					}
				}
				// insert 库存变更流水
				StoStoresHistory storesHistory = new StoStoresHistory();
				// 初始化数据
				storesHistory.setStoId(IdGen.uuid());
				storesHistory.setGoodsId(changeStoreEntity.getGoodsId());
				storesHistory.setGoodsName(goodsName);
				storesHistory.setStoNum(historyNum);
				storesHistory.setChangeNum(changeStoreEntity.getNum());

				BigDecimal bigNum = new BigDecimal(storesHistory.getStoNum());
				storesHistory.setAmount(amountUnit.multiply(bigNum));

				storesHistory.setOffice(new Office(officeId));
				storesHistory.setGoodsType(goodsType);
				storesHistory.setCreateBy(user);
				storesHistory.setCreateName(user != null ? user.getName() : "");
				storesHistory.setCreateDate(date);
				storesHistory.setBusinessId(businessId);
				storesHistory.setStoStatus(stoStatus);
				stoStoresHistoryDao.insert(storesHistory);
			}
			// 库存修改成功
			return Constant.SUCCESS;
		} else {
			// 错误，物品信息不存在
			throw new BusinessException("message.E1034");
		}
	}

	/**
	 * 根据物品Id取得最新的库存信息
	 * 
	 * @author niguoyong
	 * @return
	 */
	public StoStoresInfo getStoStoresInfoByGoodsId(String goodsId, String officeId, String excludeZeroFg) {
		return dao.getStoStoresInfoByGoodsId(goodsId, officeId, excludeZeroFg, Global.getConfig("jdbc.type"));
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月24日
	 * 
	 *          获取最新库存信息
	 * @param stoStoresInfoList
	 * @param officeId
	 * @return
	 */
	public Map<String,Object> getNewestStoreInfo(String officeId) {
		List<StoStoresInfo> stoStoresInfoList = Lists.newArrayList();
		Map<String,Object> map = Maps.newHashMap();
		// 首先判断当日库存是否存在，不存在将上一日库存copy当日
		StoStoresInfo stoStoresInfo = new StoStoresInfo();
		Date today = new Date();
		stoStoresInfo.setOffice(new Office(officeId));
		stoStoresInfo.setCreateDate(today);
		stoStoresInfoList = dao.findList(stoStoresInfo);
		boolean flag = true;// 当日是否存在库存信息

		// 当日不存在库存信息
		if (Collections3.isEmpty(stoStoresInfoList)) {
			Date dDate = dao.getMaxStoreDate(stoStoresInfo);
			Date date = dDate != null ? dDate : today;
			stoStoresInfo.setCreateDate(date);
			stoStoresInfoList = dao.findList(stoStoresInfo);
			flag = false;
		}
		map.put("flag",flag);
		map.put("stoStoresInfoList",stoStoresInfoList);
		return map;
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2015年9月28日
	 * 
	 *          修改预剩余库存数量
	 * @param list
	 * @param officeId
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public synchronized String changeSurplusStore(List<ChangeStoreEntity> list, String officeId) {
		try {
			if (!Collections3.isEmpty(list)) {
				// 获取最新库存信息
				Map<String, Object> map = Maps.newHashMap();
				map = getNewestStoreInfo(officeId);
				List<StoStoresInfo> stoStoresInfoList = (List<StoStoresInfo>) map.get("stoStoresInfoList");

				for (ChangeStoreEntity changeStoreEntity : list) {
					for (StoStoresInfo stoStoresInfo : stoStoresInfoList) {
						if (stoStoresInfo.getGoodsId().equals(changeStoreEntity.getGoodsId())) {
							long num = stoStoresInfo.getSurplusStoNum() != null ? stoStoresInfo.getSurplusStoNum() : 0;
							num = num + changeStoreEntity.getNum();
							stoStoresInfo.setSurplusStoNum(num < 0 ? 0 : num);
							dao.update(stoStoresInfo);
							break;
						}
					}
				}

			}
			return Constant.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("message.E1035");
		}
	}
	
	/**
	 * @author wh Title: getGraphData Description: 获取库存报表变化数据
	 * @param entity
	 * @return List<StoInfoReportEntity>
	 */
	public List<StoInfoReportEntity> getGraphData(StoInfoReportEntity entity) {
		return dao.getGraphData(entity);
	}

	/**
	 * @author wh Title: findListGraph Description: 获取库存报表数据
	 * @param stoStoresInfo
	 * @return List<StoStoresInfoEntity>
	 */

	public List<StoStoresInfoEntity> findListGraph(StoStoresInfoEntity stoStoresInfo) {
		return dao.findListGraph(stoStoresInfo);
	}
	
	/**
	 * @author wh Title: makeGraphData Description: 历史报表放入图形数据
	 * @param stoInfoReportEntity
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> makeGraphData(StoInfoReportEntity stoInfoReportEntity) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		// 创建配置项数据容器
		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		// 取得需要放置数据
		List<StoInfoReportEntity> resultList = getGraphData(stoInfoReportEntity);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (StoInfoReportEntity entity : resultList) {
			// 放入legend配置项数据
			if (!legendDataList.contains(entity.getGoodsName())) {
				legendDataList.add(entity.getGoodsName());
			}

			// 放入x轴数据
			if (!xAxisDataList.contains(entity.getStrDate())) {
				xAxisDataList.add(entity.getStrDate());
			}

			// 按类别放入数据
			if (!seriesMap.containsKey(entity.getGoodsName())) {
				String name = entity.getGoodsName();
				List<String> dataList = Lists.newArrayList();
				dataList.add(entity.getTotalAmount());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, name);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.LINE);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_SMOOTH_KEY, true);
				seriesMap.put(name, map);
			} else {
				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getGoodsName());
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);
				dataList.add(entity.getTotalAmount());
			}
		}

		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}

		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		rtnMap.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY,
				DictUtils.getDictLabel(stoInfoReportEntity.getFilterCondition().replace(Constant.Punctuation.HYPHEN,
						Constant.Punctuation.HALF_UNDERLINE), "report_filter_condition", ""));
		return rtnMap;
	}

	/**
	 * @author wh Title: makeReportGraphData Description: 库存报表放入图形数据
	 * @param stoReportInfo
	 * @return
	 */
	// 取得图形数据
	@SuppressWarnings("unchecked")
	public Map<String, Object> makeReportGraphData(StoReportInfo stoReportInfo) {

		Map<String, Object> rtnMap = Maps.newHashMap();

		List<String> legendDataList = Lists.newArrayList();
		List<String> xAxisDataList = Lists.newArrayList();
		List<Map<String, Object>> seriesDataList = Lists.newArrayList();
		StoStoresInfoEntity stoStoresInfo = new StoStoresInfoEntity();
		stoStoresInfo.setOffice(stoReportInfo.getOffice());
		List<StoStoresInfoEntity> resultList = findListGraph(stoStoresInfo);

		Map<String, Map<String, Object>> seriesMap = Maps.newHashMap();
		for (StoStoresInfoEntity entity : resultList) {
			// 放入legend配置项数据
			if (!legendDataList.contains(entity.getGoodsName())) {
				legendDataList.add(entity.getGoodsName());
			}

			// 放入x轴数据
			if (!xAxisDataList.contains(entity.getOffice().getName())) {
				xAxisDataList.add(entity.getOffice().getName());
			}

			// 按类别放入物品数据
			if (!seriesMap.containsKey(entity.getGoodsName())) {

				List<String> dataList = Lists.newArrayList();

				dataList.add(entity.getTotalAmount());
				Map<String, Object> map = Maps.newHashMap();
				map.put(ReportGraphConstant.SeriesProperties.SERIES_NAME_KEY, entity.getGoodsName());
				map.put(ReportGraphConstant.SeriesProperties.SERIES_TYPE_KEY, ReportGraphConstant.GraphType.BAR);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY, dataList);
				map.put(ReportGraphConstant.SeriesProperties.SERIES_STACK_KEY, Global.getConfig("report.bar.all"));
				map.put(ReportGraphConstant.SeriesProperties.SERIES_BAR_WIDTH_KEY,
						Global.getConfig("report.bar.width"));
				seriesMap.put(entity.getGoodsName(), map);
			} else {

				Map<String, Object> map = (Map<String, Object>) seriesMap.get(entity.getGoodsName());
				List<String> dataList = (List<String>) map.get(ReportGraphConstant.SeriesProperties.SERIES_DATA_KEY);

				dataList.add(entity.getTotalAmount());
			}
		}

		Iterator<String> iterator = seriesMap.keySet().iterator();

		while (iterator.hasNext()) {
			seriesDataList.add(seriesMap.get(iterator.next()));
		}

		rtnMap.put(ReportGraphConstant.DataGraphList.LEGEND_DATE_LIST, legendDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.XAXIS_DATE_LIST, xAxisDataList);
		rtnMap.put(ReportGraphConstant.DataGraphList.SERIES_DATE_LIST, seriesDataList);
		return rtnMap;
		}

}