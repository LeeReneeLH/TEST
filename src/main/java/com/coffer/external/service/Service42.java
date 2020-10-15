package com.coffer.external.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllHandoverUserDetail;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllHandoverInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service42
* <p>Description: 人行出入库查询接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service42")
@Scope("singleton")
public class Service42 extends HardwardBaseService {
	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private PbocAllHandoverInfoService pbocAllHandoverInfoService;
	
    @Autowired
    private StoGoodsLocationInfoService stoGoodsLocationInfoService;
	/**
     * @author LLF
     * @version 2016年05月31日
     * 
     *          42：人行出入库查询接口
     * @param paramMap
     * @return
     */
	@Override
	public String execute(Map<String, Object> paramMap) {

		Map<String, Object> map = new HashMap<String, Object>();
		// 版本号、服务代码
		map.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		map.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		// 取得参数
		PbocAllAllocateInfo pbocAllAllocateInfo = this.getParamOfFindPbocAllocationInfo(paramMap);
		
		if (pbocAllAllocateInfo == null) {
			// 参数异常
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			
		} else {

			// 当日所有调拨信息
			pbocAllAllocateInfo.setAcceptDate(new Date());
			// 接收机构有值的情况下，金融平台用户登录时显示所有数据
			if (pbocAllAllocateInfo.getAoffice() != null) {
				if (Constant.OfficeType.DIGITAL_PLATFORM.equals(pbocAllAllocateInfo.getAoffice().getType())) {
					pbocAllAllocateInfo.setAoffice(null);
				}
			}
			// 登记机构有值的情况下，金融平台用户登录时显示所有数据
			if (pbocAllAllocateInfo.getRoffice() != null) {
				if (Constant.OfficeType.DIGITAL_PLATFORM.equals(pbocAllAllocateInfo.getRoffice().getType())) {
					pbocAllAllocateInfo.setRoffice(null);
				}
			}
			// 执行查询
			List<PbocAllAllocateInfo> pbocAllocationList = pbocAllAllocateInfoService.findListInterface(pbocAllAllocateInfo);
			// 入库统计库区
			List<Map<String, Object>> allAreaList = Lists.newArrayList();
			List<String> businessTypeList = pbocAllAllocateInfo.getBusinessTypeList();
			if (!Collections3.isEmpty(pbocAllocationList)
					&& (businessTypeList.contains(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN)
							|| businessTypeList.contains(AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE)
							|| businessTypeList.contains(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN)
							|| businessTypeList.contains(AllocationConstant.BusinessType.PBOC_RE_COUNTING))) {
				allAreaList = stoGoodsLocationInfoService.findStoreAreaByInStoreId(pbocAllocationList);
			}

			// 封装执行结果
			List<Map<String, Object>> list = Lists.newArrayList();
			for (PbocAllAllocateInfo pbocAllAllocateTemp : pbocAllocationList) {
				Map<String, Object> detailMap = Maps.newHashMap();
				// 流水单号
				detailMap.put(Parameter.SERIALORDER_NO_KEY, pbocAllAllocateTemp.getId());
				// 客户编号
				if (AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(pbocAllAllocateTemp.getBusinessType())) {
					// 调拨入库时手动填写机构名称
					detailMap.put(Parameter.OFFICE_ID_KEY, pbocAllAllocateTemp.getRoffice() == null ? "" : pbocAllAllocateTemp.getRoffice().getId() );
					detailMap.put(Parameter.OFFICE_NAME_KEY, pbocAllAllocateTemp.getRofficeName());
				} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(pbocAllAllocateTemp.getBusinessType())
						|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(pbocAllAllocateTemp.getBusinessType())) {
					// 调拨出库时手动填写机构名称
					detailMap.put(Parameter.OFFICE_ID_KEY, pbocAllAllocateTemp.getAoffice() == null ? "" : pbocAllAllocateTemp.getAoffice().getId());
					detailMap.put(Parameter.OFFICE_NAME_KEY, pbocAllAllocateTemp.getAofficeName());
				} else {
					detailMap.put(Parameter.OFFICE_ID_KEY, pbocAllAllocateTemp.getRoffice().getId());
					detailMap.put(Parameter.OFFICE_NAME_KEY, pbocAllAllocateTemp.getRofficeName());
				}
				
				// 复点出库时，总金额为登记金额
				if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(pbocAllAllocateTemp.getBusinessType())) {
					detailMap.put(Parameter.POINT_AMOUNT_KEY, pbocAllAllocateTemp.getRegisterAmount());
				} else {
					// 审批总金额
					detailMap.put(Parameter.POINT_AMOUNT_KEY, pbocAllAllocateTemp.getConfirmAmount());
				}
				detailMap.put(Parameter.STATUS_KEY, pbocAllAllocateTemp.getStatus());
				detailMap.put(Parameter.BUSINESS_TYPE_KEY, pbocAllAllocateTemp.getBusinessType());
				detailMap.put(Parameter.IN_STORE_AMOUNT_KEY, pbocAllAllocateTemp.getInstoreAmount() == null ? "0.00" : pbocAllAllocateTemp.getInstoreAmount());
				// 统计调拨库区信息
				StringBuffer sb = new StringBuffer();
				for (Map<String, Object> itemMap : allAreaList) {
					if (pbocAllAllocateTemp.getAllId().equals(itemMap.get(Parameter.SERIALORDER_NO_KEY))) {
						// 库区类型
						String strStoreAreaType = itemMap.get(Parameter.STORE_AREA_TYPE_KEY).toString();
						// 库区类型名称
						String strStoreAreaTypeName = DictUtils.getDictLabel(strStoreAreaType, "store_area_type", "");
						sb.append(strStoreAreaTypeName);
						sb.append(" ");
						sb.append(itemMap.get(Parameter.STORE_AREA_NAME_KEY));
						sb.append(",");
					}
				}
				if (sb != null && sb.length() > 0) {
					detailMap.put(Parameter.STORE_AREA_NAME_KEY, sb.substring(0, sb.length() - 1));
				} else {
					detailMap.put(Parameter.STORE_AREA_NAME_KEY, "");
				}

				// 申请明细
				List<Map<String, Object>> itemsList = Lists.newArrayList();
				for (PbocAllAllocateItem itemsTemp : pbocAllAllocateTemp.getPbocAllAllocateItemList()) {
					// 人行审批
					if (AllocationConstant.RegistType.RegistStore.equals(itemsTemp.getRegistType())) {

						Map<String, Object> itemsMap = Maps.newHashMap();

						itemsMap.put(Parameter.GOODS_ID_KEY, itemsTemp.getGoodsId());
						itemsMap.put(Parameter.DENOMINATION_KEY, StoreCommonUtils.getPbocGoodsDenominationByGoodId(itemsTemp.getGoodsId()));
						itemsMap.put(Parameter.UNIT_KEY, itemsTemp.getUnit());
						itemsMap.put(Parameter.NUM_KEY, itemsTemp.getMoneyNumber());

						itemsList.add(itemsMap);
					}
				}

				detailMap.put(Parameter.DETAIL_LIST_KEY, itemsList);
				
				// 取得交接信息
				PbocAllHandoverInfo pbocAllHandoverInfo = pbocAllHandoverInfoService.getHandoverInfoByAllId(pbocAllAllocateTemp.getId());
				// 人行交接人
				String strPbocHandover = "";
				// 商业机构交接人
				String strFinanaceHandover = "";
				
				if (pbocAllHandoverInfo != null) {
					for (PbocAllHandoverUserDetail detail : pbocAllHandoverInfo.getHandoverUserDetailList()) {
						if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(pbocAllAllocateTemp.getBusinessType())
								|| AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(pbocAllAllocateTemp.getBusinessType())
								|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(pbocAllAllocateTemp.getBusinessType())) {
							if (AllocationConstant.UserType.accept.equals(detail.getType())) {
								strPbocHandover = strPbocHandover + detail.getEscortName() + " ";
							} else if (AllocationConstant.UserType.handover.equals(detail.getType())) {
								strFinanaceHandover = strFinanaceHandover + detail.getEscortName() + " ";
							}
						} else if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(pbocAllAllocateTemp.getBusinessType())) {
							if (AllocationConstant.UserType.handover.equals(detail.getType()) && AllocationConstant.InoutType.Out.equals(detail.getInoutType())) {
								strPbocHandover = strPbocHandover + detail.getEscortName() + " ";
							} else if (AllocationConstant.UserType.accept.equals(detail.getType()) && AllocationConstant.InoutType.Out.equals(detail.getInoutType())) {
								strFinanaceHandover = strFinanaceHandover + detail.getEscortName() + " ";
							} else if (AllocationConstant.UserType.accept.equals(detail.getType()) && AllocationConstant.InoutType.In.equals(detail.getInoutType())) {
								strPbocHandover = strPbocHandover + detail.getEscortName() + " ";
							} else if (AllocationConstant.UserType.handover.equals(detail.getType()) && AllocationConstant.InoutType.In.equals(detail.getInoutType())) {
								strFinanaceHandover = strFinanaceHandover + detail.getEscortName() + " ";
							}
						} else {
							if (AllocationConstant.UserType.handover.equals(detail.getType())) {
								strPbocHandover = strPbocHandover + detail.getEscortName() + " ";
							} else if (AllocationConstant.UserType.accept.equals(detail.getType())) {
								strFinanaceHandover = strFinanaceHandover + detail.getEscortName() + " ";
							}
						}
					}
				}
				
				// 人行交接人
				detailMap.put(Parameter.PBOC_HANDOVER_KEY, strPbocHandover);
				// 人行交接人
				detailMap.put(Parameter.FINANCE_HANDOVER_KEY, strFinanaceHandover);
				
				list.add(detailMap);
			}
			map.put(Parameter.LIST_KEY, list);
			map.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		}
		

		return gson.toJson(map);
	}
	
	/**
	 *  42：人行出入库查询接口 入参检查及取得
	 * @author WangBaozhong
	 * @version 2016年7月7日
	 * 
	 *  
	 * @param headInfo 入参信息
	 * @return 出入库查询入参
	 */
	@SuppressWarnings("unchecked")
	private PbocAllAllocateInfo getParamOfFindPbocAllocationInfo(Map<String, Object> headInfo) {
		logger.warn("42：人行出入库查询接口--------获取参数开始");
		Object officeId = headInfo.get(Parameter.OFFICE_ID_KEY);
		
		// 验证机构ID
		if (officeId == null || StringUtils.isBlank(officeId.toString())) {
			logger.warn("42：人行出入库查询接口--------参数机构ID为空");
			return null;
		}
		Office office = officeService.get(officeId.toString());
		
		if (office == null) {
			logger.warn("42：人行出入库查询接口--------参数机构ID对应机构不存在， 参数机构ID：" + officeId.toString());
			return null;
		}
		// 业务类型
		List<Map<String, Object>> itemMapList = null;
		if (headInfo.get(Parameter.LIST_KEY) != null) {
			itemMapList = (List<Map<String, Object>>) headInfo.get(Parameter.LIST_KEY);
			if (Collections3.isEmpty(itemMapList)) {
				logger.debug("42：人行出入库查询接口-------- 取得业务类型列表：" + headInfo.get(Parameter.LIST_KEY));
				return null;
			}
		} else {
			logger.debug("42：人行出入库查询接口-------- 取得业务类型列表：" + headInfo.get(Parameter.LIST_KEY));
			return null;
		}
		List<String> businessTypeList = Lists.newArrayList();
		for (Map<String, Object> map : itemMapList) {
			Object businessType = map.get(Parameter.BUSINESS_TYPE_KEY);
			if (businessType == null || StringUtils.isBlank(businessType.toString())) {
				logger.warn("42：人行出入库查询接口--------参数业务类型为空");
				return null;
			}
			businessTypeList.add(businessType.toString());
		}
		// 出入库类型
		Object inoutType = headInfo.get(Parameter.INOUT_TYPE_KEY);
		if (inoutType == null || StringUtils.isBlank(inoutType.toString())) {
			logger.warn("42：人行出入库查询接口--------参数业务类型为空");
			return null;
		}
		
		// 封装查询条件
		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
		
		String strInoutType = inoutType.toString();
		// 设定出入库类型
		pbocAllAllocateInfo.setInoutType(strInoutType);
		// 设定业务类型
		pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
		
		// 设定业务状态
		List<String> statusList = Lists.newArrayList();
		for (String businessType : businessTypeList) {
			if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(businessType) 
					|| AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(businessType)) {
				// 设定入库状态
				statusList.addAll(Global.getList("pboc_allocation_in_store_saw_task_status"));
				// 申请上缴，代理上缴 入库设定接收机构为当前人行金库
				pbocAllAllocateInfo.setAoffice(office);
			} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(businessType)) {
				// 设定入库状态
				statusList.addAll(Global.getList("pboc_allocation_in_store_saw_task_status"));
				// 调拨入库设定登记为当前人行金库
				pbocAllAllocateInfo.setAoffice(office);
			} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE.equals(businessType)
					|| AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE.equals(businessType)) {
				// 设定出库状态
				statusList.addAll(Global.getList("pboc_allocation_out_store_saw_task_status"));
				// 销毁出库  ，调拨出库 登记机构为当前人行金库
				pbocAllAllocateInfo.setRoffice(office);
			} else if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(businessType)) {
				// 设定出库状态
				statusList.addAll(Global.getList("pboc_allocation_out_store_saw_task_status"));
				// 申请下拨 接收机构为当前人行金库
				pbocAllAllocateInfo.setAoffice(office);
			} else if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(businessType)) {
				// 设定复点状态
				if (AllocationConstant.InOutCoffer.OUT.equals(strInoutType)) {
					statusList.addAll(Global.getList("pboc_allocation_recounting_out_saw_task_status"));
				} else if (AllocationConstant.InOutCoffer.IN.equals(strInoutType)) {
					statusList.addAll(Global.getList("pboc_allocation_recounting_in_saw_task_status"));
				}
				// 复点 登记机构为当前人行金库
				pbocAllAllocateInfo.setRoffice(office);
			} else {
				logger.warn("42：人行出入库查询接口--------参数业务类型不是合法类型，参数业务类型：" + businessType);
				return null;
			}
		}
		
		
		// 当日所有调拨信息
		pbocAllAllocateInfo.setStatuses(statusList);
		pbocAllAllocateInfo.setAcceptDate(new Date());
		logger.warn("42：人行出入库查询接口--------获取参数结束");
		return pbocAllAllocateInfo;
	}

}
