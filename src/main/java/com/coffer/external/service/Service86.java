package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * Title: Service86
 * <p>
 * Description:自动化查询接口
 * </p>
 * 
 * @author yanbingxu
 * @date 2018年4月28日
 */
@Component("Service86")
@Scope("singleton")
public class Service86 extends HardwardBaseService {

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;
	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		String strUpdateDate = "";
		Date date1 = null, date2 = null;
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		// 参数验证
		if (null == paramMap.get(Parameter.INOUT_TYPE_KEY)) {
			logger.error("出入库类型为空");
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
		}

		if (null != paramMap.get(Parameter.SEARCH_DATE_KEY)) {
			pbocAllAllocateInfo.setStrUpdateDate(paramMap.get(Parameter.SEARCH_DATE_KEY).toString());
		}

		List<String> businessTypeList = Lists.newArrayList();
		List<PbocAllAllocateInfo> pbocAllocationList = Lists.newArrayList();

		if (AllocationConstant.inoutType.STOCK_IN.equals(paramMap.get(Parameter.INOUT_TYPE_KEY))) {
			// 申请上缴，代理上缴，调拨入库时设置查询条件
			businessTypeList.add(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
			businessTypeList.add(AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE);
			businessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN);
			pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
			// 执行查询
			pbocAllocationList = pbocAllAllocateInfoService.findListInterface(pbocAllAllocateInfo);
			if (!Collections3.isEmpty(pbocAllocationList)) {
				date1 = pbocAllocationList.get(0).getUpdateDate();
			}
			// 复点入库时设置查询条件
			pbocAllAllocateInfo = new PbocAllAllocateInfo();
			if (null != paramMap.get(Parameter.SEARCH_DATE_KEY)) {
				pbocAllAllocateInfo.setStrUpdateDate(paramMap.get(Parameter.SEARCH_DATE_KEY).toString());
			}
			pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_RE_COUNTING);
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS);
			List<PbocAllAllocateInfo> tempPbocAllocationList = pbocAllAllocateInfoService
					.findListInterface(pbocAllAllocateInfo);
			if (!Collections3.isEmpty(tempPbocAllocationList)) {
				date2 = tempPbocAllocationList.get(0).getUpdateDate();
			}
			// 比较后设置较大值为增量查询日期
			switch (DateUtils.compareDate(date1, date2)) {
			case -1:
				strUpdateDate = DateUtils.formatDate(date2, "yyyyMMddHHmmssSSSSSS");
				break;
			default:
				strUpdateDate = DateUtils.formatDate(date1, "yyyyMMddHHmmssSSSSSS");
				break;
			}

			pbocAllocationList.addAll(tempPbocAllocationList);
		}

		if (AllocationConstant.inoutType.STOCK_OUT.equals(paramMap.get(Parameter.INOUT_TYPE_KEY))) {
			// 申请下拨，销毁出库，调拨出库时设置查询条件
			businessTypeList.add(AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE);
			businessTypeList.add(AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE);
			businessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION);
			businessTypeList.add(AllocationConstant.BusinessType.PBOC_RE_COUNTING);
			pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.OutHandoverStatus.TO_OUT_STORE_STATUS);
			// 执行查询
			pbocAllocationList = pbocAllAllocateInfoService.findListInterface(pbocAllAllocateInfo);
			// 设置增量查询日期
			if (!Collections3.isEmpty(pbocAllocationList)) {
				strUpdateDate = pbocAllocationList.get(0).getStrUpdateDate();
			}
		}

		// 根据出入库类型过滤数据
		List<Map<String, Object>> list = Lists.newArrayList();
		for (PbocAllAllocateInfo pbocAllAllocateTemp : pbocAllocationList) {
			Map<String, Object> detailMap = Maps.newHashMap();
			// 设置流水单号和业务类型
			detailMap.put(Parameter.ALL_ID, pbocAllAllocateTemp.getAllId());
			detailMap.put(Parameter.BUSINESS_TYPE_KEY, pbocAllAllocateTemp.getBusinessType());
			// 设置登记机构和接收机构
			detailMap.put(Parameter.AOFFICE_ID_KEY, pbocAllAllocateTemp.getAoffice().getId());
			detailMap.put(Parameter.AOFFICE_NAME_KEY, pbocAllAllocateTemp.getAoffice().getName());
			detailMap.put(Parameter.ROFFICE_ID_KEY, pbocAllAllocateTemp.getRoffice().getId());
			detailMap.put(Parameter.ROFFICE_NAME_KEY, pbocAllAllocateTemp.getRoffice().getName());
			// 出库时查询物品信息
			if (AllocationConstant.inoutType.STOCK_OUT.equals(paramMap.get(Parameter.INOUT_TYPE_KEY))) {
				List<Map<String, Object>> goodsList = Lists.newArrayList();
				for (PbocAllAllocateItem item : pbocAllAllocateTemp.getPbocAllAllocateItemList()) {
					if (AllocationConstant.RegistType.RegistStore.equals(item.getRegistType())) {
						Map<String, Object> goodsMap = Maps.newHashMap();
						goodsMap.put(Parameter.GOODS_ID_KEY, item.getGoodsId());
						goodsMap.put(Parameter.GOODS_NAME_KEY, StoreCommonUtils.getGoodsName(item.getGoodsId()));
						goodsMap.put(Parameter.GOODS_NUM_KEY, item.getMoneyNumber());
						goodsList.add(goodsMap);
					}
				}
				detailMap.put(Parameter.GOODS_LIST_KEY, goodsList);
			}
			// 入库时查询rfid信息
			if (AllocationConstant.inoutType.STOCK_IN.equals(paramMap.get(Parameter.INOUT_TYPE_KEY))) {
				List<Map<String, Object>> rfidList = Lists.newArrayList();
				List<StoRfidDenomination> rfidDenList = stoRfidDenominationService
						.findUsedListByOfficeId(pbocAllAllocateTemp.getAllId());
				for (StoRfidDenomination rfid : rfidDenList) {
					Map<String, Object> rfidMap = Maps.newHashMap();
					rfidMap.put(Parameter.RFID_KEY, rfid.getRfid());
					rfidMap.put(Parameter.GOODS_ID_KEY, rfid.getGoodsId());
					rfidMap.put(Parameter.GOODS_NAME_KEY, StoreCommonUtils.getGoodsName(rfid.getGoodsId()));
					rfidList.add(rfidMap);
				}
				detailMap.put(Parameter.RFID_LIST_KEY, rfidList);
			}
			list.add(detailMap);
		}

		map.put(Parameter.LIST_KEY, list);
		map.put(Parameter.SEARCH_DATE_KEY, strUpdateDate);
		map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(map);
	}
}
