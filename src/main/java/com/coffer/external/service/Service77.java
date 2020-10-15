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
import com.coffer.businesses.modules.store.v01.entity.StoBoxDetail;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoreGoodsDetail;
import com.coffer.businesses.modules.store.v01.entity.StoreGoodsInfo;
import com.coffer.businesses.modules.store.v01.entity.StoreManagementInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoreGoodsInfoService;
import com.coffer.businesses.modules.store.v01.service.StoreManagementInfoService;
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
 * Title: Service77
 * <p>
 * Description: 上传库房物品信息(PDA)
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年8月10日 上午09:01:10
 */
@Component("Service77")
@Scope("singleton")
public class Service77 extends HardwardBaseService {

	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	
	@Autowired
	private StoreManagementInfoService storeManagementInfoService;
	
	@Autowired
	private StoreGoodsInfoService storeGoodsInfoService;
	
	@Override
	@Transactional(readOnly = false)
	public String execute(Map<String, Object> paramMap) {
		logger.debug(this.getClass().getName() + "上传库房物品信息(PDA) -----------------开始");
		Map<String, Object> respMap = Maps.newHashMap();
		// 版本号、服务代码
		respMap.put(Parameter.VERSION_NO_KEY, paramMap.get(Parameter.VERSION_NO_KEY));
		respMap.put(Parameter.SERVICE_NO_KEY, paramMap.get(Parameter.SERVICE_NO_KEY));
		
		List<StoreGoodsInfo> goodsInfoList = getParam(paramMap, respMap);

		if (goodsInfoList == null) {
			return gson.toJson(respMap);
		}

		// 保存数据
		storeGoodsInfoService.saveGoods(goodsInfoList);

		respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			
		logger.debug(this.getClass().getName() + "上传库房物品信息(PDA) -----------------结束");
		return gson.toJson(respMap);
	}
	
	/**
	 * 
	 * Title: getParam
	 * <p>Description: 获取并验证参数信息</p>
	 * @author:     wangbaozhong
	 * @param paramMap 参数信息
	 * @return 保存物品列表
	 * List<StoreGoodsInfo>    返回类型
	 */
	@SuppressWarnings("unchecked")
	private List<StoreGoodsInfo> getParam(Map<String, Object> paramMap, Map<String, Object> respMap) {
		
		String storeId = StringUtils.toString(paramMap.get(Parameter.STORE_ID_KEY));
		String constraintFlag = StringUtils.toString(paramMap.get(Parameter.CONSTRAINT_KEY));
		if (StringUtils.isBlank(storeId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 库房ID 为空");
			return null;
		}
		
		String userId = StringUtils.toString(paramMap.get(Parameter.USER_ID_KEY));
		if (StringUtils.isBlank(userId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 操作用户ID 为空");
			return null;
		}
		
		if (paramMap.get(Parameter.LIST_KEY) == null) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 物品列表 为空");
			return null;
		}
		
		List<Map<String, Object>> mapList = (List<Map<String, Object>>)paramMap.get(Parameter.LIST_KEY);
		if (Collections3.isEmpty(mapList)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			logger.debug(this.getClass().getName() + "ERROR : 物品列表 为空");
			return null;
		}
		// 获取库房信息
		StoreManagementInfo storeManagementInfo = storeManagementInfoService.get(storeId);
		// 库房无效判断
		if (Constant.deleteFlag.Invalid.equals(storeManagementInfo.getDelFlag())) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E56);
			logger.debug(this.getClass().getName() + "ERROR : " + storeManagementInfo.getStoreName() + "无效");
			return null;
		}
		
		List<Office> oldOfficeList = Collections3.extractToList(storeManagementInfo.getStoreCoOfficeAssocationList(), "office");
		storeManagementInfo.setOfficeIdList(Collections3.extractToList(oldOfficeList, "id"));
		storeManagementInfo.setBoxTypeList(Collections3.extractToList(storeManagementInfo.getStoreTypeAssocationList(), "storageType"));
		List<User> userList = Collections3.extractToList(storeManagementInfo.getStoreManagerAssocationList(), "user");
		storeManagementInfo.setUserIdList(Collections3.extractToList(userList, "id"));
		
		// 库管员检查
		if (!Collections3.isEmpty(storeManagementInfo.getUserIdList()) && !storeManagementInfo.getUserIdList().contains(userId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			// 用户操作所选金库权限不足
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E52);
			logger.debug(this.getClass().getName() + "ERROR : 物品列表 为空");
			return null;
		}
		
		User operator = UserUtils.get(userId);
		
		List<StoreGoodsInfo> rtnList =  Lists.newArrayList();
		List<Map<String, Object>> errorMapList = Lists.newArrayList();
		Map<String, Object> errorMap = null;
		String boxNo = null;
		String rfid = null;
		StoBoxInfo paramBoxInfo = new StoBoxInfo();
		StoBoxInfo boxInfoResult = null;
		List<StoreGoodsDetail> detailList = null;
		// 设定有效
		paramBoxInfo.setDelFlag(Constant.deleteFlag.Valid);
		for (Map<String, Object> map : mapList) {
			boxNo = StringUtils.toString(map.get(Parameter.BOX_NO_KEY));
			if (StringUtils.isBlank(boxNo)) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				logger.debug(this.getClass().getName() + "ERROR : 箱号 为空");
				return null;
			}
			
			rfid = StringUtils.toString(map.get(Parameter.RFID_KEY));
			if (StringUtils.isBlank(rfid)) {
				respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
				respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
				logger.debug(this.getClass().getName() + "ERROR : RFID 为空");
				return null;
			}
			
			paramBoxInfo.setId(boxNo);
			paramBoxInfo.setRfid(rfid);
			
			boxInfoResult = stoBoxInfoService.getBoxInfoByRfidAndBoxNo(paramBoxInfo);
			
			if (boxInfoResult == null) {
				errorMap = Maps.newHashMap();
				errorMap.put(Parameter.BOX_NO_KEY, boxNo);
				errorMap.put(Parameter.RFID_KEY, rfid);
				// message.E1062=[验证失败]箱号{0}， RFID{1}对应箱袋明细信息不存在！
				String message = msg.getMessage("message.E1062", new String[] {boxNo, rfid}, locale);
//				errorMap.put(Parameter.ERROR_MSG_KEY, message);
				errorMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E55);
				logger.debug(this.getClass().getName() + "ERROR :" + message);
				errorMapList.add(errorMap);
				continue;
			}
			// 款项时验证箱袋明细
			if (!Constant.BoxType.BOX_TAIL.equals(boxInfoResult.getBoxType()) && boxInfoResult.getStoBoxDetail().size() == 0) {
				errorMap = Maps.newHashMap();
				errorMap.put(Parameter.BOX_NO_KEY, boxNo);
				errorMap.put(Parameter.RFID_KEY, rfid);
				// message.E1062=[验证失败]箱号{0}， RFID{1}对应箱袋明细信息不存在！
				String message = msg.getMessage("message.E1062", new String[] {boxNo, rfid}, locale);
//				errorMap.put(Parameter.ERROR_MSG_KEY, message);
				errorMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E55);
				logger.debug(this.getClass().getName() + "ERROR :" + message);
				errorMapList.add(errorMap);
				continue;
			}
			
			// 库房箱袋类型检查
			if (!Collections3.isEmpty(storeManagementInfo.getBoxTypeList()) 
					&& (!storeManagementInfo.getBoxTypeList().contains(boxInfoResult.getBoxType()) 
					&& !ExternalConstant.ConstraintFlag.CONSTRAINT_VALID.equals(constraintFlag))) {
				errorMap = Maps.newHashMap();
				errorMap.put(Parameter.BOX_NO_KEY, boxNo);
				errorMap.put(Parameter.RFID_KEY, rfid);
				// message.E1063=[验证失败]箱号:{0}， RFID:{1}对应箱袋类型不在库房【{2}】保存范围内， 请稍后再试或与系统管理员联系！
				String message = msg.getMessage("message.E1063", 
						new String[] {boxNo, rfid, storeManagementInfo.getStoreName()}, locale);
//				errorMap.put(Parameter.ERROR_MSG_KEY, message);
				// 箱袋类型不在库房设定范围内
				errorMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E53);
				logger.debug(this.getClass().getName() + "ERROR :" + message);
				errorMapList.add(errorMap);
				continue;
			}
			
			// 库房保存机构物品检查
			if (!Collections3.isEmpty(storeManagementInfo.getOfficeIdList()) 
					&& (!storeManagementInfo.getOfficeIdList().contains(boxInfoResult.getOffice().getId())
					&& !ExternalConstant.ConstraintFlag.CONSTRAINT_VALID.equals(constraintFlag))) {
				errorMap = Maps.newHashMap();
				errorMap.put(Parameter.BOX_NO_KEY, boxNo);
				errorMap.put(Parameter.RFID_KEY, rfid);
				// message.E1064=[验证失败]箱号:{0}， RFID:{1}对应箱袋所属机构【{2}】不在库房【{3}】保存范围内， 请稍后再试或与系统管理员联系！
				String message = msg.getMessage("message.E1064", 
						new String[] {boxNo, rfid, boxInfoResult.getOffice().getName(),
								storeManagementInfo.getStoreName()}, locale);
//				errorMap.put(Parameter.ERROR_MSG_KEY, message);
				// 箱袋所属机构不在库房设定范围内
				errorMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E54);
				logger.debug(this.getClass().getName() + "ERROR :" + message);
				errorMapList.add(errorMap);
				continue;
			}
			
			StoreGoodsInfo goodsInfo = new StoreGoodsInfo();
			goodsInfo.setCreateBy(operator);
			goodsInfo.setUpdateBy(operator);
			goodsInfo.setStoreId(storeId);
			goodsInfo.setBoxNo(boxNo); // 箱号
			goodsInfo.setRfid(rfid);	//RFID
			goodsInfo.setBoxType(boxInfoResult.getBoxType());	// 箱子类型
			goodsInfo.setAmount(boxInfoResult.getBoxAmount());	// 箱子总金额
			goodsInfo.setOffice(boxInfoResult.getOffice());		// 箱子所属机构
			goodsInfo.setOutDate(boxInfoResult.getOutDate());	// 尾箱预约出库日期
			goodsInfo.setInStoreDate(new Date());				// 入库日期
			goodsInfo.setDelFlag(Constant.deleteFlag.Valid);	// 设定有效标识
			detailList = Lists.newArrayList();
			for (StoBoxDetail boxDetail : boxInfoResult.getStoBoxDetail()) {
				StoreGoodsDetail goodsDetail = new StoreGoodsDetail();
				goodsDetail.setGoodsId(boxDetail.getGoodsId());	// 物品ID
				goodsDetail.setGoodsNum(new BigDecimal(boxDetail.getGoodsNum())); // 物品数量
				goodsDetail.setAmount(new BigDecimal(boxDetail.getGoodsAmount()));	// 物品金额
				
				detailList.add(goodsDetail);
			}
			
			goodsInfo.setStoreGoodsDetailList(detailList);
			
			rtnList.add(goodsInfo);
		}
		
		if (errorMapList.size() > 0) {
			respMap.put(Parameter.LIST_KEY, errorMapList);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			return null;
		}
		
		return rtnList;
	}
	

}
