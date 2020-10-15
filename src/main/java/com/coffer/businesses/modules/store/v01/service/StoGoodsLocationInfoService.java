/**
 * @author WangBaozhong
 * @version 2016年5月17日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.service;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsLocationInfoDao;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;

/**
 * 库区内物品摆放位置Service
 * @author WangBaozhong
 *
 */
@Service
@Transactional(readOnly = true)
public class StoGoodsLocationInfoService extends CrudService<StoGoodsLocationInfoDao, StoGoodsLocationInfo> {
	
	/**
	 * 根据机构ID和库区类型查询库区内存放物品数量
	 * @author WangBaozhong
	 * @version 2016年5月18日
	 * 
	 *  
	 * @param officeId 机构ID
	 * @param storeAreaType 库区类型
	 * @return 存放物品数量
	 */
	public int getGoodsCountByOfficeIdAndAreaType(String officeId, String storeAreaType) {
		return dao.getGoodsCountByOfficeIdAndAreaType(officeId, storeAreaType);
	}
	
	/**
	 * 根据库区ID查询库区内存放物品数量
	 * @author WangBaozhong
	 * @version 2016年5月18日
	 * 
	 *  
	 * @param areaId 库区ID
	 * @return 存放物品数量
	 */
	public int getGoodsCountByAreaId(String areaId) {
		return dao.getGoodsCountByAreaId(areaId);
	}
	
	/**
	 * 保存物品到库区内
	 * @author WangBaozhong
	 * @version 2016年5月20日
	 * 
	 *  
	 * @param entity 物品明细
	 * @return 保存数量
	 */
	@Transactional(readOnly = false)
	public int insert(StoGoodsLocationInfo entity) {
		return dao.insert(entity);
	}
	
	/**
	 * 根据物品ID取得物品在库区内的数量
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param goodsId 物品ID
	 * @param officeId 所属金库ID
	 * @return 物品在库区内的数量
	 */
	@Transactional(readOnly = true)
	public long getGoodsNumInStoreAreaByGoodsId(String goodsId, String officeId) {
		// 取得库区内相关物品最小日期及数量
		StoGoodsLocationInfo inStoreDateInfo = super.dao.findMinDateAndCntByGoodsId(goodsId, officeId);
		if (inStoreDateInfo == null) {
			return 0l;
		}
		return inStoreDateInfo.getGoodsNum();
	}
	
	/**
	 * 取得绑定调拨物品库区信息列表
	 * @author WangBaozhong
	 * @version 2016年5月23日
	 * 
	 *  
	 * @param allocateItemList 调拨物品信息
	 * @param intervalDays 查询物品间隔日数
	 * @param errorMessageCode 出错消息代码
	 * @param errorGoodsId 出错物品ID
	 * @param errorAllId 出错流水单号
	 * @param officeId 所属金库ID
	 * @return 绑定物品库区信息列表
	 */
	@Transactional(readOnly = false)
	public List<AllAllocateGoodsAreaDetail> getBindingAreaInfoToDetail(List<PbocAllAllocateItem> allocateItemList,
			int intervalDays, String errorMessageCode, String errorGoodsId, String errorAllId, String officeId) {
		//返回结果
		List<AllAllocateGoodsAreaDetail> goodsAreaDetailList = Lists.newArrayList();
		//查询条件对象
		StoGoodsLocationInfo searchCondition = null;
		// 某一时间范围内物品列表
		List<StoGoodsLocationInfo> goodsLocationResultList = null;
		
		for (PbocAllAllocateItem item : allocateItemList) {
			// 判断物品是否已被设定取包区域
			if (Collections3.isEmpty(item.getGoodsAreaDetailList())) {
				String goodsId = item.getGoodsId();
	
				// 取得库区内相关物品最小日期及数量
				StoGoodsLocationInfo inStoreDateInfo = super.dao.findMinDateAndCntByGoodsId(goodsId, officeId);
				if (inStoreDateInfo == null) {
					errorGoodsId = goodsId;
					errorAllId = item.getAllId();
					errorMessageCode = "message.E2032";
					return null;
				}
				// 判断库区内物品是否充足
				if (item.getMoneyNumber() > inStoreDateInfo.getGoodsNum()) {
					errorGoodsId = goodsId;
					errorAllId = item.getAllId();
					errorMessageCode = "message.E2032";
					return null;
				}
				// 查询某一时间范围内物品对应库区数量
				int iSearchGoodsNum = 0; 
				//取得库区内物品Index
				int iGoodsLocationResultListIndex = 0;
				// 查询开始时间
				Date searchStartDate = null;
				//查询结束时间
				Date searchEndDate = null;
				
				for (int iIndex = 0; iIndex < item.getMoneyNumber(); iIndex ++) {
					if (iSearchGoodsNum == 0) {
						
						//设定查询条件
						searchCondition = new StoGoodsLocationInfo();
						// 物品ID
						searchCondition.setGoodsId(goodsId);
						// 如果时间间隔大于0，则设定查询日期范围
						if (intervalDays > 0) {
							if (searchStartDate == null) {
								searchStartDate = inStoreDateInfo.getInStoreDate();
							}
							searchEndDate = DateUtils.addDate(searchStartDate, intervalDays);
							// 设定查询开始时间
							searchCondition.setSearchDateStart(DateUtils.formatDateTime(searchStartDate));
							// 设定查询结束时间
							searchCondition.setSearchDateEnd(DateUtils.formatDateTime(searchEndDate));
						}
						//设定所属金库
						searchCondition.setOfficeId(officeId);
						// 根据物品ID和日期范围 查询物品位置信息列表
						goodsLocationResultList = super.dao.findGoodsListByGoodsIdAndDate(searchCondition);
						iSearchGoodsNum = goodsLocationResultList.size();
						iGoodsLocationResultListIndex = 0;
						// 将下次查询开始时间设定为本次的查询的结束时间
						searchStartDate = searchEndDate;
						if (iSearchGoodsNum == 0) {
							iIndex--;
							continue;
						}
					}
					
					StoGoodsLocationInfo tempLocationInfo = goodsLocationResultList.get(iGoodsLocationResultListIndex);
					iSearchGoodsNum --;
					iGoodsLocationResultListIndex++;
					
					// 变更物品使用状态为已预订
					tempLocationInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED);
					tempLocationInfo.preUpdate();
					super.dao.updateReserveGoodsStatus(tempLocationInfo);
					
					AllAllocateGoodsAreaDetail goodsAreaDetail = new AllAllocateGoodsAreaDetail();
					goodsAreaDetail.setAllItemsId(item.getAllItemsId());
					goodsAreaDetail.setGoodsLocationId(tempLocationInfo.getId());
					
					// 如果当前日期间隔内物品数量小于审批数量 则为必须出库物品, 否则为非必须出库物品。 用于扫描门拦截
					if ( iSearchGoodsNum < (item.getMoneyNumber() - iIndex - 1)) {
						goodsAreaDetail.setIsNecessaryOut(Constant.NecessaryOut.NECESSARY_OUT_YES);
					} else {
						goodsAreaDetail.setIsNecessaryOut(Constant.NecessaryOut.NECESSARY_OUT_NO);
					}
					// 设定调拨流水单号
					goodsAreaDetail.setAllId(item.getAllId());
					
					goodsAreaDetailList.add(goodsAreaDetail);
					
					
				}
				
			}
			
		}
		
		return goodsAreaDetailList;
	}
	
	/**
	 * 根据RFID和出入库流水单号查询物品所在库区位置信息
	 * @author WangBaozhong
	 * @version 2016年6月7日
	 * 
	 *  
	 * @param stoGoodsLocationInfo 查询条件
	 * @return 物品所在库区位置信息
	 */
	public StoGoodsLocationInfo getByAllIDAndRfid(StoGoodsLocationInfo stoGoodsLocationInfo) {
		return super.dao.getByAllIDAndRfid(stoGoodsLocationInfo);
	}
	
	/**
	 * 根据流水单号和实际出库RFID更新库区内物品状态
	 * 将审批时预订物品还原为未使用，将实际出库物品更新为已使用
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 *  
	 * @param allId	出库流水单号
	 * @param actualRfidList 实际出库rfid列表
	 * @param reserveRfidList 预订出库rfid列表
	 * @param updateUser 更新者信息
	 * @throws BusinessException 更新失败时返回异常
	 */
	@Transactional(readOnly = false)
	public void updateGoodsOutStoreStatus(String allId, List<String> actualRfidList, List<String> reserveRfidList, User updateUser, String officeId) {
		int iUpdateCnt = 0;
		StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
		// 修改实际出库物品状态
		for (String rfid : actualRfidList) {
			if (reserveRfidList.contains(rfid)) {
				reserveRfidList.remove(rfid);
			}
			
			stoGoodsLocationInfo = new StoGoodsLocationInfo();
			stoGoodsLocationInfo.setRfid(rfid);
			// 更新者信息
			stoGoodsLocationInfo.setUpdateBy(updateUser);
			// 更新时间
			stoGoodsLocationInfo.setUpdateDate(new Date());
			// 出库流水单号
			stoGoodsLocationInfo.setOutStoreAllId(allId);
			stoGoodsLocationInfo.setOutStoreDate(stoGoodsLocationInfo.getUpdateDate());
			// 设置机构id
			stoGoodsLocationInfo.setOfficeId(officeId);
			// 更改库区物品状态为已使用
			stoGoodsLocationInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_USED);
			iUpdateCnt = super.dao.updateOutStoreGoodsStatusByRfid(stoGoodsLocationInfo);
			if (iUpdateCnt == 0) {
				logger.error("出库流水单号：" + allId + ",对应出库RFID：" + rfid + ",出库状态修改失败！");
			}
		}
		// 还原预订物品状态为未使用
		for (String rfid : reserveRfidList) {
			stoGoodsLocationInfo = new StoGoodsLocationInfo();
			stoGoodsLocationInfo.setRfid(rfid);
			// 设置机构id
			stoGoodsLocationInfo.setOfficeId(officeId);
			// 更新者信息
			stoGoodsLocationInfo.setUpdateBy(updateUser);
			// 更新时间
			stoGoodsLocationInfo.setUpdateDate(new Date());
			// 更改库区物品状态为未使用
			stoGoodsLocationInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
			iUpdateCnt = super.dao.updateReserveGoodsStatusByRfid(stoGoodsLocationInfo);
			if (iUpdateCnt == 0) {
				logger.error("出库流水单号：" + allId + ",对应还原RFID：" + rfid + ",还原状态修改失败！");
			}
		}
		
	}
	
	/**
	 * 
	 * @author LLF
	 * @version 2016年6月14日
	 * 
	 *  
	 * @param allList
	 * @return
	 */
	public List<Map<String,Object>> findStoreAreaByInStoreId(List<PbocAllAllocateInfo> allList) {
		return dao.findStoreAreaByInStoreId(allList);
	}
	
	/**
     * 
     * @author chengshu
     * @version 2016年6月14日
     * 
     *  
     * @param stoGoodsLocationInfo rfid库区信息
     * @return
     */
	@Transactional(readOnly = false)
    public int updateRfidStoreArea(StoGoodsLocationInfo stoGoodsLocationInfo) {
        return dao.updateRfidStoreArea(stoGoodsLocationInfo);
    }
    
    /**
     * 
     * Title: getCountByRfid
     * <p>Description: 取得库区内相同RFID数量</p>
     * @author:     wangbaozhong
     * @param rfid
     * @param officeId 机构ID
     * @return 
     * int    返回类型
     */
    public int getCountByRfid(String rfid, String officeId) {
    	return dao.getCountByRfid(rfid, officeId);
    }
    
    /**
     * 
     * Title: deleteByRfidAndOfficeId
     * <p>Description: 按Rfid和所属机构ID删除库区物品（初始化库区 或倒库用  将 物品审定为 清理库区 ）</p>
     * @author:     wangbaozhong
     * @param rfid
     * @param officeId 所属机构ID
     * @return 
     * int    返回类型
     */
    @Transactional(readOnly = false)
    public int deleteByRfidAndOfficeId(String rfid, String officeId) {
    	return dao.deleteByRfidAndOfficeId(rfid, officeId);
    }
    
    /**
	 * 根据物品ID变更物品预订状态
	 * @author WangBaozhong
	 * @version 2016年6月2日
	 * 
	 *  
	 * @param stoGoodsLocationInfo 更新信息
	 * @return 变更数量
	 */
    @Transactional(readOnly = false)
	public int updateReserveGoodsStatus(StoGoodsLocationInfo stoGoodsLocationInfo) {
		return dao.updateReserveGoodsStatus(stoGoodsLocationInfo);
	}
    /**
	 * 根据RFID修改物品出库状态
	 * @author WangBaozhong
	 * @version 2016年6月8日
	 * 
	 *  
	 * @return 更新数量
	 */
    @Transactional(readOnly = false)
	public int updateOutStoreGoodsStatusByRfid(StoGoodsLocationInfo stoGoodsLocationInfo) {
		return dao.updateOutStoreGoodsStatusByRfid(stoGoodsLocationInfo);
	}
    
    /**
     * 通过原rfid号替换成新的rfid号
     * @author caixiaojie
     * @param srcRfid 原rfid编号
     * @param dstRfid 新rfid编号
     * @param userId 更新者id
     * @param userName 更新者姓名
     * @return 更新数量
     */
    @Transactional(readOnly = false)
    public int updateRfidInfo(String srcRfid,String dstRfid,String userId,String userName)
    {
    	return dao.updateRfidInfo(srcRfid, dstRfid, userId, userName, new Date());
    }
    
	/**
	 * 更新实体
	 * @param entity 实体
	 * @return
	 */
	@Transactional(readOnly = false)
	public int update(StoGoodsLocationInfo entity) {
		return dao.update(entity);
	}
	/**
     * 
     * Title: updateUnUsedGoodsStatusByAreaId
     * <p>Description: 更新库区下未使用物品为清理状态</p>
     * @author:     wangbaozhong
     * @param stoGoodsLocationInfo	更新信息
     * @return 更新数量
     * int    返回类型
     */
	@Transactional(readOnly = false)
	public int clearUnUsedGoodsAtStore(String areaId, String goodsId, User loginUser) {
		
		List<String> updateRfidList = Lists.newArrayList();
		
		// 查询指定库区内所有物品信息
		StoGoodsLocationInfo param = new StoGoodsLocationInfo();
		param.setStoreAreaId(areaId);
		List<StoGoodsLocationInfo> unUsedGoodsList = super.findList(param);
		int updateCnt = 0;
		// 按库区ID更新物品清理状态
		for (StoGoodsLocationInfo goodsLocationInfo : unUsedGoodsList) {
			goodsLocationInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_CLEAR);
			goodsLocationInfo.setUpdateBy(loginUser);
			goodsLocationInfo.setUpdateDate(new Date());
			int iTempCnt = dao.updateUnUsedGoodsStatusByLocationId(goodsLocationInfo);
			updateCnt += iTempCnt;
			updateRfidList.add(goodsLocationInfo.getRfid());
		}
		if (updateCnt > 0) {
			StoAreaSettingInfo areaInfo = StoreCommonUtils.getByStoreAreaId(areaId);
			String businessType = Global.getConfig("businessType.allocation.pboc.clearStore");
			Office office = SysCommonUtils.findOfficeById(areaInfo.getOfficeId());
			if (!StoreConstant.StoreAreaType.STORE_AREA_ORIGINAL.equals(areaInfo.getStoreAreaType())) {
				// UPDATE-START  原因：更新RFID绑定信息时传入参数由本机构改为库区所属机构  update by SonyYuanYang  2018/04/17
				StoreCommonUtils.updateRfidStatusForClear(updateRfidList, Constant.BoxStatus.COFFER,
						 loginUser.getId(), loginUser.getName(), StoreConstant.RfidUseFlag.rfidClear,
						 office, businessType);
		            
	            // 将RFID当前邦定记录表导入历史表
	            StoreCommonUtils.insertInToRfidDemHistory(updateRfidList, office);
	         // UPDATE-END  原因：更新RFID绑定信息时传入参数由本机构改为库区所属机构  update by SonyYuanYang  2018/04/17
			}
			
			// 根据更新数量 变更库存
			List<ChangeStoreEntity> entiryList = Lists.newArrayList();
			ChangeStoreEntity entity = new ChangeStoreEntity();
			entity.setGoodsId(goodsId);
			entity.setNum(-Long.valueOf(updateCnt));
			entiryList.add(entity);
			// 取得清理库区流水单号
			String allId = BusinessUtils.getPbocNewBusinessNo(businessType, 
					loginUser.getOffice());
			// UPDATE-START  原因：更新库存时传入参数由本机构改为库区所属机构  update by SonyYuanYang  2018/04/17
			// 执行变更库存
			StoreCommonUtils.changePbocStoreAndSurplusStores(entiryList, office.getId(), allId, loginUser);
			// UPDATE-END  原因：更新库存时传入参数由本机构改为库区所属机构  update by SonyYuanYang  2018/04/17
		}
		
		
		return updateCnt;
	}
	 /**
     * 
     * Title: getReservedGoodsCountByAreaId
     * <p>Description: 查询库区下已预订物品数量 </p>
     * @author:     wangbaozhong
     * @param storeAreaId 库区ID
     * @return 库区下已预订物品数量
     * int    返回类型
     */
	@Transactional(readOnly = true)
	public int getReservedGoodsCountByAreaId(String storeAreaId) {
		return dao.getReservedGoodsCountByAreaId(storeAreaId);
	}
	
	/**
     * 
     * Title: findListForInterface
     * <p>Description: 接口用查询位置信息类表</p>
     * @author:     wangbaozhong
     * @param stoGoodsLocationInfo
     * @return 物品位置信息列表
     * List<StoGoodsLocationInfo>    返回类型
     */
	@Transactional(readOnly = true)
    public List<StoGoodsLocationInfo> findListForInterface(StoGoodsLocationInfo stoGoodsLocationInfo) {
		return dao.findListForInterface(stoGoodsLocationInfo);
	}
}
