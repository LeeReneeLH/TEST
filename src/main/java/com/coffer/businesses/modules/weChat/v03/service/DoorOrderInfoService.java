package com.coffer.businesses.modules.weChat.v03.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.collection.v03.entity.TaskConfirm;
import com.coffer.businesses.modules.collection.v03.entity.TaskDown;
import com.coffer.businesses.modules.collection.v03.service.CheckCashService;
import com.coffer.businesses.modules.collection.v03.service.TaskConfirmService;
import com.coffer.businesses.modules.collection.v03.service.TaskDownService;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.DoorCommonUtils;
import com.coffer.businesses.modules.doorOrder.v01.dao.DepositErrorDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorCenterAccountsMainDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorErrorInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.SaveTypeDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.DepositError;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorErrorInfo;
import com.coffer.businesses.modules.doorOrder.v01.entity.SaveType;
import com.coffer.businesses.modules.doorOrder.v01.service.DepositErrorService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorErrorInfoService;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderAmountDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderDetailDao;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderAmount;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Maps;

/**
 * 门店预约Service
 * 
 * @author wanglin
 * @version 2017-02-13
 */
@Service
@Transactional(readOnly = true)
public class DoorOrderInfoService extends CrudService<DoorOrderInfoDao, DoorOrderInfo> {

	// 门店预约DAO接口
	@Autowired
	private DoorOrderDetailDao doorOrderDetailDao;

	@Autowired
	private DoorOrderInfoDao doorOrderInfoDao;

	@Autowired
	private DoorOrderAmountDao doorOrderAmountDao;

	@Autowired
	private StoDictDao stoDictDao;

	@Autowired
	private SaveTypeDao saveTypeDao;
	@Autowired
	private DepositErrorDao depositErrorDao;

	@Autowired
	private TaskConfirmService taskConfirmService;
	@Autowired
	private TaskDownService taskDownService;
	@Autowired
	private CheckCashService checkCashService;
	@Autowired
	private DepositErrorService depositErrorService;
	@Autowired
	private DoorErrorInfoService doorErrorInfoService;
	@Autowired
	private DoorErrorInfoDao doorErrorInfoDao;
	@Autowired
	private DoorCenterAccountsMainDao doorCenterAccountsMainDao;
	@Autowired
	private UserDao userDao;
	
	public DoorOrderInfo get(String id) {
		DoorOrderInfo doorOrderInfo = super.get(id);
		if (doorOrderInfo != null && doorOrderInfo.getId() != null) {
			// 查找明细
			DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
			doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
			List<DoorOrderDetail> doorOrderDetailList = doorOrderDetailDao.findList(doorOrderDetail);
			StringBuilder amountList = new StringBuilder("");
			StringBuilder rfidList = new StringBuilder("");
			StringBuilder tickertapeList = new StringBuilder("");
			StringBuilder busTypeList = new StringBuilder("");
			// 存款备注 gzd 2019-12-16
			StringBuilder remarksList = new StringBuilder("");
			StringBuilder detailIdList = new StringBuilder("");
			StringBuilder createByList = new StringBuilder("");
			StringBuilder createDateList = new StringBuilder("");
			for (DoorOrderDetail amountDoorOrderDetail : doorOrderDetailList) {
				amountList = amountList.append(",").append(amountDoorOrderDetail.getAmount()) ;
				String strRfid = amountDoorOrderDetail.getRfid();
				if (strRfid == null) {
					strRfid = "";
				}
				rfidList = rfidList.append(",").append(strRfid);
				String strTickertape = amountDoorOrderDetail.getTickertape();
				if (strTickertape == null) {
					strTickertape = "";
				}
				tickertapeList = tickertapeList.append(",").append(strTickertape);
				// 存款备注 gzd 2019-12-16 begin
				String strRemarks = amountDoorOrderDetail.getRemarks();
				if (strRemarks == null) {
					strRemarks = "";
				}
				remarksList = remarksList.append(",").append(strRemarks);
				// end
				String strId = amountDoorOrderDetail.getId();
				if (strId == null) {
					strId = "";
				}
				detailIdList = detailIdList.append(",").append(strId);
				String strBusType = amountDoorOrderDetail.getBusinessType();
				SaveType saveType = saveTypeDao.get(strBusType);
				strBusType = saveType == null ? "" : saveType.getTypeName();
				busTypeList = busTypeList.append(",").append(strBusType);
				// 存款用户列表构造
				String createById = amountDoorOrderDetail.getCreateBy().getId();
				//User createBy = UserUtils.get(createById);
				User createBy = userDao.getUserById(createById);
				createByList = createByList.append(",").append(createBy.getName());
				// 存款日期列表构造
				Date createDate = amountDoorOrderDetail.getCreateDate();
				String strCreateDate = DateUtils.formatDateTime(createDate);
				createDateList = createDateList.append(",").append(strCreateDate);
			}
			doorOrderInfo.setAmountList(amountList.toString());
			doorOrderInfo.setRfidList(rfidList.toString());
			doorOrderInfo.setTickertapeList(tickertapeList.toString());
			doorOrderInfo.setDetailIdList(detailIdList.toString());
			doorOrderInfo.setBusTypeList(busTypeList.toString());
			doorOrderInfo.setCreateByList(createByList.toString());
			doorOrderInfo.setCreateDateList(createDateList.toString());
			// 存款备注 gzd 2019-12-16
			doorOrderInfo.setRemarksList(remarksList.toString());
		}
		return doorOrderInfo;
	}

	public List<DoorOrderInfo> findList(DoorOrderInfo doorOrderInfo) {
		return super.findList(doorOrderInfo);
	}

	public Page<DoorOrderInfo> findPage(Page<DoorOrderInfo> page, DoorOrderInfo doorOrderInfo) {
		// 当前登陆人机构类型
		if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
			// 按清分中心过滤
			doorOrderInfo.getSqlMap().put("dsf", dataScopeFilter(doorOrderInfo.getCurrentUser(), "o", null));
		} else {
			// 按门店过滤
			doorOrderInfo.getSqlMap().put("dsf", dataScopeFilter(doorOrderInfo.getCurrentUser(), "o1", null));
		}
		// 查询条件： 开始时间
		if (doorOrderInfo.getCreateTimeStart() != null) {
			doorOrderInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(doorOrderInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (doorOrderInfo.getCreateTimeEnd() != null) {
			doorOrderInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(doorOrderInfo.getCreateTimeEnd())));
		}
		// GJ 处理凭条问题 start
		Page<DoorOrderInfo> pageData = super.findPage(page, doorOrderInfo);
		List<DoorOrderInfo> list = page.getList();
		List<DoorOrderInfo> resList = new ArrayList<>();
		for (DoorOrderInfo DoorOrderInfo : list) {
			if (DoorOrderInfo.getId() != null && DoorOrderInfo.getId() != "") {
				resList.add(DoorOrderInfo);
			}
		}
		pageData.setList(resList);
		return pageData;
	}

	/**
	 * 门店存款登记
	 *
	 * @author XL
	 * @version 2019年6月28日
	 * @param doorOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public synchronized void save(DoorOrderInfo doorOrderInfo) {
		// 总笔数
		int intTotalCount = 0;
		// 设置状态（登记）
		doorOrderInfo.setStatus(DoorOrderConstant.Status.REGISTER);
		// 设置预约单号
		if (StringUtils.isBlank(doorOrderInfo.getId())) {
			String orderId = BusinessUtils.getNewBusinessNo(WeChatConstant.BusinessType.DOOR_ORDER,
					doorOrderInfo.getCurrentUser().getOffice());
			doorOrderInfo.setOrderId(orderId);
		}
		// 判断申请方式
		if (!DoorOrderConstant.MethodType.METHOD_WECHAT.equals(doorOrderInfo.getMethod())
				&& !DoorOrderConstant.MethodType.METHOD_PDA.equals(doorOrderInfo.getMethod())) {
			// 设置申请方式为PC端
			doorOrderInfo.setMethod(DoorOrderConstant.MethodType.METHOD_PC);
			// PC端直接设置为已确认
			doorOrderInfo.setStatus(DoorOrderConstant.Status.CONFIRM);
		}
		// 金额格式化
		doorOrderInfo.setAmount(doorOrderInfo.getAmount().replaceAll(",", ""));
		// 当前时间
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		// 设置申请日期
		if (StringUtils.isBlank(doorOrderInfo.getOrderDate())) {
			doorOrderInfo.setOrderDate(dateString);
			doorOrderInfo.setUpdateDate(currentTime);
		}
		doorOrderInfo.preInsert();
		// 预约明细删除
		DoorOrderDetail doorOrderDetailDel = new DoorOrderDetail();
		doorOrderDetailDel.setOrderId(doorOrderInfo.getOrderId());
		doorOrderDetailDao.deleteByOrderId(doorOrderDetailDel);
		// 金额列表
		String[] amountList = doorOrderInfo.getAmountList().split(",", -1);
		// 业务类型列表
		String[] busTypeList = doorOrderInfo.getBusTypeList().split(",", -1);
		// 凭条列表
		String[] tickertapeList = doorOrderInfo.getTickertapeList().split(",", -1);
		// 存款备注列表 gzd 2019-12-16
		String[] remarksList = doorOrderInfo.getRemarksList().split(",", -1);
		// 金额明细列表
		String[] detailList = doorOrderInfo.getDetailList().split(",", -1);
		// 凭条对应明细编号
		Map<String, String> detailMap = Maps.newHashMap();
		// 重新录入明细
		DoorOrderDetail doorOrderDetail;
		// 包号
		String rfid = doorOrderInfo.getRfid();
		// 行数
		int rowCnt = 0;
		for (int i = 0; i < amountList.length; i++) {
			// 明细金额
			String amount = amountList[i];
			// 明细业务类型
			String busType = busTypeList[i];
			// 明细凭条
			String tickertape = tickertapeList[i];
			// 明细存款备注 gzd 2019-12-16
			String remarks = remarksList[i];
			if (StringUtils.isNotBlank(amount)) {
				doorOrderDetail = new DoorOrderDetail();
				// 设置金额
				doorOrderDetail.setAmount(amount);
				// 设置包号
				doorOrderDetail.setRfid(rfid);
				// 设置凭条
				doorOrderDetail.setTickertape(tickertape);
				// 设置 业务类型
				doorOrderDetail.setBusinessType(busType);
				// 设置 存款备注 gzd 2019-12-16
				doorOrderDetail.setRemarks(remarks);
				// 设置预约单号
				doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
				// 行数编号
				rowCnt++;
				doorOrderDetail.setDetailId(String.valueOf(rowCnt));
				doorOrderDetail.setCreateBy(doorOrderInfo.getCreateBy());
				doorOrderDetail.setUpdateBy(doorOrderInfo.getUpdateBy());
				doorOrderDetail.setOrderDate(dateString);
				doorOrderDetail.setId(IdGen.uuid());
				doorOrderDetail.setCreateDate(doorOrderInfo.getCreateDate());
				doorOrderDetail.setUpdateDate(doorOrderInfo.getUpdateDate());
				doorOrderDetailDao.insert(doorOrderDetail);
				// 明细凭条对应编号
				detailMap.put(doorOrderDetail.getTickertape(), doorOrderDetail.getId());
				// 增加笔数
				intTotalCount = intTotalCount + 1;
				// 将流水关联到账务(按照明细循环插入)
				DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
				// 设置关联账务流水Id
				doorCenterAccountsMain.setBusinessId(doorOrderInfo.getOrderId());
				// 设置客户Id
				doorCenterAccountsMain.setClientId(doorOrderInfo.getDoorId());
				// 设置业务类型
				doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
				// 设置入库金额
				doorCenterAccountsMain.setInAmount(new BigDecimal(doorOrderDetail.getAmount()));
				// 设置出入库类型
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
				// 设置账务发生机构
				User userInfo = UserUtils.getUser();
				doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
				// 设置账务所在机构
				doorCenterAccountsMain.setAofficeId(doorOrderInfo.getDoorId());
				// 设置业务状态
				doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.CREATE);
				// 设置账务代付状态(未代付)
				doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
				// 存款备注 gzd 2019-12-16
				doorCenterAccountsMain.setSevenCode(remarks);
				// 设置凭条
				doorCenterAccountsMain.setTickertape(doorOrderDetail.getTickertape());
				DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
			}
		}
		// 分配状态（2：已确认）
		doorOrderInfo.setAllotStatus(DoorOrderConstant.AllotStatus.CONFIRMED);
		// 总笔数
		doorOrderInfo.setTotalCount(String.valueOf(intTotalCount));
		dao.insert(doorOrderInfo);
		// 金额明细
		for (int i = 0; i < detailList.length; i++) {
			// 金额明细内容
			String[] detail = detailList[i].split("_", -1);
			// 物品字典
			StoDict stoDict = stoDictDao.get(detail[2]);
			DoorOrderAmount doorOrderAmount = new DoorOrderAmount();
			// 主键
			doorOrderAmount.setId(IdGen.uuid());
			// 存款类型
			doorOrderAmount.setTypeId(detail[1]);
			// 明细主键
			doorOrderAmount.setDetailId(detailMap.get(detail[0]));
			// 面值
			doorOrderAmount.setDenomination(StringUtils.isBlank(detail[2]) ? null : detail[2]);
			// 张数
			doorOrderAmount.setCountZhang(StringUtils.isBlank(detail[3]) ? null : detail[3]);
			// 金额
			if (StringUtils.isNotBlank(detail[4])) {
				doorOrderAmount.setDetailAmount(detail[4]);
			} else {
				doorOrderAmount
						.setDetailAmount(String.valueOf(stoDict.getUnitVal().multiply(new BigDecimal(detail[3]))));
			}
			// 序号
			doorOrderAmount.setRowNo(i + 1);
			// 币种
			doorOrderAmount.setCurrency("1");
			doorOrderAmountDao.insert(doorOrderAmount);
		}
		//添加 detil金额明细
		doorOrderDetailDao.updateDetailAmount(tickertapeList);
		// 页面存款时将拆箱主表做成
		// 根据款袋编号获取任务
		TaskDown taskDown = new TaskDown();
		// 清分机构
		taskDown.setOffice(UserUtils.getUser().getOffice());
		// 款袋编号
		taskDown.setRfid(rfid);
		// 设置状态（登记、确认）
		taskDown.setStatusList(
				Arrays.asList(new String[] { DoorOrderConstant.Status.REGISTER, DoorOrderConstant.Status.CONFIRM }));
		// 延长日
		taskDown.setExtendeDay(WeChatConstant.EXTENDE_DAY);
		// 任务列表
		List<TaskDown> taskDownList = taskDownService.findList(taskDown);
		taskDown = taskDownList.get(0);
		TaskConfirm taskConfirm = taskConfirmService.get(taskDown.getId());
		checkCashService.createCheckCashForPage(taskConfirm, UserUtils.getUser(), new Date(),
				UserUtils.getUser().getOffice());
	}

	/**
	 * 
	 * @author qph
	 * @version 2017年4月23日
	 * 
	 *          门店预约修改
	 * @param DoorOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void updateAmount(DoorOrderInfo doorOrderInfo) {
		int intTotalCount = 0;
		// 0：登记
		doorOrderInfo.setStatus("0");
		/*
		 * if (StringUtils.isBlank(doorOrderInfo.getId())){
		 * 
		 * String orderId = BusinessUtils.getNewBusinessNo("A",null); // doorId
		 * doorOrderInfo.setOrderId(orderId); }
		 */
		doorOrderInfo.setAmount(doorOrderInfo.getAmount().replaceAll(",", ""));
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		// doorOrderInfo.setOrderDate(dateString);
		doorOrderInfo.setUpdateDate(currentTime);
		// doorOrderInfoDao.updateAmount(doorOrderInfo);
		// 预约明细删除
		DoorOrderDetail doorOrderDetailDel = new DoorOrderDetail();
		doorOrderDetailDel.setOrderId(doorOrderInfo.getOrderId());
		doorOrderDetailDao.deleteByOrderId(doorOrderDetailDel);
		String[] amountList = doorOrderInfo.getAmountList().split(",");
		String[] rfidList = doorOrderInfo.getRfidList().split(",", -1);
		// 重新登录明细
		DoorOrderDetail doorOrderDetail;
		int rowCnt = 0;
		String rfid = "";
		for (int i = 0; i < amountList.length; i++) {
			String amount = amountList[i];
			if (rfidList.length > 0) {
				rfid = rfidList[i];
			}
			if (amount != null && !"".equals(amount)) {
				doorOrderDetail = new DoorOrderDetail();
				doorOrderDetail.setUpdateBy(doorOrderInfo.getUpdateBy());
				doorOrderDetail.setCreateBy(doorOrderInfo.getCreateBy());
				doorOrderDetail.preInsert();
				doorOrderDetail.setAmount(amount);
				doorOrderDetail.setRfid(rfid);
				doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
				doorOrderDetail.setOrderDate(dateString);
				rowCnt++;
				doorOrderDetail.setDetailId(String.valueOf(rowCnt));
				doorOrderDetailDao.insert(doorOrderDetail);
				intTotalCount = intTotalCount + 1;
			}
		}

		doorOrderInfo.setTotalCount(String.valueOf(intTotalCount));// 总笔数
		doorOrderInfoDao.updateAmount(doorOrderInfo);

	}

	/**
	 * 
	 * @author iceman
	 * @version 2017年2月14日
	 * 
	 *          门店预约删除
	 * @param DoorOrderInfo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void delete(DoorOrderInfo doorOrderInfo) {
		// 门店预约删除
		// doorOrderInfo.setUpdateBy(UserUtils.getUser());
		doorOrderInfo.preUpdate();
		super.delete(doorOrderInfo);
		// 预约明细删除
		DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
		doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
		doorOrderDetailDao.deleteByOrderId(doorOrderDetail);
	}

	/**
	 * 
	 * @author qph
	 * @version 2017-04-21 通过系统日期查询当天记录
	 * @param orderDate,doorid
	 * @return DoorOrderInfo
	 */
	public DoorOrderInfo getByorderdate(String orderdate, String doorid) {
		// 通过门店ID和当前系统日期查询该门店当天的预约请求
		DoorOrderInfo doorOrderInfo = doorOrderInfoDao.getByorderDate(orderdate, doorid);
		// 判断该门店当天是否发送过预约请求
		if (doorOrderInfo != null && doorOrderInfo.getId() != null) {
			// 查找明细
			DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
			doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
			List<DoorOrderDetail> doorOrderDetailList = doorOrderDetailDao.findList(doorOrderDetail);
			String amountList = "";
			String rfidList = "";
			// 将明细表里的金额以及包号通过“，”串接起来
			for (DoorOrderDetail amountDoorOrderDetail : doorOrderDetailList) {
				amountList = amountList + "," + amountDoorOrderDetail.getAmount();
			}
			for (DoorOrderDetail amountDoorOrderDetail1 : doorOrderDetailList) {
				if (amountDoorOrderDetail1.getRfid() == null) {
					rfidList = rfidList + "," + "";
				} else {
					rfidList = rfidList + "," + amountDoorOrderDetail1.getRfid();
				}

			}
			doorOrderInfo.setAmountList(amountList);
			doorOrderInfo.setRfidList(rfidList);
		}
		return doorOrderInfo;
	}

	/**
	 * 
	 * @author qph
	 * @version 2017-04-25 管理员用户确认预约订单
	 * @param orderDate
	 * @return
	 */
	@Transactional(readOnly = false)
	public void confirm(DoorOrderInfo doorOrderInfo) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		doorOrderInfo.setOrderDate(dateString);
		doorOrderInfo.setUpdateDate(currentTime);
		doorOrderInfoDao.updateStatusconfirm(doorOrderInfo);
	}

	/**
	 * 冲正处理
	 *
	 * @author XL
	 * @version 2019年7月2日
	 * @param doorOrderInfo
	 */
	@Transactional(readOnly = false)
	public void reverse(DoorOrderInfo doorOrderInfo) {
		// 寻找该中心传统存款最近一次账务日结时间
		/*Date reportDate = dayReportDoorMerchanService.getTraditionalsaveMaxDate((UserUtils.getUser().getOffice()));
		// 日结时间大于更新时间
		if (reportDate != null && doorOrderInfo.getCreateDate() != null
				&& reportDate.getTime() > doorOrderInfo.getCreateDate().getTime()) {
			// 该预约已经日结
			throw new BusinessException("message.E7214", "", new String[] { doorOrderInfo.getOrderId() });
		}*/
		/** 根据预约单号查询是否进行过日结 add by HZY 2020-07-17 start */
		//查询该预约是否进行过日结
		if(StringUtils.isNotEmpty(doorOrderInfo.getOrderId())){
			DoorCenterAccountsMain dcAccountsMain = doorCenterAccountsMainDao.getReportId(doorOrderInfo.getOrderId());			
			if(StringUtils.isNotEmpty(dcAccountsMain.getReportId())){
				// 该预约已经日结
				throw new BusinessException("message.E7214", "", new String[] { doorOrderInfo.getOrderId() });
			}
		}		
		/** 根据预约单号查询是否进行过日结 add by HZY 2020-07-17 end */
		doorOrderInfo.preUpdate();
		// 更新主表冲正状态
		doorOrderInfoDao.update(doorOrderInfo);
		DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
		doorOrderDetail.setOrderId(doorOrderInfo.getOrderId());
		// 明细列表
		List<DoorOrderDetail> doorOrderDetailsList = doorOrderDetailDao.findList(doorOrderDetail);
		for (DoorOrderDetail detail : doorOrderDetailsList) {
			// 将冲正关联到账务(按照明细循环冲正)
			DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMain.setBusinessId(doorOrderInfo.getOrderId());
			// 设置客户Id
			doorCenterAccountsMain.setClientId(doorOrderInfo.getDoorId());
			// 设置业务类型(只有传统存款可以冲正)
			doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.TRADITIONAL_SAVE);
			// 设置出库金额
			doorCenterAccountsMain.setOutAmount(new BigDecimal(detail.getAmount()));
			// 设置出入库类型
			doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
			// 设置账务发生机构
			User userInfo = UserUtils.getUser();
			doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
			// 设置账务所在机构
			doorCenterAccountsMain.setAofficeId(doorOrderInfo.getDoorId());
			// 设置业务状态
			doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.DELETE);
			// 设置账务代付状态(未代付)
			doorCenterAccountsMain.setStatus(DoorOrderConstant.AccountPaidStatus.NOTHASPAID);
			// 设置凭条
			doorCenterAccountsMain.setTickertape(detail.getTickertape());
			// 存款备注
			doorCenterAccountsMain.setSevenCode(detail.getRemarks());
			DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
		}
		// ---------- 存款差错冲正 ----------
		// 按存款单号查找对应存款差错
		DepositError depositError = new DepositError();
		depositError.setStatus(DoorOrderConstant.StatusType.CREATE);
		depositError.setOrderId(doorOrderInfo.getOrderId());
		List<DepositError> result = depositErrorService.findList(depositError);
		// 有登记状态的差错
		if (!Collections3.isEmpty(result)) {
			// 将存款差错状态设置为冲正
			depositError = result.get(0);
			depositError.setStatus(DoorOrderConstant.StatusType.DELETE);
			depositError.preUpdate();
			depositErrorDao.update(depositError);
			// 将冲正关联到账务
			DoorCenterAccountsMain doorCenterAccountsMain = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			doorCenterAccountsMain.setBusinessId(doorOrderInfo.getOrderId());
			// 设置客户Id
			doorCenterAccountsMain.setClientId(doorOrderInfo.getDoorId());
			// 设置业务类型
			doorCenterAccountsMain.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
			if (depositError.getErrorType().equals(ClearConstant.ErrorType.SHORT_CURRENCY)) {
				// 短款
				doorCenterAccountsMain.setInAmount(new BigDecimal(depositError.getAmount()));
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
			} else if (depositError.getErrorType().equals(ClearConstant.ErrorType.LONG_CURRENCY)) {
				// 长款
				doorCenterAccountsMain.setOutAmount(new BigDecimal(depositError.getAmount()));
				doorCenterAccountsMain.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
			}
			// 设置账务发生机构
			User userInfo = UserUtils.getUser();
			doorCenterAccountsMain.setRofficeId(userInfo.getOffice().getId());
			// 设置账务所在机构
			doorCenterAccountsMain.setAofficeId(doorOrderInfo.getDoorId());
			// 设置业务状态
			doorCenterAccountsMain.setBusinessStatus(DoorOrderConstant.StatusType.DELETE);
			// 设置该笔差错是否已经处理
			doorCenterAccountsMain.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
			DoorCommonUtils.insertAccounts(doorCenterAccountsMain);
		}

		// ---------- 拆箱差错冲正 ----------
		// 按存款单号查找对应拆箱差错
		DoorErrorInfo eInfo = new DoorErrorInfo();
		eInfo.setBusinessId(doorOrderInfo.getOrderId());
		eInfo.setStatus(DoorOrderConstant.ErrorStatus.CREATE);
		List<DoorErrorInfo> eResult = doorErrorInfoService.findList(eInfo);
		// 有登记状态的差错
		if (!Collections3.isEmpty(eResult)) {
			// 将存款差错状态设置为冲正
			eInfo = eResult.get(0);
			eInfo.setDelFlag("1");
			eInfo.preUpdate();
			doorErrorInfoDao.update(eInfo);
			// 将冲正关联到账务
			DoorCenterAccountsMain door = new DoorCenterAccountsMain();
			// 设置关联账务流水Id
			door.setBusinessId(eInfo.getErrorNo());
			// 设置客户Id
			door.setClientId(doorOrderInfo.getDoorId());
			// 设置业务类型
			door.setBusinessType(DoorOrderConstant.BusinessType.DEPOSIT_ERROR_SAVE);
			if (eInfo.getErrorType().equals(ClearConstant.ErrorType.SHORT_CURRENCY)) {
				// 短款
				door.setInAmount(eInfo.getDiffAmount());
				door.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_IN);
			} else if (eInfo.getErrorType().equals(ClearConstant.ErrorType.LONG_CURRENCY)) {
				// 长款
				door.setOutAmount(eInfo.getDiffAmount());
				door.setInoutType(DoorOrderConstant.AccountsInoutType.ACCOUNTS_OUT);
			}
			// 设置账务发生机构
			User user = UserUtils.getUser();
			door.setRofficeId(user.getOffice().getId());
			// 设置账务所在机构
			door.setAofficeId(doorOrderInfo.getDoorId());
			// 设置业务状态
			door.setBusinessStatus(DoorOrderConstant.StatusType.DELETE);
			// 设置该笔差错是否已经处理
			door.setIsTakeUp(DoorOrderConstant.ErrorIsTakeUp.NOTTAKEUP);
			DoorCommonUtils.insertAccounts(door);
		}
		// ---------- 相关的拆箱记录逻辑删除 ----------
		CheckCashMain checkCashMain = new CheckCashMain();
		checkCashMain.setOutNo(doorOrderInfo.getOrderId());
		checkCashService.deleteMain(checkCashMain);
	}

	/**
	 * 获取凭条金额明细
	 *
	 * @author XL
	 * @version 2019年7月25日
	 * @param id
	 * @param tickertape
	 */
	public List<DoorOrderAmount> getDetailList(String id, String detailId) {
		return doorOrderAmountDao.getDetailList(id, detailId);
	}

	/**
	 * gzd 2020-05-27 交易报表功能移植 凭条获取单条存款数据
	 * 
	 * @param tickerTape
	 * @return
	 */
	public DoorOrderDetail getDetailByTickerTape(String tickerTape) {
		List<DoorOrderDetail> detailByTickerTape = doorOrderDetailDao.getDetailByTickerTape(tickerTape);
		if (detailByTickerTape != null && detailByTickerTape.size() != 0) {
			return detailByTickerTape.get(0);
		}
		return null;
	}

	
	/**
	 * 缴存流水 获取凭条信息
	 * 
	 * @author ZXk 
	 * @version 2020-05-27 
	 * @param tickerTape
	 * @return
	 */
	public DoorOrderDetail getTickrtapeInfo(String tickerTape){
		
		DoorOrderDetail doorOrderDetail = doorOrderDetailDao.getTickrtapeInfo(tickerTape);
		if(doorOrderDetail == null){
			doorOrderDetail = new DoorOrderDetail();
		}
		return doorOrderDetail;
	}
	
	/**
	 * 根据机具状态获取对应存款信息
	 *
	 * Description: 
	 *
	 * @author: GJ
	 * @param info
	 * @return
	 */
	public List<DoorOrderInfo> getInfoByEqpIds(DoorOrderInfo info) {
		return doorOrderInfoDao.getInfoByEqpIds(info);
	}
}