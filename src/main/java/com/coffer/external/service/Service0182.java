package com.coffer.external.service;



import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateDetail;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;



/**
 * Title: Service0182
 * <p>Description: PDA同步下拨已装箱信息接口 </p>
 * @author qipeihong
 * @date 2017年7月11日 
 */
@Component("Service0182")
@Scope("singleton")
public class Service0182 extends HardwardBaseService {
	/**
	 * 
	 * @author qipeihong PDA同步下拨已装箱信息接口
	 * @version 2017-07-11
	 * 
	 * @Description
	 * @param paramMap
	 * @return
	 */
	
	@Autowired
	AllocationService allocationservice;
	
	@Autowired
	StoBoxInfoService stoboxinfoservice;

	@Override
	public String execute(Map<String, Object> paramMap) {
		Map<String, Object> resultmap = Maps.newHashMap();
		//版本号
		String versionNo = (String)paramMap.get(Parameter.VERSION_NO_KEY);
		//服务代码 
		String serviceNo = (String)paramMap.get(Parameter.SERVICE_NO_KEY);
		//机构Id
		String officeId = (String)paramMap.get(Parameter.OFFICE_ID_KEY);
		resultmap.put(Parameter.OFFICE_ID_KEY, officeId);
		//验证接口输入参数
		String paraCheckResult=checkBoxHandoutquato(paramMap,resultmap);
		// 验证失败的场合，退出
		if(Constant.FAILED.equals(paraCheckResult)){
			resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			resultmap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return setReturnMap(resultmap, serviceNo,versionNo);
		}
		// 通过officeId查询出状态为已装箱的流水
		List<AllAllocateInfo> bankAcceptedBoxDetail = allocationservice.bankAcceptedBoxDetailList(officeId);
		// 返回boxList
		List<Map<String, Object>> boxList = Lists.newArrayList();
		if(!Collections3.isEmpty(bankAcceptedBoxDetail)){
			//获得返回信息
			for(AllAllocateInfo allocateInfo:bankAcceptedBoxDetail){
				if (!Collections3.isEmpty(allocateInfo.getAllDetailList())) {
					for(AllAllocateDetail allocateDetail : allocateInfo.getAllDetailList()){
						//返回结果加入箱袋信息
						Map<String, Object>  jsonMap = Maps.newHashMap();
						if(AllocationConstant.Status.CashOrderQuotaYes.equals(allocateInfo.getStatus())){
								jsonMap.put(Parameter.BOX_NO_KEY, allocateDetail.getBoxNo());
								jsonMap.put(Parameter.STORE_ESCORT_RFID, allocateDetail.getRfid());
								//获取箱号对应的网店机构ID
								StoBoxInfo stoboxInfo=stoboxinfoservice.get(allocateDetail.getBoxNo());	
								jsonMap.put(Parameter.BOX_OFFICE_ID, stoboxInfo.getOffice().getId());
								jsonMap.put(Parameter.BOX_OFFICE_NAME, StoreCommonUtils.getOfficeById(stoboxInfo.getOffice().getId()).getName());
								jsonMap.put(Parameter.BOX_TYPE_NAME_KEY, DictUtils.getDictLabel(stoboxInfo.getBoxType(), "sto_box_type", null));
								boxList.add(jsonMap);		
						}
					}		
				}
			}
		}
		resultmap.put(Parameter.BOX_LIST_KEY, boxList);
		resultmap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return setReturnMap(resultmap, serviceNo,versionNo);
	}
	/**
	 * @author qipeihong
	 * @version 2017-7-11
	 * 
	 * @Description 验证 PDA网点上缴同步接口 输入参数
	 * @param headInfo
	 * @return 处理结果
	 */
	private String checkBoxHandoutquato(Map<String, Object> paramMap,Map<String, Object> resultmap) {

		// 机构编号ID
		if (paramMap.get(Parameter.OFFICE_ID_KEY) == null || StringUtils.isBlank(paramMap.get(Parameter.OFFICE_ID_KEY).toString())) {
			logger.debug("参数错误--------officeId为空");
			resultmap.put(Parameter.ERROR_MSG_KEY, "参数错误--------officeId为空");
			return Constant.FAILED;
		}	
		return Constant.SUCCESS;
	}
	/**
	 * 设置接口返回内容
	 * @param map
	 * @param serviceNo
	 * @return
	 */
	private String setReturnMap(Map<String, Object> map, String serviceNo,String versionNo){
		map.put(Parameter.VERSION_NO_KEY, versionNo);
		map.put(Parameter.SERVICE_NO_KEY, serviceNo);
		
		return gson.toJson(map); 
	}
	
	
}




