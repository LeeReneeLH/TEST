package com.coffer.external.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service0503
 * <p>
 * Description: 门店-预约数据保存(PDA)
 * </p>
 * 
 * @author wanglin
 * @date 2017年10月25日 
 */
@Component("Service0503")
@Scope("singleton")
public class Service0503 extends HardwardBaseService {
	@Autowired
	private DoorOrderInfoService doorOrderInfoService;

	@Autowired
	private OfficeDao officeDao;
	
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		logger.debug(this.getClass().getName() + "门店-预约数据保存(PDA) -----------------开始");
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		DoorOrderInfo doorOrderInfo = getParam(paramMap, respMap);

		if (doorOrderInfo == null) {
			return gson.toJson(respMap);
		}

		// 保存数据
		doorOrderInfoService.save(doorOrderInfo);
		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			
		logger.debug(this.getClass().getName() + "门店-预约数据保存(PDA) -----------------结束");
		return gson.toJson(respMap);
	}
	
	/**
	 * 
	 * Title: getParam
	 * <p>Description: 获取并验证参数信息</p>
	 * @author:     wanglin
	 * @param paramMap 参数信息
	 * @return 用于保存的门店预约对象
	 * List<StoreGoodsInfo>    返回类型
	 */
	@SuppressWarnings("unchecked")
	private DoorOrderInfo getParam(Map<String, Object> paramMap, Map<String, Object> respMap) {
		
		String amountAll = "";		// 全部金额
		String boxNoAll = "";		// 全部包号

		
		//门店
		String custId = StringUtils.toString(paramMap.get("custId"));
		if (StringUtils.isBlank(custId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 门店ID 为空");
			return null;
		}
		
		//用户
		String userId = StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
		if (StringUtils.isBlank(userId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 操作用户ID 为空");
			return null;
		}
		
		//预约ID
		String orderId = StringUtils.toString(paramMap.get("keyId"));
		
		//金额列表
		if (paramMap.get(Parameter.LIST_KEY) == null) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 金额列表 为空");
			return null;
		}
		
		List<Map<String, Object>> mapList = (List<Map<String, Object>>)paramMap.get(Parameter.LIST_KEY);
		if (Collections3.isEmpty(mapList)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 金额列表 为空");
			return null;
		}
		
		// 获取门店信息
		Office office = officeDao.get(custId);
		// 门店无效判断
		if (Constant.deleteFlag.Invalid.equals(office.getDelFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E56);
			logger.debug(this.getClass().getName() + "ERROR : " + office.getName() + "无效");
			return null;
		}
		

		
		User operator = UserUtils.get(userId);
		
		DoorOrderInfo returnInfo =  new DoorOrderInfo();
		returnInfo.setDoorId(custId);
		returnInfo.setUserId(userId);
		returnInfo.setId(orderId);
		returnInfo.setMethod(WeChatConstant.MethodType.METHOD_PDA);
		
		
		List<Map<String, Object>> errorMapList = Lists.newArrayList();
		Map<String, Object> errorMap = null;
		String boxNo = null;
		String orgAmount = null;
		String amount = null;
		String sumAmount = "0";
		for (Map<String, Object> map : mapList) {
			boxNo = StringUtils.toString(map.get("boxNo"));				//包号
			orgAmount = StringUtils.toString(map.get("amount"));		//金额
			
			//金额非空检查
			if (StringUtils.isBlank(orgAmount)) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				logger.debug(this.getClass().getName() + "ERROR : 金额 为空");
				return null;
				
			}
			//金额数值检查
			amount = orgAmount.replaceAll(",","");
			boolean amountErr = false;
			if (!StringUtils.isNumber(amount)) {
				amountErr = true;
			}else{
				if (!isAmount(amount)){
					amountErr = true;
				}
			}

			if (amountErr) {
				errorMap = Maps.newHashMap();
				errorMap.put("boxNo", boxNo);
				errorMap.put("amount", amount);
				// message.E1062=[验证失败]箱号{0}， RFID{1}对应箱袋明细信息不存在！
//				String message = msg.getMessage("message.E1062", new String[] {boxNo, rfid}, locale);
				String message = "[验证失败]包号" + boxNo + "， 金额" + orgAmount + "对应金额不正确！";
//				errorMap.put(Parameter.ERROR_MSG_KEY, message);
				errorMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E55);
				logger.debug(this.getClass().getName() + "ERROR :" + message);
				errorMapList.add(errorMap);
				continue;
			}
			
			// 全部金额
			if (StringUtils.isBlank(amountAll)) {
				amountAll = amount;
			}else{
				amountAll = amountAll + "," +  amount;
			}
			
			// 全部包号
			if (StringUtils.isBlank(boxNoAll)) {
				boxNoAll = boxNo;
			}else{
				boxNoAll = boxNoAll + "," +  boxNo;
			}
			
			sumAmount = String.valueOf(Double.valueOf(sumAmount) + Double.valueOf(amount));
		}
		
		if (errorMapList.size() > 0) {
			respMap.put(Parameter.LIST_KEY, errorMapList);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			return null;
		}else{
			returnInfo.setAmountList(amountAll);
			returnInfo.setRfidList(boxNoAll);
			returnInfo.setAmount(sumAmount);
		}
		
		return returnInfo;
	}
	
    //金额验证  
    public static boolean isAmount(String str)   
    {   
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式  
        java.util.regex.Matcher match=pattern.matcher(str);   
        if(match.matches()==false)   
        {   
           return false;   
        }   
        else   
        {   
           return true;   
        }   
    }  
    
}
