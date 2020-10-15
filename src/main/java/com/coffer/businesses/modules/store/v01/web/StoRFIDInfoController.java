/**
 * @author CaiXiaojie
 * @version 2016年8月1日
 * 
 * 
 */
package com.coffer.businesses.modules.store.v01.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.businesses.modules.store.v01.entity.StoRfidDenomination;
import com.coffer.businesses.modules.store.v01.entity.StoRfidDenominationHistory;
import com.coffer.businesses.modules.store.v01.service.StoRfidDenominationService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 库房RFID箱袋管理Controller
 * @author CaiXiaojie
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/store/v01/stoRFIDInfo")
public class StoRFIDInfoController extends BaseController {
	
	@Autowired
	private StoRfidDenominationService service;

	/**
	 * RFID箱袋管理列表页面
	 * @author CaiXiaojie
	 * @version 2016年8月1日
	 * 
	 *  
	 * @param model
	 * @return 箱袋列表页面
	 */
	@RequestMapping(value={ "list", "" })
	public String list(StoRfidDenomination stoRfidDenomination, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<StoRfidDenomination> page = new Page<StoRfidDenomination>(request, response);
		
		if (stoRfidDenomination == null) {
			stoRfidDenomination = new StoRfidDenomination();
		} 
		
		// 查询条件：绑卡  开始时间
		if (stoRfidDenomination.getCreateTimeStart() != null) {
			stoRfidDenomination.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(stoRfidDenomination.getCreateTimeStart())));
		}
		// 查询条件：绑卡 结束时间
		if (stoRfidDenomination.getCreateTimeEnd() != null) {
			stoRfidDenomination.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(stoRfidDenomination.getCreateTimeEnd())));
		}
				
		if (StringUtils.isNotBlank(stoRfidDenomination.getRfid())) {
			stoRfidDenomination.setRfid(stoRfidDenomination.getRfid().toUpperCase());
		}
	
		stoRfidDenomination.setAtOfficeId(UserUtils.getUser().getOffice().getId());
		
		page = service.findPage(page, stoRfidDenomination);
		model.addAttribute("stoRfidDenomination", stoRfidDenomination);
		model.addAttribute("page", page);
		
		return "modules/store/v01/stoRFIDInfo/stoRFIDInfoList";
	}
	
	/**
	 * 
	 * Title: getHistorylist
	 * <p>Description: 查询RFID历史</p>
	 * @author:     wangbaozhong
	 * @param rfid rfid信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value="getHistorylist")
	public String getHistorylist(@RequestParam(value="rfid", required=true)String rfid, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<StoRfidDenominationHistory> historyList = Lists.newArrayList();
		service.findfullStoRfidDenominationHistoryList(rfid, historyList);
		model.addAttribute("rfid", rfid);
		model.addAttribute("historyList", historyList);
		return "modules/store/v01/stoRFIDInfo/stoRFIDHistoryInfoList";
	}
	
	/**
	 * 
	 * Title: getStoRFIDLifeCycleGraph
	 * <p>Description: 查询RFID生命周期图</p>
	 * @author:     wangbaozhong
	 * @param rfid rfid信息
	 * @param request
	 * @param response
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value="getStoRFIDLifeCycleGraph")
	public String getStoRFIDLifeCycleGraph(@RequestParam(value="rfid", required=true)String rfid, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<StoRfidDenominationHistory> historyList = Lists.newArrayList();
		service.findfullStoRfidDenominationHistoryList(rfid, historyList);
		if (historyList.size() > 0) {
			model.addAttribute("rfid", rfid);
			StoRfidDenominationHistory firstData = historyList.get(0);
			model.addAttribute("initBindingOfficeName", firstData.getOldStoRfidDenomination().getOfficeName());
			model.addAttribute("initBindingBusinessType", firstData.getOldStoRfidDenomination().getBusinessType());
		} else {
			Locale locale = LocaleContextHolder.getLocale();
			// message.I1027=[提示]：箱袋编号【{0}】，尚未生成历史记录！
			String message = msg.getMessage("message.I1027", new String[] {rfid}, locale);
			addMessage(model, message);
			return list(null, request, response, model);
		}
		
		model.addAttribute("historyList", historyList);
		return "modules/store/v01/stoRFIDInfo/stoRFIDLifeCycleGraph";
	}
}
