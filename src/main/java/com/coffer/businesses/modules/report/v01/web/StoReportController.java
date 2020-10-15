package com.coffer.businesses.modules.report.v01.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.common.Constant.BoxStatus;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.report.v01.entity.ReportInfo;
import com.coffer.businesses.modules.report.v01.entity.StoInfoReportEntity;
import com.coffer.businesses.modules.report.v01.service.ReportService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.entity.StoReportInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresHistory;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfo;
import com.coffer.businesses.modules.store.v01.entity.StoStoresInfoEntity;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.businesses.modules.store.v01.service.StoStoresInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
 * 库房报表管理Controller
 * 
 * @author LLF
 * @version 2014-12-24
 */
@Controller
@RequestMapping(value = "${adminPath}/report/v01/store")
public class StoReportController extends BaseController {

	@Autowired
	private StoStoresInfoService stoStoresInfoService;
	
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	
	@Autowired
	private ReportService reportService;

	/**
	 * @author wh 物品库存页面导出
	 * @param stoReportInfo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "exportReportGraph")
	public String exportReportGraph(StoStoresInfoEntity stoStoresInfo, HttpServletRequest request,
			HttpServletResponse response) {

		// 查询处理
		List<StoStoresInfoEntity> goodsList = stoStoresInfoService.findListGraph(stoStoresInfo);

		// 保存报表信息
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setStoStoresInfoEntityList(goodsList);
		reportInfo.setStoresInfoEntity(stoStoresInfo);

		// 报表导出
		reportService.exportReportBar(reportInfo, ReportConstant.ReportType.GOODS_INVENTORY, request, response,
				null, msg);
		return null;
	}

	/**
	 * @author wh 历史库存页面导出
	 * @param stoInfoReportEntity
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "exportStoreInfoGraph")
	public String exportGoodsInventoryReport(StoInfoReportEntity stoInfoReportEntity, HttpServletRequest request,
			HttpServletResponse response) {

		// 查询条件：开始时间
		if (stoInfoReportEntity.getCreateTimeStart() != null) {
			stoInfoReportEntity.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(stoInfoReportEntity.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (stoInfoReportEntity.getCreateTimeEnd() != null) {
			stoInfoReportEntity.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(stoInfoReportEntity.getCreateTimeEnd())));
		}

		if (StringUtils.isNotBlank(stoInfoReportEntity.getFilterCondition())) {
			stoInfoReportEntity.setFilterCondition(stoInfoReportEntity.getFilterCondition()
					.replace(Constant.Punctuation.HALF_UNDERLINE, Constant.Punctuation.HYPHEN));
		}

		// 获取当前用户
		User user = UserUtils.getUser();

		// 判断权限，除顶级机构和平台，只显示当前机构数据
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {
			stoInfoReportEntity.setOfficeId(UserUtils.getUser().getOffice().getId());

		}


		// 查询处理
		List<StoInfoReportEntity> goodsList = stoStoresInfoService.getGraphData(stoInfoReportEntity);

		// 保存报表信息
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setStoInfoReportEntityList(goodsList);
		reportInfo.setStoInfoReportEntity(stoInfoReportEntity);

		// 报表导出
		reportService.exportReport(reportInfo, ReportConstant.ReportType.STORE_GRAPH, request, response, null, msg);
		return null;
	}

	/**
	 * 出库尾箱信息检索
	 * 
	 * @author chengshu
	 * @version 2015-12-17
	 */
	@RequestMapping(value = { "outTailBoxInfoReport", "" })
	public String outTailBoxList(StoReportInfo stoReportInfo, HttpServletRequest request, HttpServletResponse response,
			Model model,String outDateRange) {

		Page<StoBoxInfo> page = new Page<StoBoxInfo>(request, response);
		StoBoxInfo stoBoxInfo = new StoBoxInfo();

		// 尾箱
		stoBoxInfo.setBoxType(StoreConstant.BoxType.BOX_TAIL);
		// 出库日期
		Date outDate = DateUtils.addDate(new Date(), 0);
		// 出库时间区间开始
		stoBoxInfo.setOutDateStart(DateUtils.foramtSearchDate(DateUtils.getDateStart(outDate)));
		if(outDateRange!=null){
			outDate = DateUtils.addDate(new Date(), Integer.valueOf(outDateRange));
		}	
		// 出库时间区间结束
		stoBoxInfo.setOutDateEnd(DateUtils.foramtSearchDate(DateUtils.getDateEnd(outDate)));

		stoBoxInfo.setBoxStatus(BoxStatus.COFFER);
		// 执行查询
		stoBoxInfoService.findBoxList(page, stoBoxInfo);
		model.addAttribute("page", page);

		// 设置箱子数量
		stoReportInfo.setBoxCount(page.getList().size());

		return "modules/report/v01/store/outBoxInfoReport";
	}
	
	/**
	 * 重空库存报表检索
	 * 
	 * @author chengshu
	 * @version 2015-12-17
	 */
	@RequestMapping(value = { "emptyInventoryReport", "" })
	public String emptyInventoryReport(StoReportInfo stoReportInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		// 查询处理
		List<StoStoresInfo> emptyInventoryList = getEmptyInventoryList(stoReportInfo);

		// 保存到Session
		model.addAttribute("emptyInventoryList", emptyInventoryList);
		return "modules/report/v01/store/emptyInventoryReport";
	}
	
	/**
	 * 重空库存报表导出
	 * 
	 * @author chengshu
	 * @version 2015-12-17
	 */
	@RequestMapping(value = { "exportEmptyInventoryReport", "" })
	public String exportEmptyInventoryReport(StoReportInfo stoReportInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		// 执行查询
		List<StoStoresInfo> emptyInventoryList = getEmptyInventoryList(stoReportInfo);

		// 查询列表没有合计行，追加一个空行
		StoStoresInfo empty = new StoStoresInfo();
		emptyInventoryList.add(empty);

		// 保存数据库取得的重空库存信息
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setStoStoresInfoList(emptyInventoryList);

		// 报表导出
		reportService.exportReport(reportInfo, ReportConstant.ReportType.EMPTY_INVENTORY, request, response, null, msg);

		return null;
	}
	
	/**
	 * 
	 * @author chengshu
	 * @version 2015年10月28日
	 * 
	 * 重空变更报表列表
	 * @param stoReportInfo 页面信息
	 * @param request 页面请求信息
	 * @param response 页面响应信息
	 * @param model 页面Session
	 * @return
	 */
	@RequestMapping(value = "emptyChangeReport")
	public String emptyChangeReport(StoReportInfo stoReportInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		// 查询重空变更信息
		List<StoStoresHistory> historyList = getEmptyHistoryList(stoReportInfo);

		model.addAttribute("emptyHistoryList", historyList);

		return "modules/report/v01/store/emptyChangeReport";
	}
	
	/**
	 * 
	 * @author chengshu
	 * @version 2015年10月28日
	 * 
	 * 重空变更报表导出
	 * @param stoReportInfo 页面信息
	 * @param request 页面请求信息
	 * @param response 页面响应信息
	 * @param model 页面Session
	 * @param redirectAttributes 页面跳转
	 * @return
	 */
	@RequestMapping(value = "exportEmptyChangeReport")
	public String exportEmptyChangeReport(StoReportInfo stoReportInfo, HttpServletRequest request,
			HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {

		// 查询重空变更信息
		List<StoStoresHistory> historyList = getEmptyHistoryList(stoReportInfo);

		// 查询列表没有合计行，自行追加一个空行
		historyList.add(new StoStoresHistory());

		// 保存报表信息
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setStoEmptyHistoryList(historyList);

		// 报表导出
		reportService.exportReport(reportInfo, ReportConstant.ReportType.EMPTY_CHANGE, request, response, null, msg);

		return null;
	}
	
	/**
	 * @author chengshu
	 * @version 2015年12月28日
	 * 
	 * 查询重空库存信息
	 * @param stoReportInfo 页面信息
	 * @return 重空库存变更列表
	 */
	private List<StoStoresHistory> getEmptyHistoryList(StoReportInfo stoReportInfo) {
	
		// 设置查询条件
		StoStoresHistory stoHistory = new StoStoresHistory();
		stoHistory.setGoodsType(StoreConstant.GoodType.BLANK_BILL);
		stoHistory.setCreateTimeStart(stoReportInfo.getCreateTimeStart());
		stoHistory.setCreateTimeEnd(stoReportInfo.getCreateTimeEnd());
		List<StoStoresHistory> historyList = StoreCommonUtils.findStoStoresHistoryList(stoHistory);
	
		return historyList;
	}

	/**
	 * @author chengshu
	 * @version 2015年12月28日
	 * 
	 * 查询重空库存信息
	 * @param stoReportInfo 页面信息
	 * @return 重空库存列表
	 */
	private List<StoStoresInfo> getEmptyInventoryList(StoReportInfo stoReportInfo) {
	
		StoStoresInfo stoStoresInfo = new StoStoresInfo();
		// 所属金库
		if (null != stoReportInfo.getOffice() && StringUtils.isNoneBlank(stoReportInfo.getOffice().getId())) {
			stoStoresInfo.setOffice(stoReportInfo.getOffice());
		}
		stoStoresInfo.setCreateDate(new Date());
		stoStoresInfo.setGoodsType(StoreConstant.GoodType.BLANK_BILL);
		// 查询处理
		List<StoStoresInfo> emptyInventoryList = stoStoresInfoService.findStoStoresInfoList(stoStoresInfo);
	
		// 设置报表时间
		for (StoStoresInfo emptyInfo : emptyInventoryList) {
			emptyInfo.setTime(DateUtils.formatDate(new Date(), Constant.Dates.FORMATE_YYYY_MM_DD));
		}
	
		return emptyInventoryList;
	}
	
	/**
	 * @author wh 物品历史库存页面
	 * @param stoInfoReportEntity
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "toStoStoresInfoGraphPage")
	public String toStoStoresInfoGraphPage(StoInfoReportEntity stoInfoReportEntity, Model model) {
		
		if (stoInfoReportEntity == null) {
			stoInfoReportEntity = new StoInfoReportEntity();
			
		}
		
		model.addAttribute("stoInfoReportEntity", stoInfoReportEntity);
		return "/modules/report/v01/store/storeInfoHistoryGraph";
	}
	
	/**
	 * @author wh 物品库存页面
	 * @param stoReportInfo
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "toGoodsInventoryReportGraph")
	public String toGoodsInventoryReportGraph(StoReportInfo stoReportInfo, Model model) {

		if (stoReportInfo == null) {
			stoReportInfo = new StoReportInfo();

		}

		model.addAttribute("stoReportInfo", stoReportInfo);
		return "/modules/report/v01/store/goodsInventoryReportGraph";
	}

	/**
	 * @author wh 历史库存页面重新查询
	 * @param stoInfoReportEntity
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getGraphData")
	@ResponseBody
	public String getGraphData(StoInfoReportEntity stoInfoReportEntity, HttpServletRequest request) {
		
		// 获取当前用户
		User user = UserUtils.getUser();

		// 判断权限，除顶级机构和平台，只显示当前机构数据
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
				&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
				&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {
			stoInfoReportEntity.setOfficeId(UserUtils.getUser().getOffice().getId());

		}

		// 查询条件：开始时间
		if (stoInfoReportEntity.getCreateTimeStart() != null) {
			stoInfoReportEntity.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(stoInfoReportEntity.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (stoInfoReportEntity.getCreateTimeEnd() != null) {
			stoInfoReportEntity.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(stoInfoReportEntity.getCreateTimeEnd())));
		}

		if (StringUtils.isNotBlank(stoInfoReportEntity.getFilterCondition())) {
			stoInfoReportEntity.setFilterCondition(stoInfoReportEntity.getFilterCondition()
					.replace(Constant.Punctuation.HALF_UNDERLINE, Constant.Punctuation.HYPHEN));
		}
		
		Map<String, Object> jsonData = stoStoresInfoService.makeGraphData(stoInfoReportEntity);
		
		return gson.toJson(jsonData);
	}

	/**
	 * @author wh 库存页面重新查询
	 * @param stoReportInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getReportGraphData")
	@ResponseBody
	public String getReportGraphData(StoReportInfo stoReportInfo, HttpServletRequest request) {

		// 获取当前用户
		User user = UserUtils.getUser();

		// 判断权限，除顶级机构和平台，只显示当前机构数据
		if (!Constant.OfficeType.ROOT.equals(user.getOffice().getType())
						&& !Constant.OfficeType.CENTRAL_BANK.equals(user.getOffice().getType())
						&& !Constant.OfficeType.DIGITAL_PLATFORM.equals(user.getOffice().getType())) {
					stoReportInfo.setOffice(user.getOffice());
			}

		Map<String, Object> jsonData = stoStoresInfoService.makeReportGraphData(stoReportInfo);

		return gson.toJson(jsonData);
	}

}
