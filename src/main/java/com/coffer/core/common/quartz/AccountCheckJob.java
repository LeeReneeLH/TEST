package com.coffer.core.common.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.coffer.core.common.launch.AccountCheckLaunch;

public class AccountCheckJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
				
				AccountCheckLaunch accountCheckLaunch = new AccountCheckLaunch();
				accountCheckLaunch.setJobName(context.getJobDetail().getName());
				accountCheckLaunch.doTask(1);
	}

}
