package com.coffer.businesses.modules.report.v01.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.v01.entity.ReportInfo;
import com.coffer.businesses.modules.report.v01.service.GraphService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @author chengshu
 * @date 2016-5-18
 * 
 * @Description 可视化图形管理
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/graph")
public class GraphController extends BaseController {

	/** 图形报表Service **/
	@Autowired
	private GraphService graphService;
	/** 商行调拨Service **/
	@Autowired
	private AllocationService allocationService;
	
	/** 开始日期 **/
	private Date startDate;
	/** 结束日期 **/
	private Date endDate;

	/**
	 * @author chengshu
	 * @version 2016-05-18
	 * 
	 * @Description 迁移到物品库存饼图
	 * @return 页面路径
	 */
	@RequestMapping(value = { "pbocStoreAreaECharts", "" })
	public String pbcStoreInfoECharts(HttpServletRequest request, HttpServletResponse response, Model model) {

		// 设置当前日期
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setSearchDate(new Date());
		model.addAttribute("reportInfo", reportInfo);
		
		// 金库人员
		StringBuffer cofferBuffer = new StringBuffer();
		cofferBuffer.append(ReportConstant.SysUserType.COFFER_MANAGER);
		cofferBuffer.append(ReportConstant.Punctuation.COMMA);
		cofferBuffer.append(ReportConstant.SysUserType.COFFER_OPT);
		model.addAttribute("cofferUser", StringUtils.toString(cofferBuffer));

		// 网点人员
		StringBuffer bankOutletsBuffer = new StringBuffer();
		bankOutletsBuffer.append(ReportConstant.SysUserType.BANK_OUTLETS_MANAGER);
		bankOutletsBuffer.append(ReportConstant.Punctuation.COMMA);
		bankOutletsBuffer.append(ReportConstant.SysUserType.BANK_OUTLETS_OPT);
		model.addAttribute("bankOutletsUser", StringUtils.toString(bankOutletsBuffer));

		// 登录人员类型
		model.addAttribute("userType", UserUtils.getUser().getUserType());

		// 登录人员类型
		if (StringUtils.contains(StringUtils.toString(cofferBuffer), UserUtils.getUser().getUserType())) {
			model.addAttribute("officeType", "coffer");

		} else if (StringUtils.contains(StringUtils.toString(bankOutletsBuffer), UserUtils.getUser().getUserType())) {
			model.addAttribute("officeType", "bankOutlets");
		}

		return "modules/report/v01/graph/pbocStoreAreaECharts";
	}

	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 网点调拨明细
	 * @return 网点调拨饼形图数据
	 */
	@RequestMapping(value = { "getBankOutletsPieData" })
	@ResponseBody
	public String getBankOutletsPieData(@ModelAttribute("type")String type, @ModelAttribute("date")String date, HttpServletRequest request, HttpServletResponse response) {
		
		// 查询网点调拨明细
		List<AllAllocateInfo> allocationInfoList = geBankOutletsDataList(type, UserUtils.getUser(), date);

		// 初始化饼图分类标题
		List<String> titleList = Lists.newArrayList();
		// 初始化饼图显示数据
		List<Map<String, String>> dataList = Lists.newArrayList();

		// 组装饼图显示用数据
		graphService.makeBankOutletsInOutEChartData(allocationInfoList, titleList, dataList);

		// 组装好的饼图数据返回页面
		Map<String, Object> jsonData = Maps.newHashMap();
		// 设置JSON数据
		if(titleList != null && titleList.size() > 0) {
			jsonData.put("title", titleList);
			jsonData.put("data", dataList);
		}else {
			// 没有数据库的场合，设置空数据
			graphService.setCommonNullJsonData(jsonData);
		}

		return new Gson().toJson(jsonData);
	}
	
	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 网点调拨明细
	 * @return 金库现金库存饼形图数据
	 */
	@RequestMapping(value = { "getCofferStoreInfoPieData" })
	@ResponseBody
	public String getCofferStoreInfoPieData(HttpServletRequest request, HttpServletResponse response) {


		// 取得最新库存信息
		Map<String, Object> storeInfoMap = StoreCommonUtils.getNewestStoreInfo(UserUtils.getUser().getOffice().getId());

		// 组装好的饼图数据返回页面
 		Map<String, Object> jsonData = Maps.newHashMap();
 
		// 人民币库存信息字典
		Map<String, Map<String, String>> rmbGoodsKeysMap = Maps.newHashMap();
		// 人民币库存信息数量
		Map<String, Map<String, Long>> rmbGoodsStoreMap = Maps.newHashMap();

		// 国际货币库存信息字典
		Map<String, String> interGoodsKeysMap = Maps.newHashMap();
		// 国际货币库存信息金额
		Map<String, Long> interGoodsStoreMap = Maps.newHashMap();
 
		// 验证库存信息是否存在
		if (null != storeInfoMap.get("stoStoresInfoList")) {

			// 取得库存列表
			@SuppressWarnings("unchecked")
			List<StoStoresInfo> storeInfoList = (List<StoStoresInfo>) storeInfoMap.get("stoStoresInfoList");

			// 保存各种物品库存信息
			graphService.setStoreInfoPieData(storeInfoList, rmbGoodsKeysMap, rmbGoodsStoreMap, interGoodsKeysMap, interGoodsStoreMap);

			// 组装页面饼图用JSON数据
			graphService.setStoreInfoPieJsonData(jsonData, rmbGoodsKeysMap, rmbGoodsStoreMap, interGoodsKeysMap, interGoodsStoreMap);

		} else {
			// 空库存设置
			graphService.setStoreNullJsonData(jsonData, rmbGoodsKeysMap, interGoodsKeysMap);
		}

		return new Gson().toJson(jsonData);
	}
	
	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 出入库现金量线图数据采集
	 * @return 出入库现金线形图
	 */
	@RequestMapping(value = { "getInOutAmountLineData" })
	@ResponseBody
	public String getInOutAmountLineData(@ModelAttribute("date")String date, HttpServletRequest request, HttpServletResponse response, Model model) {

		// 取得当前登录用户
		User loginUser = UserUtils.getUser();
		
		// 设置查询开始时间和结束时间
		setSearchDate(date);
		
		// 查询入库（上缴）的现金列表
		List<AllAllocateInfo> inAmountList = getInAmountList(loginUser);
		// 查询出库（下拨）的现金列表
		List<AllAllocateInfo> outAmountList = getOutAmountList(loginUser);

		// 组装饼图显示用数据
		Map<String, Object> jsonData = graphService.makeInOutAmountLineData(inAmountList, outAmountList);
		return new Gson().toJson(jsonData);
	}

	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 查询入库（上缴）的现金量（1：网点查询的场合，查询上缴的金额 / 2：金库查询的场合，查询入库的金额。）。
	 * @param user 登录系统的用户
	 * @return 调拨现金列表
	 */
	private List<AllAllocateInfo> getInAmountList(User user) {

		// 查询条件初始化
		AllAllocateInfo allocateInfo = new AllAllocateInfo();

		// 开始时间
		allocateInfo.setAcceptTimeStart(DateUtils.getDateStart(getStartDate()));
		// 结束时间
		allocateInfo.setAcceptTimeEnd(DateUtils.getDateEnd(getEndDate()));

		// 查询条件：完成开始时间
		allocateInfo.setSearchDateStart(DateUtils.foramtSearchDate(allocateInfo.getAcceptTimeStart()));
		// 查询条件：完成结束时间
		allocateInfo.setSearchDateEnd(DateUtils.foramtSearchDate(allocateInfo.getAcceptTimeEnd()));

		// 状态
		allocateInfo.setStatus(AllocationConstant.Status.Finish);
		// 出入库种别（入库）
		allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);

		// 根据用户角色取得数据
		if (ReportConstant.SysUserType.COFFER_MANAGER.equals(user.getUserType())
				|| ReportConstant.SysUserType.COFFER_MANAGER.equals(user.getUserType())) {
			// 商行金库角色
			// 金库机构
			allocateInfo.setaOffice(user.getOffice());

		} else if (ReportConstant.SysUserType.BANK_OUTLETS_MANAGER.equals(user.getUserType())
				|| ReportConstant.SysUserType.BANK_OUTLETS_OPT.equals(user.getUserType())) {
			// 网点角色
			// 网点机构
			allocateInfo.setrOffice(user.getOffice());
		}

		// 执行查询
		return allocationService.findAllocation(allocateInfo);
	}
	
	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 查询出库（下拨）的现金量（1：网点查询的场合，查询上缴的金额 / 2：金库查询的场合，查询入库的金额。）。
	 * @param user 登录系统的用户
	 * @return 调拨现金列表
	 */
	private List<AllAllocateInfo> getOutAmountList(User user) {

		// 查询条件初始化
		AllAllocateInfo allocateInfo = new AllAllocateInfo();

		// 开始时间
		allocateInfo.setAcceptTimeStart(DateUtils.getDateStart(getStartDate()));
		// 结束时间
		allocateInfo.setAcceptTimeEnd(DateUtils.getDateEnd(getEndDate()));

		// 查询条件：完成开始时间
		allocateInfo.setSearchDateStart(DateUtils.foramtSearchDate(allocateInfo.getAcceptTimeStart()));
		// 查询条件：完成结束时间
		allocateInfo.setSearchDateEnd(DateUtils.foramtSearchDate(allocateInfo.getAcceptTimeEnd()));

		// 状态
		allocateInfo.setStatus(AllocationConstant.Status.Finish);
		// 出入库种别（入库）
		allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);

		// 根据用户角色取得数据
		if (ReportConstant.SysUserType.COFFER_MANAGER.equals(user.getUserType())
				|| ReportConstant.SysUserType.COFFER_MANAGER.equals(user.getUserType())) {
			// 商行金库角色
			// 金库机构
			allocateInfo.setaOffice(user.getOffice());

		} else if (ReportConstant.SysUserType.BANK_OUTLETS_MANAGER.equals(user.getUserType())
				|| ReportConstant.SysUserType.BANK_OUTLETS_OPT.equals(user.getUserType())) {
			// 网点角色
			// 网点机构
			allocateInfo.setrOffice(user.getOffice());
		}

		// 执行查询
		return allocationService.findAllocation(allocateInfo);
	}

	/**
	 * @author chengshu
	 * @version 2017-08-24
	 * 
	 * @Description 查询网点上缴、下拨物品明细。
	 * @param user 登录系统的用户
	 * @param nowDate 现在时间
	 * @param date 日期
	 * @return 现金调拨列表
	 */
	private List<AllAllocateInfo> geBankOutletsDataList(String type, User user, String date) {

		// 查询条件初始化
		AllAllocateInfo allocateInfo = new AllAllocateInfo();

		// 开始时间
		allocateInfo.setAcceptTimeStart(DateUtils.getDateStart(DateUtils.parseDate(date)));
		// 结束时间
		allocateInfo.setAcceptTimeEnd(DateUtils.getDateEnd(DateUtils.parseDate(date)));

		// 查询条件：完成开始时间
		allocateInfo.setSearchDateStart(DateUtils.foramtSearchDate(allocateInfo.getAcceptTimeStart()));
		// 查询条件：完成结束时间
		allocateInfo.setSearchDateEnd(DateUtils.foramtSearchDate(allocateInfo.getAcceptTimeEnd()));

		// 状态
		allocateInfo.setStatus(AllocationConstant.Status.Finish);
		// 机构
		allocateInfo.setrOffice(UserUtils.getUser().getOffice());

		// 业务种别
		if ("in".equals(type)) {
			// 下拨
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		} else {
			// 上缴
			allocateInfo.setBusinessType(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		}

		// 执行查询
		return allocationService.findAllocation(allocateInfo);
	}
	
	/**
	 * @Description 设置查询时间
	 * 
	 * @param date 时间
	 */
	private void setSearchDate(String date) {

		// 页面设置的时间
		Calendar settingCal = Calendar.getInstance();
		settingCal.setTime(DateUtils.parseDate(date));

		// 开始时间：当月第一天
		settingCal.set(Calendar.DAY_OF_MONTH,settingCal.getActualMinimum(Calendar.DAY_OF_MONTH));  
		Date startDate = settingCal.getTime();
		
		// 结束时间：当月最后一天
		settingCal.set(Calendar.DAY_OF_MONTH,settingCal.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = settingCal.getTime();
		
		// 设置开始时间
		setStartDate(startDate);
		
		// 设置结束时间
		setEndDate(endDate);
	}
	
	/**
	 * @Description 取得开始时间
	 * 
	 * @return 开始时间
	 */
	private Date getStartDate() {
		return startDate;
	}

	/**
	 * @Description 设置开始时间
	 * 
	 * @param startDate 开始时间
	 */
	private void setStartDate(Date startDate) {
		this.startDate = DateUtils.getDateStart(startDate);
	}

	/**
	 * @Description 取得结束时间
	 * 
	 * @return 结束时间
	 */
	private Date getEndDate() {
		return endDate;
	}

	/**
	 * @Description 设置结束时间
	 * 
	 * @param endDate 结束时间
	 */
	private void setEndDate(Date endDate) {
		this.endDate = DateUtils.getDateEnd(endDate);
	}
}
