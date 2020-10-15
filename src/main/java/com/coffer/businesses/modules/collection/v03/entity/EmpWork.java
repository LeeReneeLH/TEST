package com.coffer.businesses.modules.collection.v03.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;

/**
 * 员工工作量统计Entity
 * 
 * @author wanglin
 * @version 2017-09-04
 */
public class EmpWork extends DataEntity<EmpWork> {

	private static final long serialVersionUID = 1L;
	
	@ExcelField(title = "清分人", align = 1)
	private String empName; 								// 员工姓名
	@ExcelField(title = "机械清分笔数", align = 3)
	private long machineCount = 0;							// 清分笔数（机械）
	@ExcelField(title = "机械清分金额", align = 3)
	private BigDecimal machineAmount = new BigDecimal(0);	// 清分金额（机械）
	@ExcelField(title = "手工清分笔数", align = 3)
	private long handCount = 0;								// 清分笔数（手工）
	@ExcelField(title = "手工清分金额", align = 3)
	private BigDecimal handAmount = new BigDecimal(0);		// 清分金额（手工）
	@ExcelField(title = "合计", align = 3)
	private BigDecimal sumAmount = new BigDecimal(0);		// 清分金额（合计）
	@ExcelField(title = "差错笔数", align = 3)
	private long diffCount = 0;								// 清分笔数（差错）
	@ExcelField(title = "差错金额", align = 3)
	private BigDecimal diffAmount = new BigDecimal(0);		// 清分金额（差错）
	
	private Date createTimeStart;		// 开始时间
	private Date createTimeEnd;			// 结束时间
	private String searchDateStart;		// 开始时间
	private String searchDateEnd;		// 结束时间
	private String searchClearManNo;	// 清分人
	private String searchClearManId;	// 清分人ID
	
	//private String empName; 			// 员工姓名
	
	public EmpWork() {
		super();
	}

	public EmpWork(String id) {
		super(id);
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public long getMachineCount() {
		return machineCount;
	}

	public void setMachineCount(long machineCount) {
		this.machineCount = machineCount;
	}

	public BigDecimal getMachineAmount() {
		return machineAmount;
	}

	public void setMachineAmount(BigDecimal machineAmount) {
		this.machineAmount = machineAmount;
	}

	public long getHandCount() {
		return handCount;
	}

	public void setHandCount(long handCount) {
		this.handCount = handCount;
	}

	public BigDecimal getHandAmount() {
		return handAmount;
	}

	public void setHandAmount(BigDecimal handAmount) {
		this.handAmount = handAmount;
	}

	public BigDecimal getSumAmount() {
		return sumAmount;
	}

	public void setSumAmount(BigDecimal sumAmount) {
		this.sumAmount = sumAmount;
	}

	public long getDiffCount() {
		return diffCount;
	}

	public void setDiffCount(long diffCount) {
		this.diffCount = diffCount;
	}

	public BigDecimal getDiffAmount() {
		return diffAmount;
	}

	public void setDiffAmount(BigDecimal diffAmount) {
		this.diffAmount = diffAmount;
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

	public String getSearchClearManNo() {
		return searchClearManNo;
	}

	public void setSearchClearManNo(String searchClearManNo) {
		this.searchClearManNo = searchClearManNo;
	}

	public String getSearchClearManId() {
		return searchClearManId;
	}

	public void setSearchClearManId(String searchClearManId) {
		this.searchClearManId = searchClearManId;
	}


}