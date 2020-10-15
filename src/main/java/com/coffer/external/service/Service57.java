package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service57
* <p>Description: 库房初始入库接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service57")
@Scope("singleton")
public class Service57 extends HardwardBaseService {

    @Autowired
    private StoGoodsLocationInfoService goodsLocationService;

    @Autowired
    private StoRfidDenominationService stoRfidDenominationService;
    
    @Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	/**
     * 款箱入库
     * 
     * @author wangbaozhong
     * @version 2016年8月01日
     * 
     * @param headInfo
     *            参数列表
     * @return 返回值列表
     */
    @Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> headInfo) {

		logger.debug("57 库房初始入库接口 -----------------开始");
        Map<String, Object> respMap = new HashMap<String, Object>();
        // 版本号、服务代码
        respMap.put(Parameter.VERSION_NO_KEY, headInfo.get(Parameter.VERSION_NO_KEY));
        respMap.put(Parameter.SERVICE_NO_KEY, headInfo.get(Parameter.SERVICE_NO_KEY));

        String userId = "";

        // 用户ID
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)))) {
            logger.warn("输入参数错误：" + Parameter.USER_ID_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        } else {
        	userId = StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY));
        }

//        String officeId = "";
//        // 金库机构ID
//        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)))) {
//            logger.warn("输入参数错误：" + Parameter.OFFICE_ID_KEY + " 不存在或是空。");
//            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
//            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
//            return gson.toJson(respMap);
//        } else {
//        	officeId = StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY));
//        }
        // 箱袋列表
        @SuppressWarnings("unchecked")
        List<Map<String, String>> rfidList = (List<Map<String, String>>) headInfo.get(Parameter.BOX_LIST_KEY);
        if (rfidList == null || rfidList.size() == 0) {
            logger.warn("输入参数错误：" + Parameter.BOX_LIST_KEY + " 不存在或是空。");
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
            return gson.toJson(respMap);
        }

        // 更新RFID库区位置
        StoGoodsLocationInfo stoGoodsLocationInfo = null;
        List<ChangeStoreEntity> changeGoodsList = Lists.newArrayList();
        Map<String, ChangeStoreEntity> changeGoodsMap = Maps.newHashMap();
        ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
        User loginUser = UserUtils.get(userId);
        
        Office office = loginUser.getOffice();
        
        String allId = BusinessUtils.getPbocNewBusinessNo(Global.getConfig("businessType.allocation.pboc.initStore"), loginUser.getOffice());
        
        List<Map<String, String>> paramMapList = Lists.newArrayList();
        List<Map<String, String>> paramOriginalMapList = Lists.newArrayList();
        for (Map<String, String> rfidInfo : rfidList) {
        	if (rfidInfo == null) {
                continue;
            }
        	
        	// 物品ID
            String goodsId = "";
            if (StringUtils.isBlank(StringUtils.toString(rfidInfo.get(Parameter.GOODS_ID_KEY)))) {
                logger.warn("输入参数错误：" + Parameter.GOODS_ID_KEY + " 不存在或是空。");
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
                return gson.toJson(respMap);
            } else {
            	goodsId = StringUtils.toString(rfidInfo.get(Parameter.GOODS_ID_KEY));
            }
            
            // 库区ID
            String areaId = "";
            if (StringUtils.isBlank(StringUtils.toString(rfidInfo.get(Parameter.AREA_ID_KEY)))) {
                logger.warn("输入参数错误：" + Parameter.AREA_ID_KEY + " 不存在或是空。");
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
                return gson.toJson(respMap);
            } else {
            	areaId = StringUtils.toString(rfidInfo.get(Parameter.AREA_ID_KEY));
            }
        	
        	String rfid = "";
        	if (StringUtils.isBlank(StringUtils.toString(rfidInfo.get(Parameter.RFID_KEY)))) {
                logger.warn("输入参数错误：" + Parameter.RFID_KEY + " 不存在或是空。");
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
                return gson.toJson(respMap);
            } else {
            	rfid = StringUtils.toString(rfidInfo.get(Parameter.RFID_KEY));
            }
        	String flag = ""; // 初始化标识
        	if (StringUtils.isBlank(StringUtils.toString(rfidInfo.get(Parameter.FLAG_KEY)))) {
                logger.warn("输入参数错误：" + Parameter.FLAG_KEY + " 不存在或是空。");
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
                return gson.toJson(respMap);
            } else {
            	flag = StringUtils.toString(rfidInfo.get(Parameter.FLAG_KEY));
            }
        	
        	Map<String, String> paramMap = Maps.newHashMap();
        	paramMap.put(Parameter.GOODS_ID_KEY, goodsId);
        	paramMap.put(Parameter.AREA_ID_KEY, areaId);
        	paramMap.put(Parameter.RFID_KEY, rfid);
        	paramMap.put(Parameter.FLAG_KEY, flag);
        	
        	String storeAreaType = ""; //库区类型
        	if (StringUtils.isBlank(StringUtils.toString(rfidInfo.get(Parameter.STORE_AREA_TYPE_KEY)))) {
                logger.warn("输入参数错误：" + Parameter.STORE_AREA_TYPE_KEY + " 不存在或是空。");
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
                return gson.toJson(respMap);
            } else {
            	storeAreaType = StringUtils.toString(rfidInfo.get(Parameter.STORE_AREA_TYPE_KEY));
            }
        	
        	String originalTranslate = ""; // 原封券翻译
        	if (StoreConstant.StoreAreaType.STORE_AREA_ORIGINAL.equals(storeAreaType)) {
        		if (StringUtils.isBlank(StringUtils.toString(rfidInfo.get(Parameter.ORIGINAL_TRANSLATE_KEY)))) {
                    logger.warn("输入参数错误：" + Parameter.ORIGINAL_TRANSLATE_KEY + " 不存在或是空。");
                    respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                    respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
                    return gson.toJson(respMap);
                } else {
                	originalTranslate = StringUtils.toString(rfidInfo.get(Parameter.ORIGINAL_TRANSLATE_KEY));
                }
        		//库区类型
        		paramMap.put(Parameter.STORE_AREA_TYPE_KEY, storeAreaType);
        		// 原封券翻译
        		paramMap.put(Parameter.ORIGINAL_TRANSLATE_KEY, originalTranslate);
        		paramOriginalMapList.add(paramMap);
        		continue;
        	}
        	
        	paramMapList.add(paramMap);
        }
        
        String businessType = Global.getConfig("businessType.allocation.pboc.initStore");
        // 物品入库或更新库区位置
        for (Map<String, String> paramMap : paramMapList) {
        	
        	String flag = paramMap.get(Parameter.FLAG_KEY);
        	String rfid = paramMap.get(Parameter.RFID_KEY);
        	String areaId = paramMap.get(Parameter.AREA_ID_KEY);
        	String goodsId = paramMap.get(Parameter.GOODS_ID_KEY);
        	
        	if (ExternalConstant.OperationFlag.UPDATE_FLAG.equals(flag)) {
        		StoGoodsLocationInfo entity = new StoGoodsLocationInfo();
        		entity.setRfid(rfid);
        		entity.setStoreAreaId(areaId);
        		entity.setUpdateBy(loginUser);
        		entity.setUpdateDate(new Date());
        		goodsLocationService.updateRfidStoreArea(entity);
        	} else if (ExternalConstant.OperationFlag.INSERT_FLAG.equals(flag)) {
            	// 1.邦定RFID
            	// 查询是否存在已经绑定过的RFID
                StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
                List<String> rfidTempList = Lists.newArrayList();
                rfidTempList.add(rfid);
                stoRfidDenomination.setRfidList(rfidTempList);
                // ADD-START  原因：查询既有RFID信息  add by wangbaozhong  2018/05/23
                stoRfidDenomination.setDelFlag(null);
                // ADD-END  原因：查询既有RFID信息  add by wangbaozhong  2018/05/23
                // 执行查询处理
                List<StoRfidDenomination> stoRfidDenominationList = stoRfidDenominationService.findRFIDList(stoRfidDenomination);
             
                StoGoodsLocationInfo param = new StoGoodsLocationInfo();
                param.setRfid(rfid);
                param.setOfficeId(office.getId());
                
                // ADD-START  原因：增加查询条件  add by wangbaozhong  2018/05/23
                List<String> statusFlagList = Lists.newArrayList();
                statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
                statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED);
                statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_USED);
                param.setStatusFlagList(statusFlagList);
                // ADD-END  原因：增加查询条件  add by wangbaozhong  2018/05/23
                
                List<StoGoodsLocationInfo> locationInfoList = goodsLocationService.findListForInterface(param);
    			if (locationInfoList.size() > 0) {
//    				stoRfidDenominationService.deleteByPrimaryKeyAndOfficeId(rfid, "");
    				//按Rfid和所属机构ID删除库区物品（初始化库区 或倒库用  将 物品设定为 清理库区 ）
    				goodsLocationService.deleteByRfidAndOfficeId(rfid, office.getId());
    				for (StoGoodsLocationInfo info :locationInfoList) {
    					// UPDATE-START  原因：变更过滤条件  update by wangbaozhong  2018/05/23
//    					if (!Constant.deleteFlag.Invalid.equals(info.getDelFlag())
//    							&& !Constant.deleteFlag.Valid.equals(info.getDelFlag())) {
//    						continue;
//    					}
    					if (StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED.equals(info.getDelFlag())) {
    						throw new BusinessException(null, "原RFID：" + StringUtils.left(info.getRfid(), 8) 
    						+ ",绑定物品("+ info.getGoodsName() + ")已被预订，请处理相关业务流水后，再次上传！:" + StringUtils.left(info.getRfid(), 8));
    					}
    					
    					if (!StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED.equals(info.getDelFlag())
    							&& !StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED.equals(info.getDelFlag())) {
    						continue;
    					}
    					// UPDATE-END  原因：变更过滤条件  update by wangbaozhong  2018/05/23
    					ChangeStoreEntity deleteStoreEntity = new ChangeStoreEntity();
    					// 物品信息
    					deleteStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
    					deleteStoreEntity.setGoodsId(info.getGoodsId());
    					deleteStoreEntity.setNum(-1l);
    					List<ChangeStoreEntity> deleteGoodsList = Lists.newArrayList();
    					deleteGoodsList.add(deleteStoreEntity);
    					// UPDATE-START  原因：按不同状态修改库存  update by wangbaozhong  2018/05/23
    		        	//StoreCommonUtils.changePbocStoreAndSurplusStores(deleteGoodsList, office.getId(), allId, loginUser);
    					if (StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED.equals(info.getDelFlag())) {
    						StoreCommonUtils.changePbocStoreAndSurplusStores(deleteGoodsList, office.getId(), allId, loginUser);
    					} else {
    						StoreCommonUtils.changePbocStore(deleteGoodsList, office.getId(), allId, loginUser);
    					}
    					// UPDATE-END  原因：按不同状态修改库存  update by wangbaozhong  2018/05/23
    				}
    			}
               
                if (stoRfidDenominationList.size() > 0) {
                	for (StoRfidDenomination info :stoRfidDenominationList) {
                		info.setBoxStatus(Constant.BoxStatus.COFFER);
                		info.setBusinessType(businessType);
//                		info.setOfficeId(office.getId());
//                		info.setOfficeName(office.getName());
                		info.setAtOfficeId(office.getId());
                		info.setAtOfficeName(office.getName());
                		info.setGoodsId(goodsId);
                		info.setUseFlag(StoreConstant.RfidUseFlag.use);
                		info.setUpdateBy(loginUser);
                		info.setUpdateName(loginUser.getName());
                		info.setUpdateDate(new Date());
//                		info.setAllId("");
                		stoRfidDenominationService.update(info);
                	}
                } else {
                	stoRfidDenomination = new StoRfidDenomination();
                    stoRfidDenomination.setRfid(rfid);
                    stoRfidDenomination.setBoxStatus(Constant.BoxStatus.COFFER);
                    stoRfidDenomination.setOfficeId(office.getId());
                    stoRfidDenomination.setBusinessType(businessType);
                    stoRfidDenomination.setOfficeName(office.getName());
                    stoRfidDenomination.setGoodsId(goodsId);
                    stoRfidDenomination.setUseFlag(StoreConstant.RfidUseFlag.use);
                    stoRfidDenomination.setCreateBy(loginUser);
                    stoRfidDenomination.setCreateName(loginUser.getName());
                    stoRfidDenomination.setCreateDate(new Date());
                    stoRfidDenomination.setAtOfficeId(office.getId());
                    stoRfidDenomination.setAtOfficeName(office.getName());
            		
                    stoRfidDenomination.setUpdateBy(loginUser);
                    stoRfidDenomination.setUpdateName(loginUser.getName());
                    stoRfidDenomination.setUpdateDate(new Date());
                    
                    stoRfidDenominationService.insert(stoRfidDenomination);
                }
                
                // 插入RFID历史表
                stoRfidDenominationService.insertInToHistory(rfid, loginUser.getOffice());
                
                logger.debug("57 库房初始入库接口 rfid:" + rfid + "，邦定面值成功！");
                
                // 2.录入库区
                stoGoodsLocationInfo = new StoGoodsLocationInfo();
                stoGoodsLocationInfo.setId(IdGen.uuid());
                // rfid
                stoGoodsLocationInfo.setRfid(rfid);
                // 库区ID
                stoGoodsLocationInfo.setStoreAreaId(areaId);
                // 物品ID
                stoGoodsLocationInfo.setGoodsId(goodsId);
                // 物品类型
                stoGoodsLocationInfo.setGoodsType(StoreConstant.GoodType.CURRENCY);
                // 机构ID
                stoGoodsLocationInfo.setOfficeId(office.getId());
                // 物品数量
                stoGoodsLocationInfo.setGoodsNum(1l);
                
                stoGoodsLocationInfo.setInStoreDate(new Date());
                
                // 设定物品价值
        		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsId);
        		
        		stoGoodsLocationInfo.setAmount(goodsValue);
                // 使用状态
                stoGoodsLocationInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
                stoGoodsLocationInfo.setCreateBy(loginUser);
                stoGoodsLocationInfo.setCreateName(loginUser.getName());
                stoGoodsLocationInfo.setCreateDate(new Date());
                stoGoodsLocationInfo.setUpdateBy(loginUser);
                stoGoodsLocationInfo.setUpdateName(loginUser.getName());
                stoGoodsLocationInfo.setUpdateDate(new Date());
                
            	// 执行更新处理
                goodsLocationService.insert(stoGoodsLocationInfo);
                logger.debug("57 库房初始入库接口 rfid:" + rfid + "，入库区成功！");
                // 合计物品个数
                if (changeGoodsMap.containsKey(goodsId)) {
                    changeGoodsMap.get(goodsId).setNum(changeGoodsMap.get(goodsId).getNum() + 1l);
                } else {
                    changeStoreEntity = new ChangeStoreEntity();
                    // 物品信息
                    changeStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
                    changeStoreEntity.setGoodsId(goodsId);
                    changeStoreEntity.setNum(1l);
                    changeGoodsMap.put(goodsId, changeStoreEntity);
                }
            }
        	
        }
        // 原封新券初始入库和倒库功能
        for (Map<String, String> paramMap : paramOriginalMapList) {
        	String flag = paramMap.get(Parameter.FLAG_KEY);
        	String rfid = paramMap.get(Parameter.RFID_KEY);
        	String areaId = paramMap.get(Parameter.AREA_ID_KEY);
        	String goodsId = paramMap.get(Parameter.GOODS_ID_KEY);
        	// 原封券翻译
    		String originalTranslate = paramMap.get(Parameter.ORIGINAL_TRANSLATE_KEY);
    		
        	if (ExternalConstant.OperationFlag.UPDATE_FLAG.equals(flag)) {
        		StoGoodsLocationInfo entity = new StoGoodsLocationInfo();
        		entity.setRfid(rfid);
        		entity.setStoreAreaId(areaId);
        		entity.setUpdateBy(loginUser);
        		entity.setUpdateDate(new Date());
        		goodsLocationService.updateRfidStoreArea(entity);
        	} else if (ExternalConstant.OperationFlag.INSERT_FLAG.equals(flag)) {
        		
        		StoGoodsLocationInfo param = new StoGoodsLocationInfo();
                param.setRfid(rfid);
                param.setOfficeId(office.getId());
                
                // ADD-START  原因：增加查询条件  add by wangbaozhong  2018/05/23
                List<String> statusFlagList = Lists.newArrayList();
                statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
                statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED);
                statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_USED);
                param.setStatusFlagList(statusFlagList);
                // ADD-END  原因：增加查询条件  add by wangbaozhong  2018/05/23
                
                
                List<StoGoodsLocationInfo> locationInfoList = goodsLocationService.findListForInterface(param);
    			if (locationInfoList.size() > 0) {
    				//按Rfid和所属机构ID删除库区物品（初始化库区 或倒库用  将 物品设定为 清理库区 ）
    				goodsLocationService.deleteByRfidAndOfficeId(rfid, office.getId());
    				for (StoGoodsLocationInfo info :locationInfoList) {
    					// UPDATE-START  原因：变更过滤条件  update by wangbaozhong  2018/05/23
//    					if (!Constant.deleteFlag.Invalid.equals(info.getDelFlag())
//    							&& !Constant.deleteFlag.Valid.equals(info.getDelFlag())) {
//    						continue;
//    					}
    					if (StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED.equals(info.getDelFlag())) {
    						throw new BusinessException(null, "原封新券：" + info.getRfid() + ",已被预订，请处理相关业务流水后，再次上传！:" + info.getRfid());
    					}
    					if (!StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED.equals(info.getDelFlag())
    							&& !StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED.equals(info.getDelFlag())) {
    						continue;
    					}
    					// UPDATE-END  原因：变更过滤条件  update by wangbaozhong  2018/05/23
    					ChangeStoreEntity deleteStoreEntity = new ChangeStoreEntity();
    					// 物品信息
    					deleteStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
    					deleteStoreEntity.setGoodsId(info.getGoodsId());
    					deleteStoreEntity.setNum(-1l);
    					List<ChangeStoreEntity> deleteGoodsList = Lists.newArrayList();
    					deleteGoodsList.add(deleteStoreEntity);
    					// UPDATE-START  原因：按状态修改库存  update by wangbaozhong  2018/05/23
    		        	//StoreCommonUtils.changePbocStoreAndSurplusStores(deleteGoodsList, office.getId(), allId, loginUser);
    					if (StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED.equals(info.getDelFlag())) {
    						StoreCommonUtils.changePbocStoreAndSurplusStores(deleteGoodsList, office.getId(), allId, loginUser);
    					} else {
    						StoreCommonUtils.changePbocStore(deleteGoodsList, office.getId(), allId, loginUser);
    					}
    					// UPDATE-END  原因：按状态修改库存  update by wangbaozhong  2018/05/23
    				}
    			}
        		
        		StoOriginalBanknote banknoteTemp = new  StoOriginalBanknote();
        		//箱袋编号
        		banknoteTemp.setId(rfid);
    			// 物品ID
        		banknoteTemp.setGoodsId(goodsId.toString());
    			//原封券翻译
        		banknoteTemp.setOriginalTranslate(originalTranslate);
        		// 设定物品价值
        		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsId.toString());
    			// 物品价值
        		banknoteTemp.setAmount(goodsValue.longValue());
    			// 设定物品名称
        		banknoteTemp.setGoodsName(StoreCommonUtils.getGoodsName(goodsId.toString()));
    			// 设置登记机构
        		banknoteTemp.setRoffice(loginUser.getOffice());
    			// 设置创建人
        		banknoteTemp.setCreateBy(loginUser);
    			// 设置创建人姓名
        		banknoteTemp.setCreateName(loginUser.getName());
        		
        		banknoteTemp.setCreateDate(new Date());
        		
//        		stoOriginalBanknoteService.delete(banknoteTemp);
        		StoOriginalBanknote checkExistInfo = stoOriginalBanknoteService.get(rfid);
        		
        		if (checkExistInfo == null) {
        			// 保存原封新券入库信息
    				stoOriginalBanknoteService.save(banknoteTemp);
        		} else {
        			stoOriginalBanknoteService.update(banknoteTemp);
        		}
				
				
				// 2.录入库区
                stoGoodsLocationInfo = new StoGoodsLocationInfo();
                stoGoodsLocationInfo.setId(IdGen.uuid());
                // rfid
                stoGoodsLocationInfo.setRfid(rfid);
                // 库区ID
                stoGoodsLocationInfo.setStoreAreaId(areaId);
                // 物品ID
                stoGoodsLocationInfo.setGoodsId(goodsId);
                // 物品类型
                stoGoodsLocationInfo.setGoodsType(StoreConstant.GoodType.CURRENCY);
                // 机构ID
                stoGoodsLocationInfo.setOfficeId(office.getId());
                // 物品数量
                stoGoodsLocationInfo.setGoodsNum(1l);
                
                stoGoodsLocationInfo.setInStoreDate(new Date());
        		
        		stoGoodsLocationInfo.setAmount(goodsValue);
                // 使用状态
                stoGoodsLocationInfo.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
                stoGoodsLocationInfo.setCreateBy(loginUser);
                stoGoodsLocationInfo.setCreateName(loginUser.getName());
                stoGoodsLocationInfo.setCreateDate(new Date());
                stoGoodsLocationInfo.setUpdateBy(loginUser);
                stoGoodsLocationInfo.setUpdateName(loginUser.getName());
                stoGoodsLocationInfo.setUpdateDate(new Date());
                
                goodsLocationService.deleteByRfidAndOfficeId(rfid, office.getId());
//                if (iDelCnt > 0) {
//                	ChangeStoreEntity deleteStoreEntity = new ChangeStoreEntity();
//    				// 物品信息
//    				deleteStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
//    				deleteStoreEntity.setGoodsId(goodsId);
//    				deleteStoreEntity.setNum(-1l);
//    				List<ChangeStoreEntity> deleteGoodsList = Lists.newArrayList();
//    				deleteGoodsList.add(deleteStoreEntity);
//    				StoreCommonUtils.changePbocStoreAndSurplusStores(deleteGoodsList, office.getId(), allId, loginUser);
//                }
                
            	// 执行更新处理
                goodsLocationService.insert(stoGoodsLocationInfo);
                logger.debug("57 库房初始入库接口 原封新券 rfid:" + rfid + "，入库区成功！");
                // 合计物品个数
                if (changeGoodsMap.containsKey(goodsId)) {
                    changeGoodsMap.get(goodsId).setNum(changeGoodsMap.get(goodsId).getNum() + 1l);
                } else {
                    changeStoreEntity = new ChangeStoreEntity();
                    // 物品信息
                    changeStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
                    changeStoreEntity.setGoodsId(goodsId);
                    changeStoreEntity.setNum(1l);
                    changeGoodsMap.put(goodsId, changeStoreEntity);
                }
        	}
        }
        
        // 保存物品变更数量信息
        for (Entry<String, ChangeStoreEntity> entry : changeGoodsMap.entrySet()) {
            changeGoodsList.add(entry.getValue());
        }
        if (changeGoodsList.size() > 0) {
        	StoreCommonUtils.changePbocStoreAndSurplusStores(changeGoodsList, office.getId(), allId, loginUser);
        	logger.debug("57 库房初始入库接口 物品列表:" + Collections3.convertToString(changeGoodsList, Constant.Punctuation.COMMA) + "，更新库存成功！");
        }
        // 处理成功
        respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
        logger.debug("57 库房初始入库接口 -----------------结束");
        return gson.toJson(respMap);
	}

}
