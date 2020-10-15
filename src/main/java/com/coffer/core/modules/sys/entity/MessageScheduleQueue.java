package com.coffer.core.modules.sys.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 消息定时队列参数Entity
 * 
 * @author yanbingxu
 * @version 2017-11-14
 */
public class MessageScheduleQueue implements Serializable {

	private static final long serialVersionUID = 1L;
	/** 合并后的消息类型 **/
	private String messageType;
	/** 消息内容参数 **/
	private List<String> params;
	/** 目标机构 **/
	private String targetOfficeId;
	/** 发送人 **/
	private User createUser;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getTargetOfficeId() {
		return targetOfficeId;
	}

	public void setTargetOfficeId(String targetOfficeId) {
		this.targetOfficeId = targetOfficeId;
	}

	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

}
