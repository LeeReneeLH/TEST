package com.coffer.businesses.modules.doorOrder.v01;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorOrderExceptionDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.DoorOrderExceptionDetailDao;
import com.coffer.businesses.modules.doorOrder.v01.dao.EquipmentInfoDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearAddMoney;
import com.coffer.businesses.modules.doorOrder.v01.entity.CompanyAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DayReportDoorMerchan;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorCenterAccountsMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorDayReportMain;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderException;
import com.coffer.businesses.modules.doorOrder.v01.entity.DoorOrderExceptionDetail;
import com.coffer.businesses.modules.doorOrder.v01.entity.EquipmentInfo;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearAddMoneyService;
import com.coffer.businesses.modules.doorOrder.v01.service.CompanyAccountsMainService;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorAndSevenCodeService;
import com.coffer.businesses.modules.doorOrder.v01.service.DayReportDoorMerchanService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorCenterAccountsMainService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorDayReportCenterService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorDayReportGuestAndSevenCodeService;
import com.coffer.businesses.modules.doorOrder.v01.service.DoorDayReportGuestService;
import com.coffer.businesses.modules.doorOrder.v01.service.EquipmentInfoService;
import com.coffer.businesses.modules.store.v01.dao.StoDictDao;
import com.coffer.businesses.modules.store.v01.entity.StoDict;
import com.coffer.businesses.modules.weChat.v03.dao.DoorOrderInfoDao;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.IdGen;
import com.coffer.core.common.utils.RestUtils;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StreamUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.dao.UserDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.external.Parameter;
import com.coffer.external.service.Service0805;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 共同处理类(商户收款)
 * 
 * @author wqj
 * @version 2019-07-02
 */
public class DoorCommonUtils extends BusinessUtils {

	private static Logger logger = LoggerFactory.getLogger("AccountChecking");
	private static Logger loggerTraditional = LoggerFactory.getLogger("TraditionalReport");

	/** Json实例对象 */
	protected static Gson gson = new GsonBuilder().create();

	/** 中心账务管理-服务层 **/
	private static DoorCenterAccountsMainService centerAccountsMainService = SpringContextHolder
			.getBean(DoorCenterAccountsMainService.class);
	/** 账务日结管理-服务层 **/
	private static DoorDayReportCenterService dayReportMainService = SpringContextHolder
			.getBean(DoorDayReportCenterService.class);

	/** 账务日结管理-服务层 **/
	private static DoorDayReportGuestService dayReportGuestService = SpringContextHolder
			.getBean(DoorDayReportGuestService.class);
	/** 公司账务管理-服务层 **/
	private static CompanyAccountsMainService companyAccountsMainService = SpringContextHolder
			.getBean(CompanyAccountsMainService.class);
	/** 商户日结-服务层 **/
	private static DayReportDoorMerchanService dayReportDoorMerchanService = SpringContextHolder
			.getBean(DayReportDoorMerchanService.class);
	/** 机具-服务层 **/
	private static EquipmentInfoService equipmentInfoService = SpringContextHolder.getBean(EquipmentInfoService.class);
	/** 清机加钞-服务层 **/
	private static ClearAddMoneyService clearAddMoneyService = SpringContextHolder.getBean(ClearAddMoneyService.class);
	/** 客户单独结算-服务层（张家港修改部分） **/
	private static DoorDayReportGuestAndSevenCodeService doorDayReportGuestAndSevenCodeService = SpringContextHolder
			.getBean(DoorDayReportGuestAndSevenCodeService.class);
	/** 七位码结算-服务层（张家港修改部分） **/
	private static DayReportDoorAndSevenCodeService dayReportDoorAndSevenCodeService = SpringContextHolder
			.getBean(DayReportDoorAndSevenCodeService.class);

	private static DoorOrderInfoDao doorOrderInfoDao = SpringContextHolder.getBean(DoorOrderInfoDao.class);
	private static Service0805 service0805 = SpringContextHolder.getBean(Service0805.class);
	private static DoorOrderExceptionDao doorOrderExceptionDao = SpringContextHolder
			.getBean(DoorOrderExceptionDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static EquipmentInfoDao equipmentInfoDao = SpringContextHolder.getBean(EquipmentInfoDao.class);
	private static StoDictDao stoDictDao = SpringContextHolder.getBean(StoDictDao.class);
	private static DoorOrderExceptionDetailDao doorOrderExceptionDetailDao = SpringContextHolder
			.getBean(DoorOrderExceptionDetailDao.class);

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 中心账务余额判断
	 * 
	 * @param businessType
	 * @param clientId
	 * @param outAmount
	 * @return
	 */
	public static String getNewestStoreInfo(String businessType, String clientId, BigDecimal outAmount,
			String rofficeId) {
		return centerAccountsMainService.getNewestStoreInfo(businessType, clientId, outAmount, rofficeId);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月5日 中心账务主表添加
	 * 
	 * @param centerAccountsMain
	 */
	public static void insertAccounts(DoorCenterAccountsMain centerAccountsMain) {
		centerAccountsMainService.insertAccounts(centerAccountsMain);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月4日 公司账务主表添加
	 * 
	 * @param centerAccountsMain
	 */
	public static void insertCompanyAccounts(CompanyAccountsMain companyAccountsMain) {
		companyAccountsMainService.insertCompanyAccounts(companyAccountsMain);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 中心账务日结
	 *
	 */
	public static void dayCenterAccountsReport(String windupType, DoorDayReportMain dayReportMain, Office office) {
		dayReportMainService.dayAccountReportByCenter(windupType, dayReportMain, office);
	}

	/**
	 * 
	 * @author qipeihong
	 * @version 2017年9月6日 客户账务日结
	 *
	 */
	public static void dayGuestAccountsReport(String windupType, DoorDayReportMain dayReportMain, Office office) {
		dayReportGuestService.dayAccountReportByGuest(windupType, dayReportMain, office);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月3日 获取最近一次账务日结时间
	 * 
	 * @return
	 */
	public static Date getDayReportMaxDate(Office office) {
		return dayReportMainService.getDayReportMaxDate(office);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月3日 获取本次日结的上一次日结时间
	 * 
	 * @return
	 */
	public static Date getThisDayReportMaxDate(Office office, Date date) {
		return dayReportMainService.getThisDayReportMaxDate(office, date);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月16日 获取客户余额
	 * 
	 * @return
	 */
	public static BigDecimal getGuestTotalAmount(DoorCenterAccountsMain centerAccountsMain) {
		return centerAccountsMainService.getGuestTotalAmount(centerAccountsMain);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月23日 商户结算
	 * 
	 * @return
	 */
	public static void dayMerchanAccountsReport(String windupType, DoorDayReportMain dayReportMain, Office office) {
		dayReportDoorMerchanService.dayAccountReportByMerchan(windupType, dayReportMain, office);
	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年7月23日 获取公司账户余额
	 * 
	 * @return
	 */
	public static BigDecimal getCompanyTotalAmount(String companyId) {
		return companyAccountsMainService.getCompanyTotalAmount(companyId);
	}

	/**
	 * 
	 * @author zhr
	 * @version 2019年7月31日 根据机具编号获取机具相关信息，包括基本信息，余额，距上次清机时间
	 * 
	 * @return
	 */
	public static EquipmentInfo getTimeDistanceLastTime(String equipmentId, String id) {
		EquipmentInfo equipmentInfo = equipmentInfoService.get(equipmentId);
		List<ClearAddMoney> clearAddMoney = clearAddMoneyService.getByEquipmentId(equipmentId,
				DoorOrderConstant.ClearStatus.CLEAR, id);
		// ClearPlanInfo
		// clearPlanInfo=clearPlanInfoService.getByEquipmentId(equipmentId).get(0);
		Date d1;
		if (Collections3.isEmpty(clearAddMoney)) {
			d1 = new Date();
			Date d2 = new Date();
			long diff = d2.getTime() - d1.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			equipmentInfo.setDistanceLastTime(days + "天");
			return equipmentInfo;
		} else {
			equipmentInfo.setSurplusAmount(clearAddMoney.get(0).getSurplusAmount() + "");
			d1 = clearAddMoney.get(0).getUpdateDate();
			Date d2 = new Date();
			long diff = d2.getTime() - d1.getTime();
			long days = diff / (1000 * 60 * 60 * 24);
			equipmentInfo.setDistanceLastTime(days + "天");
			return equipmentInfo;
		}

	}

	/**
	 * 
	 * @author wqj
	 * @version 2019年8月1日 商户汇款现金风险阈值
	 * 
	 * @return
	 */
	public static BigDecimal cashRiskThreshold(String merchantId) {
		return dayReportDoorMerchanService.getCashRiskThreshold(merchantId);
	}

	/**
	 * 显示照片
	 *
	 * @author XL
	 * @version 2019年8月29日
	 * @param object
	 * @param fieldName
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void showImage(Object object, String fieldName, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		OutputStream out = response.getOutputStream();
		InputStream inputStream = null;
		byte[] imageBytes = null;
		try {
			if (object != null) {
				// 获取图片
				String firstLetter = fieldName.substring(0, 1).toUpperCase();
				String getter = "get" + firstLetter + fieldName.substring(1);
				Method method = object.getClass().getMethod(getter, new Class[] {});
				Object value = method.invoke(object, new Object[] {});
				imageBytes = (byte[]) value;
				if (imageBytes == null) {
					// 将文件转成byte[]
					inputStream = request.getSession().getServletContext()
							.getResourceAsStream(Global.getConfig("image.escort.path"));
					if (inputStream == null) {
						throw new IOException();
					}
					imageBytes = StreamUtils.InputStreamTOByte(inputStream);
				}
				out.write(imageBytes);
				out.flush();
			}
		} catch (Exception e) {
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} finally {
			}
			out.close();
		}
	}

	/**
	 * 客户按自己指定的时间结算（张家港修改部分）
	 * 
	 * @author wqj
	 * @version 2019年12月11日
	 * 
	 * @return
	 */
	public static void doorDayReportGuestAndSevenCode(String windupType, DoorDayReportMain dayReportMain,
			Office office) {
		doorDayReportGuestAndSevenCodeService.dayAccountReportByGuestAndSevenCode(windupType, dayReportMain, office);
	}

	/**
	 * 七位码结算（张家港修改部分）
	 * 
	 * @author wqj
	 * @version 2019年12月11日
	 * 
	 * @return
	 */
	public static void dayReportDoorAndSevenCode(String windupType, DoorDayReportMain dayReportMain, Office office) {
		dayReportDoorAndSevenCodeService.dayReportByDoorAndSevenCode(windupType, dayReportMain, office);
	}

	/**
	 * 定时调度任务调用共同方法
	 * 
	 * @author wqj
	 * @version 2019年12月12日
	 */
	public static synchronized void scheduledTask(Office office, Date date) {
		// 张家港商户走此结算逻辑
		if (office.getParentIds().contains(Global.getConfig("officeId.zhangjiagang"))) {
			// 商户结算参数
			DoorDayReportMain dayReportForMerchan = new DoorDayReportMain();
			// 设置日结主键
			dayReportForMerchan.setReportId(IdGen.uuid());
			// 设置日结时间
			dayReportForMerchan.setReportDate(date);
			// 门店结算（用于页面展示）
			DoorCommonUtils.dayReportDoorAndSevenCode(ClearConstant.WindupType.WINDUP_AUTO, dayReportForMerchan,
					office);
			// 门店结算（用于存储数据）
			DoorCommonUtils.doorDayReportGuestAndSevenCode(ClearConstant.WindupType.WINDUP_AUTO, dayReportForMerchan,
					office);
		} else {
			// 商户结算参数
			DoorDayReportMain dayReportForMerchan = new DoorDayReportMain();
			// 设置日结主键
			dayReportForMerchan.setReportId(IdGen.uuid());
			// 设置日结时间
			dayReportForMerchan.setReportDate(date);
			// 门店结算（用于页面展示）
			DoorCommonUtils.dayMerchanAccountsReport(ClearConstant.WindupType.WINDUP_AUTO, dayReportForMerchan, office);
			// 门店结算（用于存储数据）
			DoorCommonUtils.doorDayReportGuestAndSevenCode(ClearConstant.WindupType.WINDUP_AUTO, dayReportForMerchan,
					office);
		}
	}

	/**
	 * 系统间对账方法
	 *
	 * @param office
	 *            商户机构
	 * @param date
	 *            当前系统时间
	 */
	public static synchronized void accountChecking(Office office, Date date) {
		logger.info("{}:{} 机构对账开始", office.getId(), office.getName());
		// 当前结算时间前24小时时间戳
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 当前时间减去一天
		calendar.add(Calendar.DATE, -1);
		// 接口传入数据组装
		Map<String, Object> cMap = new HashMap<>();
		List<String> strJson = new ArrayList<>();
		// 获取商户下所有设备列表
		List<EquipmentInfo> equipmentByMerchantList = equipmentInfoService.findEquipmentByMerchant(office);
		// 商户下没有设备，不必对账，直接跳出
		if (Collections3.isEmpty(equipmentByMerchantList)) {
			logger.info("{}:{} 机构无设备，跳过对账", office.getId(), office.getName());
			return;
		}
		// 构造设备列表数组
		for (EquipmentInfo equipmentInfo : equipmentByMerchantList) {
			strJson.add(equipmentInfo.getId());
		}
		// 获取设备日结总金额接口参数
		cMap.put(DoorOrderConstant.CheckAccount.EQUIPMENTIDS, strJson);
		cMap.put(DoorOrderConstant.CheckAccount.ENDTIME, DateUtils.formatDateTime(date));
		// 调用鞍山服务接口，获取各机具結算金額
		Map<String, Object> resultMapAll = RestUtils.sendPostRequest(gson.toJson(cMap),
				Global.getConfig("anshan.url.loadEqpsAmount"));
		List<Map<String, Object>> dataListAll = (List<Map<String, Object>>) resultMapAll
				.get(DoorOrderConstant.CheckAccount.DATA);
		// 平台需要对账数据搜索
		DoorCenterAccountsMain centerAccountsMain = new DoorCenterAccountsMain();
		// 设置开始时间(为了与鞍山同步，开始时间设置为日结时间-24小时)
		centerAccountsMain.setSearchDateStart(DateUtils.formatDateTime(calendar.getTime()));
		// 设置结束时间
		centerAccountsMain.setSearchDateEnd(DateUtils.formatDateTime(date));
		// 设置业务类型
		List<String> businessTypeList = Lists.newArrayList();
		// 存款账务
		businessTypeList.add(DoorOrderConstant.BusinessType.DOOR_ORDER);
		centerAccountsMain.setBusinessTypes(businessTypeList);
		// 设置发生机构
		centerAccountsMain.setMerchantOfficeId(office.getId());
		// 大连系统中当天所有存款账务记录
		List<DoorCenterAccountsMain> accountsListForReport = centerAccountsMainService.findList(centerAccountsMain);
		// 每个设备对账
		for (Map<String, Object> map : dataListAll) {
			// 鞍山返回参数机具金额判断，当天存款金额为0的设备不对账，继续对下一个设备
			if (new BigDecimal(map.get(DoorOrderConstant.CheckAccount.AMOUNT).toString())
					.compareTo(BigDecimal.ZERO) == 0) {
				logger.info("设备【{}】：鞍山系统存款金额为0，无需对账", map.get(DoorOrderConstant.CheckAccount.EQPID));
				continue;
			}
			// 设置机具ID
			centerAccountsMain.setEquipmentId((String) map.get(DoorOrderConstant.CheckAccount.EQPID));
			// 按日结区间从平台找寻数据，并按机具汇总
			List<DoorCenterAccountsMain> listTotalAmount = centerAccountsMainService
					.getTotalAmountEquipId(centerAccountsMain);
			// 验证机具总金额 如果没有数据则添加一个null对象
			if (Collections3.isEmpty(listTotalAmount)) {
				listTotalAmount.add(new DoorCenterAccountsMain());

			}
			// 验证机具对总金额是否为null 是则添加一个为0总金额
			if (listTotalAmount.get(0).getGuestTotalAmount() == null) {
				listTotalAmount.get(0).setGuestTotalAmount(new BigDecimal(0));
			}
			logger.info("设备【{}】：鞍山系统存款金额为{}，大连系统金额为{}", map.get(DoorOrderConstant.CheckAccount.EQPID),
					map.get(DoorOrderConstant.CheckAccount.AMOUNT), listTotalAmount.get(0).getGuestTotalAmount());
			// 金额比对相同且笔数相同，跳过对账
			if (listTotalAmount.get(0).getGuestTotalAmount()
					.compareTo(new BigDecimal(map.get(DoorOrderConstant.CheckAccount.AMOUNT).toString())) == 0) {
				logger.info("总金额相同，无需对账");
				continue;
			}
			// 当天设备没有存款账务数据
			// 找寻鞍山系统该机具当天详细数据，补做存款
			// 有问题的机具id信息列表
			List<String> reasonIdList = new ArrayList<>();
			Map<String, Object> reasonMap = new HashMap<>();
			reasonIdList.add((String) map.get(DoorOrderConstant.CheckAccount.EQPID));
			reasonMap.put(DoorOrderConstant.CheckAccount.EQUIPMENTIDS, reasonIdList);
			reasonMap.put(DoorOrderConstant.CheckAccount.ENDTIME, DateUtils.formatDateTime(date));
			// 调用鞍山服务接口，获取机具存款明细
			Map<String, Object> resultMapEach = RestUtils.sendPostRequest(gson.toJson(reasonMap),
					Global.getConfig("anshan.url.loadEqpsDetail"));
			// 该机具当天的存款明细
			List<Map<String, Object>> dataListEach = (List<Map<String, Object>>) resultMapEach
					.get(DoorOrderConstant.CheckAccount.DATA);
			// 遍历每笔存款明细
			for (Map<String, Object> mapEach : dataListEach) {
				// 标志位，代表该笔存款是否在平台账务中存在
				int flag = 0;
				// 取出凭条号，根据凭条从平台当天日结账务数据中寻找。
				String tick = (String) mapEach.get(DoorOrderConstant.ServiceParameter.TICKER_TAPE);
				for (DoorCenterAccountsMain doorForReport : accountsListForReport) {
					if (doorForReport.getTickertape().equals(tick)) {
						logger.info("---------------------系统找到批次：【{}】，跳过补入---------------------", tick);
						flag = 1;
						break;
					}
				}
				// 该笔存款不存在，判断能否补做存款，不能,加入存款异常
				if (flag == 0) {
					// 根据设备，包号两个参数查询该包状态
					logger.info("---------------------本地未找到匹配存款批次：【{}】，即将补入---------------------", tick);
					DoorOrderInfo doorOrderInfo = new DoorOrderInfo();
					doorOrderInfo.setEquipmentId((String) mapEach.get(DoorOrderConstant.ServiceParameter.EQUIPMENT_ID));
					doorOrderInfo.setRfid((String) mapEach.get(DoorOrderConstant.ServiceParameter.BAG_NO));
					List<DoorOrderInfo> orderInfoList = doorOrderInfoDao.findList(doorOrderInfo);
					mapEach.put(Parameter.ORDER_FROM, Service0805.FROM_ACCOUNT_CHECKING);
					// 未找到该歀袋，为更换歀袋后的第一笔存款，正常补录
					if (Collections3.isEmpty(orderInfoList)) {
						logger.info("---------------------【{}】第一笔存款，正常补入---------------------", tick);
					}
					// 该款袋在用状态，正常补录
					else if (orderInfoList.get(0).getStatus().equals(DoorOrderConstant.Status.REGISTER)) {
						logger.info("---------------------【{}】款袋在用，正常补入---------------------", tick);
					}
					// 该款袋已收回,按已拆未拆分情况处理
					else if (orderInfoList.get(0).getStatus().equals(DoorOrderConstant.Status.CONFIRM)
							|| orderInfoList.get(0).getStatus().equals(DoorOrderConstant.Status.CLEAR)) {
						logger.info("---------------------【{}】补入到已收回钞袋存款---------------------", tick);
					}
					// 以上三种情况以外不补入，基本不会发生
					else {
						logger.info("---------------------【{}】情况不明无法通过对账补入---------------------", tick);
						return;
					}
					try {
						service0805.execute(mapEach);
					} catch (BusinessException e) {
						logger.info("---------------------【{}】补入失败，存入异常，异常原因为：{}---------------------", tick,
								e.getMessageContent());
						saveException(mapEach, e.getMessageContent());
					}
				}
			}
		}
	}

	/**
	 *
	 * 存款异常信息保存 WQJ 2019-11-14
	 *
	 * @param paramMap,errorMessage
	 */
	@SuppressWarnings("unchecked")
	public static synchronized void saveException(Map<String, Object> paramMap, String errorMessage) {
		// 存款异常实体用于保存
		DoorOrderException doorOrderException = new DoorOrderException();
		// 接口传入信息
		String eqpId = (String) paramMap.get(Parameter.EQUIPMENT_ID_KEY);
		String bagNo = (String) paramMap.get(Parameter.BAG_NO_KEY);
		String businessType = (String) paramMap.get(Parameter.BUSINESS_TYPE_KEY);
		Double totalAmount = (Double) paramMap.get(Parameter.TOTAL_AMOUNT_KEY);
		String tickerTape = (String) paramMap.get(Parameter.TICKER_TAPE_KEY);
		String userId = (String) paramMap.get(Parameter.USER_ID_KEY);
		String currency = (String) paramMap.get(Parameter.CURRENCY_KEY);
		String remarks = (String) paramMap.get(Parameter.REMARKS_KEY);
		String startTime = (String) paramMap.get(Parameter.START_TIME_KEY);
		String endTime = (String) paramMap.get(Parameter.END_TIME_KEY);
		String cost = (String) paramMap.get(Parameter.COST_TIME_KEY);
		List<Map<String, Object>> detail = (List<Map<String, Object>>) paramMap.get(Parameter.DETAIL_KEY);
		// 凭条
		doorOrderException.setTickerTape(tickerTape);
		// 每笔存款只能存在一条，防止重复登记
		if (Collections3.isEmpty(doorOrderExceptionDao.findList(doorOrderException))) {
			/** 异常主表数据做成 **/
			// 机具号
			doorOrderException.setEqpId(eqpId);
			// 包号
			doorOrderException.setBagNo(bagNo);
			// 业务类型
			doorOrderException.setBusinessType(businessType);
			// 存款总金额
			doorOrderException.setTotalAmount(totalAmount.toString());
			User user = userDao.get(userId);
			if (user != null) {
				// 用户ID
				doorOrderException.setUser(user);
				// 用户姓名
				doorOrderException.setUserName(user.getName());
			}
			// 状态
			doorOrderException.setStatus(DoorOrderConstant.ExceptionStatus.CONFIRM);
			// 门店ID
			EquipmentInfo equipmentInfo = equipmentInfoDao.get(eqpId);
			if (equipmentInfo != null) {
				doorOrderException.setDoorId(equipmentInfoDao.get(eqpId).getaOffice().getId());
				// 门店名称
				doorOrderException.setDoorName((equipmentInfoDao.get(eqpId).getaOffice().getName()));
			}
			// 异常原因
			doorOrderException.setExceptionReason(errorMessage);
			// 异常类型
			doorOrderException.setExceptionType(DoorOrderConstant.ExceptionType.DATAMISSING);
			// 币种
			doorOrderException.setCurrency(currency);
			// 开始时间，结束时间，耗时
			doorOrderException.setStartTime(DateUtils.parseTimestampToDate(startTime));
			doorOrderException.setEndTime(DateUtils.parseTimestampToDate(endTime));
			doorOrderException.setCostTime(StringUtils.toString(cost));
			// 备注
			doorOrderException.setRemarks(remarks);
			doorOrderException.preInsert();
			doorOrderExceptionDao.insert(doorOrderException);
			/** 异常明细数据做成 **/
			// 行号
			int rowNo = 1;
			// 遍历存款类型
			for (Map<String, Object> detailMap : detail) {
				List<Map<String, Object>> denominationList = (List<Map<String, Object>>) detailMap
						.get(Parameter.DENOMINATION_KEY);
				if (!Collections3.isEmpty(denominationList)) {
					// 面值列表不空，遍历面值列表，每种面值一条记录
					for (Map<String, Object> denomination : denominationList) {
						DoorOrderExceptionDetail doorOrderExceptionDetail = new DoorOrderExceptionDetail();
						// 面值字典校验
						List<String> currencies = Lists.newArrayList();
						currencies.add(Constant.DenominationType.RMB_HDEN);
						currencies.add(Constant.DenominationType.RMB_PDEN);
						Map<String, Object> situation = new HashMap<>();
						situation.put("currencies", currencies);
						situation.put("id", denomination.get("ID"));
						List<StoDict> allDenomination = stoDictDao.findAllDenomination(situation);
						if (!Collections3.isEmpty(allDenomination)) {

							StoDict stoDict = allDenomination.get(0);
							BigDecimal unitVal = stoDict.getUnitVal();
							// 数字校验
							String countStr = CommonUtils.toString(denomination.get("count"));
							int count;
							double countDb = Double.parseDouble(countStr);
							if (countDb % 1 == 0) {
								count = Integer.parseInt(countStr.substring(0, countStr.indexOf(".")));
								// 本面值总额
								BigDecimal detailAmount = unitVal.multiply(new BigDecimal(count));
								doorOrderExceptionDetail.setAmount(detailAmount);
							}
						}
						doorOrderExceptionDetail.setDetailId(IdGen.uuid());
						doorOrderExceptionDetail.setId(doorOrderException.getId());
						doorOrderExceptionDetail.setType((String) detailMap.get("type"));
						doorOrderExceptionDetail.setCount(CommonUtils.toString(denomination.get("count")));
						doorOrderExceptionDetail.setDenomination((String) denomination.get("ID"));
						doorOrderExceptionDetail.setCurrency(currency);
						doorOrderExceptionDetail.setRowNo(rowNo++);
						doorOrderExceptionDetailDao.insert(doorOrderExceptionDetail);
					}
				} else {
					// 没有面值列表为设备不可清点存款类型
					DoorOrderExceptionDetail doorOrderExceptionDetail = new DoorOrderExceptionDetail();
					doorOrderExceptionDetail.setDetailId(IdGen.uuid());
					doorOrderExceptionDetail.setId(doorOrderException.getId());
					doorOrderExceptionDetail.setType((String) detailMap.get("type"));
					doorOrderExceptionDetail.setCurrency(currency);
					doorOrderExceptionDetail.setRowNo(rowNo++);
					Double amountDB = (Double) detailMap.get(Parameter.AMOUNT_KEY);
					doorOrderExceptionDetail.setAmount(new BigDecimal(amountDB.toString()));
					doorOrderExceptionDetailDao.insert(doorOrderExceptionDetail);
				}
			}
			// 判断存款来源 发送对应消息
			String oreder_form = (String) paramMap.get(Parameter.ORDER_FROM);
			String exceptionType = null;
			if (StringUtils.isEmpty(oreder_form)) {
				exceptionType = DoorOrderConstant.ExceptionType.DATAMISSING;
			} else if (oreder_form.equals(Service0805.FROM_EQP) || oreder_form.equals(Service0805.FROM_EXCEPTION)) {
				exceptionType = DoorOrderConstant.ExceptionType.SAVEERROR;
			} else if (oreder_form.equals(Service0805.FROM_ACCOUNT_CHECKING)) {
				exceptionType = DoorOrderConstant.ExceptionType.DATAMISSING;
			}

			/* 发送异常消息通知 hzy 2020/04/20 start */
			List<String> paramsList = Lists.newArrayList();
			// 发送机构名称
			String officeName = null;
			if (user == null) {
				user = new User();
				user.setName(Constant.NoBindOffice.UNKNOWN);
				officeName = Constant.OfficeId.SZYH;// 商资易汇平台
			}
			// 判断机具是否绑定机构
			if (equipmentInfo != null && equipmentInfo.getVinOffice() != null
					&& StringUtils.isNotEmpty(equipmentInfo.getVinOffice().getId())) {
				// 添加 异常通知消息内容参数
				paramsList.add(doorOrderException.getDoorName());// 门店参数
				paramsList.add(doorOrderException.getTickerTape());// 凭条号
				paramsList.add(doorOrderException.getExceptionReason());// 错误类型
				SysCommonUtils.exceptionMessageQueueAdd(Constant.MessageType.EXCEPTIONTYPE, exceptionType, paramsList,
						equipmentInfo.getVinOffice().getId(), user);
			} else {
				// 查询维护中心ID集合
				List<Office> officeList = officeDao.getClearCenterList();
				// 发送机构 如果存款人 有对应信息 发送给对应存款人所在公司的机具维护中心
				if (!Collections3.isEmpty(officeList) && user != null && user.getOffice() != null
						&& StringUtils.isNotEmpty(user.getOffice().getParentIds())) {
					for (Office o : officeList) {
						if (user.getOffice().getParentIds().contains(o.getParentId())) {
							officeName = o.getId();
							break;
						}
					}
				}else{
					officeName = Constant.OfficeId.SZYH;// 商资易汇平台
				}
				// 添加 异常通知消息内容参数 无设备信息
				if (StringUtils.isEmpty(doorOrderException.getDoorName())) {
					paramsList.add(Constant.NoBindOffice.UNKNOWNEQU);// 门店参数
				} else {
					paramsList.add(doorOrderException.getDoorName());// 门店参数
				}
				paramsList.add(doorOrderException.getTickerTape());// 凭条号
				paramsList.add(doorOrderException.getExceptionReason());// 错误类型
				SysCommonUtils.exceptionMessageQueueAdd(Constant.MessageType.EXCEPTIONTYPE, exceptionType, paramsList,
						officeName, user);
			}
			/* 发送异常消息通知 hzy 2020/04/20 end */
		}
	}

	/**
	 * 定时调度任务调用共同方法
	 * 
	 * @author HZY
	 * @version 2020年07月27日
	 */
	public static synchronized void traditionalReportTask(Office office, Date date, User user,
			DayReportDoorMerchan dayReportDoorMerchan) {
		Office o = officeDao.findOfficeByOtherId(office.getId());
		loggerTraditional.info("{}:{} 传统日结开始", o.getId(), o.getName());
		dayReportDoorMerchanService.traditionalSaveReport(o, date, user, dayReportDoorMerchan);
	}
}
