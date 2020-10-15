package com.coffer.businesses.modules.clear.v03.web;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.ClearAccountAdjustInfo;
import com.coffer.businesses.modules.clear.v03.entity.TellerAccountsMain;
import com.coffer.businesses.modules.clear.v03.service.ClearAccountAdjustInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 柜员账务：柜员调账Controller
 * 
 * @author dja
 * @version 2018年4月20日
 */
@Controller
@RequestMapping(value = "${adminPath}/clear/v03/clearAccountAdjustInfo")
public class ClearAccountAdjustInfoController extends BaseController {

	/** 调账用Service */
	@Autowired
	private ClearAccountAdjustInfoService clearAccountAdjustInfoService;
		
	/** 根据选择的金额类型，获取交，收款人 */
	@Autowired
	private StoEscortInfoService stoEscortInfoService;
		
	/**
	 * 根据调账单号，调账信息信息
	 * 
	 * @author dja
	 * @version 2018年4月26日
	 * @param 调账单号(accountId)
	 * @return  调账信息
	 */
	@ModelAttribute
	public ClearAccountAdjustInfo get(@RequestParam(required = false) String accountId) {
		ClearAccountAdjustInfo entity = null;
		if (StringUtils.isNotBlank(accountId)) {
			entity = clearAccountAdjustInfoService.get(accountId);
		}
		if (entity == null) {
			entity = new ClearAccountAdjustInfo();
		}
		return entity;
	}
	/**
	 * 柜员调账信息
	 * @author dja
	 * @version 2018年4月20日
	 * @param clearAccountAdjustInfo        
	 * @return 柜员调账信息列表页面
	 */
	@RequiresPermissions("clear:clearAccountAdjustInfo:view")
	@RequestMapping(value = { "list", "" })
	public String list(ClearAccountAdjustInfo clearAccountAdjustInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		//初始化开始时间和结束时间为当前时间 
		if (clearAccountAdjustInfo.getCreateTimeStart() == null) {
			clearAccountAdjustInfo.setCreateTimeStart(new Date());
		}
		if (clearAccountAdjustInfo.getCreateTimeEnd() == null) {
			clearAccountAdjustInfo.setCreateTimeEnd(new Date());
		}
		// 查询条件： 开始时间
		if (clearAccountAdjustInfo.getCreateTimeStart() != null) {
			clearAccountAdjustInfo.setSearchTimeStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clearAccountAdjustInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clearAccountAdjustInfo.getCreateTimeEnd() != null) {
			clearAccountAdjustInfo
					.setSearchTimeEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearAccountAdjustInfo.getCreateTimeEnd())));
		}	
		// 查询柜员调账信息
		Page<ClearAccountAdjustInfo> page = clearAccountAdjustInfoService.findPage(new Page<ClearAccountAdjustInfo>(request, response), clearAccountAdjustInfo);
		model.addAttribute("clearAccountAdjustInfo", clearAccountAdjustInfo);
		model.addAttribute("page", page);
		return "modules/clear/v03/clearAccountAdjustInfo/clearAccountAdjustInfoList";
	}
	/**
	 * 跳转到登记页面
	 * 
	 * @author dja
	 * @version 2017年4月23日
	 * @param ClearAccountAdjustInfo
	 * @param model
	 * @return 登记页面
	 */
	@RequiresPermissions("clear:clearAccountAdjustInfo:add")
	@RequestMapping(value = "form")
	public String form(ClearAccountAdjustInfo clearAccountAdjustInfo, Model model) {	
		model.addAttribute("clearAccountAdjustInfo", clearAccountAdjustInfo);
		return "modules/clear/v03/clearAccountAdjustInfo/clearAccountAdjustInfoForm";
	}
	/**
	 * 保存登记信息
	 * 
	 * @author dja
	 * @version 2018年4月23日
	 * @param ClearAccountAdjustInfo
	 * @return 列表页面
	 */
	@RequiresPermissions("clear:clearAccountAdjustInfo:add")
	@RequestMapping(value = "save")
	public String save(ClearAccountAdjustInfo clearAccountAdjustInfo, Model model, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		try {
			// 保存
			clearAccountAdjustInfoService.save(clearAccountAdjustInfo);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			addMessage(model, message);
			return form(clearAccountAdjustInfo, model);
		}
		// message.I7018=调账单号：{0}保存成功！
		message = msg.getMessage("message.I7018", new String[] { clearAccountAdjustInfo.getAccountsId() }, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + Global.getAdminPath() + "/clear/v03/clearAccountAdjustInfo/?repage";
	
	}
	/**
	 * 跳转到查看页面
	 * 
	 * @author dja
	 * @version 2018年4月26日
	 * @param clearAccountAdjustInfo
	 *            信息
	 * @param model
	 * @return 查看页面
	 */
	@RequiresPermissions("clear:clearAccountAdjustInfo:view")
	@RequestMapping(value = "view")
	public String view(ClearAccountAdjustInfo clearAccountAdjustInfo, Model model) {
		// 人员列表
		List<StoEscortInfo> userList = Lists.newArrayList();
		// 交款人
		StoEscortInfo payTeller = new StoEscortInfo();
		payTeller.setId(clearAccountAdjustInfo.getPayTellerBy());
		// 收款人
		StoEscortInfo reTeller = new StoEscortInfo();
		reTeller.setId(clearAccountAdjustInfo.getReTellerBy());
		userList.add(payTeller);
		userList.add(reTeller);
		// 根据金额类型获取柜员和余额
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
		// 获得金额类型
		tellerAccountsMain.setCashType(clearAccountAdjustInfo.getCashType());
		// 查询柜员账务
		List<TellerAccountsMain> tellerAccounts = clearAccountAdjustInfoService.findTellerAccounts(tellerAccountsMain);
		// 页面用List
		List<TellerAccountsMain> tellerAccountsForPage = Lists.newArrayList();
		for (StoEscortInfo escortInfo : userList) {
			boolean flag = false;
			for (TellerAccountsMain tellerAccountsMain2 : tellerAccounts) {
				if (escortInfo.getId().equals(tellerAccountsMain2.getTellerBy())) {
					tellerAccountsForPage.add(tellerAccountsMain2);
					flag = true;
				}
			}
			// 柜员身上没有账务
			if (!flag) {
				TellerAccountsMain teller = new TellerAccountsMain();
				teller.setTellerName(escortInfo.getEscortName());
				teller.setTotalAmount(new BigDecimal(0));
				tellerAccountsForPage.add(teller);
			}

		}
		model.addAttribute("tellerAccounts", tellerAccountsForPage);
		model.addAttribute(clearAccountAdjustInfo);
		return "modules/clear/v03/clearAccountAdjustInfo/clearAccountAdjustInfoView";
	}
	/**
	 * 返回到列表页面
	 * 
	 * @author dja
	 * @version 2018年4月24日
	 * @param ClearAccountAdjustInfo
	 * @param status
	 * @return 跳转页面（列表页面）
	 */
	@RequestMapping(value = "back")
	public String back(ClearAccountAdjustInfo clearAccountAdjustInfo, SessionStatus status) {
		return "redirect:" + adminPath + "/clear/v03/clearAccountAdjustInfo/list?repage";
	}
	/**
	 * 将数字金额转换为大写金额
	 * 
	 * @author dja
	 * @version 2018年4月24日
	 * @param amount
	 * @return 大写金额
	 */
	@RequestMapping(value = "/changRMBAmountToBig")
	@ResponseBody
	public String changRMBAmountToBig(@RequestParam(value = "amount", required = true) String amount) {
		Map<String, String> rtnMap = Maps.newHashMap();
		double dAmount = Double.parseDouble(amount);
		String strBigAmount = NumToRMB.changeToBig(dAmount);
		rtnMap.put("bigAmount", strBigAmount);
		return gson.toJson(rtnMap);
	}
	/**
	 * 根据选择的金额类型（备、非备），获取相应交接员
	 * 
	 * @author dja
	 * @param officeId
	 * @param cashType         
	 * @version 2018年4月25日
	 * @return
	 */
	@RequestMapping(value = "changeBankConnect")
	@ResponseBody
	public String changeBankConnect(String cashType) {		
		cashType = cashType.replace("&quot;", "");
		ClearAccountAdjustInfo clearAccountAdjustInfo = new ClearAccountAdjustInfo();
		// 获取用户
		User userInfo = UserUtils.getUser();
		// 获取机构
		clearAccountAdjustInfo.setOffice(userInfo.getOffice());		
		List<Map<String, Object>> dataList = Lists.newArrayList();
		//将获得的金额类型对应到相应管理员类型(备付金)
		if(cashType.equals(Constant.ClearCashType.EXESSRESERVE) && !("".equals(cashType)) && cashType != null){
			String userType =Constant.CLEAR_PROVISIONS;
			//根据用户类型查询金库人员信息
			List<StoEscortInfo> list  = stoEscortInfoService.findEscortInfoByUserType(clearAccountAdjustInfo.getOffice().getId() ,userType);
			if (!Collections3.isEmpty(list)) {
				for (StoEscortInfo item : list) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("label", item.getEscortName());
					map.put("id", item.getId());
					dataList.add(map);
				}	
			}
		//将获得的金额类型对应到相应管理员类型（非备付金）
		}else if(cashType.equals(Constant.ClearCashType.CASH) && !("".equals(cashType)) && cashType != null){
			String userType = Constant.CONNECT_PERSON;
			//根据用户类型查询金库人员信息
			List<StoEscortInfo> list = stoEscortInfoService.findEscortInfoByUserType(clearAccountAdjustInfo.getOffice().getId() ,userType);
			if (!Collections3.isEmpty(list)) {
				for (StoEscortInfo item : list) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("label", item.getEscortName());
					map.put("id", item.getId());
					dataList.add(map);
				}
			}
		}
		return gson.toJson(dataList);
	}
	/**
	 * 获取余额信息
	 * 
	 * @author dja
	 * @date 2018-04-26
	 * 
	 * @Description
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "totalAmount")
	public String getTotalAmount(@RequestParam(value = "cashType", required = false) String cashType, Model model) {	
		// 人员列表
		List<StoEscortInfo> userList = Lists.newArrayList();
		User user = UserUtils.getUser();
		// 备付金
		if (ClearConstant.CashTypeProvisions.PROVISIONS_TRUE.equals(cashType)) {
			userList = StoreCommonUtils.findEscortInfoByUserType(user.getOffice().getId(),
					Global.getConfig("clear.userType.provision"));
			// 现金
		} else if (ClearConstant.CashTypeProvisions.PROVISIONS_FALSE.equals(cashType)) {
			userList = StoreCommonUtils.findEscortInfoByUserType(user.getOffice().getId(),
					Global.getConfig("connect.person.businessType"));
			// 默认
		} else {
			userList = StoreCommonUtils.findEscortInfoByUserType(user.getOffice().getId(),
					Global.getConfig("clear.teller.businessType"));
		}
		// 根据金额类型获取柜员和余额
		TellerAccountsMain tellerAccountsMain = new TellerAccountsMain();
		//获得金额类型
		tellerAccountsMain.setCashType(cashType);
		//查询柜员账务
		List<TellerAccountsMain> tellerAccounts = clearAccountAdjustInfoService.findTellerAccounts(tellerAccountsMain);
		// 页面用List
		List<TellerAccountsMain> tellerAccountsForPage = Lists.newArrayList();
		for (StoEscortInfo escortInfo : userList) {
			boolean flag = false;
			for (TellerAccountsMain tellerAccountsMain2 : tellerAccounts) {
				if (escortInfo.getId().equals(tellerAccountsMain2.getTellerBy())) {
					tellerAccountsForPage.add(tellerAccountsMain2);
					flag = true;
				}
			}
			// 柜员身上没有账务
			if (!flag) {
				TellerAccountsMain teller = new TellerAccountsMain();
				teller.setTellerName(escortInfo.getEscortName());
				teller.setTotalAmount(new BigDecimal(0));
				tellerAccountsForPage.add(teller);
			}

		}
		model.addAttribute("tellerAccounts", tellerAccountsForPage);
		return "modules/clear/v03/clearAccountAdjustInfo/totalAmount";
	}
}
