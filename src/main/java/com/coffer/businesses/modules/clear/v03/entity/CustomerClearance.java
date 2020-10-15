package com.coffer.businesses.modules.clear.v03.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;

/**
 * 客户清分量Entity
 * 
 * @author wzj
 * @version 2017-09-06
 */
public class CustomerClearance extends DataEntity<CustomerClearance> {

	private static final long serialVersionUID = 1L;
	private String outNo; // 主键编号
	private String custNo; // 客户编号
	@ExcelField(title = "客户名称", align = 2)
	private String custName; // 客户名称
	private String denomination; // 面额
	@ExcelField(title = "未清分", align = 2)
	private String countDqf; // 未清分
	@ExcelField(title = "已清分", align = 2)
	private String countYqf; // 已清分
	@ExcelField(title = "ATM", align = 2)
	private String countAtm; // ATM
	@ExcelField(title = "完整币", align = 2)
	private String countWzq; // 完整币
	@ExcelField(title = "残损币", align = 2)
	private String countCsq; // 残损币
	@ExcelField(title = "总金额", align = 2)
	private String outAmount; // 总金额
	@ExcelField(title = "清分1", align = 2)
	private String cl1; // 清分
	@ExcelField(title = "清分2", align = 2)
	private String cl2; // 清分
	@ExcelField(title = "清分3", align = 2)
	private String cl3; // 清分
	@ExcelField(title = "清分4", align = 2)
	private String cl4; // 清分
	@ExcelField(title = "清分5", align = 2)
	private String cl5; // 清分
	@ExcelField(title = "清分6", align = 2)
	private String cl6; // 清分
	@ExcelField(title = "清分7", align = 2)
	private String cl7; // 清分
	@ExcelField(title = "清分8", align = 2)
	private String cl8; // 清分
	@ExcelField(title = "清分9", align = 2)
	private String cl9; // 清分
	@ExcelField(title = "清分10", align = 2)
	private String cl10; // 清分
	@ExcelField(title = "复点1", align = 2)
	private String re1; // 复点
	@ExcelField(title = "复点2", align = 2)
	private String re2; // 复点
	@ExcelField(title = "复点3", align = 2)
	private String re3; // 复点
	@ExcelField(title = "复点4", align = 2)
	private String re4; // 复点
	@ExcelField(title = "复点5", align = 2)
	private String re5; // 复点
	@ExcelField(title = "复点6", align = 2)
	private String re6; // 复点
	@ExcelField(title = "复点7", align = 2)
	private String re7; // 复点
	@ExcelField(title = "复点8", align = 2)
	private String re8; // 复点
	@ExcelField(title = "复点9", align = 2)
	private String re9; // 复点
	@ExcelField(title = "复点10", align = 2)
	private String re10; // 复点
	@ExcelField(title = "ATM币1", align = 2)
	private String atm1; // ATM
	@ExcelField(title = "ATM币2", align = 2)
	private String atm2; // ATM
	@ExcelField(title = "ATM币3", align = 2)
	private String atm3; // ATM
	/*
	 * @ExcelField(title = "ATM币4", align = 2) private String atm4; // ATM
	 * 
	 * @ExcelField(title = "ATM币5", align = 2) private String atm5; // ATM
	 * 
	 * @ExcelField(title = "ATM币6", align = 2) private String atm6; // ATM
	 * 
	 * @ExcelField(title = "ATM币7", align = 2) private String atm7; // ATM
	 * 
	 * @ExcelField(title = "ATM币8", align = 2) private String atm8; // ATM
	 * 
	 * @ExcelField(title = "ATM币9", align = 2) private String atm9; // ATM
	 * 
	 * @ExcelField(title = "ATM币10", align = 2) private String atm10; // ATM
	 */ @ExcelField(title = "查询时间", align = 2)
	private Date searchDate;// 查询时间
	@ExcelField(title = "查询时间", align = 2)
	private String dates;// 查询时间excel
	@ExcelField(title = "开始时间", align = 2)
	private String searchDateStart;// 开始时间
	@ExcelField(title = "结束时间", align = 2)
	private String searchDateEnd;// 结束时间
	private Date createTimeStart;// 开始时间
	private Date createTimeEnd;// 结束时间
	private List<String> businessTypes;// 业务类型列表
	private String status;// 状态
	@ExcelField(title = "金额总计", align = 2)
	private String count;// excel总金额
	private String busType;// 业务类型
	private Office rOffice;// 登记机构
	@ExcelField(title = "日期", align = 2)
	private String filterCondition;// 查询条件（年月日周季度）

	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	private String officeId;
	private String officeName;
	/* end */

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public Office getrOffice() {
		return rOffice;
	}

	public void setrOffice(Office rOffice) {
		this.rOffice = rOffice;
	}

	public String getDates() {
		return dates;
	}

	public void setDates(String dates) {
		this.dates = dates;
	}

	public String getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(String outAmount) {
		this.outAmount = outAmount;
	}

	public String getRe1() {
		if (StringUtils.isNotBlank(this.re1)) {
			return this.re1;
		} else {
			return "0";
		}
	}

	public void setRe1(String re1) {
		if (StringUtils.isNotBlank(re1)) {
			this.re1 = re1;
		} else {
			this.re1 = "0";
		}

	}

	public String getRe2() {
		if (StringUtils.isNotBlank(this.re2)) {
			return this.re2;
		} else {
			return "0";
		}
	}

	public void setRe2(String re2) {
		if (StringUtils.isNotBlank(re2)) {
			this.re2 = re2;
		} else {
			this.re2 = "0";
		}
	}

	public String getRe3() {
		if (StringUtils.isNotBlank(this.re3)) {
			return this.re3;
		} else {
			return "0";
		}
	}

	public void setRe3(String re3) {
		if (StringUtils.isNotBlank(re3)) {
			this.re3 = re3;
		} else {
			this.re3 = "0";
		}
	}

	public String getRe4() {
		if (StringUtils.isNotBlank(this.re4)) {
			return this.re4;
		} else {
			return "0";
		}
	}

	public void setRe4(String re4) {
		if (StringUtils.isNotBlank(re4)) {
			this.re4 = re4;
		} else {
			this.re4 = "0";
		}
	}

	public String getRe5() {
		if (StringUtils.isNotBlank(this.re5)) {
			return this.re5;
		} else {
			return "0";
		}
	}

	public void setRe5(String re5) {
		if (StringUtils.isNotBlank(re5)) {
			this.re5 = re5;
		} else {
			this.re5 = "0";
		}
	}

	public String getRe6() {
		if (StringUtils.isNotBlank(this.re6)) {
			return this.re6;
		} else {
			return "0";
		}
	}

	public void setRe6(String re6) {
		if (StringUtils.isNotBlank(re6)) {
			this.re6 = re6;
		} else {
			this.re6 = "0";
		}
	}

	public String getRe7() {
		if (StringUtils.isNotBlank(this.re7)) {
			return this.re7;
		} else {
			return "0";
		}
	}

	public void setRe7(String re7) {
		if (StringUtils.isNotBlank(re7)) {
			this.re7 = re7;
		} else {
			this.re7 = "0";
		}
	}

	public String getRe8() {
		if (StringUtils.isNotBlank(this.re8)) {
			return this.re8;
		} else {
			return "0";
		}
	}

	public void setRe8(String re8) {
		if (StringUtils.isNotBlank(re8)) {
			this.re8 = re8;
		} else {
			this.re8 = "0";
		}
	}

	public String getRe9() {
		if (StringUtils.isNotBlank(this.re9)) {
			return this.re9;
		} else {
			return "0";
		}
	}

	public void setRe9(String re9) {
		if (StringUtils.isNotBlank(re9)) {
			this.re9 = re9;
		} else {
			this.re9 = "0";
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

	public String getRe10() {
		if (StringUtils.isNotBlank(this.re10)) {
			return this.re10;
		} else {
			return "0";
		}
	}

	public void setRe10(String re10) {
		if (StringUtils.isNotBlank(re10)) {
			this.re10 = re10;
		} else {
			this.re10 = "0";
		}
	}

	public String getAtm1() {
		if (StringUtils.isNotBlank(this.atm1)) {
			return this.atm1;
		} else {
			return "0";
		}
	}

	public void setAtm1(String atm1) {
		if (StringUtils.isNotBlank(atm1)) {
			this.atm1 = atm1;
		} else {
			this.atm1 = "0";
		}
	}

	public String getAtm2() {
		if (StringUtils.isNotBlank(this.atm2)) {
			return this.atm2;
		} else {
			return "0";
		}
	}

	public void setAtm2(String atm2) {
		if (StringUtils.isNotBlank(atm2)) {
			this.atm2 = atm2;
		} else {
			this.atm2 = "0";
		}
	}

	public String getAtm3() {
		if (StringUtils.isNotBlank(this.atm3)) {
			return this.atm3;
		} else {
			return "0";
		}
	}

	public void setAtm3(String atm3) {
		if (StringUtils.isNotBlank(atm3)) {
			this.atm3 = atm3;
		} else {
			this.atm3 = "0";
		}
	}

	public String getCl1() {
		if (StringUtils.isNotBlank(this.cl1)) {
			return this.cl1;
		} else {
			return "0";
		}
	}

	public void setCl1(String cl1) {
		if (StringUtils.isNotBlank(cl1)) {
			this.cl1 = cl1;
		} else {
			this.cl1 = "0";
		}
	}

	public String getCl2() {
		if (StringUtils.isNotBlank(this.cl2)) {
			return this.cl2;
		} else {
			return "0";
		}
	}

	public void setCl2(String cl2) {
		if (StringUtils.isNotBlank(cl2)) {
			this.cl2 = cl2;
		} else {
			this.cl2 = "0";
		}
	}

	public String getCl3() {
		if (StringUtils.isNotBlank(this.cl3)) {
			return this.cl3;
		} else {
			return "0";
		}
	}

	public void setCl3(String cl3) {
		if (StringUtils.isNotBlank(cl3)) {
			this.cl3 = cl3;
		} else {
			this.cl3 = "0";
		}
	}

	public String getCl4() {
		if (StringUtils.isNotBlank(this.cl4)) {
			return this.cl4;
		} else {
			return "0";
		}
	}

	public void setCl4(String cl4) {
		if (StringUtils.isNotBlank(cl4)) {
			this.cl4 = cl4;
		} else {
			this.cl4 = "0";
		}
	}

	public String getCl5() {
		if (StringUtils.isNotBlank(this.cl5)) {
			return this.cl5;
		} else {
			return "0";
		}
	}

	public void setCl5(String cl5) {
		if (StringUtils.isNotBlank(cl5)) {
			this.cl5 = cl5;
		} else {
			this.cl5 = "0";
		}
	}

	public String getCl6() {
		if (StringUtils.isNotBlank(this.cl6)) {
			return this.cl6;
		} else {
			return "0";
		}
	}

	public void setCl6(String cl6) {
		if (StringUtils.isNotBlank(cl6)) {
			this.cl6 = cl6;
		} else {
			this.cl6 = "0";
		}
	}

	public String getCl7() {
		if (StringUtils.isNotBlank(this.cl7)) {
			return this.cl7;
		} else {
			return "0";
		}
	}

	public void setCl7(String cl7) {
		if (StringUtils.isNotBlank(cl7)) {
			this.cl7 = cl7;
		} else {
			this.cl7 = "0";
		}
	}

	public String getCl8() {
		if (StringUtils.isNotBlank(this.cl8)) {
			return this.cl8;
		} else {
			return "0";
		}
	}

	public void setCl8(String cl8) {
		if (StringUtils.isNotBlank(cl8)) {
			this.cl8 = cl8;
		} else {
			this.cl8 = "0";
		}
	}

	public String getCl9() {
		if (StringUtils.isNotBlank(this.cl9)) {
			return this.cl9;
		} else {
			return "0";
		}
	}

	public void setCl9(String cl9) {
		if (StringUtils.isNotBlank(cl9)) {
			this.cl9 = cl9;
		} else {
			this.cl9 = "0";
		}
	}

	public String getOutNo() {
		return outNo;
	}

	public void setOutNo(String outNo) {
		this.outNo = outNo;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getCountYqf() {
		return countYqf;
	}

	public void setCountYqf(String countYqf) {
		this.countYqf = countYqf;
	}

	public String getCountAtm() {
		return countAtm;
	}

	public void setCountAtm(String countAtm) {
		this.countAtm = countAtm;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(List<String> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
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

	public CustomerClearance() {
		super();
	}

	public CustomerClearance(String id) {
		super(id);
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

	public String getCountWzq() {
		return countWzq;
	}

	public void setCountWzq(String countWzq) {
		this.countWzq = countWzq;
	}

	public String getCountCsq() {
		return countCsq;
	}

	public void setCountCsq(String countCsq) {
		this.countCsq = countCsq;
	}

	public String getCountDqf() {
		return countDqf;
	}

	public void setCountDqf(String countDqf) {
		this.countDqf = countDqf;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

}