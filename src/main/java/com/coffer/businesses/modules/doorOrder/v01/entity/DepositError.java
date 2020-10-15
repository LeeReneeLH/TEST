
package com.coffer.businesses.modules.doorOrder.v01.entity;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 存款差错管理Entity
 * 
 * @author XL
 * @version 2019-06-26
 */
public class DepositError extends DataEntity<DepositError> {

	private static final long serialVersionUID = 1L;
	private String id; // id
	private String doorId; // 门店id
	private String doorName; // 门店名称
	private String clientId; // 商户id
	private String clientName; // 商户名称
	private String orderId; // 预约单号 非必填
	private String errorType; // 错误类型
	private String amount; // 差错金额
	private String comments; // 备注
	private String registerId; // 登记人Id
	private String registerName; // 登记人名称
	private Office office; // 所属机构
	private String status; // 状态类型(1,登记 2,冲正)

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoorId() {
		return doorId;
	}

	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getRegisterId() {
		return registerId;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public String getRegisterName() {
		return registerName;
	}

	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
