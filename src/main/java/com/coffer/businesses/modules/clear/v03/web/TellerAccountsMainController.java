package com.coffer.businesses.modules.clear.v03.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.clear.v03.service.TellerAccountsMainService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;

/**
 * 柜员账务Controller
 * @author xl
 * @version 2017-10-23
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/tellerAccountsMain")
public class TellerAccountsMainController extends BaseController {

	@Autowired
	private TellerAccountsMainService tellerAccountsMainService;
	
	/**
	 * 根据主键，取得柜员账务信息
	 * 
	 * @author xl
	 * @version 2017年10月24日
	 * @param id
	 * @return 柜员账务信息
	 */
	@ModelAttribute
	public TellerAccountsMain get(@RequestParam(required=false) String id) {
		TellerAccountsMain entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tellerAccountsMainService.get(id);
		}
		if (entity == null){
			entity = new TellerAccountsMain();
		}
		return entity;
	}
	
	/**
	 * 柜员账务列表
	 * 
	 * @author xl
	 * @version 2017年10月24日
	 * @param tellerAccountsMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 柜员账列表
	 */
	@RequiresPermissions("clear.v03:tellerAccountsMain:view")
	@RequestMapping(value = {"list", ""})
	public String list(TellerAccountsMain tellerAccountsMain, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 柜员账列表,分页
		// 设置发生机构
		Page<TellerAccountsMain> page = tellerAccountsMainService
				.findPageForMain(new Page<TellerAccountsMain>(request, response), tellerAccountsMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/tellerAccounts/tellerAccountsMainList";
	}

	/**
	 * 柜员明细列表
	 * 
	 * @author xl
	 * @version 2017年10月24日
	 * @param tellerAccountsMain
	 * @param request
	 * @param response
	 * @param model
	 * @return 柜员明细列表
	 */
	@RequiresPermissions("clear.v03:tellerAccountsMain:view")
	@RequestMapping(value = { "tellerAccountsDetail" })
	public String tellerAccountsDetail(TellerAccountsMain tellerAccountsMain, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 查询条件： 开始时间
		if (tellerAccountsMain.getCreateTimeStart() != null) {
			tellerAccountsMain.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(tellerAccountsMain.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (tellerAccountsMain.getCreateTimeEnd() != null) {
			tellerAccountsMain.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(tellerAccountsMain.getCreateTimeEnd())));
		}
		// 柜员明细列表,分页
		Page<TellerAccountsMain> page = tellerAccountsMainService
				.findPage(new Page<TellerAccountsMain>(request, response), tellerAccountsMain);
		model.addAttribute("page", page);
		return "modules/clear/v03/tellerAccounts/tellerAccountsMainDetail";
	}
}