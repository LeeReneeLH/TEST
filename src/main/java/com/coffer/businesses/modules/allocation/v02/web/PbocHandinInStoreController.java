package com.coffer.businesses.modules.allocation.v02.web;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.allocation.AllocationCommonUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.allocation.v02.entity.AllAllocateGoodsAreaDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateDetail;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateInfo;
import com.coffer.businesses.modules.allocation.v02.entity.PbocAllAllocateItem;
import com.coffer.businesses.modules.allocation.v02.service.PbocAllAllocateInfoService;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.v01.entity.StoGoodsLocationInfo;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.BusinessException;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.NumToRMB;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;

/**
 * 发行基金入库管理Controller
 * 
 * @author yuxixuan
 * @version 2016-06-06
 */
@Controller
@RequestMapping(value = "${adminPath}/allocation/v02/pbocHandinInStore")
public class PbocHandinInStoreController extends BaseController {

	@Autowired
	private PbocAllAllocateInfoService pbocAllAllocateInfoService;

	/**
	 * 取得详细信息
	 * 
	 * @param allId
	 * @return
	 */
	@ModelAttribute
	public PbocAllAllocateInfo get(@RequestParam(required = false) String allId) {
		PbocAllAllocateInfo entity = null;
		if (StringUtils.isNotBlank(allId)) {
			entity = pbocAllAllocateInfoService.get(allId);
		}
		if (entity == null) {
			entity = new PbocAllAllocateInfo();
		}
		return entity;
	}

	/**
	 * 跳转至列表页面
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            查询条件
	 * @param request
	 * @param response
	 * @param model
	 * @return 列表页面
	 */
	@RequestMapping(value = { "list", "" })
	public String list(PbocAllAllocateInfo pbocAllAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, Model model) {

		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType())) {
			// ADD-START  原因：金融平台用户登录时查询业务类型为申请上缴、代理上缴内容  add by SonyYuanYang  2018/03/17
			if (Constant.OfficeType.DIGITAL_PLATFORM
					.equals(pbocAllAllocateInfo.getCurrentUser().getOffice().getType())) {
				List<String> businessTypeList = Lists.newArrayList();
				// 金融平台用户登录时查询业务类型为申请上缴、代理上缴内容
				businessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN);
				businessTypeList.add(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
				pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
				// ADD-END  原因：金融平台用户登录时查询业务类型为申请上缴、代理上缴内容  add by SonyYuanYang  2018/03/17
			} else if (AllocationConstant.SysUserType.CLEARING_CENTER_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CLEARING_CENTER_OPT.equals(UserUtils.getUser().getUserType())) {
				pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice().getParent());
				// 清分中心只显示代理上缴
				pbocAllAllocateInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
			} else {
				// 查询接收机构是当前人民银行的数据
				pbocAllAllocateInfo.setAoffice(UserUtils.getUser().getOffice());
				List<String> businessTypeList = Lists.newArrayList();
				// 初始列表页面显示 业务类型为 申请上缴和代理上缴的数据
				businessTypeList.add(AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN);
				businessTypeList.add(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
				pbocAllAllocateInfo.setBusinessTypeList(businessTypeList);
				// 管库员 查看时 只显示 待入库和待交接任务
				if ((AllocationConstant.SysUserType.CENTRAL_STORE_MANAGER_OPT.equals(UserUtils.getUser().getUserType())
						|| AllocationConstant.SysUserType.CENTRAL_RECOUNT_MANAGER_OPT
								.equals(UserUtils.getUser().getUserType())
						|| AllocationConstant.SysUserType.CENTRAL_ALLOCATE_OPT
								.equals(UserUtils.getUser().getUserType()))
						&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
					List<String> statusList = Lists.newArrayList();
					statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS);
					statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
					pbocAllAllocateInfo.setStatuses(statusList);
				}
				// 人行管理员 查看时 显示 待入库 待交接 完成
				if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
						&& StringUtils.isBlank(pbocAllAllocateInfo.getStatus())) {
					List<String> statusList = Lists.newArrayList();
					statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_HANDOVER_STATUS);
					statusList.add(AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS);
					statusList.add(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
					pbocAllAllocateInfo.setStatuses(statusList);
				}
			}
			// 不显示驳回数据
			pbocAllAllocateInfo.setShowRejectData(AllocationConstant.ShowRejectData.SHOW_NONE);
		}

		// 查询条件：入库开始时间
		if (pbocAllAllocateInfo.getCreateTimeStart() != null) {
			pbocAllAllocateInfo.setScanGateDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(pbocAllAllocateInfo.getCreateTimeStart())));
		}
		// 查询条件：入库结束时间
		if (pbocAllAllocateInfo.getCreateTimeEnd() != null) {
			pbocAllAllocateInfo.setScanGateDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(pbocAllAllocateInfo.getCreateTimeEnd())));
		}

		Page<PbocAllAllocateInfo> page = pbocAllAllocateInfoService
				.findPage(new Page<PbocAllAllocateInfo>(request, response), pbocAllAllocateInfo);
		model.addAttribute("page", page);
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);
		return "modules/allocation/v02/handinInStore/pbocHandinInStoreList";
	}

	/**
	 * 页面跳转
	 * 
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 * 
	 * @param pbocAllAllocateInfo
	 *            申请信息
	 * @param model
	 * @return 跳转页面（查看页面或编辑页面）
	 */
	@RequestMapping(value = "form")
	public String form(PbocAllAllocateInfo pbocAllAllocateInfo, Model model) {

		for (PbocAllAllocateItem item : pbocAllAllocateInfo.getPbocAllAllocateItemList()) {
			item.setGoodsName(StoreCommonUtils.getGoodsName(item.getGoodsId()));
		}

		// 查看时设置申请总金额和审批总金额大写
		pbocAllAllocateInfo.setRegisterAmount(
				pbocAllAllocateInfo.getRegisterAmount() == null ? 0d : pbocAllAllocateInfo.getRegisterAmount());
		pbocAllAllocateInfo.setConfirmAmount(
				pbocAllAllocateInfo.getConfirmAmount() == null ? 0d : pbocAllAllocateInfo.getConfirmAmount());
		pbocAllAllocateInfo.setRegisterAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getRegisterAmount()));
		pbocAllAllocateInfo.setConfirmAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getConfirmAmount()));

		if (pbocAllAllocateInfo.getInstoreAmount() != null) {
			pbocAllAllocateInfo.setInstoreAmountBig(NumToRMB.changeToBig(pbocAllAllocateInfo.getInstoreAmount()));
		}

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
				// 原封券以外，则截取rfid前八位
				allcateDetail.setRfid(StringUtils.left(allcateDetail.getRfid(), 8));

				stoGoodsLocationInfo = StoreCommonUtils.getGoodsLocationInfoByAllIDAndRfid(stoGoodsLocationInfo);
				allcateDetail.setGoodsLocationInfo(stoGoodsLocationInfo);
			}
		}
		List<String> allIdList = Lists.newArrayList();
		allIdList.add(pbocAllAllocateInfo.getAllId());
		List<PbocAllAllocateInfo> printDataList = pbocAllAllocateInfoService.getQuotaGoodsAreaInfo(allIdList);

		for (PbocAllAllocateInfo allocateInfo : printDataList) {
			for (PbocAllAllocateItem item : allocateInfo.getPbocAllAllocateItemList()) {
				for (AllAllocateGoodsAreaDetail areaDetail : item.getGoodsAreaDetailList()) {
					// 原封券以外，则截取rfid前八位
					areaDetail.getGoodsLocationInfo()
							.setRfid(StringUtils.left(areaDetail.getGoodsLocationInfo().getRfid(), 8));
				}
			}
		}
		model.addAttribute("printDataList", printDataList);
		pbocAllAllocateInfo.setPageType(AllocationConstant.PageType.StoreHandinView);
		// 清分中心查看时，显示和商行一样的数据
		if (AllocationConstant.SysUserType.CLEARING_CENTER_MANAGER.equals(UserUtils.getUser().getUserType())
				|| AllocationConstant.SysUserType.CLEARING_CENTER_OPT.equals(UserUtils.getUser().getUserType())) {
			pbocAllAllocateInfo.setPageType(AllocationConstant.PageType.BussnessApplicationView);
		}

		// add-start 交接人员照片显示 add by yanbingxu 2018/04/03
		AllocationCommonUtils.pbocHandoverFilter(pbocAllAllocateInfo, model);
		// add-end
		model.addAttribute("pbocAllAllocateInfo", pbocAllAllocateInfo);

		return "modules/allocation/v02/goodsAllocatedCommon/pbocShowCommonDetail";

	}

	/**
	 * 返回到列表页面
	 * 
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
	public String back(PbocAllAllocateInfo pbocAllAllocateInfo) {
		return "redirect:" + adminPath + "/allocation/v02/pbocHandinInStore/list";
	}

	/**
	 * 删除 待入库信息
	 * 
	 * @author WangBaozhong
	 * @version 2016年8月30日
	 * 
	 * 
	 * @param allId
	 *            待删除流水单号
	 * @param redirectAttributes
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "delete")
	public String delete(PbocAllAllocateInfo pbocAllAllocateInfo, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Locale locale = LocaleContextHolder.getLocale();

		String message = "";

		// 上缴入库状态 待入库 可以删除
		if (!AllocationConstant.PbocOrderStatus.InHandoverStatus.TO_IN_STORE_STATUS
				.equals(pbocAllAllocateInfo.getStatus())) {
			// message.E2020=[删除失败]：流水单号[{0}]当前状态为[{1}]，不能删除！
			message = msg.getMessage("message.E2020",
					new String[] { pbocAllAllocateInfo.getAllId(),
							DictUtils.getDictLabel(pbocAllAllocateInfo.getStatus(), "pboc_order_handin_status", "") },
					locale);
		} else {

			try {
				pbocAllAllocateInfoService.delete(pbocAllAllocateInfo);
				// message.I2002=[成功]：流水单号{0}删除成功！
				message = msg.getMessage("message.I2002", new String[] { pbocAllAllocateInfo.getAllId() }, locale);
			} catch (BusinessException be) {
				message = msg.getMessage(be.getMessageCode(), be.getParameters(), locale);

			}
		}
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/allocation/v02/pbocHandinInStore/list?repage";
	}
}