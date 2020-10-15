package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.google.common.collect.Lists;

/**
 * 商行取款Entity
 * 
 * @author wxz
 * @version 2017-08-24
 */
public class ClOutMain extends DataEntity<ClOutMain> {

	private static final long serialVersionUID = 1L;
	private String outNo = ""; // 业务流水
	private String custNo = ""; // 客户编号
	private String custName = ""; // 客户名称
	private String status = ""; // 1：登记，2：冲正
	private String busType = ""; // 业务类型: 01:商行交款, 02:商行取款, 03: 向人民银行缴款,
									// 04:人民银行复点入库, 05: 人民银行复点出库
	private BigDecimal outAmount; // 入库总金额
	private String bankManNoA = ""; // 银行交接人员编号A
	private String bankManNameA = ""; // 银行交接人员A
	private String bankManNoB = ""; // 银行交接人员编号B
	private String bankManNameB = ""; // 银行交接人员B
	private String transManNo = ""; // 交接人编号
	private String transManName = ""; // 交接人
	private String checkManNo = ""; // 复核人员编号
	private String checkManName = ""; // 复核人员
	private String makesureManNo = ""; // 确认人编号
	private String makesureManName = ""; // 确认人名称

	/** 状态名 1：登记，2：冲正 **/
	private String statusName = "";

	/** 授权人id */
	private User authorizeBy;
	/** 授权人姓名 */
	private String authorizeName;
	/** 授权时间 */
	private Date authorizeDate;
	/** 授权原因 */
	private String authorizeReason;

	/** 授权人登录名 */
	private String authorizeLogin;
	/** 授权人登录密码 */
	private String authorizePass;

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;

	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;

	private String searchDateEnd;

	/** 商行取款明细 */
	private List<ClOutDetail> clOutDetailList = Lists.newArrayList(); // 子表列表

	/** 面值列表 */
	private List<DenominationInfo> denominationList = Lists.newArrayList();

	/** 登记机构 */
	private Office rOffice;

	/** 人员类型 */
	private String userType;

	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */

	/* 追加属性 修改人:sg 修改日期:2017-12-19 begin */
	private String cl01; // 未清分/完整券
	private String cl02; // 未清分/完整券
	private String cl03; // 未清分/完整券
	private String cl04; // 未清分/完整券
	private String cl05; // 未清分/完整券
	private String cl06; // 未清分/完整券
	private String cl07; // 未清分/完整券
	private String cl08; // 未清分/完整券
	private String cl09; // 未清分/完整券
	private String cl10; // 未清分/完整券
	/** 未清分/完整券总金额 */
	private BigDecimal totalAmt;
	private String ccl01; // 已清分/残损券
	private String ccl02; // 已清分/残损券
	private String ccl03; // 已清分/残损券
	private String ccl04; // 已清分/残损券
	private String ccl05; // 已清分/残损券
	private String ccl06; // 已清分/残损券
	private String ccl07; // 已清分/残损券
	private String ccl08; // 已清分/残损券
	private String ccl09; // 已清分/残损券
	private String ccl10; // 已清分/残损券
	/** 未清分/完整券总金额 */
	private BigDecimal totalAmtc;
	// 日（统计图用）
	private String handInDate;
	/* end */

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public List<DenominationInfo> getDenominationList() {
		return denominationList;
	}

	public void setDenominationList(List<DenominationInfo> denominationList) {
		this.denominationList = denominationList;
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

	public ClOutMain() {
		super();
	}

	public ClOutMain(String id) {
		super(id);
	}

	public String getOutNo() {
		return outNo;
	}

	public void setOutNo(String outNo) {
		this.outNo = outNo;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@Length(min = 0, max = 50, message = "客户名称长度必须介于 0 和 50 之间")
	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	@Length(min = 0, max = 1, message = "1：登记，2：冲正长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Length(min = 0, max = 2, message = "业务类型: 01:商行交款, 02:商行取款, 03: 向人民银行缴款, 04:人民银行复点入库, 05: 人民银行复点出库长度必须介于 0 和 2 之间")
	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
	}

	@Length(min = 0, max = 30, message = "银行交接人员编号A长度必须介于 0 和 30 之间")
	public String getBankManNoA() {
		return bankManNoA;
	}

	public void setBankManNoA(String bankManNoA) {
		this.bankManNoA = bankManNoA;
	}

	@Length(min = 0, max = 30, message = "银行交接人员A长度必须介于 0 和 30 之间")
	public String getBankManNameA() {
		return bankManNameA;
	}

	public void setBankManNameA(String bankManNameA) {
		this.bankManNameA = bankManNameA;
	}

	@Length(min = 0, max = 30, message = "银行交接人员编号B长度必须介于 0 和 30 之间")
	public String getBankManNoB() {
		return bankManNoB;
	}

	public void setBankManNoB(String bankManNoB) {
		this.bankManNoB = bankManNoB;
	}

	@Length(min = 0, max = 30, message = "银行交接人员B长度必须介于 0 和 30 之间")
	public String getBankManNameB() {
		return bankManNameB;
	}

	public void setBankManNameB(String bankManNameB) {
		this.bankManNameB = bankManNameB;
	}

	@Length(min = 0, max = 30, message = "交接人编号长度必须介于 0 和 30 之间")
	public String getTransManNo() {
		return transManNo;
	}

	public void setTransManNo(String transManNo) {
		this.transManNo = transManNo;
	}

	@Length(min = 0, max = 30, message = "交接人长度必须介于 0 和 30 之间")
	public String getTransManName() {
		return transManName;
	}

	public void setTransManName(String transManName) {
		this.transManName = transManName;
	}

	@Length(min = 0, max = 30, message = "复核人员编号长度必须介于 0 和 30 之间")
	public String getCheckManNo() {
		return checkManNo;
	}

	public void setCheckManNo(String checkManNo) {
		this.checkManNo = checkManNo;
	}

	@Length(min = 0, max = 30, message = "复核人员长度必须介于 0 和 30 之间")
	public String getCheckManName() {
		return checkManName;
	}

	public void setCheckManName(String checkManName) {
		this.checkManName = checkManName;
	}

	@Length(min = 0, max = 30, message = "确认人编号长度必须介于 0 和 30 之间")
	public String getMakesureManNo() {
		return makesureManNo;
	}

	public void setMakesureManNo(String makesureManNo) {
		this.makesureManNo = makesureManNo;
	}

	@Length(min = 0, max = 30, message = "确认人名称长度必须介于 0 和 30 之间")
	public String getMakesureManName() {
		return makesureManName;
	}

	public void setMakesureManName(String makesureManName) {
		this.makesureManName = makesureManName;
	}

	public List<ClOutDetail> getClOutDetailList() {
		return clOutDetailList;
	}

	public void setClOutDetailList(List<ClOutDetail> clOutDetailList) {
		this.clOutDetailList = clOutDetailList;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public User getAuthorizeBy() {
		return authorizeBy;
	}

	public void setAuthorizeBy(User authorizeBy) {
		this.authorizeBy = authorizeBy;
	}

	public String getAuthorizeName() {
		return authorizeName;
	}

	public void setAuthorizeName(String authorizeName) {
		this.authorizeName = authorizeName;
	}

	public Date getAuthorizeDate() {
		return authorizeDate;
	}

	public void setAuthorizeDate(Date authorizeDate) {
		this.authorizeDate = authorizeDate;
	}

	public String getAuthorizeReason() {
		return authorizeReason;
	}

	public void setAuthorizeReason(String authorizeReason) {
		this.authorizeReason = authorizeReason;
	}

	public String getAuthorizeLogin() {
		return authorizeLogin;
	}

	public void setAuthorizeLogin(String authorizeLogin) {
		this.authorizeLogin = authorizeLogin;
	}

	public String getAuthorizePass() {
		return authorizePass;
	}

	public void setAuthorizePass(String authorizePass) {
		this.authorizePass = authorizePass;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
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

	public String getCcl01() {
		if (StringUtils.isNotBlank(this.ccl01)) {
			return this.ccl01;
		} else {
			return "0";
		}
	}

	public void setCcl01(String ccl01) {
		if (StringUtils.isNotBlank(ccl01)) {
			this.ccl01 = ccl01;
		} else {
			this.ccl01 = "0";
		}
	}

	public String getCcl02() {
		if (StringUtils.isNotBlank(this.ccl02)) {
			return this.ccl02;
		} else {
			return "0";
		}
	}

	public void setCcl02(String ccl02) {
		if (StringUtils.isNotBlank(ccl02)) {
			this.ccl02 = ccl02;
		} else {
			this.ccl02 = "0";
		}
	}

	public String getCcl03() {
		if (StringUtils.isNotBlank(this.ccl03)) {
			return this.ccl03;
		} else {
			return "0";
		}
	}

	public void setCcl03(String ccl03) {
		if (StringUtils.isNotBlank(ccl03)) {
			this.ccl03 = ccl03;
		} else {
			this.ccl03 = "0";
		}
	}

	public String getCcl04() {
		if (StringUtils.isNotBlank(this.ccl04)) {
			return this.ccl04;
		} else {
			return "0";
		}
	}

	public void setCcl04(String ccl04) {
		if (StringUtils.isNotBlank(ccl04)) {
			this.ccl04 = ccl04;
		} else {
			this.ccl04 = "0";
		}
	}

	public String getCcl05() {
		if (StringUtils.isNotBlank(this.ccl05)) {
			return this.ccl05;
		} else {
			return "0";
		}
	}

	public void setCcl05(String ccl05) {
		if (StringUtils.isNotBlank(ccl05)) {
			this.ccl05 = ccl05;
		} else {
			this.ccl05 = "0";
		}
	}

	public String getCcl06() {
		if (StringUtils.isNotBlank(this.ccl06)) {
			return this.ccl06;
		} else {
			return "0";
		}
	}

	public void setCcl06(String ccl06) {
		if (StringUtils.isNotBlank(ccl06)) {
			this.ccl06 = ccl06;
		} else {
			this.ccl06 = "0";
		}
	}

	public String getCcl07() {
		if (StringUtils.isNotBlank(this.ccl07)) {
			return this.ccl07;
		} else {
			return "0";
		}
	}

	public void setCcl07(String ccl07) {
		if (StringUtils.isNotBlank(ccl07)) {
			this.ccl07 = ccl07;
		} else {
			this.ccl07 = "0";
		}
	}

	public String getCcl08() {
		if (StringUtils.isNotBlank(this.ccl08)) {
			return this.ccl08;
		} else {
			return "0";
		}
	}

	public void setCcl08(String ccl08) {
		if (StringUtils.isNotBlank(ccl08)) {
			this.ccl08 = ccl08;
		} else {
			this.ccl08 = "0";
		}
	}

	public String getCcl09() {
		if (StringUtils.isNotBlank(this.ccl09)) {
			return this.ccl09;
		} else {
			return "0";
		}
	}

	public void setCcl09(String ccl09) {
		if (StringUtils.isNotBlank(ccl09)) {
			this.ccl09 = ccl09;
		} else {
			this.ccl09 = "0";
		}
	}

	public String getCcl10() {
		if (StringUtils.isNotBlank(this.ccl10)) {
			return this.ccl10;
		} else {
			return "0";
		}
	}

	public void setCcl10(String ccl10) {
		if (StringUtils.isNotBlank(ccl10)) {
			this.ccl10 = ccl10;
		} else {
			this.ccl10 = "0";
		}
	}

	public BigDecimal getTotalAmtc() {
		if (this.totalAmtc != null) {
			return this.totalAmtc;
		} else {
			return new BigDecimal(0);
		}
	}

	public void setTotalAmtc(BigDecimal totalAmtc) {
		if (totalAmtc != null) {
			this.totalAmtc = totalAmtc;
		} else {
			this.totalAmtc = new BigDecimal(0);
		}
	}

	public String getHandInDate() {
		return handInDate;
	}

	public void setHandInDate(String handInDate) {
		this.handInDate = handInDate;
	}

	@Override
	public String toString() {
		return "clOutMain [outNo=" + outNo + ", status=" + status + ", outAmount=" + outAmount + "]";
	}

}