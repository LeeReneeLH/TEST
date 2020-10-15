package com.coffer.businesses.modules.atm.v01.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.coffer.businesses.common.Constant;
import com.coffer.businesses.modules.atm.ATMConstant;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingDetail;
import com.coffer.businesses.modules.atm.v01.entity.AtmBindingInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmBrandsInfo;
import com.coffer.businesses.modules.atm.v01.entity.AtmInfoMaintain;
import com.coffer.businesses.modules.atm.v01.entity.AtmPlanInfo;
import com.coffer.businesses.modules.atm.v01.service.AtmBindingInfoService;
import com.coffer.businesses.modules.atm.v01.service.AtmBrandsInfoService;
import com.coffer.businesses.modules.atm.v01.service.AtmInfoMaintainService;
import com.coffer.businesses.modules.atm.v01.service.AtmPlanInfoService;
import com.coffer.businesses.modules.store.v01.entity.StoBoxInfo;
import com.coffer.businesses.modules.store.v01.service.StoBoxInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.Collections3;
import com.coffer.core.common.utils.DateUtils;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.entity.User;
import com.coffer.core.modules.sys.service.SystemService;
import com.coffer.core.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;


/**
 * ATM绑定信息Controller
 * 
 * @author XL
 * @version 2017-11-13
 */
@Controller
@RequestMapping(value = "${adminPath}/atm/v01/atmBindingInfo")
public class AtmBindingInfoController extends BaseController {

	@Autowired
	private AtmBindingInfoService atmBindingInfoService;
	
	@Autowired
	private AtmPlanInfoService atmPlanInfoService;
	
	@Autowired
	private StoBoxInfoService stoBoxInfoService;
	
	@Autowired
	private AtmInfoMaintainService atmInfoMaintainService;
	
	@Autowired
	private AtmBrandsInfoService atmBrandsInfoService;
	
	@ModelAttribute
	public AtmBindingInfo get(@RequestParam(required = false) String bindingId) {
		AtmBindingInfo entity = null;
		if (StringUtils.isNotBlank(bindingId)) {
			entity = atmBindingInfoService.get(bindingId);
		}
		if (entity == null){
			entity = new AtmBindingInfo();
		}
		return entity;
	}
	
	/**
	 * 跳转至ATM绑定信息列表页面
	 * 
	 * @author XL
	 * @version 2017年11月13日
	 * @param isSearch
	 * @param atmBindingInfo
	 * @param request
	 * @param response
	 * @param model
	 * @return ATM绑定信息列表页面
	 */
	@RequiresPermissions("atm:atmBindingInfo:view")
	@RequestMapping(value = {"addList", ""})
	public String addList(@RequestParam(required = false) boolean isSearch, AtmBindingInfo atmBindingInfo,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		// 是否查询
		if (!isSearch) {
			// 不是查询，默认日期为当天
			atmBindingInfo.setCreateTimeStart(new Date());
			atmBindingInfo.setCreateTimeEnd(new Date());
		}
		// 查询条件： 开始时间
		if (atmBindingInfo.getCreateTimeStart() != null) {
			atmBindingInfo.setSearchDateStart(
					DateUtils.foramtSearchDate(DateUtils.getDateStart(atmBindingInfo.getCreateTimeStart())));
		}
		// 查询条件： 结束时间
		if (atmBindingInfo.getCreateTimeEnd() != null) {
			atmBindingInfo.setSearchDateEnd(
					DateUtils.foramtSearchDate(DateUtils.getDateEnd(atmBindingInfo.getCreateTimeEnd())));
		}
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		atmBindingInfo.getSqlMap().put("dsf", SystemService.dataScopeFilter(UserUtils.getUser(), "s", ""));
		Page<AtmBindingInfo> page = atmBindingInfoService.findPage(new Page<AtmBindingInfo>(request, response),
				atmBindingInfo);
		model.addAttribute("page", page);
		/** 添加查询条件(设备名称) 修改人：wxz 2017-11-23 begin*/
		// 获取设备名称下拉列表
		List<AtmInfoMaintain> atmInfoList = atmBindingInfoService.findByAtmName(new AtmInfoMaintain());
		model.addAttribute("atmInfoList", atmInfoList);
		/** end */
		return "modules/atm/v01/atmBindingInfo/atmBindingAddList";
	}
	
	/**
	 * 跳转至加钞绑定登记页面
	 * 
	 * @author Wanglu
	 * @version 2017年11月22日
	 * @param request
	 * @param response
	 * @param model
	 * @return 加钞绑定登记页面
	 */
	@RequiresPermissions("atm:v01:atmBindingInfo:edit")
	@RequestMapping(value = "form")
	public String form(AtmBindingInfo atmBindingInfo, Model model) {
		/*获取加钞计划状态为1或2的加钞计划*/
		List<String> statusesList = Lists.newArrayList();
		statusesList.add(ATMConstant.PlanStatus.PLAN_SYN);
		statusesList.add(ATMConstant.PlanStatus.PLAN_OUT);
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		atmPlanInfo.setStatuses(statusesList);
		/* 生成数据权限过滤条件 修改人：xl 修改时间：2018-01-04 begin */
		atmPlanInfo.getSqlMap().put("dsf", SystemService.dataScopeFilter(UserUtils.getUser(), "o8", null));
		/* end */
		List<AtmPlanInfo> atmPlanInfoList = atmPlanInfoService.getAtmPlanInfoByStatus(atmPlanInfo);
		
		
		if(StringUtils.isNotEmpty(atmBindingInfo.getBindingId())) {
			AtmBindingDetail atmBindingDetail = new AtmBindingDetail();
			atmBindingDetail.setBindingId(atmBindingInfo.getBindingId());
			List<AtmBindingDetail> atmBindingDetailList = atmBindingInfoService.getAtmBindingDetailListByBindingId(atmBindingDetail);
			model.addAttribute("atmBindingDetailList", atmBindingDetailList);
			/* 查询加钞计划标题名 修改人：xl 修改时间：2018-01-04 begin */
			atmPlanInfo = new AtmPlanInfo();
			atmPlanInfo.setAddPlanId(atmBindingInfo.getAddPlanId());
			model.addAttribute("addPlanName", atmPlanInfoService.findList(atmPlanInfo).get(0).getAddPlanName());
			/* end */
		}
		
		model.addAttribute("atmBindingInfo", atmBindingInfo);
		model.addAttribute("atmPlanInfoList", atmPlanInfoList);
		return "modules/atm/v01/atmBindingInfo/atmBindingForm";
	}
	
	/**
	 * 保存绑定信息方法
	 * 
	 * @author Wanglu
	 * @version 2017年11月24日
	 * @param request
	 * @param response
	 * @param model
	 * @return 绑定信息列表页面
	 */
	@RequiresPermissions("atm:v01:atmBindingInfo:edit")
	@RequestMapping(value = "save")
	public String save(AtmBindingInfo atmBindingInfo, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(required = false) String boxList) {
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();
		User user = UserUtils.getUser();
		try {
			List<AtmBindingDetail> atmBindingDetailList = new ArrayList<AtmBindingDetail>();
			String atmNo = atmBindingInfo.getAtmNo();
			AtmInfoMaintain atmInfoMaintain = new AtmInfoMaintain();
			atmInfoMaintain.setAtmId(atmNo);
			String atmAccount = atmInfoMaintainService.findList(atmInfoMaintain).get(0).getTellerId();
			if(StringUtils.isNotEmpty(boxList)) {
				String[] boxInfoList = boxList.split(",");	//箱号字符串变为箱号List
				for(int a=0; a<boxInfoList.length; a++) {
					AtmBindingDetail atmBindingDetailTemp= new AtmBindingDetail();
					atmBindingDetailTemp.setBoxNo(boxInfoList[a].toString());
					atmBindingDetailList.add(atmBindingDetailTemp);
				}
				
			}
			atmBindingInfo.setAtmAccount(atmAccount);
			atmBindingInfo.setDataType(ATMConstant.CoreAmountMethod.INPUT);	//设置为补录
			atmBindingInfo.setAbdL(atmBindingDetailList);	//重新设置绑定计划钞箱列表
			
			/*修改数据时，将旧有的绑定钞箱状态改为在途*/
			if(StringUtils.isNotEmpty(atmBindingInfo.getBindingId())) {
				AtmBindingDetail atmBindingDetail = new AtmBindingDetail();
				atmBindingDetail.setBindingId(atmBindingInfo.getBindingId());
				List<AtmBindingDetail> atmBindingDetailListOld = atmBindingInfoService.getAtmBindingDetailListByBindingId(atmBindingDetail);	//获取旧的绑定钞箱
				/* 绑定同一钞箱，状态不恢复在途状态 修改人：XL 修改时间：2018-01-12 begin */
				for (Iterator<AtmBindingDetail> iterator = atmBindingDetailListOld.iterator(); iterator.hasNext();) {
					AtmBindingDetail atmBindingDetailOld = (AtmBindingDetail) iterator.next();
					for (AtmBindingDetail atmBindingDetailNew : atmBindingDetailList) {
						if (atmBindingDetailOld.getBoxNo().equals(atmBindingDetailNew.getBoxNo())) {
							iterator.remove();
						}
					}
				}
				/* end */
				List<String> stoBoxInfoList = Lists.newArrayList();	//用于保存旧有的绑定钞箱的钞箱号
				for(int a=0; a<atmBindingDetailListOld.size(); a++) {
					stoBoxInfoList.add(atmBindingDetailListOld.get(a).getBoxNo());
				}
				if(stoBoxInfoList.size() > 0) {
					stoBoxInfoService.updateStatusBatch(stoBoxInfoList, Constant.BoxStatus.ONPASSAGE, user);	//批量修改钞箱状态为12在途
				}
			}
			
			atmBindingInfoService.save(atmBindingInfo);	//保存钞箱绑定信息
			addMessage(redirectAttributes, msg.getMessage("message.I4037", null, locale));
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, msg.getMessage("message.E4038", null, locale));
		}
		return "redirect:" + adminPath + "/atm/v01/atmBindingInfo/addList?isSearch=false&repage";
	}

	/**
	 * 跳转至绑定明细列表页面
	 * 
	 * @author XL
	 * @version 2017年11月13日
	 * @param atmBindingInfo
	 * @param type
	 * @param request
	 * @param response
	 * @param model
	 * @return 绑定明细列表页面
	 */
	@RequiresPermissions("atm:atmBindingInfo:view")
	@RequestMapping(value = "view")
	public String view(AtmBindingInfo atmBindingInfo, @RequestParam(required = false) boolean type,
			HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<AtmBindingDetail> page = null;
		if (type) {
			page = atmBindingInfoService.findAddPage(new Page<AtmBindingDetail>(request, response), atmBindingInfo);
		} else {
			page = atmBindingInfoService.findClearPage(new Page<AtmBindingDetail>(request, response), atmBindingInfo);
		}
		model.addAttribute("page", page);
		model.addAttribute("type", type);
		return "modules/atm/v01/atmBindingInfo/atmBindingDetail";
	}
	
	/**
	 * 返回上一级页面
	 * 
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(HttpServletResponse response, HttpServletRequest request) {
		return "redirect:" + adminPath + "/atm/v01/atmBindingInfo/addList?isSearch=false&repage";
	}
	
	@RequiresPermissions("atm:v01:atmBindingInfo:edit")
	@ResponseBody
	@RequestMapping(value = "getAtmInfo")
	public String getAtmInfo(String addPlanId) {
		Map<String,Object> atmInfoListMap = new HashMap<String, Object>();
		
		List<String> statusesList = Lists.newArrayList();
		statusesList.add(ATMConstant.PlanStatus.PLAN_SYN);
		statusesList.add(ATMConstant.PlanStatus.PLAN_OUT);
		AtmPlanInfo atmPlanInfo = new AtmPlanInfo();
		atmPlanInfo.setStatuses(statusesList);
		atmPlanInfo.setAddPlanId(addPlanId);
		List<AtmPlanInfo> atmPlanInfoList = atmPlanInfoService.getAtmInfoByPlanId(atmPlanInfo);
		
		atmInfoListMap.put("atmPlanInfoList", atmPlanInfoList);
		return new Gson().toJson(atmInfoListMap);
	}
	
	/**
	 * 验证钞箱与ATM机匹配的的AJAX方法
	 * 
	 * @author wanglu
	 * @version 2017年11月24日
	 * @param boxNo
	 * @param atmNo
	 * @return JSON
	 */
	@RequiresPermissions("atm:v01:atmBindingInfo:edit")
	@ResponseBody
	@RequestMapping(value = "checkBoxInfo")
	public String checkBoxInfo(String boxNo, String atmNo, String boxNoList) {
		
		// 国际化
		Locale locale = LocaleContextHolder.getLocale();

		Map<String, Object> checkResultMap = new HashMap<String, Object>();
		StoBoxInfo stoBoxInfo = this.checkBoxStatus(boxNo);
		if(stoBoxInfo != null) {
			String boxRfid = stoBoxInfo.getRfid();
			boolean boxType = this.checkBoxType(boxRfid, atmNo);
			String boxStatus = stoBoxInfo.getBoxStatus();
			String delFlag = StoBoxInfo.DEL_FLAG_NORMAL;
			if(!boxType) {
				checkResultMap.put("msg", msg.getMessage("message.E4040", null, locale));//"钞箱与ATM机不匹配！"
				checkResultMap.put("status", false);
			} else if (!Constant.BoxStatus.ONPASSAGE.equals(boxStatus) || !delFlag.equals(stoBoxInfo.getDelFlag())) {
				checkResultMap.put("msg", msg.getMessage("message.E4039", null, locale));//"钞箱状态不正确！"
				checkResultMap.put("status", false);
			} else if (!checkAtmAndBox(boxNo, atmNo, boxNoList)) {
				checkResultMap.put("msg", msg.getMessage("message.E4055", null, locale));// "该类型钞箱个数超出该型号ATM机钞箱个数！"
				checkResultMap.put("status", false);
			}else{
				checkResultMap.put("status", true);
			}
		}else {
			checkResultMap.put("msg", msg.getMessage("message.E4041", null, locale));	//"钞箱不存在！"
			checkResultMap.put("status", false);
		}
		return new Gson().toJson(checkResultMap);
	}
	
	/**
	 * 获取单个钞箱信息用于检查钞箱状态
	 * 
	 * @author wanglu
	 * @version 2017年11月24日
	 * @param boxNo
	 * @return StoBoxInfo
	 */
	private StoBoxInfo checkBoxStatus(String boxNo) {
		StoBoxInfo stoBoxInfo = new StoBoxInfo();
		stoBoxInfo.setId(boxNo);
		stoBoxInfo.getSqlMap().put("dsf", SystemService.dataScopeFilter(UserUtils.getUser(), "o5", null));
		List<StoBoxInfo> stoBoxInfos = stoBoxInfoService.findList(stoBoxInfo);
		if (Collections3.isEmpty(stoBoxInfos)) {
			return null;
		}
		return stoBoxInfos.get(0);
		 
	}
	
	/**
	 * 检查钞箱是否和ATM机匹配
	 * 
	 * @author wanglu
	 * @version 2017年11月28日
	 * @param boxRfid
	 * @param atmNo
	 * @return boolean
	 */
	private boolean checkBoxType(String boxRfid, String atmNo) {
		String atmRfid = atmInfoMaintainService.findInfoByAtmId(atmNo).getRfid();
		boolean returnInfo = false;
		if(StringUtils.isNoneBlank(atmRfid)) {
			String brankNo = atmRfid.substring(0, 4);
			String backBox = brankNo+Constant.BoxType.BOX_BACK.substring(1)+ atmRfid.substring(4, 5);
			String getBox =	brankNo+Constant.BoxType.BOX_GET.substring(1)+ atmRfid.substring(5, 6);
			String depositeBox = brankNo+Constant.BoxType.BOX_DEPOSITE.substring(1)+ atmRfid.substring(6, 7);
			String cycleBox = brankNo+Constant.BoxType.BOX_CYCLE.substring(1)+ atmRfid.substring(7, 8);
			
			
			String boxType = boxRfid.substring(0, 6);
			if(boxType.equals(backBox)) {
				returnInfo = true;
			}else if(boxType.equals(getBox)) {
				returnInfo = true;
			}else if(boxType.equals(depositeBox)) {
				returnInfo = true;
			}else if(boxType.equals(cycleBox)) {
				returnInfo = true;
			}else returnInfo = false;
		}
		return returnInfo;
	}

	/**
	 * 根据钞箱编号获取金额
	 * 
	 * @author XL
	 * @version 2017年12月14日
	 * @param boxNo
	 * @return
	 */
	@RequiresPermissions("atm:v01:atmBindingInfo:edit")
	@ResponseBody
	@RequestMapping(value = "getBoxAmount")
	public String getBoxAmount(String boxNo) {
		Map<String, Object> checkResultMap = new HashMap<String, Object>();
		// 钞箱
		StoBoxInfo stoBoxInfo = stoBoxInfoService.get(boxNo);
		checkResultMap.put("amount", stoBoxInfo.getBoxAmount());
		return new Gson().toJson(checkResultMap);
	}

	/**
	 * 验证ATM机加钞超箱个数
	 * 
	 * @author XL
	 * @version 2017年12月14日
	 * @param boxNo
	 * @param atmNo
	 * @param boxNoList
	 * @return
	 */
	public boolean checkAtmAndBox(String boxNo, String atmNo, String boxNoList) {
		// 钞箱箱号列表
		String[] boxNoListArray = boxNoList.split(Constant.Punctuation.COMMA);
		List<String> boxNos = Arrays.asList(boxNoListArray);
		// 取款箱个数
		int getNum = 0;
		// 回收箱个数
		int backNum = 0;
		// 存款箱个数
		int dpositeNum = 0;
		for (String BoxTypeNo : boxNos) {
			// 钞箱编号最后两位
			BoxTypeNo = BoxTypeNo.substring(6);
			if (Constant.BoxType.BOX_BACK.equals(BoxTypeNo)) {
				backNum++;
			}
			if (Constant.BoxType.BOX_GET.equals(BoxTypeNo)) {
				getNum++;
			}
			if (Constant.BoxType.BOX_DEPOSITE.equals(BoxTypeNo)) {
				dpositeNum++;
			}
		}
		// ATM机品牌信息
		AtmBrandsInfo atmBrandsInfo = atmBrandsInfoService.findByAtmNo(atmNo);
		// 加钞钞箱是否超过该型号钞箱个数
		if (atmBrandsInfo == null || atmBrandsInfo.getGetBoxNumber() < getNum
				|| atmBrandsInfo.getBackBoxNumber() < backNum
				|| atmBrandsInfo.getDepositBoxNumber() < dpositeNum) {
			return false;
		}
		return true;
	}
}