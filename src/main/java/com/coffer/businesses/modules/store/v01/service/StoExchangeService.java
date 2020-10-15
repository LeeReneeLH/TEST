package com.coffer.businesses.modules.store.v01.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoExchangeDao;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoExchange;
import com.coffer.businesses.modules.store.v01.entity.StoExchangeGood;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 兑换管理Service
 * 
 * @author niguoyong
 * @version 2015年9月21日
 */
@Service
@Transactional(readOnly = true)
public class StoExchangeService extends CrudService<StoExchangeDao, StoExchange> {

	@Autowired
	private StoExchangeDao stoExchangeDao;

	/**
	 * 根据条件查询兑换信息
	 * 
	 * @param page
	 *            分页类
	 * @param stoExchange
	 *            兑换管理Entity
	 * @return
	 */
	public Page<StoExchange> findPage(Page<StoExchange> page, StoExchange stoExchange) {
		// 查询条件：开始时间
		if (stoExchange.getCreateTimeStart() != null) {
			stoExchange.setSearchTimeStart(
					DateUtils.formatDate(
							DateUtils.getDateStart(stoExchange.getCreateTimeStart()), 
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (stoExchange.getCreateTimeEnd() != null) {
			stoExchange.setSearchTimeEnd(
					DateUtils.formatDate(
							DateUtils.getDateEnd(stoExchange.getCreateTimeEnd()), 
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		stoExchange.getSqlMap().put("dsf", dataScopeFilter(UserUtils.getUser(), "o3", null));
		
		// 查询数据列表
		return super.findPage(page, stoExchange);
	}

	/**
	 * 明细信息查询
	 * 
	 * @param id
	 *            兑换ID
	 * @return
	 */
	public StoExchange getDetailById(String id) {

		return stoExchangeDao.getDetailById(id);
	}

	/**
	 * 兑换保存
	 * 
	 * @param id
	 *            兑换ID
	 * @return
	 */
	public void save(StoExchange stoExchange, StoExchange stoExchangeDetail,Office currentOffice) {

		// 原始物品信息
		StoExchange stoExchangeSave = new StoExchange();
		stoExchangeSave.setId(BusinessUtils.getNewBusinessNo(Global.getConfig("businessType.store.exchange"),
				currentOffice));
		stoExchangeSave.setChangeGoods(stoExchange.getChangeGoods());
		stoExchangeSave.setChangeGoodsNum(stoExchange.getStoGoodSelectFrom().getMoneyNumber());
		stoExchangeSave.setIsNewRecord(true);
		stoExchangeSave.preInsert();
		stoExchangeSave.setOffice(currentOffice);
		// 兑换信息保存
		for (StoExchangeGood stoExchangeGood : stoExchangeDetail.getStoExchangeGoodList()) {
			stoExchangeSave.setStoExchangeGoodSave(stoExchangeGood);
			stoExchangeSave.getStoExchangeGoodSave().setDetailId(IdGen.uuid());
			stoExchangeDao.insert(stoExchangeSave);
		}

		// 库存信息更新
		List<ChangeStoreEntity> list = new ArrayList<ChangeStoreEntity>();
		// 原始物品
		ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
		changeStoreEntity.setGoodsId(stoExchange.getChangeGoods().getId());
		changeStoreEntity.setNum(-stoExchange.getStoGoodSelectFrom().getMoneyNumber());
		list.add(changeStoreEntity);
		// 兑换物品
		for (StoExchangeGood stoExchangeGood : stoExchangeDetail.getStoExchangeGoodList()) {
			changeStoreEntity = new ChangeStoreEntity();
			changeStoreEntity.setGoodsId(stoExchangeGood.getGoodsId());
			changeStoreEntity.setNum(stoExchangeGood.getNum());
			list.add(changeStoreEntity);
		}
		
		//库存更新
		StoreCommonUtils.changeStoreAndSurplusStores(list, currentOffice.getId(), stoExchangeSave.getId(), UserUtils.getUser());
	}
}
