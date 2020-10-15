package com.coffer.core.common.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.coffer.core.common.launch.TaskSchedulingLaunch;
import com.coffer.core.common.quartz.DayReportJob;
import com.coffer.core.common.utils.QuartzScheduler;


public class ServerStartServlet extends HttpServlet{
	
	
	
	public void init() throws ServletException {
		// 定时任务启动
		QuartzScheduler.init();
		
		
		//任务调度器定时任务初始化
		TaskSchedulingLaunch taskSchedulingLaunch = new TaskSchedulingLaunch();
		taskSchedulingLaunch.doTask(1);
	
			
		

		
	}
	
	
	public void destroy(){
		try {
			QuartzScheduler.stop();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	
}
