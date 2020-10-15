package com.coffer.core.common.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.coffer.core.common.launch.TraditionalReportLaunch;

public class TraditionalReportJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
				
				TraditionalReportLaunch traditionalReportLaunch = new TraditionalReportLaunch();
				traditionalReportLaunch.setJobName(context.getJobDetail().getName());
				traditionalReportLaunch.doTask(1);
	}

}
