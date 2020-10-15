package com.coffer.core.common.utils;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class TaskExecutorDisposer {
	private ThreadPoolTaskExecutor taskExecutor;

	public TaskExecutorDisposer() {
	}

	/**
	 * Title: before
	 * <p>Description: </p>
	 * @author:     wangpengyu 
	 * void    
	 */
	private void before() {
		taskExecutor = (ThreadPoolTaskExecutor) SpringContextHolder
				.getBean("taskExecutor");
		if (taskExecutor == null) {
			System.out.println("oh,sorry the system's ThreadPoolTaskExecutor can not get!");
		}
	}

	/**
	 * Title: submit
	 * <p>Description: </p>
	 * @author:     wangpengyu
	 * @param task 
	 * void    
	 */
	public void submit(Runnable task) {
		before();
		taskExecutor.execute(task);
	}

}
