package com.coffer.core.common.launch;

import com.coffer.core.common.task.AccountCheckTask;

public class AccountCheckLaunch extends Launch {
	private String jobName;
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public void doTask(int isAsync) {
		// 线程锁判定
        if(checkLock()){
            AccountCheckTask accountCheckTask = new AccountCheckTask();
            accountCheckTask.setJobName(jobName);
            if(isAsync==1){
                disposer.submit(accountCheckTask);
            }else{
            	accountCheckTask.run();
            }
        }		
	}

	@Override
	public void doTask() {
		doTask(0);
	}

	@Override
	public boolean checkLock() {
		if(lockQty<1){
            return true;
        }		
		return false;
	}

}
