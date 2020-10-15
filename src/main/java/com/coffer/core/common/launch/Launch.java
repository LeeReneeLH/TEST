package com.coffer.core.common.launch;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.coffer.core.common.utils.TaskExecutorDisposer;

public abstract class Launch {
	/**disposer : 线程池实例*/
	protected TaskExecutorDisposer disposer = new TaskExecutorDisposer();
    
    /**lockQty : 线程锁*/
	protected int lockQty=0;
	
    
	/**
	 * Title: . 
	 * <p>Description: 构造函数</p>
	 */
	protected Launch() {
		
	}
	
	/**
	 * Title: doTask
	 * <p>Description: 执行 服务 线程任务</p>
	 * @author:     9527
	 * @param isAsync是否异步（1多线程，0单线程）
	 * void    返回类型
	 */
	public abstract void doTask(int isAsync);
	
	/**
	 * Title: doTask
	 * <p>Description: 执行 服务 线程任务（同步，单线程执行）</p>
	 * @author:     9527
	 * void    返回类型
	 */
	public abstract void doTask();
	
	
	/**
	 * Title: lock
	 * <p>Description: 锁定线程</p>
	 * @author:     9527 
	 * void    返回类型
	 */
	public void lock(){
		this.lockQty++;
	}
	
	/**
	 * Title: release
	 * <p>Description: 释放线程</p>
	 * @author:     9527 
	 * void    返回类型
	 */
	public void release(){
		this.lockQty=lockQty>0?lockQty--:0;
	}

	
	/**
	 * Title: checkLock
	 * <p>Description: 线程锁验证</p>
	 * @author:     9527
	 * @return true可用；false已锁定。
	 * boolean    返回类型
	 */
	public abstract boolean checkLock();

}
