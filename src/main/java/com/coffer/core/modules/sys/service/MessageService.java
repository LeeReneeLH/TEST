package com.coffer.core.modules.sys.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.websocket.SystemWebSocketHandler;
import com.coffer.businesses.modules.store.v01.entity.StoRouteInfo;
import com.coffer.businesses.modules.store.v01.service.StoRouteInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.MessageDao;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.Message;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 系统通知Service
 * 
 * @author yanbingxu
 * @version 2017-9-27
 */
@Service
public class MessageService extends BaseService {

	@Autowired
	private MessageDao messageDao;
	@Autowired
	private SystemWebSocketHandler wshandler;
	@Autowired
	private MessageRelevanceService messageRelevanceService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private StoRouteInfoService routeInfoService;

	private static final String MESSAGESWITCHON = "true";
	private static final String CUSTOMMESSAGE = "custom";
	public static final String CACHE_MESSAGE_MAP = "messageMap";

	/**
	 * 根据通知ID获取通知
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public Message getMessage(String id) {
		return messageDao.get(id);
	}

	/**
	 * 根据用户权限获取通知
	 * 
	 * @return
	 */
	//@Transactional(readOnly = true)
	public Map<String, Object> findMessageByAuthority(Message message) {

		List<Message> messageList = Lists.newArrayList();
		Map<String, Object> resultMap = Maps.newHashMap();
		List<String> messageTopicList = Lists.newArrayList();
		List<String> idList = Lists.newArrayList();
		List<String> menuIdList = Lists.newArrayList();
		List<String> urlList = Lists.newArrayList();
		List<String> createNameList = Lists.newArrayList();
		List<String> createDateList = Lists.newArrayList();

		message.setUserAuthority(UserUtils.getUser().getUserType());
		message.setDateRange(Global.getConfig("showMessage.date.range"));
		message.setMaxNumber(Integer.valueOf(Global.getConfig("showMessage.max.number")));

		// 验证系统配置中通知功能开关的值
		if ((MESSAGESWITCHON).equals(Global.getConfig("sys.message.open"))) {
			// 查询通知
			messageList = messageDao.findMessageByAuthority(message);
			message.setSearchDate(new Date());
		}

		// 前台HTML拼接参数
		for (Message tempMessage : messageList) {
			messageTopicList.add(tempMessage.getMessageTopic());
			idList.add(tempMessage.getId());
			// 非自定义消息时查询关联数据
			if ((CUSTOMMESSAGE).equals(tempMessage.getMessageType())) {
				if (systemService.getMenu(tempMessage.getMessageBody()) != null) {
					urlList.add(systemService.getMenu(tempMessage.getMessageBody()).getHref());
					// 通过控制输入决定数组长度，不用做长度判断
					menuIdList.add(systemService.getMenu(tempMessage.getMessageBody()).getParentIds()
							.split(Constant.Punctuation.COMMA)[2]);
				} else {
					urlList.add(tempMessage.getMessageBody());
					menuIdList.add(tempMessage.getMessageBody());
				}
			} else {
				menuIdList.add(messageRelevanceService.findRelevance(tempMessage.getMessageType()).getMenuId());
				urlList.add(messageRelevanceService.findRelevance(tempMessage.getMessageType()).getUrl());
			}
			createNameList.add(tempMessage.getCreateName());
			createDateList.add(DateUtils.formatDateTime(tempMessage.getCreateDate()));
		}

		resultMap.put("urlList", urlList);
		resultMap.put("idList", idList);
		resultMap.put("menuIdList", menuIdList);
		resultMap.put("messageTopicList", messageTopicList);
		resultMap.put("createNameList", createNameList);
		resultMap.put("createDateList", createDateList);
		resultMap.put("searchDate", message.getSearchDate());
		return resultMap;
	}

	/**
	 * 通知已读记录
	 * 
	 * @param messageId
	 */
	@Transactional(readOnly = false)
	public void insertReadUser(String messageId) {
		Message message = new Message();
		message.preInsert();
		message.setId(messageId);
		messageDao.insertReadUser(message);
	}

	/**
	 * 查询用户权限关联表
	 * 
	 * @param messageId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<String> findUserAuthority(String messageId) {
		return messageDao.findUserAuthority(messageId);
	}

	/**
	 * 查询机构权限关联表
	 * 
	 * @param messageId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<String> findOfficeAuthority(String messageId) {
		return messageDao.findOfficeAuthority(messageId);
	}

	/**
	 * 分页查询
	 * 
	 * @param page
	 * @param message
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<Message> findList(Page<Message> page, Message message) {
		// 设置分页参数
		message.setPage(page);
		List<Message> messageList = Lists.newArrayList();
		// 执行分页查询
		if ((Constant.SysUserType.SYSTEM).equals(UserUtils.getUser().getUserType())) {
			messageList = messageDao.findList(message);
		} else {
			message.setUserAuthority(UserUtils.getUser().getUserType());
			messageList = messageDao.findMessageHistory(message);
		}
		page.setList(messageList);
		return page;
	}

	/**
	 * 保存通知
	 * 
	 * @param message
	 *            消息实体
	 * @param messageType
	 *            消息类型
	 * @param params
	 *            消息主体参数
	 * @param targetOfficeId
	 *            指定接收机构
	 * @param createUser
	 *            发送人
	 */
	@Transactional(readOnly = false)
	public void pushMessage(Message message, String messageType, List<String> params, String targetOfficeId,
			User createUser) {

		// 业务消息
		if (message == null) {
			// 验证系统配置中通知功能开关的值
			if (!(MESSAGESWITCHON).equals(Global.getConfig("sys.message.open"))) {
				return;
			}
			// 验证消息关联配置是否存在
			Message relevance = messageRelevanceService.findRelevance(messageType);
			if (relevance != null && Constant.deleteFlag.Valid.equals(relevance.getDelFlag())) {
				relevance.setMessageType(messageType);
				String strMsgContent = relevance.getMessageConstruct();
				if (!Collections3.isEmpty(params)) {
					for (int iCount = 0; iCount < params.size(); iCount++) {
						strMsgContent = strMsgContent.replace("{" + iCount + "}", params.get(iCount));
					}
				}
				relevance.setMessageBody(strMsgContent);
				relevance.setMessageTopic(strMsgContent);

				relevance.preInsert();
				if (createUser != null) {
					relevance.setCreateBy(createUser);
					relevance.setCreateName(createUser.getName());
				}
				messageDao.insert(relevance);
				// 追加目标机构
				String officeAuthority = null;
				if (StringUtils.isNotEmpty(relevance.getOfficeAuthority())) {
					officeAuthority = relevance.getOfficeAuthority() + targetOfficeId;
				} else {
					officeAuthority = targetOfficeId;
				}
				relevance.setOfficeAuthority(officeAuthority);
				saveAuthority(relevance);
				// 发送websocket消息
				wsExcute(relevance);
				CacheUtils.remove(CACHE_MESSAGE_MAP);
			} else {
				logger.error("----------无效的消息类型----------");
			}
		} else {
			// 通知消息
			message.preInsert();
			message.setMessageType(Constant.MessageType.BROADCAST);
			messageDao.insert(message);
			saveAuthority(message);
			// 验证消息关联配置是否存在
			if (messageRelevanceService.findRelevance(message.getMessageType()) != null && Constant.deleteFlag.Valid
					.equals(messageRelevanceService.findRelevance(message.getMessageType()).getDelFlag())) {
				// 发送websocket消息
				wsExcute(message);
			} else {
				logger.error("----------无效的消息类型----------");
			}
		}
	}

	/**
	 * 更新通知
	 * 
	 * @param message
	 */
	@Transactional(readOnly = false)
	public void update(Message message) {
		messageDao.update(message);
		wsExcute(message);
	}

	/**
	 * 保存机构权限和用户权限
	 * 
	 * @param message
	 */
	@Transactional(readOnly = false)
	public void saveAuthority(Message message) {
		Message tempMsg = new Message();
		tempMsg.setId(message.getId());
		// 记录用户权限
		if (StringUtils.isNotBlank(message.getUserAuthority())) {
			for (String userType : message.getUserAuthority().split(Constant.Punctuation.COMMA)) {
				tempMsg.setUserAuthority(userType);
				messageDao.insertUserAuthority(tempMsg);
			}
		}
		// 记录机构权限
		if (StringUtils.isNotBlank(message.getOfficeAuthority())) {
			for (String officeId : message.getOfficeAuthority().split(Constant.Punctuation.COMMA)) {
				tempMsg.setOfficeAuthority(officeId);
				messageDao.insertOfficeAuthority(tempMsg);
			}
		}
	}

	/**
	 * 发送websocket消息
	 * 
	 * @param message
	 */
	public void wsExcute(Message message) {
		List<String> onlineUserList = SystemWebSocketHandler.getOnLineUserIdList();
		List<String> officeIdList = Lists.newArrayList();
		List<String> userTypeList = Lists.newArrayList();
		// 获取消息目标用户及机构
		if (StringUtils.isNotBlank(message.getUserAuthority())) {
			for (String userType : message.getUserAuthority().split(Constant.Punctuation.COMMA)) {
				userTypeList.add(userType);
			}
		} else {
			if (!Collections3.isEmpty(message.getUserAuthorityList())) {
				for (Dict userType : message.getUserAuthorityList()) {
					userTypeList.add(userType.getValue());
				}
			}
		}
		if (StringUtils.isNotBlank(message.getOfficeAuthority())) {
			for (String officeId : message.getOfficeAuthority().split(Constant.Punctuation.COMMA)) {
				officeIdList.add(officeId);
			}
		} else {
			if (!Collections3.isEmpty(message.getOfficeAuthorityList())) {
				for (Office officeId : message.getOfficeAuthorityList()) {
					officeIdList.add(officeId.getId());
				}
			}
		}
		for (String userId : onlineUserList) {
			// 判断消息目标用户是否在线
			if (userTypeList.contains(UserUtils.get(userId).getUserType())
					&& officeIdList.contains(UserUtils.get(userId).getOffice().getId())) {
				Message relevance = messageRelevanceService.findRelevance(message.getMessageType());
				message.setUrl(relevance.getUrl());
				message.setMenuId(relevance.getMenuId());
				wshandler.sendStrMessageToUser(userId, message);
			}
		}
	}

	/**
	 * 通知撤回
	 * 
	 * @author: yanbingxu
	 * @param message
	 */
	@Transactional(readOnly = false)
	public void cancel(Message message) {
		message.setDelFlag(Constant.deleteFlag.Invalid);
		message.preUpdate();
		messageDao.update(message);
	}

	/**
	 * 忽略全部消息
	 * 
	 * @author: yanbingxu
	 * @param message
	 */
	@Transactional(readOnly = false)
	public void readAllMessage() {
		List<Message> messageList = Lists.newArrayList();
		Message message = new Message();
		message.setUserAuthority(UserUtils.getUser().getUserType());
		message.setDateRange(Global.getConfig("showMessage.date.range"));
		message.setMaxNumber(Integer.valueOf(Global.getConfig("showMessage.max.number")));
		messageList = messageDao.findMessageByAuthority(message);
		for (Message msg : messageList) {
			insertReadUser(msg.getId());
		}
	}

	/**
	 * 自定义消息
	 * 
	 * @author: yanbingxu
	 * @param menuId
	 *            跳转菜单ID(三级菜单)
	 * @param messageTopic
	 *            消息内容
	 * @param targetRoleIdList
	 *            指定接收岗位
	 * @param targetOfficeIdList
	 *            指定接收机构
	 * @param createUser
	 *            发送人
	 */
	@Transactional(readOnly = false)
	public void customMessage(String menuId, String messageTopic, List<String> targetRoleIdList,
			List<String> targetOfficeIdList, User createUser) {
		// 控制输入
		if (menuId.length() != 6) {
			logger.error("**************** 自定义消息参数跳转菜单ID有误 ************************");
			return;
		}
		Message message = new Message();
		message.setMessageType(CUSTOMMESSAGE);
		message.setMessageTopic(messageTopic);
		message.setMessageBody(menuId);
		message.setUrl(systemService.getMenu(menuId).getHref());
		// 通过控制输入决定数组长度，不用做长度判断
		message.setMenuId(systemService.getMenu(menuId).getParentIds().split(Constant.Punctuation.COMMA)[2]);
		message.setUserAuthority(Collections3.isEmpty(targetRoleIdList) == false
				? StringUtils.join(targetRoleIdList.toArray(new String[] {}), Constant.Punctuation.COMMA) : null);
		message.setOfficeAuthority(Collections3.isEmpty(targetOfficeIdList) == false
				? StringUtils.join(targetOfficeIdList.toArray(new String[] {}), Constant.Punctuation.COMMA) : null);
		message.preInsert();
		if (createUser != null) {
			message.setCreateBy(createUser);
			message.setCreateName(createUser.getName());
		}
		messageDao.insert(message);
		saveAuthority(message);
		wsExcute(message);
	}

	/**
	 * 
	 * Title: gpsLocationSend
	 * <p>
	 * Description: 车辆实时位置发送至前台
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @param location
	 *            void 返回类型
	 */
	public void gpsLocationSend(Map<String, String> location) {
		List<String> onlineUserList = SystemWebSocketHandler.getOnLineUserIdList();
		StoRouteInfo route = new StoRouteInfo();
		// 获取车辆所在线路所属机构
		route.setCarNo(location.get(Parameter.CAR_NO_KEY));
		List<StoRouteInfo> routeList = routeInfoService.findList(route);
		// 判断该机构人员是否在线
		for (StoRouteInfo stoRoute : routeList) {
			for (String userId : onlineUserList) {
				if (UserUtils.get(userId).getOffice().getId().equals(stoRoute.getCurOffice().getId())) {
					wshandler.sendGpsMessageToUser(userId, location);
				}
			}
		}
	}
}
