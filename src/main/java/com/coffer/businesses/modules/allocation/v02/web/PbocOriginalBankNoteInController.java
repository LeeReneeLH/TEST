package com.coffer.businesses.modules.allocation.v02.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.businesses.modules.store.v02.entity.StoOriginalBanknote;

/**
 * 人行原封新券出入库Controller
 * @author wangbaozhong
 * @version 2016-07-11
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocOriginalBankNoteIn")
public class PbocOriginalBankNoteInController extends BaseController {
	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;
	
	@ModelAttribute
	public PbocAllAllocateInfo get(@RequestParam(required=false) String allId) {
		PbocAllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)){
			entity = pbocAllAllocateInfoService.get(allId);
		}
		if (entity == null){
			entity = new PbocAllAllocateInfo();
		}
		return entity;
	}
	
	/**
	 * 跳转至列表页面
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param pbocAllAllocateInfo 查询条件
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = {"list", ""})
	public String list(PbocAllAllocateInfo pbocAllAllocateInfo, 
			@RequestParam(value="bInitFlag", required=false) Boolean bInitFlag, 
			HttpServletRequest request, HttpServletResponse response, Model model) {
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
			// 查询当前机构
			pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
		}
		
		// 查询条件：出库  开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setScanGateDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：出库 结束时间
		if (pbocAllAllocateInfo.getCreateTimeEnd() != null) {
			pbocAllAllocateInfo.setScanGateDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getCreateTimeEnd())));
		}
		// 初始状态设定
		if (bInitFlag != null && bInitFlag == true) {
			// 初始页面显待交接
			pbocAllAllocateInfo.setStatus(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS);
		}
		//设置业务类型 原封新券入库
		pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_ORIGINAL_BANKNOTE_IN_STORE);
		
		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo); 
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		
		return "modules/allocation/v02/originalBanknoteAllocated/pbocOriginalBanknoteInList";
	}

	/**
	 * 页面跳转
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param pbocAllAllocateInfo 申请信息
	 * @param model
	 * @return 跳转页面（查看页面或编辑页面）
	 */
	@RequestMapping(value = "form")
	public String form(PbocAllAllocateInfo pbocAllAllocateInfo, Model model) {
		
		for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
		}
		
		// 查看时设置申请总金额和审批总金额大写
		pbocAllAllocateInfo.setRegisterAmount(pbocAllAllocateInfo.getRegisterAmount() == null ? 0d : pbocAllAllocateInfo.getRegisterAmount());
		pbocAllAllocateInfo.setConfirmAmount(pbocAllAllocateInfo.getConfirmAmount() == null ? 0d : pbocAllAllocateInfo.getConfirmAmount());
		pbocAllAllocateInfo.setRegisterAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getRegisterAmount()));
		pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));
		
		String currentOfficeId = UserUtils.getUser().getOffice().getId();
		
		if (pbocAllAllocateInfo.getPbocAllAllocateDetailList() != null) {
			// 查询出入库物品所处库区位置
			for (PbocAllAllocateDetail allcateDetail : pbocAllAllocateInfo.getPbocAllAllocateDetailList()) {
				StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
				stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());
				if (AllocationConstant.InOutCoffer.IN.equals(pbocAllAllocateInfo.getInoutType())) {
					stoGoodsLocationInfo.setInStoreAllId(pbocAllAllocateInfo.getAllId());
				} else {
					stoGoodsLocationInfo.setOutStoreAllId(pbocAllAllocateInfo.getAllId());
				}
				// 获取原封箱信息
				StoOriginalBanknote originalBankNote = StoreCommonUtils.getStoOriginalBanknoteByBoxId(allcateDetail.getRfid(), currentOfficeId);
				allcateDetail.setStoOriginalBanknote(originalBankNote);
				// 获取物品所在库区信息
				stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
				allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
			}
		}
		
		
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		
		return "modules/allocation/v02/originalBanknoteAllocated/pbocInstoreDetail";
	}

	
	
	/**
	 * 批量修改业务状态
	 * @author WangBaozhong
	 * @version 2016年6月1日
	 * 
	 *  
	 * @param allIds	流水单号列表
	 * @param targetStatus	目标状态
	 * @param model	
	 * @param request
	 * @param response
	 * @return 列表页面
	 */
	@RequestMapping(value="/batchOperation")
	public String batchOperation(@RequestParam(value="allIds", required = true) String allIds, 
			@RequestParam(value="targetStatus", required = false) String targetStatus, 
			Model model, HttpServletRequest request, HttpServletResponse response){
		
		String[] allIdArray = allIds.split(Constant.Punctuation.COMMA);
		
		List<PbocAllAllocateInfo> printDataList = Lists.newArrayList();
		String currentOfficeId = UserUtils.getUser().getOffice().getId();
		for (String allId : allIdArray) {
			PbocAllAllocateInfo tempAllocatedInfo = pbocAllAllocateInfoService.get(allId);
			
			if (tempAllocatedInfo.getPbocAllAllocateDetailList() != null) {
				// 查询出入库物品所处库区位置
				for (PbocAllAllocateDetail allcateDetail : tempAllocatedInfo.getPbocAllAllocateDetailList()) {
					StoGoodsLocationInfo stoGoodsLocationInfo = new StoGoodsLocationInfo();
					stoGoodsLocationInfo.setRfid(allcateDetail.getRfid());
					if (AllocationConstant.InOutCoffer.IN.equals(tempAllocatedInfo.getInoutType())) {
						stoGoodsLocationInfo.setInStoreAllId(tempAllocatedInfo.getAllId());
					} else {
						stoGoodsLocationInfo.setOutStoreAllId(tempAllocatedInfo.getAllId());
					}
					// 获取原封箱信息
					StoOriginalBanknote originalBankNote = StoreCommonUtils.getStoOriginalBanknoteByBoxId(allcateDetail.getRfid(), currentOfficeId);
					allcateDetail.setStoOriginalBanknote(originalBankNote);
					// 获取物品所在库区信息
					stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
					allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
				}
			}
			
			printDataList.add(tempAllocatedInfo);
		}
		
		model.addAttribute("printDataList", printDataList);
		
		return "modules/allocation/v02/originalBanknoteAllocated/printInStoreDetail";
	}
	
	
	/**
	 * 返回到列表页面
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param pbocAllAllocateInfo
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(PbocAllAllocateInfo pbocAllAllocateInfo, Model model, HttpServletRequest request, HttpServletResponse response){
		PbocAllAllocateInfo templlAllocateInfo = new PbocAllAllocateInfo();
		templlAllocateInfo.setPageType(pbocAllAllocateInfo.getPageType());
		return list(templlAllocateInfo, false, request, response, model);
	}

}