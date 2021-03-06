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
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllHandoverInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodSelect;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.businesses.modules.store.v01.service.StoreAreaInterfaceService;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
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
* Title: Service44
* <p>Description: 人行入库确认接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service44")
@Scope("singleton")
public class Service44 extends HardwardBaseService {


	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

    @Autowired
    private PbocAllHandoverInfoService pbocAllHandoverInfoService;
    
    @Autowired
    private StoRfidDenominationService stoRfidDenominationService;
    
    @Autowired
    private StoreAreaInterfaceService storeAreaInterfaceService;
    
    @Autowired
    private StoOriginalBanknoteService stoOriginalBanknoteService;
    
    /** 判断重复提交静态列表：保存流水号 **/
//    private static List<String> interfaceList = Lists.newArrayList();
    
	/**
     * @author chengshu
     * @version 2016年06月01日
     * 
     *          44：人行入库确认接口
     * @param requestMap
     * @param serviceNo
     * @return
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {

		String serviceNo = paramMap.get(Parameter.SERVICE_NO_KEY).toString();
		
		Map<String, Object> respMap = new HashMap<String, Object>();
        // 版本号、服务代码
        respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
        respMap.put(Parameter.SERVICE_NO_KEY, serviceNo);

//        // 验证是否是重复提交
//        String serialorderNo = initInterface(paramMap, respMap, serviceNo);
//        if (Constant.FAILED.equals(serialorderNo)) {
//            return gson.toJson(respMap);
//        }
//
//        try {
            // 取得电文传入的参数
            PbocAllAllocateInfo inputParam = getConfirmInStoreParam(paramMap);
            if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, inputParam.getErrorFlag());
                return gson.toJson(respMap);
            }
            // 设定业务类型
            String businessType = BusinessUtils.getBusinessTypeFromAllId(inputParam.getAllId());
            inputParam.setBusinessType(businessType);
            
            // 判断流水号状态(是否是待入库)
            PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(inputParam);
            if (pbocAllAllocateInfo == null) {
            	respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
                return gson.toJson(respMap);
            }
            if ((AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(businessType)
            		|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(businessType)
            		|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(businessType))
            		&& !AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS
                    .equals(pbocAllAllocateInfo.getStatus())) {
            	respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
                return gson.toJson(respMap);
            } else if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)
            		&& !AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS
                    .equals(pbocAllAllocateInfo.getStatus())) {
            	respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
                return gson.toJson(respMap);
            }

            // 出入库种别：入库
            inputParam.setInoutType(AllocationConstant.InoutType.In);
            
            // 检查标签物品类别是否符合业务
            List<String> errorBoxList = this.checkGoodsByBusinessType(inputParam.getInoutType(), businessType, paramMap);
            if (!errorBoxList.isEmpty()) {
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
                List<Map<String, String>> mapList = Lists.newArrayList();
                for (String boxNo : errorBoxList) {
                	Map<String, String> infoMap = Maps.newHashMap();
                	infoMap.put(Parameter.RFID_KEY, boxNo);
                	infoMap.put(Parameter.STATUS_KEY, "");
                	mapList.add(infoMap);
                }
                respMap.put(Parameter.MESSAGE_INFO,
                        "以下箱袋入库与当前业务不符:" + Collections3.convertToString(errorBoxList, Constant.Punctuation.COMMA));

                respMap.put(Parameter.ERROR_LIST_KEY, mapList);
                logger.warn("以下箱袋入库与当前业务不符:" + Collections3.convertToString(errorBoxList, Constant.Punctuation.COMMA));
                return gson.toJson(respMap);
            }
            
            // 业务类型为 代理上缴，调拨入库，申请上缴时，入库通过扫描门后状态变为 待交接
            if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(businessType)
            		|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(businessType)
            		|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(businessType)) {
            	 // 状态：待交接
                inputParam.setStatus(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS);
            } else if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
            	// 业务类型为 复点出入库 入库通过扫描门后状态变为 待入库交接
            	inputParam.setStatus(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_IN_STORE_HANDOVER_STATUS);
            }
           
            // 取得参与原封新券入库的业务类型列表，并保存原封新券
            List<String> originalBusinessList = Global.getList("store.originalBanknote.allocation.in.store.business");
            if (originalBusinessList.contains(businessType)) {
            	User user = UserUtils.get(inputParam.getUserId());
            	// 原封箱号列表
                List<String> originalBoxList = Lists.newArrayList();
                List<StoOriginalBanknote> stoOriginalBanknoteList = Lists.newArrayList();
                String originalBanknoteClassification = Global.getConfig("store.originalBanknote.goodsClassification");
                List<Map<String, String>> inputBoxList = (List<Map<String, String>>) paramMap.get(Parameter.BOX_LIST_KEY);
                for (Map<String, String> outStoreInfoMap : inputBoxList) {
                	String goodsId = outStoreInfoMap.get(Parameter.GOODS_ID_KEY);
                	String rfid = outStoreInfoMap.get(Parameter.RFID_KEY);
                	StoGoodSelect goodsInfo = StoreCommonUtils.splitGood(goodsId);
                	// 如果物品是原封新券，则从rfid列表中移除原封箱号
                	if (goodsInfo.getClassification().equals(originalBanknoteClassification)) {
                		inputParam.getRfidList().remove(rfid);
                		originalBoxList.add(rfid);
                		// 设定物品价值
                		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsId);
                		StoOriginalBanknote stoOriginalBanknote = new StoOriginalBanknote();
                		// 入库机构
                		stoOriginalBanknote.setRoffice(inputParam.getAoffice());
                		// 原封箱号
                    	stoOriginalBanknote.setId(rfid);
                    	// 入库流水单号
                    	stoOriginalBanknote.setInId(inputParam.getAllId());
                    	// 原封券翻译
                    	stoOriginalBanknote.setOriginalTranslate(outStoreInfoMap.get(Parameter.ORIGINAL_TRANSLATE_KEY));
                    	// 设定物品名称
            			stoOriginalBanknote.setGoodsName(StoreCommonUtils.getGoodsName(goodsId));
            			// 设置创建人
            			stoOriginalBanknote.setCreateBy(user);
                    	// 设定金额
                    	stoOriginalBanknote.setAmount(goodsValue.longValue());
                    	stoOriginalBanknoteList.add(stoOriginalBanknote);
                	}
                }
                
                if (originalBoxList.size() > 0) {
                	// 验证原封箱是否重复登记
            		List<String> erroList = Lists.newArrayList();
            		
            		// 校验原封券箱是否正确
            		for (String boxNo : originalBoxList) {
            			StoOriginalBanknote stoOriginalBanknote = stoOriginalBanknoteService.getOriginalBanknoteById(boxNo, inputParam.getAoffice().getId());
            			if (stoOriginalBanknote != null) {
            				erroList.add(boxNo);
            			}
            		}
            		
            		if (erroList.size() > 0) {
            			//　登记失败，存在不正确箱子
            			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
            			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
            			respMap.put(Parameter.ERROR_MSG_KEY, "登记失败,以下箱子已经登记:"+Collections3.convertToString(erroList, ","));
            			return gson.toJson(respMap);
            		}
                }
                
                for (StoOriginalBanknote stoOriginalBanknote : stoOriginalBanknoteList) {
                	stoOriginalBanknote.setCreateDate(new Date());
                    stoOriginalBanknoteService.save(stoOriginalBanknote);
                }
            }
            
            // 接收时间
            inputParam.setAcceptDate(new Date());
            if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
            	// 复点入库时，登记清分中心入库物品信息
            	List<PbocAllAllocateItem> itemList = Lists.newArrayList();
            	//入库总金额
            	BigDecimal confirmAmount = new BigDecimal(0.0d);
            	for (PbocAllAllocateDetail detail : inputParam.getPbocAllAllocateDetailList()) {
            		PbocAllAllocateItem item = new PbocAllAllocateItem();
            		//设定流水单号
            		item.setAllId(inputParam.getAllId());
            		// 设定登记类型
            		item.setRegistType(AllocationConstant.RegistType.RegistPoint);
            		StoGoodsLocationInfo goodsInfo = detail.getGoodsLocationInfo();
            		// 设定物品数量
            		item.setMoneyNumber(goodsInfo.getGoodsNum());
            		// 设定物品ID
            		item.setGoodsId(goodsInfo.getGoodsId());
            		// 设定物品价值
            		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsInfo.getGoodsId());
            		item.setMoneyAmount(goodsValue);
            		confirmAmount = confirmAmount.add(goodsValue);
            		itemList.add(item);
            	}
            	pbocAllAllocateInfoService.saveReCountingInstoreItems(itemList);
            	inputParam.setConfirmAmount(confirmAmount.doubleValue());
            }
            // 更新调拨状态（待交接）
            pbocAllAllocateInfoService.updateAllocateConfirmStatus(inputParam);
            // 插入扫描到的箱袋信息
            pbocAllAllocateInfoService.insertAllocateDetailInfo(inputParam);
            // 按物品种类摆放库区
//            storeAreaInterfaceService.saveGoodsIntoArea(inputParam, respMap);
            // 按物品ID摆放库区
            storeAreaInterfaceService.saveGoodsIntoAreaByGoodsId(inputParam, respMap);
            
            if (StringUtils.isNotBlank(StringUtils.toString(respMap.get(Parameter.RESULT_FLAG_KEY)))) {
                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
                return gson.toJson(respMap);
            }
            // 复点入库时不登记交接信息
            if (!AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
            	// 插入交接信息
                pbocAllHandoverInfoService.insertHandover(inputParam);
            }
            
            // 更新RFID状态
            StoreCommonUtils.updateRfidStatus(inputParam.getRfidList(), Constant.BoxStatus.COFFER,
                    inputParam.getUserId(), inputParam.getUserName(), pbocAllAllocateInfo.getBusinessType());
            // 将RFID当前邦定记录表导入历史表
            User curUser = UserUtils.get(inputParam.getUserId());
            stoRfidDenominationService.insertInToHistory(inputParam.getRfidList(), curUser.getOffice());
            // 更新物品库存
            User updateUser = new User();
            updateUser.setId(inputParam.getUserId());
            updateUser.setName(inputParam.getUpdateName());
            StoreCommonUtils.changePbocStoreAndSurplusStores(inputParam.getChangeGoodsList(),
                    inputParam.getAoffice().getId(), inputParam.getAllId(), updateUser);

            // 处理成功
            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

            return gson.toJson(respMap);
//        } finally {
//            cleanSerialorderNo(serialorderNo);
//        }
	}
	
	/**
     * @author chengshu
     * @version 2016年06月16日
     * 
     *          重复调用接口屏蔽
     * @param requestMap
     * @param serviceNo
     * @return
     */
//    public String initInterface(Map<String, Object> headInfo, Map<String, Object> respMap, String serviceNo) {
//
//        // 流水号
//        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)))) {
//            logger.warn("输入参数错误：" + Parameter.SERIALORDER_NO_KEY + " 不存在或是空。");
//            respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
//            respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
//            return Constant.FAILED;
//
//        } else {
//            // 流水号+接口编号
//            String key = StringUtils.join(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)), serviceNo);
//            if (interfaceList.contains(key)) {
//                // 重复提交错误发生
//                logger.warn(StringUtils.join("重复提交错误:接口", serviceNo, "; 流水号:" + headInfo.get(Parameter.SERIALORDER_NO_KEY)));
//                respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
//                respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E44);
//                return Constant.FAILED;
//
//            } else {
//                // 保存当前流水号，验证是否重复提交
//                interfaceList.add(key);
//            }
//        }
//
//        // 返回流水号
//        return StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY));
//    }

    /**
     * 执行完成的流水号清除
     * @author:     ChengShu
     * @param inputParam
     * @param param
     * @return 
     * PbocAllAllocateInfo    返回类型
     */
//    private void cleanSerialorderNo(String serialorderNo){
//        interfaceList.remove(serialorderNo);
//    }
    
    /**
     * 
     * @author chengshu
     * @version 2016年06月01日
     * 
     *          取得人行入库确认接口的输入参数
     * @param requestMap 输入参数
     * @return
     */
    @SuppressWarnings("unchecked")
    private PbocAllAllocateInfo getConfirmInStoreParam(Map<String, Object> headInfo) {

        PbocAllAllocateInfo inputParam = new PbocAllAllocateInfo();

        // 流水号
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)))) {
            return checkParamFaile(inputParam, Parameter.SERIALORDER_NO_KEY);
        } else {
            inputParam.setAllId(StringUtils.toString(headInfo.get(Parameter.SERIALORDER_NO_KEY)));
        }

        // 用户ID
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)))) {
            return checkParamFaile(inputParam, Parameter.USER_ID_KEY);
        } else {
            inputParam.setUserId(StringUtils.toString(headInfo.get(Parameter.USER_ID_KEY)));
        }

        // 用户名称
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)))) {
            return checkParamFaile(inputParam, Parameter.USER_NAME_KEY);
        } else {
            inputParam.setUserName(StringUtils.toString(headInfo.get(Parameter.USER_NAME_KEY)));
        }

        // 金库机构ID
        if (StringUtils.isBlank(StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)))) {
            return checkParamFaile(inputParam, Parameter.OFFICE_ID_KEY);
        } else {
            Office office = new Office();
            office.setId(StringUtils.toString(headInfo.get(Parameter.OFFICE_ID_KEY)));
            inputParam.setAoffice(office);
        }

        // 箱袋信息
        List<Map<String, String>> inputBoxList = Lists.newArrayList();
        if (null == (List<Map<String, String>>) headInfo.get(Parameter.BOX_LIST_KEY)) {
            return checkParamFaile(inputParam, Parameter.BOX_LIST_KEY);
        } else {
            inputBoxList = (List<Map<String, String>>) headInfo.get(Parameter.BOX_LIST_KEY);
        }

        // 箱袋列表初始化
        List<PbocAllAllocateDetail> boxList = Lists.newArrayList();
        List<String> rfidList = Lists.newArrayList();
        List<ChangeStoreEntity> changeGoodsList = Lists.newArrayList();
        Map<String, ChangeStoreEntity> changeGoodsMap = Maps.newHashMap();

        // 设置箱袋列表
        PbocAllAllocateDetail boxDetail = new PbocAllAllocateDetail();
        StoGoodsLocationInfo goodsInfo = new StoGoodsLocationInfo();
        ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();

        String goodsId = "";
        Long goodsNum = (long) 0;
        BigDecimal instoreAmount = new BigDecimal(0.0);
        for (Map<String, String> boxMap : inputBoxList) {
            if (null == boxMap) {
                break;
            }
            boxDetail = new PbocAllAllocateDetail();
            goodsInfo = new StoGoodsLocationInfo();

            // rfid
            if (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.STORE_ESCORT_RFID)))) {
                return checkParamFaile(inputParam,
                        StringUtils.join(Parameter.BOX_LIST_KEY, ".", Parameter.STORE_ESCORT_RFID));
            } else {
                boxDetail.setRfid(boxMap.get(Parameter.STORE_ESCORT_RFID));
            }

            // goodsId
            if (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.GOODS_ID_KEY)))) {
                return checkParamFaile(inputParam,
                        StringUtils.join(Parameter.BOX_LIST_KEY, ".", Parameter.GOODS_ID_KEY));
            } else {
                goodsId = StringUtils.toString(boxMap.get(Parameter.GOODS_ID_KEY));
                goodsInfo.setGoodsId(goodsId);
                // changeStoreEntity.setGoodsId(StringUtils.toString(boxMap.get(Parameter.GOODS_ID_KEY)));
            }

            // goodsNum
            if (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.GOODS_NUM_KEY)))) {
                return checkParamFaile(inputParam,
                        StringUtils.join(Parameter.BOX_LIST_KEY, ".", Parameter.GOODS_NUM_KEY));
            } else {
                goodsNum = Long.valueOf(boxMap.get(Parameter.GOODS_NUM_KEY));
                goodsInfo.setGoodsNum(goodsNum);
                // changeStoreEntity.setNum(Long.valueOf(boxMap.get(Parameter.GOODS_NUM_KEY)));
            }
            // 计算入库物品金额
            BigDecimal amount = StoreCommonUtils.getGoodsValue(goodsId);
            amount = amount.multiply(new BigDecimal(goodsNum));
            instoreAmount = instoreAmount.add(amount);
            // RFID号列表
            rfidList.add(StringUtils.toString(boxMap.get(Parameter.STORE_ESCORT_RFID)));
            // 物品箱袋信息
            boxDetail.setGoodsLocationInfo(goodsInfo);
            boxList.add(boxDetail);

            // 合计物品个数
            if (changeGoodsMap.containsKey(goodsId)) {
                changeGoodsMap.get(goodsId).setNum(changeGoodsMap.get(goodsId).getNum() + goodsNum);
            } else {
                changeStoreEntity = new ChangeStoreEntity();
                // 物品信息
                changeStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
                changeStoreEntity.setGoodsId(goodsId);
                changeStoreEntity.setNum(goodsNum);
                changeGoodsMap.put(goodsId, changeStoreEntity);
            }
        }

        // 保存物品变更数量信息
        for (Entry<String, ChangeStoreEntity> entry : changeGoodsMap.entrySet()) {
            changeGoodsList.add(entry.getValue());
        }
        // 保存入库物品金额
        inputParam.setInstoreAmount(instoreAmount.doubleValue());
        inputParam.setRfidList(rfidList);
        inputParam.setPbocAllAllocateDetailList(boxList);
        inputParam.setChangeGoodsList(changeGoodsList);

        return inputParam;
    }
    
    /**
     * 电文传入参数验证失败返回
     * @author:     ChengShu
     * @param inputParam
     * @param param
     * @return 
     * PbocAllAllocateInfo    返回类型
     */
    private PbocAllAllocateInfo checkParamFaile(PbocAllAllocateInfo inputParam, String param) {
        inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
        logger.warn("输入参数错误：" + param + " 不存在或是空。");
        return inputParam;
    }
    
    /**
	 * 按业务类型检查出入库物品类别是否正确
	 * @author WangBaozhong
	 * @version 2016年7月13日
	 * 
	 * @return 返回错误消息
	 */
	@SuppressWarnings("unchecked")
	private List<String> checkGoodsByBusinessType(String inOutType, String businessType, Map<String, Object> headInfo) {
		List<String> errorList = Lists.newArrayList();
		if ("".equals(Global.getConfig("allocation.businessType.goodsClassification.check"))) {
			return errorList;
		}
		
		List<Map<String, String>> inputBoxList = (List<Map<String, String>>) headInfo.get(Parameter.BOX_LIST_KEY);
		
		List<String> relationList = Lists.newArrayList();
		if (AllocationConstant.InOutCoffer.IN.equals(inOutType)) {
			//入库业务与物品类别关系
			relationList = Global.getList("allocation.businessType.goodsClassification.in.store");
		} else if (AllocationConstant.InOutCoffer.OUT.equals(inOutType)) {
			//出库业务与物品类别关系
			relationList = Global.getList("allocation.businessType.goodsClassification.out.store");
		}
		//原封新券类别
		String originalBanknotType = Global.getConfig("store.originalBanknote.goodsClassification");
		
		String strbusinessClassificationRelation = "";
		for (String relation : relationList) {
			String relationArray[] = relation.split(Constant.Punctuation.HALF_COLON);
			if (businessType.equals(relationArray[0])) {
				strbusinessClassificationRelation = relationArray[1];
				break;
			}
		}
		
		for (Map<String, String> boxMap : inputBoxList) {
			String goodsId = boxMap.get(Parameter.GOODS_ID_KEY);
			String rfid = boxMap.get(Parameter.RFID_KEY);
			StoGoodSelect goodsInfo = StoreCommonUtils.splitGood(goodsId);
			if (strbusinessClassificationRelation.indexOf(goodsInfo.getClassification()) == -1) {
				// 出入库出错物品为原封券时，显示原封券箱号及翻译
				if (originalBanknotType.equals(goodsInfo.getClassification())) {
					errorList.add(rfid + Constant.Punctuation.HALF_COLON + boxMap.get(Parameter.ORIGINAL_TRANSLATE_KEY));
				} else {
					errorList.add(StringUtils.left(rfid, 8));
				}
			}
		}
		
		return errorList;
	}
}
