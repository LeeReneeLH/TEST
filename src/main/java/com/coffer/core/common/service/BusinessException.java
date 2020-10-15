package com.coffer.core.common.service;

/**
 * Service层业务异常,从由Spring管理事务的函数中抛出时会触发事务回滚.
 * @author Clark
 * @version 2015-06-11
 *
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String messageCode;
	private String[] parameters;
	private String messageContent;

	public BusinessException() {
		super();
	}
	
	public BusinessException(Throwable e) {
		super(e);
	}

	public BusinessException(String messageCode) {
		super();
		this.messageCode = messageCode;
	}
	
	public BusinessException(String messageCode, String messageContent, String... parameters) {
		super();
		this.messageCode = messageCode;
		this.parameters = parameters;
		this.messageContent = messageContent;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
}
