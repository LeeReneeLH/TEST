package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;

/**
 * 柜员账务Entity
 * @author xl
 * @version 2017-10-23
 */
public class TellerAccountsMain extends DataEntity<TellerAccountsMain> {
	
	private static final long serialVersionUID = 1L;
	private String tellerBy;		// 柜员ID
	private String tellerName;		// 柜员姓名
	private String tellerType;		// 柜员类型
	private String custNo;		// 客户编号
	private String custName;		// 客户姓名
	private String bussinessType; // 业务类型（01商行交款，02商行取款，03代理上缴，04人民银行复点入库，05人民银行复点出库，
									// 06备付金交入，07备付金取回,08复点,09清分）
	private String bussinessId;		// 业务对应流水ID
	private String bussinessStatus;		// 业务状态（1：登记，2：冲正）
	private BigDecimal inAmount; // 入库总金额（借）
	private BigDecimal outAmount; // 出库总金额（贷）
	private BigDecimal totalAmount; // 剩余总金额
	private String cashType;		// 剩余金额类型（01:备付金 02:非备付金）
	private Date registerDate;		// 登记时间
	
	private String inoutType; // 出入库类型：0：出库 1：入库
	private Date createTimeStart;// 开始时间
	private Date createTimeEnd;// 结束时间
	private String searchDateStart;// 开始时间
	private String searchDateEnd;// 结束时间
	private List<StoEscortInfo> tellerList = Lists.newArrayList();// 柜员集合
	private User loginUser;// 当前登陆用户信息

	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */

	public TellerAccountsMain() {
		super();
	}

	public TellerAccountsMain(String id){
		super(id);
	}

	@Length(min=0, max=64, message="柜员ID长度必须介于 0 和 64 之间")
	public String getTellerBy() {
		return tellerBy;
	}

	public void setTellerBy(String tellerBy) {
		this.tellerBy = tellerBy;
	}
	
	@Length(min=0, max=200, message="柜员姓名长度必须介于 0 和 200 之间")
	public String getTellerName() {
		return tellerName;
	}

	public void setTellerName(String tellerName) {
		this.tellerName = tellerName;
	}
	
	@Length(min=0, max=2, message="柜员类型长度必须介于 0 和 2 之间")
	public String getTellerType() {
		return tellerType;
	}

	public void setTellerType(String tellerType) {
		this.tellerType = tellerType;
	}
	
	@Length(min=0, max=64, message="客户编号长度必须介于 0 和 64 之间")
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	
	@Length(min=0, max=200, message="客户姓名长度必须介于 0 和 200 之间")
	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	@Length(min=0, max=2, message="业务类型（01商行交款，02商行取款，03代理上缴，04人民银行复点入库，05人民银行复点出库，06备付金交入，07备付金取回）长度必须介于 0 和 2 之间")
	public String getBussinessType() {
		return bussinessType;
	}

	public void setBussinessType(String bussinessType) {
		this.bussinessType = bussinessType;
	}
	
	@Length(min=0, max=64, message="业务对应流水ID长度必须介于 0 和 64 之间")
	public String getBussinessId() {
		return bussinessId;
	}

	public void setBussinessId(String bussinessId) {
		this.bussinessId = bussinessId;
	}
	
	@Length(min=0, max=2, message="业务状态（1：登记，2：冲正）长度必须介于 0 和 2 之间")
	public String getBussinessStatus() {
		return bussinessStatus;
	}

	public void setBussinessStatus(String bussinessStatus) {
		this.bussinessStatus = bussinessStatus;
	}
	
	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}
	
	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
	}
	
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	@Length(min=0, max=2, message="剩余金额类型（01:备付金 02:非备付金）长度必须介于 0 和 2 之间")
	public String getCashType() {
		return cashType;
	}

	public void setCashType(String cashType) {
		this.cashType = cashType;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getInoutType() {
		return inoutType;
	}

	public void setInoutType(String inoutType) {
		this.inoutType = inoutType;
	}

	public Date getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeStart(Date createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getSearchDateStart() {
		return searchDateStart;
	}

	public void setSearchDateStart(String searchDateStart) {
		this.searchDateStart = searchDateStart;
	}

	public String getSearchDateEnd() {
		return searchDateEnd;
	}

	public void setSearchDateEnd(String searchDateEnd) {
		this.searchDateEnd = searchDateEnd;
	}

	public List<StoEscortInfo> getTellerList() {
		return tellerList;
	}

	public void setTellerList(List<StoEscortInfo> tellerList) {
		this.tellerList = tellerList;
	}

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	@Override
	public String toString() {
		return "TellerAccountsMain [tellerName=" + tellerName + ", custNo=" + custNo + ", bussinessType="
				+ bussinessType
				+ ", totalAmount=" + totalAmount + ", cashType=" + cashType + "]";
	}
	
}