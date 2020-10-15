package com.coffer.businesses.modules.store.v02.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsDao;
import com.coffer.businesses.modules.store.v01.dao.StoStoresHistoryDao;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v02.dao.PbocStoStoresInfoDao;
import com.coffer.businesses.modules.store.v02.entity.PbocStoStoresInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 人民银行库存管理Service
 * 
 * @author chengshu
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class PbocStoStoresInfoService extends CrudService<PbocStoStoresInfoDao, PbocStoStoresInfo> {

	@Autowired
	StoStoresHistoryDao stoStoresHistoryDao;
	@Autowired
	StoGoodsDao stoGoodsDao;

	public PbocStoStoresInfo get(String id) {
		return super.get(id);
	}

    /**
     * @author chengshu
     * @version 2016/05/18
     * 
     * 取得商业银行的物品库存信息
     * @param pbocStoStoresInfo 查询条件
     * @return 物品库存
     */
	public List<PbocStoStoresInfo> findList(PbocStoStoresInfo pbocStoStoresInfo) {
		return super.findList(pbocStoStoresInfo);
	}

    /**
     * @author chengshu
     * @version 2016/05/18
     * 
     * 取得人民银行的物品库存信息
     * @param pbocStoStoresInfo 查询条件
     * @return 物品库存
     */
    public List<PbocStoStoresInfo> findPBCList(PbocStoStoresInfo pbocStoStoresInfo) {
        return super.findList(pbocStoStoresInfo);
    }

	public List<PbocStoStoresInfo> findPbocStoStoresInfoList(PbocStoStoresInfo pbocStoStoresInfo) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		User user = UserUtils.getUser();
		if (null == pbocStoStoresInfo.getOffice()
				|| StringUtils.isBlank(pbocStoStoresInfo.getOffice().getId())) {
			pbocStoStoresInfo.setOffice(user.getOffice());
		}

		pbocStoStoresInfo.getSqlMap().put("dsf", dataScopeFilter(user, "o6", null));
		int pageNo = pbocStoStoresInfo.getPage().getPageNo();
		List<PbocStoStoresInfo> pbocStoStoresInfoList = findList(pbocStoStoresInfo);

		// 当日不存在库存信息
		Date creatDate = pbocStoStoresInfo.getCreateDate();
		if (Collections3.isEmpty(pbocStoStoresInfoList)) {
			Date dDate = dao.getMaxStoreDate(pbocStoStoresInfo);
			Date date = dDate != null ? dDate : creatDate;
			pbocStoStoresInfo.setCreateDate(date);
			pbocStoStoresInfo.getPage().setPageNo(pageNo);
			pbocStoStoresInfoList = findList(pbocStoStoresInfo);
		}
		pbocStoStoresInfo.setCreateDate(creatDate);
		return pbocStoStoresInfoList;
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

	public Page<PbocStoStoresInfo> findPage(Page<PbocStoStoresInfo> page, PbocStoStoresInfo pbocStoStoresInfo) {
		return super.findPage(page, pbocStoStoresInfo);
	}

	@Transactional(readOnly = false)
	public void save(PbocStoStoresInfo pbocStoStoresInfo) {
		super.save(pbocStoStoresInfo);
	}

	@Transactional(readOnly = false)
	public void delete(PbocStoStoresInfo pbocStoStoresInfo) {
		super.delete(pbocStoStoresInfo);
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
	@Transactional(readOnly = false)
	public synchronized String changeStore(List<ChangeStoreEntity> list, String storeOfficeId, String businessId, User user) {

		if (!Collections3.isEmpty(list)) {
			// add by wangbaozhong 2016-10-09  -start 重复物品累加 
			Map<String, ChangeStoreEntity> entityMap = Maps.newHashMap();
			for (ChangeStoreEntity changeStoreEntity : list) {
				if (entityMap.containsKey(changeStoreEntity.getGoodsId())) {
					ChangeStoreEntity tempEntity = entityMap.get(changeStoreEntity.getGoodsId());
					tempEntity.setNum(tempEntity.getNum() + changeStoreEntity.getNum());
				} else {
					entityMap.put(changeStoreEntity.getGoodsId(), changeStoreEntity);
				}
			}
			
			list = Lists.newArrayList();
			
			for (Map.Entry<String,ChangeStoreEntity> changeStoreEntity : entityMap.entrySet()) {
				list.add(changeStoreEntity.getValue());
			}
			// add by wangbaozhong 2016-10-09  -end 重复物品累加 
			// 获取最新库存信息
			Map<String, Object> listMap = getNewestStoreInfo(storeOfficeId);
			@SuppressWarnings("unchecked")
            List<PbocStoStoresInfo> pbocStoStoresInfoList = (List<PbocStoStoresInfo>) listMap.get("PbocStoStoresInfoList");
			boolean flag = (boolean) listMap.get("flag");
			// 获取所有物品信息
			List<StoGoods> goodsList = stoGoodsDao.findAllList(null);
			// 获取业务类型：调拨、盘点、兑换等
			String stoStatus = businessId.substring(businessId.length() - 6, businessId.length() - 4);
			Date date = new Date();
			// 当日无库存年复制没有物品变更的物品库存信息
			if (!flag) {
				for (PbocStoStoresInfo temp : pbocStoStoresInfoList) {
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
				PbocStoStoresInfo pbocStoStoresInfoTemp = new PbocStoStoresInfo();
				// 初始化数据
				pbocStoStoresInfoTemp.setStoId(IdGen.uuid());
				pbocStoStoresInfoTemp.setGoodsId(changeStoreEntity.getGoodsId());
				pbocStoStoresInfoTemp.setGoodsName(goodsName);
				pbocStoStoresInfoTemp.setStoNum(changeStoreEntity.getNum());
				pbocStoStoresInfoTemp.setOffice(new Office(storeOfficeId));
				pbocStoStoresInfoTemp.setGoodsType(goodsType);
				pbocStoStoresInfoTemp.setCreateBy(user);
				pbocStoStoresInfoTemp.setCreateName(user != null ? user.getName() : "");

				Long historyNum = 0L;
				for (PbocStoStoresInfo temp : pbocStoStoresInfoList) {
					if (temp.getGoodsId().equals(changeStoreEntity.getGoodsId())) {
						// 物品存在修改库存，封装entity
						// 如果库存量<变更量
						historyNum = temp.getStoNum();
						long num = temp.getStoNum() + changeStoreEntity.getNum();
						if (num < 0) {
							// 存在物品变更数量大于库存量
							throw new BusinessException("message.E1032", "", new String[] {
									StoreCommonUtils.getGoodsName(temp.getGoodsId()), temp.getStoNum().toString(),
									changeStoreEntity.getNum().toString() });
						}
						temp.setStoNum(num);
						pbocStoStoresInfoTemp = temp;
						exist = true;
						break;
					}
				}
				// 当日库存 不存在insert entity
				if (!flag) {
					if (pbocStoStoresInfoTemp.getStoNum() >= 0) {
						pbocStoStoresInfoTemp.setStoId(IdGen.uuid());
						BigDecimal bigNum = new BigDecimal(pbocStoStoresInfoTemp.getStoNum());
						pbocStoStoresInfoTemp.setAmount(amountUnit.multiply(bigNum));
						pbocStoStoresInfoTemp.setCreateDate(date);
						dao.insert(pbocStoStoresInfoTemp);
					} else {
						// 库存中不存在物品，还要执行减库存动作 错误
						throw new BusinessException("message.E1033", "", StoreCommonUtils.getGoodsName(changeStoreEntity.getGoodsId()));
					}
				} else { // 当日存在库存
					if (exist) {
						// 库存存在，update entity
						BigDecimal bigNum = new BigDecimal(pbocStoStoresInfoTemp.getStoNum());
						pbocStoStoresInfoTemp.setAmount(amountUnit.multiply(bigNum));
						pbocStoStoresInfoTemp.setCreateDate(date);
						dao.update(pbocStoStoresInfoTemp);
					} else {
						if (changeStoreEntity.getNum() >= 0) {
							BigDecimal bigNum = new BigDecimal(pbocStoStoresInfoTemp.getStoNum());
							pbocStoStoresInfoTemp.setAmount(amountUnit.multiply(bigNum));
							pbocStoStoresInfoTemp.setCreateDate(date);
							dao.insert(pbocStoStoresInfoTemp);
						} else {
							// 库存中不存在物品，还要执行减库存动作 错误
							throw new BusinessException("message.E1033", "",
									StoreCommonUtils.getGoodsName(changeStoreEntity.getGoodsId()));
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

				storesHistory.setOffice(new Office(storeOfficeId));
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
	public PbocStoStoresInfo getPbocStoStoresInfoByGoodsId(String goodsId, String officeId, String excludeZeroFg) {
		return dao.getPbocStoStoresInfoByGoodsId(goodsId, officeId, excludeZeroFg, Global.getConfig("jdbc.type"));
	}

	/**
	 * 
	 * @author LLF
	 * @version 2015年9月24日
	 * 
	 *          获取最新库存信息
	 * @param officeId
	 * @return
	 */
	public Map<String,Object> getNewestStoreInfo(String officeId) {
		List<PbocStoStoresInfo> pbocStoStoresList = Lists.newArrayList();
		Map<String,Object> map = Maps.newHashMap();
		// 首先判断当日库存是否存在，不存在将上一日库存copy当日
		PbocStoStoresInfo pbocStoStoresInfo = new PbocStoStoresInfo();
		Date today = new Date();
		pbocStoStoresInfo.setOffice(new Office(officeId));
		pbocStoStoresInfo.setCreateDate(today);
		pbocStoStoresList = dao.findList(pbocStoStoresInfo);
		boolean flag = true;// 当日是否存在库存信息

		// 当日不存在库存信息
		if (Collections3.isEmpty(pbocStoStoresList)) {
			Date dDate = dao.getMaxStoreDate(pbocStoStoresInfo);
			Date date = dDate != null ? dDate : today;
			pbocStoStoresInfo.setCreateDate(date);
			pbocStoStoresList = dao.findList(pbocStoStoresInfo);
			flag = false;
		}
		map.put("flag",flag);
		map.put("PbocStoStoresInfoList",pbocStoStoresList);
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
	@Transactional(readOnly = false)
	public synchronized String changeSurplusStore(List<ChangeStoreEntity> list, String officeId) {
		try {
			if (!Collections3.isEmpty(list)) {
				
				// add by wangbaozhong 2017-06-07  -start 重复物品累加 
				Map<String, ChangeStoreEntity> entityMap = Maps.newHashMap();
				for (ChangeStoreEntity changeStoreEntity : list) {
					if (entityMap.containsKey(changeStoreEntity.getGoodsId())) {
						ChangeStoreEntity tempEntity = entityMap.get(changeStoreEntity.getGoodsId());
						tempEntity.setNum(tempEntity.getNum() + changeStoreEntity.getNum());
					} else {
						entityMap.put(changeStoreEntity.getGoodsId(), changeStoreEntity);
					}
				}
				
				list = Lists.newArrayList();
				
				for (Map.Entry<String,ChangeStoreEntity> changeStoreEntity : entityMap.entrySet()) {
					list.add(changeStoreEntity.getValue());
				}
				// add by wangbaozhong 2017-06-07  -end 重复物品累加 
				
				// 获取最新库存信息
				Map<String, Object> map = Maps.newHashMap();
				map = getNewestStoreInfo(officeId);
				@SuppressWarnings("unchecked")
                List<PbocStoStoresInfo> pbocStoStoresInfoList = (List<PbocStoStoresInfo>) map.get("PbocStoStoresInfoList");

				for (ChangeStoreEntity changeStoreEntity : list) {
					for (PbocStoStoresInfo pbocStoStoresInfo : pbocStoStoresInfoList) {
						if (pbocStoStoresInfo.getGoodsId().equals(changeStoreEntity.getGoodsId())) {
							long num = pbocStoStoresInfo.getSurplusStoNum() != null ? pbocStoStoresInfo.getSurplusStoNum() : 0;
							num = num + changeStoreEntity.getNum();
							pbocStoStoresInfo.setSurplusStoNum(num < 0 ? 0 : num);
							dao.update(pbocStoStoresInfo);
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
}