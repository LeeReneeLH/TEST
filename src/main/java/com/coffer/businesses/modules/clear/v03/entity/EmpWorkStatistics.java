package com.coffer.businesses.modules.clear.v03.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.common.utils.excel.annotation.ExcelField;
import com.coffer.core.modules.sys.entity.Office;
import com.google.common.collect.Lists;

/**
 * 员工工作量统计Entity
 * 
 * @author XL
 * @version 2017-09-04
 */
public class EmpWorkStatistics extends DataEntity<EmpWorkStatistics> {

	private static final long serialVersionUID = 1L;
	private String detailId; // 明细ID
	private String empNo; // 员工编号
	/* 增加导出excelField wzj 2017-11-23 begin */
	@ExcelField(title = "员工名称", align = 2)
	/* end */
	private String empName; // 员工姓名
	private Long totalCount; // 总捆数
	private String workingPositionType;// 工位类型
	private String tranStatus; // 清分状态: 08 已清分 09 清分中
	private Long jxNormal = 0L;// 机械正常清分
	private Long jxRepeat = 0L;// 机械重复清分
	private Long lsxNormal = 0L;// 流水线正常清分
	private Long lsxRepeat = 0L;// 流水线重复清分
	private Long sgNormal = 0L;// 手工正常清分
	private Long sgRepeat = 0L;// 手工重复清分
	private Long ccClear = 0L;// 抽查清分
	private Long ccComplexPoint = 0L;// 抽查复点
	private String denomination;// 面值
	private String planType; // 计划类型：01-正常清分(计划内) 02-重复清分(计划外) 03-抽查
	private Long totalCountSta;// 统计总数
	private String busType; // 业务类型：08：复点；09：清分
	private String taskType; // 任务类型: 01-任务分配 02-任务回收 03-任务登记
	private Date operateTimeStart;// 开始时间
	private Date operateTimeEnd;// 结束时间
	private String searchDateStart;// 开始时间
	private String searchDateEnd;// 结束时间
	private Long errorCount = 0L;// 差错笔数
	private List<String> taskTypes = Lists.newArrayList();// 任务类型集合：01-任务分配
	
	/* 查询状态 0年1季度2月3周 修改人：wxz 修改时间：2017-10-23 begin */
	private String filterCondition;
	// 日期
	private String handInDate;
	//机械清分（统计图表使用）
	private String mechanicsClear;
	//清分流水线（统计图表使用）
	private String assemblyLineClear;
	//手工清分（统计图表使用）
	private String manualClear;
	/* end */
	
	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */
	/* 增加按员工名称进行机械清分手工清分复点的面值数量显示元素 wzj 2017-11-22 begin */
	@ExcelField(title = "机械清分1", align = 2)
	private String j1; // 机械清分100元
	@ExcelField(title = "机械清分2", align = 2)
	private String j2; // 机械清分50元
	@ExcelField(title = "机械清分3", align = 2)
	private String j3; // 机械清分20元
	@ExcelField(title = "机械清分4", align = 2)
	private String j4; // 机械清分10元
	@ExcelField(title = "机械清分5", align = 2)
	private String j5; // 机械清分5元
	@ExcelField(title = "机械清分6", align = 2)
	private String j6; // 机械清分2元
	@ExcelField(title = "机械清分7", align = 2)
	private String j7; // 机械清分1元
	@ExcelField(title = "机械清分8", align = 2)
	private String j8; // 机械清分0.5元
	@ExcelField(title = "机械清分9", align = 2)
	private String j9; // 机械清分0.2元
	@ExcelField(title = "机械清分10", align = 2)
	private String j10; // 机械清分0.1元
	@ExcelField(title = "手工清分1", align = 2)
	private String s1; // 手工清分100元
	@ExcelField(title = "手工清分2", align = 2)
	private String s2; // 手工清分50元
	@ExcelField(title = "手工清分3", align = 2)
	private String s3; // 手工清分20元
	@ExcelField(title = "手工清分4", align = 2)
	private String s4; // 手工清分10元
	@ExcelField(title = "手工清分5", align = 2)
	private String s5; // 手工清分5元
	@ExcelField(title = "手工清分6", align = 2)
	private String s6; // 手工清分2元
	@ExcelField(title = "手工清分7", align = 2)
	private String s7; // 手工清分1元
	@ExcelField(title = "手工清分8", align = 2)
	private String s8; // 手工清分0.5元
	@ExcelField(title = "手工清分9", align = 2)
	private String s9; // 手工清分0.2元
	@ExcelField(title = "手工清分10", align = 2)
	private String s10; // 手工清分0.1元
	@ExcelField(title = "复点1", align = 2)
	private String f1; // 复点100元
	@ExcelField(title = "复点2", align = 2)
	private String f2; // 复点50元
	@ExcelField(title = "复点3", align = 2)
	private String f3; // 复点20元
	@ExcelField(title = "复点4", align = 2)
	private String f4; // 复点10元
	@ExcelField(title = "复点5", align = 2)
	private String f5; // 复点5元
	@ExcelField(title = "复点6", align = 2)
	private String f6; // 复点2元
	@ExcelField(title = "复点7", align = 2)
	private String f7; // 复点1元
	@ExcelField(title = "复点8", align = 2)
	private String f8; // 复点0.5元
	@ExcelField(title = "复点9", align = 2)
	private String f9; // 复点0.2元
	@ExcelField(title = "复点10", align = 2)
	private String f10; // 复点0.1元
	@ExcelField(title = "序号", align = 2)
	private String number;// 导出表格序号
	@ExcelField(title = "机构名称", align = 2)
	private String officeName;
	private String officeId;
	public String getJ1() {
		if (StringUtils.isNotBlank(this.j1)) {
			return this.j1;
		} else {
			return "0";
		}
	}

	public void setJ1(String j1) {
		if (StringUtils.isNotBlank(j1)) {
			this.j1 = j1;
		} else {
			this.j1 = "0";
		}

	}

	public String getJ2() {
		if (StringUtils.isNotBlank(this.j2)) {
			return this.j2;
		} else {
			return "0";
		}
	}

	public void setJ2(String j2) {
		if (StringUtils.isNotBlank(j2)) {
			this.j2 = j2;
		} else {
			this.j2 = "0";
		}
	}

	public String getJ3() {
		if (StringUtils.isNotBlank(this.j3)) {
			return this.j3;
		} else {
			return "0";
		}
	}

	public void setJ3(String j3) {
		if (StringUtils.isNotBlank(j3)) {
			this.j3 = j3;
		} else {
			this.j3 = "0";
		}
	}

	public String getJ4() {
		if (StringUtils.isNotBlank(this.j4)) {
			return this.j4;
		} else {
			return "0";
		}
	}

	public void setJ4(String j4) {
		if (StringUtils.isNotBlank(j4)) {
			this.j4 = j4;
		} else {
			this.j4 = "0";
		}
	}

	public String getJ5() {
		if (StringUtils.isNotBlank(this.j5)) {
			return this.j5;
		} else {
			return "0";
		}
	}

	public void setJ5(String j5) {
		if (StringUtils.isNotBlank(j5)) {
			this.j5 = j5;
		} else {
			this.j5 = "0";
		}
	}

	public String getJ6() {
		if (StringUtils.isNotBlank(this.j6)) {
			return this.j6;
		} else {
			return "0";
		}
	}

	public void setJ6(String j6) {
		if (StringUtils.isNotBlank(j6)) {
			this.j6 = j6;
		} else {
			this.j6 = "0";
		}
	}

	public String getJ7() {
		if (StringUtils.isNotBlank(this.j7)) {
			return this.j7;
		} else {
			return "0";
		}
	}

	public void setJ7(String j7) {
		if (StringUtils.isNotBlank(j7)) {
			this.j7 = j7;
		} else {
			this.j7 = "0";
		}
	}

	public String getJ8() {
		if (StringUtils.isNotBlank(this.j8)) {
			return this.j8;
		} else {
			return "0";
		}
	}

	public void setJ8(String j8) {
		if (StringUtils.isNotBlank(j8)) {
			this.j8 = j8;
		} else {
			this.j8 = "0";
		}
	}

	public String getJ9() {
		if (StringUtils.isNotBlank(this.j9)) {
			return this.j9;
		} else {
			return "0";
		}
	}

	public void setJ9(String j9) {
		if (StringUtils.isNotBlank(j9)) {
			this.j9 = j9;
		} else {
			this.j9 = "0";
		}
	}

	public String getJ10() {
		if (StringUtils.isNotBlank(this.j10)) {
			return this.j10;
		} else {
			return "0";
		}
	}

	public void setJ10(String j10) {
		if (StringUtils.isNotBlank(j10)) {
			this.j10 = j10;
		} else {
			this.j10 = "0";
		}
	}

	public String getS1() {
		if (StringUtils.isNotBlank(this.s1)) {
			return this.s1;
		} else {
			return "0";
		}
	}

	public void setS1(String s1) {
		if (StringUtils.isNotBlank(s1)) {
			this.s1 = s1;
		} else {
			this.s1 = "0";
		}
	}

	public String getS2() {
		if (StringUtils.isNotBlank(this.s2)) {
			return this.s2;
		} else {
			return "0";
		}
	}

	public void setS2(String s2) {
		if (StringUtils.isNotBlank(s2)) {
			this.s2 = s2;
		} else {
			this.s2 = "0";
		}
	}

	public String getS3() {
		if (StringUtils.isNotBlank(this.s3)) {
			return this.s3;
		} else {
			return "0";
		}
	}

	public void setS3(String s3) {
		if (StringUtils.isNotBlank(s3)) {
			this.s3 = s3;
		} else {
			this.s3 = "0";
		}
	}

	public String getS4() {
		if (StringUtils.isNotBlank(this.s4)) {
			return this.s4;
		} else {
			return "0";
		}
	}

	public void setS4(String s4) {
		if (StringUtils.isNotBlank(s4)) {
			this.s4 = s4;
		} else {
			this.s4 = "0";
		}
	}

	public String getS5() {
		if (StringUtils.isNotBlank(this.s5)) {
			return this.s5;
		} else {
			return "0";
		}
	}

	public void setS5(String s5) {
		if (StringUtils.isNotBlank(s5)) {
			this.s5 = s5;
		} else {
			this.s5 = "0";
		}
	}

	public String getS6() {
		if (StringUtils.isNotBlank(this.s6)) {
			return this.s6;
		} else {
			return "0";
		}
	}

	public void setS6(String s6) {
		if (StringUtils.isNotBlank(s6)) {
			this.s6 = s6;
		} else {
			this.s6 = "0";
		}
	}

	public String getS7() {
		if (StringUtils.isNotBlank(this.s7)) {
			return this.s7;
		} else {
			return "0";
		}
	}

	public void setS7(String s7) {
		if (StringUtils.isNotBlank(s7)) {
			this.s7 = s7;
		} else {
			this.s7 = "0";
		}
	}

	public String getS8() {
		if (StringUtils.isNotBlank(this.s8)) {
			return this.s8;
		} else {
			return "0";
		}
	}

	public void setS8(String s8) {
		if (StringUtils.isNotBlank(s8)) {
			this.s8 = s8;
		} else {
			this.s8 = "0";
		}
	}

	public String getS9() {
		if (StringUtils.isNotBlank(this.s9)) {
			return this.s9;
		} else {
			return "0";
		}
	}

	public void setS9(String s9) {
		if (StringUtils.isNotBlank(s9)) {
			this.s9 = s9;
		} else {
			this.s9 = "0";
		}
	}

	public String getS10() {
		if (StringUtils.isNotBlank(this.s10)) {
			return this.s10;
		} else {
			return "0";
		}
	}

	public void setS10(String s10) {
		if (StringUtils.isNotBlank(s10)) {
			this.s10 = s10;
		} else {
			this.s10 = "0";
		}
	}

	public String getF1() {
		if (StringUtils.isNotBlank(this.f1)) {
			return this.f1;
		} else {
			return "0";
		}
	}

	public void setF1(String f1) {
		if (StringUtils.isNotBlank(f1)) {
			this.f1 = f1;
		} else {
			this.f1 = "0";
		}
	}

	public String getF2() {
		if (StringUtils.isNotBlank(this.f2)) {
			return this.f2;
		} else {
			return "0";
		}
	}

	public void setF2(String f2) {
		if (StringUtils.isNotBlank(f2)) {
			this.f2 = f2;
		} else {
			this.f2 = "0";
		}
	}

	public String getF3() {
		if (StringUtils.isNotBlank(this.f3)) {
			return this.f3;
		} else {
			return "0";
		}
	}

	public void setF3(String f3) {
		if (StringUtils.isNotBlank(f3)) {
			this.f3 = f3;
		} else {
			this.f3 = "0";
		}
	}

	public String getF4() {
		if (StringUtils.isNotBlank(this.f4)) {
			return this.f4;
		} else {
			return "0";
		}
	}

	public void setF4(String f4) {
		if (StringUtils.isNotBlank(f4)) {
			this.f4 = f4;
		} else {
			this.f4 = "0";
		}
	}

	public String getF5() {
		if (StringUtils.isNotBlank(this.f5)) {
			return this.f5;
		} else {
			return "0";
		}
	}

	public void setF5(String f5) {
		if (StringUtils.isNotBlank(f5)) {
			this.f5 = f5;
		} else {
			this.f5 = "0";
		}
	}

	public String getF6() {
		if (StringUtils.isNotBlank(this.f6)) {
			return this.f6;
		} else {
			return "0";
		}
	}

	public void setF6(String f6) {
		if (StringUtils.isNotBlank(f6)) {
			this.f6 = f6;
		} else {
			this.f6 = "0";
		}
	}

	public String getF7() {
		if (StringUtils.isNotBlank(this.f7)) {
			return this.f7;
		} else {
			return "0";
		}
	}

	public void setF7(String f7) {
		if (StringUtils.isNotBlank(f7)) {
			this.f7 = f7;
		} else {
			this.f7 = "0";
		}
	}

	public String getF8() {
		if (StringUtils.isNotBlank(this.f8)) {
			return this.f8;
		} else {
			return "0";
		}
	}

	public void setF8(String f8) {
		if (StringUtils.isNotBlank(f8)) {
			this.f8 = f8;
		} else {
			this.f8 = "0";
		}
	}

	public String getF9() {
		if (StringUtils.isNotBlank(this.f9)) {
			return this.f9;
		} else {
			return "0";
		}
	}

	public void setF9(String f9) {
		if (StringUtils.isNotBlank(f9)) {
			this.f9 = f9;
		} else {
			this.f9 = "0";
		}
	}

	public String getF10() {
		if (StringUtils.isNotBlank(this.f10)) {
			return this.f10;
		} else {
			return "0";
		}
	}

	public void setF10(String f10) {
		if (StringUtils.isNotBlank(f10)) {
			this.f10 = f10;
		} else {
			this.f10 = "0";
		}
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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
	/* end */
	public String getMechanicsClear() {
		return mechanicsClear;
	}

	public void setMechanicsClear(String mechanicsClear) {
		this.mechanicsClear = mechanicsClear;
	}

	public String getAssemblyLineClear() {
		return assemblyLineClear;
	}

	public void setAssemblyLineClear(String assemblyLineClear) {
		this.assemblyLineClear = assemblyLineClear;
	}

	public String getManualClear() {
		return manualClear;
	}

	public void setManualClear(String manualClear) {
		this.manualClear = manualClear;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getHandInDate() {
		return handInDate;
	}

	public void setHandInDate(String handInDate) {
		this.handInDate = handInDate;
	}

	public EmpWorkStatistics() {
		super();
	}

	public EmpWorkStatistics(String id) {
		super(id);
	}

	@Length(min = 1, max = 64, message = "明细ID长度必须介于 1 和 64 之间")
	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}
	
	@Length(min = 0, max = 64, message = "员工编号长度必须介于 0 和 64 之间")
	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	@Length(min = 0, max = 128, message = "员工姓名长度必须介于 0 和 128 之间")
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

	public String getWorkingPositionType() {
		return workingPositionType;
	}

	public void setWorkingPositionType(String workingPositionType) {
		this.workingPositionType = workingPositionType;
	}

	public String getTranStatus() {
		return tranStatus;
	}

	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}

	public Long getJxNormal() {
		return jxNormal;
	}

	public void setJxNormal(Long jxNormal) {
		this.jxNormal = jxNormal;
	}

	public Long getJxRepeat() {
		return jxRepeat;
	}

	public void setJxRepeat(Long jxRepeat) {
		this.jxRepeat = jxRepeat;
	}

	public Long getLsxNormal() {
		return lsxNormal;
	}

	public void setLsxNormal(Long lsxNormal) {
		this.lsxNormal = lsxNormal;
	}

	public Long getLsxRepeat() {
		return lsxRepeat;
	}

	public void setLsxRepeat(Long lsxRepeat) {
		this.lsxRepeat = lsxRepeat;
	}

	public Long getSgNormal() {
		return sgNormal;
	}

	public void setSgNormal(Long sgNormal) {
		this.sgNormal = sgNormal;
	}

	public Long getSgRepeat() {
		return sgRepeat;
	}

	public void setSgRepeat(Long sgRepeat) {
		this.sgRepeat = sgRepeat;
	}

	public Long getCcClear() {
		return ccClear;
	}

	public void setCcClear(Long ccClear) {
		this.ccClear = ccClear;
	}

	public Long getCcComplexPoint() {
		return ccComplexPoint;
	}

	public void setCcComplexPoint(Long ccComplexPoint) {
		this.ccComplexPoint = ccComplexPoint;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public Long getTotalCountSta() {
		return totalCountSta;
	}

	public void setTotalCountSta(Long totalCountSta) {
		this.totalCountSta = totalCountSta;
	}

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
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

	public Long getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Long errorCount) {
		this.errorCount = errorCount;
	}

	public List<String> getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(List<String> taskTypes) {
		this.taskTypes = taskTypes;
	}

	public Date getOperateTimeStart() {
		return operateTimeStart;
	}

	public void setOperateTimeStart(Date operateTimeStart) {
		this.operateTimeStart = operateTimeStart;
	}

	public Date getOperateTimeEnd() {
		return operateTimeEnd;
	}

	public void setOperateTimeEnd(Date operateTimeEnd) {
		this.operateTimeEnd = operateTimeEnd;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

}