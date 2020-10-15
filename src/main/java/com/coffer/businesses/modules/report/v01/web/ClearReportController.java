package com.coffer.businesses.modules.report.v01.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.clear.ClearConstant;
import com.coffer.businesses.modules.clear.v03.entity.OrderClearMain;
import com.coffer.businesses.modules.report.v01.entity.ClearReportAmount;
import com.coffer.businesses.modules.report.v01.service.ClearReportService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.OfficeService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 清分报表管理Controller
 * 
 * @author xp
 * @version 2017-11-16
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/clear")
public class ClearReportController extends BaseController {

	@Autowired
	private ClearReportService clearReportService;
	@Autowired
	private OfficeService officeService;

	@RequestMapping(value = "clearAmount")
	@ResponseBody
	public String graphicalClearList() {
		User user = UserUtils.getUser();
		ClearReportAmount clearReportAmount = new ClearReportAmount();
		// 设置查询出的日期格式
		if (Constant.jdbcType.ORACLE.equals(clearReportAmount.getDbName())) {
			clearReportAmount.setFilterCondition(Global.getConfig("firstPage.oracle.findDate.format"));
		} else if (Constant.jdbcType.MYSQL.equals(clearReportAmount.getDbName())) {
			clearReportAmount.setFilterCondition(Global.getConfig("firstPage.mysql.findDate.format"));
		}
		// 设置开始日期和结束日期(间隔时间为一年)
		// 查询条件：开始时间
		clearReportAmount.setSearchDateStart(DateUtils.formatDate(
				DateUtils.getDateStart(
						DateUtils.addDate(new Date(), Integer.parseInt(Global.getConfig("firstPage.date.interval")))),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询条件：结束时间
		clearReportAmount.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 设置查询的有效标识为有效
		clearReportAmount.setDelFlag(Constant.deleteFlag.Valid);
		// 设置查询的流水状态为登记
		clearReportAmount.setStatus(ClearConstant.StatusType.CREATE);
		// 设置查询的出入库类型
		List<String> inList = Global.getList("accounts.businessType.in");
		clearReportAmount.setInStatuses(inList);
		List<String> outList = Global.getList("accounts.businessType.out");
		clearReportAmount.setOutStatuses(outList);
		// 判断用户类型
		if (Constant.OfficeType.CENTRAL_BANK.equals(UserUtils.getUser().getOffice().getType())) {
			// 如果是人行
			Office office = new Office();
			office.setType(Constant.OfficeType.COFFER);
			office.setParentIds(user.getOffice().getParentIds());
			office.setDelFlag(Constant.deleteFlag.Valid);
			List<Office> officeList = officeService.findList(office);
			clearReportAmount.setOfficeList(officeList);
			// 设置人行清分出入库类型
			inList = Global.getList("accounts.businessType.peopleBankIn");
			outList = Global.getList("accounts.businessType.peopleBankOut");
			clearReportAmount.setInStatuses(inList);
			clearReportAmount.setOutStatuses(outList);
			/* 修改人:sg 修改日期:201-12-06 begin */
			// clearReportAmount.setCustNo(user.getOffice().getId());
			/* end */
		} else if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置流水客户编号为当前用户所属机构
			clearReportAmount.setCustNo(user.getOffice().getId());
			// 设置人行清分出入库类型
			inList = Global.getList("accounts.businessType.cofferIn");
			outList = Global.getList("accounts.businessType.cofferOut");
			clearReportAmount.setInStatuses(inList);
			clearReportAmount.setOutStatuses(outList);
		} else if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())) {
			/* 登陆用户机构判断修改(清分中心) 修改人：wxz 2017-12-4 begin */
			// 如果是清分中心用户登录
			// List<Office> officeList = StoreCommonUtils.getStoCustList("1,3",
			// false);
			List<Office> officeList = Lists.newArrayList();
			officeList.add(UserUtils.getUser().getOffice());
			// 添加所需客户
			clearReportAmount.setrOfficeList(officeList);
			/* end */
		}
		// 查询清分出入库业务
		List<ClearReportAmount> clearAmountList = clearReportService.findInOrOutAmount(clearReportAmount);
		// 进行图形化过滤
		Map<String, Object> jsonData = clearReportService.inOrOutAmount(clearAmountList);
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: reserveClearVolume
	 * <p>
	 * Description: 预约清分业务量统计图
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @return String 返回类型
	 */
	@RequestMapping(value = "reserveClearVolume")
	@ResponseBody
	public String reserveClearVolume() {
		OrderClearMain orderClearMain = new OrderClearMain();
		// 如果不是金融平台人员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
				orderClearMain.setRegisterOffice(UserUtils.getUser().getOffice().getId());
			}
			if (Constant.OfficeType.CLEAR_CENTER.equals(UserUtils.getUser().getOffice().getType())) {
				orderClearMain.setrOffice(UserUtils.getUser().getOffice());
			}
		}
		Map<String, Object> jsonData = clearReportService.reserveClearVolume(orderClearMain);
		return gson.toJson(jsonData);
	}

	/**
	 * 
	 * Title: reserveClearAmount
	 * <p>
	 * Description: 预约清分总金额统计图
	 * </p>
	 * 
	 * @author: yanbingxu
	 * @return String 返回类型
	 */
	@RequestMapping(value = "reserveClearAmount")
	@ResponseBody
	public String reserveClearAmount() {
		OrderClearMain orderClearMain = new OrderClearMain();
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

}
