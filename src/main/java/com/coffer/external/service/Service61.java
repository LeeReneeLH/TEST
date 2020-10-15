package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.AllocationConstant.BusinessType;
import com.coffer.businesses.modules.allocation.AllocationConstant.PbocOrderStatus.RecountingStatus;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service61
* <p>Description: </p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service61")
@Scope("singleton")
public class Service61 extends HardwardBaseService {

	@Autowired
    private OfficeService officeService;

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;
	/**
     * @author Zhengkaiyuan
     * @version 2016年08月30日
     * 
     *          61：PDA出入库任务查询接口
     * @param paramMap
     * @return
     */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		Office office = this.getParamOfFindPDAOfficeInfo(paramMap);
		if (office == null) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			
		} else {
			PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
			List<PbocAllAllocateInfo> pbocAllocationList = Lists.newArrayList();
			List<String> statusList = Lists.newArrayList();
			List<String> businessTypeList = Lists.newArrayList();
			//查询代理上缴 申请上缴
			statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
			pbocAllAllocateInfo.setRoffice(office);
			businessTypeList.add(BusinessType.PBOC_AGENT_HANDIN);
			businessTypeList.add(BusinessType.PBOC_APPLICATION_HANDIN);
			pbocAllAllocateInfo.setStatuses(statusList);
			pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
			pbocAllocationList.addAll(pbocAllAllocateInfoService.findListInterface(pbocAllAllocateInfo));
			//查询复点入库
			statusList.clear();
			businessTypeList.clear();
			statusList.add(RecountingStatus.TO_RECOUNTING_STATUS);
			businessTypeList.add(BusinessType.PBOC_RE_COUNTING);
			pbocAllAllocateInfo.setStatuses(statusList);
			pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
			pbocAllocationList.addAll(pbocAllAllocateInfoService.findListInterface(pbocAllAllocateInfo));
			//查询调拨入库
			statusList.clear();
			businessTypeList.clear();
			statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
			businessTypeList.add(BusinessType.PBOC_ALLOCATION_IN_STORE);
			pbocAllAllocateInfo.setRoffice(null);
			pbocAllAllocateInfo.setAoffice(office);
			pbocAllocationList.addAll(pbocAllAllocateInfoService.findListInterface(pbocAllAllocateInfo));
			// 封装执行结果
			List<Map<String, Object>> list = Lists.newArrayList();
			for (PbocAllAllocateInfo pbocAllAllocateTemp : pbocAllocationList) {
				Map<String, Object> detailMap = Maps.newHashMap();
				// 流水单号
				detailMap.put(Parameter.SERIALORDER_NO_KEY, pbocAllAllocateTemp.getId());
				detailMap.put(Parameter.BUSINESS_TYPE_KEY, pbocAllAllocateTemp.getBusinessType());
				List<String> denonminationList = Lists.newArrayList();
				//复点入库登记金额即为审批金额,复点有面值
				if(BusinessType.PBOC_RE_COUNTING.equals(pbocAllAllocateTemp.getBusinessType()))
				{
					detailMap.put(Parameter.APPROVAL_AMOUNT_KEY, BigDecimal.valueOf(pbocAllAllocateTemp.getRegisterAmount()).toPlainString());
					// 申请明细
					for (PbocAllAllocateItem itemsTemp : pbocAllAllocateTemp.getPbocAllAllocateItemList()) {
						// 人行审批
						if (AllocationConstant.RegistType.RegistStore.equals(itemsTemp.getRegistType())) {
							String demonmination = StoreCommonUtils.getPbocGoodsDenominationByGoodId(itemsTemp.getGoodsId());
							if(!denonminationList.contains(demonmination)){
								denonminationList.add(demonmination);
							}
						}
					}
				}
				else
				{
					detailMap.put(Parameter.APPROVAL_AMOUNT_KEY, BigDecimal.valueOf(pbocAllAllocateTemp.getConfirmAmount()).toPlainString());
				}
				detailMap.put(Parameter.DENOMINATION_KEY,denonminationList.toString().replace("[", "").replace("]", ""));
				detailMap.put(Parameter.STATUS_KEY, pbocAllAllocateTemp.getStatus());
				list.add(detailMap);
				
			}
			
			map.put(Parameter.LIST_KEY, list);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		}
		

		return gson.toJson(map);
	}
	
	/**
	 *  61：PDA人行出入库查询接口 入参检查及取得  获取机构信息
	 * @author Zhengkaiyuan
	 * @version 2016年8月30日
	 * 
	 *  
	 * @param headInfo 入参信息
	 * @return 出入库查询入参
	 */
	private Office getParamOfFindPDAOfficeInfo(Map<String, Object> headInfo) {
		logger.warn("61：PDA人行出入库查询接口--------获取机构信息开始");
		Object officeId = headInfo.get(Parameter.OFFICE_ID_KEY);
		
		// 验证机构ID
		if (officeId == null || StringUtils.isBlank(officeId.toString())) {
			logger.warn("61：PDA人行出入库查询接口--------参数机构ID为空");
			return null;
		}
		Office office = officeService.get(officeId.toString());
		
		if (office == null) {
			logger.warn("61：PDA人行出入库查询接口--------参数机构ID对应机构不存在， 参数机构ID：" + officeId.toString());
			return null;
		}
		logger.warn("61：PDA人行出入库查询接口--------获取机构信息结束");
		return office;
	}

}
