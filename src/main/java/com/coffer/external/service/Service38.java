package com.coffer.external.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateItem;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service38
* <p>Description: 现金预约、上缴查询 （修改或查看）</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service38")
@Scope("singleton")
public class Service38 extends HardwardBaseService {

	@Autowired
	private AllocationService allocationService;
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月24日
	 * 
	 *  38 现金预约、上缴查询 （修改或查看）
	 * @param paramMap 参数
	 * @return 结果信息
	 */
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		try {
			// 取得查询条件
			AllAllocateInfo allAllocateInfoparam = this.getAllAllocateInfoByIdParamFromMap(paramMap);
			if (allAllocateInfoparam == null) {
				// 参数异常
				map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			} else {
				
				// 根据流水单号取得预约或上缴物品信息列表
				if (this.getAllAllocateInfoByAllIdMap(allAllocateInfoparam, map) == null) {
					// 参数异常
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
					//[修改失败]：流水单号[{0}]对应调拨信息不存在！
					map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E31);
				} else {
					map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
				}
			}
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
		}

		return gson.toJson(map);
	}
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  取得 38 现金预约、上缴查询 （修改或查看）接口参数
	 * @param headInfo CS上传信息
	 * @return 列表查询条件
	 */
	private AllAllocateInfo getAllAllocateInfoByIdParamFromMap(Map<String, Object> headInfo) {
		AllAllocateInfo rtnAllAllocateInfo = new AllAllocateInfo();
		// 取得网点机构Id
		if (headInfo.get(Parameter.OFFICE_ID_KEY) == null || StringUtils.isBlank(headInfo.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("现金预约、上缴查询 （修改或查看）接口-------- 网点机构Id未指定:" + headInfo.get(Parameter.OFFICE_ID_KEY));
			return null;
		}
		// 取得流水单号
		if (headInfo.get(Parameter.SERIALORDER_NO_KEY) == null
				|| StringUtils.isBlank(headInfo.get(Parameter.SERIALORDER_NO_KEY).toString())) {
			logger.debug("现金预约、上缴查询 （修改或查看）接口-------- 流水单号未指定:" + headInfo.get(Parameter.OFFICE_ID_KEY));
			return null;
		} 
		Office loginUserOffice = StoreCommonUtils.getOfficeById(headInfo.get(Parameter.OFFICE_ID_KEY).toString());
		// 网点机构ID
		rtnAllAllocateInfo.setrOffice(loginUserOffice);
		// 流水单号
		rtnAllAllocateInfo.setAllId(headInfo.get(Parameter.SERIALORDER_NO_KEY).toString());
		return rtnAllAllocateInfo;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月23日
	 * 
	 *  根据流水单号取得预约或上缴物品信息列表
	 * @param allAllocateInfoparam 查询参数
	 * @return 预约信息列表
	 */
	private String getAllAllocateInfoByAllIdMap(AllAllocateInfo allAllocateInfoparam, Map<String, Object> rtnMap) {
		// 根据查询条件，取得调拨全部信息
		AllAllocateInfo allocationInfo = allocationService.getAllocateBetween(allAllocateInfoparam.getAllId());
		
		if (allocationInfo == null) {
			return null;
		}
		
		//状态
		rtnMap.put(Parameter.STATUS_KEY, allocationInfo.getStatus());
		List<Map<String, Object>> itemList = null;
		
		String strMapKey = null;
		for (AllAllocateItem item : allocationInfo.getAllAllocateItemList()) {
			strMapKey = allocationService.getAllAllocateItemMapKey(item);
			allocationInfo.getAllAllocateItemMap().put(strMapKey, item);
		}
		
		//现金操作：现金预约 
		if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(allocationInfo.getBusinessType())
				&& AllocationConstant.Status.CashOrderQuotaYes.equals(allocationInfo.getStatus())) {
			// 合并预约、配款信息
			//allocationInfo = allocationService.unionOrderInfo(allocationInfo);
			itemList = this.getItemListFromMap(allocationInfo);
		} else if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allocationInfo.getBusinessType())
				&& AllocationConstant.Status.BankOutletsHandInConfirmYes.equals(allocationInfo.getStatus())) {
			// 合并上缴、接收信息
			allocationInfo = allocationService.unionHandinInfo(allocationInfo);
			itemList = this.getItemListFromMap(allocationInfo);
		} else {
			itemList = Lists.newArrayList();
			
			Map<String, Object> itemMap = null;
			for (AllAllocateItem item : allocationInfo.getAllAllocateItemList()) {
				itemMap = Maps.newHashMap();
				// 币种
				itemMap.put(Parameter.CURRENCY_KEY, item.getCurrency());
				// 类别
				itemMap.put(Parameter.CLASSIFICATION_KEY, item.getClassification());
				// 套别
				itemMap.put(Parameter.SETS_KEY, item.getSets());
				// 材质
				itemMap.put(Parameter.CASH_KEY, item.getCash());
				// 面值
				itemMap.put(Parameter.DENOMINATION_KEY, item.getDenomination());
				// 单位
				itemMap.put(Parameter.UNIT_KEY, item.getUnit());
				// 预约或上缴数量
				itemMap.put(Parameter.POINT_NUMBER_KEY, item.getMoneyNumber());
				// 预约或上缴金额
				itemMap.put(Parameter.POINT_AMOUNT_KEY, item.getMoneyAmount());
				
				itemList.add(itemMap);
			}
		}
		rtnMap.put(Parameter.LIST_KEY, itemList);
		
		return ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS;
	}
	
	/**
	 * 
	 * @author wangbaozhong
	 * @version 2015年12月28日
	 * 
	 *  取得已接收或已配款物品信息转换为Map列表
	 * @param allocationInfo 已接收或已配款物品信息
	 * @return 已接收或已配款物品信息Map列表
	 */
	private List<Map<String, Object>> getItemListFromMap(AllAllocateInfo allocationInfo) {
		List<Map<String, Object>> itemList = Lists.newArrayList();
		Iterator<String> keyIterator = allocationInfo.getAllAllocateItemMap().keySet().iterator();
		Map<String, Object> itemMap = null;
		while (keyIterator.hasNext()) {
			String strMapKey = keyIterator.next();
			AllAllocateItem tempItem = allocationInfo.getAllAllocateItemMap().get(strMapKey);
			itemMap = Maps.newHashMap();
			// 币种
			itemMap.put(Parameter.CURRENCY_KEY, tempItem.getCurrency());
			// 类别
			itemMap.put(Parameter.CLASSIFICATION_KEY, tempItem.getClassification());
			// 套别
			itemMap.put(Parameter.SETS_KEY, tempItem.getSets());
			// 材质
			itemMap.put(Parameter.CASH_KEY, tempItem.getCash());
			// 面值
			itemMap.put(Parameter.DENOMINATION_KEY, tempItem.getDenomination());
			// 单位
			itemMap.put(Parameter.UNIT_KEY, tempItem.getUnit());
			// 预约或上缴数量
			itemMap.put(Parameter.POINT_NUMBER_KEY, tempItem.getMoneyNumber());
			// 预约或上缴金额
			itemMap.put(Parameter.POINT_AMOUNT_KEY, tempItem.getMoneyAmount());
			// 配款或接收数量
			itemMap.put(Parameter.STORE_NUMBER_KEY, tempItem.getConfirmNumber());
			// 配款或接收金额
			itemMap.put(Parameter.STORE_AMOUNT_KEY, tempItem.getConfirmAmount());
			
			itemList.add(itemMap);
		}
		return itemList;
	}
}
