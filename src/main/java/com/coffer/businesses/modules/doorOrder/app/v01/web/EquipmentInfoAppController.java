package com.coffer.businesses.modules.doorOrder.app.v01.web;

import java.util.List;
import java.util.Map;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.app.v01.service.EquipmentInfoAppService;
import com.coffer.businesses.modules.doorOrder.v01.entity.AjaxDoorOrderInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.AjaxEquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.AjaxResponse;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentWarnings;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeApp;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentWarningsService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 查询机具列表 App对应Controller
 * 
 * @author zxk
 * 
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/app/v01/equipmentInfoApp")
public class EquipmentInfoAppController extends BaseController {

	@Autowired
	private EquipmentInfoAppService quipmentInfoAppService;
	
	@Autowired
    private DoorOrderInfoService doorOrderInfoService;
	
	@Autowired
	private EquipmentWarningsService equipmentWarningsService;
	
	/**
	 * serviceNo: 042 查询当前机构下所有商户列表 并展示相关门店机具信息
	 * 
	 * @author zxk
	 * @version 2019-8-27
	 * @param userId
	 * @param pageSize
	 * @param pageNo
	 * @param orderBy
	 * @return
	 */
	@RequestMapping(value = "getMerchartEquipInfoList", produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String getMerchartEquipInfoList(@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		/*
		 * 判断用户是否存在
		 */
		Map<String, Object> jsonData = Maps.newHashMap();
		User user = UserUtils.get(userId);
		if (user == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户不存在");
			return gson.toJson(jsonData);
		}
		jsonData = quipmentInfoAppService.getMerchartInfoList(user, new Page<OfficeApp>(pageNo, pageSize));
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * serviceNo: 043 按照商户查询门店信息列表(增加登录机构是门店的查询)
	 * 
	 * @author zxk
	 * @version 2019-8-27
	 * @param merchartId
	 * @param pageSize
	 * @param pageNo
	 * @param orderBy
	 * @return
	 */
	@RequestMapping(value = "getDoorInfoList", produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String getDoorInfoList(@RequestParam(value = "merchantId", required = false) String merchantId,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		Office office = StoreCommonUtils.getOfficeById(merchantId);
		Map<String, Object> jsonData = Maps.newHashMap();
		if (!(office.getType().equals(Constant.OfficeType.MERCHANT)
				|| office.getType().equals(Constant.OfficeType.STORE))) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数类型不正确");
			return gson.toJson(jsonData);
		}
		if (office.getType().equals(Constant.OfficeType.STORE)) {
			jsonData.put(Parameter.MERCHANT_NAME, StoreCommonUtils.getOfficeById(office.getParentId()).getName());
		}
		Page<OfficeApp> doorPage = quipmentInfoAppService.getDoorInfoList(office,
				new Page<OfficeApp>(pageNo, pageSize));
		jsonData.put(Parameter.DOOR_LIST, doorPage);
		jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(jsonData);
	}

	/**
	 * serviceNo: 按照连接状态查询机构下总机具列表
	 * 
	 * @author zxk
	 * @version 2019-8-29
	 * @return
	 */
	@RequestMapping(value = "getEquipmentListByConnStatus", produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String getEquipmentListByConnStatus(@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "connStatus", required = false) String connStatus,
			@RequestParam(value = "id", required = false) String id,
			@RequestParam(value = "pageSize", required = false) int pageSize,
			@RequestParam(value = "pageNo", required = false) int pageNo) {
		/*
		 * 判断用户是否存在(点击事件,不存在这种情况,应该后台获取登录机构)
		 */
		Map<String, Object> jsonData = Maps.newHashMap();
		User user = UserUtils.get(userId);
		if (user == null) {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "用户不存在");
			return gson.toJson(jsonData);
		}
		if (connStatus.equals(DoorOrderConstant.ConnStatus.NORMAL)
				|| connStatus.equals(DoorOrderConstant.ConnStatus.UNUSUAL)) {
			jsonData = quipmentInfoAppService.getEquipmentListByConnStatus(user, connStatus,id,
					new Page<OfficeApp>(pageNo, pageSize));

		} else {
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			jsonData.put(Parameter.ERROR_MSG_KEY, "参数类型不符");
		}
		return gson.toJson(jsonData);
	}
	
	/**
     * 获取机具在线状态接口
     *
     * Description: 
     *
     * @author: GJ
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getAppEquipmentStatus")
    public AjaxResponse getEquipmentStatus(@RequestParam(value = "userId", required = true) String userId,
    		@RequestParam(value = "pageSize", required = true) int pageSize,
    		@RequestParam(value = "pageNo", required = true) int pageNo,
    		@RequestParam(value = "seriesNumber", required = false) String seriesNumber) {
    	Office office = UserUtils.get(userId).getOffice();
    	DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
    	List<AjaxEquipmentInfo> result = Lists.newArrayList();
    	Long allCount = null;
    	//首次进入方法时获取机具总数
    	if(pageNo == 1) {
    		List<EquipmentInfo> allList = quipmentInfoAppService.findEqpList(userId, null ,seriesNumber);
    		allCount = (long) allList.size();
    	}
    	//根据当前登录人所属机构获取对应的机具列表，分页显示
    	List<EquipmentInfo> list = quipmentInfoAppService.findEqpList(userId, new Page<EquipmentInfo>(pageNo, pageSize), seriesNumber);
    	Integer currentPageCount = list.size();
    	List<String> idList = Lists.newArrayList();
    	if(!Collections3.isEmpty(list)) {
    		for (EquipmentInfo temp : list) {
        		idList.add(temp.getId());
    		}
        	doorOrderInfo.setEqpIds(idList);
        	//根据获取的机具列表查询机具中的存款信息
        	List<DoorOrderInfo> infoResult = doorOrderInfoService.getInfoByEqpIds(doorOrderInfo);
    		for (int i = 0; i < list.size(); i++) {
        		AjaxEquipmentInfo info = new AjaxEquipmentInfo();
        		EquipmentInfo currentInfo = list.get(i);
        		EquipmentWarnings equipmentWarnings = new EquipmentWarnings();
        		equipmentWarnings.setMachNo(currentInfo.getId());
        		equipmentWarnings.setOffice(office);
        		List<EquipmentWarnings> warningList = equipmentWarningsService.findWarningList(equipmentWarnings);
        		EquipmentWarnings warningInfo = null;
        		if(!CollectionUtils.isEmpty(warningList)) {
        			warningInfo = warningList.get(0);
        		}
        		info.setId(currentInfo.getId());
        		info.setName(currentInfo.getName());
        		info.setSeriesNumber(currentInfo.getSeriesNumber());
        		AjaxDoorOrderInfo orderInfo = new AjaxDoorOrderInfo();
        		orderInfo.setAmount("0");
        		orderInfo.setTotalCount("0");
        		orderInfo.setPercent("0");
        		orderInfo.setBagCapacity("0");
        		String connStatus = "正常";
        		String clearStatus = "正常";
        		String printerStatus = "正常";
        		String doorStatus = "正常";
        		//机具连线状态：01:正常 02:停用 03:关机 04:故障锁定 05:异常
        		switch(currentInfo.getConnStatus()) {
        			case "01" :
        				connStatus = "正常";
        				break;
        			case "02" :
        				connStatus = "停用";
        				break;
        			case "03" :
        				connStatus = "关机";
        				break;
        			case "04" :
        				connStatus = "故障锁定";
        				break;
        			case "05" :
        				connStatus = "异常";
        				break;
        			default :
        				connStatus = "正常";
        		}
        		if(warningInfo != null) {
        			//清分机状态：1正常，2故障，3掉线
            		switch(warningInfo.getClearStatus()) {
    	    			case "1" :
    	    				clearStatus = "正常";
    	    				break;
    	    			case "2" :
    	    				clearStatus = "故障";
    	    				break;
    	    			case "3" :
    	    				clearStatus = "掉线";
    	    				break;
    	    			default :
    	    				clearStatus = "正常";
            		}	
            		//打印机状态：1正常，2故障，3掉线，4缺纸
            		switch(warningInfo.getPrinterStatus()) {
    	    			case "1" :
    	    				printerStatus = "正常";
    	    				break;
    	    			case "2" :
    	    				printerStatus = "故障";
    	    				break;
    	    			case "3" :
    	    				printerStatus = "掉线";
    	    				break;
    	    			case "4" :
    	    				printerStatus = "缺纸";
    	    				break;
    	    			default :
    	    				printerStatus = "正常";
            		}	
            		//仓门状态：1正常，2故障，3掉线，4打开
            		switch(warningInfo.getDoorStatus()) {
    	    			case "1" :
    	    				doorStatus = "正常";
    	    				break;
    	    			case "2" :
    	    				doorStatus = "故障";
    	    				break;
    	    			case "3" :
    	    				doorStatus = "掉线";
    	    				break;
    	    			case "4" :
    	    				doorStatus = "打开";
    	    				break;
    	    			default :
    	    				doorStatus = "正常";
            		}
        		}
        		info.setConnStatus(connStatus);
        		info.setClearStatus(clearStatus);
        		info.setPrinterStatus(printerStatus);
        		info.setDoorStatus(doorStatus);
        		if(!Collections3.isEmpty(infoResult)) {
	    			for(int j = 0; j < infoResult.size(); j++) {
	        			if(list.get(i).getId().toUpperCase().equals(infoResult.get(j).getEquipmentId().toUpperCase())) {
	        				orderInfo.setAmount(infoResult.get(j).getAmount());
	        				String strTotalCount = infoResult.get(j).getMoneyCount();
	        				if(strTotalCount == null) {
	        					strTotalCount = "0";
	        				}
	        				String strBagCapacity = infoResult.get(j).getBagCapacity();
	        				orderInfo.setTotalCount(strTotalCount);
	        				orderInfo.setBagCapacity(strBagCapacity);
	        				float floatTotalCount = 0;
	        				if(!StringUtils.isEmpty(strTotalCount)) {
	        					floatTotalCount = Float.parseFloat(strTotalCount);
	        				}
	        				float floatBagCapacity = 0;
	        				if(!StringUtils.isEmpty(strBagCapacity)) {
	        					floatBagCapacity = Float.parseFloat(strBagCapacity);
	        				}
	        				double percent = 0;
	        				if(!StringUtils.isEmpty(strTotalCount) 
	        						&& !StringUtils.isEmpty(strBagCapacity) 
	        						&& floatBagCapacity != 0) {
	        					percent = floatTotalCount / floatBagCapacity;
	        				}
	        				orderInfo.setPercent(StringUtils.toString(percent));
	        				break;
	        			}
	        		}
        		} 
    			info.setOrderInfo(orderInfo);
        		result.add(info);
        	}
    	}
    	return AjaxResponse.success(result, allCount, currentPageCount);
    }
    
    /**
     * 获取机具对应的报警信息记录
     *
     * Description: 
     *
     * @author: GJ
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getWarningInfos")
    public AjaxResponse getWarningInfos(@RequestParam(value = "eqpId", required = true) String eqpId,
    		@RequestParam(value = "pageSize", required = true) int pageSize,
    		@RequestParam(value = "pageNo", required = true) int pageNo) {
    	EquipmentWarnings warning = new EquipmentWarnings();
    	warning.setMachNo(eqpId);
    	List<EquipmentWarnings> result = Lists.newArrayList();
    	Page<EquipmentWarnings> page = equipmentWarningsService.findPage(new Page<EquipmentWarnings>(pageNo, pageSize), warning);
    	result = page.getList();
    	for(EquipmentWarnings tempWarning : result) {
    		String clearStatus = "正常";
    		String printerStatus = "正常";
    		String doorStatus = "正常";
    		//机具连线状态：01:正常 02:停用 03:关机 04:故障锁定 05:异常
    		if(tempWarning != null) {
    			//清分机状态：1正常，2故障，3掉线
        		switch(tempWarning.getClearStatus()) {
	    			case "1" :
	    				clearStatus = "正常";
	    				break;
	    			case "2" :
	    				clearStatus = "故障";
	    				break;
	    			case "3" :
	    				clearStatus = "掉线";
	    				break;
	    			default :
	    				clearStatus = "正常";
        		}	
        		//打印机状态：1正常，2故障，3掉线，4缺纸
        		switch(tempWarning.getPrinterStatus()) {
	    			case "1" :
	    				printerStatus = "正常";
	    				break;
	    			case "2" :
	    				printerStatus = "故障";
	    				break;
	    			case "3" :
	    				printerStatus = "掉线";
	    				break;
	    			case "4" :
	    				printerStatus = "缺纸";
	    				break;
	    			default :
	    				printerStatus = "正常";
        		}	
        		//仓门状态：1正常，2故障，3掉线，4打开
        		switch(tempWarning.getDoorStatus()) {
	    			case "1" :
	    				doorStatus = "正常";
	    				break;
	    			case "2" :
	    				doorStatus = "故障";
	    				break;
	    			case "3" :
	    				doorStatus = "掉线";
	    				break;
	    			case "4" :
	    				doorStatus = "打开";
	    				break;
	    			default :
	    				doorStatus = "正常";
        		}
    		}
    		tempWarning.setClearStatus(clearStatus);
    		tempWarning.setPrinterStatus(printerStatus);
    		tempWarning.setDoorStatus(doorStatus);
    	}
    	int size = result.size();
    	long count = page.getCount();
    	return AjaxResponse.success(result, count, size);
    }
}
