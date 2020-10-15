package com.coffer.businesses.modules.allocation.v01.web;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v01.entity.AllAllocateInfo;
import com.coffer.businesses.modules.allocation.v01.entity.AllHandoverDetail;
import com.coffer.businesses.modules.allocation.v01.service.AllocationService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v01.service.StoEscortInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "${adminPath}/allocation/v01/cashHandin")
public class CashHandinController extends BaseController {
	@Autowired
	private AllocationService allocationService;
	
	@Autowired
	private StoEscortInfoService stoEscortInfoService;

	/**
	 * @author wangbaozhong
	 * @version 2015年9月22日
	 * 
	 *          根据流水号取得现金上缴信息
	 * @param allId
	 *            流水号
	 * @return 现金上缴信息
	 */
	@ModelAttribute
	public AllAllocateInfo get(@RequestParam(required = false) String allId) {

		AllAllocateInfo entity = null;
		if (StringUtils.isNoneBlank(allId)) {
			entity = allocationService.getAllocate(allId);
		}
		if (entity == null) {
			entity = new AllAllocateInfo();
		}
		return entity;
	}

	/**
	 * @author liuyaowen
	 * @version 2017年7月7日
	 * 
	 *          根据查询条件，查询现金上缴信息
	 * @param allocateInfo
	 *            现金上缴信息
	 * @param request
	 *            页面请求信息
	 * @param response
	 *            页面应答信息
	 * @param model
	 *            页面Session信息
	 * @return 现金上缴信息列表页面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(AllAllocateInfo allocateInfo, HttpServletRequest request, HttpServletResponse response,
			Model model) {

		// 设置业务种别(现金上缴)
		List<String> businessTypeList = Lists.newArrayList();
		businessTypeList.add(AllocationConstant.BusinessType.OutBank_Cash_HandIn);
		allocateInfo.setBusinessTypes(businessTypeList);

		// 设置交接状态
		String sStatus = allocateInfo.getStatus();
		if (StringUtils.isNotBlank(sStatus)) {
			List<String> statusList = Lists.newArrayList();
			statusList.add(allocateInfo.getStatus());
			allocateInfo.setStatuses(statusList);
		}
		//------------- 增加分组，移除机构设置   XL 2018-12-29 ------------ BEGIN
		/*
		 * // 如果不是系统管理员，设置用户的机构号，只能查询当前机构 if
		 * (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().
		 * getUserType())) { // 如果是网点，设置用户的机构号，只能查询当前机构 if
		 * (AllocationConstant.SysUserType.BANK_OUTLETS_MANAGER.equals(UserUtils
		 * .getUser().getUserType()) ||
		 * AllocationConstant.SysUserType.BANK_OUTLETS_OPT.equals(UserUtils.
		 * getUser().getUserType())) {
		 * allocateInfo.setrOffice(UserUtils.getUser().getOffice()); } else { //
		 * 否则查询本金库 allocateInfo.setaOffice(UserUtils.getUser().getOffice()); } }
		 */
		//------------- 增加分组，移除机构设置   XL 2018-12-29 ------------ END
		// 查询现金上缴信息
		Page<AllAllocateInfo> page = allocationService.findHandinCash(new Page<AllAllocateInfo>(request, response),
				allocateInfo);
		model.addAttribute("page", page);
		return "modules/allocation/v01/out/order/cashhandin/cashHandinList";
	}

	/**
	 * @author liuyaowen
	 * @version 2017年7月7日
	 * 
	 *          根据查询条件，查询现金上缴详细信息
	 * @param allocateInfo
	 *            上缴信息
	 * @param model
	 *            页面Session信息
	 * @return 现金上缴详细画面
	 */
	@RequestMapping(value = "form")
	public String form(AllAllocateInfo allocateInfo, Model model) {
		String officeType = UserUtils.getUser().getOffice().getType();
		String escortName = "";
		// ADD-START  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		// 交接人员id信息
		List<String> handoverIdList = Lists.newLinkedList();
		// ADD-END  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		if (!"".equals(allocateInfo.getStoreHandoverId())) {
			List<AllHandoverDetail> allHandoverDetailList = allocationService
					.findDetailByHandoverId(allocateInfo.getStoreHandoverId());
			for (AllHandoverDetail detail : allHandoverDetailList) {
				if (AllocationConstant.OperationType.TURN_OVER.equals(detail.getOperationType())) {
					// ADD-START  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
					// 添加交接人员id
					handoverIdList.add(detail.getEscortId());
					// ADD-END  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
					if (escortName.isEmpty()) {
						escortName = detail.getEscortName();
					} else {
						escortName = escortName + "," + detail.getEscortName();
					}
				}
			}
		}
		// ADD-START  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		model.addAttribute("handoverIdList",handoverIdList);
		// ADD-END  原因：交接人员详情显示  add by SonyYuanYang  2018/04/04
		model.addAttribute("officeType", officeType);
		model.addAttribute("escortName", escortName);
		model.addAttribute("allAllocateInfo", allocateInfo);
		return "modules/allocation/v01/out/box/boxInDetail";
	}

	/**
	 * @author wangbaozhong
	 * @version 2015年9月22日
	 * @return 调拨信息列表页面
	 */
	@RequestMapping(value = "back")
	public String back(AllAllocateInfo allocateInfo, RedirectAttributes redirectAttributes) {
		if (AllocationConstant.PageType.StoreView.equals(allocateInfo.getPageType())) {
			return "redirect:" + adminPath + "/allocation/v01/boxHandover/handin";
		}
		return "redirect:" + adminPath + "/allocation/v01/cashHandin/list";
	}

	/**
	 * 跳转到撤销预约画面
	 * 
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "toCancel")
	public String toCancel(String allId, Model model) {
		AllAllocateInfo allocate = allocationService.getAllocate(allId);
		AllAllocateInfo cancelParam = new AllAllocateInfo();
		cancelParam.setStrUpdateDate(allocate.getStrUpdateDate());
		cancelParam.setAllId(allId);
		model.addAttribute("cancelParam", cancelParam);
		return "modules/allocation/v01/out/order/cashhandin/cancelCommonForm";
	}

	/**
	 * 撤销预约
	 * 
	 * @param cancelParam
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "cancel", produces = "application/json; charset=utf-8")
	@ResponseBody
	public String cancel(AllAllocateInfo allAllocateInfo, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {

		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		// 取得撤回原因
		String cancelReason = request.getParameter("cancelReason");
		allAllocateInfo.setCancelReason(cancelReason);
		try {
			// 一致性校验
			allocationService.checkVersion(allAllocateInfo);
			// 执行撤回操作
			allocationService.handInCancel(allAllocateInfo);
		} catch (BusinessException be) {
			message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);
			return message;
		}
		return "success";
	}

	/**
	 * 跳转到显示撤回原因画面
	 * 
	 * @param allId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "displayCancelReason")
	public String displayCancelReason(AllAllocateInfo allAllocateInfo, Model model) {
		allAllocateInfo.setDisplayCancelReasonFlag(true);
		model.addAttribute("cancelParam", allAllocateInfo);
		return "modules/allocation/v01/out/order/cashhandin/cancelCommonForm";
	}
	
	/**
	 * 交接人员详情显示
	 *  
	 * @author SongYuanYang
	 * @version 2018年4月4日
	 * 
	 * @param allAllocateInfo
	 * @param model
	 * @return 详情页面
	 */
	@RequestMapping(value = "getPersonInfo")
	public String getPersonInfo(AllAllocateInfo allAllocateInfo,
			@RequestParam(value = "personList", required = false) List<String> personList, Model model) {
		
		List<StoEscortInfo> stoEscortList = Lists.newArrayList();

		for (String stoEscortId : personList) {
			stoEscortList.add(stoEscortInfoService.findByEscortId(stoEscortId));
		}

		allAllocateInfo = allocationService.get(allAllocateInfo.getAllId());

		model.addAttribute("stoEscortList", stoEscortList);
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		return "modules/store/v01/stoEscortInfo/dotHandoverPersonInfo";
	}

	/**
	 * 打印商行交款明细
	 * 
	 * @author qph
	 * @version 2018年08月07日
	 * 
	 * 
	 * @param inNo
	 *            业务流水
	 * @return 列表页面
	 */
	@RequestMapping(value = "/printDetail")
	public String printDetail(@RequestParam(value = "allID", required = true) String allID, Model model) {

		AllAllocateInfo allAllocateInfo = allocationService.getAllocate(allID);

		int handoutMan = 1;
		int receiveMan = 1;

		// 获取交接人员
		List<AllHandoverDetail> handoverList = allAllocateInfo.getStoreHandover().getDetailList();
		for (AllHandoverDetail allHandoverInfo : handoverList) {
			StoEscortInfo stoEscort = StoreCommonUtils.getEscortById(allHandoverInfo.getEscortId());
			if (stoEscort != null) {
				if (("90").equals(stoEscort.getEscortType())) {
					if (allAllocateInfo.getBusinessType().equals("30")) {
						model.addAttribute("handoutMan" + handoutMan, stoEscort.getEscortName());
						handoutMan++;
					} else {
						model.addAttribute("receiveMan" + receiveMan, stoEscort.getEscortName());
						receiveMan++;
					}
				} else {
					if (allAllocateInfo.getBusinessType().equals("30")) {
						model.addAttribute("receiveMan" + receiveMan, stoEscort.getEscortName());
						receiveMan++;
					} else {
						model.addAttribute("handoutMan" + handoutMan, stoEscort.getEscortName());
						handoutMan++;
					}
				}
			}

		}

		// 获取款箱状态
		if (AllocationConstant.BusinessType.OutBank_Cash_HandIn.equals(allAllocateInfo.getBusinessType())) {
			model.addAttribute("boxType", "入库");
		} else if (AllocationConstant.BusinessType.OutBank_Cash_Reservation.equals(allAllocateInfo.getBusinessType())) {
			model.addAttribute("boxType", "出库");
		}
		int blankcount = 0;
		// 获取最大页数
		int size = 0;
		// 计算行数
		int count = allAllocateInfo.getAllDetailList().size();
		if (count % 10 == 0) {
			size = count / 10;
		} else {
			size = count / 10 + 1;
		}

		if (count < 10) {
			blankcount = count;
		} else {
			blankcount = count - 10;
		}

		// 获取当前登录人的信息
		User user = UserUtils.getUser();
		model.addAttribute("user", user.getOffice().getName());
		model.addAttribute("allAllocateInfo", allAllocateInfo);
		model.addAttribute("allDetailList", allAllocateInfo.getAllDetailList());
		// 箱袋数量
		model.addAttribute("boxNum", allAllocateInfo.getAllDetailList().size());
		// 转成中文大写
		String strBigAmount = NumToRMB.changeToBig(count);
		String strm = strBigAmount.substring(0, strBigAmount.length() - 2);
		model.addAttribute("strBigAmount", strm);
		model.addAttribute("size", size);
		model.addAttribute("blankcount", blankcount);
		return "modules/allocation/v01/out/box/printBoxDetail";
	}

}
