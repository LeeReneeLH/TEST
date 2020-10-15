package com.coffer.core.common.utils;

import java.sql.Timestamp;
import java.util.Map;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

public class QuartzUtil {
	/**
	 * Title: addJob
	 * <p>Description: 添加job/p>
	 * @author:     wangpengyu
	 * @param name 
	 * @param group 
	 * @param jobClass 
	 * @param map 
	 * @param start 
	 * @return 
	 * boolean    
	 */
	@SuppressWarnings("rawtypes")
	public static final boolean addJob(String name, String group, Class jobClass, Map map, Timestamp start){
		try {
			QuartzScheduler.deleteJob(name, group);
			JobDetail job = new JobDetail(name, group, jobClass);
			if(map != null){
				job.getJobDataMap().putAll(map);
			}
			
			SimpleTrigger trigger = new SimpleTrigger(name, group);
			
			trigger.setStartTime(start);
			QuartzScheduler.getScher().scheduleJob(job, trigger);
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Title: deleteJob
	 * <p>Description: 删除job</p>
	 * @author:     wangpengyu
	 * @param name 
	 * @param group 
	 * @return 
	 * boolean    
	 */
	public static final boolean deleteJob(String name, String group){
		try {
			QuartzScheduler.deleteJob(name, group);
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Title: deleteJobs
	 * <p>Description: 删除jobs</p>
	 * @author:     wangpengyu
	 * @param group 
	 * @return 
	 * boolean    
	 */
	public static final boolean deleteJobs(String group){
		try {
			QuartzScheduler.deleteJobs(group);
			return true;
		} catch (SchedulerException e) {
			e.printStackTrace();
			return false;
		}
	}
}
