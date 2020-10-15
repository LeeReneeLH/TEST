package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;

/**
 * 清分管理明细Entity
 * @author QPH
 * @version 2017-08-15
 */
public class ClTaskDetail extends DataEntity<ClTaskDetail> {
	
	private static final long serialVersionUID = 1L;
	private String detailId;		// 明细ID
	private String officeNo;		// 职位编号
	private String empNo;		// 员工编号
	private String empName;		// 员工姓名
	private Long totalCount;		// 总捆数
	private BigDecimal totalAmt;		// 总金额
	private String mId;		// 主表ID
	private String workingPositionType;// 工位类型
	
	/** 面值 */
	private String denomination;	//打印时统计使用
	/** 币种*/
	private String currency; 		//打印时统计使用

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public ClTaskDetail() {
		super();
	}

	public ClTaskDetail(String id){
		super(id);
	}

	@Length(min=1, max=64, message="明细ID长度必须介于 1 和 64 之间")
	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	
	@Length(min=0, max=60, message="职位编号长度必须介于 0 和 60 之间")
	public String getOfficeNo() {
		return officeNo;
	}

	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}
	
	@Length(min=0, max=64, message="员工编号长度必须介于 0 和 64 之间")
	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}
	
	@Length(min=0, max=128, message="员工姓名长度必须介于 0 和 128 之间")
	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}
	
	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	
	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}
	
	@Length(min=0, max=64, message="主表ID长度必须介于 0 和 64 之间")
	public String getMId() {
		return mId;
	}

	public void setMId(String mId) {
		this.mId = mId;
	}

	public String getWorkingPositionType() {
		return workingPositionType;
	}

	public void setWorkingPositionType(String workingPositionType) {
		this.workingPositionType = workingPositionType;
	}

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}
	
}