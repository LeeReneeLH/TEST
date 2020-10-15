package com.coffer.external.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.service.StoGoodsLocationInfoService;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.coffer.external.hessian.HardwardBaseService;
import com.coffer.external.hessian.HardwareConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Title: Service66
 * <p>
 * Description: PDA换卡提交接口
 * </p>
 * 
 * @author wangbaozhong
 * @date 2017年7月6日 上午10:41:10
 */
@Component("Service66")
@Scope("singleton")
public class Service66 extends HardwardBaseService {

	@Autowired
	private StoRfidDenominationService stoRfidDenominationService;

	@Autowired
	private StoGoodsLocationInfoService stoGoodsLocationInfoService;

	/**
	 * @author cai xiaojie
	 * @version 2016年9月9日 66：PDA换卡提交接口
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

		// 验证原rfid号
		String srcRfid = StringUtils.toString(paramMap.get("srcRfid"));
		if (StringUtils.isBlank(srcRfid) || HardwareConstant.CardLength.rfid != srcRfid.length()) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
		}

		// 验证新rfid号
		String dstRfid = StringUtils.toString(paramMap.get("dstRfid"));
		if (StringUtils.isBlank(dstRfid) || HardwareConstant.CardLength.rfid != dstRfid.length()) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
		}

		// 验证用户编号
		String userId = StringUtils.toString(paramMap.get("userId"));
		if (StringUtils.isBlank(userId)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
		}

		// 验证用户姓名
		String userName = StringUtils.toString(paramMap.get("userName"));
		if (StringUtils.isBlank(userName)) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E03);
			return gson.toJson(respMap);
		}

		// 验证旧卡是否存在
		List<StoRfidDenomination> srcRfidList = stoRfidDenominationService.findListByBoxNo(srcRfid);
		if (srcRfidList.size() <= 0) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E49);
			return gson.toJson(respMap);
		}

		// 验证新卡是否存在
		List<StoRfidDenomination> dstRfidList = stoRfidDenominationService.findListByBoxNo(dstRfid);
		if (dstRfidList.size() > 0) {
			respMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			respMap.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E50);
			respMap.put(Parameter.ERROR_MSG_KEY, "新卡已存在！");
			return gson.toJson(respMap);
		}

		User currUser = UserUtils.get(userId);
		// 更新rfid绑定信息表
		for (StoRfidDenomination entity : srcRfidList) {
			// 修改原rfid状态
			String oldDelFlag = entity.getDelFlag();
			StoRfidDenomination updateEntity = new StoRfidDenomination();
			updateEntity.setRfid(entity.getRfid());
			updateEntity.setDelFlag(Constant.deleteFlag.Invalid);
			updateEntity.setUseFlag(StoreConstant.RfidUseFlag.rfidReplace);
			updateEntity.setDestRfid(dstRfid);
			updateEntity.setUpdateBy(currUser);
			updateEntity.setUpdateDate(new Date());
			stoRfidDenominationService.update(updateEntity);
			// 保存换卡前rfid信息至历史表
			Office rfidAtOffice = SysCommonUtils.findOfficeById(entity.getAtOfficeId());
			stoRfidDenominationService.insertInToHistory(entity.getRfid(), rfidAtOffice);
			// 插入新rfid信息
			entity.setDestRfid("");
			entity.setRfid(dstRfid);
			entity.setDelFlag(oldDelFlag);
			entity.setCreateBy(currUser);
			entity.setCreateDate(new Date());
			entity.setUpdateBy(currUser);
			entity.setUpdateDate(new Date());
			entity.setIsNewRecord(true);
			stoRfidDenominationService.save(entity);
			stoRfidDenominationService.insertInToHistory(dstRfid, rfidAtOffice);
		}

		// 更新物品位置信息
		StoGoodsLocationInfo stoGoodsLocation = new StoGoodsLocationInfo();
		stoGoodsLocation.setRfid(srcRfid);
		stoGoodsLocation.setOfficeId(currUser.getOffice().getId());
		
		// UPDATE-START  原因：增加查询条件 update by wangbaozhong  2018/05/23
		//stoGoodsLocation.setDelFlag(null);
		List<String> statusFlagList = Lists.newArrayList();
        statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_UNUSED);
        statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_RESERVED);
        statusFlagList.add(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_USED);
        stoGoodsLocation.setStatusFlagList(statusFlagList);
        // UPDATE-END  原因：增加查询条件  update by wangbaozhong  2018/05/23
        
		List<StoGoodsLocationInfo> stoGoodsLocationList = stoGoodsLocationInfoService.findList(stoGoodsLocation);
		// ADD-START  原因：增加条件判断  add by wangbaozhong  2018/05/23
		if (stoGoodsLocationList.size() == 0) {
			throw new BusinessException(null, "原RFID（" + StringUtils.left(srcRfid, 8) 
			+ "）信息不存在！:" + StringUtils.left(srcRfid, 8));
		}
		// ADD-END  原因：增加条件判断  add by wangbaozhong  2018/05/23
		
		for (StoGoodsLocationInfo entity : stoGoodsLocationList) {
			// 修改原rfid物品位置信息
			String oldDelFlag = entity.getDelFlag();
			entity.setDelFlag(StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_REPLACE);
			entity.setUpdateBy(currUser);
			entity.setUpdateDate(new Date());
			entity.setIsNewRecord(false);
			stoGoodsLocationInfoService.save(entity);

			// 插入新rfid物品位置信息
			entity.setId(IdGen.uuid());
			entity.setRfid(dstRfid);
			entity.setDelFlag(oldDelFlag);
			entity.setCreateBy(currUser);
			entity.setCreateDate(new Date());
			entity.setIsNewRecord(true);
			stoGoodsLocationInfoService.save(entity);
		}
		respMap.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return gson.toJson(respMap);
	}

}
