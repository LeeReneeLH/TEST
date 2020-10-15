package com.coffer.core.modules.sys.entity;

import com.coffer.businesses.common.Constant;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;

/**
 * 
 * Title: AutoVaultCommunication
 * <p>
 * Description: 通信错误Entity
 * </p>
 * 
 * @author yanbingxu
 * @date 2018年5月4日 上午9:38:42
 */
public class AutoVaultCommunication extends DataEntity<AutoVaultCommunication> {

	private static final long serialVersionUID = 1L;
	/** 状态 **/
	private String status;
	/** 拼接消息 **/
	private String message;
	/** 返回报文 **/
	private String outJson;
	/** 请求报文 **/
	private String inJson;
	/** 异常信息 **/
	private String exception;
	/** 更新时间 格式 ：yyyyMMddHHmmssSSSSSS */
	private String strUpdateDate;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the inJson
	 */
	public String getInJson() {
		return inJson;
	}

	/**
	 * @param inJson
	 *            the inJson to set
	 */
	public void setInJson(String inJson) {
		this.inJson = inJson;
	}

	/**
	 * @return the outJson
	 */
	public String getOutJson() {
		return outJson;
	}

	/**
	 * @param outJson
	 *            the outJson to set
	 */
	public void setOutJson(String outJson) {
		this.outJson = outJson;
	}

	/**
	 * @return the exception
	 */
	public String getException() {
		return exception;
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	public void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the strUpdateDate
	 */
	public String getStrUpdateDate() {
		if (StringUtils.isBlank(strUpdateDate) && this.updateDate != null) {
			this.strUpdateDate = DateUtils.formatDate(this.updateDate, Constant.Dates.FORMATE_YYYYMMDDHHMMSSSSSSSS);
		}
		return strUpdateDate;
	}

	/**
	 * @param strUpdateDate the strUpdateDate to set
	 */
	public void setStrUpdateDate(String strUpdateDate) {
		this.strUpdateDate = strUpdateDate;
	}

}
