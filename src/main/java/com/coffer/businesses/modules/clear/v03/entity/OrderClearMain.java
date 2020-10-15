package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 预约清分Entity
 * 
 * @author wanglin
 * @version 2017-08-11
 */
public class OrderClearMain extends DataEntity<OrderClearMain> {

	private static final long serialVersionUID = 1L;

	/** 预约单号 **/
	private String inNo = "";
	/** 状态 1：登记，2：确认 **/
	private String status = "";

	/** 状态名 1：登记，2：确认 **/
	private String statusName = "";

	/** 入库总金额 **/
	private BigDecimal inAmount;

	/** 总金额格式化 **/
	private String inAmountFormat = "";

	/** 登记机构 */
	private String registerOffice;

	/** 登记人 */
	private String registerBy;

	/** 登记日期 */
	private Date registerDate;

	/** 登记机构名 */
	private String registerOfficeNm;

	/** 登记人 */
	private String registerName;

	/** 备注 **/
	private String remarks = "";

	/** 接收人ID */
	private String receiveBy;

	/** 接收人名 */
	private String receiveName;

	/** 接收日期 */
	private Date receiveDate;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;

	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;

	private String searchDateEnd;

	/** 状态 (1：登记，2：接收)（查询用） **/
	private String searchStatus;

	/** 预约清分明细 */
	private List<OrderClearDetail> orderClearDetailList = Lists.newArrayList();

	/** 面值列表 */
	private List<DenominationInfo> denominationList = Lists.newArrayList();

	/** 清分机构 */
	private Office rOffice;

	/* 追加属性 修改人:sg 修改日期:2017-12-19 begin */
	private String cl01; // 未清分
	private String cl02; // 未清分
	private String cl03; // 未清分
	private String cl04; // 未清分
	private String cl05; // 未清分
	private String cl06; // 未清分
	private String cl07; // 未清分
	private String cl08; // 未清分
	private String cl09; // 未清分
	private String cl10; // 未清分
	/** 总金额 */
	private BigDecimal totalAmt;
	/* end */
	
	/* 追加属性 修改人:XL 修改日期:2018-05-21 begin */
	/** 申请方式（1：PC端；2：微信端；3：PDA端） */
	private String method;			
	/** 登记日期(查询用) */
	private String registerTime;
	/* end */
	
	public OrderClearMain() {
		super();
	}

	public OrderClearMain(String id) {
		super(id);
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public String getInNo() {
		return inNo;
	}

	public void setInNo(String inNo) {
		this.inNo = inNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public String getInAmountFormat() {
		return inAmountFormat;
	}

	public void setInAmountFormat(String inAmountFormat) {
		this.inAmountFormat = inAmountFormat;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public List<OrderClearDetail> getOrderClearDetailList() {
		return orderClearDetailList;
	}

	public void setOrderClearDetailList(List<OrderClearDetail> orderClearDetailList) {
		this.orderClearDetailList = orderClearDetailList;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public List<DenominationInfo> getDenominationList() {
		return denominationList;
	}

	public void setDenominationList(List<DenominationInfo> denominationList) {
		this.denominationList = denominationList;
	}

	public String getRegisterOffice() {
		return registerOffice;
	}

	public void setRegisterOffice(String registerOffice) {
		this.registerOffice = registerOffice;
	}

	public String getRegisterBy() {
		return registerBy;
	}

	public void setRegisterBy(String registerBy) {
		this.registerBy = registerBy;
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public String getRegisterName() {
		return registerName;
	}

	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}

	public String getRegisterOfficeNm() {
		return registerOfficeNm;
	}

	public void setRegisterOfficeNm(String registerOfficeNm) {
		this.registerOfficeNm = registerOfficeNm;
	}

	public String getReceiveBy() {
		return receiveBy;
	}

	public void setReceiveBy(String receiveBy) {
		this.receiveBy = receiveBy;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}

	public String getCl01() {
		if (StringUtils.isNotBlank(this.cl01)) {
			return this.cl01;
		} else {
			return "0";
		}
	}

	public void setCl01(String cl01) {
		if (StringUtils.isNotBlank(cl01)) {
			this.cl01 = cl01;
		} else {
			this.cl01 = "0";
		}
	}

	public String getCl02() {
		if (StringUtils.isNotBlank(this.cl02)) {
			return this.cl02;
		} else {
			return "0";
		}
	}

	public void setCl02(String cl02) {
		if (StringUtils.isNotBlank(cl02)) {
			this.cl02 = cl02;
		} else {
			this.cl02 = "0";
		}
	}

	public String getCl03() {
		if (StringUtils.isNotBlank(this.cl03)) {
			return this.cl03;
		} else {
			return "0";
		}
	}

	public void setCl03(String cl03) {
		if (StringUtils.isNotBlank(cl03)) {
			this.cl03 = cl03;
		} else {
			this.cl03 = "0";
		}
	}

	public String getCl04() {
		if (StringUtils.isNotBlank(this.cl04)) {
			return this.cl04;
		} else {
			return "0";
		}
	}

	public void setCl04(String cl04) {
		if (StringUtils.isNotBlank(cl04)) {
			this.cl04 = cl04;
		} else {
			this.cl04 = "0";
		}
	}

	public String getCl05() {
		if (StringUtils.isNotBlank(this.cl05)) {
			return this.cl05;
		} else {
			return "0";
		}
	}

	public void setCl05(String cl05) {
		if (StringUtils.isNotBlank(cl05)) {
			this.cl05 = cl05;
		} else {
			this.cl05 = "0";
		}
	}

	public String getCl06() {
		if (StringUtils.isNotBlank(this.cl06)) {
			return this.cl06;
		} else {
			return "0";
		}
	}

	public void setCl06(String cl06) {
		if (StringUtils.isNotBlank(cl06)) {
			this.cl06 = cl06;
		} else {
			this.cl06 = "0";
		}
	}

	public String getCl07() {
		if (StringUtils.isNotBlank(this.cl07)) {
			return this.cl07;
		} else {
			return "0";
		}
	}

	public void setCl07(String cl07) {
		if (StringUtils.isNotBlank(cl07)) {
			this.cl07 = cl07;
		} else {
			this.cl07 = "0";
		}
	}

	public String getCl08() {
		if (StringUtils.isNotBlank(this.cl08)) {
			return this.cl08;
		} else {
			return "0";
		}
	}

	public void setCl08(String cl08) {
		if (StringUtils.isNotBlank(cl08)) {
			this.cl08 = cl08;
		} else {
			this.cl08 = "0";
		}
	}

	public String getCl09() {
		if (StringUtils.isNotBlank(this.cl09)) {
			return this.cl09;
		} else {
			return "0";
		}
	}

	public void setCl09(String cl09) {
		if (StringUtils.isNotBlank(cl09)) {
			this.cl09 = cl09;
		} else {
			this.cl09 = "0";
		}
	}

	public String getCl10() {
		if (StringUtils.isNotBlank(this.cl10)) {
			return this.cl10;
		} else {
			return "0";
		}
	}

	public void setCl10(String cl10) {
		if (StringUtils.isNotBlank(cl10)) {
			this.cl10 = cl10;
		} else {
			this.cl10 = "0";
		}
	}

	public BigDecimal getTotalAmt() {
		if (this.totalAmt != null) {
			return this.totalAmt;
		} else {
			return new BigDecimal(0);
		}
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		if (totalAmt != null) {
			this.totalAmt = totalAmt;
		} else {
			this.totalAmt = new BigDecimal(0);
		}
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}

	public String toString() {
		return "OrderClearMain [inNo=" + inNo + ", status=" + status + ", inAmount=" + inAmount + ", registerName="
				+ registerName + ", receiveName=" + receiveName + "]";
	}

}