package com.coffer.core.modules.sys.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.CacheUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.dao.MessageDao;
import com.coffer.core.modules.sys.entity.Dict;
import com.coffer.core.modules.sys.entity.Message;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 系统消息关联Service
 * 
 * @author yanbingxu
 * @version 2017-9-27
 */
@Service
public class MessageRelevanceService extends BaseService {

	public static final String CACHE_MESSAGE_MAP = "messageMap";

	@Autowired
	private MessageDao messageDao;

	/**
	 * 分页查询关联信息
	 * 
	 * @param page
	 * @param message
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<Message> findRelevanceList(Page<Message> page, Message message) {
		// 设置分页参数
		message.setPage(page);
		// 执行分页查询
		List<Message> messageList = messageDao.findRelevanceList(message);
		page.setList(messageList);
		return page;
	}

	/**
	 * 通知关联启用/停用
	 * 
	 * @param message
	 */
	@Transactional(readOnly = false)
	public void relevanceValidChange(Message message) {
		if (Constant.deleteFlag.Invalid.equals(message.getDelFlag())) {
			message.setDelFlag(Constant.deleteFlag.Valid);
		} else {
			message.setDelFlag(Constant.deleteFlag.Invalid);
		}
		message.preUpdate();
		messageDao.updateRelevance(message);
		CacheUtils.remove(CACHE_MESSAGE_MAP);
	}

	/**
	 * 保存通知关联
	 * 
	 * @param message
	 */
	@Transactional(readOnly = false)
	public void save(Message message) {
		message.preInsert();
		messageDao.insertRelevance(message);
		CacheUtils.remove(CACHE_MESSAGE_MAP);
	}

	/**
	 * 更新通知关联
	 * 
	 * @param message
	 */
	@Transactional(readOnly = false)
	public void update(Message message) {
		message.preUpdate();
		messageDao.updateRelevance(message);
		CacheUtils.remove(CACHE_MESSAGE_MAP);
	}

	/**
	 * 查询通知关联信息
	 * 
	 * @param messageType
	 * @return
	 */
	//@Transactional(readOnly = true)
	public Message findRelevance(String messageType) {
		@SuppressWarnings("unchecked")
		Map<String, Message> msgRelevanceMap = (Map<String, Message>) CacheUtils.get(CACHE_MESSAGE_MAP);
		if (msgRelevanceMap == null) {
			msgRelevanceMap = Maps.newHashMap();
			for (Message tempMessage : messageDao.findRelevanceList(new Message())) {
				List<Dict> userAuthorityList = Lists.newArrayList();
				List<Office> officeAuthorityList = Lists.newArrayList();
				if (StringUtils.isNotBlank(tempMessage.getUserAuthority())) {
					for (String authority : tempMessage.getUserAuthority().split(",")) {
						userAuthorityList.addAll(DictUtils.getDictList("sys_user_type", true, authority));
					}
				}
				if (StringUtils.isNotBlank(tempMessage.getOfficeAuthority())) {
					for (String authority : tempMessage.getOfficeAuthority().split(",")) {
						officeAuthorityList.add(SysCommonUtils.findOfficeById(authority));
					}
				}
				tempMessage.setOfficeAuthorityList(officeAuthorityList);
				tempMessage.setUserAuthorityList(userAuthorityList);
				msgRelevanceMap.put(tempMessage.getMessageType(), tempMessage);
			}
			CacheUtils.put(CACHE_MESSAGE_MAP, msgRelevanceMap);
		}
		Message msg = msgRelevanceMap.get(messageType);
		return msg;
	}
}
