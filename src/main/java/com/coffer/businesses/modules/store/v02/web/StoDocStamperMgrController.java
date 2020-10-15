/**
 * wenjian:    StoDocStamperMgrController.java
 * <p>Description:  </p>
 * <p>Modification History: </p>
 * <p>Date         Author        Version       Description </p>
 * <p>------------------------------------------------------------------ </p> 
 * <p>2016年9月12日    xq     1.0         1.0 Version </p>
 * @author:     wangbaozhong
 * @version:    2.0
 * @date:   2016年9月12日 下午2:20:21
 */
package com.coffer.businesses.modules.store.v02.web;

import java.io.IOException;
import java.io.OutputStream;
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
import com.coffer.businesses.common.utils.BusinessUtils;
import com.coffer.businesses.modules.allocation.AllocationConstant;
import com.coffer.businesses.modules.store.StoreCommonUtils;
import com.coffer.businesses.modules.store.StoreConstant;
import com.coffer.businesses.modules.store.v01.entity.StoEscortInfo;
import com.coffer.businesses.modules.store.v02.entity.StoDocTempInfo;
import com.coffer.businesses.modules.store.v02.entity.StoDocTempUserDetail;
import com.coffer.businesses.modules.store.v02.entity.StoOfficeStamperInfo;
import com.coffer.businesses.modules.store.v02.service.StoDocStamperMgrService;
import com.coffer.businesses.modules.store.v02.service.StoOfficeStamperInfoService;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.utils.StringUtils;
import com.coffer.core.common.web.BaseController;
import com.coffer.core.modules.sys.SysCommonUtils;
import com.coffer.core.modules.sys.entity.Office;
import com.coffer.core.modules.sys.utils.DictUtils;
import com.coffer.core.modules.sys.utils.UserUtils;

/**
* Title: StoDocStamperMgrController 
* <p>Description: 单据印章管理控制器</p>
* @author wangbaozhong
* @date 2016年9月12日 下午2:20:21
*/
@Controller
@RequestMapping(value = "${adminPath}/store/v02/stoDocStamperMgr")
public class StoDocStamperMgrController extends BaseController {
	/** service 声明 **/
	@Autowired
	private StoDocStamperMgrService stoDocStamperMgrService;
	@Autowired
	private StoOfficeStamperInfoService stoOfficeStamperInfoService;
	
	@ModelAttribute
	public StoDocTempInfo get(@RequestParam(required=false) String id) {
		StoDocTempInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = stoDocStamperMgrService.get(id);
		}
		if (entity == null){
			entity = new StoDocTempInfo();
		}
		return entity;
	}
	
	/**
	 * 
	 * Title: list
	 * <p>Description: 单据印章管理列表页面</p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = {"list", ""})
	public String list(StoDocTempInfo stoDocTempInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		// 如果不是系统管理员，设置用户的机构号，只能查询当前机构
		if (!AllocationConstant.SysUserType.SYSTEM.equals(UserUtils.getUser().getUserType()) && !Constant.OfficeType.DIGITAL_PLATFORM
				.equals(stoDocTempInfo.getCurrentUser().getOffice().getType())) {
			if (AllocationConstant.SysUserType.COFFER_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.COFFER_OPT.equals(UserUtils.getUser().getUserType())) {
				stoDocTempInfo.setOffice(UserUtils.getUser().getOffice());
			} else if (AllocationConstant.SysUserType.CENTRAL_MANAGER.equals(UserUtils.getUser().getUserType())
					|| AllocationConstant.SysUserType.CENTRAL_OPT.equals(UserUtils.getUser().getUserType())) {
				stoDocTempInfo.setParentsOffice(UserUtils.getUser().getOffice());
			} 
		}
		
		Page<StoDocTempInfo> page = stoDocStamperMgrService.findPage(new Page<StoDocTempInfo>(request, response), stoDocTempInfo);
		model.addAttribute("page", page);
		return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrList";
	}
	
	/**
	 * 
	 * Title: form
	 * <p>Description: 页面跳转</p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo
	 * @param model
	 * @param operationType	操作类型
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "form")
	public String form(StoDocTempInfo stoDocTempInfo, Model model, String operationType) {
		String  userCacheId = UserUtils.createUserCacheId();
		// 缓存用户数据
		UserUtils.putCache(userCacheId, stoDocTempInfo);
		
		model.addAttribute("userCacheId", userCacheId);
		model.addAttribute("stoDocTempInfo", stoDocTempInfo);
		
		if (StoreConstant.OperationType.TO_SHOW_DETAIL_PAGE.equals(operationType)
				|| StoreConstant.OperationType.TO_UPDATE_PAGE.equals(operationType)
				|| StoreConstant.OperationType.TO_ADD_PBOC_STAMPER_PAGE.equals(operationType)) {
			if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(stoDocTempInfo.getBusinessType())) {
				stoDocTempInfo.setHandinStatus(stoDocTempInfo.getStatus());
			} else {
				stoDocTempInfo.setAllocateStatus(stoDocTempInfo.getStatus());
			}
		}
		
		if (StoreConstant.OperationType.TO_SHOW_DETAIL_PAGE.equals(operationType)) {
			return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrDetail";
		} else if (StoreConstant.OperationType.TO_REGIST_PAGE.equals(operationType)) {
			// 设定登记时页面初期值
			stoDocTempInfo.setOffice(UserUtils.getUser().getOffice());
			stoDocTempInfo.setBusinessType(AllocationConstant.BusinessType.PBOC_AGENT_HANDIN);
			stoDocTempInfo.setHandinStatus(AllocationConstant.PbocOrderStatus.FINISH_STATUS);
			return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrForm";
		} else if (StoreConstant.OperationType.TO_UPDATE_PAGE.equals(operationType)) {
			return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrUpdateForm";
		} else if (StoreConstant.OperationType.TO_ADD_PBOC_STAMPER_PAGE.equals(operationType)) {
			return "modules/store/v02/stoDocStamperMgr/addPbocStamper";
		}
		
		return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrForm";
	}
	
	
	/**
	 * 
	 * Title: add
	 * <p>Description: 添加人员</p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo
	 * @param stoDocTempInfoSession
	 * @param model
	 * @param request
	 * @param response
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "add")
	public String add(StoDocTempInfo stoDocTempInfo, @RequestParam(value="userCacheId", required = true)String userCacheId,
			Model model, HttpServletRequest request, HttpServletResponse response, String operationType) {
		
		StoDocTempInfo stoDocTempInfoSession = (StoDocTempInfo)UserUtils.getCache(userCacheId, new StoDocTempInfo());
		
		stoDocTempInfoSession.setOfficeStamperType(stoDocTempInfo.getOfficeStamperType());
		stoDocTempInfoSession.setDocumentType(stoDocTempInfo.getDocumentType());
		stoDocTempInfoSession.setBusinessType(stoDocTempInfo.getBusinessType());
		stoDocTempInfoSession.setAllocateStatus(stoDocTempInfo.getAllocateStatus());
		stoDocTempInfoSession.setHandinStatus(stoDocTempInfo.getHandinStatus());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		
		boolean isUserExist = false;
		boolean isResponsibilityExist = false;
		StoDocTempUserDetail userDetail = stoDocTempInfo.getStoDocTempUserDetail();
		
		
		StoEscortInfo stoEscortInfo = StoreCommonUtils.getEscortById(userDetail.getEscortId());
		userDetail.setStoEscortInfo(stoEscortInfo);
		
		for (StoDocTempUserDetail tempDetail : stoDocTempInfoSession.getDocTempUserDetailList()) {
			if (userDetail.getEscortId().equals(tempDetail.getEscortId())) {
				isUserExist = true;
			}
			if (userDetail.getResponsibilityType().equals(tempDetail.getResponsibilityType())) {
				isResponsibilityExist = true;
			}
		}
		if (isUserExist) {
			//[添加失败]{0}在列表中已经存在！
			message = msg.getMessage("message.E1056", new String[] {stoEscortInfo.getEscortName()}, locale);
			addMessage(model, message);
		} else if (isResponsibilityExist) {
			//[添加失败]{0}在列表中已经存在！
			message = msg.getMessage("message.E1056", new String[] {DictUtils.getDictLabel(userDetail.getResponsibilityType(), "RESPONSIBILITY_TYPE", "")}, locale);
			addMessage(model, message);
		} else {
			stoDocTempInfoSession.getDocTempUserDetailList().add(userDetail);
		}
		// 重新计算物品总价值
		model.addAttribute("stoDocTempInfo", stoDocTempInfoSession);
		model.addAttribute("userCacheId", userCacheId);
		
		if (StoreConstant.OperationType.TO_UPDATE_PAGE.equals(operationType)) {
			return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrUpdateForm";
		}
		return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrForm";
	}
	
	/**
	 * 
	 * Title: deleteEscort
	 * <p>Description: 删除人员</p>
	 * @author:     wangbaozhong
	 * @param escortId
	 * @param stoDocTempInfoSession
	 * @param model
	 * @param request
	 * @param response
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "deleteEscort")
	public String deleteEscort(String escortId, @RequestParam(value="userCacheId", required = true)String userCacheId, 
			Model model, HttpServletRequest request, HttpServletResponse response, String operationType) {
		StoDocTempInfo stoDocTempInfoSession = (StoDocTempInfo)UserUtils.getCache(userCacheId, new StoDocTempInfo());
		int iIndex = 0;
		for (StoDocTempUserDetail tempDetail : stoDocTempInfoSession.getDocTempUserDetailList()) {
			if (escortId.equals(tempDetail.getEscortId())) {
				stoDocTempInfoSession.getDocTempUserDetailList().remove(iIndex);
				break;
			}
			iIndex ++;
		}
		// 重新计算物品总价值
		model.addAttribute("stoDocTempInfo", stoDocTempInfoSession);
		model.addAttribute("userCacheId", userCacheId);
		if (StoreConstant.OperationType.TO_UPDATE_PAGE.equals(operationType)) {
			return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrUpdateForm";
		}
		return "modules/store/v02/stoDocStamperMgr/stoDocStamperMgrForm";
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
	 * @param operationType 操作类型
	 * @return
	 */
	@RequestMapping(value = "save")
	public String save(StoDocTempInfo stoDocTempInfo, @RequestParam(value="userCacheId", required = true)String userCacheId,
			HttpServletRequest request, HttpServletResponse response, String operationType, RedirectAttributes redirectAttributes){
		
		StoDocTempInfo stoDocTempInfoSession = (StoDocTempInfo)UserUtils.getCache(userCacheId, new StoDocTempInfo());
		
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		if (StoreConstant.OperationType.SAVE_REGIST.equals(operationType)) {
			// 取得所属人行机构
			Office parentsOffice = BusinessUtils.getPbocCenterByOffice(UserUtils.getUser().getOffice());
			stoDocTempInfoSession.setParentsOffice(parentsOffice);
			
			if (AllocationConstant.BusinessType.PBOC_AGENT_HANDIN.equals(stoDocTempInfo.getBusinessType())) {
				// 设置代理上缴状态
				stoDocTempInfoSession.setStatus(stoDocTempInfo.getHandinStatus());
			} else if (AllocationConstant.BusinessType.PBOC_APPLICATION_ALLOCATION.equals(stoDocTempInfo.getBusinessType())) {
				// 设置申请取款状态
				stoDocTempInfoSession.setStatus(stoDocTempInfo.getAllocateStatus());
			} else if (AllocationConstant.BusinessType.PBOC_APPLICATION_HANDIN.equals(stoDocTempInfo.getBusinessType())) {
				// 设置申请上缴状态
				stoDocTempInfoSession.setStatus(stoDocTempInfo.getAllocateStatus());
			} 
			// 设定业务类型
			stoDocTempInfoSession.setBusinessType(stoDocTempInfo.getBusinessType());
			// 设定单据类型
			stoDocTempInfoSession.setDocumentType(stoDocTempInfo.getDocumentType());
			// 设定机构印章类型
			stoDocTempInfoSession.setOfficeStamperType(stoDocTempInfo.getOfficeStamperType());
			
			// 获取机构印章
			StoOfficeStamperInfo officeStamperInfoCondition = new StoOfficeStamperInfo();
			officeStamperInfoCondition.setOffice(UserUtils.getUser().getOffice());
			officeStamperInfoCondition.setOfficeStamperType(stoDocTempInfo.getOfficeStamperType());
			StoOfficeStamperInfo rtn = stoOfficeStamperInfoService.get(officeStamperInfoCondition);
			
			stoDocTempInfoSession.setOfficeStamperId(rtn.getId());
			// 判断当前单据是否存在
			StoDocTempInfo existsStoDocTempInfo = stoDocStamperMgrService.getByBusinessAndStatus(stoDocTempInfo.getBusinessType(), 
					stoDocTempInfoSession.getStatus(), UserUtils.getUser().getOffice().getId());
			if (existsStoDocTempInfo != null) {
				
				//[保存失败]相同业务类型和状态的单据印章设定已经存在！
				message = msg.getMessage("message.E1057", null, locale);
				addMessage(redirectAttributes, message);
			} else {
				stoDocStamperMgrService.save(stoDocTempInfoSession);
				//message.I0005=操作成功
				message = msg.getMessage("message.I0005", null, locale);
				addMessage(redirectAttributes, message);
			}
		} else if (StoreConstant.OperationType.SAVE_UPDATE.equals(operationType)) {
			StoDocTempInfo tempInfo = stoDocStamperMgrService.get(stoDocTempInfo.getId());
			
			if (tempInfo != null) {
				if (tempInfo.getParentsOffice() == null || StringUtils.isBlank(tempInfo.getParentsOffice().getId())) {
					// 取得所属人行机构
					Office parentsOffice = BusinessUtils.getPbocCenterByOffice(UserUtils.getUser().getOffice());
					stoDocTempInfoSession.setParentsOffice(parentsOffice);
				}
				stoDocStamperMgrService.update(stoDocTempInfoSession);
				//message.I0005=操作成功
				message = msg.getMessage("message.I0005", null, locale);
				addMessage(redirectAttributes, message);
			} else {
				//message.E1058=[修改失败]源数据不存在！
				message = msg.getMessage("message.E1058", null, locale);
				addMessage(redirectAttributes, message);
			}
			
		} else if (StoreConstant.OperationType.SAVE_PBOC_STAMPER.equals(operationType)) {
			StoDocTempInfo tempInfo = stoDocStamperMgrService.get(stoDocTempInfo.getId());
			if (tempInfo != null) {
				// 获取机构印章
				StoOfficeStamperInfo officeStamperInfoCondition = new StoOfficeStamperInfo();
				officeStamperInfoCondition.setOffice(tempInfo.getOffice().getParent());
				officeStamperInfoCondition.setOfficeStamperType(stoDocTempInfo.getPbocOfficeStamperType());
				StoOfficeStamperInfo rtn = stoOfficeStamperInfoService.get(officeStamperInfoCondition);
				
				stoDocTempInfo.setPbocOfficeStamperId(rtn.getId());
				
				stoDocStamperMgrService.updatePbocOfficeStamperId(stoDocTempInfo);
				//message.I0005=操作成功
				message = msg.getMessage("message.I0005", null, locale);
				addMessage(redirectAttributes, message);
			} else {
				//message.E1058=[修改失败]源数据不存在！
				message = msg.getMessage("message.E1058", null, locale);
				addMessage(redirectAttributes, message);
			}
		}
		// 清除当前业务缓存
		UserUtils.removeCache(userCacheId);
		return "redirect:" + adminPath + "/store/v02/stoDocStamperMgr/list?repage";
	}
	
	/**
	 * 返回到列表页面
	 * @author WangBaozhong
	 * @version 2016年6月6日
	 * 
	 *  
	 * @param stoDocTempInfo
	 * @param userCacheId
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "back")
	public String back(StoDocTempInfo stoDocTempInfo, String userCacheId, Model model, HttpServletRequest request, 
			HttpServletResponse response){
		UserUtils.removeCache(userCacheId);
		return "redirect:" + adminPath + "/store/v02/stoDocStamperMgr/list?repage";
	}
	
	/**
	 * 
	 * Title: delete
	 * <p>Description: 逻辑删除单据模板信息主表信息</p>
	 * @author:     wangbaozhong
	 * @param stoDocTempInfo
	 * @param model
	 * @param request
	 * @param response
	 * @return 
	 * String    返回类型
	 */
	@RequestMapping(value = "delete")
	public String delete(StoDocTempInfo stoDocTempInfo, HttpServletRequest request, HttpServletResponse response, 
			RedirectAttributes redirectAttributes){
		Locale locale = LocaleContextHolder.getLocale();
		String message = "";
		stoDocTempInfo.preUpdate();
		stoDocStamperMgrService.delete(stoDocTempInfo);
		message = msg.getMessage("message.I0002", null, locale);
		addMessage(redirectAttributes, message);
		return "redirect:" + adminPath + "/store/v02/stoDocStamperMgr/list?repage";
	}
	
	/**
	 * 
	 * Title: showOfficeStamperImage
	 * <p>Description: 显示机构印章图片</p>
	 * @author:     wangbaozhong
	 * @param stamperType 印章类型
	 * @param request
	 * @param response
	 * @throws IOException 
	 * void    返回类型
	 */
	@RequestMapping(value = "showOfficeStamperImage")
	public void showOfficeStamperImage(@RequestParam(required = true) String stamperType, @RequestParam(required = false) String officeId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");
		
		StoOfficeStamperInfo officeStamperInfoCondition = new StoOfficeStamperInfo();
		if (!Constant.OfficeType.DIGITAL_PLATFORM.equals(UserUtils.getUser().getOffice().getType())) {
			officeStamperInfoCondition.setOffice(UserUtils.getUser().getOffice());
		} else {
			officeStamperInfoCondition.setOffice(SysCommonUtils.findOfficeById(officeId));
		}
		
		officeStamperInfoCondition.setOfficeStamperType(stamperType);
		StoOfficeStamperInfo rtn = stoOfficeStamperInfoService.get(officeStamperInfoCondition);
		
		if (rtn != null) {
			byte[] imageBytes = rtn.getOfficeStamper();
			if (imageBytes != null) {
				OutputStream out = response.getOutputStream();
				out.write(imageBytes);
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 
	 * Title: showEscortStamperImage
	 * <p>Description: 显示人员印章图片</p>
	 * @author:     wangbaozhong
	 * @param escortId
	 * @param request
	 * @param response
	 * @throws IOException 
	 * void    返回类型
	 */
	@RequestMapping(value = "showEscortStamperImage")
	public void showEscortStamperImage(@RequestParam(required = false) String escortId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");
		
		StoEscortInfo stoEscortInfo = StoreCommonUtils.getEscortById(escortId);
		
		if (stoEscortInfo != null) {
			byte[] imageBytes = stoEscortInfo.getUserStamper();
			if (imageBytes != null) {
				OutputStream out = response.getOutputStream();
				out.write(imageBytes);
				out.flush();
				out.close();
			}
		}

	}
	
	/**
	 * 
	 * Title: showOfficeStamperImageById
	 * <p>Description: 显示机构印章图片</p>
	 * @author:     wangbaozhong
	 * @param stamperType 印章类型
	 * @param request
	 * @param response
	 * @throws IOException 
	 * void    返回类型
	 */
	@RequestMapping(value = "showOfficeStamperImageById")
	public void showOfficeStamperImageById(@RequestParam(required = true) String officeStamperInfoId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/png");
		
		StoOfficeStamperInfo rtn = StoreCommonUtils.getStoOfficeStamperInfoById(officeStamperInfoId);
		
		if (rtn != null) {
			byte[] imageBytes = rtn.getOfficeStamper();
			if (imageBytes != null) {
				OutputStream out = response.getOutputStream();
				out.write(imageBytes);
				out.flush();
				out.close();
			}
		}
	}
}
