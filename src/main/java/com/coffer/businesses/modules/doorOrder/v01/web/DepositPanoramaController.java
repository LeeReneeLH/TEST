package com.coffer.businesses.modules.doorOrder.v01.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.clear.ClearCommonUtils;
import com.coffer.businesses.modules.clear.v03.entity.DenominationCtrl;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashAmount;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashDetail;
import com.coffer.businesses.modules.collection.v03.entity.CheckCashMain;
import com.coffer.businesses.modules.collection.v03.service.CheckCashService;
import com.coffer.businesses.modules.doorOrder.DoorOrderConstant;
import com.coffer.businesses.modules.doorOrder.v01.dao.ClearAddMoneyDao;
import com.coffer.businesses.modules.doorOrder.v01.entity.ClearAddMoney;
import com.coffer.businesses.modules.doorOrder.v01.service.ClearAddMoneyService;
import com.coffer.businesses.modules.doorOrder.v01.service.DepositPanoramaService;
import com.coffer.businesses.modules.report.ReportConstant;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderDetail;
import com.coffer.businesses.modules.weChat.v03.entity.DoorOrderInfo;
import com.coffer.businesses.modules.weChat.v03.service.DoorOrderInfoService;
import com.coffer.core.common.config.Global;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BaseService;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.coffer.tools.excel.templateex.ExcelExporterEx;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import freemarker.core.ParseException;

/**
 * 缴存全景Controller
 * 
 * @author lihe
 * @version 2020-05-26
 */
@Controller
@RequestMapping(value = "${adminPath}/doorOrder/v01/depositPanorama")
public class DepositPanoramaController extends BaseController {

	@Autowired
	private DepositPanoramaService depositPanoramaService;
	
	@Autowired
	private CheckCashService checkCashService;
	
    @Autowired
	private ClearAddMoneyService clearAddMoneyService;
    
	@Autowired
	private ClearAddMoneyDao clearAddMoneyDao;
	
	@Autowired
	private DoorOrderInfoService doorOrderInfoService;
	
	
	@ModelAttribute
	public DoorOrderInfo get(@RequestParam(required = false) String id) {
		DoorOrderInfo entity = null;
		if (StringUtils.isNotBlank(id)) {
			entity = depositPanoramaService.get(id);
		}
		if (entity == null) {
			entity = new DoorOrderInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("doorOrder:depositPanorama:view")
	@RequestMapping(value = { "list", "" })
	public String list(DoorOrderInfo depositPanorama, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		/** 初始化查询款袋状态为在用 */
		if (StringUtils.isBlank(depositPanorama.getStatus()) && !"0".equals(depositPanorama.getUninitFlag())) {
			depositPanorama.setStatus(DoorOrderConstant.Status.REGISTER);
		}
		if ("00".equals(depositPanorama.getStatus())) {
			depositPanorama.setStatus("");
		}
		Page<DoorOrderInfo> page = depositPanoramaService.findPage(new Page<DoorOrderInfo>(request, response),
				depositPanorama);
		model.addAttribute("depositPanorama", depositPanorama);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/depositPanorama/depositPanoramaList";
	}

	@RequiresPermissions("doorOrder:depositPanorama:view")
	@RequestMapping(value = "packageList")
	public String packageList(DoorOrderInfo depositPanorama, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<DoorOrderInfo> page = depositPanoramaService.packagePage(new Page<DoorOrderInfo>(request, response),
				depositPanorama);
		model.addAttribute("depositPanorama", depositPanorama);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/depositPanorama/packagePanoramaList";
	}

	/**
	 * 
	 * @author wanglin
	 * @version 2017年2月14日
	 * 
	 *          待清分只能查看
	 * @param depositPanorama
	 * @return
	 */
	@RequiresPermissions("doorOrder:depositPanorama:view")
	@RequestMapping(value = "depositPanoramaDetailForm")
	public String depositPanoramaDetailForm(DoorOrderInfo depositPanorama, Model model) {
		model.addAttribute("depositPanorama", depositPanorama);
		return "modules/doorOrder/v01/depositPanorama/depositPanoramaDetailForm";
	}

	/**
	 * 
	 * @author HuZhiYong
	 * @version 2020年06月03日
	 * 
	 *          拆箱查看页面
	 * @param orderId 拆箱id
	 * @return
	 */
	@RequiresPermissions("doorOrder:depositPanorama:view")
	@RequestMapping(value = "view")
	public String view(@RequestParam(required = false) String orderId , Model model) {
		
		CheckCashMain checkCashMain = null;
		if (StringUtils.isNotBlank(orderId)) {
			checkCashMain = checkCashService.get(orderId);
		}
		if (checkCashMain == null) {
			checkCashMain = new CheckCashMain();
		}
		checkCashMain.setId(orderId);
		List<CheckCashAmount> amountList = Lists.newArrayList();
		List<CheckCashDetail> amountDetailList = Lists.newArrayList();
		// 纸币面值明细
		List<CheckCashDetail> cnypdenDetailList = Lists.newArrayList();
		// 硬币面值明细
		List<CheckCashDetail> cnyhdenDetailList = Lists.newArrayList();
		// 面值（纸币）列表数据的取得
		checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList());
		// 面值（硬币）列表数据的取得
		checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList());
		if (checkCashMain.getId() != null) {
			// 每笔金额列表的设定
			amountList = checkCashService.findAmountList(checkCashMain);

			// 每笔金额面值列表的设定
			amountDetailList = checkCashService.findAmountDetailList(checkCashMain);
			CheckCashDetail checkCashDetail = new CheckCashDetail();
			checkCashDetail.setOutNo(checkCashMain.getOutNo());
			// 纸币面值明细
			checkCashDetail.setUnitId(Constant.Unit.piece);
			cnypdenDetailList = checkCashService.findList(checkCashDetail);
			// 硬币面值明细
			checkCashDetail.setUnitId(Constant.Unit.coin);
			cnyhdenDetailList = checkCashService.findList(checkCashDetail);
		}
		checkCashMain.setAmountList(amountList);
		checkCashMain.setAmountDetailList(amountDetailList);
		// 面值列表数据的取得
		DenominationCtrl denomCtrl = new DenominationCtrl();
		denomCtrl.setMoneyKeyName("denomination");
		denomCtrl.setColumnName1("countZhang");
		// 设置总金额
		denomCtrl.setTotalAmtName("detailAmount");
		checkCashMain.setDenominationList(ClearCommonUtils.getDenominationList(cnypdenDetailList, denomCtrl));
		checkCashMain.setCnyhdenList(ClearCommonUtils.getCnyhdenList(cnyhdenDetailList, denomCtrl));
		// 存款差错，实际存款金额
		checkCashMain = checkCashService.getDepositErrorByOutNo(checkCashMain);

		model.addAttribute("checkCashMain", checkCashMain);
		return "modules/doorOrder/v01/depositPanorama/panoramaCheckCashView";

	}
	
	
	/**
	 * Excel封包导出
	 * 
	 * @author HuZhiYong
	 * @version 2020-06-03
	 * @param DoorOrderInfo
	 * @return
	 */
	@RequestMapping(value = "packageExport")
	public String packageExport(DoorOrderInfo depositPanorama, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes, Model model)
			throws FileNotFoundException, ParseException, IOException {
		try {
		Locale locale = LocaleContextHolder.getLocale();
		// 设置标题信息
		Map<String, Object> titleMap = Maps.newHashMap();
		// 制表机构
		titleMap.put(ReportConstant.ReportExportData.OFFICE_NAME, UserUtils.getUser().getOffice().getName());
		// 制表时间
		titleMap.put(ReportConstant.ReportExportData.NOW_DATE, DateUtils.getDateTimeMin());
		// 查询
		List<DoorOrderInfo> list = depositPanoramaService.packageList(depositPanorama);
		// 列表为空
		DoorOrderInfo info = new DoorOrderInfo();
		info.setAmountExcel(new BigDecimal("0.00"));
		if (Collections3.isEmpty(list)) {
			list.add(new DoorOrderInfo());
		}else {
			for(DoorOrderInfo doorOrderInfo : list){
				//设置当类型是已收回或冲正 不显示拆箱单号
				if(DoorOrderConstant.Status.CONFIRM.equals(doorOrderInfo.getStatus())
						|| DoorOrderConstant.Status.REVERSE.equals(doorOrderInfo.getStatus())){
					doorOrderInfo.setOrderId(null);
				}
				//设置业务备注
				if(!DoorOrderConstant.MethodType.METHOD_WECHAT.equals(doorOrderInfo.getMethod())){
					doorOrderInfo.setRemarks("-");
				}
				// 设置状态类型
				String status = DictUtils.getDictLabel(doorOrderInfo.getStatus(),
				DoorOrderConstant.ClDictType.SYS_CLEAR_TYPE, "");
				doorOrderInfo.setStatus(status);	
				// 设置状态类型
				String method = DictUtils.getDictLabel(doorOrderInfo.getMethod(),
				DoorOrderConstant.ClDictType.DOOR_METHOD_TYPE, "");
				doorOrderInfo.setMethod(method);
				//设置金额
				doorOrderInfo.setAmountExcel(new BigDecimal(doorOrderInfo.getAmount()));
				//设置登记人
				doorOrderInfo.setCreateNameExcel(doorOrderInfo.getCreateName());
				//设置登记时间
				doorOrderInfo.setCreateDateExcel(DateUtils.formatDateTime(doorOrderInfo.getCreateDate()));
				info.setAmountExcel(info.getAmountExcel().add(doorOrderInfo.getAmountExcel()));
			}
		}
		//添加合计
		info.setDoorName("合计");
		list.add(info);
		// 模板文件名
		String fileName = msg.getMessage("report.packageList.excel", null, locale)+".xls";
		
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("report.packageList.excel", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorOrderInfo.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, list);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"封包缴存" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}
	catch (Exception e){
		return packageList(depositPanorama, request, response, model);
	}
		return null;
	}
	

	/**
	 * 存款流水列表
	 * 
	 * @author ZXK
	 * @version 2020-6-2
	 * @param clearAddMoney
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "depositSerial")
	public String getDepositSerialList(ClearAddMoney clearAddMoney, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<ClearAddMoney> page = clearAddMoneyService.getDepositSerialList(new Page<ClearAddMoney>(request, response),
				clearAddMoney);
		model.addAttribute("page", page);
		return "modules/doorOrder/v01/depositPanorama/depositSerialList";
	}
	
	/**
	 * 凭条信息
	 * 
	 * @author ZXK
	 * @version 2020-6-3
	 * @param tickrtapeId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "tickrtapeInfo")
	public String tickrtapeInfo(@RequestParam(required = false) String tickrtapeId, Model model){
		DoorOrderDetail doorOrderDetail = new DoorOrderDetail();
		doorOrderDetail.setTickertape(tickrtapeId);
		DoorOrderDetail tickrtapeInfo = doorOrderInfoService.getTickrtapeInfo(tickrtapeId);
		model.addAttribute("tickrtapeInfo", tickrtapeInfo);
		return "modules/doorOrder/v01/depositPanorama/tickrtapeInfo";
	}
	
	
	/**
	 * 存款流水列表导出
	 * 
	 * @author ZXK
	 * @version 2020年6月4日
	 */
	@RequestMapping(value = "depositSerialExport")
	public void depositSerialExport(ClearAddMoney clearAddMoney, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException, ParseException, IOException {
		// 查询条件： 开始时间
		if (clearAddMoney.getCreateTimeStart() != null) {
			clearAddMoney.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(clearAddMoney.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (clearAddMoney.getCreateTimeEnd() != null) {
			clearAddMoney.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(clearAddMoney.getCreateTimeEnd())));
		}
		/* end */
		List<ClearAddMoney> serialList = clearAddMoneyDao.getDepositSerialList(clearAddMoney);
		// 列表为空
		if (Collections3.isEmpty(serialList)) {
			serialList.add(new ClearAddMoney());
		}else{
			for (ClearAddMoney serial : serialList) {
				//serial.setType(serial.getType().equals(DoorOrderConstant.ClearStatus.DEPOSIT) ? "存款" : "清机");
				serial.setType(DictUtils.getDictLabel(serial.getType(), "CLEAR_ADD_MONEY_TYPE", " "));
			}
		}
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		// 模板文件名 /差错情况.xls
		String fileName = msg.getMessage("door.panorama.deposit", null, locale) + ".xls";
		// 取得模板路径
		String templatePath = Global.getConfig("export.template.path");
		List<Map<String, Object>> paramList = Lists.newArrayList();
		Map<String, Object> sheetMap = Maps.newHashMap();
		// sheet标题
		sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY,
				msg.getMessage("door.panorama.deposit", null, locale));
		// 设置类名
		sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, ClearAddMoney.class.getName());
		// 设置集合
		sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, serialList);
		paramList.add(sheetMap);
		ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
		// 导出excel
		exportEx.createWorkBook(request, response, templatePath, fileName,
				"存款流水" + DateUtils.formatDate(new Date(), "yyyy-MM-dd") + ".xls");
	}
	
	/**
	 * 
	 * Title: exportDepositPanorama
	 * <p>Description:设备缴存明细导出 </p>
	 * @author:     HaoShijie
	 * @param depositPanorama
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @param model
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "exportDepositPanorama")
	public String exportDepositPanorama(DoorOrderInfo depositPanorama, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
		try {
			// 当前登陆人机构类型
			if (UserUtils.getUser().getOffice().getType().equals(Constant.OfficeType.CLEAR_CENTER)) {
				// 按清分中心过滤
				depositPanorama.getSqlMap().put("dsf", BaseService.dataScopeFilter(depositPanorama.getCurrentUser(), "o", null));
			} else {
				// 按门店过滤
				depositPanorama.getSqlMap().put("dsf", BaseService.dataScopeFilter(depositPanorama.getCurrentUser(), "o1", null));
			}
			// 查询条件： 开始时间
			if (depositPanorama.getCreateTimeStart() != null) {
				depositPanorama.setSearchDateStart(
						DateUtils.foramtSearchDate(DateUtils.getDateStart(depositPanorama.getCreateTimeStart())));
			}
			// 查询条件： 结束时间
			if (depositPanorama.getCreateTimeEnd() != null) {
				depositPanorama.setSearchDateEnd(
						DateUtils.foramtSearchDate(DateUtils.getDateEnd(depositPanorama.getCreateTimeEnd())));
			}
			
			if ("00".equals(depositPanorama.getStatus())) {
				depositPanorama.setStatus("");
			}
			List<DoorOrderInfo> depositPanoramaList = depositPanoramaService.findList(depositPanorama);
			// 初始化
			Integer paperCount = 0;
			BigDecimal paperAmount = new BigDecimal(0);
			BigDecimal forceAmount = new BigDecimal(0);
			BigDecimal otherAmount = new BigDecimal(0);
			BigDecimal amount = new BigDecimal(0);
			DoorOrderInfo  doorOrderInfoSum = new DoorOrderInfo();
			doorOrderInfoSum.setPaperCount(0);
			if (Collections3.isEmpty(depositPanoramaList)) {
				depositPanoramaList.add(new DoorOrderInfo());
			} else {
				for (DoorOrderInfo doorOrderInfo : depositPanoramaList) {
					doorOrderInfo.setAmountExcel(new BigDecimal(doorOrderInfo.getAmount()));
					doorOrderInfo.setCreateDateExcel(DateUtils.formatDate(doorOrderInfo.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
					doorOrderInfo.setUpdateDateExcel(DateUtils.formatDate(doorOrderInfo.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
					doorOrderInfo.setUpdateNameExcel(doorOrderInfo.getUpdateName());
					doorOrderInfo.setStatus(DictUtils.getDictLabel(doorOrderInfo.getStatus(), "sys_clear_type", " "));
					paperCount = paperCount + doorOrderInfo.getPaperCount();
					paperAmount = paperAmount.add(doorOrderInfo.getPaperAmount());
					forceAmount = forceAmount.add(doorOrderInfo.getForceAmount());
					otherAmount = otherAmount.add(doorOrderInfo.getOtherAmount());
					amount = amount.add(doorOrderInfo.getAmountExcel());
				}
			}
			doorOrderInfoSum.setDoorName("合计");
			doorOrderInfoSum.setPaperCount(paperCount);
			doorOrderInfoSum.setPaperAmount(paperAmount);
			doorOrderInfoSum.setForceAmount(forceAmount);
			doorOrderInfoSum.setOtherAmount(otherAmount);
			doorOrderInfoSum.setAmountExcel(amount);
			depositPanoramaList.add(doorOrderInfoSum);
			// 国际化
			Locale locale = LocaleContextHolder.getLocale();
			// 模板文件名 /设备缴存.xls
			String fileName = msg.getMessage("door.panorama.excel.fileName", null, locale);
			// 文件名分割后追加时间
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			String[] split = fileName.split("\\.");
			String truefileName = split[0] + date + '.' + split[1];
			String templatePath = Global.getConfig("export.template.path");
			List<Map<String, Object>> paramList = Lists.newArrayList();
			Map<String, Object> sheetMap = Maps.newHashMap();
			// sheet标题
			sheetMap.put(ExcelExporterEx.SHEET_NAME_MAP_KEY, msg.getMessage("door.panorama.excel.title", null, locale));
			// 设置类名
			sheetMap.put(ExcelExporterEx.SHEET_DATA_ENTITY_CLASS_NAME_KEY, DoorOrderInfo.class.getName());
			// 设置集合
			sheetMap.put(ExcelExporterEx.SHEET_DATA_LIST_MAP_KEY, depositPanoramaList);
			paramList.add(sheetMap);
			ExcelExporterEx exportEx = new ExcelExporterEx(paramList);
			// 导出excel
			exportEx.createWorkBook(request, response, templatePath, fileName, truefileName);
		} catch (Exception e) {
			return list(depositPanorama, request, response, model);
		}
		return null;
	}

}