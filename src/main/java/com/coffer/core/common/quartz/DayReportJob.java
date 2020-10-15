package com.coffer.core.common.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.coffer.core.common.launch.DayReportLaunch;

public class DayReportJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
				
				DayReportLaunch dayReportLaunch = new DayReportLaunch();
				dayReportLaunch.setJobName(context.getJobDetail().getName());
				dayReportLaunch.doTask(1);
	}

}
