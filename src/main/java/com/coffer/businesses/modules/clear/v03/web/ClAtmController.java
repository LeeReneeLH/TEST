package com.coffer.businesses.modules.clear.v03.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.ClAtmAmount;
import com.coffer.businesses.modules.clear.v03.entity.ClAtmDetail;
import com.coffer.businesses.modules.clear.v03.entity.ClAtmMain;
import com.coffer.businesses.modules.clear.v03.service.ClAtmService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.google.common.collect.Lists;

/**
 * ATM钞箱拆箱Controller
 * @author wanglin
 * @version 2017-09-13
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/clAtm")
public class ClAtmController extends BaseController {

	@Autowired
	private ClAtmService clAtmService;

	
	@ModelAttribute
	public ClAtmMain get(@RequestParam(required=false) String id) {
		ClAtmMain entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = clAtmService.get(id);
		}
		if (entity == null){
			entity = new ClAtmMain();
		}
		return entity;
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          一览页面
	 * @param ClAtmMain
	 * @return
	 */
	@RequiresPermissions("clear:clAtm:view")
	@RequestMapping(value = {"list", ""})
	public String list(ClAtmMain clAtmMain, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 查询条件： 开始时间
		if (clAtmMain.getRegTimeStart() != null) {
			clAtmMain.setSearchRegDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clAtmMain.getRegTimeStart())));
		}
		// 查询条件： 结束时间
		if (clAtmMain.getRegTimeEnd() != null) {
			clAtmMain.setSearchRegDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(clAtmMain.getRegTimeEnd())));
		}
	
		Page<ClAtmMain> page = clAtmService.findPage(new Page<ClAtmMain>(request, response), clAtmMain); 
		
		model.addAttribute("page", page);
		return "modules/clear/v03/clAtm/clAtmList";

	}
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          登记修改页面
	 * @param ClAtmMain
	 * @return
	 */
	@RequiresPermissions("clear:clAtm:edit")
	@RequestMapping(value = "form")
	public String form(ClAtmMain clAtmMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		List<ClAtmAmount> amountList = Lists.newArrayList();
		List<ClAtmDetail> amountDetailList = Lists.newArrayList();
		
		//修改的场合，如果已删除
		if (StringUtils.isNotBlank(clAtmMain.getId()) && "1".equals(clAtmMain.getDelFlag())){
			String message = msg.getMessage("message.E0008", new String[] { clAtmMain.getOutNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:"+Global.getAdminPath()+"/clear/v03/clAtm/?repage";
		}

		//面值列表数据的取得
		clAtmMain.setDenominationList(ClearCommonUtils.getDenominationList());
		
		if(clAtmMain != null && clAtmMain.getId() !=null) {
			//每笔金额列表的设定
			amountList = clAtmService.findAmountList(clAtmMain);

			//每笔金额面值列表的设定
			amountDetailList = clAtmService.findAmountDetailList(clAtmMain);
		}
		clAtmMain.setAmountList(amountList);
		clAtmMain.setAmountDetailList(amountDetailList);
		
		model.addAttribute("clAtmMain", clAtmMain);
		return "modules/clear/v03/clAtm/clAtmForm";
	}
	
	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          查看页面
	 * @param ClAtmMain
	 * @return
	 */
	@RequiresPermissions("clear:clAtm:view")
	@RequestMapping(value = "view")
	public String view(ClAtmMain clAtmMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		List<ClAtmAmount> amountList = Lists.newArrayList();
		List<ClAtmDetail> amountDetailList = Lists.newArrayList();
		
		//修改的场合，如果已删除
		if (StringUtils.isNotBlank(clAtmMain.getId()) && "1".equals(clAtmMain.getDelFlag())){
			String message = msg.getMessage("message.E0008", new String[] { clAtmMain.getOutNo() }, locale);
			addMessage(redirectAttributes, message);
			return "redirect:"+Global.getAdminPath()+"/clear/v03/clAtm/?repage";
		}

		//面值列表数据的取得
		clAtmMain.setDenominationList(ClearCommonUtils.getDenominationList());
		
		if(clAtmMain != null && clAtmMain.getId() !=null) {
			//每笔金额列表的设定
			amountList = clAtmService.findAmountList(clAtmMain);

			//每笔金额面值列表的设定
			amountDetailList = clAtmService.findAmountDetailList(clAtmMain);
		}
		clAtmMain.setAmountList(amountList);
		clAtmMain.setAmountDetailList(amountDetailList);
		
		model.addAttribute("clAtmMain", clAtmMain);
		return "modules/clear/v03/clAtm/clAtmView";
	}

	
	/**
	 * 
	 * @author wanglin
	 * @version 2017年9月14日
	 * 
	 *          整单删除
	 * @param ClAtmMain
	 * @return
	 */
	@RequiresPermissions("clear:clAtm:edit")
	@RequestMapping(value = "delete")
	public String delete(ClAtmMain clAtmMain, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		clAtmService.deleteMain(clAtmMain);
		
		//删除成功
		message = msg.getMessage("message.I0002", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:"+Global.getAdminPath()+"/clear/v03/clAtm/?repage";
	}
	
	/**
	 * 返回到列表页面
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param bankPayInfo
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(ClAtmMain clAtmMain) {
		return "redirect:" + adminPath + "/clear/v03/clAtm/list?repage";
	}



	/**
	 * 保存处理
	 * 
	 * @author wanglin
	 * @version 2017年7月7日
	 * @param clAtmMain
	 * @param model
	 * @param redirectAttributes
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:clAtm:edit")
	@RequestMapping(value = "save")
	public String save(ClAtmMain clAtmMain, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 保存主表及明细表
			clAtmService.save(clAtmMain);
			
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			clAtmService.dataReturn(clAtmMain);		//异常处理的场合，页面每笔和面值列表的还原
			model.addAttribute("clAtmMain", clAtmMain);
			return "modules/clear/v03/clAtm/clAtmForm";
			
			//return form(clAtmMain, model, redirectAttributes);
		}
		// message.I7304=拆箱单号：{0}保存成功！
		message = msg.getMessage("message.I7304", new String[] { clAtmMain.getOutNo() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/clear/v03/clAtm/list";

	}
	

	
	
	
	
}