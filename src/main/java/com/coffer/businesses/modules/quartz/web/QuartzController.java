package com.coffer.businesses.modules.quartz.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.quartz.entity.Quartz;
import com.coffer.businesses.modules.quartz.service.QuartzService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
/**
 * 定时任务Controller
 * @author wangpengyu
 * @version 2019-12-04
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/quartz")
public class QuartzController extends BaseController {
	
	@Autowired
	private QuartzService quartzService;
	
	
	/**
	 * 获取所有计划中的job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月04日
	 * 
	 * @param Quartz
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(Quartz quartz,HttpServletRequest request, HttpServletResponse response, Model model) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		
		List<Quartz> taskList = new ArrayList<Quartz>();
		try {
			// 取得所有计划任务
				taskList = quartzService.findAllList();
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			message = msg.getMessage("message.I7505", null, locale);
			addMessage(model, message);
			return "modules/quartz/quartzList";
		}
		Page<Quartz> page = new Page<Quartz>(request, response);
		page.setCount(taskList.size());
		page.setList(taskList.subList(page.getFirstResult(), (taskList.size() - page.getFirstResult()) > 10
				? page.getFirstResult() + page.getMaxResults() : taskList.size()));
		model.addAttribute("page", page);
		return "modules/quartz/quartzList";
	}
	@RequestMapping(value = { "search" })
	public String search(Quartz quartz,HttpServletRequest request, HttpServletResponse response, Model model) {
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		
		List<Quartz> taskList = new ArrayList<Quartz>();
		try {
			// 取得所有计划任务
				taskList = quartzService.selectForSearch(quartz);
		} catch ( Exception e) {
			e.printStackTrace();
			message = msg.getMessage("message.I7505", null, locale);
			addMessage(model, message);
			return "modules/quartz/quartzList";
		}
		Page<Quartz> page = new Page<Quartz>(request, response);
		page.setCount(taskList.size());
		page.setList(taskList.subList(page.getFirstResult(), (taskList.size() - page.getFirstResult()) > 10
				? page.getFirstResult() + page.getMaxResults() : taskList.size()));
		model.addAttribute("page", page);
		
		return "modules/quartz/quartzList";
	}
	
	/**
	 * 跳转至任务添加页面
	 * 
	 * @author wangpengyu
	 * @version 2019年12月04日
	 * 
	 * @param quartz
	 * @param model
	 * @return 添加页面
	 */
	@RequestMapping(value = "form")
	public String form(Quartz quartz, Model model) {
		if (StringUtils.isNotBlank(quartz.getJobName())) {
			quartz = quartzService.getJob(quartz);
		}
		if(StringUtils.isNotEmpty(quartz.getCenterOfficeId())){
			quartz.setReportType("1");
		}
		if(StringUtils.isNotEmpty(quartz.getOfficeId())){
			quartz.setReportType("0");
		}
		model.addAttribute("quartz", quartz);
		return "modules/quartz/quartzForm";
	}
	/**
	 * 添加job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月9日
	 * 
	 * @param quartz
	 * @param redirectAttributes
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = "addJob")
	public String addJob(Quartz quartz, RedirectAttributes redirectAttributes, Model model) {
		// 本地化资源
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";		
		// 判断表达式是否正确
		if (!quartzService.isValidExpression(quartz.getCron())) {
			message = msg.getMessage("message.I7501", null, locale);
			addMessage(model, message);
			model.addAttribute("jobEntity", quartz);
			return "modules/quartz/quartzForm";
		}
		try {
			quartzService.addJob(quartz);
			message = msg.getMessage("message.I0005", null, locale);
		} catch (BusinessException e) {
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
			addMessage(model, message);
			model.addAttribute("quartz", quartz);
			return "modules/quartz/quartzForm";
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/sys/quartz/list?repage";
	}
	/**
	 * 更新Job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月9日
	 * 
	 * @param Quartz
	 * @param redirectAttributes
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = "updateJobCron")
	public String updateJobCron(Quartz quartz, RedirectAttributes redirectAttributes, Model model) {
		// 本地化资源
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 判断表达式是否正确
		if (!quartzService.isValidExpression(quartz.getCron())) {
			message = msg.getMessage("message.I7501", null, locale);
			addMessage(model, message);
			model.addAttribute("quartz", quartz);
			return "modules/sys/quartzForm";
		}
		try{
			quartzService.updateJob(quartz);
		}
		catch (BusinessException e) {
			message = msg.getMessage(e.getMessageCode(), e.getParameters(), locale);
			addMessage(model, message);
			model.addAttribute("quartz", quartz);
			return "modules/quartz/quartzForm";
		}
		
		message = msg.getMessage("message.I0005", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/sys/quartz/list?repage";
	}
	/**
	 * 暂停job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月05日
	 * 
	 * @param quartz
	 * @param redirectAttributes
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = "pauseJob")
	@Transactional(readOnly = false)
	public String pauseJob(Quartz quartz, RedirectAttributes redirectAttributes, Model model) {
		// 本地化资源
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		Quartz quarze1 = quartzService.findByJobName(quartz);
		if (!quartzService.isPresent(quarze1.getExecutionClass())) {
			message = msg.getMessage("message.I7507", null, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + adminPath + "/sys/quartz/list?repage";
		}
			quartzService.pauseJob(quarze1);
		// 操作成功
		message = msg.getMessage("message.I0005", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/sys/quartz/list?repage";
	}
	/**
	 * 恢复job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月05日
	 * 
	 * @param quartz
	 * @param redirectAttributes
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = "resumeJob")
	@Transactional(readOnly = false)
	public String resumeJob(Quartz quartz, RedirectAttributes redirectAttributes, Model model) {
		// 本地化资源
		Locale locale = LocaleContextHolder.getLocale();
		Quartz quarze1 = quartzService.findByJobName(quartz);
		String message = "";
		//判断要执行的类的路径是否有效
		if (!quartzService.isPresent(quarze1.getExecutionClass())) {
			message = msg.getMessage("message.I7507", null, locale);
			addMessage(redirectAttributes, message);
			return "redirect:" + adminPath + "/sys/quartz/list?repage";
		}
			quartzService.resumeJob(quarze1);
		// 操作成功
		message = msg.getMessage("message.I0005", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/sys/quartz/list?repage";
	}
	/**
	 * 删除job
	 * 
	 * @author wangpengyu
	 * @version 2019年12月5日
	 * 
	 * @param Quartz
	 * @param redirectAttributes
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = "deleteJob")
	@Transactional(readOnly = false)
	public String deleteJob(Quartz quartz, RedirectAttributes redirectAttributes, Model model) {
		// 本地化资源
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		Quartz quarze1 = quartzService.findByJobName(quartz);
		if ("1".equals(quarze1.getStatus())) {
			message = msg.getMessage("message.I7506", null, locale);
		}else {
			quartzService.deleteJob(quarze1);
			message = msg.getMessage("message.I0005", null, locale);
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/sys/quartz/list?repage";
	}
	/**
	 * 查询出所有商户ID
	 * @param quartz
	 * @return
	 */
	@RequestMapping(value = "officeIdList")
	public String getOfficeIdList(Quartz quartz,Office office,HttpServletRequest request, HttpServletResponse response,Model model) {
		User user = UserUtils.getUser();
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		List<Office> officeIdList = new ArrayList<Office>();
		try {
			// 取得所有计划任务
			officeIdList = quartzService.findOfficeByBusiness(user,quartz);
		} catch ( Exception e) {
			e.printStackTrace();
			message = msg.getMessage("message.I7505", null, locale);
			addMessage(model, message);
			return "modules/quartz/officeIdDetailList";
		}
		model.addAttribute("officeList", officeIdList);
		model.addAttribute("officeType", "商户");
		return "modules/quartz/officeIdDetailList";
	}
	/**
	 * 按条件查询商户ID
	 * @param 
	 * @return
	 */
	@RequestMapping(value = "officeSearch")
	public String officeSearch(Office office,HttpServletRequest request, HttpServletResponse response,Model model) {
		User user = UserUtils.getUser();
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		List<Office> officeIdList = new ArrayList<Office>();
		try {
			if("商户".equals(office.getType())){
				// 取得所有计划任务
				officeIdList = quartzService.officeSearch(user,office.getName(),office.getId());
				model.addAttribute("officeType", "商户");
			}else if("中心".equals(office.getType())){
				// 取得所有计划任务				
				officeIdList = quartzService.findAllCenterOffice(user,office,null);	
				model.addAttribute("officeType", "中心");
			}
		} catch ( Exception e) {
			e.printStackTrace();
			message = msg.getMessage("message.I7505", null, locale);
			addMessage(model, message);
			return "modules/quartz/officeIdDetailList";
		}
		model.addAttribute("officeList", officeIdList);
		return "modules/quartz/officeIdDetailList";	
	}
	/**
	 * 查询出所有商户ID 中心ID
	 * @param 
	 * @return
	 */
	@RequestMapping(value = "officeList")
	public String getOfficeList(Quartz quartz,Model model) {
		String officeType = "机构";
		if(quartz.getOfficeId() == null && quartz.getCenterOfficeId() != null){
			quartz.setOfficeId(quartz.getCenterOfficeId());
			officeType="中心";
		}else if(quartz.getOfficeId() != null && quartz.getCenterOfficeId() == null){
			officeType="商户";
		}
		List<Office> officeIdList = quartzService.findOfficeById(quartz);

		model.addAttribute("officeIdList",officeIdList);
		
		model.addAttribute("officeType",officeType);
		return "modules/quartz/officeList";
	}
	/**
	 * 返回到列表页面
	 * 
	 * @author wangpengyu
	 * @version 2019年12月6日
	 * 
	 * @return 列表页面
	 */
	@RequestMapping(value = "back")
	public String back() {
		return "redirect:" + adminPath + "/sys/quartz/list";
	}
	
	/**
	 * 查询出所有中心ID
	 * @param quartz
	 * @return
	 */
	@RequestMapping(value = "centerOfficeIdList")
	public String getOfficeCenterIdList(Quartz quartz,Office office,HttpServletRequest request, HttpServletResponse response,Model model) {
		User user = UserUtils.getUser();
		String message = "";
		Locale locale = LocaleContextHolder.getLocale();
		List<Office> officeIdList = new ArrayList<>();
		try {
			// 取得所有计划任务				
			officeIdList = quartzService.findAllCenterOffice(user,null,quartz);
		}catch ( Exception e) {
			e.printStackTrace();
			message = msg.getMessage("message.I7505", null, locale);
			addMessage(model, message);
			return "modules/quartz/officeIdDetailList";
		}
		model.addAttribute("officeList", officeIdList);
		model.addAttribute("officeType", "中心");
		return "modules/quartz/officeIdDetailList";
	}
		
}
