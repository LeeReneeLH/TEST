package com.coffer.core.common.task;

import java.util.ArrayList;
import java.util.List;




import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffer.businesses.modules.quartz.entity.Quartz;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.core.common.utils.QuartzScheduler;



public class TaskSchedulingTask implements Runnable {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void run() {
		logger.info("**********任务调度初始化加载开始**********");
		List<Quartz> list = new ArrayList<Quartz>();
		list = StoreCommonUtils.selectQuartzAll();
		for (Quartz quartz : list) {
			try {
				String timer  = quartz.getCron();
				Class cls = Class.forName(quartz.getExecutionClass());
				JobDetail job = new JobDetail(quartz.getJobName(), quartz.getJobGroup(),cls);
				CronTrigger trigger = new CronTrigger(quartz.getTriggerName(),quartz.getTriggerGroup(), timer);
				QuartzScheduler.addJob(job, trigger);
				logger.info("**********任务调度初始化加载成功**********");
			} catch (Exception e) {
				logger.info("**********任务调度初始化加载失败**********");
				e.printStackTrace();
			}
		}
		
	}

}
