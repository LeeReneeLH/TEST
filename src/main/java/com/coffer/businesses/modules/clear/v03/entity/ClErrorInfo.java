package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;

/**
 * 差错管理Entity
 * 
 * @author XL
 * @version 2017年9月7日
 */
public class ClErrorInfo extends DataEntity<ClErrorInfo> {

	private static final long serialVersionUID = 1L;
	/* 追加@ExcelField 修改人:sg 修改时间:2017-11-21 begin */
	@ExcelField(title = "登记单号", align = 2)
	private String errorNo; // 登记单号
	private String custNo; // 银行编号
	@ExcelField(title = "银行名称", align = 2)
	private String custName; // 银行名称
	private String operateType; // 操作类型：现金
	@ExcelField(title = "错款类别", align = 2)
	private String errorType; // 错款类别 1：假币 2：长款 3：短款
	@ExcelField(title = "币种", align = 2)
	private String currency; // 币种 01：人民币
	@ExcelField(title = "面值", align = 2)
	private String denomination; // 面值
	@ExcelField(title = "数量", align = 2)
	private Long count; // 数量
	private String sno; // 冠字号
	private String versionError; // 版本
	@ExcelField(title = "状态 ", align = 2)
	private String status; // 状态 1：登记 2冲正
	private String checkManNo; // 复核人员编号差错管理员
	@ExcelField(title = "复核人员名称", align = 2)
	private String checkManName; // 复核人员名称
	private String clearManNo; // 清分人
	@ExcelField(title = "清分人姓名", align = 2)
	private String clearManName; // 清分人姓名
	private String makesureManNo; // 确认人编号登记人
	@ExcelField(title = "确认人名称", align = 2)
	private String makesureManName; // 确认人名称
	@ExcelField(title = "发现时间", align = 2)
	private Date findTime; // 发现时间
	private String stationNo; // 工位编号
	@ExcelField(title = "发生业务类型 ", align = 2)
	private String busType; // 发生业务类型 08 复点 09清分
	@ExcelField(title = "差错金额", align = 2)
	private BigDecimal errorMoney; // 差错金额
	@ExcelField(title = "差错子类型类型", align = 2)
	private String subErrorType; // 差错子类型类型 00：正常差错；01：半值币；02：错值币
	private String seelOrg; // 封签单位
	private String seelChap; // 封签名章
	private Date seelDate; // 封签日期
	@ExcelField(title = "腰条名章", align = 2)
	/* end */
	private String stripChap; // 腰条名章
	private Long strokeCount; // 笔数
	private Date createTimeStart;// 开始时间
	private Date createTimeEnd;// 结束时间
	private String searchDateStart;// 开始时间
	private String searchDateEnd;// 结束时间
	private String findTimeDate;// 发现时间（精确到日期）
	private String findTimeSec;// 发现时间（精确到秒）
	private List<String> taskTypes = Lists.newArrayList();// 任务类型集合：01-任务分配
															// 02-任务回收 03-任务登记
	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */

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

	/** 人员类型 */
	private String userType;
	/* 添加导出时间和导出人字段 wzj 2017-11-22 begin */
	@ExcelField(title = "登记人", align = 2)
	private String registrant;// 登记人
	@ExcelField(title = "登记时间", align = 2)
	private Date boardingTime;// 登记时间

	/* end */

	/* 追加各面值数量及金额 修改人:sg 修改日期:2017-12-19 begin */
	private String cl01; // 数量
	private String cl02; // 数量
	private String cl03; // 数量
	private String cl04; // 数量
	private String cl05; // 数量
	private String cl06; // 数量
	private String cl07; // 数量
	private String cl08; // 数量
	private String cl09; // 数量
	private String cl10; // 数量
	private String ccl01; // 金额
	private String ccl02; // 金额
	private String ccl03; // 金额
	private String ccl04; // 金额
	private String ccl05; // 金额
	private String ccl06; // 金额
	private String ccl07; // 金额
	private String ccl08; // 金额
	private String ccl09; // 金额
	private String ccl10; // 金额
	/* end */

	public String getUserType() {
		return userType;
	}

	public String getRegistrant() {
		return registrant;
	}

	public void setRegistrant(String registrant) {
		this.registrant = registrant;
	}

	public Date getBoardingTime() {
		return boardingTime;
	}

	public void setBoardingTime(Date boardingTime) {
		this.boardingTime = boardingTime;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public ClErrorInfo() {
		super();
	}

	public ClErrorInfo(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "登记单号长度必须介于 1 和 64 之间")
	public String getErrorNo() {
		return errorNo;
	}

	public void setErrorNo(String errorNo) {
		this.errorNo = errorNo;
	}

	@Length(min = 0, max = 64, message = "银行编号长度必须介于 0 和 64 之间")
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@Length(min = 0, max = 100, message = "银行名称长度必须介于 0 和 100 之间")
	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	@Length(min = 0, max = 1, message = "操作类型：现金长度必须介于 0 和 1 之间")
	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	@Length(min = 0, max = 30, message = "错款类别 1：假币 2：长款 3：短款长度必须介于 0 和 30 之间")
	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	@Length(min = 0, max = 30, message = "币种 01：人民币长度必须介于 0 和 30 之间")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Length(min = 0, max = 30, message = "面值长度必须介于 0 和 30 之间")
	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Length(min = 0, max = 30, message = "冠字号长度必须介于 0 和 30 之间")
	public String getSno() {
		return sno;
	}

	public void setSno(String sno) {
		this.sno = sno;
	}

	@Length(min = 0, max = 30, message = "版本长度必须介于 0 和 30 之间")
	public String getVersionError() {
		return versionError;
	}

	public void setVersionError(String versionError) {
		this.versionError = versionError;
	}

	@Length(min = 0, max = 2, message = "状态 1：登记 2冲正长度必须介于 0 和 2 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Length(min = 0, max = 64, message = "复核人员编号差错管理员长度必须介于 0 和 64 之间")
	public String getCheckManNo() {
		return checkManNo;
	}

	public void setCheckManNo(String checkManNo) {
		this.checkManNo = checkManNo;
	}

	@Length(min = 0, max = 100, message = "复核人员名称长度必须介于 0 和 100 之间")
	public String getCheckManName() {
		return checkManName;
	}

	public void setCheckManName(String checkManName) {
		this.checkManName = checkManName;
	}

	@Length(min = 0, max = 64, message = "清分人长度必须介于 0 和 64 之间")
	public String getClearManNo() {
		return clearManNo;
	}

	public void setClearManNo(String clearManNo) {
		this.clearManNo = clearManNo;
	}

	@Length(min = 0, max = 100, message = "清分人姓名长度必须介于 0 和 100 之间")
	public String getClearManName() {
		return clearManName;
	}

	public void setClearManName(String clearManName) {
		this.clearManName = clearManName;
	}

	@Length(min = 0, max = 100, message = "确认人编号登记人长度必须介于 0 和 100 之间")
	public String getMakesureManNo() {
		return makesureManNo;
	}

	public void setMakesureManNo(String makesureManNo) {
		this.makesureManNo = makesureManNo;
	}

	@Length(min = 0, max = 100, message = "确认人名称长度必须介于 0 和 100 之间")
	public String getMakesureManName() {
		return makesureManName;
	}

	public void setMakesureManName(String makesureManName) {
		this.makesureManName = makesureManName;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getFindTime() {
		return findTime;
	}

	public void setFindTime(Date findTime) {
		this.findTime = findTime;
	}

	@Length(min = 0, max = 30, message = "工位编号长度必须介于 0 和 30 之间")
	public String getStationNo() {
		return stationNo;
	}

	public void setStationNo(String stationNo) {
		this.stationNo = stationNo;
	}

	@Length(min = 0, max = 30, message = "业务类型 01 复点 02清分长度必须介于 0 和 30 之间")
	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public BigDecimal getErrorMoney() {
		return errorMoney;
	}

	public void setErrorMoney(BigDecimal errorMoney) {
		this.errorMoney = errorMoney;
	}

	@Length(min = 0, max = 30, message = "差错子类型类型（00：正常差错；01：半值币；02：错值币）长度必须介于 0 和 30 之间")
	public String getSubErrorType() {
		return subErrorType;
	}

	public void setSubErrorType(String subErrorType) {
		this.subErrorType = subErrorType;
	}

	@Length(min = 0, max = 100, message = "封签单位长度必须介于 0 和 100 之间")
	public String getSeelOrg() {
		return seelOrg;
	}

	public void setSeelOrg(String seelOrg) {
		this.seelOrg = seelOrg;
	}

	@Length(min = 0, max = 100, message = "封签名章长度必须介于 0 和 100 之间")
	public String getSeelChap() {
		return seelChap;
	}

	public void setSeelChap(String seelChap) {
		this.seelChap = seelChap;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSeelDate() {
		return seelDate;
	}

	public void setSeelDate(Date seelDate) {
		this.seelDate = seelDate;
	}

	@Length(min = 0, max = 100, message = "腰条名章长度必须介于 0 和 100 之间")
	public String getStripChap() {
		return stripChap;
	}

	public void setStripChap(String stripChap) {
		this.stripChap = stripChap;
	}

	public Long getStrokeCount() {
		return strokeCount;
	}

	public void setStrokeCount(Long strokeCount) {
		this.strokeCount = strokeCount;
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

	public String getFindTimeDate() {
		return findTimeDate;
	}

	public void setFindTimeDate(String findTimeDate) {
		this.findTimeDate = findTimeDate;
	}

	public List<String> getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(List<String> taskTypes) {
		this.taskTypes = taskTypes;
	}

	public String getFindTimeSec() {
		return findTimeSec;
	}

	public void setFindTimeSec(String findTimeSec) {
		this.findTimeSec = findTimeSec;
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

	@Override
	public String toString() {
		return "clErrorInfo [errorNo=" + errorNo + ", custNo=" + custNo + ", errorType=" + errorType + "]";
	}

}