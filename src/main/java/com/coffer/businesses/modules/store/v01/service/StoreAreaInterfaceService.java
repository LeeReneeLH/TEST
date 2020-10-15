/**
 * @author WangBaozhong
 * @version 2016年5月19日
 * 
 * 
 */
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
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoAreaSettingInfoDao;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsDao;
import com.coffer.businesses.modules.store.v01.dao.StoGoodsLocationInfoDao;
import com.coffer.businesses.modules.store.v01.entity.StoAreaSettingInfo;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 库区接口
 * @author chengshu
 *
 */
@Service
@Transactional(readOnly = true)
public class StoreAreaInterfaceService  extends CrudService<StoGoodsDao, StoGoods>   {
	@Autowired
	private StoAreaSettingInfoDao stoAreaSettingInfoDao;

	@Autowired
	private StoGoodsLocationInfoDao stoGoodsLocationInfoDao;
	
	@Autowired
	private StoGoodsService stoGoodsService;
	
	/**
	 * 人行扫描门入库登记(保存物品到库区)
	 * @author WangBaozhong
	 * @version 2016年5月20日
	 *  
	 * @param requestMap 参数列表
	 * @return 返回值列表
	 */
	@Transactional(readOnly = false)
	public void saveGoodsIntoArea(PbocAllAllocateInfo inputParam, Map<String, Object> map) {
		logger.debug("保存物品到库区接口-------- 开始");
		
		//机构ID
		String officeId = inputParam.getAoffice().getId();
		List<StoGoodsLocationInfo> paramGoodsInfoList = this.getParam(inputParam);
		
		if (paramGoodsInfoList == null) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		} else {
			String strIsSavegoodsByAreaType = Global.getConfig("store.area.isSavegoodsByAreaType");
			// 判段是否按照物品种类存放不同库区
			if (StringUtils.isNotBlank(strIsSavegoodsByAreaType) && StoreConstant.IsSavegoodsByAreaType.YES.equals(strIsSavegoodsByAreaType)) {
				Map<String, List<StoGoodsLocationInfo>> goodsLocationInfoMap = this.getGoodsInfoByClassification(paramGoodsInfoList);
				 Map<String, List<StoAreaSettingInfo>> areaSettingInfoMap = this.getAreaActualStorageByAreaType(officeId);
				
				String strRelation = Global.getConfig("store.areaType.goodsClassification.relation");
				String[] strRelationArray =  strRelation.split(Constant.Punctuation.HALF_SEMICOLONE);
				Map<String, String> relationMap = Maps.newHashMap();
				for (String relation : strRelationArray) {
					//库区类型1;物品种类1
					String [] keyValue = relation.split(Constant.Punctuation.HALF_COLON);
					//key :物品种类, Valye: 库区类型
					relationMap.put(keyValue[1], keyValue[0]);
				}
				// 按照物品种类判断对应库区位置是否充足
				List<String> notEnoughCapabilityList = this.checkStoreCapacity(areaSettingInfoMap, goodsLocationInfoMap, relationMap);
				if (notEnoughCapabilityList.size() > 0) {
//					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
//					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
//					map.put(Parameter.ERROR_MSG_KEY,
//                            "以下库区位置不足:" + Collections3.convertToString(notEnoughCapabilityList, Constant.Punctuation.COMMA));
					
                    logger.warn("以下库区位置不足：" + Collections3.convertToString(notEnoughCapabilityList, Constant.Punctuation.COMMA));
                    throw new BusinessException("message.E1055", "以下库区位置不足:" + Collections3.convertToString(notEnoughCapabilityList, Constant.Punctuation.COMMA), "");
				} else {
					saveGoodsToDifferentArea(areaSettingInfoMap, goodsLocationInfoMap, relationMap, map);
				}
			} else {
				StoAreaSettingInfo conditionInfo = new StoAreaSettingInfo();
				conditionInfo.setOfficeId(officeId);
				List<StoAreaSettingInfo> storeAreaInfoList = stoAreaSettingInfoDao.findAreaActualStorageList(conditionInfo);
				
				saveGoods(storeAreaInfoList, paramGoodsInfoList, map);
			}
			
		}
		logger.debug("保存物品到库区接口-------- 返回结果:" + gson.toJson(map));
		logger.debug("保存物品到库区接口-------- 结束");
	}
	
	/**
	 * 人行扫描门入库登记(按物品ID保存物品到库区)
	 * @author WangBaozhong
	 * @version 2016年5月20日
	 *  
	 * @param requestMap 参数列表
	 * @return 返回值列表
	 */
	@Transactional(readOnly = false)
	public void saveGoodsIntoAreaByGoodsId(PbocAllAllocateInfo inputParam, Map<String, Object> map) {
		logger.debug("保存物品到库区接口-------- 开始");
		
		//机构ID
		String officeId = inputParam.getAoffice().getId();
		List<StoGoodsLocationInfo> paramGoodsInfoList = this.getParam(inputParam);
		
		if (paramGoodsInfoList == null) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		} else {
			String strIsSavegoodsByAreaType = Global.getConfig("store.area.isSavegoodsByAreaType");
			// 判段是否按照物品种类存放不同库区
			if (StringUtils.isNotBlank(strIsSavegoodsByAreaType) && StoreConstant.IsSavegoodsByAreaType.YES.equals(strIsSavegoodsByAreaType)) {
				Map<String, List<StoGoodsLocationInfo>> goodsLocationInfoMap = this.getGoodsInfoByGoodsId(paramGoodsInfoList);
				Map<String, List<StoAreaSettingInfo>> areaSettingInfoMap = this.getAreaActualStorageByGoodsId(officeId);
				
//				String strRelation = Global.getConfig("store.areaType.goodsClassification.relation");
//				String[] strRelationArray =  strRelation.split(Constant.Punctuation.HALF_SEMICOLONE);
//				Map<String, String> relationMap = Maps.newHashMap();
//				for (String relation : strRelationArray) {
//					//库区类型1;物品种类1
//					String [] keyValue = relation.split(Constant.Punctuation.HALF_COLON);
//					//key :物品种类, Valye: 库区类型
//					relationMap.put(keyValue[1], keyValue[0]);
//				}
				// 按照物品ID判断对应库区位置是否充足
				List<String> notEnoughCapabilityList = this.checkStoreCapacityByGoodsId(areaSettingInfoMap, goodsLocationInfoMap);
				if (notEnoughCapabilityList.size() > 0) {
//					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
//					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
//					map.put(Parameter.ERROR_MSG_KEY,
//                            "以下库区位置不足:" + Collections3.convertToString(notEnoughCapabilityList, Constant.Punctuation.COMMA));
					
                    logger.warn("以下物品库区位置不足：" + Collections3.convertToString(notEnoughCapabilityList, Constant.Punctuation.COMMA));
                    throw new BusinessException("message.E1055", "以下物品库区位置不足:" + Collections3.convertToString(notEnoughCapabilityList, Constant.Punctuation.COMMA), "");
				} else {
					saveGoodsToDifferentAreaByGoodsId(areaSettingInfoMap, goodsLocationInfoMap, map);
				}
			} else {
				StoAreaSettingInfo conditionInfo = new StoAreaSettingInfo();
				conditionInfo.setOfficeId(officeId);
				List<StoAreaSettingInfo> storeAreaInfoList = stoAreaSettingInfoDao.findAreaActualStorageList(conditionInfo);
				
				saveGoods(storeAreaInfoList, paramGoodsInfoList, map);
			}
			
		}
		logger.debug("保存物品到库区接口-------- 返回结果:" + gson.toJson(map));
		logger.debug("保存物品到库区接口-------- 结束");
	}
	
	/**
	 * 
	 * Title: checkStoreCapacity
	 * <p>Description: 按物品种类判断对应库区容量是否充足</p>
	 * @author:     wangbaozhong
	 * @param areaSettingInfoMap 按库区类型区分库区信息
	 * @param goodsLocationInfoMap 按物品种类区分物品信息
	 * @param relationMap 物品种类与库区类型关系
	 * @return 库区位置不足列表
	 * List<String>    返回类型
	 */
	private List<String> checkStoreCapacity(Map<String, List<StoAreaSettingInfo>> areaSettingInfoMap, 
			Map<String, List<StoGoodsLocationInfo>> goodsLocationInfoMap, Map<String, String> relationMap) {
		logger.debug("按物品种类判断对应库区容量是否充足-------- 开始");
		List<String> notEnoughList = Lists.newArrayList();
		//按物品种类判断对应库区容量是否充足
		Iterator<String> goodsClassificationIterator = relationMap.keySet().iterator();
		while (goodsClassificationIterator.hasNext()) {
			// 物品种类
			String strGoodsClassification = goodsClassificationIterator.next();
			// 库区类型
			String strAreaType = relationMap.get(strGoodsClassification);
			List<StoAreaSettingInfo> storeAreaInfoList = areaSettingInfoMap.get(strAreaType);
			List<StoGoodsLocationInfo> goodsInfoList = goodsLocationInfoMap.get(strGoodsClassification);
			if (goodsInfoList == null) {
				continue;
			}
			long lAllSurplusCapability = 0;
			for (StoAreaSettingInfo temp : storeAreaInfoList) {
				if (temp.getSurplusStorage() == 0) {
					continue;
				}
				lAllSurplusCapability += temp.getSurplusStorage();
			}
			
			if (lAllSurplusCapability < goodsInfoList.size()) {
				String strAreaTypeName = DictUtils.getDictLabel(strAreaType, "store_area_type", "");
				notEnoughList.add(strAreaTypeName);
			}
		}
		logger.debug("按物品种类判断对应库区容量是否充足-------- 返回结果:" + gson.toJson(notEnoughList));
		logger.debug("按物品种类判断对应库区容量是否充足-------- 结束");
		return notEnoughList;
	}
	
	/**
	 * 
	 * Title: checkStoreCapacity
	 * <p>Description: 按物品ID判断对应库区容量是否充足</p>
	 * @author:     wangbaozhong
	 * @param areaSettingInfoMap 按库区类型区分库区信息
	 * @param goodsLocationInfoMap 按物品种类区分物品信息
	 * @param relationMap 物品种类与库区类型关系
	 * @return 库区位置不足列表
	 * List<String>    返回类型
	 */
	private List<String> checkStoreCapacityByGoodsId(Map<String, List<StoAreaSettingInfo>> areaSettingInfoMap, 
			Map<String, List<StoGoodsLocationInfo>> goodsLocationInfoMap) {
		logger.debug("按物品ID判断对应库区容量是否充足-------- 开始");
		List<String> notEnoughList = Lists.newArrayList();
		//按物品种类判断对应库区容量是否充足
		Iterator<String> goodsIdIterator = goodsLocationInfoMap.keySet().iterator();
		while (goodsIdIterator.hasNext()) {
			// 物品种类
			String strGoodsId = goodsIdIterator.next();
			
			List<StoAreaSettingInfo> storeAreaInfoList = areaSettingInfoMap.get(strGoodsId);
			List<StoGoodsLocationInfo> goodsInfoList = goodsLocationInfoMap.get(strGoodsId);
			
			if (goodsInfoList == null) {
				continue;
			}
			long lAllSurplusCapability = 0;
			for (StoAreaSettingInfo temp : storeAreaInfoList) {
				if (temp.getSurplusStorage() == 0) {
					continue;
				}
				lAllSurplusCapability += temp.getSurplusStorage();
			}
			
			if (lAllSurplusCapability < goodsInfoList.size()) {
				String goodsName = StoreCommonUtils.getGoodsName(strGoodsId);
				notEnoughList.add(goodsName);
			}
		}
		logger.debug("按物品ID判断对应库区容量是否充足-------- 返回结果:" + gson.toJson(notEnoughList));
		logger.debug("按物品ID判断对应库区容量是否充足-------- 结束");
		return notEnoughList;
	}
	
	/**
	 * 
	 * Title: getGoodsInfoByClassification
	 * <p>Description: 按照物品种别区分物品</p>
	 * @author:     wangbaozhong
	 * @param paramGoodsInfoList 物品信息列表
	 * @return 物品种别区分物品Map
	 * Map<String,List<StoGoodsLocationInfo>>    返回类型 key : 物品种类
	 */
	private Map<String, List<StoGoodsLocationInfo>> getGoodsInfoByClassification(List<StoGoodsLocationInfo> paramGoodsInfoList) {
		logger.debug("按照物品种别区分物品-------- 开始");
		Map<String, List<StoGoodsLocationInfo>> rtnMap = Maps.newHashMap();
		
		for (StoGoodsLocationInfo goodsLocationInfo : paramGoodsInfoList) {
			String strClassification = StoreCommonUtils.splitGood(goodsLocationInfo.getGoodsId()).getClassification();
			
			if (rtnMap.containsKey(strClassification)) {
				rtnMap.get(strClassification).add(goodsLocationInfo);
			} else {
				List<StoGoodsLocationInfo> newList = Lists.newArrayList();
				newList.add(goodsLocationInfo);
				rtnMap.put(strClassification, newList);
			}
		}
		logger.debug("按照物品种别区分物品-------- 返回结果:" + gson.toJson(rtnMap));
		logger.debug("按照物品种别区分物品-------- 结束");
		return rtnMap;
	}
	
	/**
	 * 
	 * Title: getGoodsInfoByClassification
	 * <p>Description: 按照物品ID区分物品</p>
	 * @author:     wangbaozhong
	 * @param paramGoodsInfoList 物品信息列表
	 * @return 物品种别区分物品Map
	 * Map<String,List<StoGoodsLocationInfo>>    返回类型 key : 物品种类
	 */
	private Map<String, List<StoGoodsLocationInfo>> getGoodsInfoByGoodsId(List<StoGoodsLocationInfo> paramGoodsInfoList) {
		logger.debug("按照物品ID区分物品-------- 开始");
		Map<String, List<StoGoodsLocationInfo>> rtnMap = Maps.newHashMap();
		
		for (StoGoodsLocationInfo goodsLocationInfo : paramGoodsInfoList) {
			
			if (rtnMap.containsKey(goodsLocationInfo.getGoodsId())) {
				rtnMap.get(goodsLocationInfo.getGoodsId()).add(goodsLocationInfo);
			} else {
				List<StoGoodsLocationInfo> newList = Lists.newArrayList();
				newList.add(goodsLocationInfo);
				rtnMap.put(goodsLocationInfo.getGoodsId(), newList);
			}
		}
		logger.debug("按照物品ID区分物品-------- 返回结果:" + gson.toJson(rtnMap));
		logger.debug("按照物品ID区分物品-------- 结束");
		return rtnMap;
	}
	
	/**
	 * 
	 * Title: getAreaActualStorageByAreaType
	 * <p>Description: 按照机构ID和库区类型取得库区信息</p>
	 * @author:     wangbaozhong
	 * @param officeId 机构ID
	 * @return 库区类型对应库区信息
	 * Map<String,List<StoAreaSettingInfo>>    返回类型  key:库区类型
	 */
	private Map<String, List<StoAreaSettingInfo>> getAreaActualStorageByAreaType(String officeId) {
		logger.debug("按照机构ID和库区类型取得库区信息-------- 开始");
		Map<String, List<StoAreaSettingInfo>> rtnMap = Maps.newHashMap();
		List<Dict> storeAreaTypeList = DictUtils.getDictList("store_area_type");
		
		for (Dict typeInfo : storeAreaTypeList) {
			StoAreaSettingInfo conditionInfo = new StoAreaSettingInfo();
			conditionInfo.setOfficeId(officeId);
			conditionInfo.setStoreAreaType(typeInfo.getValue());
			List<StoAreaSettingInfo> storeAreaInfoList = stoAreaSettingInfoDao.findAreaActualStorageList(conditionInfo);
			rtnMap.put(typeInfo.getValue(), storeAreaInfoList);
		}
		logger.debug("按照机构ID和库区类型取得库区信息-------- 返回结果:" + gson.toJson(rtnMap));
		logger.debug("按照机构ID和库区类型取得库区信息 -------- 结束");
		return rtnMap;
	}
	
	/**
	 * 
	 * Title: getAreaActualStorageByAreaType
	 * <p>Description: 按照机构ID和物品ID取得库区信息</p>
	 * @author:     wangbaozhong
	 * @param officeId 机构ID
	 * @return 库区类型对应库区信息
	 * Map<String,List<StoAreaSettingInfo>>    返回类型  key:库区类型
	 */
	private Map<String, List<StoAreaSettingInfo>> getAreaActualStorageByGoodsId(String officeId) {
		logger.debug("按照机构ID和物品ID取得库区信息-------- 开始");
		Map<String, List<StoAreaSettingInfo>> rtnMap = Maps.newHashMap();
		List<StoGoods> stoGoodsList = stoGoodsService.findList(new StoGoods());
		
		for (StoGoods stoGoods : stoGoodsList) {
			StoAreaSettingInfo conditionInfo = new StoAreaSettingInfo();
			conditionInfo.setOfficeId(officeId);
			conditionInfo.setGoodsId(stoGoods.getGoodsID());
			List<StoAreaSettingInfo> storeAreaInfoList = stoAreaSettingInfoDao.findAreaActualStorageList(conditionInfo);
			rtnMap.put(stoGoods.getGoodsID(), storeAreaInfoList);
		}
		logger.debug("按照机构ID和物品ID取得库区信息-------- 返回结果:" + gson.toJson(rtnMap));
		logger.debug("按照机构ID和物品ID取得库区信息 -------- 结束");
		return rtnMap;
	}
	
	/**
	 * 保存物品信息
	 * @author WangBaozhong
	 * @version 2016年5月20日
	 * 
	 *  
	 * @param storeAreaInfoList 库区信息列表
	 * @param paramGoodsInfoList 入库物品列表
	 * @param rtnMap 物品摆放库区信息
	 */
	@Transactional(readOnly = false)
	private void saveGoods(List<StoAreaSettingInfo> storeAreaInfoList, List<StoGoodsLocationInfo> paramGoodsInfoList, Map<String, Object> rtnMap) {
		logger.debug("保存物品到库区接口（保存物品信息）-------- 开始");
		//判断库区容量是否充足
		long lAllSurplusCapability = 0;
		List<StoAreaSettingInfo> notNullStoreAreaInfoList = Lists.newArrayList();
		for (StoAreaSettingInfo temp : storeAreaInfoList) {
			if (temp.getSurplusStorage() == 0) {
				continue;
			}
			lAllSurplusCapability += temp.getSurplusStorage();
			notNullStoreAreaInfoList.add(temp);
		}
		
		if (lAllSurplusCapability < paramGoodsInfoList.size()) {
			rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E41);
			return;
		}
		
		List<Map<String, String>> rtnList = Lists.newArrayList();
		List<String> areaList = Lists.newArrayList();
		Map<String, String> goodsAreaMap = null;
		int iAreaIndex = 0;
		long lAreaSurplusCapality = 0;
		StoAreaSettingInfo areaInfo = null;
		for(StoGoodsLocationInfo goodsInfo : paramGoodsInfoList){
			
			if (lAreaSurplusCapality == 0) {
				areaInfo = this.getAreaInfo(notNullStoreAreaInfoList, iAreaIndex);
				
				if (areaInfo == null) {
		            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E43);
		            return;
				}
				
				lAreaSurplusCapality = areaInfo.getSurplusStorage();
				iAreaIndex++;
			}
			// 设定区域ID
			goodsInfo.setStoreAreaId(areaInfo.getId());
			// 设定创建日期  更新日期=创建日期=入库日期
			goodsInfo.setCreateDate(new Date());
			goodsInfo.setUpdateDate(goodsInfo.getCreateDate());
			goodsInfo.setInStoreDate(goodsInfo.getCreateDate());
			goodsInfo.setId(IdGen.uuid());
			goodsInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
			stoGoodsLocationInfoDao.insert(goodsInfo);
			
			goodsAreaMap = Maps.newHashMap();
			goodsAreaMap.put(Parameter.BOX_NO_KEY, goodsInfo.getRfid());
			goodsAreaMap.put(Parameter.GOODS_ID_KEY, goodsInfo.getGoodsId());
			goodsAreaMap.put(Parameter.GOODS_NAME_KEY, goodsInfo.getGoodsName());
			goodsAreaMap.put(Parameter.STORE_AREA_NAME_KEY, areaInfo.getStoreAreaName());
			
			rtnList.add(goodsAreaMap);
			lAreaSurplusCapality--;

            // 保存所有库区名称
            if(!areaList.contains(areaInfo.getStoreAreaName())){
                areaList.add(areaInfo.getStoreAreaName());
            }
		}

		// 每个箱袋详细库区信息
		rtnMap.put(Parameter.LIST_KEY, rtnList);
		// 当前流水号所有库区名称
		rtnMap.put(Parameter.TOTAL_AREA_KEY, Collections3.convertToString(areaList, Constant.Punctuation.COMMA));
		logger.debug("保存物品到库区接口（保存物品信息）-------- 返回结果:" + gson.toJson(rtnMap));
		logger.debug("保存物品到库区接口（保存物品信息）-------- 结束");
	}
	
	/**
	 * 按不同种类物品保存到对应库区
	 * @author WangBaozhong
	 * @version 2016年5月20日
	 * 
	 *  
	 * @param storeAreaInfoList 库区信息列表
	 * @param paramGoodsInfoList 入库物品列表
	 * @param rtnMap 物品摆放库区信息
	 */
	@Transactional(readOnly = false)
	private void saveGoodsToDifferentArea(Map<String, List<StoAreaSettingInfo>> areaSettingInfoMap, 
			Map<String, List<StoGoodsLocationInfo>> goodsLocationInfoMap, Map<String, String> relationMap, Map<String, Object> rtnMap) {
		logger.debug("按不同种类物品保存到对应库区 -------- 开始");
		List<Map<String, String>> rtnList = Lists.newArrayList();
		List<String> areaList = Lists.newArrayList();
		Iterator<String> goodsClassificationIterator = relationMap.keySet().iterator();
		while (goodsClassificationIterator.hasNext()) {
			// 物品种类
			String strGoodsClassification = goodsClassificationIterator.next();
			// 库区类型
			String strAreaType = relationMap.get(strGoodsClassification);
			List<StoAreaSettingInfo> storeAreaInfoList = areaSettingInfoMap.get(strAreaType);
			List<StoGoodsLocationInfo> goodsInfoList = goodsLocationInfoMap.get(strGoodsClassification);
			
			if (goodsInfoList == null || goodsInfoList.size() == 0) {
				continue;
			}
			
			//取得剩余库区
			List<StoAreaSettingInfo> notNullStoreAreaInfoList = Lists.newArrayList();
			for (StoAreaSettingInfo temp : storeAreaInfoList) {
				if (temp.getSurplusStorage() == 0) {
					continue;
				}
				notNullStoreAreaInfoList.add(temp);
			}
			
			Map<String, String> goodsAreaMap = null;
			int iAreaIndex = 0;
			long lAreaSurplusCapality = 0;
			StoAreaSettingInfo areaInfo = null;
			for(StoGoodsLocationInfo goodsInfo : goodsInfoList){
				
				if (lAreaSurplusCapality == 0) {
					areaInfo = this.getAreaInfo(notNullStoreAreaInfoList, iAreaIndex);
					
					if (areaInfo == null) {
			            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E43);
			            return;
					}
					
					lAreaSurplusCapality = areaInfo.getSurplusStorage();
					iAreaIndex++;
				}
				// 设定区域ID
				goodsInfo.setStoreAreaId(areaInfo.getId());
				// 设定创建日期  更新日期=创建日期=入库日期
				goodsInfo.setCreateDate(new Date());
				goodsInfo.setUpdateDate(goodsInfo.getCreateDate());
				goodsInfo.setInStoreDate(goodsInfo.getCreateDate());
				goodsInfo.setId(IdGen.uuid());
				goodsInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
				stoGoodsLocationInfoDao.insert(goodsInfo);
				
				goodsAreaMap = Maps.newHashMap();
				goodsAreaMap.put(Parameter.BOX_NO_KEY, goodsInfo.getRfid());
				goodsAreaMap.put(Parameter.GOODS_ID_KEY, goodsInfo.getGoodsId());
				goodsAreaMap.put(Parameter.GOODS_NAME_KEY, goodsInfo.getGoodsName());
				// 库区类型名称
				String strStoreAreaTypeName = DictUtils.getDictLabel(areaInfo.getStoreAreaType(), "store_area_type", "");
				goodsAreaMap.put(Parameter.STORE_AREA_NAME_KEY, strStoreAreaTypeName + " " + areaInfo.getStoreAreaName());
				
				rtnList.add(goodsAreaMap);
				lAreaSurplusCapality--;

	            // 保存所有库区名称
	            if(!areaList.contains(areaInfo.getStoreAreaName())){
	                areaList.add(areaInfo.getStoreAreaName());
	            }
			}
			
		}

		// 每个箱袋详细库区信息
		rtnMap.put(Parameter.LIST_KEY, rtnList);
		// 当前流水号所有库区名称
		rtnMap.put(Parameter.TOTAL_AREA_KEY, Collections3.convertToString(areaList, Constant.Punctuation.COMMA));
		logger.debug("按不同种类物品保存到对应库区-------- 返回结果:" + gson.toJson(rtnMap));
		logger.debug("按不同种类物品保存到对应库区-------- 结束");
	}
	
	/**
	 * 按不同物品ID保存到对应库区
	 * @author WangBaozhong
	 * @version 2016年5月20日
	 * 
	 *  
	 * @param storeAreaInfoList 库区信息列表
	 * @param paramGoodsInfoList 入库物品列表
	 * @param rtnMap 物品摆放库区信息
	 */
	@Transactional(readOnly = false)
	private void saveGoodsToDifferentAreaByGoodsId(Map<String, List<StoAreaSettingInfo>> areaSettingInfoMap, 
			Map<String, List<StoGoodsLocationInfo>> goodsLocationInfoMap, Map<String, Object> rtnMap) {
		logger.debug("按不同物品ID保存到对应库区 -------- 开始");
		List<Map<String, String>> rtnList = Lists.newArrayList();
		List<String> areaList = Lists.newArrayList();
		Iterator<String> goodsIdIterator = goodsLocationInfoMap.keySet().iterator();
		while (goodsIdIterator.hasNext()) {
			// 物品种类
			String strGoodsId = goodsIdIterator.next();
			// 库区类型
			List<StoAreaSettingInfo> storeAreaInfoList = areaSettingInfoMap.get(strGoodsId);
			List<StoGoodsLocationInfo> goodsInfoList = goodsLocationInfoMap.get(strGoodsId);
			
			if (goodsInfoList == null || goodsInfoList.size() == 0) {
				continue;
			}
			
			//取得剩余库区
			List<StoAreaSettingInfo> notNullStoreAreaInfoList = Lists.newArrayList();
			for (StoAreaSettingInfo temp : storeAreaInfoList) {
				if (temp.getSurplusStorage() == 0) {
					continue;
				}
				notNullStoreAreaInfoList.add(temp);
			}
			
			Map<String, String> goodsAreaMap = null;
			int iAreaIndex = 0;
			long lAreaSurplusCapality = 0;
			StoAreaSettingInfo areaInfo = null;
			for(StoGoodsLocationInfo goodsInfo : goodsInfoList){
				
				if (lAreaSurplusCapality == 0) {
					areaInfo = this.getAreaInfo(notNullStoreAreaInfoList, iAreaIndex);
					
					if (areaInfo == null) {
			            rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			            rtnMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E43);
			            return;
					}
					
					lAreaSurplusCapality = areaInfo.getSurplusStorage();
					iAreaIndex++;
				}
				// 设定区域ID
				goodsInfo.setStoreAreaId(areaInfo.getId());
				// 设定创建日期  更新日期=创建日期=入库日期
				goodsInfo.setCreateDate(new Date());
				goodsInfo.setUpdateDate(goodsInfo.getCreateDate());
				goodsInfo.setInStoreDate(goodsInfo.getCreateDate());
				goodsInfo.setId(IdGen.uuid());
				goodsInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
				stoGoodsLocationInfoDao.insert(goodsInfo);
				
				goodsAreaMap = Maps.newHashMap();
				goodsAreaMap.put(Parameter.BOX_NO_KEY, goodsInfo.getRfid());
				goodsAreaMap.put(Parameter.GOODS_ID_KEY, goodsInfo.getGoodsId());
				goodsAreaMap.put(Parameter.GOODS_NAME_KEY, goodsInfo.getGoodsName());
				// 库区类型名称
				String strStoreAreaTypeName = DictUtils.getDictLabel(areaInfo.getStoreAreaType(), "store_area_type", "");
				goodsAreaMap.put(Parameter.STORE_AREA_NAME_KEY, strStoreAreaTypeName + " " + areaInfo.getStoreAreaName());
				
				rtnList.add(goodsAreaMap);
				lAreaSurplusCapality--;

	            // 保存所有库区名称
	            if(!areaList.contains(areaInfo.getStoreAreaName())){
	                areaList.add(areaInfo.getStoreAreaName());
	            }
			}
			
		}

		// 每个箱袋详细库区信息
		rtnMap.put(Parameter.LIST_KEY, rtnList);
		// 当前流水号所有库区名称
		rtnMap.put(Parameter.TOTAL_AREA_KEY, Collections3.convertToString(areaList, Constant.Punctuation.COMMA));
		logger.debug("按不同物品ID保存到对应库区-------- 返回结果:" + gson.toJson(rtnMap));
		logger.debug("按不同物品ID保存到对应库区-------- 结束");
	}
	
	/**
	 * 取得指定库区信息
	 * @author WangBaozhong
	 * @version 2016年5月20日
	 * 
	 *  
	 * @param storeAreaInfoList 库区信息列表
	 * @param iAreaIndex 库区INDEX
	 * @return 指定库区信息
	 */
	private StoAreaSettingInfo getAreaInfo(List<StoAreaSettingInfo> storeAreaInfoList, int iAreaIndex) {
		
		if (iAreaIndex < storeAreaInfoList.size()) {
			return storeAreaInfoList.get(iAreaIndex);
		}
		
		return null;
	}
	
	/**
	 * 取得入库物品信息列表
	 * @author WangBaozhong
	 * @version 2016年5月19日
	 * 
	 *  
	 * @param requestMap 参数
	 * @param officeId 机构ID
	 * @return 入库物品信息列表
	 */
	public List<StoGoodsLocationInfo> getParam(PbocAllAllocateInfo inputParam) {
		logger.debug("保存物品到库区接口（取得入库物品信息列表）-------- 开始");
		// 取得更新者信息
		User loginUser = UserUtils.get(inputParam.getUserId());

		//流水单号
		String serialorderNo = inputParam.getAllId();

		List<StoGoodsLocationInfo> goodsInfoList = Lists.newArrayList();
			for (PbocAllAllocateDetail detail : inputParam.getPbocAllAllocateDetailList()) {
				StoGoodsLocationInfo goodsInfo = new StoGoodsLocationInfo();

				//入库流水单号
				goodsInfo.setInStoreAllId(serialorderNo);
				//物品归属人民银行ID
				goodsInfo.setOfficeId(inputParam.getAoffice().getId());
				// 物品数量
				goodsInfo.setGoodsNum(detail.getGoodsLocationInfo().getGoodsNum());
				//RFID
				goodsInfo.setRfid(detail.getRfid());
				//物品ID
				goodsInfo.setGoodsId(detail.getGoodsLocationInfo().getGoodsId());
				goodsInfo.setLoginUser(loginUser);

				goodsInfo.setCreateBy(loginUser);
				goodsInfo.setUpdateBy(loginUser);
				goodsInfo.setCreateName(loginUser.getName());
				goodsInfo.setUpdateName(loginUser.getName());

				StoGoods stoGoods = StoreCommonUtils.getGoodsInfoById(goodsInfo.getGoodsId());
				if (stoGoods == null) {
					logger.debug("保存物品到库区接口（取得入库物品信息列表）-------- 物品不存在，物品ID：" + goodsInfo.getGoodsId());
					logger.debug("保存物品到库区接口（取得入库物品信息列表）-------- 结束");
					return null;
				}

				//物品类型
				goodsInfo.setGoodsType(stoGoods.getGoodsType());
				//物品名称
				goodsInfo.setGoodsName(stoGoods.getGoodsName());
				// 物品价值
				goodsInfo.setAmount(stoGoods.getGoodsVal().multiply(new BigDecimal(goodsInfo.getGoodsNum())));	

				goodsInfoList.add(goodsInfo);
			}
		logger.debug("保存物品到库区接口（取得入库物品信息列表）-------- 结束");
		return goodsInfoList;
	}
}
