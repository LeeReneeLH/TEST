package com.coffer.businesses.modules.weChat.v03.entity;



public class TemplateMsgResult extends ResultState {

	private static final long serialVersionUID = 1L;

	private String msgid; // 消息id(发送模板消息)

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}


}
