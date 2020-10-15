package com.coffer.businesses.modules.weChat.v03.entity;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 
 * @author qph
 * @version 2018-05-18
 */
@XStreamAlias("xml")
public class TextMessage extends WeChatBaseMessage {

	private static final long serialVersionUID = 1L;
	@XStreamAlias("Content")
	private String Content; // 文本
	@XStreamAlias("MsgID")
	private String MsgID; //
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getMsgID() {
		return MsgID;
	}
	public void setMsgID(String msgID) {
		MsgID = msgID;
	}



}
