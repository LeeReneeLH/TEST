package com.coffer.businesses.modules.doorOrder.app.v01.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DayReportDoorMerchanDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositAmount;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.OfficeAmount;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.ExternalConstant;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 门店预约Service
 * 
 * @author lihe
 * @version 2019-08-14
 */
@Service
@Transactional(readOnly = true)
public class DoorOrderInfoAppService extends CrudService<DoorOrderInfoDao, DoorOrderInfo> {

	// 门店预约DAO接口
	@Autowired
	private DoorCenterAccountsMainDao doorCenterAccountsMainDao;

	@Autowired
	private DayReportDoorMerchanDao dayReportDoorMerchanDao;

	@Autowired
	private DoorOrderInfoDao doorOrderInfoDao;

	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	/** 现金业务类型 */
	// private static String[] cashBusinessType = { "74", "78", "79" };

	/** 现金存款类型 */
	private static String[] cashDepositType = { "74", "81", "82" };

	/** 现金差错类型 */
	// private static String[] cashErrorType = { "79" };

	/** 现金存款状态 */
	private static String[] cashDepositStatus = { "0", "1", "2", "3" };

	/**
	 * 
	 * Title: getClientInfoForCenter
	 * <p>
	 * Description: 中心登录获取商户存款信息
	 * </p>
	 * 
	 * @author: lihe
	 * @param office
	 * @return Map<String,Object> 返回类型
	 */
	public List<DepositAmount> getClientInfoForCenter(DepositAmount depositAmount, Office office) {
		// 设置业务类型
		List<String> busTypelist = Arrays.asList(cashDepositType);
		// 设置业务类型列表
		depositAmount.setBusinessTypes(busTypelist);
		if (Constant.OfficeType.CLEAR_CENTER.equals(office.getType())
				|| Constant.OfficeType.STORE.equals(office.getType())) {
			depositAmount.getSqlMap().put("dsf",
					"AND (A.parent_ids LIKE '%" + office.getParentId() + "%' OR A.ID = " + office.getParentId() + ")");
		} else {
			depositAmount.getSqlMap().put("dsf",
					"AND (A.parent_ids LIKE '%" + office.getId() + "%' OR A.ID = " + office.getId() + ")");
		}

		List<DepositAmount> merchantInfoList = doorCenterAccountsMainDao.getClientInfoForCenter(depositAmount);
		if (Collections3.isEmpty(merchantInfoList)) {
			merchantInfoList = Lists.newArrayList();
		}
		return merchantInfoList;
	}

	/**
	 * 
	 * Title: getDoorInfoByMerchantId
	 * <p>
	 * Description: 根据商户ID获取门店存款信息列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param merchantId
	 * @return Page<DepositAmount> 返回类型
	 */
	public List<DepositAmount> getDoorInfoByMerchantId(DepositAmount depositAmount, User user) {
		// 设置业务类型
		List<String> busTypelist = Arrays.asList(cashDepositType);
		// 设置业务类型列表
		depositAmount.setBusinessTypes(busTypelist);
		if (Constant.OfficeType.CLEAR_CENTER.equals(user.getOffice().getType())) {
			depositAmount.getSqlMap().put("dsf", "AND so.parent_ids LIKE '%" + user.getOffice().getParentId() + "%'");
		} else {
			depositAmount.getSqlMap().put("dsf", "AND( so1.parent_ids LIKE '%" + user.getOffice().getId()
					+ "%' OR so1.ID =" + user.getOffice().getId() + ")");
		}
		List<DepositAmount> result = doorCenterAccountsMainDao.getDoorDepositByMerchantId(depositAmount);
		if (Collections3.isEmpty(result)) {
			result = Lists.newArrayList();
		}
		return result;
	}

	/**
	 * 
	 * Title: getAmountAndCount
	 * <p>
	 * Description: 获取存款总额和笔数
	 * </p>
	 * 
	 * @author: lihe
	 * @return Map<String,Object> 返回类型
	 */
	public Map<String, Object> getAmountAndCount(User user) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		Office userOffice = user.getOffice();
		OfficeAmount officeAmount = new OfficeAmount();
		// ------------- 获取上次日结时间 ------------------
		// 设置查询上次日结条件
		DayReportDoorMerchan dayReportDoorMerchan = new DayReportDoorMerchan();
		// 设置当前登录人机构ID
		if (Constant.OfficeType.CLEAR_CENTER.equals(userOffice.getType())) {
			dayReportDoorMerchan.setRofficeId(userOffice.getId());
			officeAmount.setId(userOffice.getId());
		} else if (Constant.OfficeType.MERCHANT.equals(userOffice.getType())) {
			dayReportDoorMerchan.setOfficeId(userOffice.getId());
		} else if (Constant.OfficeType.STORE.equals(userOffice.getType())) {
			dayReportDoorMerchan.setOfficeId(userOffice.getParentId());
		}
		dayReportDoorMerchan.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		// 获取上次日结时间
		List<DayReportDoorMerchan> dateList = dayReportDoorMerchanDao.getLastDayReportDate(dayReportDoorMerchan);
		if (!Collections3.isEmpty(dateList)) {
			dateList.get(0);
			// 查询条件： 开始时间
			if (StringUtils.isNotBlank(dateList.get(0).getDayReportDate())) {
				officeAmount.setSearchDateStart(
						DateUtils.foramtSearchDate(DateUtils.parseDate(dateList.get(0).getDayReportDate())));
			}
		}
		// -------------- 查询机构存款总额和笔数 ----------------
		// 查询条件： 开始时间
		// if (StringUtils.isNotBlank(date)) {
		// officeAmount
		// .setSearchDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(DateUtils.parseDate(date))));
		// }
		List<String> busTypelist = Lists.newArrayList();
		// 设置业务类型
		busTypelist = Arrays.asList(cashDepositType);
		officeAmount.setBusinessTypes(busTypelist);
		// 查询条件： 结束时间
		officeAmount.setSearchDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(new Date())));
		if (Constant.OfficeType.CLEAR_CENTER.equals(userOffice.getType())) {
			officeAmount.getSqlMap().put("dsf", "AND D.parent_ids LIKE '%" + userOffice.getParentId() + "%'");
		} else {
			officeAmount.getSqlMap().put("dsf",
					"AND( D.parent_ids LIKE '%" + userOffice.getId() + "%' OR D.ID =" + userOffice.getId() + ")");
		}
		OfficeAmount result = doorCenterAccountsMainDao.getCenterDepoAndCount(officeAmount);
		if (result == null) {
			result = new OfficeAmount();
		}
		rtnMap.put(Parameter.RESULT, result);
		rtnMap.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
		return rtnMap;
	}

	/**
	 * 
	 * Title: getOrderInfoByDoorId
	 * <p>
	 * Description: 小程序根据门店编号查询存款列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param depositInfo
	 * @return Page<DepositInfo> 返回类型
	 */
	public List<DepositInfo> getOrderInfoByDoorId(DepositInfo depositInfo, User user) {
		// 设置业务类型
		List<String> busTypelist = Arrays.asList(cashDepositType);
		// 设置业务类型列表
		depositInfo.setBusinessTypes(busTypelist);
		if (Constant.OfficeType.CLEAR_CENTER.equals(user.getOffice().getType())) {
			depositInfo.getSqlMap().put("dsf", "AND so.parent_ids LIKE '%" + user.getOffice().getParentId() + "%'");
		} else {
			depositInfo.getSqlMap().put("dsf", "AND( so1.parent_ids LIKE '%" + user.getOffice().getId()
					+ "%' OR so1.ID =" + user.getOffice().getId() + ")");
		}
		List<DepositInfo> result = doorCenterAccountsMainDao.getOrderInfoByDoorId(depositInfo);
		if (Collections3.isEmpty(result)) {
			result = Lists.newArrayList();
		}
		return result;
	}

	/**
	 * 
	 * Title: getDoorListByUser
	 * <p>
	 * Description: 根据用户获取缴存列表
	 * </p>
	 * 
	 * @author: lihe
	 * @param page
	 * @param doorOrderInfo
	 * @param user
	 * @return Page<DoorOrderInfo> 返回类型
	 */
	public List<DoorOrderInfo> getDoorListByUser(DoorOrderInfo doorOrderInfo, User user, int pageSize) {
		// 当前登陆人机构类型
		if (user.getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 按清分中心过滤
			// doorOrderInfo.getSqlMap().put("dsf",
			// dataScopeFilter(doorOrderInfo.getCurrentUser(), "o", null));
			doorOrderInfo.getSqlMap().put("dsf", "AND o.parent_ids LIKE '%" + user.getOffice().getParentId() + "%'");
		} else {
			// 按门店过滤
			// doorOrderInfo.getSqlMap().put("dsf",
			// dataScopeFilter(doorOrderInfo.getCurrentUser(), "o1", null));
			doorOrderInfo.getSqlMap().put("dsf", "AND( o1.parent_ids LIKE '%" + user.getOffice().getId()
					+ "%' OR o1.ID =" + user.getOffice().getId() + ")");
		}
		// 设置业务类型
		List<String> statusList = Arrays.asList(cashDepositStatus);
		doorOrderInfo.setStatusList(statusList);
		List<DoorOrderInfo> result = doorOrderInfoDao.getDoorListByUser(doorOrderInfo);
		if (!Collections3.isEmpty(result) && result.size() > 0) {
			// if (StringUtils.isNotBlank(result.get(0).getDoorId())) {
			// Page<DepositInfo> orderPage = new Page<DepositInfo>(1, pageSize);
			// DepositInfo depositInfo = new DepositInfo();
			// depositInfo.setPage(orderPage);
			// depositInfo.setDoorId(result.get(0).getDoorId());
			// depositInfo.setOrderId(result.get(0).getOrderId());
			// depositInfo.setCreateBy(user);
			// List<String> busTypelist = Lists.newArrayList();
			// // 设置业务类型
			// busTypelist = Arrays.asList(cashDepositType);
			// // 设置业务类型列表
			// depositInfo.setBusinessTypes(busTypelist);
			// orderPage.setList(getOrderInfoByDoorId(depositInfo));
			// result.get(0).setOrderPage(orderPage);
			// }
		} else {
			result = Lists.newArrayList();
		}
		return result;
	}

	/**
	 * serviceNo:015 校验和保存凭条图片
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> checkAndSaveTicker(String orderId, String tickerNo, byte[] photoImg, User user) {
		Map<String, Object> jsonData = Maps.newHashMap();
		// 根据订单编号和凭条查询是否有记录
		List<DoorOrderDetail> list = doorOrderDetailDao.findDetailByOrderIdAndTicker(orderId, tickerNo);
		if (Collections3.isEmpty(list)) {
			jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E01);
			jsonData.put(Parameter.ERROR_MSG_KEY, "库中没有相关信息！");
			jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
		} else {
			DoorOrderDetail doorOrderDetail = list.get(0);
			// 凭条照片
			// doorOrderDetail.setPhoto(photoImg);
			doorOrderDetail.setPhoto(new byte[0]);
			// 上传用户
			doorOrderDetail.setUpdateBy(user);
			// 上传时间
			doorOrderDetail.setUpdateDate(new Date());
			int i = doorOrderDetailDao.changePhotoById(list.get(0));
			if (i > 0) {
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_SUCCESS);
			} else {
				jsonData.put(Parameter.ERROR_NO_KEY, ExternalConstant.HardwareInterface.ERROR_NO_E02);
				jsonData.put(Parameter.ERROR_MSG_KEY, "图片保存失败,请重试！");
				jsonData.put(Parameter.RESULT_FLAG_KEY, ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			}
		}
		return jsonData;
	}

	/**
	 * 获取凭条信息
	 *
	 * @author XL
	 * @version 2019年8月29日
	 * @param orderId
	 * @param tickerNo
	 * @return
	 */
	public DoorOrderDetail getDoorOrderDetailPhoto(String orderId, String tickerNo) {
		DoorOrderDetail doorOrderDetail = null;
		// 查询凭条列表
		List<DoorOrderDetail> doorOrderDetailList = doorOrderDetailDao.findDetailByOrderIdAndTicker(orderId, tickerNo);
		if (!Collections3.isEmpty(doorOrderDetailList)) {
			doorOrderDetail = doorOrderDetailList.get(0);
		}
		return doorOrderDetail;
	}
}