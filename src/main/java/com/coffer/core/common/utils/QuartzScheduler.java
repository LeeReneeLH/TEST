package com.coffer.core.common.utils;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzScheduler {
	private static Scheduler scher;
	/**
	 * Title: init
	 * <p>Description: 任务初始化 cheduler</p>
	 * @author:     wangpengyu 
	 * void    
	 */
	public static void init(){
		try {
			scher = StdSchedulerFactory.getDefaultScheduler();
			if(!scher.isStarted()){
				scher.start();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Title: stop
	 * <p>Description: 停止任务调度</p>
	 * @author:     wangpengyu
	 * @throws SchedulerException 
	 * void    
	 */
	public static void stop() throws SchedulerException{
		if(scher!=null){

			for (String groupId : scher.getTriggerGroupNames()) {
				for (String jobName : scher.getJobNames(groupId)) {
					scher.deleteJob(jobName, groupId);
				}
			}
			scher.shutdown();
		}
	}
	/**
	 * Title: getScher
	 * <p>Description: 获取Scheduler</p>
	 * @author:     wangpengyu
	 * @return 
	 * Scheduler    
	 */
	public static Scheduler getScher(){
		return scher;
	}
	/**
	 * Title: addJob
	 * <p>Description: 添加job</p>
	 * @author:     wangpengyu
	 * @param job 
	 * @param trigger 
	 * void    
	 */
	public static void addJob(JobDetail job, CronTrigger trigger){
		try {
			if(scher.isShutdown()){
				scher.start();
			}
			if(scher.isInStandbyMode()){
				scher.pauseAll();
			}
			scher.scheduleJob(job, trigger);
			scher.resumeAll();
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Title: deleteJobs
	 * <p>Description: 删除jobs</p>
	 * @author:     9527
	 * @param groupName 组名称
	 * @throws SchedulerException 
	 * void    
	 */
	public static void deleteJobs(String groupName) throws SchedulerException{
		if(scher==null){
			return;
		}
		if(scher.isInStandbyMode()){
			scher.pauseAll();
		}
		String[] jobNames = scher.getJobNames(groupName);
		for (String jobName : jobNames) {
			scher.deleteJob(jobName, groupName);
		}
		scher.resumeAll();
	}
	
	/**
	 * Title: deleteJob
	 * <p>Description: 删除job</p>
	 * @author:     wangpengyu
	 * @param jobName 工作名称
	 * @param groupName 组名称
	 * @throws SchedulerException 
	 * void    
	 */
	public static void deleteJob(String jobName, String groupName) throws SchedulerException{
		if(scher==null){
			return;
		}
		if(scher.isInStandbyMode()){
			scher.pauseAll();
		}
		try{
			if(scher.getJobDetail(jobName, groupName)!=null){
				scher.deleteJob(jobName, groupName);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		scher.resumeAll();
	}
	
}
