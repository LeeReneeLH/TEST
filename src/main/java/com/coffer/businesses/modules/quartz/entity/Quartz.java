package com.coffer.businesses.modules.quartz.entity;


import java.util.Date;

import com.coffer.core.common.persistence.DataEntity;


public class Quartz  extends DataEntity<Quartz> {
    private String id;

    private String taskName;

    private String triggerName;

    private String triggerGroup;

    private String jobName;

    private String jobGroup;

    private String cron;
    
    private String cronDescribe;

    private String executionClass;

    private String status;

    private String delFlag;

    private String describe;

    private Date createTime;
    
    private String officeId;
    
    private String centerOfficeId;	//中心机构id
    
    private String reportType;	//结算类型
    
    public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getCenterOfficeId() {
		return centerOfficeId;
	}

	public void setCenterOfficeId(String centerOfficeId) {
		this.centerOfficeId = centerOfficeId;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName == null ? null : triggerName.trim();
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup == null ? null : triggerGroup.trim();
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName == null ? null : jobName.trim();
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup == null ? null : jobGroup.trim();
    }

    public String getCronDescribe() {
        return cronDescribe;
    }

    public void setCronDescribe(String cronDescribe) {
        this.cronDescribe = cronDescribe == null ? null : cronDescribe.trim();
    }
    
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron == null ? null : cron.trim();
    }

    public String getExecutionClass() {
        return executionClass;
    }

    public void setExecutionClass(String executionClass) {
        this.executionClass = executionClass == null ? null : executionClass.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId == null ? null : officeId.trim();
	}

}