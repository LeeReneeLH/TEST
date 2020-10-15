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

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllHandoverInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.ChangeStoreEntity;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.service.StoreAreaInterfaceService;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.businesses.modules.store.v02.service.StoOriginalBanknoteService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.ExceptionUtil;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service56
* <p>Description: 原封新券入库接口(new)</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service56")
@Scope("singleton")
public class Service56 extends HardwardBaseService {

	@Autowired
	private StoOriginalBanknoteService stoOriginalBanknoteService;
	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;
	@Autowired
	PbocAllHandoverInfoService pbocAllHandoverInfoService;
	@Autowired
	StoreAreaInterfaceService storeAreaInterfaceService;
	@Autowired
	private OfficeService officeService;
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2016年6月1日
	 * 
	 *      56    原封新券入库接口(new)
	 * @param paramMap 参数列表
	 * @return
	 */
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		PbocAllAllocateInfo inputParam = this.getOriginalBanknoteInStoreParam(paramMap);
		if (inputParam == null) {
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(map);
		}
		
		// 验证原封箱是否重复登记
		List<String> erroList = Lists.newArrayList();
		
		try {
			
			// 校验原封券箱是否正确
			for (StoOriginalBanknote banknoteTemp : inputParam.getBanknoteItemList()) {
				StoOriginalBanknote stoOriginalBanknote = stoOriginalBanknoteService.getOriginalBanknoteById(banknoteTemp.getId(), inputParam.getRoffice().getId());
				if (stoOriginalBanknote != null) {
					erroList.add(banknoteTemp.getId());
				}
			}
			
			if (Collections3.isEmpty(erroList)) {
				// 出入库种别：入库
	            inputParam.setInoutType(AllocationConstant.InOutCoffer.IN);
				//1.登记人行调拨主表
				//2.登记人行调拨物品明细表
				pbocAllAllocateInfoService.savePbocOriginalBanknoteInStore(inputParam);
				//3.登记人行调拨箱袋明细表
				// 插入扫描到的箱袋信息
	            pbocAllAllocateInfoService.insertAllocateDetailInfo(inputParam);
				
				//4. 登记原封 人行原封券信息 并记录入库流水单号
				for (StoOriginalBanknote banknoteTemp : inputParam.getBanknoteItemList()) {
					banknoteTemp.setCreateDate(new Date());
					banknoteTemp.setInId(inputParam.getAllId());
					// 保存原封新券入库信息
					stoOriginalBanknoteService.save(banknoteTemp);
				}
				//5.登记库房物品位置表
				// 按物品种类摆放库区
//	            storeAreaInterfaceService.saveGoodsIntoArea(inputParam, map);
	            // 按物品ID摆放库区
	            storeAreaInterfaceService.saveGoodsIntoAreaByGoodsId(inputParam, map);
	            if (StringUtils.isNotBlank(StringUtils.toString(map.get(Parameter.RESULT_FLAG_KEY)))) {
	            	map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
	                return gson.toJson(map);
	            }
	            // 6.插入交接信息
	            pbocAllHandoverInfoService.insertHandover(inputParam);
				// 7.登记人行库存信息
	            // 更新物品库存
	            StoreCommonUtils.changePbocStoreAndSurplusStores(inputParam.getChangeGoodsList(),
	                    inputParam.getAoffice().getId(), inputParam.getAllId(), inputParam.getLoginUser());
	            // 设定流水单号
	            map.put(Parameter.SERIALORDER_NO_KEY, inputParam.getAllId());
				// 成功结果
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				// 　登记失败，存在不正确箱子
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				map.put(Parameter.ERROR_MSG_KEY, "登记失败,以下箱子已经登记:"+Collections3.convertToString(erroList, ","));
			}
		} catch (BusinessException be) {
			if (!"message.E1055".equals(be.getMessageCode())) {
				throw be;
			}
			// 结果标识
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 错误代码
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E45);
			// 错误信息
			map.put(Parameter.ERROR_MSG_KEY, be.getMessageContent());
			// LOG
			logger.error(ExceptionUtil.getExceptionTrace(be));
		}
		
		// JSON电文
		return gson.toJson(map);
	}
	
	/**
	 * 检查 56    原封新券入库接口(new)接口参数是否正确
	 * @author WangBaozhong
	 * @version 2016年7月11日
	 * 
	 *  
	 * @return true :正确  false：参数有误
	 */
	@SuppressWarnings("unchecked")
	private PbocAllAllocateInfo getOriginalBanknoteInStoreParam(Map<String, Object> headInfo) {
		logger.debug("检查 56    原封新券入库接口(new)接口参数是否正确 --------- 开始");
		
		// 验证机构ID
		Object officeId = headInfo.get(Parameter.OFFICE_ID_KEY);
		if (officeId == null || StringUtils.isBlank(officeId.toString())) {
			logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----机构ID为空！");
			return null;
		}
		Office office = officeService.get(officeId.toString());
		if (office == null) {
			logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----机构ID:" + officeId.toString() + "，对应机构不存在！");
			return null;
		}
		// 验证用户ID
		Object userId = headInfo.get(Parameter.USER_ID_KEY);
		if (userId == null || StringUtils.isBlank(userId.toString())) {
			logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----用户ID为空！");
			return null;
		}
		
		User user = UserUtils.get(userId.toString());
		// 验证用户
		if (user == null) {
			logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----用户ID:" + userId.toString() + "，对应用户不存在");
			return null;
		}
		
		// 验证原封券明细
		if (headInfo.get(Parameter.LIST_KEY) == null ) {
			logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----原封券明细列表未传！");
			return null;
		}
		List<Map<String, Object>> list = (List<Map<String, Object>>) headInfo.get(Parameter.LIST_KEY);
		if (Collections3.isEmpty(list) == true) {
			logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----原封券明细列表为空！");
			return null;
		}
		List<StoOriginalBanknote> banknoteItemList = Lists.newArrayList();
		List<PbocAllAllocateItem> allocatedItemList = Lists.newArrayList();
		List<PbocAllAllocateDetail> allocateDetailList = Lists.newArrayList();
		Map<String, ChangeStoreEntity> changeGoodsMap = Maps.newHashMap();
		// 提交总金额
		BigDecimal totalMoney = new BigDecimal(0d);
		for (Map<String, Object> itemMap : list) {
			// 验证物品ID
			Object goodsId = itemMap.get(Parameter.GOODS_ID_KEY);
			if (goodsId == null || StringUtils.isBlank(goodsId.toString())) {
				logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----物品ID为空！");
				return null;
			}
			// 设定物品价值
    		BigDecimal goodsValue = StoreCommonUtils.getGoodsValue(goodsId.toString());
    		if (goodsValue == null) {
    			logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----物品ID:" + goodsId + ",对应物品价值不存在！");
				return null;
    		}
			// 验证箱袋编号
			Object boxNo = itemMap.get(Parameter.BOX_NO_KEY);
			if (boxNo == null || StringUtils.isBlank(boxNo.toString())) {
				logger.warn("检查 56    原封新券入库接口(new)接口参数是否正确 -----箱袋编号为空！");
				return null;
			}
			
    		
			PbocAllAllocateItem allocatedItem = new PbocAllAllocateItem();
			StoOriginalBanknote stoOriginalBanknote = new StoOriginalBanknote();
			PbocAllAllocateDetail allocateDetail = new PbocAllAllocateDetail();
			
			
			//箱袋编号
			stoOriginalBanknote.setId(boxNo.toString());
			// 物品ID
			stoOriginalBanknote.setGoodsId(goodsId.toString());
			//原封券翻译
			stoOriginalBanknote.setOriginalTranslate(itemMap.get(Parameter.ORIGINAL_TRANSLATE_KEY).toString());
			// 物品价值
			stoOriginalBanknote.setAmount(goodsValue.longValue());
			// 设定物品名称
			stoOriginalBanknote.setGoodsName(StoreCommonUtils.getGoodsName(goodsId.toString()));
			// 设置登记机构
			stoOriginalBanknote.setRoffice(office);
			// 设置创建人
			stoOriginalBanknote.setCreateBy(user);
			// 设置创建人姓名
			stoOriginalBanknote.setCreateName(user.getName());
			
			banknoteItemList.add(stoOriginalBanknote);
			/*
			 * 调拨物品明细
			 */
			// 物品ID
			allocatedItem.setGoodsId(goodsId.toString());
			// 登记类型 库房登记
			allocatedItem.setRegistType(AllocationConstant.RegistType.RegistStore);
			// 设定物品数量
			allocatedItem.setMoneyNumber(1L);
			// 物品价值
			allocatedItem.setMoneyAmount(goodsValue);
			allocatedItemList.add(allocatedItem);
			/*
			 * 箱袋调拨明细
			 */
			// rfid编号
			allocateDetail.setRfid(boxNo.toString());
			// 扫描状态
			allocateDetail.setScanFlag(AllocationConstant.ScanFlag.Scan);
			// 箱袋所处位置
			allocateDetail.setPlace(AllocationConstant.Place.StoreRoom);
			/*
			 * 物品库区位置信息
			 */
			StoGoodsLocationInfo goodsLocationInfo = new StoGoodsLocationInfo();
			//物品ID
			goodsLocationInfo.setGoodsId(goodsId.toString());
			// 设定物品数量
			goodsLocationInfo.setGoodsNum(allocatedItem.getMoneyNumber());
			allocateDetail.setGoodsLocationInfo(goodsLocationInfo);
			allocateDetailList.add(allocateDetail);
			
			 // 合计物品个数
            if (changeGoodsMap.containsKey(goodsId)) {
                changeGoodsMap.get(goodsId).setNum(changeGoodsMap.get(goodsId).getNum() + allocatedItem.getMoneyNumber());
            } else {
            	ChangeStoreEntity changeStoreEntity = new ChangeStoreEntity();
                // 物品信息
                changeStoreEntity.setGoodType(StoreConstant.GoodType.CURRENCY);
                changeStoreEntity.setGoodsId(allocatedItem.getGoodsId());
                changeStoreEntity.setNum(allocatedItem.getMoneyNumber());
                changeGoodsMap.put(allocatedItem.getGoodsId(), changeStoreEntity);
            }
            totalMoney = totalMoney.add(allocatedItem.getMoneyAmount());
		}
		List<ChangeStoreEntity> changeGoodsList = Lists.newArrayList();
		 // 保存物品变更数量信息
        for (Entry<String, ChangeStoreEntity> entry : changeGoodsMap.entrySet()) {
            changeGoodsList.add(entry.getValue());
        }
		
		PbocAllAllocateInfo allocatedInfo = new PbocAllAllocateInfo();
		
		// 设置接收机构
		allocatedInfo.setAoffice(office);
		// 设置登记机构
		allocatedInfo.setRoffice(office);
		// 设置用户ID
		allocatedInfo.setUserId(userId.toString());
		// 设置用户名称
		allocatedInfo.setUserName(user.getName());
		// 设置登陆用户
		allocatedInfo.setLoginUser(user);
		//设置原封新券物品列表
		allocatedInfo.setBanknoteItemList(banknoteItemList);
		// 设置调拨物品明细列表
		allocatedInfo.setPbocAllAllocateItemList(allocatedItemList);
		// 设置箱袋调拨明细列表
		allocatedInfo.setPbocAllAllocateDetailList(allocateDetailList);
		// 设置库存变更列表
		allocatedInfo.setChangeGoodsList(changeGoodsList);
		// 设置登记总金额
		allocatedInfo.setRegisterAmount(totalMoney.doubleValue());
		// 设置接收总金额
		allocatedInfo.setConfirmAmount(totalMoney.doubleValue());
		
		logger.debug("检查 56    原封新券入库接口(new)接口参数是否正确 --------- 结束");
		return allocatedInfo;
	}

}
