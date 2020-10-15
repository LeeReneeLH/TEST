package com.coffer.businesses.modules.doorOrder.v01.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.doorOrder.v01.entity.BankAccountInfo;
import com.coffer.businesses.modules.doorOrder.v01.service.BankAccountInfoService;

/**
 * 银行卡管理Controller
 * @author yinkai
 * @version 2019-08-06
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/bankAccountInfo")
public class BankAccountInfoController extends BaseController {

	@Autowired
	private BankAccountInfoService bankAccountInfoService;
	
	@ModelAttribute
	public BankAccountInfo get(@RequestParam(required=false) String id) {
		BankAccountInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = bankAccountInfoService.get(id);
		}
		if (entity == null){
			entity = new BankAccountInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("doorOrder:v01:bankAccountInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(BankAccountInfo bankAccountInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		bankAccountInfo.getSqlMap().put("dsf",
				"AND o2.parent_ids LIKE '%" + UserUtils.getUser().getOffice().getParentId() + "%' AND (o2.type='9' OR O2.type='7')");
		Page<BankAccountInfo> page = bankAccountInfoService.findPage(new Page<BankAccountInfo>(request, response), bankAccountInfo); 
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/bankAccountInfo/bankAccountInfoList";
	}

	@RequiresPermissions("doorOrder:v01:bankAccountInfo:view")
	@RequestMapping(value = "form")
	public String form(BankAccountInfo bankAccountInfo, Model model) {
		model.addAttribute("bankAccountInfo", bankAccountInfo);
		return "modules/doorOrder/v01/bankAccountInfo/bankAccountInfoForm";
	}

	@RequiresPermissions("doorOrder:v01:bankAccountInfo:edit")
	@RequestMapping(value = "save")
	public String save(BankAccountInfo bankAccountInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bankAccountInfo)){
			return form(bankAccountInfo, model);
		}
		bankAccountInfoService.save(bankAccountInfo);
		addMessage(redirectAttributes, "保存银行卡信息成功");
		return "redirect:"+Global.getAdminPath()+"/doorOrder/v01/bankAccountInfo/?repage";
	}
	
	@RequiresPermissions("doorOrder:v01:bankAccountInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(BankAccountInfo bankAccountInfo, RedirectAttributes redirectAttributes) {
		bankAccountInfoService.delete(bankAccountInfo);
		addMessage(redirectAttributes, "删除银行卡信息成功");
		return "redirect:"+Global.getAdminPath()+"/doorOrder/v01/bankAccountInfo/?repage";
	}

	/**
	 * 改变银行卡绑定状态(解绑)
	 * @author zxk
	 * 2019-8-15
	 * @param bankAccountInfo
	 * @return
	 */
	@RequiresPermissions("doorOrder:v01:bankAccountInfo:edit")
	@RequestMapping(value = "changeStatus")
	public String changeStatus(BankAccountInfo bankAccountInfo, RedirectAttributes redirectAttributes) {
		BankAccountInfo bac = new BankAccountInfo();
		bac.setId(bankAccountInfo.getId());
		if(bankAccountInfo.getStatus().equals(Constant.BankStatus.NOBIND)){
			bac.setStatus(Constant.BankStatus.BIND);
			addMessage(redirectAttributes, "银行卡绑定成功");
		}else{
			bac.setStatus(Constant.BankStatus.NOBIND);
			addMessage(redirectAttributes, "银行卡解绑成功");
		}
		bankAccountInfoService.changeStatus(bac);
		return "redirect:"+Global.getAdminPath()+"/doorOrder/v01/bankAccountInfo/?repage";
	}
	
	
	/**
	 * 银行卡绑定 前检查商户是否已绑定
	 * @author zxk
	 * 2019-8-15
	 * @param bankAccountInfo
	 * @return
	 */
	@RequiresPermissions("doorOrder:v01:bankAccountInfo:edit")
	@RequestMapping(value = "changeStatus4check")
	public String changeStatus4check(BankAccountInfo bankAccountInfo, RedirectAttributes redirectAttributes) {
		BankAccountInfo bac = new BankAccountInfo();
		bac.setOfficeId(bankAccountInfo.getOfficeId());
		bac.setStatus(Constant.BankStatus.BIND);
		List<BankAccountInfo> list = bankAccountInfoService.findByMerchantAndStatus(bac);
		if(Collections3.isEmpty(list)){
			bankAccountInfo.setStatus(Constant.BankStatus.BIND);
			bankAccountInfoService.changeStatus(bankAccountInfo);
			addMessage(redirectAttributes, "银行卡绑定成功");
		}else{
			addMessage(redirectAttributes, "[绑定失败]:该机构已绑定银行卡,请先将已绑定的银行卡解绑!");
			
		}
		return "redirect:"+Global.getAdminPath()+"/doorOrder/v01/bankAccountInfo/?repage";
	}
	
	
}