package com.coffer.core.common.launch;



import com.coffer.core.common.task.TaskSchedulingTask;

public class TaskSchedulingLaunch extends Launch {
	
	@Override
	public void doTask(int isAsync) {
		// 线程锁判定
        if(checkLock()){
            TaskSchedulingTask taskSchedulingTask = new TaskSchedulingTask();
            if(isAsync==1){
                disposer.submit(taskSchedulingTask);
            }else{
            	taskSchedulingTask.run();
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
