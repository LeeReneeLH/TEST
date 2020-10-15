package com.coffer.businesses.modules.store.v01.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.dao.StoRfidDenominationDao;
import com.coffer.businesses.modules.store.v01.dao.StoRfidDenominationHistoryDao;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenominationHistory;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * RFID面值绑定Service
 * 
 * @author chengshu
 * @version 2016-05-31
 */
@Service
@Transactional(readOnly = true)
public class StoRfidDenominationService extends CrudService<StoRfidDenominationDao, StoRfidDenomination> {
	
	@Autowired
	private StoRfidDenominationHistoryDao stoRfidDenominationHistoryDao;

	/**
	 * @author chengshu
	 * @version 2016-05-31
	 * 
	 * @Description 取得RFID的面值信息
	 * @return
	 */
	public List<StoRfidDenomination> findRFIDList(StoRfidDenomination inputParam) {

		// 设置查询条件
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfidList(inputParam.getRfidList());
		stoRfidDenomination.setOfficeId(inputParam.getOfficeId());
		// ADD-START  原因：按参数设定  add by wangbaozhong  2018/05/23
		stoRfidDenomination.setDelFlag(inputParam.getDelFlag());
		// ADD-END  原因：按参数设定  add by wangbaozhong  2018/05/23
		// 如果没有指定delflag,默认设置成有效
		// DELETE-START  原因：按参数设定  delete by wangbaozhong  2018/05/23
//		if (StringUtils.isBlank(stoRfidDenomination.getDelFlag())) {
//			stoRfidDenomination.setDelFlag(Constant.deleteFlag.Valid);
//		}
		// DELETE-END  原因：按参数设定  delete by wangbaozhong  2018/05/23
		return dao.findRFIDList(stoRfidDenomination);
	}

	/**
	 * 
	 * Title: findUnUsedListByOfficeId
	 * <p>
	 * Description: 根据机构ID和业务类型 查询未使用待入库的RFID信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param allId
	 *            流水单号
	 * @return RFID信息列表 List<StoRfidDenomination> 返回类型
	 */
	public List<StoRfidDenomination> findUnUsedListByOfficeId(String allId) {
		// 设置查询条件
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setDelFlag(Constant.deleteFlag.Valid);
		stoRfidDenomination.setAllId(allId);
		return dao.findUnUsedListByOfficeId(stoRfidDenomination);
	}
	
	/**
	 * 
	 * Title: findUsedListByOfficeId
	 * <p>Description: 根据机构ID和业务类型 查询已使用入库的RFID信息</p>
	 * @author:     yanbingxu
	 * @param allId
	 * @return 
	 * List<StoRfidDenomination>    返回类型
	 */
	public List<StoRfidDenomination> findUsedListByOfficeId(String allId) {
		// 设置查询条件
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setDelFlag(Constant.deleteFlag.Valid);
		stoRfidDenomination.setAllId(allId);
		return dao.findUsedListByOfficeId(stoRfidDenomination);
	}

	/**
	 * @author chengshu
	 * @version 2016-05-31
	 * 
	 * @Description 取得RFID的面值信息，和商行所属人民银行信息
	 * @return
	 */
	public List<StoRfidDenomination> findListWithStore(StoRfidDenomination inputParam) {

		// 设置查询条件
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfidList(inputParam.getRfidList());

		// 如果没有指定delflag,默认设置成有效
		if (StringUtils.isBlank(stoRfidDenomination.getDelFlag())) {
			stoRfidDenomination.setDelFlag(Constant.deleteFlag.Valid);
		}

		return dao.findListWithStore(stoRfidDenomination);
	}

	/**
	 * @author chengshu
	 * @version 2016-05-31
	 * 
	 * @Description 插入绑定信息
	 * @return
	 */
	@Transactional(readOnly = false)
	public void insert(StoRfidDenomination stoRfidDenomination) {
		// 设置默认删除表示为有效
		if (StringUtils.isBlank(stoRfidDenomination.getDelFlag())) {
			stoRfidDenomination.setDelFlag(Constant.deleteFlag.Valid);
		}

		// 数据库插入处理
		dao.insert(stoRfidDenomination);
	}

	/**
	 * 
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          执行插入处理：RFID与面值的绑定信息
	 * @param inputParam
	 *            输入参数
	 * @return
	 */
	@Transactional(readOnly = false)
	public void insertRfidDenomination(StoRfidDenomination inputParam) {

		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();

		for (String rfid : inputParam.getRfidList()) {
			stoRfidDenomination = new StoRfidDenomination();
			stoRfidDenomination.setRfid(rfid);
			stoRfidDenomination.setBoxStatus(Constant.BoxStatus.BANK_OUTLETS);
			stoRfidDenomination.setOfficeId(inputParam.getOfficeId());
			stoRfidDenomination.setOfficeName(inputParam.getOfficeName());
			stoRfidDenomination.setDenomination(inputParam.getDenomination());
			stoRfidDenomination.setGoodsId(inputParam.getGoodsId());
			stoRfidDenomination.setUseFlag(StoreConstant.RfidUseFlag.init);
			// 设定业务类型
			stoRfidDenomination.setBusinessType(inputParam.getBusinessType());

			User createuser = new User();
			createuser.setId(inputParam.getUserId());
			createuser.setName(inputParam.getUserName());
			stoRfidDenomination.setCreateBy(createuser);
			stoRfidDenomination.setCreateDate(new Date());

			User updateuser = new User();
			updateuser.setId(inputParam.getUserId());
			updateuser.setName(inputParam.getUserName());
			stoRfidDenomination.setUpdateBy(updateuser);
			stoRfidDenomination.setUpdateDate(new Date());

			stoRfidDenomination.setAtOfficeId(inputParam.getOfficeId());
			stoRfidDenomination.setAtOfficeName(inputParam.getOfficeName());
			
			insert(stoRfidDenomination);
		}
	}

	/**
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          批量更新RFID的面值/机构等绑定信息
	 * @param errorRfidList
	 *            重新绑定rfid列表
	 * @return
	 */
	@Transactional(readOnly = false)
	public int updateList(List<String> reBindingRfidList, StoRfidDenomination inputParam) {

		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfidList(reBindingRfidList);

		stoRfidDenomination.setOfficeId(inputParam.getOfficeId());
		stoRfidDenomination.setOfficeName(inputParam.getOfficeName());
		stoRfidDenomination.setDenomination(inputParam.getDenomination());
		stoRfidDenomination.setGoodsId(inputParam.getGoodsId());
		// 设定业务类型
		stoRfidDenomination.setBusinessType(inputParam.getBusinessType());

		User updateuser = new User();
		updateuser.setId(inputParam.getUserId());
		updateuser.setName(inputParam.getUserName());
		stoRfidDenomination.setUpdateBy(updateuser);
		stoRfidDenomination.setUpdateDate(new Date());

		return this.updateRfidDenominationByRfidList(stoRfidDenomination);
	}

	/**
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          批量更新RFID状态
	 * @param rfidList
	 *            rfid列表
	 * @param status
	 *            状态
	 * @param curBusinessType 当前业务类型
	 * @return
	 */
	public int updateRfidStatus(List<String> rfidList, String status, String userId, String userName, String curBusinessType) {
		if (Collections3.isEmpty(rfidList)) {
			return 0;
		}
		
		Office atOffice = UserUtils.get(userId).getOffice();
		
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfidList(rfidList);
		stoRfidDenomination.setBoxStatus(status);
		stoRfidDenomination.setUseFlag(StoreConstant.RfidUseFlag.use);

		stoRfidDenomination.setAtOfficeId(atOffice.getId());
		stoRfidDenomination.setAtOfficeName(atOffice.getName());
		
		User updateuser = new User();
		updateuser.setId(userId);
		updateuser.setName(userName);
		stoRfidDenomination.setUpdateBy(updateuser);
		stoRfidDenomination.setUpdateDate(new Date());

		stoRfidDenomination.setBusinessType(curBusinessType);
		
		return this.updateRfidDenominationByRfidList(stoRfidDenomination);
	}
	
	/**
	 * 
	 * Title: updateRfidDenominationByRfidList
	 * <p>Description: 由于SQL IN 关键字限制最多能容纳1000个参数，因此分批执行更新</p>
	 * @author:     wangbaozhong
	 * @param stoRfidDenomination 参数
	 * @return 更新数量
	 * int    返回类型
	 */
	@Transactional(readOnly = false)
	public int updateRfidDenominationByRfidList(StoRfidDenomination stoRfidDenomination) {
		List<String> rfidList = stoRfidDenomination.getRfidList();
		List<String> rfidUpdateList = null;
		int iListSize = rfidList.size();
		// 截取RFID列表，执行分批更新
		Double splitCnt = Math.ceil(Double.parseDouble(Integer.toString(iListSize)) 
				/ Double.parseDouble(Integer.toString(Constant.NUMBER_PER_UPDATE)));
		int iMaxCnt = splitCnt.intValue();
		int iFromIndex = 0;
		int iToIndex = 0;
		int iUpdateCnt = 0;
		for (int splitIndex = 0; splitIndex < iMaxCnt; splitIndex++) {
			if (splitIndex > 0) {
				iFromIndex = splitIndex * Constant.NUMBER_PER_UPDATE;
			} 
			
			iToIndex = iFromIndex + Constant.NUMBER_PER_UPDATE;
			iToIndex = iToIndex > iListSize ? iListSize : iToIndex;
			
			rfidUpdateList = rfidList.subList(iFromIndex, iToIndex);
			stoRfidDenomination.setRfidList(rfidUpdateList);
			iUpdateCnt += dao.updateList(stoRfidDenomination);
		}
		
		return iUpdateCnt;
	}
	
	/**
	 * 
	 * Title: updateRfidDenByRfidAndAtOffice
	 * <p>Description: 根据RFID及当前所在机构更新rifd表信息 由于SQL IN 关键字限制最多能容纳1000个参数，因此分批执行更新</p>
	 * @author:     wangbaozhong
	 * @param stoRfidDenomination 参数
	 * @return 更新数量
	 * int    返回类型
	 */
	@Transactional(readOnly = false)
	public int updateRfidDenByRfidAndAtOffice(StoRfidDenomination stoRfidDenomination) {
		List<String> rfidList = stoRfidDenomination.getRfidList();
		List<String> rfidUpdateList = null;
		int iListSize = rfidList.size();
		// 截取RFID列表，执行分批更新
		Double splitCnt = Math.ceil(Double.parseDouble(Integer.toString(iListSize)) 
				/ Double.parseDouble(Integer.toString(Constant.NUMBER_PER_UPDATE)));
		int iMaxCnt = splitCnt.intValue();
		int iFromIndex = 0;
		int iToIndex = 0;
		int iUpdateCnt = 0;
		for (int splitIndex = 0; splitIndex < iMaxCnt; splitIndex++) {
			if (splitIndex > 0) {
				iFromIndex = splitIndex * Constant.NUMBER_PER_UPDATE;
			} 
			
			iToIndex = iFromIndex + Constant.NUMBER_PER_UPDATE;
			iToIndex = iToIndex > iListSize ? iListSize : iToIndex;
			
			rfidUpdateList = rfidList.subList(iFromIndex, iToIndex);
			stoRfidDenomination.setRfidList(rfidUpdateList);
			iUpdateCnt += dao.updateRfidListByAtOffice(stoRfidDenomination);
		}
		
		return iUpdateCnt;
	}
	
	/**
	 * 
	 * Title: updateRfidOutStoreStatus
	 * <p>Description: 批量更新RFID出库状态</p>
	 * @author:     wangbaozhong
	 * @param rfidList	rfid列表
	 * @param status	状态
	 * @param userId	用户ID
	 * @param userName	用户名称
	 * @param rfidUseFlag	rfid使用状态
	 * @param atOffice	RFID当前所在机构
	 * @param curBusinessType	当前业务类型
	 * @return 
	 * int    返回类型
	 */
	@Transactional(readOnly = false)
	public int updateRfidStatus(List<String> rfidList, String status, String userId, String userName, 
			String rfidUseFlag, Office atOffice, String curBusinessType) {
		if (Collections3.isEmpty(rfidList)) {
			return 0;
		}
		
		User loginUser = UserUtils.get(userId);
		
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfidList(rfidList);
		stoRfidDenomination.setBoxStatus(status);
		stoRfidDenomination.setUseFlag(rfidUseFlag);

		stoRfidDenomination.setAtOfficeId(atOffice.getId());
		stoRfidDenomination.setAtOfficeName(atOffice.getName());
		
		User updateuser = new User();
		updateuser.setId(userId);
		updateuser.setName(loginUser.getName());
		stoRfidDenomination.setUpdateBy(updateuser);
		stoRfidDenomination.setUpdateDate(new Date());
		
		stoRfidDenomination.setBusinessType(curBusinessType);

		return this.updateRfidDenominationByRfidList(stoRfidDenomination);
	}
	
	/**
	 * 
	 * Title: updateRfidOutStoreStatus
	 * <p>Description: 批量更新RFID出库状态(清除库区用)</p>
	 * @author:     wangbaozhong
	 * @param rfidList	rfid列表
	 * @param status	状态
	 * @param userId	用户ID
	 * @param userName	用户名称
	 * @param rfidUseFlag	rfid使用状态
	 * @param atOffice	RFID当前所在机构
	 * @param curBusinessType	当前业务类型
	 * @return 
	 * int    返回类型
	 */
	@Transactional(readOnly = false)
	public int updateRfidStatusForClear(List<String> rfidList, String status, String userId, String userName, 
			String rfidUseFlag, Office atOffice, String curBusinessType) {
		if (Collections3.isEmpty(rfidList)) {
			return 0;
		}
		
		User loginUser = UserUtils.get(userId);
		
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfidList(rfidList);
		stoRfidDenomination.setBoxStatus(status);
		stoRfidDenomination.setUseFlag(rfidUseFlag);

		stoRfidDenomination.setAtOfficeId(atOffice.getId());
		stoRfidDenomination.setAtOfficeName(atOffice.getName());
		
		User updateuser = new User();
		updateuser.setId(userId);
		updateuser.setName(loginUser.getName());
		stoRfidDenomination.setUpdateBy(updateuser);
		stoRfidDenomination.setUpdateDate(new Date());
		
		stoRfidDenomination.setBusinessType(curBusinessType);

		return updateRfidDenByRfidAndAtOffice(stoRfidDenomination);
	}

	/**
	 * 
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          执行插入处理：RFID与面值的绑定信息
	 * @param rfidDenominationList
	 *            rfid与面值绑定信息
	 * @param inputParam
	 *            电文输入参数
	 * @return
	 */
	public List<Map<String, Object>> setRfidAndDenominationResult(List<StoRfidDenomination> rfidDenominationList,
			StoRfidDenomination inputParam) {

		String storeOfficeId = inputParam.getOfficeId();
		String inoutType = inputParam.getInoutType();

		List<String> rfidList = inputParam.getRfidList();
		List<String> rfidFindList = Lists.newArrayList();

		Map<String, Object> rfidDenominationMap = Maps.newHashMap();
		List<Map<String, Object>> resultList = Lists.newArrayList();

		Map<String, Office> officeMap = Maps.newHashMap();

		// 已查询到的RFID信息，设置到返回列表里
		for (StoRfidDenomination rfidDenomination : rfidDenominationList) {
			// RFID 在库区清理，被替换，已出库状态时 不应被查询到
			if (StoreConstant.RfidUseFlag.rfidClear.equals(rfidDenomination.getUseFlag())
					|| StoreConstant.RfidUseFlag.rfidReplace.equals(rfidDenomination.getUseFlag())
					|| StoreConstant.RfidUseFlag.outStore.equals(rfidDenomination.getUseFlag())) {
				continue;
			}
			rfidDenominationMap = Maps.newHashMap();
			rfidDenominationMap.put("rfid", rfidDenomination.getRfid());
			rfidDenominationMap.put("goodsId", rfidDenomination.getGoodsId());
			// rfidDenominationMap.put("denomination",
			// rfidDenomination.getDenomination());
			BigDecimal amount = StoreCommonUtils.getGoodsValue(rfidDenomination.getGoodsId());
			// 物品价值
			rfidDenominationMap.put(Parameter.AMOUNT_KEY, amount == null ? "0.0" : amount);
			// 物品名称
			String goodsName = StoreCommonUtils.getGoodsName(rfidDenomination.getGoodsId());
			rfidDenominationMap.put(Parameter.GOODS_NAME_KEY, StringUtils.isBlank(goodsName) ? "" : goodsName);
			rfidDenominationMap.put("officeId", rfidDenomination.getOffice().getId());
			rfidDenominationMap.put("officeName", rfidDenomination.getOffice().getName());
			// 库区类型名称
			String strStoreAreaTypeName = DictUtils.getDictLabel(rfidDenomination.getStoreAreaType(), "store_area_type",
					"");
			rfidDenominationMap.put(Parameter.STORE_AREA_NAME_KEY,
					strStoreAreaTypeName + " " + rfidDenomination.getAreaName());

			// 设定库区类型
			rfidDenominationMap.put(Parameter.STORE_AREA_TYPE_KEY, rfidDenomination.getStoreAreaType());
			// 设定所属业务类型
			rfidDenominationMap.put(Parameter.BUSINESS_TYPE_KEY, rfidDenomination.getBusinessType());
			// 验证RFID所属人民银行
			rfidDenominationMap.put("flag", checkRfidStore(storeOfficeId, rfidDenominationMap, inoutType,
					rfidDenomination.getBoxStatus(), rfidDenomination.getOffice(), officeMap));

			// 保存到查询到的RFID列表里
			rfidFindList.add(rfidDenomination.getRfid());
			resultList.add(rfidDenominationMap);
		}

		// 没有绑定面值的RFID存在的场合
		if (rfidList.size() != rfidDenominationList.size() || resultList.size() != rfidList.size()) {

			for (String rfid : rfidList) {
				if (!rfidFindList.contains(rfid)) {
					StoOriginalBanknote originalBanknote = StoreCommonUtils.getStoOriginalBanknoteByBoxId(rfid,
							inputParam.getOfficeId());
					StoGoodsLocationInfo goodsLocationParam = new StoGoodsLocationInfo();
					goodsLocationParam.setRfid(rfid);
					goodsLocationParam.setDelFlag(null);

					List<StoGoodsLocationInfo> goodsLocationList = StoreCommonUtils
							.getGoodsListFromLocation(goodsLocationParam);
					boolean isGoodsExist = false;
					for (StoGoodsLocationInfo goodsLocationInfo : goodsLocationList) {
						if (isGoodsExist == false && goodsLocationInfo.getRfid().startsWith(rfid)
								&& !StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_USED.equals(goodsLocationInfo.getDelFlag())
								&& !StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_REPLACE.equals(goodsLocationInfo.getDelFlag())
								&& !StoreConstant.StoreAreaGoodsUsedStatus.GOODS_STATUS_CLEAR.equals(goodsLocationInfo.getDelFlag())) {
							isGoodsExist = true;
						}
					}
					if (originalBanknote != null && isGoodsExist == true) {
						rfidDenominationMap = Maps.newHashMap();
						rfidDenominationMap.put("rfid", originalBanknote.getId());
						// 物品ID
						rfidDenominationMap.put("goodsId", originalBanknote.getGoodsId());
						// 物品价值
						rfidDenominationMap.put(Parameter.AMOUNT_KEY, originalBanknote.getAmount());
						// 物品名称
						rfidDenominationMap.put(Parameter.GOODS_NAME_KEY, originalBanknote.getGoodsName());
						rfidDenominationMap.put("officeId", "");
						rfidDenominationMap.put("officeName", "");
						rfidDenominationMap.put("storeId", "");
						rfidDenominationMap.put("storeName", "");
						rfidDenominationMap.put(Parameter.STORE_AREA_NAME_KEY, "");
						// 设定库区类型
						rfidDenominationMap.put(Parameter.STORE_AREA_TYPE_KEY, "");
						// 设定所属业务类型
						rfidDenominationMap.put(Parameter.BUSINESS_TYPE_KEY, "");

						rfidDenominationMap.put("flag", ExternalConstant.RfidErrorFlag.SUCCESS);
						resultList.add(rfidDenominationMap);

						continue;
					}

					rfidDenominationMap = Maps.newHashMap();
					rfidDenominationMap.put("rfid", rfid);
					// rfidDenominationMap.put("denomination", "");
					// 物品价值
					rfidDenominationMap.put(Parameter.AMOUNT_KEY, "");
					// 物品名称
					rfidDenominationMap.put(Parameter.GOODS_NAME_KEY, "");
					rfidDenominationMap.put("officeId", "");
					rfidDenominationMap.put("officeName", "");
					rfidDenominationMap.put("storeId", "");
					rfidDenominationMap.put("storeName", "");
					rfidDenominationMap.put(Parameter.STORE_AREA_NAME_KEY, "");
					// 设定库区类型
					rfidDenominationMap.put(Parameter.STORE_AREA_TYPE_KEY, "");
					// 设定所属业务类型
					rfidDenominationMap.put(Parameter.BUSINESS_TYPE_KEY, "");
					rfidDenominationMap.put("flag", ExternalConstant.RfidErrorFlag.EXIST_ERROR);
					resultList.add(rfidDenominationMap);
				}
			}
		}

		return resultList;
	}

	/**
	 * 
	 * @author chengshu
	 * @version 2016年05月31日
	 * 
	 *          验证RFID所属商业银行是否属于当前人民银行
	 * @param inputStoreOfficeId
	 *            电文传入的当前的人民银行
	 * @param rfidDenominationMap
	 *            RFID所属的人民银行
	 * @param inoutType
	 *            出入库种别
	 * @param status
	 *            RFID状态
	 * @return
	 */
	private String checkRfidStore(String inputStoreOfficeId, Map<String, Object> rfidDenominationMap, String inoutType,
			String status, Office office, Map<String, Office> officeMap) {

		String result = ExternalConstant.RfidErrorFlag.SUCCESS;
		Office storeOffice;

		// 入库的场合，验证商业银行是否属于当前人民银行
		if (Constant.InoutType.In.equals(inoutType)) {

			// 取得所属人民银行
			if (!officeMap.containsKey(office.getId())) {
				storeOffice = BusinessUtils.getPbocCenterByOffice(office);
				officeMap.put(office.getId(), storeOffice);

			} else {
				storeOffice = officeMap.get(office.getId());
			}

			// 设置RFID所属人民银行信息
			rfidDenominationMap.put("storeId", storeOffice.getId());
			rfidDenominationMap.put("storeName", storeOffice.getName());

			// RFID所属商业银行属于当前人民银行的场合
			if (!storeOffice.getId().equals(inputStoreOfficeId)) {
				// RFID所属商业银行不属于当前人民银行的场合
				result = ExternalConstant.RfidErrorFlag.OFFICE_ERROR;

			} else if (!Constant.BoxStatus.BANK_OUTLETS.equals(status)) {
				// 箱袋状态不正确
				result = ExternalConstant.RfidErrorFlag.STATUS_ERROR;
			}

		} else {
			if (!Constant.BoxStatus.COFFER.equals(status)) {
				// 箱袋状态不正确
				result = ExternalConstant.RfidErrorFlag.STATUS_ERROR;
			}
		}

		return result;
	}

	/**
	 * 
	 * Title: updateByPrimaryKeySelective
	 * <p>
	 * Description: 按机构ID和rfid修改物品邦定信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param record
	 * @return int 返回类型
	 */
	public int updateByPrimaryKeySelective(StoRfidDenomination record) {
		return dao.updateByPrimaryKeySelective(record);
	}

	/**
	 * 
	 * Title: deleteByPrimaryKeyAndOfficeId
	 * <p>
	 * Description: 按RFID和机构id删除邦定数据
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param rfid
	 * @param officeId
	 *            机构id
	 * @return int 返回类型
	 */
	public int deleteByPrimaryKeyAndOfficeId(String rfid, String officeId) {
		return dao.deleteByPrimaryKeyAndOfficeId(rfid, officeId);
	}

	/**
	 * 根据流水单号删除RFID绑定信息
	 * 
	 * @param allID
	 * @return
	 */
	@Transactional(readOnly = false)
	public int deleteByAllID(String allID) {
		return dao.deleteByAllID(allID);
	}

	/**
	 * 
	 * Title: findUnUsedListByOfficeId
	 * <p>
	 * Description: 根据机构ID和业务类型 查询未使用待入库的RFID信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId
	 *            机构ID
	 * @param businessType
	 *            业务类型
	 * @return RFID信息列表 List<StoRfidDenomination> 返回类型
	 */
	public List<StoRfidDenomination> pdaFindList(StoRfidDenomination stoRfidDenomination) {
		// 设置查询条件
		stoRfidDenomination.setDelFlag(Constant.deleteFlag.Valid);
		List<StoRfidDenomination> stoRfidDenominationList = dao.pdaFindList(stoRfidDenomination);
		return stoRfidDenominationList;
	}

	/**
     * 
     * Title: unbindingAllIdByRfid
     * <p>Description: 解除RFID流水单号绑定</p>
     * @author:     wangbaozhong
     * @param param rfid信息
     * @return 
     * int    返回类型
     */
    public int unbindingAllIdByRfid(StoRfidDenomination param) {
    	return dao.unbindingAllIdByRfid(param);
    }
    
    
	/**
	 * 删除rfid绑定信息
	 * 
	 * @author caixiaojie
	 * @param rfid
	 *            rfid编号
	 * @return 更新数量
	 */
	@Transactional(readOnly = false)
	public int deleteByPrimaryKey(String rfid) {
		return dao.deleteByPrimaryKey(rfid);
	}

	/**
	 * 根据包号查询RFID信息
	 * 
	 * @author caixiaojie
	 * @param boxNo
	 *            包号
	 * @return rfid列表信息
	 */
	public List<StoRfidDenomination> findListByBoxNo(String boxNo) {
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfid(boxNo);
		return dao.findList(stoRfidDenomination);
	}
	
	/**
	 * 
	 * Title: findRfidInfoByBoxNo
	 * <p>Description: 根据包号查询RFID信息</p>
	 * @author:     wangbaozhong
	 * @param boxNo 包号
	 * @return 
	 * List<StoRfidDenomination>    返回类型
	 */
	public StoRfidDenomination findRfidInfoByBoxNo(String boxNo) {
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfid(boxNo);
		return dao.getRfidInfo(stoRfidDenomination);
	}

	/**
	 * 通过原rfid号替换成新的rfid号
	 * 
	 * @author caixiaojie
	 * @param srcRfid
	 *            原rfid编号
	 * @param dstRfid
	 *            新rfid编号
	 * @param userId
	 *            更新者id
	 * @param userName
	 *            更新者姓名
	 * @return 更新数量
	 */
	@Transactional(readOnly = false)
	public int updateRfidByPrimaryKey(String srcRfid, String dstRfid, String userId, String userName) {
		return dao.updateRfidByPrimaryKey(srcRfid, dstRfid, userId, userName, new Date());
	}
	
	/**
	 * 更新实体
	 * @param entity 实体
	 * @return
	 */
	@Transactional(readOnly = false)
	public int update(StoRfidDenomination entity) {
		return dao.update(entity);
	}
	
	/**
	 * 
	 * Title: insertInToHistory
	 * <p>Description: 插入RFID历史 表</p>
	 * @author:     wangbaozhong
	 * @param rfidList RFID列表
	 * @param office 当前机构
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void insertInToHistory(List<String> rfidList, Office office) {
		StoRfidDenomination condition = null;
		StoRfidDenominationHistory entity = null;
		for (String rfid : rfidList) {
			condition = new StoRfidDenomination();
			condition.setRfid(rfid);
			condition = super.get(condition);
			
			entity = new StoRfidDenominationHistory();
			entity.setOldStoRfidDenomination(condition);
			
			entity.setUpdatedAtOfficeId(office.getId());
			entity.setUpdatedAtOfficeName(office.getName());
			entity.setId(IdGen.uuid());
			stoRfidDenominationHistoryDao.insert(entity);
		}
	}
	
	/**
	 * 
	 * Title: insertInToHistory
	 * <p>Description: 插入RFID历史 表</p>
	 * @author:     wangbaozhong
	 * @param rfid	RFID
	 * @param office 当前机构
	 * void    返回类型
	 */
	@Transactional(readOnly = false)
	public void insertInToHistory(String rfid, Office office) {
		StoRfidDenomination condition = new StoRfidDenomination();
		condition.setRfid(rfid);
		condition = super.get(condition);
		
		StoRfidDenominationHistory entity = new StoRfidDenominationHistory();
		entity.setOldStoRfidDenomination(condition);
		
		entity.setUpdatedAtOfficeId(office.getId());
		entity.setUpdatedAtOfficeName(office.getName());
		entity.setId(IdGen.uuid());
		stoRfidDenominationHistoryDao.insert(entity);
	}
	
	/**
	 * 
	 * Title: findStoRfidDenominationHistoryList
	 * <p>Description: 根据查询条件查询rfid历史表信息i</p>
	 * @author:     wangbaozhong
	 * @param param	查询条件
	 * @return 
	 * List<StoRfidDenominationHistory>    返回类型
	 */
	public List<StoRfidDenominationHistory> findStoRfidDenominationHistoryList(StoRfidDenominationHistory param) {
		return stoRfidDenominationHistoryDao.findList(param);
	}
	
	/**
	 * 
	 * Title: findfullStoRfidDenominationHistoryList
	 * <p>Description: </p>
	 * @author:     wangbaozhong
	 * @param rfid
	 * @param rtnList 
	 * void    返回类型
	 */
	public void findfullStoRfidDenominationHistoryList(String rfid, List<StoRfidDenominationHistory> rtnList) {
		
		StoRfidDenominationHistory condition = new StoRfidDenominationHistory();
		StoRfidDenomination stoRfidDenomination = new StoRfidDenomination();
		stoRfidDenomination.setRfid(rfid);
		condition.setOldStoRfidDenomination(stoRfidDenomination);
		rtnList.addAll(stoRfidDenominationHistoryDao.findList(condition));
		
		StoRfidDenomination oldRfidCondition = new StoRfidDenomination();
		oldRfidCondition.setRfid(rfid);
		
		StoRfidDenomination oldRfidInfo = super.get(oldRfidCondition);
		if (oldRfidInfo != null && !StringUtils.isBlank(oldRfidInfo.getDestRfid())) { 
			findfullStoRfidDenominationHistoryList(oldRfidInfo.getDestRfid(), rtnList);
		}
	}
	
	/**
	 * 根据条件，重新绑定RIFD
	 * @author WangBaozhong
	 * @version 2017年4月20日
	 * 
	 *  
	 * @param bussnessType	业务类型
	 * @param loginUser	当前登录用户
	 * @param rfidDenominationList	RFID列表
	 * @param allId	当前业务流水单号
	 */
	@Transactional(readOnly = false)
	public void reBindingRfid(String bussnessType, User loginUser, List<StoRfidDenomination> rfidDenominationList, String allId) {
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
		for (StoRfidDenomination item : rfidDenominationList) {
			item.setBusinessType(bussnessType);
			item.setOfficeId(loginUser.getOffice().getId());
			item.setOfficeName(loginUser.getOffice().getName());
			
			// 设定当前rfid所处机构
			item.setAtOfficeId(loginUser.getOffice().getId());
			item.setAtOfficeName(loginUser.getOffice().getName());
			
			item.setUseFlag(StoreConstant.RfidUseFlag.init);
			item.setAllId(allId);
			
			item.setBoxStatus(rfidPosition);
			
			item.setCreateBy(loginUser);
			item.setCreateDate(new Date());

			item.setUpdateBy(loginUser);
			item.setUpdateDate(new Date());

			
			// 如果未拆包，初始邦卡机构不变
			item.setOfficeId(null);
			item.setOfficeName(null);
			
			update(item);
			// 将RFID当前邦定记录表导入历史表
	        insertInToHistory(item.getRfid(), loginUser.getOffice());
			
			
		}
	}
}