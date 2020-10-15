package com.coffer.core.modules.sys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.utils.CommonUtils;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.task.QueueManager;
import com.coffer.core.common.utils.SpringContextHolder;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.entity.AutoVaultCommunication;
import com.coffer.core.modules.sys.entity.DbConfigProperty;
import com.coffer.core.modules.sys.entity.MessageScheduleQueue;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.AutoVaultCommunicationService;
import com.coffer.core.modules.sys.service.DbConfigPropertyService;
import com.coffer.core.modules.sys.service.LoginIdSerialNumberService;
import com.coffer.core.modules.sys.service.MessageService;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.external.Parameter;

/**
 * @author Clark
 * @version 2015-01-13
 */
public class SysCommonUtils {

	private static StoEscortInfoService escortInfoService = SpringContextHolder.getBean(StoEscortInfoService.class);

	private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);

	private static OfficeService officeService = SpringContextHolder.getBean(OfficeService.class);

	private static LoginIdSerialNumberService loginIdSerialNumberService = SpringContextHolder
			.getBean(LoginIdSerialNumberService.class);

	private static MessageService messageService = SpringContextHolder.getBean(MessageService.class);

	private static QueueManager queueManager = SpringContextHolder.getBean(QueueManager.class);

	private static StoRouteInfoService routeInfoService = SpringContextHolder.getBean(StoRouteInfoService.class);

	private static DbConfigPropertyService dbConfigPropertyService = SpringContextHolder
			.getBean(DbConfigPropertyService.class);

	private static AutoVaultCommunicationService autoVaultCommunicationService = SpringContextHolder
			.getBean(AutoVaultCommunicationService.class);

	/**
	 * 保存交接相关信息
	 * 
	 * @param user
	 */
	public static void saveEscortInfo(User user) {
		if (user != null && StringUtils.isNotBlank(user.getIdcardNo())) {

			StoEscortInfo stoEscortInfo = escortInfoService.findByIdcardNo(user.getIdcardNo());
			if (stoEscortInfo == null) {
				stoEscortInfo = new StoEscortInfo();
			}

			stoEscortInfo.setUser(user);
			stoEscortInfo.setEscortName(user.getName());
			stoEscortInfo.setIdcardNo(user.getIdcardNo());
			stoEscortInfo.setOffice(user.getOffice());
			stoEscortInfo.setPhone(user.getMobile());
			stoEscortInfo.setUserFaceId(user.getUserFaceId());
			StoreCommonUtils.saveBankEscort(stoEscortInfo);
		}
	}

	/**
	 * 
	 * Title: findOfficeById
	 * <p>
	 * Description: 根据机构ID查询机构信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId
	 *            机构ID
	 * @return Office 返回类型
	 */
	public static Office findOfficeById(String officeId) {
		return officeService.get(officeId);
	}

	/**
	 * 删除交接相关信息
	 * 
	 * @param userId
	 */
	public static void deleteEscortInfo(String userId) {
		if (StringUtils.isNotBlank(userId)) {
			StoreCommonUtils.deleteBankEscort(userId);
		}
	}

	/**
	 * 
	 * Title: updateByIdcardNo
	 * <p>
	 * Description: 通过身份证号更新用户信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param user
	 *            用户信息
	 * @return int 返回类型
	 */
	@Transactional(readOnly = false)
	public static int updateByIdcardNo(User user) {
		return systemService.updateByIdcardNo(user);
	}

	/**
	 * 根据不同机构，生成登陆用户名
	 * 
	 * @author WangBaozhong
	 * @version 2017年5月31日
	 * 
	 * 
	 * @param officeId
	 *            所属机构ID
	 * @return 登陆用户名
	 */
	public static synchronized String createLoginName(String officeId) {

		// 自增序列长度
		int seqLength = Integer.parseInt(Global.getConfig("user.login.serialNo.seqLength"));
		// 当前序列号
		int seqNo = loginIdSerialNumberService.getSerialNumber(officeId);

		return officeId + CommonUtils.fillSeqNo(seqNo, seqLength);
	}

	/**
	 * 
	 * Title: findUserInfoByOfficeId
	 * <p>
	 * Description: 按照机构ID及上次查询日期查询用户信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId
	 *            机构ID
	 * @param lastSearchDate
	 *            上次查询日期
	 * @return 用户列表 List<User> 返回类型
	 */
	@Transactional(readOnly = true)
	public static List<User> findUserInfoByOfficeId(String officeId, String lastSearchDate) {
		return systemService.findUserInfoByOfficeId(officeId, lastSearchDate);
	}

	/**
	 * 
	 * Title: findUserInfoByOfficeIds
	 * <p>
	 * Description: 按照机构ID列表及上次查询日期查询用户信息
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param lastSearchDate
	 *            上次查询日期
	 * @param officeIds
	 *            机构列表
	 * @return 用户信息列表 List<User> 返回类型
	 */
	@Transactional(readOnly = true)
	public static List<User> findUserInfoByOfficeIds(String lastSearchDate, List<String> officeIds) {
		return systemService.findUserInfoByOfficeIds(lastSearchDate, officeIds);
	}

	/**
	 * 
	 * Title: findOfficeList
	 * <p>
	 * Description: 查询当前机构及其下属机构
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param office
	 *            当前机构信息
	 * @return 当前机构及其下属机构列表 List<Office> 返回类型
	 */
	@Transactional(readOnly = true)
	public static List<Office> findOfficeList(Office office) {

		return officeService.findList(office);
	}

	/**
	 * 
	 * Title: findOfficeListForInterface
	 * <p>
	 * Description:按照参数条件查询本机构及其下属机构信息列表
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param officeId
	 *            本机构ID
	 * @param parentIds
	 *            本机构的所有父ID
	 * @param lastSearchDate
	 *            上次查询日期
	 * @return List<Office> 返回类型
	 */
	@Transactional(readOnly = true)
	public static List<Office> findOfficeListForInterface(String officeId, String parentIds, String lastSearchDate) {
		return officeService.findOfficeListForInterface(officeId, parentIds, lastSearchDate);
	}

	/**
	 * 推送消息
	 * 
	 * @author: yanbingxu
	 * @param messageType
	 *            合并后的消息类型
	 * @param params
	 *            消息内容参数
	 * @param targetOfficeId
	 *            目标机构
	 * @param createUser
	 *            发送人
	 * @version 2017-9-27
	 */
	@Transactional(readOnly = false)
	public static void pushMessage(MessageScheduleQueue schedule) {
		messageService.pushMessage(null, schedule.getMessageType(), schedule.getParams(), schedule.getTargetOfficeId(),
				schedule.getCreateUser());
	}

	/**
	 * 调拨消息添加至对列
	 * 
	 * @author: yanbingxu
	 * @param businessType
	 *            业务类型
	 * @param businessStatus
	 *            业务状态
	 * @param params
	 *            消息内容参数
	 * @param targetOfficeId
	 *            目标机构
	 * @param createUser
	 *            发送人
	 * @version 2017-11-15
	 */
	public static void allocateMessageQueueAdd(String businessType, String businessStatus, List<String> params,
			String targetOfficeId, User createUser) {
		addMessageQueue(Constant.MessageType.ALLOCATION, businessType, businessStatus, params, targetOfficeId,
				createUser);
	}

	/**
	 * 清分消息添加至对列
	 * 
	 * @author: yanbingxu
	 * @param businessType
	 *            业务类型
	 * @param businessStatus
	 *            业务状态
	 * @param params
	 *            消息内容参数
	 * @param targetOfficeId
	 *            目标机构
	 * @param createUser
	 *            发送人
	 * @version 2017-11-15
	 */
	public static void clearMessageQueueAdd(String businessType, String businessStatus, List<String> params,
			String targetOfficeId, User createUser) {
		addMessageQueue(Constant.MessageType.CLEAR, businessType, businessStatus, params, targetOfficeId, createUser);
	}

	/**
	 * 存款异常消息添加至对列
	 * 
	 * @author: huzhiyong
	 * @param businessType
	 *            业务类型
	 * @param businessStatus
	 *            业务状态
	 * @param params
	 *            消息内容参数
	 * @param targetOfficeId
	 *            目标机构
	 * @param createUser
	 *            发送人
	 * @version 2020-03-02
	 */
	public static void exceptionMessageQueueAdd(String businessType, String businessStatus, List<String> params,
			String targetOfficeId, User createUser) {
		addMessageQueue(Constant.MessageType.EXCEPTION, businessType, businessStatus, params, targetOfficeId,
				createUser);
	}

	/**
	 * 机具报警消息添加至对列
	 * 
	 * @author: huzhiyong
	 * @param businessType
	 *            业务类型
	 * @param businessStatus
	 *            业务状态
	 * @param params
	 *            消息内容参数
	 * @param targetOfficeId
	 *            目标机构
	 * @param createUser
	 *            发送人
	 * @version 2020-03-04
	 */
	public static void equWarnMessageQueueAdd(String businessType, String businessStatus, List<String> params,
			String targetOfficeId, User createUser) {
		addMessageQueue(Constant.MessageType.EXCEPTION, businessType, businessStatus, params, targetOfficeId,
				createUser);
	}

	/**
	 * 
	 * Title: addMessageQueue
	 * <p>
	 * Description: 消息添加至对列
	 * </p>
	 * 
	 * @author: wangbaozhong
	 * @param msgBusiType
	 * @param businessType
	 * @param businessStatus
	 * @param params
	 * @param targetOfficeId
	 * @param createUser
	 *            void 返回类型
	 */
	public static void addMessageQueue(String msgBusiType, String businessType, String businessStatus,
			List<String> params, String targetOfficeId, User createUser) {
		String messageType = messageTypeMake(msgBusiType, businessType, businessStatus);

		if (StringUtils.isEmpty(messageType)) {
			return;
		}

		MessageScheduleQueue schedule = new MessageScheduleQueue();
		schedule.setCreateUser(createUser);
		schedule.setMessageType(messageType);
		schedule.setParams(params);
		schedule.setTargetOfficeId(targetOfficeId);
		queueManager.put(schedule);
	}

	/**
	 * 根据菜单编号获取菜单名称
	 * 
	 * @author: yanbingxu
	 * @param menuId
	 *            菜单编号
	 * @version 2017-9-27
	 * @return
	 */
	@Transactional(readOnly = true)
	public static String getMenuById(String menuId) {
		return systemService.getMenu(menuId).getName();
	}

	/**
	 * 
	 * Title: messageTypeMake
	 * <p>
	 * Description: 消息类型拼接
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param messageType
	 * @param businessType
	 * @param businessStatus
	 * @return String 返回类型
	 */
	public static String messageTypeMake(String messageType, String businessType, String businessStatus) {
		if (StringUtils.isNotBlank(messageType) && StringUtils.isNotBlank(businessType)
				&& StringUtils.isNotBlank(businessStatus)) {
			return messageType + businessType + businessStatus;
		}
		return null;
	}

	/**
	 * 
	 * Title: gpsLocationQueueAdd
	 * <p>
	 * Description: 车辆实时位置记录到队列
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param location
	 *            void 返回类型
	 */
	public static void gpsLocationQueueAdd(Map<String, String> location) {
		queueManager.gpsPut(location);
	}

	/**
	 * 
	 * Title: gpsLocationSend
	 * <p>
	 * Description: 车辆实时位置保存并发送至前台
	 * </p>
	 * 
	 * @author: yanbingxu void 返回类型
	 */
	@Transactional(readOnly = false)
	public static void gpsLocationSend(Map<String, String> location) {
		StoRouteInfo route = new StoRouteInfo();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		route.setTaskFlag(location.get(Parameter.TASK_FLAG_KEY).toString());
		route.setCarNo(location.get(Parameter.CAR_NO_KEY).toString());
		route.setRoutePlanId(location.get(Parameter.ROUTE_PLAN_ID_KEY).toString());
		route.setRouteLnglat(location.get(Parameter.LONGITUDE_KEY).toString() + Constant.Punctuation.COMMA
				+ location.get(Parameter.LATITUDE_KEY).toString());
		try {
			route.setCreateDate(dateFormat.parse(location.get(Parameter.UPLOAD_DATE_KEY)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		route.setCarSpeed(location.get(Parameter.SPEED_KEY).toString());
		routeInfoService.saveCarLocation(route);
		messageService.gpsLocationSend(location);
	}

	/**
	 * 
	 * Title: findByUpdateDate
	 * <p>
	 * Description: 根据更新时间查询所有参数
	 * </p>
	 * 
	 * @author: lihe
	 * @version 2018-1-23
	 * @param dbConfigProperty
	 * @return List<DbConfigProperty> 返回类型
	 */
	public static List<DbConfigProperty> findByUpdateDate(DbConfigProperty dbConfigProperty) {
		return dbConfigPropertyService.findByUpdateDate(dbConfigProperty);
	}

	/**
	 * 根据机构id取得机构名称
	 * 
	 * @author SongYuanYang
	 * @version 2018年3月15日
	 * 
	 *          获取机构名称
	 * @param officeId
	 * @return String
	 */
	public static String getOfficeNameById(String officeId) {
		return officeService.getOfficeNameById(officeId);
	}

	/**
	 * 
	 * Title: updateCommunication
	 * <p>
	 * Description: 更新通信信息
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param autoVaultCommunication
	 *            void 返回类型
	 */
	public static void updateCommunication(AutoVaultCommunication autoVaultCommunication) {
		autoVaultCommunicationService.save(autoVaultCommunication);
	}

	/**
	 * 
	 * Title: findFailedCommunicationList
	 * <p>
	 * Description: 查找失败的通讯信息
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @return List<AutoVaultCommunication> 返回类型
	 */
	public static List<AutoVaultCommunication> findFailedCommunicationList() {

		AutoVaultCommunication autoVaultCommunication = new AutoVaultCommunication();
		autoVaultCommunication.setStatus(Constant.CommunicationStatus.FAIL);
		autoVaultCommunication.getSqlMap().put("dsf", "AND rownum <= " + Global.getConfig("auto.vault.max"));
		return autoVaultCommunicationService.findList(autoVaultCommunication);
	}

	/**
	 * 
	 * Title: dayReportMessageQueueAdd
	 * <p>
	 * Description: 定时日结消息添加至队列
	 * </p>
	 * 
	 * @author: lihe
	 * @version 2020-06-12
	 * @param businessType
	 * @param businessStatus
	 * @param params
	 * @param targetOfficeId
	 * @param createUser
	 *            void 返回类型
	 */
	public static void dayReportMessageQueueAdd(String businessType, String businessStatus, List<String> params,
			String targetOfficeId, User createUser) {
		addMessageQueue(Constant.MessageType.DAY_REPORT, businessType, businessStatus, params, targetOfficeId,
				createUser);
	}
}
