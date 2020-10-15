package com.coffer.businesses.modules.common.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.common.AjaxConstant;
import com.coffer.businesses.modules.common.entity.ReceiveEntity;
import com.coffer.businesses.modules.common.service.IpadAjaxService;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessCount;
import com.coffer.businesses.modules.report.v01.entity.AllocateReportBusinessDegree;
import com.coffer.businesses.modules.report.v01.entity.ReportCondition;
import com.coffer.businesses.modules.report.v01.entity.StoBoxInfoGraphEntity;
import com.coffer.businesses.modules.report.v01.service.AllocateReportBusinessService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.external.ExternalConstant;
import com.coffer.external.hessian.HardwareConstant;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author LLF
 * @version 2015年9月17日 ajax请求控制类
 *
 */
@Controller
@RequestMapping(value = "${adminPath}/ipadAjax")
public class IpadAjaxController extends BaseController {

	@Autowired
	IpadAjaxService ipadAjaxService;
	
	@Autowired
	PbocAllAllocateInfoService pbocAllAllocateInfoService;

	@Autowired
	private AllocateReportBusinessService allocateReportBusinessService;
	/**
	 * 
	 * @author LLF
	 * @version 2015年9月17日
	 * 
	 *          接收ajax请求
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "ajax", method = RequestMethod.POST)
	@ResponseBody
	public String ajaxDatas(ReceiveEntity entity, HttpServletRequest request, HttpServletResponse response) {
		if (entity == null || StringUtils.isBlank(entity.getServiceNo())) {
			String data = (String) request.getParameter(AjaxConstant.Interface.REQUEST_JSON_KEY);
			entity = gson.fromJson(data, ReceiveEntity.class);
		}
		logger.info("接收JSON:" + entity.toString());
		String serviceNo = entity.getServiceNo();
		String resultString = "";
		Map<String, Object> map = Maps.newHashMap();
		map.put("serviceNo", entity.getServiceNo());
		map.put("versionNo", entity.getVersionNo());
		try {
			if (HardwareConstant.ServiceNo.service_no_S13.equals(serviceNo)) {
				// 同步数据字典信息
				resultString = ipadAjaxService.getGoodsDictInfo(serviceNo, entity);
			} else if (HardwareConstant.ServiceNo.service_no_S14.equals(serviceNo)) {
				// 同步物品信息
				resultString = ipadAjaxService.getGoodsAndStores(serviceNo, entity);
			} else if (HardwareConstant.ServiceNo.service_no_S15.equals(serviceNo)) {
				// 同步字典关联关系
				resultString = ipadAjaxService.getStockCountRelevance(serviceNo, entity);
			} else if (HardwareConstant.ServiceNo.service_no_S16.equals(serviceNo)) {
				// 上传盘点信息
				resultString = ipadAjaxService.uploadStockCountInfo(serviceNo, entity);
			} else if (HardwareConstant.ServiceNo.service_no_S17.equals(serviceNo)) {
				// 盘点信息更新库存
				resultString = ipadAjaxService.updateStoreByStockCount(serviceNo, entity);
			} else if (HardwareConstant.ServiceNo.service_no_S30.equals(serviceNo)) {
				// 盘点用户登录
				resultString = ipadAjaxService.padUserLogin(serviceNo, entity);
			} else if (HardwareConstant.ServiceNo.service_no_S31.equals(serviceNo)) {
				// 盘点所属机构下用户信息
				resultString = ipadAjaxService.getUserByOffice(serviceNo, entity);
			} else if (HardwareConstant.ServiceNo.service_no_S06.equals(serviceNo)) {
				// 管理员授权
				resultString = ipadAjaxService.userAuthorization(serviceNo, entity);
			} else {
				resultString = ipadAjaxService.erro(serviceNo);
			}
		} catch (BusinessException be) {
			// 业务异常
			map.put("resultFlag", AjaxConstant.Interface.RESULT_FLAG_FAILURE);
			map.put("errorNo", AjaxConstant.Interface.ERROR_NO_E05);
			map.put("errorMsg", msg.getMessage(be.getMessageCode(), be.getParameters(), null) );
			resultString = gson.toJson(map);
		} catch (Exception e) {
			// 处理异常
			e.printStackTrace();
			map.put("resultFlag", ExternalConstant.HardwareInterface.RESULT_FLAG_FAILURE);
			map.put("errorNo", ExternalConstant.HardwareInterface.ERROR_NO_E02);
			resultString = gson.toJson(map);
		}
		response.setHeader("Access-Control-Allow-Origin", "*");
		logger.info("返回JSON:" + resultString);
		return resultString;
	}

//	待办列表备份。如果需要待办列表的情况，使用次方法。如果需要图形报表的场合，下面的方法。
//	/**
//	 * 
//	 * @author LLF
//	 * @version 2016年5月19日
//	 * 首页加载
//	 *  
//	 * @param request
//	 * @param response
//	 * @param model
//	 * @return
//	 */
//	@RequestMapping(value = "first")
//	public String initFirst(HttpServletRequest request, HttpServletResponse response,Model model) {
//		PbocAllAllocateInfo pbocAllAllocateInfo = new PbocAllAllocateInfo();
//		pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_QUOTA_STATUS);
//		
//		// 下拨- 待配款
//		List<String> allocatedBusinessTypeList = Lists.newArrayList();
//		pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
//		//申请下拨
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION);
//		pbocAllAllocateInfo.setBusinessTypeList(allocatedBusinessTypeList);
//		
//		Page<PbocAllAllocateInfo> quotaPageList = pbocAllAllocateInfoService.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
//		
//		allocatedBusinessTypeList.clear();
//		//调拨出库
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_ALLOCATION_OUT_STORE);
//		// 销毁出库
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_DESTROY_OUT_STORE);
//		// 复点出库
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_RE_COUNTING);
//		pbocAllAllocateInfo.setAoffice(null);
//		pbocAllAllocateInfo.setRoffice(UserUtils.getUser().getOffice());
//		pbocAllAllocateInfo.setBusinessTypeList(allocatedBusinessTypeList);
//		Page<PbocAllAllocateInfo> pbocQuotaPageList = pbocAllAllocateInfoService.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
//		if (quotaPageList.getList().size() < quotaPageList.getPageSize() && pbocQuotaPageList.getList().size() > 0) {
//			for (int iIndex = 0; iIndex < pbocQuotaPageList.getList().size(); iIndex ++) {
//				quotaPageList.getList().add(pbocQuotaPageList.getList().get(iIndex));
//				
//				if (quotaPageList.getList().size() == quotaPageList.getPageSize()) {
//					
//					break;
//				}
//			}
//		}
//		quotaPageList.setCount(quotaPageList.getList().size());
//		model.addAttribute("quotaPageList", quotaPageList);
//		// 下拨- 待接收 
//		//申请下拨
//		pbocAllAllocateInfo.setAoffice(null);
//		pbocAllAllocateInfo.setRoffice(UserUtils.getUser().getOffice());
//		allocatedBusinessTypeList.clear();
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION);
//		pbocAllAllocateInfo.setBusinessTypeList(allocatedBusinessTypeList);
//		pbocAllAllocateInfo.setStatuses(Global.getList("pboc_allocation_accept_status"));
//		pbocAllAllocateInfo.setStatus("");
//		Page<PbocAllAllocateInfo> acceptPageList = pbocAllAllocateInfoService.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
//		model.addAttribute("acceptPageList", acceptPageList);
//		
//		// 下拨-上缴-代理上缴 待审批
//		pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
//		pbocAllAllocateInfo.setRoffice(null);
//		allocatedBusinessTypeList.clear();
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION);
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN);
//		allocatedBusinessTypeList.add(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
//		pbocAllAllocateInfo.setBusinessTypeList(allocatedBusinessTypeList);
//		pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.TO_APPROVE_STATUS);
//		//　清空任务状态
////		pbocAllAllocateInfo.setBusinessType("");
//		pbocAllAllocateInfo.setStatuses(null);
//		Page<PbocAllAllocateInfo> approvePageList = pbocAllAllocateInfoService.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
//		model.addAttribute("approvePageList", approvePageList);
//		
//		return "modules/sys/sysFirst";
//	}
	
	
	/**
	 * 
	 * @author ChengShu	
	 * @version 2017年9月3日
	 * 首页加载
	 *  
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "first")
	public String initFirst(HttpServletRequest request, HttpServletResponse response,Model model) {
		return "modules/sys/sysMainGraph";
	}

	/**
	 * 查询常规现金业务和临时现金业务的金额（首页）
	 * 
	 * @author xp
	 * @param request
	 * @param response
	 * @return json
	 */
	@RequestMapping(value = "AllocateAmount")
	@ResponseBody
	public String graphicalAllocateList(HttpServletRequest request,
			HttpServletResponse response) {
		User user = UserUtils.getUser();
		ReportCondition reportCondition = new ReportCondition();
		// 设置查询出的日期格式
		if (Constant.jdbcType.ORACLE.equals(reportCondition.getDbName())) {
			reportCondition.setFilterCondition(Global.getConfig("firstPage.oracle.findDate.format"));
		} else if (Constant.jdbcType.MYSQL.equals(reportCondition.getDbName())) {
			reportCondition.setFilterCondition(Global.getConfig("firstPage.mysql.findDate.format"));
		}
		// 设置开始日期和结束日期(间隔时间为一年)
		// 查询条件：开始时间
		reportCondition
				.setSearchDateStart(DateUtils.formatDate(
						DateUtils.getDateStart(
								DateUtils.addDate(new Date(),
										Integer.parseInt(Global.getConfig("firstPage.date.interval")))),
						AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询条件：结束时间
		reportCondition.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 设置查询的有效标识为有效
		reportCondition.setDelFlag(Constant.deleteFlag.Valid);
		// 添加查询的业务类型
		List<String> businessTypes = Lists.newArrayList();
		businessTypes.add(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		businessTypes.add(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		reportCondition.setBusinessTypes(businessTypes);
		// 如果是金融平台用户登录
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置查询条件的机构为数字化金融平台
			reportCondition.setOffice(user.getOffice());
		} else if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置流水接收机构为当前用户所属机构
			reportCondition.setaOffice(user.getOffice());
		} else if (Constant.OfficeType.OUTLETS.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置流水登记机构为当前用户所属机构
			reportCondition.setrOffice(user.getOffice());
		}
		// 查询常规现金业务和临时现金业务的金额
		List<AllocateReportBusinessCount> allocateBusinessCount = allocateReportBusinessService
				.findBusinessCount(reportCondition);
		// 进行图形化过滤
		Map<String, Object> jsonData = allocateReportBusinessService.graphicalBusinessCount(allocateBusinessCount);
		return gson.toJson(jsonData);
	}
	/**
	 * 
	 * @author admin
	 * @version 2016年6月3日
	 * 
	 *  更多待审批
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "findMoreList")
	public String findMoreList(PbocAllAllocateInfo pbocAllAllocateInfo,HttpServletRequest request, HttpServletResponse response,Model model) {
		
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		
		return "redirect:" + adminPath + "/allocation.v02/pbocAllAllocateInfo/list";
	}
	
	/**
	 * 
	 * Title: getFirstBoxStatusGraphData
	 * <p>Description: 获取工作台页面箱袋状态图数据</p>
	 * @author:     wanghan
	 * @param stoBoxInfoGraphEntity
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "getFirstBoxStatusGraphData")
	@ResponseBody
	public String getFirstBoxStatusGraphData(StoBoxInfoGraphEntity stoBoxInfoGraphEntity) {

		// 获取当前用户
		User user = UserUtils.getUser();
		// 判断权限，除顶级机构和平台，只显示当前机构数据
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {
			stoBoxInfoGraphEntity.setOffice(UserUtils.getUser().getOffice());

		}

		Map<String, Object> jsonData = ipadAjaxService.makeFirstBoxStatusGraphData(stoBoxInfoGraphEntity);

		return gson.toJson(jsonData);
	}
	
	/**
	 * 首页显示当前用户所属机构的常规业务及临时业务的状态
	 * 
	 * @author xp
	 * @version 2017-11-6
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "businessStatus")
	@ResponseBody
	public String graphBusinessStatus(HttpServletRequest request, HttpServletResponse response) {
		User user = UserUtils.getUser();
		ReportCondition reportCondition = new ReportCondition();
		// 设置查询出的日期格式
		if (Constant.jdbcType.ORACLE.equals(reportCondition.getDbName())) {
			reportCondition.setFilterCondition(Global.getConfig("firstPage.oracle.findDate.format"));
		} else if (Constant.jdbcType.MYSQL.equals(reportCondition.getDbName())) {
			reportCondition.setFilterCondition(Global.getConfig("firstPage.mysql.findDate.format"));
		}
		// 设置开始日期和结束日期(间隔时间为一年)
		// 查询条件：开始时间
		reportCondition.setSearchDateStart(DateUtils.formatDate(
				DateUtils.getDateStart(
						DateUtils.addDate(new Date(), Integer.parseInt(Global.getConfig("firstPage.date.interval")))),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 查询条件：结束时间
		reportCondition.setSearchDateEnd(DateUtils.formatDate(DateUtils.getDateEnd(new Date()),
				AllocationConstant.Dates.FORMATE_YYYY_MM_DD_HH_MM_SS));
		// 设置查询的有效标识为有效
		reportCondition.setDelFlag(Constant.deleteFlag.Valid);
		// 添加查询的业务类型
		List<String> businessTypes = Lists.newArrayList();
		businessTypes.add(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		businessTypes.add(AllocationConstant.BusinessType.OutBank_Cash_Reservation);
		reportCondition.setBusinessTypes(businessTypes);
		// 设置查询的条件
		reportCondition.setType(Global.getConfig("firstPage,sysDict.type"));
		// 如果是金融平台用户登录
		if (Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置查询条件的机构为数字化金融平台
			reportCondition.setOffice(user.getOffice());
		} else if (Constant.OfficeType.COFFER.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置流水接收机构为当前用户所属机构
			reportCondition.setaOffice(user.getOffice());
		} else if (Constant.OfficeType.OUTLETS.equals(UserUtils.getUser().getOffice().getType())) {
			// 设置流水登记机构为当前用户所属机构
			reportCondition.setrOffice(user.getOffice());
		}
		// 查询当前用户所属机构的常规业务及临时业务的状态
		List<AllocateReportBusinessDegree> statusList = allocateReportBusinessService
				.findBusinessStatus(reportCondition);
		// 进行图形化过滤
		Map<String, Object> jsonData = allocateReportBusinessService.graphicalBusinessStatus(statusList);
		return gson.toJson(jsonData);
	}
}
