package com.coffer.businesses.modules.quartz.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.coffer.businesses.modules.quartz.dao.QuartzDao;
import com.coffer.businesses.modules.quartz.entity.Quartz;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.service.CrudService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.QuartzScheduler;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.dao.OfficeDao;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
/**
 * 
 * Title: QuartzService
 * <p>
 * Description: 任务调度逻辑层
 * </p>
 * 
 * @author wangpengyu
 * @date 2019年12月03日 下午13:16:54
 */
@Service
@Transactional(readOnly = true)
public class QuartzService extends CrudService<QuartzDao, Quartz> {
	@Autowired
	private QuartzDao quartzDao;
	@Autowired
	private OfficeDao officeDao;
	
	
	
	/**
	 * 
	 * Title: findList
	 * <p>
	 * Description: 查询全部为删除且为运行状态下的定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return List<Quartz> 返回类型
	 */
	public List<Quartz> findList(){
		return quartzDao.selectAll();
	}
	/**
	 * 
	 * Title: findAllList
	 * <p>
	 * Description: 查询全部未删除的定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return List<Quartz> 返回类型
	 */
	public List<Quartz> findAllList(){
		return quartzDao.findAllList();
	}
	/**
	 * 
	 * Title: selectForSearch
	 * <p>
	 * Description: 列表页面查询条件
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return List<Quartz> 返回类型
	 */
	public List<Quartz> selectForSearch(Quartz quartz){
		return quartzDao.selectForSearch(quartz);
	}
	/**
	 * 
	 * Title: getJob
	 * <p>
	 * Description: 获取单个定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return Quartz 返回类型
	 */
	public Quartz getJob(Quartz quartz){
		return quartzDao.getJob(quartz);
	}
	/**
	 * 
	 * Title: pauseJob
	 * <p>
	 * Description: 暂停定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return 
	 */
	public void pauseJob(Quartz quartz){
				 try {
					 quartzDao.pauseJob(quartz);
						if ("1".equals(quartz.getStatus())) {
							delQuartz(quartz);
						}
				} catch (Exception e) {
					if ("1".equals(quartz.getStatus())) {
						addQuartz(quartz);
					}
					e.printStackTrace();
				}
	}
	/**
	 * 
	 * Title: resumeJob
	 * <p>
	 * Description: 恢复定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return 
	 */
	public void resumeJob(Quartz quartz){
				 try {
					 quartzDao.resumeJob(quartz);
						addQuartz(quartz);
				} catch (Exception e) {
					delQuartz(quartz);
					e.printStackTrace();
				}
	}
	/**
	 * 
	 * Title: delQuartz
	 * <p>
	 * Description: 删除定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return 
	 */
	public void delQuartz(Quartz quartz){
		 String jobName = quartz.getJobName();
  	   String jobGroup = quartz.getJobGroup();
		 try {
			 if (QuartzScheduler.getScher().getJobDetail(jobName, jobGroup)!=null) {
 				QuartzScheduler.deleteJob(jobName, jobGroup);
 			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * Title: addQuartz
	 * <p>
	 * Description: 添加定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return 
	 */
	public void addQuartz(Quartz quartz){
		if (isPresent(quartz.getExecutionClass())) {
    		try {
    			Class cls = Class.forName(quartz.getExecutionClass());
    			JobDetail job = new JobDetail(quartz.getJobName(), quartz.getJobGroup(),
    					cls);
    			CronTrigger trigger = new CronTrigger(quartz.getJobName(),
    					quartz.getJobGroup(), quartz.getCron());
    			QuartzScheduler.addJob(job, trigger);
    			
    		} catch (Exception e) {
    			e.printStackTrace();
				return;
    		}
		}
	}
	/**
	 * 
	 * Title: findByJobName
	 * <p>
	 * Description: 根据任务名查询定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return 
	 */
	public Quartz findByJobName(Quartz quartz){
		return quartzDao.getJob(quartz);
	}
	/**
	 * 
	 * Title: deleteJob
	 * <p>
	 * Description: 删除定时任务
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return 
	 */
	public void deleteJob(Quartz quartz){
		try {
			 quartzDao.removeJob(quartz.getJobName());
			delQuartz(quartz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 * Title: isPresent
	 * <p>
	 * Description: 判断执行类是否存在
	 * </p>
	 * 
	 * @author: wangpengyu
	 * @return boolean
	 */
	public boolean isPresent(String name) {
        try {
        	Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
	/**
	 * 根据ID获取商户
	 * @version 2019年12月05日
	 * @author: wangpengyu 
	 */
	public List<Office> findOfficeById(Quartz quartz){
				List<Office> officeList = new ArrayList<Office>();
				String[] officeId = quartz.getOfficeId().split(",");
				for (String id : officeId) {
					Office off = officeDao.findOfficeById(id);
					if (off!=null) {
						officeList.add(off);
					}
					
				}
				return officeList;
	}
	/**
	 * 添加job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月9日
	 * 
	 * @param Quartz
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = false)
	public void addJob(Quartz quartz) {
		//判断taskName是否存在
		if (quartzDao.findByTaskName(quartz.getTaskName())!=null) {
			throw new BusinessException("message.I7502");
		}
		//判断jobName是否存在
		if (quartzDao.getJob(quartz)!=null) {
			throw new BusinessException("message.I7503");
		}
			//判断执行类是否存在
			if (isPresent(quartz.getExecutionClass())) {
				List<Quartz> listQuartz = quartzDao.selectByClass(quartz);
				for(Quartz q : listQuartz){
					if(StringUtils.isNotEmpty(q.getCenterOfficeId())){
						String[] officeId = q.getCenterOfficeId().split(",");
						for(String id : officeId){
							if(StringUtils.isNotEmpty(quartz.getCenterOfficeId()) && quartz.getCenterOfficeId().contains(id)){
								throw new BusinessException("message.I7508");
							}
						}
					}
				}
				
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = sdf.format(new Date());
					Date creatTime = sdf.parse(time);
					quartz.setId(UUID.randomUUID().toString());
					quartz.setDelFlag("0");
					quartz.setTriggerName(quartz.getJobName());
					quartz.setTriggerGroup(quartz.getJobGroup());
					quartz.setCreateTime(creatTime);
					quartzDao.insert(quartz);
					//根据运行状态判断是否添加到任务调度器中
					if (quartz.getStatus().equals("1")) {
						addQuartz(quartz);
					}
				} catch (Exception e) {
					if (quartz.getStatus().equals("1")) {
						delQuartz(quartz);
					}
					e.printStackTrace();
				}
				
			}else {
				throw new BusinessException("message.I7504");
			}
	}
	/**
	 * 更新job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月9日
	 * 
	 * @param Quartz
	 */
	@Transactional(readOnly = false)
	public void updateJob(Quartz quartz) {
		Quartz qua = quartzDao.getJob(quartz);
		String stuOld = qua.getStatus();
		String stuNew = quartz.getStatus();
		try {
			List<Quartz> listQuartz = quartzDao.selectByClass(qua);
			for(Quartz q : listQuartz){
				if(!qua.getId().equals(q.getId()) && StringUtils.isNotEmpty(q.getCenterOfficeId())){
					String[] officeId = q.getCenterOfficeId().split(",");
					for(String id : officeId){
						if(StringUtils.isNotEmpty(quartz.getCenterOfficeId()) && quartz.getCenterOfficeId().contains(id)){
							throw new BusinessException("message.I7508");
						}
					}
				}
			}
			quartzDao.updateByPrimaryKeySelective(quartz);
			//根据前任务状态和新任务状态来判断时候删除或添加到任务调度器中
			if ("1".equals(stuOld) && "1".equals(stuNew)) {
				delQuartz(qua);
				addQuartz(quartz);
			}
			if ("1".equals(stuOld) && "0".equals(stuNew)) {
				delQuartz(qua);
			}
			if ("0".equals(stuOld) && "1".equals(stuNew)) {
				addQuartz(quartz);
			}
		}catch (BusinessException e) {
			throw new BusinessException("message.I7508");
		} 
		catch (Exception e) {
			if ("1".equals(stuOld) && "1".equals(stuNew)) {
				deleteJob(quartz);
				addJob(qua);
			}
			if ("1".equals(stuOld) && "0".equals(stuNew)) {
				addJob(qua);
			}
			if ("0".equals(stuOld) && "1".equals(stuNew)) {
				deleteJob(quartz);
			}
			e.printStackTrace();
		}
	} 
	/**
	 * 判断表达式是否正确
	 * 
	 * @author wangpengyu
	 * @version 2019年12月9日
	 * 
	 * @param String cron
	 */
	public boolean isValidExpression(String cron) {
		CronTrigger trigger = new CronTrigger();
		try {
			trigger.setCronExpression(cron);
			Date date = trigger.computeFirstFireTime(null);
			
			Boolean flag = date.after(new Date()) || DateUtils.isSameDay(date, new Date());
			return flag && date != null;
		} catch (Exception e) {
			logger.error("[isValidExpression]:failed. throw ex:", e);
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获取所有商户ID
	 * @version 2019年12月05日
	 * @author: wangpengyu
	 * @param Quartz quartz
	 */
	public List<Office> findOfficeByBusiness(User user,Quartz quartz){
		//商户List
		List<Office> officeList = new ArrayList<Office>();
		//获取当前账户对应机构
		Office of = user.getOffice();
		//商户Map
		Map<String, Office> officeMap = new HashMap<String, Office>();	
		//查询出当前登录账号下所有商户
		List<Office> olist = officeDao.findOfficeByBusiness(of);
		//查询目前所有定时任务
		List<Quartz> qlist = quartzDao.findAllList();
		//已在定时任务中的商户Map
		Map<String, String> qMap = new HashMap<String, String>();
		//循环所有定时任务
		//判断定时任务实体中商户ID是否为空
		if (StringUtils.isNotBlank(quartz.getOfficeId())) {
			
			//商户ID数组
			String[] idList = quartz.getOfficeId().split(",");
			
			//循环数组
			for (String s : idList) {
				//判断商户Map中是否存在此ID
				if (!officeMap.containsKey(s)) {
					//如果不存在将此ID的商户对象放入商户Map中
					officeMap.put(s, officeDao.findOfficeById(s));
				}
			}
			//循环所有定时任务
			for (Quartz quartz2 : qlist) {
				//判断定时任务中officeId是否为空
				if (StringUtils.isNotBlank(quartz2.getOfficeId())) {
					//获取商户数组
					String [] officeId = quartz2.getOfficeId().split(",");
					for (String s : officeId) {
						//将商户放入已在定时任务中的商户Map
						qMap.put(s, s);
					}
				}
			}
			//获得当前定时任务officeId
			if (StringUtils.isNotBlank(quartz.getId())) {
				Quartz qua = quartzDao.selectByPrimaryKey(quartz.getId());
				String officeId1 = null;
				if (qua!=null) {
					officeId1 = qua.getOfficeId();
				}
				if (StringUtils.isNotBlank(officeId1)) {
					//定时任务商户ID数组
					String[] idList1 = officeId1.split(",");
					for (String s1 : idList1) {
						if (!officeMap.containsKey(s1)) {
							qMap.remove(s1);
						}
					}
				}
			}
			
			//循环当前登录账号所有商户
			for (Office office : olist) {
				//判断商户是否已经存在
				if (!qMap.containsKey(office.getId())) {
					//判断商户Map中是否存在
					if (!officeMap.containsKey(office.getId())) {
						officeMap.put(office.getId(), office);
					}
				}
			}
			//循环商户Map
			for (String ss : officeMap.keySet()) {
				//将商户添加到商户List中
				officeList.add(officeMap.get(ss));
			}
			
		}
		else {
			if (StringUtils.isNotBlank(quartz.getId())) {
				Quartz qua = quartzDao.selectByPrimaryKey(quartz.getId());
				String officeId1 = null;
				if (qua!=null) {
					officeId1 = qua.getOfficeId();
				}
				if (StringUtils.isNotBlank(officeId1)) {
					Map<String, String> map = new HashMap<String, String>();
					//商户ID数组
					String[] idList = officeId1.split(",");
					for (String s : idList) {
						//判断商户Map中是否存在此ID
						if (!map.containsKey(s)) {
							//如果不存在将此ID的商户对象放入商户Map中
							map.put(s, s);
						}
					}
					//循环所有定时任务
					for (Quartz quartz2 : qlist) {
						//判断定时任务中officeId是否为空
						if (StringUtils.isNotBlank(quartz2.getOfficeId())) {
							//获取商户数组
							String [] officeId = quartz2.getOfficeId().split(",");
							for (String s : officeId) {
								//将商户放入已在定时任务中的商户Map
								if (!map.containsKey(s)) {
									qMap.put(s, s);
								}
							}
						}
					}
					//循环当前登录账号所有商户
					for (Office office : olist) {
						//判断商户是否已经存在
						if (!qMap.containsKey(office.getId())) {
							//判断商户Map中是否存在
							if (!officeMap.containsKey(office.getId())) {
								officeMap.put(office.getId(), office);
							}
						}
					}
					//循环商户Map
					for (String ss : officeMap.keySet()) {
						//将商户添加到商户List中
						officeList.add(officeMap.get(ss));
					}
				}else {
					for (Quartz quartz2 : qlist) {
						//判断定时任务中officeId是否为空
						if (StringUtils.isNotBlank(quartz2.getOfficeId())) {
							//获取商户数组
							String [] officeId = quartz2.getOfficeId().split(",");
							for (String s : officeId) {
								//将商户放入已在定时任务中的商户Map
								qMap.put(s, s);
							}
						}
					}
					//循环当前登录账户下所有商户
					for (Office office : olist) {
						//判断商户是否已经存在
						if (!qMap.containsKey(office.getId())) {
							//将商户添加到商户List中
							officeList.add(office);
						}
					}
				}
				
			}
			else {
				for (Quartz quartz2 : qlist) {
					//判断定时任务中officeId是否为空
					if (StringUtils.isNotBlank(quartz2.getOfficeId())) {
						//获取商户数组
						String [] officeId = quartz2.getOfficeId().split(",");
						for (String s : officeId) {
							//将商户放入已在定时任务中的商户Map
							qMap.put(s, s);
						}
					}
				}
				//循环当前登录账户下所有商户
				for (Office office : olist) {
					//判断商户是否已经存在
					if (!qMap.containsKey(office.getId())) {
						//将商户添加到商户List中
						officeList.add(office);
					}
				}
			}
		}
			return officeList;
		
	}
	/**
	 * 获取所有商户ID
	 * @version 2019年12月05日
	 * @author: wangpengyu
	 */
	public List<Office> officeSearch(User user,String officeName,String officeId){
		//商户List
		List<Office> officeList = new ArrayList<Office>();
		//获取当前账户对应机构
		Office of = user.getOffice();
		of.setName(officeName);
		//商户Map
		Map<String, Office> officeMap = new HashMap<String, Office>();
		//判断前端是否传入商户ID
		if (StringUtils.isNotBlank(officeId)) {
			//按条件查询出所有商户
			List<Office> olist = officeDao.officeSearch(of);
			//查询出所有定时任务
			List<Quartz> qlist = quartzDao.findAllList();
			//已在定时任务中的商户Map
			Map<String, String> qMap = new HashMap<String, String>();
			//循环所有定时任务
			for (Quartz quartz2 : qlist) {
				//判断定时任务中商户ID是否为空
				if (StringUtils.isNotBlank(quartz2.getOfficeId())) {
					//已存在商户ID数组
					String [] officeId1 = quartz2.getOfficeId().split(",");
					//循环数组
					for (String s : officeId1) {
						//放入已在定时任务的商户Map
						qMap.put(s, s);
					}
				}
			}
			//循环按条件查询出的所有商户
			for (Office office1 : olist) {
				//判断商户ID是否存在
				if (!qMap.containsKey(office1.getId())) {
					//判断商户Map中是否存在
					if (!officeMap.containsKey(office1.getId())) {
					   //放入商户Map中
						officeMap.put(office1.getId(), office1);
					}
				}
			}
			//获得前台传入商户ID数组
			String[] idList = officeId.split(",");
			//循环数组
			for (String s : idList) {
				//判断商户Map中是否存在
				if (officeMap.containsKey(s)) {
					officeMap.put(s, officeDao.findOfficeById(s));
				}else {
					officeMap.put(s, officeDao.findOfficeByOtherId(s));
				}
			}
			//循环商户Map
			for (String ss : officeMap.keySet()) {
				//放入商户List中
				officeList.add(officeMap.get(ss));
			}
		}else {
			//按条件查询出所有商户
			List<Office> olist = officeDao.officeSearch(of);
			//查询出所有定时任务
			List<Quartz> qlist = quartzDao.findAllList();
			//已在定时任务中的商户Map
			Map<String, String> qMap = new HashMap<String, String>();
			//循环所有定时任务
			for (Quartz quartz2 : qlist) {
				//判断定时任务中商户ID是否为空
				if (StringUtils.isNotBlank(quartz2.getOfficeId())) {
					//已存在商户ID数组
					String [] officeId1 = quartz2.getOfficeId().split(",");
					//循环数组
					for (String s : officeId1) {
						//放入已在定时任务的商户Map
						qMap.put(s, s);
					}
				}
			}
			//循环查询出的所有商户
			for (Office office1 : olist) {
				//判断商户是否已经存在
				if (!qMap.containsKey(office1.getId())) {
					//放入商户List中
					officeList.add(office1);
				}
			}
			
		}
			return officeList;
		
	}
	
	/**
	 * 获取所有中心ID
	 * @version 2020年07月27日
	 * @author: HuZhiYong
	 * @param Quartz quartz
	 */
	public List<Office> findAllCenterOffice(User user,Office o,Quartz quartz){
		
		//商户ID数组
		String[] idList = null;
		if(quartz != null){
			idList = quartz.getCenterOfficeId().split(",");
		}else{
			idList = o.getId().split(",");
		}
		//中心List
		List<Office> officeCenterList = new ArrayList<>();
		//获取当前账户对应机构
		Office of = user.getOffice();
		List<Office> officeList = officeDao.getClearCenterOfficeList(o);
		//遍历所有中心 寻找符合当前机构
		for(Office office : officeList){
			//证明用户所在机构小于等于中心
			if(of.getParentIds().contains(office.getParentIds())){
				
				for(String id : idList){
					if(id.equals(office.getId())){
						office.setParentIds(null);//设为选中
					}
				}
				officeCenterList.add(office);
				return officeCenterList;
			}
			//证明用户所在机构大于中心 即可以查看多个中心
			if(office.getParentIds().contains(of.getParentIds())){
				for(String id : idList){
					if(id.equals(office.getId())){
						office.setParentIds(null);//设为选中
					}
				}
				officeCenterList.add(office);
			}
		}
		
		
		return officeCenterList;
	}
}
