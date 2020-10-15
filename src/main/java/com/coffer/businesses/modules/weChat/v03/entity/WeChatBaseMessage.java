package com.coffer.businesses.modules.weChat.v03.entity;


import com.coffer.core.common.persistence.DataEntity;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author qph
 * @version 2018-05-18
 */
@XStreamAlias("xml")
public class WeChatBaseMessage extends DataEntity<WeChatBaseMessage> {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("ToUserName")
	private String ToUserName;
	@XStreamAlias("FromUserName")
	private String FromUserName;
	@XStreamAlias("CreateTime")
	private String CreateTime; // 时间
	@XStreamAlias("MsgType")
	private String MsgType; // 消息类型
	public String getToUserName() {
		return ToUserName;
	}
	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}
	public String getFromUserName() {
		return FromUserName;
	}
	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getMsgType() {
		return MsgType;
	}
	public void setMsgType(String msgType) {
		MsgType = msgType;
	}



}
