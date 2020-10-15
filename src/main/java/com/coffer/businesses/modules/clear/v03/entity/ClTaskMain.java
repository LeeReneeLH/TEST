package com.coffer.businesses.modules.clear.v03.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.coffer.core.common.persistence.DataEntity;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 清分管理Entity
 * @author QPH
 * @version 2017-08-15
 */
public class ClTaskMain extends DataEntity<ClTaskMain> {
	
	private static final long serialVersionUID = 1L;
	private String taskNo;		// 业务流水
	private String valueKey;		// 券别
	private String busType;		// 业务类型：1：复点；2：清分
	private String taskType; // 任务类型: 01-任务分配 02-任务回收 03-员工工作量
	private String tranStatus;		// 清分状态:  08 已清分   09  清分中
	private String planType;		// 计划类型：01-正常清分(计划内) 02-重复清分(计划外)
	private String joinManNo;		// 交接人编号
	private String joinManName;		// 交接人姓名
	private Long totalCount;		// 总捆数
	private BigDecimal totalAmt;		// 总金额
	private String operatorBy;		// 操作人编号
	private String operatorName;		// 操作人姓名
	private Date operateDate;		// 操作时间
	private String checkStatus;		// 是否抽查: 1 - 否  2 - 是
	private String checkType;		// 任务种类: 01-普通任务  02- 抽查任务
	private String clearGroup;		// 清分组
	
	private String goodsId; // 物品ID
	
	/** 币种 */
	private String currency;
	/** 类别 */
	private String classification;
	/** 套别 */
	private String sets;
	/** 材质 */
	private String cash;
	/** 面值 */
	private String denomination;
	/** 单位 */
	private String unit;
	
	private ClTaskDetail clTaskDetail; //清分管理明细
	
	/** 清分管理明细列表 */
	private List<ClTaskDetail> clTaskDetailList;
	
	/** 页面对应的开发时间和结束时间（查询用） **/
	private Date createTimeStart;
	private Date createTimeEnd;

	/** 页面对应的开发时间和结束时间（查询用） **/
	private String searchDateStart;
	private String searchDateEnd;
	
	/** 追加操作开始时间和结束时间 修改人：xl 修改时间：2017-10-12 begin **/
	private String operateDateStart;
	private String operateDateEnd;
	/** end **/

	/** 当前登陆用户信息 */
	private User loginUser;
	
	/** 清分组人员详细(页面传递用) */
	private List<String> UserList;
	
	/** 分配数量 */
	private Long distributionAmount;

	/* 增加office 发生机构（清分中心）修改人：qph 修改时间：2017-11-14 begin */
	private Office office;
	/* end */
	/** 任务类型（集合） */
	private List<String> taskTypes;
	/** 任务人员 */
	private User taskUser;
	private String workingPositionType;// 工位类型
	public ClTaskMain() {
		super();
	}

	public ClTaskMain(String id){
		super(id);
	}

	@Length(min=1, max=64, message="业务流水长度必须介于 1 和 64 之间")
	public String getTaskNo() {
		return taskNo;
	}

	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}

	
	@Length(min=0, max=2, message="币种长度必须介于 0 和 2 之间")
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@Length(min=0, max=5, message="单位长度必须介于 0 和 5 之间")
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	@Length(min=0, max=2, message="券别长度必须介于 0 和 2 之间")
	public String getValueKey() {
		return valueKey;
	}

	public void setValueKey(String valueKey) {
		this.valueKey = valueKey;
	}
	
	@Length(min=0, max=1, message="业务类型：1：复点；2：清分长度必须介于 0 和 1 之间")
	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}
	
	@Length(min=0, max=2, message="任务类型: 01-任务分配  02-任务回收长度必须介于 0 和 2 之间")
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	@Length(min=0, max=2, message="清分状态:  08 已清分   09  清分中长度必须介于 0 和 2 之间")
	public String getTranStatus() {
		return tranStatus;
	}

	public void setTranStatus(String tranStatus) {
		this.tranStatus = tranStatus;
	}
	
	@Length(min=0, max=2, message="计划类型：01-正常清分(计划内) 02-重复清分(计划外)长度必须介于 0 和 2 之间")
	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}
	
	@Length(min=0, max=20, message="交接人编号长度必须介于 0 和 20 之间")
	public String getJoinManNo() {
		return joinManNo;
	}

	public void setJoinManNo(String joinManNo) {
		this.joinManNo = joinManNo;
	}
	
	@Length(min=0, max=200, message="交接人姓名长度必须介于 0 和 200 之间")
	public String getJoinManName() {
		return joinManName;
	}

	public void setJoinManName(String joinManName) {
		this.joinManName = joinManName;
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
	
	@Length(min=0, max=20, message="操作人编号长度必须介于 0 和 20 之间")
	public String getOperatorBy() {
		return operatorBy;
	}

	public void setOperatorBy(String operatorBy) {
		this.operatorBy = operatorBy;
	}
	
	@Length(min=0, max=60, message="操作人姓名长度必须介于 0 和 60 之间")
	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getOperateDate() {
		return operateDate;
	}

	public void setOperateDate(Date operateDate) {
		this.operateDate = operateDate;
	}
	
	@Length(min=0, max=1, message="是否抽查: 1 - 否  2 - 是长度必须介于 0 和 1 之间")
	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}
	
	@Length(min=0, max=2, message="任务种类: 01-普通任务  02- 抽查任务长度必须介于 0 和 2 之间")
	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
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

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}

	public ClTaskDetail getClTaskDetail() {
		return clTaskDetail;
	}

	public void setClTaskDetail(ClTaskDetail clTaskDetail) {
		this.clTaskDetail = clTaskDetail;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getSets() {
		return sets;
	}

	public void setSets(String sets) {
		this.sets = sets;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public Long getDistributionAmount() {
		return distributionAmount;
	}

	public void setDistributionAmount(Long distributionAmount) {
		this.distributionAmount = distributionAmount;
	}

	public String getClearGroup() {
		return clearGroup;
	}

	public void setClearGroup(String clearGroup) {
		this.clearGroup = clearGroup;
	}

	public List<String> getUserList() {
		return UserList;
	}

	public void setUserList(List<String> userList) {
		UserList = userList;
	}

	public List<ClTaskDetail> getClTaskDetailList() {
		return clTaskDetailList;
	}

	public void setClTaskDetailList(List<ClTaskDetail> clTaskDetailList) {
		this.clTaskDetailList = clTaskDetailList;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getOperateDateStart() {
		return operateDateStart;
	}

	public void setOperateDateStart(String operateDateStart) {
		this.operateDateStart = operateDateStart;
	}

	public String getOperateDateEnd() {
		return operateDateEnd;
	}

	public void setOperateDateEnd(String operateDateEnd) {
		this.operateDateEnd = operateDateEnd;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public List<String> getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(List<String> taskTypes) {
		this.taskTypes = taskTypes;
	}

	public String getWorkingPositionType() {
		return workingPositionType;
	}

	public void setWorkingPositionType(String workingPositionType) {
		this.workingPositionType = workingPositionType;
	}

	public User getTaskUser() {
		return taskUser;
	}

	public void setTaskUser(User taskUser) {
		this.taskUser = taskUser;
	}

	@Override
	public String toString() {
		return "ClTaskMain [taskNo=" + taskNo + ", busType=" + busType + ", taskType=" + taskType + ", totalCount="
				+ totalCount + ", totalAmt=" + totalAmt + "]";
	}
	
}