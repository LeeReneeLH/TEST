package com.coffer.external.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoGoods;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.entity.StoRfidEntity;
import com.coffer.businesses.modules.store.v01.service.StoGoodsService;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.coffer.external.hessian.HardwareConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
* Title: Service62
* <p>Description: 按流水单号邦定RFID接口</p>
* @author wangbaozhong
* @date 2017年7月6日 上午10:41:10
*/
@Component("Service62")
@Scope("singleton")
public class Service62 extends HardwardBaseService {

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@Autowired
	private StoGoodsService stoGoodsService;
	
	/**
	 * @author cai xiaojie
	 * @version 2016年8月30日 62：按流水单号邦定RFID接口
	 * @param paramMap
	 * @return
	 */
	@Transactional(readOnly = false)
	@Override
	public String execute(Map<String, Object> paramMap) {
		
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));

		// 取得电文输入参数
		StoRfidEntity inputParam = getBindingRfidBySNOParam(paramMap);
		if (StringUtils.isNotBlank(inputParam.getErrorFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, inputParam.getErrorFlag());
			return gson.toJson(respMap);
		}

		// 验证流水单号对应业务类型是否正确
		PbocAllAllocateInfo pbocAllAllocateInfo = pbocAllAllocateInfoService.get(inputParam.getAllId());
		
		if (pbocAllAllocateInfo == null) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E14);
			return gson.toJson(respMap);
		} 
		
		String bussnessType = pbocAllAllocateInfo.getBusinessType();
		if (!(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(bussnessType)
				|| AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(bussnessType)
				|| AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(bussnessType)
				|| AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(bussnessType))) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E47);
			return gson.toJson(respMap);
		}
		if (AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(bussnessType)) {
			// 复点入库时 验证流水单号业务状态 是否为清分中
			if (!AllocationConstant.PbocOrderStatus.RecountingStatus.TO_RECOUNTING_STATUS
					.equals(pbocAllAllocateInfo.getStatus())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
				return gson.toJson(respMap);
			}
		} else {
			// 验证流水单号业务状态 是否为待入库
			if (!AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS
					.equals(pbocAllAllocateInfo.getStatus())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E42);
				return gson.toJson(respMap);
			}
		}
		// 验证审批金额 是否大于等于绑卡物品总金额
		BigDecimal rfidAmount = new BigDecimal(0);
		for (StoRfidDenomination item : inputParam.getRfidDenominationList()) {
			StoGoods goods = stoGoodsService.get(item.getGoodsId());
			rfidAmount = rfidAmount.add(goods.getGoodsVal());
		}
		if(AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(pbocAllAllocateInfo.getBusinessType())) {
			//复点入库金额必须相等
			if(pbocAllAllocateInfo.getRegisterAmount() != rfidAmount.doubleValue()){
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E48);
				return gson.toJson(respMap);
			}
		} else if (pbocAllAllocateInfo.getConfirmAmount() < rfidAmount.doubleValue()) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E48);
			return gson.toJson(respMap);
		}

		// 查询是否存在已经绑定过的RFID
		User loginUser = UserUtils.get(inputParam.getUserId());
		
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		
		stoRfidDenomination.setRfidList(inputParam.getRfidList());
		// 执行查询处理
		List<StoRfidDenomination> stoRfidDenominationList = stoRfidDenominationService
				.findRFIDList(stoRfidDenomination);
		// 已绑定RFID列表
		List<String> updateRfidList = Lists.newArrayList();
		// 错误RFID列表
		List<String> errorRfidList = Lists.newArrayList();
		// 如果查询结果不为空的场合
		if (stoRfidDenominationList != null && !stoRfidDenominationList.isEmpty()) {
			// 默认绑定的场合，存在已经绑定RFID的场合，返回错误信息；
			for (StoRfidDenomination rfidDenomination : stoRfidDenominationList) {
				updateRfidList.add(rfidDenomination.getRfid());
				// RFID号已经被使用的场合，不能重新绑定
				if (StoreConstant.RfidUseFlag.use.equals(rfidDenomination.getUseFlag())) {
					errorRfidList.add(rfidDenomination.getRfid());
				}
			}

			// RFID已经成功入库的场合，不能重新绑定
			if (!errorRfidList.isEmpty()) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put("errorMsg",
						"RFID已经入库，不能重新绑定:" + Collections3.convertToString(errorRfidList, Constant.Punctuation.COMMA));
				return gson.toJson(respMap);
			}
			// 2016/11/02 删除 结束
			if (HardwareConstant.ReBindingFlag.binding.equals(inputParam.getReBindingFlag())) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E99);
				respMap.put("errorMsg", "RFID已经存在绑定信息，是否重新绑定:"
						+ Collections3.convertToString(updateRfidList, Constant.Punctuation.COMMA));

				return gson.toJson(respMap);
			}
		}
		// 2016/11/02 删除 开始
//		//删除rfid原有绑定关系
//		for(String rfid:updateRfidList)
//		{
//			stoRfidDenominationService.deleteByPrimaryKey(rfid);
//		}
		
//		// 删除流水单号 已绑卡
//		stoRfidDenominationService.deleteByAllID(inputParam.getAllId());
		
		// 检索流水单号已经绑定的rfid卡信息
		stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setAllId(inputParam.getAllId());
		List<StoRfidDenomination> rfidBindingList = stoRfidDenominationService.pdaFindList(stoRfidDenomination);
		// 解除流水单号绑定
		for (StoRfidDenomination item : rfidBindingList) {
			if (!inputParam.getRfidList().contains(item.getRfid())) {
		        // 解除rfid流水单号绑定
				item.setUpdateBy(loginUser);
				item.setUpdateDate(new Date());
				stoRfidDenominationService.unbindingAllIdByRfid(item);
				// 将待解除绑定RFID记录表导入历史表
		        stoRfidDenominationService.insertInToHistory(item.getRfid(), loginUser.getOffice());
			}
		}
		
		// 根据绑卡业务类型 指定当前rfid卡所在位置
		String rfidPosition = "";
		if (AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(bussnessType)) {
			rfidPosition = Constant.BoxStatus.BUSSNESS_BANK;
		} else if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(bussnessType)
				|| AllocationConstant.BusinessType.PBOC_RE_COUNTING.equals(bussnessType)) {
			rfidPosition = Constant.BoxStatus.CLASSFICATION;
		} else if (AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(bussnessType)) {
			rfidPosition = Constant.BoxStatus.COFFER;
		}
		// 插入新邦卡信息
		for (StoRfidDenomination item : inputParam.getRfidDenominationList()) {
			item.setBusinessType(pbocAllAllocateInfo.getBusinessType());
			if (AllocationConstant.BusinessType.PBOC_ALLOCATION_IN_STORE.equals(bussnessType)) {
				item.setOfficeId(pbocAllAllocateInfo.getAoffice().getId());
				item.setOfficeName(pbocAllAllocateInfo.getAoffice().getName());
			} else {
				item.setOfficeId(pbocAllAllocateInfo.getRoffice().getId());
				item.setOfficeName(pbocAllAllocateInfo.getRoffice().getName());
			}
			
			// 设定当前rfid所处机构
			item.setAtOfficeId(loginUser.getOffice().getId());
			item.setAtOfficeName(loginUser.getOffice().getName());
			
			item.setUseFlag(StoreConstant.RfidUseFlag.init);
			item.setAllId(pbocAllAllocateInfo.getAllId());
			
			item.setBoxStatus(rfidPosition);
			
			item.setCreateBy(loginUser);
			item.setCreateDate(new Date());

			item.setUpdateBy(loginUser);
			item.setUpdateDate(new Date());

			StoRfidDenomination checkItem = stoRfidDenominationService.get(item);
			if (checkItem == null) {
				
				stoRfidDenominationService.insert(item);
				// 将RFID当前邦定记录表导入历史表
		        stoRfidDenominationService.insertInToHistory(item.getRfid(), loginUser.getOffice());
			} else {
				// 如果未拆包，初始邦卡机构不变
				item.setOfficeId(null);
				item.setOfficeName(null);
				
				stoRfidDenominationService.update(item);
				// 将RFID当前邦定记录表导入历史表
		        stoRfidDenominationService.insertInToHistory(item.getRfid(), loginUser.getOffice());
			}
			
		}

		respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);

		return gson.toJson(respMap);
	}
	
	/**
	 * @author caixiaojie
	 * @version 2016年8月30日 取得流水单号邦定RFID接口的输入参数
	 * 
	 * @param requestMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private StoRfidEntity getBindingRfidBySNOParam(Map<String, Object> requestMap) {
		StoRfidEntity inputParam = new StoRfidEntity();

		// 取得流水单号
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("serialorderNo")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：serialorderNo 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setAllId(StringUtils.toString(requestMap.get("serialorderNo")));
		}

		// 取得再次绑定标识
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("reBindingFlag")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：reBindingFlag 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setReBindingFlag(StringUtils.toString(requestMap.get("reBindingFlag")));
		}

		// 取得用户ID
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("userId")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：userId 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setUserId(StringUtils.toString(requestMap.get("userId")));
		}

		// 取得用户名称
		if (StringUtils.isBlank(StringUtils.toString(requestMap.get("userName")))) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：userName 不存在或是空。");
			return inputParam;
		} else {
			inputParam.setUserName(StringUtils.toString(requestMap.get("userName")));
		}

		// 取得RFID列表
		List<Map<String, String>> inputBoxList = Lists.newArrayList();
		if (null == requestMap.get("rfidList")) {
			inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.warn("输入参数错误：rfidList 不存在。");
			return inputParam;
		} else {
			inputBoxList = (List<Map<String, String>>) requestMap.get("rfidList");
		}

		List<StoRfidDenomination> rfidDenominationList = Lists.newArrayList();
		List<String> rfidList = Lists.newArrayList();
		for (Map<String, String> boxMap : inputBoxList) {

			if (null == boxMap) {
				break;
			}
			StoRfidDenomination rfid = new StoRfidDenomination();
			if (StringUtils.isBlank(StringUtils.toString(boxMap.get(Parameter.RFID_KEY)))) {
				inputParam.setErrorFlag(ExternalConstant.HardwareInterface.ERROR_NO_E03);
				logger.warn("输入参数错误：rfid 不存在。");
				return inputParam;
			} else {
				rfid.setRfid(boxMap.get(Parameter.RFID_KEY));
				rfid.setGoodsId(boxMap.get(Parameter.GOODS_ID_KEY));

				rfidList.add(boxMap.get(Parameter.RFID_KEY));
			}
			rfidDenominationList.add(rfid);
		}
		inputParam.setRfidDenominationList(rfidDenominationList);
		inputParam.setRfidList(rfidList);

		return inputParam;
	}
}
