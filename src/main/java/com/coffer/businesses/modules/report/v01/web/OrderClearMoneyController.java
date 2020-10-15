package com.coffer.businesses.modules.report.v01.web;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.report.v01.service.ClearReportService;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 预约清分总金额统计图
 * 
 * @author wxz
 * @version 2017年11月30日
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/orderClearMoney")
public class OrderClearMoneyController extends BaseController {

	@Autowired
	private ClearReportService clearReportService;
	
	/**
	 * 根据查询条件，预约清分总金额统计图一览信息
	 * 
	 * @author wxz
	 * @version 2017-11-30
	 * @param ReportCondition
	 * @return 预约清分总金额统计图
	 */
	@RequestMapping(value = { "toGraphPage", "" })
	public String toGraphPage(OrderClearMain orderClearMain, Model model) {
		// 获得日历
		Calendar time = Calendar.getInstance();
		// 获得当前时间
		Date now = new Date();
		time.setTime(now);
		// 本月一号
		time.set(Calendar.DATE, 1);
		Date firstDate = time.getTime();
		if (orderClearMain.getCreateTimeStart() == null) {
			orderClearMain.setCreateTimeStart(firstDate);
		}
		if (orderClearMain.getCreateTimeEnd() == null) {
			orderClearMain.setCreateTimeEnd(now);
		}
		model.addAttribute("orderClearMain", orderClearMain);
		return "modules/clear/v03/orderClearMoney/orderClearMoney";
	}

	/**
	 * 
	 * Title: reserveClearAmount
	 * <p>Description: 预约清分总金额统计图</p>
	 * @author:     wxz
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "reserveClearAmount")
	@ResponseBody
	public String reserveClearAmount(OrderClearMain orderClear, HttpServletRequest request,
			HttpServletResponse response) {
		OrderClearMain orderClearMain = selectConditionClear(orderClear);
		// 如果不是金融平台人员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
				orderClearMain.setRegisterOffice(UserUtils.getUser().getOffice().getId());
			}
			if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())) {
				orderClearMain.setrOffice(UserUtils.getUser().getOffice());
			}
		}
		Map<String, Object> jsonData = clearReportService.reserveClearAmount(orderClearMain);
		return gson.toJson(jsonData);
	}
	
	/**
	 * @author wxz
	 * @version 2017年11月30日 添加查询条件
	 * @param reportCondition
	 *            预约清分总金额统计图查询信息
	 * 
	 * @return LatticePointHandin
	 */
	private OrderClearMain selectConditionClear(OrderClearMain orderClearMain) {
		OrderClearMain alllatticePointHandin = new OrderClearMain();
		
		// 查询条件：开始时间
		if (orderClearMain.getCreateTimeStart() != null) {
			alllatticePointHandin.setSearchDateStart(
					DateUtils.formatDate(DateUtils.getDateStart(orderClearMain.getCreateTimeStart()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		// 查询条件：结束时间
		if (orderClearMain.getCreateTimeEnd() != null) {
			alllatticePointHandin
					.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(orderClearMain.getCreateTimeEnd()),
							AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		}
		return alllatticePointHandin;
	}
}
